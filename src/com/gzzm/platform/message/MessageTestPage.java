package com.gzzm.platform.message;

import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.KeyValue;

import java.util.List;

/**
 * 消息测试页
 *
 * @author camel
 * @date 2011-3-29
 */
@Service
public class MessageTestPage
{
    /**
     * 当前用户ID
     */
    @UserId
    private Integer userId;

    /**
     * 发送的消息内容
     */
    public String content;

    /**
     * 发送消息的方式
     */
    private String[] method;

    public MessageTestPage()
    {
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String[] getMethod()
    {
        return method;
    }

    public void setMethod(String[] method)
    {
        this.method = method;
    }

    /**
     * 所有可选的发送方式
     *
     * @return 所有可选的发送方式
     */
    @NotSerialized
    public List<KeyValue<String>> getAllMethods()
    {
        return Message.getAllMethods();
    }

    @Forward(page = "/platform/message/test.ptl")
    @Service(url = "/message/test")
    public String show()
    {
        return null;
    }

    @Service(method = HttpMethod.post)
    public void send()
    {
        Message message = new Message();

        message.setUserId(userId);
        message.setMethods(method);
        message.setContent(content);
        message.setForce(true);

        message.send();
    }
}
