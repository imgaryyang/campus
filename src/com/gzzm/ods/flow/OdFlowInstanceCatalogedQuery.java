package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * @author camel
 * @date 12-4-26
 */
@Service(url = "/ods/flow/instance_cataloged")
public class OdFlowInstanceCatalogedQuery extends DeptOwnedQuery<OdFlowInstance, Long>
{
    @Inject
    private OdFlowDao dao;

    private Long[] keys;

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

    /**
     * 来文单位
     */
    @Like("document.sourceDept")
    private String sourceDept;

    @Contains("document.textContent")
    private String text;

    @Equals("tag")
    private String odTag;

    private boolean cataloged;

    public OdFlowInstanceCatalogedQuery()
    {
        addOrderBy("startTime", OrderType.desc);
    }

    public Long[] getKeys()
    {
        return keys;
    }

    public void setKeys(Long[] keys)
    {
        this.keys = keys;
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getOdTag()
    {
        return odTag;
    }

    public void setOdTag(String odTag)
    {
        this.odTag = odTag;
    }

    public Boolean getCataloged()
    {
        return cataloged;
    }

    public void setCataloged(Boolean cataloged)
    {
        this.cataloged = cataloged;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "state<2 and nocataloged=0";
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        PageTableView view;

        if (authDeptIds == null || authDeptIds.size() > 1)
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
        }
        else
        {
            view = new PageTableView(true);
        }

        view.addComponent("标题", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("拟稿时间", "time_start", "time_end");
        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("编号", "serial");
        view.addMoreComponent("主题词", "subject");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment")))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("标题", new HrefCell("document.titleText").setProperty("style", "white-space:normal")
                .setAction("openDocument(${instanceId},true)")).setOrderFiled("document.title");
        view.addColumn("发文字号", "document.sendNumber");
        view.addColumn("来文单位", "document.sourceDept");
        view.addColumn("编号", "serial");
        view.addColumn("类型", "typeName").setWidth("45");
        view.addColumn("拟稿时间/收文时间", "startTime").setWidth("125");
        view.addColumn("拟稿人/接收人", "createUser.userName").setWidth("90");


        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        if (cataloged)
        {
            view.addButton("取消归档", "uncatalog();");
        }
        else
        {
            view.addButton("归档", "catalog();");
            view.addButton("不归档", "nocatalog();");
        }

        view.importJs("/ods/flow/catalog.js");

        return view;
    }

    @Service(method = HttpMethod.post)
    public void catalog() throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.catalog(instanceIds);
    }

    @Service(method = HttpMethod.post)
    public void uncatalog() throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.uncatalog(instanceIds);
    }

    @Service(method = HttpMethod.post)
    public void nocatalog() throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.nocatalog(instanceIds);
    }
}
