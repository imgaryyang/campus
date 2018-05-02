package com.gzzm.platform.commons.crud;

import java.util.*;

/**
 * @author camel
 * @date 11-8-30
 */
public interface DeptOwnedCrud
{
    public Collection<Integer> getAuthDeptIds();

    public Integer getDeptId();

    public void setDeptId(Integer deptId);

    public List<Integer> getDeptIds();

    public Collection<Integer> getTopDeptIds();

    public Collection<Integer> getQueryDeptIds();

    public void setQueryDeptIds(Collection<Integer> queryDeptIds);
}