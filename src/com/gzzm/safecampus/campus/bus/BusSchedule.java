package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.Teacher;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.Transient;

import java.util.Date;


/**
 * 校巴排班表
 */
@Entity(table = "SCBUSSCHEDULE",keys = "busScheduleId")
public class BusSchedule extends BaseBean{

    @Generatable(length = 6)
    private Integer busScheduleId;

    /**
     * 校巴ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer busId;

    @NotSerialized
    private BusInfo busInfo;

    /**
     * 跟车老师ID
     */
    private Integer teacherId;

    @NotSerialized
    private Teacher teacher;

    /**
     * 排班类型
     */
    private ScheduleType scheduleType;

    /**
     * 排班具体日期
     */
    private Date scheduleTime;

    @Transient
    private String busLicense;

    @Transient
    private String teacherNo;

    @Transient
    private String teacherName;

    @Transient
    private String monday;

    @Transient
    private String tuesday;

    @Transient
    private String wednesday;

    @Transient
    private String friday;

    @Transient
    private String saturday;

    @Transient
    private String sunday;

    @Transient
    private String thursday;

    public BusSchedule(){}

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public BusInfo getBusInfo() {
        return busInfo;
    }

    public void setBusInfo(BusInfo busInfo) {
        this.busInfo = busInfo;
    }

    public Integer getBusScheduleId() {
        return busScheduleId;
    }

    public void setBusScheduleId(Integer busScheduleId) {
        this.busScheduleId = busScheduleId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getBusLicense() {
        return busLicense;
    }

    public void setBusLicense(String busLicense) {
        this.busLicense = busLicense;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusSchedule that = (BusSchedule) o;

        return busScheduleId.equals(that.busScheduleId);

    }

    @Override
    public int hashCode() {
        return busScheduleId.hashCode();
    }
}
