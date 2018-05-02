package com.gzzm.platform.flow;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.crud.view.components.Component;

import java.sql.Date;
import java.util.*;

/**
 * 内部流程管理
 *
 * @author camel
 * @date 12-11-1
 */
@Service
public abstract class FlowInstanceQuery<I extends SystemFlowInstance> extends DeptOwnedQuery<I, Long>
{
    private SystemFlowDao systemFlowDao;

    @Like
    private String title;

    @Lower(column = "startTime")
    private Date time_start;

    @Upper(column = "startTime")
    private Date time_end;

    private String creator;

    private String disposer;

    /**
     * 状态
     */
    private Integer state;

    private PageUserSelector creatorSelector;

    private PageUserSelector disposerSelector;

    public FlowInstanceQuery()
    {
        addOrderBy("startTime", OrderType.desc);
    }

    protected SystemFlowDao getSystemFlowDao() throws Exception
    {
        if (systemFlowDao == null)
            systemFlowDao = createSystemFlowDao();

        return systemFlowDao;
    }

    protected SystemFlowDao createSystemFlowDao() throws Exception
    {
        return DefaultSystemFlowDao.getInstance();
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

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getDisposer()
    {
        return disposer;
    }

    public void setDisposer(String disposer)
    {
        this.disposer = disposer;
    }

    public Integer getState()
    {
        return state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    @Select(field = "creator")
    public PageUserSelector getCreatorSelector()
    {
        if (creatorSelector == null)
        {
            creatorSelector = new PageUserSelector();
            creatorSelector.setAppId("");
            if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
            {
                creatorSelector.setRootId(getAuthDeptIds().iterator().next());
            }
        }

        return creatorSelector;
    }

    @Select(field = "disposer")
    public PageUserSelector getDisposerSelector()
    {
        if (disposerSelector == null)
        {
            disposerSelector = new PageUserSelector();
            disposerSelector.setAppId("");
            if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
            {
                disposerSelector.setRootId(getAuthDeptIds().iterator().next());
            }
        }

        return disposerSelector;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = null;

        if (state == null)
            s = "state>=0";

        if (!StringUtils.isEmpty(disposer))
        {
            if (s != null)
                s = " and ";
            else
                s = "";

            s += "(select 1 from " + systemFlowDao.getStepClass().getName() + " s where s.instanceId=" +
                    getAlias() + ".instanceId and s.receiver=:disposer and s.state in (0,1,2,6,7,15)) is not empty";
        }

        return s;
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = getAuthDeptIds() != null && getAuthDeptIds().size() == 1 ? new PageTableView(true) :
                new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("标题", "title");
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
        {
            view.addComponent("发起人", "creator");
            view.addComponent("处理人", "disposer");
        }
        view.addComponent("状态", new CCombox("state", new Object[]{
                new KeyValue<String>("0", "未结束"),
                new KeyValue<String>("1", "已结束"),
        }));
        view.addComponent("开始时间", "time_start", "time_end");

        view.addColumn("标题", new HrefCell("(title==null||title.length()==0)?'无标题':title")
                .setAction("display(${instanceId},'${flowTag}')")).setOrderFiled("title");
        view.addColumn("开始时间", new FieldCell("startTime", "yyyy-MM-dd HH:mm")).setWidth("120");
        view.addColumn("结束时间", new FieldCell("state==0?null:endTime", "yyyy-MM-dd HH:mm"))
                .setWidth("120").setOrderFiled("endTime").setAlign(Align.right);
        view.addColumn("状态", "state==0?'未结束':'已结束'").setWidth("60").setOrderFiled("state")
                .setAlign(Align.center);
        view.addColumn("流程类型", "flowTagName").setWidth("70").setAlign(Align.center).setOrderFiled("flowTag");
        view.addColumn("删除", new CButton("删除", "deleteInstance(${instanceId})")).setWidth("45");
        view.addColumn("办结", new CButton("办结", "stopInstance(${instanceId})")).setWidth("45");
        view.addColumn("环节维护", new CButton("环节维护", "control(${instanceId})")).setWidth("80");
        view.addColumn("意见维护", new CUnion()
        {
            @Override
            protected List<Component> getComponents(Object obj)
            {
                SystemFlowInstance instance = (SystemFlowInstance) obj;

                List<InstanceBody> bodies = instance.getBodies();

                if (bodies == null || bodies.size() == 0)
                {
                    if (instance.getBodyId() == null)
                        return null;
                    else
                        return Collections.<Component>singletonList(
                                new CButton("默认表单", "controlForm(" + instance.getBodyId() + ")"));
                }
                else
                {
                    List<Component> components = new ArrayList<Component>(bodies.size() + 1);

                    boolean defaultExists = false;
                    for (InstanceBody body : bodies)
                    {
                        if (instance.getBodyId() != null && body.getBodyId().equals(instance.getBodyId()))
                            defaultExists = true;

                        if (components.size() > 0)
                            components.add(new CBr());

                        components.add(new CButton(body.getTitle(), "controlForm(" + body.getBodyId() + ")"));
                    }

                    if (!defaultExists && instance.getBodyId() != null)
                        components.add(0, new CButton("默认表单", "controlForm(" + instance.getBodyId() + ")"));

                    if (components.size() > 0)
                        components.add(1, new CBr());

                    return components;
                }
            }
        }).setWidth("80");

        view.defaultInit(false);
        view.addButton("批量办结", "stopAll()");

        view.importJs("/platform/flow/instancequery.js");

        return view;
    }

    @Service
    @ObjectResult
    public Long getLastStepId(Long instanceId) throws Exception
    {
        return Long.valueOf(getSystemFlowDao().getLastStepId(instanceId.toString()));
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void deleteInstance(Long instanceId) throws Exception
    {
        FlowApi.getController(instanceId, getSystemFlowDao()).deleteInstance();
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void stopInstance(Long instanceId) throws Exception
    {
        FlowApi.getController(instanceId, getSystemFlowDao()).stopInstance();
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

    protected Class<? extends FlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        return FlowPage.class;
    }
}
