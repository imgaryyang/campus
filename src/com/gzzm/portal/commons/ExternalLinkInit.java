package com.gzzm.portal.commons;

import net.cyan.arachne.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.proteus.*;
import net.cyan.proteus.base.TagContext;

/**
 * @author camel
 * @date 2018/2/9
 */
public class ExternalLinkInit implements TemplateConfiger
{
    public ExternalLinkInit()
    {
    }

    @Override
    public void config(TemplateContext context)
    {
        TagContext tagContext = TagContext.getTagContext(context);

        tagContext.addFactory(new ExternalLinkTransformerFactory());
        tagContext.addValueTransformer(new ExternalLinkValueTransformer());

        PageConfig.setRedirectFilter(new RedirectFilter()
        {
            @Override
            public String filter(String href) throws Exception
            {
                if (!StringUtils.isEmpty(href))
                {
                    int index = href.indexOf("://");
                    if (index > 0)
                    {
                        href = "/web/external.jsp?url=" + WebUtils.encode(href);
                    }
                }

                return href;
            }
        });
    }
}
