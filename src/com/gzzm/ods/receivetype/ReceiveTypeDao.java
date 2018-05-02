package com.gzzm.ods.receivetype;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 11-11-5
 */
public abstract class ReceiveTypeDao extends GeneralDao
{
    public ReceiveTypeDao()
    {
    }

    public ReceiveType getReceiveType(Integer receiveTypeId) throws Exception
    {
        return load(ReceiveType.class, receiveTypeId);
    }

    public ReceiveType getRootReceiveType() throws Exception
    {
        ReceiveType receiveType = getReceiveType(0);

        if (receiveType == null)
        {
            receiveType = new ReceiveType(0, "根节点");
            receiveType.setParentReceiveTypeId(-1);
            add(receiveType);
        }

        return receiveType;
    }

    @OQL("select t from ReceiveType t where deptId=:1 and parentReceiveTypeId=0 and type=:2 order by orderId")
    public abstract List<ReceiveType> getTopReceiveTypes(Integer deptId, int type) throws Exception;

    @OQL("select t from ReceiveType t where deptId=:1")
    public abstract List<ReceiveType> getReceiveTypes(Integer deptId) throws Exception;

    @OQL("select t from ReceiveType t where t.deptId=:1 and exists d in sourceDepts : d.deptName=:2")
    public abstract ReceiveType getReceiveType(Integer deptId, String sourceDept) throws Exception;
}
