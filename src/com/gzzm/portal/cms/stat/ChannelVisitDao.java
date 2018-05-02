package com.gzzm.portal.cms.stat;

import com.gzzm.portal.cms.channel.Channel;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.sql.Date;
import java.util.List;

/**
 * @author sjy
 * @date 2017/5/23
 */
public abstract class ChannelVisitDao extends GeneralDao
{
    @OQL("select c.channelId as channelId,c.channelName as channelName,t.visitCount as visitCount from VisitTotal t,Channel c where t.type=1 and c.parentChannelId=?1 and t.objectId=c.channelId order by c.leftValue")
    public abstract List<ChannelVisitBean> queryChannelVisit(Integer parentChannelId);

    @OQL("select c from Channel c where c.parentChannelId=?1 and c.channelId<>0 order by c.leftValue")
    public abstract List<Channel> queryChannels(Integer parentChannelId);

    @OQL("select sum(v.visitCount) from VisitTotal v,Channel c,Channel cc where v.objectId=c.channelId and c.leftValue>=cc.leftValue and c.leftValue<cc.rightValue and cc.channelId=:1")
    public abstract long countChannelsVisitTotal(Integer parentChannelId);

    @OQL("select v.visitCount from VisitTotal v where v.objectId=:1 and v.type=799")
    public abstract Integer queryVisitTotal(Integer visitId);

    @OQLUpdate("update VisitTotal set visitCount=:1 where type=0 and objectId=:2 ")
    public abstract int updateStationVisitTotal(Integer visitCount, Integer stationId) throws Exception;

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
     * 根据访问日期统计站点访问量
     *
     * @param type       对象类型Id,站点时为0
     * @param objectId   对象ID,即站点id
     * @param time_start 开始日期
     * @param time_end   结束日期
     * @return 站点访问总量
     * @throws Exception
     */
    @OQL("select count(*) from VisitRecord  where type=:1 and objectId=:2  and visitTime >= ?3 and visitTime <= ?4")
    public abstract Integer getVisitTotalByRecord(Integer type, Integer objectId, Date time_start, Date time_end)
            throws Exception;

    /**
     * 根据访问日期统计栏目访问量
     *
     * @param type            对象类型Id,栏目时为1
     * @param parentChannelId 对象ID,即栏目id
     * @param time_start      开始日期
     * @param time_end        结束日期
     * @return 栏目访问总量
     * @throws Exception
     */
    @OQL("select sum(v.visitCount) from VisitRecord v,Channel c,Channel cc where v.objectId=c.channelId and v.type=:1 and c.leftValue>=cc.leftValue and c.leftValue<cc.rightValue and cc.channelId=:2 and v.visitTime >= ?3 and v.visitTime <= ?4")
    public abstract long countChannelsVisitTotalByDate(Integer type, Integer parentChannelId, Date time_start,
                                                       Date time_end) throws Exception;

}
