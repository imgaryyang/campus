package com.gzzm.portal.cms.channel;

import com.gzzm.portal.cms.commons.PublishPeriod;
import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.commons.util.StringUtils;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 栏目维护相关的dao
 *
 * @author camel
 * @date 2011-3-2
 */
public abstract class ChannelDao extends GeneralDao
{
    public ChannelDao()
    {
    }

    public Channel getChannel(Integer channelId) throws Exception
    {
        return load(Channel.class, channelId);
    }

    public Channel getRootChannel() throws Exception
    {
        Channel channel = getChannel(0);
        if (channel == null)
        {
            channel = new Channel();
            channel.setChannelId(0);
            channel.setChannelName("根节点");
            channel.setChannelCode("");
            channel.setDeleteTag((byte) 0);
            channel.setType(ChannelType.information);
            channel.setLeftValue(0);
            channel.setRightValue(1);
            add(channel);
        }

        return channel;
    }

    public Station getStation(Integer stationId) throws Exception
    {
        return load(Station.class, stationId);
    }

    /**
     * 获得某个栏目的编号
     *
     * @param channelId 栏目ID
     * @return 栏目编号
     * @throws Exception 数据库查询错误
     */
    @OQL("select channelCode from Channel where channelId=:1")
    public abstract String getChannelCode(Integer channelId) throws Exception;

    /**
     * 通过栏目编码获得栏目
     *
     * @param channelCode 栏目编码
     * @return 栏目对象
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Channel c where channelCode=:1 and deleteTag=0")
    public abstract Channel getChannelByCode(String channelCode) throws Exception;

    /**
     * 通过栏目编码获得栏目Id
     *
     * @param channelCode 栏目编码
     * @return 栏目Id
     * @throws Exception 数据库查询错误
     */
    @OQL("select channelId from Channel c where channelCode=:1 and deleteTag=0")
    public abstract Integer getChannelIdByCode(String channelCode) throws Exception;

    /**
     * 获得某个栏目的子栏目的最大编码，用于新建栏目时
     *
     * @param parentChannelId 栏目ID
     * @return 最大编码
     * @throws Exception 数据库查询错误
     */
    @OQL("select max(channelCode) from Channel where parentChannelId=:1")
    public abstract String getMaxChannelCode(Integer parentChannelId) throws Exception;

    /**
     * 获得所有子孙栏目的ID
     *
     * @param channelId 栏目ID，返回的结果为此栏目的所有子孙栏目的ID
     * @return channelId所代表的栏目的所有子孙栏目的ID
     * @throws Exception 数据库查询错误
     */
    @OQL("select c.channelId from Channel c join Channel c0 " +
            "on c.leftValue>=c0.leftValue and c.leftValue<c0.rightValue where c0.channelId=:1 and c.deleteTag=0 " +
            "order by c.leftValue")
    public abstract List<Integer> getDescendantChannelIds(Integer channelId) throws Exception;

    /**
     * 更新子孙栏目的编号
     *
     * @param oldChannelCode 旧的栏目编号
     * @param newChannelCode 新的栏目编号
     * @throws Exception 数据库更新数据错误
     */
    @OQLUpdate("update Channel set channelCode=:2||substring(channelCode,length(:1)+1)" +
            " where channelCode like :1||'%'")
    public abstract void updateDescendantChannelCode(String oldChannelCode, String newChannelCode) throws Exception;

    @OQL("select c,[c.properties] from Channel c where deleteTag=0 order by leftValue")
    public abstract List<Channel> getAllChannels() throws Exception;

    /**
     * 获得所有需要做url映射的栏目
     *
     * @return 定义了url的栏目列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Channel c where notempty(url) and c.deleteTag=0")
    public abstract List<Channel> getChannelsForUrlMap() throws Exception;

    /**
     * 根据发布频次获得某个栏目的所有没有被删除的子栏目
     *
     * @param channelId 父栏目ID
     * @param period    发布频次
     * @return 所有没有被删除的子栏目，按leftValue排序
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Channel c where parentChannelId=:1 and deleteTag=0  and period=?2 order by leftValue")
    public abstract List<Channel> getChildChannels(Integer channelId, PublishPeriod period) throws Exception;

    /**
     * 根据发布频次获得某个栏目的所有没有被删除的子孙栏目
     *
     * @param channelId 祖先目ID
     * @param period    发布频次
     * @return 所有没有被删除的子栏目，按leftValue排序
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Channel c join Channel c0 on c.leftValue>c0.leftValue and c.leftValue<c0.rightValue " +
            "where c0.channelId=:1 and c.deleteTag=0 and c.period=?2 order by c.leftValue")
    public abstract List<Channel> getDescendantChannels(Integer channelId, PublishPeriod period) throws Exception;

    public String initChannelCode(Integer parentChannelId) throws Exception
    {
        String channelCode = getMaxChannelCode(parentChannelId);

        if (channelCode == null)
        {
            //还没有子栏目，在父栏目的编号后面加001为子栏目的编号
            String parentChannelCode = getChannelCode(parentChannelId);

            if (parentChannelCode == null)
                parentChannelCode = "";

            return parentChannelCode + "001";
        }
        else
        {
            //取掉最后三位，前面的为上级栏目的编号
            String parentChannelCode = channelCode.substring(0, channelCode.length() - 3);

            //取最后三位+1接到上级栏目编号后面即为新栏目的编号
            String code = channelCode.substring(channelCode.length() - 3);

            code = Integer.toString(Integer.parseInt(code) + 1);

            code = StringUtils.leftPad(code, 3, '0');

            return parentChannelCode + code;
        }
    }

    public PageTemplate getPageTemplate(Integer templateId) throws Exception
    {
        return load(PageTemplate.class, templateId);
    }
}
