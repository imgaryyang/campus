package com.gzzm.portal.inquiry;

import java.util.List;

/**
 * @author camel
 * @date 2014/11/10
 */
public class InquiryConfig
{
    /**
     * 显示在列表的扩展属性
     */
    private List<InquiryAttribute> columns;

    /**
     * 作为查询条件的扩展属性
     */
    private List<InquiryAttribute> conditions;

    /**
     * 在显示页面显示的扩展属性
     */
    private List<InquiryAttribute> displayAttributes;

    /**
     * 更多查询条件
     */
    private List<InquiryAttribute> more;

    public InquiryConfig()
    {
    }

    public List<InquiryAttribute> getColumns()
    {
        return columns;
    }

    public void setColumns(List<InquiryAttribute> columns)
    {
        this.columns = columns;
    }

    public List<InquiryAttribute> getConditions()
    {
        return conditions;
    }

    public void setConditions(List<InquiryAttribute> conditions)
    {
        this.conditions = conditions;
    }

    public List<InquiryAttribute> getDisplayAttributes()
    {
        return displayAttributes;
    }

    public void setDisplayAttributes(List<InquiryAttribute> displayAttributes)
    {
        this.displayAttributes = displayAttributes;
    }

    public List<InquiryAttribute> getMore()
    {
        return more;
    }

    public void setMore(List<InquiryAttribute> more)
    {
        this.more = more;
    }
}
