package com.gzzm.platform.commons.crud;

import net.cyan.crud.view.CrudView;

/**
 * 可选择的view，如可选的树，列表等
 *
 * @author camel
 * @date 2009-12-27
 */
public interface SelectableView extends CrudView
{
    public void setOnViewSelect(Object onviewselect);

    public void enableViewDrop(Object ondrop);
}