package com.gzzm.portal.solicit;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.sql.*;
import java.util.SortedSet;

/**
 * @author lk
 * @date 13-10-17
 */
@Entity(table = "PLSOLICIT", keys = "solicitId")
public class Solicit
{
	public Solicit()
	{
	}

	@Generatable(length = 8)
	private Integer solicitId;

	@Require
	@ColumnDescription(type = "varchar(250)", nullable = false)
	private String title;

	@ColumnDescription(type = "number(6)", nullable = false)
	@Require
	private Integer typeId;

	private SolicitType type;

	@ColumnDescription(type = "number(9)", nullable = false)
	private Integer creatorId;

	@ToOne("creatorId")
	@NotSerialized
	private User creator;

	@ColumnDescription(nullable = false)
	private Timestamp createTime;

	private java.util.Date publishTime;

	private Integer deptId;

	@ToOne("DEPTID")
	private Dept dept;

	/**
	 * 发布部门
	 */
	private Integer publishDeptId;

	@ToOne("PUBLISHDEPTID")
	private Dept publishDept;

	@Require
	@ColumnDescription(nullable = false)
	private SolicitState state;

	private Date endDate;

	private char[] content;

	@ColumnDescription(type = "number(12)")
    private Long attachmentId;

      /**
       * 附件列表，征集上传的附件
       *
       * @see com.gzzm.platform.attachment.Attachment
       */
      @NotSerialized
      @EntityList(column = "ATTACHMENTID")
      private SortedSet<Attachment> attachments;

	@Require
	@ColumnDescription(type = "number(8)", nullable = false)
	private Integer stationId;

	//	@NotSerialized
	private Station station;

	@ColumnDescription(type = "number(7)")
	private Integer orderId;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getSolicitId()
	{
		return solicitId;
	}

	public void setSolicitId(Integer solicitId)
	{
		this.solicitId = solicitId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Integer getTypeId()
	{
		return typeId;
	}

	public void setTypeId(Integer typeId)
	{
		this.typeId = typeId;
	}

	public SolicitType getType()
	{
		return type;
	}

	public void setType(SolicitType type)
	{
		this.type = type;
	}

	public Integer getCreatorId()
	{
		return creatorId;
	}

	public void setCreatorId(Integer creatorId)
	{
		this.creatorId = creatorId;
	}

	public Integer getStationId()
	{
		return stationId;
	}

	public void setStationId(Integer stationId)
	{
		this.stationId = stationId;
	}

	public Station getStation()
	{
		return station;
	}

	public void setStation(Station station)
	{
		this.station = station;
	}

	public User getCreator()
	{
		return creator;
	}

	public void setCreator(User creator)
	{
		this.creator = creator;
	}

	public Timestamp getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Timestamp createTime)
	{
		this.createTime = createTime;
	}

	public SolicitState getState()
	{
		return state;
	}

	public void setState(SolicitState state)
	{
		this.state = state;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public char[] getContent()
	{
		return content;
	}

	public void setContent(char[] content)
	{
		this.content = content;
	}

	public java.util.Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(java.util.Date publishTime) {
		this.publishTime = publishTime;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public Dept getDept() {
		return dept;
	}

	public void setDept(Dept dept) {
		this.dept = dept;
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

	public Integer getPublishDeptId() {
		return publishDeptId;
	}

	public void setPublishDeptId(Integer publishDeptId) {
		this.publishDeptId = publishDeptId;
	}

	public Dept getPublishDept() {
		return publishDept;
	}

	public void setPublishDept(Dept publishDept) {
		this.publishDept = publishDept;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Solicit solicit = (Solicit) o;

		if (!solicitId.equals(solicit.solicitId))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return solicitId.hashCode();
	}

	@Override
	public String toString()
	{
		return title;
	}
}
