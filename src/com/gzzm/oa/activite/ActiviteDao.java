package com.gzzm.oa.activite;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author lfx
 * @date 11-9-30
 */
public abstract class ActiviteDao extends GeneralDao
{
    public ActiviteDao()
    {
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public Activite getActivite(Integer activiteId) throws Exception
    {
        return load(Activite.class, activiteId);
    }

    /**
     * 获取日常活动类型列表
     *
     * @return 日常活动类型列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select b from ActiviteType b order by orderId")
    public abstract List<ActiviteType> getActiviteTypes() throws Exception;

    /**
     * 获取预算年份列表
     *
     * @return 预算年份列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select b from ActiviteBudget b where deptId= ?1 order by b.budgetYears desc")
    public abstract List<ActiviteBudget> getActiviteBudgets(Integer deptId) throws Exception;

    /**
     * 获取年度活动列表
     *
     * @return 年度活动列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select a from Activite a where a.activiteBudget.budgetYears= ?1 order by a.startTime")
    public abstract List<Activite> getActivitiesByYear(Integer year) throws Exception;

    /**
     * 获取活动列表
     *
     * @return 活动列表
     * @throws Exception 数据库查询异常
     */
    @OQL("select a from Activite a order by a.startTime")
    public abstract List<Activite> getAllActivitiesBy() throws Exception;

    /**
     * 获取年度活动预算总额
     *
     * @return 活动年度活动预算总额
     * @throws Exception 数据库查询异常
     */
    @OQL("select sum(b.budgetAmount) from ActiviteBudget b ")
    public abstract Double sumActiviteAmount() throws Exception;

    public ActiviteMember getActiviteMember(Integer memberId) throws Exception
    {
        return load(ActiviteMember.class, memberId);
    }

    @GetByField({"activiteId", "userId"})
    public abstract ActiviteMember getActiviteMember(Integer activiteId, Integer userId) throws Exception;

    /**
     * 获取最大排序号
     *
     * @param activiteId 活动Id
     * @return 获取最大排序号
     * @throws Exception 数据库查询异常
     */
    @OQL("select max(orderId) from ActiviteMember where activiteId=:1")
    public abstract Integer getMaxMemberOrder(Integer activiteId) throws Exception;
}
