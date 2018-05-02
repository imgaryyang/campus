package com.gzzm.ods.exchange;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 退回公文列表，用于已退回公文查询和被退回公文查询
 *
 * @author camel
 * @date 12-10-23
 */
@Service(url = "/ods/backlist")
public class BackQuery extends DeptOwnedQuery<Back, Long>
{
    @Inject
    private ExchangeSendService sendService;

    @Inject
    private ExchangeReceiveService receiveService;

    @Inject
    private OdFlowDao flowDao;

    /**
     * true表示查询退回公文，false表示查询被退回公文
     */
    private boolean self;

    @Lower(column = "backTime")
    private Date time_start;

    @Upper(column = "backTime")
    private Date time_end;

    @Like("receiveBase.document.title")
    private String title;

    /**
     * 发文编号
     */
    @Like("receiveBase.document.sendNumber")
    private String sendNumber;

    /**
     * 主题词
     */
    @Like("receiveBase.document.subject")
    private String subject;

    @Contains("receiveBase.document.textContent")
    private String text;

    private BackState state;

    private String page;

    @Like("receiveBase.document.sourceDept")
    private String sendDeptName;

    @Like("receiveBase.dept.deptName")
    private String backDeptName;

    /**
     * 没什么作用，仅用来接收前台参数，用于区分管理员菜单和非管理员菜单
     */
    private boolean manager;

    public BackQuery()
    {
        addOrderBy("backTime", OrderType.desc);
        addForceLoad("reason");
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public BackState getState()
    {
        return state;
    }

    public void setState(BackState state)
    {
        this.state = state;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public String getSendDeptName()
    {
        return sendDeptName;
    }

    public void setSendDeptName(String sendDeptName)
    {
        this.sendDeptName = sendDeptName;
    }

    public String getBackDeptName()
    {
        return backDeptName;
    }

    public void setBackDeptName(String backDeptName)
    {
        this.backDeptName = backDeptName;
    }

    public boolean isManager()
    {
        return manager;
    }

    public void setManager(boolean manager)
    {
        this.manager = manager;
    }

    @Override
    @NotCondition
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    @NotCondition
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (!self && state == null)
            state = BackState.NODEALED;

        setDefaultDeptId();
    }

    @Override
    public Integer getDeptId(Back entity) throws Exception
    {
        if (self)
            return entity.getReceiveBase().getDeptId();
        else
            return entity.getDeptId();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (self)
            return "receiveBase.deptId in ?queryDeptIds and receiveBase.deptId=?deptId";
        else
            return "deptId in ?queryDeptIds and deptId=?deptId";
    }

    @Override
    @Forward(page = "/ods/exchange/back.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Service
    @ObjectResult
    public void end(Long backId) throws Exception
    {
        Back entity = new Back();
        entity.setBackId(backId);
        entity.setState(BackState.DEALED);

        getCrudService().update(entity);
    }

    @Service
    @ObjectResult
    @Transactional
    public String withdraw(Long backId, boolean force) throws Exception
    {
        Back back = sendService.getDao().getBack(backId);

        Send send = back.getSend();

        if (send == null)
            return null;

        if (send.getState() == SendState.canceled)
            throw new NoErrorException("ods.exchange.sendCaneled");

        //撤回全部收文
        DeptInfo dept = receiveService.withdrawAllReceives(send.getDocumentId(), force);

        if (dept != null)
            return dept.getDeptName();

        //取消发文
        sendService.cancelSend(send, userOnlineInfo);

        back.setState(BackState.DEALED);
        sendService.getDao().update(back);

        return null;
    }

    @Redirect
    @Service(url = "/ods/flow/back/{$0}/open")
    public String openFlow(Long backId) throws Exception
    {
        Back back = flowDao.getBack(backId);

        ReceiveBase receiveBase = back.getReceiveBase();
        OdFlowInstance odFlowInstance = null;
        if (receiveBase.getType() == ReceiveType.collect)
        {
            Collect collect = flowDao.getCollect(receiveBase.getReceiveId());
            if (collect != null)
                odFlowInstance = collect.getCollectInstance();
        }
        if (receiveBase.getType() == ReceiveType.uniondeal)
        {
            UnionDeal unionDeal = flowDao.getUnionDeal(receiveBase.getReceiveId());
            if (unionDeal != null)
                odFlowInstance = unionDeal.getUnionInstance();
        }
        else
        {
            odFlowInstance = flowDao.getSendOdFlowInstanceByDocumentId(receiveBase.getDocumentId());
        }

        if (odFlowInstance != null)
            return OdFlowService.getFirstStepUrl(odFlowInstance);

        return null;
    }


    @Override
    protected Object createListView() throws Exception
    {
        if ("list".equals(getPage()))
        {
            SimplePageListView view = new SimplePageListView();

            view.setDisplay(new CUnion(
                    new CLabel(new HrefCell("receiveBase.document.titleText").setPrompt("${receiveBase.document.title}")
                            .setAction("show(${backId})")).setClass("title title_readed"),
                    new CLabel(new FieldCell("backTime").setFormat("yyyy-MM-dd")).setClass("time")));

            view.addAction(Action.more());

            view.importJs("/ods/exchange/back.js");

            return view;
        }

        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", false) :
                new PageTableView(false);

        view.addComponent("文件名", "title");
        view.addComponent("退回时间", "time_start", "time_end");
        if (!showDeptTree && authDeptIds != null && authDeptIds.size() > 1)
        {
            view.addComponent(self ? "收文部门" : "发文部门", "deptIds");
        }

        if (self)
            view.addComponent("发文部门", "sendDeptName");
        else
            view.addComponent("退文部门", "backDeptName");

        if (!self)
            view.addComponent("状态", "state");

        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("主题词", "subject");
        view.addMoreComponent("文件内容", "text");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${receiveBase.documentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${receiveBase.document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("receiveBase.document.title").setAction("show(${backId})")).setWrap(true);

        view.addColumn("发文字号", "receiveBase.document.sendNumber").setWidth("95").setWrap(true);

        view.addColumn("退回时间", new FieldCell("backTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("85")
                .setAlign(Align.center).setWrap(true);
        if (self)
            view.addColumn("退回人", "backUser.userName").setWidth("60").setWrap(true);

        if (!showDeptTree && authDeptIds.size() > 1)
        {
            if (self)
                view.addColumn("收文部门", "receiveBase.dept.deptName").setWidth("80").setWrap(true);
            else
                view.addColumn("发文部门", "dept.deptName").setWidth("80").setWrap(true);
        }

        if (self)
            view.addColumn("发文部门", "receiveBase.document.sourceDept").setWidth("70").setWrap(true);
        else
            view.addColumn("退文部门", "receiveBase.dept.deptName").setWidth("70").setWrap(true);

        view.addColumn("退回原因", "reason").setWidth("140").setWrap(true);
        view.addColumn("退文详情", new ConditionComponent().add("paperId!=null",
                new CButton("退文详情", "showBackPaper(${backId})"))).setWidth("80").setWrap(true);

        if (!self)
        {
            view.addColumn("完成", new ConditionComponent().add("state.name()=='NODEALED'",
                    new CButton("完成", "end(${backId})"))).setWidth("40");
            view.addColumn("撤回公文", new ConditionComponent().add("sendId!=null&&send!=null&&send.state.name()=='sended'",
                    new CButton("撤回公文", "withdraw(${backId})"))).setWidth("65");
            view.addColumn("跟踪", new ConditionComponent().add("sendId!=null", new CHref("跟踪").
                    setAction("track(${sendId})"))).setWidth("40");
        }

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/exchange/back.js");
        view.importCss("/ods/exchange/back.css");

        return view;
    }
}
