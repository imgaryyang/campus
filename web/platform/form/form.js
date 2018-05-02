Cyan.importJs("/platform/group/member.js");
Cyan.importJs("widgets/tooltip.js");

Cyan.onload(function ()
{
    if (Cyan.Valmiki.ParallelText)
    {
        Cyan.Valmiki.ParallelText.prototype.getText = function (text, callback, html)
        {
            if (html)
            {
                System.Opinion.editHtml(text, callback);
            }
            else
            {
                System.Opinion.edit(text, callback);
            }
        };

        Cyan.importJs("/platform/opinion/opinion.js");
    }

    Cyan.Valmiki.textAreaShowBaks = function (formName, name)
    {
        showTextAreaBaks(formName, name, {
            target: "_page"
        });
    };

    Cyan.Class.overwrite(window, "invokeFormEvent", function (formName, componentName, eventName)
    {
        this.inherited(formName, componentName, eventName, {
            callback: function (result)
            {
                if (Cyan.isString(result))
                {
                    Cyan.message(result);
                }
                else if (Cyan.isBoolean(result) && result)
                {
                    window.location.reload();
                }
            },
            form: Cyan.$$("form")[0]
        });
    });

    setTimeout(function ()
    {
        initPages();

        Cyan.$$("button.btn").each(function ()
        {
            Cyan.ToolTip.render(this.parentNode);
        });
    }, 400);
});

function initPages()
{
    var pages = Cyan.$$("#pages .page");
    var i;

    if (pages.length > 1)
    {
        var select = function ()
        {
            selectTab(this);
        };

        var tabBar = document.createElement("DIV");
        tabBar.className = "tabBar";
        Cyan.$("pages").insertBefore(tabBar, pages[0]);
        var tabs = document.createElement("DIV");
        tabs.className = "tabs";
        tabBar.appendChild(tabs);

        for (i = 0; i < pages.length; i++)
        {
            var page = pages[i];
            if (i > 0)
                page.style.display = "none";

            var tab = document.createElement("DIV");
            tab.className = i == 0 ? "selected" : "unselected";
            tab.innerHTML = page.title;
            tab.title = "点击这里切换到" + page.title;
            tabs.appendChild(tab);

            Cyan.attach(tab, "click", select);
            page.active = select;
        }
    }

    for (i = 0; i < pages.length; i++)
    {
        pages[i].title = "";
    }

    if (Cyan.Arachne.form.defaultPage)
    {
        selectPage(Cyan.Arachne.form.defaultPage);
    }

    window.setTimeout(resize, 50);
}

function selectTab(tab)
{
    var tabs = Cyan.$$("#pages .tabs")[0].childNodes;
    var pages = Cyan.$$("#pages .page");

    for (var i = 0; i < tabs.length; i++)
    {
        var tab1 = tabs[i];
        var page1 = pages[i];
        if (tab1 == tab || page1 == tab)
        {
            tab1.className = "selected";
            page1.style.display = "";
        }
        else
        {
            tab1.className = "unselected";
            page1.style.display = "none";
        }
    }
}

function selectPage(page)
{
    page = Cyan.$(page);
    if (!page)
        return;

    while (page.className != "page" && page != document.body)
        page = page.parentNode;

    if (page.className != "page")
        return;

    var tabs = Cyan.$$("#pages .tabs")[0].childNodes;
    var pages = Cyan.$$("#pages .page");

    for (var i = 0; i < pages.length; i++)
    {
        var page1 = pages[i];
        if (page1 == page)
        {
            tabs[i].className = "selected";
            page1.style.display = "";
        }
        else
        {
            tabs[i].className = "unselected";
            page1.style.display = "none";
        }
    }
}

function resize()
{
    if (!Cyan.navigator.isIE() || Cyan.navigator.version != 6)
    {
        var bodyHeight = document.documentElement.clientHeight;
        var topHeight = Cyan.Elements.getComponentSize(Cyan.$("top")).height;
        Cyan.$("container").style.height = (bodyHeight - topHeight - 26) + "px";
    }
}

function getBusinessDeptId()
{
    return Cyan.Arachne.form.businessDeptId;
}

function selectSingleUser(selected, callback, scopeName)
{
    System.selectMembers({
        deptId: getBusinessDeptId(), types: ["user"], selected: selected, callback: function (result)
        {
            if (result && result.length)
            {
                if (result.length > 1)
                {
                    Cyan.message("只允许选择一个用户");
                    return;
                }

                if (callback)
                    callback(result[0]);
            }
        }, scopeName: scopeName
    });
}

function getSingleUserSelector(scopeName)
{
    return function (selected, callback)
    {
        return selectSingleUser(selected, callback, scopeName);
    }
}

function selectSingleDept(selected, callback, scopeName)
{
    System.selectMembers({
        deptId: getBusinessDeptId(), types: ["dept"], selected: selected, callback: function (result)
        {
            if (result && result.length)
            {
                if (result.length > 1)
                {
                    Cyan.message("只允许选择一个部门");
                    return;
                }

                if (callback)
                    callback(result[0]);
            }
        }, scopeName: scopeName
    });
}

function getSingleDeptSelector(scopeName)
{
    return function (selected, callback)
    {
        return selectSingleDept(selected, callback, scopeName);
    }
}

function selectDept(selected, callback, scopeName)
{
    System.selectMembers({
        deptId: getBusinessDeptId(),
        types: ["dept"],
        selected: selected,
        callback: callback,
        scopeName: scopeName
    });
}

function getDeptSelector(scopeName)
{
    return function (selected, callback)
    {
        return selectDept(selected, callback, scopeName);
    }
}

function selectDeptAndGroup(selected, callback, scopeName)
{
    System.selectMembers({
        deptId: getBusinessDeptId(),
        types: ["dept", "deptgroup", "custom"],
        selected: selected,
        callback: callback,
        scopeName: scopeName
    });
}

function getDeptAndGroupSelector(scopeName)
{
    return function (selected, callback)
    {
        return selectDeptAndGroup(selected, callback, scopeName);
    }
}

function selectDeptAndUser(selected, callback, scopeName)
{
    System.selectMembers({
        deptId: getBusinessDeptId(),
        types: ["dept", "deptgroup", "user", "usergroup", "station", "custom"],
        selected: selected,
        callback: callback,
        scopeName: scopeName
    });
}

function getDeptAndUserSelector(scopeName)
{
    return function (selected, callback)
    {
        return selectDeptAndUser(selected, callback, scopeName);
    }
}

function selectUser(selected, callback, scopeName)
{
    System.selectMembers({
        deptId: getBusinessDeptId(), types: ["user"
        ], selected: selected, callback: callback, scopeName: scopeName
    });
}

function getUserSelector(scopeName)
{
    return function (selected, callback)
    {
        return selectUser(selected, callback, scopeName);
    }
}

function getFormComponent(name)
{
    return Cyan.Valmiki.getComponent(name);
}

function exit()
{
    System.closePage();
}

function checkData(action, self)
{
    return Cyan.Valmiki.checkAll(action, self);
}