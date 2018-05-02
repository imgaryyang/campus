package com.gzzm.platform.message;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户消息配置
 *
 * @author camel
 * @date 2010-5-23
 */
@Service
public class UserMessageConfigPage
{
    @Inject
    private MessageDao dao;

    @UserId
    private Integer userId;

    @NotSerialized
    private UserMessageConfig config;

    /**
     * 默认的消息发送方式
     */
    private List<String> defaultMethods;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 办公电话
     */
    private String officePhone;

    public UserMessageConfigPage()
    {
    }

    public UserMessageConfig getConfig()
    {
        return config;
    }

    public void setConfig(UserMessageConfig config)
    {
        this.config = config;
    }

    public List<String> getDefaultMethods()
    {
        return defaultMethods;
    }

    public void setDefaultMethods(List<String> defaultMethods)
    {
        this.defaultMethods = defaultMethods;
    }

    /**
     * 所有可选的消息通知方式
     *
     * @return 所有可选的发送方式
     */
    @NotSerialized
    public List<KeyValue<String>> getAllMethods()
    {
        return Message.getAllMethods();
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getOfficePhone()
    {
        return officePhone;
    }

    public void setOfficePhone(String officePhone)
    {
        this.officePhone = officePhone;
    }

    @Service(url = "/message/config")
    public String showConfig() throws Exception
    {
        config = dao.getMessageConfig(userId);

        String defaultMethodString = config.getDefaultMethods();
        if (defaultMethodString != null)
            defaultMethods = Arrays.asList(defaultMethodString.split(","));

        phone = dao.getUser(userId).getPhone();


        final Map<String, List<String>> methods = config.getMethods();

        //设置某个功能的通知方式如果没有配置的话则使用默认配置
        config.setMethods(new Map<String, List<String>>()
        {
            public int size()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public boolean isEmpty()
            {
                return false;
            }

            public boolean containsKey(Object key)
            {
                return true;
            }

            public boolean containsValue(Object value)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public List<String> get(Object key)
            {
                String name = (String) key;

                List<String> result = null;

                if (name != null)
                {
                    if (methods != null)
                        result = methods.get(name);
                }

                if (result == null)
                    result = defaultMethods;

                return result;
            }

            public List<String> put(String key, List<String> value)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public List<String> remove(Object key)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public void putAll(Map<? extends String, ? extends List<String>> m)
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public void clear()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public Set<String> keySet()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public Collection<List<String>> values()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }

            public Set<Entry<String, List<String>>> entrySet()
            {
                throw new UnsupportedOperationException("Method not implemented.");
            }
        });

        return "config";
    }

    @Service(url = "/message/config", method = HttpMethod.post)
    public void saveConfig() throws Exception
    {
        config.setUserId(userId);

        if (config.isSendWhenOnline() == null)
            config.setSendWhenOnline(false);

        if (config.isSupportMobileOffice() == null)
            config.setSupportMobileOffice(false);

        if (defaultMethods == null)
            config.setDefaultMethods("");
        else
            config.setDefaultMethods(StringUtils.concat(defaultMethods, ","));

        dao.save(config);

        if (phone != null || officePhone != null)
        {
            User user = new User();
            user.setPhone(phone);
            user.setOfficePhone(officePhone);
            user.setUserId(userId);

            dao.update(user);
        }
    }
}
