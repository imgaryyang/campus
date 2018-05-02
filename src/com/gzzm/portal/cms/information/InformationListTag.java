package com.gzzm.portal.cms.information;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.tag.EntityQueryTag;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 信息列表的标签
 *
 * @author camel
 * @date 2011-6-16
 */
@Tag(name = "info")
public class InformationListTag extends EntityQueryTag<Information, Long>
{
    @Inject
    private static Provider<InformationDao> daoProvider;

    @Inject
    private static Provider<ChannelTree> channelTreeProvider;

    /**
     * 栏目ID
     */
    @NotCondition
    private Integer channelId;

    /**
     * 发布部门
     */
    @NotCondition
    private Integer deptId;

    private Integer topDeptId;

    @Like
    private String title;

    @Like
    private String fileCode;

    @Like
    private String indexCode;

    @Lower(column = "publishTime")
    private Date time_start;

    @Upper(column = "publishTime")
    private Date time_end;

    private Integer catalogId;

    @Like
    private String source;

    @Contains
    private String text;

    /**
     * 排序方式，默认以更新时间排序
     */
    private String order = "topmost desc,i.orderId desc,publishTime desc,informationId desc";

    /**
     * 是否包括子栏目的文章
     */
    private boolean descendant;

    /**
     * 是否只查询有标题图片的文章
     */
    @NotCondition
    private boolean photo;

    /**
     * 仅查询本部门发布的数据
     */
    private boolean self;

    /**
     * 是否做查询
     */
    private boolean query;

    private String channelCodes;

    private Channel channel;

    private Map<String, Object> properties;

    private String[] words;

    private String where;

    private Long infoId;

    private InformationDao dao;

    private ChannelTree channelTree;

    private List<Integer> channelIds;

    private String channelName;

    private Map<Integer, Integer> linkChannelIdMap;

    public InformationListTag()
    {
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getTopDeptId()
    {
        return topDeptId;
    }

    public void setTopDeptId(Integer topDeptId)
    {
        this.topDeptId = topDeptId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getFileCode()
    {
        return fileCode;
    }

    public void setFileCode(String fileCode)
    {
        this.fileCode = fileCode;
    }

    public String getIndexCode()
    {
        return indexCode;
    }

    public void setIndexCode(String indexCode)
    {
        this.indexCode = indexCode;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }

    public boolean isDescendant()
    {
        return descendant;
    }

    public void setDescendant(boolean descendant)
    {
        this.descendant = descendant;
    }

    public boolean isPhoto()
    {
        return photo;
    }

    public void setPhoto(boolean photo)
    {
        this.photo = photo;
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }

    public boolean isQuery()
    {
        return query;
    }

    public void setQuery(boolean query)
    {
        this.query = query;
    }

    public String getChannelCodes()
    {
        return channelCodes;
    }

    public void setChannelCodes(String channelCodes)
    {
        this.channelCodes = channelCodes;
    }

    public String getWhere()
    {
        return where;
    }

    public void setWhere(String where)
    {
        this.where = where;
    }

    public Long getInfoId()
    {
        return infoId;
    }

    public void setInfoId(Long infoId)
    {
        this.infoId = infoId;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public List<Integer> getChannelIds() throws Exception
    {
        if (channelIds == null)
        {
            channelIds = new ArrayList<Integer>();
            ChannelTree channelTree = getChannelTree();

            if (!StringUtils.isEmpty(channelCodes))
            {
                for (String channelCode : channelCodes.split(","))
                {
                    ChannelCache channel = channelTree.getChannelByCode(channelCode);
                    if (channel != null)
                        getChannelIds(channel);
                }
            }
            else if (channelId != null)
            {
                getChannelIds(channelTree.getChannel(channelId));
            }
        }

        return channelIds;
    }

    private <T extends ChannelData> void getChannelIds(ChannelData<T> channel)
    {
        if (channel.getType() == ChannelType.link)
        {
            channelIds.add(channel.getLinkChannelId());
            putLinkChannelId(channel.getLinkChannelId(), channel.getChannelId());
        }
        else
        {
            channelIds.add(channel.getChannelId());

            List<T> linkChannels = channel.getLinkChannels();
            if (linkChannels != null && linkChannels.size() > 0)
            {
                for (T linkChannel : linkChannels)
                {
                    if (linkChannel.getDeleteTag() == null || linkChannel.getDeleteTag() == 0)
                    {
                        channelIds.add(linkChannel.getChannelId());
                        putLinkChannelId(linkChannel.getChannelId(), channel.getChannelId());
                    }
                }
            }
        }

        if (descendant)
        {
            List<T> children = channel.getChildren();
            if (children != null)
            {
                for (T child : children)
                {
                    if (child.getDeleteTag() == null || child.getDeleteTag() == 0)
                    {
                        getChannelIds(child);
                    }
                }
            }
        }
    }

    private InformationDao getDao()
    {
        if (dao == null)
            dao = daoProvider.get();

        return dao;
    }

    private ChannelTree getChannelTree()
    {
        if (channelTree == null)
            channelTree = channelTreeProvider.get();
        return channelTree;
    }

    private Channel getChannel() throws Exception
    {
        if (channel == null && channelId != null)
            channel = getDao().getChannel(channelId);

        return channel;
    }

    private void putLinkChannelId(Integer linkChannelId, Integer channelId)
    {
        if (linkChannelIdMap == null)
            linkChannelIdMap = new HashMap<Integer, Integer>();

        linkChannelIdMap.put(linkChannelId, channelId);
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public String[] getWords()
    {
        if (words == null && !StringUtils.isEmpty(text))
            words = text.split(" ");

        return words;
    }

    @Override
    public String getAlias()
    {
        return "i";
    }

    @Override
    public Object getValue(Map<String, Object> context) throws Exception
    {
        if (infoId != null)
        {
            InformationDao dao = getDao();

            Channel channel = null;
            if (channelId != null)
                channel = dao.getChannel(channelId);

            Information information = dao.getInformation(infoId);
            if (information == null)
            {
                return Collections.emptyList();
            }
            else
            {
                return new InformationInfo(information, channel);
            }
        }

        return super.getValue(context);
    }

    @Override
    protected void init(Map<String, Object> context) throws Exception
    {
        super.init(context);

        if (query && channelId != null)
        {
            for (InfoProperty property : getChannel().getAllInfoProperties())
            {
                String name = property.getPropertyName();

                switch (property.getDataType())
                {
                    case STRING:
                    {
                        Object value = context.get(name);
                        if (value != null)
                        {
                            if (StringUtils.isEmpty(property.getEnumValues()))
                                setProperty(name, "%" + value + "%");
                            else
                                setProperty(name, value);
                        }
                        break;
                    }
                    case INTEGER:
                    {
                        Object value = context.get(name);
                        if (value != null)
                        {
                            setProperty(name, DataConvert.convertType(Integer.class, value));
                        }
                        else if (!StringUtils.isEmpty(property.getEnumValues()))
                        {
                            Object start = context.get(name + "_start");
                            if (start != null)
                                setProperty(name + "_start", DataConvert.convertType(Integer.class, start));

                            Object end = context.get(name + "_end");
                            if (end != null)
                                setProperty(name + "_end", DataConvert.convertType(Integer.class, end));
                        }
                        break;
                    }
                    case NUMBER:
                    {
                        Object start = context.get(name + "_start");
                        if (start != null)
                            setProperty(name + "_start", DataConvert.convertType(Float.class, start));

                        Object end = context.get(name + "_end");
                        if (end != null)
                            setProperty(name + "_end", DataConvert.convertType(Float.class, end));

                        break;
                    }
                    case DATE:
                    {
                        Object start = context.get(name + "_start");
                        if (start != null)
                            setProperty(name + "_start", DataConvert.convertType(Date.class, start));

                        Object end = context.get(name + "_end");
                        if (end != null)
                            setProperty(name + "_end",
                                    DateUtils.addDate(DataConvert.convertType(Date.class, end), 1));

                        break;
                    }
                }
            }
        }
    }

    private void setProperty(String name, Object value)
    {
        if (properties == null)
            properties = new HashMap<String, Object>();

        properties.put(name, value);
    }

    @Override
    protected String getQueryString() throws Exception
    {
        StringBuilder buffer = new StringBuilder("select i from Information i");

        if (!self && topDeptId != null)
        {
            buffer.append(" join Dept d on dept.leftValue>=d.leftValue and dept.leftValue<d.rightValue");
        }

        buffer.append(" where valid=1 and (validTime is null or validTime>sysdate())");

        if (!StringUtils.isEmpty(channelName))
        {
            buffer.append(" and i.channel.channelName=:channelName");
        }
        else if (channelId != null || !StringUtils.isEmpty(channelCodes))
        {
            buffer.append(" and i.channelId in :channelIds");
        }

        if (self)
        {
            if (deptId != null)
                buffer.append(" and deptId=:deptId");
        }
        else if (topDeptId != null)
        {
            buffer.append(" and d.deptId=:topDeptId");
        }

        if (photo)
            buffer.append(" and (i.photo is not null or photoFilePath is not null)");

        if (!StringUtils.isEmpty(where))
            buffer.append(" and (").append(where).append(")");

        if (query)
        {
            String condition = createCondition();
            if (condition != null)
                buffer.append(" and ").append(condition);

            if (channelId != null && properties != null)
            {
                for (InfoProperty property : getChannel().getAllInfoProperties())
                {
                    String name = property.getPropertyName();

                    switch (property.getDataType())
                    {
                        case STRING:
                        {
                            if (properties.containsKey(name))
                            {
                                if (StringUtils.isEmpty(property.getEnumValues()))
                                    buffer.append(" and i.properties.").append(name).append(" like ?properties.")
                                            .append(name);
                                else
                                    buffer.append(" and i.properties.").append(name).append("=?properties.")
                                            .append(name);
                            }
                            break;
                        }
                        case INTEGER:
                        {
                            if (properties.containsKey(name))
                            {
                                buffer.append(" and i.properties.").append(name).append("=?properties.")
                                        .append(name);
                            }
                            else if (!StringUtils.isEmpty(property.getEnumValues()))
                            {
                                if (properties.containsKey(name + "_start"))
                                    buffer.append(" and i.properties.").append(name).append(">=?properties.")
                                            .append(name).append("_start");

                                if (properties.containsKey(name + "_end"))
                                    buffer.append(" and i.properties.").append(name).append("<=?properties.")
                                            .append(name).append("_end");
                            }
                            break;
                        }
                        case NUMBER:
                        case DATE:
                        {
                            if (properties.containsKey(name + "_start"))
                                buffer.append(" and i.properties.").append(name).append(">=?properties.")
                                        .append(name).append("_start");

                            if (properties.containsKey(name + "_end"))
                                buffer.append(" and i.properties.").append(name).append("<=?properties.")
                                        .append(name).append("_end");

                            break;
                        }
                    }
                }
            }
        }

        buffer.append(" order by ").append(order);

        return buffer.toString();
    }

    @Override
    protected Object transform(Information information) throws Exception
    {
        ChannelCache channel = null;
        Integer channelId = information.getChannelId();
        if (linkChannelIdMap != null)
        {
            Integer linkChannelId = linkChannelIdMap.get(channelId);
            if (linkChannelId != null)
                channel = getChannelTree().getChannel(linkChannelId);
        }

        InformationInfo informationInfo = new InformationInfo(information, channel);
        if (query && !StringUtils.isEmpty(text))
            informationInfo.setWords(getWords());

        return informationInfo;
    }
}
