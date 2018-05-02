package com.gzzm.sms;

import net.cyan.commons.annotation.*;

import java.util.*;

/**
 * 短信列表对象，用于短信接收接口
 *
 * @author camel
 * @date 2010-11-29
 */
public class SmsList
{
    /**
     * 错误信息，如果没有错误为null
     */
    private String error = "";

    /**
     * 短信列表
     */
    @ElementWrapper("smsList")
    @ElementName("element")
    private List<SmsItem> smsList;

    public SmsList()
    {
    }

    public SmsList(List<SmsMo> moList)
    {
        smsList = new ArrayList<SmsItem>(moList.size());

        for (SmsMo mo : moList)
        {
            SmsItem item = new SmsItem();

            item.setPhone(mo.getPhone());
            item.setSerial(mo.getSerial());
            if (mo.getContent() != null)
                item.setContent(new String(mo.getContent()));
            item.setReceiveTime(mo.getReceiveTime());
            item.setSendTime(mo.getSendTime());

            smsList.add(item);
        }
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public List<SmsItem> getSmsList()
    {
        return smsList;
    }

    public void setSmsList(List<SmsItem> smsList)
    {
        this.smsList = smsList;
    }
}
