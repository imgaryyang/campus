package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.components.EntityPageTreeModel;

/**
 * @author camel
 * @date 12-8-30
 */
public class InformationCatalogTreeModel extends EntityPageTreeModel<InformationCatalog>
{
    @Override
    protected Object getRootKey()
    {
        return 0;
    }

    @Override
    protected InformationCatalog createRoot() throws Exception
    {
        InformationCatalog catalog = new InformationCatalog();
        catalog.setCatalogId(0);
        catalog.setCatalogName("根节点");

        return catalog;
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
}
