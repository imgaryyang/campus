package com.gzzm.safecampus.device.attendance.entity;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author liyabin
 * @date 2018/3/29
 */
@Entity(table = "SCATTENDANCELOG",keys = "logId")
public class AttendanceLog
{
    @Generatable(length = 20)
    private Long logId;

    /**
     * 交易号
     */
    @Index
    private Integer logNo;
    /**
     * 卡序列号/卡号。有type 决定
     */
    @ColumnDescription(type = "varchar(20)")
    private String cardNo;
    /**
     *0-卡物理序列号1-逻辑卡号
     */
    @ColumnDescription(type = "Number(2)")
    private Integer type;
    /**
     * 0-正常卡 1-非法卡 2-黑名单卡
     */
    @ColumnDescription(type = "Number(2)")
    private Integer state;
    /**
     * 考勤时间
     */
    private Date attendanceTime;

    private String deviceSn;
    public AttendanceLog()
    {
    }

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getState()
    {
        return state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    public Date getAttendanceTime()
    {
        return attendanceTime;
    }

    public void setAttendanceTime(Date attendanceTime)
    {
        this.attendanceTime = attendanceTime;
    }

    public Integer getLogNo()
    {
        return logNo;
    }

    public void setLogNo(Integer logNo)
    {
        this.logNo = logNo;
    }

    public String getDeviceSn()
    {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }
}
