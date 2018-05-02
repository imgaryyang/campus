package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.PageConfig;
import net.cyan.crud.Crud;

/**
 * 页面视图
 *
 * @author camel
 * @date 2009-11-8
 */
public abstract class AbstractPageView<T> implements PageView
{
    public static final String DEFAULTEDITFORWARD = "";

    private Crud crud;

    private String viewName;

    /**
     * 页面前的内容
     */
    private StringBuilder header;

    /**
     * 页面底部的内容
     */
    private StringBuilder bottom;

    /**
     * html标记之前的内容
     */
    private StringBuilder top;

    /**
     * 转向的页面
     */
    private String page;

    public AbstractPageView()
    {
    }

    public void setCrud(Crud crud)
    {
        this.crud = crud;
    }

    public Crud getCrud()
    {
        return crud;
    }

    public String getViewName()
    {
        return viewName;
    }

    public void setViewName(String viewName)
    {
        this.viewName = viewName;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public String getTop()
    {
        return top == null ? "" : top.toString();
    }

    public String getHeader()
    {
        return header == null ? null : header.toString();
    }

    public StringBuilder getBottom()
    {
        return bottom;
    }

    @SuppressWarnings("unchecked")
    public T addTop(String s)
    {
        if (top == null)
            top = new StringBuilder();

        top.append(s);

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setDocType(String docType)
    {
        Object configDocType = PageConfig.getExtra("doctype");
        if (configDocType == null || "".equals(configDocType))
            addTop(docType + "\n");

        return (T) this;
    }

    public T setDocType()
    {
        return setDocType("<!DOCTYPE>");
    }

    @SuppressWarnings("unchecked")
    private T writeToHeader(String s)
    {
        if (header == null)
            header = new StringBuilder();

        header.append(s);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    private T writeToBottom(String s)
    {
        if (bottom == null)
            bottom = new StringBuilder();

        bottom.append(s);
        return (T) this;
    }


    public T importJs(String path)
    {
        writeToBottom("<script src=\"");
        writeToBottom(Tools.getContextPath(path));
        return writeToBottom("\"></script>\n");
    }

    public T importCss(String path)
    {
        writeToHeader("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        writeToHeader(Tools.getContextPath(path));
        return writeToHeader("\">\n");
    }

    public T importLanguage(String path)
    {
        writeToBottom("<script>\n$.onload(function()\n{\nSystem.importLanguage(\"");
        writeToBottom(path);
        return writeToBottom("\");\n});\n</script>\n");
    }

    public T run(String script)
    {
        writeToBottom("<script>\n$.onload(function()\n{\n");
        writeToBottom(script);
        return writeToBottom("\n});\n</script>\n");
    }
}
