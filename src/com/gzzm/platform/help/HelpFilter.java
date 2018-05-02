package com.gzzm.platform.help;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.message.comet.CometService;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author camel
 * @date 2010-12-13
 */
public class HelpFilter implements Filter
{
    @Inject
    private static Provider<HelpContainer> containerProvider;

    @Inject
    private static Provider<CometService> cometServiceProvider;

    @Inject
    private static Provider<HelpDao> daoProvider;

    public void init(FilterConfig config) throws ServletException
    {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        chain.doFilter(request, response);

        try
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(httpRequest);

            if (userOnlineInfo != null)
            {
                String uri = WebUtils.getRequestURI(httpRequest);

                HelpInfo help = null;

                HelpContainer container = containerProvider.get();

//                String queryString = httpRequest.getQueryString();
//                if (queryString != null && queryString.length() > 0)
//                    help = container.getHelp(uri + "?" + queryString);

                if (help == null)
                    help = container.getHelp(uri);

                if (help != null)
                {
                    if (!daoProvider.get().isHelpExcluded(userOnlineInfo.getUserId(), help.getHelpId()))
                        cometServiceProvider.get().sendMessage(help, userOnlineInfo.getUserId());
                }
            }
        }
        catch (Throwable ex)
        {
            //发送帮助错误，不影响主逻辑
            Tools.log(ex);
        }
    }

    public void destroy()
    {
    }
}
