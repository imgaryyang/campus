package com.gzzm.platform.commons.crud;

import net.cyan.crud.view.BaseTreeView;

/**
 * 可选择的树视图，一般用于界面的左边
 *
 * @author camel
 * @date 2009-12-27
 */
public class SelectableTreeView extends BaseTreeView implements SelectableView
{
    public SelectableTreeView()
    {
        enableSupportSearch();
    }

    public void setOnViewSelect(Object onviewselect)
    {
        setOnTreeSelect(onviewselect);
    }

    public void enableViewDrop(Object ondrop)
    {
        setEnableDrop(true);
        setOnDrop(ondrop);
        setOnDragOver("return Cyan.Widget.DragContext.dropPoint==\"append\";");
    }
}
