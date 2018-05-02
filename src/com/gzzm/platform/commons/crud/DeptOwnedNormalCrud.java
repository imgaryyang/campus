package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;

import java.util.*;

/**
 * 部门所拥有的数据的crud
 *
 * @author camel
 * @date 2009-7-18
 */
@Service
public abstract class DeptOwnedNormalCrud<E, K> extends DeptOwnedEditableCrud<E, K> implements OwnedCrud<E, K, Integer>
{
    public String getOwnerField()
    {
        return "deptId";
    }

    public Integer getOwnerKey(E entity) throws Exception
    {
        return getDeptId(entity);
    }

    public void setOwnerKey(E entity, Integer deptId) throws Exception
    {
        setDeptId(entity, deptId);
    }

    public void moveTo(K key, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        DeptOwnedCrudUtils.moveTo(Collections.singleton(key), newDeptId, oldDeptId, this);
    }

    @Transactional
    public void moveAllTo(K[] keys, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        if (keys != null)
            setKeys(keys);

        DeptOwnedCrudUtils.moveTo(Arrays.asList(getKeys()), newDeptId, oldDeptId, this);
    }

    public void copyTo(K key, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        DeptOwnedCrudUtils.copyTo(Collections.singleton(key), newDeptId, oldDeptId, this);
    }

    @Transactional
    public void copyAllTo(K[] keys, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        if (keys != null)
            setKeys(keys);

        DeptOwnedCrudUtils.copyTo(Arrays.asList(getKeys()), newDeptId, oldDeptId, this);
    }
}
