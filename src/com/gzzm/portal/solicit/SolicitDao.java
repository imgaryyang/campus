package com.gzzm.portal.solicit;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;
import java.util.List;

/**
 * @author lk
 * @date 13-10-18
 */
public abstract class SolicitDao extends GeneralDao
{
	@OQL("select t from SolicitType t where t.stationId = :1 order by orderId")
	public abstract List<SolicitType> getSolicitTypes(int stationId);

	@OQLUpdate("update SolicitReply set state=:1 , checkContent=:3 , checkId=:4 ,checkTime=:5 where replyId=:2")
	public abstract Integer updateReply(Integer state, Integer replyId, char[] checkContent, Integer checkId, Date checkTime);
}
