package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.im.ios.*;
import com.gzzm.mo.MoBind;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.message.comet.CometService;
import com.gzzm.platform.message.sms.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和即使消息相关的服务
 *
 * @author camel
 * @date 2010-12-30
 */
public class ImService
{
    @Inject
    private static Provider<CometService> cometServiceProvider;

    /**
     * 是否已经初始化
     */
    private static boolean inited;

    @Inject
    private ImDao dao;

    public ImService()
    {
        init();
    }

    public static void init()
    {
        synchronized (ImService.class)
        {
            if (!inited)
            {
                inited = true;

                cometServiceProvider.get().addListener(new ImCometListener());
                SmsService.addReplyProcessor(ImSmsReplyProcessor.IMS, new ImSmsReplyProcessor());

            }
        }
    }

    public ImDao getDao()
    {
        return dao;
    }

    /**
     * 获得某用户的好友列表，并将好友列表分组存放
     *
     * @param userId 用户ID
     * @return 好友列表
     * @throws Exception 数据库读取数据错误
     */
    public FriendList getFriendList(Integer userId) throws Exception
    {
        List<FriendGroup> groups = dao.getFriendGroups(userId);

        FriendList friendList = new FriendList();

        for (FriendGroup group : groups)
        {
            friendList.addGroup(new FriendGroupInfo(group.getGroupId(), group.getGroupName()));
        }

        List<Friend> friends = dao.getFriends(userId);
        for (Friend friend : friends)
        {
            ImUserInfo friendInfo = new ImUserInfo(friend.getFriendUserId(), friend.getFriendUser().getUserName(),
                    friend.getFriendUser().allDeptName());
            friendInfo.setSignature(friend.getConfig().getSignature());
            friendInfo.setPhone(friend.getFriendUser().getPhone());
            friendInfo.setOfficePhone(friend.getFriendUser().getOfficePhone());
            friendInfo.setSex(DataConvert.toString(friend.getFriendUser().getSex()));
            friendInfo.setDuty(DataConvert.toString(friend.getFriendUser().getDuty()));
            friendInfo.setMyFriend(true);

            if (!StringUtils.isEmpty(friend.getFriendUser().getPhone()))
            {
                //由原来的需要用户确定绑定手机改成只要有手机就可以发短信
                friendInfo.setPhoneBound(true);
            }
            friendList.addFriend(friendInfo, friend.getGroupId());
        }

        return friendList;
    }

    /**
     * 获得某个群的所有好友列表
     *
     * @param groupId 群ID
     * @return 好友列表
     * @throws Exception 数据库读取数据错误
     */
    public GroupMemberList getGroupMemberList(Integer groupId) throws Exception
    {
        GroupMemberList groupMemberList = new GroupMemberList(groupId);

        List<GroupMember> groupMembers = dao.getGroupMembers(groupId);
        for (GroupMember groupMember : groupMembers)
        {
            ImUserInfo userInfo = new ImUserInfo(groupMember.getUserId(), groupMember.getUser().getUserName(),
                    groupMember.getUser().allSimpleDeptName());

            groupMemberList.addGroupMember(userInfo);
        }

        return groupMemberList;
    }

    /**
     * 获得某个用户的所加入的群列表
     *
     * @param userId 用户id
     * @return 群信息列表
     * @throws Exception 从数据库读取数据错误
     */
    public List<GroupInfo> getGroupList(Integer userId) throws Exception
    {
        List<ImGroup> groups = dao.getGroups(userId);
        List<GroupInfo> groupList = new ArrayList<GroupInfo>(groups.size());

        for (ImGroup group : groups)
        {
            groupList.add(new GroupInfo(group.getGroupId(), group.getGroupName()));
        }

        return groupList;
    }

    /**
     * 获取当前时刻上线或离线的用户所在群的群id
     *
     * @param userId 用户id
     * @return 群id列表
     * @throws Exception 从数据库读取数据错误
     */
    public List<Integer> getGroupIds(Integer userId) throws Exception
    {
        return dao.getGroupIds(userId);
    }

    /**
     * 添加群成员
     * 声明事务，把多个群成员的添加作为一个事务
     *
     * @param memberIds 群成员ID
     * @param groupId   群ID
     * @return 新增到群中的用户ID列表，即memberIds排除原来群中已经有的用户
     * @throws Exception 从数据库读取数据错误或者写数据到数据库错误
     */
    @Transactional
    public List<Integer> addGroupMembers(Integer[] memberIds, Integer groupId) throws Exception
    {
        if (memberIds != null)
        {
            List<Integer> result = new ArrayList<Integer>(memberIds.length);

            for (Integer memberId : memberIds)
            {
                if (addGroupMember(memberId, groupId))
                    result.add(memberId);
            }

            return result;
        }

        return null;
    }

    /**
     * 添加一个用户到群中
     *
     * @param memberId 用户id
     * @param groupId  群id
     * @return 如果此用户原来在群中，则返回false，否则返回true
     * @throws Exception 数据库操作错误
     */
    public boolean addGroupMember(Integer memberId, Integer groupId) throws Exception
    {
        GroupMember groupMember = dao.getMember(memberId, groupId);

        if (groupMember == null)
        {
            groupMember = new GroupMember();
            groupMember.setUserId(memberId);
            groupMember.setGroupId(groupId);
            dao.add(groupMember);

            return true;
        }

        return false;
    }

    /**
     * 添加好友
     * 声明事务，把多个好友的添加作为一个事务
     *
     * @param userId        当前用户ID
     * @param friendUserIds 好友ID
     * @param groupId       好友分组ID
     * @throws Exception 从数据库读取数据错误或者写数据到数据库错误
     */
    @Transactional
    public void addFriends(Integer userId, Integer[] friendUserIds, Long groupId) throws Exception
    {
        if (friendUserIds != null)
        {
            for (Integer friendUserId : friendUserIds)
            {
                addFriend(userId, friendUserId, groupId);
            }
        }
    }

    public void addFriend(Integer userId, Integer friendUserId, Long groupId) throws Exception
    {
        //不添加自己做好友
        if (friendUserId != null && !userId.equals(friendUserId))
        {
            Friend friend = dao.getFriend(userId, friendUserId);

            if (friend == null)
            {
                //以此判断是否对方已添加自己为好友
                Friend friendUser = dao.getFriend(friendUserId, userId);

                //此用户还不是好友，添加之
                friend = new Friend();
                friend.setUserId(userId);
                friend.setFriendUserId(friendUserId);
                friend.setGroupId(groupId);
                friend.setConfirmed(friendUser != null);
                dao.add(friend);

                //如果对方还没有加自己为好友则发送消息
                if (friendUser == null)
                {
                    FriendNotify notify = new FriendNotify(userId, dao.getUserName(userId), FriendNotifyType.add);
                    cometServiceProvider.get().sendMessage(notify, friendUserId);
                }
            }
            else
            {
                //好友已经存在，移动到新的分组
                if (groupId != null)
                {
                    friend.setGroupId(groupId);
                    friend.setFriendGroup(null);
                    dao.update(friend);
                }
            }
        }
    }

    /**
     * 通知好友，在登录或退出时调用
     *
     * @param userId 当前用户ID
     * @param type   通知类型
     * @throws Exception 发送通知错误或者从数据库查询好友信息错误
     */
    public void notifyFriends(Integer userId, FriendNotifyType type) throws Exception
    {
        List<Integer> groupIds = getGroupIds(userId);

        List<Integer> userIds = dao.getUserIdsInWhoseFriends(userId);

        for (Integer groupId : groupIds)
        {
            for (Integer groupUserId : dao.getGroupUserIds(groupId))
            {
                if (!userIds.contains(groupUserId))
                    userIds.add(groupUserId);
            }
        }

        if (userIds.size() > 0)
        {
            FriendNotify notify = new FriendNotify(userId, dao.getUserName(userId), type);
            notify.setGroupIds(groupIds);
            cometServiceProvider.get().sendMessage(notify, userIds);
        }
    }

    /**
     * 发送消息
     *
     * @param message 消息对象，包含了消息的内容，类型，发送者和接收者信息
     * @param sendSms 是否通过手机短信发送给接收者
     * @throws Exception 发送错误
     */
    public void sendUserMessage(UserMessage message, boolean sendSms) throws Exception
    {
        Integer sender = message.getSender();
        Integer receiver = message.getReceiver();
        String senderName = dao.getUserName(sender);

        String attachmentUUID;
        Integer fileSize;
        if (message.getAttachmentId() == null)
        {
            attachmentUUID = null;
            fileSize = null;
        }
        else
        {
            Map<String, Object> map = dao.getAttachmentInfo(message.getAttachmentId());
            attachmentUUID = map.get("uuid").toString();
            fileSize = Integer.parseInt(map.get("fileSize").toString());
        }

        Date time = new Date();
        //保存接收者的聊天记录
        message.setSendTime(time);
        message.setUserId(receiver);
        saveUserMessage(message);

        if (sendSms)
        {
            Message m = new Message();
            m.setSender(sender);
            m.setUserId(receiver);
            m.setForce(true);
            m.setMessage(message.getContent() + "  " + senderName);
            m.setMethods(SmsMessageSender.SMS);
            //编码为ims开头的短信表示从即时消息发送出去的短信
            m.setCode(ImSmsReplyProcessor.IMS + message.getMessageId());
            m.send();
        }

        CometService cometService = cometServiceProvider.get();

        //接收者是否在线
        boolean online = cometService.isOnline(receiver);
        if (online)
        {
            //接收者在线，发送在线消息给接收者
            UserMessageInfo messageInfo = new UserMessageInfo();
            messageInfo.setReaded(message.isReaded());
            messageInfo.setMessageId(message.getMessageId());
            messageInfo.setSender(sender);
            messageInfo.setSenderName(senderName);
            messageInfo.setTime(time);
            messageInfo.setContent(message.getContent());
            messageInfo.setAttachmentId(message.getAttachmentId());
            if (message.getAttachmentId() != null)
            {
                messageInfo.setAttachmentUUID(attachmentUUID);
                messageInfo.setFileSize(fileSize);
            }
            messageInfo.setType(message.getType());

            cometService.sendMessage(messageInfo, receiver);
        }
        //检查接收者是否是ios端，是就推送消息
        MoBind bind = dao.queryMoBindByUserId(message.getReceiver());
        if (bind != null)
        {
            message = dao.load(UserMessage.class, message.getMessageId());
            ApnsUtil.sendMegToIOS(bind.getToken(), message, dao.countAllNotReadMessage(message.getReceiver()));
        }
    }

    @Transactional
    protected void saveUserMessage(final UserMessage message) throws Exception
    {
        Integer sender = message.getSender();
        message.setReaded(false);
        dao.add(message);

        if (sender > 0)
        {
            //保存发送者的聊天记录
            UserMessage message2 = message.cloneMessage();
            message2.setSendTime(message.getSendTime());
            message2.setReaded(true);
            message2.setUserId(sender);
            dao.add(message2);

            Date lastTime = new Date();
            //保存接收者的最近联系人
            ImRecent recent = new ImRecent();
            recent.setUserId(message.getReceiver());
            recent.setType(RecentType.USER);
            recent.setTargetId(sender);
            recent.setLastTime(lastTime);
            dao.save(recent);

            //保存发送者的最近联系人
            ImRecent recent2 = new ImRecent();
            recent2.setUserId(sender);
            recent2.setType(RecentType.USER);
            recent2.setTargetId(message.getReceiver());
            recent2.setLastTime(lastTime);
            dao.save(recent2);
        }
    }

    public void sendSystemMessage(Integer receiver, String content) throws Exception
    {
        UserMessage message = new UserMessage();

        message.setReceiver(receiver);
        message.setSender(-1);
        message.setType(MessageType.html);
        message.setContent(content);

        sendUserMessage(message, false);
    }

    /**
     * 回复某条信息
     *
     * @param content   信息内容
     * @param messageId 被回复的信息的ID
     * @param type      短信类型，只允许取message和sms两种
     * @throws Exception 数据库错误
     */
    public void replyUserMessage(String content, Long messageId, MessageType type) throws Exception
    {
        UserMessage sourceMessage = dao.getUserMessage(messageId);

        UserMessage message = new UserMessage();
        message.setContent(content);
        message.setType(type);
        message.setSender(sourceMessage.getReceiver());
        message.setReceiver(sourceMessage.getSender());

        sendUserMessage(message, false);
    }

    /**
     * 读取某个用户未读的消息
     *
     * @param userId 用户ID
     * @return 此用户未读的消息的列表
     * @throws Exception 数据库查询错误
     */
    @Transactional(mode = TransactionMode.supported)
    public List<UserMessageInfo> getNoReadedUserMessage(Integer userId) throws Exception
    {
        List<UserMessage> messages = dao.getNoReadedUserMessage(userId);

        List<UserMessageInfo> result = new ArrayList<UserMessageInfo>(messages.size());

        for (UserMessage message : messages)
        {
            UserMessageInfo info = new UserMessageInfo();
            info.setReaded(message.isReaded());
            info.setMessageId(message.getMessageId());
            info.setSender(message.getSender());
            info.setSenderName(message.getSenderUser().getUserName());
            info.setTime(message.getSendTime());
            info.setContent(message.getContent());
            info.setAttachmentId(message.getAttachmentId());
            if (message.getAttachmentId() != null)
            {
                Map<String, Object> map = dao.getAttachmentInfo(message.getAttachmentId());
                info.setAttachmentUUID(map.get("uuid").toString());
                info.setFileSize(Integer.parseInt(map.get("fileSize").toString()));
            }
            info.setType(message.getType());
            result.add(info);
        }

        return result;
    }

    /**
     * 读取某个用户未读的群消息
     *
     * @param userId 用户ID
     * @return 此用户未读的消息的列表
     * @throws Exception 数据库查询错误
     */
    @Transactional(mode = TransactionMode.supported)
    public List<GroupMessageInfo> getNoReadedGroupMessage(Integer userId) throws Exception
    {
        List<GroupMessageRecord> groupMessageRecords = dao.getNoReadedGroupMessage(userId);

        List<GroupMessageInfo> result = new ArrayList<GroupMessageInfo>(groupMessageRecords.size());

        for (GroupMessageRecord groupMessageRecord : groupMessageRecords)
        {
            if (groupMessageRecord.getMessage().getSenderUser() != null)
            {
                GroupMessageInfo info = new GroupMessageInfo();
                info.setMessageId(groupMessageRecord.getMessage().getMessageId());
                info.setSender(groupMessageRecord.getMessage().getSender());
                info.setSenderName(groupMessageRecord.getMessage().getSenderUser().getUserName());
                info.setTime(groupMessageRecord.getMessage().getSendTime());
                info.setContent(groupMessageRecord.getMessage().getContent());
                info.setAttachmentId(groupMessageRecord.getMessage().getAttachmentId());
                if (groupMessageRecord.getMessage().getAttachmentId() != null)
                {
                    Map<String, Object> map = dao.getAttachmentInfo(groupMessageRecord.getMessage().getAttachmentId());
                    info.setAttachmentUUID(map.get("uuid").toString());
                    info.setFileSize(Integer.parseInt(map.get("fileSize").toString()));
                }
                info.setGroupId(groupMessageRecord.getMessage().getGroupId());
                info.setType(groupMessageRecord.getMessage().getType());
                info.setOperationType(OperationType.sendMessage);
                result.add(info);
            }
        }
        return result;
    }

    /**
     * 设置某个好友消息已读
     *
     * @param messageId 消息ID
     * @throws Exception 数据库错误
     */
    public void setUserMessageReaded(Long messageId) throws Exception
    {
        UserMessage message = new UserMessage();
        message.setMessageId(messageId);
        message.setReaded(true);

        dao.update(message);
    }

    /**
     * 设置某个群中当前用户的消息已读
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @throws Exception 数据库错误
     */
    public void setGroupMessageReaded(Long messageId, Integer userId) throws Exception
    {
        GroupMessageRecord message = new GroupMessageRecord();
        message.setMessageId(messageId);
        message.setUserId(userId);
        message.setReaded(true);

        dao.update(message);
    }

    /**
     * 发送群消息
     *
     * @param message 消息对象，包含了消息的内容，类型，发送者和接收者信息
     * @param userId  用户ID
     * @throws Exception 发送错误
     */
    public void sendGroupMessage(GroupMessage message, Integer userId) throws Exception
    {
        Integer sender = message.getSender();
        String senderName = dao.getUserName(sender);
        String attachmentUUID;
        Integer fileSize;
        if (message.getAttachmentId() == null)
        {
            attachmentUUID = null;
            fileSize = null;
        }
        else
        {
            Map<String, Object> map = dao.getAttachmentInfo(message.getAttachmentId());
            attachmentUUID = map.get("uuid").toString();
            fileSize = Integer.parseInt(map.get("fileSize").toString());
        }

        Date time = new Date();

        //保存发送者的聊天记录
        message.setSendTime(time);

        final List<Integer> receivers = saveGroupMessage(message, userId);

        CometService cometService = cometServiceProvider.get();

        for (Integer receiver : receivers)
        {
            //接收者是否在线
            boolean online = cometService.isOnline(receiver);
            if (online && !userId.equals(receiver))
            {
                //接收者在线，发送在线消息给接收者
                GroupMessageInfo messageInfo = new GroupMessageInfo();
                messageInfo.setMessageId(message.getMessageId());
                messageInfo.setSender(sender);
                messageInfo.setSenderName(senderName);
                messageInfo.setTime(time);
                messageInfo.setContent(message.getContent());
                messageInfo.setGroupId(message.getGroupId());
                messageInfo.setAttachmentId(message.getAttachmentId());
                messageInfo.setAttachmentUUID(attachmentUUID);
                messageInfo.setFileSize(fileSize);
                messageInfo.setType(message.getType());
                messageInfo.setOperationType(message.getOperationType());
                cometService.sendMessage(messageInfo, receiver);
            }
        }
    }

    @Transactional
    protected List<Integer> saveGroupMessage(GroupMessage message, Integer userId) throws Exception
    {
        dao.add(message);

        List<Integer> receivers = new ArrayList<Integer>();

        for (GroupMember groupMember : getDao().getGroup(message.getGroupId()).getMembers())
        {
            Integer receiver = groupMember.getUserId();

            if (!userId.equals(receiver))
            {
                //把发给群里每一个好友的信息存入数据库
                GroupMessageRecord groupMessageRecord = new GroupMessageRecord();
                groupMessageRecord.setUserId(receiver);
                groupMessageRecord.setMessageId(message.getMessageId());
                groupMessageRecord.setMessage(message);
                groupMessageRecord.setReaded(false);
                dao.add(groupMessageRecord);

                receivers.add(receiver);
            }

            //保存每个用户的最新联系人信息
            ImRecent recent = new ImRecent();
            recent.setUserId(receiver);
            recent.setType(RecentType.GROUP);
            recent.setTargetId(message.getGroupId());
            recent.setLastTime(new Date());
            dao.save(recent);
        }

        return receivers;
    }

    /**
     * 获得某个用户的所有好友分组
     *
     * @param userId 用户id
     * @return 群信息列表
     * @throws Exception 从数据库读取数据错误
     */
    public List<FriendGroup> getFriendGroupList(Integer userId) throws Exception
    {
        return dao.getFriendGroups(userId);
    }

    /**
     * 读取用户与联系人的聊天记录
     *
     * @param userId    用户主键
     * @param linkMan   联系人主键
     * @param startTime 记录开始时间
     * @param endTime   记录结束时间
     * @param type      记录类型
     * @param pageNo    页数
     * @param pageSize  条数
     * @return 此用户与联系人的消息列表
     * @throws Exception 数据库查询错误
     */
    @Transactional(mode = TransactionMode.supported)
    public List<UserMessageInfo> getUserMessage(Integer userId, Integer linkMan, java.sql.Date startTime,
                                                java.sql.Date endTime, Integer type, Integer pageNo, Integer pageSize)
            throws Exception
    {
        if (pageSize == null || Null.Integer.equals(pageSize) || pageSize == 0)
        {
            pageSize = 20;
        }
        if (pageNo == null)
            pageNo = 0;
        else
        {
            pageNo = pageNo * pageSize;
        }
        MessageType messageType = type != null && type == 1 ? MessageType.file : null;
        List<UserMessage> messages =
                dao.getUserMessageByLinkMan(userId, linkMan, startTime, endTime, messageType, pageNo, pageSize);

        List<UserMessageInfo> result = new ArrayList<UserMessageInfo>(messages.size());

        for (UserMessage message : messages)
        {
            UserMessageInfo info = new UserMessageInfo();
            info.setReaded(message.isReaded());
            info.setMessageId(message.getMessageId());
            info.setSender(message.getSender());
            info.setSenderName(message.getSenderUser().getUserName());
            info.setTime(message.getSendTime());
            info.setContent(message.getContent());
            info.setAttachmentId(message.getAttachmentId());
            if (message.getAttachmentId() != null)
            {
                Map<String, Object> map = dao.getAttachmentInfo(message.getAttachmentId());
                info.setAttachmentUUID(map.get("uuid").toString());
                info.setFileSize(Integer.parseInt(map.get("fileSize").toString()));
            }
            info.setType(message.getType());
            result.add(info);
        }

        return result;
    }

    /**
     * 读取用户与群的聊天记录
     *
     * @param groupId   群主键
     * @param startTime 记录开始时间
     * @param endTime   记录结束时间
     * @param type      记录类型
     * @param pageNo    页数
     * @param pageSize  条数
     * @return 此用户与群的消息列表
     * @throws Exception 数据库查询错误
     */
    @Transactional(mode = TransactionMode.supported)
    public List<GroupMessageInfo> getGroupMessage(Integer groupId, java.sql.Date startTime,
                                                  java.sql.Date endTime, Integer type, Integer pageNo, Integer pageSize)
            throws Exception
    {
        if (pageSize == null || Null.Integer.equals(pageSize) || pageSize == 0)
        {
            pageSize = 20;
        }
        if (pageNo == null)
            pageNo = 0;
        else
        {
            pageNo = pageNo * pageSize;
        }
        MessageType messageType = type != null && type == 1 ? MessageType.file : null;

        List<GroupMessage> groupMessages =
                dao.getGroupMessageByGroupId(groupId, startTime, endTime, messageType, pageNo, pageSize);

        List<GroupMessageInfo> result = new ArrayList<GroupMessageInfo>(groupMessages.size());

        for (GroupMessage groupMessage : groupMessages)
        {
            if (groupMessage.getSenderUser() != null)
            {
                GroupMessageInfo info = new GroupMessageInfo();
                info.setMessageId(groupMessage.getMessageId());
                info.setSender(groupMessage.getSender());
                info.setSenderName(groupMessage.getSenderUser().getUserName());
                info.setTime(groupMessage.getSendTime());
                info.setContent(groupMessage.getContent());
                info.setAttachmentId(groupMessage.getAttachmentId());
                if (groupMessage.getAttachmentId() != null)
                {
                    Map<String, Object> map = dao.getAttachmentInfo(groupMessage.getAttachmentId());
                    info.setAttachmentUUID(map.get("uuid").toString());
                    info.setFileSize(Integer.parseInt(map.get("fileSize").toString()));
                }
                info.setGroupId(groupMessage.getGroupId());
                info.setType(groupMessage.getType());
                info.setOperationType(OperationType.sendMessage);
                result.add(info);
            }
        }
        return result;
    }
}
