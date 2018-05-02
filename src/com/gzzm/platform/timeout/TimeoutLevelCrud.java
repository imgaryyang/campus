package com.gzzm.platform.timeout;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.*;

/**
 * @author camel
 * @date 12-8-16
 */
@Service(url = "/timeout/level")
public class TimeoutLevelCrud extends BaseNormalCrud<TimeoutLevel, Integer>
{
    public TimeoutLevelCrud()
    {
        setLog(true);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("级别名称", "levelName");
        view.addColumn("警告颜色", new CLabel("").setBackgroundColor("${color}").setWidth(100).setHeight(17)
                .setProperty("style", "float:left")).setWidth("120");
        view.addColumn("警告图标", new CImage("/timeout/level/icon/${levelId}").setWidth(17).setHeight(17)
                .setProperty("style", "cursor:pointer"));

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("级别名称", "levelName");
        view.addComponent("警告颜色", "color").setProperty("datatype", "color");
        view.addComponent("警告图标", new CFile("icon")).setFileType("$image");
        view.addComponent("当前图标", new CImage(getEntity().getIcon() == null ? Tools.getCommonIcon("blank") :
                "/timeout/level/icon/" + getEntity().getLevelId()).setProperty("id", "icon").setWidth(17)
                .setHeight(17));

        view.addDefaultButtons();

        return view;
    }

    @Service(url = "/timeout/level/icon/{$0}")
    public byte[] getIcon(Integer levelId) throws Exception
    {
        return getEntity(levelId).getIcon();
    }
}
