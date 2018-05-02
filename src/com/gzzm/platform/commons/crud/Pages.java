package com.gzzm.platform.commons.crud;

import java.util.*;

/**
 * @author camel
 * @date 2009-11-26
 */
public final class Pages
{
    public static final String PATH = "/platform/commons/";

    public static final String LIST = PATH + "list.ptl";

    public static final String TREE = PATH + "tree.ptl";

    public static final String TABLE_TREE = PATH + "tabletree.ptl";

    public static final String PANEL = PATH + "panel.ptl";

    public static final String COMPLEX = PATH + "complex.ptl";

    public static final String SIMPLE_LIST = PATH + "simplelist.ptl";

    public static final String TABLE = PATH + "table.ptl";

    public static final String EDIT = PATH + "edit.ptl";

    public static final String EDIT_DOUBLE = PATH + "edit2.ptl";

    public static final String EDIT_BIG = PATH + "edit_big.ptl";

    public static final String QUERY_DOUBLE = PATH + "query2.ptl";

    public static final String DIALOG_LIST = PATH + "dialog_list.ptl";

    public static final String QUERY = PATH + "query.ptl";

    public static final String SORT = PATH + "sort.ptl";

    public static final String IMP = PATH + "imp.ptl";

    public static final String CHART = PATH + "chart.ptl";

    public static final String PLAIN_CHART = PATH + "plain_chart.ptl";

    static final Map<String, String> PAGES = new HashMap<String, String>();

    static
    {
        PAGES.put("list", LIST);
        PAGES.put("complex", COMPLEX);
        PAGES.put("simple_list", SIMPLE_LIST);
        PAGES.put("table", TABLE);

        PAGES.put("tree", TREE);
        PAGES.put("table_tree", TABLE_TREE);

        PAGES.put("edit", EDIT);
        PAGES.put("double", EDIT_DOUBLE);
        PAGES.put("big", EDIT_BIG);

        PAGES.put("plain_chart", PLAIN_CHART);

        PAGES.put("panel", PANEL);
    }

    private Pages()
    {
    }
}
