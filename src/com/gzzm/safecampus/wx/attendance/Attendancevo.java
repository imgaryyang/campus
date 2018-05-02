package com.gzzm.safecampus.wx.attendance;

import com.gzzm.safecampus.campus.classes.Student;

import java.sql.Date;
import java.util.List;

/**
 * describe:
 *
 * @author xxx
 * @date 2018/03/23
 */
public class Attendancevo {

    private Integer aoId;

    /**
     * 迟到人数
     */
    private Integer lateCount;

    /**
     * 请假人数
     */
    private Integer leaveCount;

    /**
     * 正常到达人数
     */
    private Integer arrvedCount;

    /**
     * 正常到达人数
     */
    private Integer unsendedCount;

    /**
     * 迟到学生
     */
    private List<Student> lateStudents;

    /**
     * 请假学生
     */
    private List<Student> leaveStudents;

    /**
     * 未发送通知学生
     */
    private List<Student> unsendedStudents;

    /**
     * 班级ID
     */
    private Integer classesId;

    /**
     * 路线id
     */
    private Integer routeId;

    /**
     * 日期
     */
    private Date date;

    /**
     * 上午上车时间
     */
    private String morningGetOnTime;

    /**
     * 上午下车时间
     */
    private String morningGetOffTime;

    /**
     * 下午上车时间
     */
    private String afternoonGetOnTime;

    /**
     * 下午下车时间
     */
    private String afternoonGetOffTime;

    /**
     * 到达时间
     */
    private String arrivedTime;

    /**
     * 午休时间
     */
    private String siestaTime;

    /**
     * 托管时间
     */
    private String tursteeshipTime;

    private String attName;

    public Attendancevo() {
    }

    public String getAttName() {
        return attName;
    }

    public void setAttName(String attName) {
        this.attName = attName;
    }

    public Integer getAoId() {
        return aoId;
    }

    public void setAoId(Integer aoId) {
        this.aoId = aoId;
    }

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
    }

    public Integer getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(Integer leaveCount) {
        this.leaveCount = leaveCount;
    }

    public Integer getArrvedCount() {
        return arrvedCount;
    }

    public void setArrvedCount(Integer arrvedCount) {
        this.arrvedCount = arrvedCount;
    }

    public List<Student> getLateStudents() {
        return lateStudents;
    }

    public void setLateStudents(List<Student> lateStudents) {
        this.lateStudents = lateStudents;
    }

    public List<Student> getLeaveStudents() {
        return leaveStudents;
    }

    public void setLeaveStudents(List<Student> leaveStudents) {
        this.leaveStudents = leaveStudents;
    }

    public List<Student> getUnsendedStudents() {
        return unsendedStudents;
    }

    public void setUnsendedStudents(List<Student> unsendedStudents) {
        this.unsendedStudents = unsendedStudents;
    }

    public Integer getClassesId() {
        return classesId;
    }

    public void setClassesId(Integer classesId) {
        this.classesId = classesId;
    }

    public Integer getUnsendedCount() {
        return unsendedCount;
    }

    public void setUnsendedCount(Integer unsendedCount) {
        this.unsendedCount = unsendedCount;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getMorningGetOnTime() {
        return morningGetOnTime;
    }

    public void setMorningGetOnTime(String morningGetOnTime) {
        this.morningGetOnTime = morningGetOnTime;
    }

    public String getMorningGetOffTime() {
        return morningGetOffTime;
    }

    public void setMorningGetOffTime(String morningGetOffTime) {
        this.morningGetOffTime = morningGetOffTime;
    }

    public String getAfternoonGetOnTime() {
        return afternoonGetOnTime;
    }

    public void setAfternoonGetOnTime(String afternoonGetOnTime) {
        this.afternoonGetOnTime = afternoonGetOnTime;
    }

    public String getAfternoonGetOffTime() {
        return afternoonGetOffTime;
    }

    public void setAfternoonGetOffTime(String afternoonGetOffTime) {
        this.afternoonGetOffTime = afternoonGetOffTime;
    }

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getSiestaTime() {
        return siestaTime;
    }

    public void setSiestaTime(String siestaTime) {
        this.siestaTime = siestaTime;
    }

    public String getTursteeshipTime() {
        return tursteeshipTime;
    }

    public void setTursteeshipTime(String tursteeshipTime) {
        this.tursteeshipTime = tursteeshipTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Attendancevo{" +
                "lateCount=" + lateCount +
                ", leaveCount=" + leaveCount +
                ", arrvedCount=" + arrvedCount +
                ", lateStudents=" + lateStudents +
                ", leaveStudents=" + leaveStudents +
                ", unsendedStudents=" + unsendedStudents +
                '}';
    }
}
