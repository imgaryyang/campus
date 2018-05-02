package com.gzzm.safecampus.device.card.common;

import net.cyan.commons.util.*;
import org.w3c.dom.*;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/20
 */
public class XMLUtils
{
    public static String createXML(Object param, Class type) throws IllegalAccessException
    {
        StringBuilder xml = new StringBuilder("");
        xml.append("<ERRECORDS>");
        for (Field field : type.getDeclaredFields())
        {
            xml.append(createNode(field, param));
        }
        xml.append("</ERRECORDS>");
        return xml.toString();
    }

    public static String createNode(Field field, Object obj) throws IllegalAccessException
    {
        StringBuilder node = new StringBuilder();
        field.setAccessible(true);
        Object val = field.get(obj);
        if (val == null || "".equals(val)) return "";
        if (!isBaseType(field))
        {
            node.append("<").append(field.getName()).append(">");
            if (Collection.class.isAssignableFrom(field.getType()))
            {
                Collection collection = (Collection) val;
                Iterator iterator = collection.iterator();
                while (iterator.hasNext())
                {
                    Object next = iterator.next();
                    node.append("<");
                    node.append(next.getClass().getSimpleName());
                    node.append(">");
                    for (Field child : next.getClass().getDeclaredFields())
                    {
                        node.append(createNode(child, next));
                    }
                    node.append("</");
                    node.append(next.getClass().getSimpleName());
                    node.append(">");
                }
            }
            else
            {
                for (Field child : field.getType().getDeclaredFields())
                {
                    node.append(createNode(field, val));
                }
            }
            node.append("</").append(field.getName()).append(">");
        }
        else
        {
            node.append("<").append(field.getName()).append(">").append(String.valueOf(
                    field.get(obj) == null ? "" : field.get(
                            obj))).append("</")
                    .append(field.getName()).append(">");
        }
        return node.toString();
    }

    public static Object loadBean(String documentXml, Class bean, String baseNode) throws Exception
    {
        Document document = net.cyan.commons.util.xml.XmlUtils
                .createDocument(new String(documentXml).getBytes("UTF-8"));
        return loadBean(document, bean, baseNode);
    }

    /**
     * 把document 转成实体
     *
     * @param document
     * @param bean
     * @param baseNode
     * @return
     * @throws Exception
     */
    public static Object loadBean(Document document, Class bean, String baseNode) throws Exception
    {
        NodeList elements = document.getElementsByTagName(baseNode);
        Object instance = null;
        if (elements == null || elements.getLength() == 0) return null;
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < elements.getLength(); i++)
        {
            instance = bean.newInstance();
            Node item = elements.item(i);
            initField(instance, bean, item);
            list.add(instance);
        }
        return list;
    }

    /**
     * 初始化字段信息
     *
     * @param object
     * @param type
     * @param item
     * @throws Exception
     */
    public static void initField(Object object, Class type, Node item) throws Exception
    {
        for (Field filed : type.getDeclaredFields())
        {
            //如果是集合类型的话
            if (Collection.class.isAssignableFrom(filed.getType()))
            {
                Class collectionClass = getCollectionClass(filed);
                //找到这个字段对应的document节点
                Node node = findField(filed, item.getFirstChild());
                //如果这个节点不为空而且他必须要有子节点
                if (node != null && node.hasChildNodes())
                {
                    filed.setAccessible(true);
                    Object o = filed.get(object);
                    List list = null;
                    if (o == null)
                        list = new ArrayList();
                    else
                        list = (List) o;
                    Object childObj = collectionClass.newInstance();
                    //递归继续初始化这个对象的字段信息
                    initField(childObj, collectionClass, node.getFirstChild());
                    String oldNode = node.getFirstChild().getNodeName();
                    Node next = node.getFirstChild().getNextSibling();
                    list.add(childObj);
                    while (next != null)
                    {
                        if (!oldNode.equals(next.getNodeName()))
                        {
                            next = next.getNextSibling();
                            continue;
                        }
                        ;
                        Object childObj2 = collectionClass.newInstance();
                        initField(childObj2, collectionClass, next);
                        next = next.getNextSibling();
                        list.add(childObj2);
                    }
                    filed.setAccessible(true);
                    filed.set(object, list);
                }

            }
            else
            {
                initField(filed, item.getFirstChild(), object);
            }
        }
    }

    public static Node findField(Field filed, Node item) throws Exception
    {
        if (item == null) return null;
        if (filed.getName().equals(item.getNodeName()))
        {
            return item;
        }
        else if (item.getNextSibling() != null)
        {
            return findField(filed, item.getNextSibling());
        }
        return null;
    }

    /**
     * 递归对字段赋值
     *
     * @param filed
     * @param item
     * @param object
     * @throws Exception
     */
    public static void initField(Field filed, Node item, Object object) throws Exception
    {
        if (item == null) return;
        if (filed.getName().equals(item.getNodeName()))
        {
            filed.setAccessible(true);
            filed.set(object,
                    convertData(filed, item.getNodeValue() == null ? item.getTextContent() : item.getNodeValue()));
        }
        else if (item.getNextSibling() != null)
        {
            initField(filed, item.getNextSibling(), object);
        }
    }

    /**
     * 封装常用的字符格式转成field的值
     *
     * @param filed
     * @param data
     * @return
     * @throws ParseException
     */
    public static Object convertData(Field filed, String data) throws ParseException
    {
        if (filed.getType().equals(String.class))
        {
            return data;
        }
        else if (filed.getType().equals(Integer.class)
                || filed.getType().toString().trim().equals("int"))
        {
            return Integer.parseInt(data);
        }
        else if (filed.getType().equals(Long.class)
                || filed.getType().toString().trim().equals("long"))
            return Long.parseLong(data);
        else if (filed.getType().equals(Double.class)
                || filed.getType().toString().trim().equals("double"))
            return Double.parseDouble(data);
        else if (filed.getType().equals(Float.class)
                || filed.getType().toString().trim().equals("float"))
            return Float.parseFloat(data);
        if (filed.getType().equals(Short.class)
                || filed.getType().toString().trim().equals("short"))
            return Short.parseShort(data);
        if (filed.getType().equals(Byte.class)
                || filed.getType().toString().trim().equals("byte"))
            return Byte.parseByte(data);
        if (filed.getType().equals(Character.class)
                || filed.getType().toString().trim().equals("char"))
            return Integer.parseInt(data);
        if (filed.getType().equals(Boolean.class)
                || filed.getType().toString().trim().equals("boolean"))
            return Integer.parseInt(data);
        else if (filed.getType().equals(Date.class))
        {
            String realValue = getRealValue(data, 14);
            SimpleDateFormat formatSecond = new SimpleDateFormat("yyyyMMddhhmmss".substring(0, realValue.length()));
            Date parse = formatSecond.parse(realValue);
            return parse;
        }
        else if (filed.getType().equals(java.sql.Date.class))
        {
            String realValue = getRealValue(data, 8);
            SimpleDateFormat formatSecond = new SimpleDateFormat("yyyyMMdd".substring(0, realValue.length()));
            Date parse = formatSecond.parse(realValue);
            return parse;
        }
        else if (filed.getType().isEnum())
        {
           return  DataConvert.convertType(filed.getType(), Integer.parseInt(data));
        }
        return data;
    }

    /**
     * 帮助类，用于切割所有时间字符串中那些非数字的字符。从而统一时间格式
     *
     * @param time 时间字符串
     * @param len  长度返回结果的长度
     * @return
     */
    private static String getRealValue(String time, int len)
    {
        if (time == null) return null;
        StringBuilder result = new StringBuilder();
        char[] chars = time.toCharArray();
        int arrLen = chars.length;
        for (int i = 0; i < arrLen; i++)
        {
            if (48 > chars[i] || chars[i] > 57)
            {
                if (i < arrLen - 2)
                {
                    if (48 > chars[i + 2] || chars[i + 2] > 57)
                    {
                        result.append("0");
                    }
                }
            }
            else if (48 <= chars[i] && chars[i] <= 57)
            {
                result.append(chars[i]);
            }
        }
        if (result.length() <= len)
            return result.toString();
        else return result.substring(0, len - 1);
    }

    /**
     * 找到实体类的集合中泛型的class。
     *
     * @param parameter
     * @return
     * @throws ClassNotFoundException
     */
    public static Class getCollectionClass(Field parameter) throws ClassNotFoundException
    {
        Type type = parameter.getGenericType();
        if (ParameterizedType.class.isAssignableFrom(type.getClass()))
        {
            for (Type result : ((ParameterizedType) type).getActualTypeArguments())
            {
                return Class.forName(result.toString().substring(result.toString().indexOf(" ") + 1));
            }
        }
        return parameter.getType();
    }

    /**
     * XML组装成bean
     *
     * @param xml
     * @param bean
     * @return
     * @throws Exception
     */
    public static <T> T readXML(String xml, Class<T> bean) throws Exception
    {
        Object o = XMLUtils.loadBean(xml, bean, "ERRECORD");
        if (o == null) return null;
        if ((o instanceof Collection) && ((Collection) o).size() > 0)
        {
            return (T) ((List) o).get(0);
        }
        return (T) o;

    }

    /**
     * 根据请求参数生成响应对象-默认是请求成功如果失败业务方法设置result 设置为-1
     * @param oldBean
     * @param result
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T createResBean(Object oldBean, Class<T> result) throws Exception
    {
        if (oldBean == null) return null;
        String token = BeanEncodeUtils.createToken(oldBean);
        Object resBean = result.newInstance();
        ReflectionUtils.setValue(resBean, "Token", token);
        try
        {
            ReflectionUtils.setValue(resBean, "SequenceId", ReflectionUtils.getValue(oldBean, "SequenceId"));
            ReflectionUtils.setValue(resBean, "ERPCode", ReflectionUtils.getValue(oldBean, "ERPCode"));
            ReflectionUtils.setValue(resBean, "TimeStamp", DateUtils.toString(new Date(), "yyyyMMddHHmmss"));
            ReflectionUtils.setValue(resBean, "Result", "0");
            ReflectionUtils.setValue(resBean, "Error", "");
        }
        catch (Exception e)
        {
        }
        return (T) resBean;
    }

    public static String toXML(Object resBean) throws IllegalAccessException
    {
        return createXML(resBean, resBean.getClass());
    }

    /**
     * 生成响应信息XML 、同时对结果进行加密生成token
     * @param resBean
     * @return
     * @throws Exception
     */
    public static String response(Object resBean) throws Exception
    {
        return toXML(BeanEncodeUtils.initToken(resBean));
    }


    public static String DataFormat(Field filed, Object data) throws ParseException
    {
        if (data == null) return "";
        if (filed.getType().equals(String.class))
        {
            return data.toString();
        }
        else if (filed.getType().equals(Integer.class)
                || filed.getType().toString().trim().equals("int"))
        {
            return String.valueOf(data);
        }
        else if (filed.getType().equals(Long.class)
                || filed.getType().toString().trim().equals("long"))
            return String.valueOf(data);
        else if (filed.getType().equals(Double.class)
                || filed.getType().toString().trim().equals("double"))
            return String.valueOf(data);
        else if (filed.getType().equals(Float.class)
                || filed.getType().toString().trim().equals("float"))
            return String.valueOf(data);
        if (filed.getType().equals(Short.class)
                || filed.getType().toString().trim().equals("short"))
            return String.valueOf(data);
        if (filed.getType().equals(Byte.class)
                || filed.getType().toString().trim().equals("byte"))
            return String.valueOf(data);
        if (filed.getType().equals(Character.class)
                || filed.getType().toString().trim().equals("char"))
            return String.valueOf(data);
        if (filed.getType().equals(Boolean.class)
                || filed.getType().toString().trim().equals("boolean"))
            return String.valueOf(data);
        else if (filed.getType().equals(Date.class))
        {
            if (data instanceof String) return (String) data;
            SimpleDateFormat formatSecond = new SimpleDateFormat("yyyyMMddHHmmSS");
            return formatSecond.format(data);
        }
        else if (filed.getType().equals(java.sql.Date.class))
        {
            if (data instanceof String) return (String) data;
            SimpleDateFormat formatSecond = new SimpleDateFormat("yyyyMMdd");
            return formatSecond.format(data);
        }
        else if (filed.getType().isEnum())
        {
            Enum temp = (Enum) data;
            return String.valueOf(temp.ordinal());
        }
        return data.toString();
    }

    public static boolean isBaseType(Field filed)
    {
        if (filed.getType().equals(String.class) || filed.getType().equals(Integer.class)
                || filed.getType().toString().trim().equals("int") || filed.getType().equals(Long.class)
                || filed.getType().toString().trim().equals("long") || filed.getType().equals(Double.class)
                || filed.getType().toString().trim().equals("double") || filed.getType().equals(Float.class)
                || filed.getType().toString().trim().equals("float") || filed.getType().equals(Short.class)
                || filed.getType().toString().trim().equals("short") || filed.getType().equals(Byte.class)
                || filed.getType().toString().trim().equals("byte") || filed.getType().equals(Character.class)
                || filed.getType().toString().trim().equals("char") || filed.getType().equals(Boolean.class)
                || filed.getType().toString().trim().equals("boolean") || filed.getType().equals(Date.class) ||
                filed.getType().equals(java.sql.Date.class) || filed.getType().isEnum())
        {
            return true;
        }
        return false;
    }

}
