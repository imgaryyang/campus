package com.gzzm.platform.flow;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CButton;

/**
 * 内部流程管理
 *
 * @author camel
 * @date 12-11-1
 */
@Service(url = "/flow/instance/bakquery")
public class FlowInstanceBakQuery extends FlowInstanceQuery<FlowInstanceBak>
{
    public FlowInstanceBakQuery()
    {
    }

    protected SystemFlowDao getSystemFlowDao() throws Exception
    {
        return BakSystemFlowDao.getInstance();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = getAuthDeptIds() != null && getAuthDeptIds().size() == 1 ? new PageTableView(false) :
                new ComplexTableView(new AuthDeptDisplay(), "deptId", false);

        view.addComponent("标题", "title");
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
        {
            view.addComponent("发起人", "creator");
            view.addComponent("处理人", "disposer");
        }
        view.addComponent("开始时间", "time_start", "time_end");

        view.addColumn("标题",
                new HrefCell("(title==null||title.length()==0)?'无标题':title")
                        .setAction("display(${instanceId},'${flowTag}')")
        ).setOrderFiled("title");
        view.addColumn("开始时间", new FieldCell("startTime", "yyyy-MM-dd HH:mm")).setWidth("120");
        view.addColumn("结束时间", new FieldCell("state==0?null:endTime", "yyyy-MM-dd HH:mm"))
                .setWidth("120").setOrderFiled("endTime").setAlign(Align.right);
        view.addColumn("状态", "state==0?'未结束':'已结束'").setWidth("60").setOrderFiled("state")
                .setAlign(Align.center);
        view.addColumn("流程类型", "flowTagName").setWidth("70").setAlign(Align.center);
        view.addColumn("恢复", new CButton("恢复", "receoverInstance(${instanceId})")).setWidth("45");

        view.defaultInit(false);

        view.importJs("/platform/flow/instancebak.js");

        return view;
    }

    @Service
    @ObjectResult
    public void receoverInstance(Long instanceId) throws Exception
    {
        ((BakSystemFlowDao) getSystemFlowDao()).recover(instanceId);
    }
}
