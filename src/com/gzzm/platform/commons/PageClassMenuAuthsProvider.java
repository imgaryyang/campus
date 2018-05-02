package com.gzzm.platform.commons;

import com.gzzm.platform.menu.HttpMethod;
import com.gzzm.platform.menu.*;
import net.cyan.arachne.*;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * 从PageClass中权限的基类
 *
 * @author camel
 * @date 2009-12-22
 */
public abstract class PageClassMenuAuthsProvider implements MenuAuthsProvider
{
    /**
     * 权限信息，定义此Class对应哪些权限，一个对象代表一个权限
     */
    protected static class AuthInfo
    {
        /**
         * 权限代码
         */
        private String code;

        /**
         * 权限名称
         */
        private String name;

        /**
         * 方法列表，代表此权限能访问哪些方法
         */
        private List<Method> methods = new ArrayList<Method>();

        public AuthInfo(String code, String name)
        {
            this.code = code;
            this.name = name;
        }

        /**
         * 添加一个方法
         *
         * @param method    方法名称
         * @param name      方法描述名
         * @param parameter 此方法是否校验参数
         * @return 返回对象本身以链式编程
         */
        public AuthInfo addMethod(String method, String name, boolean parameter)
        {
            methods.add(new Method(method, name, parameter));
            return this;
        }

        public AuthInfo addMethod(String method, String name)
        {
            return addMethod(method, name, true);
        }
    }

    protected static class Method
    {
        /**
         * 方法的java名称
         */
        private String method;

        /**
         * 方法的中文名，即方法的说明
         */
        private String name;

        /**
         * 是否要求校验参数
         */
        private boolean parameter;

        private Method(String method, String name, boolean parameter)
        {
            this.method = method;
            this.name = name;
            this.parameter = parameter;
        }
    }

    public PageClassMenuAuthsProvider()
    {
    }

    @SuppressWarnings("unchecked")
    public List<MenuAuth> getAuths(String url) throws Exception
    {
        int index = url.indexOf('?');
        String queryString = null;
        if (index > 0)
        {
            queryString = url.substring(index + 1);
            url = url.substring(0, index);
        }

        List<KeyValue<String>> parameters = null;
        if (queryString != null)
        {
            parameters = queryStringToParameter(queryString);
        }

        PageClass pageClass = null;
        try
        {
            pageClass = PageClass.getClassInfo(url);
        }
        catch (Exception ex)
        {
            //url不是对应一个class，跳过
        }

        if (pageClass != null)
        {
            List<AuthInfo> authInfos = getAuthInfos(pageClass);
            if (authInfos != null)
            {
                int n = authInfos.size();
                if (n == 0)
                    return Collections.EMPTY_LIST;

                List<MenuAuth> auths = new ArrayList<MenuAuth>(n);

                for (AuthInfo authInfo : authInfos)
                {
                    MenuAuth menuAuth = new MenuAuth();
                    menuAuth.setAuthCode(authInfo.code);
                    menuAuth.setAuthName(authInfo.name);

                    List<MenuAuthUrl> urls = new ArrayList<MenuAuthUrl>();

                    //解析各方法的url
                    for (Method method : authInfo.methods)
                    {
                        PageMethod pageMethod = pageClass.getMethod(method.method);

                        if (pageMethod != null)
                        {
                            HttpMethod httpMethod = null;
                            if (pageMethod.getHttpMethod() != null)
                                httpMethod = HttpMethod.valueOf(pageMethod.getHttpMethod().toString().toUpperCase());

                            for (String s : loadUrls(pageClass, pageMethod))
                            {
                                if (parameters != null && httpMethod == HttpMethod.GET && method.parameter)
                                    s = addParametersToUrl(s, parameters, queryString);

                                urls.add(new MenuAuthUrl(method.name, s, httpMethod));
                            }
                        }
                    }

                    menuAuth.setAuthUrls(urls);
                    auths.add(menuAuth);
                }

                return auths;
            }
        }

        return null;
    }

    /**
     * 从PageMethod中解析url
     *
     * @param pageClass class
     * @param method    方法
     * @return url列表
     * @throws Exception 解析url出错
     */
    protected static List<String> loadUrls(PageClass pageClass, PageMethod method) throws Exception
    {
        List<String> urlList = method.getUrls();

        if (urlList != null)
        {
            List<String> urls = new ArrayList<String>(urlList.size());

            for (String url : urlList)
            {
                urls.add(url.replaceAll("\\{@class\\}", pageClass.getUrl().substring(1))
                        .replaceAll("\\{@method\\}", method.getName()).replaceAll("(\\{[^\\}]+\\})|(\\[[^\\]]+\\])",
                                "\\*"));
            }

            return urls;
        }
        else
        {
            return Collections.singletonList(
                    method.getUrl(pageClass, null, method.getName().equals(pageClass.getDefaultMethod())));
        }
    }

    protected List<KeyValue<String>> queryStringToParameter(String queryString)
    {
        String[] ss = queryString.split("&");
        List<KeyValue<String>> parameters = new ArrayList<KeyValue<String>>(ss.length);

        for (String s : ss)
        {
            int index = s.indexOf('=');
            if (index > 0)
            {
                parameters.add(new KeyValue<String>(s.substring(0, index), s.substring(index + 1)));
            }
        }

        return parameters;
    }

    protected String addParametersToUrl(String url, List<KeyValue<String>> parameters, String queryString)
    {
        if (parameters == null)
        {
            if (StringUtils.isEmpty(queryString))
                return url;

            parameters = queryStringToParameter(queryString);
        }

        int index = url.indexOf('?');
        if (index < 0)
        {
            if (queryString == null)
            {
                StringBuilder buffer = new StringBuilder(url).append("?");
                boolean first = true;
                for (KeyValue<String> parameter : parameters)
                {
                    if (first)
                        first = false;
                    else
                        buffer.append("&");
                    buffer.append(parameter.getKey()).append("=").append(parameter.getValue());
                }

                return buffer.toString();
            }
            else
            {
                return url + "?" + queryString;
            }
        }
        else
        {
            List<KeyValue<String>> parameters2 = queryStringToParameter(url.substring(index + 1));
            StringBuilder buffer = null;

            boolean b = false;

            for (KeyValue<String> parameter : parameters)
            {
                String value = null;
                for (Iterator<KeyValue<String>> iterator = parameters2.iterator(); iterator.hasNext(); )
                {
                    KeyValue<String> parameter2 = iterator.next();
                    if (parameter.getKey().equals(parameter2.getKey()))
                    {
                        value = parameter2.getValue();
                        if ("*".equals(value))
                        {
                            iterator.remove();
                            value = null;
                            b = true;
                        }

                        break;
                    }
                }

                if (value == null)
                {
                    if (buffer == null)
                        buffer = new StringBuilder();
                    else
                        buffer.append("&");
                    buffer.append(parameter.getKey()).append("=").append(parameter.getValue());
                }
            }

            if (buffer == null)
            {
                return url;
            }
            else
            {
                if (b)
                {
                    StringBuilder buffer2 = new StringBuilder(url.substring(0, index)).append("?");

                    for (KeyValue<String> parameter : parameters2)
                        buffer2.append(parameter.getKey()).append("=").append(parameter.getValue()).append("&");

                    buffer2.append(buffer);

                    return buffer2.toString();
                }
                else
                {
                    buffer.insert(0, "&");
                    buffer.insert(0, url);

                    return buffer.toString();
                }
            }
        }
    }

    /**
     * 获得次class的权限列表
     *
     * @param pageClass pageClass
     * @return 权限列表，每个元素对应多个AuthInfo
     * @throws Exception 允许子类实现此方法时抛出异常
     */
    protected abstract List<AuthInfo> getAuthInfos(PageClass pageClass) throws Exception;
}
