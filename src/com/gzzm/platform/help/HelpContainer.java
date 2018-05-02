package com.gzzm.platform.help;

import com.gzzm.platform.annotation.CacheInstance;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2010-12-13
 */
@CacheInstance("Help")
public class HelpContainer
{
    @Inject
    private static Provider<HelpDao> daoProvider;

    private Map<String, HelpInfo> helps = new HashMap<String, HelpInfo>();

    public HelpContainer() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        for (Help help : daoProvider.get().getAllHelps())
        {
            String appId = help.getAppId();
            String url;
            if (StringUtils.isEmpty(appId))
                url = help.getUrl();
            else
                url = "/menu/" + appId;
            helps.put(url, new HelpInfo(help.getHelpId(), help.getContent()));
        }
    }

    public HelpInfo getHelp(String url)
    {
        return helps.get(url);
    }
}
