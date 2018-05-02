Cyan.onload(function ()
{
    System.Desktop.init();
    System.reload = function ()
    {
        Cyan.lazyEach(Cyan.$$(".desktop-module"), function (callback)
        {
            this.reload(callback);
        });
    };

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
                                System.Desktop.addModule(module, i, true);
                        }
                    }
                }
            }
        }
    });
});

(function ()
{
    var columnCount = parseInt(Cyan.getUrlParameter("columnCount")) || 2;

    var getMinHeight = function ()
    {
        if (columnCount <= 2)
            return 195;
        else if (columnCount == 3)
            return 150;
    };

    var getWidth = function ()
    {
        if (columnCount == 1)
            return "96%";
        else if (columnCount == 2)
            return "47%";
        else if (columnCount == 3)
            return "31%";
    };

    var getColumnSpacing = function ()
    {
        if (columnCount == 1)
            return "2%";
        else if (columnCount == 2)
            return "2%";
        else if (columnCount == 3)
            return "1%";
    };

    var getSpacing = function ()
    {
        if (columnCount == 1)
            return 20;
        else if (columnCount == 2)
            return 20;
        else if (columnCount == 3)
            return 10;
    };

    var setTitle = function (title)
    {
        this.title0 = title;
        Cyan.$$(this).$(".desktop-module-title").html(title);
    };

    var setHeight = function (height)
    {
        var minHeight = getMinHeight();
        Cyan.$$(this).$(".desktop-module-content").css("height", height + "px");
    };

    var getModuleDiv = function (div)
    {
        while (div.className != "desktop-module" && div != document.body)
        {
            div = div.parentNode;
        }
        if (div.className == "desktop-module")
            return div;
        else
            return null;
    };

    var close = function ()
    {
        var div = getModuleDiv(this);
        if (div)
        {
            Cyan.confirm("确定将模块从桌面移除？", function (ret)
            {
                if (ret == "ok")
                {
                    div.parentNode.removeChild(div);
                    System.Desktop.save();
                }
            });
        }
    };

    var refresh = function ()
    {
        getModuleDiv(this).reload();
    };

    var reload = function (callback)
    {
        var moduleDiv = this;
        var iframe = Cyan.$$(moduleDiv).$("iframe")[0];
        if (iframe)
        {
            var win = iframe.contentWindow;
            if (win.System && win.System.reload)
            {
                win.System.reload(function ()
                {
                    var height = Cyan.getBodyHeight(win);
                    var minHeight = getMinHeight();
                    if (height < minHeight)
                        height = minHeight;
                    moduleDiv.setHeight(height);

                    if (callback)
                        callback();
                });
            }
            else
            {
                win.location.reload();
                if (callback)
                    callback();
            }
        }
    };

    var load = function ()
    {
        var moduleDiv = getModuleDiv(this);
        var iframe = Cyan.$$(moduleDiv).$("iframe")[0];
        if (iframe)
        {
            var win = iframe.contentWindow;
            if (win)
            {
                setTimeout(function ()
                {
                    var height = Cyan.getBodyHeight(win);
                    var minHeight = getMinHeight();
                    if (height < minHeight)
                        height = minHeight;
                    moduleDiv.setHeight(height);
                }, 500);
            }
        }
    };

    var config = function ()
    {
        var moduleDiv = getModuleDiv(this);
        var iframe = Cyan.$$(moduleDiv).$("iframe")[0];
        if (iframe)
        {
            var win = iframe.contentWindow;
            if (win.System && win.System.config)
                win.System.config();
            else
                System.Desktop.showConfig();
        }
        else
        {
            System.Desktop.showConfig();
        }
    };

    System.Desktop = {
        init: function ()
        {
            var main = Cyan.$("desktop_main");
            var width = getWidth();
            var spacing = getColumnSpacing();
            for (var i = 0; i < columnCount; i++)
            {
                var div = document.createElement("div");
                div.className = "desktop-column";
                div.style.width = width;
                div.style.marginLeft = spacing;
                main.appendChild(div);
            }
        },
        save: function ()
        {
            var columns = Cyan.$$(".desktop-column");
            var n = columns.length;
            var def = {
                columns: new Array(n)
            };

            for (var i = 0; i < n; i++)
            {
                var columnDiv = columns[i];
                var moduleDivs = columnDiv.childNodes;
                var m = moduleDivs.length;
                var column = {modules: new Array(m)};
                for (var j = 0; j < m; j++)
                {
                    var moduleDiv = moduleDivs[j];
                    column.modules[j] = {
                        appId: moduleDiv.appId,
                        url: moduleDiv.url,
                        title: moduleDiv.title0,
                        content: moduleDiv.html
                    };
                }
                def.columns[i] = column;
            }

            var url = "/desktop";
            if (System.desktopGroupId)
                url += "?groupId=" + System.desktopGroupId;

            Cyan.Arachne.post(url, [def]);
        },
        getModule: function (appId)
        {
            return Cyan.$$(".desktop-module").searchFirst(function ()
            {
                return this.appId == appId;
            });
        },
        addModule: function (module, column, loading)
        {
            if (Cyan.isString(module))
            {
                if (System.Desktop.getModule(module))
                    return;
                module = {appId: module};
            }

            if (column == null)
            {
                var columns = Cyan.$$(".desktop-column");
                var min = null;
                for (var i = 0; i < columns.length; i++)
                {
                    var count = columns[i].childNodes.length;
                    if (min == null || count < min)
                    {
                        min = count;
                        column = i;
                    }
                }
            }

            if (column < 0)
                column = 0;

            column %= columnCount;

            var columnDiv = Cyan.$$(".desktop-column")[column];

            var moduleDiv = document.createElement("div");
            moduleDiv.appId = module.appId;
            moduleDiv.url = module.url;
            moduleDiv.title0 = module.title;
            moduleDiv.html = module.html;
            moduleDiv.className = "desktop-module";
            moduleDiv.style.marginTop = getSpacing() + "px";
            columnDiv.appendChild(moduleDiv);

            moduleDiv.reload = reload;
            moduleDiv.config = config;

            var url = module.url;
            if (!url && module.appId)
                url = "/desktop/module/" + module.appId;

            var moduleHeader = document.createElement("div");
            moduleHeader.className = "desktop-module-header";
            moduleDiv.appendChild(moduleHeader);
            Cyan.Elements.disableSelection(moduleHeader);

            var moduleTitle = document.createElement("div");
            moduleTitle.className = "desktop-module-title";
            moduleTitle.innerHTML = module.title || "加载中...";
            moduleHeader.appendChild(moduleTitle);
            moduleDiv.setTitle = setTitle;

            var moduleButtons = document.createElement("div");
            moduleButtons.className = "desktop-module-buttons";
            moduleHeader.appendChild(moduleButtons);

            var configButton = document.createElement("button");
            configButton.className = "desktop-module-button-config";
            configButton.onclick = config;
            moduleButtons.appendChild(configButton);

            var refreshButton = document.createElement("button");
            refreshButton.className = "desktop-module-button-refresh";
            refreshButton.onclick = refresh;
            moduleButtons.appendChild(refreshButton);

            var closeButton = document.createElement("button");
            closeButton.className = "desktop-module-button-close";
            closeButton.onclick = close;
            moduleButtons.appendChild(closeButton);

            var moduleContent = document.createElement("div");
            moduleContent.className = "desktop-module-content";
            moduleDiv.appendChild(moduleContent);

            moduleContent.style.height = getMinHeight() + "px";
            moduleDiv.setHeight = setHeight;

            if (url)
            {
                var frame = document.createElement("iframe");
                frame.id = Cyan.generateId("module_frame");
                frame.frameBorder = 0;
                moduleContent.appendChild(frame);
                frame.src = url;
                frame.pageObject = moduleDiv;
                Cyan.attach(frame, "load", load);
            }
            else
            {
                moduleContent.innerHTML = module.html || "";
            }

            if (!loading)
                System.Desktop.save();
        },
        removeModule: function (module)
        {
            Cyan.$$(".desktop-module").search(function ()
            {
                return this.appId == module;
            }).remove();
            System.Desktop.save();
        },
        showConfig: function ()
        {
            var url = "/desktop/def";
            if (System.desktopGroupId)
                url += "?groupId=" + System.desktopGroupId;
            System.showDialog(url, "desktop_def_config", false);
        }
    };
})();