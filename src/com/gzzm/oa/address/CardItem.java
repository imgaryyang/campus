package com.gzzm.oa.address;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Value;

import java.util.*;

/**
 * 导出项，定义一个导出项的javafield和中文名的对应关系
 *
 * @author whf
 * @date 2010-3-28
 */
public class CardItem implements Value<String>
{
    private static final long serialVersionUID = 3661273074754492242L;

    private static final List<CardItem> ITEMS = new ArrayList<CardItem>();

    public static final String EMAIL = "email";

    public static final String MOBILEPHONE = "mobilePhone";

    /**
     * 默认导出的列
     */
    public static final String[] DEFAULTCOLS = new String[]{
            "cardName",
            "sex",
            MOBILEPHONE,
            "birthday",
            "address",
            EMAIL,
            "office",
            "industry",
            "adDivision"};

    /**
     * 和电话相关的属性
     */
    public static final String[] PHONES = {MOBILEPHONE, "homePhone", "workPhone", "fax"};


    static
    {
        //定义全部可到处的项
        addItem("cardName", "姓名");
        addItem("nick", "昵称");
        addItem("sex", "性别");
        addItem("birthday", "生日");
        addItem(MOBILEPHONE, "手机");
        addItem("homePhone", "家庭电话");
        addItem("address", "家庭地址");
        addItem("nativePlace", "籍贯");
//        addItem("postcode", "邮编");
        addItem("national", "民族");
        addItem("interests", "爱好");
        addItem("office", "工作单位");
        addItem("officeAddress", "单位地址");
        addItem("duty", "职务");
        addItem("workTime", "任职时间");
        addItem("workPhone", "工作电话");
        addItem("fax", "传真");
        addItem("industry", "行业");
        addItem("adDivision", "地区");
        addItem("diploma", "学历");
        addItem("specialty", "专业");
        addItem("graduation", "毕业院校");
        addItem("homeUrl", "主页");
        addItem("email", "Email");
        addItem("qq", "QQ");
        addItem("msn", "MSN");
        addItem("icq", "ICQ");
    }

    private String field;

    private String name;

    public CardItem(String field, String name)
    {
        this.field = field;
        this.name = name;
    }

    private static void addItem(String field, String name)
    {
        ITEMS.add(new CardItem(field, name));
    }

    public static List<CardItem> getItems()
    {
        return ITEMS;
    }

    public static CardItem getItem(String field)
    {
        for (CardItem item : ITEMS)
        {
            if (item.field.equals(field))
                return item;
        }

        return null;
    }

    public String valueOf()
    {
        return field;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public String getField()
    {
        return field;
    }

    public String getName()
    {
        String s = "com.gzzm.oa.address.AddressCardCrud." + name;
        String s1 = Tools.getMessage(s);

        if (!s.equals(s1))
            return s1;

        return name;
    }

    public static boolean isSimpleAttribute(String field)
    {
        return "cardName".equals(field) || "nick".equals(field) || "sex".equals(field);
    }
}
