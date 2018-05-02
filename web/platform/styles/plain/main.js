Cyan.importJs("render.js");
Cyan.importJs("event.js");
Cyan.importJs("widgets/tree.js");
Cyan.importJs("widgets/menu.js");

Cyan.onload(function ()
{
    var leftExpandedCookieName = System.Menu.group + "_desktop_expandmenu";
    var expanded = Cyan.getCookie(leftExpandedCookieName);
    if (expanded == null)
        expanded = window.defaultMenuExpand || "true";

    var collapseLeft = function ()
    {
        Cyan.$("left").className = "left_collapsed";
        Cyan.$("left_title").style.display = "none";
        Cyan.$("menus").style.display = "none";
        Cyan.$("left_expand").style.display = "";
        setTimeout(resize, 1);

        Cyan.setCookie(leftExpandedCookieName, "false");
    };

    var expandLeft = function ()
    {
        Cyan.$("left").className = "";
        Cyan.$("left_title").style.display = "";
        Cyan.$("menus").style.display = "";
        Cyan.$("left_expand").style.display = "none";
        setTimeout(resize, 1);

        Cyan.setCookie(leftExpandedCookieName, "true");
    };

    var initLeft = function ()
    {
        var leftCollapse = document.createElement("div");
        leftCollapse.id = "left_collapse";
        leftCollapse.onclick = collapseLeft;
        leftCollapse.title = "点击收起主菜单";
        Cyan.$("left_title").appendChild(leftCollapse);

        var leftExpand = document.createElement("div");
        leftExpand.id = "left_expand";
        leftExpand.onclick = expandLeft;
        leftExpand.title = "点击展开主菜单";
        leftExpand.style.display = expanded == "false" ? "" : "none";
        Cyan.$("left").appendChild(leftExpand);

        if (expanded == "false")
        {
            Cyan.$("left").className = "left_collapsed";
            Cyan.$("left_title").style.display = "none";
            Cyan.$("menus").style.display = "none";
        }
    };

    var initMain = function ()
    {
        var freeFrames = [];

        var mainDiv = Cyan.$("main");

        var mainTopDiv = document.createElement("div");
        mainTopDiv.id = "main_top";
        mainDiv.appendChild(mainTopDiv);

        var tabsDiv0 = document.createElement("div");
        tabsDiv0.id = "main_tabs0";
        mainTopDiv.appendChild(tabsDiv0);

        var tabsDiv = document.createElement("div");
        tabsDiv.id = "main_tabs";
        tabsDiv.style.left = 0;
        tabsDiv.style.top = 0;
        tabsDiv0.appendChild(tabsDiv);

        var backgroundDiv = document.createElement("div");
        backgroundDiv.className = "main_tabs_background";
        tabsDiv.appendChild(backgroundDiv);

        var tabsScrollDiv = document.createElement("div");
        tabsScrollDiv.id = "main_tabs_scroll";
        mainTopDiv.appendChild(tabsScrollDiv);

        var scrollLeftDiv = document.createElement("div");
        scrollLeftDiv.id = "main_tabs_scroll_left";
        scrollLeftDiv.onclick = function ()
        {
            var left = Cyan.toInt(tabsDiv.style.left) + 100;
            if (left > 0)
                left = 0;
            tabsMoveTo(left, true);
        };
        scrollLeftDiv.style.display = "none";
        tabsScrollDiv.appendChild(scrollLeftDiv);

        var scrollRightDiv = document.createElement("div");
        scrollRightDiv.id = "main_tabs_scroll_right";
        scrollRightDiv.onclick = function ()
        {
            var left = Cyan.toInt(tabsDiv.style.left) - 100;
            var lastTab = tabsDiv.childNodes[tabsDiv.childNodes.length - 2];
            var max = Cyan.Elements.getComponentSize(tabsDiv0).width - lastTab.offsetLeft -
                    Cyan.Elements.getComponentSize(lastTab).width;
            if (max > 0)
                max = 0;
            if (left < max)
                left = max;
            tabsMoveTo(left, true);
        };
        scrollRightDiv.style.display = "none";
        tabsScrollDiv.appendChild(scrollRightDiv);

        var framesDiv = document.createElement("div");
        framesDiv.id = "main_frames";
        mainDiv.appendChild(framesDiv);

        var createFrame = function ()
        {
            if (freeFrames.length > 0)
                return freeFrames.pop();

            var frame = document.createElement("iframe");
            frame.style.left = "-3000px";
            frame.style.top = "-3000px";
            frame.id = Cyan.generateId("main_frame");
            frame.frameBorder = 0;
            frame.hidden = true;
            framesDiv.appendChild(frame);

            return frame;
        };

        var showFrame = function (frame)
        {
            frame = Cyan.$(frame);
            var childNodes = framesDiv.childNodes;
            var n = childNodes.length;
            for (var i = 0; i < n; i++)
            {
                var childNode = childNodes[i];
                if (childNode.nodeName == "IFRAME")
                {
                    if (childNode == frame)
                    {
                        childNode.style.left = "0";
                        childNode.style.top = "0";
                    }
                    else
                    {
                        childNode.style.left = "-3000px";
                        childNode.style.top = "-3000px";
                    }
                }
            }

            setTimeout(function ()
            {
                frame.hidden = false;
            }, 100);
            var win = frame.contentWindow;
            if (win && win.System && win.System.reload)
                win.System.reload();
        };

        var lastSelectedTab;
        var selectTab0 = function (tabDiv, aim)
        {
            var childNodes = tabsDiv.childNodes;
            var n = childNodes.length - 1;
            for (var i = 0; i < n; i++)
            {
                var childNode = childNodes[i];
                if (childNode == tabDiv)
                {
                    childNode.className = "main_tab_selected";
                    childNode.selected = true;
                }
                else
                {
                    if (childNode.className == "main_tab_selected")
                        lastSelectedTab = childNode;
                    childNode.className = "main_tab";
                    childNode.selected = false;
                }
            }

            setTimeout(function ()
            {
                var left0 = Cyan.toInt(tabsDiv.style.left);
                var min = tabDiv.offsetLeft;
                var max = min + Cyan.Elements.getComponentSize(tabDiv).width;
                var min1 = -left0;
                var width = Cyan.Elements.getComponentSize(tabsDiv0).width;
                var max1 = min1 + width;
                var left = null;

                if (min < min1)
                    left = -min + 10;
                else if (max > max1)
                    left = width - max - 10;

                var lastTab = tabsDiv.childNodes[tabsDiv.childNodes.length - 2];
                var max2 = Cyan.Elements.getComponentSize(tabsDiv0).width - lastTab.offsetLeft -
                        Cyan.Elements.getComponentSize(lastTab).width;


                scrollLeftDiv.style.display = scrollRightDiv.style.display = max2 < 0 ? "" : "none";

                if (left != null && left > 0)
                {
                    left = 0;
                }
                else
                {
                    if (left == null)
                        left = left0;

                    if (max2 < 0)
                    {
                        if (left < max2)
                            left = max2;
                    }
                    else
                    {
                        left = 0;
                    }
                }

                if (left != left0)
                    tabsMoveTo(left, aim);
            }, 1);
        };

        var tabsMoveTo = function (x, aim)
        {
            if (aim)
                Cyan.$$(tabsDiv).fadeMove(x, 0, 1);
            else
                tabsDiv.style.left = x + "px";
        };

        var selectTab = function (tabDiv, aim)
        {
            tabDiv = Cyan.$(tabDiv);

            selectTab0(tabDiv, aim);
            if (tabDiv.frameId)
            {
                showFrame(tabDiv.frameId);
            }
        };

        var closeTab = function (tabDiv)
        {
            tabDiv = Cyan.$(tabDiv);

            var opener;
            if (tabDiv.frameId)
            {
                var frame = Cyan.$(tabDiv.frameId);
                if (frame)
                {
                    frame.src = Cyan.getRealPath("empty.html");
                    freeFrames.push(frame);
                }
            }

            if (lastSelectedTab == tabDiv)
                lastSelectedTab = null;
            var previousSibling = tabDiv.previousSibling;
            tabsDiv.removeChild(tabDiv);

            if (tabDiv.selected)
            {
                var selectedTab = lastSelectedTab || previousSibling;
                selectTab(selectedTab || tabsDiv.childNodes[0], previousSibling == selectedTab);
            }
        };

        var closeAllTabs = function (excludeTab)
        {
            var childNodes = tabsDiv.childNodes;
            var n = childNodes.length - 1;
            for (var i = n - 1; i >= 0; i--)
            {
                var childNode = childNodes[i];
                if (childNode != excludeTab &&
                        (childNode.className == "main_tab_selected" || childNode.className == "main_tab"))
                {
                    if (childNode.closable)
                        closeTab(childNode);
                }
            }
        };

        var tabClick = function ()
        {
            selectTab(this, true);
        };

        var tabDblClick = function ()
        {
            if (this.closable)
                closeTab(this);
        };

        var tabCloseClick = function (event)
        {
            closeTab(this.parentNode.parentNode);
            new Cyan.Event(event || window.event).stop();
        };

        var tabContextMenu = new Cyan.Menu([{
            text: "关闭本页",
            action: function ()
            {
                if (currentTab)
                    closeTab(currentTab);
            }
        }, {
            text: "关闭其它页",
            action: function ()
            {
                closeAllTabs(currentTab);
            }
        }, {
            text: "关闭所有页",
            action: closeAllTabs
        }]);

        var currentTab;
        var onTabContextMenu = function (event)
        {
            var el = event.target;
            while (el.className != "main_tab" && el.className != "main_tab_selected" && el != document.body)
            {
                el = el.parentNode;
            }

            if (el.className == "main_tab" || el.className == "main_tab_selected")
            {
                currentTab = el;
                tabContextMenu.showAt(event.pageX, event.pageY);
                event.stop();
                return false;
            }
        };

        var openPage = function (id, url, title, win, closable)
        {
            var tabDiv = Cyan.$("tab_" + id);
            if (tabDiv)
            {
                selectTab(tabDiv, false);
                return;
            }

            id = id || url;
            title = title || "加载中";

            tabDiv = document.createElement("div");
            tabDiv.id = "tab_" + id;
            tabDiv.className = "main_tab";
            tabDiv.onclick = tabClick;
            if (closable)
            {
                tabDiv.ondblclick = tabDblClick;
                Cyan.attach(tabDiv, "contextmenu", onTabContextMenu);
            }
            tabDiv.closable = closable;
            Cyan.Elements.disableSelection(tabDiv);
            tabsDiv.insertBefore(tabDiv, backgroundDiv);

            var tabContentDiv = document.createElement("div");
            tabContentDiv.className = "main_tab_content";
            tabDiv.appendChild(tabContentDiv);

            var tabTextDiv = document.createElement("div");
            tabTextDiv.className = "main_tab_text";
            tabTextDiv.innerHTML = title;
            tabContentDiv.appendChild(tabTextDiv);

            if (closable)
            {
                var closeDiv = document.createElement("div");
                closeDiv.className = "main_tab_close";
                closeDiv.onclick = tabCloseClick;
                tabContentDiv.appendChild(closeDiv);
            }

            selectTab0(tabDiv, false);

            var frame = createFrame();
            frame.src = url;
            tabDiv.frameId = frame.id;
            frame.pageObject = {
                opener: win,
                setTitle: function (title)
                {
                    tabTextDiv.innerHTML = title;
                },
                closePage: function ()
                {
                    closeTab(tabDiv);
                }
            };
            showFrame(frame);
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

            openPage(name, url, title, win, true);
        };

        var groupId = Cyan.getUrlParameter("groupId");
        if (!groupId)
            groupId = "desktop";

        var desktop = Cyan.getUrlParameter("desktop");
        if (!desktop)
            desktop = "desktop";
        openPage("desktop", desktop + ".ptl?groupId=" + groupId, "桌面", null, false);
    };

    initLeft();
    initMain();

    var resize = function ()
    {
        var top_height = Cyan.Elements.getComponentSize(Cyan.$("top")).height;
        var height = window.document.documentElement.clientHeight;
        var width = window.document.documentElement.clientWidth;

        var left = Cyan.$("left");
        var center = Cyan.$("center");

        var leftHeight = height - top_height;
        left.style.height = leftHeight + "px";

        var centerHeight = leftHeight;
        var centerPaddingTop = Cyan.toInt(Cyan.Elements.getCss(center, "padding-top"));
        if (centerPaddingTop)
            centerHeight -= centerPaddingTop;

        var left_width = Cyan.Elements.getComponentSize(left).width;
        var centerWidth = width - left_width - 1;

        center.style.height = centerHeight + "px";
        center.style.width = centerWidth + "px";

        var mainFramesHeight = centerHeight - Cyan.Elements.getComponentSize(Cyan.$("main_top")).height;
        Cyan.$("main_frames").style.height = mainFramesHeight + "px";

        var tabsScrollWidth = Cyan.Elements.getComponentSize(Cyan.$("main_tabs_scroll")).width;
        var mainWidth = Cyan.Elements.getComponentSize(Cyan.$("main")).width - 2;
        Cyan.$("main_tabs0").style.width = (mainWidth - tabsScrollWidth - 1) + "px";
    };

    resize();
    var resizing = false;
    var resize1 = function ()
    {
        resize();
        resizing = false;
    };
    Cyan.attach(window, "resize", function ()
    {
        if (resizing)
            return;
        resizing = true;
        setTimeout(resize1, 100);
    });

    var maximized = false;
    window.maximize = function (closeMessage)
    {
        Cyan.$("top").style.display = "none";
        Cyan.$("left").style.display = "none";
        Cyan.$("main_top").style.display = "none";
        Cyan.$("main").className = "max";
        Cyan.$("center").className = "max";

        var height = window.document.documentElement.clientHeight;
        var width = window.document.documentElement.clientWidth;
        Cyan.$("center").style.width = width + "px";
        Cyan.$("center").style.height = height + "px";
        Cyan.$("main_frames").style.height = height + "px";

        maximized = true;

        if (closeMessage)
        {
            window.parent.onbeforeunload = function ()
            {
                return closeMessage;
            };
        }
    };

    window.restore = function ()
    {
        Cyan.$("top").style.display = "";
        Cyan.$("left").style.display = "";
        Cyan.$("main_top").style.display = "";
        Cyan.$("main").className = "";
        Cyan.$("center").className = "";

        resize();

        maximized = false;

        window.parent.onbeforeunload = null;
    };

    window.isMaximized = function ()
    {
        return maximized;
    };
});

System.Menu.create = function (menuTree, group, showSize)
{
    if (!showSize)
        showSize = 10;
    System.Menu.showSize = showSize;
    System.Menu.group = group;

    var f = function ()
    {
        var select0 = function (menu, callback)
        {
            if (menu.parentMenu)
            {
                select0(menu.parentMenu, callback);
            }
            else
            {
                expandMenu(Cyan.$("menu" + menu.menuId), true, function ()
                {
                    callback(menu.tree);
                });
            }
        };

        System.Menu.select = function ()
        {
            var menu = this;
            select0(this, function (tree)
            {
                if (menu.parentMenu)
                {
                    if (tree)
                    {
                        var treeNode = tree.getNodeById(menu.menuId);
                        if (treeNode)
                        {
                            treeNode.ensureVisible();
                            treeNode.select();
                            Cyan.Elements.scrollYTo(Cyan.$("left"), Cyan.$(treeNode.el));
                        }
                    }
                    else
                    {
                        var menuDiv = Cyan.$("menu" + menu.menuId);
                        if (menuDiv)
                        {
                            selectMenuItem(menuDiv);
                            Cyan.Elements.scrollYTo(Cyan.$("left"), menuDiv);
                        }
                    }
                }
            });
        };

        var initMenu = function (menu)
        {
            System.Menu.menus[menu.menuId] = menu;
            System.Menu.initMenu(menu);
            var children = menu.children;
            if (children)
            {
                var n = children.length;
                for (var i = 0; i < n; i++)
                {
                    var child = children[i];
                    child.parentMenu = menu;
                    initMenu(child);
                }
            }
        };

        for (var i = 0; i < menuTree.length; i++)
        {
            initMenu(menuTree[i]);
        }

        var menusDiv = Cyan.$("menus");

        var menuClick = function ()
        {
            var menu = System.Menu.menus[this.menuId];
            if (!menu.url && menu.defaultMenuId)
            {
                var subMenusDiv = this.nextSibling;
                if ((!subMenusDiv || subMenusDiv.className != "sub_menus" || subMenusDiv.style.display == "none"))
                {
                    var defaultMenu = System.Menu.menus[menu.defaultMenuId];
                    if (defaultMenu)
                    {
                        defaultMenu.select();
                        openMenu(menu.defaultMenuId);
                        return;
                    }
                }
            }
            expandMenu(this);
            openMenu(this.menuId);
        };

        var menuItemClick = function ()
        {
            selectMenuItem(this);
            openMenu(this.menuId);
        };

        var treeNodeClick = function ()
        {
            openMenu(this.id);
        };

        var createMenu = function (menu)
        {
            var menuDiv = document.createElement("div");

            menusDiv.appendChild(menuDiv);
            menuDiv.className = "menu_item";
            menuDiv.id = "menu" + menu.menuId;
            if (menu.hint)
                menuDiv.title = menu.hint;

            var menuIcon = document.createElement("div");
            menuIcon.className = "menu_item_icon";
            menuDiv.appendChild(menuIcon);

            if (menu.iconPath)
            {
                var img = document.createElement("img");
                menuIcon.appendChild(img);
                img.src = menu.iconPath;
            }

            var menuTitle = document.createElement("div");
            menuTitle.className = "menu_item_title";
            menuDiv.appendChild(menuTitle);
            menuTitle.innerHTML = menu.title;

            menuDiv.menuId = menu.menuId;
            menuDiv.onclick = menuClick;
        };

        var createSubMenus = function (menu, menuDiv)
        {
            var subMenusDiv = document.createElement("div");
            subMenusDiv.className = "sub_menus";
            subMenusDiv.style.display = "none";

            var nextSibling = menuDiv.nextSibling;
            if (nextSibling)
                menusDiv.insertBefore(subMenusDiv, nextSibling);
            else
                menusDiv.appendChild(subMenusDiv);

            var tree = false;
            var children = menu.children;
            for (var i = 0; i < children.length; i++)
            {
                var item = children[i];
                if (item.children && item.children.length)
                {
                    tree = true;
                    break;
                }
            }

            (tree ? createSubMenus2 : createSubMenus1)(menu, subMenusDiv);

            return subMenusDiv;
        };

        var subMenuHeight;
        var getSubMenusHeight = function (menu, subMenusDiv)
        {
            var height, div = subMenusDiv.childNodes[0];
            if (menu.tree)
            {
                height = menu.tree.getHeight();
            }
            else
            {
                if (!subMenuHeight)
                    subMenuHeight = Cyan.toInt(Cyan.Elements.getCss(div.childNodes[0], "height"));
                height = subMenuHeight * menu.children.length;
            }
            height += Cyan.toInt(Cyan.Elements.getCss(div, "padding-top"));
            height += Cyan.toInt(Cyan.Elements.getCss(div, "padding-bottom"));

            return height;
        };

        var createSubMenus1 = function (menu, subMenusDiv)
        {
            var itemsDiv = document.createElement("div");
            itemsDiv.className = "sub_menus_items";
            subMenusDiv.appendChild(itemsDiv);

            var children = menu.children;
            var n = children.length;
            for (var i = 0; i < n; i++)
            {
                createSubMenuItem1(children[i], itemsDiv);
            }
        };

        var createSubMenuItem1 = function (menu, itemsDiv)
        {
            var menuDiv = document.createElement("div");
            menuDiv.className = "sub_menu_item";
            menuDiv.menuId = menu.menuId;
            menuDiv.id = "menu" + menu.menuId;
            itemsDiv.appendChild(menuDiv);

            var menuIcon = document.createElement("div");
            menuIcon.className = "sub_menu_item_icon";
            menuDiv.appendChild(menuIcon);

            if (menu.iconPath)
            {
                var img = document.createElement("img");
                menuIcon.appendChild(img);
                img.src = menu.iconPath;
            }

            var menuTitle = document.createElement("div");
            menuTitle.className = "sub_menu_item_title";
            menuDiv.appendChild(menuTitle);
            menuTitle.innerHTML = menu.title;

            menuDiv.menuId = menu.menuId;
            menuDiv.onclick = menuItemClick;
            if (menu.hint)
                menuDiv.title = menu.hint;
        };

        var createSubMenus2 = function (menu, subMenusDiv)
        {
            var treeDiv = document.createElement("div");
            treeDiv.className = "sub_menus_tree";
            subMenusDiv.appendChild(treeDiv);

            var root = toTreeNode(menu);
            var tree = new Cyan.Tree(menu.id, root);
            tree.autoRender = true;
            menu.tree = tree;
            tree.rootVisible = false;
            tree.init(treeDiv);

            tree.onclick = treeNodeClick;
        };

        var toTreeNode = function (menu)
        {
            var node = {id: menu.menuId, text: menu.title, leaf: menu.leaf, icon: menu.iconPath, tip: menu.hint};

            var children = menu.children;
            if (children && children.length)
            {
                var n = children.length;
                node.children = new Array(n);
                for (var i = 0; i < children.length; i++)
                {
                    node.children[i] = toTreeNode(children[i]);
                }
            }

            return node;
        };

        for (i = 0; i < menuTree.length; i++)
        {
            createMenu(menuTree[i]);
        }

        var expandMenu = function (menuDiv, expend, callback)
        {
            var menu = System.Menu.menus[menuDiv.menuId];

            if (menuDiv.className != "menu_item" && menuDiv.className != "menu_item menu_item_selected")
                return;

            if (menuDiv.className == "menu_item")
                menuDiv.className = "menu_item menu_item_selected";

            var subMenusDiv = menuDiv.nextSibling;
            if ((!subMenusDiv || subMenusDiv.className != "sub_menus"))
            {
                subMenusDiv = null;

                if (menu.children && menu.children.length > 0)
                    subMenusDiv = createSubMenus(menu, menuDiv);
            }

            var childNodes = menusDiv.childNodes;
            var n = childNodes.length;
            for (i = 0; i < n; i++)
            {
                var childNode = childNodes[i];
                if (childNode.className == "sub_menus" && childNode != subMenusDiv)
                    childNode.style.display = "none";
                else if (childNode.className == "menu_item menu_item_selected" && childNode != menuDiv)
                    childNode.className = "menu_item";
            }

            if (subMenusDiv)
            {
                if (subMenusDiv.style.display == "none")
                {
                    if (expend || expend == null)
                    {
                        subMenusDiv.style.height = getSubMenusHeight(menu, subMenusDiv) + "px";
                        var fadeIn = new Cyan.Render.PositionFade(subMenusDiv);
                        fadeIn.onComplete = function ()
                        {
                            subMenusDiv.style.height = "";
                            if (callback)
                                callback();
                        };
                        fadeIn.fadeIn(1, 2);
                    }
                    else if (callback)
                    {
                        callback();
                    }
                }
                else
                {
                    if (expend == false || expend == null)
                    {
                        subMenusDiv.style.height = getSubMenusHeight(menu, subMenusDiv) + "px";
                        var fadeOut = new Cyan.Render.PositionFade(subMenusDiv);
                        fadeOut.onComplete = function ()
                        {
                            subMenusDiv.style.height = "";
                            if (callback)
                                callback();
                        };
                        fadeOut.fadeOut(1, 2);
                    }
                    else if (callback)
                    {
                        callback();
                    }
                }
            }
        };

        var selectMenuItem = function (menuItemDiv)
        {
            var childNodes = menuItemDiv.parentNode.childNodes;
            var n = childNodes.length;
            for (var i = 0; i < n; i++)
            {
                var childNode = childNodes[i];
                if (childNode != menuItemDiv)
                {
                    if (childNode.className == "sub_menu_item sub_menu_item_selected")
                        childNode.className = "sub_menu_item";
                }
                else
                {
                    menuItemDiv.className = "sub_menu_item sub_menu_item_selected";
                }
            }
        };

        var openMenu = function (menuId)
        {
            var menu = System.Menu.menus[menuId];

            var url = menu.url;
            if (url)
            {
                if (url.indexOf("javascript:") == 0)
                {
                    eval(url.substring(11));
                }
                else if (url.indexOf("://") > 0)
                {
                    window.open(url);
                }
                else
                {
                    System.Menu.openPage("/menu/" + menu.menuId, menu.menuId, menu.title);
                }
            }
        };
    };

    f();

    Cyan.onload(function ()
    {
        var menuId = Cyan.getUrlParameter("menuId");
        if (menuId)
            System.goMenu(menuId);
    });
};