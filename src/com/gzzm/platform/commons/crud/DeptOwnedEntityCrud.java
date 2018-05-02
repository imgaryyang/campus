package com.gzzm.platform.commons.crud;

/**
 * 部门所拥有的数据的crud
 *
 * @author camel
 * @date 2010-2-16
 */
public interface DeptOwnedEntityCrud<E, K> extends DeptOwnedEntityBaseCrud<E, K>, DeptOwnedCrud
{
    public void setDeptId(E entity, Integer deptId) throws Exception;

    public Integer getQueryUserId();

    public void setQueryUserId(Integer queryUserId);
}