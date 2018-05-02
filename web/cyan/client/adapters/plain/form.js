Cyan.importJs("widgets/menu.js");
Cyan.importJs("combox.js");
Cyan.importJs("event.js");
Cyan.importJs("/My97DatePicker/WdatePicker.js");
Cyan.Plain.importCss("form");

(function ()
{

    var inited = false;
    var init = function ()
    {
        if (!inited)
        {
            inited = true;
            Cyan.importJs("widgets/list.js");
        }
    };

    var isRender = function (component)
    {
        return (component.render || component.getAttribute("render")) != "false";
    };

    var isEditable = function (select)
    {
        return select.getAttribute("editable") == "true";
    };

    var isTargetList = function (select)
    {
        return (select.getAttribute("type") || select.type ) == "targetlist";
    };

    var isDate = function (component)
    {
        return (component.getAttribute("dataType") || component.getAttribute("datatype")) == "date";
    };

    var isDateTime = function (component)
    {
        var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
        return datatype == "dateTime" || datatype == "datetime";
    };

    var isYearMonth = function (component)
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

    var isMonthDay = function (component)
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

    var getDateFormat = function (component)
    {
        try
        {
            var format = component.getAttribute("format");
            if (format)
                return format;

            if (isDateTime(component))
                return "yyyy-MM-dd HH:mm:ss";
            else if (isDate(component))
                return "yyyy-MM-dd";
            else if (isYearMonth(component))
                return "yyyy-MM";
            else if (isMonthDay(component))
                return "MM/dd";
        }
        catch (e)
        {
            return "yyyy-MM-dd HH:mm:ss";
        }
    };

    var isByteSize = function (component)
    {
        var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
        return datatype == "bytesize" || datatype == "byteSize";
    };

    var getByteUnit = function (component)
    {
        return component.getAttribute("unit") || "B";
    };

    var isSecond = function (component)
    {
        var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
        return datatype == "second" || datatype == "second";
    };

    var isMinute = function (component)
    {
        var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
        return datatype == "minute" || datatype == "minute";
    };

    var isHour = function (component)
    {
        var datatype = component.getAttribute("dataType") || component.getAttribute("datatype");
        return datatype == "hour" || datatype == "hour";
    };

    var isSupportInputType = (function ()
    {
        var supportTypes;
        return function (type)
        {
            return type == "text";

            //if (!supportTypes)
            //    supportTypes = {};
            //
            //var supported = supportTypes[type];
            //if (supported == null)
            //{
            //    var input = document.createElement("input");
            //    input.setAttribute("type", type);
            //    supported = supportTypes[type] = input.type != "text";
            //}
            //
            //return supported;
        };
    })();

    var makeIcon = function (text, className, action)
    {
        text.className = text.className + " cyan-noclear";
        var img = document.createElement("span");
        img.className = className;
        img.style.height = (Cyan.toInt(Cyan.Elements.getCss(text, "height")) + 2) + "px";

        if (text.style.position == "absolute")
        {
            img.style.position = "absolute";
            img.style.top = (Cyan.toInt(text.style.top) + 1) + "px";
            img.style.left = (Cyan.toInt(text.style.left) + Cyan.toInt(text.style.width) - 14) + "px";
            if (text.nextSibling)
                text.parentNode.insertBefore(img, text.nextSibling);
            else
                text.appendChild(img);
        }
        else
        {
            var inputDiv = document.createElement("div");
            inputDiv.className = "cyan-form-input-div";
            text.parentNode.insertBefore(inputDiv, text);
            inputDiv.appendChild(text);

            img.style.left = (Cyan.toInt(Cyan.Elements.getCss(text, "width")) +
                    Cyan.toInt(Cyan.Elements.getCss(text, "margin-left") ||
                            Cyan.Elements.getCss(text, "margin")) - 12) + "px";
            img.style.display = "none";
            inputDiv.appendChild(img);
        }

        img.onclick = action;
        Cyan.attach(document.body, "mousemove", function (event)
        {
            img.style.display =
                    event.isOn(text) && ( className != "cyan-selectable-clear" || text.value ) ? "" : "none";
        });
    };

    var getSelectable = (function ()
    {
        var queue = [];
        return function (component, callback)
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

    var makeSelectable = (function ()
    {
        var selectableMenus = {};

        var getSelectableText = function (component)
        {
            if (component.text != null)
                return component.text;
            return component.getAttribute("text");
        };

        return function (component, selectable)
        {
            if (!selectable)
            {
                getSelectable(component, makeSelectable);
                return;
            }

            var readOnly = component.readOnly;
            var disabled = component.disabled;
            var keep = component.getAttribute("keep");

            var text, onchange;
            var editable = isEditable(component);
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
                text.className = component.className + " cyan-selectable";
                text.id = Cyan.generateId("cyan_selectable");
                var s = getSelectableText(component);
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
                    menu = selectableMenus[selectable.variableName];
                    if (menu == null)
                    {
                        if (!selectable.width &&
                                (Cyan.Tree && selectable instanceof Cyan.Tree ||
                                Cyan.List && selectable instanceof Cyan.List))
                        {
                            var width = size.width;
                            if (width < 200)
                                width = 200;
                            selectable.width = width;
                        }

                        if (!selectable.height &&
                                (Cyan.Tree && selectable instanceof Cyan.Tree ||
                                Cyan.List && selectable instanceof Cyan.List))
                        {
                            selectable.height = 240;
                        }

                        if (selectable.height)
                        {
                            var maxHeight = Cyan.getBodyHeight() - y - 40;
                            if (selectable.height > maxHeight)
                                selectable.height = maxHeight;
                        }

                        menu = selectableMenus[selectable.variableName] =
                                new Cyan.Menu(selectable);

                        menu.onselect = function (widget)
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
                        };
                    }

                    var node = text;
                    var scroll = function ()
                    {
                        menu.hide();
                    };
                    while ((node = node.parentNode).nodeName != "BODY")
                    {
                        Cyan.attach(node, "scroll", scroll);
                    }
                    Cyan.attach(document.body, "scroll", scroll);
                    Cyan.attach(window, "scroll", scroll);
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

                menu.showAt(position.x, y);
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
                    makeIcon(text, "cyan-selectable-down", onselect);
                }
                else
                {
                    Cyan.attach(text, "click", onselect);
                    makeIcon(text, "cyan-selectable-clear", function ()
                    {
                        text.value = "";
                        if (multiple)
                        {
                            var options = component.options;
                            while (options.length)
                                options[0] = null;
                        }
                        else
                        {
                            component.value = "";
                        }

                        if (component.onchange)
                            component.onchange();
                        if (selectable.clearSelectedItems)
                            selectable.clearSelectedItems();
                        else if (selectable.clearSelections)
                            selectable.clearSelections();
                    });
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
    })();

    var toCombox = function (select, form)
    {
        var targetList = isTargetList(select);
        var editable = !targetList && isEditable(select);
        var disabled = select.disabled;
        var onchange = select.onchange;

        var div;
        if (!targetList)
        {
            div = document.createElement("div");
            div.className = "cyan-combox-items";
            div.style.display = "none";
            document.body.appendChild(div);
        }

        var selectedIndex = 0;
        var options = select.options;
        var n = options.length;
        var value = "", text, empty = "", items = new Array(n);
        for (var i = 0; i < n; i++)
        {
            var option = options[i];
            var value1 = option.value;
            var text1 = option.text;
            if (value1 == "" && !empty)
                empty = text1 || "--------";
            items[i] = {value: value1, text: text1};
            if (targetList)
                items[i].id = value1;
            if (option.selected)
            {
                value = value1;
                text = text1;
                selectedIndex = i;
            }
        }

        var itemClick, list, menu;
        if (!targetList)
        {
            itemClick = function ()
            {
                div.style.display = "none";
                hidden.value = this.value;
                selectedIndex = this.index;

                updateText(this.value, true);

                if (onchange)
                    onchange.apply(hidden);
            };
        }

        var setItems = function (items1)
        {
            items = items1;
            var i, n, item;
            if (targetList)
            {
                n = items.length;
                for (i = 0; i < n; i++)
                {
                    item = items[i];
                    item.id = item.value;
                }
                if (list)
                    list.reload(false, null);
            }
            else
            {
                div.innerHTML = "";
                n = items.length;
                for (i = 0; i < n; i++)
                {
                    item = items[i];
                    var itemDiv = document.createElement("div");
                    var className = "cyan-combox-item";
                    if (item.value == (component ? component.value : value))
                        className += " cyan-combox-item-selected";
                    itemDiv.className = className;
                    itemDiv.text = itemDiv.innerHTML = Cyan.escapeHtml(item.text || empty);
                    itemDiv.value = item.value;
                    itemDiv.index = i;
                    itemDiv.onclick = itemClick;
                    div.appendChild(itemDiv);
                }
            }
        };

        if (!targetList)
            setItems(items);

        var filter;
        if (editable)
        {
            filter = function (text)
            {
                var itemDivs = div.childNodes;
                for (var i = 0; i < itemDivs.length; i++)
                {
                    var itemDiv = itemDivs[i];
                    itemDiv.style.display = (!text || itemDiv.text.indexOf(text) >= 0) ? "" : "none";
                }
            };
        }

        var inputDiv;
        if (select.style.position != "absolute")
        {
            inputDiv = document.createElement("div");
            inputDiv.className = "cyan-form-input-div";
            select.parentNode.insertBefore(inputDiv, select);
        }

        var input = document.createElement("input");
        input.style.cssText = select.style.cssText;
        input.disabled = disabled;
        if (editable)
            input.className = select.className + " cyan-noclear";
        else
            input.className = select.className + " cyan-combox";
        input.id = Cyan.generateId("cyan_combox");
        if (!editable)
        {
            input.readOnly = true;
        }
        else
        {
            Cyan.attach(input, "input", function ()
            {
                hidden.value = this.value;
            });
        }
        input.value = text || empty;

        var hidden = document.createElement("input");
        hidden.type = "hidden";
        hidden.value = value;
        hidden.id = select.id;
        hidden.name = select.name;
        select.parentNode.insertBefore(hidden, select);

        var width = Cyan.Elements.getCss(select, "width");
        if (width && width != "auto")
        {
            input.style.width = width;
        }
        else
        {
            var size = Cyan.Elements.getComponentSize(select);
            input.style.width = size.width + "px";
        }

        if (select.style.position == "absolute")
            select.parentNode.insertBefore(input, select);
        else
            inputDiv.appendChild(input);

        var inputWidth = Cyan.Elements.getCss(input, "width");
        if (Cyan.endsWith(inputWidth, "%"))
        {
            inputWidth = Cyan.Elements.getComponentSize(select).width - 2;
        }
        else
        {
            inputWidth = Cyan.toInt(inputWidth);
        }
        width = inputWidth + 2;

        if (!targetList)
            div.style.width = ( width < 45 ? 45 : width) + 2 + "px";

        var img = document.createElement("span");
        img.className = "cyan-combox-down";
        img.style.height = (Cyan.toInt(Cyan.Elements.getCss(input, "height")) + 2) + "px";

        if (select.style.position == "absolute")
        {
            img.style.position = "absolute";
            img.style.top = (Cyan.toInt(select.style.top) + 1) + "px";
            img.style.left = (Cyan.toInt(select.style.left) + Cyan.toInt(select.style.width) - 10) + "px";
            select.parentNode.insertBefore(img, select);
        }
        else
        {
            img.style.left = (inputWidth + Cyan.toInt(Cyan.Elements.getCss(input, "margin-left") ||
                            Cyan.Elements.getCss(input, "margin")) - 10) + "px";
            inputDiv.appendChild(img);
        }

        select.parentNode.removeChild(select);

        var click = function ()
        {
            if (!disabled)
            {
                if (targetList)
                {
                    showMenu();
                }
                else
                {
                    if (editable)
                        filter("");
                    showItems();
                }
            }
        };

        var showItems, showMenu;
        if (targetList)
        {
            showMenu = function ()
            {
                if (!menu)
                {
                    list = new Cyan.List(select.name + "$list", items);
                    list.width = width;
                    list.loadItems = function ()
                    {
                        return items;
                    };
                    menu = new Cyan.Menu(list);
                    menu.onselect = function (list)
                    {
                        var value = list.getSelectedId();
                        hidden.value = value;

                        var n = items.length;
                        for (var i = 0; i < n; i++)
                        {
                            var item = items[i];
                            if (item.value == value)
                            {
                                selectedIndex = i;
                                input.value = item.text;
                                break;
                            }
                        }
                    };
                }
                var position = Cyan.Elements.getPosition(input);
                var size = Cyan.Elements.getComponentSize(input);
                var y = position.y + size.height + 4;
                menu.showAt(position.x, y);
            }
        }
        else
        {
            showItems = function ()
            {
                var position = Cyan.Elements.getPosition(input);
                var size = Cyan.Elements.getComponentSize(input);

                div.style.left = position.x + "px";
                div.style.top = (position.y + size.height + 1) + "px";
                div.style.display = "";

                div.style.hegith = "";
                var height = div.clientHeight;
                var maxHeight = window.document.documentElement.clientHeight - position.y - 40;
                if (height > maxHeight)
                    div.style.height = maxHeight + "px";

                if (items.length)
                {
                    var childNode = div.childNodes[selectedIndex];
                    if (childNode.style.display != "none")
                        Cyan.Elements.scrollYTo(div, childNode);
                }
            };
        }

        if (!editable)
        {
            Cyan.attach(input, "click", click);
        }
        Cyan.attach(img, "click", click);

        if (!targetList)
        {
            Cyan.attach(document.body, "mousedown", function (event)
            {
                if (div.style.display != "none" && !event.isOn(div))
                {
                    div.style.display = "none";
                    if (!targetList)
                    {
                        if (items.length)
                        {
                            updateText(items[selectedIndex].value, false);
                            selectedIndex1 = selectedIndex;
                        }
                    }
                }
            });
        }

        var selectedIndex1;
        if (!targetList)
        {
            selectedIndex1 = selectedIndex;
            Cyan.attach(input, "keydown", function (event)
            {
                if (!disabled)
                {
                    var selectedIndex0;
                    if (event.keyCode == 38)
                    {
                        event.stop();
                        if (div.style.display != "none" && selectedIndex1 > 0)
                        {
                            selectedIndex0 = selectedIndex1;
                            while (--selectedIndex1 >= 0 && div.childNodes[selectedIndex1].style.display == "none")
                            {
                            }
                            if (selectedIndex1 < 0)
                                selectedIndex1 = selectedIndex0;
                            else
                                updateText(items[selectedIndex1].value, false);
                        }
                    }
                    else if (event.keyCode == 40)
                    {
                        event.stop();

                        if (div.style.display == "none")
                        {
                            click();
                            selectedIndex1 = selectedIndex;
                        }
                        else if (selectedIndex1 < items.length - 1)
                        {
                            selectedIndex0 = selectedIndex1;
                            while (++selectedIndex1 < items.length &&
                            div.childNodes[selectedIndex1].style.display == "none")
                            {
                            }
                            if (selectedIndex1 >= items.length)
                                selectedIndex1 = selectedIndex0;
                            else
                                updateText(items[selectedIndex1].value, false);
                        }
                    }
                    else if (event.keyCode == 13)
                    {
                        event.stop();
                        itemClick.apply(div.childNodes[selectedIndex1]);
                        Cyan.focus(input);
                    }
                    else if (event.keyCode == 9)
                    {
                        itemClick.apply(div.childNodes[selectedIndex1]);
                    }
                    else
                    {
                        filter(this.value);
                        showItems();
                    }
                }
            });
            if (editable)
            {
                Cyan.attach(input, "input", function (event)
                {
                    if (!disabled)
                    {
                        filter(this.value);
                        showItems();
                    }
                });
            }
        }

        var updateText = function (value, selected)
        {
            var item, n = items.length, i;
            for (i = 0; i < n; i++)
            {
                item = items[i];
                if (item.value == value)
                {
                    if (selected)
                        input.value = item.text || empty;

                    if (targetList)
                        break;

                    div.childNodes[i].className = "cyan-combox-item cyan-combox-item-selected";
                    Cyan.Elements.scrollYTo(div, div.childNodes[i]);
                }
                else if (!targetList)
                {
                    div.childNodes[i].className = "cyan-combox-item";
                }
            }

            if (!value && selected && i == n && n > 0 && !targetList)
            {
                for (i = 0; i < n; i++)
                {
                    item = items[i];
                    if (i == 0)
                    {
                        input.value = item.text || empty;
                        hidden.value = item.value;
                        div.childNodes[i].className = "cyan-combox-item cyan-combox-item-selected";
                        Cyan.Elements.scrollYTo(div, div.childNodes[i]);
                    }
                    else
                    {
                        div.childNodes[i].className = "cyan-combox-item";
                    }
                }
            }
        };

        hidden.setValue = function (value)
        {
            hidden.value = value;
            if (updateText)
                updateText(value, true);
        };
        hidden.onvaluechange = function ()
        {
            updateText(this.value, true);
        };

        var component = hidden || input;
        component.ondisable = function ()
        {
            input.disabled = "true";
            disabled = true;
        };
        component.onenable = function ()
        {
            input.disabled = "false";
            disabled = false;
        };
        component.onBeforeRefresh = function ()
        {
            this.oldValue = this.value;
            this.value = "";
            setItems([]);
        };
        component.onAfterRefresh = function (selectable)
        {
            if (!selectable)
                selectable = [];
            var oldValue = this.oldValue;
            this.oldValue = null;
            var newValue = null;

            var n = selectable.length, items = new Array(n);
            for (var i = 0; i < n; i++)
            {
                var item = selectable[i];
                var value = Cyan.valueOf(item);
                var text = Cyan.toString(item);

                items[i] = {value: value, text: text};

                if (value == oldValue)
                    newValue = value;
            }

            if (!newValue && items.length)
                newValue = items[0].value;

            setItems(items);
            component.value = newValue;
            updateText(newValue, true);
        };
        component.selectedIndex = function ()
        {
            return selectedIndex;
        };
        component.allItems = function ()
        {
            return Cyan.clone(items);
        };
        component.allValues = function ()
        {
            return Cyan.get(items, "value");
        };
        component.addItem = function (value, text)
        {
            if (!Cyan.searchFirst(items, function ()
                    {
                        return this.value == value;
                    }))
            {
                items.push({value: value, text: text});
                setItems(items);
            }
        };
        component.removeItem = function (value)
        {
            var oldValue = component.value;
            Cyan.Array.removeCase(items, function ()
            {
                return this.value == value;
            });
            setItems(items);
            if (oldValue == value)
            {
                if (items.length)
                    component.value = items[0].value;
                else
                    component.value = "";

                updateText(component.value, true);
            }

        };
        component.clearAll = function ()
        {
            setItems(items = []);
            component.value = "";
            if (hidden)
                input.text = "";
        };

        var keep = select.getAttribute("keep");
        if (keep)
        {
            component.setAttribute("keep", keep);
            input.setAttribute("keep", keep);
        }

        if (form)
        {
            if (keep != "true")
            {
                Cyan.attach(form, "reset", function ()
                {
                    component.value = value;
                    updateText(value, true);
                });
            }
        }

        var node = input.parentNode;
        var scroll = function ()
        {
            if (targetList)
            {
                if (menu)
                    menu.hide();
            }
            else
            {
                div.style.display = "none";
            }
        };
        while ((node = node.parentNode).nodeName != "BODY")
        {
            Cyan.attach(node, "scroll", scroll);
        }
        Cyan.attach(document.body, "scroll", scroll);
        Cyan.attach(window, "scroll", scroll);
    };

    var toList = function (select, form)
    {
        var disabled = select.disabled;
        var onchange = select.onchange;
        var onclick = select.onclick;
        var ondblclick = select.ondblclick;

        var div = document.createElement("div");
        div.className = "cyan-form-list";
        div.onclick = onclick;
        div.ondblclick = ondblclick;


        var lastClickedItem;
        var itemClick = function (event)
        {
            if (lastClickedItem && !lastClickedItem.parentNode)
                lastClickedItem = null;

            var ctrl = (event || window.event).ctrlKey;
            var shift = (event || window.event).shiftKey;

            if (shift && lastClickedItem)
            {
                var start, end;
                if (this.index > lastClickedItem.index)
                {
                    start = lastClickedItem.index;
                    end = this.index;
                }
                else
                {
                    start = this.index;
                    end = lastClickedItem.index;
                }

                var childNodes = div.childNodes;
                for (var i = start; i <= end; i++)
                    selectItem(childNodes[i], i > start || ctrl, true);
            }
            else
            {
                selectItem(this, ctrl, false);
                lastClickedItem = this;
            }

            if (onchange)
                onchange.apply(select);
        };

        var startItem, selectedValues, moved;
        div.onmousedown = function (event)
        {
            if (!event)
                event = window.event;

            if (event.button <= 1)
            {
                var src = (event.target || event.srcElement);
                if (src.className == "cyan-form-list-item" ||
                        src.className == "cyan-form-list-item cyan-form-list-item-selected")
                {
                    startItem = src;
                    moved = false;
                    selectedValues =
                            event.ctrlKey ? Cyan.map(Cyan.search(select.options, "selected"), "value") : [];
                }
            }
        };
        Cyan.attach(document.body, "mouseup", function (event)
        {
            if (startItem)
            {
                if (moved)
                    lastClickedItem = startItem;

                startItem = null;
                selectedValues = null;
                moved = false;
            }
        });
        Cyan.attach(document.body, "mousemove", function (event)
        {
            if (startItem)
            {
                event = new Cyan.Event(event || window.event);
                var src;
                var childNodes = div.childNodes;
                var n = childNodes.length;
                for (var i = 0; i < n; i++)
                {
                    var childNode = childNodes[i];
                    if (event.isOn(childNode))
                    {
                        src = childNode;
                        break;
                    }
                }
                if (src)
                {
                    var start, end;
                    if (src.index > startItem.index)
                    {
                        start = startItem.index;
                        end = src.index;
                    }
                    else
                    {
                        start = src.index;
                        end = startItem.index;
                    }

                    var options = select.options;
                    n = options.length;
                    for (i = 0; i < n; i++)
                    {
                        var option = options[i];
                        option.selected =
                                i >= start && i <= end || Cyan.Array.contains(selectedValues, option.value);
                    }

                    refreshItems();
                    moved = true;

                    if (onchange)
                        onchange.apply(select);
                }
            }
        });

        var selectItem = function (itemDiv, append, selected)
        {
            var options = select.options;
            var n = options.length, i, option;
            if (append)
            {
                for (i = 0; i < n; i++)
                {
                    option = options[i];
                    if (option.value == itemDiv.value)
                    {
                        option.selected = selected || !option.selected;
                        itemDiv.className = option.selected ? "cyan-form-list-item cyan-form-list-item-selected" :
                                "cyan-form-list-item";
                        break;
                    }
                }
            }
            else
            {
                var itemDivs = div.childNodes;
                for (i = 0; i < n; i++)
                {
                    option = options[i];
                    option.selected = option.value == itemDiv.value;
                    itemDivs[i].className = option.value == itemDiv.value ?
                            "cyan-form-list-item cyan-form-list-item-selected" : "cyan-form-list-item";
                }
            }
        };

        var addItem = function (item, index)
        {
            var itemDiv = document.createElement("div");
            var className = "cyan-form-list-item";
            if (item.selected)
                className += " cyan-form-list-item-selected";
            itemDiv.className = className;
            itemDiv.value = item.value;
            itemDiv.innerHTML = item.text;
            itemDiv.index = index;
            itemDiv.onclick = itemClick;
            Cyan.Elements.disableSelection(itemDiv);
            div.appendChild(itemDiv);
        };

        var refreshItems = function ()
        {
            var childNodes = div.childNodes;
            var options = select.options;
            var n = options.length;
            for (var i = 0; i < n; i++)
            {
                var item = options[i];
                childNodes[i].className =
                        item.selected ? "cyan-form-list-item cyan-form-list-item-selected" : "cyan-form-list-item"
            }
        };
        var updateItems = function ()
        {
            div.innerHTML = "";
            var options = select.options;
            var n = options.length;
            for (var i = 0; i < n; i++)
            {
                var item = options[i];
                addItem(item, i);
            }
        };
        updateItems();

        div.style.width = Cyan.Elements.getCss(select, "width");
        div.style.height = Cyan.Elements.getCss(select, "height");
        select.parentNode.insertBefore(div, select);
        select.style.display = "none";

        var options = Cyan.Options(select.options);

        var wrap = function (name, name1, update)
        {
            var f = update ? updateItems : refreshItems;
            if (!name1)
                name1 = name;
            if (options == select && name == name1)
            {
                Cyan.overwrite(select, name, function ()
                {
                    var ret = this.inherited.apply(this, arguments);
                    f();
                    return ret;
                });
            }
            else
            {
                select[name] = function ()
                {
                    var ret = options[name1].apply(options, arguments);
                    f();
                    return ret;
                }
            }
        };

        var copy = function (name, name1)
        {
            if (!name1)
                name1 = name;
            if (select == options)
            {
                if (name != name1)
                    select[name] = options[name1];
            }
            else
            {
                select[name] = function ()
                {
                    return options[name1].apply(options, arguments);
                }
            }
        };

        select.allItems = function ()
        {
            return this.options;
        };

        wrap("up", null, true);
        wrap("down", null, true);
        wrap("selectAll", null, false);
        wrap("clearSelected", null, true);
        wrap("addItem", "addOption", true);
        wrap("removeItem", "remove", true);
        wrap("clearAll", "clear", true);


        copy("selectedItems", "selecteds");
        copy("selectedItem", "selected");
        copy("selectedIndex", "getSelectedIndex");
        if (options != select)
        {
            copy("selectedIndexes");
            copy("selectedValues");
            copy("allValues");
        }
    };

    var showDateMenu = function (component, format)
    {
        var onpicked;
        if (component)
        {
            onpicked = function ()
            {
                Cyan.fireEvent(component, "change");

                if (component.onchange)
                    component.onchange();
            };
        }
        WdatePicker({
            el: component, dateFmt: format || "yyyy-MM-dd", onpicked: onpicked
        });
    };

    var makeDateTimeComponent = function (component)
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

        var format = getDateFormat(component);
        var date = Cyan.Date.parse(component.value, format);

        var onchange = function ()
        {
            var value = component.value;
            if (dateInput.value)
            {
                var s = dateInput.value + " " + Cyan.$(hoursSelectName).value + ":" +
                        Cyan.$(minutesSelectName).value +
                        ":00";
                component.value = Cyan.Date.format(Cyan.Date.parse(s), format);
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

        var dateInputName = component.name + "$date";
        var dateInput = document.createElement("INPUT");
        dateInput.name = dateInputName;
        dateInput.style.cssText = component.style.cssText;
        dateInput.className = component.className + " cyan-form-date";
        dateInput.style.width = "75px";
        if (dateInput.style.borderBottomWidth == "0" || dateInput.style.borderBottomWidth == "0px")
            dateInput.style.borderBottomWidth = "1px";
        dateInput.readOnly = true;
        dateInput.setAttribute("format", "yyyy-MM-dd");
        if (date)
            dateInput.value = Cyan.Date.format(date, "yyyy-MM-dd");
        Cyan.attach(dateInput, "click", function ()
        {
            showDateMenu(this);
        });
        component.parentNode.insertBefore(dateInput, component);

        var hoursSelectName = component.name + "$hours";
        var hoursSelect = document.createElement("SELECT");
        hoursSelect.name = hoursSelectName;
        hoursSelect.style.cssText = component.style.cssText;
        hoursSelect.style.width = "30px";
        if (component.style.left)
            hoursSelect.style.left = (Cyan.toInt(component.style.left) + 85) + "px";
        if (component.style.top)
            hoursSelect.style.top = Cyan.toInt(component.style.top) + "px";
        if (hoursSelect.style.borderBottomWidth == "0" || hoursSelect.style.borderBottomWidth == "0px")
            hoursSelect.style.borderBottomWidth = "1px";
        hoursSelect.style.textAlign = "left";
        hoursSelect.className = component.className + " cyan-form-hour";
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
        component.parentNode.insertBefore(hoursSelect, component);
        toCombox(hoursSelect, component.form);

        var splitDiv = document.createElement("div");
        splitDiv.className = "cyan-form-hour-minute-split";
        splitDiv.style.lineHeight = size.height + "px";
        splitDiv.innerHTML = ":";
        splitDiv.style.position = component.style.position;
        if (component.style.left)
            splitDiv.style.left = (Cyan.toInt(component.style.left) + 115) + "px";
        if (component.style.top)
            splitDiv.style.top = (Cyan.toInt(component.style.top) - 4) + "px";
        component.parentNode.insertBefore(splitDiv, component);

        var minutesSelectName = component.name + "$minutes";
        var minutesSelect = document.createElement("SELECT");
        minutesSelect.name = minutesSelectName;
        minutesSelect.className = component.className + " cyan-form-minute";
        minutesSelect.style.cssText = component.style.cssText;
        minutesSelect.style.width = "30px";
        if (component.style.left)
            minutesSelect.style.left = (Cyan.toInt(component.style.left) + 125) + "px";
        if (component.style.top)
            minutesSelect.style.top = Cyan.toInt(component.style.top) + "px";
        if (minutesSelect.style.borderBottomWidth == "0" || minutesSelect.style.borderBottomWidth == "0px")
            minutesSelect.style.borderBottomWidth = "1px";
        minutesSelect.style.textAlign = "left";
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
        component.parentNode.insertBefore(minutesSelect, component);
        toCombox(minutesSelect, component.form);

        Cyan.attach(dateInput, "input", onchange);
        dateInput.onchange = onchange;

        component.style.display = "none";
        component.showElement = dateInput;
        dateInput.active = function ()
        {
            if (component.active)
                component.active();
        };
        dateInput.showErrorTo = component;

        component.onenable = function ()
        {
            Cyan.$(hoursSelectName).onenable();
            Cyan.$(minutesSelectName).onenable();
            dateInput.disabled = false;
        };
        component.ondisable = function ()
        {
            Cyan.$(hoursSelectName).ondisable();
            Cyan.$(minutesSelectName).ondisable();
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
                Cyan.$(hoursSelectName).setValue(date.getHours());
                Cyan.$(minutesSelectName).setValue(date.getMinutes());
            }
            else
            {
                this.value = "";
                dateInput.value = "";
                Cyan.$(hoursSelectName).setValue("");
                Cyan.$(minutesSelectName).setValue("");
            }
        };
    };

    var makeYearMonthComponent = function (component)
    {
        makeYearMonthDayComponent0(component, true, true, false);
    };

    var makeMonthDayComponent = function (component)
    {
        makeYearMonthDayComponent0(component, false, true, true);
    };

    var makeYearMonthDayComponent = function (component)
    {
        makeYearMonthDayComponent0(component, true, true, true);
    };

    var makeYearMonthDayComponent0 = function (component, year, month, day)
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

        var format = getDateFormat(component);
        var date = Cyan.Date.parse(component.value, format);
        var size = Cyan.Elements.getComponentSize(component);
        var i;

        var onchange = function ()
        {
            var date = new Date();
            date.setFullYear(year ? Cyan.$(yearsSelectName).value : 0);
            date.setMonth(month ? Cyan.$(monthsSelectName).value : 0);
            date.setDate(day ? Cyan.$(daysSelectName).value : 1);
            date.setHours(0);
            date.setHours(0);
            date.setSeconds(0);
            date.setMilliseconds(0);

            component.value = Cyan.Date.format(date, format);

            if (component.onchange)
                component.onchange();
        };

        var yearsSelectName, monthsSelectName, daysSelectName;
        var initDays;
        if (day)
        {
            initDays = function ()
            {
                Cyan.Date.initDays(daysSelectName, monthsSelectName, yearsSelectName);
            };
        }

        if (year)
        {
            yearsSelectName = component.name + "$years";
            var yearsSelect = document.createElement("SELECT");
            yearsSelect.name = yearsSelectName;
            yearsSelect.className = component.className + " cyan-form-year";
            yearsSelect.style.width = "50px";
            yearsSelect.onchange = function ()
            {
                if (day)
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
            var yearValue;
            if (endYear > startYear)
            {
                for (i = startYear; i <= endYear; i++)
                {
                    yearValue = i.toString();
                    yearsSelect.options[yearsSelect.options.length] = new Option(yearValue, yearValue);
                }
            }
            else
            {
                for (i = startYear; i >= endYear; i--)
                {
                    yearValue = i.toString();
                    yearsSelect.options[yearsSelect.options.length] = new Option(yearValue, yearValue);
                }
            }
            if (date)
                yearsSelect.value = date.getFullYear().toString();
            component.parentNode.insertBefore(yearsSelect, component);
            toCombox(yearsSelect, component.form);
        }

        if (month)
        {
            monthsSelectName = component.name + "$months";
            var monthsSelect = document.createElement("SELECT");
            monthsSelect.name = monthsSelectName;
            monthsSelect.className = component.className + " cyan-form-month";
            monthsSelect.style.width = "30px";
            monthsSelect.onchange = function ()
            {
                if (day)
                    initDays();
                onchange();
            };
            for (i = 0; i < 12; i++)
                monthsSelect.options[monthsSelect.options.length] = new Option((i + 1).toString(), i.toString());
            if (date)
                monthsSelect.value = date.getMonth().toString();
            component.parentNode.insertBefore(monthsSelect, component);
            toCombox(monthsSelect, component.form);
        }

        if (day)
        {
            daysSelectName = component.name + "$days";
            var daysSelect = document.createElement("SELECT");
            daysSelect.name = daysSelectName;
            daysSelect.className = component.className + " cyan-form-days";
            daysSelect.style.width = "30px";
            daysSelect.onchange = onchange;
            component.parentNode.insertBefore(daysSelect, component);
            toCombox(daysSelect, component.form);

            initDays();
            Cyan.$(daysSelectName).setValue(date ? date.getDate() : "1");
        }

        component.style.display = "none";

        component.onenable = function ()
        {
            if (year)
                Cyan.$(yearsSelectName).onenable();
            if (month)
                Cyan.$(monthsSelectName).onenable();
            if (day)
                Cyan.$(daysSelectName).onenable();
        };
        component.ondisable = function ()
        {
            if (year)
                Cyan.$(yearsSelectName).ondisable();
            if (month)
                Cyan.$(monthsSelectName).ondisable();
            if (day)
                Cyan.$(daysSelectName).ondisable();
        };

        component.setValue = function (value)
        {
            this.value = value;
            var date = Cyan.Date.parse(value, format);
            if (year)
                Cyan.$(yearsSelectName).setValue(date.getFullYear());
            if (month)
                Cyan.$(monthsSelectName).setValue(date.getMonth());
            if (day)
                Cyan.$(daysSelectName).setValue(date.getDate());
        };
    };

    var createByteSizeInput = (function ()
    {
        var byteSizeUnits = ["B", "K", "M", "G", "T"];
        return function (input)
        {
            var unit = getByteUnit(input);
            var width = Cyan.toInt(Cyan.Elements.getCss(input, "width"));
            var comboxWidth = 35;

            var inputSize = width - comboxWidth - 5;

            var input2 = document.createElement("INPUT");
            input2.className = input.className + " cyan-form-bytesize";
            input2.style.cssText = input.style.cssText;
            input2.style.width = inputSize + "px";
            input2.title = input.title;
            input.parentNode.insertBefore(input2, input);

            var units = [];
            var b = false;
            for (var i = 0; i < byteSizeUnits.length; i++)
            {
                if (!b)
                    b = unit == byteSizeUnits[i];
                if (b)
                    units.push({value: units.length, text: byteSizeUnits[i]});
            }
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
                    var value = parseFloat(this.value);
                    for (var i = 0; i < unitValue; i++)
                        value *= 1024;
                    input.value = value;
                }
                else
                {
                    input.value = "";
                }
            });

            var unitSelectName = input.name + "$unit";
            var select = document.createElement("select");
            select.name = unitSelectName;
            select.className = input.className + " cyan-form-bytesize-unit";
            select.style.cssText = input.style.cssText;
            select.style.width = comboxWidth + "px";
            for (i = 0; i < units.length; i++)
            {
                var unit2 = units[i];
                select.options[i] = new Option(unit2.text, unit2.value);
            }
            select.value = unitValue;
            input.parentNode.insertBefore(select, input);
            select.onchange = function ()
            {
                if (input.value)
                {
                    unitValue = this.value;
                    var value = parseFloat(input.value);

                    for (var i = 0; i < unitValue; i++)
                        value /= 1024;
                    input2.value = value;
                }
                else
                {
                    input2.value = "";
                }
            };
            toCombox(select, input.form);

            input.onvaluechange = function ()
            {
                if (this.value)
                {
                    var value = parseFloat(this.value);
                    for (var i = 0; i < unitValue; i++)
                        value /= 1024;
                    input2.value = value;
                }
                else
                {
                    input2.value = "";
                }
            };
            input.ondisable = function ()
            {
                input2.disabled = true;
                Cyan.$(unitSelectName).ondisable();
            };
            input.onenable = function ()
            {
                input2.disabled = false;
                Cyan.$(unitSelectName).onenable();
            };

            input.style.display = "none";
        }
    })();

    var createTimeInput = (function ()
    {
        var timeUnits = ["second", "minute", "hour", "day"];

        return function (input, unit)
        {
            var width = Cyan.toInt(Cyan.Elements.getCss(input, "width"));
            var comboxWidth = 40;
            var inputSize = width - comboxWidth - 5;

            var input2 = document.createElement("INPUT");
            input2.className = input.className + " cyan-form-time";
            input2.style.cssText = input.style.cssText;
            input2.style.width = inputSize + "px";
            input2.title = input.title;
            input.parentNode.insertBefore(input2, input);

            var units = [];
            var b = false;
            for (var i = 0; i < timeUnits.length; i++)
            {
                var timeUnit = timeUnits[i];
                if (!b)
                    b = unit == timeUnit;
                if (b)
                    units.push({value: timeUnit, text: Cyan.Date[timeUnit]});
            }

            var unitValue = unit;
            if (input.value)
            {
                var timeValue = parseFloat(input.value);
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
                    var value = parseFloat(this.value);

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

                    input.value = value;
                }
                else
                {
                    input.value = "";
                }
            });

            var unitSelectName = input.name + "$unit";
            var select = document.createElement("select");
            select.name = unitSelectName;
            select.className = input.className + " cyan-form-time-unit";
            select.style.cssText = input.style.cssText;
            select.style.width = comboxWidth + "px";
            for (i = 0; i < units.length; i++)
            {
                var unit2 = units[i];
                select.options[i] = new Option(unit2.text, unit2.value);
            }
            select.value = unitValue;
            input.parentNode.insertBefore(select, input);
            select.onchange = function ()
            {
                if (input.value)
                {
                    unitValue = this.value;
                    var value = parseInt(input.value);

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
            toCombox(select, input.form);

            input.onvaluechange = function ()
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
            input.ondisable = function ()
            {
                input2.disabled = true;
                Cyan.$(unitSelectName).ondisable();
            };
            input.onenable = function ()
            {
                input2.disabled = false;
                Cyan.$(unitSelectName).onenable();
            };

            input.style.display = "none";
        }
    })();

    Cyan.Plain.Form = {
        renderField: function (component)
        {
            init();

            if (!component && this.nodeName)
                component = this;

            if (component)
            {
                component = Cyan.$(component);
                if (component && component.style.visibility != "hidden" && component.style.display != "none" &&
                        isRender(component))
                {
                    if (component.nodeName == "SELECT")
                    {
                        if (component.multiple)
                        {
                            toList(component, component.form);
                        }
                        else
                        {
                            toCombox(component, component.form);
                        }
                    }
                    else if (component.nodeName == "INPUT" && component.type == "text")
                    {
                        if (!getSelectable(component, makeSelectable))
                        {
                            if (!component.readOnly)
                            {
                                var format, date;
                                if (isYearMonth(component))
                                {
                                    if (isSupportInputType("month"))
                                    {
                                        component.setAttribute("type", "month");
                                    }
                                    else
                                    {
                                        makeYearMonthComponent(component);
                                    }
                                }
                                else if (isMonthDay(component))
                                {
                                    makeMonthDayComponent(component);
                                }
                                else if (isDateTime(component))
                                {
                                    var type = null;
                                    if (isSupportInputType("datetime"))
                                    {
                                        type = "datetime";
                                    }
                                    else if (isSupportInputType("datetime-local"))
                                    {
                                        type = "datetime-local";
                                    }

                                    if (type)
                                    {
                                        if (component.value)
                                        {
                                            format = getDateFormat(component);
                                            date = Cyan.Date.parse(component.value, format);
                                            component.value = Cyan.Date.format(date, "yyyy-MM-ddTmm:ss");
                                        }
                                        component.setAttribute("type", type);
                                    }
                                    else
                                    {
                                        makeDateTimeComponent(component);
                                    }
                                }
                                else if (isDate(component))
                                {
                                    format = component.getAttribute("format");
                                    if (isSupportInputType("date"))
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
                                            makeYearMonthDayComponent(component);
                                        }
                                        else
                                        {
                                            if (isEditable(component))
                                            {
                                                makeIcon(component, "cyan-selectable-down", function ()
                                                {
                                                    if (!component.disabled1)
                                                        showDateMenu(component, getDateFormat(this));
                                                });
                                            }
                                            else
                                            {
                                                component.readOnly = true;
                                                Cyan.attach(component, "click", function ()
                                                {
                                                    if (!this.disabled1)
                                                        showDateMenu(this, getDateFormat(this));
                                                });
                                                component.className += " cyan-form-field-date";
                                            }
                                        }
                                    }
                                }
                                else if (isByteSize(component))
                                {
                                    createByteSizeInput(component);
                                }
                                else if (isSecond(component))
                                {
                                    createTimeInput(component, "second");
                                }
                                else if (isMinute(component))
                                {
                                    createTimeInput(component, "minute");
                                }
                                else if (isHour(component))
                                {
                                    createTimeInput(component, "hour");
                                }
                            }
                        }
                    }
                    else if (component.nodeName == "TEXTAREA")
                    {
                        //if (isHtmlEditor(component))
                        //{
                        //    createHtmlEditor(component);
                        //}
                    }
                    else if (component.nodeName == "FORM")
                    {
                        Cyan.each(Cyan.clone(component), function ()
                        {
                            Cyan.Plain.Form.renderField(this);
                        });
                    }
                }
            }
            else
            {
                Cyan.$$("FORM").each(function ()
                {
                    Cyan.Plain.Form.renderField(this);
                });
            }
        },
        reset: function (form)
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
        }
    };

})();

Cyan.renderField = Cyan.Plain.Form.renderField;
Cyan.reset = Cyan.Plain.Form.reset;
Cyan.Elements.prototype.reset = function ()
{
    this.each(function ()
    {
        if (this.nodeName == "FORM")
            Cyan.Plain.Form.reset(this);
    });
};