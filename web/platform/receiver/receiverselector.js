Cyan.importJs("widgets/targetlist.js");

System.ReceiverSelector = Cyan.Class.extend(function (id, root, selected, name)
{
    this.id = id;
    this.root = root;
    this.selected = selected;
    this.name = name;
    this.selectable = [];
    this.selected = [];
}, Cyan.Widget);

System.ReceiverSelector.prototype.create = function ()
{
    var selector = this;
    this.itemSelector = new Cyan.ItemSelector(this.id,
            {
                init: function (div)
                {
                    selector.init(div);
                },
                selectedItems: function ()
                {
                    var indexes = selector.receiverList.selectedIndexes();
                    var items = [];
                    var names = null;
                    for (var i = 0; i < indexes.length; i++)
                    {
                        var index = indexes[i];
                        var receiver = selector.selectable[index];
                        if (receiver.valid)
                        {
                            items.push({value: receiver.value, text: receiver.name});
                            if (!selector.selected.searchFirst(function ()
                                    {
                                        return this.value == receiver.value;
                                    }))
                                selector.selected.push(receiver);
                        }
                        else
                        {
                            if (names == null)
                                names = [];
                            names.push(receiver.name);
                        }
                    }

                    if (names)
                    {
                        Cyan.tip(selector.getEmptyMessage(names));
                    }
                    return items;
                },
                allItems: function ()
                {
                    var items = [];
                    var names = null;
                    for (var i = 0; i < selector.selectable.length; i++)
                    {
                        var receiver = selector.selectable[i];
                        if (receiver.valid)
                        {
                            items.push({value: receiver.value, text: receiver.name});
                            if (!selector.selected.searchFirst(function ()
                                    {
                                        return selector.value == receiver.value;
                                    }))
                                selector.selected.push(receiver);
                        }
                        else
                        {
                            if (names == null)
                                names = [];
                            names.push(receiver.name);
                        }
                    }

                    if (names)
                    {
                        Cyan.tip(selector.getEmptyMessage(names));
                    }
                    return items;
                },
                addItem: function (value, text)
                {
                    selector.selected.removeCase(function ()
                    {
                        return this.value == value;
                    });
                }
            }, this.selected);
    this.itemSelector.name = this.name;
    this.itemSelector.init(this.id);
};

System.ReceiverSelector.prototype.init = function (div)
{
    var receiverSelector = this;
    var component = this.component = window[this.id];
    this.typeName = component.typeName;
    component.getSelector = function ()
    {
        return receiverSelector;
    };

    var treeDiv = document.createElement("DIV");
    treeDiv.className = "grouptree";

    div.appendChild(treeDiv);

    this.tree = new Cyan.Tree(null, this.root);
    this.tree.rootVisible = false;
    this.tree.lazyLoadNode = function (id, callback)
    {
        component.loadChildren(id, {wait: false, check: false, callback: callback});
    };
    this.tree.searchByText = function (text, callback)
    {
        component.queryGroup(text, {wait: false, check: false, callback: callback});
    };
    this.tree.autoRender = true;
    this.tree.enableSearch();

    var receiversDiv = document.createElement("DIV");
    receiversDiv.className = "receivers";
    div.appendChild(receiversDiv);

    this.receiverList = this.itemSelector.createListComponent("from$" +
    this.name, [], receiversDiv, this.itemSelector.from.ondblclick);

    this.tree.onclick = function ()
    {
        component.loadReceivers(this.id, {
            wait: false, check: false, callback: function (receivers)
            {
                receiverSelector.clearSelectable();
                if (receivers)
                {
                    var n = receivers.length;
                    for (var i = 0; i < n; i++)
                    {
                        var receiver = receivers[i];
                        var s = receiver.name;
                        if (!receiver.valid)
                            s = "<span class='receiver_invalid'>" + s + "</span>";

                        receiverSelector.addSelectable(receiver, s);
                    }
                }
            }
        });
    };
    this.tree.init(treeDiv);
};

System.ReceiverSelector.prototype.clearSelectable = function ()
{
    this.receiverList.clearAll();
    this.selectable.length = 0;
};

System.ReceiverSelector.prototype.addSelectable = function (receiver, text)
{
    this.selectable.push(receiver);
    this.receiverList.addItem(receiver.value, text);
};

System.ReceiverSelector.prototype.addSelected = function (receiver)
{
    if (!this.selected.searchFirst(function ()
            {
                return this.value == receiver.value;
            }))
    {
        this.selected.push(receiver);
        this.itemSelector.addItem(receiver.value, receiver.name);
    }
};

System.ReceiverSelector.prototype.clearSelected = function (receiver)
{
    this.selected.length = 0;
    this.itemSelector.removeAll();
};

System.ReceiverSelector.prototype.bindQuery = function (component)
{
    component = Cyan.$(component);
    var selector = this;
    var lastText;
    Cyan.$$(component).onkeyup(function ()
    {
        var s = component.value;
        setTimeout(function ()
        {
            if (component.value == s && s != lastText)
            {
                lastText = s;
                selector.query(s, component);
            }
        }, 300);
    });
};

System.ReceiverSelector.prototype.query = function (text, component)
{
    if (text)
    {
        var selector = this;
        this.component.queryReceiver(text, {
            wait: false,
            check: false,
            callback: function (receivers)
            {
                if (component && component.value != text)
                    return;

                selector.clearSelectable();
                if (receivers)
                {
                    var n = receivers.length;
                    if (n)
                    {
                        for (var i = 0; i < n; i++)
                        {
                            var receiver = receivers[i];
                            var s = receiver.name;
                            if (receiver.remark)
                                s += "<span class='receiver_remark'>(" + receiver.remark + ")</span>";

                            if (!receiver.valid)
                                s = "<span class='receiver_invalid'>" + s + "</span>";

                            selector.addSelectable(receiver, s);
                        }
                    }
                    else
                    {
                        selector.tree.search(text);
                    }
                }
            }
        });
    }
    else
    {
        this.receiverList.clearAll();
        this.tree.search(text);
    }
};

System.ReceiverSelector.prototype.getEmptyMessage = function (names)
{
    return names.join(",") + "没有绑定" + this.typeName;
};

System.ReceiverInput = function (type)
{
    this.items = [];
    this.type = type;
};

System.ReceiverInput.prototype.initReceiver = function (receiver)
{
    var s = receiver.value;
    var index = s.indexOf("<");
    if (index > 0)
    {
        var valid = false;
        if (s.endsWith(">"))
        {
            var name = s.substring(0, index);
            s = s.substring(index + 1, s.length - 1);

            receiver.simpleValue = s;
            if (!this.isValid || s.endsWith("@local") || this.isValid(s))
            {
                if (name)
                {
                    var text = name;
                    if (Cyan.startsWith(name, "'"))
                    {
                        text = text.substring(1);
                        name = "\"" + name.substring(1);
                    }
                    else if (Cyan.startsWith(name, "\""))
                    {
                        text = text.substring(1);
                    }
                    else
                    {
                        name = "\"" + name;
                    }

                    if (Cyan.endsWith(name, "\'"))
                    {
                        text = text.substring(0, text.length - 1);
                        name = name.substring(0, name.length - 1) + "\"";
                    }
                    else if (Cyan.endsWith(name, "\""))
                    {
                        text = text.substring(0, text.length - 1);
                    }
                    else if (!name.endsWith("\""))
                    {
                        name += "\"";
                    }

                    receiver.value = name + "<" + s + ">";
                    receiver.text = text;
                }

                valid = true;
                if (s.endsWith("@local") && receiver.editable == null)
                    receiver.editable = false;
            }
        }
        receiver.valid = valid;
    }
    else
    {
        receiver.simpleValue = s;
        receiver.valid = !this.isValid || s.endsWith("@local") || this.isValid(s);
    }

    if (receiver.editable == null)
        receiver.editable = true;
};

System.ReceiverInput.instances = {};

System.ReceiverInput.prototype.getReceiverList = function ()
{
    var result = "";
    for (var i = 0; i < this.items.length; i++)
    {
        if (i > 0)
            result += ";";
        result += this.items[i].value;
    }

    return result;
};

System.ReceiverInput.prototype.create = function (input)
{
    this.input = input;
    this.readOnly = input.readOnly;

    System.ReceiverInput.instances[input.name] = this;

    input.style.display = "none";
    this.div = document.createElement("DIV");
    this.div.className = "receiverlist";


    var text = document.createElement("INPUT");
    text.readOnly = this.readOnly;
    this.div.appendChild(text);

    input.parentNode.insertBefore(this.div, input);
    this.div.title = input.title;

    this.hiddenText = document.createElement("INPUT");
    this.hiddenText.className = "hidden";
    this.div.appendChild(this.hiddenText);

    var component = this;
    Cyan.attach(this.div, "click", function (event)
    {
        component.focus();
    });
    if (this.tip)
    {
        Cyan.attach(this.tip, "click", function (event)
        {
            component.focus();
        });
    }

    if (!this.readOnly)
    {
        this.initText(text);
        Cyan.attach(this.hiddenText, "keyup", function (event)
        {
            if (event.keyCode == 37)
            {
                component.goPrevious();
            }
            else if (event.keyCode == 39)
            {
                component.goNext();
            }
            else if (event.keyCode == 8 || event.keyCode == 46)
            {
                component.removeReceiver();
            }
            else if (event.keyCode == 35)
            {
                component.goLast();
            }
            else if (event.keyCode == 36)
            {
                component.goFirst();
            }
            event.stop();
        });
    }

    this.initReceiverList();

    if (!this.input.value)
    {
        this.tip = document.createElement("SPAN");
        this.tip.innerHTML = this.input.title;
        this.tip.className = "receiverlist_tip";
        this.div.appendChild(this.tip);
    }
};

System.ReceiverInput.prototype.initReceiverList = function ()
{
    if (this.input.value)
    {
        var ss = this.input.value.replaceAll(",", ";").split(";");
        for (var i = 0; i < ss.length; i++)
        {
            var value = ss[i];
            var text = value;
            var index = text.indexOf("<");
            if (index >= 0 && text.endsWith(">"))
            {
                text = text.substring(0, index);

                if (text.startsWith("\"") || text.startsWith("\'"))
                    text = text.substring(1);
                if (text.endsWith("\"") || text.endsWith("\'"))
                    text = text.substring(0, text.length - 1);
            }

            this.addReceiver({value: value, name: text}, null, true);
        }

        this.input.value = this.getReceiverList();
    }
};

System.ReceiverInput.prototype.clear = function ()
{
    this.items.length = 0;
    this.input.value = "";

    while (this.div.childNodes.length > 2)
        this.div.removeChild(this.div.firstChild);
};

System.ReceiverInput.prototype.focus = function ()
{
    if (this.tip)
    {
        this.div.removeChild(this.tip);
        this.tip = null;
    }
    var childNodes = this.div.childNodes;
    childNodes[childNodes.length - 2].focus();
};

System.ReceiverInput.prototype.getErrorReceiverList = function ()
{
    var errorReceiverList;
    for (var i = 0; i < this.items.length; i++)
    {
        if (!this.items[i].valid)
        {
            if (!errorReceiverList)
                errorReceiverList = [];
            errorReceiverList.push(this.items[i].value);
        }
    }
    return errorReceiverList;
};

System.ReceiverInput.prototype.queryReceiver = function ($0)
{
    var url = "/receiver/query?word=${$0}&type=" + this.type;
    if (this.deptId)
        url += "&deptId=" + this.deptId;
    if (this.appId)
        url += "&appId=" + this.appId;
    Cyan.Arachne.doGet(url, arguments, 3);
};

System.ReceiverInput.queryError = function ()
{
    var list = System.ReceiverInput.getTargetList();
    if (list.isVisible())
        list.hide();
};

System.ReceiverInput.prototype.initText = function (text)
{
    var component = text.component = this;
    Cyan.attach(text, "focus", System.ReceiverInput.textFocus);
    Cyan.attach(text, "blur", System.ReceiverInput.textBlur);
    Cyan.attach(text, "click", System.ReceiverInput.textClick);
    Cyan.attach(text, "keydown", System.ReceiverInput.textKeydown);
    Cyan.attach(text, "keyup", System.ReceiverInput.textKeyup);
    Cyan.attach(text, "input", System.ReceiverInput.textInput);

    var isValid = function ()
    {
        return this.valid;
    };
    var lastText;
    System.ReceiverInput.getTargetList().bind(text, function (s, callback)
    {
        setTimeout(function ()
        {
            if (text.value == s && s != lastText)
            {
                lastText = s;
                component.queryReceiver(s, {
                    form: null, callback: function (result)
                    {
                        if (s == text.value)
                        {
                            if (result)
                                result = result.search(isValid);
                            else
                                result = [];
                            callback(result);
                        }
                    }, error: System.ReceiverInput.queryError
                });
            }
        }, 300);
    });
};

System.ReceiverInput.textFocus = function ()
{
    this.component.clearSelected();
};

System.ReceiverInput.textBlur = function ()
{
    var list = System.ReceiverInput.getTargetList();
    if (list.bindComponent != this || !list.isVisible())
        this.component.finishText(this);
};

System.ReceiverInput.textClick = function (event)
{
    event.stop();
};

System.ReceiverInput.textKeydown = function (event)
{
    if ((event.keyCode == 59 || event.keyCode == 44 || event.keyCode == 188 || event.keyCode == 186) && !event.shiftKey)
    {
        this.component.finishText(this);
        event.stop();
    }
    else if (event.keyCode == 37)
    {
        if (Cyan.Event.getSelectionStart(event.srcElement) == 0)
            this.component.goPrevious(this);
    }
    else if (event.keyCode == 39)
    {
        if (Cyan.Event.getSelectionStart(event.srcElement) == this.value.length)
            this.component.goNext(this);
    }
    else if (event.keyCode == 8)
    {
        if (Cyan.Event.getSelectionStart(event.srcElement) == 0)
        {
            this.component.removeReceiver(this);
            event.stop();
        }
    }
    else if (event.keyCode == 35)
    {
        this.component.goLast(this);
    }
    else if (event.keyCode == 36)
    {
        this.component.goFirst(this);
    }
};

System.ReceiverInput.textKeyup = function ()
{
    System.ReceiverInput.fitTextWidth(this);
};

System.ReceiverInput.textInput = function ()
{
    System.ReceiverInput.fitTextWidth(this);
};

System.ReceiverInput.fitTextWidth = function (input)
{
    var pattern = /[u00-uFF]/;
    var text = input.value;
    var length = 0;
    for (var i = 0; i < text.length; i++)
    {
        if (pattern.test(text.charAt(i)))
            length += 1;
        else
            length += 2;
    }
    var width = (length * 6 + 2) + "px";
    if (input.style.width != width)
    {
        input.style.width = width;
        if (Cyan.navigator.isFF())
            input.value = text;
    }
};

System.ReceiverInput.prototype.finishText = function (input)
{
    var texts = input.value.replace(/([;|,]*$)/g, "").trim().split(/[;|,]/);

    for (var i = 0; i < texts.length; i++)
    {
        var text = texts[i];
        if (text.length > 0)
            this.addReceiver({value: text, name: text, editable: true}, input);
    }
    input.value = "";
    System.ReceiverInput.fitTextWidth(input);
};

System.ReceiverInput.prototype.addReceiver = function (receiver, input, init)
{
    for (var i = 0; i < this.items.length; i++)
    {
        if (this.items[i].value == receiver.value)
            return;
    }

    this.items.push(receiver);

    if (!input)
        input = this.div.childNodes[this.div.childNodes.length - 2];

    var text = document.createElement("INPUT");
    this.div.insertBefore(text, input);
    this.initText(text);

    var div = document.createElement("DIV");

    this.initReceiver(receiver);

    if (!receiver.valid)
        div.className = "invalid";

    var s = receiver.editable ? receiver.value : receiver.name;
    if (s.length > 30)
        s = s.substring(0, 27) + "...";
    div.appendChild(document.createTextNode(s + ";"));

    this.div.insertBefore(div, input);

    var component = this;
    Cyan.attach(div, "click", function (event)
    {
        component.select(this);
        event.stop();
    });

    if (!init)
    {
        this.input.value = this.getReceiverList();
        if (this.onchange)
            this.onchange();
    }
};

System.ReceiverInput.prototype.removeReceiver = function (obj)
{
    var index;
    var childNodes = this.div.childNodes;
    if (Cyan.isNumber(obj))
    {
        index = obj;
    }
    else if (obj)
    {
        for (var i = 0; i < childNodes.length; i++)
        {
            if (childNodes[i] == obj)
            {
                index = (i - 1) / 2;
                if (index >= 0)
                    index = parseInt(index);
                break;
            }
        }
    }
    else
    {
        if (this.selectedIndex != null)
            index = this.selectedIndex;
        else
            return;
    }

    if (index >= 0)
    {
        this.items.removeByIndex(index);
        this.div.removeChild(this.div.childNodes[index * 2]);
        this.div.removeChild(this.div.childNodes[index * 2]);

        if (this.selectedIndex == index)
            this.selectedIndex = null;
        else
            this.clearSelected();

        childNodes[index * 2].focus();
        this.input.value = this.getReceiverList();
        if (this.onchange)
            this.onchange();
    }
};

System.ReceiverInput.prototype.removeSelected = function ()
{
    if (this.selectedIndex != null)
        this.removeReceiver(this.selectedIndex);
};

System.ReceiverInput.prototype.select = function (obj)
{
    var index, div;
    if (Cyan.isNumber(obj))
    {
        index = obj;
        div = this.div.childNodes[index * 2 + 1];
    }
    else
    {
        div = obj;
        var childs = this.div.childNodes;
        for (var i = 0; i < childs.length; i++)
        {
            if (childs[i] == div)
            {
                index = parseInt(i / 2);
                break;
            }
        }
    }

    if (this.selectedIndex != null)
    {
        if (this.selectedIndex == index)
            return;
        else
            this.clearSelected();
    }

    this.selectedIndex = index;
    $$(div).addClass("selected");

    if (this.hiddenText.focus)
        this.hiddenText.focus();
};

System.ReceiverInput.prototype.clearSelected = function ()
{
    if (this.selectedIndex != null)
    {
        $$(this.div.childNodes[this.selectedIndex * 2 + 1]).removeClass("selected");
        this.selectedIndex = null;
    }
};

System.ReceiverInput.prototype.goNext = function (obj)
{
    var obj0 = obj;
    if (!obj)
    {
        if (this.selectedIndex != null)
            obj = this.div.childNodes[this.selectedIndex * 2 + 1];
        else
            return;
    }

    if (obj.nodeName == "INPUT")
        System.ReceiverInput.fitTextWidth(obj);

    this.clearSelected();
    obj = obj.nextSibling;
    if (obj)
    {
        if (obj.nodeName == "DIV")
            obj = obj.nextSibling;
        if (obj.nextSibling)
        {
            obj.focus();
            if (obj0 && obj0.nodeName == "INPUT")
                this.finishText(obj0);
        }
    }
};

System.ReceiverInput.prototype.goPrevious = function (obj)
{
    var obj0 = obj;
    if (!obj)
    {
        if (this.selectedIndex != null)
            obj = this.div.childNodes[this.selectedIndex * 2 + 1];
        else
            return;
    }

    if (obj.nodeName == "INPUT")
        System.ReceiverInput.fitTextWidth(obj);

    this.clearSelected();
    obj = obj.previousSibling;
    if (obj)
    {
        if (obj0 && obj0.nodeName == "INPUT")
        {
            this.finishText(obj0);
        }

        if (obj.nodeName == "DIV")
            obj = obj.previousSibling;
        obj.focus();
    }
};

System.ReceiverInput.prototype.goFirst = function (obj)
{
    this.clearSelected();
    this.div.childNodes[0].focus();
    if (obj && obj != this.div.childNodes[0])
    {
        this.finishText(obj);
    }
};

System.ReceiverInput.prototype.goLast = function (obj)
{
    this.clearSelected();
    var obj1 = this.div.childNodes[this.div.childNodes.length - 2];
    if (obj && obj != obj1)
    {
        obj1.focus();
        this.finishText(obj);
    }
};

System.ReceiverInput.getTargetList = function ()
{
    if (!System.ReceiverInput.targetList)
    {
        System.ReceiverInput.targetList = new Cyan.TargetList();
        System.ReceiverInput.targetList.onselect = function (item)
        {
            this.bindComponent.component.addReceiver(item, this.bindComponent);
            this.bindComponent.value = "";
        };
        System.ReceiverInput.targetList.render = function (item)
        {
            var s = item.name;
            if (item.remark)
                s += "<span class='receiver_remark'>(" + item.remark + ")</span>";

            if (!item.valid)
                s = "<span class='receiver_invalid'>" + s + "</span>";
            return s;
        };
        System.ReceiverInput.targetList.width = 300;
    }

    return System.ReceiverInput.targetList;
};

System.ReceiverInput.prototype.openSelectDialog = function (typeName)
{
    if (this.tip)
    {
        this.div.removeChild(this.tip);
        this.tip = null;
    }

    var receiverInput = System.ReceiverInput.currentInstance = this;

    System.getReceivers(this.type, typeName, this.appId, this.deptId, function (result)
    {
        if (result)
        {
            receiverInput.clear();
            for (var i = 0; i < result.length; i++)
                receiverInput.addReceiver(result[i]);
        }
        System.ReceiverInput.currentInstance = null;
    });
};

System.ReceiverInput.prototype.isValid = function ()
{
    return false;
};

System.getReceivers = function (type, typeName, appId, deptId, callback)
{
    var url = "/receiver/select?type=" + type;
    if (typeName)
        url += "&typeName=" + encodeURIComponent(typeName);
    if (appId)
        url += "&appId=" + appId;
    if (deptId)
        url += "&deptId=" + deptId;
    System.showModal(url, callback);
};