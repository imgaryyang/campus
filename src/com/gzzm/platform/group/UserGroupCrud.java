package com.gzzm.platform.group;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;

import java.util.*;

/**
 * 用户组维护
 *
 * @author camel
 * @date 2010-8-26
 */
@Service(url = "/UserGroup")
public class UserGroupCrud extends BaseNormalCrud<UserGroup, Integer>
{
    private static final String[] ORDERWITHFIELDS = {"type", "owner"};

    /**
     * 类型，表示对个人用户组还是对单位用户组维护
     */
    private UserGroupType type = UserGroupType.user;

    @UserId
    @NotCondition
    @NotSerialized
    private Integer userId;

    @NotCondition
    private Integer deptId;

    @NotSerialized
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @Like
    private String groupName;

    public UserGroupCrud()
    {
        setLog(true);
    }

    public UserGroupType getType()
    {
        return type;
    }

    public void setType(UserGroupType type)
    {
        this.type = type;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @NotSerialized
    public Integer getOwner()
    {
        return type == UserGroupType.user ? userId : deptId;
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
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
    @Forward(page = "/platform/group/usergroup.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/platform/group/usergroup.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/platform/group/usergroup.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    public void initEntity(UserGroup entity) throws Exception
    {
        super.initEntity(entity);

        entity.setType(type);
        if (type == UserGroupType.user)
            entity.setOwner(userId);
        else
            entity.setOwner(deptId);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (type == UserGroupType.dept && deptId == null)
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        UserGroup group = getEntity();

        if (group.getType() == null)
            group.setType(type);

        if (group.getType() == UserGroupType.user)
            group.setOwner(userId);
        else if (group.getOwner() == null)
            group.setOwner(deptId);

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        UserGroup group = getEntity();

        group.setType(null);
        group.setOwner(null);

        return true;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        UserGroup group = getEntity();
        if (group.getUsers() == null)
            group.setUsers(new ArrayList<User>(0));

        return true;
    }

    @Override
    public UserGroup clone(UserGroup entity) throws Exception
    {
        UserGroup group = super.clone(entity);
        group.setUsers(new ArrayList<User>(entity.getUsers()));

        return group;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (type == UserGroupType.user || authDeptIds != null && authDeptIds.size() == 1)
            view = new PageTableView(true);
        else
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("用户组名称", "groupName");

        view.addColumn("用户组名称", "groupName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
    }

    @Override
    public boolean isLog()
    {
        return type == UserGroupType.dept;
    }
}
