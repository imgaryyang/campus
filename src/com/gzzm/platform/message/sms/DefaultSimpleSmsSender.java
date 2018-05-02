package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.SystemException;
import com.gzzm.sms.*;
import com.gzzm.sms.SmsService;
import net.cyan.commons.util.*;
import net.cyan.commons.util.xml.XmlBeanSerializer;
import net.cyan.nest.annotation.Inject;

import java.io.InputStream;
import java.net.*;
import java.util.List;

/**
 * 使用gzzm的sms包发短信
 *
 * @author camel
 * @date 2010-12-7
 */
public class DefaultSimpleSmsSender implements SimpleSmsSender
{
    @Inject
    private static Provider<SmsService> serviceProvider;

    /**
     * 发短信的服务器url
     */
    private String server;

    /**
     * 发短信的用户名
     */
    private String userName;

    /**
     * 发短信的用户的密码
     */
    private String password;

    /**
     * 客户端编码
     */
    private String clientCode;

    public DefaultSimpleSmsSender()
    {
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getClientCode()
    {
        return clientCode;
    }

    public void setClientCode(String clientCode)
    {
        this.clientCode = clientCode;
    }

    public String send(String phone, String message, String serial) throws Exception
    {
        List<SendResultItem> items;
        if (server == null)
        {
            //通过本地短信发送
            SmsService service = serviceProvider.get();

            SmsUser user = service.getUserByLoginName(userName);
            items = service.sendSms(message, new String[]{phone}, serial, user, clientCode);
        }
        else
        {
            //通过网络服务器发送短信

            URLConnection connection = new URL(server + "/sms/send").openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            StringBuilder buffer = new StringBuilder("userName=").append(userName).append("&password=").append(password)
                    .append("&phone=").append(phone).append("&content=")
                    .append(CommonUtils.byteArrayToBase64(message.getBytes("UTF-8"))).append("&base64=true");

            if (!StringUtils.isEmpty(serial))
                buffer.append("&serial=").append(serial);

            connection.getOutputStream().write(buffer.toString().getBytes("UTF-8"));

            connection.connect();

            InputStream in = connection.getInputStream();

            SendResult result = XmlBeanSerializer.load(in, SendResult.class);

            if (!StringUtils.isEmpty(result.getError()))
                throw new SystemException(result.getError());

            items = result.getItems();
        }

        SendResultItem item = items.get(0);
        if (!StringUtils.isEmpty(item.getError()))
            throw new SystemException(item.getError());

        return Long.toString(item.getSmsId());
    }
}
