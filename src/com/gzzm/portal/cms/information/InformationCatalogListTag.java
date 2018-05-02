package com.gzzm.portal.cms.information;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.commons.ListItemAdapter;
import com.gzzm.portal.tag.EntityQueryTag;
import net.cyan.crud.annotation.Equals;

/**
 * 信息目录标签
 *
 * @author camel
 * @date 12-9-4
 */
@Tag(name = "catalog")
public class InformationCatalogListTag extends EntityQueryTag<InformationCatalog, Integer>
{
    @Equals("parentCatalogId")
    private Integer catalogId = 0;

    private String channelCode;

    public InformationCatalogListTag()
    {
        addOrderBy("orderId");
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getChannelCode()
    {
        return channelCode;
    }

    public void setChannelCode(String channelCode)
    {
        this.channelCode = channelCode;
    }

    @Override
    protected Object transform(InformationCatalog entity) throws Exception
    {
        ListItemAdapter item = new ListItemAdapter(entity);

        item.setUrl("/channel/" + channelCode + "?catalogId=" + entity.getCatalogId());

        return item;
    }
}
