package com.gzzm.portal.cms.station;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.ChannelTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Chinese;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 网站维护
 *
 * @author camel
 * @date 2011-5-3
 */
@Service(url = "/portal/station")
public class StationCrud extends DeptOwnedNormalCrud<Station, Integer>
{
    @Inject
    private StationDao dao;

    @UserId
    @NotSerialized
    private Integer userId;

    /**
     * 用网站名称做查询条件
     */
    @Like
    private String stationName;

    /**
     * 用网站路径做查询条件
     */
    private String path;

    private String type;

    private ChannelTreeModel channelTree;

    private List<StationTemplate> templates;

    public StationCrud()
    {
        setLog(true);
        addOrderBy("stationName");
    }

    public String getStationName()
    {
        return stationName;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getUserId()
    {
        return userId;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"type"};
    }

    @NotSerialized
    @NotCondition
    @Select(field = {"type", "entity.type"})
    public List<String> getTypes() throws Exception
    {
        return dao.getStationTypes();
    }

    @ObjectResult
    @Service
    public String generatePath()
    {
        return Chinese.getFirstLetters(getEntity().getStationName()).replaceAll("\\s", "");
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreator(userId);

        return true;
    }

    @Select(field = "entity.channelId")
    public ChannelTreeModel getChannelTree()
    {
        if (channelTree == null)
            channelTree = new ChannelTreeModel();

        return channelTree;
    }

    @Select(field = "entity.templateId")
    public List<StationTemplate> getTemplates() throws Exception
    {
        if (templates == null)
            templates = dao.getAllStationTemplates();

        return templates;
    }

    @Override
    public void initEntity(Station entity) throws Exception
    {
        super.initEntity(entity);

        entity.setValid(true);
    }

    @Override
    public void afterChange() throws Exception
    {
        Station.setUpdated();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("名称", "stationName");
        view.addComponent("路径", "path");
        view.addComponent("类别", "type");

        view.addColumn("名称", "stationName").setWidth("150");
        view.addColumn("域名", "domainName").setWidth("150");
        view.addColumn("主栏目", "channel.channelName").setWidth("120h");
        view.addColumn("路径", "path").setAutoExpand(true);
        view.addColumn("类别", "type");
        view.addColumn("所属部门", "dept.deptName").setOrderFiled("dept.leftValue");
        view.addColumn("创建人", "createUser.userName");
        view.addColumn("有效", "valid");

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.importJs("/portal/cms/station/station.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("站点名称", "stationName");
        view.addComponent("所属部门", "deptId")
                .setProperty("text", getEntity().getDept() == null ? "" : getEntity().getDept().getDeptName());
        view.addComponent("主栏目", "channelId")
                .setProperty("text", getEntity().getChannel() == null ? "" : getEntity().getChannel().getChannelName());
        view.addComponent("所用模板", "templateId");
        view.addComponent("站点标题", "title");
        view.addComponent("站点路径", "path");
        view.addComponent("域名", "domainName");
        view.addComponent("繁体域名", "gb5Domain");
        view.addComponent("网站标记码", "siteIDCode");
        view.addComponent("站点类别", "type").setProperty("editable", "true");
        view.addComponent("有效", "valid");

        view.addDefaultButtons();

        view.importCss("/portal/cms/station/station.css");
        view.importJs("/portal/cms/station/station.js");

        return view;
    }
}
