package com.gzzm.platform.visit;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 访问记录相关的数据库操作
 *
 * @author camel
 * @date 2011-6-21
 */
public abstract class VisitDao extends GeneralDao
{
    public VisitDao()
    {
    }

    /**
     * 得到某个对象的访问总数
     *
     * @param type     对象类型ID
     * @param objectId 对象ID
     * @return 此对象的访问总数
     * @throws Exception 数据库查询错误
     */
    @OQL("select visitCount from VisitTotal where type=:1 and objectId=:2")
    public abstract Integer getVisitTotal(Integer type, Integer objectId) throws Exception;

    /**
     * 提交访问记录到数据库中
     *
     * @param records 要提交的访问记录
     * @throws Exception 数据库操作错误
     */
    public void pushVisitRecords(List<VisitRecord> records) throws Exception
    {
        for (VisitRecord record : records)
            add(record);
    }

    /**
     * 提交访问数到数据库中
     *
     * @param totals 要提交的访问数
     * @throws Exception 数据库操作错误
     */
    public void pushVisitTotals(List<VisitTotal> totals) throws Exception
    {
        for (VisitTotal total : totals)
            save(total);
    }

    @GetByField(value = "visitName")
    public abstract Visit getVisit(String name) throws Exception;
}
