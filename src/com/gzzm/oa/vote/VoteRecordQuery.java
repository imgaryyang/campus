package com.gzzm.oa.vote;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.SimpleCell;
import net.cyan.crud.view.components.CHref;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 投票记录Crud
 *
 * @author db
 * @date 11-12-3
 */
@Service(url = "/oa/vote/VoteRecord")
public class VoteRecordQuery extends BaseQueryCrud<VoteRecord, Integer>
{
    @Inject
    private VoteDao dao;

    /**
     * 自动注入用户的相关信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @NotCondition
    private Integer deptId;

    @Like("user.userName")
    private String userName;

    @Like("dept.deptName")
    private String deptName;

    @Like
    private String objectName;

    @Lower(column = "voteTime")
    private Date timeStart;

    @Upper(column = "voteTime")
    private Date timeEnd;

    /**
     * 接收页面传来的投票信息ID
     */
    private Integer voteId;

    @NotSerialized
    private Vote vote;

    private Map<String, List<Integer>> options;

    private Map<String, Integer> maxs;

    private Map<String, Integer> mins;

    private VoteType type;

    private VoteRecordState state = VoteRecordState.committed;

    private Integer problemId;

    private boolean self;

    public VoteRecordQuery()
    {
        addOrderBy("voteTime", OrderType.desc);
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

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
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

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
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

    public Integer getProblemId()
    {
        return problemId;
    }

    public void setProblemId(Integer problemId)
    {
        this.problemId = problemId;
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }

    @NotCondition
    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    @NotSerialized
    public Integer getDeptId() throws Exception
    {
        if (deptId == null)
        {
            Vote vote = getVote();
            if (vote.getScopeType() == VoteScopeType.DEPT)
            {
                for (VoteScope scope : vote.getVoteScopes())
                {
                    if (authDeptIds == null && userOnlineInfo.getDeptId().equals(scope.getObjectId()) ||
                            authDeptIds != null && authDeptIds.contains(scope.getObjectId()))
                    {
                        deptId = scope.getObjectId();
                        break;
                    }
                }
            }
        }

        return deptId;
    }

    public VoteRecordState getState()
    {
        return state;
    }

    @NotSerialized
    public Vote getVote() throws Exception
    {
        if (vote == null)
            vote = dao.getVoteById(voteId);
        return vote;
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
    public List<VoteProblem> getProblems() throws Exception
    {
        return getVote().getProblems();
    }

    protected VoteType getType() throws Exception
    {
        if (type == null)
            type = getVote().getType();

        return type;
    }

    @NotSerialized
    public String getActionName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder buffer = null;
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
                        buffer.append("(exists s in selecteds : s.optionId in :options.p")
                                .append(problemId).append(")");
                    }

                    if (other)
                    {
                        if (options.size() > 1)
                        {
                            buffer.append(" or ");
                        }

                        buffer.append("(exists i in voteInputList : i.problemId=").append(problemId).append(")");

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
                    buffer.append("(exists i in voteInputList : (i.problemId=").append(problemId)
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
                    buffer.append("(exists i in voteInputList : (i.problemId=").append(problemId)
                            .append(" and i.numberValue>=:mins.p").append(problemId).append("))");
                }
            }
        }

        if (self)
        {
            if (buffer == null)
                buffer = new StringBuilder();
            else
                buffer.append(" and ");

            if (getDeptId() != null)
                buffer.append(" deptId=:deptId");
            else
                buffer.append(" userId=:userId");
        }

        return buffer == null ? null : buffer.toString();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        Vote vote = dao.getVoteById(voteId);
        if (vote.isAnonymous() != null && vote.isAnonymous())
            userName = null;
    }

    @Forward(name = "query", page = "/oa/vote/record_query.ptl")
    public String forward(String forward)
    {
        return super.forward(forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        Vote vote = getVote();

        //设置标题
        // 因为此视图没有菜单
        view.setTitle(getActionName() + "记录—" + vote.getTitle());

        VoteScopeType scopeType = vote.getScopeType();

        if (!self)
        {
            if (vote.isAnonymous() == null || !vote.isAnonymous())
            {
                if (scopeType == VoteScopeType.DEPT)
                    view.addComponent(getActionName() + "部门", "deptName");
                else if (scopeType == VoteScopeType.USER)
                    view.addComponent(getActionName() + "人", "userName");
                else if (scopeType == VoteScopeType.ALL)
                    view.addComponent(getActionName() + "对象", "objectName");
            }
        }
        view.addComponent(getActionName() + "时间", "timeStart", "timeEnd");

        // 添加显示列
        if (!self)
        {
            if (scopeType == VoteScopeType.DEPT)
                view.addColumn(getActionName() + "部门", "anonymous!=null&&anonymous?'匿名':dept.deptName")
                        .setOrderFiled("dept.leftValue");
            if (scopeType == VoteScopeType.ALL)
                view.addColumn(getActionName() + "对象", "anonymous!=null&&anonymous?'匿名':objectName");

            view.addColumn(getActionName() + "人", "anonymous!=null&&anonymous?'匿名':user.userName")
                    .setOrderFiled("user.userName");
        }
        else
        {
            view.addColumn(getActionName() + "人", "user.userName").setOrderFiled("user.userName");
        }

        view.addColumn(getActionName() + "时间", "voteTime");

        if ("exp".equals(getAction()) || problemId != null)
        {
            for (final VoteProblem problem : vote.getProblems())
            {
                if (problemId == null || problem.getProblemId().equals(problemId))
                {
                    view.addColumn(problem.getProblemName(), new SimpleCell()
                    {
                        @Override
                        public String display(Object entity) throws Exception
                        {
                            return ((VoteRecord) entity).getVoteResult(problem);
                        }

                        public boolean isText()
                        {
                            return true;
                        }

                        public String getFormat()
                        {
                            return null;
                        }

                        public Object getValue(Object entity) throws Exception
                        {
                            return display(entity);
                        }

                        public Class getType(Class<?> entityType)
                        {
                            return String.class;
                        }
                    }).setAutoExpand(problemId != null);
                }
            }
        }

        boolean hasScore = dao.hasScore(voteId);
        if (hasScore)
        {
            view.addColumn("得分", "score");
        }

        if (self)
        {
            if (problemId == null)
                view.addColumn("查看内容", new CHref("查看内容").setAction("editRecordContent(${recordId})"));
            else
                view.addColumn("查看全部内容", new CHref("查看全部内容").setAction("editRecordContent(${recordId})"));
        }
        else
        {
            if (problemId == null)
                view.addColumn("查看内容", new CHref("查看内容").setAction("showRecordContent(${recordId})"));
            else
                view.addColumn("查看全部内容", new CHref("查看全部内容").setAction("showRecordContent(${recordId})"));
        }

        view.defaultInit();
        view.addButton(Buttons.moreQuery2());

        // 添加导出按钮功能（导出为电子表格文件）
        view.addButton(Buttons.export("xls"));

        // 导入JS
        view.importJs("/oa/vote/vote.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters(getActionName() + "记录列表");
    }
}
