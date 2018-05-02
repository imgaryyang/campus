package com.gzzm.platform.flow;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 流程信息维护
 *
 * @author camel
 * @date 2011-7-4
 */
@Service(url = "/flowinfo")
public class FlowInfoCrud extends DeptOwnedNormalCrud<FlowInfo, Integer>
{
    @Inject
    private FlowInfoDao dao;

    /**
     * 用流程名称做查询条件
     */
    @Like
    private String flowName;

    /**
     * 获取当前用户ID
     */
    @UserId
    @NotCondition
    private Integer userId;

    /**
     * 用流程类型做查询条件
     */
    private FlowType type;

    public FlowInfoCrud()
    {
        addOrderBy("flowName", OrderType.asc);
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public FlowType getType()
    {
        return type;
    }

    public void setType(FlowType type)
    {
        this.type = type;
    }

    public Integer getUserId()
    {
        return userId;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "published=?false";
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
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("流程名称", "flowName");
        view.addComponent("流程类型", "type");

        view.addColumn("流程名称", "flowName");
        view.addColumn("流程类型", "type");
        view.addColumn("创建人", "createUser.userName");
        view.addColumn("发布", new CButton("发布", "publish(${flowId})"));
        view.addColumn("下载", new CHref("下载", "/flow/${flowId}/down").setTarget("_blank"));

        view.defaultInit();
        view.importJs("/platform/flow/flowinfo.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("流程名称", "flowName");
        view.addComponent("流程类型", "type");
        view.addComponent("上传文件", new CFile("flow").setFileType("xml"));
        view.addDefaultButtons();

        return view;
    }

    @Override
    public FlowInfo clone(FlowInfo entity) throws Exception
    {
        FlowInfo c = super.clone(entity);

        c.setIeFlowId(null);

        return c;
    }

    /**
     * 保存默认值
     *
     * @return ：执行成功
     * @throws Exception
     */
    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreator(userId);
        getEntity().setPublishTime(new Date());
        getEntity().setPublished(false);
        getEntity().setUsed(false);
        getEntity().setUpdateTime(new Date());

        return super.beforeInsert();
    }

    /**
     * @param flowId :流程Id
     * @return :下载的xml文件
     * @throws Exception :
     */
    @Service(url = "/flow/{$0}/down")
    public InputFile down(Integer flowId) throws Exception
    {
        FlowInfo flowInfo = getEntity(flowId);
        String flowName = flowInfo.getFlowName() + ".xml";

        if (flowInfo.getFlow() != null)
            return new InputFile(new String(flowInfo.getFlow()).getBytes("UTF-8"), flowName);
        return null;
    }

    /**
     * 获得流程的最后一个版本
     *
     * @param flowId 流程ID
     * @return 要发布的流程的Id
     * @throws Exception 数据库操作异常
     */
    @Service
    public FlowInfo getLastFlow(Integer flowId) throws Exception
    {
        FlowInfo flowInfo = dao.getFlowInfo(flowId);

        return dao.getLastFlow(flowInfo.getIeFlowId());
    }

    /**
     * 发布后，自动生成一个新版本的流程。版本号为数据库中相同ieFlowId的记录的最大的version加1
     *
     * @param flowId 要发布的流程的Id
     * @param cover  是否覆盖原来的版本
     * @return 新的版本号
     * @throws Exception 数据库操作异常
     */
    @Service
    public Integer publish(Integer flowId, boolean cover) throws Exception
    {
        FlowInfo flowInfo = dao.getFlowInfo(flowId);

        if (cover)
        {
            FlowInfo lastFlowInfo = dao.getLastFlow(flowInfo.getIeFlowId());

            lastFlowInfo.setFlowName(flowInfo.getFlowName());
            lastFlowInfo.setFlow(flowInfo.getFlow());
            lastFlowInfo.setUpdateTime(new Date());
            lastFlowInfo.setType(flowInfo.getType());

            dao.update(lastFlowInfo);

            return lastFlowInfo.getVersion();
        }
        else
        {
            FlowInfo newFlowInfo = new FlowInfo();

            newFlowInfo.setFlowName(flowInfo.getFlowName());
            newFlowInfo.setIeFlowId(flowInfo.getIeFlowId());
            newFlowInfo.setType(flowInfo.getType());

            Integer version = dao.getMaxVersion(flowInfo.getIeFlowId());
            if (version != null)
            {
                newFlowInfo.setVersion(++version);
            }
            else
            {
                newFlowInfo.setVersion(version = 1);
            }

            newFlowInfo.setFlow(flowInfo.getFlow());
            newFlowInfo.setCreator(userId);
            newFlowInfo.setPublishTime(new Date());
            newFlowInfo.setPublished(true);
            newFlowInfo.setUsed(false);
            newFlowInfo.setDeptId(flowInfo.getDeptId());
            newFlowInfo.setUpdateTime(new Date());

            dao.add(newFlowInfo);

            return version;
        }
    }
}
