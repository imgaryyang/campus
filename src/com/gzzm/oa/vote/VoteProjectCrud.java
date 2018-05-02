package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-3-29
 */
@Service(url = "/oa/vote/Project")
public class VoteProjectCrud extends DeptOwnedTreeCrud<VoteProject, Integer>
{
    @Inject
    private VoteDao dao;

    private Integer typeId;

    public VoteProjectCrud()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    @Override
    public VoteProject getRoot() throws Exception
    {
        return dao.getRootProject();
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "projectName";
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("项目名称", "projectName");

        view.addDefaultButtons();

        return view;
    }
}
