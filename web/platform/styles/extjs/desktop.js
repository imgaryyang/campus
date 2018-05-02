Cyan.importJs("adapters/extjs/Portal.js");
Cyan.importJs("adapters/extjs/PortalColumn.js");
Cyan.importCss("adapters/extjs/portal.css");

Cyan.onload(function ()
{
    Ext.QuickTips.init(true);

    System.reload = null;

    Cyan.attach(document.body, "dblclick", function (event)
    {
        if (event.srcElement.className.indexOf("x-portal") >= 0)
            System.Desktop.showConfig();
    });

    var url = "/desktop";
    var groupId = System.desktopGroupId || Cyan.getUrlParameter("groupId");
    if (groupId)
        url += "?groupId=" + groupId;
    Cyan.Arachne.get(url, null, function (ret)
    {
        if (ret && ret.columns)
        {
            if (ret.columns)
            {
                var n = ret.columns.length;
                for (var i = 0; i < n; i++)
                {
                    var column = ret.columns[i];
                    if (column && column.modules)
                    {
                        var m = column.modules.length;
                        for (var j = 0; j < m; j++)
                        {
                            var module = column.modules[j];
                            if (module)
                                System.Desktop.addModule(module, i);
                        }
                    }
                }
            }
        }

        System.reload = function ()
        {
            var items = System.Desktop.portal.items;
            var n = items.length;
            for (var i = 0; i < n; i++)
            {
                var portalColumn = items.itemAt(i);
                var m = portalColumn.items.length;
                for (var j = 0; j < m; j++)
                {
                    var portlet = portalColumn.items.itemAt(j);
                    portlet.reload();
                }
            }
        };
    });

    var listeners = {
        add: System.Desktop.save
    };

    var items = new Array(System.Desktop.columnCount);

    var spacing = System.Desktop.getSpacing();
    for (var i = 0; i < System.Desktop.columnCount; i++)
    {
        items[i] = {
            columnWidth: System.Desktop.getWidth(),
            style: "padding:0 " + spacing + "px " + spacing + "px " + spacing + "px",
            listeners: listeners
        };
    }

    System.Desktop.portal = new Ext.ux.Portal(
            {
                items: items
            });
    System.Desktop.portal.region = "center";
    System.Desktop.portal.border = false;
    new Ext.Viewport({
        layout: 'fit',
        items: [
            {
                layout: "border",
                items: [
                    {
                        height: System.Desktop.topHeight,
                        region: "north",
                        border: false,
                        html: "<div id='desktop_top'></div>"
                    },
                    System.Desktop.portal,
                    {
                        height: System.Desktop.bottomHeight,
                        region: "south",
                        border: false,
                        html: "<div id='desktop_bottom'></div>"
                    }
                ]
            }
        ]
    });

    System.Desktop.tools = [
        {
            id: 'gear',
            qtip: '设置',
            handler: function (e, target, panel)
            {
                var win;
                if (panel.frameId)
                {
                    win = Cyan.$(panel.frameId).contentWindow;
                    if (win.System && win.System.config)
                        win.System.config();
                    else
                        System.Desktop.showConfig();
                }
                else
                {
                    System.Desktop.showConfig();
                }
            }
        },
        {
            id: 'refresh',
            qtip: '刷新',
            handler: function (e, target, panel)
            {
                panel.reload();
            }
        },
        {
            id: 'close',
            qtip: '移除',
            handler: function (e, target, panel)
            {
                Cyan.confirm("确定将模块从桌面移除？", function (ret)
                {
                    if (ret == "ok")
                    {
                        System.Desktop.removeModule(panel);
                        if (panel.appId)
                        {
                            var win = Cyan.Window.getWindow("desktop_def_config");
                            if (win)
                            {
                                var checkbox = win.getFrame().contentWindow.Cyan.$$("#appIds@value=" +
                                panel.appId).first;
                                if (checkbox)
                                    checkbox.checked = false;
                            }
                        }
                    }
                });
            }
        }
    ];
});

System.Desktop = {
    loading: false,
    columnCount: parseInt(Cyan.getUrlParameter("columnCount")) || 2,
    topHeight: 0,
    bottomHeight: 0,
    getMinHeight: function ()
    {
        if (System.Desktop.columnCount <= 2)
            return 222;
        else if (System.Desktop.columnCount == 3)
            return 150;
    },
    getWidth: function ()
    {
        if (System.Desktop.columnCount == 1)
            return 98;
        else if (System.Desktop.columnCount == 2)
            return .48;
        else if (System.Desktop.columnCount == 3)
            return .33;
    },
    getSpacing: function ()
    {
        if (System.Desktop.columnCount == 1)
            return 10;
        else if (System.Desktop.columnCount == 2)
            return 30;
        else if (System.Desktop.columnCount == 3)
            return 10;
    },
    save: function ()
    {
        if (!System.Desktop.loading)
        {
            var items = System.Desktop.portal.items;
            var n = items.length;
            var def = {
                columns: new Array(n)
            };
            for (var i = 0; i < n; i++)
            {
                var portalColumn = items.itemAt(i);
                var m = portalColumn.items.length;
                var column = {modules: new Array(m)};
                for (var j = 0; j < m; j++)
                {
                    var portlet = portalColumn.items.itemAt(j);
                    column.modules[j] = {
                        appId: portlet.appId,
                        url: portlet.url,
                        title: portlet.title,
                        content: portlet.frameId ? null : portlet.html
                    };
                }
                def.columns[i] = column;
            }

            var url = "/desktop";
            if (System.desktopGroupId)
                url += "?groupId=" + System.desktopGroupId;

            Cyan.Arachne.post(url, [def]);
        }
    },
    showFrame: function ()
    {
        var frame = Cyan.$(this.frameId);
        var loading = frame.previousSibling;
        if (loading.style.display != "none")
        {
            var portlet = this;
            window.setTimeout(function ()
            {
                var height = Cyan.getBodyHeight(frame.contentWindow) + 29;
                if (height < System.Desktop.getMinHeight())
                    height = System.Desktop.getMinHeight();
                portlet.setHeight(height);
                loading.style.display = "none";
                portlet = null;
            }, 300);
        }
    },
    getModule: function (appId)
    {
        var items = System.Desktop.portal.items;
        var n = items.length;
        for (var i = 0; i < n; i++)
        {
            var portalColumn = items.itemAt(i);
            var m = portalColumn.items.length;
            for (var j = 0; j < m; j++)
            {
                var portlet = portalColumn.items.itemAt(j);
                if (portlet.appId == appId)
                    return portlet;
            }
        }
        return null;
    },
    addModule: function (module, column)
    {
        column %= System.Desktop.columnCount;

        System.Desktop.loading = true;
        try
        {
            if (Cyan.isString(module))
            {
                if (System.Desktop.getModule(module))
                    return;
                module = {appId: module};
            }

            if (!column)
                column = 0;

            var portalColumn = System.Desktop.portal.items.itemAt(column);

            var title = module.title;
            var url = module.url;
            if (module.appId)
                url = "/desktop/module/" + module.appId;

            var html, frameId;
            if (url)
            {
                while (Cyan.$(frameId = "DesktopModulFrame$" + Math.random().toString().substring(2)));

                html = "<div class='cyan-window-loading' style='height:" + System.Desktop.getMinHeight() + "px'>" +
                Cyan.titles["loading"] + ". . .</div><iframe id='" + frameId +
                "' style='width:100%;height:100%;' frameborder='0'";
                if (url)
                    html += " src='" + Cyan.formatUrl(url) + "'";
                html += "></iframe>";
            }
            else
            {
                html = module.content || "";
            }

            var portlet = portalColumn.add({
                collapsible: false,
                title: title || "加载中...",
                html: html,
                height: System.Desktop.getMinHeight(),
                tools: System.Desktop.tools
            });

            portlet.appId = module.appId;
            portlet.url = module.url;
            portlet.title = module.title;
            portlet.html = html;

            if (frameId)
                portlet.frameId = frameId;

            portalColumn.doLayout();

            portlet.reload = System.Desktop.reload;
            if (frameId)
            {
                Cyan.$(frameId).pageObject = portlet;
                portlet.showFrame = System.Desktop.showFrame;
            }

            System.Desktop.save();
        }
        finally
        {
            System.Desktop.loading = false;
        }
    },
    removeModule: function (module)
    {
        if (Cyan.isString(module))
            module = System.Desktop.getModule(module);

        if (!module)
            return;

        if (module.frameId)
        {
            var frame = Cyan.$(module.frameId);
            frame.pageObject = null;
            frame.parentNode.removeChild(frame);
        }
        module.ownerCt.remove(module, true);
        System.Desktop.save();
    },
    reload: function ()
    {
        var win;
        if (this.frameId)
        {
            win = Cyan.$(this.frameId).contentWindow;
            if (win.System && win.System.reload)
            {
                var portlet = this;
                win.System.reload(function ()
                {
                    var height = Cyan.getBodyHeight(win) + 29;
                    if (height < System.Desktop.getMinHeight())
                        height = System.Desktop.getMinHeight();
                    portlet.setHeight(height);
                    win = null;
                    portlet = null;
                });
            }
            else
            {
                win.location.reload();
            }
        }
    },
    showConfig: function ()
    {
        var url = "/desktop/def";
        if (System.desktopGroupId)
            url += "?groupId=" + System.desktopGroupId;
        System.showDialog(url, "desktop_def_config", false);
    }
};