package com.gzzm.portal.cms.station;

import com.gzzm.platform.commons.UpdateTimeService;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.PageCache;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 内容管理站点，一个系统可以建立多个站点
 *
 * @author camel
 * @date 2011-5-3
 */
@Entity(table = "PLSTATION", keys = "stationId")
public class Station
{
    @Generatable(length = 6)
    private Integer stationId;

    @Unique
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String stationName;

    /**
     * 站点标题，在站点网站主页上显示的标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 站点主栏目的ID
     */
    private Integer channelId;

    /**
     * 站点主栏目
     */
    @NotSerialized
    private Channel channel;

    /**
     * 站点所属的部门
     */
    @Index
    private Integer deptId;

    /**
     * 关联部门对象
     */
    @NotSerialized
    private Dept dept;

    /**
     * 域名
     */
    @Unique
    @ColumnDescription(type = "varchar(50)")
    private String domainName;

    /**
     * 繁体中文域名
     */
    @Unique
    @ColumnDescription(type = "varchar(50)")
    private String gb5Domain;

    /**
     * 站点路径
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String path;

    /**
     * 网站分类，分类的名字由用户自定义，只要名字相同的均在同一类
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    /**
     * 排序，定义站点在页面上显示时的顺序，仅当同一类的站点可以进行排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 站点的创建人的ID
     */
    private Integer creator;

    /**
     * 关联站点创建人的User对象
     */
    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 网站创建事件
     */
    private Date createTime;

    /**
     * 是否有效，true则有效，可以在网站上展示，false则无效，不能在网站上展示
     */
    @Require
    private Boolean valid;

    /**
     * 网站使用的模板，可以不使用模板，则为null
     */
    private Integer templateId;

    /**
     * 关联网站使用的模板对象
     */
    @NotSerialized
    private StationTemplate template;

    /**
     * 网站标记码
     */
    @ColumnDescription(type = "varchar(250)")
    private String siteIDCode;

    public Station()
    {
    }

    public Station(Integer stationId, String stationName)
    {
        this.stationId = stationId;
        this.stationName = stationName;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public String getStationName()
    {
        return stationName;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public StationTemplate getTemplate()
    {
        return template;
    }

    public void setTemplate(StationTemplate template)
    {
        this.template = template;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public String toString()
    {
        return stationName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDomainName()
    {
        return domainName;
    }

    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }

    public String getGb5Domain()
    {
        return gb5Domain;
    }

    public void setGb5Domain(String gb5Domain)
    {
        this.gb5Domain = gb5Domain;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public String getSiteIDCode()
    {
        return siteIDCode;
    }

    public void setSiteIDCode(String siteIDCode)
    {
        this.siteIDCode = siteIDCode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Station))
            return false;

        Station station = (Station) o;

        return stationId.equals(station.stationId);
    }

    @Override
    public int hashCode()
    {
        return stationId.hashCode();
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新站点缓存
        UpdateTimeService.updateLastTime("cms.station", new Date());

        //更新页面缓存
        PageCache.updateCache();
    }
}
