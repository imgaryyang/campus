package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.UpdateTimeService;
import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.FieldValidator;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.commons.validate.annotation.Warning;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;
import java.util.Objects;

/**
 * 学年信息表
 *
 * @author yuanfang
 * @date 18-03-15 10:47
 */

@Entity(table = "SCSCHOOLYEAR", keys = "schoolYearId")
public class SchoolYear extends BaseBean
{
    @Inject
    private static Provider<SchoolYearDao> daoProvider;

    @Generatable(length = 6)
    private Integer schoolYearId;

    @ToOne("SCHOOLID")
    private School school;

    private Integer schoolId;

    @Require
    @ColumnDescription(type = "number(4)", nullable = false)
    private Integer year;
    /**
     * 是否当前学年
     */
    private Boolean status;

    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getSchoolYearId()
    {
        return schoolYearId;
    }

    public void setSchoolYearId(Integer schoolYearId)
    {
        this.schoolYearId = schoolYearId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolYear that = (SchoolYear) o;
        return Objects.equals(schoolYearId, that.schoolYearId);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(schoolYearId);
    }

    @Override
    public String toString()
    {
        return year + "";
    }

    @FieldValidator("year")
    @Warning("year.schoolYear_exists")
    public Integer checkYear() throws Exception
    {
        return daoProvider.get().checkYear(getYear(), getDeptId(), getSchoolYearId());
    }

    @FieldValidator("status")
    @Warning("status.schoolYear_exists")
    public Integer checkYearStatus() throws Exception
    {
        return daoProvider.get().checkYearStatus();
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("SchoolYear", new Date());
    }
}
