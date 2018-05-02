package com.gzzm.ods.dispatch;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author LDP
 * @date 2017/7/17
 */
@Entity(table = "ODDISPATCHITEM", keys = "itemId")
public class DispatchItem
{
    /**
     * 主键
     */
    @Generatable(length = 11)
    private Long itemId;

    /**
     * 对应的发文记录
     */
    private Long recordId;

    @NotSerialized
    @ToOne("RECORDID")
    private DispatchRecord record;

    /**
     * 如果是从OA提取过来的数据，记录收文单位ID
     */
    @ColumnDescription(type = "number(7)")
    private Integer deptId;

    /**
     * 单位
     */
    @Require
    @ColumnDescription(type = "varchar2(200)")
    private String deptName;

    /**
     * 人员
     */
    @ColumnDescription(type = "varchar2(200)")
    private String userName;

    /**
     * 联系电话
     */
    @ColumnDescription(type = "varchar2(100)")
    private String phone;

    /**
     * 回复时间
     */
    private Date replayTime;

    /**
     * 备注
     */
    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    public DispatchItem()
    {
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public DispatchRecord getRecord()
    {
        return record;
    }

    public void setRecord(DispatchRecord record)
    {
        this.record = record;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Date getReplayTime()
    {
        return replayTime;
    }

    public void setReplayTime(Date replayTime)
    {
        this.replayTime = replayTime;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof DispatchItem)) return false;

        DispatchItem that = (DispatchItem) o;

        return itemId != null ? itemId.equals(that.itemId) : that.itemId == null;

    }

    @Override
    public int hashCode()
    {
        return itemId != null ? itemId.hashCode() : 0;
    }
}
