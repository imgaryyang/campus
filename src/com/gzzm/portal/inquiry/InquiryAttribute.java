package com.gzzm.portal.inquiry;

/**
 * 来信属性，定义一些扩展的来信属性
 *
 * @author camel
 * @date 2014/11/10
 */
public class InquiryAttribute
{
    /**
     * 属性名称，对应PLINQUIRYATTRIBUTE表中的ATTRIBUTENAME字段
     *
     * @see com.gzzm.portal.inquiry.Inquiry#attributes
     */
    private String name;

    /**
     * 在界面上显示的属性名称
     */
    private String label;

    public InquiryAttribute()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
