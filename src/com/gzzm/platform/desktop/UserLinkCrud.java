package com.gzzm.platform.desktop;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.CrudUtils;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户自定义连接的维护
 *
 * @author camel
 * @date 2010-6-5
 */
@Service(url = "/desktop/userLink")
public class UserLinkCrud extends UserOwnedNormalCrud<UserLink, Long>
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    private String page;

    private MenuTreeModel menuTree;

    @Store(scope = "user")
    private Integer type;

    @Inject
    private UserLinkDao dao;

    private String groupName;

    @NotCondition
    private Integer groupId;

    private List<UserLinkGroup> displayGroups;

    /**
     * 批量添加是提交过来的菜单列表
     */
    private String[] appIds;

    public UserLinkCrud()
    {
        setPageSize(-1);

        addOrderBy("userId");
        addOrderBy("linkGroup.orderId");
        addOrderBy("orderId");
    }

    @Override
    @NotSerialized
    @NotCondition
    public Integer getUserId()
    {
        return super.getUserId();
    }

    @NotSerialized
    @Select(field = "groupName")
    public List<String> getGroupNames() throws Exception
    {
        return dao.getGroupNames(userOnlineInfo.getUserId());
    }

    @NotSerialized
    @Select(field = "groupId")
    public List<UserLinkGroup> getGroups() throws Exception
    {
        List<UserLinkGroup> linkGroups = dao.getGroups(userOnlineInfo.getUserId());

        if (linkGroups.size() > 0)
        {
            UserLinkGroup userLinkGroup = new UserLinkGroup();
            userLinkGroup.setGroupId(0);
            userLinkGroup.setGroupName("未分组");
            linkGroups.add(0, userLinkGroup);
        }

        return linkGroups;
    }

    @Service
    public List<UserLinkGroup> getDisplayGroups() throws Exception
    {
        if (displayGroups == null && "list".equals(page))
        {
            displayGroups = new ArrayList<UserLinkGroup>();
            List<UserLink> userLinks = dao.getUserLinks(getUserId(), 0);

            for (UserLink userLink : userLinks)
            {
                if (accept(userLink))
                {
                    Integer groupId = userLink.getGroupId();
                    UserLinkGroup linkGroup = null;
                    if (groupId != null)
                    {
                        linkGroup = dao.getGroup(groupId);
                        if (linkGroup == null)
                            groupId = null;
                    }
                    if (groupId == null)
                        groupId = 0;

                    boolean b = false;
                    for (UserLinkGroup linkGroup1 : displayGroups)
                    {
                        if (linkGroup1.getGroupId().equals(groupId))
                        {
                            b = true;
                            break;
                        }
                    }

                    if (!b)
                    {
                        if (linkGroup == null)
                        {
                            linkGroup = new UserLinkGroup();
                            linkGroup.setGroupId(0);
                            linkGroup.setGroupName("未分组");
                        }

                        displayGroups.add(linkGroup);
                    }
                }
            }
        }

        return displayGroups;
    }

    @Override
    public String getOrderField()
    {
        if ("list".equals(page))
            return null;
        else
            return "orderId";
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        if ("list".equals(page))
        {
            if (groupId == null)
            {
                List<UserLinkGroup> linkGroups = getDisplayGroups();
                if (linkGroups.size() > 1)
                {
                    groupId = linkGroups.get(0).getGroupId();
                }
            }
        }
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String[] getAppIds()
    {
        return appIds;
    }

    public void setAppIds(String[] appIds)
    {
        this.appIds = appIds;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s;

        if ("list".equals(page))
        {
            if (type == null || type == 0)
                s = "userId in (:userId,0)";
            else if (type == 1)
                s = "userId=:userId";
            else
                s = "userId=0";
        }
        else
        {
            s = "userId=:userId";
        }

        if (!Null.isNull(groupId))
        {
            s += " and ";
            if (groupId == 0)
                s += "(linkGroup.groupName is null)";
            else
                s += "groupId=:groupId";
        }

        return s;
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        String s = "userId=:userId and ";

        if (groupId == 0)
        {
            s += "linkGroup.groupName is null";
        }
        else
        {
            s += "groupId=:groupId";
        }

        return s;
    }

    @Override
    @Forwards({@Forward(name = "link", page = Pages.EDIT),
            @Forward(name = "menu", page = Pages.EDIT),
            @Forward(name = "menus", page = Pages.EDIT)})
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Service(method = HttpMethod.post)
    public void saveMenus() throws Exception
    {
        if (appIds != null)
        {
            MenuContainer menuContainer = menuContainerProvider.get();

            Integer groupId = null;
            if (!StringUtils.isEmpty(groupName))
            {
                UserLinkGroup linkGroup = dao.getGroupByName(userOnlineInfo.getUserId(), groupName);
                if (linkGroup == null)
                {
                    linkGroup = new UserLinkGroup();
                    linkGroup.setUserId(userOnlineInfo.getUserId());
                    linkGroup.setGroupName(getGroupName());
                    linkGroup.setOrderId(CrudUtils.getOrderValue(6, true));
                    dao.add(linkGroup);
                }

                groupId = linkGroup.getGroupId();
            }

            for (String appId : appIds)
            {
                MenuItem menu = menuContainer.getMenu(appId);

                UserLink userLink = new UserLink();
                userLink.setAppId(appId);
                userLink.setTitle(menu.getTitle());
                userLink.setUserId(getUserId());
                userLink.setGroupId(groupId);

                CrudUtils.initOrderValue(userLink, this, true);

                dao.add(userLink);
            }
        }
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        UserLinkGroup linkGroup = getEntity().getLinkGroup();
        if (linkGroup != null)
            groupName = linkGroup.getGroupName();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.afterSave();

        if (getEntity().getTarget() == null)
        {
            getEntity().setTarget(
                    StringUtils.isEmpty(getEntity().getAppId()) ? UserLinkTarget.blank : UserLinkTarget.tab);
        }

        if (!StringUtils.isEmpty(groupName))
        {
            UserLinkGroup linkGroup = dao.getGroupByName(userOnlineInfo.getUserId(), groupName);
            if (linkGroup == null)
            {
                linkGroup = new UserLinkGroup();
                linkGroup.setUserId(userOnlineInfo.getUserId());
                linkGroup.setGroupName(getGroupName());
                linkGroup.setOrderId(CrudUtils.getOrderValue(6, true));
                dao.add(linkGroup);
            }

            getEntity().setGroupId(linkGroup.getGroupId());
        }
        else
        {
            getEntity().setGroupId(Null.Integer);
        }

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        if ("list".equals(page))
        {
            SimplePageListView view = new SimplePageListView();

            view.setDisplay(new CHref("${title}").setAction("openHref('${appId}','${url}','${target.toString()}')"));

            view.importJs("/platform/desktop/userlink.js");
            view.importCss("/platform/desktop/userlink.css");

            boolean b1 = false;
            boolean b2 = false;

            List<UserLink> userLinks;
            if (type == null || type == 0)
                userLinks = dao.getUserLinks(getUserId(), 0);
            else if (type == 1)
                userLinks = dao.getUserLinks(getUserId());
            else
                userLinks = dao.getUserLinks(0);

            for (UserLink userLink : userLinks)
            {
                if (accept(userLink))
                {
                    if (userLink.getUserId() == 0)
                        b1 = true;
                    else
                        b2 = true;

                    if (b1 && b2)
                        break;
                }
            }

            if (b1 && b2)
            {
                view.addAction("显示所有链接", "showAll()", "");
                view.addAction("仅显示自定义链接", "showSelf()", "");
            }

            return view;
        }
        else
        {
            PageTableView view = new PageTableView();

            view.addComponent("分组", "groupId");

            view.addColumn("标题", "title").setWidth("250");
            view.addColumn("链接", new CHref("${hrefText}")
                    .setAction("openHref('${appId}','${url}','${title}','${target.toString()}')"))
                    .setAutoExpand(true).setAlign(Align.left);
            view.addColumn("打开方式", "target");
            view.addColumn("所属分组", "linkGroup.groupName");

            view.addButton(Buttons.query());
            view.addButton(Buttons.add("menu", "新增菜单链接").setProperty("id", "add_menu"));
            view.addButton(Buttons.add("menus", "批量新增菜单链接").setProperty("id", "add_menus"));
            view.addButton(Buttons.add("link", "新增URL链接").setProperty("id", "add_link"));
            view.addButton(Buttons.delete());
            view.addButton(Buttons.sort());
            view.addButton(new CButton("分组管理", "editGroups();"));
            view.makeEditable();

            view.importJs("/platform/desktop/userlink.js");

            return view;
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();


        if ("menus".equals(getForward()))
        {
            view.addComponent("菜单", "this.appIds").setProperty("require", null);
            view.addComponent("分组", "this.groupName").setProperty("editable", "true");

            view.addButton("crud.save", "saveMenus()");
            view.addButton(Buttons.close());

            view.importCss("/platform/desktop/userlink.css");
            view.importJs("/platform/desktop/userlink.js");
        }
        else if ("menu".equals(getForward()) || !StringUtils.isEmpty(getEntity().getAppId()))
        {
            view.addComponent("标题", "title");
            view.addComponent("菜单", "appId").setProperty("text", getEntity().getMenuTitle())
                    .setProperty("require", null).setProperty("onchange", "menuChange();");
            view.addComponent("分组", "this.groupName").setProperty("editable", "true");
            view.addDefaultButtons();

            view.importCss("/platform/desktop/userlink.css");
            view.importJs("/platform/desktop/userlink.js");
        }
        else
        {
            view.addComponent("标题", "title");
            view.addComponent("连接", "url");
            view.addComponent("打开方式", "target");
            view.addComponent("分组", "this.groupName").setProperty("editable", "true");
            view.addDefaultButtons();
        }


        return view;
    }

    @Select(field = {"entity.appId", "appIds"})
    public MenuTreeModel getMenuTree()
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setCheckable("menus".equals(getForward()));
            menuTree.setGroup("oa");
            menuTree.setLeafOnly(true);
        }

        return menuTree;
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        if (!userOnlineInfo.isAdmin())
        {
            for (Iterator<UserLink> iterator = getList().iterator(); iterator.hasNext(); )
            {
                UserLink userLink = iterator.next();

                if (!accept(userLink))
                {
                    iterator.remove();
                }
            }
        }
    }

    private boolean accept(UserLink userLink)
    {
        return userOnlineInfo.isAdmin() ||
                StringUtils.isEmpty(userLink.getAppId()) || userOnlineInfo.isAccessable(userLink.getAppId());
    }
}
