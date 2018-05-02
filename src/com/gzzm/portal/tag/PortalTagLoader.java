package com.gzzm.portal.tag;

import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.xml.*;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author camel
 * @date 2011-6-14
 */
public class PortalTagLoader implements XmlLoader.ElementLoader
{
    public PortalTagLoader()
    {
    }

    public void load(Element element, XmlLoader loader) throws Exception
    {
        String name = element.getAttribute("name");
        final String query = XmlUtils.getElementValue(element).trim();
        final String database = element.hasAttribute("database") ? element.getAttribute("database") : null;
        String type = element.getAttribute("type");

        if (!StringUtils.isEmpty(name))
        {
            if ("oql".equals(type))
            {
                PortalTagContainer.add(name, new OQLTag<Object>()
                {
                    @Override
                    protected String getQueryString(Map<String, Object> parameters) throws Exception
                    {
                        return query;
                    }

                    @Override
                    protected String getDatabase(Map<String, Object> parameters) throws Exception
                    {
                        return database;
                    }
                });
            }
            else if ("sql".equals(type))
            {
                final boolean nativeSql = "true".equals(element.getAttribute("native"));

                PortalTagContainer.add(name, new SQLTag<Map>()
                {
                    @Override
                    protected String getQueryString(Map<String, Object> parameters) throws Exception
                    {
                        return query;
                    }

                    @Override
                    protected boolean isNative(Map<String, Object> parameters) throws Exception
                    {
                        return nativeSql;
                    }

                    @Override
                    protected String getDatabase(Map<String, Object> parameters) throws Exception
                    {
                        return database;
                    }
                });
            }
        }
    }
}
