package com.gzzm.portal.cms.template;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.portal.cms.station.Station;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 页面url映射，缓存所有定义了url的页面
 * 保存url和页面id直接的关系，urlwriter可以读取此缓存，决定哪个url跳转到哪个页面
 *
 * @author camel
 * @date 2011-6-9
 */
@CacheInstance("cms.page")
public class PageUrlMapper
{
    @Inject
    private static Provider<PageTemplateDao> daoProvider;

    /**
     * 保存url和页面id的关系，key为页面url，value为页面ID
     */
    private Map<String, Integer> map = new HashMap<String, Integer>();

    private PageUrlMapper() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        for (PageTemplate template : daoProvider.get().getPageTemplatesForUrlMap())
        {
            String url = template.getUrl();


            Integer templateId = template.getTemplateId();
            Station station = template.getStation();
            if (station != null)
            {
                String domain = template.getStation().getDomainName();
                if (!StringUtils.isEmpty(domain))
                    map.put(domain + url, templateId);
            }

            map.put(url, templateId);
            map.put(template.getPath(), templateId);
        }
    }

    public Integer getTemplateId(String url)
    {
        return map.get(url);
    }
}
