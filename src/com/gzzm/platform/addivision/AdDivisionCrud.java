package com.gzzm.platform.addivision;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.nest.annotation.Inject;

/**
 * 行政区划维护
 *
 * @author camel
 * @date 2011-4-29
 */
@Service(url = "/adDivision")
public class AdDivisionCrud extends BaseTreeCrud<AdDivision, Integer>
{
    private static final EntityTreeOrganizer ORGANIZER = new NestedSetTreeOrganizer("leftValue", "rightValue");

    @Inject
    private AdDivisionDao dao;

    public AdDivisionCrud()
    {
        setLog(true);
        setDuplicateChildren(true);

        setOrganizer(ORGANIZER);
    }

    @Override
    public AdDivision getRoot() throws Exception
    {
        return dao.getRoot();
    }

    @Override
    public void setString(AdDivision division, String s) throws Exception
    {
        division.setDivisionName(s);
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        if ("exp".equals(getAction()))
        {
            PageTreeTableView view = new PageTreeTableView();

            view.setRootVisible(false);
            view.addColumn("行政区划名称", "divisionName");
            view.addColumn("行政区划代码", "divisionCode");
            return view;
        }
        else
        {
            PageTreeView view = new PageTreeView();
            view.defaultInit();
            view.addButton(Buttons.export("xls"));
            view.addButton(Buttons.imp());
            return view;
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("区划名称", "divisionName");
        view.addComponent("区划代码", "divisionCode");

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("行政区划列表");
    }

    @Override
    protected void initImportor(CrudEntityImportor<AdDivision, Integer> importor) throws Exception
    {
        super.initImportor(importor);

        importor.addColumnMap("名称", "divisionName");
        importor.addColumnMap("区划名称", "divisionName");
        importor.addColumnMap("行政区划名称", "divisionName");

        importor.addColumnMap("代码", "divisionCode");
        importor.addColumnMap("区划代码", "divisionCode");
        importor.addColumnMap("行政区划代码", "divisionCode");
    }
}
