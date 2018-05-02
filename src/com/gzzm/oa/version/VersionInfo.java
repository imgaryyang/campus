package com.gzzm.oa.version;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 项目版本更新发布信息记录
 * @author zy
 * @date 2017/1/10 9:49
 */
@Entity(table = "OAVERSIONINFO" , keys = "versionId")
public class VersionInfo
{
    /**
     * 版本主键
     */
    @Generatable(length = 6)
    private Integer versionId;

    /**
     * 项目
     */
    @ColumnDescription(type = "varchar2(250)")
    @Require
    private ProjectDir project;

    /**
     * 版本名称
     */
    @ColumnDescription(type = "varchar2(250)")
    @Require
    private String verName;

    /**
     * 版本号
     */
    @ColumnDescription(type = "varchar2(30)")
    @Require
    private String verNo;

    /**
     * 发布内容
     */
    @ColumnDescription(type = "clob")
    @Require
    private String publishContent;

    /**
     * 发布更新时间
     */
    @Require
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    @ColumnDescription(type = "number(1)" , defaultValue = "0")
    private Integer deleteTag;

    /**
     * 发布内容添加方式
     */
    private ReleaseMode releaseMode;

    /**
     * 如果是手动添加需要关联发布人员
     */
    private Integer publisher;

    @ToOne("PUBLISHER")
    @NotSerialized
    private User user;

    /**
     * 是否主动推送
     */
    private Boolean push;

    public VersionInfo()
    {
    }

    public Integer getVersionId()
    {
        return versionId;
    }

    public void setVersionId(Integer versionId)
    {
        this.versionId = versionId;
    }

    public ProjectDir getProject()
    {
        return project;
    }

    public void setProject(ProjectDir project)
    {
        this.project = project;
    }

    public String getVerName()
    {
        return verName;
    }

    public void setVerName(String verName)
    {
        this.verName = verName;
    }

    public String getVerNo()
    {
        return verNo;
    }

    public void setVerNo(String verNo)
    {
        this.verNo = verNo;
    }

    public String getPublishContent()
    {
        return publishContent;
    }

    public void setPublishContent(String publishContent)
    {
        this.publishContent = publishContent;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public ReleaseMode getReleaseMode()
    {
        return releaseMode;
    }

    public void setReleaseMode(ReleaseMode releaseMode)
    {
        this.releaseMode = releaseMode;
    }

    public Integer getPublisher()
    {
        return publisher;
    }

    public void setPublisher(Integer publisher)
    {
        this.publisher = publisher;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Boolean getPush()
    {
        return push;
    }

    public void setPush(Boolean push)
    {
        this.push = push;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof VersionInfo)) return false;

        VersionInfo that = (VersionInfo) o;

        return !(versionId != null ? !versionId.equals(that.versionId) : that.versionId != null);

    }

    @Override
    public int hashCode()
    {
        return versionId != null ? versionId.hashCode() : 0;
    }
}
