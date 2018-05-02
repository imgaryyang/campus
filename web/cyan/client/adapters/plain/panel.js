Cyan.Plain.importCss("panel");
Cyan.Plain.importLanguage("panel");
Cyan.importJs("widgets/mask.js");

(function ()
{
    var render = function (element)
    {
        if (element.nodeName && element.nodeType != 3)
        {
            renderOne(element);
            for (var i = 0; i < element.childNodes.length; i++)
                render(element.childNodes[i]);
        }
    };

    var renderOne = function (element)
    {
        if (element.nodeName == "DIV")
        {
            if (element.className == "tab")
            {
                renderTab(element);
            }
        }
    };

    var selectTab = function ()
    {
        var page = this;
        var tab = page;
        while (!Cyan.Array.contains(tab.className.split(" "), "cyan-tab"))
        {
            tab = tab.parentNode;
        }

        tab = Cyan.$$(tab);
        var pages = tab.$(".cyan-tab-pages")[0].childNodes;
        var tabs = tab.$(".cyan-tab-tabs")[0].childNodes;

        for (var i = 0; i < tabs.length; i++)
        {
            var tab1 = tabs[i];
            var page1 = pages[i];
            if (tab1 == page || page1 == page)
            {
                tab1.className = "cyan-tab-selected";
                page1.style.display = "";
            }
            else
            {
                tab1.className = "cyan-tab-unselected";
                page1.style.display = "none";
            }
        }
    };

    var select = function ()
    {
        selectTab(this);
    };

    var renderTab = function (element)
    {
        element.className += " cyan-tab";

        var pages = document.createElement("DIV");
        pages.className = "cyan-tab-pages";

        var tabBar = document.createElement("DIV");
        tabBar.className = "cyan-tab-bar";

        var tabs = document.createElement("DIV");
        tabs.className = "cyan-tab-tabs";
        tabBar.appendChild(tabs);

        var childNodes = Cyan.clone(element.childNodes);
        var n = childNodes.length;

        var activePage, firstPage;
        for (var i = 0; i < n; i++)
        {
            var childNode = childNodes[i];
            if (childNode.nodeName == "DIV")
            {
                if (!firstPage)
                    firstPage = childNode;
                if (!activePage && childNode.getAttribute("active") != null
                        && childNode.getAttribute("active") != "false")
                {
                    activePage = childNode;
                }
            }
        }

        if (!activePage)
            activePage = firstPage;

        for (i = 0; i < n; i++)
        {
            childNode = childNodes[i];
            if (childNode.nodeName == "DIV")
            {
                var selected = childNode == activePage;
                var tab = document.createElement("DIV");
                tab.className = selected ? "cyan-tab-selected" : "cyan-tab-unselected";
                tab.innerHTML = childNode.getAttribute("legend");
                tabs.appendChild(tab);

                if (!selected)
                    childNode.style.display = "none";

                Cyan.attach(tab, "click", selectTab);
                childNode.active = selectTab;
                pages.appendChild(childNode);
            }
        }

        element.appendChild(tabBar);
        element.appendChild(pages);
    };

    Cyan.Plain.Panel = {
        render: render
    };
})();

Cyan.Panel = Cyan.Class.extend(function (name)
{
    this.name = name;
}, Cyan.Widget);

Cyan.Panel.prototype.init = function (el)
{
    this.el = Cyan.$(el);

    if (!el)
        this.autoRender = false;


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
    this.el.innerHTML = "<div id=\"" + this.name + "\" className=\"cyan-panel\"></div>";
};

Cyan.Panel.prototype.getElement = function ()
{
    return Cyan.$(this.name);
};

Cyan.Panel.prototype.showLoading = function ()
{
    if (!this.mask)
    {
        this.mask = new Cyan.LoadingMask(this.el);
        this.mask.message = this.texts.loadingMsg;
    }
    this.mask.show();
};

Cyan.Panel.prototype.hideLoading = function ()
{
    if (this.mask)
        this.mask.hide();
};
