package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.GlobalConfig;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author camel
 * @date 12-12-12
 */
@Service(url = "/portal/informationquery")
public class InformationQuery extends BaseQueryCrud<Information, Integer>
{
    @Inject
    private GlobalConfig globalConfig;

    @NotCondition
    private Integer[] channelIds;

    private boolean descendant;

    private String page;

    @Lower(column = "updateTime")
    private Date time_start;

    @Upper(column = "updateTime")
    private Date time_end;

    public InformationQuery()
    {
        addOrderBy("updateTime", OrderType.desc);
    }

    public Integer[] getChannelIds()
    {
        return channelIds;
    }

    public void setChannelIds(Integer[] channelIds)
    {
        this.channelIds = channelIds;
    }

    public boolean isDescendant()
    {
        return descendant;
    }

    public void setDescendant(boolean descendant)
    {
        this.descendant = descendant;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
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

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (descendant)
        {
            return "channelId in (select c.channelId from Channel c join Channel c0 on " +
                    "c.leftValue>=c0.leftValue and c.leftValue<c0.rightValue where c0.channelId in :channelIds)";
        }
        else
        {
            return "channelId in :channelIds";
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        if ("list".equals(page))
        {
            SimplePageListView view = new SimplePageListView()
            {
                @Override
                public Object display(Object obj) throws Exception
                {
                    Information information = (Information) obj;

                    return "<span class=\"title_trunc " + "\"><a href=\"#\" onclick=\"return display(" +
                            information.getInformationId() + ")\"  title=\"" +
                            HtmlUtils.escapeAttribute(information.getTitle()) + "\">" + information.getTitle() +
                            "</a></span>" + "<span class=\"time\">" +
                            DateUtils.toString(information.getUpdateTime(), "yyyy-MM-dd") + "</span>";
                }
            };

            view.addAction(Action.more());

            view.importJs("/portal/cms/information/list.js");
            view.importCss("/portal/cms/information/list.css");

            return view;
        }
        else
        {
            PageTableView view = new PageTableView();

            view.addComponent("标题", "title");
            view.addComponent("更新时间", "time_start", "time_end");
            view.addButton(Buttons.query());

            view.addColumn("标题", new HrefCell("title").setAction("display(${informationId})"));
            view.addColumn("发布时间", "publishTime");
            view.addColumn("更新时间", "updateTime");
            view.addColumn("发布部门", "dept.deptName");

            view.importJs("/portal/cms/information/list.js");
            view.importCss("/portal/cms/information/list.css");

            return view;
        }
    }
}
