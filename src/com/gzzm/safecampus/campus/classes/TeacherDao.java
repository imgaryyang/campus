package com.gzzm.safecampus.campus.classes;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author yuanfang
 * @date 18-02-06 10:00
 */
public abstract class TeacherDao extends GeneralDao
{
    public TeacherDao()
    {
    }

    @LoadByKey
    public Teacher getTeacher(Integer teacherId) throws Exception
    {
        return load(Teacher.class, teacherId);
    }

    /**
     * 获取教师头像存储路径
     *
     * @param teacherId 教师id
     * @return 头像存储路径
     * @throws Exception
     */
    @OQL(" select filePath from Teacher where teacherId = :1")
    public abstract String getFilePath(Integer teacherId) throws Exception;

    /**
     * 由部门id获取教师集合
     *
     * @param deptId 部门id
     * @return 教师集合
     * @throws Exception
     */
    @OQL("select t from  Teacher t where deptId = :1  and (deleteTag=0 or deleteTag is null)")
    public abstract List<Teacher> getTeachers(Integer deptId) throws Exception;

    /**
     * 校验：由工号获取教师id
     *
     * @param teacherNo 教师工号
     * @param deptId    部门id
     * @param teacherId 教师id
     * @return 相同工号的教师id
     * @throws Exception
     */
    @OQL("select teacherId from Teacher  where teacherNo=:1 and deptId =:2 and teacherId<>?3 and (deleteTag=0 or deleteTag is null)")
    public abstract Integer checkTeacherNo(String teacherNo, Integer deptId, Integer teacherId) throws Exception;

    /**
     * 判断是否存在相同工号和名字的老师
     *
     * @param teacherNo   工号
     * @param teacherName 名字
     * @param deptId      所在学校
     * @return 老师Id
     * @throws Exception 操作异常
     */
    @OQL("select teacherId from Teacher  where teacherNo=:1 and deptId =:3 and teacherName=:2 and (deleteTag=0 or deleteTag is null)")
    public abstract Integer getTeacherId(String teacherNo, String teacherName, Integer deptId) throws Exception;


    /**
     * 由教师工号获取教师id
     *
     * @param teacherNo 教师工号
     * @param deptId    部门id
     * @return 教师id
     * @throws Exception
     */
    @OQL("select teacherId from Teacher  where teacherNo=:1 and deptId =:2 and (deleteTag=0 or deleteTag is null)")
    public abstract Integer getTeacherId(String teacherNo, Integer deptId) throws Exception;

    /**
     * 由班级id获取班主任
     *
     * @param classesId 班级id
     * @return 班级教师（关联教师）
     * @throws Exception
     */
    @OQL("select s from com.gzzm.safecampus.campus.classes.ClassTeacher s where s.classesId=:1 and s.teacherRole = 1")
    public abstract ClassTeacher getMaster(Integer classesId) throws Exception;

    @OQL("select teacherId from Teacher  where idCard=:1 and deptId =:2 and teacherId<>?3 and (deleteTag=0 or deleteTag is null)")
    public abstract Integer checkTeacherIdCard(String idCard, Integer deptId, Integer teacherId);

    @OQL("select teacherId from Teacher  where phone=:1 and deptId =:2 and teacherId<>?3 and (deleteTag=0 or deleteTag is null)")
    public abstract Integer checkTeacherPhone(String phone, Integer deptId, Integer teacherId);
}
