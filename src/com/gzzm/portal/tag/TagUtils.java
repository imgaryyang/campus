package com.gzzm.portal.tag;

import com.gzzm.portal.cms.channel.ChannelInfo;
import com.gzzm.portal.cms.commons.CmsPage;
import com.gzzm.portal.cms.station.StationInfo;
import net.cyan.commons.util.*;

import java.util.Map;

/**
 * @author camel
 * @date 2011-6-15
 */
public final class TagUtils
{
    private TagUtils()
    {
    }

    /**
     * 得到当前上下文下的站点ID
     *
     * @param context 上下文信息
     * @return 站点ID
     * @throws Exception 获取数据错误，一般是数据库读取数据错误
     */
    public static Integer getStationId(Map<String, Object> context) throws Exception
    {
        Object value = context.get("stationId");
        if ("null".equals(value))
            return null;

        Integer stationId = DataConvert.convertType(Integer.class, value);

        if (stationId == null)
            stationId = getStationId();

        return stationId;
    }

    public static Integer getStationId() throws Exception
    {
        CmsPage cmsPage = CmsPage.getPage();

        if (cmsPage != null)
        {
            StationInfo station = cmsPage.getStation();

            if (station != null)
                return station.getId();
        }

        return null;
    }

    /**
     * 得到当前上下文下的栏目ID
     *
     * @param context 上下文信息
     * @return 栏目ID
     * @throws Exception 获取数据错误，一般是数据库读取数据错误
     */
    public static Integer getChannelId(Map<String, Object> context) throws Exception
    {
        Integer channelId = DataConvert.convertType(Integer.class, context.get("channelId"));

        return channelId == null ? getChannelId0(context) : channelId;
    }

    public static Integer getChannelId(String channelCode) throws Exception
    {
        return ChannelInfo.getChannelId(channelCode);
    }

    static Integer getChannelId0(Map<String, Object> context) throws Exception
    {
        String channelCode = StringUtils.toString(context.get("channelCode"));
        if (!StringUtils.isEmpty(channelCode))
        {
            return getChannelId(channelCode);
        }
        else
        {
            CmsPage cmsPage = CmsPage.getPage();

            if (cmsPage != null)
            {
                ChannelInfo channel = cmsPage.getChannel();

                if (channel != null)
                    return channel.getChannelId();
            }
        }

        return null;
    }

    public static ChannelInfo getChannel(Map<String, Object> context) throws Exception
    {
        Object object = context.get("channel");
        if (object instanceof ChannelInfo)
            return (ChannelInfo) object;

        Integer channelId = getChannelId(context);

        if (channelId != null)
        {
            CmsPage cmsPage = CmsPage.getPage();

            if (cmsPage != null)
            {
                ChannelInfo channel = cmsPage.getChannel();
                if (channel != null && channel.getChannelId().equals(channelId))
                    return channel;
            }

            return ChannelInfo.get(channelId);
        }

        return null;
    }

    /**
     * 得到当前上下文的部门ID
     *
     * @param context 当前上下文信息
     * @return 部门ID
     * @throws Exception 从数据库获得站点信息错误
     */
    public static Integer getDeptId(Map<String, Object> context) throws Exception
    {
        Object value = context.get("deptId");
        if ("null".equals(value))
            return null;

        Integer deptId = DataConvert.convertType(Integer.class, value);

        if (deptId == null)
        {
            deptId = getDeptId();
        }

        return deptId;
    }

    public static Integer getDeptId() throws Exception
    {
        CmsPage cmsPage = CmsPage.getPage();

        if (cmsPage != null)
            return cmsPage.getDeptId();

        return null;
    }
}