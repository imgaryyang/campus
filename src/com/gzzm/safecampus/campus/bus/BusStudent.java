package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.base.BaseStudent;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * @author czy
 * 校巴学生管理表
 */
@Entity(table = "SCBUSSTUDENT",keys = "busStudentId")
public class BusStudent extends BaseStudent
{

    @Generatable(length = 6)
    private Integer busStudentId;

    /**
     * 校巴ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer busId;

    @NotSerialized
    private BusInfo busInfo;

    /**
     * 途经站点ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer siteId;

    @NotSerialized
    private BusSite busSite;

    public BusStudent(){}

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

    public BusSite getBusSite() {
        return busSite;
    }

    public void setBusSite(BusSite busSite) {
        this.busSite = busSite;
    }

    public Integer getBusStudentId() {
        return busStudentId;
    }

    public void setBusStudentId(Integer busStudentId) {
        this.busStudentId = busStudentId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusStudent that = (BusStudent) o;

        return busStudentId.equals(that.busStudentId);

    }

    @Override
    public int hashCode() {
        return busStudentId.hashCode();
    }
}
