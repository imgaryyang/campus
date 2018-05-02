package com.gzzm.portal.commons;

import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.proteus.base.*;

/**
 * @author camel
 * @date 2018/1/25
 */
public class ExternalLinkTransformer extends BaseTagProcessor
{
    @Override
    public boolean doStart() throws Exception
    {
        String href = StringUtils.toString(getAttribute("href"));

        if (!StringUtils.isEmpty(href))
        {
            int index = href.indexOf("://");
            if (index > 0)
            {
                href = "/web/external.jsp?url=" + WebUtils.encode(href);

                setAttribute("href", href);
                setAttribute("target", "_blank");
            }
        }

        return super.doStart();
    }
}