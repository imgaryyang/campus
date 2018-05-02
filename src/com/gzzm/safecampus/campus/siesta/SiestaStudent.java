package com.gzzm.safecampus.campus.siesta;

import com.gzzm.safecampus.campus.base.BaseStudent;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.Transient;


/**
 * 午休学生表
 * Created by Huangmincong on 2018/3/12.
 */
@Entity(table = "SCSIESTASTUDENT", keys = "ssId")
public class SiestaStudent extends BaseStudent
{

    @Generatable(length = 9)
    private Integer ssId;

    /**
     * 午休室ID
     */
    private Integer sroomId;

    @NotSerialized
    private SiestaRoom siestaRoom;

    @Transient
    private String roomName;

    @Transient
    private String studentNo;

    @Transient
    private String studentName;

    @Transient
    private String gradeName;

    @Transient
    private String className;

    public SiestaStudent() {
    }

    public Integer getSsId() {
        return ssId;
    }

    public void setSsId(Integer ssId) {
        this.ssId = ssId;
    }

    public Integer getSroomId() {
        return sroomId;
    }

    public void setSroomId(Integer sroomId) {
        this.sroomId = sroomId;
    }

    public SiestaRoom getSiestaRoom() {
        return siestaRoom;
    }

    public void setSiestaRoom(SiestaRoom siestaRoom) {
        this.siestaRoom = siestaRoom;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiestaStudent that = (SiestaStudent) o;

        return ssId.equals(that.ssId);

    }

    @Override
    public int hashCode() {
        return ssId.hashCode();
    }

    @Override
    public String toString() {
        return deptId.toString();
    }
}
