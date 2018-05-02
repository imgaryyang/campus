package com.gzzm.safecampus.campus.wx.message;

/**
 * 考勤消息
 *
 * @author Neo
 * @date 2018/4/2 18:29
 */
public class AttendanceMsg extends StudentMsg
{
    /**
     * 考勤类型
     */
    private String attendanceType;

    /**
     * 考勤时间
     */
    private String attendanceTime;

    /**
     * 考勤情况
     */
    private String attendanceStatus;

    public AttendanceMsg()
    {
        super(TemplateMessageType.ATTENDANCE);
    }

    public String getAttendanceType()
    {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType)
    {
        this.attendanceType = attendanceType;
    }

    public String getAttendanceTime()
    {
        return attendanceTime;
    }

    public void setAttendanceTime(String attendanceTime)
    {
        this.attendanceTime = attendanceTime;
    }

    public String getAttendanceStatus()
    {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus)
    {
        this.attendanceStatus = attendanceStatus;
    }

    public String getFirst()
    {
        return title + "\n您的孩子" + studentName + "有新的考勤信息";
    }
}
