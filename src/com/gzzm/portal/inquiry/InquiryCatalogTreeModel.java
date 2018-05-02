package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.components.EntityPageTreeModel;

/**
 * @author camel
 * @date 12-11-11
 */
public class InquiryCatalogTreeModel extends EntityPageTreeModel<InquiryCatalog>
{
    public InquiryCatalogTreeModel()
    {
    }

    @Override
    protected Object getRootKey()
    {
        return 0;
    }

    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "catalogName";
    }

    @Override
    protected InquiryCatalog createRoot() throws Exception
    {
        InquiryCatalog catalog = new InquiryCatalog();
        catalog.setCatalogId(0);
        catalog.setCatalogName("根节点");

        return catalog;
    }
}
