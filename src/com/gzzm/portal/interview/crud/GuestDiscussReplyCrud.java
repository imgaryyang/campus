package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.interview.entity.GuestDiscussReply;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;

/**
 * @author Xrd
 * @date 2018/4/4 15:01
 */
@Service(url = "/portal/interview/crud/GuestDiscussReplyCrud")
public class GuestDiscussReplyCrud extends BaseNormalCrud<GuestDiscussReply, Integer>
{
    private Integer discussId;

    @Like
    private String replyName;

    public GuestDiscussReplyCrud()
    {
        setLog(true);
        addOrderBy("replyTime", OrderType.desc);
    }

    public Integer getDiscussId()
    {
        return discussId;
    }

    public void setDiscussId(Integer discussId)
    {
        this.discussId = discussId;
    }

    public String getReplyName()
    {
        return replyName;
    }

    public void setReplyName(String replyName)
    {
        this.replyName = replyName;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return " discussId=?discussId ";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("回复人","replyName");
        view.addColumn("回复人", "replyName").setWidth("150").setAlign(Align.center);
        view.addColumn("回复时间", "replyTime").setWidth("150").setAlign(Align.center);
        view.addColumn("回复内容", "replyContent").setAlign(Align.center);

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("回复人", "replyName");
        view.addComponent("回复时间", "replyTime");
        view.addComponent("回复内容", new CTextArea("replyContent"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setDiscussId(discussId);
        return super.beforeInsert();
    }
}
