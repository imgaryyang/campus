package com.gzzm.portal.commons;

import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.proteus.TemplateContext;
import net.cyan.proteus.base.*;

/**
 * @author camel
 * @date 2018/2/9
 */
public class ExternalLinkValueTransformer implements TagValueTransformer
{
    public static TagAttribute.ValueFilter valueFilter = new TagAttribute.ValueFilter()
    {
        @Override
        public Object filter(Object value, TemplateContext context) throws Exception
        {
            if (value != null)
            {
                String href = StringUtils.toString(value);
                int index = href.indexOf("://");
                if (index > 0)
                {
                    href = "/web/external.jsp?url=" + WebUtils.encode(href);
                    value = href;
                }

            }

            return value;
        }
    };

    public ExternalLinkValueTransformer()
    {
    }

    @Override
    public Object transform(Object value, String expression, TagProcessor tagProcessor) throws Exception
    {
        if ("a".equalsIgnoreCase(tagProcessor.getTagName()) && ExternalLinkTransformerFactory.exists())
        {
            TagInfo tagInfo = tagProcessor.getTagInfo();
            TagAttribute attribute = tagInfo.getAttributeInfo("href");

            if (attribute != null)
            {
                boolean el = false;
                for (AttributeItem item : attribute.getValue().getValue())
                {
                    if (item.isEl())
                    {
                        el = true;
                        break;
                    }
                }

                if (el)
                {
                    attribute.setFilter(valueFilter);
                }
                else
                {
                    String href = attribute.getValue().getString();
                    if (!StringUtils.isEmpty(href))
                    {
                        int index = href.indexOf("://");
                        if (index > 0)
                        {
                            href = "/web/external.jsp?url=" + WebUtils.encode(href);

                            attribute.setString(href);
                            tagInfo.setAttribute("target", "_blank");
                        }
                    }
                }
            }
        }

        return null;
    }
}
