package com.gzzm.ods.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.flow.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2017/7/25
 */
@Service(url = "/ods/flow/instance_manager")
public class OdFlowInstanceManager extends FlowInstanceQuery<SystemFlowInstance>
{
    @Inject
    private OdFlowService service;

    public OdFlowInstanceManager()
    {
    }

    protected SystemFlowDao getSystemFlowDao() throws Exception
    {
        return OdSystemFlowDao.getInstance();
    }

    @Override
    protected Class<? extends FlowPage> getFlowPageClass(String flowTag) throws Exception
    {
        return OdFlowService.getFlowPageClass(flowTag);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (PageTableView) super.createListView();

        view.importJs("/ods/flow/manager.js");

        return view;
    }

    @Override
    @Transactional
    public void deleteInstance(Long instanceId) throws Exception
    {
        super.deleteInstance(instanceId);

        OdFlowInstance instance = service.getDao().getOdFlowInstance(instanceId);

        String componentType = instance.getBusiness().getComponentType();
        if (!StringUtils.isEmpty(componentType))
        {
            OdFlowComponent flowComponent = (OdFlowComponent) Tools.getBean(Class.forName(componentType));
            if (flowComponent != null)
                flowComponent.deleteFlow(instance);
        }
    }


    @Override
    @Transactional
    public void stopInstance(Long instanceId) throws Exception
    {
        super.stopInstance(instanceId);

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
}
