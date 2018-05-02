package com.gzzm.platform.login;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.PageError;
import net.cyan.arachne.result.JsonResultSerializer;
import net.cyan.commons.log.Log;
import net.cyan.commons.util.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * 权限过滤器
 *
 * @author camel
 * @date 2009-7-22
 */
public class AuthorityFilter implements Filter
{
    private volatile Authoritys authoritys;

    /**
     * 不检查的扩展名
     */
    private final Set<String> no_checked_exts = new HashSet<String>();

    public AuthorityFilter()
    {
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        no_checked_exts.add("js");
        no_checked_exts.add("css");
        no_checked_exts.add("jpg");
        no_checked_exts.add("jpge");
        no_checked_exts.add("gif");
        no_checked_exts.add("bmp");
        no_checked_exts.add("png");
        no_checked_exts.add("zip");
        no_checked_exts.add("rar");
        no_checked_exts.add("jar");
        no_checked_exts.add("tar");
        no_checked_exts.add("gz");
        no_checked_exts.add("doc");
        no_checked_exts.add("xls");
        no_checked_exts.add("pdf");
        no_checked_exts.add("ppt");
        no_checked_exts.add("swf");
        no_checked_exts.add("lp");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException
    {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);

        LoginPage.autoLogin(httpRequest, (HttpServletResponse) response);

        long time = System.currentTimeMillis();
        Authoritys authoritys = getAuthoritys();
        String uri = WebUtils.getRequestURI(httpRequest);

        try
        {
            boolean checked = true;

            //排除不需要检查的扩展名
            int index = uri.lastIndexOf(".");
            if (index > 0)
                checked = !no_checked_exts.contains(uri.substring(index + 1).toLowerCase());


            if (checked)
            {
                //排除不需要检查的路径
                checked = !authoritys.isNoCheckedUrl(uri);

                if (checked)
                {
                    try
                    {
                        authoritys.check(httpRequest);
                    }
                    catch (Throwable ex)
                    {
                        String message;
                        if (ex instanceof NoErrorException)
                        {
                            message = ((NoErrorException) ex).getDisplayMessage();
                        }
                        else
                        {
                            Tools.log(ex);
                            message = Tools.getMessage("common.unknown_error");
                        }

                        //异常，处理异常，转向错误提示的页面
                        String accept = httpRequest.getHeader("Accept");

                        if (Mime.isJson(accept))
                        {
                            try
                            {
                                JsonResultSerializer.INSTANCE.serialize(new PageError(message), httpRequest,
                                        (HttpServletResponse) response);
                            }
                            catch (Throwable ex1)
                            {
                                Tools.log(ex1);
                            }
                        }
                        else
                        {
                            String charset = "UTF-8";
                            response.setCharacterEncoding(charset);
                            response.setContentType("text/plain; charset=" + charset);
                            response.getWriter().write(message);
                        }

                        return;
                    }
                }
            }

            filterChain.doFilter(request, response);
        }
        finally
        {
            Log log = authoritys.getLog();
            if (log != null)
            {
                long cost = System.currentTimeMillis() - time;
                String s = "access " + uri + " with " + cost + "ms";
                if (cost > 2000)
                    s += " very very long";
                else if (cost > 1000)
                    s += " very long";
                else if (cost > 500)
                    s += " long";

                log.debug(s);
            }
        }
    }

    private Authoritys getAuthoritys()
    {
        if (authoritys == null)
        {
            synchronized (this)
            {
                if (authoritys == null)
                {
                    try
                    {
                        authoritys = Tools.getBean(Authoritys.class);
                    }
                    catch (Exception ex)
                    {
                        Tools.wrapException(ex);
                    }
                }
            }
        }

        return authoritys;
    }

    public void destroy()
    {
    }
}
