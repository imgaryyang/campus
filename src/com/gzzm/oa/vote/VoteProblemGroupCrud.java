package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CHref;

import java.util.*;

/**
 * 投票问题分组管理
 *
 * @author camel
 * @date 12-3-30
 */
@Service(url = "/oa/vote/ProblemGroup")
public class VoteProblemGroupCrud extends BaseNormalCrud<VoteProblemGroup, Integer>
{
    /**
     * 投票ID
     */
    private Integer voteId;

    @Like
    private String groupName;

    public VoteProblemGroupCrud()
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

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"voteId"};
    }

    @Override
    public void initEntity(VoteProblemGroup entity) throws Exception
    {
        super.initEntity(entity);

        entity.setVoteId(voteId);
    }

    @Override
    public VoteProblemGroup clone(VoteProblemGroup entity) throws Exception
    {
        VoteProblemGroup group = super.clone(entity);

        group.setGroupName(group.getGroupName() + "(复制)");

        List<VoteProblem> problems = new ArrayList<VoteProblem>(entity.getProblems().size());
        for (VoteProblem problem : entity.getProblems())
        {
            VoteProblem problem1 = problem.cloneProblem();
            problem1.setVoteId(voteId);
            problems.add(problem1);
        }
        group.setProblems(problems);

        return group;
    }

    @Override
    public String duplicate(Integer key, String forward) throws Exception
    {
        String s = super.duplicate(key, forward);

        saveEntity(getEntity(), true);
        setNew$(false);

        return s;
    }


    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.setTitle("问题分组管理");

        view.addComponent("分组名称", "groupName");

        view.addColumn("分组名称", "groupName");
        view.addColumn("问题管理", new CHref("问题管理").setAction("forwardProblem(${groupId})"));

        view.defaultInit();

        view.importJs("/oa/vote/problemgroup.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("分组名称", "groupName");

        view.addDefaultButtons();

        view.importJs("/oa/vote/problemgroup.js");

        return view;
    }
}
