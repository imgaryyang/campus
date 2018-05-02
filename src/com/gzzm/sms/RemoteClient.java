package com.gzzm.sms;

import net.cyan.commons.log.LightLog;
import net.cyan.commons.pool.job.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.xml.XmlBeanSerializer;
import net.cyan.sms.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author camel
 * @date 13-2-6
 */
public class RemoteClient implements BaseSmsClient
{
    private String server;

    private int port = 80;

    private String userName;

    private String password;

    private Schedule schedule;

    private SmsProcessor smsProcessor;

    private LightLog log;

    public RemoteClient()
    {
    }

    public void init(String server, int port) throws IOException, SmsException
    {
        this.server = server;
        this.port = port;
    }

    public void init(String server) throws IOException, SmsException
    {
        this.server = server;
    }

    public void login(final String userName, final String password) throws IOException, SmsException
    {
        this.userName = userName;
        this.password = password;

        schedule = new Schedule();
        schedule.start();

        schedule.addJob(new SingleRunnable(new Runnable()
        {
            public void run()
            {
                if (smsProcessor != null)
                {
                    //通过网络服务器接收短信
                    URLConnection connection;
                    InputStream in;

                    try
                    {
                        connection = new URL("http://" + server + ":" + port + "/sms/receipt?userName=" + userName
                                + "&password=" + password).openConnection();

                        connection.setDoInput(true);
                        connection.setUseCaches(false);
                        connection.connect();
                        in = connection.getInputStream();

                        ReceiptList receiptResult = XmlBeanSerializer.load(in, ReceiptList.class);
                        if (!StringUtils.isEmpty(receiptResult.getError()))
                        {
                            if (log != null)
                                log.error(receiptResult.getError());
                            return;
                        }
                        List<Receipt> receiptList = receiptResult.getReceiptList();
                        if (receiptList != null)
                        {
                            //接收回执
                            for (Receipt receipt : receiptList)
                            {
                                try
                                {
                                    Mr mr = new Mr();
                                    mr.setMessageId(Long.toString(receipt.getSmsId()));
                                    mr.setTargetPhone(receipt.getPhone());
                                    mr.setTime(receipt.getReceiveTime());
                                    if (!StringUtils.isEmpty(receipt.getError()))
                                    {
                                        mr.setError(receipt.getError());
                                    }

                                    smsProcessor.process(mr, RemoteClient.this);
                                }
                                catch (Throwable ex)
                                {
                                    if (log != null)
                                        log.error(ex);
                                }
                            }
                        }

                        connection = new URL("http://" + server + ":" + port + "/sms/receive?userName=" + userName +
                                "&password=" + password).openConnection();
                        connection.setDoInput(true);
                        connection.setUseCaches(false);
                        connection.connect();
                        in = connection.getInputStream();

                        SmsList receiveList = XmlBeanSerializer.load(in, SmsList.class);
                        if (!StringUtils.isEmpty(receiveList.getError()))
                        {
                            if (log != null)
                                log.error(receiptResult.getError());
                            return;
                        }
                        List<SmsItem> smsList = receiveList.getSmsList();
                        if (smsList != null)
                        {
                            for (SmsItem item : smsList)
                            {
                                try
                                {
                                    Mo mo = new Mo();
                                    mo.setContent(item.getContent());
                                    mo.setSrcPhone(item.getPhone());
                                    mo.setTime(item.getReceiveTime());
                                    mo.setSerial(item.getSerial());


                                    smsProcessor.process(mo, RemoteClient.this);
                                }
                                catch (Throwable ex)
                                {
                                    if (log != null)
                                        log.error(ex);
                                }
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        if (log != null)
                            log.error(ex);
                    }
                }
            }
        }), new SimpleTrigger(20 * 1000));
    }

    public void ping() throws IOException, SmsException
    {
    }

    public void initArgs(Map<String, Object> args)
    {
    }

    public void setSpNumber(String spNumber)
    {
    }

    public void setLog(LightLog log)
    {
        this.log = log;
    }

    public void setSmsProcessor(SmsProcessor smsProcessor)
    {
        this.smsProcessor = smsProcessor;
    }

    public void sendSms(Mt sms, final SmsSendCallback callback) throws IOException, SmsException
    {
        //通过网络服务器发送短信

        URLConnection connection = new URL("http://" + server + ":" + port + "/sms/send").openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);

        StringBuilder buffer = new StringBuilder("userName=").append(userName).append("&password=").append(password);

        for (String phone : sms.getTargetPhone())
        {
            buffer.append("&phone=").append(phone);
        }

        buffer.append("&content=").append(CommonUtils.byteArrayToBase64(sms.getContent().getBytes("UTF-8")))
                .append("&base64=true");

        if (sms.getSerial() != null && sms.getSerial().length() > 0)
            buffer.append("&serial=").append(sms.getSerial());

        connection.getOutputStream().write(buffer.toString().getBytes("UTF-8"));

        connection.connect();

        InputStream in = connection.getInputStream();

        SendResult result;
        try
        {
            result = XmlBeanSerializer.load(in, SendResult.class);
        }
        catch (IOException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new SmsException(ex);
        }

        if (!StringUtils.isEmpty(result.getError()))
            throw new SmsException(result.getError());

        for (final SendResultItem item : result.getItems())
        {
            if (!StringUtils.isEmpty(item.getError()))
                throw new SmsException(result.getError());

            if (callback != null)
            {
                Jobs.run(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            callback.call(Long.toString(item.getSmsId()));
                        }
                        catch (Throwable ex)
                        {
                            if (log != null)
                                log.error(ex);
                        }
                    }
                });
            }
        }
    }

    public void sendSms(String content, String[] phone, SmsSendCallback callback) throws IOException, SmsException
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public void sendSms(String content, String phone, SmsSendCallback callback) throws IOException, SmsException
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public void close() throws IOException, SmsException
    {
        if (schedule != null)
            schedule.release();
    }
}
