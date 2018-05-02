package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 通用的按钮
 *
 * @author camel
 * @date 2009-11-4
 */
public final class Buttons
{
    private Buttons()
    {
    }

    public static String getIcon(String name)
    {
        if (name == null)
            return null;

        return Tools.getCommonIcon(name);
    }

    public static CButton getButton(String text, Object action)
    {
        if (action == null)
            return null;

        return new CButton(text, action);
    }

    public static CButton getButton(String text, Object action, String icon)
    {
        return getButton(text, action, icon, null);
    }

    public static CButton getButton(String text, Object action, String icon, String id)
    {
        if (action == null)
            return null;

        CButton button = new CButton(text, action);
        if (icon != null)
            button.setIcon(getIcon(icon));

        if (id != null)
            button.setProperty("id", id);

        return button;
    }

    public static CButton query(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.query";

        return getButton(text, Actions.query(), "query", "btn_query");
    }

    public static CButton query()
    {
        return query(null);
    }

    public static CButton stat()
    {
        return query("crud.stat");
    }

    public static CButton moreQuery(String forward, String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.moreQuery";

        return getButton(text, Actions.moreQuery(forward), "query", "btn_more_query");
    }

    public static CButton moreQuery(String forward)
    {
        return moreQuery(forward, null);
    }

    public static CButton moreQuery()
    {
        return moreQuery("query");
    }

    public static CButton moreQuery2(String forward, String text)
    {
        return getButton(text, Actions.moreQuery2(forward), "query", "btn_more_query");
    }

    public static CButton moreQuery2(String forward)
    {
        return moreQuery2(forward, "crud.moreQuery");
    }

    public static CButton moreQuery2()
    {
        return moreQuery2("query");
    }

    public static CButton add(String forward, String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.add";

        return getButton(text, Actions.add(forward), "add", "btn_add");
    }

    public static CButton add(String forward)
    {
        return add(forward, null);
    }

    public static CButton add()
    {
        return add(null);
    }

    public static CButton edit(String forward, String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.modify";

        return getButton(text, Actions.show(forward), "edit");
    }

    public static CButton edit(String forward)
    {
        return edit(forward, null);
    }

    public static CButton edit()
    {
        return edit(null);
    }

    public static AbstractComponent<?> editImg(String forward)
    {
        return editImg(forward, "crud.modify");
    }

    public static AbstractComponent<?> editImg(String forward, String text)
    {
        return getEditStyleImg(Actions.show(forward), text);
    }

    public static AbstractComponent<?> editImg()
    {
        return editImg(null);
    }

    public static CButton duplicate(String forward, String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.duplicate";

        return getButton(text, Actions.duplicate(forward), "duplicate");
    }

    public static CButton duplicate(String forward)
    {
        return duplicate(forward, null);
    }

    public static CButton duplicate()
    {
        return duplicate(null);
    }

    public static AbstractComponent<?> duplicateImg(String forward, String title)
    {
        return getCopyStyleImg(Actions.duplicate(forward), title);
    }

    public static AbstractComponent<?> duplicateImg(String forward)
    {
        return duplicateImg(forward, "crud.duplicate");
    }

    public static AbstractComponent<?> duplicateImg()
    {
        return duplicateImg(null);
    }

    public static AbstractComponent<?> getEditStyleImg(Object action, String text)
    {
        return action == null ? null : new CImage(null, action).setClass("crud-button-edit").setPrompt(text);
    }

    public static AbstractComponent<?> getCopyStyleImg(Object action, String text)
    {
        return action == null ? null : new CImage(null, action).setClass("crud-button-copy").setPrompt(text);
    }

    public static CButton delete(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.delete";

        return getButton(text, Actions.delete(), "delete", "btn_delete");
    }

    public static CButton delete()
    {
        return delete(null);
    }

    public static CButton save(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.save";

        return getButton(text, Actions.save(), null, "btn_save");
    }

    public static CButton save()
    {
        return save(null);
    }

    public static CButton close(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.close";

        return getButton(text, Actions.close(), null, "btn_close");
    }

    public static CButton close()
    {
        return close(null);
    }

    public static CButton export(String text, String[] types)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.export";

        if (types != null && types.length == 1)
        {
            return getButton(text, Actions.export(types), types[0], "btn_export_" + types[0]);
        }
        else if (CrudAuths.isExportable())
        {

            if (types == null || types.length == 0)
                types = Actions.EXPORTTYPES;

            List<CMenuItem> items = new ArrayList<CMenuItem>(types.length);
            for (String type : types)
                items.add(new CMenuItem(type, Actions.export(type)));

            return new CMenuButton(null, text, items).setIcon(getIcon("export")).setProperty("id", "btn_export");
        }

        return null;
    }

    public static CButton export(String... types)
    {
        return export(null, types);
    }

    public static CButton exportEntitys(String type)
    {
        return exportEntitys(null, type);
    }

    public static CButton exportEntitys(String text, String type)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.export";

        return getButton(text, Actions.exportEntitys(type), type);
    }

    public static CButton print(String text, String type)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.print";

        return getButton(text, Actions.print(type), "print", "btn_print");
    }

    public static CButton print(String type)
    {
        return print(null, type);
    }

    public static CButton print()
    {
        return print(null);
    }

    public static CButton imp(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.imp";

        return getButton(text, Actions.imp(), "imp", "btn_imp");
    }

    public static CButton imp()
    {
        return imp(null);
    }

    public static CButton down(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.down";

        return getButton(text, Actions.down(), "down", "btn_down");
    }

    public static CButton down()
    {
        return down(null);
    }

    public static CButton up(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.up";

        return getButton(text, Actions.up(), "up", "btn_up");
    }

    public static CButton up()
    {
        return up(null);
    }

    public static CButton sort(String forward, String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.sort";

        if (forward == null)
            forward = "";

        return getButton(text, Actions.sort(forward), "sort", "btn_sort");
    }

    public static CButton sort(String forward)
    {
        return sort(forward, null);
    }

    public static CButton sort()
    {
        return sort("", null);
    }

    public static CButton showSubView(String name, String icon, String id)
    {
        name = Tools.getMessage(name);
        return getButton(Tools.getMessage("crud.hideSubView", new Object[]{name}), Actions.showSubView(name), icon, id);
    }

    public static CButton showSubView(String text)
    {
        return showSubView(text, null, "btn_showSubView");
    }

    public static CButton showChart()
    {
        return showChart(null);
    }

    public static CButton showChart(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.chart";

        return showSubView(text, "chart", "btn_showChart");
    }

    public static CButton showPieChart()
    {
        return showPieChart(null);
    }

    public static CButton showPieChart(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.chart";

        return showSubView(text, "chart_pie", "btn_showChart");
    }

    public static CButton showLineChart()
    {
        return showLineChart(null);
    }

    public static CButton showLineChart(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.chart";

        return showSubView(text, "chart_line", "btn_showChart");
    }

    public static CButton showCurveChart()
    {
        return showCurveChart(null);
    }

    public static CButton showCurveChart(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.chart";

        return showSubView(text, "chart_curve", "btn_showChart");
    }

    public static CButton showBarChart()
    {
        return showBarChart(null);
    }

    public static CButton showBarChart(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.chart";

        return showSubView(text, "chart_bar", "btn_showChart");
    }

    public static CButton ok(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.ok";

        return new CButton(text, Actions.ok()).setProperty("id", "btn_ok");
    }

    public static CButton ok()
    {
        return ok(null);
    }

    public static CButton cancel(String text)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.cancel";

        return new CButton(text, Actions.cancel()).setProperty("id", "btn_cancel");
    }

    public static CButton cancel()
    {
        return cancel(null);
    }

    public static CButton copy(String text, String name)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.copy";

        return getButton(text, Actions.copy(name), "copy", "btn_copy");
    }

    public static CButton copy(String text)
    {
        return copy(text, null);
    }

    public static CButton copy()
    {
        return copy(null, null);
    }

    public static CButton paste(String text, String name)
    {
        if (StringUtils.isEmpty(text))
            text = "crud.paste";

        return getButton(text, Actions.paste(name), "paste", "btn_paste");
    }

    public static CButton paste(String text)
    {
        return paste(text, null);
    }

    public static CButton paste()
    {
        return paste(null, null);
    }

    public static CButton setId(CButton button, String id) throws Exception
    {
        if (button != null && id != null)
            button.setProperty("id", id);

        return button;
    }
}
