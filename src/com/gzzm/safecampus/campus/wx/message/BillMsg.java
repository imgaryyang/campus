package com.gzzm.safecampus.campus.wx.message;

import com.gzzm.safecampus.campus.pay.BillStatus;

/**
 * 账单通知消息
 *
 * @author Neo
 * @date 2018/4/3 9:20
 */
public class BillMsg extends StudentMsg
{
    /**
     * 账单类型/缴费类型
     */
    private String paymentType;

    /**
     * 账单名称/缴费项名称
     */
    private String payItemName;

    /**
     * 缴费金额
     */
    private String money;

    /**
     * 待缴账单表示截止时间，已缴账单表示缴费时间
     */
    private String time;

    /**
     * 账单状态
     */
    private BillStatus billStatus;

    public BillMsg(TemplateMessageType messageType)
    {
        super(messageType);
    }

    /**
     * 账单的title
     *
     * @return title
     */
    public String getFirst()
    {
        if (billStatus == BillStatus.Wait)
            return title + "\n您的孩子" + studentName + "有一份新账单待缴纳。若您已缴清本账单，请飘过~~";
        if (billStatus == BillStatus.Finnish)
            return title + "\n您的孩子" + studentName + "的" + paymentType + "账单已缴纳";
        return null;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getPayItemName()
    {
        return payItemName;
    }

    public void setPayItemName(String payItemName)
    {
        this.payItemName = payItemName;
    }

    public String getMoney()
    {
        return money;
    }

    public void setMoney(String money)
    {
        this.money = money;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public BillStatus getBillStatus()
    {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus)
    {
        this.billStatus = billStatus;
    }
}
