package com.gzzm.oa.address;

import com.gzzm.platform.addivision.*;
import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.dict.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.Crud;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.BaseColumn;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 通讯录维护，继承自OwnedCrud，以实现通讯录在不同组之间的拖放
 *
 * @author whf
 * @date 2010-3-10
 */
@Service(url = "/oa/address/card")
public class AddressCardCrud extends BaseNormalCrud<AddressCard, Integer> implements
        OwnedCrud<AddressCard, Integer, Integer>
{
    @Inject
    private AddressCardDao dao;

    /**
     * 表示当前是对部门通讯录还是个人通讯录的维护
     */
    private AddressType type = AddressType.user;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 当前维护的部门ID
     */
    private Integer deptId;

    /**
     * 拥有权限维护的部门
     */
    @AuthDeptIds
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Collection<Integer> authDeptIds;

    /**
     * 名称查询条件，可以是联系人姓名，也可以是简拼，全拼
     */
    private String name;

    /**
     * 电话，包括手机，电话，传真等
     */
    private String phone;

    /**
     * 电子邮件
     */

    @Like("attributes.email")
    private String email;

    private Integer groupId;

    @Like("attributes.industry")
    private String industry;

    @Like("attributes.adDivision")
    private String adDivision;

    @NotSerialized
    private List<AddressGroup> availableGroups;

    @NotSerialized
    private List<AddressGroup> userGroups;

    //删除selGroups和headImg属性，因为可以使用addressCard对象本身的属性

    /**
     * 是否删除头像图片
     */
    private boolean delHead;

    /**
     * 是否只读
     */
    private boolean readOnly;

    private AdDivisionTreeModel adDivisionTree;

    private DictItemTreeModel industryTree;

    @Like("attributes.office")
    private String office;

    public AddressCardCrud()
    {
        setLog(true);
        addOrderBy("cardName");
        addOrderBy("cardId");
    }

    public AddressType getType()
    {
        return type;
    }

    public void setType(AddressType type)
    {
        this.type = type;
    }

    public Integer getDeptId()
    {
        if (deptId == null)
        {
            if (authDeptIds != null)
            {
                if (authDeptIds.size() > 0)
                    deptId = authDeptIds.iterator().next();
            }
            else
            {
                deptId = userOnlineInfo.getBureauId();
            }
        }

        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @NotSerialized
    public Integer getOwner()
    {
        return type == AddressType.user ? userOnlineInfo.getUserId() : getDeptId();
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Integer getGroupId()
    {
        //groupid==0表示查询所有通讯录
        if (groupId != null && groupId == 0)
            return null;

        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getIndustry()
    {
        return industry;
    }

    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    public String getAdDivision()
    {
        return adDivision;
    }

    public void setAdDivision(String adDivision)
    {
        this.adDivision = adDivision;
    }

    public String getOffice()
    {
        return office;
    }

    public void setOffice(String office)
    {
        this.office = office;
    }

    //删除对userId的setter方法，防止其被恶意修改 ccs

    public boolean isDelHead()
    {
        return delHead;
    }

    public void setDelHead(boolean delHead)
    {
        this.delHead = delHead;
    }

    public List<AddressGroup> getAvailableGroups() throws Exception
    {
        if (availableGroups == null)
            availableGroups = dao.getGroupsByOwner(type, getOwner());

        return availableGroups;
    }

    public List<AddressGroup> getUserGroups() throws Exception
    {
        if (userGroups == null)
            userGroups = dao.getGroupsByOwner(AddressType.user, userOnlineInfo.getUserId());

        return userGroups;
    }

    @Select(field = {"entity.attributes.adDivision", "adDivision"})
    public AdDivisionTreeModel getAdDivisionTree()
    {
        if (adDivisionTree == null)
        {
            adDivisionTree = new AdDivisionTreeModel();
            adDivisionTree.setValueType(AdDivisionValueType.NAME);
        }
        return adDivisionTree;
    }

    @Select(field = {"entity.attributes.industry", "industry"})
    public DictItemTreeModel getIndustryTree()
    {
        if (industryTree == null)
        {
            industryTree = new DictItemTreeModel();

            industryTree.setDictCode("industry");
            industryTree.setValueType(DictValueType.NAME);
        }

        return industryTree;
    }

    //删除availableGroups的setter方法，因为完全没有任何用处 ccs

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder buffer = null;

        if (!StringUtils.isEmpty(name))
        {
            buffer = new StringBuilder("(cardName like '%'||?name||'%' or nick like '%'||?name||'%' or " +
                    "simpleSpell like ?name||'%' or completeSpell like ?name||'%')");
        }

        if (!StringUtils.isEmpty(phone))
        {
            if (buffer == null)
                buffer = new StringBuilder();
            else
                buffer.append(" and ");
            buffer.append("(");

            boolean first = true;
            for (String s : CardItem.PHONES)
            {
                if (first)
                    first = false;
                else
                    buffer.append(" or ");

                buffer.append("attributes.").append(s).append(" like '%'||?phone||'%'");
            }


            buffer.append(")");
        }

        if (getGroupId() != null && !Null.isNull(getGroupId()))
        {
            if (buffer == null)
                buffer = new StringBuilder();
            else
                buffer.append(" and ");
            buffer.append("(exists g in groups : groupId=:groupId)");
        }

        return buffer == null ? null : buffer.toString();
    }

    @NotSerialized
    private List<CMenuItem> getGroupMenus() throws Exception
    {
        List<AddressGroup> groups = getAvailableGroups();
        if (groups != null && groups.size() > 0)
        {
            List<CMenuItem> items = new ArrayList<CMenuItem>();
            for (AddressGroup group : groups)
            {
                String s = "moveCards(" + group.getGroupId() + ")";
                items.add(new CMenuItem(group.getGroupName(), s));
            }

            return items;
        }

        return null;
    }

    @NotSerialized
    public List<CMenuItem> getUserGroupMenus() throws Exception
    {
        List<AddressGroup> groups = getUserGroups();
        if (groups != null && groups.size() > 0)
        {
            List<CMenuItem> items = new ArrayList<CMenuItem>();
            for (AddressGroup group : groups)
            {
                String s = "copyToUser(" + group.getGroupId() + ")";
                items.add(new CMenuItem(group.getGroupName(), s));
            }

            return items;
        }

        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if (type == AddressType.dept && deptId == null)
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
    }

    protected Object createListView() throws Exception
    {
        Crud left;
        String field;

        if (type == AddressType.user || (authDeptIds != null && authDeptIds.size() == 1))
        {
            //维护用户本身的数据或者只有权限维护一个部门的数据，左边显示组列表
            //Tools.getBean保证注入被执行，ccs
            AddressGroupDisplay groupDisplay = Tools.getBean(AddressGroupDisplay.class);
            groupDisplay.setType(type);
            groupDisplay.setOwner(getOwner());

            left = groupDisplay;
            field = "groupId";
        }
        else
        {
            //拥有权限维护多个部门的数据，左边显示部门树
            left = new AuthDeptDisplay();
            field = "deptId";
        }

        ComplexTableView view = new ComplexTableView(left, field, true);

        view.addComponent("姓名", "name");
        view.addComponent("电话", "phone");
//        view.addComponent("行业", "industry").setProperty("editable", "true");
//        view.addComponent("地区", "adDivision").setProperty("editable", "true");
        view.addComponent("单位", "office");
        view.addComponent("邮箱", "email");

        view.addButton(Buttons.query());

        if (readOnly)
        {
            if (type == AddressType.dept)
            {
                List<CMenuItem> groupMenus = getUserGroupMenus();
                if (groupMenus == null)
                    view.addButton("复制到个人通讯录", "copyAllToUser()");
                else
                    view.addButton(new CMenuButton(null, "复制到个人通讯录", groupMenus));
            }
        }
        else
        {
            view.addButton(Buttons.add(null));
            view.addButton(Buttons.delete());
        }

        view.addButton(new CButton("导出", "exportCards()").setIcon(Buttons.getIcon("export")));

        if (!readOnly)
        {
            view.addButton(new CButton("导入", "importCards()"));

            if (type == AddressType.user || (authDeptIds != null && authDeptIds.size() == 1))
            {
                List<CMenuItem> groupMenus = getGroupMenus();
                if (groupMenus != null)
                    view.addButton(new CMenuButton(null, "移动到", groupMenus).setIcon(Buttons.getIcon("catalog")));
            }
        }

        view.addColumn("姓名", "cardName");
        view.addColumn(getAttributeColumn("职务", "duty")).setWidth("150");
        view.addColumn(getAttributeColumn("手机", "mobilePhone")).setWidth("120");
        view.addColumn(getAttributeColumn("工作单位", "office")).setWidth("120");
        view.addColumn(getAttributeColumn("行业", "industry")).setWidth("200");
        view.addColumn(getAttributeColumn("地区", "adDivision")).setWidth("120");

        if (readOnly)
        {
            view.enableShow();
        }
        else
        {
            view.makeEditable();
            view.addDuplicateColumn(null);
        }

        if (left instanceof AddressGroupDisplay && !readOnly)
            view.enableDD();

        view.importJs("/oa/address/card.js");
        return view;
    }

    private BaseColumn getAttributeColumn(String title, String attribute)
    {
        return new BaseColumn(title, "attributes.get('" + attribute + "')").setOrderFiled("attributes." + attribute);
    }

    @Override
    @Forward(page = "/oa/address/card.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/oa/address/card.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/oa/address/card.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    /**
     * 添加和复制记录时执行
     */
    @Override
    public void initEntity(AddressCard entity) throws Exception
    {
        if (entity.getGroups() == null && getGroupId() != null)
        {
            //如果从某个组进来维护，则默认添加此组
            entity.setGroups(Collections.singletonList(dao.getAddressGroup(groupId)));
        }
    }

    @Override
    public AddressCard clone(AddressCard entity) throws Exception
    {
        AddressCard card = super.clone(entity);

        card.setCardName(card.getCardName() + "(复制)");
        card.setGroups(new ArrayList<AddressGroup>(entity.getGroups()));

        card.setAttributes(new HashMap<String, String>(entity.getAttributes()));

        return card;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setType(type);
        getEntity().setOwner(getOwner());

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if (getEntity().getGroups() == null)
            getEntity().setGroups(Collections.EMPTY_LIST);

        if (delHead)
            getEntity().setHeadImg(new byte[0]);

        return true;
    }

    //不需要编码删除中间表，由数据库的级联功能实现删除 ccs

    /**
     * 获得某个姓名的联系人，用于检查联系人是否重复
     *
     * @param name 联系人名称
     * @return list
     * @throws Exception 异常
     */
    @Service
    public List<AddressCard> getCardsWithName(String name) throws Exception
    {
        List<AddressCard> list = dao.getCardsWithName(name, type, getOwner());
        return list.size() == 0 ? null : list;
    }

    @Service(url = "/oa/address/icon/{$0}")
    public byte[] getIcon(Integer cardId) throws Exception
    {
        AddressCard card = dao.getAddressCard(cardId);
        if (card.getHeadImg() == null)
            return IOUtils.fileToBytes(Tools.getAppPath("/oa/address/images/head.gif"));

        return card.getHeadImg();
    }

    public String getOwnerField()
    {
        return "groupId";
    }

    public Integer getOwnerKey(AddressCard entity) throws Exception
    {
        //这两个方法不会被调用 ccs
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public void setOwnerKey(AddressCard entity, Integer ownerKey) throws Exception
    {
        //这两个方法不会被调用 ccs
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public void copyAllTo(Integer[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        if (newOwnerKey != null && newOwnerKey != 0)
            dao.copyCardstoGroup(keys, newOwnerKey);
    }

    public void copyTo(Integer key, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        if (newOwnerKey != null && newOwnerKey != 0)
            dao.copyCardstoGroup(new Integer[]{key}, newOwnerKey);
    }

    public void moveAllTo(Integer[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        if (newOwnerKey != null && newOwnerKey != 0 && !newOwnerKey.equals(oldOwnerKey))
            dao.moveCardstoGroup(keys, newOwnerKey, oldOwnerKey);
    }

    public void moveTo(Integer key, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        if (newOwnerKey != null && newOwnerKey != 0 && !newOwnerKey.equals(oldOwnerKey))
            dao.moveCardstoGroup(new Integer[]{key}, newOwnerKey, oldOwnerKey);
    }

    @Service
    public void copyCardToUser(Integer cardId, Integer groupId) throws Exception
    {
        AddressCard card = getEntity(cardId);

        AddressCard newCard = super.clone(card);
        newCard.setAttributes(new HashMap<String, String>(card.getAttributes()));

        newCard.setOwner(userOnlineInfo.getUserId());
        newCard.setType(AddressType.user);

        if (groupId != null)
        {
            List<AddressGroup> groups = new ArrayList<AddressGroup>();
            groups.add(new AddressGroup(groupId));
            newCard.setGroups(groups);
        }

        dao.add(newCard);
    }

    @Service
    @Transactional
    public void copyCardsToUser(Integer groupId) throws Exception
    {
        for (Integer cardId : getKeys())
            copyCardToUser(cardId, groupId);
    }
}
