package com.gzzm.ods.sendnumber;

import com.gzzm.ods.business.BusinessModel;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 11-11-5
 */
public abstract class SendNumberDao extends GeneralDao
{
    public SendNumberDao()
    {
    }

    @OQL("select s from SendNumber s where deptId=:1 order by orderId")
    public abstract List<SendNumber> getSendNumbers(Integer deptId) throws Exception;

    @OQL("select s from SendNumber s where deptId in :1 order by dept.leftValue,orderId")
    public abstract List<SendNumber> getSendNumbers(Collection<Integer> deptId) throws Exception;

    public SendNumber getSendNumber(Integer sendNumberId) throws Exception
    {
        return load(SendNumber.class, sendNumberId);
    }

    public BusinessModel getBusiness(Integer businessId) throws Exception
    {
        return get(BusinessModel.class, businessId);
    }
}
