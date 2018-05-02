package com.gzzm.portal.pressconference;

import com.gzzm.platform.attachment.Attachment;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Null;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.sql.*;

/**
 * @author lk
 * @date 13-10-20
 */
@Entity(table = "PLPRESSCONFERENCE", keys = "conferenceId")
public class PressConference
{
	public PressConference()
	{
	}

	@Generatable(length = 8)
	private Integer conferenceId;

	@Require
	@ColumnDescription(type = "varchar(250)", nullable = false)
	private String title;

	@Require
	@ColumnDescription(type = "varchar(50)", nullable = false)
	private String spokesman;

	@Require
	@ColumnDescription(nullable = false)
	private Date releaseDate;

	@Require
	@ColumnDescription(type = "varchar(250)", nullable = false)
	private String address;

	@ColumnDescription(nullable = false, defaultValue = "0")
	private PressConferenceState state;

	@Require
	@ColumnDescription(nullable = false)
	private char[] spokesmanIntroduction;

	@Require
	@ColumnDescription(type = "varchar(800)", nullable = false)
	private String attendees;

	@Require
	@ColumnDescription(nullable = false)
	private char[] background;

	@Require
	@ColumnDescription(nullable = false)
	private char[] content;

	@ColumnDescription(nullable = false)
	private Integer creatorId;

	@ColumnDescription(nullable = false)
	private Timestamp createTime;

	private byte[] photo;

	@ColumnDescription(type = "number(12)")
	private Long attachmentId;

	@NotSerialized
	private Attachment attachment;

	public Integer getConferenceId()
	{
		return conferenceId;
	}

	public void setConferenceId(Integer conferenceId)
	{
		this.conferenceId = conferenceId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getSpokesman()
	{
		return spokesman;
	}

	public void setSpokesman(String spokesman)
	{
		this.spokesman = spokesman;
	}

	public Date getReleaseDate()
	{
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public PressConferenceState getState()
	{
		return state;
	}

	public void setState(PressConferenceState state)
	{
		this.state = state;
	}

	public char[] getSpokesmanIntroduction()
	{
		return spokesmanIntroduction;
	}

	public void setSpokesmanIntroduction(char[] spokesmanIntroduction)
	{
		this.spokesmanIntroduction = spokesmanIntroduction;
	}

	public String getAttendees()
	{
		return attendees;
	}

	public void setAttendees(String attendees)
	{
		this.attendees = attendees;
	}

	public char[] getBackground()
	{
		return background;
	}

	public void setBackground(char[] background)
	{
		this.background = background;
	}

	public char[] getContent()
	{
		return content;
	}

	public void setContent(char[] content)
	{
		this.content = content;
	}

	public Integer getCreatorId()
	{
		return creatorId;
	}

	public void setCreatorId(Integer creatorId)
	{
		this.creatorId = creatorId;
	}

	public Timestamp getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Timestamp createTime)
	{
		this.createTime = createTime;
	}

	public byte[] getPhoto()
	{
		return photo;
	}

	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}

	public Long getAttachmentId()
	{
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId)
	{
		this.attachmentId = attachmentId;
	}

	public Attachment getAttachment()
	{
		return attachment;
	}

	public void setAttachment(Attachment attachment)
	{
		this.attachment = attachment;
	}

	public boolean isPhotoExists()
	{
		return photo != null && !photo.equals(Null.ByteArray);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PressConference that = (PressConference) o;

		return conferenceId.equals(that.conferenceId);

	}

	@Override
	public int hashCode()
	{
		return conferenceId.hashCode();
	}

	@Override
	public String toString()
	{
		return title;
	}
}
