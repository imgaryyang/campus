package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.common.Node;
import net.cyan.thunwind.annotation.LoadByKey;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author yuanfang
 * @date 18-02-06 10:00
 */
public abstract class ClassesDao extends GeneralDao
{
    public ClassesDao()
    {
    }

    @LoadByKey
    public Classes getClasses(Integer classId) throws Exception
    {
        return load(Classes.class, classId);
    }

    @OQL("select c from Classes c where c.gradeId =:1  and c.deleteTag=0 order by c.orderId")
    public abstract List<Classes> getGradeClasses(Integer gradeId) throws Exception;

    @OQL("select c.classes from Student c where studentId =:1")
    public abstract Classes getByStudentId(Integer studentId) throws Exception;

    @OQL("select count(*) from Classes c where c.gradeId =:1  and c.deleteTag=0")
    public abstract boolean hasChildren(int i) throws Exception;

    @OQL("select gradeId from Classes  where classesId =:1")
    public abstract Integer getGradeId(Integer classesId) throws Exception;


    @OQL("select classesName from Classes  where classesId =:1")
    public abstract String getClassesName(Integer classesId) throws Exception;

   @OQL("select c.classesId as nodeId, c.classesName as nodeName, c.gradeId as parentId from Classes c " +
            "where c.deptId =:1  and (c.deleteTag=0 or c.deleteTag is null) order by c.orderId ")
    public abstract List<Node> getClassesByDept(int deptId) throws Exception;

    @OQL("select gradeName from Grade  where  gradeId= first(select gradeId from Classes where  classesId =:1)")
    public abstract String getGradeName(int classId) throws Exception;

    @OQL("select deptId from Classes c where classesId =:1")
    public abstract Integer getDeptId(Integer classesId) throws Exception;

    @OQL("select distinct(c) from Classes c left join ClassTeacher t on t.classesId=c.classesId where t.teacherId =:1 order by c.classesName")
    public abstract List<Classes> getclassByTeacherId(Integer teacherId) throws Exception;

    @OQL("select c from Classes c where c.classesName = :1 and c.gradeId = :2 and c.deptId =:3 and c.deleteTag = 0")
    public abstract Classes getClassesByGradeName(String classesName, Integer gradeId, Integer deptId) throws Exception;

    @OQL("select g  from  Grade g where g.gradeName = :1 and g.deptId=:2")
    public abstract Grade getGradeByName(String gradeName, Integer defaultDeptId) throws Exception;

    @OQL("select c from Classes c where c.classesName = :1 and c.gradeId = :2 and c.deptId = :3 and c.deleteTag = 0")
    public abstract Classes getClassesByNameAndGradeId(String className, Integer gradeId, Integer defaultDeptId) throws Exception;

    @OQL("select s from Student s where s.classesId=:1")
    public abstract List<Student> getStudentsByClassesId(Integer classesId) throws Exception;

    @OQL("select c from Classes c where c.gradeId =:1 and c.deleteTag =0 order by c.classesName")
    public abstract List<Classes> getGradeClasses0(Integer gradeId) throws Exception;

    @OQL("select distinct(s) from Student s left join BusStudent bs on s.studentId=bs.studentId where bs.busId = :1 ")
    public abstract List<Student> getStudentsByBus(Integer busId);
}
