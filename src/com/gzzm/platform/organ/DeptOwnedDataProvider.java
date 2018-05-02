package com.gzzm.platform.organ;

import java.util.List;

/**
 * @author camel
 * @date 11-9-21
 */
public interface DeptOwnedDataProvider<T>
{
    public List<T> get(Integer deptId) throws Exception;
}
