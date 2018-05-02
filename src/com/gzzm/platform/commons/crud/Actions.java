package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.StringUtils;
import net.cyan.crud.*;
import net.cyan.crud.util.ValueGetter;

/**
 * 通用的动作列表
 *
 * @author camel
 * @date 2009-11-4
 */
public final class Actions
{
    private final static Object DELETE = new ValueGetter()
    {
        @SuppressWarnings("unchecked")
        public Object get(Object obj) throws Exception
        {
            if (obj instanceof Crud)
                return "del()";
            else
                return "del('" + ((KeyBaseCrud) CrudConfig.getContext().getCrud()).getKey(obj) + "')";
        }
    };

    static String[] EXPORTTYPES = {"xls", "csv", "doc"};

    private Actions()
    {
    }

    public static Object query()
    {
        return "query()";
    }

    public static Object add(String forward)
    {
        return CrudAuths.isAddable() ? (forward == null ? "add()" : "add('" + forward + "')") : null;
    }

    public static Object delete()
    {
        return CrudAuths.isDeletable() ? delete0() : null;
    }

    public static Object delete0()
    {
        return DELETE;
    }

    public static Object show(final String forward)
    {
        return new ValueGetter()
        {
            @SuppressWarnings("unchecked")
            public Object get(Object obj) throws Exception
            {
                if (obj instanceof Crud)
                    return "show(null,'" + (forward == null ? "" : forward) + "')";
                else
                    return "show('" + ((KeyBaseCrud) CrudConfig.getContext().getCrud()).getKey(obj) + "','" +
                            (forward == null ? "" : forward) + "')";
            }
        };
    }

    public static Object duplicate(final String forward)
    {
        return CrudAuths.isModifiable() ? new ValueGetter()
        {
            @SuppressWarnings("unchecked")
            public Object get(Object obj) throws Exception
            {
                if (obj instanceof Crud)
                    return "duplicate(null,'" + (forward == null ? "" : forward) + "')";
                else
                    return "duplicate('" + ((KeyBaseCrud) CrudConfig.getContext().getCrud()).getKey(obj) +
                            "','" + (forward == null ? "" : forward) + "')";
            }
        } : null;
    }

    public static Object save()
    {
        return (CrudAuths.isAddable() || CrudAuths.isModifiable()) ? "save()" : null;
    }

    public static Object close()
    {
        return "closeWindow()";
    }

    public static Object export(String type)
    {
        return "exp('" + type + "')";
    }

    public static Object export(String... types)
    {
        if (!CrudAuths.isExportable())
            return null;

        if (types == null || types.length == 0)
            return "exp()";

        StringBuilder buffer = new StringBuilder("exp(");
        boolean first = true;
        for (String type : types)
        {
            if (first)
                first = false;
            else
                buffer.append(",");
            buffer.append("'").append(type).append("'");
        }

        buffer.append(")");

        return buffer.toString();
    }

    public static Object print(String type)
    {
        if (!CrudAuths.isExportable())
            return null;
        return type == null ? "print()" : "print('" + type + "')";
    }

    public static Object exportEntity(final String type)
    {
        return new ValueGetter()
        {
            @SuppressWarnings("unchecked")
            public Object get(Object obj) throws Exception
            {
                return "exportEntity('" + ((KeyBaseCrud) CrudConfig.getContext().getCrud()).getKey(obj) + "','" + type +
                        "')";
            }
        };
    }

    public static Object exportEntity()
    {
        return exportEntity("doc");
    }

    public static Object exportEntitys(String type)
    {
        return "exportEntitys('" + type + "')";
    }

    public static Object exportEntitys()
    {
        return exportEntitys("doc");
    }

    public static Object imp()
    {
        if (!CrudAuths.isImpable())
            return null;
        return "showImp()";
    }

    public static Object up()
    {
        return CrudAuths.isModifiable() ? "up()" : null;
    }

    public static Object down()
    {
        return CrudAuths.isModifiable() ? "down()" : null;
    }

    public static Object move()
    {
        if (CrudAuths.isModifiable())
            return CrudAuths.isAddable() ? "move(0)" : "move(1)";
        else
            return CrudAuths.isAddable() ? "move(2)" : null;
    }

    public static Object moreQuery(String forward)
    {
        return forward == null ? "moreQuery(\"\")" : "moreQuery(\"" + forward + "\");";
    }

    public static Object moreQuery()
    {
        return moreQuery("query");
    }

    public static Object moreQuery2(String forward)
    {
        return forward == null ? "moreQuery2(\"\")" : "moreQuery2(\"" + forward + "\");";
    }

    public static Object moreQuery2()
    {
        return moreQuery("query");
    }

    public static Object moveToLeft()
    {
        return moveToLeft(true);
    }

    public static Object moveToLeft(boolean copyable)
    {
        if (CrudAuths.isModifiable())
            return CrudAuths.isAddable() && copyable ? "moveToLeft(0)" : "moveToLeft(1)";
        else
            return CrudAuths.isAddable() ? "moveToLeft(2)" : null;
    }

    public static Object sort(String forward)
    {
        return CrudAuths.isModifiable() ? "showSortList('" + forward + "')" : null;
    }

    public static Object leftSelect(String field)
    {
        return "leftSelect('" + field + "')";
    }

    public static Object more()
    {
        return "more()";
    }

    /**
     * 显示子视图，主要用来显示一个图表
     *
     * @param name 视图的名称，如chart
     * @return 显示子视图的动作脚本
     */
    public static Object showSubView(String name)
    {
        return "showSubView('" + name + "')";
    }

    public static Object ok()
    {
        return "ok()";
    }

    public static Object cancel()
    {
        return "cancel()";
    }

    public static Object copy(String name)
    {
        if (StringUtils.isEmpty(name))
            return "clone()";
        else
            return "clone(null,'" + name + "')";
    }

    public static Object copy()
    {
        return copy(null);
    }

    public static Object paste(String name)
    {
        if (StringUtils.isEmpty(name))
            return "paste()";
        else
            return "paste(null,'" + name + "')";
    }

    public static Object paste()
    {
        return paste(null);
    }
}
