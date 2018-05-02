package com.gzzm.safecampus.campus.attendance;

import com.gzzm.safecampus.campus.bus.BusSite;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * 考勤站点关联表:
 *
 * @author Huangmincong
 * @date 2018/03/15
 */
@Entity(table = "SCATTENDANCEBUSSITE",keys = "rId")
public class AttendanceBusSite {

    @Generatable(length = 12)
    private Integer rId;

    private Integer attId;

    @NotSerialized
    private Attendance attendance;

    @ColumnDescription(type = "VARCHAR2(50)")
    private String siteName;

    public AttendanceBusSite() {
    }

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public Integer getAttId() {
        return attId;
    }

    public void setAttId(Integer attId) {
        this.attId = attId;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceBusSite that = (AttendanceBusSite) o;

        if (!rId.equals(that.rId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rId.hashCode();
    }

    @Override
    public String toString() {
        return "AttendanceBusSite{" +
                "rId=" + rId +
                '}';
    }
}
