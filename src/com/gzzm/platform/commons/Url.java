package com.gzzm.platform.commons;

import net.cyan.commons.util.io.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 定义一个url模式，能匹配某种格式的url
 *
 * @author camel
 * @date 2009-12-28
 */
public class Url
{
    /**
     * serverPath的正则表达式
     */
    private Pattern serverPath;

    private String method;

    /**
     * 参数列表
     */
    private Map<String, List<String>> parameters;

    public Url(String url, String method)
    {
        if (method != null)
            this.method = method.toUpperCase();

        url = url.trim();
        String serverPath = url;

        int index = url.indexOf("?");
        if (index > 0)
        {
            serverPath = url.substring(0, index);
            url = url.substring(index + 1);

            if (url.length() > 0)
            {
                String[] ss = url.split("&");

                parameters = new HashMap<String, List<String>>(ss.length);
                for (String s : ss)
                {
                    index = s.indexOf("=");
                    if (index > 0)
                    {
                        String name = s.substring(0, index);
                        String value = s.substring(index + 1);
                        value = WebUtils.decode(value);

                        List<String> values = parameters.get(name);
                        if (values == null)
                        {
                            parameters.put(name, values = new ArrayList<String>());
                        }

                        values.add(value);
                    }
                }

                for (List<String> values : parameters.values())
                {
                    if (values.size() > 1)
                    {
                        Collections.sort(values);
                    }
                }
            }
        }

        this.serverPath = Pattern.compile(serverPath.replace("*", "[^/\\.]+"));
    }

    /**
     * 判断请求和是否匹配
     *
     * @param request http请求
     * @return 匹配返回true，不匹配返回false
     */
    public boolean matches(HttpServletRequest request)
    {
        if (method != null && !method.equals(request.getMethod()))
        {
            //如果定义了method，则必须匹配method
            return false;
        }

        String serverPath = WebUtils.getRequestURI(request);
        if (this.serverPath.matcher(serverPath).matches())
        {
            if (parameters != null)
            {
                for (Map.Entry<String, List<String>> entry : parameters.entrySet())
                {
                    List<String> values = entry.getValue();
                    String[] values1 = request.getParameterValues(entry.getKey());

                    if (values1 == null)
                        return false;

                    int size = values.size();
                    if (values1.length != size)
                        return false;

                    if (values1.length > 0)
                    {
                        Arrays.sort(values1);

                        for (int i = 0; i < size; i++)
                        {
                            if (!values.get(i).equalsIgnoreCase(values1[i]))
                                return false;
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }
}
