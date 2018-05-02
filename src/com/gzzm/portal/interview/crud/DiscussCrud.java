package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.interview.dao.*;
import com.gzzm.portal.interview.entity.*;
import com.gzzm.portal.interview.enumtype.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 在线访谈-留言维护
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
@Service(url = "/portal/interview/crud/DiscussCrud")
public class DiscussCrud extends BaseNormalCrud<GuestDiscuss, Integer> {

    @Inject
    private GuestDiscussReplyDao guestDiscussReplyDao;

    public DiscussCrud() {
        setLog(true);
        addOrderBy("createTime", OrderType.desc);
        addOrderBy("orderId", OrderType.desc);
    }

    /**
     * url参数-在线访谈Id
     */
    private Integer interviewId;

    /**
     * 查询条件-发言人名称
     */
    @Like
    private String visitorName;

    /**
     * 查询条件-留言人类别
     */
    private DiscussGuestType guestType;

    /**
     * 查询条件-发布状态
     */
    private PublishState state;

    /**
     * 留言信息 - 表单使用
     */
    private GuestDiscuss discuss;

    private GuestDiscussReply reply;

    @Inject
    private InterviewBaseDao dao;

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public DiscussGuestType getGuestType() {
        return guestType;
    }

    public void setGuestType(DiscussGuestType guestType) {
        this.guestType = guestType;
    }

    public PublishState getState() {
        return state;
    }

    public void setState(PublishState state) {
        this.state = state;
    }

    public GuestDiscuss getDiscuss() {
        return discuss;
    }

    public void setDiscuss(GuestDiscuss discuss) {
        this.discuss = discuss;
    }

    public GuestDiscussReply getReply()
    {
        return reply;
    }

    public void setReply(GuestDiscussReply reply)
    {
        this.reply = reply;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("发言人类别", "guestType");
        view.addComponent("发言人名称", "visitorName");
        view.addComponent("发布状态", "state");

        view.addColumn("发言人类别", "guestType").setWidth("80");
        view.addColumn("发言人名称", "visitorName").setWidth("120");
        view.addColumn("内容", "content");
        view.addColumn("提交时间", "createTime").setWidth("150");
        view.addColumn("ip地址", "guestIp").setWidth("100");
        view.addColumn("发布状态", "state");
        view.addColumn("回复",new CButton("回复","showReplyModal(${discussId})"));
        view.addColumn("查看回复",new CButton("查看回复","showReplyPage(${discussId})"));

        view.defaultInit();
        view.addButton("发布", "publish(true)");
        view.addButton("取消发布", "publish(false)");

        view.importJs("/portal/interview/discuss.js");

        return view;
    }

    @Service(url = "/portal/interview/crud/showReplyView")
    public String showReplyView(Integer discussId){
        reply=new GuestDiscussReply();
        reply.setReplyTime(new Date());
        reply.setDiscussId(discussId);
        return "portal/interview/reply_modal.ptl";
    }

    @Service
    @RequestBody
    public boolean saveReplyEntity(String replyContent)
    {
        try{
            reply.setReplyContent(replyContent);
            guestDiscussReplyDao.save(reply);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("发言人类别", "guestType");
        view.addComponent("发言人名称", "visitorName");
        view.addComponent("提交时间", "createTime");
        view.addComponent("内容", "content");
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception {
        getEntity().setInterviewId(interviewId);
        DiscussGuestType guestType = getEntity().getGuestType();
        if (!guestType.equals(DiscussGuestType.VISITOR)) {
            getEntity().setVisitorName(getGuestName(getEntity().getGuestType()));
        }
        return super.beforeInsert();
    }

    public String getGuestName(DiscussGuestType guestType) throws Exception {
        if (guestType.equals(DiscussGuestType.HOST)) {
            return dao.load(Interview.class, interviewId).getHostName();
        }
        if (guestType.equals(DiscussGuestType.GUEST)) {
            return dao.load(Interview.class, interviewId).getGuestName();
        }
        return "";
    }

    @Override
    protected String getComplexCondition() throws Exception {
        return " interviewId=?interviewId ";
    }

    @Service(method = HttpMethod.post)
    public void publish(boolean p) throws Exception {
        PublishState state = p ? PublishState.PUBLISHED : PublishState.UNPUBLISH;
        for (int key : getKeys()) {
            GuestDiscuss entity = getEntity(key);
            entity.setState(state);
            saveEntity(entity, false);
        }
    }
}
