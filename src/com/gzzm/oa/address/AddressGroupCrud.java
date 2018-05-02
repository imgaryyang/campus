package com.gzzm.oa.address;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author whf
 * @date 2010-3-15
 */
@Service(url = "/oa/address/group")
public class AddressGroupCrud extends BaseNormalCrud<AddressGroup, Integer>
{
    private static final String[] ORDERWITHFIELDS = new String[]{"type", "owner"};

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
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @Like("groupName")
    private String groupName;

    @Inject
    private AddressCardDao dao;

    public AddressGroupCrud()
    {
        setLog(true);
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
        return type == AddressType.user ? userOnlineInfo.getUserId() : deptId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public void initEntity(AddressGroup entity) throws Exception
    {
        super.initEntity(entity);

        getEntity().setType(type);
        getEntity().setOwner(getOwner());
    }

    protected Object createListView() throws Exception
    {
        PageTableView view =
                type == AddressType.user || (authDeptIds != null && authDeptIds.size() == 1) ? new PageTableView() :
                        new ComplexTableView(new AuthDeptDisplay(), "owner");

        view.addComponent("组名称", "groupName");

        view.addColumn("通讯组名称", "groupName");

        view.addColumn("组成员数", new FieldCell("cards.size()"));

        view.addColumn("复制组成员", new CButton("复制组成员", "copy(${groupId},event)"));
        view.addColumn("移动组成员", new CButton("移动组成员", "move(${groupId},event)"));
        view.addColumn("移除组成员", new CButton("移除组成员", "moveOut(${groupId},event)"));

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.importJs("/oa/address/group.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("组名称", "groupName");
        view.addDefaultButtons();

        return view;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
    }

    public boolean beforeInsert()
    {
        getEntity().setType(type);
        getEntity().setOwner(getOwner());

        return true;
    }

    //删除组时不必编码删除中间表，由数据库级联功能自动删除 ccs


    /**
     * 将一个组的成员移到另外一个组
     *
     * @param oldGroupId 原来的组ID
     * @param newGroupId 新的组ID
     * @throws Exception 数据库操作异常
     */
    @ObjectResult
    @Service
    public void moveTo(Integer oldGroupId, Integer newGroupId) throws Exception
    {
        dao.moveGroupToGroup(oldGroupId, newGroupId);
    }

    /**
     * 将一个组的成员复制到另外一个组
     *
     * @param oldGroupId 原来的组ID
     * @param newGroupId 新的组ID
     * @throws Exception 数据库操作异常
     */
    @ObjectResult
    @Service
    public void copyTo(Integer oldGroupId, Integer newGroupId) throws Exception
    {
        dao.copyGroupToGroup(oldGroupId, newGroupId);
    }

    /**
     * 将整个组中的成员从组中移除
     *
     * @param groupId 组ID
     * @throws Exception 数据库操作异常
     */
    @ObjectResult
    @Service
    public void moveOut(Integer groupId) throws Exception
    {
        if (groupId != null)
            dao.deleteCardsInGroup(groupId);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 7;
    }
}
