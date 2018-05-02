package com.gzzm.safecampus.pay.cmb;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 银行业务流水
 *
 * @author yuanfang
 * @date 18-04-13 13:38
 */
@Entity(table = "SCBANKBUSINESS", keys = "bbId")
public class BankBusiness
{
    @Generatable(length = 6)
    private Integer bbId;

    /**
     * 商户分行号
     */
    @ColumnDescription(type = "varchar(4)")
    private String branchNo;

    /**
     * 商户号
     */
    @ColumnDescription(type = "varchar(6)")
    private String merchantNo;

    /**
     * 商户的订单日期
     */
    @ColumnDescription(type = "varchar(8)")
    private String date;

    /**
     * 商户的订单号
     */
    @ColumnDescription(type = "varchar(20)")
    private String orderNo;

    /**
     * 银行的订单流水号
     */
    @ColumnDescription(type = "varchar(20)")
    private String bankSerialNo;

    /**
     * 订单参考号
     */
    @ColumnDescription(type = "varchar(32)")
    private String orderRefNo;

    /**
     * 交易币种,固定为：“10”
     */
    @ColumnDescription(type = "varchar(4)")
    private String currency;

    /**
     * 订单金额格式：xxxx.xx
     */
    @ColumnDescription(type = "varchar(20)")
    private String orderAmount;

    /**
     * 费用金额,格式：xxxx.xx
     */
    @ColumnDescription(type = "varchar(20)")
    private String fee;

    /**
     * 银行受理日期,格式：yyyyMMdd
     */
    @ColumnDescription(type = "varchar(8)")
    private String acceptDate;

    /**
     * 银行受理时间,格式：HHmmss
     */
    @ColumnDescription(type = "varchar(6)")
    private String acceptTime;

    /**
     * 结算金额,格式：xxxx.xx
     */
    @ColumnDescription(type = "varchar(20)")
    private String settleAmount;

    /**
     * 优惠金额,单位为元，精确到小数点后两位；
     * 格式为：xxxx.xx元；无优惠则返回0.00
     */
    @ColumnDescription(type = "varchar(13)")
    private String discountAmount;

    /**
     * 订单状态,0：已结账
     * 1：已撤销
     * 2：部分结账
     * 4：未结账
     * 5：无效状态
     * 6：未知状态
     */
    @ColumnDescription(type = "varchar(3)")
    private String orderStatus;

    /**
     * 银行处理日期,格式：yyyyMMdd
     */
    @ColumnDescription(type = "varchar(8)")
    private String settleDate;

    /**
     * 银行处理时间,格式：HHmmss
     */
    @ColumnDescription(type = "varchar(6)")
    private String settleTime;

    /**
     * 卡类型,02：一卡通
     * 03：信用卡
     * 08：他行储蓄卡
     * 09：他行信用卡
     */
    @ColumnDescription(type = "varchar(4)")
    private String cardType;

    public BankBusiness()
    {
    }

    public Integer getBbId()
    {
        return bbId;
    }

    public void setBbId(Integer bbId)
    {
        this.bbId = bbId;
    }

    public String getBranchNo()
    {
        return branchNo;
    }

    public void setBranchNo(String branchNo)
    {
        this.branchNo = branchNo;
    }

    public String getMerchantNo()
    {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo)
    {
        this.merchantNo = merchantNo;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getBankSerialNo()
    {
        return bankSerialNo;
    }

    public void setBankSerialNo(String bankSerialNo)
    {
        this.bankSerialNo = bankSerialNo;
    }

    public String getOrderRefNo()
    {
        return orderRefNo;
    }

    public void setOrderRefNo(String orderRefNo)
    {
        this.orderRefNo = orderRefNo;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getOrderAmount()
    {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount)
    {
        this.orderAmount = orderAmount;
    }

    public String getFee()
    {
        return fee;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public String getAcceptDate()
    {
        return acceptDate;
    }

    public void setAcceptDate(String acceptDate)
    {
        this.acceptDate = acceptDate;
    }

    public String getAcceptTime()
    {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public String getSettleAmount()
    {
        return settleAmount;
    }

    public void setSettleAmount(String settleAmount)
    {
        this.settleAmount = settleAmount;
    }

    public String getDiscountAmount()
    {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount)
    {
        this.discountAmount = discountAmount;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getSettleDate()
    {
        return settleDate;
    }

    public void setSettleDate(String settleDate)
    {
        this.settleDate = settleDate;
    }

    public String getSettleTime()
    {
        return settleTime;
    }

    public void setSettleTime(String settleTime)
    {
        this.settleTime = settleTime;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankBusiness that = (BankBusiness) o;
        return Objects.equals(bbId, that.bbId);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(bbId);
    }

    @Override
    public String toString()
    {
        return orderNo ;
    }
}
