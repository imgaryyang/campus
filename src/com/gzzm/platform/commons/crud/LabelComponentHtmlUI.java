package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.HtmlUtils;
import net.cyan.crud.view.UIManager;
import net.cyan.crud.view.html.*;

import java.util.Map;

/**
 * LabelComponent的html ui
 *
 * @author camel
 * @date 2009-10-21
 */
public class LabelComponentHtmlUI extends AbstractComponentHtmlUI<LabelComponent>
{
    public LabelComponentHtmlUI()
    {
    }

    public Object display(LabelComponent component, Object obj, String expression, UIManager uiManager) throws Exception
    {
        String html = component.getComponent().display(obj, expression, uiManager).toString();

        String label = component.getLabel(obj);

        if (component.getProperties() != null && component.getProperties().size() > 0)
        {
            Map<String, String> properties = HtmlUIUtils.getProperties(component, obj, null);
            return HtmlUIUtils.createHtml("span", properties) + HtmlUtils.escapeHtml(label) + ":</span>" + html;
        }
        else
        {
            return HtmlUtils.escapeHtml(label) + ":" + html;
        }
    }
}