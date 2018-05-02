package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.crud.Crud;

/**
 * @author camel
 * @date 2017/2/14
 */
public class ComplexPanelView extends PagePanelView
{
    /**
     * 用于显示左边视图的crud
     */
    private Crud left;

    public ComplexPanelView()
    {
    }

    public ComplexPanelView(Crud left, String field) throws Exception
    {
        super();
        this.left = left;

        Object view = left.getView();
        if (view instanceof SelectableView)
            ((SelectableView) view).setOnViewSelect(Actions.leftSelect(field));
    }

    public ComplexPanelView(Class<? extends Crud> leftType, String field) throws Exception
    {
        this(Tools.getBean(leftType), field);
    }

    public Crud getLeft()
    {
        return left;
    }
}
