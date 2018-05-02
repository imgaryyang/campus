package com.gzzm.oa.help;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.MaxLen;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.sql.Date;

/**
 * 系统使用小窍门实体
 * @author gyw
 * @date 2017/7/20 0020
 */
@Entity(table = "OASYSUSERSKILL", keys = "skillId")
public class SysUseSkill {

    @Generatable(length = 6)
    private Integer skillId;

    /**
     * 创建人
     */
    private Integer creatorId;

    @NotSerialized
    @ToOne("CREATORID")
    private User creator;

    /**
     * 内容
     */
    @NotSerialized
    @MaxLen(value = 2000)
    @Require
    @ColumnDescription(type = "varchar2(4000)", nullable = false)
    private String content;

    /**
     * 发布标志
     */
    @ColumnDescription(type = "number(2)",defaultValue = "0")
    private Boolean publishTag;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 发布时间
     */
    private Date publishDate;

    public SysUseSkill() {
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublishTag() {
        return publishTag;
    }

    public void setPublishTag(Boolean publishTag) {
        this.publishTag = publishTag;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SysUseSkill)) return false;

        SysUseSkill that = (SysUseSkill) o;

        return skillId != null ? skillId.equals(that.skillId) : that.skillId == null;

    }

    @Override
    public int hashCode() {
        return skillId != null ? skillId.hashCode() : 0;
    }
}
