package com.gzzm.sms;

import net.cyan.commons.annotation.*;

import java.util.*;

/**
 * @author camel
 * @date 2010-11-30
 */
public class ReceiptList
{
    /**
     * 错误信息，如果没有错误为空字符串
     */
    private String error = "";

    @ElementWrapper("receiptList")
    @ElementName("element")
    private List<Receipt> receiptList;

    public ReceiptList()
    {
    }

    public ReceiptList(List<SmsMt> mtList)
    {
        receiptList = new ArrayList<Receipt>(mtList.size());

        for (SmsMt mt : mtList)
        {
            Receipt receipt = new Receipt();
            receipt.setSmsId(mt.getSmsId());
            receipt.setPhone(mt.getPhone());
            receipt.setReceiveTime(mt.getReceiveTime());
            receipt.setSerial(mt.getSerial());
            receipt.setError(mt.getError());

            receiptList.add(receipt);
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

    public List<Receipt> getReceiptList()
    {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList)
    {
        this.receiptList = receiptList;
    }
}
