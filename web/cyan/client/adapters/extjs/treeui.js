Ext.ux.CyanTreeNodeUI = Ext.extend(Ext.tree.TreeNodeUI, {
    renderElements:function(n, a, targetNode, bulkRender)
    {
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';
        var checked = this.isBoxChecked(n, a);
        var icon = this.getIcon(n, a);
        var iconCls = this.getIconCls(n, a);
        var href = this.getHref(n, a);
        var hrefTarget = this.getHrefTarget(n, a);
        var textCls = this.getTextCls(n, a);
        var textStyle = this.getTextStyle(n, a);
        var tree = n.getOwnerTree();
        var cb = Cyan.isBoolean(checked);
        href = href ? href : Ext.isGecko ? "" : "#";
        textCls = textCls ? textCls : "x-tree-node-anchor";
        var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,
            '" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">',
            '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
            '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
            '<img src="', icon || this.emptyIcon, '" class="x-tree-node-icon',(icon ? " x-tree-node-inline-icon" : ""),(
                    iconCls ? " " + iconCls : ""),'" unselectable="on" />',
            cb ? ('<input class="x-tree-node-cb" type="' + tree.checkMode + '" name="' + tree.checkboxName +
                    '" value="' + a.id + '"' + (checked ? 'checked="checked" />' : '/>')) :
                    '', '<a hidefocus="on" ' + (textStyle ? 'style="' + textStyle + '"' : '') + ' class="' + textCls +
                    '" href="',href,'" tabIndex="1" ',hrefTarget ? ' target="' + hrefTarget + '"' : "",
            '><span unselectable="on">',n.text,'</span></a></div>',
            '<ul class="x-tree-node-ct" style="display:none;"></ul>','</li>'].join('');
        var nel;
        if (bulkRender !== true && n.nextSibling && (nel = n.nextSibling.ui.getEl()))
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
        else
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1];
        var cs = this.elNode.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        var index = 3;
        if (cb)
        {
            this.checkbox = cs[3];
            this.checkbox.defaultChecked = this.checkbox.checked;
            if (tree.checkMode == "radio")
                this.checkbox.onmouseup = this.radioMouseUp;
            this.checkbox.node = n;
            Cyan.attach(this.checkbox, "click", this.checkboxclick);
            index++;
        }
        this.anchor = cs[index];
        this.textNode = cs[index].firstChild;
    },
    isBoxChecked:function(n, a)
    {
        return a.checked;
    },
    getIcon:function(n, a)
    {
        return a.icon || n.getOwnerTree().icon;
    },
    getIconCls:function(n, a)
    {
        return a.iconCls || n.getOwnerTree().iconCls;
    },
    getHref:function(n, a)
    {
        return a.href;
    },
    getHrefTarget:function(n, a)
    {
        return a.hrefTarget || n.getOwnerTree().hrefTarget;
    },
    getTextCls:function(n, a)
    {
        return a.textCls || n.getOwnerTree().textCls;
    },
    getTextStyle:function(n, a)
    {
        return a.textStyle || n.getOwnerTree().textStyle;
    },
    radioMouseUp:function()
    {
        this.checked = true;
    },
    checkboxclick:function()
    {
        var tree = this.node.getOwnerTree();
        if (tree.oncheck)
            tree.oncheck(this.node);
    }
});