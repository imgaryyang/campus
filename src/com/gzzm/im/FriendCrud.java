package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 好友维护
 *
 * @author camel
 * @date 2011-1-5
 */
@Service(url = "/im/FriendCrud")
public class FriendCrud extends UserOwnedNormalCrud<Friend, Long> implements
        OwnedCrud<Friend, Long, Long>
{
    @Inject
    private ImService service;

    private Long groupId;

    @Like("friendUser.userName")
    private String userName;

    public FriendCrud()
    {
        addOrderBy("friendUser.userName");
    }

    public Long getGroupId()
    {
        return groupId == null || groupId <= 0 ? null : groupId;
    }

    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @NotSerialized
    @Select(field = {"groupId","entity.groupId"})
    public List<FriendGroup> getGroups() throws Exception
    {
        return service.getDao().getFriendGroups(getUserId());
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (groupId != null && !Null.isNull(groupId) && groupId == 0)
            return "groupId is null";
        return null;
    }

    @Override
    @Forward(page = "/im/friends_add.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Service(url = "/im/FriendCrud/add")
    public String addFriend()
    {
        setEntity(new Friend());
        return "/im/friends_add.ptl";
    }

    @Service(url = "/im/friend/group")
    public String setFriendGroup(Integer linkMan) throws Exception
    {

        setEntity(service.getDao().getFriend(getUserId(),linkMan));
        if(getEntity()==null)
        {
            setEntity(new Friend());
        }
        getEntity().setFriendUserId(linkMan);
        return "/im/friend_group.ptl";
    }

    @Override
    protected Object createListView() throws Exception
    {
        //Tools.getBean保证注入被执行
        FriendGroupCrud left = Tools.getBean(FriendGroupCrud.class);
        left.setDisplay(true);

        ComplexTableView view = new ComplexTableView(left, "groupId", true);

        view.addComponent("姓名", "userName");
        view.addColumn("姓名", "friendUser.userName");
        view.addColumn("性别", "friendUser.sex");
        view.addColumn("所属部门", "friendUser.allDeptName()").setAutoExpand(true);
        view.addColumn("岗位", "friendUser.allStationName()");

        view.addButton(Buttons.query());
        view.addButton(Buttons.add(null, "添加好友"));
        view.addButton(Buttons.delete());
        view.addButton(Buttons.getButton("分组管理", "groupManage()", "group"));
        view.enableDD(false);

        view.importJs("/im/friend.js");
        return view;
    }

    public String getOwnerField()
    {
        return "groupId";
    }

    public Long getOwnerKey(Friend entity) throws Exception
    {
        return entity.getGroupId();
    }

    public void setOwnerKey(Friend entity, Long ownerKey) throws Exception
    {
        entity.setGroupId(ownerKey);
    }

    public void moveTo(Long key, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        if (newOwnerKey == 0)
            newOwnerKey = Null.Long;

        OwnedCrudUtils.moveTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    public void moveAllTo(Long[] keys, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        if (newOwnerKey == 0)
            newOwnerKey = Null.Long;

        OwnedCrudUtils.moveTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    public void copyTo(Long key, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public void copyAllTo(Long[] keys, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Service(method = HttpMethod.post)
    public void saveFriends(Integer[] friendUserIds) throws Exception
    {
        service.addFriends(getUserId(), friendUserIds, groupId);
    }

    @Service(method = HttpMethod.post)
    public void saveFriend(Integer friendUserId) throws Exception
    {
        service.addFriend(getUserId(), friendUserId, groupId==0L?null:groupId);
    }
}
