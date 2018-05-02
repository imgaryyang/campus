package com.gzzm.safecampus.campus.base;

import net.cyan.commons.util.DataConvert;
import net.cyan.crud.view.UIManager;
import net.cyan.crud.view.html.AbstractComponentHtmlUI;
import net.cyan.crud.view.html.HtmlUIUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author Neo
 * @date 2018/4/10 11:22
 */
public class HourMinuteHtmlUI extends AbstractComponentHtmlUI<CHourMinute>
{
    public HourMinuteHtmlUI()
    {
    }

    @Override
    public Object display(CHourMinute component, Object obj, String expression, UIManager uiManager) throws Exception
    {
        Map<String, String> properties;
        Integer hour = component.getHourValue(obj);
        String hourFormat = component.getHourFormat();
        String hourWidth = component.getHourWidth();
        Integer minute = component.getMinuteValue(obj);
        String minuteFormat = component.getMinuteFormat();
        String minuteWidth = component.getMinuteWidth();

        StringBuilder buffer = new StringBuilder();

        buffer.append("<div style=\"float:left\">");

        component.setWidth(StringUtils.isEmpty(hourWidth) ? "60px" : hourWidth);
        properties = HtmlUIUtils.getProperties(component, obj, null);
        properties.put("name", component.getHourExpression(expression));
        if (hour != null)
            properties.put("value", Integer.toString(hour));
        HtmlUIUtils.createHtml("select", properties, buffer).append("\n");

        if (component.isNullable())
        {
            buffer.append("<option value=\"\">----</option>");
        }

        for (int h = 0; h < 24; h++)
        {
            Object hourStr = StringUtils.isEmpty(hourFormat) ? h : DataConvert.format(h, hourFormat);
            buffer.append("<option value=\"").append(h).append("\"");
            if (hour != null && h == hour)
                buffer.append(" selected");
            buffer.append(">").append(hourStr).append("</option>\n");
        }

        buffer.append("\n</select>");

        buffer.append("</div>");

        buffer.append("<div style=\"float:left;padding-left:5px;padding-right:5px;\">:</div>");

        buffer.append("<div style=\"float:left\">");

        component.setWidth(StringUtils.isEmpty(minuteWidth) ? "60px" : minuteWidth);
        properties = HtmlUIUtils.getProperties(component, obj, null);
        properties.put("name", component.getMinuteExpression(expression));
        if (minute != null)
            properties.put("value", Integer.toString(minute));
        HtmlUIUtils.createHtml("select", properties, buffer).append("\n");

        if (component.isNullable())
        {
            buffer.append("<option value=\"\">--</option>");
        }

        for (int m = 0; m < 60; m++)
        {
            Object minuteStr = StringUtils.isEmpty(minuteFormat) ? m : DataConvert.format(m, minuteFormat);
            buffer.append("<option value=\"").append(m).append("\"");
            if (minute != null && m == minute)
                buffer.append(" selected");
            buffer.append(">").append(minuteStr).append("</option>\n");
        }

        buffer.append("\n</select>");

        buffer.append("</div>");

        return buffer.toString();
    }
}
