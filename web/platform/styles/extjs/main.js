Cyan.importJs("render.js");

System.Menu.items = [];
System.Menu.create = function (menuTree, group, showSize)
{
    if (!showSize)
        showSize = 10;
    System.Menu.showSize = showSize;
    System.Menu.group = group;
    Ext.QuickTips.init();
    Ext.BLANK_IMAGE_URL = Cyan.getRealPath("../../extjs/resources/images/default/tree/s.gif");
    System.Menu.topMenus = [];
    var n = menuTree.length;
    for (var i = 0; i < n; i++)
    {
        var menu = menuTree[i];
        System.Menu.initMenu(menu);
        if (!menu.hidden)
        {
            System.Menu.menus[menu.menuId] = menu;
            menu.node = new Ext.tree.TreeNode({
                id: menu.menuId,
                text: menu.title,
                leaf: false
            });
            var treeConfig = {
                title: menu.title,
                rootVisible: false,
                border: false,
                root: menu.node,
                autoScroll: true,
                id: menu.menuId
            };
            if (menu.iconPath)
            {
                var iconCls = "menu_icon_" + menu.menuId;
                try
                {
                    Cyan.addStyle("." + iconCls, "background-image:url(\"" + menu.iconPath + "\")");
                }
                catch (e)
                {
                    //
                }
                treeConfig.iconCls = iconCls;
            }
            menu.tree = new Ext.tree.TreePanel(treeConfig);
            System.Menu.items.push(menu.tree);
            System.Menu.createChildren.apply(menu);
            menu.tree.on("click", System.Menu.treeclick);
            menu.tree.on("beforeexpand", System.Menu.treeexpand);
            menu.tree.on("beforecollapse", System.Menu.treecollapse);

            menu.hidden = menu.tree.hidden = i >= showSize;
            System.Menu.topMenus.push(menu);
        }
    }
};
System.Menu.createChildren = function ()
{
    if (this.children)
    {
        var n = this.children.length;
        for (var i = 0; i < n; i++)
        {
            var menu = this.children[i];
            System.Menu.initMenu(menu);
            if (!menu.hidden)
            {
                System.Menu.menus[menu.menuId] = menu;
                var item = {
                    id: menu.menuId,
                    text: menu.title,
                    qtip: menu.hint,
                    leaf: !(menu.children && menu.children.length)
                };
                if (menu.iconPath)
                    item.icon = menu.iconPath;
                menu.node = new Ext.tree.TreeNode(item);
                this.node.appendChild(menu.node);
                System.Menu.createChildren.apply(menu);
            }
        }
    }
};
System.Menu.addIcon = function (menu, hidden)
{
    var icon = document.createElement("IMG");

    icon.src = menu.iconPath;
    icon.title = menu.title;
    icon.menuId = menu.menuId;
    icon.onclick = System.Menu.iconClick;
    if (hidden)
        icon.style.display = "none";

    $("menu_bottom").appendChild(icon);
    menu.icon = icon;
};
System.Menu.showMoreMenu = function ()
{
    for (var i = 0; i < System.Menu.topMenus.length; i++)
    {
        var menu = System.Menu.topMenus[i];
        if (menu.hidden)
        {
            menu.hidden = false;
            menu.tree.setVisible(true);
            menu.icon.style.display = "none";
            System.Menu.menuPanel.doLayout();

            System.Menu.showSize++;
            System.Menu.updateShowSize();
            break;
        }
    }
};
System.Menu.showLessMenu = function ()
{
    var lastMenu;
    for (i = 0; i < System.Menu.topMenus.length; i++)
    {
        var menu = System.Menu.topMenus[i];
        if (menu.hidden)
            break;
        lastMenu = menu;
    }

    if (lastMenu)
    {
        lastMenu.hidden = true;
        if (lastMenu.tree.collapsed)
            lastMenu.tree.setVisible(false);
        lastMenu.icon.style.display = "";
        System.Menu.showSize--;
        System.Menu.updateShowSize();
        System.Menu.menuPanel.doLayout();
    }
};
System.Menu.updateShowSize = function ()
{
    Cyan.Arachne.post("/menuconfig/" + System.Menu.group + "/size", {
        size: System.Menu.showSize
    }, null);
};
System.Menu.configMenu = function ()
{
    System.showModal("/menuconfig/" + System.Menu.group, function (menuIds)
    {
        if (menuIds)
            System.Menu.sortMenu(menuIds);
    });
};
System.Menu.sortMenu = function (menuIds)
{
    if (System.Menu.topMenus.length != menuIds.length)
    {
        window.parent.location.reload();
    }

    for (var i = 0; i < menuIds.length; i++)
    {
        var menuId = menuIds[i];
        var menu = System.Menu.menus[menuId];
        if (menu)
        {
            menu.hidden = i >= System.Menu.showSize;
            System.Menu.topMenus[i] = menu;
        }
        else
        {
            window.parent.location.reload();
            break;
        }
    }

    System.Menu.refreshMenu();
};
System.Menu.refreshMenu = function ()
{
    var menus = System.Menu.topMenus;
    var menu_bottom = $("menu_bottom");
    var moreMenu = System.Menu.moreMenu;
    var moreMenuNode = moreMenu.items.itemAt(0).el.dom.parentNode.parentNode;

    var b = false;
    for (var i = 0; i < menus.length; i++)
    {
        var menu = menus[i];

        menu.icon.style.display = menu.hidden ? "" : "none";

        var icon = menu_bottom.childNodes[i];
        if (icon && icon != menu.icon)
            menu_bottom.insertBefore(menu.icon, icon);

        if (menu.tree.collapsed)
        {
            menu.tree.setVisible(!menu.hidden);
            var node = menu.tree.el.dom;
            var node1 = node.parentNode.childNodes[i + (b ? 1 : 2)];
            if (node1 && node1 != node)
                node.parentNode.insertBefore(node, node1);
        }
        else
        {
            b = true;
        }

        for (var j = 4; j < moreMenu.items.length; j++)
        {
            var menuItem = moreMenu.items.itemAt(j);
            if (menuItem.menuId == menu.menuId)
            {
                var menuItemNode = menuItem.el.dom.parentNode;
                var menuItemNode1 = moreMenuNode.childNodes[4 + i];
                if (menuItemNode1 && menuItemNode1 != menuItemNode)
                    moreMenuNode.insertBefore(menuItemNode, menuItemNode1);
                break;
            }
        }
    }

    System.Menu.menuPanel.doLayout();
};
System.Menu.treeclick = function (node)
{
    System.Menu.menus[node.id].go();
};
System.Menu.treeexpand = function (item)
{
    var menu = System.Menu.menus[item.id];
    if (!menu.url || menu.url.indexOf("javascript:") != 0 && menu.url.indexOf("://") <= 0)
        item.expending = true;
    menu.go();
    if (menu.url && (menu.url.indexOf("javascript:") == 0 || menu.url.indexOf("://") > 0))
        return false;

    var node = item.el.dom;
    var firstNode = node.parentNode.childNodes[0];
    if (node != firstNode)
        node.parentNode.insertBefore(node, firstNode);
};
System.Menu.treecollapse = function (item)
{
    var node = item.el.dom;
    var firstNode = node.parentNode.childNodes[0];
    var menus = System.Menu.topMenus;
    var b = false;
    for (var i = 0; i < menus.length; i++)
    {
        var menu = menus[i];
        if (!b && menu.tree.el.dom == firstNode)
        {
            b = true;
        }

        if (menu.menuId == item.id)
        {
            if (menu && menu.hidden)
                item.setVisible(false);

            if (i)
            {
                if (b)
                    i++;
                else
                    i += 2;

                var node1 = node.parentNode.childNodes[i];
                if (node1)
                    node.parentNode.insertBefore(node, node1);
                else
                    node.parentNode.appendChild(node);
            }
            break;
        }
    }
};
System.Menu.select = function ()
{
    var tree;
    if (this.tree)
    {
        tree = this.tree;
    }
    else
    {
        this.node.ensureVisible();
        this.node.select();
        tree = this.node.getOwnerTree();
    }

    tree.setVisible(true);
    System.Menu.menuPanel.doLayout();
    if (!tree.expending)
        tree.expand();
    else
        tree.expending = false;
};
System.Menu.iconClick = function ()
{
    System.Menu.menus[this.menuId].select();
};
System.Menu.getTabWindow = function ()
{
    return Cyan.$(this.frameId).contentWindow;
};
System.Menu.openPage = function (url, name, title, win)
{
    if (name)
    {
        var shortcut = System.Shortcut.getShortcut(name);
        if (shortcut)
        {
            shortcut.flash(false);
            shortcut.hideMessage();
        }
    }

    if (!name)
        name = url;
    var tab = System.tabs[name];
    var frame, div;

    if (!tab)
    {
        var frameId, html;

        if (System.Menu.PageIFramesDiv.childNodes.length)
            div = System.Menu.PageIFramesDiv.childNodes[0];

        if (div)
        {
            html = "";
            frame = div.childNodes[1];
            frame.previousSibling.style.display = "";
            frameId = frame.id;
        }
        else
        {
            while (Cyan.$(frameId = "PageFrame$" + Math.random().toString().substring(2)));
            html = "<div style='width:100%;height:100%;overflow:hidden'><div class='cyan-window-loading'" +
            " style='height:100%'>" + Cyan.titles["loading"] + ". . .</div><iframe id='" + frameId +
            "' name='" + frameId + "' width='100%' height='100%' frameborder='0'";
            if (url)
                html += " src='" + Cyan.escapeAttribute(url) + "'";
            html += "></iframe></div>";
        }
        System.tabs[name] = tab = System.tabPanel.add({
            title: title || "加载中...",
            html: html,
            closable: true,
            name: name,
            url: System.clearMenuId(url),
            listeners: {
                beforeshow: System.tabShow,
                beforehide: System.tabHide
            }
        });
        tab.frameId = frameId;
        tab.getWindow = System.Menu.getTabWindow;
        tab.closePage = System.closeTab;
        tab.showFrame = System.showTabFrame;
        tab.opener = win;

        if (url.indexOf(":") > 0)
        {
            tab.showFrame();
        }
        else
        {
            setTimeout(function ()
            {
                var f = function ()
                {
                    try
                    {
                        if (!(this.contentWindow.System && this.contentWindow.System.showFrame))
                        {
                            tab.showFrame();
                        }
                    }
                    catch (ex)
                    {
                        //
                    }
                };
                Cyan.attach(frameId, "load", f);
            }, 10);
        }
    }
    else if (url)
    {
        var url0 = System.clearMenuId(url);
        if (tab.url != url0)
        {
            frame = Cyan.$(tab.frameId);
            frame.previousSibling.style.display = "";
            frame.src = url;
            tab.loaded = false;
            tab.url = url0;
        }
    }
    tab.show();
    if (div)
    {
        tab.body.dom.appendChild(div);
        frame.src = url;
    }
    if (!frame)
        frame = $(tab.frameId);
    if (!frame.pageObject)
        frame.pageObject = tab;

    return tab.frameId;
};
System.Menu.getPage = function (name)
{
    return System.tabs[name || this.menuId];
};
System.closeTab = function ()
{
    System.tabPanel.remove(this, true);
};
System.showTabFrame = function ()
{
    var frame = Cyan.$(this.frameId);
    frame.previousSibling.style.display = "none";
    this.loaded = true;
};
System.tabClose = function (tabs, tab)
{
    if (maximized)
        return false;

    var frame = Cyan.$(tab.frameId);

    try
    {
        var win = frame.contentWindow;
        var onunload = win.onunload || win.document.body.onunload;
        if (onunload)
        {
            if (!onunload())
                return false;
            win.onunload = null;
        }
    }
    catch (e)
    {
    }

    frame.src = Cyan.getRealPath("empty.html");
    frame.pageObject = null;
    System.Menu.PageIFramesDiv.appendChild(frame.parentNode);
    tab.url = null;
    System.tabs[tab.name] = null;

    return true;
};
System.tabShow = function ()
{
    if (System.tabPanel.activeTab == this)
    {
        var shortcut = System.Shortcut.getShortcut(this.name);
        if (shortcut)
            shortcut.flash(false);

        if (this.loaded)
        {
            if (this.getWindow)
            {
                var win = this.getWindow();
                if (win && win.System && win.System.reload)
                    win.System.reload();
            }
        }
        else
        {
            this.loaded = true;
        }
    }
};
System.tabHide = function ()
{
    if (this.getWindow)
    {
        var win = this.getWindow();
        if (win && win.System && win.System.onHide)
            win.System.onHide();
    }
};
System.tabs = {};

Cyan.onload(function ()
{
    var menuTab;
    var tabMenu = new Ext.menu.Menu({
        items: [
            {
                text: "关闭本页",
                handler: function ()
                {
                    System.tabPanel.remove(menuTab, true);
                }
            },
            {
                text: "关闭其它页",
                handler: function ()
                {
                    var items = System.tabPanel.items;
                    var n = items.length - 1;
                    var b = false;
                    for (var i = 0; i < n; i++)
                    {
                        if (b)
                        {
                            System.tabPanel.remove(items.itemAt(2), true);
                        }
                        else
                        {
                            var item = items.itemAt(1);
                            if (item == menuTab)
                                b = true;
                            else
                                System.tabPanel.remove(item);
                        }
                    }
                }
            },
            {
                text: "关闭所有页",
                handler: function ()
                {
                    var items = System.tabPanel.items;
                    var n = items.length - 1;
                    for (var i = 0; i < n; i++)
                        System.tabPanel.remove(items.itemAt(1), true);
                }
            }
        ]
    });

    var groupId = Cyan.getUrlParameter("groupId");
    if (!groupId)
        groupId = "desktop";

    var desktop = Cyan.getUrlParameter("desktop");
    if (!desktop)
        desktop = "desktop";

    System.tabPanel = new Ext.TabPanel({
        region: 'center',
        margins: '5 5 5 0',
        minTabWidth: 115,
        tabWidth: 135,
        enableTabScroll: true,
        defaults: {autoScroll: true},
        activeTab: 0,
        listeners: {
            contextmenu: function (panel, tab, e)
            {
                if (tab != panel.items.itemAt(0))
                {
                    menuTab = tab;
                    tabMenu.showAt([e.getPageX(), e.getPageY()]);
                }
            },
            beforeremove: System.tabClose
        },
        items: [
            {
                title: "桌面",
                html: "<iframe id='desktop' src='" + desktop + ".ptl?groupId=" + groupId +
                "' frameborder='0' width='100%' height='99.8%'></iframe>",
                closable: false,
                scroll: false,
                autoScroll: false,
                listeners: {
                    beforeshow: reloadDesktop
                }
            }
        ]
    });

    System.Menu.menuPanel = new Ext.Panel({
        region: 'center',
        layout: 'accordion',
        border: false,
        items: System.Menu.items,
        layoutConfig: {
            animate: true
        }
    });

    var leftPanel = new Ext.Panel({
        title: '菜单',
        region: 'west',
        margins: '5 0 5 5',
        layout: 'border',
        collapsible: true,
        split: true,
        minSize: 125,
        maxSize: 400,
        width: 175,
        layoutConfig: {
            animate: true
        },
        items: [
            System.Menu.menuPanel,
            {
                layout: 'border',
                region: 'south',
                border: false,
                height: 30,
                items: [
                    {
                        region: 'center',
                        border: false,
                        html: "<div id='menu_bottom'></div>"
                    },
                    {
                        region: 'east',
                        border: false,
                        width: 22,
                        html: "<div id='menu_bottom_right'><span class='icon' title='更多菜单'></span></div>"
                    }
                ]
            }
        ]
    });
    window.viewPort = new Ext.Viewport({
        layout: 'border',
        items: [
            //左边菜单
            leftPanel,
            //上面的头
            {
                region: 'north',
                height: 65,
                html: "<div id='topWrapper'></div>"
            },
            //右边的主窗口
            System.tabPanel
        ]
    });

    System.Menu.PageIFramesDiv = document.createElement("DIV");
    System.Menu.PageIFramesDiv.style.display = "none";
    document.body.appendChild(System.Menu.PageIFramesDiv);

    Cyan.$("topWrapper").appendChild($("top"));

    var moreMenuItems = [
        {
            text: "显示更多菜单",
            icon: "/platform/commons/icons/up.gif",
            handler: System.Menu.showMoreMenu
        },
        {
            text: "显示更少菜单",
            icon: "/platform/commons/icons/down.gif",
            handler: System.Menu.showLessMenu
        },
        {
            text: "个人菜单管理",
            icon: "/platform/commons/icons/config.gif",
            handler: System.Menu.configMenu
        },
        new Ext.menu.Separator()
    ];

    var n = System.Menu.topMenus.length;
    for (var i = 0; i < n; i++)
    {
        var menu = System.Menu.topMenus[i];
        System.Menu.addIcon(menu, i < System.Menu.showSize);

        moreMenuItems.push({
            text: menu.title,
            icon: menu.iconPath,
            menuId: menu.menuId,
            handler: System.Menu.iconClick
        });
    }

    var moreMenu = System.Menu.moreMenu = new Ext.menu.Menu({items: moreMenuItems});

    $$("#menu_bottom_right .icon").onclick(function ()
    {
        var position = Cyan.Elements.getPosition(this);
        var size = Cyan.Elements.getComponentSize(this);

        var items = moreMenu.items;

        var hasSeparator = false;
        for (var i = 0; i < items.length; i++)
        {
            var item = moreMenu.items.itemAt(i);
            if (i == 0)
            {
                item.setDisabled(!(System.Menu.topMenus.length &&
                System.Menu.topMenus[System.Menu.topMenus.length - 1].hidden));
            }
            else if (i == 1)
            {
                item.setDisabled(!System.Menu.topMenus.length || System.Menu.topMenus[0].hidden);
            }
            else if (item.menuId)
            {
                var menu = System.Menu.menus[item.menuId];
                if (menu.hidden)
                {
                    var icon = menu.icon;
                    if (icon.offsetTop > 20)
                    {
                        item.setVisible(true);
                        hasSeparator = true;
                    }
                    else
                    {
                        item.setVisible(false);
                    }
                }
                else
                {
                    item.setVisible(false);
                }
            }

            items.itemAt(3).setVisible(hasSeparator);
        }

        moreMenu.showAt([position.x + size.width, position.y]);
    });

    var menuId = Cyan.getUrlParameter("menuId");
    if (menuId)
        System.goMenu(menuId);

    var cookieName = System.Menu.group + "_desktop_expandmenu";
    setTimeout(function ()
    {
        var expand = Cyan.getCookie(cookieName);
        if (expand == null)
        {
            expand = window.defaultMenuExpand || "true";
        }

        if (expand == "false")
            leftPanel.collapse(false);

        leftPanel.on("expand", function ()
        {
            Cyan.setCookie(cookieName, "true");
        });
        leftPanel.on("collapse", function ()
        {
            Cyan.setCookie(cookieName, "false");
        });
    }, 10);
});

var viewPortRect;
var maximized = false;

function maximize()
{
    var items = window.viewPort.items;

    var tab = items.itemAt(2);

    if (!viewPortRect)
    {
        viewPortRect = {};

        var position = tab.getPosition();
        var size = tab.getSize();

        viewPortRect.x = position[0];
        viewPortRect.y = position[1];
        viewPortRect.width = size.width;
        viewPortRect.height = size.height;
    }

    items.itemAt(0).hide();
    items.itemAt(1).hide();
    tab.setPosition(0, -25);
    tab.setSize(document.documentElement.clientWidth, document.documentElement.clientHeight + 25);

    maximized = true;
}

function restore()
{
    var items = window.viewPort.items;

    var tab = items.itemAt(2);

    tab.setPosition(viewPortRect.x, viewPortRect.y);
    tab.setSize(viewPortRect.width, viewPortRect.height);

    items.itemAt(0).show();
    items.itemAt(1).show();

    maximized = false;
}

function isMaximized()
{
    return maximized;
}

function reloadDesktop()
{
    var desktop = Cyan.$("desktop");
    if (desktop)
    {
        if (desktop.contentWindow && desktop.contentWindow.System &&
                desktop.contentWindow.System.reload)
            desktop.contentWindow.System.reload();
    }
}