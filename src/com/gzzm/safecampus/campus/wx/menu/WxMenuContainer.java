package com.gzzm.safecampus.campus.wx.menu;

import com.gzzm.platform.annotation.CacheInstance;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 微信菜单容器，缓存微信菜单信息
 *
 * @author Neo
 * @date 2018/4/25 9:19
 */
@CacheInstance("WxMenu")
public class WxMenuContainer
{
    @Inject
    private static Provider<MenuDao> menuDaoProvider;

    private Map<Integer, Collection<String>> tagUrlMaps = new HashMap<>();

    public WxMenuContainer() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        List<MenuGroup> groups = menuDaoProvider.get().getAllEntities(MenuGroup.class);
        for (MenuGroup group : groups)
        {
            Collection<String> menuUrls = new ArrayList<>();
            List<Menu> menus = group.getMenus();
            for (Menu menu : menus)
            {
                if (StringUtils.isNotEmpty(menu.getUrl()))
                {
                    menuUrls.add(menu.getUrl());
                }
            }
            tagUrlMaps.put(group.getTagId() == null ? -1 : group.getTagId(), menuUrls);
        }
    }

    /**
     * 获取某个标签分组的所有菜单url
     *
     * @param tagId 标签Id
     * @return 菜单url列表
     */
    public Collection<String> getTagUrls(Integer tagId)
    {
        return tagUrlMaps.get(tagId);
    }
}
