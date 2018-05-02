package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.User;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 11-9-28
 */
public class SmsVoteReplyProcessor implements SmsReplyProcessor
{
    public static final String VOT = "VOT";

    @Inject
    private static Provider<SmsDao> daoProvider;

    public SmsVoteReplyProcessor()
    {
    }

    public void process(SmsReply reply) throws Exception
    {
        if (reply.getType() == 1)
        {
            SmsDao dao = daoProvider.get();
            Integer voteId = Integer.valueOf(reply.getCode());
            SmsVote vote = dao.getVote(voteId);

            String s = process0(reply, vote, dao);

            if (s != null)
            {
                com.gzzm.platform.message.Message message = new com.gzzm.platform.message.Message();

                message.setPhone(reply.getPhone());
                message.setForce(true);
                message.setMessage(s);
                message.setMethods(SmsMessageSender.SMS);
                message.setCode(SmsVoteReplyProcessor.VOT + vote.getVoteId());
                message.setFromDeptId(vote.getDeptId());
                message.send();
            }
        }
    }

    private String process0(SmsReply reply, SmsVote vote, SmsDao dao) throws Exception
    {
        Integer voteValue = null;

        try
        {
            voteValue = Integer.valueOf(reply.getContent());
        }
        catch (NumberFormatException ex)
        {
            //在后面处理
        }

        if (voteValue != null)
        {
            if (voteValue <= 0 || voteValue > vote.getOptions().size())
                voteValue = null;
        }

        if (voteValue == null)
        {
            return "回复的内容不正确，请回复1-" + vote.getOptions().size();
        }

        Date replyTime = reply.getTime();
        if (vote.getEndTime() != null)
        {
            if (DateUtils.truncate(replyTime).after(vote.getEndTime()))
                return "投票已经结束";
        }

        if (vote.getVoteCount() != null && vote.getVoteCount() > 0)
        {
            Integer voteCount = dao.getVoteReplyCount(vote.getVoteId(), reply.getPhone());
            if (voteCount != null && voteCount >= vote.getVoteCount())
            {
                return "超过投票次数，每个电话号码只能投" + vote.getVoteCount() + "次";
            }
        }

        String userName = null;
        List<Receiver> receiverList = Receiver.parseReceiverList(vote.getReceiver());
        boolean exists = false;
        for (Receiver receiver : receiverList)
        {
            String phone;
            if (receiver.getId() != null)
            {
                User user = dao.getUser(Integer.valueOf(receiver.getId()));
                phone = user.getPhone();
            }
            else
            {
                phone = receiver.getValue();
            }

            if (reply.getPhone().equals(phone))
            {
                userName = receiver.getName();
                exists = true;
                break;
            }
        }

        if (vote.isRestrictReceiver() && !exists)
        {
            return "不允许此手机号码参加投票";
        }

        SmsVoteReply voteReply = new SmsVoteReply();
        voteReply.setVoteId(vote.getVoteId());
        voteReply.setPhone(reply.getPhone());
        voteReply.setReplyTime(replyTime);
        voteReply.setVoteValue(voteValue);
        voteReply.setUserName(userName);

        dao.add(voteReply);

        return "投票成功";
    }
}
