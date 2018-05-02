package com.gzzm.ods.receipt.normal;

import com.gzzm.ods.receipt.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.CrudUtils;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * @author camel
 * @date 12-4-8
 */
@Service
public class NormalReceiptPage extends ReceiptBasePage
{
    @Inject
    private NormalReceiptDao normalReceiptDao;

    private NormalReceipt normalReceipt;

    private boolean readOnly;

    @UserId
    private Integer userId;

    private PageAttachmentList attachments;

    public NormalReceiptPage()
    {
    }

    public NormalReceipt getNormalReceipt()
    {
        return normalReceipt;
    }

    public void setNormalReceipt(NormalReceipt normalReceipt)
    {
        this.normalReceipt = normalReceipt;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public PageAttachmentList getAttachments()
    {
        return attachments;
    }

    public void setAttachments(PageAttachmentList attachments)
    {
        this.attachments = attachments;
    }

    @Service(url = {"/ods/receipt/normal/{receiptId}/reply", "/ods/receipt/normal/reply"})
    public String showReply() throws Exception
    {
        loadReceipt();

        if (receipt != null)
            normalReceipt = normalReceiptDao.getNormalReceipt(receiptId);

        attachments = new PageAttachmentList();
        if (normalReceipt != null)
            attachments.setAttachmentId(normalReceipt.getAttachmentId());
        attachments.setAuthority(new DeptAttachmentAuthority(deptId, false));

        ReceiptReply reply = getReply();
        if (reply != null && reply.getReplied() != null && reply.getReplied())
        {
            readOnly = true;
        }

        attachments.setEditable(!readOnly);

        return "reply";
    }

    @Transactional
    @Service(method = HttpMethod.post)
    public Long saveReply() throws Exception
    {
        if (normalReceipt == null)
            normalReceipt = new NormalReceipt();

        normalReceipt.setReceiptId(receiptId);

        if (attachments != null)
        {
            normalReceipt.setAttachmentId(attachments.save(userId, deptId, "od_receipt"));
        }

        normalReceiptDao.save(normalReceipt);

        return receiptId;
    }

    @Override
    @Transactional
    @Service(method = HttpMethod.post)
    public void reply() throws Exception
    {
        saveReply();

        normalReceiptDao.makeItemReplied(receiptId, deptId);

        super.reply();
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public boolean addUsers(Integer[] userIds) throws Exception
    {
        boolean result = false;
        if (userIds != null)
        {
            for (Integer userId : userIds)
            {
                NormalReceiptItem item = normalReceiptDao.getItemByUserId(receiptId, deptId, userId);
                if (item == null)
                {
                    User user = normalReceiptDao.getUser(userId);

                    item = new NormalReceiptItem();
                    item.setReceiptId(receiptId);
                    item.setDeptId(deptId);
                    item.setUserId(userId);
                    item.setCreateTime(new Date());
                    item.setCreator(this.userId);
                    item.setUserName(user.getUserName());
                    item.setSex(user.getSex());
                    item.setPhone(user.getPhone());
                    item.setStation(StringUtils.concat(user.getStations(), ","));
                    item.setOrderId(CrudUtils.getOrderValue(6, true));
                    item.setReplied(false);

                    normalReceiptDao.add(item);

                    result = true;
                }
            }
        }

        return result;
    }
}
