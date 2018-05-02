package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.*;

/**
 * 投票选项Crud
 *
 * @author db
 * @date 11-12-7
 */
@Service(url = "/oa/vote/Option")
public class VoteOptionCrud extends SubListCrud<VoteOption, Integer>
{
    /**
     * 接收页面传来的投票问题ID
     */
    private Integer problemId;

    public VoteOptionCrud()
    {
    }

    public Integer getProblemId()
    {
        return problemId;
    }

    public void setProblemId(Integer problemId)
    {
        this.problemId = problemId;
    }

    @Override
    protected String getParentField()
    {
        return "problemId";
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if (getEntity().isOpinion() == null)
            getEntity().setOpinion(false);

        return true;
    }

    @Override
    protected void initListView(SubListView view) throws Exception
    {
        view.addColumn("选项内容", "optionName");
        view.addColumn("分数", "score");
        view.addColumn("说明", "remark");
        view.addColumn("允许填意见", "opinion");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("选项内容", "optionName");
        view.addComponent("分数", "score");
        view.addComponent("说明", new CTextArea("remark"));
        view.addComponent("允许填意见", new CCheckbox("opinion"));

        view.addDefaultButtons();

        return view;
    }
}
