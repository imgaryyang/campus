package com.gzzm.portal.tag;

import com.gzzm.platform.organ.Dept;
import com.gzzm.portal.annotation.Tag;
import net.cyan.commons.util.StringUtils;

/**
 * 部门列表的标签
 *
 * @author camel
 * @date 12-9-12
 */
@Tag(name = "dept")
public class DeptListTag extends EntityQueryTag<Dept, Integer>
{
    /**
     * 根部门ID
     */
    private Integer topDeptId;

    /**
     * 部门级别，只查询此级别的部门
     */
    private Integer level;

    /**
     * 更多的查询条件
     */
    private String where;

    public DeptListTag()
    {
    }

    public Integer getTopDeptId()
    {
        return topDeptId;
    }

    public void setTopDeptId(Integer topDeptId)
    {
        this.topDeptId = topDeptId;
    }

    public Integer getLevel()
    {
        return level;
    }

    public void setLevel(Integer level)
    {
        this.level = level;
    }

    public String getWhere()
    {
        return where;
    }

    public void setWhere(String where)
    {
        this.where = where;
    }

    @Override
    protected int getDefaultPageSize() throws Exception
    {
        //默认不分页
        return 0;
    }

    @Override
    protected String getQueryString() throws Exception
    {
        StringBuilder buffer = new StringBuilder("select d from Dept d");

        if (topDeptId != null)
        {
            buffer.append(" join Dept d1 on d.leftValue>=d1.leftValue and d.leftValue<d1.rightValue");
        }

        buffer.append(" where d.state=0");

        if (level != null)
            buffer.append(" and d.deptLevel=:level");

        if (topDeptId != null)
        {
            buffer.append(" and d1.deptId=:topDeptId");
        }

        if (!StringUtils.isEmpty(where))
            buffer.append(" and (").append(where).append(")");

        buffer.append(" order by d.leftValue");

        return buffer.toString();
    }
}
