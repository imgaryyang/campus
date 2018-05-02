package com.gzzm.platform.message.sms;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-4-28
 */
@Entity(table = "PFPHONELISTITEM", keys = "itemId")
public class PhoneListItem
{
    @Generatable(length = 10)
    private Long itemId;

    private Integer listId;

    private PhoneList phoneList;

    @Require
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    @ColumnDescription(type = "varchar(250)")
    private String itemName;

    public PhoneListItem()
    {
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Integer getListId()
    {
        return listId;
    }

    public void setListId(Integer listId)
    {
        this.listId = listId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public PhoneList getPhoneList()
    {
        return phoneList;
    }

    public void setPhoneList(PhoneList phoneList)
    {
        this.phoneList = phoneList;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof PhoneListItem))
            return false;

        PhoneListItem that = (PhoneListItem) o;

        return itemId.equals(that.itemId);
    }

    @Override
    public int hashCode()
    {
        return itemId.hashCode();
    }
}
