package com.gzzm.platform.commons.components;

import com.gzzm.platform.commons.CommonDao;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.crud.util.CrudBeanUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2014/12/4
 */
@Service
public abstract class EntityPageOwendSelector<NE, IE> extends PageOwnedSelector
{
    @Inject
    private static Provider<CommonDao> commonDaoProvider;

    private CommonDao commonDao;

    private Class<NE> nodeEntityClass;

    private Class<IE> itemEntityClass;

    private Class nodeKeyClass;

    private Class itemKeyClass;

    private PropertyInfo childrenPropertyInfo;

    private Map<String, List<NE>> childrenMap;

    private String nodeAlias;

    private String itemAlias;

    private String nodeSearchQueryString;

    private String parentField;

    private String ownerField;

    @NotSerialized
    private Object ownerKey;

    @NotSerialized
    private String text;

    private String itemQueryString;

    protected List<OrderBy> orderBys;

    private String itemSearchQueryString;

    public EntityPageOwendSelector()
    {
    }

    public Object getOwnerKey()
    {
        return ownerKey;
    }

    protected void setOwnerKey(Object ownerKey)
    {
        this.ownerKey = ownerKey;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    protected void addOrderBy(OrderBy orderBy)
    {
        if (orderBys == null)
            orderBys = new ArrayList<OrderBy>(5);
        orderBys.add(orderBy);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void addOrderBy(String field, OrderType type)
    {
        addOrderBy(new OrderBy(field, type));
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void addOrderBy(String field)
    {
        addOrderBy(new OrderBy(field));
    }

    public List<OrderBy> getOrderBys()
    {
        return orderBys;
    }

    protected abstract Object getRootKey() throws Exception;

    protected CommonDao getCommonDao() throws Exception
    {
        if (commonDao == null)
            commonDao = commonDaoProvider.get();

        return commonDao;
    }

    @SuppressWarnings("unchecked")
    private Class<NE> getNodeEntityClass() throws Exception
    {
        if (nodeEntityClass == null)
            nodeEntityClass = BeanUtils.toClass(BeanUtils.getRealType(EntityPageOwendSelector.class, "NE", getClass()));

        return nodeEntityClass;
    }

    @SuppressWarnings("unchecked")
    private Class<IE> getItemEntityClass() throws Exception
    {
        if (itemEntityClass == null)
            itemEntityClass = BeanUtils.toClass(BeanUtils.getRealType(EntityPageOwendSelector.class, "IE", getClass()));

        return itemEntityClass;
    }

    private Class getNodeKeyClass() throws Exception
    {
        if (nodeKeyClass == null)
            nodeKeyClass = CrudConfig.getPersistenceDialect().getKeyType(getNodeEntityClass());

        return nodeKeyClass;
    }

    private Class getItemKeyClass() throws Exception
    {
        if (itemKeyClass == null)
            itemKeyClass = CrudConfig.getPersistenceDialect().getKeyType(getItemEntityClass());

        return itemKeyClass;
    }

    protected PropertyInfo getChildrenPropertyInfo() throws Exception
    {
        if (childrenPropertyInfo == null)
        {
            Class<NE> entityType = getNodeEntityClass();
            childrenPropertyInfo = BeanUtils
                    .getProperty(entityType, CrudConfig.getPersistenceDialect().getChildrenField(entityType));
        }

        return childrenPropertyInfo;
    }

    protected NE createRoot() throws Exception
    {
        return null;
    }

    @Override
    protected Node getRoot() throws Exception
    {
        Object rootKey = getRootKey();
        NE root = getCommonDao().load(getNodeEntityClass(), rootKey);

        if (root == null)
        {
            NE root1 = createRoot();

            if (root1 != null)
            {
                getCommonDao().save(root1);

                root = getCommonDao().load(getNodeEntityClass(), rootKey);
            }
        }

        return getTreeNode(root);
    }

    protected Node getTreeNode(NE e) throws Exception
    {
        Node node = new Node();

        node.setId(getNodeId(e));
        node.setText(getNodeText(e));
        node.setLeaf(isLeaf(e));

        return node;
    }

    protected Object toNodeKey(String id) throws Exception
    {
        return DataConvert.convertValue(getNodeKeyClass(), id);
    }

    @SuppressWarnings("unchecked")
    protected NE getNode(String id) throws Exception
    {
        return getCommonDao().get(getNodeEntityClass(), toNodeKey(id));
    }

    protected boolean isLeaf(NE e) throws Exception
    {
        return getChildCount(e) == 0;
    }

    protected int getChildCount(NE parent) throws Exception
    {
        int n = 0;
        List<NE> children = getChildren(parent);
        for (NE child : children)
        {
            if (acceptNode(child))
                n++;
        }

        return n;
    }

    protected List<NE> getChildren(NE parent) throws Exception
    {
        List<NE> children;
        String id = getNodeId(parent);
        if (childrenMap == null)
        {
            childrenMap = new HashMap<String, List<NE>>();
            children = null;
        }
        else
        {
            children = childrenMap.get(id);
        }

        if (children == null)
            childrenMap.put(id, children = loadNodeChildren(parent));

        return children;
    }

    protected Item getItem(IE e) throws Exception
    {
        return new Item(getItemId(e), getItemText(e));
    }

    @SuppressWarnings("unchecked")
    protected List<NE> loadNodeChildren(NE parent) throws Exception
    {
        return (List<NE>) getChildrenPropertyInfo().get(parent);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected boolean acceptNode(NE e) throws Exception
    {
        return true;
    }

    protected String getNodeId(NE e) throws Exception
    {
        return DataConvert.getValueString(e);
    }

    protected String getNodeText(NE e) throws Exception
    {
        return DataConvert.toString(e);
    }

    protected String getItemId(IE e) throws Exception
    {
        return DataConvert.getValueString(e);
    }

    protected String getItemText(IE e) throws Exception
    {
        return DataConvert.toString(e);
    }

    protected String getNodeAlias() throws Exception
    {
        if (nodeAlias == null)
            nodeAlias = getNodeEntityClass().getSimpleName().toLowerCase();

        return nodeAlias;
    }

    protected String getItemAlias() throws Exception
    {
        if (itemAlias == null)
            itemAlias = getItemEntityClass().getSimpleName().toLowerCase();

        return itemAlias;
    }

    protected Collection<NE> searchNodeEntity(String text) throws Exception
    {
        String queryString = getNodeSearchQueryString();
        if (queryString != null)
        {
            Map<String, String> map = new HashMap<String, String>(2);
            map.put("text", "%" + text + "%");
            map.put("text1", text + "%");
            return getCommonDao().oqlQuery(queryString, map);
        }

        return null;
    }

    protected String getNodeSearchQueryString() throws Exception
    {
        if (nodeSearchQueryString == null)
        {
            String condition = getNodeSearchCondition();
            if (condition != null)
            {
                String alias = getNodeAlias();
                nodeSearchQueryString = "select " + alias + " from " +
                        CrudBeanUtils.getClassWord(getNodeEntityClass()) + " " + alias + " where " + condition;
            }
        }

        return nodeSearchQueryString;
    }

    protected String getNodeSearchCondition() throws Exception
    {
        String textField = getNodeTextField();

        if (textField != null)
            return textField + " like :text";

        return null;
    }

    protected String getNodeTextField() throws Exception
    {
        return null;
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
            Class<?> entityType = getNodeEntityClass();
            parentField = CrudConfig.getPersistenceDialect().getParentKeyField(entityType);

            if (parentField == null)
                throw new CrudException("cannot find the parent key field for " + BeanUtils.getClassName(entityType) +
                        ",please override getParentField");
        }

        return parentField;
    }

    @SuppressWarnings("unchecked")
    protected Object getParentKey(NE entity) throws Exception
    {
        return BeanUtils.getValue(entity, getParentField());
    }

    protected NE getParent(NE e) throws Exception
    {
        Object parentKey = getParentKey(e);
        if (parentKey == null)
            return null;

        return getCommonDao().get(getNodeEntityClass(), parentKey);
    }

    /**
     * item中关联node的field
     *
     * @return item中关联node的field
     * @throws Exception 异常
     */
    protected String getOwnerField() throws Exception
    {
        if (ownerField == null)
        {
            Class<?> entityType = getNodeEntityClass();
            List<String> keyFields = CrudConfig.getPersistenceDialect().getKeyFields(entityType);
            if (keyFields.size() == 1)
                ownerField = keyFields.get(0);

            if (ownerField == null)
            {
                throw new CrudException("cannot find the owner key field for " +
                        BeanUtils.getClassName(getItemEntityClass()) + ",please override getOwnerField");
            }
        }

        return ownerField;
    }

    protected String getItemCondition() throws Exception
    {
        return null;
    }

    protected String getItemQueryString() throws Exception
    {
        if (itemQueryString == null)
        {
            String alias = getItemAlias();
            String ownerField = getOwnerField();
            String condition = getItemCondition();

            StringBuilder buffer = new StringBuilder("select ").append(alias);
            buffer.append(" from ").append(CrudBeanUtils.getClassWord(getItemEntityClass())).append(" ")
                    .append(alias).append(" where ");
            buffer.append(ownerField).append("=:ownerKey");

            if (!StringUtils.isEmpty(condition))
                buffer.append(" and (").append(condition).append(")");

            List<OrderBy> orderBys = getOrderBys();
            if (orderBys != null)
            {
                buffer.append(" order by ");
                StringUtils.concat(orderBys, ",", buffer);
            }

            itemQueryString = buffer.toString();
        }

        return itemQueryString;
    }

    protected Collection<IE> searchItemEntity(String text) throws Exception
    {
        String queryString = getItemSearchQueryString();
        if (queryString != null)
        {
            Map<String, String> map = new HashMap<String, String>(2);
            map.put("text", "%" + text + "%");
            map.put("text1", text + "%");
            return getCommonDao().oqlQuery(queryString, map);
        }

        return null;
    }

    protected String getItemSearchQueryString() throws Exception
    {
        if (itemSearchQueryString == null)
        {
            String searchCondition = getItemSearchCondition();
            if (searchCondition != null)
            {
                String alias = getItemAlias();
                String condition = getItemCondition();

                StringBuilder buffer = new StringBuilder("select ").append(alias);
                buffer.append(" from ").append(CrudBeanUtils.getClassWord(getItemEntityClass())).append(" ")
                        .append(alias).append(" where ");

                buffer.append("(").append(searchCondition).append(")");

                if (!StringUtils.isEmpty(condition))
                    buffer.append(" and (").append(condition).append(")");

                List<OrderBy> orderBys = getOrderBys();
                if (orderBys != null)
                {
                    buffer.append(" order by ");
                    StringUtils.concat(orderBys, ",", buffer);
                }

                itemSearchQueryString = buffer.toString();
            }
        }

        return itemSearchQueryString;
    }

    protected String getItemSearchCondition() throws Exception
    {
        String textField = getItemTextField();

        if (textField != null)
            return textField + " like :text";

        return null;
    }

    protected String getItemTextField() throws Exception
    {
        return null;
    }

    @Override
    public List<Node> loadChildren(String parent) throws Exception
    {
        List<NE> children = getChildren(getNode(parent));

        List<Node> result = new ArrayList<Node>(children.size());

        for (NE child : children)
        {
            if (acceptNode(child))
            {
                result.add(getTreeNode(child));
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String>[] searchNode(String text) throws Exception
    {
        Collection<NE> nodes = searchNodeEntity(text);

        List<String>[] result = new List[nodes.size()];

        int index = 0;
        for (NE node : nodes)
        {
            List<String> path = result[index++] = new ArrayList<String>();

            while (node != null)
            {
                String id = getNodeId(node);

                path.add(0, id);

                node = getParent(node);
            }
        }

        return result;
    }

    @Override
    public List<Item> loadItems(String nodeId) throws Exception
    {
        setOwnerKey(toNodeKey(nodeId));

        List<IE> entitys = getCommonDao().oqlQuery(getItemQueryString(), new CollectionUtils.EvalMap(this));
        List<Item> items = new ArrayList<Item>(entitys.size());

        for (IE entity : entitys)
        {
            items.add(getItem(entity));
        }

        return items;
    }

    @Override
    public List<Item> queryItems(String s) throws Exception
    {
        setText("%" + s + "%");

        List<IE> entitys = getCommonDao().oqlQuery(getItemSearchQueryString(), new CollectionUtils.EvalMap(this));
        List<Item> items = new ArrayList<Item>(entitys.size());

        for (IE entity : entitys)
        {
            items.add(getItem(entity));
        }

        return items;
    }
}
