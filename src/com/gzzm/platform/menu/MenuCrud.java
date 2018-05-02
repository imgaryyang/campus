package com.gzzm.platform.menu;

import com.gzzm.platform.annotation.MenuTitle;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.util.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 菜单的crud
 *
 * @author camel
 * @date 2009-10-10
 */
@Service(url = "/MenuCrud")
public class MenuCrud extends BaseTreeCrud<Menu, String>
{
    static
    {
        ParameterCheck.addNoCheckURL("/MenuCrud");
    }

    @Inject
    private MenuDao dao;

    private String group;

    @MenuTitle
    private String menuTitle;

    @Select(field = "entity.defaultMenuId")
    private MenuTreeModel menuTree;

    public MenuCrud()
    {
        setDuplicateChildren(true);
    }

    public Menu getRoot() throws Exception
    {
        Menu menu = dao.getMenu(getGroup());
        if (menu == null)
        {
            menu = new Menu(getGroup(), StringUtils.isEmpty(menuTitle) ? "根节点" : menuTitle);
            dao.save(menu);
        }

        return menu;
    }

    public MenuTreeModel getMenuTree() throws Exception
    {
        if (menuTree == null && getEntity() != null && getEntity().getMenuId() != null)
        {
            menuTree = Tools.getBean(MenuTreeModel.class);
            menuTree.setRootId(getEntity().getMenuId());
            menuTree.setCheckable(false);
        }
        return menuTree;
    }

    @Override
    protected String getParentField() throws Exception
    {
        return "parentMenuId";
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    @Override
    public void setString(Menu menu, String s) throws Exception
    {
        menu.setMenuTitle(s);
    }

    @Override
    public Menu clone(Menu menu) throws Exception
    {
        Menu c = super.clone(menu);

        if (!"duplicate".equals(getAction()))
            cloneMore(menu, c);

        String menuId = menu.getMenuId();
        if (menuId != null && !DataConvert.isInteger(menuId))
            c.setMenuId(menuId);

        return c;
    }

    private void cloneMore(Menu source, Menu taregt) throws Exception
    {
        //复制图标
        if (taregt.getIcon() == null)
            taregt.setIcon(source.getIcon());

        //复制权限
        SortedSet<MenuAuth> auths = new TreeSet<MenuAuth>();
        for (MenuAuth auth : source.getAuths())
            auths.add(auth.clone());

        taregt.setAuths(auths);
    }

    @Override
    public void initEntity(Menu entity) throws Exception
    {
        super.initEntity(entity);

        entity.setValid(true);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        String menuId = getEntity().getMenuId();
        if (menuId != null)
        {
            if (menuId.trim().length() == 0)
                getEntity().setMenuId(null);
            else
                getEntity().setHidden(true);
        }

        //保存之前复制图片和权限信息
        if (getDuplicateKey() != null)
            cloneMore(dao.getMenu(getDuplicateKey()), getEntity());

        return true;
    }

    @Override
    public void afterChange() throws Exception
    {
        Menu.setUpdated();
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public void setKey(Menu entity, String key) throws Exception
    {
        if (key == null)
        {
            String oldKey = entity.getMenuId();
            if (oldKey != null && !DataConvert.isInteger(oldKey))
                return;
        }

        super.setKey(entity, key);
    }

    /**
     * 显示菜单图片的方法
     *
     * @param menuId 菜单ID
     * @param count  在图标右上角显示的小数字，如果为0表示不显示数字，使用原图大小，-1表示仅把图片放大，不加数字
     * @return 菜单图标的字节数组
     * @throws Exception 数据库读取数据异常
     */
    @Service(url = "/MenuCrud/icon/{$0}?count={$1}")
    public byte[] getIcon(String menuId, int count) throws Exception
    {
        byte[] bs = dao.getMenu(menuId).getIcon();

        if (bs != null && count != 0)
        {
            String text = count < 0 ? "" : count > 100 ? "99+" : "8";
            int n = text.length() - 1;

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bs));

            BufferedImage image1 =
                    new BufferedImage(image.getWidth() + 14, image.getHeight() + 14, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = (Graphics2D) image1.getGraphics();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0f));
            g.setBackground(Color.white);
            g.fillRect(0, 0, image1.getWidth(), image1.getHeight());

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.drawImage(image, 7, 7, null);

            if (count > 0)
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.white);
                g.fillRoundRect(image1.getWidth() - 22 - 5 * n, 0, 22 + 5 * n, 22, 22, 22);

                g.setColor(Color.red);
                g.fillRoundRect(image1.getWidth() - 19 - 5 * n, 3, 16 + 5 * n, 16, 16, 16);

                g.setFont(new Font("Default", Font.BOLD, 12));
                g.setColor(Color.white);
                g.drawString(text, image1.getWidth() - 14 - 6 * n, 15);
            }

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(image1, "png", bout);
            bs = bout.toByteArray();
        }

        return bs;
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        if ("exp".equals(getAction()))
        {
            PageTreeTableView view = new PageTreeTableView();

            view.addColumn("菜单标题", "menuTitle");
            view.addColumn("URL", "url");
            view.addColumn("菜单提示", "hint");
            view.addColumn("功能标题", "appTitle");
            view.addColumn("功能说明", "appRemark");
            return view;
        }
        else
        {
            PageTreeView view = new PageTreeView();
            view.importJs("/platform/menu/menucrud.js");

            view.defaultInit();
            view.addButton(Buttons.export("xls"));
            view.addButton(Buttons.copy());
            view.addButton(Buttons.paste());
            view.addButton(new CButton("操作维护", "editAuth()"));
            view.addButton(new CButton("URL维护", "editUrl()"));
            return view;
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        boolean hidden = false;

        view.addComponent("菜单标题", "menuTitle");

        if (!isNew$())
            hidden = getEntity().isHidden() != null && getEntity().isHidden();

        if (!hidden)
        {
            view.addComponent("URL", "url");
        }

        if (isNew$())
            view.addComponent("菜单ID", "menuId");

        if (hidden)
            view.addComponent("菜单ID", "menuId").setProperty("readOnly", null);

        if (!hidden)
        {
            view.addComponent("菜单提示", "hint");
            view.addComponent("图标", new CFile("icon")).setFileType("$image");
            view.addComponent("当前图标", new CImage(getEntity().getIcon() == null ? Tools.getCommonIcon("blank") :
                    "/MenuCrud/icon/" + getEntity().getMenuId()).setProperty("id", "icon").setWidth(17).setHeight(17));
            view.addComponent("默认菜单", "defaultMenuId").setProperty("text",
                    getEntity().getDefaultMenu() == null ? "" : getEntity().getDefaultMenu().getMenuTitle());
            view.addComponent("功能标题", "appTitle");
            view.addComponent("功能说明", "appRemark");
            view.addComponent("查询条件", "condition");
            view.addComponent("待办条件", "countCondition");
            view.addComponent("有效", "valid");
        }

        view.importJs("/platform/menu/menucrud.js");
        view.addDefaultButtons();

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("菜单列表");
    }
}
