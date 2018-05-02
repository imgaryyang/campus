package com.gzzm.oa.diary;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author wxj
 * @date 11-10-27
 */
public abstract class DiaryDao extends GeneralDao
{
    public DiaryDao()
    {
    }

    public Diary getDiary(Integer diaryId) throws Exception
    {
        return load(Diary.class, diaryId);
    }

    @OQL("select d from Diary d where d.diaryId in :1 order by diaryTime,createTime asc")
    public abstract List<Diary> getDiaries(Integer... diaryId) throws Exception;

    public void deleteDiary(Integer diaryId) throws Exception
    {
        delete(Diary.class, diaryId);
    }

    /**
     * 查询某个用户在某个时间内的日志
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日志列表
     * @throws Exception 数据库异常
     */
    @OQL("select d from Diary d where userId=:1 and diaryTime>=?2 and diaryTime<=?3 order by diaryTime,createTime asc")
    public abstract List<Diary> getUserDiaries(Integer userId, Date startTime, Date endTime) throws Exception;

}
