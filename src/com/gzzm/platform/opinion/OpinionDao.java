package com.gzzm.platform.opinion;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 意见维护相关的dao
 *
 * @author camel
 * @date 2014/12/1
 */
public abstract class OpinionDao extends GeneralDao
{
    public OpinionDao()
    {
    }

    @GetByField({"userId", "content"})
    public abstract Opinion getOpinionByContent(Integer userId, String content) throws Exception;

    /**
     * 更新常用意见的使用频率
     *
     * @param userId    用户ID
     * @param content   意见内容
     * @param frequency 使用频率
     * @return 如果常用意见存在，则返回1，否则返回0
     * @throws Exception 数据库错误
     */
    @OQLUpdate("update Opinion set frequency=frequency+:3 where userId=:1 and content=:2")
    public abstract int updateFrequency(Integer userId, String content, int frequency) throws Exception;

    @LoadByKey
    public abstract OpinionConfig getOpinionConfig(Integer userId);

    @OQL("select o from Opinion o where userId=:1 order by orderId,frequency desc limit :2")
    public abstract List<Opinion> getOpinions(Integer userId, int count) throws Exception;
}
