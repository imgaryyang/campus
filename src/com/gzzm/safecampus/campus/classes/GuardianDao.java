package com.gzzm.safecampus.campus.classes;

import net.cyan.thunwind.annotation.GetByField;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author yuanfang
 * @date 18-03-16 18:03
 */
public abstract class GuardianDao extends GeneralDao
{
    public GuardianDao()
    {
    }

    @OQL(" select g.name from Guardian g where g.guardianId =:1")
    public abstract String getName(Integer guardianId);

    @OQL(" select g.phone from Guardian g where g.guardianId =:1")
    public abstract String getPhone(Integer guardianId);

    /**
     * 获取家长电话号码
     *
     * @param studentId 学生id
     * @return 家长电话号码
     * @throws Exception 操作异常
     */
    @OQL("select s.phone from Guardian s where s.studentId=:1 limit 1")
    public abstract String getGuardianPhone(Integer studentId) throws Exception;

    /**
     * 获取家长信息
     *
     * @param studentId 学生Id
     * @return 家长信息
     * @throws Exception 操作异常
     */
    @GetByField("studentId")
    public abstract Guardian getStudentGuardian(Integer studentId) throws Exception;

    /**
     * 获取学生的家长（可能会有多个家长）
     *
     * @param studentId 学生Id
     * @return 家长集合
     */
    @GetByField("studentId")
    public abstract List<Guardian> getStudentGuardians(Integer studentId) throws Exception;

    @GetByField({"name", "phone"})
    public abstract Guardian getGuardianId(String name, String phone);

    @GetByField({"studentId", "phone"})
    public abstract Guardian getStuGuardianId(Integer sid, String phone);

    @OQL("select guardianId from Guardian s where s.studentId=:1 and s.phone =:2")
    public abstract Integer checkGuardianPhone(Integer studentId, String phone);
}
