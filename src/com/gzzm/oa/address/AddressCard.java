package com.gzzm.oa.address;

import com.gzzm.platform.commons.Sex;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Chinese;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 通讯录联系人实体对象，对应数据库通讯录联系人信息表
 *
 * @author whf
 * @date 2010-3-10
 */
@Entity(table = "OAADDRESSCARD", keys = "cardId")
public class AddressCard
{
    @Generatable(length = 9)
    private Integer cardId;

    /**
     * 姓名
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String cardName;

    /**
     * 简拼
     */
    @ColumnDescription(type = "varchar(250)")
    private String simpleSpell;

    /**
     * 全拼
     */
    @ColumnDescription(type = "varchar(250)")
    private String completeSpell;

    /**
     * 信息拥有者
     * type=0时存放用户id，type=1时存放单位id
     */
    @Index
    @Require
    @ColumnDescription(type = "number(9)", nullable = false)
    private Integer owner;

    /**
     * 类型：0表示是个人通讯录联系人，1表示是单位通讯录联系人,默认0。
     */
    @Require
    @ColumnDescription(nullable = false, defaultValue = "0")
    private AddressType type;

    /**
     * 头像
     */
    private byte[] headImg;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 联系人昵称
     */
    @ColumnDescription(type = "varchar(250)")
    private String nick;

    /**
     * 其他属性
     */
    @Lazy(false)
    @ValueMap(table = "OAADDRESSCARDATTRIBUTE", keyColumn = "ATTRIBUTENAME", valueColumn = "ATTRIBUTEVALUE")
    private Map<String, String> attributes;

    /**
     * 所属的组
     */
    @ManyToMany(table = "OAADDRESSCARDGROUP")
    @NotSerialized
    private List<AddressGroup> groups;

    public AddressCard()
    {
    }

    public AddressCard(Integer cardId)
    {
        this.cardId = cardId;
    }

    public Integer getCardId()
    {
        return cardId;
    }

    public void setCardId(Integer cardId)
    {
        this.cardId = cardId;
    }

    public String getCardName()
    {
        return cardName;
    }

    public void setCardName(String cardName)
    {
        this.cardName = cardName;
    }

    public Integer getOwner()
    {
        return owner;
    }

    public void setOwner(Integer owner)
    {
        this.owner = owner;
    }

    public AddressType getType()
    {
        return type;
    }

    public void setType(AddressType type)
    {
        this.type = type;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public byte[] getHeadImg()
    {
        return headImg;
    }

    public void setHeadImg(byte[] headImg)
    {
        this.headImg = headImg;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    public String getCompleteSpell()
    {
        return completeSpell;
    }

    public void setCompleteSpell(String completeSpell)
    {
        this.completeSpell = completeSpell;
    }

    public List<AddressGroup> getGroups()
    {
        return groups;
    }

    public void setGroups(List<AddressGroup> groups)
    {
        this.groups = groups;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof AddressCard))
            return false;

        AddressCard card = (AddressCard) o;

        return cardId.equals(card.cardId);
    }

    @Override
    public int hashCode()
    {
        return cardId.hashCode();
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify() throws Exception
    {
        //设置简拼和全拼
        String name = getCardName();
        if (name != null)
        {
            setCompleteSpell(Chinese.getLetters(name));
            setSimpleSpell(Chinese.getFirstLetters(name));
        }
    }
}
