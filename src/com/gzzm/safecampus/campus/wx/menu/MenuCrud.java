package com.gzzm.safecampus.campus.wx.menu;

import com.gzzm.platform.commons.crud.BaseTreeCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTreeView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * 微信菜单管理
 *
 * @author Neo
 * @date 2018/4/8 11:49
 */
@Service(url = "/campus/wx/menu/menu")
public class MenuCrud extends BaseTreeCrud<Menu, Integer>
{

    @Inject
    private MenuDao menuDao;

    /**
     * 所属分组
     */
    private Integer groupId;

    public MenuCrud()
    {
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    @Override
    public Menu getRoot() throws Exception
    {
        return menuDao.getRoot();
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public void initEntity(Menu entity) throws Exception
    {
        super.initEntity(entity);
        entity.setGroupId(groupId);
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();

        view.addComponent("菜单名称", "text");
        view.addButton(Buttons.query());
        view.defaultInit();
        return view;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "menuName";
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("菜单名称", "menuName");
        view.addComponent("类型", "type");
        view.addComponent("登陆后跳转", "login");
        view.addComponent("url", new CTextArea("url"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public void afterChange() throws Exception
    {
        Menu.setUpdated();
    }
}
