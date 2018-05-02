package com.gzzm.platform.commons.crud;

import net.cyan.crud.view.BaseListView;

/**
 * 可选择的列表视图，一般用于界面的左边
 *
 * @author camel
 * @date 2010-3-18
 */
public class SelectableListView extends BaseListView implements SelectableView
{
    public SelectableListView()
    {
    }

    public void setOnViewSelect(Object onviewselect)
    {
        setOnListSelect(onviewselect);
    }

    public void enableViewDrop(Object ondrop)
    {
        setEnableDrop(true);
        setOnDrop(ondrop);
        setOnDragOver("return Cyan.Widget.DragContext.dropPoint==\"append\";");
    }
}
