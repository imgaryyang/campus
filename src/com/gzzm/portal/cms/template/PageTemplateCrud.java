package com.gzzm.portal.cms.template;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 页面模版维护
 *
 * @author camel
 * @date 2011-5-10
 */
@Service(url = "/portal/pagetemplate")
public class PageTemplateCrud extends BaseNormalCrud<PageTemplate, Integer>
{
    @Inject
    private StationDao stationDao;

    @UserId
    private Integer userId;

    /**
     * 用模版名称做查询条件
     */
    @Like
    private String templateName;

    /**
     * 用路径做查询条件
     */
    @Like
    private String path;

    /**
     * 用所属站点做查询条件
     */
    @NotCondition
    private Integer stationId;

    /**
     * 用模版类型做查询条件
     */
    private PageTemplateType type;

    /**
     * 用url做查询条件
     */
    @Like
    private String url;

    public PageTemplateCrud()
    {
        addOrderBy("station.stationName");
        addOrderBy("templateName");

        setLog(true);
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public PageTemplateType getType()
    {
        return type;
    }

    public void setType(PageTemplateType type)
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (stationId != null)
        {
            if (stationId == -1)
                return "stationId is null";
            else
                return "stationId=?stationId";
        }

        return null;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreator(userId);

        Integer stationId = getEntity().getStationId();
        if (stationId == null || stationId == -1)
            getEntity().setStationId(Null.Integer);

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        Integer stationId = getEntity().getStationId();
        if (stationId == null || stationId == -1)
            getEntity().setStationId(Null.Integer);

        return true;
    }

    @Override
    public void initEntity(PageTemplate entity) throws Exception
    {
        super.initEntity(entity);

        entity.setType(PageTemplateType.PAGE);
    }

    @Override
    public String check() throws Exception
    {
        PageTemplateType type = getEntity().getType();
        if (type == PageTemplateType.MAIN)
        {
            //主页和其他页面，必须属于一个站点
            Integer stationId = getEntity().getStationId();
            if (stationId == null || stationId == -1)
            {
                return "portal.cms.pageWithoutStation";
            }

            PageTemplate mainPage = stationDao.getMainPage(stationId);
            if (mainPage != null && !mainPage.getTemplateId().equals(getEntity().getTemplateId()))
                return "portal.cms.mainPageExist";
        }

        return super.check();
    }

    @Override
    public void afterChange() throws Exception
    {
        PageTemplate.setUpdated();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("页面名称", "templateName");
        view.addComponent("页面路径", "path");
        view.addComponent("URL", "url");
        view.addComponent("所属网站", "stationId");
        view.addComponent("类型", "type");

        view.addColumn("页面名称", "templateName");
        view.addColumn("页面类型", "type");
        view.addColumn("页面路径", "path").setWidth("200");
        view.addColumn("URL", "url").setWidth("200");
        view.addColumn("所属网站", "station.stationName").setWidth("150");
        view.addColumn("创建人", "createUser.userName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("页面名称", "templateName");
        view.addComponent("页面类型", "type");
        view.addComponent("页面路径", "path");
        view.addComponent("URL", "url");
        view.addComponent("所属网站", "stationId").setProperty("editable", "true");

        view.addDefaultButtons();

        return view;
    }

    @NotSerialized
    @Select(field = {"stationId", "entity.stationId"})
    public List<Station> getStations() throws Exception
    {
        List<Station> stations = stationDao.getAllStations();

        stations.add(0, new Station(-1, "适用于所有站点"));

        return stations;
    }
}
