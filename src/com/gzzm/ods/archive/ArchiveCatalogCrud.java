package com.gzzm.ods.archive;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author camel
 * @date 2016/8/27
 */
@Service(url = "/ods/archive/catalog")
public class ArchiveCatalogCrud extends DeptOwnedEditableCrud<ArchiveCatalog, Integer>
{
    private Integer year;

    @Like
    private String catalogName;

    public ArchiveCatalogCrud()
    {
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"year", "deptId"};
    }

    @Override
    public void initEntity(ArchiveCatalog entity) throws Exception
    {
        super.initEntity(entity);

        entity.setYear(year);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("目录名称", "catalogName");
        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("目录名称", "catalogName");

        view.addColumn("案件目录名称", "catalogName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }
}
