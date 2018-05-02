package com.gzzm.portal.cms.commons;

import net.cyan.arachne.urlrewrite.BaseRewriterResult;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;

/**
 * @author camel
 * @date 2015/7/3
 */
public class CmsRewriterResult extends BaseRewriterResult
{
    @Inject
    private static Provider<PageCache> pageCacheProvider;

    public CmsRewriterResult(String rewriteUrl)
    {
        super(rewriteUrl);
    }

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PageCache pageCache = pageCacheProvider.get();

        String path = pageCache.getPath(rewriteUrl);

        File file = null;
        if (path != null)
            file = new File(path);

        if (file != null && file.exists())
        {
            String charset = "UTF-8";
            response.setContentType(Mime.getHtmlContentType(charset));
            response.setCharacterEncoding(charset);
            response.setContentLength((int) file.length());
            IOUtils.fileToStream(path, response.getOutputStream());
            response.flushBuffer();
        }
        else
        {
            final CacheData cacheData = new CacheData();

            try
            {
                request.setAttribute("net.cyan.arachne.result.cache_data", cacheData);

                request.getRequestDispatcher(rewriteUrl).forward(request, response);

                if (cacheData.size() > 0)
                {
                    pageCache.addCache(rewriteUrl, cacheData);
                }
            }
            finally
            {
                try
                {
                    cacheData.clear();
                }
                catch (Throwable ex)
                {
                    //释放资源失败不抛出异常
                }
            }
        }
    }
}
