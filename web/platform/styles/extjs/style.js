Cyan.Widget.prototype.autoRender = false;
Cyan.importJs("widgets/menu.js");
Cyan.importJs("adapters/extjs/form.js");
Cyan.importJs("adapters/extjs/panel.js");
Cyan.importCss("/platform/styles/extjs/style.css");

System.baseStylePath = "/platform/styles/extjs";

TooltipTextfield = Ext.extend(Ext.form.TextField, {
    title: "",
    onRender: function (ct, position)
    {
        TooltipTextfield.superclass.onRender.call(this, ct, position);
        if (this.title)
        {
            new Ext.ToolTip({
                target: this.id,
                trackMouse: false,
                draggable: true,
                maxWidth: 200,
                minWidth: 100,
                html: this.title
            });
        }
    }
});
Ext.reg('tooltiptextfield', TooltipTextfield);

System.BUTTONIDS = {};

function menuButtonHandler()
{
    this.showMenu();
}

System.showFrame = function ()
{
    var parentIframe = window.frameElement;
    var pageObject, title;
    if (parentIframe)
        pageObject = parentIframe.pageObject;

    if (pageObject && pageObject.showFrame)
        pageObject.showFrame();
};

System.getButton = function (id)
{
    var button = Cyan.$(id);

    if (button)
        return Cyan.$(button.buttonId);
};

System.hideButton = function (id)
{
    var button = System.getButton(id);
    if (button)
        button.style.display = "none";
};

System.showButton = function (id)
{
    var button = System.getButton(id);
    if (button)
        button.style.display = "";
};

System.getComponent = function (id)
{
    return Cyan.$(id);
};

Cyan.onload(function ()
        {
            if (Cyan.Table)
            {
                Cyan.Table.prototype.createPagingBar = function ()
                {
                    var table = this;
                    var showPage = function ()
                    {
                        table.reload({pageNo: this.page});
                    };

                    var createPage = function (page, text, activePage, gridPagesSpan)
                    {
                        var span = document.createElement("SPAN");
                        if (page == activePage)
                        {
                            span.innerHTML = text;
                            span.className = "gridPagesItemSpan gridPagesItemSpan_selected";
                        }
                        else
                        {
                            var href = document.createElement("A");
                            href.innerHTML = text;
                            href.href = "#";
                            href.page = page;
                            href.onclick = showPage;

                            span.appendChild(href);

                            span.className = "gridPagesItemSpan";
                        }

                        gridPagesSpan.parentNode.appendChild(span);
                    };
                    return new Ext.PagingToolbar({
                        pageSize: this.pageSize,
                        store: this.store,
                        displayInfo: true,
                        displayMsg: "<span id='gridPagesSpan'>当前显示:{0}-{1}条 共{2}条</span>",
                        listeners: {
                            change: function (bar, data)
                            {
                                var gridPagesSpan = Cyan.$("gridPagesSpan");
                                if (gridPagesSpan)
                                {
                                    var pages = data.pages;
                                    var activePage = data.activePage;

                                    var startPage = Math.floor((activePage - 1) / 10) * 10 + 1;
                                    var endPage = startPage + 9;

                                    if (endPage > pages)
                                        endPage = pages;

                                    if (startPage > 1)
                                    {
                                        createPage(1, "<<", activePage, gridPagesSpan);
                                    }

                                    for (var i = startPage; i <= endPage; i++)
                                    {
                                        createPage(i, "[" + i + "]", activePage, gridPagesSpan);
                                    }

                                    if (endPage < pages)
                                    {
                                        createPage(endPage + 1, ">>", activePage, gridPagesSpan);
                                    }
                                }
                            }
                        }
                    });
                };
            }

            var parentIframe = window.frameElement;
            var pageObject, title;
            if (parentIframe)
                pageObject = parentIframe.pageObject;

            if (pageObject && pageObject.showFrame)
                pageObject.showFrame();


            Cyan.Extjs.Form.isHtmlEditor = function (component)
            {
                return Cyan.Array.contains(component.className.split(" "), "htmleditor_simple");
            };

            Cyan.Extjs.Form.HtmlEditor.fontFamilies =
                    ['Arial', 'Courier New', 'Tahoma', 'Times New Roman', 'Verdana', "宋体", "仿宋", "新宋体", "黑体", "楷体",
                        "隶书", "幼圆",
                        "华文中宋", "华文仿宋", "华文细黑", "华文行楷", "华文新魏", "华文彩云", "方正舒体", "方正姚体"];
            Cyan.Extjs.Form.HtmlEditor.defaultFont = "宋体";
            Cyan.Extjs.Form.HtmlEditor.defaultValue =
                    "<span style='font-family:宋体;'><font size='2'>&nbsp;</font></span>";

            Cyan.Extjs.Panel.registerClass("panel", "panel");
            Cyan.Extjs.Panel.registerClass("tab", "tab");
            Cyan.Extjs.Panel.registerAttribute("legend", "title");
            Cyan.Extjs.Panel.registerAttribute("collapsible", "collapsible");

            var win = Cyan.Window.getWindow();
            if (!win)
                Ext.form.Field.prototype.msgTarget = "qtip";
            else
                Ext.form.Field.prototype.msgTarget = "under";

            var dialog = null;
            if (pageObject)
            {
                if (pageObject.setTitle)
                {
                    title = System.getTitle();

                    if (title)
                    {
                        var s = title;
                        if (s.length > 14)
                            s = s.substring(0, 12) + "...";
                        pageObject.setTitle(s);
                    }
                }

                System.page = pageObject;
                System.opener = pageObject.opener;
            }
            else
            {
                dialog = Cyan.Window.getWindow();
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

            var mainBody = Cyan.$("mainBody");
            if (mainBody)
            {
                var mainComponent, mainBodyComponent, leftComponent, leftSearch = false;

                var initPage = function ()
                {
                    var b = mainBodyComponent != null;
                    if (leftComponent)
                    {
                        var left = window.left;
                        mainBodyComponent.bodyStyle = System.BODYSTYLE;
                        var leftPanel;
                        if (left.searchByText)
                        {
                            leftSearch = true;
                            leftComponent.region = "center";
                            leftPanel = new Ext.Panel({
                                layout: 'border',
                                items: [
                                    leftComponent,
                                    {
                                        region: "north",
                                        html: "<div id='left_search'><input name='left_search_input' id='left_search_input' class='search'></div>"
                                    }
                                ]
                            });
                            leftPanel.width = leftComponent.width;
                        }
                        else
                        {
                            leftPanel = leftComponent;
                        }

                        if (!leftPanel.width)
                            leftPanel.width = 160;
                        leftPanel.bodyStyle = System.BODYSTYLE;
                        leftPanel.split = true;
                        leftPanel.region = "west";

                        mainBodyComponent.region = "center";
                        mainComponent = new Ext.Panel({
                            layout: 'border',
                            items: [
                                //左边菜单
                                leftPanel,
                                //右边的主窗口
                                mainBodyComponent
                            ]
                        });
                    }
                    else if (mainBodyComponent)
                    {
                        mainComponent = mainBodyComponent;
                        mainComponent.bodyStyle = System.BODYSTYLE;
                    }
                    else
                    {
                        mainComponent = new Ext.Panel({
                            html: "<div id='$$$mainBody$$$'></div>",
                            bodyStyle: System.BODYSTYLE2 + ";overflow-y:auto;"
                        });
                        mainBodyComponent = mainComponent;
                    }

                    mainComponent.frame = true;
                    if (!dialog)
                        mainComponent.elements += ',header';
                    mainComponent.border = true;
                    var remark = System.getRemark() || title;
                    if (remark)
                        mainComponent.title = remark;

                    System.setRemark = function (remark)
                    {
                        mainComponent.setTitle(remark);
                    };

                    var components = Cyan.$("components");
                    var buttons = Cyan.$("buttons");
                    var moreComponents = Cyan.$("moreComponents");

                    if (components || buttons)
                    {
                        mainBodyComponent.topToolbar = new Ext.Toolbar({});
                        mainBodyComponent.elements += ',tbar';

                        var pageBody = Cyan.$("pageBody");

                        var bootom = pageBody && pageBody.className && pageBody.className.split(" ").contains("bottom");
                        if (bootom)
                        {
                            mainBodyComponent.bottomToolbar = new Ext.Toolbar({});
                            mainBodyComponent.elements += ',bbar';
                        }

                        var moreBar;

                        mainBodyComponent.on('render', function ()
                        {
                            if (pageBody && pageBody.className && pageBody.className.split(" ").contains("left"))
                            {
                                this.topToolbar.addText("&nbsp;&nbsp;");
                                if (bootom)
                                    this.bottomToolbar.addText("&nbsp;&nbsp;");
                            }
                            else
                            {
                                if (leftComponent)
                                {
                                    window.collapseLeft = function ()
                                    {
                                        leftButton.btnEl.dom.title = "展开";
                                        leftButton.btnEl.dom.style.backgroundImage =
                                                "url(/platform/styles/icons/more_right.gif)";
                                        leftPanel.collapse(false);
                                    };
                                    window.expandLeft = function ()
                                    {
                                        leftButton.btnEl.dom.title = "收起";
                                        leftButton.btnEl.dom.style.backgroundImage =
                                                "url(/platform/styles/icons/more_left.gif)";
                                        leftPanel.expand(false);
                                    };
                                    var leftButton = new Ext.Toolbar.Button(
                                            {
                                                handler: function ()
                                                {
                                                    if (!leftPanel.collapsed)
                                                    {
                                                        collapseLeft();
                                                    }
                                                    else
                                                    {
                                                        expandLeft();
                                                    }
                                                },
                                                cls: "x-btn-more-icon",
                                                icon: "/platform/styles/icons/more_left.gif",
                                                tooltip: "收起",
                                                tooltipType: "title"
                                            });
                                    this.topToolbar.addItem(leftButton);
                                }

                                this.topToolbar.addItem(new Ext.Panel({
                                    html: "<div id='toolbar_left'></div>"
                                }));
                                this.topToolbar.addFill();
                                if (bootom)
                                    this.bottomToolbar.addFill();
                            }

                            var bars = bootom ? [this.topToolbar, this.bottomToolbar] : [this.topToolbar];

                            if (components)
                                System.initToolBars(components, bars);

                            if (moreComponents)
                            {
                                var button = new Ext.Toolbar.Button(
                                        {
                                            handler: function ()
                                            {
                                                var barHeight;
                                                var mainBodyd = Cyan.$$(mainBodyComponent.getEl().dom);
                                                var scroller = mainBodyd.$(".x-grid3-scroller")[0];
                                                var grid = mainBodyd.$(".x-grid3")[0];
                                                var panelBody = grid.parentNode;

                                                if (moreBar.isVisible())
                                                {
                                                    this.btnEl.dom.title = "展开更多查询条件";
                                                    this.btnEl.dom.style.backgroundImage =
                                                            "url(/platform/styles/icons/more_down.gif)";

                                                    barHeight =
                                                            Cyan.Elements.getComponentSize(moreBar.getEl().dom).height;

                                                    moreBar.hide();

                                                    scroller.style.height =
                                                            (Cyan.toInt(scroller.style.height) + barHeight) + "px";
                                                    grid.style.height =
                                                            (Cyan.toInt(grid.style.height) + barHeight) + "px";
                                                    panelBody.style.height =
                                                            (Cyan.toInt(panelBody.style.height) + barHeight) + "px";
                                                }
                                                else
                                                {
                                                    this.btnEl.dom.title = "收起更多查询条件";
                                                    this.btnEl.dom.style.backgroundImage =
                                                            "url(/platform/styles/icons/more_up.gif)";
                                                    moreBar.show();
                                                    barHeight =
                                                            Cyan.Elements.getComponentSize(moreBar.getEl().dom).height;

                                                    scroller.style.height =
                                                            (Cyan.toInt(scroller.style.height) - barHeight) + "px";
                                                    grid.style.height =
                                                            (Cyan.toInt(grid.style.height) - barHeight) + "px";
                                                    panelBody.style.height =
                                                            (Cyan.toInt(panelBody.style.height) - barHeight) + "px";
                                                }
                                            },
                                            cls: "x-btn-more-icon",
                                            icon: "/platform/styles/icons/more_down.gif",
                                            tooltip: "展开更多查询条件",
                                            tooltipType: "title"
                                        });
                                this.topToolbar.addItem(button);
                            }

                            if (buttons)
                                System.initToolBars(buttons, bars);

                            if (moreComponents)
                            {
                                var paddingLeft = 104;
                                if (leftComponent)
                                    paddingLeft = 20;
                                moreBar = new Ext.Toolbar({
                                    style: "padding-left:" + paddingLeft +
                                    "px;padding-top:15px;padding-bottom:15px;"
                                });
                                System.initToolBars(moreComponents, [moreBar]);
                                moreBar.render(this.tbar);
                                moreBar.hide();
                            }
                        }, mainBodyComponent);
                    }

                    var formPanel;

                    if (Cyan.$$("#mainBody form").length)
                    {
                        formPanel = new Ext.Panel({
                            layout: "fit",
                            items: [mainComponent]
                        });
                    }
                    else
                    {
                        formPanel = new Ext.form.FormPanel({
                            layout: "fit",
                            items: [mainComponent]
                        });
                    }
                    var viewPort = new Ext.Viewport({
                        layout: "fit",
                        items: [formPanel]
                    });
                    var form = Cyan.$$("form").first;
                    if (components || buttons)
                    {
                        if (components)
                            form.appendChild(components);
                        if (buttons)
                            form.appendChild(buttons);
                    }

                    if (moreComponents)
                    {
                        var moreDiv = document.createElement("DIV");
                        form.appendChild(moreDiv);
                    }

                    var div;
                    if (Cyan.navigator.isFF() || Cyan.navigator.isIE())
                    {
                        window.setTimeout(function ()
                        {
                            Cyan.Extjs.Form.renderField();
                            Cyan.Extjs.Panel.render(document.body);
                            div = Cyan.$("$$$mainBody$$$");
                            if (div)
                            {
                                while (mainBody.childNodes.length > 0)
                                    div.appendChild(mainBody.childNodes[0]);
                            }
                        }, 10);
                    }
                    else
                    {
                        Cyan.Extjs.Form.renderField();
                        Cyan.Extjs.Panel.render(document.body);
                        div = Cyan.$("$$$mainBody$$$");
                        if (div)
                        {
                            while (mainBody.childNodes.length > 0)
                                div.appendChild(mainBody.childNodes[0]);
                        }
                    }

                    if (pageObject && pageObject.showFrame)
                        pageObject.showFrame();

                    if (leftSearch)
                    {
                        Cyan.run(function ()
                        {
                            return Cyan.$("left_search_input");
                        }, function ()
                        {
                            window.left.bindSearch("left_search_input");
                        });
                    }

                    Cyan.$$("form")[0].cyanOnSubmit = function ()
                    {
                        return true;
                    };

                    var displayComponent = function (component, hidden)
                    {
                        component = Cyan.$(component);

                        while (component && component.nodeName != "TD" && component.nodeName != "BODY")
                        {
                            component = component.parentNode;
                        }

                        if (!component || component.nodeName == "BODY")
                            return;

                        if (component.className != "x-toolbar-cell")
                            return;

                        if (hidden)
                            component.style.display = "none";
                        else
                            component.style.display = "";

                        var preTd = component.previousSibling;
                        var childNodes = preTd.childNodes;
                        for (var i = 0; i < childNodes.length; i++)
                        {
                            if (childNodes[i].className == "xtb-text")
                            {
                                if (hidden)
                                    preTd.style.display = "none";
                                else
                                    preTd.style.display = "";
                                break;
                            }
                        }

                        preTd = preTd.previousSibling;
                        childNodes = preTd.childNodes;
                        for (i = 0; i < childNodes.length; i++)
                        {
                            if (childNodes[i].className == "xtb-text")
                            {
                                if (hidden)
                                    preTd.style.display = "none";
                                else
                                    preTd.style.display = "";
                                break;
                            }
                        }
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
                };

                if (window.mainBody && window.mainBody.getExtComponent)
                {
                    Cyan.run(function ()
                    {
                        mainBodyComponent = window.mainBody.getExtComponent();
                        if (window.left && window.left.getExtComponent)
                        {
                            leftComponent = window.left.getExtComponent();
                            return mainBodyComponent && leftComponent;
                        }
                        else
                        {
                            return mainBodyComponent;
                        }
                    }, initPage);
                }
                else
                {
                    initPage();
                }

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

                if (Cyan.navigator.isIE() && Cyan.navigator.version == "9")
                {
                    Cyan.Class.overwrite(System, "reload", function ()
                    {
                        setTimeout(this.inherited, 100);
                    });
                }
            }
            else
            {
                if (win)
                {
                    title = System.getTitle();
                    if (title && !win.getTitle())
                        win.setTitle(title);
                }

                Cyan.Extjs.Panel.render(document.body);
                Cyan.Extjs.Form.renderField();
            }
        }
);

System.initToolBars = function (component, toolbars)
{
    var nodeName = component.nodeName;
    var i;
    if (nodeName == "SPAN" || nodeName == "DIV" || nodeName == "LABEL")
    {
        //span,div,label，添加子元素

        var childNodes = component.childNodes;
        var n = childNodes.length;
        var b, childNode;
        for (i = 0; i < n; i++)
        {
            childNode = childNodes[i];
            if (childNode.nodeType != 3 || childNode.nodeValue.trim())
                b = System.initToolBars(childNode, toolbars);
        }

        for (i = 0; i < n; i++)
        {
            childNode = childNodes[i];
            if (childNode.nodeName != "SPAN" && childNode.nodeName != "DIV" && childNode.nodeName != "LABEL" &&
                    childNode.nodeName != "BUTTON" && (childNode.nodeName != "INPUT" || childNode.type != "button"))
            {
                component.removeChild(childNode);
                n--;
                i--;
            }
        }

        if (!b)
        {
            for (i = 0; i < toolbars.length; i++)
                toolbars[i].addText(" ");
        }

        return true;
    }

    for (i = 0; i < toolbars.length; i++)
        System.initToolBar(component, toolbars[i]);

    return false;
};

(function ()
{
    var getBoxLabel0 = function (node)
    {
        if (node.nodeType == 3 && node.nodeValue.trim())
        {
            var text = node.nodeValue;
            node.value = "";
            return text;
        }
        else
        {
            var nodeName = node.nodeName;
            if (nodeName == "SPAN" || nodeName == "DIV" || nodeName == "LABEL")
            {
                var childNodes = node.childNodes;
                if (childNodes.length)
                    return getBoxLabel0(childNodes[0]);
            }
        }

        return "";
    };

    var getBoxLabel = function (node)
    {
        if (node.nextSibling)
            return getBoxLabel0(node.nextSibling);
        else
            return getBoxLabel0(node.parentNode);
    };

    var toYearMonths = function (component)
    {
        var format = Cyan.Extjs.Form.getDateFormat(component);
        var date = Cyan.Date.parse(component.value, format);

        var select = function ()
        {
            var year = yearsCombox.getValue();
            var month = monthsCombox.getValue();

            if (year && month)
            {
                var date = new Date();
                date.setFullYear(parseInt(year));
                date.setMonth(parseInt(month));
                date.setDate(1);
                date.setHours(0);
                date.setHours(0);
                date.setSeconds(0);
                date.setMilliseconds(0);

                hidden.setValue(Cyan.Date.format(date, format));
            }
            else
            {
                hidden.setValue("");
            }
        };

        var startYear = parseInt(component.getAttribute("startYear"));
        var endYear = parseInt(component.getAttribute("endYear"));

        if (!endYear)
            endYear = new Date().getFullYear();

        if (!startYear)
            startYear = -10;

        if (startYear < 0)
            startYear = endYear + startYear;

        if (component.getAttribute("desc") != "false")
        {
            var temp = startYear;
            startYear = endYear;
            endYear = temp;
        }

        var years = [{value: "", text: "----"}], i, year;

        if (endYear > startYear)
        {
            for (i = startYear; i <= endYear; i++)
            {
                year = i.toString();
                years.push({value: year, text: year});
            }
        }
        else
        {
            for (i = startYear; i >= endYear; i--)
            {
                year = i.toString();
                years.push({value: year, text: year});
            }
        }

        var yearsStore = new Ext.data.JsonStore({
            data: years,
            autoLoad: true,
            fields: ["value", "text"]
        });

        var yearsCombox = new Ext.form.ComboBox({
            value: date ? date.getFullYear().toString() : "",
            editable: false,
            triggerAction: "all",
            mode: 'local',
            store: yearsStore,
            valueField: "value",
            displayField: "text",
            width: 55,
            listeners: {
                select: select
            }
        });

        var months = [{value: "", text: "----"}];

        for (i = 0; i < 12; i++)
        {
            months.push({value: i.toString(), text: (i + 1).toString()});
        }

        var monthsStore = new Ext.data.JsonStore({
            data: months,
            autoLoad: true,
            fields: ["value", "text"]
        });

        var monthsCombox = new Ext.form.ComboBox({
            value: date ? date.getMonth().toString() : "",
            editable: false,
            triggerAction: "all",
            mode: 'local',
            store: monthsStore,
            valueField: "value",
            displayField: "text",
            width: 40, listeners: {
                select: select
            }
        });

        var hidden = new Ext.form.Hidden({
            name: component.name,
            value: component.value
        });

        return [yearsCombox, " ", monthsCombox, hidden];
    };

    var getDays = function (month)
    {
        var days = [{value: "", text: "----"}];

        if (month != null)
        {
            var maxDay = Cyan.Date.getMaxDay(month);

            for (var i = 1; i <= maxDay; i++)
            {
                var day = i.toString();
                days.push({value: day, text: day});
            }
        }

        return days;
    };

    var toMonthDays = function (component)
    {
        var format = Cyan.Extjs.Form.getDateFormat(component);
        var date = Cyan.Date.parse(component.value, format);

        var select = function ()
        {
            var month = monthsCombox.getValue();
            var day = daysCombox.getValue();

            if (month && day)
            {
                var date = new Date();
                date.setFullYear(0);
                date.setMonth(parseInt(month));
                date.setDate(parseInt(day));
                date.setHours(0);
                date.setHours(0);
                date.setSeconds(0);
                date.setMilliseconds(0);

                hidden.setValue(Cyan.Date.format(date, format));
            }
            else
            {
                hidden.setValue("");
            }
        };

        var months = [{value: "", text: "----"}], i;

        for (i = 0; i < 12; i++)
        {
            months.push({value: i.toString(), text: (i + 1).toString()});
        }

        var monthsStore = new Ext.data.JsonStore({
            data: months,
            autoLoad: true,
            fields: ["value", "text"]
        });

        var month;
        if (date)
            month = date.getMonth();

        var monthsCombox = new Ext.form.ComboBox({
            value: month == null ? "" : month.toString(),
            editable: false,
            triggerAction: "all",
            mode: 'local',
            store: monthsStore,
            valueField: "value",
            displayField: "text",
            width: 40, listeners: {
                select: function ()
                {
                    var month = this.getValue();
                    var days = getDays(month ? parseInt(month) : null);
                    daysStore.loadData(days);
                    var day = daysCombox.getValue();

                    if (day >= days.length)
                        daysCombox.setValue(days.length - 1);

                    select();
                }
            }
        });

        var daysStore = new Ext.data.JsonStore({
            data: getDays(month),
            autoLoad: true,
            fields: ["value", "text"]
        });

        var daysCombox = new Ext.form.ComboBox({
            value: date ? date.getDate().toString() : "",
            editable: false,
            triggerAction: "all",
            mode: 'local',
            store: daysStore,
            valueField: "value",
            displayField: "text",
            width: 55,
            listeners: {
                select: select
            }
        });

        var hidden = new Ext.form.Hidden({
            name: component.name,
            value: component.value
        });

        return [monthsCombox, " ", daysCombox, hidden];
    };

    System.initToolBar = function (component, toolbar)
    {
        if (component.nodeType == 3)
        {
            toolbar.addText(component.nodeValue.trim());
            return false;
        }

        var nodeName = component.nodeName;
        if (nodeName == "SPAN" || nodeName == "DIV" || nodeName == "LABEL")
        {
            //span,div,label，添加子元素

            var childNodes = component.childNodes;
            var n = childNodes.length;
            var b, i, childNode;
            for (i = 0; i < n; i++)
            {
                childNode = childNodes[i];
                if (childNode.nodeType != 3 || childNode.nodeValue.trim())
                    b = System.initToolBar(childNode, toolbar);
            }

            for (i = 0; i < n; i++)
            {
                childNode = childNodes[i];
                if (childNode.nodeName != "SPAN" && childNode.nodeName != "DIV" && childNode.nodeName != "LABEL" &&
                        childNode.nodeName != "BUTTON" &&
                        (childNode.nodeName != "INPUT" || childNode.type != "button"))
                {
                    component.removeChild(childNode);
                    n--;
                    i--;
                }
            }

            if (!b)
                toolbar.addText(" ");

            return true;
        }

        if (nodeName == "BUTTON" || nodeName == "INPUT" && component.type == "button")
        {
            var menu = component.menu || component.getAttribute("menu");
            var text = component.value || component.innerHTML;
            var button;
            if (menu)
            {
                var extMenu;
                if (menu == "date")
                {
                    extMenu = new Ext.menu.DateMenu();
                    extMenu.on("select", function (picker, date)
                    {
                        component.date = date;
                        component.click();
                    });
                }
                else if (menu == "color")
                {
                    extMenu = new Ext.menu.ColorMenu();
                    extMenu.on("select", function (cm, color)
                    {
                        component.color = color;
                        component.click();
                    });
                }
                else
                {
                    menu = window[menu];
                    if (menu instanceof Cyan.Menu)
                    {
                        extMenu = menu.getExtMenu();
                    }
                    else if (menu instanceof Cyan.Widget)
                    {
                        extMenu = new Cyan.Extjs.WidgetMenu(menu, {
                            onselect: function (widget)
                            {
                                var items = widget.selectedItems();
                                if (items.length)
                                {
                                    component.value = items[0].value;
                                    component.text = items[0].text;
                                    component.click();
                                }
                            }
                        });
                    }
                    else
                    {
                        extMenu = new Cyan.Menu(menu).getExtMenu();
                    }
                }
                button = new Ext.Toolbar.SplitButton({menu: extMenu, handler: menuButtonHandler});
            }
            else
            {
                var buttonId;
                if (component.id)
                {
                    buttonId = "btn_" + component.id;
                }
                else
                {
                    while (System.BUTTONIDS[buttonId = "btn_$" + Math.random().toString().substring(2)]);
                }

                button = new Ext.Toolbar.Button({
                    id: buttonId,
                    handler: function ()
                    {
                        component.click();
                    }
                });

                component.buttonId = buttonId;
            }

            button.text = text;

            var icon = component.icon || component.getAttribute("icon");
            if (icon)
            {
                button.cls = "x-btn-text-icon";
                button.icon = icon;
            }

            toolbar.addItem(button);

            Cyan.Class.overwrite(component, "setText", function (text)
            {
                if (this.inherited)
                    this.inherited(text);
                button.setText(text);
            });
        }
        else
        {
            var items, selectable;
            if (nodeName == "INPUT" && component.type == "checkbox")
            {
                items = [new Ext.form.Checkbox({
                    name: component.name, inputValue: component.value, boxLabel: getBoxLabel(component)
                })];
            }
            if (nodeName == "INPUT" && component.type == "radio")
            {
                items = [new Ext.form.Radio({
                    name: component.name, inputValue: component.value, boxLabel: getBoxLabel(component)
                })];
            }
            else if (nodeName == "INPUT")
            {
                var width;
                if (component.style.width)
                    width = Cyan.toInt(component.style.width);

                var type = component.type;
                if (type == "text")
                {
                    if (Cyan.Extjs.Form.isYearMonth(component))
                    {
                        items = toYearMonths(component);
                    }
                    else if (Cyan.Extjs.Form.isMonthDay(component))
                    {
                        items = toMonthDays(component);
                    }
                    else if (Cyan.Extjs.Form.isDate(component))
                    {
                        items = [new Ext.form.DateField({
                            name: component.name, value: component.value, width: width ||
                            100, format: "Y-m-d"
                        })];
                    }
                    else
                    {
                        selectable = component.selectable || component.getAttribute("selectable");
                        if (selectable)
                        {
                            if (Cyan.isString(selectable))
                                selectable = eval(selectable);
                            var multiple = component.getAttribute("multiple");
                            var editable = component.getAttribute("editable");

                            items = [new Cyan.Extjs.WidgetField({
                                name: component.name, value: component.value,
                                text: Cyan.Extjs.Form.getSelectableText(component), width: width ||
                                100, selectable: selectable, multiple: multiple != null &&
                                multiple != "false", editable: editable != null && editable != "false",
                                onchange: component.onchange
                            })];
                        }
                        else
                        {
                            items = [new TooltipTextfield({
                                name: component.name, value: component.value, width: width ||
                                100, title: component.title
                            })];
                        }
                    }
                }
            }
            else if (nodeName == "SELECT")
            {
                items = [Cyan.Extjs.Form.toCombox(component)];
            }

            if (items)
            {
                for (var k = 0; k < items.length; k++)
                {
                    var item = items[k];
                    if (Cyan.isString(item))
                        toolbar.addText(item);
                    else
                        toolbar.addItem(item);
                }
            }

            if (selectable && selectable.reload)
            {
                setTimeout(function ()
                {
                    Cyan.$(component.name).refresh = function ()
                    {
                        selectable.reload();
                    };
                }, 100);
            }
        }

        return false;
    };
})();

if (!Ext.grid.GridView.prototype.templates)
{
    Ext.grid.GridView.prototype.templates = {};
}
Ext.grid.GridView.prototype.templates.cell = new Ext.Template(
        '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} x-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>',
        '<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
        '</td>'
);
