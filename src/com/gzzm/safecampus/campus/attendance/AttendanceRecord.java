package com.gzzm.safecampus.campus.attendance;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.bus.BusInfo;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.campus.siesta.SiestaRoom;
import com.gzzm.safecampus.campus.trusteeship.TrusteeshipRoom;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.Lazy;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 考勤总表
 *
 * @author hmc
 * @date 2018/03/29
 */
@Entity(table = "SCATTENDANCERECORD",keys = "aoId")
public class AttendanceRecord extends BaseBean
{

    @Generatable(length = 12)
    private Integer aoId;

    /**
     * 关联ID
     */
    private Integer relationId;

    /**
     * 考勤老师ID
     */
    private Integer teacherId;

    @NotSerialized
    private Teacher teacher;

    /**
     * 考勤日期
     */
    private Date date;

    /**
     * 考勤类型
     */
    private AttendanceType type;

    /**
     * 考勤批次
     */
    private String attendanceBatch;

/*    @ToOne("RELATIONID")
    @NotSerialized
    @Override
    public Classes getClasses()
    {
        return super.getClasses();
    }*/

    @ToOne("RELATIONID")
    @Lazy
    @NotSerialized
    private BusInfo busInfo;

    @ToOne("RELATIONID")
    @Lazy
    @NotSerialized
    private SiestaRoom siestaRoom;

    @ToOne("RELATIONID")
    @Lazy
    @NotSerialized
    private TrusteeshipRoom trusteeshipRoom;

    public AttendanceRecord() {
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Integer getAoId() {
        return aoId;
    }

    public void setAoId(Integer aoId) {
        this.aoId = aoId;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AttendanceType getType() {
        return type;
    }

    public void setType(AttendanceType type) {
        this.type = type;
    }

    public String getAttendanceBatch() {
        return attendanceBatch;
    }

    public void setAttendanceBatch(String attendanceBatch) {
        this.attendanceBatch = attendanceBatch;
    }

    public BusInfo getBusInfo() {
        return busInfo;
    }

    public void setBusInfo(BusInfo busInfo) {
        this.busInfo = busInfo;
    }

    public SiestaRoom getSiestaRoom() {
        return siestaRoom;
    }

    public void setSiestaRoom(SiestaRoom siestaRoom) {
        this.siestaRoom = siestaRoom;
    }

    public TrusteeshipRoom getTrusteeshipRoom() {
        return trusteeshipRoom;
    }

    public void setTrusteeshipRoom(TrusteeshipRoom trusteeshipRoom) {
        this.trusteeshipRoom = trusteeshipRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceRecord that = (AttendanceRecord) o;

        if (!aoId.equals(that.aoId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return aoId.hashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "aoId=" + aoId +
                ", relationId=" + relationId +
                ", date=" + date +
                ", type=" + type +
                ", attendanceBatch='" + attendanceBatch + '\'' +
                '}';
    }
}
