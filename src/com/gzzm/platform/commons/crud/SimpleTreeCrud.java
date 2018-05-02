package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.BeanUtils;
import net.cyan.crud.CrudDao;

/**
 * @author camel
 * @date 2018/3/28
 */
@Service
public abstract class SimpleTreeCrud<E> extends BaseTreeCrud<E, Integer>
{
    private String nameField;

    private String nameTitle;

    public SimpleTreeCrud(String nameField, String nameTitle)
    {
        this.nameField = nameField;
        this.nameTitle = nameTitle;
        setLog(true);
    }

    @Override
    public E getRoot() throws Exception
    {
        Class<E> entityType = getEntityType();
        CrudDao dao = getCrudService().getDao();
        E entity = dao.get(entityType, 0);

        if (entity == null)
        {
            entity = entityType.newInstance();
            setKey(entity, 0);
            initRoot(entity);
            dao.add(entity);
        }

        return entity;
    }

    protected void setEntityName(E entity, String name) throws Exception
    {
        BeanUtils.setValue(entity, nameField, name, getEntityType());
    }

    protected void initRoot(E root) throws Exception
    {
        setEntityName(root, "根节点");
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent(nameTitle, nameField);

        view.addDefaultButtons();

        return view;
    }
}
