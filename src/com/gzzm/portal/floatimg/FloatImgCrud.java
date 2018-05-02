package com.gzzm.portal.floatimg;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.commons.StationOwnedCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;

/**
 * 浮动图片维护
 *
 * @author fwj
 * @date 2011-7-14
 */
@Service(url = "/portal/floatimg")
public class FloatImgCrud extends StationOwnedCrud<FloatImg, Integer>
{
    @Like
    private String imgName;

    public FloatImgCrud()
    {
    }

    public String getImgName()
    {
        return imgName;
    }

    public void setImgName(String imgName)
    {
        this.imgName = imgName;
    }

    /**
     * 获得图片
     *
     * @param imgId 链接的ID
     * @return 链接图片的字节数组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/portal/floatimg/{$0}/photo")
    public byte[] getImg(Integer imgId) throws Exception
    {
        return getEntity(imgId).getImg();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        if (!isOneStationOnly())
            view.addComponent("所属网站", "stationId");

        view.addComponent("名称", "imgName");

        view.addColumn("名称", "imgName");

        if (!isOneStationOnly())
            view.addColumn("所属网站", "station.stationName");

        view.addColumn("链接地址", "url").setWidth("200");
        view.addColumn("有效", "valid");
        view.addColumn("链接图片", new CHref("${img==null?'':'查看'}").setAction("showImg(${imgId})"));

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.importJs("/portal/floatimg/floatimg.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "imgName");

        if (!isOneStationOnly())
        {
            view.addComponent("所属网站", "stationId").setProperty("text",
                    getEntity().getStation() == null ? "" : getEntity().getStation().getStationName());
        }

        view.addComponent("链接地址", "url");
        view.addComponent("链接图片", new CFile("img")).setFileType("$image");
        view.addComponent("有效", "valid");

        view.addDefaultButtons();

        if (!isOneStationOnly())
            view.importCss("/portal/floatimg/floatimg.css");

        return view;
    }
}
