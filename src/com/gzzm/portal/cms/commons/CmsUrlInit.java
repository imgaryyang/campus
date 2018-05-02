package com.gzzm.portal.cms.commons;

import net.cyan.arachne.urlrewrite.UrlRewriteFilter;

/**
 * cmsurl初始化，添加一个urlrewriter
 *
 * @author camel
 * @date 2011-6-9
 */
public class CmsUrlInit implements Runnable
{
    public CmsUrlInit()
    {
    }

    public void run()
    {
        UrlRewriteFilter.addRewriter(new CmsUrlRewriter());
    }
}
