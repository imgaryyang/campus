package com.gzzm.portal.pressconference;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author lk
 * @date 13-10-22
 */
@Service(url = "/portal/pressconference/AttachmentCrud?attachmentId={$0}")
public class AttachmentCrud extends SubListCrud<Attachment, Long>
{
	public AttachmentCrud()
	{
	}

	@Inject
	private AttachmentDao dao;

	private Long attachmentId;

	public Long getAttachmentId()
	{
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId)
	{
		this.attachmentId = attachmentId;
	}

	@Override
	protected String getParentField()
	{
		return "attachmentId";
	}

	@Override
	protected void initListView(SubListView view) throws Exception
	{
	}

	@Override
	public void update(Attachment entity) throws Exception
	{
		super.update(entity);
	}

	@Override
	public Long save() throws Exception
	{
		return super.save();
	}

	@Override
	public List<Attachment> getList() throws Exception
	{
		if (attachmentId == null)
		{
			return Collections.emptyList();
		}

		return dao.getAttachments(attachmentId);
	}
}
