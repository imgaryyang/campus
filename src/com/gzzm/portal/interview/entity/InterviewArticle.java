package com.gzzm.portal.interview.entity;

import com.gzzm.platform.organ.User;
import com.gzzm.portal.interview.enumtype.PublishState;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 在线访谈-文章信息
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
@Entity(table = "PLINTERVIEWARTICLE",keys="articleId")
public class InterviewArticle {

    public InterviewArticle() {
    }

    /**
     * 主键
     */
    @Generatable(length = 11)
    private Integer articleId;

    /**
     * 在线访谈信息
     */
    @ColumnDescription(type= "number(8)")
    private Integer interviewId;

    @NotSerialized
    @ToOne("INTERVIEWID")
    private Interview interview;

    /**
     * 标题
     */
    @ColumnDescription(type = "varchar2(500)")
    private String title;

    /**
     * 内容
     */
    private char[] content;

    /**
     * 发布状态
     */
    @ColumnDescription(type= "number(1)")
    private PublishState state;

    /**
     * 创建人
     */
    @ColumnDescription(type = "number(9)")
    private Integer creatorId;

    @NotSerialized
    @ToOne("CREATORID")
    private User creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 发布人
     */
    @ColumnDescription(type = "number(9)")
    private Integer publishUserId;

    @NotSerialized
    @ToOne("PUBLISHUSERID")
    private User publishUser;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 修改人
     */
    @ColumnDescription(type = "number(9)")
    private Integer modifyUserId;

    @NotSerialized
    @ToOne("MODIFYUSERID")
    private User modifyUser;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 排序
     */
    @ColumnDescription(type="number(8)")
    private Integer orderId;

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public char[] getContent() {
        return content;
    }

    public void setContent(char[] content) {
        this.content = content;
    }

    public PublishState getState() {
        return state;
    }

    public void setState(PublishState state) {
        this.state = state;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Integer publishUserId) {
        this.publishUserId = publishUserId;
    }

    public User getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Integer modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public User getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(User modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterviewArticle)) return false;

        InterviewArticle that = (InterviewArticle) o;

        return articleId.equals(that.articleId);

    }

    @Override
    public int hashCode() {
        return articleId.hashCode();
    }
}
