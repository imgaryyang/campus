package com.gzzm.oa.vote;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 投票管理Crud
 *
 * @author db
 * @date 11-12-2
 */
@Service(url = "/oa/vote/Vote")
public class VoteCrud extends DeptOwnedNormalCrud<Vote, Integer>
{
    @Inject
    private VoteDao dao;

    @Like
    private String title;

    @Lower(column = "startTime")
    private Date timeStart;

    @Upper(column = "endTime")
    private Date timeEnd;

    /**
     * 注入当前用户ID
     * 用于保存投票创建人
     */
    @NotSerialized
    @UserId
    private Integer userId;

    /**
     * 用投票类型ID过滤
     */
    private Integer typeId;

    private VoteType type;

    public VoteCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    protected VoteType getType() throws Exception
    {
        if (type == null && typeId != null)
            type = dao.getVoteType(typeId);

        return type;
    }

    @NotSerialized
    public String getTypeName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getTypeName();
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
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = getAuthDeptIds() == null || getAuthDeptIds().size() > 1 ?
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true) : new PageTableView(true);

        // 添加查询条件
        view.addComponent("标题", "title");
        view.addComponent("时间", "timeStart", "timeEnd");

        // 添加显示列
        view.addColumn("标题", "title");
        view.addColumn("创建人", "user.userName");
        view.addColumn("创建时间", new FieldCell("createTime", "yyyy-MM-dd HH:mm")).setWidth("110");
        view.addColumn("开始时间", "startTime").setWidth("75");
        view.addColumn("结束时间", "endTime").setWidth("75");
        view.addColumn("已发布", "valid").setWidth("50");
        view.addColumn("问题管理", new CHref("问题管理").setAction("forwardProblem(${voteId})")).setWidth("65");
        view.addColumn("分组管理", new CHref("分组管理").setAction("forwardGroup(${voteId})")).setWidth("65");
        view.addColumn(getActionName() + "范围", new CHref(getTypeName() + "范围").setAction("editScope(${voteId})"))
                .setWidth("85");
        view.addColumn("预览", new CButton("预览", "preview(${voteId})")).setWidth("50");

        // 添加增删查改按钮
        view.defaultInit(true);

        // 引入JS文件
        view.importJs("/oa/vote/vote.js");

        return view;
    }

    @Override
    @Forward(page = "/oa/vote/vote.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/oa/vote/vote.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/oa/vote/vote.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        super.duplicate(key, forward);

        saveEntity(getEntity(), true);
        setNew$(false);

        return null;
    }

    @Override
    public void initEntity(Vote entity) throws Exception
    {
        super.initEntity(entity);

        //新增时初始化参数 是否有效设置为”是“
        entity.setValid(true);
        if (typeId != null)
            entity.setTypeId(typeId);
    }

    @Override
    public Vote clone(Vote entity) throws Exception
    {
        Vote vote = super.clone(entity);

        vote.setTitle(vote.getTitle() + "(复制)");

        List<VoteScope> scopes = new ArrayList<VoteScope>(entity.getVoteScopes().size());

        for (VoteScope scope : entity.getVoteScopes())
        {
            VoteScope scope1 = new VoteScope();
            scope1.setObjectId(scope.getObjectId());
            scope1.setObjectName(scope.getObjectName());
            scope1.setType(scope.getType());
            scope1.setVoteCount(scope.getVoteCount());

            if (scope.getType() == VoteScopeType.DEPT)
                scope1.setDept(scope.getDept());
            else
                scope1.setUser(scope.getUser());

            scopes.add(scope1);
        }

        vote.setVoteScopes(scopes);

        List<VoteProblemGroup> groups = entity.getGroups();
        Map<Integer, Integer> groupMap = null;
        if (groups != null && groups.size() > 0)
        {
            List<VoteProblemGroup> newGroups = new ArrayList<VoteProblemGroup>(groups.size());
            groupMap = new HashMap<Integer, Integer>();
            for (VoteProblemGroup group : groups)
            {
                VoteProblemGroup newGroup = group.cloneGroup();
                dao.add(newGroup);
                groupMap.put(group.getGroupId(), newGroup.getGroupId());

                newGroups.add(newGroup);
            }
            vote.setGroups(newGroups);
        }

        List<VoteProblem> problems = new ArrayList<VoteProblem>(entity.getProblems().size());
        for (VoteProblem problem : entity.getProblems())
        {
            VoteProblem newProblem = problem.cloneProblem();

            if (problem.getGroupId() != null && groupMap != null)
                newProblem.setGroupId(groupMap.get(problem.getGroupId()));

            problems.add(newProblem);
        }
        vote.setProblems(problems);

        return vote;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        // 在插入数据前保存投票创建人Id
        getEntity().setCreator(userId);
        getEntity().setCreateTime(new java.util.Date());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        List<VoteScope> voteScopes = getEntity().getVoteScopes();
        if (voteScopes != null)
        {
            List<VoteScope> voteScopes1 = getEntity(getEntity().getVoteId()).getVoteScopes();

            for (VoteScope scope : voteScopes)
            {
                for (VoteScope scope1 : voteScopes1)
                {
                    if (scope1.getType().equals(scope.getType()) &&
                            scope1.getObjectId().equals(scope.getObjectId()))
                    {
                        scope.setVoteCount(scope1.getVoteCount());
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        // 在打开Crud视图前将当前用户部门ID设置到实体中
        setDefaultDeptId();
    }
}
