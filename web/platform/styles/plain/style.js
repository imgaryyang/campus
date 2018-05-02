Cyan.Widget.prototype.autoRender = false;
System.baseStylePath = "/platform/styles/plain";
Cyan.importJs("adapters/plain/form.js");
Cyan.importJs("adapters/plain/panel.js");
Cyan.importJs("widgets/menu.js");

window.leftCollapsed = false;
window.collapseLeft = function ()
{
    window.leftCollapsed = true;
    if (window.collapseLeft0)
        window.collapseLeft0();
};
window.expandLeft = function ()
{
    window.leftCollapsed = false;
    if (window.expandLeft0)
        window.expandLeft0();
};

Cyan.onload(function ()
{
    var parentIframe = window.frameElement;
    var pageObject, title;
    if (parentIframe)
        pageObject = parentIframe.pageObject;

    if (pageObject)
    {
        if (pageObject.setTitle)
        {
            title = System.getTitle();

            if (title)
            {
                var s = title;
                if (s.length > 12)
                    s = s.substring(0, 10) + "...";
                pageObject.setTitle(s);
            }
        }

        System.page = pageObject;
        System.opener = pageObject.opener;
    }
    else
    {
        var dialog = Cyan.Window.getWindow();
        if (dialog)
        {
            title = System.getTitle();
            if (title)
            {
                dialog.setTitle(title);
                document.title = title;
            }
        }
    }

    var mainBodyDiv = Cyan.$("mainBody");
    var moreComponents, moreComponentsExpanded = false;
    if (mainBodyDiv)
    {
        document.body.style.overflow = "hidden";

        var left = Cyan.$("left");

        var components = Cyan.$("components");
        var buttons = Cyan.$("buttons");

        var form = document.createElement("form");
        document.body.insertBefore(form, document.body.childNodes[0]);

        var container = document.createElement("div");
        form.appendChild(container);

        var main, leftPanel;

        var pageBody = Cyan.$("pageBody");

        if (left)
        {
            leftPanel = document.createElement("div");
            leftPanel.id = "main_left";
            container.appendChild(leftPanel);

            var leftContent = document.createElement("div");
            leftContent.id = "main_left_content";
            leftPanel.appendChild(leftContent);

            if (window.left && window.left.bindSearch && window.left.searchable)
            {
                var searchDiv = document.createElement("div");
                searchDiv.id = "main_left_search";
                leftContent.appendChild(searchDiv);

                var searchInput = document.createElement("input");
                searchInput.className = "search";
                searchDiv.appendChild(searchInput);

                window.left.bindSearch(searchInput);
            }

            leftContent.appendChild(left);

            main = document.createElement("div");
            main.id = "main_right";
            container.appendChild(main);
        }
        else
        {
            main = container;
        }

        var toolbar, bottomToolBar;
        var leftAlign = pageBody && pageBody.className &&
                Cyan.Array.contains(pageBody.className.split(" "), "left");
        if (components || buttons)
        {
            toolbar = document.createElement("div");
            toolbar.className = "toolbar";

            if (leftPanel)
            {
                var leftIcon = document.createElement("div");
                if (leftCollapsed)
                {
                    leftIcon.title = "展开";
                    leftIcon.className = "main_left_collapsed";
                    leftPanel.style.display = "none";
                }
                else
                {
                    leftIcon.title = "收起";
                    leftIcon.className = "main_left_expanded";
                }
                leftIcon.onclick = function ()
                {
                    if (leftCollapsed)
                        expandLeft();
                    else
                        collapseLeft();
                };
                toolbar.appendChild(leftIcon);

                window.collapseLeft0 = function ()
                {
                    leftIcon.title = "展开";
                    leftIcon.className = "main_left_collapsed";
                    leftPanel.style.display = "none";
                    setTimeout(resize, 1);
                };
                window.expandLeft0 = function ()
                {
                    leftIcon.title = "收起";
                    leftIcon.className = "main_left_expanded";
                    leftPanel.style.display = "";
                    setTimeout(resize, 1);
                };
            }

            if (leftAlign)
                toolbar.className += " toolbar_left";
            else
                toolbar.className += " toolbar_right";

            var toolbarLeft = document.createElement("div");
            toolbarLeft.id = "toolbar_left";
            toolbar.appendChild(toolbarLeft);

            main.appendChild(toolbar);

            if (buttons)
            {
                toolbar.appendChild(buttons);
                buttons.className = "buttons";
            }

            moreComponents = Cyan.$("moreComponents");
            if (moreComponents)
            {
                var moreIcon = document.createElement("div");
                toolbar.appendChild(moreIcon);
                moreIcon.className = "more_components_collapsed";
                moreIcon.title = "展开更多查询条件";
                moreIcon.onclick = function ()
                {
                    moreComponentsExpanded = !moreComponentsExpanded;
                    if (moreComponentsExpanded)
                    {
                        moreIcon.className = "more_components_expanded";
                        moreIcon.title = "收起查询条件";
                        moreComponents.style.display = "";
                    }
                    else
                    {
                        moreIcon.className = "more_components_collapsed";
                        moreIcon.title = "展开更多查询条件";
                        moreComponents.style.display = "none";
                    }

                    resize();
                };

                main.appendChild(moreComponents);
                Cyan.$$(moreComponents).$(">div>span").each(function ()
                {
                    this.className = "toolbar_span";
                });
            }

            if (components)
            {
                toolbar.appendChild(components);
                components.className = "components";
            }

            Cyan.$$(buttons).$("button").onmousedown(function ()
            {
                if (!this.menu)
                    this.parentNode.className = "toolbar_span_active";
            }).onmouseup(function ()
            {
                this.parentNode.className = "toolbar_span";
            }).onmouseout(function ()
            {
                this.parentNode.className = "toolbar_span";
            }).each(function (index)
            {
                var className = " toolbar_button" + (leftAlign ? 1 : (index % 3));
                var icon = this.getAttribute("icon");
                if (icon)
                {
                    className += " toolbar_button0_icon";
                    this.style.backgroundImage = "url(" + icon + ")";
                }

                this.className += className;

                var menu = this.getAttribute("menu"), button, click0, click1;
                if (menu == "date")
                {
                    var input = document.createElement("input");
                    input.style.width = "0";
                    input.style.height = "0";
                    input.style.borderWidth = "0";
                    this.parentNode.insertBefore(input, this);

                    this.menu = menu;
                    click0 = this.onclick;
                    button = this;
                    var onpicked = function ()
                    {
                        button.date = Cyan.Date.parse(input.value, "yyyy-MM-dd");
                        button.onclick = click0;
                        try
                        {
                            button.click();
                        }
                        finally
                        {
                            button.onclick = click1;
                        }
                    };
                    click1 = this.onclick = function ()
                    {
                        WdatePicker({el: input, dateFmt: "yyyy-MM-dd", onpicked: onpicked});
                    };
                }
                else if (menu)
                {
                    this.menu = menu;
                    try
                    {
                        menu = eval(menu);
                    }
                    catch (e)
                    {
                        menu = null;
                    }

                    if (menu)
                    {
                        click0 = this.onclick;
                        button = this;
                        menu = new Cyan.Menu(menu);

                        click1 = this.onclick = function ()
                        {
                            menu.showWith(this);
                        };
                        if (click0)
                        {
                            menu.onselect = function (widget)
                            {
                                var items = widget.selectedItems();
                                button.value = Cyan.get(items, "value").join(",");

                                button.onclick = click0;
                                try
                                {
                                    button.click();
                                }
                                finally
                                {
                                    button.onclick = click1;
                                }
                            };
                        }
                    }
                }
            });

            Cyan.$$(toolbar).$(">div>span").each(function ()
            {
                this.className = "toolbar_span";
            });
        }

        main.appendChild(mainBodyDiv);

        if (pageBody && pageBody.className &&
                Cyan.Array.contains(pageBody.className.split(" "), "bottom"))
        {
            bottomToolBar = document.createElement("div");
            bottomToolBar.className = "toolbar toolbar_bottom ";

            if (leftAlign)
                bottomToolBar.className += " toolbar_left";
            else
                bottomToolBar.className += " toolbar_right";

            main.appendChild(bottomToolBar);

            if (buttons)
            {
                var bottomButtons = document.createElement("div");
                bottomToolBar.appendChild(bottomButtons);
                bottomButtons.className = "buttons";
                bottomButtons.innerHTML = buttons.innerHTML;
                Cyan.Event.initEvent(bottomButtons);
            }

            if (components)
            {
                var bottomComponents = document.createElement("div");
                bottomToolBar.appendChild(bottomComponents);
                bottomComponents.className = "components";
                bottomComponents.innerHTML = components.innerHTML;
            }

            Cyan.$$(bottomButtons).$("button").onmousedown(function ()
            {
                this.parentNode.className = "toolbar_span_active";
            }).onmouseup(function ()
            {
                this.parentNode.className = "toolbar_span";
            }).onmouseout(function ()
            {
                this.parentNode.className = "toolbar_span";
            }).each(function (index)
            {
                var className = " toolbar_button" + (leftAlign ? 1 : (index % 3));
                var icon = this.getAttribute("icon");
                if (icon)
                {
                    className += " toolbar_button0_icon";
                    this.style.backgroundImage = "url(" + icon + ")";
                }

                this.className += className;
            });

            Cyan.$$(bottomToolBar).$(">div>span").each(function ()
            {
                this.className = "toolbar_span";
            });
        }

        var resize = function ()
        {
            var width = window.document.documentElement.clientWidth;
            var height = window.document.documentElement.clientHeight;
            container.style.width = width + "px";
            container.style.height = height + "px";

            if (leftPanel && !leftCollapsed)
            {
                leftPanel.style.height = height + "px";
                width -= Cyan.Elements.getComponentSize(leftPanel).width + 1;
            }

            main.style.width = width + "px";
            main.style.height = height + "px";

            if (toolbar)
                height -= Cyan.Elements.getComponentSize(toolbar).height;

            if (moreComponents && moreComponentsExpanded)
                height -= Cyan.Elements.getComponentSize(moreComponents).height;

            if (bottomToolBar)
                height -= Cyan.Elements.getComponentSize(bottomToolBar).height;

            mainBodyDiv.style.width = width + "px";
            mainBodyDiv.style.height = height + "px";
            if (window.mainBody && mainBody.resize)
                mainBody.resize();
        };

        resize();
        if (window.mainBody && mainBody.render)
            mainBody.render();

        if (window.left && window.left.render)
            window.left.render();

        Cyan.attach(window, "resize", function ()
        {
            if (!window.frameElement || !window.frameElement.hidden)
                resize();
        });

        var subBody = Cyan.$("subBody");
        if (subBody)
        {
            var subBodyContainer = document.createElement("DIV");
            document.body.appendChild(subBodyContainer);
            subBodyContainer.style.position = "absolute";

            var width = 540;
            var height = 310;
            var bodySize = Cyan.getBodySize();
            subBodyContainer.style.width = width + "px";
            subBodyContainer.style.height = height + "px";

            subBodyContainer.style.left = (bodySize.width - width - 20) + "px";
            subBodyContainer.style.top = "100px";// (bodySize.height - height - 20) + "px";

            subBody.style.width = width + "px";
            subBody.style.height = height + "px";
            subBody.style.position = "absolute";
            subBodyContainer.appendChild(subBody);
        }
    }
    else
    {
        Cyan.Plain.Panel.render(document.body);
    }

    Cyan.Plain.Form.renderField();
    if (moreComponents)
        moreComponents.style.display = "none";

    var displayComponent = function (component, hidden)
    {
        component = Cyan.$(component);

        while (component && component.className != "toolbar_span" && component.nodeName != "BODY")
        {
            component = component.parentNode;
        }

        if (!component || component.nodeName == "BODY")
            return;

        if (hidden)
            component.style.display = "none";
        else
            component.style.display = "";
    };

    window.hideComponent = function (component)
    {
        displayComponent(component, true);
    };

    window.showComponent = function (component)
    {
        displayComponent(component, false);
    };

    System.hideComponent = window.hideComponent;
    System.showComponent = window.showComponent;
});

System.getButton = function (id)
{
    return Cyan.$(id);
};

System.hideButton = function (id)
{
    var button = Cyan.$(id);
    if (button)
        button.style.display = "none";
};

System.showButton = function (id)
{
    var button = Cyan.$(id);
    if (button)
        button.style.display = "";
};

System.getComponent = function (id)
{
    return Cyan.$(id);
};