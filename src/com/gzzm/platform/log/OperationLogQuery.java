package com.gzzm.platform.log;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.*;
import net.cyan.crud.annotation.Contains;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.FieldCell;

import java.util.*;

/**
 * 日志查询
 *
 * @author camel
 * @date 2009-7-23
 */
@Service(url = "/OperationLog")
public class OperationLogQuery extends UserLogQuery<OperationLog>
{
    /**
     * 用action作为查询条件
     */
    private LogAction logAction;

    /**
     * 用目标作为查询条件
     */
    @Contains
    private String targetName;

    /**
     * 用说明做查询条件
     */
    @Contains
    private String remark;

    private String type;

    public OperationLogQuery()
    {
    }

    public LogAction getLogAction()
    {
        return logAction;
    }

    public void setLogAction(LogAction logAction)
    {
        this.logAction = logAction;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getType()
    {
        if ("all".equals(type))
            return null;
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public List<OrderBy> getOrderBys()
    {
        OrderBy orderBy = getOrderBy();
        if (orderBy != null)
        {
            if ("target".equals(orderBy.getName()))
            {
                List<OrderBy> orderBys = new ArrayList<OrderBy>(3);

                orderBys.add(new OrderBy("type", orderBy.getType()));
                orderBys.add(new OrderBy("targetName", orderBy.getType()));
                orderBys.add(new OrderBy("logTime", OrderType.desc));

                return orderBys;
            }
        }

        return super.getOrderBys();
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new OperationLogTypeDisplay(), "type", false);

        view.addComponent("操作者", "userName");
        view.addComponent("目标", "targetName");
        view.addComponent("操作时间", "time_start", "time_end");
        view.addComponent("说明", "remark");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.addColumn("操作者", "user.userName");
        view.addColumn("操作目标", "typeString+':'+targetName").setOrderFiled("target").setWidth("200");
        view.addColumn("动作", "logAction");
        view.addColumn("操作时间", "logTime");
        view.addColumn("IP", "ip").setHidden(true);
        view.addColumn("浏览器版本", "navigator").setHidden(true);
        view.addColumn("说明", new FieldCell("remarkDisplayString").setOrderable(false)).setAutoExpand(true)
                .setWrap(true);

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("操作日志");
    }
}
