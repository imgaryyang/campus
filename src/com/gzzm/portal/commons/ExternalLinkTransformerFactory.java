package com.gzzm.portal.commons;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.proteus.BaseTemplateParser;
import net.cyan.proteus.base.*;

import java.io.File;

/**
 * 判断外部链接，转向提醒页面
 *
 * @author camel
 * @date 2018/1/25
 */
public class ExternalLinkTransformerFactory implements TagProcessorFactory
{
    public ExternalLinkTransformerFactory()
    {
        PageConfig.addForwardInterceptor(new ForwardInterceptor()
        {
            @Override
            public void start(ForwardContext context, boolean action) throws Exception
            {
            }

            @Override
            public void end(ForwardContext context, boolean action) throws Exception
            {
                if (exists())
                    context.importJs("/web/external.js");
            }
        });
    }

    public static boolean exists()
    {
        return new File(Tools.getAppPath("/web/external.jsp")).exists();
    }

    @Override
    public Class<? extends TagProcessor> getProcessor(TagInfo tagInfo, BaseTemplateParser.Context context)
    {
        if (!tagInfo.containsKey("bind") && "a".equalsIgnoreCase(tagInfo.getTagName()) && exists())
        {
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

                tagInfo.setTransformed(true);

                if (el)
                    return ExternalLinkTransformer.class;

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

        return null;
    }
}
