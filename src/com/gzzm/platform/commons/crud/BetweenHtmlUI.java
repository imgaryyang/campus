package com.gzzm.platform.commons.crud;

import net.cyan.crud.view.UIManager;
import net.cyan.crud.view.html.AbstractComponentHtmlUI;

/**
 * labelbetween 组件的 html ui
 *
 * @author camel
 * @date 2009-10-21
 */
public class BetweenHtmlUI extends AbstractComponentHtmlUI<CBetween>
{
    public BetweenHtmlUI()
    {
    }

    public Object display(CBetween component, Object obj, String expression, UIManager uiManager) throws Exception
    {
        String html_start = component.getStart().display(obj, expression, uiManager).toString();
        String html_end = component.getEnd().display(obj, expression, uiManager).toString();

        return html_start + "<span> - </span>" + html_end;
    }
}
