package com.gzzm.platform.organ.syn;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

/**
 * 数据同步dao
 *
 * @author camel
 * @date 2011-4-19
 */
public abstract class OrganSynDao extends OrganDao
{
    public OrganSynDao()
    {
    }

    /**
     * 根据sourceId获取本系统中的部门
     *
     * @param sourceId 原部门ID，原来系统中的部门
     * @return 本系统中的部门
     * @throws Exception 数据库读取数据错误
     */
    @GetByField("sourceId")
    public abstract Dept getDeptWithSourceId(String sourceId) throws Exception;

    /**
     * 根据sourceId获取本系统中的部门ID
     *
     * @param sourceId 原部门ID，原来系统中的部门ID
     * @return 本系统中的部门ID
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select deptId from Dept where sourceId=:1")
    public abstract Integer getDeptIdWithSourceId(String sourceId) throws Exception;

    /**
     * 根据sourceId获取本系统中的用户
     *
     * @param sourceId 原用户ID，原来系统中的用户ID
     * @return 本系统中的用户ID
     * @throws Exception 数据库读取数据错误
     */
    @GetByField("sourceId")
    public abstract User getUserWithSourceId(String sourceId) throws Exception;

    /**
     * 根据sourceId获取本系统中的用户ID
     *
     * @param sourceId 原用户ID，原来系统中的用户ID
     * @return 本系统中的用户ID
     * @throws Exception 数据库读取数据错误
     */
    @OQL("select userId from User where sourceId=:1")
    public abstract Integer getUserIdWithSourceId(String sourceId) throws Exception;
}
