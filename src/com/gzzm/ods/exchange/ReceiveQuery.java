package com.gzzm.ods.exchange;

import com.gzzm.ods.document.DocumentAttribute;
import com.gzzm.ods.exchange.back.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.ods.timeout.OdTimeout;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.timeout.TimeoutService;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author fwj
 * @date 2011-7-6
 */
@Service(url = "/ods/receivelist")
public class ReceiveQuery extends DeptOwnedQuery<ReceiveBase, Long>
{
    @Inject
    protected static Provider<ExchangeNotifyService> exchangeNotifyServiceProvider;
    @Inject
    protected static Provider<ExchangeSendService> exchangeSendServiceProvider;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    protected ReceiptDao receiptDao;

    @Inject
    private MenuItem menuItem;
    @Inject
    private ExchangeReceiveService service;

    @Inject
    private ExchangeNotifyService notifyService;

    @Inject
    private TimeoutService timeoutService;

    @Inject
    private DeptService deptService;

    @Inject
    private BackService backService;

    /**
     * 文件名称
     */
    @Like("document.title")
    private String title;

    /**
     * 原文号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    /**
     * 来文单位
     */
    @Like("document.sourceDept")
    private String sourceDept;

    /**
     * 主题词
     */
    @Like("document.subject")
    private String subject;

    @Lower(column = "sendTime")
    private Date sendTime_start;

    @Upper(column = "sendTime")
    private Date sendTime_end;

    @Lower(column = "acceptTime")
    private Date acceptTime_start;

    @Upper(column = "acceptTime")
    private Date acceptTime_end;

    @Like
    private String serial;

    /**
     * 状态
     */
    private ReceiveState[] state;

    /**
     * 收文类型
     */
    private ReceiveType[] type;

    @Contains("document.textContent")
    private String text;

    @Equals("state")
    private ReceiveState state1;

    private ReceiveMethod method;

    private List<Receive> receives;

    /**
     * 没什么作用，仅用来接收前台参数，用于区分管理员菜单和非管理员菜单
     */
    private boolean manager;

    private String page;

    private Map<String, String> attributes;

    @NotSerialized
    private Receipt receipt;

    private boolean receiptLoaded;

    /**
     * 1：黄牌
     * 2：红牌
     * 3: 未完成红牌
     */
    private Integer warn;

    public ReceiveQuery()
    {
        addOrderBy("nvl(sendTime,acceptTime)", OrderType.desc);
        addOrderBy("acceptTime", OrderType.desc);

        addForceLoad("document");
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getSendTime_start()
    {
        return sendTime_start;
    }

    public void setSendTime_start(Date sendTime_start)
    {
        this.sendTime_start = sendTime_start;
    }

    public Date getAcceptTime_start()
    {
        return acceptTime_start;
    }

    public void setAcceptTime_start(Date acceptTime_start)
    {
        this.acceptTime_start = acceptTime_start;
    }

    public Date getAcceptTime_end()
    {
        return acceptTime_end;
    }

    public void setAcceptTime_end(Date acceptTime_end)
    {
        this.acceptTime_end = acceptTime_end;
    }

    public Date getSendTime_end()
    {
        return sendTime_end;
    }

    public void setSendTime_end(Date sendTime_end)
    {
        this.sendTime_end = sendTime_end;
    }

    public ReceiveState[] getState()
    {
        return state;
    }

    public void setState(ReceiveState[] state)
    {
        this.state = state;
    }

    public ReceiveState getState1()
    {
        return state1;
    }

    public void setState1(ReceiveState state1)
    {
        this.state1 = state1;
    }

    public ReceiveType[] getType()
    {
        return type;
    }

    public void setType(ReceiveType[] type)
    {
        this.type = type;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public ReceiveMethod getMethod()
    {
        return method;
    }

    public void setMethod(ReceiveMethod method)
    {
        this.method = method;
    }

    public boolean isManager()
    {
        return manager;
    }

    public void setManager(boolean manager)
    {
        this.manager = manager;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public Integer getWarn()
    {
        return warn;
    }

    public void setWarn(Integer warn)
    {
        this.warn = warn;
    }

    private List<Receive> getReceives() throws Exception
    {
        if (receives == null)
        {
            List<Long> receiveIds = new ArrayList<Long>();
            for (ReceiveBase receiveBase : getList())
            {
                receiveIds.add(receiveBase.getReceiveId());
            }

            receives = service.getDao().getReceives(receiveIds);
        }

        return receives;
    }

    public void setReceives(List<Receive> receives)
    {
        this.receives = receives;
    }

    public Receive getReceive(Long receiveId) throws Exception
    {
        for (Receive receive : getReceives())
        {
            if (receive.getReceiveId().equals(receiveId))
                return receive;
        }

        return null;
    }

    public Receipt getReceipt() throws Exception
    {
        if (receipt == null && !receiptLoaded && getEntity() != null)
        {
            receipt = receiptDao.getReceiptByDocumentId(getEntity().getDocumentId());
            if (receipt != null)
            {
                List<Dept> receiptDepts = receipt.getReceiptDepts();
                if (receiptDepts.size() > 0)
                {
                    boolean b = false;
                    Integer deptId = getEntity().getDeptId();
                    for (Dept receiptDept : receiptDepts)
                    {
                        if (receiptDept.getDeptId().equals(deptId))
                        {
                            b = true;
                            break;
                        }
                    }

                    if (!b)
                        receipt = null;
                }
            }

            receiptLoaded = true;
        }
        return receipt;
    }

    protected boolean containsState(ReceiveState state)
    {
        if (this.state == null)
            return true;

        for (ReceiveState state1 : this.state)
        {
            if (state1 == state)
                return true;
        }

        return false;
    }

    @Override
    public String getAlias()
    {
        return "r";
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 3)
            setDefaultDeptId();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if (getDeptId() == null && (getAuthDeptIds() == null || getAuthDeptIds().size() > 3))
            setDefaultDeptId();

        if (!StringUtils.isEmpty(serial))
            join(Receive.class, "r1", "r.receiveId=r1.receiveId");
    }

    /**
     * 撤销收文
     *
     * @param receiveId 收文ID
     * @throws Exception 数据库操作错误引起
     */
    @Service(url = "/ods/receive/{$0}/cancelReceive")
    @ObjectResult
    public void cancelReceive(Long receiveId) throws Exception
    {
        service.cannelReceive(receiveId);
    }

    @Service(url = "/ods/receive/{$0}/back", method = HttpMethod.post)
    @ObjectResult
    public void back(Long receiveId, String reason) throws Exception
    {
        Long backId = service.back(receiveId, reason, userOnlineInfo.getUserId());

        try
        {
            exchangeNotifyServiceProvider.get().sendBackMessage(backId);
        }
        catch (Throwable ex)
        {
            //发送消息失败，不影响其他逻辑
        }
    }

    /**
     * 转发公文
     *
     * @param receiveId 要转发的公文
     * @param receivers 收文单位列表，即转发的目标
     * @throws Exception 转发公文错误，由数据库操作错误引起
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public void turnDocument(Long receiveId, List<Member> receivers) throws Exception
    {
        Long sendId = exchangeSendServiceProvider.get().turnDocument(
                receiveId, receivers, userOnlineInfo.getUserId(), userOnlineInfo.getDeptId());

        notifyService.sendMessageWithSendId(sendId, userOnlineInfo.getUserId(), null, false);
    }

    @Override
    @Forward(page = "/ods/exchange/receive.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "";
        if (state == null)
            s = "state<4";

        if (!StringUtils.isEmpty(serial))
        {
            if (s.length() > 0)
                s += " and ";
            s += "r1.serial like ?serial";
        }

        if (attributes != null)
        {
            for (Map.Entry<String, String> entry : attributes.entrySet())
            {
                if (!StringUtils.isEmpty(entry.getValue()))
                {
                    String key = entry.getKey();
                    DocumentAttribute documentAttribute =
                            service.getDao().getDocumentAttribute(Integer.valueOf(key.substring(1)));

                    if (documentAttribute != null)
                    {
                        if (s.length() > 0)
                            s += " and ";

                        if (StringUtils.isEmpty(documentAttribute.getEnumValues()))
                        {
                            s += "document.attributes." + documentAttribute.getAttributeName() +
                                    " like '%'||:attributes." + key + "||'%'";
                        }
                        else
                        {
                            s += "document.attributes." + documentAttribute.getAttributeName() + "=:attributes." + key;
                        }
                    }
                }
            }
        }
        if (warn != null)
        {
            if (s.length() > 0)
            {
                s += " and ";
            }
            if (warn == 1)
            {
                s +=
                        " documentId in( select r.documentId from com.gzzm.ods.receipt.Receipt r where exists d in receiptDepts : d.deptId in ?authDeptIds " +
                                "and not exists rr in r.replies : (rr.deptId in ?authDeptIds and rr.replied =1) and r.deadline > sysdate()" +
                                " and r.warningTime < sysdate())";
            }
            else if (warn == 2)
            {
                s +=
                        " documentId in( select r.documentId from com.gzzm.ods.receipt.Receipt r where exists d in receiptDepts : d.deptId in ?authDeptIds  and " +
                                "(exists rr in r.replies : (rr.deptId in ?authDeptIds and rr.replied =1 and rr.replyTime>r.deadline) or  " +
                                "(not exists rr in r.replies : (rr.deptId in ?authDeptIds and rr.replied =1) and sysdate() > r.deadline)))";
            }
            else if (warn == 3)
            {
                s +=
                        " documentId in( select r.documentId from com.gzzm.ods.receipt.Receipt r where exists d in receiptDepts : d.deptId in ?authDeptIds  and " +
                                "(not exists rr in r.replies : (rr.deptId in ?authDeptIds and rr.replied =1) and sysdate() > r.deadline)))";
            }
        }


        if (!StringUtils.isEmpty(s))
            return s;
        else
            return null;
    }

    @NotSerialized
    @Select(field = "deptIds")
    public List<DeptInfo> getDepts() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (authDeptIds != null && authDeptIds.size() <= 3)
        {
            return deptService.getDepts(authDeptIds);
        }

        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        if ("list".equals(getPage()))
        {
            SimplePageListView view = new SimplePageListView();
            view.setDisplay(new CUnion(
                    new CLabel(new HrefCell("document.titleText").setPrompt("${document.title}").setAction(
                            "showReceive(${receiveId},'${state}')"))
                            .setClass("title ${state.name()=='noAccepted'?'title_noread':'title_readed'}"),
                    new CLabel(new FieldCell("sendTime").setFormat("yyyy-MM-dd")).setClass("time")));

            view.addAction(Action.more());

            view.importJs("/ods/exchange/receive.js");

            return view;
        }

        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", false) :
                new PageTableView(false);

        view.addComponent("文件名", "title");
        view.addComponent("来文单位", "sourceDept");
        view.addComponent("发文时间", "sendTime_start", "sendTime_end");
        view.addComponent("文件内容", "text");

        if (state == null)
        {
            view.addComponent("状态", new CCombox("state1", new ReceiveState[]{
                    ReceiveState.noAccepted,
                    ReceiveState.accepted,
                    ReceiveState.flowing,
                    ReceiveState.end
            }));
        }

        for (DocumentAttribute documentAttribute : service.getDao().getDocumentAttributes())
        {
            if (documentAttribute.getQuery() != null && documentAttribute.getQuery())
            {
                String enumValues = documentAttribute.getEnumValues();
                if (StringUtils.isEmpty(enumValues))
                {
                    if (documentAttribute.getMoreQuery() != null && documentAttribute.getMoreQuery())
                    {
                        view.addMoreComponent(documentAttribute.getAttributeName(),
                                "attributes.a" + documentAttribute.getAttributeId());
                    }
                    else
                    {
                        view.addComponent(documentAttribute.getAttributeName(),
                                "attributes.a" + documentAttribute.getAttributeId());
                    }
                }
                else
                {
                    if (documentAttribute.getMoreQuery() != null && documentAttribute.getMoreQuery())
                    {
                        view.addMoreComponent(documentAttribute.getAttributeName(),
                                new CCombox("attributes.a" + documentAttribute.getAttributeId(),
                                        enumValues.split("\\|")));
                    }
                    else
                    {
                        view.addComponent(documentAttribute.getAttributeName(),
                                new CCombox("attributes.a" + documentAttribute.getAttributeId(),
                                        enumValues.split("\\|")));
                    }
                }
            }
        }

        if (containsType(ReceiveType.receive) && containsState(ReceiveState.accepted))
        {
            view.addMoreComponent("公文来源", "method");
        }

        if (!showDeptTree && authDeptIds.size() > 1)
            view.addComponent("收文部门", "deptIds");

        view.addMoreComponent("原文号", "sendNumber");

        if (state == null || state.length > 1 || state[0] != ReceiveState.noAccepted)
            view.addMoreComponent("编号", "serial");
//        view.addMoreComponent("主题词", "subject");

        if (state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
            view.addMoreComponent("接收时间", "acceptTime_start", "acceptTime_end");

        view.setClass("${state.name()=='noAccepted'?'new_bold':''}");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${encodedDocumentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        if (containsState(ReceiveState.noAccepted))
        {
            view.addColumn("接收", new ConditionComponent().add("state.name()=='noAccepted'",
                    new CButton("接收", "receive(${receiveId},'${type.name()}',${deptId})"))).setWidth("40");
        }
        if (warn != null && warn == 1)
        {
            view.addColumn("预警", new BaseSimpleCell()
            {
                @Override
                public Object getValue(Object o) throws Exception
                {
                    return "<img src='/ods/dispatch/images/yellow.png' style='width:15px;height:15px'>";
                }
            }).setWidth("40").setAlign(Align.center);
        }
        else if (warn != null && warn == 2)
        {
            view.addColumn("预警", new BaseSimpleCell()
            {
                @Override
                public Object getValue(Object o) throws Exception
                {
                    return "<img src='/ods/dispatch/images/red.png' style='width:15px;height:15px'>";
                }
            }).setWidth("40").setAlign(Align.center);
        }

        if (containsType(ReceiveType.receive))
        {
            view.addColumn("回执", new Cell()
            {
                public Object getValue(Object entity) throws Exception
                {
                    return null;
                }

                public Class getType(Class<?> entityType)
                {
                    return null;
                }

                public Object display(Object entity) throws Exception
                {
                    ReceiveBase receive = (ReceiveBase) entity;
                    if (receive.getType() == ReceiveType.receive)
                    {
                        Receipt receipt = receiptDao.getReceiptByDocumentId(receive.getDocumentId());
                        if (receipt != null)
                        {
                            boolean b = false;
                            List<Dept> receiptDepts = receipt.getReceiptDepts();
                            if (receiptDepts.size() == 0)
                            {
                                b = true;
                            }
                            else
                            {
                                for (Dept receiptDept : receiptDepts)
                                {
                                    if (receiptDept.getDeptId().equals(receive.getDeptId()))
                                    {
                                        b = true;
                                        break;
                                    }
                                }
                            }

                            if (b)
                            {
                                String type = receipt.getType();
                                return "<a href=\"#\" onclick=\"return showReceipt(" + receive.getReceiveId() +
                                        ")\" title=\"点击查看" + ReceiptComponents.getName(type) + "信息\">回执</a>";
                            }
                        }
                    }

                    return null;
                }
            }).setWidth("38").setLocked(true);
        }

        view.addColumn("文件名称",
                new HrefCell("document.titleText").setAction("showReceive(${receiveId},'${state.name()}')"))
                .setOrderFiled("document.title").setWrap(true);

        view.addColumn("来文单位", "document.sourceDept").setWidth("90").setWrap(true);

        if (authDeptIds == null || authDeptIds.size() > 1)
        {
            view.addColumn("收文部门", "dept.deptName").setWidth("90").setWrap(true);
        }

        view.addColumn("原文号", "document.sendNumber").setWidth("95").setWrap(true);

        if (state == null || state.length > 1 || state[0] != ReceiveState.noAccepted)
            view.addColumn("编号", "crud$.getReceive(receiveId).serial").setWidth("95").setWrap(true);

        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("80")
                .setAlign(Align.center).setWrap(true);
        if (state == null || state.length > 1 || state[0] != ReceiveState.noAccepted)
        {
            view.addColumn("接收时间", new FieldCell("acceptTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("80")
                    .setAlign(Align.center).setWrap(true);
        }

        if (containsType(ReceiveType.receive))
        {
            view.addColumn("类型", "type.name()=='receive'?sendType:type").setAlign(Align.center).setWidth("40")
                    .setWrap(true);
        }

        if (state == null || state.length > 1)
            view.addColumn("状态", "state").setWidth("50");

        if (state == null || state.length != 1 || state[0] != ReceiveState.end)
        {
            if (timeoutService.hasTimeoutConfig(OdTimeout.RECEIVE_ID, authDeptIds))
            {
                view.addColumn("警告", new ConditionComponent().add("validTimeout!=null",
                        new CImage("/timeout/level/icon/${validTimeout.levelId}")
                                .setPrompt("${validTimeout.level.levelName}").setProperty("style", "cursor:pointer")
                )).setWidth("45");
            }
        }

        if (state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
        {
            view.addColumn("撤销接收", new ConditionComponent().add("state.name()!='noAccepted'",
                    new CButton("${method.name()=='manual'?'删除':'撤销接收'}", "cancelReceive(${receiveId})")))
                    .setWidth("65");
        }

        Integer deptId = getDeptId();
        if (deptId == null)
            deptId = getDefaultDeptId();
        List<BackPaper> backPapers = backService.getBackPapers(deptId);

        if (backPapers == null || backPapers.size() == 0)
        {
            view.addColumn("退回", new ConditionComponent().add("state.name()=='noAccepted'||state.name()=='accepted'",
                    new CButton("退回", "back(${receiveId})"))).setWidth("40");
        }
        else if (backPapers.size() == 1)
        {
            Integer paperId = backPapers.get(0).getPaperId();
            view.addColumn("退回", new ConditionComponent().add("state.name()=='noAccepted'||state.name()=='accepted'",
                    new CButton("退回", "backByPaper(${receiveId},${deptId}," + paperId + ")"))).setWidth("40");
        }
        else
        {
            view.addColumn("退回", new ConditionComponent().add("state.name()=='noAccepted'||state.name()=='accepted'",
                    new CButton("退回", "backByPaper(${receiveId},${deptId})"))).setWidth("40");
        }

        Collection<Integer> turnAuthDeptIds = userOnlineInfo.getAuthDeptIds("od_turn_dept_select");
        if (turnAuthDeptIds == null || turnAuthDeptIds.size() > 0)
        {
            view.addColumn("转发", new CButton("转发", "turnDocument(${receiveId})")).setWidth("40");
        }

        if (state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
        {
            view.addColumn("跟踪", new ConditionComponent().add("state.name()!='noAccepted'",
                    new CHref("跟踪").setAction("track(${receiveId})"))).setWidth("40");
        }

        view.addColumn("所有收文部门", new ConditionComponent().add("type.name()=='receive'&&method.name()!='manual'",
                new CHref("所有收文部门").setAction("allReceives(${documentId})"))).setWidth("85");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/exchange/receive.js");
        view.importCss("/ods/exchange/receive.css");

        return view;
    }

    protected boolean containsType(ReceiveType type)
    {
        if (this.type == null)
            return true;

        for (ReceiveType type1 : this.type)
        {
            if (type1 == type)
                return true;
        }

        return false;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters(menuItem == null ? "收文列表" : menuItem.getTitle());
    }
}
