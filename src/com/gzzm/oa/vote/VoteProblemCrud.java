package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 投票问题Crud
 *
 * @author db
 * @date 11-12-2
 */
@Service(url = "/oa/vote/VoteProblem")
public class VoteProblemCrud extends BaseNormalCrud<VoteProblem, Integer>
{
    @Inject
    private VoteDao dao;

    @Like
    private String problemName;

    /**
     * 接收投票信息ID
     */
    private Integer voteId;

    private Integer groupId;

    private List<VoteProblemGroup> groups;

    public VoteProblemCrud()
    {
    }

    public String getProblemName()
    {
        return problemName;
    }

    public void setProblemName(String problemName)
    {
        this.problemName = problemName;
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    @Select(field = "entity.groupId")
    @NotSerialized
    public List<VoteProblemGroup> getGroups() throws Exception
    {
        if (groups == null)
            groups = dao.getProblemGroups(voteId);

        return groups;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public void initEntity(VoteProblem entity) throws Exception
    {
        super.initEntity(entity);

        // 新增时，初始化相关参数
        entity.setDataType(ProblemDataType.STRING);
        entity.setRequired(true);
        entity.setOther(false);
        entity.setMinCount(1);
        entity.setMaxCount(1);

        entity.setVoteId(voteId);
        entity.setGroupId(getGroupId());
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        if (getEntity().getMinCount() == null)
            getEntity().setMinCount(1);
        if (getEntity().getMaxCount() == null)
            getEntity().setMaxCount(1);
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        if (getEntity().getOther() == null)
            getEntity().setOther(false);

        return super.beforeUpdate();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        // 设置标题
        // 因为此视图没有菜单
        view.setTitle("问题管理");

        // 添加查询条件
        view.addComponent("问题名", "problemName");

        // 添加显示列
        view.addColumn("问题", "problemName");
        view.addColumn("分组", "problemGroup.groupName").setWidth("200").setOrderFiled("problemGroup.orderId");
        view.addColumn("类型", "dataType");
        view.addColumn("必填", "required");
        view.addColumn("最小值", "minval").setWidth("60");
        view.addColumn("最大值", "maxval").setWidth("60");
        view.addColumn("最多选择", new FieldCell("maxCount").setUnit("项")).setWidth("75");
        view.addColumn("至少选择", new FieldCell("minCount").setUnit("项")).setWidth("75");

        // 添加增删查改按钮
        view.defaultInit(true);

        // 添加排序按钮
        // 需重写getOrderWithFields() 和 实体类的toString()方法
        view.addButton(Buttons.sort());
        view.addButton(new CButton("分组管理", "forwardGroup()"));
        view.addButton(new CButton("预览", "preview()"));

        // 引入JS文件
        view.importJs("/oa/vote/vote.js");

        return view;
    }

    @Override
    @Forward(page = "/oa/vote/voteproblem.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/oa/vote/voteproblem.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/oa/vote/voteproblem.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        super.duplicate(key, forward);

        saveEntity(getEntity(), true);
        setNew$(false);

        return null;
    }

    @Override
    public VoteProblem clone(VoteProblem entity) throws Exception
    {
        VoteProblem problem = entity.cloneProblem();
        problem.setVoteId(entity.getVoteId());
        problem.setProblemName(problem.getProblemName() + "(复制)");

        return problem;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        //排序要过虑的字段
        // 如果不设则会出现将所有的投票信息中的投票问题全都加载的错误
        if (groupId == null)
            return new String[]{"voteId"};
        else
            return new String[]{"groupId"};
    }
}
