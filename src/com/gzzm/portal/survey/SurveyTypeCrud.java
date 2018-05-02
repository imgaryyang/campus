package com.gzzm.portal.survey;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.StationDisplay;
import com.gzzm.portal.commons.StationOwnedCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 调查类型管理
 *
 * @author camel
 * @date 13-11-13
 */
@Service(url = "/portal/survey/type")
public class SurveyTypeCrud extends StationOwnedCrud<SurveyType, Integer>
{
    @Like
    private String typeName;

    public SurveyTypeCrud()
    {
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;
        if (isOneStationOnly())
        {
            view = new PageTableView();
        }
        else
        {
            view = new ComplexTableView(Tools.getBean(StationDisplay.class), "stationId");
        }

        view.addComponent("类型名称", "typeName");

        view.addColumn("类型名称", "typeName");

        if (!isOneStationOnly())
            view.addColumn("所属网站", "station.stationName").setWidth("180");

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.importJs("/portal/survey/type.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("类型名称", "typeName");

        view.addDefaultButtons();

        return view;
    }
}
