package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.view.FieldCell;

/**
 * 显示文件名的单元格，独立为一个类，以重用
 *
 * @author camel
 * @date 2010-4-9
 */
public class DeptFileNameCell extends FieldCell
{
    public DeptFileNameCell()
    {
        //用fileName排序
        super("fileName");
    }

    @Override
    public String display(Object entity) throws Exception
    {
        DeptFile deptFile = (DeptFile) entity;

        //改用StringBuilder拼字符串，ccs
        StringBuilder html = new StringBuilder();

        //不使用表格，通过样式文件控制单元格的格式。ccs

        html.append("<div class=\"fileName");

        html.append("\">");

        //把获得文件图标的功能移到platform.commons.Tools，以供其他模块共用，ccs
        String iconUrl = Tools.getFileTypeIcon(deptFile.getFileType());
        html.append("<img class=\"fileTypeIcon\" src=\"");
        html.append(iconUrl).append("\">");

        html.append("<span>");

        html.append(deptFile.getFileName());

        //这里考虑fileType有可能为空
        if (!StringUtils.isEmpty(deptFile.getFileType()))
            html.append(".").append(deptFile.getFileType());

        html.append("</span>");
        html.append("</div>");

        return html.toString();
    }
}
