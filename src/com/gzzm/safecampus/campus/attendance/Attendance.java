package com.gzzm.safecampus.campus.attendance;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseStudent;
import com.gzzm.safecampus.campus.bus.BusInfo;
import com.gzzm.safecampus.campus.bus.BusSite;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.campus.device.Camera;
import com.gzzm.safecampus.campus.siesta.SiestaRoom;
import com.gzzm.safecampus.campus.trusteeship.TrusteeshipRoom;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 考勤表
 *
 * @author Huangmincong
 * @date 2018/03/15
 */
@Entity(table = "SCATTENDANCE", keys = "attId")
public class Attendance extends BaseStudent {

    @Generatable(length = 12)
    private Integer attId;

    /**
     * 学校id
     */
    private Integer schoolId;

    /**
     * 关系id
     */
    private Integer relationId;

    @NotSerialized
    private School school;

    /**
     * 考勤批次id（总记录id）
     */
    private Integer aoId;

    @NotSerialized
    private AttendanceRecord attendanceRecord;

    /**
     * 图片id
     */
    @ColumnDescription(type = "varchar2(32)")
    private String imgId;

    /**
     * 设备id
     */
    private Integer cameraId;

    @NotSerialized
    private Camera camera;

    /**
     * 考勤时间
     */
    private Date attendanceTime;

    /**
     * 考勤情况
     */
    @ColumnDescription(type = "NUMBER(2)")
    private AttendanceStatus attendanceStatus;

    /**
     * 发送状态
     */
    private SendState sendState;

    /**
     * 考勤老师ID
     */
    private Integer teacherId;

    @NotSerialized
    private Teacher teacher;

    /**
     * 途径地点ID
     */
    private Integer siteId;

    @NotSerialized
    private BusSite busSite;

    /**
     * 备注
     */
    @ColumnDescription(type = "VARCHAR2(100)")
    private String remark;

    /**
     * 考勤类型
     */
    private AttendanceType type;

    @Transient
    private String phone;

    @ToOne("relationId")
    @NotSerialized
    private Classes classes;

    @ToOne("relationId")
    @NotSerialized
    private BusInfo busInfo;

    @ToOne("relationId")
    @NotSerialized
    private SiestaRoom siestaRoom;

    @ToOne("relationId")
    @NotSerialized
    private TrusteeshipRoom trusteeshipRoom;

    public Attendance() {
    }

    @Override
    public Classes getClasses() {
        return classes;
    }

    @Override
    public void setClasses(Classes classes) {
        this.classes = classes;
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

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAttId() {
        return attId;
    }

    public void setAttId(Integer attId) {
        this.attId = attId;
    }

    public Integer getAoId() {
        return aoId;
    }

    public void setAoId(Integer aoId) {
        this.aoId = aoId;
    }

    public AttendanceRecord getAttendanceRecord() {
        return attendanceRecord;
    }

    public void setAttendanceRecord(AttendanceRecord attendanceRecord) {
        this.attendanceRecord = attendanceRecord;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }


    public Date getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(Date attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public SendState getSendState() {
        return sendState;
    }

    public void setSendState(SendState sendState) {
        this.sendState = sendState;
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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public BusSite getBusSite() {
        return busSite;
    }

    public void setBusSite(BusSite busSite) {
        this.busSite = busSite;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public AttendanceType getType() {
        return type;
    }

    public void setType(AttendanceType type) {
        this.type = type;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attendance that = (Attendance) o;

        return attId.equals(that.attId);

    }

    @Override
    public int hashCode() {
        return attId.hashCode();
    }
}
