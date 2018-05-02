package com.gzzm.safecampus.campus.bus;

/**
 * @author Neo
 * @date 2018/3/24 13:48
 */
public class BusTeacher
{
    private Integer busId;

    private String busName;

    private Integer teacherId;

    private String teacherName;

    public BusTeacher()
    {
    }

    public Integer getBusId()
    {
        return busId;
    }

    public void setBusId(Integer busId)
    {
        this.busId = busId;
    }

    public String getBusName()
    {
        return busName;
    }

    public void setBusName(String busName)
    {
        this.busName = busName;
    }

    public Integer getTeacherId()
    {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId)
    {
        this.teacherId = teacherId;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }
}
