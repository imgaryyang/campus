package com.gzzm.portal.interview.dao;

import com.gzzm.portal.interview.entity.GuestDiscuss;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 留言信息dao
 *
 * @author lishiwei
 * @date 2016/8/1.
 */
public abstract class DiscussDao extends GeneralDao {

    public DiscussDao() {
    }

    /**
     * 查询主持人/嘉宾发言信息
     * @param interviewId 在线访谈ID
     * @throws Exception SQL异常
     */
    @OQL("select d from GuestDiscuss d where d.interviewId = ?1 and d.guestType!=2 order by createTime desc")
    public abstract List<GuestDiscuss> getGuestDiscusses(Integer interviewId) throws Exception;

    /**
     * 查询网友留言信息
     * @param interviewId 在线访谈信息ID
     * @throws Exception SQL异常
     */
    @OQL("select d from GuestDiscuss d where d.interviewId = ?1 and d.guestType=2 and state=1 order by createTime desc")
    public abstract List<GuestDiscuss> getVisitorDiscusses(Integer interviewId) throws Exception;
}
