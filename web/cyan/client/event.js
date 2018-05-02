Cyan.EVENTS = ["abort", "blur", "change", "click", "dblclick", "error", "focus", "keydown", "keypress",
    "keyup", "load", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "reset", "resize", "select",
    "submit", "unload", "mousewheel", "contextmenu", "scroll", "input"];
Cyan.BUBBLEEVENTS = ["click", "dblclick", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout",
    "mouseover", "mouseup", "mousewheel", "contextmenu"];
Cyan.IEEVENTS = ["abort", "activate", "afterprint", "afterupdate", "beforeactivate", "beforecopy", "beforecut",
    "beforedeactivate", "beforeeditfocus", "beforepaste", "beforeprint", "beforeunload", "beforeupdate", "blur",
    "bounce", "canplay", "canplaythrough", "cellchange", "change", "click", "contextmenu", "controlselect", "copy",
    "cut", "dataavailable", "datasetchanged", "datasetcomplete", "dblclick", "deactivate", "drag", "dragend",
    "dragenter", "dragleave", "dragover", "dragstart", "drop", "durationchange", "emptied", "ended", "error",
    "errorupdate", "filterchange", "finish", "focus", "focusin", "focusout", "hashchange", "help", "keydown",
    "keypress", "keyup", "layoutcomplete", "load", "loadeddata", "loadedmetadata", "loadstart", "losecapture",
    "message", "mousedown", "mouseenter", "mouseleave", "mousemove", "mouseout", "mouseover", "mouseup",
    "mousewheel", "move", "moveend", "movestart", "mssitemodeinstalled", "mssitemodejumplistitemremoved",
    "msthumbnailclick", "offline", "online", "page", "paste", "pause", "play", "playing", "progress", "progress"
    , "propertychange", "ratechange", "readystatechange", "readystatechange", "reset", "resize", "resizeend"
    , "resizestart", "rowenter", "rowexit", "rowsdelete", "rowsinserted", "scroll", "seeked", "seeking", "select"
    , "selectionchange", "selectstart", "stalled", "start", "stop", "storage", "storagecommit", "submit", "suspend",
    "timeout", "timeupdate", "unload", "volumechange", "waiting"];

Cyan.attach = function (component, event, callable, useCapture)
{
    if (!event)
        return;
    component = Cyan.$(component);
    if (!component)
        return;
    if (!Cyan.isString(event) && !callable)
    {
        Cyan.each(event, function (event)
        {
            if (this instanceof Function)
                Cyan.attach(component, event, this, useCapture);
        });
    }
    else
    {
        if (!callable)
            return;

        if (Cyan.isString(callable))
            callable = new Function("event", callable);

        var callable0 = callable;

        if (event == "input")
        {
            if (Cyan.navigator.isIE() && Cyan.navigator.version < 9)
            {
                event = "propertychange";

                callable = callable.$$cyan$$eventAction$$ieInput;
                if (!callable)
                {
                    callable = function (event)
                    {
                        event.type = "input";
                        if (event.event.propertyName == "value")
                            callable0.apply(this, [event]);
                    };
                    callable.$$cyan$$eventAction$$ieInput = callable;
                }
            }
        }
        else if (event == "mousewheel")
        {
            if (Cyan.navigator.isFF())
                event = "DOMMouseScroll";
        }

        if (component.addEventListener)
        {
            callable = Cyan.Event.action(callable);
            component.addEventListener(event, callable, useCapture || false);
        }
        else if (Cyan.Array.contains(Cyan.IEEVENTS, event))
        {
            callable = Cyan.Event.action(callable, component);
            component.attachEvent("on" + event, callable);
        }
        else
        {
            if (!component.$$cyan$$customEvents)
                component.$$cyan$$customEvents = {};
            if (!component.$$cyan$$customEvents[event])
                component.$$cyan$$customEvents[event] = [];
            component.$$cyan$$customEvents[event].push(callable);
        }
        return callable;
    }
};
Cyan.detach = function (component, event, callable, useCapture)
{
    if (!event || !callable)
        return;

    component = Cyan.$(component);
    if (!component)
        return;

    if (event == "input")
    {
        if (Cyan.navigator.isIE() && Cyan.navigator.version < 9)
        {
            event = "propertychange";
            if (callable.$$cyan$$eventAction$$ieInput)
                callable = callable.$$cyan$$eventAction$$ieInput;
        }
    }
    else if (event == "mousewheel")
    {
        if (Cyan.navigator.isFF())
            event = "DOMMouseScroll";
    }

    if (callable.$$cyan$$eventAction)
    {
        callable = callable.$$cyan$$eventAction;
    }
    else if (callable.$$cyan$$eventActions)
    {
        var c = callable.$$cyan$$eventActions[component];
        if (c)
            callable = c;
    }

    if (component.removeEventListener)
    {
        component.removeEventListener(event, callable, useCapture || false);
    }
    else if (Cyan.Array.contains(Cyan.IEEVENTS, event))
    {
        component.detachEvent("on" + event, callable);
    }
    else
    {
        if (component.$$cyan$$customEvents && component.$$cyan$$customEvents[event])
            Cyan.Array.removeElement(component.$$cyan$$customEvents[event], callable);
    }
};
Cyan.fireEvent = function (component, event, args, bubbles)
{
    if (!event)
        return;

    component = Cyan.$(component);
    if (!component)
        return;

    var eventObject;
    if (document.createEvent)
    {
        if (bubbles == null)
        {
            if (Cyan.Array.contains(Cyan.EVENTS, event))
            {
                bubbles = Cyan.Array.contains(Cyan.BUBBLEEVENTS, event);
            }
            else
            {
                bubbles = !(Cyan.customEvents[event] && Cyan.customEvents[event].bubbles == false);
            }
        }
        eventObject = document.createEvent('HTMLEvents');
        eventObject.initEvent(event, bubbles, true);
    }
    else
    {
        if (bubbles == null && !Cyan.Array.contains(Cyan.IEEVENTS, event) && Cyan.customEvents[event] &&
                Cyan.customEvents[event].bubbles == false)
            bubbles = false;

        eventObject = document.createEventObject();
        if (bubbles == false)
            eventObject.cancelBubble = true;
    }
    if (event == "input")
    {
        if (Cyan.navigator.isIE() && Cyan.navigator.version < 9)
        {
            event = "propertychange";
            eventObject.propertyName = "value";
        }
    }
    else if (event == "mousewheel")
    {
        if (Cyan.navigator.isFF())
            event = "DOMMouseScroll";
    }

    if (args)
    {
        Cyan.clone(args, eventObject);
        eventObject.$$eventData$$ = args;
    }

    if (!window.addEventListener)
    {
        if (this.button == 0)
            this.button = 1;
        else if (this.button == 1)
            this.button = 4;
    }

    if (component.dispatchEvent)
    {
        return component.dispatchEvent(eventObject);
    }
    else if (Cyan.Array.contains(Cyan.IEEVENTS, event))
    {
        return component.fireEvent("on" + event, eventObject);
    }
    else
    {
        eventObject.srcElement = eventObject;
        eventObject.type = "event";

        var cyanEvent = new Cyan.Event(eventObject);
        var result = Cyan.fireIECustomEvent(component, event, cyanEvent);
        component = component.parentNode;
        while (component != document.body)
        {
            if (eventObject.cancelBubble)
                break;
            Cyan.fireIECustomEvent(component, event, cyanEvent);
            component = component.parentNode;
        }
        if (!eventObject.cancelBubble)
            Cyan.fireIECustomEvent(document.body, event, cyanEvent);

        return result;
    }
};
Cyan.dispatchEvent = Cyan.fireEvent;
Cyan.fireIECustomEvent = function (component, name, event)
{
    var result;
    event.currentTarget = component;
    if (component.$$cyan$$customEvents && component.$$cyan$$customEvents[name])
    {
        var lisenters = component.$$cyan$$customEvents[name];
        for (var i = 0; i < lisenters.length; i++)
        {
            var r = lisenters[i].apply(component, [event]);
            if (i == 0)
                result = r != false;
        }
    }
    if (event.returnValue != null)
        result = event.returnValue != false;
    else if (result == null)
        result = true;
    return result;
};
Cyan.Event = function (event)
{
    if (event)
    {
        this.event = event;
        this.type = event.type;
        if (this.type == "DOMMouseScroll")
            this.type = "mousewheel";
        else if (this.type == "propertychange" && event.propertyName == "value")
            this.type = "input";

        this.target = event.target || event.srcElement;
        this.srcElement = this.target;
        if (this.target)
            this.src = Cyan.$$(this.target);
        this.relatedTarget = event.relatedTarget || null;
        this.fromElement = event.fromElement || null;
        this.toElement = event.toElement || null;
        if (!this.relatedTarget)
        {
            if (event.type == "mouseover")
                this.relatedTarget = event.fromElement;
            else if (event.type == "mouseout")
                this.relatedTarget = event.toElement;
        }
        if (!this.srcElement)
        {
            if (event.type == "mouseover")
                this.fromElement = event.relatedTarget;
            else if (event.type == "mouseout")
                this.fromElement = event.target;
        }
        if (!this.toElement)
        {
            if (event.type == "mouseover")
                this.toElement = event.target;
            else if (event.type == "mouseout")
                this.toElement = event.relatedTarget;
        }

        this.ctrlKey = event.ctrlKey || false;
        this.altKey = event.altKey || false;
        this.shiftKey = event.shiftKey || false;
        this.metaKey = event.metaKey || false;

        if (window.addEventListener)
        {
            this.button = event.button;
        }
        else
        {
            if (this.button == 1)
                this.button = 0;
            else if (this.button == 4)
                this.button = 1;
        }
        this.leftButton = this.button == 0;
        this.rightButton = this.button == 2;
        this.middleButton = this.button == 4;

        this.clientX = event.clientX;
        this.clientY = event.clientY;
        this.screenX = event.screenX;
        this.screenY = event.screenY;
        this.pageX = event.pageX;
        this.pageY = event.pageY;
        this.offsetX = event.offsetX;
        this.offsetY = event.offsetY;
        if (this.pageX == null)
        {
            this.pageX = event.clientX + (document.body.scrollLeft || document.documentElement.scrollLeft) -
                    (document.body.clientLeft || document.documentElement.clientLeft);
        }
        if (this.pageY == null)
        {
            this.pageY = event.clientY + (document.body.scrollTop || document.documentElement.scrollTop) -
                    (document.body.clientTop || document.documentElement.clientTop);
        }
        if (this.offsetX == null || this.offsetY == null)
        {
            var offset = this.getOffset();
            this.offsetX = offset.x;
            this.offsetY = offset.y;
        }
        if (!Cyan.navigator.isIE() || Cyan.navigator.isOpera())
        {
            this.offsetX -= this.target.clientLeft;
            this.offsetY -= this.target.clientY;
        }

        this.keyCode = event.keyCode;
        this.charCode = event.charCode;
        this.which = event.which;
        if (this.charCode == null)
        {
            if (this.type == "keypress")
                this.charCode = this.keyCode;
            else
                this.charCode = 0;
        }
        if (this.which == null)
            this.which = this.keyCode;
        if (!this.keyCode)
            this.keyCode = this.charCode;
        this.charValue = String.fromCharCode(this.charCode || 0);

        this.detail = event.detail;
        this.wheelDelta = event.wheelDelta;
        if (!this.detail && this.wheelDelta)
            this.detail = event.wheelDelta > 0 ? -1 : 1;
        if (!this.wheelDelta && this.detail)
            this.wheelDelta = -120 * event.detail;

        if (event.$$eventData$$)
        {
            for (name in event.$$eventData$$)
            {
                if (this[name] == null)
                    this[name] = event.$$eventData$$[name];
            }
        }
    }
};
Cyan.Event.stop = function (event)
{
    event.stop();
    return false;
};
Cyan.Event.action = function (action, component)
{
    if (!action)
        return null;

    var wrapAction = action.$$cyan$$eventAction;
    if (!wrapAction && component && component.$$cyan$$eventActionId && action.$$cyan$$eventActions)
        wrapAction = action.$$cyan$$eventActions[component.$$cyan$$eventActionId];

    if (!wrapAction)
    {
        wrapAction = function (event)
        {
            var event0 = event || window.event;
            var event1 = window.event$;
            var clearEvent = !window.event;

            if (event0)
            {
                event = event0.$$cyan$$event;
                if (!event)
                {
                    event = new Cyan.Event(event0);
                    event0.$$cyan$$event = event;
                }
                event.currentTarget = component || this;

                event.eventPhase = event.event.eventPhase;
                if (event.eventPhase == null)
                    event.eventPhase = (event.target == event.currentTarget) ? 2 : 3;

                window.event$ = event;
                if (clearEvent)
                    window.event = event0;
            }

            var result = action.apply(component || this, [event]);
            window.event$ = event1;
            if (clearEvent)
                window.event = event1;
            return result;
        };

        if (component)
        {
            if (!action.$$cyan$$eventActions)
                action.$$cyan$$eventActions = {};
            if (!component.$$cyan$$eventActionId)
            {
                component.$$cyan$$eventActionId = component.nodeId +
                        "$$cyan$$eventActionId$$" + Math.random().toString().substring(2);
            }
            action.$$cyan$$eventActions[component.$$cyan$$eventActionId] = wrapAction;
        }
        else
        {
            action.$$cyan$$eventAction = wrapAction;
        }
        wrapAction.$$cyan$$eventAction = wrapAction;
    }

    return wrapAction;
};
Cyan.Event.prototype.getOffset = function (element, noscroll)
{
    var p = (element = element ? Cyan.$$$(element) : this.src).getPosition()[0];
    return {
        x: this.pageX - p.x + (noscroll ? element.first.scrollLeft : 0),
        y: this.pageY - p.y + (noscroll ? element.first.scrollTop : 0)
    };
};
Cyan.Event.prototype.getOffsetX = function (element)
{
    return this.getOffset(element).x;
};
Cyan.Event.prototype.getOffsetY = function (element)
{
    return this.getOffset(element).y;
};
/**
 * 判断触发事件时，鼠标是否在某个元素熵
 * @param element
 */
Cyan.Event.prototype.isOn = function (element)
{
    element = Cyan.$$$(element);
    if (element.length > 0)
    {
        var p = this.getOffset(element, false);
        element = element[0];
        return p.x > 0 && p.y > 0 && p.x <= element.offsetWidth && p.y <= element.offsetHeight;
    }
    else
        return 0;
};
Cyan.Event.prototype.preventDefault = function ()
{
    if (this.event.preventDefault)
        this.event.preventDefault();
    else
        this.event.returnValue = false;
};
Cyan.Event.prototype.stopPropagation = function ()
{
    if (this.event.stopPropagation)
        this.event.stopPropagation();
    else
        this.event.cancelBubble = true;
};
Cyan.Event.prototype.stop = function ()
{
    this.preventDefault();
    this.stopPropagation();
};
Cyan.Event.getSelectionStart = function (component)
{
    if (Cyan.isNumber(component.selectionStart))
        return component.selectionStart;

    return -1;
};
Cyan.Elements.prototype.dispatchEvent = Cyan.Elements.prototype.fireEvent;
Cyan.Event.change = function (element, event, custom)
{
    if (event == "unload")
        return;

    var action;
    if (custom)
    {
        action = element["on" + event];
        if (!action && element.getAttribute)
            action = element.getAttribute("on" + event);
        if (action)
            Cyan.attach(element, event, action);
    }
    else
    {
        try
        {
            action = element["on" + event];
            if (action)
                element["on" + event] = Cyan.Event.action(action);
        }
        catch (e)
        {
        }
    }
};
Cyan.Event.initEvent = function (element)
{
    Cyan.Elements.eachElement(element || document.body, function ()
    {
        if (this.nodeType == 1)
        {
            var change = Cyan.Event.change, i;
            for (i = 0; i < Cyan.EVENTS.length; i++)
                change(this, Cyan.EVENTS[i], false);
            for (var event in Cyan.customEvents)
                change(this, event, !Cyan.navigator.isIE() || !Cyan.IEEVENTS.contains(event));
        }
    });
};
Cyan.Event.makeEvent = function (name)
{
    if (name)
    {
        var name1 = name.charAt(0).toUpperCase() + name.substring(1);
        var onname = "on" + name;
        var on;
        if (!Cyan.Elements.prototype[onname])
        {
            on = function (callable, useCapture)
            {
                this.attach(name, callable, useCapture);
                return this;
            };
            Cyan.Elements.prototype[onname] = on;
        }
        var onname1 = "on" + name1;
        if (!Cyan.Elements.prototype[onname1])
        {
            if (!on)
                on = function (callable, useCapture)
                {
                    this.attach(name, callable, useCapture);
                };
            Cyan.Elements.prototype[onname1] = on;
        }
        var fireName = "fire" + name1;
        if (!Cyan.Elements.prototype[fireName])
        {
            Cyan.Elements.prototype[fireName] = function (args)
            {
                return this.fireEvent(name, args);
            };
        }
    }
    else
    {
        var i;
        for (i = 0; i < Cyan.EVENTS.length; i++)
            Cyan.Event.makeEvent(Cyan.EVENTS[i]);
        for (var event in  Cyan.customEvents)
            Cyan.Event.makeEvent(event);
    }
};
/**
 * 为某个元素绑定一个热键
 * @param key 热键，#编码或者字符，可以是一个对象，里面每个属性表示一个热键，对应的值为触发热键的方法
 * @param callable 触发的动作
 */
Cyan.Elements.prototype.hotKey = function (key, callable)
{
    var handle = key;
    if (Cyan.isString(key) && callable instanceof Function)
    {
        handle = {};
        handle[key] = callable;
    }
    var b, handle2 = {};
    for (key in handle)
    {
        var action = handle[key];
        if (action instanceof Function)
        {
            key = Cyan.replaceAll(key, " ", "_").split("_");
            if (key.length <= 4)
            {
                var c = null, ctrl = false, alt = false, shift = false;
                for (var i = 0; i < key.length; i++)
                {
                    if (key[i] == "ctrl")
                    {
                        if (ctrl)
                            break;
                        ctrl = true;
                    }
                    else if (key[i] == "alt")
                    {
                        if (alt)
                            break;
                        alt = true;
                    }
                    else if (key[i] == "shift")
                    {
                        if (shift)
                            break;
                        shift = true;
                    }
                    else if (i < key.length - 1)
                    {
                        break;
                    }
                    else if (key[i] == 'esc')
                    {
                        c = 27;
                    }
                    else if (key[i] == 'space')
                    {
                        c = 32;
                    }
                    else if (key[i] == '\r' || key[i] == '\n' || key[i] == 'enter')
                    {
                        c = 13;
                    }
                    else if (key[i].length == 1)
                    {
                        c = key[i].toUpperCase().charCodeAt(0);
                    }
                    else if (key[i].charAt(0) == '#')
                    {
                        //#号开头表示热键的编码
                        c = Cyan.toInt(key[i].substr(1));
                    }
                }
                if (c)
                {
                    handle2[c + "_" + (ctrl ? "1" : "0") + (alt ? "1" : "0") + (shift ? "1" : "0")] = action;
                    b = true;
                }
            }
        }
    }
    if (b)
    {
        //如果至少定义了一个热键
        this.attach("keyup", function (event)
        {
            var f = handle2[event.keyCode + "_" + (event.ctrlKey ? "1" : "0") + (event.altKey ? "1" : "0") +
            (event.shiftKey ? "1" : "0")];
            if (f instanceof Function)
            {
                f.apply(this, [event]);
                event.stop();
            }
        });
    }
    return this;
};
/**
 * 定义可输入域键盘按下的动作，用于通过方向键在不同的输入域之间跳转
 */
Cyan.Event.fieldKeyPress1 = function (event)
{
    var srcElement = event.target;
    if (srcElement.form)
    {
        if (!event.altKey && !event.shiftKey && !event.ctrlKey)
        {
            var keyCode = event.keyCode;
            if (keyCode == 13)
            {
                if (srcElement.nodeName == "INPUT" && !Cyan.navigator.isOpera())
                {
                    if (srcElement.type == "image")
                    {
                        //image，激活图片的click动作
                        srcElement.click();
                    }
                    else if (srcElement.type == "checkbox")
                    {
                        //checkbox，在选择和不选择之间切换
                        srcElement.checked = !srcElement.checked;
                    }
                    else if (srcElement.type == "radio")
                    {
                        //radio，选择之
                        srcElement.checked = true;
                    }
                }
                else if (srcElement.nodeName == "SELECT" && !Cyan.navigator.isOpera())
                {
                    //跳到下一个控件
                    Cyan.Event.activeField(Cyan.Elements.nextField(srcElement), true);
                }
            }
        }
    }
};
/**
 * 定义可输入域按钮按下是的动作，用于通过方向键在不同的输入域之间跳转
 */
Cyan.Event.fieldKeyDown1 = function (event)
{
    var srcElement = event.target;
    if (srcElement.form)
    {
        if (!event.altKey && !event.shiftKey && !event.ctrlKey)
        {
            var keyCode = event.keyCode;
            if (keyCode == 13)
            {
                if (srcElement.nodeName == "INPUT" && srcElement.type != "file" && !Cyan.navigator.isOpera())
                    event.stop();
            }
            if (keyCode == 37)
            {
                Cyan.Event.activeField(Cyan.Elements.previousField(srcElement), false);
            }
            else if (keyCode == 39)
            {
                Cyan.Event.activeField(Cyan.Elements.nextField(srcElement), false);
            }
            else if (keyCode == 38)
            {
                //上键，跳到位于上方的输入域
                if (srcElement.nodeName != "SELECT")
                    Cyan.Event.activeField(Cyan.Elements.fieldAbove(srcElement), false);
            }
            else if (keyCode == 40)
            {
                //下键，跳到位于下方的输入域
                if (srcElement.nodeName != "SELECT")
                    Cyan.Event.activeField(Cyan.Elements.fieldUnder(srcElement), false);
            }
            else if (keyCode == 35)
            {
                //end，跳到最后一个可输入域
                var lastField = Cyan.Elements.lastField(srcElement.form);
                if (lastField != srcElement)
                    Cyan.Event.activeField(lastField, false);
            }
            else if (keyCode == 36)
            {
                //home，跳到第一个可输入域
                var firstField = Cyan.Elements.firstField(srcElement.form);
                if (firstField != srcElement)
                    Cyan.Event.activeField(firstField, false);
            }
        }
    }
};

/**
 * 定义可输入域按钮按下是的动作，用于通过方向键在不同的输入域之间跳转
 */
Cyan.Event.fieldKeyPress2 = function (event)
{
    var srcElement = event.target;
    if (srcElement.form && !event.altKey && !event.shiftKey && !event.ctrlKey)
    {
        if (event.keyCode == 13)
        {
            if (srcElement.nodeName == "INPUT" && !Cyan.navigator.isOpera())
            {
                //text域，接收回车，焦点跳到下一个可输入域
                Cyan.Event.activeField(Cyan.Elements.nextField(srcElement), true);
                event.stop();
            }
        }
    }
};

/**
 * 输入框，keypress之前的动作，判断当光标在第一个位置或最后一个位置时跳转
 */
Cyan.Event.fieldKeyDown2 = function (event)
{
    var srcElement = event.target;
    if (srcElement.form && !event.altKey && !event.shiftKey && !event.ctrlKey)
    {
        var pos, s;
        if (event.keyCode == 13)
        {
            // if (srcElement.nodeName == "INPUT" && !Cyan.navigator.isOpera())
            //    event.stop();
        }
        else if (event.keyCode == 37)
        {
            //左健，当光标在第一个位置时，焦点跳到下一个可输入域
            if (Cyan.Event.getSelectionStart(srcElement) == 0)
            {
                Cyan.Event.activeField(Cyan.Elements.previousField(srcElement), false);
                event.stop();
            }
        }
        else if (event.keyCode == 39)
        {
            //右健，当光标在最后一个位置时，焦点跳到下一个可输入域
            if (Cyan.Event.getSelectionStart(srcElement) == srcElement.value.length)
            {
                Cyan.Event.activeField(Cyan.Elements.nextField(srcElement), false);
                event.stop();
            }
        }
        else if (event.keyCode == 38)
        {
            //上键，跳到位于上方的输入域
            if (srcElement.nodeName == "INPUT")
            {
                Cyan.Event.activeField(Cyan.Elements.fieldAbove(srcElement), false);
            }
            else
            {
                pos = Cyan.Event.getSelectionStart(srcElement);
                if (pos >= 0 && pos <= srcElement.value.length)
                {
                    if (Cyan.navigator.isIE())
                    {
                        s = srcElement.value.substring(0, pos + 1);
                        if (s.indexOf('\r') < 0 && s.indexOf('\n') < 0)
                            Cyan.Event.activeField(Cyan.Elements.fieldAbove(srcElement), false);
                    }
                    else
                    {
                        s = srcElement.value.substring(0, pos);
                        if (s.indexOf('\n') < 0)
                            Cyan.Event.activeField(Cyan.Elements.fieldAbove(srcElement), false);
                    }
                }
            }
        }
        else if (event.keyCode == 40)
        {
            //下键，跳到位于下方的输入域
            if (srcElement.nodeName == "INPUT")
            {
                Cyan.Event.activeField(Cyan.Elements.fieldUnder(srcElement), false);
            }
            else
            {
                pos = Cyan.Event.getSelectionStart(srcElement);
                if (pos >= 0 && pos <= srcElement.value.length)
                {
                    if (Cyan.navigator.isIE())
                    {
                        s = srcElement.value.substring(pos + 1, srcElement.value.length);
                        if (s.indexOf('\r') < 0 && s.indexOf('\n') < 0)
                            Cyan.Event.activeField(Cyan.Elements.fieldUnder(srcElement), false);
                    }
                    else
                    {
                        s = srcElement.value.substring(pos, srcElement.value.length);
                        if (s.indexOf('\n') < 0)
                            Cyan.Event.activeField(Cyan.Elements.fieldUnder(srcElement), false);
                    }
                }
            }
        }
        else if (event.keyCode == 35)
        {
            //end键，当光标在最后一个位置时，焦点跳到最后一个可输入域
            if (Cyan.Event.getSelectionStart(srcElement) == srcElement.value.length)
            {
                var lastField = Cyan.Elements.lastField(srcElement.form);
                if (lastField != srcElement)
                    Cyan.Event.activeField(lastField, false);
            }
        }
        else if (event.keyCode == 36)
        {
            //home键，当光标在第一个位置时，焦点跳到第一个可输入域
            if (Cyan.Event.getSelectionStart(srcElement) == 0)
            {
                var firstField = Cyan.Elements.firstField(srcElement.form);
                if (firstField != srcElement)
                    Cyan.Event.activeField(firstField, false);
            }
        }
    }
};
/**
 * 获取下一个可输入域
 * @param element 当前控件
 */
Cyan.Elements.nextField = function (element)
{
    if (element && element.form)
    {
        for (var i = 0; i < element.form.length; i++)
        {
            if (element.form[i] == element)
            {
                if (i == element.form.length)
                    return null;
                var nextField = element.form[i + 1];
                return Cyan.Elements.isFieldSelectable(nextField) ? nextField : Cyan.Elements.nextField(nextField);
            }
        }
    }
    return null;
};
/**
 * 获取上一个可输入域
 * @param element 当前控件
 */
Cyan.Elements.previousField = function (element)
{
    if (element.form)
    {
        for (var i = 0; i < element.form.length; i++)
        {
            if (element.form[i] == element)
            {
                if (i == 0)
                    return null;
                var previousField = element.form[i - 1];
                return (Cyan.Elements.isFieldSelectable(previousField) ? previousField :
                        Cyan.Elements.previousField(previousField));
            }
        }
    }
    return null;
};
/**
 * 获取位于上方的可输入域
 * @param element 当前可输入域
 */
Cyan.Elements.fieldAbove = function (element)
{
    var position = Cyan.Elements.getPosition.apply(element), min, result = null;
    Cyan.each(element.form, function ()
    {
        if (this != element && Cyan.Elements.isFieldSelectable(element))
        {
            var position1 = Cyan.Elements.getPosition.apply(this);
            if (position1.y < position.y)
            {
                //在上边的元素中获取距离最小的一个
                var v = position.y - position1.y + Math.abs(position.x - position1.x);
                if (!min || v < min)
                {
                    min = v;
                    result = this;
                }
            }
        }
    });
    return result;
};
/**
 * 获取位于下方的可输入域
 * @param element 当前控件
 */
Cyan.Elements.fieldUnder = function (element)
{
    var position = Cyan.Elements.getPosition.apply(element), min, result = null;
    Cyan.each(element.form, function ()
    {
        if (this != element && Cyan.Elements.isFieldSelectable(element))
        {
            var position1 = Cyan.Elements.getPosition.apply(this);
            if (position1.y > position.y)
            {
                //在下方的元素中获取距离最小的一个
                var v = position1.y - position.y + Math.abs(position1.x - position.x);
                if (!min || v < min)
                {
                    min = v;
                    result = this;
                }
            }
        }
    });
    return result;
};
/**
 * 获取form中第一个可输入域
 * @param form form
 */
Cyan.Elements.firstField = function (form)
{
    if (form)
    {
        for (var i = 0; i < form.length; i++)
        {
            var element = form[i];
            if (Cyan.Elements.isFieldSelectable(element))
                return element;
        }
    }
    return null;
};
/**
 * 获取form中最后一个输入域
 * @param form form
 */
Cyan.Elements.lastField = function (form)
{
    if (form)
    {
        for (var i = form.length - 1; i >= 0; i--)
        {
            var element = form[i];
            if (Cyan.Elements.isFieldSelectable(element))
                return element;
        }
    }
    return null;
};
/**
 * 判断一个控件是否为可输入域
 * @param element 被判断的控件
 */
Cyan.Elements.isFieldSelectable = function (element)
{
    return element && (element.nodeName != "INPUT" || element.type != "hidden") && element.style.display != "none" &&
            element.selectable != "false" && !element.disabled && !element.readOnly;
};
/**
 * 激活一个可输入域
 * @param element 被激活的可输入域
 * @param action 是否同时激活动作
 */
Cyan.Event.activeField = function (element, action)
{
    if (element)
    {
        try
        {
            element.focus();
        }
        catch (e)
        {
        }
        if (element.nodeName == "INPUT")
        {
            if ((element.type == "text" || element.type == "password") && element.select instanceof Function)
            {
                element.select();
            }
            else if (element.type == "button" || element.type == "image" || element.type == "submit" ||
                    element.type == "reset")
            {
                if (action)
                    element.click();
            }
        }
        else if (element.nodeName == "BUTTON")
        {
            if (action)
                element.click();
        }
    }
};
Cyan.Elements.prototype.nextField = function ()
{
    return new Cyan.Elements(this.get(Cyan.Elements.nextField).removeElement(null));
};
Cyan.Elements.prototype.previousField = function ()
{
    return new Cyan.Elements(this.get(Cyan.Elements.previousField).removeElement(null));
};
Cyan.Elements.prototype.fieldAbove = function ()
{
    return new Cyan.Elements(this.get(Cyan.Elements.fieldAbove).removeElement(null));
};
Cyan.Elements.prototype.fieldUnder = function ()
{
    return new Cyan.Elements(this.get(Cyan.Elements.fieldUnder).removeElement(null));
};
/**
 * 渲染每个可输入域，增加其方向键和回车键的处理
 * @param element 被渲染的元素
 */
Cyan.Event.renderField = function (element)
{
    if (!element.keyEventRendered$)
    {
        if (element.nodeName == "INPUT" && (element.type == "text" || element.type == "password") ||
                element.nodeName == "TEXTAREA")
        {
            if (element.nodeName == "INPUT")
                Cyan.attach(element, "keypress", Cyan.Event.fieldKeyPress2);
            Cyan.attach(element, "keydown", Cyan.Event.fieldKeyDown2);
        }
        else
        {
            Cyan.attach(element, "keyup", Cyan.Event.fieldKeyPress1);
            Cyan.attach(element, "keydown", Cyan.Event.fieldKeyDown1);
        }

        //标识已经渲染过，避免重复渲染
        element.keyEventRendered$ = true;
    }
};
Cyan.Event.wrapCallback = function (callback)
{
    if (!callback)
        return null;

    if (window.event$)
    {
        var event = window.event$;
        return function ()
        {
            window.event$ = event;
            callback.apply(this, arguments);
            window.event$ = null;
        };
    }

    return callback;
};

Cyan.Event.makeEvent();
Cyan.addCustomEvent = function (event, config)
{
    if (!Cyan.customEvents[event])
    {
        Cyan.customEvents[event] = config || {};
        Cyan.Event.makeEvent(event);
        if (Cyan.Event.inited && document.getElementsByName("*").length < 600)
        {
            Cyan.Elements.eachElement(document.body, function ()
            {
                if (this.nodeType == 1)
                    Cyan.Event.change(this, event, !Cyan.navigator.isIE() || !Cyan.IEEVENTS.contains(event));
            });
        }
    }
};
Cyan.bindField = function (component1, component2)
{
    component1 = Cyan.$(component1);
    component2 = Cyan.$(component2);

    if (!component2.value)
        component2.value = component1.value;

    var value0 = component1.value;
    var f = function ()
    {
        if (!component2.value || component2.value == value0)
        {
            component2.value = value0 = component1.value;
        }
    };
    Cyan.attach(component1, "input", f);
    Cyan.attach(component1, "change", f);
};
Cyan.onload(function ()
{
    if (document.getElementsByName("*").length < 600)
    {
        Cyan.Event.initEvent();
        Cyan.elementInitializers.push(Cyan.Event.initEvent);
    }
    Cyan.each(document.getElementsByTagName("FORM"), function ()
    {
        Cyan.each(this, function ()
        {
            Cyan.Event.renderField(this);
        });
    });
    Cyan.$$(document.documentElement).onkeydown(function (event)
    {
        if (event.keyCode == 8)
        {
            if (!Cyan.isField(event.target) || event.target.readOnly)
                event.stop();
        }
    });
    Cyan.Event.inited = true;
});

Cyan.imagePaste = function (callback)
{
    document.body.addEventListener("paste", function (e)
    {
        var clipboard = e.clipboardData;
        var n = clipboard.items.length;
        for (var i = 0; i < n; i++)
        {
            var item = clipboard.items[i];
            var type = item.type;
            if (item.kind = "file" && type.startsWith("image/"))
            {
                var extName = type.substring(type.indexOf("/") + 1);
                var file = item.getAsFile();
                if (file)
                    callback(file, extName);
            }
        }
    });
};