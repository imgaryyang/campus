package com.gzzm.safecampus.campus.account;

import com.gzzm.safecampus.campus.classes.*;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author yuanfang
 * @date 18-02-06 10:00
 */
public abstract class SchoolDao extends GeneralDao
{
    public SchoolDao()
    {
    }

    @OQL("select s from School  s where deptId =:1")
    public abstract School getSchool(Integer deptId) throws Exception;

    /**
     * 获取学校的商户信息
     *
     * @param schoolId 学校主键
     * @return 商户信息
     * @throws Exception
     */
    @OQL("select m from com.gzzm.safecampus.campus.account.Merchant m where m.schoolId=:1")
    public abstract Merchant getSchoolMerchant(Integer schoolId) throws Exception;

    /**
     * 获取用户的权限
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 用户权限的主键数组
     * @throws Exception
     */
    @OQL("select r.roleId from com.gzzm.platform.organ.UserRole r where r.userId=:1 and r.deptId=:2")
    public abstract List<Integer> getUserRoleByUser(Integer userId, Integer deptId) throws Exception;

    /**
     * 获取学校的最大编号
     *
     * @return 学校编号
     * @throws Exception
     */
    @OQL("select max(c.schoolNum) from com.gzzm.safecampus.campus.account.School c")
    public abstract String getSchoolMaxNum() throws Exception;

    /**
     * 获取某个学校等级下的年级
     *
     * @param levelId 学校等级
     * @param deptId  部门Id
     * @return 年级列表
     * @throws Exception 操作异常
     */
    @OQL("select s from Grade s where s.levelId=:1 and s.deptId=:2 and (s.deleteTag=0 or s.deleteTag is null) order by s.orderId")
    public abstract List<Grade> getGradeByLevelId(Integer levelId, Integer deptId) throws Exception;

    @OQL("select s from Subject s where s.gradeId=:1 and s.deptId=:2 and (s.deleteTag=0 or s.deleteTag is null) order by s.orderId")
    public abstract List<Subject> getSubjectByGradeId(Integer gradeId, Integer deptId) throws Exception;

    /**
     * 获取所有的学校等级
     *
     * @return 学校等级列表
     * @throws Exception 操作异常
     */
    @OQL("select s from SchoolLevel s where (s.deleteTag=0 or s.deleteTag is null) order by s.orderId")
    public abstract List<SchoolLevel> getSchoolLevel() throws Exception;
}
