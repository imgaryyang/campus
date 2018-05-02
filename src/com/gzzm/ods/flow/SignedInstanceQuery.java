package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 领导签发过的文件的查询
 *
 * @author camel
 * @date 12-4-25
 */
@Service(url = "/ods/flow/instance_signs")
public class SignedInstanceQuery extends DeptOwnedQuery<OdFlowInstance, Long>
{
    @Inject
    private OdSignService service;

    /**
     * 类型
     */
    private String[] type;

    @Equals("business.tag")
    private BusinessTag tag;

    /**
     * 状态
     */
    private OdFlowInstanceState state;

    @Like("document.title")
    private String title;

    @Lower(column = "startTime")
    private Date time_start;

    @Upper(column = "startTime")
    private Date time_end;

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

    @Contains("document.textContent")
    private String text;

    private Integer[] signer;

    private PageUserSelector userSelector;

    private Integer[] excludeBusinessId;

    public SignedInstanceQuery()
    {
        addOrderBy("startTime", OrderType.desc);
    }

    public String[] getType()
    {
        return type;
    }

    public void setType(String[] type)
    {
        this.type = type;
    }

    public BusinessTag getTag()
    {
        return tag;
    }

    public void setTag(BusinessTag tag)
    {
        this.tag = tag;
    }

    public OdFlowInstanceState getState()
    {
        return state;
    }

    public void setState(OdFlowInstanceState state)
    {
        this.state = state;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public boolean isSigned()
    {
        return true;
    }

    public Integer[] getSigner()
    {
        return signer;
    }

    public void setSigner(Integer[] signer)
    {
        this.signer = signer;
    }

    public Integer[] getExcludeBusinessId()
    {
        return excludeBusinessId;
    }

    public void setExcludeBusinessId(Integer[] excludeBusinessId)
    {
        this.excludeBusinessId = excludeBusinessId;
    }

    @Select(field = "signer")
    public PageUserSelector getUserSelector()
    {
        if (userSelector == null)
            userSelector = new PageUserSelector();

        return userSelector;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "state<2";

        if (signer != null)
        {
            s += " and (exists s in signers : (s.userId in ?signer))";
        }

        if (excludeBusinessId != null)
        {
            s += " and businessId not in ?excludeBusinessId";
        }

        return s;
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId") : new PageTableView();

        view.addComponent("标题", "title");
        view.addComponent("拟稿时间", "time_start", "time_end");

        if (!showDeptTree)
            view.addComponent("批示人", "signer");

        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("主题词", "subject");
        view.addMoreComponent("文件内容", "text");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment")))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("标题", new HrefCell("document.titleText").setProperty("style", "white-space:normal")
                .setAction("openDocument(${instanceId},true)")).setOrderFiled("document.title").setWidth("200");
        view.addColumn("编号", "serial");
        view.addColumn("类型", "typeName").setOrderFiled("type").setWidth("45").setAlign(Align.center);
        view.addColumn("拟稿时间/收文时间", "startTime").setWidth("125");
        view.addColumn("拟稿人/接收人", "createUser.userName").setWidth("90");

        view.addColumn("领导批示", new FieldCell("signs").setOrderable(false)).setAlign(Align.left).setAutoExpand(true)
                .setWrap(true);

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/flow/instance.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("领导批示意见一览表");
    }
}
