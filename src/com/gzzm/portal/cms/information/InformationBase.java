package com.gzzm.portal.cms.information;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.Dept;
import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.PublishPeriod;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.ext.TextExtractors;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author camel
 * @date 2014/8/5
 */
public abstract class InformationBase<C extends InformationContentBase, F extends InformationFileBase>
        extends InformationBase0<C>
{
    /**
     * 信息标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 信息副标题
     */
    @ColumnDescription(type = "varchar(250)")
    private String subTitle;

    /**
     * 所属栏目ID
     */
    @Index
    private Integer channelId;

    /**
     * 关联所属栏目
     */
    @NotSerialized
    private Channel channel;

    /**
     * 信息类型
     */
    private InformationType type;

    /**
     * 链接，仅当type为url时才有效
     */
    @ColumnDescription(type = "varchar(500)")
    private String linkUrl;

    /**
     * 关键字
     */
    @ColumnDescription(type = "varchar(250)")
    private String keywords;

    /**
     * 来源
     */
    @ColumnDescription(type = "varchar(250)")
    private String source;

    /**
     * 作者
     */
    @ColumnDescription(type = "varchar(250)")
    private String author;

    /**
     * 文件编号
     */
    @ColumnDescription(type = "varchar(250)")
    private String fileCode;

    /**
     * 索引号
     */
    @ColumnDescription(type = "varchar(250)")
    private String indexCode;

    /**
     * 主题词
     */
    @ColumnDescription(type = "varchar(500)")
    private String subject;

    /**
     * 摘要
     */
    @ColumnDescription(type = "varchar(4000)")
    private String summary;

    @ColumnDescription(type = "varchar(50)")
    private String orgCode;

    @ColumnDescription(type = "varchar(250)")
    private String orgName;

    private Integer catalogId;

    @NotSerialized
    private InformationCatalog catalog;

    /**
     * 是否有效，true为有效，false为无效
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean valid;

    /**
     * 置顶
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean topmost;

    /**
     * 发布时间，即第一次发布信息时的时间
     */
    private Date publishTime;

    /**
     * 数据更新时间，当第一次发布时和publishTime相同，当修改信息后重新发布publishTime不变，updateTime修改
     */
    @IndexTimestamp
    @Index
    private Date updateTime;

    /**
     * 发布周期类型，从channel表中copy过来，当channel表被修改后，保留原来的周期类型
     */
    private PublishPeriod period;

    /**
     * 发布周期对应的开始时间
     */
    private Date periodTime;

    /**
     * 发布信息的部门的ID
     */
    @Index
    private Integer deptId;

    /**
     * 关联发布信息的部门
     */
    @NotSerialized
    private Dept dept;

    /**
     * 有效时间，如果当前时间在此时间之后，信息不显示，如果为null表示信息一直有效
     */
    private java.sql.Date validTime;

    @ColumnDescription(type = "varchar(50)")
    private String lang;

    /**
     * 文章的序号，用此序号排序，序号越大表示越排在前面
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @NotSerialized
    private InformationReadTimes readTimes;

    @NotSerialized
    private Map<String, String> properties;

    /**
     * 关联的文件，仅对type为file时有效
     */
    @NotSerialized
    private F file;

    /**
     * 关联的数据ID，一般是某个业务数据对应的记录的主键值
     */
    @ColumnDescription(type = "varchar(200)")
    private String linkId;

    private Long attachmentId;

    /**
     * 图片列表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @NotSerialized
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    public InformationBase()
    {
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubTitle()
    {
        return subTitle;
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle = subTitle;
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

    public InformationType getType()
    {
        return type;
    }

    public void setType(InformationType type)
    {
        this.type = type;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLink(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    /**
     * 由于之前linkUrl使用的字段名是link，并且在某些地方被调用，
     * 修改字段之后保留原来的get方法和set方法
     * 之所以修改字段是因为link在某些数据库下是关键字
     *
     * @return linkUrl
     */
    @Transient
    public String getLink()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
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

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public InformationCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(InformationCatalog catalog)
    {
        this.catalog = catalog;
    }

    public Boolean isValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public Boolean isTopmost()
    {
        return topmost;
    }

    public void setTopmost(Boolean topmost)
    {
        this.topmost = topmost;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    public Date getPeriodTime()
    {
        return periodTime;
    }

    public void setPeriodTime(Date periodTime)
    {
        this.periodTime = periodTime;
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

    public java.sql.Date getValidTime()
    {
        return validTime;
    }

    public void setValidTime(java.sql.Date validTime)
    {
        this.validTime = validTime;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public InformationReadTimes getReadTimes()
    {
        return readTimes;
    }

    public void setReadTimes(InformationReadTimes readTimes)
    {
        this.readTimes = readTimes;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public void setProperty(String name, String value)
    {
        Map<String, String> properties = getProperties();
        if (properties == null)
            setProperties(properties = new HashMap<String, String>());

        properties.put(name, value);
    }

    public F getFile()
    {
        return file;
    }

    public void setFile(F file)
    {
        this.file = file;
    }

    public String getLinkId()
    {
        return linkId;
    }

    public void setLinkId(String linkId)
    {
        this.linkId = linkId;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public SortedSet<Attachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(SortedSet<Attachment> attachments)
    {
        this.attachments = attachments;
    }

    @NotSerialized
    public String getContentText() throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        getContentText(buffer);

        return buffer.toString();
    }

    protected void getContentText(StringBuilder buffer) throws Exception
    {
        InformationType type = getType();
        if (type == InformationType.information)
        {
            List<C> contents = getContents();
            if (contents != null)
            {
                for (C content : contents)
                {
                    if (buffer.length() > 0)
                        buffer.append("\r\n");

                    if (content != null && content.getContent() != null)
                    {
                        buffer.append(HtmlUtils.getPlainText(new String(content.getContent())));
                    }
                }
            }
        }
        else if (type == InformationType.file)
        {
            F file = getFile();
            if (file != null)
            {
                if (buffer.length() > 0)
                    buffer.append("\r\n");
                buffer.append(file.getFileName());
                String extName = IOUtils.getExtName(file.getFileName());

                try
                {
                    String text = TextExtractors.extract(file.getContent(), extName);

                    if (text != null)
                        buffer.append(" ").append(text);
                }
                catch (Throwable ex)
                {
                    //读取文件失败，不影响索引建立
                    Tools.log(ex);
                }
            }
        }
        else if (type == InformationType.images)
        {
            //图片，获得图片说明
        }

        try
        {
            Channel channel = getChannel();
            String componentType = channel.getComponentType();
            if (!StringUtils.isEmpty(componentType))
            {
                InformationComponent component = (InformationComponent) Tools.getBean(Class.forName(componentType));

                String text = component.getText(this);
                if (!StringUtils.isEmpty(text))
                {
                    if (buffer.length() > 0)
                        buffer.append("\r\n");
                    buffer.append(text);
                }
            }
        }
        catch (Throwable ex)
        {
            //扩展文本加载错误不影响索引的建立
            Tools.log(ex);
        }
    }

    @Override
    public String toString()
    {
        return title;
    }
}
