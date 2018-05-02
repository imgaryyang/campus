package com.gzzm.safecampus.campus.common;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.commons.util.BeanUtils;

/**
 * 子列表维护
 *
 * @author camel
 * @date 2010-12-9
 */
public abstract class ScSubListCrud<E, K> extends BaseNormalCrud<E, K>
{
    private boolean readOnly;

    /**
     * 是否能修改 默认能编辑
     */
    private boolean editable = true;

    protected boolean showAdd = true;

    public ScSubListCrud()
    {
        setLog(true);
        setPageSize(-1);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public boolean isShowAdd() {
        return showAdd;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    /**
     * 关联父列表的属性，可由子类实现
     *
     * @return 关联父列表的属性
     */
    protected abstract String getParentField();

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{getParentField()};
    }

    @Override
    public void initEntity(E entity) throws Exception
    {
        super.initEntity(entity);

        setParentValue(entity);
    }

    protected Object getParentValue() throws Exception
    {
        String parentField = getParentField();

        return BeanUtils.getValue(this, parentField);
    }

    protected void setParentValue(E entity, Object parentValue) throws Exception
    {
        String parentField = getParentField();

        if (parentValue == null)
            throw new SystemException(parentField + " may not be null");

        BeanUtils.setValue(entity, parentField, parentValue);
    }

    protected void setParentValue(E entity) throws Exception
    {
        setParentValue(entity, getParentValue());
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        //不允许修改父字段的值
        BeanUtils.setValue(getEntity(), getParentField(), null);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ScSubListView view = new ScSubListView(getParentField());

        view.setOrderable(getOrderField() != null);
        view.setReadOnly(readOnly);
        view.setShowAdd(showAdd);
        view.importCss("/safecampus/campus/base/crud/sublist.css");

        initListView(view);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        initShowView(view);
        view.addDefaultButtons();
        view.importJs("/safecampus/campus/base/crud/sublist.js");
        return view;
    }

    //增加修改成功样式
    protected abstract void initShowView(SimpleDialogView view) throws Exception;

    protected abstract void initListView(ScSubListView view) throws Exception;
}
