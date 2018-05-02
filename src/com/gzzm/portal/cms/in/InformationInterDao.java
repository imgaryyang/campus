package com.gzzm.portal.cms.in;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author Xrd
 * @date 2017/7/5 0005 19:24
 */
public abstract class InformationInterDao extends GeneralDao
{
    public InformationInterDao()
    {
    }
    @OQL("select deptId from Dept c where c.deptCode=:1 limit 1")
        public abstract Integer getDeptIdByDeptCode(String deptCode);

    @OQL("select c from InformationInterface c where c.informationId = :1 and c.deptId = :2")
    public abstract InformationInterface getInfoInByInformationIdAndDeptId(Long informationId,Integer deptId)throws Exception;
}
