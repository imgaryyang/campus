package com.gzzm.safecampus.campus.tutorship;

import net.cyan.commons.util.Inputable;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.Date;

/**
 * 课外辅导--机构
 * @author yiuman
 * @date 18-03-12 17:15
 */

@Entity(table = "SCINSITUTION", keys = "institutionId")
public class TutorInstitution extends TutorBase {

    /**
     * 机构ID
     */
    @Generatable(length = 6)
    private Integer institutionId;

    /**
     * 机构名称
     */
    @ColumnDescription(type = "varchar(250)")
    private String institutionName;

    /**
     * 机构介绍
     */
    @CommonFileColumn(pathColumn = "filePath", target="target", path = "institution/{yyyyMM}/{yyyyMMdd}/{institutionId}")
    private byte[] institutionDes;

    /**
     * 服务电话
     */
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    /**
     * 机构地址
     */
    @ColumnDescription(type = "varchar(500)")
    private String address;

    /**
     * 插图
     */
    @CommonFileColumn(pathColumn = "picPath", target = "target", path = "instiution/pic")
    private Inputable picture;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    @ColumnDescription(type = "varchar(250)")
    private String picPath;

    private Date createTime;

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public byte[] getInstitutionDes() {
        return institutionDes;
    }

    public void setInstitutionDes(byte[] institutionDes) {
        this.institutionDes = institutionDes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Inputable getPicture() {
        return picture;
    }

    public void setPicture(Inputable picture) {
        this.picture = picture;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TutorInstitution)) return false;

        TutorInstitution that = (TutorInstitution) o;

        return institutionId != null ? institutionId.equals(that.institutionId) : that.institutionId == null;
    }

    @Override
    public int hashCode() {
        return institutionId != null ? institutionId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return institutionName;
    }
}
