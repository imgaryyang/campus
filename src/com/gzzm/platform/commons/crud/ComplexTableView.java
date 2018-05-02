package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.crud.*;
import net.cyan.crud.view.BaseTableView;

/**
 * 复杂的表格视图，左边一个树或者列表，或者其它视图，右边一个表格
 *
 * @author camel
 * @date 2009-12-26
 */
public class ComplexTableView extends PageTableView
{
    /**
     * 用于显示左边视图的crud
     */
    private Crud left;

    public ComplexTableView(Crud left, String field, BaseTableView body) throws Exception
    {
        super(body);
        this.left = left;

        Object view = left.getView();
        if (view instanceof SelectableView)
            ((SelectableView) view).setOnViewSelect(Actions.leftSelect(field));
    }

    public ComplexTableView(Crud left, String field, boolean checkable) throws Exception
    {
        this(left, field, new BaseTableView(checkable));
    }

    public ComplexTableView(Crud left, String field) throws Exception
    {
        this(left, field, true);
    }

    public ComplexTableView(Class<? extends Crud> leftType, String field, boolean checkable) throws Exception
    {
        this(Tools.getBean(leftType), field, new BaseTableView(checkable));
    }

    public ComplexTableView(Class<? extends Crud> leftType, String field) throws Exception
    {
        this(Tools.getBean(leftType), field, true);
    }

    public ComplexTableView(Crud left)
    {
        this.left = left;
    }

    public Crud getLeft()
    {
        return left;
    }

    public ComplexTableView enableDD()
    {
        return enableDD(true);
    }

    public ComplexTableView enableDD(boolean copyable)
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if (crud instanceof OwnedCrud && (CrudAuths.isModifiable() || CrudAuths.isAddable()))
        {
            Object view;
            try
            {
                view = left.getView();
            }
            catch (Exception ex)
            {
                //之前已经调用过getView成功，这里产生异常的可能性很小
                Tools.wrapException(ex);
                return null;
            }

            if (view instanceof SelectableView)
            {
                setEnableDrag(true);
                ((SelectableView) view).enableViewDrop(Actions.moveToLeft(copyable));
            }
        }

        return this;
    }

    @Override
    public PageTableView defaultInit(String editForward, boolean duplicate)
    {
        super.defaultInit(editForward, duplicate);

        enableDD();

        return this;
    }
}
