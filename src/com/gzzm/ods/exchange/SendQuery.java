package com.gzzm.ods.exchange;

import com.gzzm.ods.document.DocumentAttribute;
import com.gzzm.ods.flow.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.ods.sendnumber.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author wxj
 * @date 2011-7-6
 */
@Service(url = "/ods/sendlist")
public class SendQuery extends DeptOwnedQuery<Send, Long>
{
    @Inject
    private MenuItem menuItem;

    @Inject
    private ExchangeSendService service;

    @Inject
    private ExchangeNotifyService notifyService;

    @Inject
    private OdFlowDao odFlowDao;

    @Inject
    protected ReceiptDao receiptDao;

    @Inject
    private SendNumberDao sendNumberDao;

    @Inject
    private DeptService deptService;

    @UserId
    private Integer userId;

    /**
     * 公文标题
     */
    @Like("document.title")
    private String title;

    /**
     * 发文编号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    /**
     * 主题词
     */
    @Like("document.subject")
    private String subject;

    @Lower(column = "sendTime")
    private Date date_start;

    @Upper(column = "sendTime")
    private Date date_end;

    @Contains("document.textContent")
    private String text;

    private SendState state;

    /**
     * 是否只查询自己拟稿的公文
     */
    private boolean self;

    @NotCondition
    private Integer sendNumberId;

    private Integer[] createDeptId;

    @Like
    private String userName;

    private AuthDeptTreeModel createDeptTree;

    private Map<String, String> attributes;

    public SendQuery()
    {
        addOrderBy("sendTime", OrderType.desc);

        addForceLoad("document");

        //过滤出已没有被撤回的公文
        state = SendState.sended;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Date getDate_start()
    {
        return date_start;
    }

    public void setDate_start(Date date_start)
    {
        this.date_start = date_start;
    }

    public Date getDate_end()
    {
        return date_end;
    }

    public void setDate_end(Date date_end)
    {
        this.date_end = date_end;
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

    public SendState getState()
    {
        return state;
    }

    public void setState(SendState state)
    {
        this.state = state;
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public Integer[] getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer[] createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
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

    @NotSerialized
    @Select(field = "sendNumberId")
    public List<SendNumber> getSendNumbers() throws Exception
    {
        if (!self && getAuthDeptIds() != null && getAuthDeptIds().size() <= 3)
            return sendNumberDao.getSendNumbers(getAuthDeptIds());
        else
            return null;
    }

    @Select(field = "createDeptId")
    public AuthDeptTreeModel getCreateDeptTree() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (createDeptTree == null && !self && authDeptIds != null && authDeptIds.size() <= 3)
        {
            createDeptTree = new AuthDeptTreeModel();
            createDeptTree.setShowBox(true);
            DeptService deptService = createDeptTree.getService();
            List<Integer> deptIds = new ArrayList<Integer>();
            for (Integer deptId : authDeptIds)
            {
                deptIds.addAll(deptService.getDept(deptId).allSubDeptIds());
            }

            createDeptTree.setAuthDeptIds(deptIds);
        }
        return createDeptTree;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "";

        if (self)
            s = "creator=:userId";

        if (sendNumberId != null && sendNumberId > 0)
        {
            if (s.length() > 0)
                s += " and ";

            s += "sendNumberId=:sendNumberId";
        }

        if (!StringUtils.isEmpty(userName))
        {
            if (s.length() > 0)
                s += " and ";

            s += "(creatorName like :userName or creatorName is null and createUser.userName like :userName)";
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

        if (!StringUtils.isEmpty(s))
            return s;
        else
            return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (!self && (authDeptIds == null || authDeptIds.size() > 3))
            setDefaultDeptId();
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void reSend(Long sendId, List<Member> receivers) throws Exception
    {
        Send send = service.getDao().getSend(sendId);
        List<Long> receiveIds = service.reSend(send.getDocumentId(), receivers);

        notifyService.sendMessage(receiveIds, send.getDeptId(), userId, null, false);
    }

    @Override
    @Forward(page = "/ods/exchange/send.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = !self && (authDeptIds == null || authDeptIds.size() > 3);

        PageTableView view;

        if (showDeptTree)
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", false);
        else if (sendNumberId != null && sendNumberId == 0)
            view = new ComplexTableView(Tools.getBean(SendNumberList.class), "sendNumberId", false);
        else
            view = new PageTableView(false);

        view.addComponent("文件名", "title");
        view.addComponent("发文字号", "sendNumber");
        view.addComponent("文件内容", "text");

        for (DocumentAttribute documentAttribute : service.getDao().getDocumentAttributes())
        {
            if (documentAttribute.getQuery() != null && documentAttribute.getQuery())
            {
                String enumValues = documentAttribute.getEnumValues();
                if (StringUtils.isEmpty(enumValues))
                {
                    if (documentAttribute.getMoreQuery())
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

        if (!showDeptTree && authDeptIds != null && authDeptIds.size() > 1)
            view.addComponent("发文部门", "deptIds");

        view.addMoreComponent("发文时间", "date_start", "date_end");
//        view.addMoreComponent("主题词", "subject");

        if (sendNumberId == null && !showDeptTree && !self)
        {
            view.addMoreComponent("发文类型", "sendNumberId");
            view.addMoreComponent("拟稿科室", "createDeptId");
        }

        if (!self)
            view.addMoreComponent("拟稿人", "userName");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${encodedDocumentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("document.title").setAction("show(${sendId})")).setWrap(true);
        view.addColumn("发文字号", "document.sendNumber").setWidth("95").setWrap(true);
        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("85")
                .setAlign(Align.center).setWrap(true);
        view.addColumn("拟稿人", "creatorString").setWidth("60").setOrderFiled("creatorName").setWrap(true);
        if (self || authDeptIds == null || authDeptIds.size() > 1)
            view.addColumn("发文部门", "dept.deptName").setWidth("80").setWrap(true);

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
                Send send = (Send) entity;
                Receipt receipt = receiptDao.getReceiptByDocumentId(send.getDocumentId());
                if (receipt != null)
                {
                    String type = receipt.getType();
                    return "<a href=\"#\" onclick=\"return trackReceipt(" + receipt.getReceiptId() +
                            ")\" title=\"点击查看" + ReceiptComponents.getName(type) + "信息\">回执</a>";
                }

                return null;
            }
        }).setWidth("38").setLocked(true);

        view.addColumn("发文稿笺", new CHref("发文稿笺")
        {
            @Override
            public Object display(Object obj, String expression, UIManager uiManager) throws Exception
            {
                Send send = (Send) obj;
                SendFlowInstance sendFlowInstance =
                        odFlowDao.getSendFlowInstanceByDocumentId(send.getDocumentId());

                if (sendFlowInstance != null)
                    return super.display(obj, expression, uiManager);
                else
                    return null;
            }
        }.setAction("openFlow(${sendId})")).setWidth("65");
        view.addColumn("跟踪", new CHref("跟踪").setAction("track(${sendId})")).setWidth("40");
        view.addColumn("补发", new CHref("补发").setAction("reSend(${sendId},${deptId})")).setWidth("40");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/exchange/send.js");
        view.importCss("/ods/exchange/send.css");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters(menuItem == null ? "发文列表" : menuItem.getTitle());
    }
}
