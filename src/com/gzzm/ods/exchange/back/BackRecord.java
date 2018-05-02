package com.gzzm.ods.exchange.back;

import com.gzzm.ods.exchange.Back;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 退文登记表
 *
 * @author ldp
 * @date 2018/1/9
 */
@Entity(table = "ODBACKRECORD", keys = "recordId")
public class BackRecord
{

    @Generatable(length = 10)
    private Long recordId;

    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 退文表
     */
    @Index
    private Long backId;

    @NotSerialized
    private Back back;

    /**
     * 退文编号
     */
    @ColumnDescription(type = "varchar(100)")
    private String backNo;

    /**
     * 公文标题
     */
    @Require
    @ColumnDescription(type = "varchar(500)")
    private String title;

    /**
     * 发文编号
     */
    @ColumnDescription(type = "varchar(250)")
    private String sendNumber;

    /**
     * 退回时间
     */
    @Require
    private Date backTime;

    /**
     * 退回公文的用户的ID
     */
    private Integer backUserId;

    @NotSerialized
    @ToOne("BACKUSERID")
    private User backUser;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String backUserName;

    /**
     * 来文单位
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String sourceDept;

    /**
     * 退回的原因
     */
    @Require
    @ColumnDescription(type = "varchar(4000)")
    private String reason;

    /**
     * 错情摘要
     */
    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    public BackRecord()
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

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Long getBackId()
    {
        return backId;
    }

    public void setBackId(Long backId)
    {
        this.backId = backId;
    }

    public Back getBack()
    {
        return back;
    }

    public void setBack(Back back)
    {
        this.back = back;
    }

    public String getBackNo()
    {
        return backNo;
    }

    public void setBackNo(String backNo)
    {
        this.backNo = backNo;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public Date getBackTime()
    {
        return backTime;
    }

    public void setBackTime(Date backTime)
    {
        this.backTime = backTime;
    }

    public Integer getBackUserId()
    {
        return backUserId;
    }

    public void setBackUserId(Integer backUserId)
    {
        this.backUserId = backUserId;
    }

    public User getBackUser()
    {
        return backUser;
    }

    public void setBackUser(User backUser)
    {
        this.backUser = backUser;
    }

    public String getBackUserName()
    {
        return backUserName;
    }

    public void setBackUserName(String backUserName)
    {
        this.backUserName = backUserName;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
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
        if (this == o)
            return true;

        if (!(o instanceof BackRecord))
            return false;

        BackRecord that = (BackRecord) o;

        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
