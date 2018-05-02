package com.gzzm.platform.menu;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Equals;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * 菜单权限url维护
 *
 * @author camel
 * @date 2009-12-23
 */
@Service(url = "/MenuAuthUrl")
public class MenuAuthUrlCrud extends BaseNormalCrud<MenuAuthUrl, Integer>
{
    @Inject
    private MenuDao dao;

    @Equals("auth.menuId")
    private String menuId;

    private Integer authId;

    private String menuTitle;

    public MenuAuthUrlCrud()
    {
        addOrderBy("urlName");
    }

    public Integer getAuthId()
    {
        return authId;
    }

    public void setAuthId(Integer authId)
    {
        this.authId = authId;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public String getMenuTitle() throws Exception
    {
        if (menuTitle == null)
            menuTitle = dao.getMenuTitle(menuId);
        return menuTitle;
    }

    @Override
    public void initEntity(MenuAuthUrl entity) throws Exception
    {
        entity.setAuthId(authId);
    }

    @Override
    public void afterDelete(Integer key, boolean exists) throws Exception
    {
        Menu.setUpdated();
    }

    @Override
    public void afterDeleteAll() throws Exception
    {
        Menu.setUpdated();
    }

    @Select(field = {"authId", "entity.authId"})
    @NotSerialized
    public Collection<MenuAuth> getAuths() throws Exception
    {
        return dao.getMenu(menuId).getAuths();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.importJs("/platform/menu/menucrud.js");

        view.setTitle("菜单URL管理-{menuTitle}");
        view.setTitle("菜单URL管理-{menuTitle}");

        view.addComponent("权限", "authId");

        view.addColumn("URL名称", "urlName");
        view.addColumn("权限", "auth.authName");
        view.addColumn("URL", "url").setAutoExpand(true);
        view.addColumn("METHOD", "method");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("URL名称", "urlName");
        view.addComponent("所属权限", "authId");
        view.addComponent("URL", "url");
        view.addComponent("METHOD", "method");

        view.addDefaultButtons();

        return view;
    }
}
