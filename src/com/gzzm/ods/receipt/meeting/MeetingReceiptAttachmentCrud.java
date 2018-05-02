package com.gzzm.ods.receipt.meeting;

import com.gzzm.ods.receipt.ReceiptAttachmentCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/6/14
 */
@Service(url = "/ods/receipt/meeting/attachment")
public class MeetingReceiptAttachmentCrud extends ReceiptAttachmentCrud
{
    @Inject
    private MeetingReceiptDao meetingReceiptDao;

    public MeetingReceiptAttachmentCrud()
    {
    }

    @Override
    protected Long loadAttachmentId() throws Exception
    {
        ReceiptMeeting meeting = meetingReceiptDao.getMeeting(getReceiptId());

        return meeting.getAttachmentId();
    }
}
