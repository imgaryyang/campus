package com.gzzm.safecampus.campus.trusteeship;

import com.gzzm.safecampus.campus.base.BaseStudent;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.Transient;

/**
 * 托管学生管理表
 */
@Entity(table = "SCTRUSTEESHIPSTUDENT", keys = "ttId")
public class TrusteeshipStudent extends BaseStudent
{

    @Generatable(length = 6)
    private Integer ttId;

    /**
     * 托管室ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer troomId;

    @NotSerialized
    private TrusteeshipRoom trusteeshipRoom;

    @Transient
    private String studentNo;

    @Transient
    private String studentName;

    @Transient
    private String roomName;

    @Transient
    private String gradeName;

    @Transient
    private String className;


    public TrusteeshipStudent()
    {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Integer getTtId()
    {
        return ttId;
    }

    public void setTtId(Integer ttId)
    {
        this.ttId = ttId;
    }

    public Integer getTroomId()
    {
        return troomId;
    }

    public void setTroomId(Integer troomId)
    {
        this.troomId = troomId;
    }

    public TrusteeshipRoom getTrusteeshipRoom()
    {
        return trusteeshipRoom;
    }

    public void setTrusteeshipRoom(TrusteeshipRoom trusteeshipRoom)
    {
        this.trusteeshipRoom = trusteeshipRoom;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrusteeshipStudent that = (TrusteeshipStudent) o;

        return ttId.equals(that.ttId);

    }

    @Override
    public int hashCode()
    {
        return ttId.hashCode();
    }


}
