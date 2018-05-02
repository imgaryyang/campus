package com.gzzm.ods.archive;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

/**
 * 归档配置，配置每个部门的全宗号和部门名称
 *
 * @author camel
 * @date 2016/5/10
 */
@Entity(table = "ODARCHIVECONFIG", keys = "deptId")
@AutoAdd(false)
public class ArchiveConfig
{
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 全宗号
     */
    @ColumnDescription(type = "varchar(250)")
    private String generalNo;

    /**
     * 单位名称
     */
    @ColumnDescription(type = "varchar(250)")
    private String generalName;

    public ArchiveConfig()
    {
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

    public String getGeneralNo()
    {
        return generalNo;
    }

    public void setGeneralNo(String generalNo)
    {
        this.generalNo = generalNo;
    }

    public String getGeneralName()
    {
        return generalName;
    }

    public void setGeneralName(String generalName)
    {
        this.generalName = generalName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ArchiveConfig))
            return false;

        ArchiveConfig that = (ArchiveConfig) o;

        return deptId.equals(that.deptId);
    }

    @Override
    public int hashCode()
    {
        return deptId.hashCode();
    }

    @Override
    public String toString()
    {
        if (dept != null)
            return dept.getDeptName();
        else
            return null;
    }
}
