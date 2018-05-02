package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 微信用户和学生关联实体
 *
 * @author Neo
 * @date 2018/3/28 9:42
 */
@Entity(table = "SCWXSTUDENT", keys = "wsId")
public class WxStudent
{
    @Generatable(length = 9)
    private Integer wsId;

    /**
     * 微信用户Id
     */
    private Integer wxUserId;

    @NotSerialized
    @ToOne
    private WxUser wxUser;

    /**
     * 学生Id
     */
    private Integer studentId;

    @NotSerialized
    @ToOne
    private Student student;

    /**
     * 绑定时间
     */
    private Date bindTime;

    public WxStudent()
    {
    }

    public WxStudent(Integer wxUserId, Integer studentId, Date bindTime)
    {
        this.wxUserId = wxUserId;
        this.studentId = studentId;
        this.bindTime = bindTime;
    }

    public Integer getWsId()
    {
        return wsId;
    }

    public void setWsId(Integer wsId)
    {
        this.wsId = wsId;
    }

    public Integer getWxUserId()
    {
        return wxUserId;
    }

    public void setWxUserId(Integer wxUserId)
    {
        this.wxUserId = wxUserId;
    }

    public WxUser getWxUser()
    {
        return wxUser;
    }

    public void setWxUser(WxUser wxUser)
    {
        this.wxUser = wxUser;
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public Date getBindTime()
    {
        return bindTime;
    }

    public void setBindTime(Date bindTime)
    {
        this.bindTime = bindTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof WxStudent)) return false;

        WxStudent wxStudent = (WxStudent) o;

        return wsId.equals(wxStudent.wsId);

    }

    @Override
    public int hashCode()
    {
        return wsId.hashCode();
    }
}
