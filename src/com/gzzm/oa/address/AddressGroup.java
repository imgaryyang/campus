package com.gzzm.oa.address;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.DataConvert;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.Set;


/**
 * 通讯组实体对象，对应数据库通讯组表
 *
 * @author whf
 * @date 2010-3-11
 */
@Entity(table = "OAADDRESSGROUP", keys = "groupId")
public class AddressGroup implements Comparable<AddressGroup>
{
    /**
     * 通讯组ID，主键
     */
    @Generatable(length = 7)
    private Integer groupId;

    /**
     * 通讯组名称
     */
    @Require
    @MaxLen(30)
    @Unique(with = {"type", "owner"})
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String groupName;

    /**
     * 通讯组拥有者
     * type=0时存放用户id，type=1时存放单位id
     */
    @Index
    @Require
    @ColumnDescription(type = "number(9)", nullable = false)
    private Integer owner;

    /**
     * 排序id，序号越大越后显示，按升序排序
     */
    private Integer orderId;

    /**
     * 类型：0表示是个人通讯录联系人，1表示是单位通讯录联系人,默认0。
     */
    @Require
    @ColumnDescription(nullable = false, defaultValue = "0")
    private AddressType type;

    /**
     * 组中的联系人
     */
    @ManyToMany(table = "OAADDRESSCARDGROUP")
    @NotSerialized
    private Set<AddressCard> cards;

    public AddressGroup()
    {
    }

    public AddressGroup(Integer groupId, String groupName)
    {
        this.groupId = groupId;
        this.groupName = groupName;
        this.orderId = 0;
    }

    public AddressGroup(Integer groupId)
    {
        this.groupId = groupId;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Integer getOwner()
    {
        return owner;
    }

    public void setOwner(Integer owner)
    {
        this.owner = owner;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public AddressType getType()
    {
        return type;
    }

    public void setType(AddressType type)
    {
        this.type = type;
    }

    public Set<AddressCard> getCards()
    {
        return cards;
    }

    public void setCards(Set<AddressCard> cards)
    {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof AddressGroup))
            return false;

        AddressGroup group = (AddressGroup) o;

        return groupId.equals(group.groupId);

    }

    @Override
    public int hashCode()
    {
        return groupId.hashCode();
    }

    public String toString()
    {
        //原来在组名后面加上数量，这是与展示相关的逻辑，将此逻辑移到Page类中
        return groupName;
    }

    public int compareTo(AddressGroup o)
    {
        return DataConvert.compare(orderId, o.getOrderId());
    }
}
