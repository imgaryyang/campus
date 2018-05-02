package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 群人员维护
 *
 * @author fwj
 * @date 2011-3-7
 */
@Service(url = "/im/GroupMemberCrud")
public class GroupMemberCrud extends BaseNormalCrud<GroupMember, Integer>
{
    @Inject
    private ImService service;

    /**
     * 群ID
     */
    private Integer groupId;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 群成员名字
     */
    @Like("user.userName")
    private String userName;

    private Integer creator;

    public GroupMemberCrud()
    {
        addOrderBy("user.userName");
    }

    public Integer getGroupId()
    {
        return groupId == null || groupId <= 0 ? null : groupId;
    }

    public void setGroupId(Integer groupId)
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

    private String getGroupName() throws Exception
    {
        return service.getDao().load(ImGroup.class, groupId).getGroupName();
    }

    public Integer getCreator() throws Exception
    {
        if (creator == null)
            creator = service.getDao().load(ImGroup.class, groupId).getCreator();

        return creator;
    }

    @Override
    public Integer getKey(GroupMember entity) throws Exception
    {
        return entity.getUserId();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "user.state<>2";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        if (getCreator().equals(userOnlineInfo.getUserId()))
        {
            view.setCheckable("${userId!=crud$.creator}");
        }
        else
        {
            view.setCheckable("${!groupAdmin.booleanValue()}");
        }

        view.setTitle("群成员管理-" + getGroupName());

        view.addComponent("姓名", "userName");

        view.addColumn("姓名", "user.userName");
        view.addColumn("性别", "user.sex");
        view.addColumn("所属部门", "user.allDeptName()").setAutoExpand(true);
        view.addColumn("岗位", "user.allStationName()");

        if (getCreator().equals(userOnlineInfo.getUserId()))
        {
            view.addColumn("管理员", new ConditionComponent()
                            .add("userId==crud$.creator", new CCheckbox("groupAdmin").setProperty("disabled", null))
                            .add(new CCheckbox("groupAdmin").setProperty("onclick", "setAdmin(${userId},this.checked)"))
            );
        }
        else
        {
            view.addColumn("管理员", new CCheckbox("groupAdmin").setProperty("disabled", null));
        }

        view.addButton(Buttons.query());

        //如果当前用户是群的管理员或群主
        if (userOnlineInfo.getUserId().equals(getCreator()) ||
                service.getDao().isGroupAdmin(userOnlineInfo.getUserId(), groupId))
        {
            view.addButton(Buttons.add(null, "添加成员"));
            view.addButton(Buttons.delete());
        }
        view.importJs("/im/groupmember.js");

        return view;
    }

    @Override
    @Forward(page = "/im/groupmember_add.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Service(method = HttpMethod.post)
    public void saveMembers(Integer[] memberIds) throws Exception
    {
        if (memberIds != null)
        {
            for (Integer userId : service.addGroupMembers(memberIds, getGroupId()))
            {
                sendMessage("im.addToGroup", userId, OperationType.addGroupMember);
            }
        }
    }

    @Service
    @ObjectResult
    public void setAdmin(Integer userId, Boolean admin) throws Exception
    {
        //只有群主才能设置某个群用户为管理员
        if (userOnlineInfo.getUserId().equals(getCreator()))
        {
            GroupMember groupMember = new GroupMember();
            groupMember.setGroupId(groupId);
            groupMember.setUserId(userId);
            groupMember.setGroupAdmin(admin);
            update(groupMember);
        }
    }

    private void sendMessage(String mesage, Integer userId, OperationType type) throws Exception
    {
        GroupMessage message = new GroupMessage();

        String curentUserName = userOnlineInfo.getUserName();
        String userName = service.getDao().getUserName(userId);
        String content = Tools.getMessage(mesage, curentUserName, new Date(), userName);
        message.setContent(content);
        message.setGroupId(groupId);
        message.setSender(userOnlineInfo.getUserId());
        message.setType(MessageType.text);
        message.setOperationType(type);

        service.sendGroupMessage(message, userOnlineInfo.getUserId());
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        Integer creator = getCreator();
        Integer[] userIds = getKeys();
        for (Integer userId : userIds)
        {
            //群主不能被删除
            if (userId.equals(creator))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterDeleteAll() throws Exception
    {
        Integer[] userIds = getKeys();
        for (Integer userId : userIds)
        {
            sendMessage("im.deleteFromGroup", userId, OperationType.deleteGroupMember);
        }
        super.afterDeleteAll();
    }

    @Override
    public boolean delete(Integer key) throws Exception
    {
        service.getDao().deleteGroupMember(groupId, key);
        return true;
    }

    @Override
    public int deleteAll() throws Exception
    {
        return service.getDao().deleteGroupMembers(groupId, getKeys());
    }
}
