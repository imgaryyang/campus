package com.gzzm.portal.cms.visit;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import com.gzzm.portal.cms.template.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.nest.annotation.*;

import java.sql.*;

/**
 * 访问记录统计菜单，菜单参数必须传递type参数
 *
 * @author sjy
 * @date 2018/3/16
 */
@Service(url = "/visit/visitDayQuery")
public class VisitQueryPage extends BaseNormalCrud<VisitDayTotal, Long>
{
    /**
     * 访问类型 0为网站，2为页面
     */
    private Integer type;

    @Lower(column = "visitDay")
    private Date visitDay_start;

    @Upper(column = "visitDay")
    private Date visitDay_end;

    @Inject
    private VisitDao dao;

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        view.addComponent("访问时间", "visitDay_start", "visitDay_end");
        if (0 == type)
        {
            view.addColumn("站点名称", new BaseSimpleCell()
            {
                @Override
                public Object getValue(Object entity) throws Exception
                {
                    return null;
                }

                @Override
                public String display(Object entity) throws Exception
                {
                    try
                    {
                        VisitDayTotal record = (VisitDayTotal) entity;
                        Integer stationId = record.getObjectId();
                        return dao.load(Station.class, stationId).getStationName();
                    } catch (Exception e)
                    {
                        Tools.log(e);
                        return "";
                    }
                }
            });
        }
        else if (2 == type)
        {
            view.addColumn("页面名称", new BaseSimpleCell()
            {
                @Override
                public Object getValue(Object entity) throws Exception
                {
                    return null;
                }

                @Override
                public String display(Object entity) throws Exception
                {
                    try
                    {
                        VisitDayTotal record = (VisitDayTotal) entity;
                        Integer templateId = record.getObjectId();
                        return dao.load(PageTemplate.class, templateId).getTemplateName();
                    } catch (Exception e)
                    {
                        Tools.log(e);
                        return "";
                    }
                }
            });

        }
        view.addColumn("访问量", "visitCount").setAlign(Align.center);
        view.addColumn("点击量", "clickCount").setAlign(Align.center);
        view.addButton(Buttons.query());
        return view;
    }

    @Override
    protected String getQueryString() throws Exception
    {
        String oql = "select t.objectId as recordId, t.objectId as objectId,sum(t.visitCount) as visitCount,sum(t.clickCount) as clickCount ";
        oql += "from com.gzzm.portal.cms.visit.VisitDayTotal t where t.type=:type and t.visitDay>=?visitDay_start and t.visitDay<addDay(?visitDay_end,1) ";
        if (0 == type)
        {
            oql += " and t.objectId in (select s.stationId from com.gzzm.portal.cms.station.Station s) ";
        }
        oql += " group by t.objectId order by t.objectId";
        return oql;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Date getVisitDay_start()
    {
        return visitDay_start;
    }

    public void setVisitDay_start(Date visitDay_start)
    {
        this.visitDay_start = visitDay_start;
    }

    public Date getVisitDay_end()
    {
        return visitDay_end;
    }

    public void setVisitDay_end(Date visitDay_end)
    {
        this.visitDay_end = visitDay_end;
    }
}
