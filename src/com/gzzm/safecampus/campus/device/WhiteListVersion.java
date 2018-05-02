package com.gzzm.safecampus.campus.device;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.commons.util.StringUtils;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 白名单
 *
 * @author liyabin
 * @date 2018/3/28
 */
@Entity(table = "SCWHITELIST", keys = "id")
public class WhiteListVersion extends BaseBean
{
    @Generatable(length = 9)
    private Integer id;
    /**
     * 白名单版本
     */
    private Integer versionNo;
    /**
     * 描述
     */
    @ColumnDescription(type = "varchar(300)")
    private String versionDescriptive;
    /**
     * 所属学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    private School school;

    private Integer orderId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 上一个版本号
     */
    private Integer upVersion;
    @ToOne("UPVERSION")
    private WhiteListVersion upListVersion;
    public Integer getUpVersion()
    {
        return upVersion;
    }

    public void setUpVersion(Integer upVersion)
    {
        this.upVersion = upVersion;
    }

    public WhiteListVersion getUpListVersion()
    {
        return upListVersion;
    }

    public void setUpListVersion(WhiteListVersion upListVersion)
    {
        this.upListVersion = upListVersion;
    }

    public WhiteListVersion()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getVersionNo()
    {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo)
    {
        this.versionNo = versionNo;
    }

    public String getVersionDescriptive()
    {
        return versionDescriptive;
    }

    public void setVersionDescriptive(String versionDescriptive)
    {
        this.versionDescriptive = versionDescriptive;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }


    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof WhiteListVersion)) return false;

        WhiteListVersion whiteList = (WhiteListVersion) o;

        return id != null ? id.equals(whiteList.id) : whiteList.id == null;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public String toString()
    {
        if(versionNo==null) return "-------------------";
        return "版本："+versionNo+"("+ (StringUtils.isEmpty(versionDescriptive)?"":versionDescriptive)+")";
    }
}
