package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.account.SchoolLevel;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 年级信息表
 *
 * @author yuanfang
 * @date 18-02-06 11:16
 */
@Entity(table = "SCGRADE", keys = "gradeId")
public class Grade extends BaseBean
{
    @Generatable(length = 6)
    private Integer gradeId;

    /**
     * 年级名称
     */
    @ColumnDescription(type = "varchar(30)")
    private String gradeName;

    /**
     * 学校等级
     */
    private Integer levelId;

    @NotSerialized
    @ToOne
    private SchoolLevel schoolLevel;

    /**
     * 关联学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    @NotSerialized
    private School school;

    /**
     * 删除标识
     */
    @ColumnDescription(type = "NUMBER(1)", defaultValue = "0")
    private Integer deleteTag;

    /**
     * 源年级Id：每个学校一份年级信息，在创建账户的时候从平台部门下的年级信息拷贝过来。
     * 为了后续能统计与年级相关的数据，在拷贝的时候记住sourceId。
     */
    private Integer sourceId;

    @ComputeColumn("select s from Subject s where s.gradeId=this.gradeId and (s.deleteTag=0 or s.deleteTag is null)")
    @NotSerialized
    private List<Subject> subjects;

    public Grade()
    {
    }

    public Grade(String gradeName, Integer schoolId)
    {
        this.gradeName = gradeName;
        this.schoolId = schoolId;
    }

    public Grade(Integer gradeId, String gradeName, Integer schoolId)
    {
        this.gradeId = gradeId;
        this.schoolId = schoolId;
        this.gradeName = gradeName;
    }

    public Grade(String gradeName, Integer schoolId, Integer deptId, Integer orderId)
    {
        this.gradeName = gradeName;
        this.schoolId = schoolId;
        this.deptId = deptId;
        this.orderId = orderId;
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public String getGradeName()
    {
        return gradeName;
    }

    public void setGradeName(String gradeName)
    {
        this.gradeName = gradeName;
    }

    public SchoolLevel getSchoolLevel()
    {
        return schoolLevel;
    }

    public void setSchoolLevel(SchoolLevel schoolLevel)
    {
        this.schoolLevel = schoolLevel;
    }

    public Integer getLevelId()
    {
        return levelId;
    }

    public void setLevelId(Integer levelId)
    {
        this.levelId = levelId;
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

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(Integer sourceId)
    {
        this.sourceId = sourceId;
    }

    public List<Subject> getSubjects()
    {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects)
    {
        this.subjects = subjects;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(gradeId, grade.gradeId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(gradeId);
    }

    @Override
    public String toString()
    {
        return gradeName == null ? "" : gradeName;
    }
}
