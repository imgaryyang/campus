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
public class DefaultSmsSender implements SmsSender
{
    @Inject
    private static Provider<SmsDao> daoProvider;

    @Inject
    private static Provider<SmsService> serviceProvider;

    /**
     * 发短信的服务器url
     */
    private String server;

    /**
     * 客户端编码
     */
    private String clientCode;

    public DefaultSmsSender()
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

    public String getClientCode()
    {
        return clientCode;
    }

    public void setClientCode(String clientCode)
    {
        this.clientCode = clientCode;
    }

    public void send(SmsLog smsLog) throws Exception
    {
        String serial = null;

        if (!StringUtils.isEmpty(smsLog.getMessageCode()))
        {
            serial = daoProvider.get().getSmsSerial().toString();
            smsLog.setSerial(serial);
        }

        SmsService service = serviceProvider.get();
        Integer fromDeptId = smsLog.getFromDeptId();
        if (fromDeptId == null)
            fromDeptId = 1;

        SmsUser user = service.getUserByDeptId(fromDeptId);

        List<SendResultItem> items;

        if (server == null)
        {
            //通过本地短信发送
            items = service.sendSms(smsLog.getContent(), new String[]{smsLog.getPhone()}, serial, user,
                    clientCode);
        }
        else
        {
            //通过网络服务器发送短信

            URLConnection connection = new URL(server + "/sms/send").openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            StringBuilder buffer = new StringBuilder("userName=").append(user.getLoginName()).append("&password=")
                    .append(user.getPassword()).append("&phone=").append(smsLog.getPhone()).append("&content=")
                    .append(CommonUtils.byteArrayToBase64(smsLog.getContent().getBytes("UTF-8"))).append(
                            "&base64=true");

            if (serial != null)
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

        smsLog.setMessageId(Long.toString(item.getSmsId()));
    }
}
