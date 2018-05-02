package com.gzzm.portal.eval;

import com.gzzm.platform.commons.crud.*;

/**
 * @author sjy
 * @date 2018/2/26
 */
public class EvalTypeDisplay extends BaseQueryCrud<EvalType,Integer>
{
    private EvalCatalog catalog;

    public EvalTypeDisplay()
    {
        addOrderBy("orderId");
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }

    public EvalCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(EvalCatalog catalog)
    {
        this.catalog = catalog;
    }
}
