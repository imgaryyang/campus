package com.gzzm.platform.organ;

import java.util.*;

/**
 * 部门接口，标识一个数据是一个部门信息
 *
 * @author camel
 * @date 2009-7-29
 */
public interface DeptInfo extends SimpleDeptInfo
{
    public List<? extends DeptInfo> subDepts();

    public Map<String, String> getAttributes();

    public DeptInfo parentDept();

    public DeptInfo getParentDept(int level);

    public List<? extends DeptInfo> allParentDepts();

    public List<Integer> allParentDeptIds();

    public List<? extends DeptInfo> allSubDepts();

    public List<? extends DeptInfo> getSubDepts(Collection<Integer> authDeptIds);

    public List<Integer> allSubDeptIds();

    public void getAllSubDeptIds(List<Integer> deptIds);

    public List<Integer> getSubDeptIds(Collection<Integer> authDeptIds);

    public void getSubDeptIds(Collection<Integer> authDeptIds, List<Integer> deptIds);

    public boolean isSubDeptIdsIn(Collection<Integer> deptIds);

    public boolean isParentDeptIdsIn(Collection<Integer> deptIds);

    public boolean containsLevel(int level);
}