package com.gzzm.oa.notice;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 内部信息相关的数据接口
 *
 * @author czf
 * @date 2010-3-16
 */
public abstract class NoticeDao extends GeneralDao
{
    public NoticeDao()
    {
    }

    public Dept getDept(Integer deptId) throws Exception
    {
        return load(Dept.class, deptId);
    }

    public Notice getNotice(Integer noticeId) throws Exception
    {
        return load(Notice.class, noticeId);
    }

    public NoticeTemplate getTemplate(Integer templateId) throws Exception
    {
        return load(NoticeTemplate.class, templateId);
    }

    public NoticeType getNoticeType(Integer typeId) throws Exception
    {
        return load(NoticeType.class, typeId);
    }

    public NoticeTrace getNoticeTrace(Integer noticeId, Integer userId) throws Exception
    {
        return load(NoticeTrace.class, noticeId, userId);
    }

    @OQL("select n from NoticeTemplate n order by n.orderId")
    public abstract List<NoticeTemplate> getAllNoticeTemplates() throws Exception;

    @OQL("select s from NoticeSort s where deptId in :1 order by dept.leftValue,s.orderId")
    public abstract List<NoticeSort> getSorts(Collection<Integer> deptId) throws Exception;

    @OQL("select n from NoticeType n where n.deptId=:1 and n.sortId=?2 order by n.orderId")
    public abstract List<NoticeType> getNoticeTypes(Integer deptId, Integer sortId) throws Exception;

    @OQL("select n from NoticeTrace n where n.noticeId=:1")
    public abstract List<NoticeTrace> getNoticeTraceList(Integer noticeId) throws Exception;

    @OQLUpdate("update Notice set publishTime=sysdate(),state=1 where noticeId in :1")
    public abstract void publish(Integer[] noticeIds) throws Exception;

    @OQLUpdate("update Notice set publishTime=null,state=0 where noticeId in :1")
    public abstract void cancelPublish(Integer[] noticeIds) throws Exception;

    /**
     * 第一次浏览内部信息时，在信息跟踪表插入一条记录
     *
     * @param noticeId 被阅读的信息ID
     * @param userId   阅读信息的用户
     * @throws Exception 数据库查询或写入错误
     */
    public void addNoticeTrace(Integer noticeId, Integer userId) throws Exception
    {
        NoticeTrace trace = getNoticeTrace(noticeId, userId);
        if (trace == null)
        {
            trace = new NoticeTrace();
            trace.setNoticeId(noticeId);
            trace.setUserId(userId);
            trace.setReadTime(new Date());
            add(trace);
        }
    }
}
