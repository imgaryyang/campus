package com.gzzm.platform.menu;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

/**
 * 菜单权限维护
 *
 * @author camel
 * @date 2009-12-21
 */
@Service(url = "/MenuAuth")
public class MenuAuthCrud extends BaseNormalCrud<MenuAuth, Integer>
{
    private static final String[] ORDERWITHFIELDS = new String[]{"menuId"};

    @Inject
    private MenuDao dao;

    private String menuId;

    private String menuTitle;

    public MenuAuthCrud()
    {
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
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
    }

    @Override
    public void initEntity(MenuAuth entity) throws Exception
    {
        entity.setMenuId(menuId);
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

    /**
     * 初始化权限列表
     *
     * @return 初始化后菜单权限是否变化，变化返回true，没有变化返回false
     * @throws Exception 初始化异常
     */
    @Service
    public boolean initAuths() throws Exception
    {
        Menu menu = dao.getMenu(menuId);
        boolean result = menu.initAuths();
        dao.update(menu);

        return result;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.importJs("/platform/menu/menucrud.js");

        view.setTitle("菜单操作管理-{menuTitle}");

        view.addColumn("操作代码", "authCode");
        view.addColumn("操作名称", "authName");
        view.addColumn("URL维护", new CButton("URL维护", "editUrl(${authId})"));

        view.defaultInit();
        view.addButton(Buttons.sort());
        view.addButton(new CButton("初始化", "initAuths()"));

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("操作代码", "authCode");
        view.addComponent("操作名称", "authName");

        view.addDefaultButtons();

        return view;
    }
}
