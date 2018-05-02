package com.gzzm.oa.notice;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.meta.CascadeType;

import java.io.InputStream;
import java.util.Date;

/**
 * 内部信息实体类，对应数据库的内部信息表
 *
 * @author czf
 * @date 2010-3-16
 */
@Entity(table = "OANOTICE", keys = "noticeId")
public class Notice
{
    /**
     * 信息ID，长度9位，系统生成
     */
    @Generatable(length = 9)
    private Integer noticeId;

    /**
     * 信息标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String title;

    /**
     * 内容，当类型为0是有效
     */
    private char[] content;

    /**
     * 创建人或最后的修改人
     */
    private Integer creator;

    /**
     * 用户信息，通过CREATOR字段关联User对象
     */
    @ToOne("CREATOR")
    private User user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 置顶标志，0-不置顶，1-置顶
     */
    @Require
    private TopTag topTag;

    /**
     * 置顶有效时间，当topTag为1时有效
     */
    private java.sql.Date topInvalidTime;

    /**
     * 有效时间
     */
    private java.sql.Date invalidTime;

    /**
     * 部门ID
     */
    @Index
    private Integer deptId;

    private Dept dept;

    /**
     * 类型ID
     */
    @Require
    private Integer typeId;

    /**
     * 类型信息，通过typeId字段关联NoticeType对象
     */
    @Cascade(CascadeType.setNull)
    private NoticeType noticeType;

    /**
     * 状态，0-未发布，1-已发布
     */
    private NoticeState state;

    /**
     * 阅读次数
     */
    @ColumnDescription(type = "number(5)")
    private Integer readTimes;

    /**
     * 类型，0-采编，1-URL
     */
    @ColumnDescription(type = "number(1)")
    private InfoType type;

    /**
     * 模板ID
     */
    private Integer templateId;

    @NotSerialized
    @Cascade(CascadeType.setNull)
    private NoticeTemplate template;

    /**
     * url,当类型为1是有效
     */
    @ColumnDescription(type = "varchar(250)")
    private String url;

    /**
     * 文件，仅当type为2时有效
     */
    private InputStream fileContent;

    /**
     * 文件类型，即扩展名，仅当type为2时有效
     */
    private String fileType;

    public Notice()
    {
    }

    public Integer getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Integer noticeId)
    {
        this.noticeId = noticeId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public TopTag getTopTag()
    {
        return topTag;
    }

    public void setTopTag(TopTag topTag)
    {
        this.topTag = topTag;
    }

    public java.sql.Date getTopInvalidTime()
    {
        return topInvalidTime;
    }

    public void setTopInvalidTime(java.sql.Date topInvalidTime)
    {
        this.topInvalidTime = topInvalidTime;
    }

    public java.sql.Date getInvalidTime()
    {
        return invalidTime;
    }

    public void setInvalidTime(java.sql.Date invalidTime)
    {
        this.invalidTime = invalidTime;
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

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public NoticeType getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeType(NoticeType noticeType)
    {
        this.noticeType = noticeType;
    }

    public NoticeState getState()
    {
        return state;
    }

    public void setState(NoticeState state)
    {
        this.state = state;
    }

    public Integer getReadTimes()
    {
        return readTimes;
    }

    public void setReadTimes(Integer readTimes)
    {
        this.readTimes = readTimes;
    }

    public InfoType getType()
    {
        return type;
    }

    public void setType(InfoType type)
    {
        this.type = type;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public NoticeTemplate getTemplate()
    {
        return template;
    }

    public void setTemplate(NoticeTemplate template)
    {
        this.template = template;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public InputStream getFileContent()
    {
        return fileContent;
    }

    public void setFileContent(InputStream fileContent)
    {
        this.fileContent = fileContent;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    /**
     * 判断信息是否置顶
     *
     * @return 置顶，返回true，不置顶返回false
     */
    public boolean isTop()
    {
        return topTag == TopTag.top &&
                (topInvalidTime == null || topInvalidTime.getTime() >= System.currentTimeMillis());
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Notice))
            return false;

        Notice that = (Notice) o;

        return noticeId.equals(that.noticeId);
    }

    @Override
    public int hashCode()
    {
        return noticeId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}
