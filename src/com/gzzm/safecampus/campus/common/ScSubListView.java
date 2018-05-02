package com.gzzm.safecampus.campus.common;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.Buttons;
import net.cyan.crud.view.BaseTableView;
import net.cyan.crud.view.components.CButton;
import net.cyan.crud.view.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 子列表，用于在父表的维护页面里嵌入子表的维护功能
 *
 * @author camel
 * @date 2010-12-7
 */
public class ScSubListView extends BaseTableView
{
    private String field;

    /**
     * 是否支持排序
     */
    private boolean orderable;

    private boolean readOnly;

    /**
     * 页面前的内容
     */
    private StringBuilder header;

    /**
     * 按钮列表
     */
    protected List<Component> buttons;

    protected boolean showAdd = true;

    public ScSubListView(String field)
    {
        this.field = field;
    }

    public String getName() throws Exception
    {
        return getPropertyWithLanguage(null, "name");
    }

    public void setName(String name)
    {
        setProperty("name", name);
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public boolean isOrderable()
    {
        return orderable;
    }

    public void setOrderable(boolean orderable)
    {
        this.orderable = orderable;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isShowAdd() {
        return showAdd;
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    public <C extends Component> C addButton(C component)
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

    public String getHeader()
    {
        return header == null ? null : header.toString();
    }

    @SuppressWarnings("unchecked")
    private ScSubListView writeToHeader(String s)
    {
        if (header == null)
            header = new StringBuilder();

        header.append(s);
        return this;
    }

    public ScSubListView importCss(String path)
    {
        writeToHeader("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        writeToHeader(Tools.getContextPath(path));
        return writeToHeader("\">\n");
    }
}