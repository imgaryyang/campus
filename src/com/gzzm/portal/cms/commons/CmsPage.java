package com.gzzm.portal.cms.commons;

import com.gzzm.platform.organ.*;
import com.gzzm.platform.visit.VisitService;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.cms.station.*;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.integration.RemoteAddr;

import java.util.List;

/**
 * 显示页面，包括栏目，主页，其他页面和文章
 * 提供一些和页面相关的基础方法，方便在各种页面上调用
 *
 * @author camel
 * @date 2011-6-9
 */
@Service
@Default("show")
public abstract class CmsPage
{
    public static final int STATION = 0;

    public static final int CHANNEL = 1;

    public static final int PAGE = 2;

    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private static Provider<VisitService> visitServiceProvider;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 栏目信息，当前正在访问的栏目的信息，
     * 如果是访问某个栏目则为访问的栏目的信息，
     * 如果是访问某篇文章，则为这篇文章所属的栏目的信息
     * 如果是访问网站主页或者其他页面，则是当前网站的主栏目信息
     */
    protected ChannelInfo channel;

    /**
     * 网站信息，当前正在访问的文章，栏目，页面所在的网站的信息
     */
    protected StationInfo station;

    /**
     * 部门信息
     */
    protected SimpleDeptInfo dept;

    /**
     * 客户端ip地址
     */
    @RemoteAddr
    private String ip;

    public CmsPage()
    {
    }

    @NotSerialized
    public Integer getDeptId() throws Exception
    {
        if (deptId == null)
        {
            StationInfo station = getStation();
            if (station != null)
                deptId = station.getDeptId();
        }

        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    protected abstract Station getStationObject() throws Exception;

    protected abstract Channel getChannelObject() throws Exception;

    protected abstract PageTemplate getPageTemplate() throws Exception;

    /**
     * 访问记录类型
     *
     * @return 0为网站，1为栏目，2为页面，-1表示不记录访问记录
     * @throws Exception 允许子类抛出异常
     */
    protected abstract int getVisitType() throws Exception;

    /**
     * 访问记录ID
     *
     * @return 当visitType为0时是网站ID，当visitType是1时是栏目ID，当visitType是2时是页面ID
     * @throws Exception 允许子类抛出异常
     */
    protected abstract Integer getVisitId() throws Exception;

    @NotSerialized
    public ChannelInfo getChannel() throws Exception
    {
        if (channel == null)
        {
            Channel channelObject = getChannelObject();
            if (channelObject != null)
                channel = new ChannelInfo(channelObject);
        }

        return channel;
    }

    @NotSerialized
    public StationInfo getStation() throws Exception
    {
        if (station == null)
        {
            Station stationObject = getStationObject();
            if (stationObject != null)
                station = new StationInfo(stationObject);
        }

        return station;
    }

    @NotSerialized
    public SimpleDeptInfo getDept() throws Exception
    {
        if (dept == null)
        {
            Integer deptId = getDeptId();
            if (deptId != null)
                dept = deptServiceProvider.get().getDept(deptId);
        }
        return dept;
    }

    /**
     * 网站根目录的栏目id
     *
     * @return 网站根目录的栏目id
     * @throws Exception 从数据库获取站点信息错误
     */
    @NotSerialized
    public Integer getStationChannelId() throws Exception
    {
        return getStationObject().getChannelId();
    }

    @NotSerialized
    public List<ChannelInfo> getChannels() throws Exception
    {
        return getChannel().getChannels();
    }

    /**
     * 转向要显示的页面
     *
     * @return 要显示的页面，如果是站点，转向主页，如果是栏目，转向栏目模版，如果是文章，转向文章模版
     * @throws Exception 获取模版错误
     */
    @Service
    public String show() throws Exception
    {
        int visitType = getVisitType();
        if (visitType >= 0)
        {
            //记录访问记录
            visitServiceProvider.get().visit(visitType, getVisitId(), null, ip);
        }

        return getForward();
    }

    /**
     * 获得转向的页面的路径
     *
     * @return 转向的页面的路径
     * @throws Exception 从数据库查询页面信息错误
     */
    protected String getForward() throws Exception
    {
        PageTemplate template = getPageTemplate();

        if (template == null)
            return null;

        return template.getPath();
    }

    public static CmsPage getPage()
    {
        Object form = RequestContext.getContext().getForm();
        if (form instanceof CmsPage)
            return (CmsPage) form;

        return null;
    }

    public static String getServerName()
    {
        return RequestContext.getContext().getRequest().getServerName();
    }
}
