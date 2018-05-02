package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.organ.User;
import com.gzzm.safecampus.campus.base.BaseStudent;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * 账单信息表
 *
 * @author yuanfang
 * @date 18-02-06 17:51
 */
@Entity(table = "SCBILL", keys = "billId")
public class Bill extends BaseStudent
{

    @Generatable(length = 6)
    private Integer billId;

    /**
     * 关联缴费项目
     */
    @ToOne("PAYITEMID")
    private PayItem payItem;

    private Integer payItemId;

    /**
     * 创建账单时间
     */
    private Timestamp createTime;

    /**
     * 缴费金额，单位/元
     */
    @ColumnDescription(type = "number(10,2)")
    private Double money;

    /**
     * 截止时间
     */
    private Date deadline;

    /**
     * 缴费时间
     */
    private Timestamp payTime;

    /**
     * 账单状态
     */
    @ColumnDescription(defaultValue = "0")
    private BillStatus billStatus;

    /**
     * 账单备注
     */
    @ColumnDescription(type = "varchar2(200)")
    private String note;

    /**
     * 账单编号
     */
    @ColumnDescription(type = "varchar2(30)")
    private String serialNo;

    /**
     * 关联操作员（用户）
     */
    @ToOne("OPERATORID")
    private User operator;

    private Integer operatorId;

    /**
     * 缴费方式
     */
    private PaymentMethod paymentMethod;

    /**
     * 关联缴费信息
     */
    @ToOne("PAYMENTID")
    private Payment payment;

    private Integer paymentId;

    /**
     * 无效账单删除标志
     */
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;

    /**
     * 待缴、已缴账单删除标志
     */
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer cancelFlag;

    public Bill()
    {
    }

    public Integer getCancelFlag()
    {
        return cancelFlag;
    }

    public void setCancelFlag(Integer cancelFlag)
    {
        this.cancelFlag = cancelFlag;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Payment getPayment()
    {
        return payment;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    public String getSerialNo()
    {
        return serialNo;
    }

    public void setSerialNo(String serialNo)
    {
        this.serialNo = serialNo;
    }

    public Integer getBillId()
    {
        return billId;
    }

    public void setBillId(Integer billId)
    {
        this.billId = billId;
    }

    public PaymentMethod getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId)
    {
        this.paymentId = paymentId;
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

    public Double getMoney()
    {
        return money;
    }

    public void setMoney(Double money)
    {
        this.money = money;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public Timestamp getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime)
    {
        this.createTime = createTime;
    }

    public Timestamp getPayTime()
    {
        return payTime;
    }

    public void setPayTime(Timestamp payTime)
    {
        this.payTime = payTime;
    }

    public BillStatus getBillStatus()
    {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus)
    {
        this.billStatus = billStatus;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public User getOperator()
    {
        return operator;
    }

    public void setOperator(User operator)
    {
        this.operator = operator;
    }

    public Integer getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId)
    {
        this.operatorId = operatorId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Objects.equals(billId, bill.billId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(billId);
    }

    @Override
    public String toString()
    {
        return payItem.getPayItemName();
    }
}
