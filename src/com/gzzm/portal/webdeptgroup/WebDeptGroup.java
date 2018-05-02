package com.gzzm.portal.webdeptgroup;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 门户网站的部门组维护表
 * @author Xrd
 * @date 2018/3/20 16:56
 */
@Entity(table = "PLWEBDEPTGROUP", keys = "webDeptGroupId")
public class WebDeptGroup
{
    /**
     * 部门组ID
     */
    @Generatable(length = 6)
    private Integer webDeptGroupId;

    /**
     * 用途ID
     */
    @ColumnDescription(type = "varchar(50)")
    private String purposeId;

    /**
     * 分组名称
     */
    @ColumnDescription(type = "varchar(500)")
    private String groupName;

    /**
     * 部门
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public WebDeptGroup()
    {
    }

    public Integer getWebDeptGroupId()
    {
        return webDeptGroupId;
    }

    public void setWebDeptGroupId(Integer webDeptGroupId)
    {
        this.webDeptGroupId = webDeptGroupId;
    }

    public String getPurposeId()
    {
        return purposeId;
    }

    public void setPurposeId(String purposeId)
    {
        this.purposeId = purposeId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
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
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof WebDeptGroup))
            return false;

        WebDeptGroup webDeptGroup = (WebDeptGroup) o;

        return webDeptGroupId.equals(webDeptGroup.webDeptGroupId);
    }

    @Override
    public int hashCode()
    {
        return webDeptGroupId.hashCode();
    }

    @Override
    public String toString()
    {
        if(dept!=null){
            return dept.getDeptName();
        }
        else return null;
    }
}
