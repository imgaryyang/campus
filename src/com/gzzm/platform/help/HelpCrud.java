package com.gzzm.platform.help;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2010-12-12
 */
@Service(url = "/HelpCrud")
public class HelpCrud extends BaseNormalCrud<Help, Integer>
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Like
    private String content;

    @Like
    private String url;

    private String appId;

    private MenuTreeModel menuTree;

    public HelpCrud()
    {
        addOrderBy("helpId");
        setLog(true);
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (!StringUtils.isEmpty(getEntity().getAppId()))
            getEntity().setUrl("");

        return true;
    }

    @Override
    public void afterChange() throws Exception
    {
        Help.setUpdated();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("菜单", "appId");
        view.addComponent("URL", "url");
        view.addComponent("内容", "content");

        view.addColumn("菜单或URL", "(appId==null||appId.length()==0)?url:menuTitle").setWidth("300");
        view.addColumn("内容", "content");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("菜单", "appId").setProperty("text", getEntity().getMenuTitle());
        view.addComponent("URL", "url");
        view.addComponent("内容", new CTextArea("content"));

        view.importCss("/platform/help/helpcrud.css");

        view.addDefaultButtons();

        return view;
    }

    @Select(field = {"entity.appId", "appId"})
    public MenuTreeModel getMenuTree()
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setCheckable(false);
            menuTree.setGroup("oa");
        }

        return menuTree;
    }
}
