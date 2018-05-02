package com.gzzm.ods.exchange;

import com.gzzm.ods.exchange.back.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.ods.receivetype.ReceiveType;
import com.gzzm.ods.receivetype.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author camel
 * @date 12-2-10
 */
@Service(url = "/ods/receivequery")
public class ReceiveReceiveQuery extends DeptOwnedQuery<Receive, Long>
{
    @Inject
    protected static Provider<ExchangeNotifyService> exchangeNotifyServiceProvider;

    @Inject
    private ExchangeReceiveService service;

    @Inject
    protected ReceiptDao receiptDao;

    @Inject
    private ReceiveTypeDao receiveTypeDao;

    @Inject
    private DeptService deptService;

    @Inject
    private BackService backService;

    /**
     * 文件名称
     */
    @Like("receiveBase.document.title")
    private String title;

    /**
     * 原文号
     */
    @Like("receiveBase.document.sendNumber")
    private String sendNumber;

    /**
     * 收文编号
     */
    @Like
    private String serial;

    /**
     * 来文单位
     */
    @Like("receiveBase.document.sourceDept")
    private String sourceDept;

    /**
     * 主题词
     */
    @Like("receiveBase.document.subject")
    private String subject;

    @Lower(column = "receiveBase.sendTime")
    private Date sendTime_start;

    @Upper(column = "receiveBase.sendTime")
    private Date sendTime_end;

    @Lower(column = "receiveBase.acceptTime")
    private Date acceptTime_start;

    @Upper(column = "receiveBase.acceptTime")
    private Date acceptTime_end;

    /**
     * 状态
     */
    @In("receiveBase.state")
    private ReceiveState[] state = new ReceiveState[]{
            ReceiveState.accepted,
            ReceiveState.flowing,
            ReceiveState.end
    };

    @Contains("receiveBase.document.textContent")
    private String text;

    @NotCondition
    private Integer receiveTypeId;

    @NotCondition
    @NotSerialized
    private List<Integer> receiveTypeIds;

    public ReceiveReceiveQuery()
    {
        addOrderBy("receiveBase.sendTime", OrderType.desc);
        addOrderBy("receiveBase.acceptTime", OrderType.desc);
    }

    @Equals("receiveBase.deptId")
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @In("receiveBase.deptId")
    @Override
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
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

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
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

    public Date getSendTime_end()
    {
        return sendTime_end;
    }

    public void setSendTime_end(Date sendTime_end)
    {
        this.sendTime_end = sendTime_end;
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

    public ReceiveState[] getState()
    {
        return state;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Integer getReceiveTypeId()
    {
        if (receiveTypeId != null && receiveTypeId == 0)
            return null;

        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public List<Integer> getReceiveTypeIds()
    {
        return receiveTypeIds;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        Integer receiveTypeId = getReceiveTypeId();
        if (receiveTypeId != null)
        {
            if (receiveTypeId == -1)
            {
                return "receiveTypeId is null";
            }
            else if (receiveTypeId > 0)
            {
                ReceiveType receiveType = receiveTypeDao.getReceiveType(receiveTypeId);

                if (receiveType != null)
                {
                    receiveTypeIds = receiveType.getAllReceiveTypeIds();

                    return "receiveTypeId in :receiveTypeIds";
                }
            }
        }

        return null;
    }

    /**
     * 撤销收文
     *
     * @param receiveId 收文ID
     * @throws Exception 数据库操作错误引起
     */
    @Service(url = "/ods/receivequery/{$0}/cancelReceive")
    @ObjectResult
    public void cancelReceive(Long receiveId) throws Exception
    {
        service.cannelReceive(receiveId);
    }

    @Service(url = "/ods/receivequery/{$0}/back", method = HttpMethod.post)
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
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

        Crud left;
        if (showDeptTree)
        {
            left = new AuthDeptDisplay();
        }
        else
        {
            ReceiveTypeDisplay typeDisplay = Tools.getBean(ReceiveTypeDisplay.class);
            typeDisplay.setDeptId(authDeptIds.iterator().next());
            left = typeDisplay;
        }

        ComplexTableView view = new ComplexTableView(left, showDeptTree ? "deptId" : "receiveTypeId", false);

        view.addComponent("文件名", "title");
        view.addComponent("发文时间", "sendTime_start", "sendTime_end");
        view.addComponent("文件内容", "text");
        if (!showDeptTree && authDeptIds.size() > 1)
            view.addComponent("收文部门", "deptIds");

        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("原文号", "sendNumber");
        view.addMoreComponent("编号", "serial");
//        view.addMoreComponent("主题词", "subject");

//        if (state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
//            view.addMoreComponent("接收时间", "acceptTime_start", "acceptTime_end");


        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${receiveBase.encodedDocumentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${receiveBase.document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

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
                Receive receive = (Receive) entity;

                Receipt receipt = receiptDao.getReceiptByDocumentId(receive.getReceiveBase().getDocumentId());
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
                            if (receiptDept.getDeptId().equals(receive.getReceiveBase().getDeptId()))
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
                return null;
            }
        }).setWidth("38").setLocked(true);


        view.addColumn("文件名称", new HrefCell("receiveBase.document.titleText")
                .setAction("showReceive(${receiveId})")).setOrderFiled("receiveBase.document.title");

        view.addColumn("来文单位", "receiveBase.document.sourceDept").setWidth("80").setWrap(true);
        view.addColumn("原文号", "receiveBase.document.sendNumber").setWidth("85").setWrap(true);
        view.addColumn("编号", "serial").setWidth("75").setWrap(true);
        view.addColumn("发文时间", new FieldCell("receiveBase.sendTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("85")
                .setWrap(true).setAlign(Align.center);

        if (state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
        {
            view.addColumn("撤销接收", new ConditionComponent().add("receiveBase.state.name()=='accepted'",
                    new CButton("${receiveBase.method.name()=='manual'?'删除':'撤销接收'}", "cancelReceive(${receiveId})")))
                    .setWidth("65");
        }

        Integer deptId = getDeptId();
        if (deptId == null)
            deptId = getDefaultDeptId();
        List<BackPaper> backPapers = backService.getBackPapers(deptId);

        if (backPapers == null || backPapers.size() == 0)
        {
            view.addColumn("退回", new ConditionComponent()
                    .add("receiveBase.state.name()=='noAccepted'||receiveBase.state.name()=='accepted'",
                            new CButton("退回", "back(${receiveId})"))).setWidth("40");
        }
        else if (backPapers.size() == 1)
        {
            Integer paperId = backPapers.get(0).getPaperId();
            view.addColumn("退回", new ConditionComponent()
                    .add("receiveBase.state.name()=='noAccepted'||receiveBase.state.name()=='accepted'",
                            new CButton("退回", "backByPaper(${receiveId},${receiveBase.deptId}," + paperId + ")")))
                    .setWidth("40");
        }
        else
        {
            view.addColumn("退回", new ConditionComponent()
                    .add("receiveBase.state.name()=='noAccepted'||receiveBase.state.name()=='accepted'",
                            new CButton("退回", "backByPaper(${receiveId},${receiveBase.deptId})"))).setWidth("40");
        }

        if (state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
        {
            view.addColumn("跟踪", new ConditionComponent().add("receiveBase.state.name()!='noAccepted'",
                    new CHref("跟踪").setAction("track(${receiveId})"))).setWidth("40");
        }

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/exchange/receive.js");
        view.importCss("/ods/exchange/receive.css");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("收文列表");
    }
}
