package com.gzzm.portal.cms.station;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 站点模板维护
 *
 * @author camel
 * @date 2011-5-3
 */
@Service(url = "/portal/stationtemplate")
public class StationTemplateCrud extends BaseNormalCrud<StationTemplate, Integer>
{
    @UserId
    private Integer userId;

    @Like
    private String templateName;

    @Like
    private String path;

    public StationTemplateCrud()
    {
        setLog(true);
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreator(userId);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("模板名称", "templateName");
        view.addComponent("模板路径", "path");

        view.addColumn("模板名称", "templateName");
        view.addColumn("模板路径", "path");
        view.addColumn("创建人", "createUser.userName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("模板名称", "templateName");
        view.addComponent("模板路径", "path");
        view.addComponent("主页路径", "homePath");
        view.addComponent("通用栏目模板路径", "channelPath");
        view.addComponent("通用文章模板路径", "textPath");

        view.addDefaultButtons();

        return view;
    }
}
