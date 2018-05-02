package com.gzzm.platform.message;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 消息相关的数据库操作
 *
 * @author camel
 * @date 2010-5-20
 */
public abstract class MessageDao extends GeneralDao
{
    public MessageDao()
    {
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public UserMessageConfig getMessageConfig(Integer userId) throws Exception
    {
        UserMessageConfig config = load(UserMessageConfig.class, userId);

        if (config == null)
        {
            UserMessageConfig config0 = load(UserMessageConfig.class, 0);

            config = new UserMessageConfig();
            config.setUserId(userId);

            if (config0 != null)
            {
                config.setDefaultMethods(config0.getDefaultMethods());
                config.setSendWhenOnline(config0.isSendWhenOnline());
                config.setSupportMobileOffice(config0.isSupportMobileOffice());
                config.setMethods(config0.getMethods());
                config.setAttributes(config0.getAttributes());
            }

            add(config);

            config = load(UserMessageConfig.class, userId);
        }

        return config;
    }

    @OQL("select m from (select m from ImMessage m where m.userId=:1 order by m.sendTime desc limit 20) m where m.readed=0")
    public abstract List<ImMessage> getNoReadedImMessages(Integer userId) throws Exception;

    public void setImMessageReaded(Long messageId) throws Exception
    {
        ImMessage message = new ImMessage();
        message.setMessageId(messageId);
        message.setReaded(true);

        update(message);
    }
}
