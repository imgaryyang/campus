package com.gzzm.ods.bak;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receivetype.ReceiveType;
import com.gzzm.ods.sendnumber.SendNumber;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.Date;
import java.util.List;

/**
 * @author zjw
 * @date 12-10-16
 */
public abstract class OdDeptBakDao extends GeneralDao
{
    public OdDeptBakDao()
    {
    }

    public OdDeptBak getOdDeptBak(Integer backId) throws Exception
    {
        return load(OdDeptBak.class, backId);
    }

    public Send getSend(Long sendId) throws Exception
    {
        return load(Send.class, sendId);
    }

    public Receive getReceive(Long ReceiveId) throws Exception
    {
        return load(Receive.class, ReceiveId);
    }

    @OQL("select p from ReceiveType p order by orderId")
    public abstract List<ReceiveType> getReceiveTypeList();

    @OQL("select p from SendNumber p where p.deptId=:1 order by orderId")
    public abstract List<SendNumber> getSendNumbers(Integer deptId);

    @OQL("select s from Send s where s.sendTime>=:1 and s.sendTime<=:2 and " +
            "s.deptId=:3 and s.sendNumberId=?4 and s.state=0 order by s.sendTime")
    public abstract List<Send> getSendIds(Date startTime, Date endTime, Integer deptId,
                                          Integer SendNumberId);

    @OQL("select r from Receive r where r.receiveBase.acceptTime>=:1 and " +
            "r.receiveBase.acceptTime<=:2 and r.receiveBase.deptId=:3 and r.receiveTypeId in ?4 and " +
            "r.receiveBase.state<=4 order by r.receiveBase.acceptTime")
    public abstract List<Receive> getReceiveIds(Date startTime, Date endTime, Integer deptId,
                                                List<Integer> receiveTypeIds);

}
