package com.gzzm.ods.business;

import com.gzzm.platform.organ.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 11-9-21
 */
public class BusinessService
{
    @Inject
    private BusinessDao dao;

    @Inject
    private DeptService deptService;

    public BusinessService()
    {
    }

    public BusinessDao getDao()
    {
        return dao;
    }

    public DeptService getDeptService()
    {
        return deptService;
    }

    public List<BusinessModel> getSelectableBusinesses(Integer deptId, final String[] types, final BusinessTag tag,
                                                       Collection<Integer> authDeptIds) throws Exception
    {
        return deptService.loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<BusinessModel>()
        {
            public List<BusinessModel> get(Integer deptId) throws Exception
            {
                return dao.getBusinesses(deptId, types, tag);
            }
        }, authDeptIds, null);
    }

    public List<BusinessModel> getSelectableBusinessesByComponent(Integer deptId, final String type,
                                                                  final String component,
                                                                  Collection<Integer> authDeptIds) throws Exception
    {
        return deptService.loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<BusinessModel>()
        {
            public List<BusinessModel> get(Integer deptId) throws Exception
            {
                return dao.getBusinessesByComponent(deptId, type, component);
            }
        }, authDeptIds, null);
    }

    public BusinessModel getBusinessByComponent(Integer deptId, String type, String component)
            throws Exception
    {
        BusinessModel businessModel = dao.getBusinessByComponent(deptId, type, component);

        if (businessModel != null)
            return businessModel;

        DeptInfo dept = deptService.getDept(deptId);
        while ((dept = dept.parentDept()) != null)
        {
            businessModel = dao.getBusinessByComponent(dept.getDeptId(), type, component);

            if (businessModel != null)
                return businessModel;
        }

        return null;
    }
}
