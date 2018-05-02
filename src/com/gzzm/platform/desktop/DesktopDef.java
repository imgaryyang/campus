package com.gzzm.platform.desktop;

import java.io.Serializable;
import java.util.List;

/**
 * 桌面定义
 *
 * @author camel
 * @date 2010-6-3
 */
public class DesktopDef implements Serializable
{
    private static final long serialVersionUID = 503999244818583980L;

    /**
     * 记录桌面上每一列的功能模块，每一个元素为一个DesktopColumn对象，其中的modules记录此列中的功能模块
     */
    private List<DesktopColumn> columns;

    public DesktopDef()
    {
    }

    public List<DesktopColumn> getColumns()
    {
        return columns;
    }

    public void setColumns(List<DesktopColumn> columns)
    {
        this.columns = columns;
    }
}
