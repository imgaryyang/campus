package com.gzzm.portal.tag;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * 标签页面容器，保存所有的门户标签
 *
 * @author camel
 * @date 2011-6-11
 */
public final class PortalTagContainer
{
    /**
     * 存放所有单例标签
     */
    private static final Map<String, PortalTag> tags = new HashMap<String, PortalTag>();

    /**
     * 存放所有非单例标签对应的class
     */
    private static final Map<String, Class<? extends PortalTag>> tagClasses =
            new HashMap<String, Class<? extends PortalTag>>();

    /**
     * 缓存所有非单例标签的属性列表，用于将上下文中的属性复制到标签对象的属性中
     */
    private static final Map<Class<? extends PortalTag>, List<PropertyInfo>> propertiesMap =
            new HashMap<Class<? extends PortalTag>, List<PropertyInfo>>();

    static
    {
        add("oql", new OQLTag());
        add("sql", new SQLTag());
    }

    private PortalTagContainer()
    {
    }

    /**
     * 添加一个单例标签
     *
     * @param name 标签名称
     * @param tag  标签对象
     */
    public static void add(String name, PortalTag tag)
    {
        synchronized (tags)
        {
            tags.put(name, tag);
        }
    }

    /**
     * 添加一个非单例标签
     *
     * @param name 标签名称
     * @param c    标签的class
     */
    public static void add(String name, Class<? extends PortalTag> c)
    {
        synchronized (tagClasses)
        {
            tagClasses.put(name, c);
        }
    }

    /**
     * 获得一个标签对象
     *
     * @param name    标签的名称
     * @param context 上下文信息
     * @return 标签对象
     * @throws Exception 构建标签对象时或者填充标签属性时错误
     */
    public static PortalTag getTag(String name, Map<String, Object> context) throws Exception
    {
        //先看有没有对应的单例标签实例
        PortalTag tag;
        synchronized (tags)
        {
            tag = tags.get(name);
        }

        if (tag != null)
            return tag;

        //获得非单例标签的class
        Class<? extends PortalTag> c;
        synchronized (tagClasses)
        {
            c = tagClasses.get(name);
        }

        if (c == null)
            return null;

        //创建标签
        tag = Tools.getBean(c);

        //初始化标签，即根据上下文信息填充标签属性
        initTag(tag, c, context);

        return tag;
    }

    private static void initTag(PortalTag tag, Class<? extends PortalTag> c, Map<String, Object> context)
            throws Exception
    {
        for (PropertyInfo property : getProperties(c))
        {
            String name = property.getName();
            Object value = context.get(name);

            if (value != null)
                property.setObject(tag, value);
        }
    }

    protected static List<PropertyInfo> getProperties(Class<? extends PortalTag> c) throws Exception
    {
        synchronized (c)
        {
            List<PropertyInfo> properties;
            synchronized (propertiesMap)
            {
                properties = propertiesMap.get(c);
            }

            if (properties == null)
            {
                properties = BeanUtils.getProperties(c, BeanUtils.WRITABLEPROPERTIES);

                synchronized (propertiesMap)
                {
                    propertiesMap.put(c, properties);
                }
            }

            return properties;
        }
    }
}
