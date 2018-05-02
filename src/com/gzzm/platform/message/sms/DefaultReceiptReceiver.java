package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.*;
import com.gzzm.sms.*;
import com.gzzm.sms.SmsService;
import net.cyan.commons.util.*;
import net.cyan.commons.util.xml.XmlBeanSerializer;
import net.cyan.nest.annotation.Inject;

import java.io.InputStream;
import java.net.*;
import java.util.*;

/**
 * 使用gzzm的sms包接收短信回执
 *
 * @author camel
 * @date 2011-2-14
 */
public class DefaultReceiptReceiver implements ReceiptReceiver
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

    private String clientCode;

    public DefaultReceiptReceiver()
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

    public Collection<ReceiptInfo> receive() throws Exception
    {
        if (!StringUtils.isEmpty(userName))
            return receiveWithUserName();

        else
            return receiveWithUsers();
    }

    public Collection<ReceiptInfo> receiveWithUserName() throws Exception
    {
        List<Receipt> receiptList;
        List<SmsItem> smsList;

        if (server == null)
        {
            //通过本地接收短信
            SmsService service = serviceProvider.get();

            SmsUser user = service.getUserByLoginName(userName);
            receiptList = new ReceiptList(service.readSmsMtList(user.getUserId(), clientCode)).getReceiptList();
            smsList = new SmsList(service.readSmsMoList(user.getUserId(), clientCode)).getSmsList();
        }
        else
        {
            //通过网络服务器接收短信
            URLConnection connection;
            InputStream in;

            connection =
                    new URL(server + "/sms/receipt?userName=" + userName + "&password=" + password).openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();
            in = connection.getInputStream();

            ReceiptList receiptResult = XmlBeanSerializer.load(in, ReceiptList.class);
            if (!StringUtils.isEmpty(receiptResult.getError()))
                throw new SystemException(receiptResult.getError());
            receiptList = receiptResult.getReceiptList();


            connection =
                    new URL(server + "/sms/receive?userName=" + userName + "&password=" + password).openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();
            in = connection.getInputStream();

            SmsList receiveList = XmlBeanSerializer.load(in, SmsList.class);
            if (!StringUtils.isEmpty(receiveList.getError()))
                throw new SystemException(receiveList.getError());
            smsList = receiveList.getSmsList();
        }

        int m = 0;
        if (receiptList != null)
            m = receiptList.size();

        if (smsList != null)
            m += smsList.size();

        if (m > 0)
        {
            List<ReceiptInfo> result = new ArrayList<ReceiptInfo>(m);

            if (receiptList != null)
            {
                //接收回执
                for (Receipt receipt : receiptList)
                {
                    ReceiptInfo receiptInfo = new ReceiptInfo();
                    receiptInfo.setMessageId(Long.toString(receipt.getSmsId()));
                    receiptInfo.setSerial(receipt.getSerial());
                    receiptInfo.setPhone(receipt.getPhone());
                    receiptInfo.setTime(receipt.getReceiveTime());
                    receiptInfo.setType(StringUtils.isEmpty(receipt.getError()) ? 0 : 2);
                    receiptInfo.setContent(receipt.getError());
                    result.add(receiptInfo);
                }
            }

            if (smsList != null)
            {
                //短信回复
                for (SmsItem sms : smsList)
                {
                    if (!StringUtils.isEmpty((sms.getSerial())))
                    {
                        ReceiptInfo receiptInfo = new ReceiptInfo();
                        receiptInfo.setMessageId(sms.getSerial());
                        receiptInfo.setSerial(sms.getSerial());
                        receiptInfo.setPhone(sms.getPhone());
                        receiptInfo.setContent(sms.getContent());
                        receiptInfo.setTime(sms.getReceiveTime());
                        receiptInfo.setType(1);
                        result.add(receiptInfo);
                    }
                }
            }

            return result;
        }

        return null;
    }

    public Collection<ReceiptInfo> receiveWithUsers() throws Exception
    {
        List<Receipt> receiptList = null;
        List<SmsItem> smsList = null;
        SmsService service = serviceProvider.get();

        for (SmsUser user : service.getDao().getAllDeptUsers())
        {
            List<Receipt> receiptList1;
            List<SmsItem> smsList1;
            if (server == null)
            {
                //通过本地接收短信
                receiptList1 =
                        new ReceiptList(service.readSmsMtList(user.getUserId(), clientCode)).getReceiptList();
                smsList1 = new SmsList(service.readSmsMoList(user.getUserId(), clientCode)).getSmsList();
            }
            else
            {
                //通过网络服务器发送短信
                URLConnection connection;
                InputStream in;

                connection =
                        new URL(server + "/sms/receipt?userName=" + userName + "&password=" + password)
                                .openConnection();
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.connect();
                in = connection.getInputStream();

                ReceiptList receiptResult = XmlBeanSerializer.load(in, ReceiptList.class);
                if (!StringUtils.isEmpty(receiptResult.getError()))
                    throw new SystemException(receiptResult.getError());
                receiptList1 = receiptResult.getReceiptList();


                connection =
                        new URL(server + "/sms/receive?userName=" + userName + "&password=" + password)
                                .openConnection();
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.connect();
                in = connection.getInputStream();

                SmsList receiveList = XmlBeanSerializer.load(in, SmsList.class);
                if (!StringUtils.isEmpty(receiveList.getError()))
                    throw new SystemException(receiveList.getError());
                smsList1 = receiveList.getSmsList();
            }

            if (receiptList == null)
                receiptList = receiptList1;
            else
                receiptList.addAll(receiptList1);

            if (smsList == null)
                smsList = smsList1;
            else
                smsList.addAll(smsList1);
        }

        int m = 0;
        if (receiptList != null)
            m = receiptList.size();

        if (smsList != null)
            m += smsList.size();

        if (m > 0)
        {
            List<ReceiptInfo> result = new ArrayList<ReceiptInfo>(m);

            if (receiptList != null)
            {
                //接收回执
                for (Receipt receipt : receiptList)
                {
                    ReceiptInfo receiptInfo = new ReceiptInfo();
                    receiptInfo.setMessageId(Long.toString(receipt.getSmsId()));
                    receiptInfo.setSerial(receipt.getSerial());
                    receiptInfo.setPhone(receipt.getPhone());
                    receiptInfo.setTime(receipt.getReceiveTime());
                    receiptInfo.setType(StringUtils.isEmpty(receipt.getError()) ? 0 : 2);
                    receiptInfo.setContent(receipt.getError());
                    result.add(receiptInfo);
                }
            }

            if (smsList != null)
            {
                //短信回复
                for (SmsItem sms : smsList)
                {
                    if (!StringUtils.isEmpty((sms.getSerial())))
                    {
                        ReceiptInfo receiptInfo = new ReceiptInfo();
                        receiptInfo.setMessageId(sms.getSerial());
                        receiptInfo.setSerial(sms.getSerial());
                        receiptInfo.setPhone(sms.getPhone());
                        receiptInfo.setContent(sms.getContent());
                        receiptInfo.setTime(sms.getReceiveTime());
                        receiptInfo.setType(1);
                        result.add(receiptInfo);
                    }
                }
            }

            return result;
        }


        return null;
    }
}
