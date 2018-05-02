package com.gzzm.safecampus.campus.pay;

import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.thunwind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * 支付信息表
 * 用户发起支付，后台生成并提交银行字段与银行交易返回字段
 *
 * @author yuanfang
 * @date 18-03-08 11:34
 */

@Entity(table = "SCPAYMENT", keys = "paymentId")
public class Payment
{

    @Generatable(length = 9)
    private Integer paymentId;

    /**
     * 关联账单
     */
    @ToOne("BILLID")
    private Bill bill;

    private Integer billId;

    /**
     * 关联缴费项目
     */
    @ToOne("PAYITEMID")
    private PayItem payItem;

    private Integer payItemId;

    /**
     * 关联学生
     */
    @ToOne("STUDENTID")
    private Student student;

    private Integer studentId;

    /**
     * 分行号
     */
    @ColumnDescription(type = "varchar(4)")
    private String branchNo;

    /**
     * 商户号
     */
    @ColumnDescription(type = "varchar(6)")
    private String merchantNo;

    /**
     * 银行通知流水号
     */
    @ColumnDescription(type = "varchar(30)")
    private String noticeSerialNo;

    /**
     * 发起时间
     */
    @ColumnDescription(type = "varchar(16)")
    private String dateTime;

    /**
     * 发起日期
     */
    @ColumnDescription(type = "varchar(8)")
    private String date;

    /**
     * 订单号
     */
    @ColumnDescription(type = "varchar(32)")
    private String orderNo;

    /**
     * 银行受理日期
     */
    @ColumnDescription(type = "varchar(8)")
    private String bankDate;

    /**
     * 订单金额
     */
    @ColumnDescription(type = "number(10,2)")
    private Double amount;

    /**
     * 订单银行折扣
     */
    @ColumnDescription(type = "number(8,2)", defaultValue = "0")
    private Double discount;

    /**
     * 实际支付金额
     */
    @ColumnDescription(type = "number(10,2)")
    private Double money;

    /**
     * 用户协议号
     */
    @ColumnDescription(type = "varchar(32)")
    private String agrNo;

    /**
     * 银行订单流水号
     */
    @ColumnDescription(type = "varchar(20)")
    private String bankSerialNo;

    /**
     *优惠标志，Y:有优惠 N：无
     */
    @ColumnDescription(type = "varchar(1)")
    private String discountFlag;

    /**
     * 优惠金额，单位为元
     */
    @ColumnDescription(type = "varchar(13)")
    private String discountAmount;

    /**
     * 支付卡类型，03:信用卡,02:借记卡
     */
    @ColumnDescription(type = "varchar(4)")
    private String cardType;

    /**
     * 支付完成时间
     */
    private Timestamp payTime;

    /**
     * 支付发起时间
     */
    private Timestamp createTime;

    /**
     * 支付方式
     */
    private PaymentMethod paymentMethod;

    /**
     *备注
     */
    @ColumnDescription(type = "varchar2(100)")
    private String note;

    /**
     * 支付状态
     */
    private BillStatus billStatus;

    public Payment()
    {
    }

    public String getNoticeSerialNo()
    {
        return noticeSerialNo;
    }

    public void setNoticeSerialNo(String noticeSerialNo)
    {
        this.noticeSerialNo = noticeSerialNo;
    }

    public String getBankDate()
    {
        return bankDate;
    }

    public void setBankDate(String bankDate)
    {
        this.bankDate = bankDate;
    }

    public String getDiscountFlag()
    {
        return discountFlag;
    }

    public void setDiscountFlag(String discountFlag)
    {
        this.discountFlag = discountFlag;
    }

    public String getDiscountAmount()
    {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount)
    {
        this.discountAmount = discountAmount;
    }

    public Timestamp getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime)
    {
        this.createTime = createTime;
    }

    public Integer getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId)
    {
        this.paymentId = paymentId;
    }

    public Bill getBill()
    {
        return bill;
    }

    public void setBill(Bill bill)
    {
        this.bill = bill;
    }

    public Integer getBillId()
    {
        return billId;
    }

    public void setBillId(Integer billId)
    {
        this.billId = billId;
    }

    public PayItem getPayItem()
    {
        return payItem;
    }

    public void setPayItem(PayItem payItem)
    {
        this.payItem = payItem;
    }

    public Integer getPayItemId()
    {
        return payItemId;
    }

    public void setPayItemId(Integer payItemId)
    {
        this.payItemId = payItemId;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
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

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
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

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public Double getDiscount()
    {
        return discount;
    }

    public void setDiscount(Double discount)
    {
        this.discount = discount;
    }

    public Double getMoney()
    {
        return money;
    }

    public void setMoney(Double money)
    {
        this.money = money;
    }

    public String getBankSerialNo()
    {
        return bankSerialNo;
    }

    public void setBankSerialNo(String bankSerialNo)
    {
        this.bankSerialNo = bankSerialNo;
    }

    public Timestamp getPayTime()
    {
        return payTime;
    }

    public void setPayTime(Timestamp payTime)
    {
        this.payTime = payTime;
    }

    public PaymentMethod getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public BillStatus getBillStatus()
    {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus)
    {
        this.billStatus = billStatus;
    }

    public String getAgrNo()
    {
        return agrNo;
    }

    public void setAgrNo(String agrNo)
    {
        this.agrNo = agrNo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString()
    {
        return orderNo;
    }
}
