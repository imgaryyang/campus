package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.UpdateTimeService;
import com.gzzm.portal.cms.commons.*;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.Unique;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 栏目，频道
 *
 * @author camel
 * @date 2011-3-2
 */
@Entity(table = "PLCHANNEL", keys = "channelId")
public class Channel implements ChannelData<Channel>
{
    @Inject
    private static Provider<ChannelDao> daoProvider;


    /**
     * 栏目id，主键
     */
    @Generatable(length = 8)
    private Integer channelId;

    /**
     * 父栏目的id
     */
    @Index
    private Integer parentChannelId;

    /**
     * 关联父栏目对象
     */
    @NotSerialized
    @ToOne("PARENTCHANNELID")
    private Channel parentChannel;

    /**
     * 栏目名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String channelName;

    /**
     * 栏目编号，用于标签中使用，唯一性，可以通过栏目编号唯一定位栏目，但是栏目编号是可变的，而栏目id不可变
     * 栏目编号的规则是每一层栏目为三位编号，下一级栏目的编号必须是上一级栏目的编号为前缀
     */
    @Unique
    @Index(unique = true)
    @ColumnDescription(type = "varchar(60)")
    private String channelCode;

    /**
     * 删除标记，0为未删除，1为删除
     */
    @ColumnDescription(type = "tinyint(1)", nullable = false, defaultValue = "0")
    private Byte deleteTag;

    /**
     * 左值，用于支持nested set结构
     *
     * @see net.cyan.crud.NestedSetTreeOrganizer
     */
    @Index
    @ColumnDescription(type = "number(8)")
    private Integer leftValue;

    /**
     * 右值，用于支持nested set结构
     *
     * @see net.cyan.crud.NestedSetTreeOrganizer
     */
    @ColumnDescription(type = "number(8)")
    private Integer rightValue;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 栏目类型，信息采编或者链接其他栏目，或者链接url
     */
    @Require
    private ChannelType type;

    /**
     * 链接的目标，当type为url和page时有效，标识链接的url
     */
    @ColumnDescription(type = "varchar(200)")
    private String linkUrl;

    /**
     * 链接的栏目的ID,当type为link时有效
     */
    private Integer linkChannelId;

    /**
     * 链接的栏目的对象
     */
    @NotSerialized
    @ToOne("LINKCHANNELID")
    private Channel linkChannel;

    /**
     * 访问此栏目的url
     */
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 栏目图片
     */
    private byte[] photo;

    /**
     * 栏目模版
     */
    private Integer channelTemplateId;

    /**
     * 文章模版
     */
    private Integer infoTemplateId;

    /**
     * 关联栏目模版对象
     */
    @NotSerialized
    @ToOne("CHANNELTEMPLATEID")
    private PageTemplate channelTemplate;

    /**
     * 关联文章模版对象
     */
    @NotSerialized
    @ToOne("INFOTEMPLATEID")
    private PageTemplate infoTemplate;

    /**
     * 栏目所属的站点的ID
     */
    @NotSerialized
    @ComputeColumn("min(select s.stationId from com.gzzm.portal.cms.station.Station s where " +
            "s.channel.leftValue<=this.leftValue and s.channel.rightValue>this.leftValue and s.channel.deleteTag=0" +
            " order by s.channel.leftValue desc)")
    private Integer stationId;

    @OrderBy(column = "LEFTVALUE")
    @OneToMany("PARENTCHANNELID")
    @NotSerialized
    private List<Channel> childChannels;

    /**
     * 栏目的扩展属性，根据系统的实际需要扩展不同的属性
     */
    @NotSerialized
    @ValueMap(table = "PLCHANNELPROPERTY", keyColumn = "PROPERTYNAME", valueColumn = "PROPERTYVALUE")
    private Map<String, String> properties;

    @NotSerialized
    @OrderBy(column = "ORDERID")
    @OneToMany
    private List<InfoProperty> infoProperties;

    /**
     * 最后的更新时间
     */
    private Date lastModified;

    /**
     * 索引号编码规则
     *
     * @see com.gzzm.platform.wordnumber.WordNumber
     */
    private String indexCode;

    /**
     * 是否自动生成索引
     */
    private Boolean generateIndex;

    /**
     * 是否继承父类数据
     */
    private Boolean propertyInherited;

    /**
     * 重新发布时是否重新生成排序号
     */
    private Boolean reGenerateOrder;

    /**
     * 手动调整发布时间时，根据发布时间生成排序号
     */
    private Boolean genOrderByTime;

    /**
     * 是否允许修改发布时间
     */
    private Boolean publishTimeEditable;

    /**
     * 信息采编的页面路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String page;

    /**
     * 信息组件类型，为一个java类的全名，此java类实现InformationFlowComponent，为增加扩展功能
     *
     * @see com.gzzm.portal.cms.information.InformationComponent
     */
    @ColumnDescription(type = "varchar(250)")
    private String componentType;

    /**
     * 发布周期，每星期发布一次，每月发布一次，或者每季度发布一次，或者每半年发布一次，或者每年发布一次
     */
    private PublishPeriod period;

    /**
     * 栏目关键字
     */
    @ColumnDescription(type = "varchar(250)")
    private String keywords;

    /**
     * 栏目说明
     */
    @ColumnDescription(type = "varchar(500)")
    private String remark;

    /**
     * 栏目是否已归档，默认不归档
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean cataloged;

    /**
     * 关联的栏目，当在这些栏目发布信息，同时也发布一份到此栏目
     */
    @NotSerialized
    @ManyToMany(table = "PLCHANNELRELATED", joinColumn = "CHANNELID", reverseJoinColumn = "RELATEDCHANNELID")
    private List<Channel> relatedChannels;

    /**
     * 关联的栏目，当在此栏目发布信息，同时也发布一份到相关的栏目
     */
    @NotSerialized
    @ManyToMany(table = "PLCHANNELRELATED", joinColumn = "RELATEDCHANNELID", reverseJoinColumn = "CHANNELID")
    private List<Channel> relatedChannels2;

    /**
     * 关联的栏目，当在这些栏目发布信息，同时也在此栏目显示
     */
    @NotSerialized
    @ManyToMany(table = "PLCHANNELLINKS", joinColumn = "CHANNELID", reverseJoinColumn = "LINKCHANNELID")
    private List<Channel> linkChannels;

    public Channel()
    {
    }

    public Channel(Integer channelId, String channelName)
    {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public Boolean getCataloged()
    {
        return cataloged;
    }

    public void setCataloged(Boolean cataloged)
    {
        this.cataloged = cataloged;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public Integer getParentChannelId()
    {
        return parentChannelId;
    }

    public void setParentChannelId(Integer parentChannelId)
    {
        this.parentChannelId = parentChannelId;
    }

    public Channel getParentChannel()
    {
        return parentChannel;
    }

    public void setParentChannel(Channel parentChannel)
    {
        this.parentChannel = parentChannel;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public String getChannelCode()
    {
        return channelCode;
    }

    public void setChannelCode(String channelCode)
    {
        this.channelCode = channelCode;
    }

    public Byte getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Byte deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getLeftValue()
    {
        return leftValue;
    }

    public void setLeftValue(Integer leftValue)
    {
        this.leftValue = leftValue;
    }

    public Integer getRightValue()
    {
        return rightValue;
    }

    public void setRightValue(Integer rightValue)
    {
        this.rightValue = rightValue;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public ChannelType getType()
    {
        return type;
    }

    public void setType(ChannelType type)
    {
        this.type = type;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    /**
     * 由于之前linkUrl使用的字段名是link，并且在某些地方被调用，
     * 修改字段之后保留原来的get方法
     * 之所以修改字段是因为link在某些数据库下是关键字
     *
     * @return linkUrl
     */
    @Transient
    public String getLink()
    {
        return getLinkUrl();
    }

    public Integer getLinkChannelId()
    {
        return linkChannelId;
    }

    public void setLinkChannelId(Integer linkChannelId)
    {
        this.linkChannelId = linkChannelId;
    }

    public Channel getLinkChannel()
    {
        return linkChannel;
    }

    public void setLinkChannel(Channel linkChannel)
    {
        this.linkChannel = linkChannel;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto(byte[] photo)
    {
        this.photo = photo;
    }

    public Integer getChannelTemplateId()
    {
        return channelTemplateId;
    }

    public void setChannelTemplateId(Integer channelTemplateId)
    {
        this.channelTemplateId = channelTemplateId;
    }

    public Integer getInfoTemplateId()
    {
        return infoTemplateId;
    }

    public void setInfoTemplateId(Integer infoTemplateId)
    {
        this.infoTemplateId = infoTemplateId;
    }

    public PageTemplate getChannelTemplate()
    {
        return channelTemplate;
    }

    public void setChannelTemplate(PageTemplate channelTemplate)
    {
        this.channelTemplate = channelTemplate;
    }

    public PageTemplate getInfoTemplate()
    {
        return infoTemplate;
    }

    public void setInfoTemplate(PageTemplate infoTemplate)
    {
        this.infoTemplate = infoTemplate;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public List<Channel> getChildChannels()
    {
        return childChannels;
    }

    @NotSerialized
    public List<Channel> getChildren()
    {
        return getChildChannels();
    }

    public void setChildChannels(List<Channel> childChannels)
    {
        this.childChannels = childChannels;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public List<InfoProperty> getInfoProperties()
    {
        return infoProperties;
    }

    public void setInfoProperties(List<InfoProperty> infoProperties)
    {
        this.infoProperties = infoProperties;
    }

    @NotSerialized
    public List<InfoProperty> getAllInfoProperties()
    {
        List<InfoProperty> allProperties = new ArrayList<InfoProperty>();

        if (isPropertyInherited() != null && isPropertyInherited())
        {
            Channel parentChannel = getParentChannel();
            if (parentChannel != null)
                allProperties.addAll(parentChannel.getAllInfoProperties());
        }


        List<InfoProperty> infoProperties = getInfoProperties();
        if (infoProperties != null)
            allProperties.addAll(infoProperties);

        return allProperties;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public String getIndexCode()
    {
        return indexCode;
    }

    public void setIndexCode(String indexCode)
    {
        this.indexCode = indexCode;
    }

    /**
     * 获得有效的索引号，如果本栏目没有设置索引号，则寻找上一级栏目的索引号
     *
     * @return 有效的索引号
     */
    @NotSerialized
    public String getValidIndexCode()
    {
        if (StringUtils.isEmpty(indexCode))
        {
            Channel parentChannel = getParentChannel();
            if (parentChannel != null)
                return parentChannel.getValidIndexCode();
        }

        return indexCode;
    }

    public Boolean isGenerateIndex()
    {
        return generateIndex;
    }

    public void setGenerateIndex(Boolean generateIndex)
    {
        this.generateIndex = generateIndex;
    }

    public Boolean isPropertyInherited()
    {
        return propertyInherited;
    }

    public void setPropertyInherited(Boolean propertyInherited)
    {
        this.propertyInherited = propertyInherited;
    }

    public Boolean isReGenerateOrder()
    {
        return reGenerateOrder;
    }

    public void setReGenerateOrder(Boolean reGenerateOrder)
    {
        this.reGenerateOrder = reGenerateOrder;
    }

    public Boolean isGenOrderByTime()
    {
        return genOrderByTime;
    }

    public void setGenOrderByTime(Boolean genOrderByTime)
    {
        this.genOrderByTime = genOrderByTime;
    }

    public Boolean isPublishTimeEditable()
    {
        return publishTimeEditable;
    }

    public void setPublishTimeEditable(Boolean publishTimeEditable)
    {
        this.publishTimeEditable = publishTimeEditable;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public String getComponentType()
    {
        return componentType;
    }

    public void setComponentType(String componentType)
    {
        this.componentType = componentType;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    public List<Channel> getRelatedChannels()
    {
        return relatedChannels;
    }

    public void setRelatedChannels(List<Channel> relatedChannels)
    {
        this.relatedChannels = relatedChannels;
    }

    public List<Channel> getRelatedChannels2()
    {
        return relatedChannels2;
    }

    public void setRelatedChannels2(List<Channel> relatedChannels2)
    {
        this.relatedChannels2 = relatedChannels2;
    }

    public List<Channel> getLinkChannels()
    {
        return linkChannels;
    }

    public void setLinkChannels(List<Channel> linkChannels)
    {
        this.linkChannels = linkChannels;
    }

    /**
     * 获得栏目的完整路径名，将栏目的名称和栏目父栏目的名称串起来
     *
     * @return 栏目完整的路径名
     */
    @NotSerialized
    public String getChannelPathName()
    {
        if (channelId != null)
        {
            StringBuilder buffer = new StringBuilder();

            buffer.append(channelName);

            Channel channel = getParentChannel();
            if (channel != null)
            {
                while (channel.getChannelId() != 0)
                {
                    buffer.insert(0, "-").insert(0, channel.getChannelName());

                    channel = channel.getParentChannel();
                }
            }

            return buffer.toString();
        }
        return null;
    }

    /**
     * 要求栏目编号必须是以上级栏目编号开头
     *
     * @return 错误信息
     * @throws Exception 数据库查询错误
     */
    @FieldValidator("channelCode")
    @Warning("portal.cms.channelCode_error")
    public Channel checkCode() throws Exception
    {
        if (channelCode != null)
        {
            if (getParentChannelId() == null)
                return null;

            Channel parentChannel = daoProvider.get().getChannel(getParentChannelId());
            String parentChannelCode = parentChannel.getChannelCode();

            if (parentChannelCode == null)
                parentChannelCode = "";

            if (!channelCode.startsWith(parentChannelCode))
                return parentChannel;

            if (channelCode.length() != parentChannelCode.length() + 3)
            {
                if (parentChannel.getChannelCode() == null)
                    parentChannel.setChannelCode("");
                return parentChannel;
            }

            try
            {
                //noinspection ResultOfMethodCallIgnored
                Long.parseLong(channelCode);
            }
            catch (Throwable ex)
            {
                //解析整数错误，栏目编码错误
                return parentChannel;
            }
        }

        return null;
    }

    public boolean containsChildChannel(Collection<Integer> channelIds) throws Exception
    {
        List<Channel> childChannels = getChildChannels();
        if (childChannels != null)
        {
            for (Channel channel : childChannels)
            {
                if (channelIds == null || channelIds.contains(channel.getChannelId()) ||
                        channel.containsChildChannel(channelIds))
                    return true;
            }
        }

        return false;
    }

    public boolean containsChildChannel(Integer channelId) throws Exception
    {
        List<Channel> childChannels = getChildChannels();
        if (childChannels != null)
        {
            for (Channel channel : childChannels)
            {
                if (channel.getChannelId().equals(channelId) || channel.containsChildChannel(channelId))
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Channel))
            return false;

        Channel channel = (Channel) o;

        return channelId.equals(channel.channelId);
    }

    @Override
    public int hashCode()
    {
        return channelId.hashCode();
    }

    @Override
    public String toString()
    {
        return channelName;
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeSave() throws Exception
    {
        setLastModified(new Date());
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新栏目缓存
        UpdateTimeService.updateLastTime("cms.channel", new Date());

        //更新页面缓存
        PageCache.updateCache();
    }
}
