package com.gzzm.platform.commons;

import com.gzzm.platform.desktop.StyleUtils;
import net.cyan.arachne.*;
import net.cyan.commons.file.CommonFileService;
import net.cyan.commons.log.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.commons.util.language.Language;
import net.cyan.nest.annotation.Inject;

import java.io.File;
import java.util.Map;

/**
 * 工具类
 *
 * @author camel
 * @date 2009-7-18
 */
public final class Tools
{
    @Inject
    private static Provider<CommonDao> daoProvider;

    /**
     * 系统编码,-1表示还未加载系统编码，需要从数据库加载
     */
    private static int systemCode = -1;

    private Tools()
    {
    }

    /**
     * 将某个信息国际化
     *
     * @param message 信息，其中参数用{0} {1} {2}表示
     * @return 国际化后的信息
     */
    public static String getMessage(String message)
    {
        return Language.getLanguage().getWord(message, message);
    }

    /**
     * 将某个信息国际化
     *
     * @param message 信息，其中参数用{0} {1} {2}表示
     * @param args    参数值
     * @return 国际化后的信息
     */
    public static String getMessage(String message, Object... args)
    {
        return Language.getLanguage().getWord(message, message, args);
    }

    /**
     * 将某个信息国际化
     *
     * @param message 信息，其中参数用{name}表示
     * @param args    参数,key为参数名，value为参数值
     * @return 国际化后的信息
     */
    public static String getMessage(String message, Map args)
    {
        return Language.getLanguage().getWord(message, message, args);
    }

    public static String getMessage(String message, Object obj)
    {
        return Language.getLanguage().getWord(message, message, obj);
    }

    public static void log(String message, Throwable ex)
    {
        getLog().error(message, ex);
    }

    public static void log(Throwable ex)
    {
        getLog().error(ex);
    }

    public static void log(String message)
    {
        getLog().info(message);
    }

    public static void debug(String message)
    {
        getLog().debug(message);
    }

    private static Log getLog()
    {
        Log log = getLog(4);
        if (log instanceof BaseLog)
        {
            ((BaseLog) log).addNoTrack("com.gzzm.platform.commons.Tools");
        }

        return log;
    }

    private static Log getLog(int index)
    {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        try
        {
            StackTraceElement element = ExceptionUtils.getFirstStackTraceElement(null, elements, index);
            if (element != null)
                return LogManager.getLog(Class.forName(element.getClassName()));
            else
                return LogManager.getLog("com.gzzm");
        }
        catch (ClassNotFoundException ex)
        {
            return getLog(index + 1);
        }
    }

    /**
     * 获得一个对象
     *
     * @param c 对象的类型
     * @return 对象
     * @throws Exception 容器创建对象或初始化对象错误
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> c) throws Exception
    {
        return (T) SystemConfig.getInstance().get(c, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> c, String name) throws Exception
    {
        return (T) SystemConfig.getInstance().get(c, name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBeanWithDefualt(Class<T> c, String name) throws Exception
    {
        if (StringUtils.isEmpty(name))
            return (T) SystemConfig.getInstance().get(c);

        T bean = (T) SystemConfig.getInstance().get(c, name);
        if (bean == null)
            bean = (T) SystemConfig.getInstance().get(c);

        return bean;
    }

    public static CommonFileService getCommonFileService(String name) throws Exception
    {
        return getBeanWithDefualt(CommonFileService.class, name);
    }

    /**
     * 获得绝对路径
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getAppPath(String path)
    {
        return SystemConfig.getInstance().getAppPath(path);
    }

    /**
     * 获得绝对路径
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getConfigPath(String path)
    {
        return SystemConfig.getInstance().getRealPath(path);
    }

    /**
     * 包装异常，将非Runtime的异常包装成Runtime的异常，然后抛出
     *
     * @param ex 要包装的异常
     */
    public static void wrapException(Throwable ex)
    {
        ExceptionUtils.wrapException(ex);
    }

    /**
     * 将Throwable抛出
     *
     * @param ex 要抛出的Throwable
     * @throws Exception 抛出的Exception
     */
    public static void handleException(Throwable ex) throws Exception
    {
        ExceptionUtils.handleException(ex);
    }

    public static Throwable getRealException(Throwable ex)
    {
        return ExceptionUtils.getRealException(ex);
    }

    /**
     * 获得数据库里某个配置项的值，并自动转化为目标类型，配置项是值保存在PFCONFIG表中的数据
     *
     * @param name 配置项名称
     * @param type 配置项类型
     * @return 配置项的值
     * @throws Exception 数据库获取数据异常
     */
    public static <T> T getConfig(String name, Class<T> type) throws Exception
    {
        return DataConvert.convertValue(type, daoProvider.get().getConfig(name));
    }

    /**
     * 设置数据库某个配置项的值
     *
     * @param name  配置项的名称
     * @param value 配置项的值
     * @throws Exception 数据库保存数据异常
     */
    public static void setConfig(String name, Object value) throws Exception
    {
        daoProvider.get().setConfig(name, DataConvert.toString(value));
    }

    /**
     * 获得数据库里某个配置项的值，并自动转化为目标类型，配置项是值保存在PFCONFIG表中的数据
     *
     * @param name         配置项名称
     * @param type         配置项类型
     * @param defaultValue 默认值，当数据库没有配置此配置项时返回此值，并用其生成数据库配置项
     * @return 配置项的值
     * @throws Exception 数据库获取数据异常
     */
    public static <T> T getConfig(String name, Class<T> type, Object defaultValue) throws Exception
    {
        return DataConvert.convertValue(type, daoProvider.get().getConfig(name, StringUtils.toString(defaultValue)));
    }

    /**
     * 以字符串形式获取数据库某个配置项的值
     *
     * @param name 配置项名称
     * @return 配置项值
     * @throws Exception 数据库读取数据异常
     */
    public static String getConfig(String name) throws Exception
    {
        return daoProvider.get().getConfig(name);
    }

    /**
     * 获得系统编号，系统编号代表一个系统的唯一性，系统编号保存在config表中，key为SYSTEMCODE
     * 系统产生序列号时在序列号的前面加上系统编号，当多个系统的数据整合时，保证序列号不会冲突
     *
     * @return 系统编号
     * @throws Exception 从数据库读取系统编号错误
     */
    public static synchronized int getSystemCode() throws Exception
    {
        if (systemCode == -1)
        {
            String s = getConfig("SYSTEMCODE");
            if (s != null)
            {
                try
                {
                    systemCode = Integer.parseInt(s);
                }
                catch (NumberFormatException ex)
                {
                    //系统定义的编码不是整数，使用默认编码，并输出异常
                }
            }

            //如果数据库中没有定义系统编号，则默认使用系统编号为0
            if (systemCode == -1)
                systemCode = 0;
        }
        return systemCode;
    }

    public static int getStartYear()
    {
        try
        {
            int startYear = getConfig("START_YEAR", Integer.class, 2005);

            if (startYear == 0)
                startYear = 2005;

            return startYear;
        }
        catch (Throwable ex)
        {
            return 2005;
        }
    }

    /**
     * 获得一个uuid
     *
     * @return uuid
     */
    public static String getUUID()
    {
        return CommonUtils.uuid();
    }

    /**
     * 调用线程池执行一个对象
     *
     * @param runnable 执行的runnable
     */
    public static void run(Runnable runnable)
    {
        Jobs.run(runnable);
    }

    public static String getContextPath(String path)
    {
        return PageConfig.getContextPath(path);
    }

    public static String getFileTypeIcon(String ext)
    {
        if (ext != null)
        {
            int index = ext.lastIndexOf('.');
            if (index >= 0)
                ext = ext.substring(index + 1);
        }

        String path = "/platform/commons/icons/files/";
        String defaultIcon = "default.gif";

        if (StringUtils.isEmpty(ext))
            return path + defaultIcon;

        String icon = path + ext.toLowerCase() + ".gif";
        return new File(getAppPath(icon)).exists() ? icon : path + defaultIcon;
    }

    public static String getCommonIcon(String name)
    {
        String stylePath = StyleUtils.getStylePath();
        if (!StringUtils.isEmpty(stylePath))
        {
            String icon = stylePath + "/icons/" + name + ".gif";
            if (new File(getAppPath(icon)).exists())
                return icon;
        }

        String icon = "/platform/commons/icons/" + name + ".gif";
        return new File(getAppPath(icon)).exists() ? icon : null;
    }

    public static boolean matchText(String s, String text)
    {
        return s != null && text != null && (matchText0(s, text) || matchText0(Chinese.getFirstLetters(s), text) ||
                matchText0(Chinese.getLetters(s), text));
    }

    private static boolean matchText0(String s, String text)
    {
        int n = s.length();
        int m = text.length();
        for (int i = 0; i < n; i++)
        {
            int pos = 0;
            for (int j = i; j < n; j++)
            {
                if (s.charAt(j) == text.charAt(pos))
                {
                    pos++;
                    if (pos == m)
                        return true;
                }
                else
                {
                    if (pos == 0)
                        break;
                }
            }
        }

        return false;
    }

    public static Long saveError(Throwable ex, String message)
    {
        try
        {
            StringBuilder buffer = new StringBuilder();
            if (message != null)
                buffer.append(message).append("\r\n");

            ExceptionUtils.logThrowable(ex, buffer);

            SystemError error = new SystemError();
            error.setMessage(message);
            error.setContent(buffer.toString().toCharArray());

            daoProvider.get().add(error);

            return error.getErrorId();
        }
        catch (Throwable ex1)
        {
            //记录错误日志错误不再生成新的错误

            log(ex1);
            log(message, ex);
        }

        return 0L;
    }

    public static String getPdfViewUrl(String url)
    {
        String navigator = WebUtils.getNavigator(RequestContext.getContext().getRequest());
        if (navigator.startsWith("IE"))
        {
            try
            {
                String s = navigator.substring(2);
                int version = Integer.parseInt(s);
                if (version > 8)
                    return "/pdfjs/viewer/viewer.html?file=" + url;
            }
            catch (Throwable ex)
            {
                //无法识别的浏览器版本。
            }
        }
        return null;
    }

//    public static String
}