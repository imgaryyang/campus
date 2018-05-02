/**
 * 定义render对象
 */
Cyan.Render = {};

Cyan.Render.sound = function (path)
{
    if (Cyan.navigator.isIE())
    {
        var bgs = Cyan.$("cyan_bsg");
        if (!bgs)
        {
            bgs = document.createElement("BGSOUND");
            bgs.loop = "1";
            document.body.appendChild(bgs);
        }
        bgs.src = path;
    }
};

/**
 * 设置控件的透明度
 * @param element 要设置的控件
 * @param opacity 透明度
 */
Cyan.Render.setOpacity = function (element, opacity)
{
    if (opacity >= 0 && opacity <= 1)
    {
        //ie使用filter，其他的浏览器使用opacity属性
        if (Cyan.navigator.isIE())
            element.style.filter = "alpha(opacity=" + opacity * 100 + ")";
        else
            element.style.opacity = opacity;
    }
};

/**
 * 渐变动作的超类
 */
Cyan.Render.Gradient = function ()
{
};
Cyan.Render.Gradient.prototype.play = function ()
{
    if (this.onStart)
        this.onStart();
    var index = 0;
    var gradient = this;
    //设置渐变的计时器
    this.gradientable = setInterval(function ()
    {
        //触发第n次变化
        if (gradient.onChange)
            gradient.onChange(index);
        if (!gradient.next(index++))
        {
            //触发变化完成的动作，并清掉计时器
            clearInterval(gradient.gradientable);
            if (gradient.onComplete)
                gradient.onComplete();
        }
    }, 100);
};

Cyan.Render.Fade = Cyan.Class.extend(function (element)
{
    this.element = element;
}, Cyan.Render.Gradient);
Cyan.Render.Fade.prototype.fade = function ()
{
    (this.element.style.display == "none" ? this.fadeIn : this.fadeOut).apply(this, arguments);
};

Cyan.Render.Fade.UP = 0;
Cyan.Render.Fade.DOWN = 1;
Cyan.Render.Fade.LEFT = 2;
Cyan.Render.Fade.RIGHT = 3;
Cyan.Render.Fade.HORIZONTAL = 4;
Cyan.Render.Fade.VERTICAL = 5;
Cyan.Render.Fade.CENTER = 6;
Cyan.Render.Fade.LEFTUP = 7;
Cyan.Render.Fade.LEFTDOWN = 8;
Cyan.Render.Fade.RIGHTUP = 9;
Cyan.Render.Fade.RIGHTDOWN = 10;

Cyan.Render.PositionFade = Cyan.Class.extend(function (element)
{
    this.inherited(element);
}, Cyan.Render.Fade);

Cyan.Render.PositionFade.prototype.fadeIn = function (direction, speed)
{
    if (this.element.fading$ != null)
        return;

    speed = Cyan.toInteger(speed, 2, 1);

    var d;
    if (Cyan.isString(direction) && (d = Cyan.Render.Fade[direction.toUpperCase()]) != null)
        direction = d;
    direction = Cyan.toInteger(direction, null, 0, 10);
    if (direction == null)
        direction = this.direction;
    if (direction == null)
        direction = (direction = Math.round(Math.random() * 11)) == 11 ? 10 : direction;

    if (direction == Cyan.Render.Fade.LEFT || direction == Cyan.Render.Fade.RIGHT ||
            direction == Cyan.Render.Fade.HORIZONTAL || direction >= Cyan.Render.Fade.CENTER)
    {
        //有水平方向上的变化

        //计算目标宽度，目标宽度有元素的targetWidth属性描述
        this.targetWidth =
                (this.targetWidth = this.element.targetWidth == null ? this.element.getAttribute("targetWidth") :
                        this.element.targetWidth, Cyan.isString(this.targetWidth) ? Cyan.toInt(this.targetWidth) :
                        this.targetWidth);

        //一开始为隐藏，元素框度为0
        this.originalWidth = 0;

        if (direction == Cyan.Render.Fade.RIGHT || direction == Cyan.Render.Fade.RIGHTUP ||
                direction == Cyan.Render.Fade.RIGHTDOWN)
        {
            //右移，只需要改变宽度，将left设置为目标位置
            var left = this.element.targetLeft == null ? this.element.getAttribute("targetLeft") :
                    this.element.targetLeft;
            if (left != null)
                this.element.style.left = Math.round(left) + "px";
        }
        else
        {
            //计算目标横坐标
            this.targetLeft = (this.targetLeft =
                    this.element.targetLeft == null ? this.element.getAttribute("targetLeft") :
                            this.element.targetLeft,
                    Cyan.isString(this.targetLeft) ? Cyan.toInt(this.targetLeft) : this.targetLeft);
        }

        if (direction < Cyan.Render.Fade.CENTER)
        {
            var height = this.element.targetHeight == null ? this.element.getAttribute("targetHeight") :
                    this.element.targetHeight;
            if (height)
                this.element.style.height = Math.round(height) + "px";
            var top = this.element.targetTop == null ? this.element.getAttribute("targetTop") : this.element.targetTop;
            if (top)
                this.element.style.top = Math.round(top) + "px";
        }
    }
    if (direction == Cyan.Render.Fade.UP || direction == Cyan.Render.Fade.DOWN ||
            direction == Cyan.Render.Fade.VERTICAL || direction >= Cyan.Render.Fade.CENTER)
    {
        this.targetHeight = (this.targetHeight =
                this.element.targetHeight == null ? this.element.getAttribute("targetHeight") :
                        this.element.targetHeight,
                Cyan.isString(this.targetHeight) ? Cyan.toInt(this.targetHeight) : this.targetHeight);
        this.originalHeight = 0;

        if (direction == Cyan.Render.Fade.DOWN || direction == Cyan.Render.Fade.LEFTDOWN ||
                direction == Cyan.Render.Fade.RIGHTDOWN)
        {
            top = this.element.targetTop == null ? this.element.getAttribute("targetTop") : this.element.targetTop;
            if (top)
                this.element.style.top = Math.round(top) + "px";
        }
        else
        {
            this.targetTop = (this.targetTop =
                    this.element.targetTop == null ? this.element.getAttribute("targetTop") :
                            this.element.targetTop,
                    Cyan.isString(this.targetTop) ? Cyan.toInt(this.targetTop) : this.targetTop);
        }

        if (direction < Cyan.Render.Fade.CENTER)
        {
            var width = this.element.targetWidth == null ? this.element.getAttribute("targetWidth") :
                    this.element.targetWidth;
            if (width)
                this.element.style.width = Math.round(width) + "px";
            left = this.element.targetLeft == null ? this.element.getAttribute("targetLeft") : this.element.targetLeft;
            if (left)
                this.element.style.left = Math.round(left) + "px";
        }
    }

    if (this.element.style.display == "none")
    {
        if (direction == Cyan.Render.Fade.UP || direction == Cyan.Render.Fade.DOWN ||
                direction == Cyan.Render.Fade.VERTICAL || direction >= Cyan.Render.Fade.CENTER)
        {
            if (this.targetHeight == null)
                this.targetHeight = (this.targetHeight = this.element.style.height) ? Cyan.toInt(this.targetHeight) : 0;
            if (direction != Cyan.Render.Fade.DOWN && this.targetTop == null)
                this.targetTop = (this.targetTop = this.element.style.top) ? Cyan.toInt(this.targetTop) : 0;
            this.element.style.height = "0";
        }
        if (direction == Cyan.Render.Fade.LEFT || direction == Cyan.Render.Fade.RIGHT ||
                direction == Cyan.Render.Fade.HORIZONTAL || direction >= Cyan.Render.Fade.CENTER)
        {
            if (this.targetWidth == null)
                this.targetWidth = (this.targetWidth = this.element.style.width) ? Cyan.toInt(this.targetWidth) : 0;
            if (direction != Cyan.Render.Fade.RIGHT && this.targetLeft == null)
                this.targetLeft = (this.targetLeft = this.element.style.left) ? Cyan.toInt(this.targetLeft) : 0;
            this.element.style.width = "0";
        }
        this.element.style.display = "";
    }

    if (direction == Cyan.Render.Fade.LEFT || direction == Cyan.Render.Fade.LEFTDOWN)
    {
        this.originalLeft = this.targetLeft + this.targetWidth;
    }
    else if (direction == Cyan.Render.Fade.UP || direction == Cyan.Render.Fade.RIGHTUP)
    {
        this.originalTop = this.targetTop + this.targetHeight;
    }
    else if (direction == Cyan.Render.Fade.HORIZONTAL)
    {
        this.originalLeft = this.targetLeft + this.targetWidth / 2;
    }
    else if (direction == Cyan.Render.Fade.VERTICAL)
    {
        this.originalTop = this.targetTop + this.targetHeight / 2;
    }
    else if (direction == Cyan.Render.Fade.CENTER)
    {
        this.originalLeft = this.targetLeft + this.targetWidth / 2;
        this.originalTop = this.targetTop + this.targetHeight / 2;
    }
    else if (direction == Cyan.Render.Fade.LEFTUP)
    {
        this.originalLeft = this.targetLeft + this.targetWidth;
        this.originalTop = this.targetTop + this.targetHeight;
    }

    this.fade0(speed);
};

/**
 * 淡出
 * @param direction 方向
 * @param speed 速度
 */
Cyan.Render.PositionFade.prototype.fadeOut = function (direction, speed)
{
    if (this.element.fading$ != null)
        return;

    //默认熟读为2
    speed = Cyan.toInteger(speed, 2, 1);

    var d;
    if (Cyan.isString(direction) && (d = Cyan.Render.Fade[direction.toUpperCase()]) != null)
        direction = d;
    //随机产生默认方向
    direction = Cyan.toInteger(direction, null, 0, 10);
    if (direction == null)
        direction = (direction = Math.round(Math.random() * 11)) == 11 ? 10 : direction;

    if (direction == Cyan.Render.Fade.LEFT || direction == Cyan.Render.Fade.RIGHT ||
            direction == Cyan.Render.Fade.HORIZONTAL || direction >= Cyan.Render.Fade.CENTER)
    {
        this.element.targetWidth = this.originalWidth =
                (this.originalWidth = Cyan.toInt(this.element.style.width)) ? this.originalWidth :
                        this.element.clientWidth;
        this.targetWidth = 0;
        if (direction != Cyan.Render.Fade.RIGHT && direction != Cyan.Render.Fade.RIGHTUP &&
                direction != Cyan.Render.Fade.RIGHTDOWN)
        {
            this.element.targetLeft =
                    this.originalLeft =
                            (this.originalLeft = this.element.style.left) ? Cyan.toInt(this.originalLeft) : 0;
            this.targetLeft = this.originalLeft +
            (direction == Cyan.Render.Fade.CENTER || direction == Cyan.Render.Fade.HORIZONTAL ?
            this.originalWidth / 2 : this.originalWidth);
        }
    }
    if (direction == Cyan.Render.Fade.UP || direction == Cyan.Render.Fade.DOWN ||
            direction == Cyan.Render.Fade.VERTICAL || direction >= Cyan.Render.Fade.CENTER)
    {
        this.element.targetHeight = this.originalHeight =
                (this.originalHeight = Cyan.toInt(this.element.style.height)) ?
                        this.originalHeight :
                        this.element.clientHeight;
        this.targetHeight = 0;
        if (direction != Cyan.Render.Fade.DOWN && direction != Cyan.Render.Fade.LEFTDOWN &&
                direction != Cyan.Render.Fade.RIGHTDOWN)
        {
            this.element.targetTop =
                    this.originalTop = (this.originalTop = this.element.style.top) ? Cyan.toInt(this.originalTop) : 0;
            this.targetTop = this.originalTop +
            (direction == Cyan.Render.Fade.CENTER || direction == Cyan.Render.Fade.VERTICAL ?
            this.originalHeight / 2 : this.originalHeight);
        }
    }

    this.fade0(speed);

    this.direction = direction;
};

/**
 * 漂移到另外一个位置和大小
 * @param range 目标区域
 * @param speed  速度
 */
Cyan.Render.PositionFade.prototype.fadeRange = function (range, speed)
{
    if (this.element.fading$ != null)
        return;

    //默认熟读为2
    speed = Cyan.toInteger(speed, 2, 1);

    //计算原始位置和目标位置
    this.originalLeft = (this.originalLeft = this.element.style.left) ? Cyan.toInt(this.originalLeft) : 0;

    this.originalWidth =
            (this.originalWidth = this.element.style.width) ? Cyan.toInt(this.originalWidth) : this.element.clientWidth;

    this.originalTop = (this.originalTop = this.element.style.top) ? Cyan.toInt(this.originalTop) : 0;

    this.originalHeight =
            (this.originalHeight = this.element.style.height) ? Cyan.toInt(this.originalHeight) :
                    this.element.clientHeight;

    this.targetLeft = range.left;
    this.targetWidth = range.width;
    this.targetTop = range.top;
    this.targetHeight = range.height;

    this.fade0(speed);
};

/**
 * 漂移到另外一个位置
 * @param x 位置的横坐标
 * @param y 位置的纵坐标
 * @param speed 速度
 */
Cyan.Render.PositionFade.prototype.move = function (x, y, speed)
{
    if (this.element.fading$ != null)
        return;

    //默认速度为2
    speed = Cyan.toInteger(speed, 2, 1);

    //计算目标位置
    this.originalLeft = (this.originalLeft = this.element.style.left) ? Cyan.toInt(this.originalLeft) : 0;
    this.originalTop = (this.originalTop = this.element.style.top) ? Cyan.toInt(this.originalTop) : 0;
    this.targetLeft = x;
    this.targetTop = y;

    this.fade0(speed);
};

/**
 * 渐变，通过原始区域和目标区域来控制渐变的效果
 * originalLeft,originalRight,originalWidth,originalHeight
 * targetLeft,targetRight,targetWidth,targetHeight
 * @param speed 速度
 */
Cyan.Render.PositionFade.prototype.fade0 = function (speed)
{
    //正在渐变，退出
    if (this.element.fading$ != null)
        return;

    //计算每次漂移的间隔，并初始化目前区域为原始区域，目前区域用left,right,with,height定义

    if (this.originalLeft != null)
        this.leftInterval = (this.targetLeft - (this.left = this.originalLeft)) / 3 * speed;

    if (this.originalWidth != null)
        this.widthInterval = (this.targetWidth - (this.width = this.originalWidth)) / 3 * speed;

    if (this.originalTop != null)
        this.topInterval = (this.targetTop - ( this.top = this.originalTop)) / 3 * speed;

    if (this.originalHeight != null)
        this.heightInterval = (this.targetHeight - ( this.height = this.originalHeight)) / 3 * speed;

    //隐藏滚动条
    if (this.widthInterval)
        this.element.style.overflowX = "hidden";
    if (this.heightInterval)
        this.element.style.overflowY = "hidden";

    //漂移的次数
    this.count = 10 / speed;

    //记录正在渐变
    this.element.fading$ = this;

    this.play();
};

/**
 * 每次漂移的动作，实现自Gradient
 * @param index 第n次动作
 */
Cyan.Render.PositionFade.prototype.next = function (index)
{
    if (this.leftInterval)
    {
        this.left =
                ((this.left += this.leftInterval) - this.targetLeft) * this.leftInterval < 0 ? this.left :
                        this.targetLeft;
        this.element.style.left = Math.round(this.left) + "px";
    }
    if (this.widthInterval)
    {
        this.width = ((this.width += this.widthInterval) - this.targetWidth) * this.widthInterval < 0 ? this.width :
                this.targetWidth;
        this.element.style.width = Math.round(this.width) + "px";
    }
    if (this.topInterval)
    {
        this.top = ((this.top += this.topInterval) - this.targetTop) * this.topInterval < 0 ? this.top : this.targetTop;
        this.element.style.top = Math.round(this.top) + "px";
    }
    if (this.heightInterval)
    {
        this.height =
                ((this.height += this.heightInterval) - this.targetHeight) * this.heightInterval < 0 ?
                        this.height :
                        this.targetHeight;
        this.element.style.height = Math.round(this.height) + "px";
    }

    if (index > this.count ||
            this.left == this.targetLeft && this.width == this.targetWidth && this.top == this.targetTop &&
            this.height == this.targetHeight)
    {
        //漂移结束，清除漂移状态
        this.element.fading$ = null;

        //漂移后高度和框度没了，隐藏之
        if (this.targetWidth == 0 || this.targetHeight == 0)
            this.element.style.display = "none";

        //情况漂移状态变量
        this.originalLeft = this.originalWidth = this.originalTop = this.originalHeight = null;
        this.left = this.width = this.top = this.height = null;
        this.targetLeft = this.targetWidth = this.targetTop = this.targetHeight = null;
        this.leftInterval = this.widthInterval = this.topInterval = this.heightInterval = null;

        //结束
        return false;
    }

    //未结束
    return true;
};

/**
 * 透明度渐变
 */
Cyan.Render.OpacityFade = Cyan.Class.extend(function (element)
{
    this.inherited(element);
}, Cyan.Render.Fade);

/**
 * 淡入
 * @param speed 速度，默认为2
 */
Cyan.Render.OpacityFade.prototype.fadeIn = function (speed)
{
    if (this.element.opacityFading$ != null)
        return;
    speed = Cyan.toInteger(speed, 2, 1);
    this.element.style.display = "";
    this.interval = 0.01 * speed;
    this.opacity = 0;
    this.element.opacityFading$ = this;
    this.play();
};

/**
 * 淡出
 * @param speed 速度，默认为2
 */
Cyan.Render.OpacityFade.prototype.fadeOut = function (speed)
{
    if (this.element.opacityFading$ != null)
        return;
    speed = Cyan.toInteger(speed, 2, 1);
    this.interval = -0.01 * speed;
    this.opacity = 1;
    this.element.opacityFading$ = this;
    this.play();
};

/**
 * 改变透明度
 */
Cyan.Render.OpacityFade.prototype.next = function ()
{
    this.opacity += this.interval;
    Cyan.Render.setOpacity(this.element, this.opacity);
    if (this.interval < 0 && this.opacity <= 0)
    {
        this.element.style.display = "none";
        this.element.opacityFading$ = null;
        return false;
    }
    else if (this.interval > 0 && this.opacity >= 1)
    {
        this.element.opacityFading$ = null;
        return false;
    }
    return true;
};

/**
 * 漂移
 */
Cyan.Render.Float = function (element)
{
    this.element = element;
};
Cyan.Render.float$ = function (element)
{
    return new Cyan.Render.Float(element);
};
Cyan.Render.Float.prototype.start = function (speed, random)
{
    if (this.element.floating$ == null)
    {
        speed = Cyan.toInteger(speed, 10, 1);
        this.x = (this.x = this.element.style.left) ? Cyan.toInt(this.x) : 0;
        this.y = (this.y = this.element.style.top) ? Cyan.toInt(this.y) : 0;
        var float$ = this;
        var interval = (interval = Math.round(10 / speed)) ? interval : 1;
        var r = interval * speed / 10;
        this.h = this.h0 = r;
        this.v = this.v0 = r;
        this.element.floating$ = setInterval(function ()
        {
            if (float$.element.floatPausing$)
            {
                float$.x = (float$.x = float$.element.style.left) ? Cyan.toInt(float$.x) : 0;
                float$.y = (float$.y = float$.element.style.top) ? Cyan.toInt(float$.y) : 0;
            }
            else
            {
                var parent;
                if (random)
                {
                    var r = Math.random();
                    if (r < 0.0025)
                        float$.h = float$.h ? 0 : float$.h0;
                    else if (r > 0.9925)
                        float$.h = float$.h ? -float$.h : float$.h0;
                    r = Math.random();
                    if (r < 0.0025)
                        float$.v = float$.v ? 0 : float$.v0;
                    else if (r > 0.9925)
                        float$.v = float$.v ? -float$.v : float$.v0;

                    if (!float$.v && !float$.h)
                    {
                        float$.h = float$.h0;
                        float$.v = float$.v0;
                    }
                }
                parent = Cyan.Elements.getOffsetParent(float$.element);
                float$.x += float$.h;
                float$.y += float$.v;
                var left = float$.left ? float$.left : parent.scrollLeft;
                if (float$.x <= left)
                {
                    float$.x = left;
                    float$.h = float$.h0;
                }
                else
                {
                    var right = float$.right ? float$.right : parent.clientWidth + parent.scrollLeft;
                    if (float$.x + float$.element.clientWidth >= right)
                    {
                        float$.x = right - float$.element.clientWidth;
                        float$.h = -float$.h0;
                    }
                }
                var top = float$.top ? float$.top : parent.scrollTop;
                if (float$.y <= top)
                {
                    float$.y = top;
                    float$.v = float$.v0;
                }
                else
                {
                    var bottom = float$.bottom ? float$.bottom : parent.clientHeight + parent.scrollTop;
                    if (float$.y + float$.element.clientHeight >= bottom)
                    {
                        float$.y = bottom - float$.element.clientHeight;
                        float$.v = -float$.v0;
                    }
                }
                float$.element.style.left = Math.round(float$.x) + "px";
                float$.element.style.top = Math.round(float$.y) + "px";
            }
        }, interval * 5);
    }
};
Cyan.Render.Float.prototype.pause = function (pause)
{
    Cyan.Render.Float.pause(this.element, pause);
};
Cyan.Render.Float.pause = function (element, pause)
{
    if (element.floating$ != null)
        element.floatPausing$ = pause;
};

/**
 * 固定元素在页面上的绝对位置
 * @param element 元素
 * @param fixed true为固定，false为取消固定
 * @param fading 移动是是否漂移
 */
Cyan.Render.makeFixed = function (element, fixed, fading)
{
    if (fixed == null)
        fixed = element.fixed$ == null;
    if (fixed && element.fixed$ == null)
    {
        var parent = Cyan.Elements.getOffsetParent(element);
        var lastScrollX = parent.scrollLeft, lastScrollX1 = lastScrollX;
        var lastScrollY = parent.scrollTop, lastScrollY1 = lastScrollY;
        var fade;
        if (fading)
            fade = new Cyan.Render.Fade(element);
        var change = false;
        element.fixed$ = setInterval(function ()
        {
            var scrollX = parent.scrollLeft;
            var scrollY = parent.scrollTop;
            if (scrollX != lastScrollX1 || scrollY != lastScrollY1)
            {
                lastScrollX1 = scrollX;
                lastScrollY1 = scrollY;
                change = true;
            }
            else if (change && !element.fading$)
            {
                var x = ((x = element.style.left) ? Cyan.toInt(x) : 0);
                var y = ((y = element.style.top) ? Cyan.toInt(y) : 0);
                if (fading)
                    fade.fadeRange({
                        left: x + scrollX - lastScrollX, top: y + scrollY -
                        lastScrollY, width: element.clientWidth, height: element.clientHeight
                    });
                else
                {
                    element.style.left = (x + scrollX - lastScrollX) + "px";
                    element.style.top = (y + scrollY - lastScrollY) + "px";
                }
                lastScrollX = scrollX;
                lastScrollY = scrollY;
                change = false;
            }
        }, 90);
    }
    else if (!fixed && element.fixed$ != null)
    {
        clearInterval(element.fixed$);
    }
};

/**
 * 高亮
 */
Cyan.Render.highlightText = function (text, keys, index, cssText, className)
{
    var key = keys[index], ss = text.split(key), result = "", s;
    for (var i = 0; i < ss.length; i++)
    {
        if (i > 0)
        {
            if (i == 1)
            {
                s = "<font";
                if (cssText)
                    s += " style=\"" + cssText + "\"";
                if (className)
                    s += " class=\"" + className + "\"";
                s += ">" + key + "</font>";
            }
            result += s;
        }
        result += index == keys.length - 1 ? ss[i] : highlightText(text, keys, index + 1, cssText, className);
    }
    return result;
};
Cyan.Render.highlight = function (keys, style, element)
{
    if (!(keys instanceof Array))
        keys = [keys];
    if (!element)
        element = document.body;
    var cssText = style.indexOf(":") > 0 ? style : null, className = cssText ? null : style;
    Cyan.Elements.eachElement(element, function ()
    {
        if (this.nodeType == 3)
        {
            var text = this.nodeValue;
            var highlightText = Cyan.Render.highlightText(text, keys, 0, cssText, className);
            if (highlightText != text)
            {
                var node = document.createElement("font");
                node.innerHTML = highlightText;
                this.parentNode.replaceChild(node, element);
            }
        }
    });
};

/**
 * 闪烁
 */
Cyan.Render.Flash = function (element, flash)
{
    this.element = element;
    if (flash instanceof Function)
        this.flash = flash;
    else if (flash.indexOf(":") <= 0)
    {
        this.className = flash;
        this.className0 = element.className;
        this.flash = function (flash)
        {
            if (!Cyan.Render.Flash.stoped)
            {
                if (flash)
                    this.element.className = this.className;
                else
                    this.element.className = this.className0;
            }
        };
    }
    else
    {
        this.style = new Cyan.Style(flash);
        this.style0 = element.style.cssText;
        this.flash = function (flash)
        {
            if (!Cyan.Render.Flash.stoped)
            {
                if (flash)
                    this.style.set(this.element);
                else
                    this.element.style.cssText = this.style0;
            }
        };
    }
    this.flashed = false;
    var flashObject = this;
    this.refresh = function ()
    {
        flashObject.flash(flashObject.flashed = !flashObject.flashed);
    };

    Cyan.Render.Flash.flashs.push(this);
    this.started = true;
};

Cyan.Render.Flash.flashs = [];

Cyan.Render.Flash.stoped = false;
Cyan.Render.Flash.stopCount = 0;
Cyan.Render.Flash.prototype.start = function ()
{
    if (!this.element.flash$)
    {
        this.element.flash$ = this;
        if (!Cyan.Render.Flash.stoped)
            this.interval = window.setInterval(this.refresh, 300);
        this.started = true;
    }
    return this;
};
Cyan.Render.Flash.prototype.stop = function ()
{
    if (this.interval)
    {
        window.clearInterval(this.interval);
        this.interval = null;
        this.flash(this.flashed = false);
    }
    if (this.element.flash$ == this)
        this.element.flash$ = null;
    this.started = false;
    return this;
};
Cyan.Render.Flash.stop = function (element)
{
    if (element)
    {
        return element.flash$ ? element.flash$.stop() : null;
    }
    else
    {
        if (!Cyan.Render.Flash.stopCount)
        {
            Cyan.Array.forEach(Cyan.Render.Flash.flashs, function (flash)
            {
                if (flash.started)
                {
                    window.clearInterval(flash.interval);
                    flash.interval = null;
                }
            });
        }

        Cyan.Render.Flash.stoped = true;
        Cyan.Render.Flash.stopCount++;
    }
};
Cyan.Render.Flash.flash = function (element, flash)
{
    return flash ? new Cyan.Render.Flash(element, flash).start() : Cyan.Render.Flash.stop(element);
};
Cyan.Render.Flash.start = function ()
{
    if (--Cyan.Render.Flash.stopCount == 0)
    {
        Cyan.Render.Flash.stoped = false;

        Cyan.Array.forEach(Cyan.Render.Flash.flashs, function (flash)
        {
            if (flash.started)
            {
                flash.interval = window.setInterval(flash.refresh, 300);
            }
        });
    }
};

Cyan.Elements.prototype.setOpacity = function (opacity)
{
    this.each(function ()
    {
        Cyan.Render.setOpacity(this, opacity);
    });
    return this;
};

Cyan.Elements.prototype.getFades = function ()
{
    if (this.fades == null)
    {
        this.fades = this.get(function ()
        {
            return new Cyan.Render.PositionFade(this);
        });
    }
    return this.fades;
};
Cyan.Elements.prototype.fadeIn = function (direction, speed)
{
    Cyan.each(this.getFades(), function ()
    {
        this.fadeIn(direction, speed);
    });
    return this;
};
Cyan.Elements.prototype.fadeOut = function (direction, speed)
{
    Cyan.each(this.getFades(), function ()
    {
        this.fadeOut(direction, speed);
    });
    return this;
};
Cyan.Elements.prototype.fade = function (direction, speed)
{
    Cyan.each(this.getFades(), function ()
    {
        this.fade(direction, speed);
    });
    return this;
};
Cyan.Elements.prototype.fadeRange = function (range, speed)
{
    Cyan.each(this.getFades(), function ()
    {
        this.fadeRange(range, speed);
    });
    return this;
};
Cyan.Elements.prototype.fadeMove = function (x, y, speed)
{
    Cyan.each(this.getFades(), function ()
    {
        this.move(x, y, speed);
    });
    return this;
};

Cyan.Elements.prototype.getOpacityFades = function ()
{
    if (this.opactityFades == null)
        this.opactityFades = this.get(function ()
        {
            return new Cyan.Render.OpacityFade(this);
        });
    return this.opactityFades;
};
Cyan.Elements.prototype.opacityFadeIn = function (speed)
{
    Cyan.each(this.getOpacityFades(), function ()
    {
        this.fadeIn(speed);
    });
    return this;
};
Cyan.Elements.prototype.opacityFadeOut = function (speed)
{
    Cyan.each(this.getOpacityFades(), function ()
    {
        this.fadeOut(speed);
    });
    return this;
};
Cyan.Elements.prototype.opacityFade = function (speed)
{
    Cyan.each(this.getOpacityFades(), function ()
    {
        this.fade(speed);
    });
    return this;
};
Cyan.Elements.prototype.startFloat = function (speed, range, random)
{
    Cyan.each(this.get(function ()
    {
        return new Cyan.Render.Float(this);
    }), function ()
    {
        if (range)
        {
            this.left = range.left;
            this.top = range.top;
            this.right = range.right;
            this.bottom = range.bottom;
        }
        this.start(speed, random);
    });
    return this;
};
Cyan.Elements.prototype.pauseFloat = function (pause)
{
    this.each(function ()
    {
        Cyan.Render.Float.pause(this, pause);
    });
    return this;
};
Cyan.Elements.prototype.makeFixed = function (fixed, fading)
{
    this.each(function ()
    {
        Cyan.Render.makeFixed(this, fixed, fading);
    });
    return this;
};
Cyan.Elements.prototype.flash = function (flash)
{
    return this.get(function ()
    {
        return Cyan.Render.Flash.flash(this, flash);
    });
};

/**
 * 装饰
 * @param style 装饰后的样式
 * @param getEvent 开始装饰的事件
 * @param lostEvent 取消装饰的事件
 */
Cyan.Elements.prototype.decorate = function (style, getEvent, lostEvent)
{
    var className = style.indexOf(":") <= 0;
    if (!className)
        style = new Cyan.Style(style);
    this.each(function ()
    {
        var style0;
        var element = this;
        Cyan.attach(this, getEvent, className ? function ()
        {
            style0 = element.className;
            element.className = style;
        } : function ()
        {
            style0 = element.style.cssText;
            style.set(element);
        });
        if (lostEvent)
        {
            Cyan.attach(element, lostEvent, className ? function ()
            {
                element.className = style0;
            } : function ()
            {
                element.style.cssText = style0;
            });
        }
    });
    return this;
};

Cyan.Elements.prototype.hover = function (style)
{
    this.decorate(style, "mouseover", "mouseout");
    return this;
};

Cyan.Elements.prototype.focus = function (style)
{
    if (!style)
    {
        if (this.first && this.first.focus)
            this.first.focus();
    }
    else
    {
        this.decorate(style, "focus", "blur");
    }
    return this;
};

/**
 * 提示输入
 * @param element  输入框
 * @param prompt 提示的文字
 * @param style 提示文字的样式
 */
Cyan.Elements.promptInput = function (element, prompt, style)
{
    if ((element.nodeName == "TEXTAREA" || (element.nodeName == "INPUT" && element.type == "text")) &&
            !element.readOnly && !element.disabled)
    {
        if (!prompt)
            prompt = element.prompt || element.getAttribute("prompt");
        element.emptyValue = prompt;
        if (element.value == "" || element.value == prompt)
        {
            if (!style)
                style = "color:gray;font-style:italic";
            element.value = prompt;

            if (element.form)
            {
                Cyan.attach(element.form, "submit", function ()
                {
                    if (element.value == prompt)
                        element.value = "";
                });
            }

            if (style.indexOf(":") <= 0)
            {
                Cyan.$$(element).addClass(style);
                Cyan.attach(element, "focus", function ()
                {
                    if (element.value == prompt)
                    {
                        element.value = "";
                        Cyan.$$(element).removeClass(style);
                        if (element.select instanceof Function)
                            element.select();
                    }
                });
                Cyan.attach(element, "blur", function ()
                {
                    if (element.value == "")
                    {
                        element.value = prompt;
                        Cyan.$$(element).addClass(style);
                    }
                });
            }
            else
            {
                style = new Cyan.Style(style);
                var style0 = new Cyan.Style(element.style.cssText);
                var style1 = new Cyan.Style("");
                for (var name in style)
                {
                    if (!(style[name] instanceof Function))
                        style1[name] = style0[name];
                }

                style.set(element);
                Cyan.attach(element, "focus", function ()
                {
                    if (element.value == prompt)
                    {
                        element.value = "";
                        style1.set(element);
                        if (element.select instanceof Function)
                            element.select();
                    }
                });
                Cyan.attach(element, "blur", function ()
                {
                    if (element.value == "")
                    {
                        setTimeout(function ()
                        {
                            element.value = prompt;
                            style.set(element);
                        }, 10);
                    }
                });
            }
        }
    }
};

Cyan.Elements.prototype.promptInput = function (prompt, style)
{
    this.each(function ()
    {
        Cyan.Elements.promptInput(this, prompt, style);
    });
};