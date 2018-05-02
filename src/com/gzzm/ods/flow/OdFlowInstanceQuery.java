package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * @author camel
 * @date 12-4-26
 */
@Service(url = "/ods/flow/instance_query")
public class OdFlowInstanceQuery extends DeptOwnedQuery<OdFlowInstance, Long>
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

    @Like
    private String serial;

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

    /**
     * 共享目录ID
     */
    @NotCondition
    private Integer catalogId;

    @Equals("tag")
    private String odTag;

    @In("document.priority")
    private String[] priority;

    public OdFlowInstanceQuery()
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

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
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

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getOdTag()
    {
        return odTag;
    }

    public void setOdTag(String odTag)
    {
        this.odTag = odTag;
    }

    public String[] getPriority()
    {
        return priority;
    }

    public void setPriority(String[] priority)
    {
        this.priority = priority;
    }

    @AuthDeptIds
    public void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        super.setAuthDeptIds(authDeptIds);
    }

    public OdInstanceCatalogTreeModel getCatalogTree() throws Exception
    {
        if (getDeptId() != null)
        {
            OdInstanceCatalogTreeModel tree = Tools.getBean(OdInstanceCatalogTreeModel.class);
            tree.setDeptId(getDeptId());

            return tree;
        }

        return null;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "state<2";

        if (catalogId != null)
        {
            if (catalogId == 0)
                s += " and catalogId is not null";
            else if (catalogId != -1)
                s += " and catalogId=:catalogId";
        }

        return s;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (catalogId != null && (catalogId == 0 || catalogId == -1))
            setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
//        Collection<Integer> authDeptIds = getAuthDeptIds();
        PageTableView view;

        if (catalogId != null && catalogId != -1)
        {
            OdInstanceCatalogDisplay catalogDisplay = Tools.getBean(OdInstanceCatalogDisplay.class);
            catalogDisplay.setDeptId(getDeptId());
            view = new ComplexTableView(catalogDisplay, "catalogId", false);
        }
//        else if (authDeptIds == null || authDeptIds.size() > 3)
//        {
//            view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
//        }
        else
        {
            view = new PageTableView(catalogId != null);
        }

        view.addComponent("标题", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("拟稿时间", "time_start", "time_end");

        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (authDeptIds == null || authDeptIds.size() > 1)
            view.addComponent("所属部门", "deptIds");

        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("收文编号", "serial");
//        view.addMoreComponent("主题词", "subject");

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
        view.addColumn("拟稿时间/收文时间", "startTime").setWidth("140");
        view.addColumn("拟稿人/接收人", "createUser.userName").setWidth("90");


        if (catalogId != null && catalogId == -1)
            view.addColumn("共享目录", "catalog.catalogName");

        view.addButton(Buttons.query());

        if (catalogId != null && catalogId == -1)
        {
            view.addButton(new CMenuButton("catalogTree", "共享")).setIcon(Buttons.getIcon("catalog"))
                    .setProperty("onclick", "catalog(this.value)");
            view.addButton(new CButton("取消共享", "cancelCatalog()"));
        }

        view.importJs("/ods/flow/instance.js");

        return view;
    }

    @Service(method = HttpMethod.post)
    public void catalog(Integer catalogId) throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.catalog(instanceIds, catalogId);
    }

    @Service(method = HttpMethod.post)
    public void cancelCatalog() throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.cancelCatalog(instanceIds);
    }
}
