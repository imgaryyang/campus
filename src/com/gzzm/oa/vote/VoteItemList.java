package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author camel
 * @date 12-4-12
 */
@Service(url = "/oa/vote/items")
public class VoteItemList extends BaseOQLQueryCrud<VoteItem>
{
    @Inject
    private VoteDao dao;

    /**
     * 投票ID
     */
    private Integer voteId;

    @NotSerialized
    private Vote vote;

    private VoteType type;

    @Lower
    private Date timeStart;

    @Upper
    private Date timeEnd;

    @Like
    private String userName;

    @Like
    private String deptName;

    private Map<String, List<Integer>> options;

    private Map<String, Integer> maxs;

    private Map<String, Integer> mins;

    public VoteItemList()
    {
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public Date getTimeStart()
    {
        return timeStart;
    }

    public void setTimeStart(Date timeStart)
    {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd()
    {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd)
    {
        this.timeEnd = timeEnd;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Map<String, List<Integer>> getOptions()
    {
        return options;
    }

    public void setOptions(Map<String, List<Integer>> options)
    {
        this.options = options;
    }

    public Map<String, Integer> getMaxs()
    {
        return maxs;
    }

    public void setMaxs(Map<String, Integer> maxs)
    {
        this.maxs = maxs;
    }

    public Map<String, Integer> getMins()
    {
        return mins;
    }

    public void setMins(Map<String, Integer> mins)
    {
        this.mins = mins;
    }

    public Vote getVote() throws Exception
    {
        if (vote == null)
            vote = dao.getVoteById(voteId);
        return vote;
    }

    protected VoteType getType() throws Exception
    {
        if (type == null)
            type = getVote().getType();

        return type;
    }

    public Integer getVoteDeptId() throws Exception
    {
        return getVote().getDeptId();
    }

    public VoteScopeType getScopeType() throws Exception
    {
        return getVote().getScopeType();
    }

    @NotSerialized
    public List<VoteProblem> getProblems() throws Exception
    {
        return getVote().getProblems();
    }

    @NotSerialized
    public List<VoteProblemGroup> getGroups() throws Exception
    {
        List<VoteProblemGroup> groups = new ArrayList<VoteProblemGroup>(getVote().getGroups());

        VoteProblemGroup group = new VoteProblemGroup();
        group.setProblems(getProblems(group));

        groups.add(group);

        return groups;
    }

    public List<VoteProblem> getProblems(VoteProblemGroup group) throws Exception
    {
        if (group.getGroupId() != null)
            return group.getProblems();

        return dao.getProblemsWithoutGroup(voteId == null ? vote.getVoteId() : voteId);
    }

    @NotSerialized
    public String getActionName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    @NotSerialized
    public String getTypeName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getTypeName();
    }

    @Override
    protected String getQueryString() throws Exception
    {
        StringBuilder buffer = new StringBuilder("select r.recordId,r.voteTime," +
                "nvl(r.user.userName,case when s.type=1 then s.user.userName else null end) as userName," +
                "case when s.type=0 then s.dept.deptName else null end as deptName,r.score" +
                " from VoteScope s left join VoteRecord r on (r.deptId=s.objectId and s.type=0 and r.type=0" +
                " or r.userId=s.objectId and s.type=1 and r.type=1) and r.state=1 and s.voteId=r.voteId where " +
                "s.voteId=:voteId and r.voteTime>=?timeStart and r.voteTime<?timeEnd and " +
                "nvl(r.user.userName,case when s.type=1 then s.user.userName else null end) like ?userName");

        if (!StringUtils.isBlank(deptName))
            buffer.append(" and s.type=0 and s.dept.deptName like ?deptName");


        if (options != null)
        {
            for (Map.Entry<String, List<Integer>> entry : options.entrySet())
            {
                Integer problemId = Integer.valueOf(entry.getKey().substring(1));
                List<Integer> options = entry.getValue();
                if (options != null && options.size() > 0)
                {
                    if (buffer == null)
                        buffer = new StringBuilder();
                    else
                        buffer.append(" and ");

                    boolean other = options.contains(-1);

                    if (!other || options.size() > 1)
                    {
                        if (other)
                            buffer.append("(");
                        buffer.append("(exists s in r.selecteds : s.optionId in :options.p")
                                .append(problemId).append(")");
                    }

                    if (other)
                    {
                        if (options.size() > 1)
                        {
                            buffer.append(" or ");
                        }

                        buffer.append("(exists i in r.voteInputList : i.problemId=").append(problemId).append(")");

                        if (options.size() > 1)
                        {
                            buffer.append(")");
                        }
                    }
                }
            }
        }

        if (maxs != null)
        {
            for (Map.Entry<String, Integer> entry : maxs.entrySet())
            {
                if (entry.getValue() != null)
                {
                    if (buffer == null)
                        buffer = new StringBuilder();
                    else
                        buffer.append(" and ");

                    Integer problemId = Integer.valueOf(entry.getKey().substring(1));
                    buffer.append("(exists i in r.voteInputList : (i.problemId=").append(problemId)
                            .append(" and i.numberValue<=:maxs.p").append(problemId).append("))");
                }
            }
        }

        if (mins != null)
        {
            for (Map.Entry<String, Integer> entry : mins.entrySet())
            {
                if (entry.getValue() != null)
                {
                    if (buffer == null)
                        buffer = new StringBuilder();
                    else
                        buffer.append(" and ");

                    Integer problemId = Integer.valueOf(entry.getKey().substring(1));
                    buffer.append("(exists i in r.voteInputList : (i.problemId=").append(problemId)
                            .append(" and i.numberValue>=:mins.p").append(problemId).append("))");
                }
            }
        }

        buffer.append(" order by ");

        OrderBy orderBy = getOrderBy();
        String name = null;
        if (orderBy != null)
        {
            name = orderBy.getName();
            OrderType type = orderBy.getType();
            if ("voteTime".equals(name))
            {
                if (type == OrderType.asc)
                    buffer.append("nvl(r.voteTime,sysdate())");
                else
                    buffer.append("nvl(r.voteTime,{1900-01-01 00:00:00}) desc");
            }
            else
            {
                //标识排序字段能否被识别
                boolean b = true;

                if ("deptName".equals(name))
                {
                    buffer.append("case when s.type=0 then s.dept.leftValue else null end");

                    if (type == OrderType.desc)
                        buffer.append(" desc");
                }
                else if ("userName".equals(name))
                {
                    if (type == OrderType.asc)
                        buffer.append("nvl(r.user.userName,case when s.type=1 then s.user.userName else '#' end)");
                    else
                        buffer.append("nvl(r.user.userName,case when s.type=1 then s.user.userName else 'a' end) desc");
                }
                else if ("state".equals(name))
                {
                    buffer.append("nvl(r.state,0)");

                    if (type == OrderType.desc)
                        buffer.append(" desc");
                }
                else if ("score".equals(name))
                {
                    if (type == OrderType.asc)
                        buffer.append("nvl(r.score,0)");
                    else
                        buffer.append("nvl(r.score,0) desc");
                }
                else
                {
                    b = false;
                }

                if (b)
                    buffer.append(",");
            }
        }

        if (!"voteTime".equals(name))
        {
            buffer.append("nvl(r.voteTime,sysdate())");
        }

        return buffer.toString();
    }

    @Forward(name = "query", page = "/oa/vote/record_query.ptl")
    public String forward(String forward)
    {
        return super.forward(forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        Vote vote = getVote();

        boolean hasScore = dao.hasScore(voteId);

        boolean hasDept =
                vote.getScopeType() != VoteScopeType.USER || dao.getScopeCount(voteId, VoteScopeType.DEPT) > 0;

        view.setRemark(vote.getTitle() + ",已" + getActionName() + dao.getVoteRecordCount(voteId) +
                ",未" + getActionName() + dao.getNotVotedCount(voteId));

        if (hasDept)
            view.addComponent(getActionName() + "部门", "deptName");

        if (vote.getScopeType() != VoteScopeType.DEPT)
            view.addComponent(getActionName() + "人", "userName");

        view.addComponent(getActionName() + "时间", "timeStart", "timeEnd");

        if (vote.getScopeType() != VoteScopeType.USER)
            view.addColumn(getActionName() + "部门", "deptName");

        view.addColumn(getActionName() + "人", "userName").setWidth("140");

        if (vote.getScopeType() == VoteScopeType.USER && hasDept)
            view.addColumn(getActionName() + "部门", "deptName");

        view.addColumn("状态", "recordId==null?'未" + getActionName() + "':'已" + getActionName() + "'")
                .setOrderFiled("state");
        view.addColumn(getActionName() + "时间", "voteTime");

        if (hasScore)
            view.addColumn("得分", "score");

        view.addColumn("查看内容", new ConditionComponent()
                .add("recordId!=null", new CHref("查看内容").setAction("showRecordContent(${recordId})")));

        view.importJs("/oa/vote/vote.js");
        view.importJs("/oa/vote/votescope.js");

        view.defaultInit();
        view.addButton(Buttons.moreQuery2());
        view.addButton(Buttons.getButton("crud.export",
                "window.open('/oa/vote/VoteRecord/export/xls?voteId=" + voteId + "')", "xls"));
        view.addButton(new CButton("添加" + getTypeName() + "范围", "addScopes();"));

        return view;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public boolean addScopes(List<VoteScope> scopes) throws Exception
    {
        Vote vote = getVote();

        boolean result = false;

        List<VoteScope> scopes0 = vote.getVoteScopes();
        for (VoteScope scope : scopes)
        {
            boolean exists = false;
            for (VoteScope scope0 : scopes0)
            {
                if (scope0.getObjectId().equals(scope.getObjectId()) && scope0.getType() == scope.getType())
                {
                    exists = true;
                    break;
                }
            }

            if (!exists)
            {
                result = true;
                scope.setVoteId(voteId);
                dao.add(scope);
            }
        }

        return result;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters(getActionName() + "记录列表");
    }
}
