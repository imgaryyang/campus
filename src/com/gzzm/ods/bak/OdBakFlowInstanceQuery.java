package com.gzzm.ods.bak;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.CollectionPageListModel;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author camel
 * @date 12-4-26
 */
@Service(url = "/ods/bak/instance_query")
public class OdBakFlowInstanceQuery extends DeptOwnedQuery<OdFlowInstance, Long>
{
    @Inject
    private OdDeptInstanceBakDao dao;

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

    private Integer bakId;

    private CollectionPageListModel baks;

    public OdBakFlowInstanceQuery()
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

    public Integer getBakId()
    {
        return bakId;
    }

    public void setBakId(Integer bakId)
    {
        this.bakId = bakId;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "state<2";

        if (bakId != null)
        {
            s += " and instanceId in (select instanceId from OdDeptInstanceBak b,b.instances i where b.backId=:bakId)";
        }

        return s;
    }

    public CollectionPageListModel getBaks() throws Exception
    {
        if (baks == null)
        {
            baks = new CollectionPageListModel<OdDeptInstanceBak>(dao.getBaks(getDeptId()));
        }

        return baks;
    }

    public CollectionPageListModel getBaks1() throws Exception
    {
        return getBaks();
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
        PageTableView view = new PageTableView(true);

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

        if (bakId == null)
        {
            view.addButton(new CMenuButton("baks", "添加选择的公文到压缩包")).setIcon(Buttons.getIcon("catalog"))
                    .setProperty("onclick", "bak(this.value)");
            view.addButton(new CMenuButton("baks1", "添加查询到的公文到压缩包")).setIcon(Buttons.getIcon("catalog"))
                    .setProperty("onclick", "bakAll(this.value)");
        }
        else
        {
            view.addButton("从备份包删除", "cancelBak()");
        }

        view.importJs("/ods/bak/instance.js");

        return view;
    }

    @Service(method = HttpMethod.post)
    public void bak(Integer bakId) throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.bak(instanceIds, bakId);
    }

    @Service(method = HttpMethod.post)
    public void bakAll(Integer bakId) throws Exception
    {
        dao.bak(getList(), bakId);
    }

    @Service(method = HttpMethod.post)
    public void cancelBak() throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds == null || instanceIds.length == 0)
            return;

        dao.cancelBak(instanceIds, bakId);
    }
}
