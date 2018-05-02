package com.gzzm.portal.link;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.commons.StationOwnedCrud;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 友情链接维护
 *
 * @author camel
 * @date 2011-5-30
 */
@Service(url = "/portal/link/link")
public class LinkCrud extends StationOwnedCrud<Link, Integer>
{
    private static String[] ORDERWITHFIELDS = new String[]{"stationId", "type"};

    @Inject
    private LinkDao dao;

    @UserId
    @NotSerialized
    private Integer userId;

    /**
     * 通过名称过滤友情链接
     */
    @Like
    private String linkName;

    /**
     * 通过类别过滤友情链接
     */
    private String type;

    public LinkCrud()
    {
        setLog(true);
    }

    public Integer getUserId()
    {
        return userId;
    }

    public String getLinkName()
    {
        return linkName;
    }

    public void setLinkName(String linkName)
    {
        this.linkName = linkName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @NotSerialized
    @NotCondition
    @Select(field = {"type", "entity.type"})
    public List<String> getTypes() throws Exception
    {
        Integer stationId = null;

        if (getEntity() != null)
            stationId = getEntity().getStationId();

        if (stationId == null)
            stationId = getStationId();

        if (stationId == null)
            return Collections.emptyList();

        return dao.getLinkTypes(stationId);
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public void initEntity(Link entity) throws Exception
    {
        super.initEntity(entity);

        entity.setStationId(getStationId());
        entity.setType(getType());
    }

    /**
     * 获得图片
     *
     * @param linkId 链接的ID
     * @return 链接图片的字节数组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/portal/link/link/{$0}/photo")
    public byte[] getPhoto(Integer linkId) throws Exception
    {
        return getEntity(linkId).getPhoto();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        if (!isOneStationOnly())
        {
            view.addComponent("所属网站", "stationId").setProperty("onchange", "stationChange();");
        }

        view.addComponent("名称", "linkName");
        view.addComponent("类别", "type").setWidth("120px");

        view.addColumn("名称", "linkName");
        view.addColumn("URL", "url").setWidth("230");

        if (!isOneStationOnly())
            view.addColumn("所属网站", "station.stationName").setWidth("160");

        view.addColumn("类别", "type");

        view.addColumn("链接图片", new CHref("${photo==null?'':'查看'}").setAction("showPhoto(${linkId})"));

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.importJs("/portal/link/link.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        if (!isOneStationOnly())
        {
            view.addComponent("所属网站", "stationId").setProperty("text",
                    getEntity().getStation() == null ? "" : getEntity().getStation().getStationName())
                    .setProperty("onchange", "stationChange();");
        }

        view.addComponent("链接名称", "linkName");
        view.addComponent("URL", "url");
        view.addComponent("类别", "type").setProperty("editable", "true");
        view.addComponent("链接图片", new CFile("photo")).setFileType("$image");

        view.importJs("/portal/link/link.js");

        if (!isOneStationOnly())
            view.importCss("/portal/link/link.css");

        view.addDefaultButtons();

        return view;
    }
}
