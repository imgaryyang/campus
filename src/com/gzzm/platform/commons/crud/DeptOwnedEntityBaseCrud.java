package com.gzzm.platform.commons.crud;

import net.cyan.crud.EntityDisplayCrud;

import java.util.*;

/**
 * @author camel
 * @date 13-11-12
 */
public interface DeptOwnedEntityBaseCrud<E, K> extends EntityDisplayCrud<E, K>
{
    public Collection<Integer> getAuthDeptIds();

    public Integer getDeptId() throws Exception;

    public Integer getDeptId(E entity) throws Exception;

    public List<Integer> getDeptIds(Collection<K> keys) throws Exception;

    public Integer getUserId(E entity) throws Exception;

    public List<Integer> getUserIds(Collection<K> keys) throws Exception;

    public boolean checkDeptId(E entity, Collection<Integer> authDeptIds) throws Exception;
}