package com.gzzm.platform.opinion;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.view.components.CTextArea;

/**
 * @author camel
 * @date 14-1-24
 */
@Service(url = "/opinion/common/crud")
public class CommonOpinionCrud extends DeptOwnedNormalCrud<CommonOpinion, Integer>
{
    public CommonOpinionCrud()
    {
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        CommonOpinion entity = getEntity();
        if (StringUtils.isEmpty(entity.getTitle()))
            entity.setTitle(entity.getContent());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        getEntity().setTitle(getEntity().getContent());

        return super.beforeUpdate();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("意见标题", "title").setWidth("300");
        view.addColumn("意见内容", "content");

        view.defaultInit(false);
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("意见标题", "title");
        view.addComponent("意见内容", new CTextArea("content"));
        view.addDefaultButtons();

        return view;
    }
}
