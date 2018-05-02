package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 和即时消息相关的请求
 *
 * @author camel
 * @date 2010-12-30
 */
@Service
public class ImPage
{
    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    @Inject
    private ImService service;

    @UserId
    private Integer userId;

    @NotSerialized
    private List<FriendGroup> friendGroupList;

    private Integer senderId;

    private Long groupId;

    @NotSerialized
    private InputFile file;

    public ImPage()
    {
    }

    public List<FriendGroup> getFriendGroupList()
    {
        return friendGroupList;
    }

    public void setFriendGroupList(List<FriendGroup> friendGroupList)
    {
        this.friendGroupList = friendGroupList;
    }

    public Integer getSenderId()
    {
        return senderId;
    }

    public void setSenderId(Integer senderId)
    {
        this.senderId = senderId;
    }

    public Long getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    /**
     * 获得用户在线头像
     *
     * @param userId 用户ID
     * @return 用户头像
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/user/{$0}/head/on")
    public byte[] getOnHead(Integer userId) throws Exception
    {
        ImUserConfig config = service.getDao().getImUserConfig(userId);
        if(config != null && config.getHeadImg() != null)
            return config.getHeadImg();

        Sex sex = config.getUser().getSex();
        return IOUtils.fileToBytes(Tools.getAppPath(sex == Sex.female ?
                "im/images/online_female.jpg" : "im/images/online_male.jpg"));
    }

    /**
     * 获得用户离线头像
     *
     * @param userId 用户ID
     * @return 用户头像
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/user/{$0}/head/off")
    public byte[] getOffHead(Integer userId) throws Exception
    {
        ImUserConfig config = service.getDao().getImUserConfig(userId);
        if(config != null)
        {
            byte[] offHeadImg = config.getOffHeadImg();

            if(offHeadImg != null)
                return offHeadImg;

            byte[] headImg = config.getHeadImg();
            if(headImg != null)
            {
                offHeadImg = ImUserConfig.getOffHeadImg(headImg);
                config.setOffHeadImg(offHeadImg);
                service.getDao().update(config);

                return offHeadImg;
            }
        }

        Sex sex = config.getUser().getSex();
        return IOUtils.fileToBytes(Tools.getAppPath(sex == Sex.female ?
                "im/images/offline_female.jpg" : "im/images/offline_male.jpg"));
    }

    /**
     * 获得用户头像，用于个人配置
     *
     * @return 用户头像
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/myself/head")
    public byte[] getHead() throws Exception
    {
        ImUserConfig config = service.getDao().getImUserConfig(userId);
        return config == null || config.getHeadImg() == null ?
                IOUtils.fileToBytes(Tools.getAppPath("im/images/head.gif")) : config.getHeadImg();
    }

    /**
     * 获取群的头像
     *
     * @return ：群头像
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/myself/group")
    public byte[] getGroupHead() throws Exception
    {
        return IOUtils.fileToBytes(Tools.getAppPath("im/images/manage.gif"));
    }

    @Service(url = "/im/sound/{$0}")
    public byte[] sound(String name) throws Exception
    {
        ImUserConfig config = service.getDao().getImUserConfig(userId);

        if(config != null)
        {
            if("msg".equals(name))
            {
                byte[] msgSound = config.getMsgSound();
                if(msgSound != null)
                    return msgSound;
            }
            else
            {
                byte[] onlineSound = config.getOnlineSound();
                if(onlineSound != null)
                    return onlineSound;
            }
        }

        return IOUtils.fileToBytes(RequestContext.getContext().getRealPath("/im/sound/" + name + ".wav"));
    }

    /**
     * 获取某个群的所有好友
     *
     * @param groupId 群id
     * @return 好友列表
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/group/{$0}/groupMembers")
    public GroupMemberList getGroupMembers(Integer groupId) throws Exception
    {
        return service.getGroupMemberList(groupId);
    }

    /**
     * 获取当前时刻上线或离线的用户所在群的群id
     *
     * @param userId 群id
     * @return 群id
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/user/{$0}/groupIds")
    public List<Integer> getGroupIds(Integer userId) throws Exception
    {
        return service.getGroupIds(userId);
    }


    /**
     * 获得当前用户的好友列表
     *
     * @return 好友列表
     * @throws Exception 从数据库读取数据错误
     */
    @NotSerialized
    @Service(url = "/im/myself/friends")
    public FriendList getFriendList() throws Exception
    {
        return service.getFriendList(userId);
    }

    /**
     * 获得当前用户的群列表
     *
     * @return 好友列表
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/myself/groups")
    public List<GroupInfo> getGroupList() throws Exception
    {
        return service.getGroupList(userId);
    }

    /**
     * 发送消息给某个用户
     *
     * @param content  消息内容
     * @param receiver 目标用户的ID
     * @param sms      是否通过手机短信发送，如果true表示通过手机短信发送，否则不通过手机短信发送
     * @throws Exception 发送消息失败
     */
    @Service(url = "/im/sendToUser/{$1}?sms={$2}", method = HttpMethod.post)
    @ObjectResult
    public void sendUserMessage(String content, Integer receiver, boolean sms) throws Exception
    {
        UserMessage message = new UserMessage();
        message.setContent(content);
        message.setReceiver(receiver);
        message.setSender(userId);
        message.setType(MessageType.text);

        service.sendUserMessage(message, sms);
    }

    /**
     * 发送群消息
     *
     * @param content 消息内容
     * @param groupId 群id
     * @throws Exception 发送消息失败
     */
    @Service(url = "/im/sendToGroup/{$1}", method = HttpMethod.post)
    @ObjectResult
    public void sendGroupMessage(String content, Integer groupId) throws Exception
    {
        GroupMessage message = new GroupMessage();
        message.setContent(content);
        message.setGroupId(groupId);
        message.setSender(userId);
        message.setType(MessageType.text);

        service.sendGroupMessage(message, userId);
    }

    /**
     * 发送文件给某个用户
     *
     * @param receiver 目标用户的ID
     * @param type     文件类型，文件或者图片
     * @return 返回编码后的附件ID
     * @throws Exception 发送消息失败
     */
    @Service(url = "/im/sendFileToUser/{$0}?type={$1}", method = HttpMethod.post)
    @ObjectResult
    public String sendUserFile(Integer receiver, MessageType type) throws Exception
    {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getName());
        attachment.setAttachmentName(file.getName());
        attachment.setInputable(file.getInputable());
        attachment.setFileType(type == MessageType.image ? FileType.image : FileType.attachment);
        attachment.setUuid(CommonUtils.uuid());

        Long attachmentId = attachmentServiceProvider.get().save(Collections.singleton(attachment), "im", userId, null);

        UserMessage message = new UserMessage();
        message.setContent(file.getName());
        message.setAttachmentId(attachmentId);
        message.setReceiver(receiver);
        message.setSender(userId);
        message.setType(type);

        service.sendUserMessage(message, false);

        return attachment.getUuid();
    }

    /**
     * 发送文件给某个群
     *
     * @param groupId 群ID
     * @param type    文件类型，文件或者图片
     * @return 编码后的附件ID
     * @throws Exception 发送消息失败
     */
    @Service(url = "/im/sendFileToGroup/{$0}?type={$1}", method = HttpMethod.post)
    @ObjectResult
    public String sendGroupFile(Integer groupId, MessageType type) throws Exception
    {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getName());
        attachment.setAttachmentName(file.getName());
        attachment.setInputable(file.getInputable());
        attachment.setFileType(type == MessageType.image ? FileType.image : FileType.attachment);
        attachment.setUuid(CommonUtils.uuid());

        Long attachmentId = attachmentServiceProvider.get().save(Collections.singleton(attachment), "im", userId, null);

        GroupMessage message = new GroupMessage();
        message.setContent(file.getName());
        message.setAttachmentId(attachmentId);
        message.setGroupId(groupId);
        message.setSender(userId);
        message.setType(type);

        service.sendGroupMessage(message, userId);

        return attachment.getUuid();
    }

    /**
     * 查看某个用户未读的好友消息
     *
     * @return 未读的好友消息列表
     * @throws Exception 数据库查询失败
     */
    @Service(url = "/im/messages/noreaded/user")
    public List<UserMessageInfo> getNoReadedUserMessage() throws Exception
    {
        if(userId == null)
            throw new LoginExpireException();

        return service.getNoReadedUserMessage(userId);
    }

    /**
     * 查看某个用户未读的群信息消息
     *
     * @return 未读的群消息列表
     * @throws Exception 数据库查询失败
     */
    @Service(url = "/im/messages/noreaded/group")
    public List<GroupMessageInfo> getNoReadedGroupMessage() throws Exception
    {
        if(userId == null)
            throw new LoginExpireException();

        return service.getNoReadedGroupMessage(userId);
    }

    /**
     * 设置某个好友消息已读
     *
     * @param messageId 消息ID
     * @throws Exception 数据库操作错误
     */
    @Service(url = "/im/messages/user/{$0}/read")
    public void setUserMessageReaded(Long messageId) throws Exception
    {
        service.setUserMessageReaded(messageId);
    }

    /**
     * 设置某个群众中当前用户的消息已读
     *
     * @param messageId 消息ID
     * @throws Exception 数据库操作错误
     */
    @Service(url = "/im/messages/group/{$0}/read")
    public void setGroupMessageReaded(Long messageId) throws Exception
    {
        service.setGroupMessageReaded(messageId, userId);
    }

    /**
     * 确认被添加为好友的用户已经收到消息
     *
     * @param sender 消息发送人id
     * @return 当前用户的所有用户组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/im/myself/{$0}/confirmFriend")
    public String getFriendGroups(Integer sender) throws Exception
    {
        service.getDao().updateFriend(sender, userId);

        senderId = sender;
        friendGroupList = service.getFriendGroupList(userId);
        groupId = -1L;
        return "/im/confirmed.ptl";
    }

    /**
     * 如果没有好友分组直接添加到我的好友
     *
     * @param sender 消息发送人id
     * @throws Exception 数据库插入异常
     */
    @Service(url = "/im/myself/{$0}/confirmToDefault")
    public void addFriendToDefault(Integer sender) throws Exception
    {
        service.getDao().updateFriend(sender, userId);
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendUserId(sender);
        friend.setConfirmed(true);
        friend.setGroupId(null);
        Long friendId = service.getDao().getFriendId(userId, sender);
        if(friendId != null)
        {
            friend.setFriendId(friendId);
            service.getDao().update(friend);
        }
        else
            service.getDao().save(friend);
    }

    /**
     * 不添加对方为好友
     *
     * @param sender 消息发送者
     * @throws Exception 数据库更新异常
     */
    @Service(url = "/im/myself/{$0}/abolish")
    public void setConfirmed(Integer sender) throws Exception
    {
        service.getDao().updateFriend(sender, userId);
    }

    /**
     * 确认也添加对方为好友
     *
     * @param sender 消息发送人id
     * @throws Exception 数据库插入异常
     */
    @ObjectResult
    @Service
    public void addNewFriend(Integer sender) throws Exception
    {
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendUserId(sender);
        if(groupId != null && groupId >= 0)
            friend.setGroupId(groupId);
        friend.setConfirmed(true);

        Long friendId = service.getDao().getFriendId(userId, sender);
        if(friendId != null)
        {
            friend.setFriendId(friendId);
            service.getDao().update(friend);
        }
        else
            service.getDao().save(friend);
    }

    /**
     * 当用户登录时，获取当前用户未确认的添加好友的提示
     *
     * @return 提示列表
     * @throws Exception 数据库查询异常
     */
    @Service(url = "/im/messages/noconfirmed/friend")
    public List<FriendNotify> getNoConfirmedUserMessage() throws Exception
    {
        if(userId == null)
            throw new LoginExpireException();

        List<User> users = service.getDao().getNoConfirmUsers(userId);
        List<FriendNotify> notifyList = new ArrayList<FriendNotify>(users.size());
        for (User user : users)
        {
            FriendNotify friendNotify =
                    new FriendNotify(user.getUserId(), user.getUserName(), FriendNotifyType.add);
            notifyList.add(friendNotify);
        }
        return notifyList;
    }

    /**
     * 是否自动弹出窗口
     *
     * @throws Exception 数据库更新异常
     */
    @Service(url = "/im/myself/windowFocus")
    @NotSerialized
    public boolean isWindowFocus() throws Exception
    {
        ImUserConfig config = service.getDao().getImUserConfig(userId);

        if(config == null)
            return true;

        Boolean windowFocus = config.getWindowFocus();
        if(windowFocus == null)
            return false;

        return windowFocus;
    }

    /**
     * 是否自动弹出系统消息窗口
     *
     * @throws Exception 数据库更新异常
     */
    @Service(url = "/im/myself/sysAutoShow")
    @NotSerialized
    public boolean isSysAutoShow() throws Exception
    {
        ImUserConfig config = service.getDao().getImUserConfig(userId);

        if(config == null)
            return false;

        Boolean sysAutoShow = config.getSysAutoShow();
        if(sysAutoShow == null)
            return false;

        return sysAutoShow;
    }

    /**
     * 用户与联系人的聊天记录
     *
     * @param linkMan   联系人主键
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param type      消息类型
     * @param pageNo    页数
     * @param pageSize  条数
     * @return 消息记录
     * @throws Exception
     */
    @Service(url = "/im/userMessage/list")
    @ObjectResult
    public List<UserMessageInfo> getUserMessage(Integer linkMan, java.sql.Date startTime, Date endTime, Integer type,
                                                Integer pageNo, Integer pageSize)
            throws Exception
    {
        if(userId == null)
            throw new LoginExpireException();
        return service.getUserMessage(userId, linkMan, startTime, endTime, type, pageNo, pageSize);
    }

    /**
     * 用户与群的聊天记录
     *
     * @param groupId   群主键
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param type      消息类型
     * @param pageNo    页数
     * @param pageSize  条数
     * @return 消息记录
     * @throws Exception
     */
    @Service(url = "/im/groupMessage/list")
    @ObjectResult
    public List<GroupMessageInfo> getGroupMessage(Integer groupId, java.sql.Date startTime, Date endTime, Integer type,
                                                  Integer pageNo, Integer pageSize)
            throws Exception
    {
        if(userId == null)
            throw new LoginExpireException();
        return service.getGroupMessage(groupId, startTime, endTime, type, pageNo, pageSize);
    }

    /**
     * 添加常用联系人
     *
     * @param linkMan 联系人主键
     * @return 联系人主键
     * @throws Exception
     */
    @Service(url = "/im/friend/addLink")
    public Integer addLinkMan(Integer linkMan) throws Exception
    {
        if(userId == null)
            throw new LoginExpireException();

        service.addFriend(userId, linkMan, null);
        return linkMan;
    }
}