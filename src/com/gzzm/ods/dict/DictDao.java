package com.gzzm.ods.dict;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 12-3-15
 */
public abstract class DictDao extends GeneralDao
{
    public DictDao()
    {
    }

    @OQL("select p from Priority p order by orderId")
    public abstract List<Priority> getPriorityList();

    @OQL("select s from Secret s order by orderId")
    public abstract List<Secret> getSecretList();

    @OQL("select t from Tag t order by orderId")
    public abstract List<Tag> getTagList();

    @OQL("select s from Seal s where deptId=:1 order by orderId")
    public abstract List<Seal> getSealList(Integer deptId);
}
