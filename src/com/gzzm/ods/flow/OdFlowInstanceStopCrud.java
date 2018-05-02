package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
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
@Service(url = "/ods/flow/instance_stop")
public class OdFlowInstanceStopCrud extends DeptOwnedQuery<OdFlowInstance, Long>
{
    @Inject
    private OdFlowService service;

    private Long[] keys;

    /**
     * 类型
     */
    private String[] type;

    @Equals("business.tag")
    private BusinessTag tag;

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

    public OdFlowInstanceStopCrud()
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
        return "state=0";
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (authDeptIds == null || authDeptIds.size() > 3)
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
        }
        else
        {
            view = new PageTableView(true);
        }

        view.addComponent("标题", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("拟稿时间", "time_start", "time_end");

        if (authDeptIds != null && authDeptIds.size() > 1 && authDeptIds.size() <= 3)
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
        view.addColumn("类型", "typeName").setWidth("100");
        view.addColumn("拟稿时间/收文时间", "startTime").setWidth("140");
        view.addColumn("拟稿人/接收人", "createUser.userName").setWidth("90");
        view.addColumn("办结", new CButton("办结", "stopInstance(${instanceId})")).setWidth("45");

        view.addButton(Buttons.query());
        view.addButton("批量办结", "stopAll()");

        view.importJs("/ods/flow/instance.js");

        return view;
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void stopInstance(Long instanceId) throws Exception
    {
        FlowApi.getController(instanceId, OdSystemFlowDao.getInstance()).stopInstance();

        OdFlowInstance instance = service.getDao().getOdFlowInstance(instanceId);

        String componentType = instance.getBusiness().getComponentType();
        if (!StringUtils.isEmpty(componentType))
        {
            OdFlowComponent flowComponent = (OdFlowComponent) Tools.getBean(Class.forName(componentType));
            if (flowComponent != null)
                flowComponent.stopFlow(instance);
        }

        service.stopInstance(instance, userOnlineInfo.getUserId(), userOnlineInfo.getDeptId());
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void stopAll() throws Exception
    {
        Long[] instanceIds = getKeys();
        if (instanceIds != null)
        {
            for (Long instanceId : instanceIds)
            {
                stopInstance(instanceId);
            }
        }
    }
}
