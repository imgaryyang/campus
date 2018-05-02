package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Filter;

/**
 * 根据一个文本搜索部门名称对应的部门
 *
 * @author camel
 * @date 2010-8-7
 */
public class TextDeptFilter implements Filter<DeptInfo>
{
    private String text;

    public TextDeptFilter(String text)
    {
        this.text = text;
    }

    public boolean accept(DeptInfo dept) throws Exception
    {
        return Tools.matchText(dept.getDeptName(), text);
    }
}
