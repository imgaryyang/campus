Cyan.Extjs.Panel = {};

Cyan.Extjs.Panel.divIds = {};
Cyan.Extjs.Panel.classes = {};
Cyan.Extjs.Panel.attributes = {};
Cyan.Extjs.Panel.panels = {};

Cyan.Extjs.Panel.generateDivId = function ()
{
    var id;
    while (Cyan.Extjs.Panel.divIds[id = "cyan$ext$panel$" + Math.random().toString().substring(2)]);
    Cyan.Extjs.Panel.divIds[id] = true;
    return id;
};

/**
 * 渲染一个元素及其子元素
 * @param element 要渲染的html元素
 */
Cyan.Extjs.Panel.render = function (element)
{
    element = element.nodeName ? element : this;

    //如果不渲染此元素，则尝试渲染子元素
    if (element.nodeName && element.nodeType != 3)
    {
        Cyan.Extjs.Panel.renderOne(element);
        for (var i = 0; i < element.childNodes.length; i++)
            Cyan.Extjs.Panel.render(element.childNodes[i]);
    }
};

/**
 * 渲染一个元素，返回渲染后的extjs控件
 * @param element 要渲染的html元素
 */
Cyan.Extjs.Panel.renderOne = function (element)
{
    element = element.nodeName ? element : this;

    if (element.nodeName == "DIV")
    {
        var panelType = Cyan.Extjs.Panel.getPanelType(element);
        if (panelType)
        {
            var f = Cyan.Extjs.Panel["render" + panelType.charAt(0).toUpperCase() + panelType.substring(1)];
            if (f && (f instanceof Function))
            {
                var panel = f(element, Cyan.Extjs.Panel.getPanelProperties(element, panelType));
                if (element.id)
                {
                    Cyan.Extjs.Panel.panels[element.id] = panel;
                }
                return panel;
            }
        }
    }

    return null;
};

/**
 * 注册一个class对应到某种panel
 * @param className className
 * @param type panel的类型
 * @param properties panel的属性
 */
Cyan.Extjs.Panel.registerClass = function (className, type, properties)
{
    Cyan.Extjs.Panel.classes[className] = {type: type, properties: properties};
};

/**
 * 注册一个html元素属性对应到一个panel组件的属性
 * @param attributeName html元素属性名
 * @param propertyName panel组件属性名
 */
Cyan.Extjs.Panel.registerAttribute = function (attributeName, propertyName)
{
    propertyName = propertyName || attributeName;
    Cyan.Extjs.Panel.attributes[attributeName] = propertyName;
};

/**
 * 获得元素要渲染成的panel的类型
 * @param element 要渲染的html元素
 */
Cyan.Extjs.Panel.getPanelType = function (element)
{
    if (element.className)
    {
        var ss = element.className.split(" ");
        for (var i = 0; i < ss.length; i++)
        {
            var panelInfo = Cyan.Extjs.Panel.classes[ss[i]];
            if (panelInfo)
                return panelInfo.type;
        }
    }
    return null;
};

/**
 * 获得要渲染成的panel的属性
 * @param element 要渲染的html元素
 * @param panelType 要渲染成的panel的类型
 */
Cyan.Extjs.Panel.getPanelProperties = function (element, panelType)
{
    var properties = {};
    if (element.className)
    {
        var panelInfo = Cyan.Extjs.Panel.classes[element.className];
        if (panelInfo && panelInfo.type == panelType)
            Cyan.clone(panelInfo["properties"], properties);
    }

    for (var attribute in Cyan.Extjs.Panel.attributes)
        properties[Cyan.Extjs.Panel.attributes[attribute]] = element.getAttribute(attribute) || element[attribute];

    if (panelType != "tab")
        Cyan.Extjs.Panel.initSize(element, properties);
    if (properties.title && properties.border == null)
        properties.border = true;

    return properties;
};

/**
 * 初始化panel的大小
 * @param element html元素
 * @param properties panel的属性对象
 */
Cyan.Extjs.Panel.initSize = function (element, properties)
{
    var size = Cyan.Elements.getComponentSize(element);
    if (!properties.width)
        properties.width = size.width;
    if (!properties.height)
        properties.autoHeight = true;
};

/**
 * 将一个元素渲染成panel
 * @param element html元素
 * @param properties panel的属性
 */
Cyan.Extjs.Panel.renderPanel = function (element, properties)
{
    var id = Cyan.Extjs.Panel.generateDivId();
    properties.html = "<div id='" + id + "'></div>";
    var component = new Ext.Panel(properties);
    component.render(element);
    var div = Cyan.$(id);
    while (element.childNodes.length > 1)
        div.appendChild(element.childNodes[0]);

    if (Cyan.navigator.name == "IE6")
    {
        var f = function ()
        {
            div.style.background = "";
        };

        Cyan.attach(document.body, "click", f);
        Cyan.attach(document.body, "dblclick", f);
        Cyan.attach(document.body, "contextmenu", f);
    }

    return component;
};

/**
 * 将一个元素渲染成tab页
 * @param element html元素
 * @param properties panel的属性
 */
Cyan.Extjs.Panel.renderTab = function (element, properties)
{
    var items = [];
    var divs = [];
    var divIds = [];
    var tabs = [];

    var activeTab = -1;
    var i;
    for (i = 0; i < element.childNodes.length; i++)
    {
        var child = element.childNodes[i];
        if (child.nodeName == "DIV")
        {
            if (child.style.display == "none")
            {
                child.style.display = "block";
            }
            else if (activeTab == -1 || (child.getAttribute("active") != null
                    && child.getAttribute("active") != "false"))
            {
                activeTab = items.length;
            }

            var id = Cyan.Extjs.Panel.generateDivId();
            var panelProperties = Cyan.Extjs.Panel.getPanelProperties(child, "panel");

            panelProperties.html = "<div id='" + id + "'></div>";
            panelProperties.listeners = {
                beforeshow: function ()
                {
                    Cyan.fireEvent(Cyan.$(this.divId).childNodes[0], "focus");
                },
                beforehide: function ()
                {
                    Cyan.fireEvent(Cyan.$(this.divId).childNodes[0], "blur");

                    try
                    {
                        var activeElement = document.activeElement;
                        if (activeElement && activeElement.nodeName != "BODY")
                            activeElement.blur();
                    }
                    catch (e)
                    {
                    }
                }
            };
            var tabPanel = items[items.length] = new Ext.Panel(panelProperties);
            tabPanel.divId = id;
            divs.push(child);
            divIds.push(id);
            tabs.push(tabPanel);

            if (child.id)
                Cyan.Extjs.Panel.panels[child.id] = tabPanel;
        }
    }

    properties.items = items;
    properties.deferredRender = false;
    properties.activeTab = activeTab == -1 ? 0 : activeTab;
    properties.enableTabScroll = true;
    var component = new Ext.TabPanel(properties);
    component.render(element);

    for (i = 0; i < divs.length; i++)
    {
        var div = divs[i];
        div.cyanExtjsTab = tabs[i];
        Cyan.Class.overwrite(div, "active", function ()
        {
            if (this.inherited)
                this.inherited();

            component.activate(this.cyanExtjsTab);
        });
        div.hide = function ()
        {
            component.hideTabStripItem(this.cyanExtjsTab);
        };
        div.show = function ()
        {
            component.unhideTabStripItem(this.cyanExtjsTab);
        };

        Cyan.$(divIds[i]).appendChild(div);
    }

    return component;
};

Cyan.Extjs.Panel.getPanel = function (id)
{
    return Cyan.Extjs.Panel.panels[id];
};

Cyan.Panel = Cyan.Class.extend(function (name)
{
    this.name = name;
}, Cyan.Widget);

Cyan.Panel.prototype.init = function (el)
{
    this.el = Cyan.$(el);

    if (!el)
        this.autoRender = false;

    this.id = Cyan.generateId("panel_body");
    var extPanel = this.extPanel =
            new Ext.Panel({
                html: "<div id=\"" + this.id + "\" style=\"overflow-y:auto;overflow-x:hidden;height:100%\"></div>"
            });

    if (this.autoRender)
    {
        var panel = this;
        Cyan.onload(function ()
        {
            panel.render();
        });
    }
};


Cyan.Panel.prototype.render = function ()
{
    this.extPanel.render();
};

Cyan.Panel.prototype.getExtComponent = function ()
{
    return this.extPanel;
};

Cyan.Panel.prototype.getElement = function ()
{
    return Cyan.$(this.id);
};

Cyan.Panel.prototype.showLoading = function ()
{
    if (!this.mask)
    {
        this.mask = new Ext.LoadMask(this.extPanel.getEl(), {});
    }

    this.mask.show();
};

Cyan.Panel.prototype.hideLoading = function ()
{
    if (this.mask)
        this.mask.hide();
};
