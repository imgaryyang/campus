package com.gzzm.platform.flow.worksheet;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 11-10-27
 */
public class WorkSheetCatalogDisplay extends BaseTreeDisplay<WorkSheetCatalog, Integer>
{
    @Inject
    private WorkSheetCatalogDao dao;

    private String type;

    @UserId
    private Integer userId;

    public WorkSheetCatalogDisplay()
    {
        addOrderBy("orderId");
    }

    @Override
    public WorkSheetCatalog getRoot() throws Exception
    {
        return new WorkSheetCatalog(0, "全部收藏文件");
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
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }
}
