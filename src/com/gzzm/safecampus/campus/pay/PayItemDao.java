package com.gzzm.safecampus.campus.pay;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

/**
 * 缴费项目
 *
 * @author yuanfang
 * @date 18-02-27 17:43
 */
public abstract class PayItemDao extends GeneralDao
{
    public PayItemDao()
    {
    }

    /**
     * 由缴费项目ID获取学校ID
     *
     * @param payItemId 缴费项目ID
     * @return 学校ID
     * @throws Exception oql异常
     */
    @OQL(" select schoolId from School where " +
            "deptId = first( select deptId from PayItem where payItemId =:1)")
    public abstract Integer getSchoolId(Integer payItemId) throws Exception;

    /**
     * 由缴费项目ID获取部门ID
     *
     * @param payItemId 费项目ID
     * @return 部门ID
     * @throws Exception oql异常
     */
    @OQL(" select deptId from PayItem where payItemId =:1")
    public abstract Integer getDeptId(Integer payItemId) throws Exception;

    /**
     * 验证缴费项目编号唯一
     * （不含已删除的缴费项目编号）
     *
     * @param serialNo  项目编号
     * @param deptId    部门id
     * @param payItemId 缴费项目ID
     * @return 缴费项目ID
     * @throws Exception oql异常
     */
    @OQL("select payItemId from PayItem  where serialNo=:1 and deptId =:2 and payItemId<>?3 and deleteTag =0")
    public abstract Integer checkSerialNo(String serialNo, Integer deptId, Integer payItemId) throws Exception;
}
