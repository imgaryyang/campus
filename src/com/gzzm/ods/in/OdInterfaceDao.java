package com.gzzm.ods.in;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 2016/12/3
 */
public abstract class OdInterfaceDao extends GeneralDao
{
    public OdInterfaceDao()
    {
    }

    @OQL("select r.receiveBase,[r.receiveBase.document],[r.receiveBase.document.text] from Receive r" +
            " where r.receiveBase.deptId in ?1 and syned=0 " +
            "and receiveBase.state=0 order by receiveBase.sendTime limit :2")
    public abstract List<ReceiveBase> getReceives(Collection<Integer> deptIds, Integer count) throws Exception;

    @GetByKey
    public abstract Receive getReceive(Long receiveId) throws Exception;

    @GetByKey
    public abstract ReceiveBase getReceiveBase(Long receiveId) throws Exception;

    @GetByKey
    public abstract OfficeDocument getDocument(Long documentId) throws Exception;
}
