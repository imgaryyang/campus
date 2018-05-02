package com.gzzm.ods.dispatch;

import com.gzzm.ods.exchange.Send;
import com.gzzm.ods.receipt.Receipt;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 公文发送记录（支持手工录入）
 *
 * @author LDP
 * @date 2017/7/17
 */
@Entity(table = "ODDISPATCHRECORD", keys = "recordId")
public class DispatchRecord
{
    /**
     * 主键
     */
    @Generatable(length = 11)
    private Long recordId;

    /**
     * 发文编号
     */
    @ColumnDescription(type = "varchar(50)")
    private String sendNumber;

    /**
     * 公文的标题
     */
    @Require
    @ColumnDescription(type = "varchar(500)")
    private String title;

    /**
     * 发起单位
     */
    @Require
    @ColumnDescription(type = "varchar2(200)")
    private String deptName;

    /**
     * 发起时间
     */
    private Date sendTime;

    /**
     * 限时办结时间
     */
    private Date limitDate;

    /**
     * 提前预警天数
     *
     * @deprecated 取消预警天数，直接通过指定预警时间（warningDate）完成预警
     */
    @Deprecated
    @MinVal("1")
    @ColumnDescription(type = "number(3)")
    private Integer warningDays;

    /**
     * 预警时间
     * 挂黄牌的时间
     */
    private Date warningDate;

    /**
     * 创建人
     */
    private Integer creatorId;

    @ColumnDescription(type = "varchar2(500)")
    private String creatorName;

    @NotSerialized
    @ToOne("CREATORID")
    private User creator;

    @NotSerialized
    @OneToMany("RECORDID")
    private List<DispatchItem> items;

    /**
     * 是否办结标记
     */
    @ColumnDescription(type = "number(2)")
    private Boolean finishTag;

    /**
     * 录入单位ID
     * 如果是OA对接，则存储发文单位ID
     */
    private Integer createDeptId;

    /**
     * 发文ID
     * 为空时，表示手工录入的数据
     */
    @ColumnDescription(type = "number(11)")
    private Long sendId;

    @ToOne("SENDID")
    private Send send;

    /**
     * 回执ID（限时回复发文所应的ID）
     * 此字段为冗余字段，由于经常需要使用，为了提高效率，添加此字段
     */
    private Long receiptId;

    @NotSerialized
    @ToOne("RECEIPTID")
    private Receipt receipt;

    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Boolean redWarnTag;

    /**
     * 黄牌提醒
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Boolean warningNotified;

    /**
     * 黄牌提醒
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Boolean redNotified;

    public DispatchRecord()
    {
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getLimitDate()
    {
        return limitDate;
    }

    public void setLimitDate(java.sql.Date limitDate)
    {
        this.limitDate = limitDate;
    }

    public void setLimitDate(Date limitDate)
    {
        this.limitDate = limitDate;
    }

    @Deprecated
    public Integer getWarningDays()
    {
        return warningDays;
    }

    @Deprecated
    public void setWarningDays(Integer warningDays)
    {
        this.warningDays = warningDays;
    }

    public Date getWarningDate()
    {
        return warningDate;
    }

    public void setWarningDate(Date warningDate)
    {
        this.warningDate = warningDate;
    }

    public Integer getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId)
    {
        this.creatorId = creatorId;
    }

    public User getCreator()
    {
        return creator;
    }

    public void setCreator(User creator)
    {
        this.creator = creator;
    }

    public List<DispatchItem> getItems()
    {
        return items;
    }

    public void setItems(List<DispatchItem> items)
    {
        this.items = items;
    }

    public Boolean getFinishTag()
    {
        return finishTag;
    }

    public void setFinishTag(Boolean finishTag)
    {
        this.finishTag = finishTag;
    }

    public String getCreatorName()
    {
        return creatorName;
    }

    public void setCreatorName(String creatorName)
    {
        this.creatorName = creatorName;
    }

    public Long getSendId()
    {
        return sendId;
    }

    public void setSendId(Long sendId)
    {
        this.sendId = sendId;
    }

    public Integer getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public Boolean getRedWarnTag()
    {
        return redWarnTag;
    }

    public void setRedWarnTag(Boolean redWarnTag)
    {
        this.redWarnTag = redWarnTag;
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Send getSend()
    {
        return send;
    }

    public void setSend(Send send)
    {
        this.send = send;
    }

    public Boolean getWarningNotified()
    {
        return warningNotified;
    }

    public void setWarningNotified(Boolean warningNotified)
    {
        this.warningNotified = warningNotified;
    }

    public Boolean getRedNotified()
    {
        return redNotified;
    }

    public void setRedNotified(Boolean redNotified)
    {
        this.redNotified = redNotified;
    }

    public Receipt getReceipt()
    {
        return receipt;
    }

    public void setReceipt(Receipt receipt)
    {
        this.receipt = receipt;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof DispatchRecord)) return false;

        DispatchRecord that = (DispatchRecord) o;

        return recordId != null ? recordId.equals(that.recordId) : that.recordId == null;

    }

    @Override
    public int hashCode()
    {
        return recordId != null ? recordId.hashCode() : 0;
    }
}
