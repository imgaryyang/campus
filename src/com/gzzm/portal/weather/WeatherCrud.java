package com.gzzm.portal.weather;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.OrganDao;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author lk
 * @date 13-9-25
 */
@Service(url = "/portal/weather")
public class WeatherCrud extends DeptOwnedNormalCrud<Weather, Integer>
{
    @Inject
    private WeatherConfig config;

    @Inject
    private OrganDao organDao;

    @Lower(column = "weatherDate")
    private Date startTime;

    @Upper(column = "weatherDate")
    private Date endTime;

    public WeatherCrud()
    {
        addOrderBy("weatherDate", OrderType.desc);
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("日期", "startTime", "endTime");

        view.addColumn("日期", "weatherDate");

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 1)
            view.addColumn("地区", "dept.deptName").setWidth("160");

        view.addColumn("内容", "content");
        view.addColumn("创建人", "creator.userName");
        view.addColumn("创建时间", "createTime");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 1)
        {
            view.addComponent("地区", "deptId")
                    .setProperty("text", getEntity().getDept() != null ? getEntity().getDept().getDeptName() : "");
        }

        view.addComponent("日期", "weatherDate");
        view.addComponent("内容", new CTextArea("content").setHeight("200px"));

        view.addDefaultButtons();

        view.importCss("/portal/weather/weather.css");

        view.setPage("big");

        return view;
    }

    @Override
    public void initEntity(Weather entity) throws Exception
    {
        entity.setWeatherDate(new Date(System.currentTimeMillis()));
        entity.setContent(config.getDefaultContent());

        entity.setDeptId(getDefaultDeptId());
        entity.setDept(organDao.getDept(entity.getDeptId()));
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreatorId(userOnlineInfo.getUserId());
        getEntity().setCreateTime(new java.util.Date());

        return true;
    }
}
