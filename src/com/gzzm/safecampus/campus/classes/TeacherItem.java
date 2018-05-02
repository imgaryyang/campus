package com.gzzm.safecampus.campus.classes;

import net.cyan.commons.util.Value;

import java.util.*;

public class TeacherItem implements Value<String>
{
    private static final long serialVersionUID = 3661273074754492242L;

    private static final List<TeacherItem> ITEMS = new ArrayList<TeacherItem>();

    static
    {
        addItem("teacherNo", "工号");
        addItem("teacherName", "姓名");
        addItem("sex", "性别");
        addItem("job", "职务");
        addItem("entryTime", "入职时间");
        addItem("birthday", "出生日期");
        addItem("province.divisionName", "省份");
        addItem("city.divisionName", "城市");
        addItem("phone", "电话");
        addItem("address", "住址");
        addItem("idCard", "身份证号");
        addItem("desc", "备注");
    }

    private String field;

    private String name;

    public TeacherItem(String field, String name)
    {
        this.field = field;
        this.name = name;
    }

    private static void addItem(String field, String name)
    {
        ITEMS.add(new TeacherItem(field, name));
    }

    public static List<TeacherItem> getItems()
    {
        return ITEMS;
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
        return name;
    }

}
