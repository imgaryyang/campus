package com.gzzm.safecampus.campus.classes;

import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 家属相关业务处理类
 *
 * @author Neo
 * @date 2018/4/10 15:13
 */
public class GuardianService
{
    @Inject
    private GuardianDao guardianDao;

    public GuardianService()
    {
    }

    /**
     * 获取学生的家长电话
     * @param studentId 学生Id
     * @return 家长电话
     * @throws Exception 操作异常
     */
    public String getGuardianPhone(Integer studentId) throws Exception
    {
        return guardianDao.getGuardianPhone(studentId);
    }

    public Guardian getStudentGuardian(Integer studentId) throws Exception
    {
        return guardianDao.getStudentGuardian(studentId);
    }

    public List<Guardian> getStudentGuardians(Integer studentId) throws Exception
    {
        return guardianDao.getStudentGuardians(studentId);
    }
}
