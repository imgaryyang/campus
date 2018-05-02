package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;

/**
 * @author lfx
 * @date 2010-3-26
 */
public class MailCatalogDisplay extends BaseQueryCrud<MailCatalog, Long>
{
    /**
     * 用户ID，邮件拥有者的ID
     */
    @UserId
    private Integer userId;

    public Integer getUserId()
    {
        return userId;
    }

    public MailCatalogDisplay()
    {
        addOrderBy("orderId");
    }

    @Override
    protected Object createView() throws Exception
    {
        return new SelectableListView();
    }
}
