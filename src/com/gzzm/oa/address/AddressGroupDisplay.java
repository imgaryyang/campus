package com.gzzm.oa.address;

import com.gzzm.platform.commons.crud.*;
import net.cyan.crud.ToString;
import net.cyan.nest.annotation.Inject;

/**
 * @author whf
 * @date 2010-3-18
 */
public class AddressGroupDisplay extends BaseQueryCrud<AddressGroup, Integer> implements ToString<AddressGroup>
{
    /**
     * 表示当前是对部门通讯录还是个人通讯录的维护
     */
    private AddressType type;

    private Integer owner;

    @Inject
    private AddressCardDao dao;

    public AddressType getType()
    {
        return type;
    }

    public void setType(AddressType type)
    {
        this.type = type;
    }

    public Integer getOwner()
    {
        return owner;
    }

    public void setOwner(Integer owner)
    {
        this.owner = owner;
    }

    public AddressGroupDisplay()
    {
        addOrderBy("orderId");
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }

    @Override
    public void afterQuery() throws Exception
    {
        AddressGroup card = new AddressGroup(0, "所有联系人");
        getList().add(0, card);
    }

    //不需要owner的setter方法，避免被恶意修改owner

    public String toString(AddressGroup group) throws Exception
    {
        if (group.getGroupId() == 0)
            return group.getGroupName() + " (" + dao.getCardsCount(type, owner) + ")";
        else
            return group.getGroupName() + " (" + group.getCards().size() + ")";

        //原来这里用ex.print....输入异常，违反编码规范，删除，ccs
    }
}
