package com.gzzm.portal.cms.stat;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author sjy
 * @date 2017/5/23
 */
@Service(url = "/portal/station/visitStat")
public class StationVisitStat extends StationCrud
{
    @Inject
    private ChannelVisitDao dao;

    @Inject
    private StationVisitConfig config;

    private Date time_start;

    private Date time_end;

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

    public StationVisitStat()
    {
    }

    @Override
    protected Object createListView() throws Exception
    {
        updateExtStationVisit();
        PageTableView view = new PageTableView(false);
        view.addComponent("站点名称", "stationName");
        view.addComponent("路径", "path");
        view.addComponent("类别", "type");
        view.addComponent("访问时间", "time_start", "time_end");

        view.addColumn("站点名称", "stationName").setWidth("150");
        view.addColumn("路径（域名）", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object o) throws Exception
            {
                Station station = (Station) o;
                String res = station.getPath();
                if (StringUtils.isNotEmpty(station.getDomainName()))
                {
                    res += "（" + station.getDomainName() + "）";
                }
                return res;
            }
        });
        view.addColumn("类别", "type");
        view.addColumn("访问量", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object o) throws Exception
            {
                Station station = (Station) o;

                if (!(Null.Date.equals(time_start)) || !(Null.Date.equals(time_end)))
                {
                    Integer visitTotal =
                            dao.getVisitTotalByRecord(0, station.getStationId(), time_start,
                                    time_end);
                    if (visitTotal == null)
                    {
                        visitTotal = 0;
                    }
                    return visitTotal;
                }
                else
                {
                    Integer visitTotal = dao.getVisitTotal(0, station.getStationId());

                    if (visitTotal == null)
                    {
                        visitTotal = 0;
                    }
                    return visitTotal;
                }
            }
        });
        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));
        return view;
    }

    private void updateExtStationVisit()
    {
        //更新统计数据不影响列表查询功能
        try
        {
            if (config != null)
            {
                if (config.getVisitId() != null && config.getStationId() != null)
                {
                    dao.updateStationVisitTotal(dao.queryVisitTotal(config.getVisitId()), config.getStationId());
                }
            }
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }

    @Override
    protected InputFile export(String exportFormat, ExportParameters parameters) throws Exception
    {
        parameters.setFileName("站点访问量统计");
        return super.export(exportFormat, parameters);
    }
}
