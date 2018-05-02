Cyan.Extjs.WidgetItem = function (component)
{
    Cyan.Extjs.WidgetItem.superclass.constructor.call(this, component, null);
    this.component = component;
};

Ext.extend(Cyan.Extjs.WidgetItem, Ext.menu.BaseItem, {
    canActivate: true,
    onRender: function (container, position)
    {
        this.component.render(container);
        this.el = this.component.getEl();
    },
    activate: function ()
    {
        if (this.disabled)
        {
            return false;
        }
        this.component.focus();
        this.fireEvent("activate", this);
        return true;
    },
    deactivate: function ()
    {
        this.fireEvent("deactivate", this);
    },
    disable: function ()
    {
        this.component.disable();
        Cyan.Extjs.WidgetItem.superclass.disable.call(this);
    },
    enable: function ()
    {
        this.component.enable();
        Cyan.Extjs.WidgetItem.superclass.enable.call(this);
    }
});

Cyan.Extjs.WidgetMenu = function (widget, config)
{
    var menu = this;
    widget.onselect = function ()
    {
        if (!widget.loading)
        {
            menu.hide();
            if (config && config.onselect)
                config.onselect(widget);

            if (widget.clearSelections)
                widget.clearSelections();
        }
    };
    widget.oncheck = function ()
    {
        if (config && config.onselect)
            config.onselect(widget);

        if (widget.clearSelections)
            widget.clearSelections();
    };

    var component;
    if (widget.getExtComponent)
    {
        widget.init(null);
        component = widget.getExtComponent();
        if (!component.width)
            component.width = (config ? config.width : null) || widget.width || 240;
        if (!component.height)
            component.height = (config ? config.height : null) || widget.height || 280;

        Cyan.Class.overwrite(component, "afterRender", function ()
        {
            this.inherited();
            if (widget.search && widget.searchable)
            {
                var dom = this.getEl().dom;

                var div = document.createElement("DIV");
                dom.parentNode.insertBefore(div, dom);

                var input = document.createElement("INPUT");
                div.appendChild(input);
                if (config && config.clearable)
                    input.style.width = (component.width - 26) + "px";
                else
                    input.style.width = (component.width - 4) + "px";
                input.className = "cyan-search";

                if (config && config.clearable)
                {
                    var clearButton = document.createElement("BUTTON");
                    clearButton.className = "cyan-clear";
                    clearButton.onclick = function ()
                    {
                        if (widget.clearSelectedItems)
                            widget.clearSelectedItems();
                        else if (widget.clearSelections)
                            widget.clearSelections();

                        if (config && config.onselect)
                            config.onselect(widget);
                    };
                    div.appendChild(clearButton);
                }

                Cyan.attach(input, "click", function (e)
                {
                    e.stop();
                });

                Cyan.attach(dom.parentNode, "mousemove", function ()
                {
                    try
                    {
                        input.focus();
                    }
                    catch (e)
                    {
                    }
                });

                var lastSearchText;

                Cyan.attach(input, "keyup", function ()
                {
                    var text = this.value;
                    var input = this;
                    if (text == "")
                    {
                        widget.search(null);
                    }
                    else
                    {
                        window.setTimeout(function ()
                        {
                            if (text == input.value)
                            {
                                if (lastSearchText == text)
                                    return;
                                lastSearchText = text;

                                widget.search(text);
                            }
                        }, 1000);
                    }
                });
            }
        });
    }
    else
    {
        component = new Ext.Panel({border: false});
        widget.clearable = config && config.clearable;
        component.on("afterrender", function ()
        {
            var div = component.el.dom;
            widget.init(div);
            window.setTimeout(function ()
            {
                if (Cyan.navigator.name == "IE7")
                {
                    while (!div.style.width && div.nodeName != "BODY")
                        div = div.parentNode;

                    if (div.nodeName != "BODY")
                    {
                        div.style.width = (Cyan.$(divId).clientWidth) + "px";
                    }
                }
            });
        });
    }

    component.on("render", function ()
    {
        component.getEl().swallowEvent("click");
    });
    this.widget = component;
    Cyan.Extjs.WidgetMenu.superclass.constructor.call(this, null);
    this.plain = true;
    var di = new Cyan.Extjs.WidgetItem(component, config);
    this.add(di);

    this.on("hide", function ()
    {
        var maskFrame = Cyan.Extjs.WidgetMenu.maskFrame;
        if (maskFrame)
        {
            maskFrame.style.display = "none";
        }
    });
};
Ext.extend(Cyan.Extjs.WidgetMenu, Ext.menu.Menu, {
    showAt: function (postion)
    {
        Cyan.Extjs.WidgetMenu.superclass.showAt.call(this, postion);

        var maskFrame = Cyan.Extjs.WidgetMenu.maskFrame;
        if (!maskFrame)
        {
            Cyan.Extjs.WidgetMenu.maskFrame = maskFrame = document.createElement("iframe");
            maskFrame.style.zIndex = 400;
            maskFrame.style.position = "absolute";
            maskFrame.style.borderWidth = "0";
            maskFrame.frameSize = 0;
            maskFrame.style.filter = "alpha(opacity=0)";
            maskFrame.style.opacity = "0";
            document.body.appendChild(maskFrame);
        }
        else
        {
            maskFrame.style.display = "";
        }

        maskFrame.style.left = postion[0] + "px";
        maskFrame.style.top = postion[1] + "px";
        maskFrame.style.width = this.getWidth() + "px";
        maskFrame.style.height = this.getHeight() + "px";
    }
});

Cyan.Extjs.WidgetMenu.generateDivId = function ()
{
    var id;
    if (!Cyan.Extjs.WidgetMenu.divIds)
        Cyan.Extjs.WidgetMenu.divIds = {};
    while (Cyan.Extjs.WidgetMenu.divIds[id = "cyan$ext$menu$panel$" + Math.random().toString().substring(2)]);
    Cyan.Extjs.WidgetMenu.divIds[id] = true;
    return id;
};

Cyan.Extjs.WidgetField = Ext.extend(Ext.form.TriggerField,
        {
            enableKeyEvents: true,
            triggerClass: 'x-form-arrow-trigger',
            defaultAutoCreate: {tag: "input", type: "text", size: "10", readOnly: "true"},

            initComponent: function ()
            {
                if (!this.editable)
                {
                    this.hiddenName = this.name;
                    this.name = "fieldName_text_" + Math.random().toString().substring(2);
                }
                this.id = this.name;
                Cyan.Extjs.WidgetField.superclass.initComponent.call(this);
                if (this.hiddenName)
                {
                    this.addListener("keydown", function (field, event)
                    {
                        var key = event.getKey();
                        if (key == 46 || key == 8)
                        {
                            if (field.hiddenField)
                                field.hiddenField.value = "";
                            field.setValue("");

                            if (key == 8)
                                event.stopEvent();
                        }
                    });
                }
            },
            onRender: function (ct, position)
            {
                Cyan.Extjs.WidgetField.superclass.onRender.call(this, ct, position);
                if (this.hiddenName)
                {
                    if (this.multiple)
                    {
                        this.hiddenField =
                                this.el.insertSibling({
                                    tag: 'select', multiple: 'true', name: this.hiddenName, size: "3",
                                    style: "position:absolute;visibility:hidden;width:0;height:0"
                                }, 'before', true);
                    }
                    else
                    {
                        this.hiddenField =
                                this.el.insertSibling({tag: 'input', type: 'hidden', name: this.hiddenName}, 'before',
                                        true);
                    }

                    var item = this;
                    this.hiddenField.setText = function (text)
                    {
                        item.setValue(text);
                    };

                    try
                    {
                        this.hiddenField.onchange = this.onchange;
                    }
                    catch (e)
                    {
                    }
                    this.hiddenField.value = this.value;
                    if (this.text)
                        this.setValue(this.text);
                }
            },
            onTriggerClick: function ()
            {
                if (!this.menu)
                {
                    var component = this;
                    this.menu = new Cyan.Extjs.WidgetMenu(this.selectable, {
                        clearable: true,
                        onselect: function (widget)
                        {
                            if (widget.selectedItems)
                            {
                                var items = widget.selectedItems();
                                component.setValue(Cyan.get(items, "text").join(","));
                                if (component.hiddenField)
                                {
                                    if (component.multiple)
                                    {
                                        var select = component.hiddenField;
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
                                    }
                                    else
                                    {
                                        component.hiddenField.value = Cyan.get(items, "value").join(",");
                                    }

                                    if (component.hiddenField.onchange)
                                        component.hiddenField.onchange();
                                }
                            }
                        }
                    });
                }
                this.menu.show(this.el, "tl-bl?");
            },
            beforeBlur: function ()
            {

            }
        });

if (Cyan.Menu)
{
    Cyan.Menu.prototype.getExtMenu = function ()
    {
        if (!this.extMenu)
            this.extMenu = this.toExtMenu(this.items);

        return this.extMenu;
    };

    Cyan.Menu.prototype.toExtMenu = function (obj)
    {
        var items;
        if (obj instanceof Cyan.Menu)
        {
            return obj.getExtMenu();
        }
        else if (Cyan.isString(obj))
        {

        }
        else if (obj instanceof Cyan.Widget)
        {
            return new Cyan.Extjs.WidgetMenu(obj, {
                width: this.width,
                height: this.height,
                onselect: this.onselect
            });
        }
        else if (Cyan.isArray(obj))
        {
            var n = obj.length;
            items = new Array(n);
            for (var i = 0; i < n; i++)
                items[i] = this.toExtMenuItem(obj[i]);
        }
        else
        {
            items = [this.toExtMenuItem(obj)];
        }

        if (items)
        {
            var menu = new Ext.menu.Menu({items: items});
            menu.focus = function ()
            {
            };
            return menu;
        }
    };

    Cyan.Menu.prototype.toExtMenuItem = function (item)
    {
        if (Cyan.isString(item))
        {
            if (item == "_")
                return new Ext.menu.Separator();
            else
                return new Ext.menu.TextItem({text: item});
        }

        var extItem = {};

        var text = item.text;

        if (item.style || item.color || item.bold)
        {
            var style = "";

            if (item.style)
            {
                style = item.style;
            }
            else
            {
                if (item.color)
                    style = "color:" + item.color;

                if (item.bold)
                {
                    if (style)
                        style += ";";
                    style += "font-weight:bold";
                }
            }

            text = "<span style='" + style + "'>" + text + "</span>";
        }

        extItem.text = text;
        extItem.handler = Cyan.Menu.getAction(item);
        if (item.icon)
            extItem.icon = item.icon;
        if (item.className)
            extItem.itemCls = item.className;
        if (item.group)
            extItem.group = item.group;
        if (item.checked)
            extItem.checked = item.checked;

        if (item.menu)
            extItem.menu = this.toExtMenu(item.menu);

        if (item.checked != null)
            return new Ext.menu.CheckItem(extItem);

        return new Ext.menu.Item(extItem);
    };

    Cyan.Menu.prototype.showAt = function (x, y)
    {
        this.getExtMenu().showAt([x, y]);
    };

    Cyan.Menu.prototype.hide = function ()
    {
        this.getExtMenu().hide();
    };

    Cyan.Menu.prototype.init = function ()
    {
        if (this.items instanceof Cyan.Widget)
            this.items.autoRender = false;
    };

    Cyan.Menu.prototype.getElement = function ()
    {
        var el = this.getExtMenu().getEl();
        return el ? el.dom : null;
    };
}