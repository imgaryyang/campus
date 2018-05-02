package com.gzzm.platform.flow.worksheet;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 11-10-26
 */
public abstract class WorkSheetCatalogDao extends GeneralDao
{
    public WorkSheetCatalogDao()
    {
    }

    public WorkSheetCatalog getCatalog(Integer catalogId) throws Exception
    {
        return load(WorkSheetCatalog.class, catalogId);
    }

    public WorkSheetCatalog getRootCatalog() throws Exception
    {
        WorkSheetCatalog rootCatalog = getCatalog(0);

        if (rootCatalog == null)
        {
            rootCatalog = new WorkSheetCatalog(0, "根目录");
            add(rootCatalog);
        }

        return rootCatalog;
    }

    /**
     * 获得某个用户定义的第一层目录
     *
     * @param userId 用户ID
     * @param type   目录用途
     * @return 目录列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from WorkSheetCatalog c where userId=:1 and type=:2 and parentCatalogId=0 order by orderId")
    public abstract List<WorkSheetCatalog> getTopCatalogs(Integer userId, String type) throws Exception;

    /**
     * 取消收藏，即删除收藏记录
     *
     * @param userId      用户ID
     * @param instanceIds 要取消收藏的文件ID列表
     * @throws Exception 数据库操作错误
     */
    @OQLUpdate("delete WorkSheetCatalogList c where userId=:1 and instanceId in :2")
    public abstract void cancelCatalog(Integer userId, Collection<Long> instanceIds) throws Exception;
}
