package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.PageConfig;
import net.cyan.commons.util.language.Language;
import net.cyan.crud.view.BasePanelView;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * @author camel
 * @date 2017/2/14
 */
public class PagePanelView extends BasePanelView implements PageView
{
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

    /**
     * 页面上显示的标题
     */
    private String pageTitle;

    /**
     * 页面上显示的说明
     */
    private String remark;

    /**
     * 组件列表
     */
    protected List<Component> components;

    /**
     * 更多组件列表
     */
    protected List<Component> moreComponents;

    /**
     * 按钮列表
     */
    protected List<Component> buttons;


    public PagePanelView()
    {
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
    public PagePanelView addTop(String s)
    {
        if (top == null)
            top = new StringBuilder();

        top.append(s);

        return this;
    }

    @SuppressWarnings("unchecked")
    public PagePanelView setDocType(String docType)
    {
        Object configDocType = PageConfig.getExtra("doctype");
        if (configDocType == null || "".equals(configDocType))
            addTop(docType + "\n");

        return this;
    }

    public PagePanelView setDocType()
    {
        return setDocType("<!DOCTYPE>");
    }

    @SuppressWarnings("unchecked")
    private PagePanelView writeToHeader(String s)
    {
        if (header == null)
            header = new StringBuilder();

        header.append(s);
        return this;
    }

    @SuppressWarnings("unchecked")
    private PagePanelView writeToBottom(String s)
    {
        if (bottom == null)
            bottom = new StringBuilder();

        bottom.append(s);
        return this;
    }


    public PagePanelView importJs(String path)
    {
        writeToBottom("<script src=\"");
        writeToBottom(Tools.getContextPath(path));
        return writeToBottom("\"></script>\n");
    }

    public PagePanelView importCss(String path)
    {
        writeToHeader("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        writeToHeader(Tools.getContextPath(path));
        return writeToHeader("\">\n");
    }

    public PagePanelView importLanguage(String path)
    {
        writeToBottom("<script>\n$.onload(function()\n{\nSystem.importLanguage(\"");
        writeToBottom(path);
        return writeToBottom("\");\n});\n</script>\n");
    }

    public PagePanelView run(String script)
    {
        writeToBottom("<script>\n$.onload(function()\n{\n");
        writeToBottom(script);
        return writeToBottom("\n});\n</script>\n");
    }

    public String getPageTitle() throws Exception
    {
        return pageTitle == null ? null : Language.getLanguage().getWord(pageTitle, pageTitle, getCrud());
    }

    public PagePanelView setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
        return this;
    }

    public String getRemark() throws Exception
    {
        return remark == null ? null : Language.getLanguage().getWord(remark, remark, getCrud());
    }

    public PagePanelView setRemark(String remark)
    {
        this.remark = remark;
        return this;
    }

    public <T extends Component> T addComponent(T component)
    {
        if (components == null)
            components = new ArrayList<Component>();

        components.add(component);

        return component;
    }

    public <T extends Component> T addComponent(String label, T component)
    {
        addComponent(new LabelComponent(label, component));
        return component;
    }

    public CData addComponent(String label, String expression)
    {
        return addComponent(label, new CData(expression));
    }

    public void addComponent(String label, Component start, Component end)
    {
        addComponent(label, new CBetween(start, end));
    }

    public void addComponent(String label, String start, String end)
    {
        addComponent(label, new CData(start), new CData(end));
    }

    public List<Component> getComponents()
    {
        return components;
    }

    public <T extends Component> T addMoreComponent(T component)
    {
        if (moreComponents == null)
            moreComponents = new ArrayList<Component>();

        moreComponents.add(component);

        return component;
    }

    public <T extends Component> T addMoreComponent(String label, T component)
    {
        addMoreComponent(new LabelComponent(label, component));
        return component;
    }

    public CData addMoreComponent(String label, String expression)
    {
        return addMoreComponent(label, new CData(expression));
    }

    public void addMoreComponent(String label, Component start, Component end)
    {
        addMoreComponent(label, new CBetween(start, end));
    }

    public void addMoreComponent(String label, String start, String end)
    {
        addMoreComponent(label, new CData(start), new CData(end));
    }

    public List<Component> getMoreComponents()
    {
        return moreComponents;
    }

    public <T extends Component> T addButton(T component)
    {
        if (component != null)
        {
            if (buttons == null)
                buttons = new ArrayList<Component>();

            buttons.add(component);
        }

        return component;
    }

    public CButton addButton(String text, Object action)
    {
        return action == null ? null : addButton(Buttons.getButton(text, action));
    }

    public List<Component> getButtons()
    {
        return buttons;
    }
}
