package com.gzzm.ods.redhead;

import com.gzzm.platform.organ.OrganDao;
import net.cyan.thunwind.annotation.OQL;

import java.util.*;

/**
 * 红头模版相关的数据操作
 *
 * @author camel
 * @date 2010-12-17
 */
public abstract class RedHeadDao extends OrganDao
{
    public RedHeadDao()
    {
    }

    public RedHead getRedHead(Integer redHeadId) throws Exception
    {
        return load(RedHead.class, redHeadId);
    }

    public RedHeadTitle getRedHeadTitle(Integer redHeadTitleId) throws Exception
    {
        return load(RedHeadTitle.class, redHeadTitleId);
    }

    @OQL("select t from RedHeadType t order by t.orderId")
    public abstract List<RedHeadType> getRedHeadTypes() throws Exception;

    @OQL("select r from RedHead r where r.deptId=:1 and r.typeId=?2 order by r.orderId")
    public abstract List<RedHead> getRedHeads(Integer deptId, Integer typeId) throws Exception;

    @OQL("select r,[r.dept] from RedHead r where r.deptId in ?1 order by r.dept.leftValue,r.orderId")
    public abstract List<RedHead> getRedHeads(Collection<Integer> deptIds) throws Exception;

    @OQL("select r from RedHead r where r.deptId=:1 and r.redHeadName=:2 order by r.orderId")
    public abstract List<RedHead> getRedHeadsByName(Integer deptId, String redHeadName) throws Exception;

    @OQL("select t from RedHeadTitle t where t.deptId in :1 order by t.dept.leftValue,t.orderId")
    public abstract List<RedHeadTitle> getRedHeadTitles(List<Integer> deptIds) throws Exception;
}
