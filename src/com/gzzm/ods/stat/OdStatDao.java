package com.gzzm.ods.stat;

import com.gzzm.ods.business.BusinessModel;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 12-2-22
 */
public abstract class OdStatDao extends GeneralDao
{
    public OdStatDao()
    {
    }

    @OQL("select m from BusinessModel m where m.businessId in :1 order by dept.leftValue desc,m.orderId")
    public abstract List<BusinessModel> getBusinesses(Integer[] businessIds) throws Exception;

    @OQL("select min(sendTime) from ReceiveBase where deptId=:1")
    public abstract Date getMinReceiveTime(Integer deptId) throws Exception;

    @OQL("select min(sendTime) from Send where deptId=:1")
    public abstract Date getMinSendTime(Integer deptId) throws Exception;

    @OQL("select min(startTime) from OdFlowInstance where deptId=:1")
    public abstract Date getMinInstanceTime(Integer deptId) throws Exception;
}
