package com.gzzm.platform.message.sms;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.receiver.Receiver;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.CHref;

import java.util.*;

/**
 * 投票管理
 *
 * @author camel
 * @date 11-9-27
 */
@Service(url = "/message/sms/vote")
public class SmsVoteCrud extends DeptOwnedEditableCrud<SmsVote, Integer>
{
    @UserId
    private Integer userId;

    @Like
    private String title;

    /**
     * 新增加的参与者
     */
    private String receiver;

    private Integer voteId;

    public SmsVoteCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public Integer getVoteId()
    {
        if (voteId == null && getEntity() != null)
            voteId = getEntity().getVoteId();
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public void initEntity(SmsVote entity) throws Exception
    {
        List<String> options = new ArrayList<String>(1);
        options.add("");
        entity.setOptions(options);
        entity.setVoteCount(1);
        entity.setSended(false);
        entity.setAnonymous(false);
        entity.setRestrictReceiver(false);

        super.initEntity(entity);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreator(userId);
        getEntity().setCreateTime(new Date());

        return true;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (getEntity().isAnonymous() == null)
            getEntity().setAnonymous(false);

        if (getEntity().isRestrictReceiver() == null)
            getEntity().setRestrictReceiver(false);

        return true;
    }

    @Override
    @Forward(page = "/platform/message/smsvote.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/platform/message/smsvote.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("标题", "title");

        view.addColumn("标题", "title").setWidth("250");
        view.addColumn("创建人", "createUser.userName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("选项", new FieldCell("optionsText").setOrderable(false)).setAutoExpand(true);
        view.addColumn("回复列表", new CHref("回复列表").setAction("showReplys(${voteId})"));
        view.addColumn("查看结果", new CHref("查看结果").setAction("showStat(${voteId})"));

        view.defaultInit(false);

        view.importJs("/platform/message/smsvote.js");

        return view;
    }

    @Forward(page = "/platform/message/smsvote_addreceiver.ptl")
    @Service(url = "/message/sms/vote/{voteId}/addReceiver")
    public String addReceiver() throws Exception
    {
        return null;
    }

    @Service(method = HttpMethod.post)
    public void send() throws Exception
    {
        String receivers = this.receiver;
        SmsVote vote;
        if (receivers == null)
        {
            save();
            vote = getEntity();
            receivers = vote.getReceiver();
        }
        else
        {
            vote = getEntity(getVoteId());
            vote.setReceiver(vote.getReceiver() + ";" + receivers);
        }


        List<Receiver> receiverList = Receiver.parseReceiverList(receivers);
        if (receiverList != null)
        {
            for (Receiver receiver : receiverList)
            {
                Message message = new Message();
                message.setSender(vote.getCreator());
                message.setFromDeptId(vote.getDeptId());

                if (receiver.getId() != null)
                    message.setUserId(Integer.valueOf(receiver.getId()));
                else
                    message.setPhone(receiver.getValue());
                message.setForce(true);
                message.setMessage(vote.getTitle() + "\n" + vote.getOptionsText());
                message.setMethods(SmsMessageSender.SMS);
                message.setCode(SmsVoteReplyProcessor.VOT + vote.getVoteId());
                message.send();
            }
        }

        vote.setSended(true);
        update(vote);
    }
}
