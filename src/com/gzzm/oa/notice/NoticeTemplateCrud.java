package com.gzzm.oa.notice;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * 信息模板管理
 *
 * @author camel
 * @date 2016/9/28
 */
@Service(url = "/oa/notice/template")
public class NoticeTemplateCrud extends BaseNormalCrud<NoticeTemplate, Integer>
{
    public NoticeTemplateCrud()
    {
        setLog(true);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 5;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("模板名称", "templateName");
        view.addColumn("模板路径", "templatePath");
        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("模板名称", "templateName");
        view.addComponent("模板路径", "templatePath");

        view.addDefaultButtons();

        return view;
    }
}
