package com.gzzm.portal.cms.in;

import net.cyan.thunwind.annotation.Entity;

import java.util.Date;

/**
 * @author lfx
 * @date 17-4-21
 */
@Entity(table = "PLINFOR_IN", keys = "informationId")
public class InformationInterface {
    /**
     * 信息发布表的主键
     */
    private Long informationId;

    /**
     * 新增时间
     */
    private Date addTime;

    /**
     * 最后的修改时间
     */
    private Date updateTime;

    /**
     * 操作的接口
     */
    private Integer deptId;

    public InformationInterface() {
    }

    public Long getInformationId() {
        return informationId;
    }

    public void setInformationId(Long informationId) {
        this.informationId = informationId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof InformationInterface)) return false;

        InformationInterface that = (InformationInterface) o;

        if (informationId != null ? !informationId.equals(that.informationId) : that.informationId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return informationId != null ? informationId.hashCode() : 0;
    }
}
