package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.timeout.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * 来信统计
 *
 * @author camel
 * @date 12-11-22
 */
@Service(url = "/portal/inquiry/stat/dept")
public class InquiryStat extends DeptTreeStat
{
    @Inject
    private InquiryDao dao;

    @Inject
    private TimeoutService timeoutService;

    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    private Integer catalogId;

    private Integer wayId;

    private List<InquiryType> types;

    private List<TimeoutLevel> timeoutLevels;

    public InquiryStat()
    {
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

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public Integer getWayId()
    {
        return wayId;
    }

    public void setWayId(Integer wayId)
    {
        this.wayId = wayId;
    }

    private List<InquiryType> getTypes() throws Exception
    {
        if (types == null)
            types = dao.getTypes();

        return types;
    }

    private List<TimeoutLevel> getTimeoutLevels() throws Exception
    {
        if (timeoutLevels == null)
        {
            timeoutLevels = timeoutService
                    .getTimeoutLimits(getAuthDeptIds(), InquiryTimeout.ACCEPTID, InquiryTimeout.PROCESSID);
        }

        return timeoutLevels;
    }

    @Override
    protected void initStats() throws Exception
    {
        super.initStats();

        List<TimeoutLevel> timeoutLevels = getTimeoutLevels();

        join(InquiryProcess.class, "process", "process.deptId=dept.deptId and process.inquiry.deleted=0 " +
                "and process.inquiry.wayId=?wayId and process.inquiry.catalogId=?catalogId");

        if (timeoutLevels.size() > 0)
        {
            join(Timeout.class, "timeout",
                    "timeout.recordId=process.processId and timeout.valid=1 and timeout.typeId in ('" +
                            InquiryTimeout.ACCEPTID + "','" + InquiryTimeout.PROCESSID + "')"
            );
        }

        addStat("sendCount",
                "count(distinct processId,process.startTime>=?time_start and process.startTime<?time_end and processId is not null and previousProcessId is null)");
        addStat("turnCount",
                "count(distinct processId,process.startTime>=?time_start and process.startTime<?time_end and previousProcessId is not null)");

        addStat("processCount",
                "count(distinct processId,process.endTime>=?time_start and process.endTime<?time_end and process.state=2)");
        addStat("turnedCount",
                "count(distinct processId,process.endTime>=?time_start and process.endTime<?time_end and process.state=3)");
        addStat("processingCount", "count(process.state<2)");

        for (InquiryType type : getTypes())
        {
            addStat("type" + type.getTypeId(),
                    "count(distinct processId,process.startTime>=?time_start and process.startTime<?time_end and process.inquiry.typeId=" +
                            type.getTypeId() + ")"
            );
        }

        for (TimeoutLevel level : timeoutLevels)
        {
            addStat("timeout" + level.getLevelId(),
                    "count(distinct processId,timeout.timeoutTime>=?time_start and timeout.timeoutTime<?time_end and timeout.levelId=" +
                            level.getLevelId() + ")");
        }
    }

    @Override
    protected void initView(PageTreeTableView view) throws Exception
    {
        view.addComponent("统计时间", "time_start", "time_end");

        view.addColumn("部门名称", "deptName").setWidth("300");
        view.addColumn(new ColumnGroup("来信数",
                        new BaseColumn("直接来信", "sendCount").setWidth("100").setAlign(Align.center),
                        new BaseColumn("转自其他部门", "turnCount").setWidth("100").setAlign(Align.center))
        );
        view.addColumn("处理数", "processCount").setWidth("100").setAlign(Align.center);
        view.addColumn("已转部门数", "turnedCount").setWidth("100").setAlign(Align.center);
        view.addColumn("累计未处理数", "processingCount").setWidth("100").setAlign(Align.center);

        for (InquiryType type : getTypes())
        {
            view.addColumn(type.getTypeName() + "件", "type" + type.getTypeId()).setWidth("80").setAlign(Align.center);
        }

        for (TimeoutLevel level : getTimeoutLevels())
        {
            view.addColumn(level.getLevelName(), "timeout" + level.getLevelId()).setWidth("80").setAlign(Align.center);
        }

        view.defaultInit();
        view.addButton(Buttons.export("xls"));
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("来信统计");
    }
}
