package com.gzzm.ods.business;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 11-9-21
 */
public abstract class BusinessDao extends GeneralDao
{
    public BusinessDao()
    {
    }

    public BusinessModel getBusiness(Integer businessId) throws Exception
    {
        return get(BusinessModel.class, businessId);
    }

    @OQL("select m from BusinessModel m where deptId=:1 and type in :2 and tag=?3 and display=1 and state=1 order by orderId")
    public abstract List<BusinessModel> getBusinesses(Integer deptId, String[] type, BusinessTag tag) throws Exception;

    @OQL("select m from BusinessModel m where deptId=:1 and type=:2  and state=1 order by orderId")
    public abstract List<BusinessModel> getBusinesses(Integer deptId, String type) throws Exception;

    @OQL("select m from BusinessModel m where deptId=:1 and type=:2 and componentType=:3  and state=1 order by orderId")
    public abstract List<BusinessModel> getBusinessesByComponent(Integer deptId, String type, String component)
            throws Exception;

    @OQL("select m from BusinessModel m where deptId=:1 and type=:2 and componentType=:3  and state=1 order by orderId limit 1")
    public abstract BusinessModel getBusinessByComponent(Integer deptId, String type, String component)
            throws Exception;

    @OQL("select m from BusinessModel m join Dept d on " +
            "m.dept.leftValue<=d.leftValue and m.dept.rightValue>d.leftValue " +
            "where d.deptId in ?1 and type in ?2 and m.state=1 order by m.dept.leftValue,m.orderId")
    public abstract List<BusinessModel> getAllBusiness(Collection<Integer> deptIds, String[] type) throws Exception;
}
