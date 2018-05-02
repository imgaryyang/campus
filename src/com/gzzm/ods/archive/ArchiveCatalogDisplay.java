package com.gzzm.ods.archive;

import com.gzzm.platform.commons.crud.*;

/**
 * @author camel
 * @date 2016/8/28
 */
public class ArchiveCatalogDisplay extends DeptOwnedQuery<ArchiveCatalog, Integer>
{
    private Integer year;

    public ArchiveCatalogDisplay()
    {
        addOrderBy("orderId");
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        ArchiveCatalog all = new ArchiveCatalog();
        all.setCatalogId(-1);
        all.setCatalogName("所有归档文件");
        getList().add(0, all);
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }
}
