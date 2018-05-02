package com.gzzm.platform.help;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * 新功能提醒维护
 *
 * @author camel
 * @date 2010-12-13
 */
@Service(url = "/RemindCrud")
public class RemindCrud extends BaseNormalCrud<Remind, Integer>
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Like
    private String content;

    private String appId;

    private Boolean valid;

    private MenuTreeModel menuTree;

    public RemindCrud()
    {
        addOrderBy("remindId");
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

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("关联菜单", "appId");
        view.addComponent("内容", "content");
        view.addComponent("有效", "valid");

        view.addColumn("关联菜单", "menuTitle").setWidth("150");
        view.addColumn("内容", "content");
        view.addColumn("创建时间", "createTime");
        view.addColumn("有效", "valid");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("菜单", "appId").setProperty("text", getEntity().getMenuTitle())
                .setProperty("require", null);
        view.addComponent("内容", new CTextArea("content"));
        view.addComponent("有效", "valid");

        view.addDefaultButtons();

        view.importCss("/platform/help/remind.css");

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
