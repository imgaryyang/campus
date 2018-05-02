package com.gzzm.ods.receipt;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 回执回复信息
 *
 * @author camel
 * @date 2016/4/16
 */
@Entity(table = "ODRECEIPTREPLY", keys = {"receiptId", "deptId"})
public class ReceiptReply
{
    private Long receiptId;

    private Integer deptId;

    @NotSerialized
    private Receipt receipt;

    @NotSerialized
    private Dept dept;

    @ColumnDescription(defaultValue = "0")
    private Boolean replied;

    private Date replyTime;

    @ColumnDescription(defaultValue = "0", nullable = false)
    private Boolean readed;

    public ReceiptReply()
    {
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Receipt getReceipt()
    {
        return receipt;
    }

    public void setReceipt(Receipt receipt)
    {
        this.receipt = receipt;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Boolean getReplied()
    {
        return replied;
    }

    public void setReplied(Boolean replied)
    {
        this.replied = replied;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public Boolean getReaded()
    {
        return readed;
    }

    public void setReaded(Boolean readed)
    {
        this.readed = readed;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ReceiptReply))
            return false;

        ReceiptReply that = (ReceiptReply) o;

        return deptId.equals(that.deptId) && receiptId.equals(that.receiptId);
    }

    @Override
    public int hashCode()
    {
        int result = receiptId.hashCode();
        result = 31 * result + deptId.hashCode();
        return result;
    }
}
