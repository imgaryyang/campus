package com.gzzm.safecampus.device.card.common;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;
import net.cyan.commons.util.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.DateFormat;
import java.util.*;

public class ReflectionUtils
{

    /***
     * 获取私有成员变量的值
     *
     */
    public static Object getValue(Object instance, String fieldName)
            throws IllegalAccessException, NoSuchFieldException
    {

        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true); // 参数值为true，禁止访问控制检查
        return field.get(instance);
    }

    /***
     * 获取私有成员变量的值
     *
     */
    public static Object getValue(Object instance, Field field)
            throws IllegalAccessException
    {

        field.setAccessible(true); // 参数值为true，禁止访问控制检查

        return field.get(instance);
    }

    /***
     * 获取私有成员变量的值
     *
     */
    public static Object getValue(Object instance, Class tigerClass, String fieldName)
            throws IllegalAccessException, NoSuchFieldException
    {

        Field field = tigerClass.getDeclaredField(fieldName);
        field.setAccessible(true); // 参数值为true，禁止访问控制检查

        return field.get(instance);
    }

    /***
     * 设置私有成员变量的值
     *
     */
    public static void setValue(Object instance, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {

        Field field = instance.getClass().getDeclaredField(fileName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    /***
     * 访问私有方法
     *
     */
    public static Object callMethod(Object instance, String methodName, Class[] classes, Object[] objects)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {

        Method method = instance.getClass().getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(instance, objects);
    }

    /***
     * 访问私有方法
     *
     */
    public static Method getMethod(Class type, String methodName)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Method[] methods = type.getMethods();
        for (Method method : methods)
        {
            if (methodName.equals(method.getName())) return method;

        }
        return null;
    }

    public static Field getField(Class tigerClass, String fieldName) throws NoSuchFieldException
    {
        Field field = tigerClass.getDeclaredField(fieldName);
        return field;
    }

    /**
     * 初始化1 个bean 从另一个bean中复制值 这里要求字段名称和类型一致
     *
     * @param bean
     * @param obj
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T initBeanValue(Class<T> bean, Object obj) throws Exception
    {
        T result = bean.newInstance();
        Field[] resultFields = bean.getDeclaredFields();
        Field[] beanField = obj.getClass().getDeclaredFields();
        for (Field r : resultFields)
        {
            for (Field b : beanField)
            {
                if (r.getType().equals(b.getType()) && r.getName().equals(b.getName()))
                {
                    r.setAccessible(true);
                    b.setAccessible(true);
                    r.set(result, b.get(obj));
                }
            }
        }
        return result;

    }

    public static <T> T initBeanValue(Class<T> bean, Object obj, Class objClass) throws Exception
    {
        T result = bean.newInstance();
        Field[] resultFields = bean.getDeclaredFields();
        Field[] beanField = objClass.getDeclaredFields();
        for (Field r : resultFields)
        {
            for (Field b : beanField)
            {
                //如果有申明这个字段需要被映射实体对象的具体属性的情况
                if (hasFieldAnnotation(r, ReflectionField.class))
                {
                    ReflectionField reflectionField = (ReflectionField) getFieldAnnotation(r, ReflectionField.class);
                    if (r.getType().equals(b.getType()) && isReflectionField(reflectionField, b))
                    {
                        r.setAccessible(true);
                        b.setAccessible(true);
                        r.set(result, b.get(obj));
                    }
                }
                else if (r.getType().equals(b.getType()) && r.getName().equals(b.getName()))
                {
                    r.setAccessible(true);
                    b.setAccessible(true);
                    r.set(result, b.get(obj));
                }
            }
        }
        return result;

    }

    public static <T> T copyBeanValue(Class<T> bean, T resultBean, Class copyClass, Object copyObj) throws Exception
    {
        T result = resultBean;
        if (resultBean == null)
        {
            result = bean.newInstance();
        }
        Field[] resultFields = bean.getDeclaredFields();
        Field[] copyField = copyClass.getDeclaredFields();
        for (Field r : resultFields)
        {
            for (Field copy : copyField)
            {
                r.setAccessible(true);
                copy.setAccessible(true);
                //如果有申明这个字段需要被映射实体对象的具体属性的情况
                if (hasFieldAnnotation(r, ReflectionField.class))
                {
                    ReflectionField reflectionField = (ReflectionField) getFieldAnnotation(r, ReflectionField.class);
                    if (r.getType().equals(copy.getType()) && isReflectionField(reflectionField, copy) &&
                            copy.get(copyObj) != null)
                    {

                        r.set(result, copy.get(copyObj));
                    }
                }
                else if (r.getType().equals(copy.getType()) && r.getName().equals(copy.getName()) &&
                        copy.get(copyObj) != null)
                {
                    r.set(result, copy.get(copyObj));
                }
            }
        }
        return result;

    }

    /**
     * 获取字段的get方法
     *
     * @param c
     * @param fieldName
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getGetter(Class c, String fieldName) throws NoSuchMethodException
    {
        Method getter = null;
        if (Annotation.class.isAssignableFrom(c))
        {
            getter = c.getMethod(fieldName);
        }
        else
        {
            String methodName0 = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String methodName = methodName0;
            if (fieldName.length() > 1 && Character.isUpperCase(fieldName.charAt(1)))
            {
                methodName = fieldName;
            }

            Object th = null;

            try
            {
                getter = c.getMethod("get" + methodName);
            }
            catch (NoSuchMethodException var13)
            {
                th = var13;
            }
            catch (NoSuchMethodError var14)
            {
                th = var14;
            }

            if (getter == null)
            {
                try
                {
                    getter = c.getMethod("is" + methodName);
                }
                catch (NoSuchMethodError var11)
                {
                    ;
                }
                catch (NoSuchMethodException var12)
                {
                    ;
                }
            }

            if (getter == null && !methodName0.equals(methodName))
            {
                try
                {
                    getter = c.getMethod("get" + methodName0);
                }
                catch (NoSuchMethodError var9)
                {
                    ;
                }
                catch (NoSuchMethodException var10)
                {
                    ;
                }

                if (getter == null)
                {
                    try
                    {
                        getter = c.getMethod("is" + methodName0);
                    }
                    catch (NoSuchMethodError var7)
                    {
                        ;
                    }
                    catch (NoSuchMethodException var8)
                    {
                        ;
                    }
                }
            }

            if (getter == null)
            {
                if (th instanceof NoSuchMethodException)
                {
                    throw (NoSuchMethodException) th;
                }

                if (th instanceof NoSuchMethodError)
                {
                    throw (NoSuchFieldError) th;
                }
            }
        }

        return getter;
    }

    /**
     * 通过get方法获取值
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws Exception
     */
    public static Object getValueByMethod(Object obj, String fieldName) throws Exception
    {
        if (obj instanceof Map)
        {
            return ((Map) obj).get(fieldName);
        }
        else
        {
            Method getter = getGetter(obj.getClass(), fieldName);
            getter.setAccessible(true);
            return getter.invoke(obj);
        }
    }

    /**
     * 通过set方法设置值
     *
     * @param obj
     * @param property
     * @param value
     * @throws Exception
     */
    public static void setValueByMethod(Object obj, String property, Object value) throws Exception
    {
        setValue(obj, property, value, obj.getClass());
    }

    /**
     * 通过set方法设置值
     *
     * @param obj
     * @param property
     * @param value
     * @param type
     * @throws Exception
     */
    public static void setValue(Object obj, String property, Object value, Type type) throws Exception
    {
        Method method = setGetter(obj.getClass(), property);
        if (method != null && method.getParameterTypes()[1].isAssignableFrom(value.getClass()))
            method.invoke(obj, value);
    }

    /**
     * 获取set方法
     *
     * @param c
     * @param property
     * @return
     * @throws NoSuchMethodException
     */
    public static Method setGetter(Class c, String property) throws NoSuchMethodException
    {
        Method getter = null;
        if (Annotation.class.isAssignableFrom(c))
        {
            getter = c.getMethod(property);
        }
        else
        {
            String methodName0 = Character.toUpperCase(property.charAt(0)) + property.substring(1);
            String methodName = methodName0;
            if (property.length() > 1 && Character.isUpperCase(property.charAt(1)))
            {
                methodName = property;
            }

            Object th = null;

            try
            {
                getter = c.getMethod("set" + methodName);
            }
            catch (NoSuchMethodException var13)
            {
                th = var13;
            }
            catch (NoSuchMethodError var14)
            {
                th = var14;
            }

            if (getter == null)
            {
                try
                {
                    getter = c.getMethod("set" + methodName);
                }
                catch (NoSuchMethodError var11)
                {
                    ;
                }
                catch (NoSuchMethodException var12)
                {
                    ;
                }
            }

            if (getter == null && !methodName0.equals(methodName))
            {
                try
                {
                    getter = c.getMethod("set" + methodName0);
                }
                catch (NoSuchMethodError var9)
                {
                    ;
                }
                catch (NoSuchMethodException var10)
                {
                    ;
                }

                if (getter == null)
                {
                    try
                    {
                        getter = c.getMethod("set" + methodName0);
                    }
                    catch (NoSuchMethodError var7)
                    {
                        ;
                    }
                    catch (NoSuchMethodException var8)
                    {
                        ;
                    }
                }
            }

            if (getter == null)
            {
                if (th instanceof NoSuchMethodException)
                {
                    throw (NoSuchMethodException) th;
                }

                if (th instanceof NoSuchMethodError)
                {
                    throw (NoSuchFieldError) th;
                }
            }
        }

        return getter;
    }

    /**
     * 得到真实值，没有转换过得值
     *
     * @param bean
     * @param filedName
     * @param className
     * @return
     * @throws IllegalAccessException
     */
    public static Object getActualVal(Object bean, String filedName, Class className)
            throws IllegalAccessException
    {
        if (filedName.indexOf(".") != -1)
        {
            String fileds = filedName.substring(filedName.indexOf(".") + 1, filedName.length());
            String str = filedName.substring(0, filedName.indexOf("."));
            for (Field field : className.getDeclaredFields())
                if (field.getName().equals(str))
                {
                    field.setAccessible(true);
                    return getFiledVal(field.get(bean), fileds, field.getType());
                }
        }
        else
        {
            if (bean == null) return null;
            for (Field field : className.getDeclaredFields())
            {
                if (field.getName().equals(filedName))
                {
                    getValue(bean, field);
                    field.setAccessible(true);
                    Object obj = field.get(bean);
                    try
                    {
                        obj = obj == null ? getValueByMethod(bean, field.getName()) : obj;
                    }
                    catch (Exception e)
                    {
                    }
                    return obj;
                }
            }
        }

        return null;
    }

    /**
     * 通过字段得到封装后的值，时间会转成String yyyy-mm-dd.Double 会被金额格式化
     *
     * @param commerceInfo
     * @param filedName
     * @param className
     * @return
     * @throws IllegalAccessException
     */
    public static Object getFiledVal(Object commerceInfo, String filedName, Class className)
            throws IllegalAccessException
    {
        if (filedName.indexOf(".") != -1)
        {
            String fileds = filedName.substring(filedName.indexOf(".") + 1, filedName.length());
            String str = filedName.substring(0, filedName.indexOf("."));
            for (Field field : className.getDeclaredFields())
                if (field.getName().equals(str))
                {
                    field.setAccessible(true);
                    return getFiledVal(field.get(commerceInfo), fileds, field.getType());
                }
        }
        else
        {
            if (commerceInfo == null) return null;
            for (Field field : className.getDeclaredFields())
            {
                if (field.getName().equals(filedName))
                {
                    if (field.getType().getName().equals("java.util.Date"))
                    {
                        field.setAccessible(true);
                        Object obj = field.get(commerceInfo);
                        try
                        {
                            obj = obj == null ? getValueByMethod(commerceInfo, field.getName()) : obj;
                        }
                        catch (Exception e)
                        {
                        }
                        DateFormat dateFormat = DateUtils.getDateFormat("yyyy-MM-dd HH:ss:dd");
                        return obj == null ? "" : dateFormat.format((Date) obj);
                    }
                    if (field.getType().getName().equals("java.sql.Date"))
                    {
                        field.setAccessible(true);
                        Object obj = field.get(commerceInfo);
                        try
                        {
                            obj = obj == null ? getValueByMethod(commerceInfo, field.getName()) : obj;
                        }
                        catch (Exception e)
                        {
                        }
                        DateFormat dateFormat = DateUtils.getDateFormat("yyyy-MM-dd");
                        return obj == null ? "" :  dateFormat.format((Date) obj);
                    }

                    if ((field.getType().getName().equals("java.lang.Double")) ||
                            (field.getType().getSimpleName().equals("double")))
                    {
                        field.setAccessible(true);
                        Object obj = field.get(commerceInfo);
                        try
                        {
                            obj = obj == null ? getValueByMethod(commerceInfo, field.getName()) : obj;
                        }
                        catch (Exception e)
                        {
                        }
                        return NumberUtils.keepPrecision("" + obj, 2);
                    }
                    if ((field.getType().getName().equals("java.lang.Integer")) ||
                            (field.getType().getSimpleName().equals("int")))
                    {
                        field.setAccessible(true);
                        Object obj = field.get(commerceInfo);
                        try
                        {
                            obj = obj == null ? getValueByMethod(commerceInfo, field.getName()) : obj;
                        }
                        catch (Exception e)
                        {
                        }
                        return obj == null ? "" : String.valueOf(obj);
                    }
                    field.setAccessible(true);
                    Object obj = field.get(commerceInfo);
                    try
                    {
                        obj = obj == null ? getValueByMethod(commerceInfo, field.getName()) : obj;
                    }
                    catch (Exception e)
                    {
                    }
                    return obj;
                }
            }
        }

        return "";
    }

    /**
     * 得到真实的字段，一对一关系的实体中经常出现 bean.bean2.name 这个方法可以得到bean2的name字段
     *
     * @param filedName
     * @param otherClass
     * @return
     */

    public static Field getActualField(String filedName, Class otherClass)
    {
        if (filedName.indexOf(".") != -1)
        {
            String fileds = filedName.substring(filedName.indexOf(".") + 1, filedName.length());
            String temp = filedName.substring(0, filedName.indexOf("."));
            for (Field field : otherClass.getDeclaredFields())
            {
                ReflectionField fieldAnnotation =
                        (ReflectionField) ReflectionUtils.getFieldAnnotation(field, ReflectionField.class);

                if (isReflectionField(fieldAnnotation, filedName))
                {
                    return getActualField(fileds, field.getType());
                }
                else if (field.getName().equals(temp))
                {
                    return getActualField(fileds, field.getType());
                }
            }
        }
        else
        {
            for (Field field : otherClass.getDeclaredFields())
            {
                ReflectionField fieldAnnotation =
                        (ReflectionField) ReflectionUtils.getFieldAnnotation(field, ReflectionField.class);
                if (ReflectionUtils.isReflectionField(fieldAnnotation, field)) return field;
                if (field.getName().equals(filedName))
                {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获取类的字段
     *
     * @param type
     * @return
     */
    public static Field[] getClassFiled(Class type)
    {
        return type.getDeclaredFields();
    }

    /**
     * 得到类上面的注解
     *
     * @param type
     * @param annotation
     * @param <T>
     * @return
     */
    public static <T> Annotation getAnnotation(Class type, T annotation)
    {
        for (Annotation annotation1 : type.getAnnotations())
        {
            if (annotation1.annotationType().equals(annotation))
                return annotation1;
        }
        return null;
    }

    public static boolean isReflectionField(ReflectionField reflectionField, Field field)
    {
        if (reflectionField == null) return false;
        for (String filedName : reflectionField.value())
        {
            if (filedName.equals(field.getName())) return true;
        }
        return false;
    }

    public static boolean isReflectionField(ReflectionField reflectionField, String field)
    {
        if (reflectionField == null) return false;
        for (String filedName : reflectionField.value())
        {
            if (filedName.equals(field)) return true;
        }
        return false;
    }

    /**
     * 得到字段上面的注解
     *
     * @param annotation
     * @param <T>
     * @return
     */
    public static <T> Annotation getFieldAnnotation(Field field, T annotation)
    {
        for (Annotation annotation1 : field.getAnnotations())
        {
            if (annotation1.annotationType().equals(annotation))
                return annotation1;
        }
        return null;
    }

    /**
     * 判断字段上是否拥有注解
     *
     * @param field
     * @param annotation
     * @param <T>
     * @return
     */
    public static <T> boolean hasFieldAnnotation(Field field, T annotation)
    {
        for (Annotation annotation1 : field.getAnnotations())
        {
            if (annotation1.annotationType().equals(annotation))
                return true;
        }
        return false;
    }

    /**
     * 判断类上面是否拥有某个注解
     *
     * @param type
     * @param annotation
     * @param <T>
     * @return
     */
    public static <T> boolean hasAnnotation(Class type, T annotation)
    {
        for (Annotation annotation1 : type.getAnnotations())
        {
            if (annotation1.annotationType().equals(annotation))
                return true;
        }
        return false;
    }

    public static void initDefaultValue(Field filed, Object data) throws IllegalAccessException
    {
        if (data == null) return;
        filed.setAccessible(true);
        if (filed.get(data) != null) return;
        if (filed.getType().equals(String.class))
        {
            filed.set(data, "");
        }
        else if (filed.getType().equals(Integer.class)
                || filed.getType().toString().trim().equals("int"))
        {
            filed.set(data, 0);
        }
        else if (filed.getType().equals(Long.class)
                || filed.getType().toString().trim().equals("long"))
            filed.set(data, Long.parseLong("0"));
        else if (filed.getType().equals(Double.class)
                || filed.getType().toString().trim().equals("double"))
            filed.set(data, Double.parseDouble("0"));
        else if (filed.getType().equals(Float.class)
                || filed.getType().toString().trim().equals("float"))
            filed.set(data, Float.parseFloat("0"));
        if (filed.getType().equals(Short.class)
                || filed.getType().toString().trim().equals("short"))
            filed.set(data, Short.parseShort("0"));
        if (filed.getType().equals(Byte.class)
                || filed.getType().toString().trim().equals("byte"))
            filed.set(data, Byte.parseByte("0"));
        if (filed.getType().equals(Character.class)
                || filed.getType().toString().trim().equals("char"))
            filed.set(data, '0');
        if (filed.getType().equals(Boolean.class)
                || filed.getType().toString().trim().equals("boolean"))
            filed.set(data, false);
        else if (filed.getType().equals(Date.class))
        {
            filed.set(data, new Date());
        }
        else if (filed.getType().equals(java.sql.Date.class))
        {
            filed.set(data, new java.sql.Date(new Date().getTime()));
        }
    }

    public static <T> void setFiledValue(Object obj, String filed, Object value, Class invokeClass, Class<T> target)
            throws Exception
    {
        if (value == null || StringUtils.isEmpty(filed)) return;
        Field setfield = ReflectionUtils.getField(obj.getClass(), filed);
        if (Collection.class.isAssignableFrom(setfield.getType()))
        {
            List result = new ArrayList();
            List values = (List) value;
            if (values.size() == 0) return;
            Field[] classFileds = ReflectionUtils.getClassFiled(invokeClass);
            for (Object object : values)
            {
                Object o = invokeClass.newInstance();
                for (Field field : classFileds)
                {
                    initItem(o, field.getName(), object, target);
                }
                result.add(o);
            }
            ReflectionUtils.setValue(obj, filed, result);
        }
        else
        {
            initItem(obj, filed, value, target);
        }
    }

    /**
     * 不是自己和对象的属性设置方法
     *
     * @param obj   被设置的对象
     * @param filed 被设置字段名字
     * @param value 被设置的值对象
     * @throws Exception
     */
    public static void initItem(Object obj, String filed, Object value, Class target) throws Exception
    {
        if (value == null || StringUtils.isEmpty(filed)) return;
        Field setfield = ReflectionUtils.getField(obj.getClass(), filed);
        if (Collection.class.isAssignableFrom(setfield.getType())) return;
        else
        {
            Field[] classFileds = ReflectionUtils.getClassFiled(target);
            for (Field field : classFileds)
            {
                boolean isFind = false;
                if (filed.equals(field.getName()) && field.getType().equals(setfield.getType()))
                {
                    setFiled(field, setfield, obj, value);
                    isFind = true;
                }
                //被设置的字段有ReflectionField注解
                if (!isFind && ReflectionUtils.hasFieldAnnotation(setfield, ReflectionField.class))
                {
                    if (inReflectionField(field.getName(),
                            (ReflectionField) ReflectionUtils.getFieldAnnotation(setfield, ReflectionField.class)))
                    {
                        isFind = true;
                        setFiled(field, setfield, obj, value);
                    }

                }
                //选择的字段有ReflectionField注解
                if (!isFind && ReflectionUtils.hasFieldAnnotation(field, ReflectionField.class))
                {
                    if (inReflectionField(setfield.getName(),
                            (ReflectionField) ReflectionUtils.getFieldAnnotation(field, ReflectionField.class)))
                    {
                        isFind = true;
                        setFiled(field, setfield, obj, value);
                    }
                }
            }
        }
    }

    /**
     * @param value 值的字段
     * @param filed 被设置的值的字段
     * @param obj   被设置对象
     * @param val   值对象
     * @throws Exception
     */
    public static void setFiled(Field value, Field filed, Object obj, Object val) throws Exception
    {
        Object value1 = ReflectionUtils.getValue(val, value);
        if (value1 == null) value1 = BeanUtils.getValue(val, value.getName());
        ReflectionUtils
                .setValue(obj, filed.getName(), XMLUtils.convertData(filed, XMLUtils.DataFormat(value, value1)));
    }

    public static boolean inReflectionField(String filedName, ReflectionField reflectionField)
    {
        if (reflectionField == null) return false;
        if (StringUtils.isEmpty(filedName)) return false;
        for (String name : reflectionField.value())
        {
            if (name.equals(filedName)) return true;
        }
        return false;
    }

    /**
     * 复制值-由于接口方定义字段比较奇葩。有时大写有时下划线。通过ReflectionField 注解配置要复制值的字段
     * @param createClass
     * @param target
     * @param targetClass
     * @param <T>
     * @param <E>
     * @return
     * @throws Exception
     */
    public static <T, E> T copyBeanValue(Class<T> createClass, Object target, Class<E> targetClass) throws Exception
    {
        if (createClass == null || target == null) return null;
        Field[] classFiled = ReflectionUtils.getClassFiled(createClass);
        Object invoke = createClass.newInstance();
        for (Field filed : classFiled)
        {
            initItem(invoke, filed.getName(), target, targetClass);
        }
        return (T) invoke;
    }

    /**
     *  根据一个集合生成另一个集合对象-支持 ReflectionField 注解
     * @param createClass 需要创建集合对象的泛型class
     * @param invokeList 依赖的集合
     * @param invokeClass 依赖集合中的class 类型-由于invokeList 可能是代理对象所以这里需要主动传入
     * @param optionInterface 操作接口对象-可能会对生成的每一个bean进行转意 例如：把里面的type为1 转成 “男”
     * @param c 需要操作类依赖的参数
     * @param <T>
     * @param <E>
     * @param <C>
     * @return
     * @throws Exception
     */
    public static <T, E, C> List<T> createListBean(Class<T> createClass, List<E> invokeList, Class<E> invokeClass,
                                                OptionInterface<T,C> optionInterface,
                                                C c)
            throws Exception
    {
        if (createClass == null || invokeClass == null | invokeList == null || invokeList.size() == 0)
            return new ArrayList<T>();
        List<T> results = new ArrayList<T>();
        for (E e : invokeList)
        {
            T result = copyBeanValue(createClass, e, invokeClass);
            optionInterface.pushBefore(result, c);
            results.add(result);
        }
        return results;
    }

    public static <T, E> List<T> createListBean(Class<T> createClass, List<E> invokeList, Class<E> invokeClass)
            throws Exception
    {
        if (createClass == null || invokeClass == null | invokeList == null || invokeList.size() == 0)
            return new ArrayList<T>();
        List<T> results = new ArrayList<T>();
        for (E e : invokeList)
        {
            T result = copyBeanValue(createClass, e, invokeClass);
            results.add(result);
        }
        return results;
    }

    public static void main(String[] agrs) throws IllegalAccessException, InstantiationException, NoSuchFieldException
    {
    }

}