Cyan.importJs("widgets/targetlist.js");
Cyan.importJs("adapters/extjs/DDView.js");
Cyan.importJs("adapters/extjs/MultiSelect.js");
Cyan.importCss("adapters/extjs/multiselect.css");
Cyan.importJs("adapters/extjs/menu.js");

Cyan.Extjs.Form = {
    selectableMenus: {}
};

Cyan.Extjs.Form.init = function ()
{
    if (!Cyan.Extjs.Form.inited)
    {
        Cyan.Extjs.Form.inited = true;

        Cyan.Extjs.initBlank();

        Cyan.Class.overwrite(Ext.DatePicker.prototype, "onRender", function (container, position)
        {
            this.inherited(container, position);

            var buttons = this.el.child("td.x-date-bottom", true);
            buttons.removeChild(buttons.childNodes[0]);

            var picker = this;

            var todayButton = Cyan.Elements.createButton(this.todayText);
            todayButton.style.cursor = "pointer";
            todayButton.onclick = function ()
            {
                picker.selectToday();
            };
            buttons.appendChild(todayButton);

            var clearButton = Cyan.Elements.createButton(Cyan.titles.clear);
            clearButton.style.cursor = "pointer";
            clearButton.onclick = function ()
            {
                picker.fireEvent("select", picker, null);
            };
            buttons.appendChild(clearButton);
        });
    }
};

Cyan.Extjs.Form.getTopWin = function ()
{
    //寻找最外层的页面
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Cyan.Extjs && win.parent.Cyan.Extjs.Form)
            win = win.parent;
    }
    catch (e)
    {
    }
    return win;
};

Cyan.Extjs.Form.isSupportInputType = function (type)
{
    return type == "text";

    //if (Cyan.Extjs.Form.supportTypes == null)
    //    Cyan.Extjs.Form.supportTypes = {};
    //
    //var supported = Cyan.Extjs.Form.supportTypes[type];
    //if (supported == null)
    //{
    //    var input = document.createElement("input");
    //    input.setAttribute("type", type);
    //    supported = Cyan.Extjs.Form.supportTypes[type] = input.type != "text";
    //}
    //
    //return supported;
};

Cyan.Extjs.Form.isDate = function (component)
{
    return (component.getAttribute("dataType") || component.getAttribute("datatype")) == "date";
};

Cyan.Extjs.Form.isDateTime = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    return datatype == "dateTime" || datatype == "datetime";
};

Cyan.Extjs.Form.isYearMonth = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    if (datatype == "YearMonth" || datatype == "yearmonth" || datatype == "yearMonth")
        return true;

    if (datatype == "date")
    {
        var format = component.getAttribute("format");
        return format && Cyan.Date.yearMonthFormats.contains(format);
    }

    return false;
};


Cyan.Extjs.Form.isMonthDay = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    if (datatype == "MonthDay" || datatype == "monthday" || datatype == "monthDay")
        return true;

    if (datatype == "date")
    {
        var format = component.getAttribute("format");
        return format && Cyan.Date.monthDayFormats.contains(format);
    }

    return false;
};

Cyan.Extjs.Form.getDateFormat = function (component)
{
    try
    {
        var format = component.getAttribute("format");
        if (format)
            return format;

        if (Cyan.Extjs.Form.isDateTime(component))
            return "yyyy-MM-dd HH:mm:ss";
        else if (Cyan.Extjs.Form.isDate(component))
            return "yyyy-MM-dd";
        else if (Cyan.Extjs.Form.isYearMonth(component))
            return "yyyy-MM";
        else if (Cyan.Extjs.Form.isMonthDay(component))
            return "MM/dd";
    }
    catch (e)
    {
        return "yyyy-MM-dd HH:mm:ss";
    }
};

Cyan.Extjs.Form.isColor = function (component)
{
    return (component.getAttribute("dataType") || component.getAttribute("datatype")) == "color";
};

Cyan.Extjs.Form.isByteSize = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    return datatype == "bytesize" || datatype == "byteSize";
};

Cyan.Extjs.Form.getByteUnit = function (component)
{
    return component.getAttribute("unit") || "B";
};

Cyan.Extjs.Form.isSecond = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    return datatype == "second" || datatype == "second";
};

Cyan.Extjs.Form.isMinute = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    return datatype == "minute" || datatype == "minute";
};

Cyan.Extjs.Form.isHour = function (component)
{
    var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
    return datatype == "hour" || datatype == "hour";
};

(function ()
{
    var queue = [];
    Cyan.Extjs.Form.getSelectable = function (component, callback)
    {
        var selectable = component.selectable || component.getAttribute("selectable");
        if (selectable)
        {
            if (Cyan.isString(selectable))
            {
                var name;
                if (selectable.endsWith(".class"))
                {
                    if (Cyan.Arachne)
                    {
                        name = component.getAttribute("selectableName");
                        if (!name)
                            name = "$selectable$" + Math.random().toString().substring(2);

                        var model = component.getAttribute("selectableModel");
                        var s = selectable;

                        var f = function ()
                        {
                            Cyan.Arachne.createComponent(s.substring(0, s.length - 6), name, null, null,
                                    model, function ()
                                    {
                                        var selectable = window[name];
                                        selectable.variableName = name;
                                        component.selectable = selectable;

                                        if (callback)
                                            callback(component, selectable);

                                        Cyan.Array.remove(queue, 0);

                                        if (queue.length > 0)
                                            queue[0]();
                                    }
                            );
                        };

                        queue.push(f);
                        if (queue.length == 1)
                            f();
                    }

                    selectable = null;
                }
                else
                {
                    name = selectable;
                    selectable = eval(selectable);
                    if (selectable)
                    {
                        selectable.variableName = name;
                        component.selectable = selectable;
                    }
                }
            }

            if (selectable && callback)
                callback(component, selectable);

            return selectable;
        }
    };
})();

Cyan.Extjs.Form.showDateMenuAt = function (component, x, y, win)
{
    var menu = Cyan.Extjs.Form.dateMenu;
    if (!menu)
    {
        menu = new Ext.menu.DateMenu();
        Cyan.Extjs.Form.dateMenu = menu;
        var detachCloseAction = function ()
        {
            if (this.closeAction)
            {
                try
                {
                    this.openWindow.Cyan.detach(Cyan.navigator.isIE() ? this.openWindow.document.body :
                            this.openWindow, "click", this.closeAction);
                }
                catch (e)
                {
                }
                this.closeAction = null;
                this.openWindow = null;
            }
        };
        menu.on("show", detachCloseAction, menu);
        menu.on("hide", detachCloseAction, menu);
        menu.on("select", function (picker, date)
        {
            try
            {
                if (this.target)
                {
                    var format = Cyan.Extjs.Form.getDateFormat(this.target);
                    if (this.target.value && Cyan.Extjs.Form.isDateTime(this.target))
                    {
                        var date2 = Cyan.Date.parse(Cyan.trim(this.target.value), format);
                        if (date2)
                        {
                            date.setHours(date2.getHours());
                            date.setMinutes(date2.getMinutes());
                            date.setSeconds(date2.getSeconds());
                        }
                    }

                    this.target.value = date ? Cyan.Date.format(date, format) : "";
                    Cyan.fireEvent(this.target, "change");

                    if (this.target.onchange)
                        this.target.onchange();
                    try
                    {
                        this.target.focus();
                    }
                    catch (e)
                    {
                    }
                    this.target = null;
                }
            }
            catch (e)
            {
            }
        }, menu);
    }
    menu.target = component;
    menu.showAt([x, y]);
    var format = Cyan.Extjs.Form.getDateFormat(component);
    var date = Cyan.Date.parse(Cyan.trim(component.value), format);
    if (date)
        menu.picker.setValue(date);

    window.setTimeout(function ()
    {
        if (menu.isVisible() && !menu.closeAction)
        {
            menu.target = component;
            menu.closeAction = win.Cyan.attach(Cyan.navigator.isIE() ?
                    win.document.body : win, "click", function (event)
            {
                if (!event.isOn(menu.getEl().dom))
                    menu.hide();
            });
            menu.openWindow = win;
        }
    }, 500);
};

Cyan.Extjs.Form.makeDateTimeComponent = function (component)
{
    if (!component)
    {
        if (this.nodeName)
            component = this;
    }
    else
    {
        component = Cyan.$(component);
    }

    var format = Cyan.Extjs.Form.getDateFormat(component);
    var date = Cyan.Date.parse(component.value, format);

    var onchange = function ()
    {
        var value = component.value;
        if (dateInput.value)
        {
            component.value = Cyan.Date.format(Cyan.Date.parse(dateInput.value + " " + hoursCombox.getValue() + ":" +
                    minutesCombox.getValue() + ":00"), format);
        }
        else
        {
            component.value = "";
        }

        Cyan.fireEvent(component, "change");

        if (component.onchange)
            component.onchange();

        if (Cyan.Validator)
            Cyan.Validator.checkField.apply(component);
    };

    var size = Cyan.Elements.getComponentSize(component);

    var dateInput = document.createElement("INPUT");
    dateInput.name = component.name + "$date";
    dateInput.style.cssText = component.style.cssText;
    dateInput.style.width = "80px";
    dateInput.style.height = size.height + "px";
    dateInput.style.cssFloat = "left";
    dateInput.style.styleFloat = "left";
    if (dateInput.style.borderBottomWidth == "0" || dateInput.style.borderBottomWidth == "0px")
        dateInput.style.borderBottomWidth = "1px";
    dateInput.readOnly = true;
    dateInput.setAttribute("format", "yyyy-MM-dd");
    if (date)
        dateInput.value = Cyan.Date.format(date, "yyyy-MM-dd");
    Cyan.attach(dateInput, "click", function ()
    {
        Cyan.Extjs.Form.showDateMenu(this);
    });
    component.parentNode.insertBefore(dateInput, component);

    var hoursSelect = document.createElement("SELECT");
    hoursSelect.name = component.name + "$hours";
    hoursSelect.style.width = "40px";
    hoursSelect.style.height = size.height + "px";
    hoursSelect.onchange = onchange;
    var hours, i;
    for (i = 0; i < 24; i++)
    {
        hours = i < 10 ? "0" + i : i.toString();
        hoursSelect.options[hoursSelect.options.length] = new Option(hours, hours);
    }
    if (date)
    {
        hours = date.getHours();
        hoursSelect.value = hours < 10 ? "0" + hours : hours.toString();
    }
    else
    {
        hours = component.getAttribute("default_hours");
        if (hours)
        {
            hours = parseInt(hours);
            hoursSelect.value = hours < 10 ? "0" + hours : hours.toString();
        }
    }
    var hoursCombox = Cyan.Extjs.Form.toCombox(hoursSelect, component.form);
    var hoursDiv = document.createElement("DIV");
    hoursDiv.style.cssFloat = "left";
    hoursDiv.style.styleFloat = "left";
    hoursDiv.style.marginLeft = "1px";
    hoursDiv.style.position = component.style.position;
    if (component.style.left)
    {
        hoursDiv.style.left = (Cyan.toInt(component.style.left) + 85) + "px";
    }
    if (component.style.top)
    {
        hoursDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(hoursDiv, component);
    hoursCombox.render(hoursDiv);

    var splitDiv = document.createElement("DIV");
    splitDiv.style.cssFloat = "left";
    splitDiv.style.styleFloat = "left";
    splitDiv.style.width = "10px";
    splitDiv.style.lineHeight = size.height + "px";
    splitDiv.style.textAlign = "center";
    splitDiv.innerHTML = ":";
    splitDiv.style.position = component.style.position;
    if (component.style.left)
    {
        splitDiv.style.left = (Cyan.toInt(component.style.left) + 125) + "px";
    }
    if (component.style.top)
    {
        splitDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(splitDiv, component);

    var minutesSelect = document.createElement("SELECT");
    minutesSelect.name = component.name + "$minutes";
    minutesSelect.style.width = "40px";
    minutesSelect.style.height = size.height + "px";
    minutesSelect.onchange = onchange;
    var minutes;
    for (i = 0; i < 60; i++)
    {
        minutes = i < 10 ? "0" + i : i.toString();
        minutesSelect.options[minutesSelect.options.length] = new Option(minutes, minutes);
    }
    if (date)
    {
        minutes = date.getMinutes();
        minutesSelect.value = minutes < 10 ? "0" + minutes : minutes.toString();
    }
    else
    {
        minutes = component.getAttribute("default_minutes");
        if (minutes)
        {
            minutes = parseInt(minutes);
            minutesSelect.value = minutes < 10 ? "0" + minutes : minutes.toString();
        }
    }
    var minutesCombox = Cyan.Extjs.Form.toCombox(minutesSelect, component.form);
    var minutesDiv = document.createElement("DIV");
    minutesDiv.style.cssFloat = "left";
    minutesDiv.style.styleFloat = "left";
    minutesDiv.style.marginLeft = "1px";
    minutesDiv.style.position = component.style.position;
    if (component.style.left)
    {
        minutesDiv.style.left = (Cyan.toInt(component.style.left) + 135) + "px";
    }
    if (component.style.top)
    {
        minutesDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(minutesDiv, component);
    minutesCombox.render(minutesDiv);

    Cyan.attach(dateInput, "input", onchange);
    dateInput.onchange = onchange;

    component.style.display = "none";
    component.showElement = dateInput;
    dateInput.active = function ()
    {
        component.active();
    };
    dateInput.showErrorTo = component;

    component.onenable = function ()
    {
        hoursCombox.enable();
        minutesCombox.enable();
        dateInput.disabled = false;
    };
    component.ondisable = function ()
    {
        hoursCombox.disable();
        minutesCombox.disable();
        dateInput.disabled = true;
    };

    component.setValue = function (value)
    {
        if (value)
        {
            var date, text;
            if (Cyan.isDate(value))
            {
                date = value;
                text = Cyan.Date.format(value, format);
            }
            else
            {
                text = value;
                date = Cyan.Date.parse(value, format);
            }

            this.value = text;
            dateInput.value = Cyan.Date.format(date, "yyyy-MM-dd");
            hoursCombox.setValue(date.getHours());
            minutesCombox.setValue(date.getMinutes());
        }
        else
        {
            this.value = "";
            dateInput.value = "";
            hoursCombox.setValue("");
            minutesCombox.setValue("");
        }
    };
};

Cyan.Extjs.Form.showDateMenu = function (component)
{
    if (!component)
    {
        if (this.nodeName)
            component = this;
    }
    else
    {
        component = Cyan.$(component);
    }
    var win = Cyan.Extjs.Form.getTopWin();
    if (win.Cyan.Extjs.Form.dateMenu && win.Cyan.Extjs.Form.dateMenu.isVisible() &&
            win.Cyan.Extjs.Form.dateMenu.target == component)
    {
        win.Cyan.Extjs.Form.dateMenu.hide();
        return;
    }
    var position = Cyan.Elements.getPositionInFrame(component, win);
    var size = Cyan.Elements.getComponentSize(component);
    win.Cyan.Extjs.Form.showDateMenuAt(component, position.x, position.y + size.height + 2, window);

    if (component.readOnly)
        makeClearable(component, null);
};

Cyan.Extjs.Form.makeYearMonthComponent = function (component)
{
    if (!component)
    {
        if (this.nodeName)
            component = this;
    }
    else
    {
        component = Cyan.$(component);
    }

    var format = Cyan.Extjs.Form.getDateFormat(component);
    var date = Cyan.Date.parse(component.value, format);
    var size = Cyan.Elements.getComponentSize(component);
    var i;

    var onchange = function ()
    {
        var date = new Date();
        date.setFullYear(yearsCombox.getValue());
        date.setMonth(monthsCombox.getValue());
        date.setDate(1);
        date.setHours(0);
        date.setHours(0);
        date.setSeconds(0);
        date.setMilliseconds(0);

        component.value = Cyan.Date.format(date, format);

        if (component.onchange)
            component.onchange();
    };

    var yearsSelect = document.createElement("SELECT");
    yearsSelect.name = component.name + "$years";
    yearsSelect.style.width = "60px";
    yearsSelect.style.height = size.height + "px";
    yearsSelect.onchange = onchange;

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
    var year;
    if (endYear > startYear)
    {
        for (i = startYear; i <= endYear; i++)
        {
            year = i.toString();
            yearsSelect.options[yearsSelect.options.length] = new Option(year, year);
        }
    }
    else
    {
        for (i = startYear; i >= endYear; i--)
        {
            year = i.toString();
            yearsSelect.options[yearsSelect.options.length] = new Option(year, year);
        }
    }
    if (date)
    {
        yearsSelect.value = date.getFullYear().toString();
    }
    var yearsCombox = Cyan.Extjs.Form.toCombox(yearsSelect, component.form);
    var yearsDiv = document.createElement("DIV");
    yearsDiv.style.cssFloat = "left";
    yearsDiv.style.styleFloat = "left";
    yearsDiv.style.marginLeft = "1px";
    yearsDiv.style.width = "60px";
    yearsDiv.style.position = component.style.position;
    if (component.style.left)
    {
        yearsDiv.style.left = Cyan.toInt(component.style.left) + "px";
    }
    if (component.style.top)
    {
        yearsDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(yearsDiv, component);
    yearsCombox.render(yearsDiv);

    var monthsSelect = document.createElement("SELECT");
    monthsSelect.name = component.name + "$months";
    monthsSelect.style.width = "40px";
    monthsSelect.style.height = size.height + "px";
    monthsSelect.onchange = onchange;
    for (i = 0; i < 12; i++)
    {
        monthsSelect.options[monthsSelect.options.length] = new Option((i + 1).toString(), i.toString());
    }
    if (date)
    {
        monthsSelect.value = date.getMonth().toString();
    }
    var monthsCombox = Cyan.Extjs.Form.toCombox(monthsSelect, component.form);
    var monthsDiv = document.createElement("DIV");
    monthsDiv.style.cssFloat = "left";
    monthsDiv.style.styleFloat = "left";
    monthsDiv.style.marginLeft = "1px";
    monthsDiv.style.width = "40px";
    monthsDiv.style.position = component.style.position;
    if (component.style.left)
    {
        monthsDiv.style.left = (Cyan.toInt(component.style.left) + 125) + "px";
    }
    if (component.style.top)
    {
        monthsDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(monthsDiv, component);
    monthsCombox.render(monthsDiv);

    component.style.display = "none";

    component.onenable = function ()
    {
        yearsCombox.enable();
        monthsCombox.enable();
    };
    component.ondisable = function ()
    {
        yearsCombox.disable();
        monthsCombox.disable();
    };

    component.setValue = function (value)
    {
        if (Cyan.isDate(value))
        {
            yearsCombox.setValue(value.getFullYear());
            monthsCombox.setValue(value.getMonth());
        }
        else if (value)
        {
            this.value = value;
            var date = Cyan.Date.parse(value, format);
            yearsCombox.setValue(date.getFullYear());
            monthsCombox.setValue(date.getMonth());
        }
        else
        {
            this.value = "";
            yearsCombox.setValue("");
            monthsCombox.setValue("");
        }
    };
};

Cyan.Extjs.Form.makeMonthDayComponent = function (component)
{
    if (!component)
    {
        if (this.nodeName)
            component = this;
    }
    else
    {
        component = Cyan.$(component);
    }

    var format = Cyan.Extjs.Form.getDateFormat(component);
    var date = Cyan.Date.parse(component.value, format);
    var size = Cyan.Elements.getComponentSize(component);
    var i;

    var onchange = function ()
    {
        var date = new Date();
        date.setFullYear(0);
        date.setMonth(monthsCombox.getValue());
        date.setDate(daysCombox.getValue());
        date.setHours(0);
        date.setHours(0);
        date.setSeconds(0);
        date.setMilliseconds(0);

        component.value = Cyan.Date.format(date, format);

        if (component.onchange)
            component.onchange();
    };

    var initDays = function ()
    {
        Cyan.Date.initDays(component.name + "$days", component.name + "$months");
    };

    var monthsSelect = document.createElement("SELECT");
    monthsSelect.name = component.name + "$months";
    monthsSelect.style.width = "40px";
    monthsSelect.style.height = size.height + "px";
    monthsSelect.onchange = function ()
    {
        initDays();
        onchange();
    };
    for (i = 0; i < 12; i++)
    {
        monthsSelect.options[monthsSelect.options.length] = new Option((i + 1).toString(), i.toString());
    }
    if (date)
    {
        monthsSelect.value = date.getMonth().toString();
    }
    var monthsCombox = Cyan.Extjs.Form.toCombox(monthsSelect, component.form);
    var monthsDiv = document.createElement("DIV");
    monthsDiv.style.cssFloat = "left";
    monthsDiv.style.styleFloat = "left";
    monthsDiv.style.marginLeft = "1px";
    monthsDiv.style.width = "40px";
    monthsDiv.style.position = component.style.position;
    if (component.style.left)
    {
        monthsDiv.style.left = Cyan.toInt(component.style.left) + "px";
    }
    if (component.style.top)
    {
        monthsDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(monthsDiv, component);
    monthsCombox.render(monthsDiv);

    var daysSelect = document.createElement("SELECT");
    daysSelect.name = component.name + "$days";
    daysSelect.style.width = "40px";
    daysSelect.style.height = size.height + "px";
    daysSelect.onchange = onchange;

    var daysCombox = Cyan.Extjs.Form.toCombox(daysSelect, component.form);
    var daysDiv = document.createElement("DIV");
    daysDiv.style.cssFloat = "left";
    daysDiv.style.styleFloat = "left";
    daysDiv.style.marginLeft = "1px";
    daysDiv.style.width = "40px";
    daysDiv.style.position = component.style.position;
    if (component.style.left)
    {
        daysDiv.style.left = (Cyan.toInt(component.style.left) + 105) + "px";
    }
    if (component.style.top)
    {
        daysDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(daysDiv, component);
    daysCombox.render(daysDiv);
    Cyan.$(component.name + "$days").value = date ? date.getDate() : "1";

    initDays();

    component.style.display = "none";

    component.onenable = function ()
    {
        daysCombox.enable();
        monthsCombox.enable();
    };
    component.ondisable = function ()
    {
        daysCombox.disable();
        monthsCombox.disable();
    };

    component.setValue = function (value)
    {
        if (Cyan.isDate(value))
        {
            monthsCombox.setValue(value.getMonth());
            daysCombox.setValue(value.getDate());
        }
        else if (!value)
        {
            this.value = value;
            var date = Cyan.Date.parse(value, format);
            monthsCombox.setValue(date.getMonth());
            daysCombox.setValue(date.getDate());
        }
        else
        {
            this.value = "";
            monthsCombox.setValue("");
            daysCombox.setValue("");
        }
    };
};

Cyan.Extjs.Form.makeYearMonthDayComponent = function (component)
{
    if (!component)
    {
        if (this.nodeName)
            component = this;
    }
    else
    {
        component = Cyan.$(component);
    }

    var format = Cyan.Extjs.Form.getDateFormat(component);
    var date = Cyan.Date.parse(component.value, format);
    var size = Cyan.Elements.getComponentSize(component);
    var i;

    var onchange = function ()
    {
        var date = new Date();
        date.setFullYear(yearsCombox.getValue());
        date.setMonth(monthsCombox.getValue());
        date.setDate(daysCombox.getValue());
        date.setHours(0);
        date.setHours(0);
        date.setSeconds(0);
        date.setMilliseconds(0);

        component.value = Cyan.Date.format(date, format);

        if (component.onchange)
            component.onchange();
    };

    var initDays = function ()
    {
        Cyan.Date.initDays(component.name + "$days", component.name + "$months", component.name + "$years");
    };

    var yearsSelect = document.createElement("SELECT");
    yearsSelect.name = component.name + "$years";
    yearsSelect.style.width = "60px";
    yearsSelect.style.height = size.height + "px";
    yearsSelect.onchange = function ()
    {
        initDays();
        onchange();
    };

    var startYear = parseInt(component.getAttribute("startYear"));
    var endYear = parseInt(component.getAttribute("endYear"));

    if (!endYear)
        endYear = new Date().getFullYear();

    if (!startYear)
        startYear = -50;

    if (startYear < 0)
        startYear = endYear + startYear;

    if (component.getAttribute("desc") != "false")
    {
        var temp = startYear;
        startYear = endYear;
        endYear = temp;
    }
    var year;
    if (endYear > startYear)
    {
        for (i = startYear; i <= endYear; i++)
        {
            year = i.toString();
            yearsSelect.options[yearsSelect.options.length] = new Option(year, year);
        }
    }
    else
    {
        for (i = startYear; i >= endYear; i--)
        {
            year = i.toString();
            yearsSelect.options[yearsSelect.options.length] = new Option(year, year);
        }
    }
    if (date)
    {
        yearsSelect.value = date.getFullYear().toString();
    }
    var yearsCombox = Cyan.Extjs.Form.toCombox(yearsSelect, component.form);
    var yearsDiv = document.createElement("DIV");
    yearsDiv.style.cssFloat = "left";
    yearsDiv.style.styleFloat = "left";
    yearsDiv.style.marginLeft = "1px";
    yearsDiv.style.width = "60px";
    yearsDiv.style.position = component.style.position;
    if (component.style.left)
    {
        yearsDiv.style.left = Cyan.toInt(component.style.left) + "px";
    }
    if (component.style.top)
    {
        yearsDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(yearsDiv, component);
    yearsCombox.render(yearsDiv);

    var monthsSelect = document.createElement("SELECT");
    monthsSelect.name = component.name + "$months";
    monthsSelect.style.width = "40px";
    monthsSelect.style.height = size.height + "px";
    monthsSelect.onchange = function ()
    {
        initDays();
        onchange();
    };
    for (i = 0; i < 12; i++)
    {
        monthsSelect.options[monthsSelect.options.length] = new Option((i + 1).toString(), i.toString());
    }
    if (date)
    {
        monthsSelect.value = date.getMonth().toString();
    }
    var monthsCombox = Cyan.Extjs.Form.toCombox(monthsSelect, component.form);
    var monthsDiv = document.createElement("DIV");
    monthsDiv.style.cssFloat = "left";
    monthsDiv.style.styleFloat = "left";
    monthsDiv.style.marginLeft = "1px";
    monthsDiv.style.width = "40px";
    monthsDiv.style.position = component.style.position;
    if (component.style.left)
    {
        monthsDiv.style.left = (Cyan.toInt(component.style.left) + 125) + "px";
    }
    if (component.style.top)
    {
        monthsDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(monthsDiv, component);
    monthsCombox.render(monthsDiv);

    var daysSelect = document.createElement("SELECT");
    daysSelect.name = component.name + "$days";
    daysSelect.style.width = "40px";
    daysSelect.style.height = size.height + "px";
    daysSelect.onchange = onchange;

    var daysCombox = Cyan.Extjs.Form.toCombox(daysSelect, component.form);
    var daysDiv = document.createElement("DIV");
    daysDiv.style.cssFloat = "left";
    daysDiv.style.styleFloat = "left";
    daysDiv.style.marginLeft = "1px";
    daysDiv.style.width = "40px";
    daysDiv.style.position = component.style.position;
    if (component.style.left)
    {
        daysDiv.style.left = (Cyan.toInt(component.style.left) + 175) + "px";
    }
    if (component.style.top)
    {
        daysDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
    }
    component.parentNode.insertBefore(daysDiv, component);
    daysCombox.render(daysDiv);
    Cyan.$(component.name + "$days").value = date ? date.getDate() : "1";

    initDays();

    component.style.display = "none";

    component.onenable = function ()
    {
        yearsCombox.enable();
        monthsCombox.enable();
        daysCombox.enable();
    };
    component.ondisable = function ()
    {
        yearsCombox.disable();
        monthsCombox.disable();
        daysCombox.disable();
    };

    component.setValue = function (value)
    {
        this.value = value;
        var date = Cyan.Date.parse(value, format);
        yearsCombox.setValue(date.getFullYear());
        monthsCombox.setValue(date.getMonth());
        daysCombox.setValue(date.getDate());
    };
};

Cyan.Extjs.Form.showColorMenuAt = function (component, x, y, win)
{
    var menu = Cyan.Extjs.Form.colorMenu;
    if (!menu)
    {
        menu = new Ext.menu.ColorMenu();
        Cyan.Extjs.Form.colorMenu = menu;
        var detachCloseAction = function ()
        {
            if (this.closeAction)
            {
                this.openWindow.Cyan.detach(Cyan.navigator.isIE() ? this.openWindow.document.body :
                        this.openWindow, "click", this.closeAction);
                this.closeAction = null;
                this.openWindow = null;
            }
        };
        menu.on("show", detachCloseAction, menu);
        menu.on("hide", detachCloseAction, menu);
        menu.on("select", function (palette, color)
        {
            if (this.target)
            {
                this.target.value = "#" + color;
                try
                {
                    this.target.focus();
                }
                catch (e)
                {
                }
                this.target = null;
            }
        }, menu);
    }
    menu.target = component;
    menu.showAt([x, y]);
    window.setTimeout(function ()
    {
        if (menu.isVisible() && !menu.closeAction)
        {
            menu.target = component;
            menu.closeAction = win.Cyan.attach(Cyan.navigator.isIE() ? win.document.body : win, "click", function ()
            {
                menu.hide();
            });
            menu.openWindow = win;
        }
    }, 500);
};

Cyan.Extjs.Form.showColorMenu = function (component)
{
    if (!component)
    {
        if (this.nodeName)
            component = this;
    }
    else
    {
        component = Cyan.$(component);
    }
    var win = Cyan.Extjs.Form.getTopWin();
    if (win.Cyan.Extjs.Form.colorMenu && win.Cyan.Extjs.Form.colorMenu.isVisible() &&
            win.Cyan.Extjs.Form.colorMenu.target == component)
    {
        win.Cyan.Extjs.Form.colorMenu.hide();
        return;
    }
    var position = Cyan.Elements.getPositionInFrame(component, win);
    var size = Cyan.Elements.getComponentSize(component);
    win.Cyan.Extjs.Form.showColorMenuAt(component, position.x, position.y + size.height + 2, window);
};

Cyan.Extjs.Form.toCombox = function (select, form)
{
    var n = select.options.length;
    var data = new Array(n);
    var i;
    for (i = 0; i < n; i++)
    {
        var option = select.options[i];
        data[i] = {value: option.value, text: option.text || "--------"};
    }
    var store = new Ext.data.JsonStore({
        data: data,
        autoLoad: true,
        fields: ["value", "text"]
    });

    var width;
    if (select.style.width)
    {
        width = Cyan.toInt(select.style.width);
    }
    else if (select.clientWidth)
    {
        width = select.clientWidth;

        if (document.compatMode == "BackCompat")
        {
            if (Cyan.navigator.isChrome() || Cyan.navigator.isSafari())
                width += 2;
            else if (Cyan.navigator.isOpera() || Cyan.navigator.isFF())
                width += 4;
            else if (Cyan.navigator.isIE())
            {
                if (select.multiple)
                    width -= 10;
                else
                    width += 4;
            }
        }
        else
        {
            if (Cyan.navigator.isIE())
            {
                if (select.multiple)
                    width -= 10;
                else if (Cyan.navigator.version == 7)
                    width += 27;
                else if (Cyan.navigator.version < 10)
                    width += 8;
                else
                    width += 5;
            }
            else if (Cyan.navigator.isFF())
            {
                if (select.multiple || width > 20)
                    width += 8;
                else
                    width += 20;
            }
            else
                width += 6;
        }
    }
    else
    {
        width = Cyan.Elements.getComponentSize(select).width;
    }

    var listeners = {
        render: function ()
        {
            Cyan.Extjs.Form.initCombox(combox, select);
        }
    };

    var combox, height;
    if (select.multiple)
    {
        height = Cyan.Elements.getComponentSize(select).height;
        if (!height)
            height = 160;

        if (Cyan.navigator.name == "IE7")
        {
            height += 12;
        }

        listeners.dblclick = function (vw, index, node, e)
        {
            this.setValue(Cyan.valueOf(this.originalData[index]));
            if (select.ondblclick)
                select.ondblclick();
        };

        if (select.onclick)
        {
            listeners.click = function ()
            {
                select.onclick();
            };
        }

        combox =
                new Ext.ux.Multiselect({
                    name: select.name, editable: Cyan.Extjs.Form.isEditable(select),
                    triggerAction: "all", mode: 'local', store: store, valueField: "value", displayField: "text",
                    width: width, height: height, value: select.value, listeners: listeners
                });
    }
    else
    {
        if (!width)
        {
            width = 44;
            for (i = 0; i < data.length; i++)
            {
                var text = data[i].text;
                var length = 0;
                var b;
                for (var j = 0; j < text.length; j++)
                {
                    if (text.charAt(i) != '-')
                        b = true;
                    length += text.charCodeAt(i) > 127 ? 2 : 1;
                }

                if (b)
                {
                    var width1 = length * 6 + 28;
                    if (width1 > width)
                        width = width1;
                }
            }
        }

        var hiddenId = select.id;
        if (!hiddenId)
            hiddenId = select.name + "_" + Math.random().toString().substring(2);

        if (select.onchange)
        {
            var onchange = select.onchange;
            listeners.select = function ()
            {
                onchange.apply(Cyan.$(hiddenId));
            };
        }

        height = 22;

        var value = select.getAttribute("value");
        if (value == null)
            value = select.value;

        if (value == "" && data.length > 0)
        {
            for (var k = 0; k < data.length; k++)
            {
                if (data[k].value == "")
                {
                    value = "";
                    break;
                }
                else if (k == 0)
                {
                    value = data[k].value;
                }
            }
        }

        combox = new Ext.form.ComboBox({
            hiddenName: select.name,
            hiddenId: hiddenId,
            editable: Cyan.Extjs.Form.isEditable(select),
            triggerAction: "all",
            mode: 'local',
            store: store,
            valueField: "value",
            displayField: "text",
            width: width,
            height: height,
            value: value,
            listeners: listeners,
            disabled: select.disabled
        });
    }
    combox.originalData = data;

    if (form)
    {
        if (select.getAttribute("keep") == "true")
        {
            Cyan.attach(form, "reset", function ()
            {
                var initialValue = Cyan.$(hiddenId).value;
                setTimeout(function ()
                {
                    Cyan.$(hiddenId).value = initialValue;
                    combox.setValue(initialValue);
                }, 1);
            });
        }
        else
        {
            var initialValue = select.value;
            Cyan.attach(form, "reset", function ()
            {
                setTimeout(function ()
                {
                    combox.setValue(initialValue);
                }, 1);
            });
        }
    }

    return combox;
};

Cyan.Extjs.Form.isEditable = function (select)
{
    return select.getAttribute("editable") == "true";
};

Cyan.Extjs.Form.initCombox = function (combox, select)
{
    var hidden = Cyan.searchFirst(document.getElementsByName(select.name), function ()
    {
        return this.nodeName == "INPUT" && this.type == "hidden" && (!combox.hiddenId || (this.id == combox.hiddenId));
    });
    hidden.onvaluechange = function ()
    {
        combox.setValue(this.value);
    };
    hidden.setValue = function (value)
    {
        this.value = value;
        combox.setValue(value == null ? "" : value.toString());
    };
    hidden.ondisable = function ()
    {
        combox.disable();
    };
    hidden.onenable = function ()
    {
        combox.enable();
    };
    hidden.onBeforeRefresh = function ()
    {
        this.oldValue = combox.getValue();
        combox.setValue("");
        combox.store.removeAll();
    };
    hidden.onAfterRefresh = function (selectable)
    {
        if (!selectable)
            selectable = [];
        var oldValue = this.oldValue;

        this.oldValue = null;
        var newValue = null;

        var n = selectable.length, data, start = 0;
        if (combox.originalData.length && combox.originalData[0].value == "")
        {
            start++;
            data = new Array(n + 1);
            data[0] = combox.originalData[0];
        }
        else
        {
            data = new Array(n);
        }

        for (var i = 0; i < n; i++)
        {
            var item = selectable[i];
            var value = Cyan.valueOf(item);
            if (start && value == "")
            {
                start--;
                data.length = data.length - 1;
            }
            else
            {
                data[i + start] = {value: value, text: Cyan.toString(item)};

                if (oldValue instanceof Array)
                {
                    if (Cyan.Array.contains(oldValue, value))
                    {
                        if (newValue == null)
                            newValue = [];
                        newValue.push(value);
                    }
                }
                else if ((i == 0 && start == 0) || oldValue == value)
                {
                    newValue = value;
                }
            }
        }

        combox.originalData = data;
        combox.store.loadData(data);

        if (newValue)
            combox.setValue(newValue);
    };
    hidden.allItems = function ()
    {
        return Cyan.clone(combox.originalData);
    };
    hidden.allValues = function ()
    {
        return Cyan.get(combox.originalData, "value");
    };
    hidden.addItem = function (value, text)
    {
        if (!Cyan.searchFirst(combox.originalData, function ()
                {
                    return this.value == value;
                }))
        {
            var oldValue = combox.getValue();
            combox.originalData.push({value: value, text: text});
            combox.store.loadData(combox.originalData);
            combox.setValue(oldValue);
        }
    };
    hidden.removeItem = function (value)
    {
        var oldValue = combox.getValue();
        Cyan.Array.removeCase(combox.originalData, function ()
        {
            return this.value == value;
        });
        combox.store.loadData(combox.originalData);
        combox.setValue(oldValue);
    };
    hidden.clearAll = function ()
    {
        combox.originalData.length = 0;
        combox.store.loadData(combox.originalData);
    };

    if (select.multiple)
    {
        hidden.up = function ()
        {
            var value = combox.getValue();
            var data = combox.originalData;
            var indexs = combox.view.getSelectedIndexes();
            var cannotMove = {};
            for (var i = 0; i < indexs.length; i++)
            {
                var index = indexs[i];
                if (index > 0 && !cannotMove[index - 1])
                {
                    var temp = data[index - 1];
                    data[index - 1] = data[index];
                    data[index] = temp;
                }
                else
                {
                    cannotMove[index] = true;
                }
            }
            combox.store.loadData(data);
            combox.setValue(value);
        };
        hidden.down = function ()
        {
            var value = combox.getValue();
            var data = combox.originalData;
            var indexs = combox.view.getSelectedIndexes();
            var cannotMove = {};
            for (var i = indexs.length - 1; i >= 0; i--)
            {
                var index = indexs[i];
                if (index < data.length - 1 && !cannotMove[index + 1])
                {
                    var temp = data[index + 1];
                    data[index + 1] = data[index];
                    data[index] = temp;
                }
                else
                {
                    cannotMove[index] = true;
                }
            }
            combox.store.loadData(data);
            combox.setValue(value);
        };
        hidden.selectedItems = function ()
        {
            var n, i;
            if (combox.view)
            {
                var indexes = combox.view.getSelectedIndexes();
                n = indexes.length;
                var result = new Array(n);
                for (i = 0; i < n; i++)
                    result[i] = combox.originalData[indexes[i]];
                return result;
            }
            else
            {
                var values = combox.getValue().split(",");
                n = combox.originalData.length;
                var items = [];
                for (i = 0; i < n; i++)
                {
                    var item = combox.originalData[i];
                    if (Cyan.Array.contains(values, item.value))
                        items.push(item);
                }

                return items;
            }
        };
        hidden.selectedIndexes = function ()
        {
            if (combox.view)
            {
                return combox.view.getSelectedIndexes();
            }
            else
            {
                var values = combox.getValue().split(",");
                var n = combox.originalData.length;
                var indexes = [];
                for (var i = 0; i < n; i++)
                {
                    var item = combox.originalData[i];
                    if (Cyan.Array.contains(values, item.value))
                        indexes.push(i);
                }

                return indexes;
            }
        };
        hidden.selectedValues = function ()
        {
            return combox.getValue().split(",");
        };
        hidden.selectAll = function (selected)
        {
            if (selected)
            {
                combox.setValue(this.allValues());
            }
            else if (typeof selected == "undefined")
            {
                var value = combox.getValue();
                combox.setValue(value && value.length ? [] : this.allValues());
            }
            else
            {
                combox.setValue([]);
            }
        };
        hidden.clearSelected = function (value)
        {
            Cyan.Array.remove(combox.originalData, combox.view.getSelectedIndexes());
            combox.store.loadData(combox.originalData);
        };
    }

    hidden.selectedItem = function ()
    {
        if (combox.view)
        {
            var indexes = combox.view.getSelectedIndexes();
            if (indexes && indexes.length)
                return combox.originalData[indexes[0]];
            else
                return null;
        }
        else
        {
            var value = combox.getValue().split(",");
            var n = combox.originalData.length;
            for (var i = 0; i < n; i++)
            {
                var item = combox.originalData[i];
                if (item.value == value)
                    return item;
            }

            return null;
        }
    };
    hidden.selectedIndex = function ()
    {
        if (combox.view)
        {
            var indexes = combox.view.getSelectedIndexes();
            return indexes && indexes.length ? indexes[0] : -1;
        }
        else
        {
            var value = combox.getValue().split(",");
            var n = combox.originalData.length;
            for (var i = 0; i < n; i++)
            {
                var item = combox.originalData[i];
                if (item.value == value)
                    return i;
            }

            return null;
        }
    };
};

Cyan.Extjs.Form.makeSelectable = function (component, selectable)
{
    if (!selectable)
    {
        Cyan.Extjs.Form.getSelectable(component, Cyan.Extjs.Form.makeSelectable);
        return;
    }

    var readOnly = component.readOnly;
    var disabled = component.disabled;
    var keep = component.getAttribute("keep");

    var text, onchange;
    var editable = Cyan.Extjs.Form.isEditable(component);
    if (editable)
    {
        text = component;
    }
    else
    {
        text = document.createElement("INPUT");
        text.type = "text";
        text.readOnly = true;
        text.style.cssText = component.style.cssText;
        text.className = component.className + " cyan-form-field-selectable";
        text.id = "fieldId_text_" + Math.random().toString().substring(2);
        var s = Cyan.Extjs.Form.getSelectableText(component);
        var textValue;
        if (s != null)
            text.value = s;
        else
            textValue = component.value;

        if (disabled)
            text.disabled = true;

        onchange = component.onchange;
        component.parentNode.insertBefore(text, component);

        var multiple = component.getAttribute("multiple");
        multiple = multiple != null && multiple != "false";
        if (multiple)
        {
            if (textValue)
                text.value = textValue;

            var div = document.createElement("DIV");
            div.style.display = "none";
            var s1 = "<select name='" + component.name + "' size='3' multiple='true' ";
            if (keep == "true")
                s1 += " keep='true'";
            s1 += "></select>";
            div.innerHTML = s1;

            component.parentNode.insertBefore(div, component);

            var select = component.previousSibling.childNodes[0];

            var require = component.require || component.getAttribute("require");
            if (require != null && require != "false")
            {
                select.setAttribute("require", "true");
                select.setAttribute("showErrorTo", text.id);
            }

            component.parentNode.removeChild(component);

            if (component.value)
            {
                var values = component.value.split(",");
                for (var i = 0; i < values.length; i++)
                {
                    var option = new Option("", values[i]);
                    select.options[i] = option;
                    option.selected = true;
                }
            }

            component = select;
        }
        else
        {
            component.style.display = "none";

            if (textValue && !text.value && selectable.getText)
            {
                selectable.getText(textValue, function (ret)
                {
                    if (ret)
                        text.value = ret;
                    else
                        text.value = textValue;
                });
            }
        }

        component.showElement = text.id;
        text.active = function ()
        {
            component.active();
        };
        component.multiple = multiple;
        component.onenable = function ()
        {
            text.disabled = false;
        };
        component.ondisable = function ()
        {
            text.disabled = true;
        };
    }

    var menu;

    var onselect = function ()
    {
        var position = Cyan.Elements.getPosition(text);
        var size = Cyan.Elements.getComponentSize(text);
        var y = position.y + size.height + 4;

        if (!menu)
        {
            menu = Cyan.Extjs.Form.selectableMenus[selectable.variableName];
            if (menu == null)
            {
                var component1;
                if (selectable.getExtComponent)
                    component1 = selectable.getExtComponent();
                if (!component1)
                    component1 = selectable;
                var maxHeight = Cyan.getBodyHeight() - y - 40;
                if (component1.height)
                {
                    if (component1.height > maxHeight)
                        component1.height = maxHeight;
                }
                else
                {
                    if (maxHeight > 280)
                        maxHeight = 280;
                    component1.height = maxHeight;
                }

                menu = Cyan.Extjs.Form.selectableMenus[selectable.variableName] =
                        new Cyan.Extjs.WidgetMenu(selectable, {
                            onselect: function (widget)
                            {
                                var items = widget.selectedItems();
                                var value;
                                if (!menu.valueComponent.multiple)
                                {
                                    value = Cyan.get(items, "value").join(",");
                                }

                                var text = Cyan.get(items, "text").join(",");
                                menu.textComponent.value = text;
                                menu.valueComponent.setAttribute("text", text);

                                if (!editable)
                                {
                                    if (menu.valueComponent.multiple)
                                    {
                                        var select = menu.valueComponent;
                                        select.options.length = 0;
                                        if (items && items.length)
                                        {
                                            for (var i = 0; i < items.length; i++)
                                            {
                                                var option = new Option("", items[i].value);
                                                select.options[i] = option;
                                                option.selected = true;
                                            }
                                        }
                                        if (onchange)
                                            onchange.apply(menu.valueComponent);
                                        if (Cyan.Validator)
                                            Cyan.Validator.checkField.apply(menu.valueComponent);
                                    }
                                    else
                                    {
                                        menu.valueComponent.value = value;
                                        if (onchange)
                                            onchange.apply(menu.valueComponent);
                                        if (Cyan.Validator)
                                            Cyan.Validator.checkField.apply(menu.valueComponent);
                                    }
                                }

                                try
                                {
                                    text.focus();
                                }
                                catch (e)
                                {
                                }
                            }
                        });
            }

            var node = text;
            var scroll = function ()
            {
                var position = Cyan.Elements.getPosition(text);
                var size = Cyan.Elements.getComponentSize(text);
                var y = position.y + size.height + 4;
                menu.showAt([position.x, y]);
            };
            var scroll0 = function (event)
            {
                if (menu && menu.textComponent == text && menu.isVisible())
                {
                    setTimeout(scroll, 1);
                }
            };
            while ((node = node.parentNode).nodeName != "BODY")
            {
                Cyan.attach(node, "scroll", scroll0);
            }
            Cyan.attach(document.body, "scroll", scroll0);
            Cyan.attach(window, "scroll", scroll0);
        }

        menu.valueComponent = component;
        menu.textComponent = text;

        var values;
        if (selectable.setSelectedValues)
        {
            if (component.multiple)
            {
                values = Cyan.map(component.options, "value");
            }
            else
            {
                values = component.value.split(",");
            }
            selectable.setSelectedValues(values);
        }
        else if (selectable.setSelectedItems)
        {
            if (component.multiple)
            {
                values = Cyan.map(component.options, "value");
            }
            else
            {
                values = component.value.split(",");
            }
            var texts = text.value.split(",");

            selectable.setSelectedItems(Cyan.map(values, function (index)
            {
                return {value: this, text: texts[index]};
            }));
        }

        menu.showAt([position.x, y]);
    };

    component.refresh = function (callback)
    {
        if (selectable.reload)
            selectable.reload(callback);
    };

    if (text != null)
    {
        component.getText = function ()
        {
            return text.value;
        };

        component.setText = function (value)
        {
            text.value = value;
        };
    }

    if (!readOnly)
    {
        if (editable)
        {
            makeSelectIcon(text, onselect);
        }
        else
        {
            Cyan.attach(text, "click", onselect);
            if (!component.multiple)
                makeClearable(text, component);
        }
    }
    if (keep == "true")
    {
        Cyan.attach(component.form, "reset", function ()
        {
            var textValue = text.value;
            setTimeout(function ()
            {
                text.value = textValue;
            }, 1);
        });
    }
    else
    {
        var textValue0 = text.value;
        Cyan.attach(component.form, "reset", function ()
        {
            setTimeout(function ()
            {
                text.value = textValue0;
            }, 1);
        });
    }
};

Cyan.Extjs.Form.getSelectableText = function (component)
{
    if (component.text != null)
        return component.text;
    return component.getAttribute("text");
};

Cyan.Extjs.Form.ByteSizeUnits = ["B", "K", "M", "G", "T"];

Cyan.Extjs.Form.createByteSizeInput = function (input)
{
    var container = document.createElement("SPAN");

    var hidden = document.createElement("INPUT");
    hidden.type = "hidden";
    hidden.name = input.name;
    hidden.id = input.id;
    hidden.value = input.value;
    hidden.setAttribute("require", input.getAttribute("require"));
    container.appendChild(hidden);

    var unit = Cyan.Extjs.Form.getByteUnit(input);
    var size = Cyan.Elements.getComponentSize(input);
    var comboxWidth = 35;

    var inputSize = (size.width - comboxWidth);
    if (Cyan.navigator.isIE())
    {
        inputSize -= 5;
    }
    else if (Cyan.navigator.isChrome())
    {
        inputSize -= 5;
        comboxWidth -= 5;
    }

    var input2 = document.createElement("INPUT");
    input2.style.width = inputSize + "px";
    input2.style.cssFloat = "left";
    input2.style.styleFloat = "left";
    input2.title = input.title;
    container.appendChild(input2);

    var units = [];
    var b = false;
    for (var i = 0; i < Cyan.Extjs.Form.ByteSizeUnits.length; i++)
    {
        if (!b)
            b = unit == Cyan.Extjs.Form.ByteSizeUnits[i];
        if (b)
            units.push({value: units.length, text: Cyan.Extjs.Form.ByteSizeUnits[i]});
    }
    var store = new Ext.data.JsonStore({
        data: units,
        autoLoad: true,
        fields: ["value", "text"]
    });
    var unitValue = 0;
    if (input.value)
    {
        var byteValue = parseInt(input.value);
        while (byteValue % 1024 == 0 && unitValue < units.length - 1)
        {
            byteValue /= 1024;
            unitValue++;
        }
        input2.value = byteValue;
    }
    else
    {
        unitValue = 2;
    }
    Cyan.attach(input2, "input", function ()
    {
        if (this.value)
        {
            var value = parseInt(this.value);
            for (var i = 0; i < unitValue; i++)
                value *= 1024;
            hidden.value = value;
        }
        else
        {
            hidden.value = "";
        }
    });

    var listeners = {
        select: function ()
        {
            if (hidden.value)
            {
                unitValue = this.getValue();
                var value = parseInt(hidden.value);

                for (var i = 0; i < unitValue; i++)
                    value /= 1024;
                input2.value = value;
            }
            else
            {
                input2.value = "";
            }
        }
    };
    var combox = new Ext.form
            .ComboBox({
        editable: false,
        triggerAction: "all",
        mode: 'local',
        store: store,
        valueField: "value",
        displayField: "text",
        width: comboxWidth,
        height: size.height - 2,
        value: unitValue,
        listeners: listeners
    });
    var span = document.createElement("SPAN");
    container.appendChild(span);
    span.style.cssFloat = "left";
    span.style.styleFloat = "left";
    combox.render(span);

    hidden.onvaluechange = function ()
    {
        if (this.value)
        {
            var value = parseInt(this.value);
            for (var i = 0; i < unitValue; i++)
                value /= 1024;
            input2.value = value;
        }
        else
        {
            input2.value = "";
        }
    };
    hidden.ondisable = function ()
    {
        input2.disabled = true;
        combox.disable();
    };
    hidden.onenable = function ()
    {
        input2.disabled = false;
        combox.enable();
    };

    input.parentNode.insertBefore(container, input);
    input.parentNode.removeChild(input);
};

Cyan.Extjs.Form.TimeUnits = ["second", "minute", "hour", "day"];

Cyan.Extjs.Form.createTimeInput = function (input, unit)
{
    var container = document.createElement("SPAN");

    var hidden = document.createElement("INPUT");
    hidden.type = "hidden";
    hidden.name = input.name;
    hidden.id = input.id;
    hidden.value = input.value;
    container.appendChild(hidden);

    var size = Cyan.Elements.getComponentSize(input);
    var comboxWidth = 40;

    var input2 = document.createElement("INPUT");
    input2.style.width = (size.width - comboxWidth - 15) + "px";
    input2.style.height = size.height + "px";
    input2.style.cssFloat = "left";
    input2.style.styleFloat = "left";
    input2.title = input.title;
    container.appendChild(input2);

    if (Cyan.navigator.isIE())
        comboxWidth = 50;

    var units = [];
    var b = false;
    for (var i = 0; i < Cyan.Extjs.Form.TimeUnits.length; i++)
    {
        var timeUnit = Cyan.Extjs.Form.TimeUnits[i];
        if (!b)
            b = unit == timeUnit;
        if (b)
            units.push({value: timeUnit, text: Cyan.Date[timeUnit]});
    }
    var store = new Ext.data.JsonStore({
        data: units,
        autoLoad: true,
        fields: ["value", "text"]
    });

    var unitValue = unit;
    if (input.value)
    {
        var timeValue = parseInt(input.value);
        if (unitValue == "second")
        {
            if (timeValue % 60 == 0)
            {
                timeValue /= 60;
                unitValue = "minute";
            }
        }

        if (unitValue == "minute")
        {
            if (timeValue % 60 == 0)
            {
                timeValue /= 60;
                unitValue = "hour";
            }
        }

        if (unitValue == "hour")
        {
            if (timeValue % 24 == 0)
            {
                timeValue /= 24;
                unitValue = "day";
            }
        }
        input2.value = timeValue;
    }
    Cyan.attach(input2, "input", function ()
    {
        if (this.value)
        {
            var value = parseInt(this.value);

            if (unit == "second")
            {
                if (unitValue == "minute")
                    value = value * 60;
                else if (unitValue == "hour")
                    value = value * 60 * 60;
                else if (unitValue == "day")
                    value = value * 60 * 60 * 24;
            }
            else if (unit == "minute")
            {
                if (unitValue == "hour")
                    value = value * 60;
                else if (unitValue == "day")
                    value = value * 60 * 24;
            }
            else if (unit == "hour")
            {
                if (unitValue == "day")
                    value = value * 24;
            }

            hidden.value = value;
        }
        else
        {
            hidden.value = "";
        }
    });

    var listeners = {
        select: function ()
        {
            if (hidden.value)
            {
                unitValue = this.getValue();
                var value = parseInt(hidden.value);

                if (unit == "second")
                {
                    if (unitValue == "minute")
                        value = value / 60;
                    else if (unitValue == "hour")
                        value = value / 60 / 60;
                    else if (unitValue == "day")
                        value = value / 60 / 60 / 24;
                }
                else if (unit == "minute")
                {
                    if (unitValue == "hour")
                        value = value / 60;
                    else if (unitValue == "day")
                        value = value / 60 / 24;
                }
                else if (unit == "hour")
                {
                    if (unitValue == "day")
                        value = value / 24;
                }

                input2.value = value;
            }
            else
            {
                input2.value = "";
            }
        }
    };
    var combox = new Ext.form
            .ComboBox({
        editable: false,
        triggerAction: "all",
        mode: 'local',
        store: store,
        valueField: "value",
        displayField: "text",
        width: comboxWidth,
        height: size.height,
        value: unitValue,
        listeners: listeners
    });
    var span = document.createElement("SPAN");
    container.appendChild(span);
    span.style.cssFloat = "left";
    span.style.styleFloat = "left";
    combox.render(span);

    hidden.onvaluechange = function ()
    {
        if (this.value)
        {
            var value = this.value;

            if (unit == "second")
            {
                if (unitValue == "minute")
                    value = value / 60;
                else if (unitValue == "hour")
                    value = value / 60 / 60;
                else if (unitValue == "day")
                    value = value / 60 / 60 / 24;
            }
            else if (unit == "minute")
            {
                if (unitValue == "hour")
                    value = value / 60;
                else if (unitValue == "day")
                    value = value / 60 / 24;
            }
            else if (unit == "hour")
            {
                if (unitValue == "day")
                    value = value / 24;
            }

            input2.value = value;
        }
        else
        {
            input2.value = "";
        }
    };
    hidden.ondisable = function ()
    {
        input2.disabled = true;
        combox.disable();
    };
    hidden.onenable = function ()
    {
        input2.disabled = false;
        combox.enable();
    };

    input.parentNode.insertBefore(container, input);
    input.parentNode.removeChild(input);
};

Cyan.Extjs.Form.isRender = function (component)
{
    return (component.render || component.getAttribute("render")) != "false";
};

Cyan.Extjs.Form.isTargetList = function (select)
{
    return (select.getAttribute("type") || select.type ) == "targetlist";
};

Cyan.Extjs.Form.isHtmlEditor = function (component)
{
    return Cyan.Array.contains(component.className.split(" "), "htmleditor");
};

Cyan.Extjs.Form.createHtmlEditor = function (component)
{
    Cyan.run(function ()
    {
        return component.clientWidth && component.clientHeight;
    }, function ()
    {
        Cyan.Extjs.Form.replaceHtmlEditor(component);
    });
};

Cyan.Extjs.Form.HtmlEditor = {
    fontFamilies: null,
    defaultValue: " ",
    defaultFont: "Arial"
};

Cyan.Extjs.Form.replaceHtmlEditor = function (component)
{
    var size = Cyan.Elements.getComponentSize(component);
    var value = component.value;
    if (!value && Cyan.Extjs.Form.HtmlEditor.defaultValue)
        value = Cyan.Extjs.Form.HtmlEditor.defaultValue;

    var config = {
        id: component.id ||
        component.name, name: component.name, width: size.width, height: size.height, value: value
    };
    if (Cyan.Extjs.Form.HtmlEditor.fontFamilies)
        config.fontFamilies = Cyan.Extjs.Form.HtmlEditor.fontFamilies;
    if (Cyan.Extjs.Form.HtmlEditor.defaultFont)
        config.defaultFont = Cyan.Extjs.Form.HtmlEditor.defaultFont;

    var htmlEditor = new Ext.form.HtmlEditor(config);

    var keep = component.getAttribute("keep");

    var div = document.createElement("DIV");
    component.parentNode.insertBefore(div, component);
    component.parentNode.removeChild(component);
    htmlEditor.initFrame = function ()
    {
        Ext.TaskMgr.stop(this.monitorTask);
        this.doc = this.getDoc();
        this.win = this.getWin();

        this.doc.open();
        this.doc.write(this.getDocMarkup());
        this.doc.close();

        this.doc.designMode = "on";
        this.initEditor.defer(10, this);
    };

    htmlEditor.render(div);

    component = Cyan.$(component.name);
    component.setAttribute("keep", keep);

    if (component)
    {
        var onchange = function ()
        {
            htmlEditor.setValue(component.value);
        };
        component.onvaluechange = onchange;
        if (component.form)
        {
            Cyan.attach(component.form, "reset", function ()
            {
                onchange();
            });
        }
    }
};

Cyan.Extjs.Form.renderField = function (component)
{
    Cyan.Extjs.Form.init();
    if (!component && this.nodeName)
        component = this;
    if (component)
    {
        component = Cyan.$(component);
        if (component && component.style.visibility != "hidden" && component.style.display != "none" &&
                Cyan.Extjs.Form.isRender(component))
        {
            if (component.nodeName == "SELECT" && component.style.position != "absolute")
            {
                if (!component.multiple && Cyan.Extjs.Form.isTargetList(component))
                {
                    Cyan.TargetList.replaceCombox(component);
                }
                else
                {
                    var combox = Cyan.Extjs.Form.toCombox(component, component.form);
                    var div = document.createElement("DIV");
                    div.style.position = component.style.position;
                    div.style.left = component.style.left;
                    div.style.top = component.style.top;
                    div.style.float = "left";

                    if (component.getAttribute("require") != null && component.getAttribute("require") != false &&
                            component.options.length)
                    {
                        var hiddenId = combox.hiddenId;
                        setTimeout(function ()
                        {
                            Cyan.$(hiddenId).setAttribute("require", "true");
                            Cyan.$(hiddenId).showErrorTo = div;
                        }, 200);
                    }

                    component.parentNode.insertBefore(div, component);
                    component.parentNode.removeChild(component);

                    combox.render(div);
                }
            }
            else if (component.nodeName == "INPUT" && component.type == "text")
            {
                if (!Cyan.Extjs.Form.getSelectable(component, Cyan.Extjs.Form.makeSelectable))
                {
                    if (!component.readOnly)
                    {
                        var format, date;
                        if (Cyan.Extjs.Form.isYearMonth(component))
                        {
                            if (Cyan.Extjs.Form.isSupportInputType("month"))
                            {
                                component.setAttribute("type", "month");
                            }
                            else
                            {
                                setTimeout(function ()
                                {
                                    Cyan.Extjs.Form.makeYearMonthComponent(component);
                                }, 10);
                            }
                        }
                        else if (Cyan.Extjs.Form.isMonthDay(component))
                        {
                            setTimeout(function ()
                            {
                                Cyan.Extjs.Form.makeMonthDayComponent(component);
                            }, 10);
                        }
                        else if (Cyan.Extjs.Form.isDateTime(component))
                        {
                            var type = null;
                            if (Cyan.Extjs.Form.isSupportInputType("datetime"))
                            {
                                type = "datetime";
                            }
                            else if (Cyan.Extjs.Form.isSupportInputType("datetime-local"))
                            {
                                type = "datetime-local";
                            }

                            if (type)
                            {
                                if (component.value)
                                {
                                    format = Cyan.Extjs.Form.getDateFormat(component);
                                    date = Cyan.Date.parse(component.value, format);
                                    component.value = Cyan.Date.format(date, "yyyy-MM-ddTmm:ss");
                                }
                                component.setAttribute("type", type);
                            }
                            else
                            {
                                setTimeout(function ()
                                {
                                    Cyan.Extjs.Form.makeDateTimeComponent(component);
                                }, 10);
                            }
                        }
                        else if (Cyan.Extjs.Form.isDate(component))
                        {
                            format = component.getAttribute("format");
                            if (Cyan.Extjs.Form.isSupportInputType("date"))
                            {
                                if (component.value && format != "yyyy-MM-dd")
                                {
                                    date = Cyan.Date.parse(component.value, format);
                                    component.value = Cyan.Date.format(date, "yyyy-MM-dd");
                                }
                                component.setAttribute("type", "date");
                            }
                            else
                            {
                                if (format == "yyyyMMdd")
                                {
                                    setTimeout(function ()
                                    {
                                        Cyan.Extjs.Form.makeYearMonthDayComponent(component);
                                    }, 10);
                                }
                                else
                                {
                                    if (Cyan.Extjs.Form.isEditable(component))
                                    {
                                        makeSelectIcon(component, function ()
                                        {
                                            if (!component.disabled1)
                                                Cyan.Extjs.Form.showDateMenu(component);
                                        });
                                    }
                                    else
                                    {
                                        component.readOnly = true;
                                        Cyan.attach(component, "click", function ()
                                        {
                                            if (!this.disabled1)
                                                Cyan.Extjs.Form.showDateMenu(this);
                                        });
                                        component.className += " cyan-form-field-date";
                                    }
                                }
                            }
                        }
                        else if (Cyan.Extjs.Form.isColor(component))
                        {
                            if (Cyan.Extjs.Form.isSupportInputType("color"))
                            {
                                component.setAttribute("type", "color");
                            }
                            else
                            {
                                Cyan.attach(component, "click", function ()
                                {
                                    Cyan.Extjs.Form.showColorMenu(this);
                                });
                                component.className += " cyan-form-field-color";
                            }
                        }
                        else if (Cyan.Extjs.Form.isByteSize(component))
                        {
                            Cyan.Extjs.Form.createByteSizeInput(component);
                        }
                        else if (Cyan.Extjs.Form.isSecond(component))
                        {
                            Cyan.Extjs.Form.createTimeInput(component, "second");
                        }
                        else if (Cyan.Extjs.Form.isMinute(component))
                        {
                            Cyan.Extjs.Form.createTimeInput(component, "minute");
                        }
                        else if (Cyan.Extjs.Form.isHour(component))
                        {
                            Cyan.Extjs.Form.createTimeInput(component, "hour");
                        }
                    }
                }
            }
            else if (component.nodeName == "TEXTAREA")
            {
                if (Cyan.Extjs.Form.isHtmlEditor(component))
                {
                    Cyan.Extjs.Form.createHtmlEditor(component);
                }
            }
            else if (component.nodeName == "FORM")
            {
                Cyan.each(Cyan.clone(component), function ()
                {
                    Cyan.Extjs.Form.renderField(this);
                });
            }
        }
    }
    else
    {
        Cyan.$$("FORM").each(function ()
        {
            Cyan.Extjs.Form.renderField(this);
        });
    }
};

function makeClearable(text, component)
{
    var clearImg;
    Cyan.attach(text, "mouseover", function ()
    {
        if (!clearImg)
        {
            clearImg = document.createElement("DIV");
            clearImg.className = "cyan-selectable-clear";
            clearImg.style.display = "none";
            clearImg.onclick = function ()
            {
                text.value = "";
                if (component)
                {
                    component.value = "";
                    if (component.onchange)
                        component.onchange();
                }
                else if (text.onchange)
                {
                    text.onchange();
                }
                try
                {
                    text.focus();
                }
                catch (e)
                {
                }
            };
            document.body.appendChild(clearImg);
        }

        var position = Cyan.Elements.getPosition(text);
        var size = Cyan.Elements.getComponentSize(text);
        clearImg.style.left = (position.x + size.width - 18) + "px";
        clearImg.style.top = position.y + 3 + "px";
        clearImg.style.display = "";
    });
    Cyan.attach(text, "mouseout", function (event)
    {
        if (clearImg && !event.isOn(text))
            clearImg.style.display = "none";
    });
    Cyan.attach(text, "blur", function (event)
    {
        if (clearImg)
        {
            setTimeout(function ()
            {
                clearImg.style.display = "none";
            }, 500);
        }
    });
    var node = text;
    var scroll = function ()
    {
        var position = Cyan.Elements.getPosition(text);
        var size = Cyan.Elements.getComponentSize(text);
        clearImg.style.left = (position.x + size.width - 18) + "px";
        clearImg.style.top = position.y + 3 + "px";
    };
    var scroll0 = function (event)
    {
        if (clearImg && clearImg.style.display != "none")
        {
            setTimeout(scroll, 1);
        }
    };
    while ((node = node.parentNode).nodeName != "BODY")
    {
        Cyan.attach(node, "scroll", scroll0);
    }
    Cyan.attach(document.body, "scroll", scroll0);
    Cyan.attach(window, "scroll", scroll0);
}

function makeSelectIcon(text, onselect)
{
    var selectImg;
    Cyan.attach(text, "mouseover", function ()
    {
        if (!selectImg)
        {
            selectImg = document.createElement("DIV");
            selectImg.className = "cyan-selectable-icon";
            selectImg.style.display = "none";
            selectImg.onclick = onselect;
            document.body.appendChild(selectImg);
        }

        var position = Cyan.Elements.getPosition(text);
        var size = Cyan.Elements.getComponentSize(text);
        selectImg.style.left = (position.x + size.width - 18) + "px";
        selectImg.style.top = position.y + 3 + "px";
        selectImg.style.display = "";
    });
    Cyan.attach(text, "mouseout", function (event)
    {
        if (selectImg && !event.isOn(text))
            selectImg.style.display = "none";
    });
    Cyan.attach(text, "blur", function (event)
    {
        if (selectImg)
        {
            setTimeout(function ()
            {
                selectImg.style.display = "none";
            }, 500);
        }
    });
    var node = text;
    var scroll = function ()
    {
        var position = Cyan.Elements.getPosition(text);
        var size = Cyan.Elements.getComponentSize(text);
        selectImg.style.left = (position.x + size.width - 18) + "px";
        selectImg.style.top = position.y + 3 + "px";
    };
    var scroll0 = function (event)
    {
        if (selectImg && selectImg.style.display != "none")
        {
            setTimeout(scroll, 1);
        }
    };
    while ((node = node.parentNode).nodeName != "BODY")
    {
        Cyan.attach(node, "scroll", scroll0);
    }
    Cyan.attach(document.body, "scroll", scroll0);
    Cyan.attach(window, "scroll", scroll0);
}

Cyan.renderField = Cyan.Extjs.Form.renderField;

Cyan.Extjs.Form.reset = function (form)
{
    form = Cyan.$(form);
    Cyan.each(form, function ()
    {
        if (this.getAttribute("keep") == "true")
        {
            this.initialValue = Cyan.$$(this).getValue();
        }
    });
    form.reset();
    Cyan.each(form, function ()
    {
        if (this.getAttribute("keep") == "true")
        {
            Cyan.$$(this).setValue(this.initialValue);
            this.initialValue = null;
        }
    });
};

Cyan.reset = Cyan.Extjs.Form.reset;
Cyan.Elements.prototype.reset = function ()
{
    this.each(function ()
    {
        if (this.nodeName == "FORM")
            Cyan.Extjs.Form.reset(this);
    });
};