package com.gzzm.platform.message;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.ValueContext;
import net.cyan.commons.validate.annotation.Pattern;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 用户消息配置
 *
 * @author camel
 * @date 2010-5-20
 */
@Entity(table = "PFUSERMESSAGECONFIG", keys = "userId")
public class UserMessageConfig extends ValueContext
{
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关联用户对象
     */
    @NotSerialized
    private User user;

    /**
     * email
     */
    @Pattern(Pattern.EMAIL)
    @ColumnDescription(type = "varchar(40)")
    private String email;

    /**
     * 默认接受的信息发送方式，如果接收多种方式，用逗号隔开
     *
     * @see com.gzzm.platform.message.Message#methods
     * @see MessageSender#getType()
     * @see #methods
     */
    @ColumnDescription(type = "varchar(100)", defaultValue = "'im,sms'")
    private String defaultMethods;

    /**
     * 在线时是否接收出即时消息以外的信息，默认为不接收
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean sendWhenOnline;

    /**
     * 是否支持移动办公功能
     */
    private Boolean supportMobileOffice;

    /**
     * 配置每个具体的功能模块的消息接收方式，key为功能模块的编号，值为消息的接收方式列表，
     * 如果某个功能模块没有定义对应的消息接收方式，则使用defaultMethods字段所配置的接收方式
     * 以xml的方式保存到数据库的大字段中
     *
     * @see com.gzzm.platform.message.Message#methods
     * @see MessageSender#getType()
     * @see #defaultMethods
     */
    @Xml
    private Map<String, List<String>> methods;

    /**
     * 定义一些扩展的属性，由扩展的消息发送方式定义
     * key为属性名，value为属性值
     * 以xml的方式保存到数据库的大字段中
     */
    @Xml
    private Map<String, String> attributes;

    public UserMessageConfig()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getDefaultMethods()
    {
        return defaultMethods;
    }

    public void setDefaultMethods(String defaultMethods)
    {
        this.defaultMethods = defaultMethods;
    }

    public Boolean isSendWhenOnline()
    {
        return sendWhenOnline;
    }

    public void setSendWhenOnline(Boolean sendWhenOnline)
    {
        this.sendWhenOnline = sendWhenOnline;
    }

    public Boolean isSupportMobileOffice()
    {
        return supportMobileOffice;
    }

    public void setSupportMobileOffice(Boolean supportMobileOffice)
    {
        this.supportMobileOffice = supportMobileOffice;
    }

    public Map<String, List<String>> getMethods()
    {
        return methods;
    }

    public void setMethods(Map<String, List<String>> methods)
    {
        this.methods = methods;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name)
    {
        Map<String, String> attributes = getAttributes();
        return (T) (attributes == null ? null : attributes.get(name));
    }

    /**
     * 获得某个功能模块的短信接收方式
     *
     * @param name 功能模块名称
     * @return 短信接收方式列表
     */
    public List<String> getMethods(String name)
    {
        List<String> result = null;

        if (name != null)
        {
            Map<String, List<String>> methods = getMethods();
            if (methods != null)
                result = methods.get(name);
        }

        if (result == null)
        {
            if (defaultMethods != null)
                result = Arrays.asList(defaultMethods.split(","));
            else
                result = Collections.emptyList();
        }

        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserMessageConfig))
            return false;

        UserMessageConfig config = (UserMessageConfig) o;

        return userId.equals(config.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}
