package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.bus.BusStudent;
import com.gzzm.safecampus.campus.common.Node;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author yuanfang
 * @date 18-02-06 10:00
 */
public abstract class StudentDao extends GeneralDao {
    public StudentDao() {
    }

    /**
     * 通过部门id获取学生集合构造成node结构
     *
     * @param deptId 部门id
     * @return node结构结合
     * @throws Exception
     */
    @OQL("select c.studentId as nodeId, c.studentName as nodeName, c.classesId as parentId " +
            "from Student c where c.deptId=:1 and (c.deleteTag=0 or c.deleteTag is null)")
    public abstract List<Node> getStudentByDept(Integer deptId) throws Exception;

    /**
     * 获取学生头像路径
     *
     * @param studentId 学生id
     * @return 头像路径
     * @throws Exception
     */
    @OQL(" select filePath from Student where studentId = :1")
    public abstract String getFilePath(Integer studentId) throws Exception;

    @OQL(" select s.studentId from Student s where s.studentNo = :1 and s.studentName=:2 and s.deptId=:4 and classes.classesId=:3")
    public abstract Integer getIdByStuNoAndCid(String studentNo, String studentName, Integer classesId, Integer deptId) throws Exception;

    /**
     * 校验：学生学号唯一性
     *
     * @param studentNo 学号
     * @param deptId    部门id
     * @return 学生id
     * @throws Exception
     */
    @OQL("select studentId from com.gzzm.safecampus.campus.classes.Student where studentId<>?1 and studentNo=:2 and deptId=:3")
    public abstract Integer checkStudentNo(Integer studentId, String studentNo, Integer deptId) throws Exception;

    @GetByKey
    public abstract Student getStudent(Integer studentId);

    @OQL("select b from BusStudent b where b.studentId=:1 limit 0,1")
    public abstract BusStudent getByStuId(Integer studentId);

    @OQL("select studentId from com.gzzm.safecampus.campus.classes.Student where studentId<>?1 and phone=:2 and deptId=:3")
    public abstract Integer checkStudentPhone(Integer studentId, String phone, Integer deptId);

    @OQL("select studentId from com.gzzm.safecampus.campus.classes.Student where studentId<>?1 and idCard=:2 and deptId=:3")
    public abstract Integer checkStudentIdCard(Integer studentId, String idCard, Integer deptId);

    @OQL("select s from Student s where s.studentId in (select w.studentId from WxStudent w where w.wxUserId=?1) and (s.deleteTag=0 or s.deleteTag is null)")
    public abstract List<Student> getStudentsByUserId(Integer wxUserId);

    @OQL("select s from Student s where s.studentId in (select w.studentId from WxStudent w where w.wxUserId=?1) and (s.deleteTag=0 or s.deleteTag is null) limit :2,1")
    public abstract Student getOneStudentByUserId(Integer wxUserId,Integer startNum);

    @OQL("select b from BusStudent b where b.busId=?1 ")
    public abstract List<BusStudent> getStudentIdsByBusId(Integer busId);
}
