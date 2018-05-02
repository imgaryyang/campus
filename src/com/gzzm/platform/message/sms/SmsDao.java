package com.gzzm.platform.message.sms;

import com.gzzm.platform.message.MessageDao;
import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 短信相关的数据库操作
 *
 * @author camel
 * @date 2010-5-23
 */
public abstract class SmsDao extends MessageDao
{
    public SmsDao()
    {
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    public Sms getSms(Long smsId) throws Exception
    {
        return load(Sms.class, smsId);
    }

    public SmsTemplate getSmsTemplate(Integer templateId) throws Exception
    {
        return load(SmsTemplate.class, templateId);
    }

    /**
     * 撤销短信发送，仅那些未发送的短信才允许撤销
     *
     * @param receiptIds 要撤销发送的短信
     * @return 实际撤销的短信的条数，即那些未发送的短信的数量
     * @throws Exception 数据库操作错误
     */
    @OQLUpdate(
            "update SmsLog set state=4 where state=0 and smsId in (select logId from SmsReceipt r where receiptId in :1)")
    public abstract int cancelSms(Long[] receiptIds) throws Exception;

    public SmsLog getSmsLog(String smsId) throws Exception
    {
        return load(SmsLog.class, smsId);
    }

    /**
     * 根据messageId获得SmsLog，用于接收到短信回执时设置短信的状态
     *
     * @param messageId messageId
     * @param phone     接收者的手机号码
     * @return SmsLog对象
     * @throws Exception 数据库查询错误
     * @see ReceiptInfo#messageId
     */
    @OQL("select l from SmsLog l where messageId=:1 and phone=:2 order by sendTime desc limit 1")
    public abstract SmsLog getSmsLogByMessageId(String messageId, String phone) throws Exception;

    /**
     * 根据serial获得SmsLog，用于接收到短信回执时设置短信的状态
     *
     * @param serial 短信尾码
     * @param phone  接收者的手机号码
     * @return SmsLog对象
     * @throws Exception 数据库查询错误
     * @see ReceiptInfo#messageId
     */
    @OQL("select l from SmsLog l where serial=:1 and phone=:2 order by sendTime desc limit 1")
    public abstract SmsLog getSmsLogBySerial(String serial, String phone) throws Exception;

    @OQL("select l from SmsLog l where messageId is null and sendTime>addday(sysdate(),-1) order by sendTime desc limit 500")
    public abstract List<SmsLog> queryNoSendedSms() throws Exception;

    /**
     * 根据messageId获得SmsReceipt，用于接收到短信回执时设置回执的状态和回复
     *
     * @param messageId messageId
     * @param phone     接收者的手机号码
     * @return SmsLog对象
     * @see ReceiptInfo#messageId
     */
    @OQL("select r from SmsReceipt r where messageId=:1 and phone=:2 order by sms.sendTime desc limit 1")
    public abstract SmsReceipt getReceiptByMessageId(String messageId, String phone);

    /**
     * 根据logId获得SmsReceipt，用于接收到短信回执时设置回执的状态和回复
     *
     * @param logId logId，对应SmsLog中的smsId
     * @return SmsLog对象
     * @see SmsLog#smsId
     */
    @GetByField("logId")
    public abstract SmsReceipt getReceiptByLogId(String logId);

    @OQL("select r from SmsReceipt r where r.smsId=:1 order by receiptId")
    public abstract List<SmsReceipt> getReceiptsBySmsId(Long smsId) throws Exception;

    @OQL("select r from SmsReceipt r where r.receiptId in :1 order by receiptId")
    public abstract List<SmsReceipt> getReceipts(Long[] receiptIds) throws Exception;

    public Integer getSmsSerial() throws Exception
    {
        return getId("PFSMSSERIAL", 2, Integer.class);
    }

    public SmsVote getVote(Integer voteId) throws Exception
    {
        return get(SmsVote.class, voteId);
    }

    @OQL("select count(*) from SmsVoteReply where voteId=:1 and phone=:2")
    public abstract Integer getVoteReplyCount(Integer voteId, String phone) throws Exception;

    @GetByField("listId")
    public abstract List<PhoneListItem> getPhoneListItems(Integer listId) throws Exception;

    @OQL("select p from PhoneList p where deptId in :1 order by dept.leftValue,p.orderId")
    public abstract List<PhoneList> getPhoneLists(Collection<Integer> deptId) throws Exception;
}
