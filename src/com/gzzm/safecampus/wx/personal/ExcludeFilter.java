package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.wx.common.WxConfig;
import net.cyan.commons.util.Filter;
import net.cyan.nest.annotation.Inject;

/**
 * 例外路径过滤器
 * 在wxconfig中配置例外的路径不进行拦截
 *
 * @author Neo
 * @date 2018/3/27 18:07
 */
public class ExcludeFilter implements Filter<String>
{
    @Inject
    protected WxConfig wxConfig;

    public ExcludeFilter()
    {
    }

    /**
     * 路径拦截
     *
     * @param uri 请求路径
     * @return 返回 true 不拦截 false拦截
     * @throws Exception 操作异常
     */
    @Override
    public boolean accept(String uri) throws Exception
    {
        if (wxConfig != null && wxConfig.getExcludeUrls() != null)
        {
            for (String excludeUrl : wxConfig.getExcludeUrls())
            {
                if (excludeUrl.charAt(excludeUrl.length() - 1) == '/')
                {
                    if (uri.startsWith(excludeUrl))
                        return true;
                } else if (uri.equals(excludeUrl))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
