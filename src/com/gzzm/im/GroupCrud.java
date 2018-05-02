package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 群维护
 *
 * @author fwj
 * @date 2011-3-4
 */
@Service(url = "/im/GroupCrud")
public class GroupCrud extends BaseNormalCrud<ImGroup, Integer>
{
    @Inject
    private ImDao dao;

    /**
     * 获取当前用户ID
     */
    @UserId
    private Integer userId;

    /**
     * 群ID
     */
    private Integer groupId;

    private Integer friendUserId;

    public GroupCrud()
    {
        addOrderBy("groupName");
    }

    public Integer getGroupId()
    {
        return groupId == null || groupId <= 0 ? null : groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Integer getFriendUserId()
    {
        return friendUserId;
    }

    public void setFriendUserId(Integer friendUserId)
    {
        this.friendUserId = friendUserId;
    }

    @Select(field = "groupId")
    public List<ImGroup> getImGroupList() throws Exception
    {
        List<ImGroup> imGroups=dao.getImGroupByCreator(getUserId());
        if(imGroups==null)
            imGroups=new ArrayList<ImGroup>();
        return imGroups;
    }

    @Override
    public void initEntity(ImGroup entity) throws Exception
    {
        super.initEntity(entity);

        entity.setCreator(userId);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "creator=:userId or exists m in members :(m.userId=:userId and m.groupAdmin=1)";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        //当前用户不是当前群的群主时禁用当前复选框
        view.setCheckable("${creator==crud$.userId}");

        view.addColumn("群名称", "groupName");
        view.addColumn("创建时间", "createdTime");
        view.addColumn("创建者", "createUser.userName");
        view.addColumn("群成员管理", new CButton("群成员管理", "groupManage(${groupId})"));

        view.addButton(Buttons.add(""));
        view.addButton(Buttons.delete());
        view.makeEditable();

        view.importJs("/im/group.js");

        return view;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        Integer[] groupIds = getKeys();
        for (Integer groupId : groupIds)
        {
            Integer creator = dao.getCreator(groupId);
            //只有某个群的群主才能删除该群
            if (!userId.equals(creator))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("群名称", "groupName");

        view.addDefaultButtons();

        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreator(userId);
        getEntity().setCreatedTime(new Date());

        return true;
    }

    @Override
    public void afterInsert() throws Exception
    {
        //创建群时也不创建者加入到群里
        GroupMember creator = new GroupMember();
        creator.setGroupId(getEntity().getGroupId());
        creator.setUserId(getUserId());
        creator.setGroupAdmin(true);
        dao.add(creator);
    }

    @Service(url = "/im/group/friendMember")
    public String addFriendGroupMember()
    {
        return "/im/friendgroup_member.ptl";
    }

    @Service(method = HttpMethod.post)
    public void saveFriendGroup() throws Exception
    {
        GroupMember member=dao.getMember(getFriendUserId(),getGroupId());
        if(member==null)
        {
            member = new GroupMember();
            member.setUserId(getFriendUserId());
            member.setGroupId(getGroupId());
            member.setGroupAdmin(false);
            dao.save(member);
        }
    }
}
