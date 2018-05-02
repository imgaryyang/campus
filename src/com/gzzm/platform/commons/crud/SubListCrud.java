package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.SystemException;
import net.cyan.commons.util.BeanUtils;

/**
 * 子列表维护
 *
 * @author camel
 * @date 2010-12-9
 */
public abstract class SubListCrud<E, K> extends BaseNormalCrud<E, K>
{
    private boolean readOnly;

    public SubListCrud()
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
        SubListView view = new SubListView(getParentField());

        view.setOrderable(getOrderField() != null);
        view.setReadOnly(readOnly);

        initListView(view);

        return view;
    }

    protected abstract void initListView(SubListView view) throws Exception;
}
