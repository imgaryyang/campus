package com.gzzm.ods.exchange.back;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 退文理由数据操作
 *
 * @author ldp
 * @date 2018/1/10
 */
public abstract class BackReasonDao extends GeneralDao
{
    public BackReasonDao()
    {
    }

    @LoadByKey
    public abstract BackReason getBackReason(Integer reasonId) throws Exception;

    /**
     * 查询退文理由类型，以BackReasonBean返回
     */
    @OQL("select t.typeId as id,t.typeName as name,0 as type from BackReasonType t order by orderId")
    public abstract List<BackReasonBean> getTypesForBean() throws Exception;

    /**
     * 根据类型查询退文理由，以BackReasonBean返回
     */
    @OQL("select r.reasonId as id,r.reason as name,1 as type from BackReason r where r.typeId=?1 and deptId=:1 order by orderId")
    public abstract List<BackReasonBean> getReasonsForBean(Integer typeId, Integer deptId) throws Exception;
}
