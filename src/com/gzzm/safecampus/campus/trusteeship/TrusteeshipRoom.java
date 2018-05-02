package com.gzzm.safecampus.campus.trusteeship;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.base.TimeUtils;
import com.gzzm.safecampus.campus.classes.Teacher;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 托管室
 *
 * @author czy
 */
@Entity(table = "SCTRUSTEESHIPROOM", keys = "troomId")
public class TrusteeshipRoom extends BaseBean
{
    /**
     * 托管室ID
     */
    @Generatable(length = 6)
    private Integer troomId;

    /**
     * 托管室名称
     */
    @Require
    @ColumnDescription(type = "varchar2(30)")
    private String name;

    /**
     * 生活老师ID
     */
    @Require
    @ColumnDescription(type = "number(6)")
    private Integer teacherId;

    @NotSerialized
    @ToOne
    private Teacher teacher;

    /**
     * 托管开始时间时分
     */
    @ColumnDescription(type = "number(2)")
    private Integer trusteeStartHour;

    /**
     * 托管开始时间分钟
     */
    @ColumnDescription(type = "number(2)")
    private Integer trusteeStartMinute;

    /**
     * 托管结束时间时分
     */
    @ColumnDescription(type = "number(2)")
    private Integer trusteeEndHour;

    /**
     * 托管结束时间分钟
     */
    @ColumnDescription(type = "number(2)")
    private Integer trusteeEndMinute;

    /**
     * 备注
     */
    @ColumnDescription(type = "varchar2(100)")
    private String remark;

    /**
     * 删除标识
     */
    @ColumnDescription(type = "NUMBER(1)")
    private Integer deleteTag;

    public TrusteeshipRoom()
    {
    }

    public TrusteeshipRoom(Integer troomId, String name)
    {
        this.troomId = troomId;
        this.name = name;
    }

    public Integer getTrusteeEndHour()
    {
        return trusteeEndHour;
    }

    public void setTrusteeEndHour(Integer trusteeEndHour)
    {
        this.trusteeEndHour = trusteeEndHour;
    }

    public Integer getTrusteeEndMinute()
    {
        return trusteeEndMinute;
    }

    public void setTrusteeEndMinute(Integer trusteeEndMinute)
    {
        this.trusteeEndMinute = trusteeEndMinute;
    }

    public Integer getTrusteeStartHour()
    {
        return trusteeStartHour;
    }

    public void setTrusteeStartHour(Integer trusteeStartHour)
    {
        this.trusteeStartHour = trusteeStartHour;
    }

    public Integer getTrusteeStartMinute()
    {
        return trusteeStartMinute;
    }

    public void setTrusteeStartMinute(Integer trusteeStartMinute)
    {
        this.trusteeStartMinute = trusteeStartMinute;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Teacher getTeacher()
    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)
    {
        this.teacher = teacher;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getTeacherId()
    {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId)
    {
        this.teacherId = teacherId;
    }

    public Integer getTroomId()
    {
        return troomId;
    }

    public void setTroomId(Integer troomId)
    {
        this.troomId = troomId;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public String getTrusteeTime()
    {
        return TimeUtils.hourMinuteFormat(trusteeStartHour, trusteeStartMinute) + " - " + TimeUtils.hourMinuteFormat(trusteeEndHour, trusteeEndMinute);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrusteeshipRoom that = (TrusteeshipRoom) o;

        return troomId.equals(that.troomId);

    }

    @Override
    public int hashCode()
    {
        return troomId.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
