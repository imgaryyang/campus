Ext.grid.RowExpander = function (config)
{
    Ext.apply(this, config);

    this.addEvents({
        beforeexpand: true,
        expand: true,
        beforecollapse: true,
        collapse: true
    });

    Ext.grid.RowExpander.superclass.constructor.call(this);

    this.state = {};
    this.bodyContent = {};
};

Ext.extend(Ext.grid.RowExpander, Ext.util.Observable, {
    header: "",
    width: 20,
    sortable: false,
    fixed: true,
    menuDisabled: true,
    dataIndex: '',
    id: 'expander',
    lazyRender: true,
    enableCaching: true,

    getRowClass: function (record, rowIndex, p, ds)
    {
        p.cols = p.cols - 1;
        var content = this.bodyContent[record.id];
        if (!content && !this.lazyRender)
        {
            content = this.getBodyContent(record, rowIndex);
        }
        if (content)
        {
            p.body = content;
        }
        var className = this.state[record.id] ? 'x-grid3-row-expanded' : 'x-grid3-row-collapsed';

        var s = this.table.getRowClass(this.table.getRecord(rowIndex));
        if (s)
            className += " " + s;

        return className;
    },

    init: function (grid)
    {
        this.grid = grid;

        var view = grid.getView();
        view.getRowClass = this.getRowClass.createDelegate(this);

        view.enableRowBody = true;

        grid.on('render', function ()
        {
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    getBodyContent: function (record, index)
    {
        if (!this.enableCaching)
        {
            return record.data.remark$;
        }
        var content = this.bodyContent[record.id];
        if (!content)
        {
            content = record.data.remark$;

            if (this.bodyRenderer)
                content = this.bodyRenderer(content);

            this.bodyContent[record.id] = content;
        }

        return content;
    },

    onMouseDown: function (e, t)
    {
        if (t.className == 'x-grid3-row-expander')
        {
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            this.toggleRow(row);
        }
    },

    renderer: function (v, p, record)
    {
        p.cellAttr = 'rowspan="2"';

        if (record.data.remark$)
            return '<div class="x-grid3-row-expander">&#160;</div>';
        else
            return "<div>&#160;</div>";
    },

    beforeExpand: function (record, body, rowIndex)
    {
        if (this.fireEvent('beforeexpand', this, record, body, rowIndex) !== false)
        {
            if (this.lazyRender)
            {
                body.innerHTML = this.getBodyContent(record, rowIndex);
            }
            return true;
        }
        else
        {
            return false;
        }
    },

    toggleRow: function (row)
    {
        if (typeof row == 'number')
        {
            row = this.grid.view.getRow(row);
        }
        this[Ext.fly(row).hasClass('x-grid3-row-collapsed') ? 'expandRow' : 'collapseRow'](row);
    },

    expandRow: function (row)
    {
        if (typeof row == 'number')
        {
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.DomQuery.selectNode('tr:nth(2) div.x-grid3-row-body', row);
        if (this.beforeExpand(record, body, row.rowIndex))
        {
            this.state[record.id] = true;
            Ext.fly(row).replaceClass('x-grid3-row-collapsed', 'x-grid3-row-expanded');
            this.fireEvent('expand', this, record, body, row.rowIndex);
        }
    },

    collapseRow: function (row)
    {
        if (typeof row == 'number')
        {
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.fly(row).child('tr:nth(1) div.x-grid3-row-body', true);
        if (this.fireEvent('beforecollapse', this, record, body, row.rowIndex) !== false)
        {
            this.state[record.id] = false;
            Ext.fly(row).replaceClass('x-grid3-row-expanded', 'x-grid3-row-collapsed');
            this.fireEvent('collapse', this, record, body, row.rowIndex);
        }
    }
});
