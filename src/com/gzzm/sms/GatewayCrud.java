package com.gzzm.sms;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.ExceptionUtils;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * 短信网关维护
 *
 * @author camel
 * @date 2010-11-4
 */
@Service(url = "/sms/gateway")
public class GatewayCrud extends BaseNormalCrud<Gateway, Integer>
{
    @Inject
    private SmsClients clients;

    /**
     * 网关名称
     */
    @Like
    private String gatewayName;

    public GatewayCrud()
    {
        setLog(true);
        addOrderBy("gatewayName");
    }

    public String getGatewayName()
    {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName)
    {
        this.gatewayName = gatewayName;
    }

    public GatewayState getState(Integer gatewayId) throws Exception
    {
        SmsClientItem item = clients.getItem(gatewayId);

        return item == null ? GatewayState.stoped : item.getState();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        Gateway gateway = getEntity();
        if (gateway.getPort() == null)
            gateway.setPort(gateway.getGatewayClass().getDefaultPort());

        return true;
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        if (getEntity().isAvailable())
        {
            Tools.run(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        clients.reset(getEntity().getGatewayId());
                    }
                    catch (Throwable ex)
                    {
                        ExceptionUtils.logException(ex);
                    }
                }
            });
        }
        else
        {
            Tools.run(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        clients.stop(getEntity().getGatewayId());
                    }
                    catch (Throwable ex)
                    {
                        ExceptionUtils.logException(ex);
                    }
                }
            });
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("网关名称", "gatewayName");

        view.addColumn("名称", "gatewayName");
        view.addColumn("类型", "gatewayClass");
        view.addColumn("IP", "ip").setWidth("100");
        view.addColumn("端口", "port");
        view.addColumn("用户名", "userName").setWidth("100");
        view.addColumn("特服号", "spNumber").setWidth("100");
        view.addColumn("有效", "available").setWidth("50");
        view.addColumn("状态", "crud$.getState(gatewayId)").setWidth("60").setAlign(Align.center);
        view.addColumn("启动/停止", new ConditionComponent()
                .add("available&&(crud$.getState(gatewayId).name()=='stoped'||crud$.getState(gatewayId).name()=='error')",
                        new CButton("启动", "start(${gatewayId})"))
                .add("available&&crud$.getState(gatewayId).name()=='running'",
                        new CButton("停止", "stop(${gatewayId})")));

        view.defaultInit();
        view.importJs("/sms/gateway.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("网关名称", "gatewayName");
        view.addComponent("网关类型", "gatewayClass");
        view.addComponent("服务器IP", "ip");
        view.addComponent("服务器端口", "port");
        view.addComponent("接收端口", "receivePort");
        view.addComponent("用户名", "userName");
        view.addComponent("密码", new CPassword("password"));
        view.addComponent("特服号", "spNumber");
        view.addComponent("企业代码", "spId");
        view.addComponent("节点ID", "args.nodeId");
        view.addComponent("是否有效", "available");

        view.addDefaultButtons();

        return view;
    }

    @Service
    @ObjectResult
    public void stop(Integer gatewayId) throws Exception
    {
        clients.stop(gatewayId);
    }

    @Service
    @ObjectResult
    public void start(Integer gatewayId) throws Exception
    {
        clients.reset(gatewayId);
    }
}
