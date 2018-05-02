package com.gzzm.platform.commons.components;

import com.gzzm.platform.commons.CommonDao;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.crud.util.CrudBeanUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-9-5
 */
public abstract class EntityPageTreeModel<E> implements LazyPageTreeModel<E>, SearchablePageTreeModel<E>
{
    @Inject
    private static Provider<CommonDao> commonDaoProvider;

    private CommonDao commonDao;

    private Class<E> entityType;

    private Class keyType;

    private PropertyInfo childrenPropertyInfo;

    private E root;

    private Map<String, List<E>> childrenMap;

    private String alias;

    private String searchQueryString;

    private String parentField;

    public EntityPageTreeModel()
    {
    }

    protected abstract Object getRootKey() throws Exception;

    protected CommonDao getCommonDao() throws Exception
    {
        if (commonDao == null)
            commonDao = commonDaoProvider.get();

        return commonDao;
    }

    @SuppressWarnings("unchecked")
    protected Class<E> getEntityType() throws Exception
    {
        if (entityType == null)
            entityType = BeanUtils.toClass(BeanUtils.getRealType(EntityPageTreeModel.class, "E", this.getClass()));

        return entityType;
    }

    protected Class getKeyType() throws Exception
    {
        if (keyType == null)
            keyType = CrudConfig.getPersistenceDialect().getKeyType(getEntityType());

        return keyType;
    }

    protected PropertyInfo getChildrenPropertyInfo() throws Exception
    {
        if (childrenPropertyInfo == null)
        {
            Class<E> entityType = getEntityType();
            childrenPropertyInfo = BeanUtils
                    .getProperty(entityType, CrudConfig.getPersistenceDialect().getChildrenField(entityType));
        }

        return childrenPropertyInfo;
    }

    public boolean isLazyLoad(E e) throws Exception
    {
        return true;
    }

    @NotSerialized
    public E getRoot() throws Exception
    {
        if (root == null)
        {
            Object rootKey = getRootKey();
            root = getCommonDao().load(getEntityType(), rootKey);

            if (root == null)
            {
                E root1 = createRoot();

                if (root1 != null)
                {
                    getCommonDao().save(root1);

                    root = getCommonDao().load(getEntityType(), rootKey);
                }
            }
        }

        return root;
    }

    protected E createRoot() throws Exception
    {
        return null;
    }

    protected List<E> getChildren(E parent) throws Exception
    {
        List<E> children;
        String id = getId(parent);
        if (childrenMap == null)
        {
            childrenMap = new HashMap<String, List<E>>();
            children = null;
        }
        else
        {
            children = childrenMap.get(id);
        }

        if (children == null)
            childrenMap.put(id, children = loadChildren(parent));

        return children;
    }

    @SuppressWarnings("unchecked")
    protected List<E> loadChildren(E parent) throws Exception
    {
        return (List<E>) getChildrenPropertyInfo().get(parent);
    }

    public boolean isLeaf(E e) throws Exception
    {
        return getChildCount(e) == 0;
    }

    public int getChildCount(E parent) throws Exception
    {
        int n = 0;
        List<E> children = getChildren(parent);
        for (E child : children)
        {
            if (accept(child))
                n++;
        }

        return n;
    }

    public E getChild(E parent, int index) throws Exception
    {
        int n = 0;
        List<E> children = getChildren(parent);
        for (E child : children)
        {
            if (accept(child))
            {
                if (n == index)
                    return child;

                n++;
            }
        }

        return null;
    }

    public String getId(E e) throws Exception
    {
        return DataConvert.getValueString(e);
    }

    public String toString(E e) throws Exception
    {
        return DataConvert.toString(e);
    }

    @SuppressWarnings("unchecked")
    public E getNode(String id) throws Exception
    {
        return (E) getCommonDao().get(getEntityType(), DataConvert.convertValue(getKeyType(), id));
    }

    @SuppressWarnings("UnusedDeclaration")
    protected boolean accept(E e) throws Exception
    {
        return true;
    }

    public void beforeLazyLoad(String id) throws Exception
    {
    }

    protected String getTextField() throws Exception
    {
        return null;
    }

    protected String getSearchCondition() throws Exception
    {
        String textField = getTextField();

        if (textField != null)
            return textField + " like :text";

        return null;
    }

    protected String getSearchQueryString() throws Exception
    {
        if (searchQueryString == null)
        {
            String condition = getSearchCondition();
            if (condition != null)
            {
                String alias = getAlias();
                searchQueryString = "select " + alias + " from " + CrudBeanUtils.getClassWord(getEntityType()) + " " +
                        alias + " where " + condition;
            }
        }

        return searchQueryString;
    }

    protected String getAlias() throws Exception
    {
        if (alias == null)
            alias = getEntityType().getSimpleName().toLowerCase();

        return alias;
    }

    public Collection<E> search(String text) throws Exception
    {
        String queryString = getSearchQueryString();
        if (queryString != null)
        {
            Map<String, String> map = new HashMap<String, String>(2);
            map.put("text", "%" + text + "%");
            map.put("text1", text + "%");
            return getCommonDao().oqlQuery(queryString, map);
        }

        return null;
    }

    @NotSerialized
    public boolean isSearchable() throws Exception
    {
        return getSearchQueryString() != null;
    }

    /**
     * 关联父对象的field
     *
     * @return 关联父对象的field
     * @throws Exception 异常
     */
    protected String getParentField() throws Exception
    {
        if (parentField == null)
        {
            Class<?> entityType = getEntityType();
            parentField = CrudConfig.getPersistenceDialect().getParentKeyField(entityType);

            if (parentField == null)
                throw new CrudException("cannot find the parent key field for " + BeanUtils.getClassName(entityType) +
                        ",please override getParentField");
        }

        return parentField;
    }

    @SuppressWarnings("unchecked")
    protected Object getParentKey(E entity) throws Exception
    {
        return BeanUtils.getValue(entity, getParentField());
    }

    public E getParent(E e) throws Exception
    {
        Object parentKey = getParentKey(e);
        if (parentKey == null)
            return null;

        return getCommonDao().get(getEntityType(), parentKey);
    }
}