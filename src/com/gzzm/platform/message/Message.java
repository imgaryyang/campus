package com.gzzm.platform.message;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.*;
import com.gzzm.platform.menu.MenuContainer;
import com.gzzm.platform.message.sms.SmsMessageSender;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.Serializable;
import java.util.*;

/**
 * 通过系统发送的一个消息
 *
 * @author camel
 * @date 2010-5-20
 */
public class Message implements Serializable
{
    private static final long serialVersionUID = 1148494952903455078L;

    @Inject
    private static Provider<MessageDao> daoProvider;

    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    @Inject
    private static Provider<List<MessageSender>> sendersProvider;

    @Inject
    private static Provider<UserOnlineList> userOnlineListProvider;

    /**
     * 消息code，由每个模块自己定义，用于标识消息的唯一性，前三位为模块编码，后面的为id
     * 可以为空，当需要接收反馈消息或者对消息进一步处理时，则必须定义此messageCode。
     * 当有反馈消息时，会根据前三位的模块编码找到相应的消息处理器，处理反馈消息
     *
     * @see com.gzzm.platform.message.sms.SmsLog#messageCode
     */
    private String code;

    /**
     * 用户ID，接收消息的用户，可以为null
     */
    private Integer userId;

    /**
     * 短信发送者用户ID，可以为null
     */
    private Integer sender;

    /**
     * 发送消息的部门，可以为null，一般不为null
     */
    private Integer fromDeptId;

    /**
     * 接收消息的部门，可以为null
     */
    private Integer toDeptId;

    /**
     * 发送短信的功能模块
     */
    private String app;

    /**
     * 接收者的电话，可以为null，如果为null，并且userId不为null，由userId决定
     */
    private String phone;

    /**
     * 接收者的电子邮件，可以为null，如果为null，并且userId不为null，由userId决定
     */
    private String email;

    /**
     * 是否强制发送，如果true表示即使用户在线也发送消息，否则只发送给不在线的用户
     */
    private boolean force;

    /**
     * 标题
     */
    private String title;

    /**
     * 信息，即时信息或者短信息时的内容，如果为null则使用content代替
     */
    private String message;

    /**
     * 内容，如果为null，则使用message代替
     */
    private String content;

    /**
     * 查看消息的url
     */
    private String[] urls;

    /**
     * 失效时间，超过此时间之后消息自动失效
     */
    private Date timeout;

    /**
     * 其它属性，留给扩展的消息接口使用
     */
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private Map<Object, Object> properties;

    /**
     * 发送方式，如im，sms，email等，对用MessageSender.getType
     * 如果为null，则自动判断发送方式，如果定义了userId，根据用户配置判断通过那些方式发送消息
     * 如果没有定义userId，则尝试每种方式，如果设置了此方式所需要的参数，则通过此方式发送出去
     *
     * @see MessageSender#getType()
     * @see com.gzzm.platform.message.UserMessageConfig#methods
     * @see com.gzzm.platform.message.UserMessageConfig#defaultMethods
     */
    private Collection<String> methods;

    private transient UserMessageConfig config;

    private transient User user;

    private transient Integer userDeptId;

    /**
     * 定时发送时间
     */
    private Date sendTime;

    public Message()
    {
    }

    public Message(Message m)
    {
        fromDeptId = m.fromDeptId;
        app = m.app;
        force = m.force;
        title = m.title;
        message = m.message;
        content = m.content;
        properties = m.properties;
        methods = m.methods;
        urls = m.urls;
        code = m.code;
    }

    public static List<MessageSender> getSenders()
    {
        return sendersProvider.get();
    }

    public static List<KeyValue<String>> getAllMethods()
    {
        List<MessageSender> senders = getSenders();
        List<KeyValue<String>> methods = new ArrayList<KeyValue<String>>(senders.size());

        String name = Message.class.getName();
        for (MessageSender sender : senders)
        {
            String type = sender.getType();
            methods.add(new KeyValue<String>(type, Tools.getMessage(name + "." + type)));
        }

        return methods;
    }

    public static List<String> getOnlineMethods()
    {
        List<MessageSender> senders = getSenders();
        List<String> methods = new ArrayList<String>(senders.size());

        for (MessageSender sender : senders)
        {
            if (sender instanceof OnlineMessageSender)
            {
                String type = sender.getType();
                methods.add(type);
            }
        }

        return methods;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public synchronized Integer getFromDeptId() throws Exception
    {
        if (fromDeptId == null)
        {
            if (sender != null)
            {
                List<Dept> depts = daoProvider.get().getUser(sender).getDepts();
                if (depts.size() > 0)
                    fromDeptId = depts.get(0).getDeptId();
            }
            else
            {
                fromDeptId = getUserDeptId();
            }
        }

        return fromDeptId;
    }

    public void setFromDeptId(Integer fromDeptId)
    {
        this.fromDeptId = fromDeptId;
    }

    public Integer getToDeptId() throws Exception
    {
        if (toDeptId == null)
            toDeptId = getUserDeptId();

        return toDeptId;
    }

    public void setToDeptId(Integer toDeptId)
    {
        this.toDeptId = toDeptId;
    }

    public String getApp()
    {
        return app;
    }

    public void setApp(String app)
    {
        this.app = app;
    }

    public synchronized String getPhone() throws Exception
    {
        if (phone == null && userId != null)
            phone = getUser().getPhone();

        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public synchronized String getEmail() throws Exception
    {
        if (email == null && userId != null)
            email = getConfig().getEmail();

        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isForce()
    {
        return force;
    }

    public void setForce(boolean force)
    {
        this.force = force;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMessage()
    {
        return message == null ? content : message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getContent()
    {
        return content == null ? message : content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String[] getUrls()
    {
        return urls;
    }

    public void setUrls(String... urls)
    {
        this.urls = urls;
    }

    public void setUrl(String url)
    {
        this.urls = new String[]{url};
    }

    public List<String> getAppIds()
    {
        return menuContainerProvider.get().getMenuIdsByUrl(urls);
    }

    public Date getTimeout()
    {
        return timeout;
    }

    public void setTimeout(Date timeout)
    {
        this.timeout = timeout;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Object key)
    {
        return properties == null ? null : (T) properties.get(key);
    }

    public void setProperty(Object key, Object value)
    {
        if (properties == null)
            properties = new HashMap<Object, Object>();

        properties.put(key, value);
    }

    public Collection<String> getMethods()
    {
        return methods;
    }

    public void setMethods(Collection<String> methods)
    {
        this.methods = methods;
    }

    public void setMethods(String... methods)
    {
        if (methods != null)
            this.methods = Arrays.asList(methods);
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public void send()
    {
        //另起一个线程发送信息
        Tools.run(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    send0();
                }
                catch (Throwable ex)
                {
                    Tools.log("send message fail,userId:" + userId, ex);
                }
            }
        });
    }

    private synchronized UserMessageConfig getConfig() throws Exception
    {
        if (config == null && userId != null)
            config = daoProvider.get().getMessageConfig(userId);

        return config;
    }

    private synchronized User getUser() throws Exception
    {
        if (user == null && userId != null)
            user = daoProvider.get().getUser(userId);

        return user;
    }

    private synchronized Integer getUserDeptId() throws Exception
    {
        if (userDeptId == null && userId != null)
        {
            List<Dept> depts = getUser().getDepts();
            if (depts.size() > 0)
                userDeptId = depts.get(0).getDeptId();
        }

        return userDeptId;
    }

    /**
     * 发送消息
     *
     * @throws Exception 获得用户的消息配置信息错误
     */
    private void send0() throws Exception
    {
        List<MessageSender> senders = getSenders();
        if (senders != null)
        {
            Collection<String> methods = this.methods;

            //标识是否发送除了在线消息以外的信息
            boolean sendExceptOnline;

            if (userId != null)
            {
                //没有指定消息发送方式，使用用户配置的消息发送方式
                if (methods == null)
                    methods = getConfig().getMethods(app);

                //不强制发送，并在线时不发送消息
                sendExceptOnline =
                        force || getConfig().isSendWhenOnline() || !userOnlineListProvider.get().isOnline(userId);
            }
            else
            {
                sendExceptOnline = true;
            }

            for (MessageSender sender : getSenders())
            {
                if ((sendExceptOnline || sender instanceof OnlineMessageSender) &&
                        (methods == null || methods.contains(sender.getType())))
                {
                    try
                    {
                        sender.send(this);
                    }
                    catch (Throwable ex)
                    {
                        Tools.log("send message fail," + message, ex);
                    }
                }
            }
        }
    }

    private void sendTo(final Collection<String> appIds, final List<Integer> deptIds)
    {
        //另开一个线程遍历用户，不影响当前线程
        Tools.run(new Runnable()
        {
            public void run()
            {
                try
                {
                    Collection<String> appIds1 = appIds;
                    if (appIds1 == null)
                        appIds1 = getAppIds();

                    if (deptIds == null || deptIds.size() == 0)
                    {
                        sendTo0(appIds1);
                    }
                    else
                    {
                        sendTo0(appIds1, deptIds);
                    }
                }
                catch (Throwable ex)
                {
                    Tools.log("send message to users fail,appIds:" + appIds + ",deptIds:" + deptIds, ex);
                }
            }
        });
    }

    public void sendTo(String appId, List<Integer> deptIds)
    {
        sendTo(Collections.singleton(appId), deptIds);
    }

    public void sendTo(String appId, Integer... deptIds)
    {
        if (deptIds == null || deptIds.length == 0)
            sendTo(appId, (Integer) null);
        else
            sendTo(appId, Arrays.asList(deptIds));
    }

    public void sendTo(List<Integer> deptIds)
    {
        sendTo((Collection<String>) null, deptIds);
    }

    public void sendTo(Integer... deptIds)
    {
        sendTo((Collection<String>) null, deptIds == null ? null : Arrays.asList(deptIds));
    }

    public void sendTo(String appId)
    {
        sendTo(appId, (Integer) null);
    }

    public void sendWithUrl()
    {
        sendTo((Collection<String>) null, null);
    }

    /**
     * 发送消息给某个部门拥有某些权限的用户
     *
     * @param appId  应用的id，一般情况下为菜单ID，也可以是某些隐藏的特殊应用
     * @param deptId 部门ID
     */
    public void sendTo(final String appId, final Integer deptId)
    {
        //另开一个线程遍历用户，不影响当前线程
        Tools.run(new Runnable()
        {
            public void run()
            {
                try
                {
                    sendTo0(appId, deptId);
                }
                catch (Throwable ex)
                {
                    Tools.log("send message to users fail,appId:" + appId + ",deptId:" + deptId, ex);
                }
            }
        });
    }

    private void sendTo0(Collection<String> appIds) throws Exception
    {
        DeptService service = deptServiceProvider.get();
        Set<Integer> sendedUserIds = new HashSet<Integer>();

        for (String appId : appIds)
        {
            sendTo(appId, null, service, sendedUserIds);
        }
    }

    private void sendTo0(Collection<String> appIds, List<Integer> deptIds) throws Exception
    {
        DeptService service = deptServiceProvider.get();
        Set<Integer> sendedUserIds = new HashSet<Integer>();

        for (String appId : appIds)
        {
            for (Integer deptId : deptIds)
                sendTo(appId, deptId, service, sendedUserIds);
        }
    }


    private void sendTo0(Collection<String> appIds, Integer... deptIds) throws Exception
    {
        DeptService service = deptServiceProvider.get();
        Set<Integer> sendedUserIds = new HashSet<Integer>();

        for (String appId : appIds)
        {
            for (Integer deptId : deptIds)
                sendTo(appId, deptId, service, sendedUserIds);
        }
    }

    private void sendTo0(String appId, Integer deptId) throws Exception
    {
        sendTo(appId, deptId, deptServiceProvider.get(), null);
    }

    /**
     * 给某个部门里拥有权限的用户发送短信
     *
     * @param appId         应用的id，一般情况下为菜单ID，也可以是某些隐藏的特殊应用
     * @param deptId        部门ID
     * @param service       部门服务，用于查询某个部门下拥有某一权限的用户列表
     * @param sendedUserIds 已经发送过消息的用户集合，防止重复发送
     * @throws Exception 从数据库中查询用户
     */
    private void sendTo(String appId, Integer deptId, DeptService service, Set<Integer> sendedUserIds) throws Exception
    {
        Collection<Integer> userIds =
                deptId == null ? service.getUserIdsByApp(appId) : service.getUserIdsByApp(appId, deptId);

        for (Integer userId : userIds)
        {
            if (sendedUserIds != null)
            {
                if (sendedUserIds.contains(userId))
                    continue;
                else
                    sendedUserIds.add(userId);
            }

            //给每一个有权限的人发送短息

            Message message = new Message(this);
            message.setUserId(userId);
            message.setToDeptId(deptId);

            message.send();
        }
    }

    public void sendToAllUsers() throws Exception
    {
        //另开一个线程遍历用户，不影响当前线程
        Tools.run(new Runnable()
        {
            public void run()
            {
                try
                {
                    sendToAllUsers0();
                }
                catch (Throwable ex)
                {
                    Tools.log("send message to all users fail", ex);
                }
            }
        });
    }

    private void sendToAllUsers0() throws Exception
    {
        for (Integer userId : deptServiceProvider.get().getDao().getAllUserIds())
        {
            Message message = new Message(this);
            message.setUserId(userId);

            message.send();
        }
    }

    public void sendToAllOnlineUsers() throws Exception
    {
        //另开一个线程遍历用户，不影响当前线程
        Tools.run(new Runnable()
        {
            public void run()
            {
                try
                {
                    sendToAllOnlineUsers0();
                }
                catch (Throwable ex)
                {
                    Tools.log("send message to all users fail", ex);
                }
            }
        });
    }

    private void sendToAllOnlineUsers0() throws Exception
    {
        for (UserOnlineInfo userOnlineInfo : userOnlineListProvider.get().query(null))
        {
            Message message = new Message(this);
            message.setUserId(userOnlineInfo.getUserId());

            message.send();
        }
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "userId=" + userId +
                ", fromDeptId=" + fromDeptId +
                ", toDeptId=" + toDeptId +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", force=" + force +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", content='" + content + '\'' +
                ", properties=" + properties +
                '}';
    }

    public static void sendSmsToPhone(String content, String phone) throws Exception
    {
        Message message = new Message();

        message.setMessage(content);
        message.setPhone(phone);
        message.setMethods(SmsMessageSender.SMS);

        message.send();
    }

    public static void sendMessageToUser(String content, Integer userId) throws Exception
    {
        Message message = new Message();

        message.setMessage(content);
        message.setUserId(userId);
        message.setForce(true);

        message.send();
    }
}