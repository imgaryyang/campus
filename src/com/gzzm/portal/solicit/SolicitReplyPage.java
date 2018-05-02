package com.gzzm.portal.solicit;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 民意征集相关服务
 * Created by sjy on 2016/3/22.
 */
@Service(url = "/solicit/solicitReplyPage")
public class SolicitReplyPage extends BaseNormalCrud<SolicitReply, Integer>
{

    private Integer solicitId;

    private String solicitTitle;

    @Like
    private String userName;

    private SolicitReplyState state;

    @Lower(column = "replyTime")
    private Date startReplyTime;

    @Upper(column = "replyTime")
    private Date endReplyTime;

    @Inject
    private UserOnlineInfo userOnlineInfo;
    @Inject
    private SolicitDao dao;

    public SolicitReplyPage()
    {
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.setTitle(solicitTitle);
        view.addComponent("回复人", "userName");
        view.addComponent("回复状态", "state");
        view.addComponent("回复时间", "startReplyTime", "endReplyTime");
        view.addColumn("回复人", "userName").setWidth("120px");
        view.addColumn("回复内容", "content").setWrap(true);
        view.addColumn("回复时间", "replyTime").setAlign(Align.center);
        view.addColumn("回复状态", "state");
        view.addColumn("查看", new CButton("查看", "showReply(${replyId},'${solicit.title}')"));
        view.setOnDblClick("showReply(${replyId},'${solicit.title}')");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.addButton(Buttons.export("xls"));
        view.importJs("/portal/solicit/solicit_reply.js");
        return view;
    }

    @Override
    @Forward(page = "/portal/solicit/reply.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    /**
     * 征集回复审核方法
     *
     * @param state        审核状态
     * @param replyId      征集id
     * @param checkContent 审核信息
     * @return
     * @throws Exception
     */
    @Service
    @Transactional
    public Integer checkReply(Integer state, Integer replyId, char[] checkContent) throws Exception
    {
        return dao.updateReply(state, replyId, checkContent, userOnlineInfo.getUserId(), new java.util.Date());
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return null;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getSolicitTitle()
    {
        return solicitTitle;
    }

    public void setSolicitTitle(String solicitTitle)
    {
        this.solicitTitle = solicitTitle;
    }

    public Integer getSolicitId()
    {
        return solicitId;
    }

    public void setSolicitId(Integer solicitId)
    {
        this.solicitId = solicitId;
    }

    public Date getStartReplyTime()
    {
        return startReplyTime;
    }

    public void setStartReplyTime(Date startReplyTime)
    {
        this.startReplyTime = startReplyTime;
    }

    public Date getEndReplyTime()
    {
        return endReplyTime;
    }

    public void setEndReplyTime(Date endReplyTime)
    {
        this.endReplyTime = endReplyTime;
    }

    public SolicitReplyState getState()
    {
        return state;
    }

    public void setState(SolicitReplyState state)
    {
        this.state = state;
    }
}
