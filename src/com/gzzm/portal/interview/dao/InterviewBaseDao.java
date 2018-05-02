package com.gzzm.portal.interview.dao;

import com.gzzm.portal.interview.entity.InterviewType;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 在线访谈-公共dao
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
public abstract class InterviewBaseDao extends GeneralDao {

    @OQL("select t from InterviewType t order by orderId")
    public abstract List<InterviewType> getInterviewTypes() throws Exception;
}
