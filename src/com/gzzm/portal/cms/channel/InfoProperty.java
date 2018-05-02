package com.gzzm.portal.cms.channel;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 信息扩展属性定义，定义某个栏目具有哪些扩展属性
 *
 * @author camel
 * @date 2011-5-11
 */
@Entity(table = "PLINFOPROPERTY", keys = "propertyId")
public class InfoProperty
{
    /**
     * 属性ID，主键
     */
    @Generatable(length = 7)
    private Integer propertyId;

    /**
     * 属性所属的栏目id
     */
    @Index
    private Integer channelId;

    /**
     * 关联所属栏目对象
     */
    private Channel channel;

    @Require
    @Unique(with = "channelId")
    @ColumnDescription(type = "varchar(50)")
    private String propertyName;

    @Require
    @Unique(with = "channelId")
    @ColumnDescription(type = "varchar(50)")
    private String showName;

    /**
     * 枚举值，多个值用|分开
     */
    @ColumnDescription(type = "varchar(500)")
    private String enumValues;

    /**
     * 是否必填，如果true表示可为空，不是必填，false表示必填
     */
    @Require
    private Boolean nullable;

    /**
     * 数据类型
     */
    @Require
    @ColumnDescription(nullable = false, defaultValue = "0")
    private InfoDataType dataType;

    @Require
    private Boolean multiple;

    /**
     * 选择器的类名
     */
    @ColumnDescription(type = "varchar(500)")
    private String selectable;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(5)")
    private Integer orderId;

    public InfoProperty()
    {
    }

    public Integer getPropertyId()
    {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId)
    {
        this.propertyId = propertyId;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String getShowName()
    {
        return showName;
    }

    public void setShowName(String showName)
    {
        this.showName = showName;
    }

    public Boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(Boolean nullable)
    {
        this.nullable = nullable;
    }

    public InfoDataType getDataType()
    {
        return dataType;
    }

    public void setDataType(InfoDataType dataType)
    {
        this.dataType = dataType;
    }

    public Boolean getMultiple()
    {
        return multiple;
    }

    public void setMultiple(Boolean multiple)
    {
        this.multiple = multiple;
    }

    public String getEnumValues()
    {
        return enumValues;
    }

    public void setEnumValues(String enumValues)
    {
        this.enumValues = enumValues;
    }

    /**
     * 以数组返回枚举值
     *
     * @return 将枚举值根据|拆开，转化为数组
     */
    @NotSerialized
    public String[] getEnumArray()
    {
        return enumValues == null ? null :
                (nullable == null || nullable) && (multiple == null || !multiple) ?
                        ("|" + enumValues).split("\\|") : enumValues.split("\\|");
    }

    public String getSelectable()
    {
        return selectable;
    }

    public void setSelectable(String selectable)
    {
        this.selectable = selectable;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InfoProperty))
            return false;

        InfoProperty that = (InfoProperty) o;

        return propertyId.equals(that.propertyId);
    }

    @Override
    public int hashCode()
    {
        return propertyId.hashCode();
    }

    @Override
    public String toString()
    {
        return propertyName;
    }
}
