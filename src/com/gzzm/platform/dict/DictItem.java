package com.gzzm.platform.dict;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Chinese;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 字典条目，代表某个字典中的一条记录，字典中的条目可以是树形结构，也可以是列表结构
 *
 * @author camel
 * @date 13-3-26
 */
@Entity(table = "PFDICTITEM", keys = "itemId")
@Indexes(
        @Index(columns = {"dictId", "itemCode"})
)
public class DictItem
{
    /**
     * 字典条目ID
     */
    @Generatable(length = 7)
    private Integer itemId;

    /**
     * 条目名称
     */
    @ColumnDescription(type = "varchar(500)", nullable = false)
    @Require
    private String itemName;

    /**
     * 条目编码
     */
    @ColumnDescription(type = "varchar(50)")
    private String itemCode;

    /**
     * 所属字典ID
     */
    private Integer dictId;

    @NotSerialized
    private Dict dict;

    /**
     * 父级字典项的ID，仅对树形结构的字典有效，如果是根节点或者列表结构，此字段为空
     */
    private Integer parentItemId;

    /**
     *
     */
    @NotSerialized
    @ToOne("PARENTITEMID")
    private DictItem parent;

    @NotSerialized
    @OneToMany("PARENTITEMID")
    @OrderBy(column = "ORDERID")
    private List<DictItem> children;

    /**
     * 拼音
     */
    @ColumnDescription(type = "varchar(1000)")
    private String spell;

    /**
     * 姓名简拼
     */
    @ColumnDescription(type = "varchar(250)")
    private String simpleSpell;

    /**
     * 备注
     */
    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public DictItem()
    {
    }

    public Integer getItemId()
    {
        return itemId;
    }

    public void setItemId(Integer itemId)
    {
        this.itemId = itemId;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemCode()
    {
        return itemCode;
    }

    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    public Integer getDictId()
    {
        return dictId;
    }

    public void setDictId(Integer dictId)
    {
        this.dictId = dictId;
    }

    public Dict getDict()
    {
        return dict;
    }

    public void setDict(Dict dict)
    {
        this.dict = dict;
    }

    public Integer getParentItemId()
    {
        return parentItemId;
    }

    public void setParentItemId(Integer parentItemId)
    {
        this.parentItemId = parentItemId;
    }

    public DictItem getParent()
    {
        return parent;
    }

    public void setParent(DictItem parent)
    {
        this.parent = parent;
    }

    public List<DictItem> getChildren()
    {
        return children;
    }

    public void setChildren(List<DictItem> children)
    {
        this.children = children;
    }

    public String getSpell()
    {
        return spell;
    }

    public void setSpell(String spell)
    {
        this.spell = spell;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @NotSerialized
    public String getFullName()
    {
        StringBuilder buffer = new StringBuilder();

        for (DictItem item = this; item.getParentItemId() != null; item = item.getParent())
        {
            buffer.insert(0, item.getItemName());
        }

        return buffer.toString();
    }

    @NotSerialized
    public Collection<Integer> getDescendantIds() throws Exception
    {
        List<Integer> result = new ArrayList<Integer>();

        getDescendantIds(result);

        return result;
    }

    private void getDescendantIds(Collection<Integer> ids) throws Exception
    {
        ids.add(getItemId());

        for (DictItem child : getChildren())
            child.getDescendantIds(ids);
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify() throws Exception
    {
        //设置简拼和全拼
        String itemName = getItemName();
        if (itemName != null)
        {
            setSpell(Chinese.getLetters(itemName));
            setSimpleSpell(Chinese.getFirstLetters(itemName));
        }
    }

    @Override
    public String toString()
    {
        return itemName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof DictItem))
            return false;

        DictItem dictItem = (DictItem) o;

        return itemId.equals(dictItem.itemId);
    }

    @Override
    public int hashCode()
    {
        return itemId.hashCode();
    }
}
