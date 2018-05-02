package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.mo.MoBind;
import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 即时消息相关的dao
 *
 * @author camel
 * @date 2010-9-6
 */
public abstract class ImDao extends GeneralDao
{
    public ImDao()
    {
    }

    /**
     * 获得某用户的即时消息配置信息
     *
     * @param userId 用户ID
     * @return 用户即时消息配置信息
     * @throws Exception 数据库读取数据错误
     */
    public ImUserConfig getImUserConfig(Integer userId) throws Exception
    {
        ImUserConfig config = load(ImUserConfig.class, userId);

        if (config == null)
        {
            //防止多线程访问下，重复保存
            synchronized (ImDao.class)
            {
                config = load(ImUserConfig.class, userId);
                if (config == null)
                {
                    config = new ImUserConfig();
                    config.setUserId(userId);
                    add(config);

                    config = load(ImUserConfig.class, userId);
                }
            }
        }

        return config;
    }

    public ImGroup getGroup(Integer groupId) throws Exception
    {
        return load(ImGroup.class, groupId);
    }

    public UserMessage getUserMessage(Long messageId) throws Exception
    {
        return load(UserMessage.class, messageId);
    }

    @OQL("select g from GroupMember g where g.groupId=:1 and user.state<>2 order by user.userName")
    public abstract List<GroupMember> getGroupMembers(Integer groupId) throws Exception;

    @OQL("select signature from ImUserConfig where userId=:1")
    public abstract String getSignature(Integer userId) throws Exception;

    @OQL("select userName from User where userId=:1")
    public abstract String getUserName(Integer userId) throws Exception;

    @OQL("select groupName from ImGroup where groupId=:1")
    public abstract String getGroupName(Integer groupId) throws Exception;

    @OQL("select distinct groupId from GroupMember where userId=:1")
    public abstract List<Integer> getGroupIds(Integer userId) throws Exception;

    @OQL("select distinct userId from GroupMember where groupId=:1")
    public abstract List<Integer> getGroupUserIds(Integer groupId) throws Exception;

    /**
     * 获得某用户所建的用户分组
     *
     * @param userId 用户ID
     * @return 用户分组列表
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select g from FriendGroup g where userId=:1 order by orderId")
    public abstract List<FriendGroup> getFriendGroups(Integer userId) throws Exception;

    /**
     * 获得某用户的好友列表
     *
     * @param userId 用户ID
     * @return 用户分组列表
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select f from Friend f where userId=:1 and friendUser.state<>2 order by f.friendUser.userName")
    public abstract List<Friend> getFriends(Integer userId) throws Exception;

    /**
     * 把某条添加好友信息设置为已读
     *
     * @param userId       用户id
     * @param friendUserId 好友id
     * @return 更新的数量
     * @throws Exception 数据库更新异常
     */
    @OQLUpdate("update Friend set confirmed=1 where userId=:1 and friendUserId=:2")
    public abstract Integer updateFriend(Integer userId, Integer friendUserId) throws Exception;

    /**
     * 获得某用户所参加的群
     *
     * @param userId 用户id
     * @return 群列表
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select g from ImGroup g where exists m in g.members : m.userId=:1 order by groupName")
    public abstract List<ImGroup> getGroups(Integer userId) throws Exception;

    /**
     * 获取某个用户在某个群中是否为管理员的属性值
     *
     * @param userId  用户id
     * @param groupId 群id
     * @return boolean值
     * @throws Exception 数据库查询异常
     */
    @OQL("select groupAdmin from GroupMember g where g.userId=:1 and g.groupId=:2")
    public abstract boolean isGroupAdmin(Integer userId, Integer groupId) throws Exception;

    /**
     * 获取某个群的群主id
     *
     * @param groupId 群id
     * @return 群主id
     * @throws Exception 数据库查询异常
     */
    @OQL("select creator from ImGroup g where g.groupId=:1")
    public abstract Integer getCreator(Integer groupId) throws Exception;

    /**
     * 获取GroupMember对象
     *
     * @param userId  用户Id
     * @param groupId 群Id
     * @return 群人员，没有返回null
     * @throws Exception 数据库读取数据错误
     */
    @GetByField({"userId", "groupId"})
    public abstract GroupMember getMember(Integer userId, Integer groupId) throws Exception;

    /**
     * 获得Friend对象
     *
     * @param userId       用户ID
     * @param friendUserId 好友用户ID
     * @return 如果是好友返回true，如果不是返回false
     * @throws Exception 数据库读取数据错误
     */
    @GetByField({"userId", "friendUserId"})
    public abstract Friend getFriend(Integer userId, Integer friendUserId) throws Exception;

    /**
     * 获取friendId
     *
     * @param userId       用户id
     * @param friendUserId 好友id
     * @return friendid
     * @throws Exception 数据库查询异常
     */
    @OQL("select friendId from Friend f where f.userId=:1 and f.friendUserId=:2")
    public abstract Long getFriendId(Integer userId, Integer friendUserId) throws Exception;

    /**
     * 获得某用户的好友数量
     *
     * @param userId 用户ID
     * @return 用户分组列表
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select count(*) from Friend f where userId=:1")
    public abstract Integer getFriendCount(Integer userId) throws Exception;

    /**
     * 获得某用户没有分组的好友数量
     *
     * @param userId 用户ID
     * @return 用户分组列表
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select count(*) from Friend f where userId=:1 and groupId is null")
    public abstract Integer getFriendCountNotInGroup(Integer userId) throws Exception;

    /**
     * 查询加了某用户为好友的用户的ID
     *
     * @param userId 指定用户的ID
     * @return 加了此用户为好友的用户的ID列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select userId from Friend where friendUserId=:1")
    public abstract List<Integer> getUserIdsInWhoseFriends(Integer userId) throws Exception;

    /**
     * 读取某个用户未读的用户个人消息
     *
     * @param userId 用户ID
     * @return 此用户未读的消息的列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select m from UserMessage m where userId=:1 and readed=0 order by sendTime")
    public abstract List<UserMessage> getNoReadedUserMessage(Integer userId) throws Exception;

    /**
     * 读取某个用户未读的群消息
     *
     * @param userId 用户ID
     * @return 此用户未读的群消息的列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select m from GroupMessageRecord m where userId=:1 and readed=0")
    public abstract List<GroupMessageRecord> getNoReadedGroupMessage(Integer userId) throws Exception;

    public void deleteGroupMember(Integer groupId, Integer userId) throws Exception
    {
        delete(GroupMember.class, groupId, userId);
    }

    /**
     * 获取当前用户所有没有确认添加好友信息的用户id
     *
     * @param userId 当前用户id
     * @return 用户id列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select f.user from Friend f where f.friendUserId=:1 and f.confirmed=0 " +
            "and f.userId not in (select friendUserId from Friend where userId=:1) and f.user.state<>2")
    public abstract List<User> getNoConfirmUsers(Integer userId) throws Exception;

    /**
     * 删除群好友
     *
     * @param groupId 群id
     * @param userIds 好友id
     * @return 删除的数量
     * @throws Exception 数据库更新错误
     */
    @OQLUpdate("delete GroupMember where groupId=:1 and userId in :2")
    public abstract int deleteGroupMembers(Integer groupId, Integer[] userIds) throws Exception;

    @OQL("select uuid,fileSize from Attachment where attachmentId=:1")
    public abstract Map<String, Object> getAttachmentInfo(Long attachmentId) throws Exception;

    @OQL("select uuid from Attachment where attachmentId=:1")
    public abstract String getAttachmentUUID(Long attachmentId) throws Exception;

    /**
     * 获取用户的最后在线时间
     *
     * @param userId 用户主键
     * @return 最后在线时间
     * @throws Exception
     */
    @OQL("select loginTime from LoginTime where userId=:1")
    public abstract Date getLoginTime(Integer userId) throws Exception;

    /**
     * 获取部门
     *
     * @param deptId 部门主键
     * @return 部门
     * @throws Exception
     */
    @OQL("select d from com.gzzm.platform.organ.Dept d where d.deptId=?1 and d.state=0 order by d.orderId")
    public abstract Dept getDeptById(Integer deptId) throws Exception;

    /**
     * 获取根节点部门
     *
     * @return 部门
     * @throws Exception
     */
    @OQL("select d from com.gzzm.platform.organ.Dept d where d.parentDeptId is null and d.state=0 order by d.orderId")
    public abstract Dept getRootDept() throws Exception;

    /**
     * 查询用户与联系人聊天记录
     *
     * @param userId     用户主键
     * @param linkMan    联系人主键
     * @param startTime  记录开始时间
     * @param endTime    记录结束时间
     * @param type       消息类型
     * @param limitStart 查询记录开始数
     * @param pageSize   查询数量
     * @return 聊天记录
     * @throws Exception
     */
    @OQL("select m from com.gzzm.im.entitys.UserMessage m where m.userId=:1 and (m.sender=:2 or m.receiver=:2) and m.sendTime>=?3 and m.sendTime<=?4 and m.type=?5 order by m.sendTime desc limit :6,:7")
    public abstract List<UserMessage> getUserMessageByLinkMan(Integer userId, Integer linkMan, java.sql.Date startTime,
                                                              java.sql.Date endTime, MessageType type,
                                                              Integer limitStart, Integer pageSize) throws Exception;

    /**
     * 查询用户与群聊天记录
     *
     * @param groupId    群主键
     * @param startTime  记录开始时间
     * @param endTime    记录结束时间
     * @param type       消息类型
     * @param limitStart 查询记录开始数
     * @param pageSize   查询数量
     * @return 聊天记录
     * @throws Exception
     */
    @OQL("select m from com.gzzm.im.entitys.GroupMessage m where m.groupId=:1 and m.sendTime>=?2 and m.sendTime<=?3 and m.type=?4 order by m.sendTime desc limit :5,:6")
    public abstract List<GroupMessage> getGroupMessageByGroupId(Integer groupId, java.sql.Date startTime,
                                                                java.sql.Date endTime, MessageType type,
                                                                Integer limitStart, Integer pageSize) throws Exception;

    /**
     * 获取用户创建的群
     *
     * @param creator 用户主键
     * @return 群列表
     * @throws Exception
     */
    @OQL("select g from com.gzzm.im.entitys.ImGroup g where g.creator=:1 order by g.createdTime desc")
    public abstract List<ImGroup> getImGroupByCreator(Integer creator) throws Exception;

    @OQL("select t from MoBind t where t.user.userId=:1 and t.token is not null order by t.bindTime desc")
    public abstract MoBind queryMoBindByUserId(Integer userId);

    @OQL("select count(t) from UserMessage t where t.receiver=:1 and t.userId=:1 and t.readed=0")
    public abstract Integer countAllNotReadMessage(Integer userId);
}
