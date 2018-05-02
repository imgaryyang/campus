package com.gzzm.portal.cms.information;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.EntityTreeTag;

import java.util.Map;

/**
 * @author camel
 * @date 12-9-5
 */
@Tag(name = "catalogTree")
public class InformationCatalogTreeTag extends EntityTreeTag<InformationCatalog>
{
    public InformationCatalogTreeTag()
    {
    }

    @Override
    protected Object getRootKey(Map<String, Object> context)
    {
        return 0;
    }

    @Override
    protected String getDefaultFunctionName()
    {
        return "initCatalogTree";
    }
}
