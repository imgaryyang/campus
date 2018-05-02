package com.gzzm.portal.interview.entity;

import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Null;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

import java.util.Date;

/**
 * 在线访谈信息
 *
 * @author lk
 * @date 13-10-8
 */
@Entity(table = "PLINTERVIEW", keys = "interviewId")
public class Interview
{
	public Interview()
	{
	}

	/**
	 * 主键
	 */
	@Generatable(length = 8)
	private Integer interviewId;

	/**
	 * 标题
	 */
	@Require
	@ColumnDescription(type = "varchar(250)", nullable = false)
	private String title;

	/**
	 * 内容
	 */
	private char[] content;

	/**
	 * 开始时间
	 */
	@Require
	private Date startTime;

	/**
	 * 结束时间
	 */
	@Require
	private Date endTime;

	/**
	 * 嘉宾
	 */
	@ColumnDescription(type = "varchar2(100)")
	private String guestName;

	/**
	 * 主持人
	 */
	@ColumnDescription(type = "varchar2(100)")
	private String hostName;

	/**
	 * 发布状态
	 */
	@Require
	@ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
	private Integer state;

	/**
	 * 类别
	 */
	@Require
	@ColumnDescription(type = "number(6)", nullable = false)
	private Integer typeId;

	@NotSerialized
	private InterviewType type;

	/**
	 * 所属网站
	 */
	@ColumnDescription(type="number(6)",nullable=false)
	private Integer stationId;

	private Station station;

	/**
	 * 播放文件
	 */
	private byte[] playFile;

	/**
	 * 播放文件地址
	 */
	@ColumnDescription(type = "varchar(500)")
	private String playFileUrl;

	/**
	 * 下期预告
	 */
	@ColumnDescription(type = "varchar(4000)")
	private String foreNotice;

	/**
	 * 图片
	 */
	private byte[] photo;

	/**
	 * 嘉宾感言
	 */
	private char[] classicQuotation;

	/**
	 * 排序
	 */
	@ColumnDescription(type = "number(8)")
	private Integer orderId;

	public Integer getOrderId()
	{
		return orderId;
	}

	public void setOrderId(Integer orderId)
	{
		this.orderId = orderId;
	}

	public char[] getClassicQuotation()
	{
		return classicQuotation;
	}

	public void setClassicQuotation(char[] classicQuotation)
	{
		this.classicQuotation = classicQuotation;
	}

	public char[] getContent()
	{
		return content;
	}

	public void setContent(char[] content)
	{
		this.content = content;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public String getForeNotice()
	{
		return foreNotice;
	}

	public void setForeNotice(String foreNotice)
	{
		this.foreNotice = foreNotice;
	}

	public Integer getInterviewId()
	{
		return interviewId;
	}

	public void setInterviewId(Integer interviewId)
	{
		this.interviewId = interviewId;
	}

	public byte[] getPhoto()
	{
		return photo;
	}

	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}

	public boolean isPhotoExists()
	{
		return photo != null && !Null.ByteArray.equals(photo);
	}

	public byte[] getPlayFile()
	{
		return playFile;
	}

	public void setPlayFile(byte[] playFile)
	{
		this.playFile = playFile;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Integer getState()
	{
		return state;
	}

	public void setState(Integer state)
	{
		this.state = state;
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

	public InterviewType getType()
	{
		return type;
	}

	public void setType(InterviewType type)
	{
		this.type = type;
	}

	public Integer getStationId() {
		return stationId;
	}

	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getPlayFileUrl() {
		return playFileUrl;
	}

	public void setPlayFileUrl(String playFileUrl) {
		this.playFileUrl = playFileUrl;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Interview interview = (Interview) o;

		if (!interviewId.equals(interview.interviewId))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return interviewId.hashCode();
	}

	@Override
	public String toString()
	{
		return title;
	}
}
