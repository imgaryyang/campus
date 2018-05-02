Cyan.importJs("render.js");

Cyan.Valmiki = {
    requireComponents: [],
    selectData: function (component, selector, callback)
    {
        component = Cyan.$(component);

        var value = component.value;
        if (value)
        {
            try
            {
                value = eval("(" + value + ")");
            }
            catch (e)
            {
                value = null;
            }
        }
        else
        {
            value = null;
        }
        selector(value, function (result)
        {
            if (result)
            {
                component.value = "(" + Cyan.Ajax.toJson(result) + ")";
                var text = Cyan.$(component.name + "$text");
                if (text)
                    text.value = Cyan.isArray(result) ? result.join(",") : result.toString();

                if (callback)
                    callback();

                Cyan.Valmiki.dataChange();
            }
        });
    },
    getComponent: function (name, formName)
    {
        name = "." + name;

        if (formName)
            formName += ".";

        return Cyan.each(Cyan.$$("form")[0], function ()
        {
            var id = this.name || this.id;
            if (id && (!formName || Cyan.startsWith(id, formName)))
            {
                if (formName)
                {
                    id = id.substring(formName.length);

                    if (Cyan.endsWith(id, name))
                        return this;
                }

                if ((formName != "" || id.split(".").length <= 2) && Cyan.endsWith(id, name))
                    return this;
            }
        });
    },
    getComponents: function (name, formName)
    {
        name = "." + name;

        if (formName)
            formName += ".";

        return Cyan.search(Cyan.$$("form")[0], function ()
        {
            var id = this.name || this.id;
            if (id && (!formName || Cyan.startsWith(id, formName)))
            {
                if (formName)
                    id = id.substring(formName.length);

                if (Cyan.endsWith(id, name))
                    return this;
            }
        });
    },
    getValue: function (name, formName)
    {
        name = "." + name;

        if (formName)
            formName += ".";

        var values = null;

        var result = Cyan.each(Cyan.$$("form")[0], function ()
        {
            var id = this.name || this.id;
            if (id && (!formName || Cyan.startsWith(id, formName)))
            {
                if (formName)
                    id = id.substring(formName.length);

                if (Cyan.endsWith(id, name))
                {
                    if (this.type == "checkbox")
                    {
                        if (values == null)
                            values = [];
                        if (this.checked)
                            values.push(this.value);
                    }
                    else if (this.type == "radio")
                    {
                        if (this.checked)
                            return this.value;
                    }
                    else
                    {
                        return this.value;
                    }
                }
            }
        });

        if (result == null)
            result = values;

        return result;
    },
    setValue: function (name, value, formName)
    {
        name = "." + name;

        if (formName)
            formName += ".";

        var values = null;

        var result = Cyan.each(Cyan.$$("form")[0], function ()
        {
            var id = this.name || this.id;
            if (id && (!formName || Cyan.startsWith(id, formName)))
            {
                if (formName)
                    id = id.substring(formName.length);

                if (Cyan.endsWith(id, name))
                {
                    if (this.type == "checkbox")
                    {
                        this.checked = Cyan.isArray(value) && value.contains(this.value) || this.value == value;
                    }
                    else if (this.type == "radio")
                    {
                        if (this.value == value)
                            this.checked = true;
                    }
                    else
                    {
                        this.value = value;
                    }
                }
            }
        });
    },
    getRequireMessage: function (showName)
    {
        return showName;
    },
    checkAll: function (action, self)
    {
        var components = Cyan.Valmiki.requireComponents;
        for (var i = 0; i < components.length; i++)
        {
            var component = components[i];
            if (!self && !component.action ||
                    action && component.action && Cyan.Array.contains(component.action, action))
            {
                if (component.condition)
                {
                    if (!(component instanceof Function))
                        component.condition = Cyan.innerFunction(component.condition);

                    if (!component.condition())
                        continue;
                }

                var elements = Cyan.$$(document.getElementsByName(component.name));
                if (elements.length)
                {
                    var values = elements[0].type == "radio" || elements[0].type == "checkbox" ?
                            elements.checkedValues() : elements.values();
                    if (!values.length || values.length == 1 && (!values[0] || values[0] == component.inputTip))
                    {
                        var remind = component.remind;
                        if (!remind || remind == "false")
                            remind = Cyan.Valmiki.getRequireMessage(component.showName);
                        Cyan.message(remind);

                        return false;
                    }
                }
            }
        }

        return true;
    },
    getPage: function (element)
    {
        while (true)
        {
            if (element.className == "valmiki_page")
                return element;
            else if (element == document.body)
                return null;

            element = element.parentNode;
        }
    },
    textAreaShowBaksText: "show baks",
    initTextArea: function (name, formName, fullName, bak)
    {
        setTimeout(function ()
        {
            Cyan.Valmiki.initTextArea0(name, formName, fullName, bak);
        }, 100);
    },
    initTextArea0: function (name, formName, fullName, bak)
    {
        if (!formName)
            formName = "";

        var textArea = Cyan.$(name);

        var left = Cyan.toInt(textArea.style.left);
        var top = Cyan.toInt(textArea.style.top);
        var width = Cyan.toInt(textArea.style.width);
        var height = Cyan.toInt(textArea.style.height);

        if (bak)
        {
            var button = Cyan.Elements.createButton(Cyan.Valmiki.textAreaShowBaksText);

            button.style.position = "absolute";
            button.style.left = left + width + 5 + "px";
            button.style.top = top + 3 + "px";
            button.style.width = "80px";
            button.style.padding = "0";
            button.style.lineHeight = "18px";
            button.style.fontSize = "12px";
            button.onclick = function ()
            {
                Cyan.Valmiki.textAreaShowBaks(formName, name);
            };

            textArea.parentNode.appendChild(button);
        }

        if (textArea.nodeName == "DIV")
        {
            Cyan.Valmiki.resize(textArea);
        }
        else
        {
            var resize = function ()
            {
                Cyan.Valmiki.resize(div, textDiv);
            };

            var div = document.createElement("div");
            div.id = name + "$div";
            div.style.cssText = textArea.style.cssText;
            div.style.cursor = "text";
            div.style.borderStyle = "solid";
            div.height0 = height;

            div.borderColor = textArea.style.borderColor;
            if (textArea.disabled)
                div.style.borderColor = "gray";

            var textDiv = document.createElement("div");
            textDiv.id = name + "$textdiv";
            textDiv.style.color = textArea.style.color;
            textDiv.style.fontFamily = textArea.style.fontFamily;
            textDiv.style.fontSize = textArea.style.fontSize;
            textDiv.style.fontWeight = textArea.style.fontWeight;
            textDiv.style.fontStyle = textArea.style.fontStyle;
            textDiv.style.background = textArea.style.background;
            textDiv.style.textDecoration = textArea.style.textDecoration;
            div.appendChild(textDiv);

            Cyan.Elements.setText(textDiv, textArea.value);

            textArea.parentNode.insertBefore(div, textArea);
            textArea.style.display = "none";
            resize();

            var showing = false;
            div.onclick = function ()
            {
                if (!textArea.disabled)
                {
                    textArea.style.left = div.style.left;
                    textArea.style.top = div.style.top;
                    textArea.style.width = div.style.width;
                    textArea.style.height = div.style.height;
                    textArea.style.display = "";
                    showing = true;
                    try
                    {
                        textArea.focus();
                    }
                    catch (e)
                    {
                    }
                    div.style.display = "none";
                }
            };
            Cyan.attach(textArea, "blur", function ()
            {
                textArea.style.left = "-3000px";
                textArea.style.top = "-3000px";
                div.style.display = "";

                Cyan.Elements.setText(textDiv, textArea.value);
                resize();
            });
            Cyan.attach(textArea, "focus", function ()
            {
                if (showing)
                {
                    showing = false;
                }
                else
                {
                    textArea.style.left = div.style.left;
                    textArea.style.top = div.style.top;
                    textArea.style.width = div.style.width;
                    textArea.style.height = div.style.height;
                    textArea.style.display = "";
                    div.style.display = "none";
                }
            });
            textArea.onvaluechange = function ()
            {
                Cyan.Elements.setText(textDiv, textArea.value);
                resize();
            };
            textArea.ondisable = function ()
            {
                this.disabled = true;
                div.style.borderColor = "gray";
            };
            textArea.onenable = function ()
            {
                this.disabled = false;
                div.style.borderColor = div.borderColor;
            };
        }
    },
    textAreaShowBaks: function (formName, name)
    {
    },
    dataChange: function ()
    {
        var spans = document.getElementsByTagName("span");
        var n = spans.length;
        for (var i = 0; i < n; i++)
        {
            var span = spans[i];
            var s = span.getAttribute("textEl");
            if (s)
            {
                var index, start = 0;
                while ((index = s.indexOf("${", start)) >= 0)
                {
                    var index2 = s.indexOf("}", index + 2);
                    if (index2 < 0)
                        break;

                    var v = s.substring(index + 2, index2);

                    var component = Cyan.Valmiki.getComponent(v);
                    if (component)
                    {
                        v = component.value;
                    }
                    else
                    {
                        s = null;
                        break;
                    }

                    s = s.substring(0, index) + v + s.substring(index2 + 1);
                }

                if (s != null)
                    span.innerHTML = s;
            }
        }
    },
    resize: function (component, realComponent)
    {
        var oldHeight = Cyan.toInt(component.style.height);

        if (!component.height0)
        {
            component.height0 = oldHeight;
        }

        if (!realComponent)
            realComponent = component.childNodes[0];

        var height;
        if (realComponent && realComponent != component && realComponent.style)
        {
            height = Cyan.Elements.getComponentSize(realComponent).height;
        }
        else
        {
            height = component.scrollHeight;
        }

        var newHeight = height;

        if (newHeight < component.height0)
            newHeight = component.height0;

        if (newHeight == oldHeight)
            return;

        var bottom0 = Cyan.toInt(component.style.top) + oldHeight;
        var diff = newHeight - oldHeight;

        if (!diff)
            return;

        var page = Cyan.Valmiki.getPage(component);
        Cyan.$$(page).eachElement(function ()
        {
            if (this.style && this.style.position == "absolute" && this.style.top)
            {
                var top = Cyan.toInt(this.style.top);
                if (top >= bottom0)
                {
                    this.style.top = (top + diff) + "px";
                }
                else
                {
                    var height = Cyan.toInt(this.style.height);
                    var bottom = top + height;
                    if (bottom >= bottom0)
                    {
                        if (height + diff > 0)
                            this.style.height = (height + diff) + "px";
                    }
                }
            }
        });

        page.style.height = (Cyan.toInt(page.style.height) + diff) + "px";
    }
};

window.Valmiki = Cyan.Valmiki;
if (!window._$)
    window._$ = Cyan.Valmiki.getComponent;

Cyan.importLanguage("valmiki");
