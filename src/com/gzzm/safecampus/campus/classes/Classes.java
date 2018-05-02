package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 班级信息
 *
 * @author yuanfang
 * @date 18-02-05 17:53
 */
@Entity(table = "SCCLASSES", keys = "classesId")
public class Classes extends BaseBean
{
    @Generatable(length = 6)
    private Integer classesId;

    /**
     * 班级名称
     */
    @ColumnDescription(type = "varchar2(30)")
    private String classesName;

    @ToOne("GRADEID")
    @NotSerialized
    private Grade grade;

    /**
     * 年级ID
     */
    private Integer gradeId;

    /**
     * 备注
     */
    @ColumnDescription(type = "varchar2(200)")
    private String note;

    /**
     * 班主任（教师）ID
     */
    private Integer masterId;

    @ToOne("MASTERID")
    @NotSerialized
    private Teacher master;

    /**
     * 删除标志
     */
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer deleteTag;

    /**
     * 班级下的所有学生
     */
    @NotSerialized
    @OneToMany
    private List<Student> students;

    public Classes()
    {
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getMasterId()
    {
        return masterId;
    }

    public void setMasterId(Integer masterId)
    {
        this.masterId = masterId;
    }

    public Teacher getMaster()
    {
        return master;
    }

    public void setMaster(Teacher master)
    {
        this.master = master;
    }

    public Classes(Integer classesId, String classesName, Integer gradeId)
    {
        this.classesId = classesId;
        this.classesName = classesName;
        this.gradeId = gradeId;
    }

    public Classes(Integer classesId, String classesName)
    {
        this.classesId = classesId;
        this.classesName = classesName;
    }

    public Integer getClassesId()
    {
        return classesId;
    }

    public void setClassesId(Integer classesId)
    {
        this.classesId = classesId;
    }

    public String getClassesName()
    {
        return classesName;
    }

    public void setClassesName(String classesName)
    {
        this.classesName = classesName;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public Grade getGrade()
    {
        return grade;
    }

    public void setGrade(Grade grade)
    {
        this.grade = grade;
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public List<Student> getStudents()
    {
        return students;
    }

    public void setStudents(List<Student> students)
    {
        this.students = students;
    }

    public String getAllName()
    {
        if (grade != null)
            return grade.getGradeName() + classesName;
        return classesName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classes classes = (Classes) o;
        return classesId.equals(classes.classesId);
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(classesId);
    }

    @Override
    public String toString()
    {
        return classesName == null ? "" : classesName;
    }
}
