package com.gzzm.safecampus.campus.pay;

import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.FieldValidator;
import net.cyan.commons.validate.annotation.Warning;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;
import java.util.Objects;

/**
 * 缴费项目表
 *
 * @author yuanfang
 * @date 18-02-06 18:02
 */
@Entity(table = "SCPAYITEM", keys = "payItemId")
public class PayItem extends BaseBean
{
    @Inject
    private static Provider<PayItemDao> daoProvider;

    @Generatable(length = 6)
    private Integer payItemId;

    /**
     * 项目编号
     */
    @ColumnDescription(type = "varchar2(20)")
    private String serialNo;

    /**
     * 所属服务
     */
    @ToOne("SERVICEID")
    private PayService payService;

    private Integer serviceId;

    /**
     * 缴费项目名称
     */
    @ColumnDescription(type = "varchar2(100)")
    private String payItemName;

    /**
     * 缴费类型
     */
    private PaymentType paymentType;

    /**
     * 缴费金额
     */
    @ColumnDescription(type = "number(10,2)")
    private Double money;

    /**
     * 缴费周期
     */
    private PaymentCycle paymentCycle;

    /**
     * 截止时间
     */
    private Date deadline;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 创建时间
     */
    private Date creatTime;

    /**
     * 缴费项目状态
     */
    @ColumnDescription(defaultValue = "1")
    private PayItemStatus payItemStatus;

    /**
     * 协议
     */
    @Lazy
    @ColumnDescription(type = "varchar2(1000)")
    private String pact;

    /**
     * 说明
     */
    @ColumnDescription(type = "varchar2(250)")
    private String desc;

    /**
     * 删除标志
     */
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;


    public PayItem()
    {
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Date getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(Date beginTime)
    {

        this.beginTime = beginTime;
    }

    public PayService getPayService()
    {
        return payService;
    }

    public void setPayService(PayService payService)
    {
        this.payService = payService;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }

    public Date getCreatTime()
    {
        return creatTime;
    }

    public void setCreatTime(Date creatTime)
    {
        this.creatTime = creatTime;
    }

    public PayItemStatus getPayItemStatus()
    {
        return payItemStatus;
    }

    public void setPayItemStatus(PayItemStatus payItemStatus)
    {
        this.payItemStatus = payItemStatus;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public PaymentType getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType)
    {
        this.paymentType = paymentType;
    }

    public Double getMoney()
    {
        return money;
    }

    public void setMoney(Double money)
    {
        this.money = money;
    }

    public PaymentCycle getPaymentCycle()
    {
        return paymentCycle;
    }

    public void setPaymentCycle(PaymentCycle paymentCycle)
    {
        this.paymentCycle = paymentCycle;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public Date getDate()
    {
        return creatTime;
    }

    public void setDate(Date creatTime)
    {
        this.creatTime = creatTime;
    }

    public String getPact()
    {
        return pact;
    }

    public void setPact(String pact)
    {
        this.pact = pact;
    }

    public Integer getPayItemId()
    {
        return payItemId;
    }

    public void setPayItemId(Integer payItemId)
    {
        this.payItemId = payItemId;
    }

    public String getPayItemName()
    {
        return payItemName;
    }

    public void setPayItemName(String payItemName)
    {
        this.payItemName = payItemName;
    }

    public String getSerialNo()
    {
        return serialNo;
    }

    public void setSerialNo(String serialNo)
    {
        this.serialNo = serialNo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayItem payItem = (PayItem) o;
        return Objects.equals(payItemId, payItem.payItemId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(payItemId);
    }

    @Override
    public String toString()
    {
        return payItemName;
    }

    /**
     * 缴费项目编号唯一
     * @return 缴费项目编号
     */
    @FieldValidator("serialNo")
    @Warning("payItem.serialNo_exists")
    public Integer checkSerialNo() throws Exception
    {
        return !StringUtils.isEmpty(getSerialNo()) ?
                daoProvider.get().checkSerialNo(getSerialNo(), getDeptId(), getPayItemId()) : null;
    }

}
