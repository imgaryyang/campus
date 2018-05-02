package com.gzzm.platform.menu;

import com.gzzm.platform.commons.SystemException;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.JsonUtils;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 生成菜单树的组件
 *
 * @author camel
 * @date 2009-7-27
 */
@Injectable
public class MenuComponent extends AbstractPageComponent<MenuTree>
{
    @Inject
    private static Provider<MenuContainer> containerProvider;

    private String group;

    @Inject
    private MenuDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    public MenuComponent()
    {
    }

    public String createHtml(String tagName, Map<String, Object> attributes) throws Exception
    {
        if (model == null)
        {
            if (group == null)
                throw new SystemException("group cannot be null");

            model = containerProvider.get().getMenuTree(group);

            if (model == null)
                throw new SystemException("menu tree " + group + " does not exist");
        }
        else
        {
            group = model.getGroup();
        }


        List<MenuItem> menus = model.getTree(userOnlineInfo, false, true);

        UserMenuConfig userMenuConfig = dao.getUserMenuConfig(userOnlineInfo.getUserId(), group);
        if (userMenuConfig != null)
        {
            MenuConfig menuConfig = userMenuConfig.getMenuConfig();

            if (menuConfig != null && menuConfig.getMenuIds() != null)
            {
                Collections.sort(menus, menuConfig);
            }
        }

        StringBuilder buffer = new StringBuilder("<script>\n");

        buffer.append("System.Menu.create(");

        JsonUtils.toJson(menus, buffer, false);

        buffer.append(",\"").append(group).append("\"");

        if (userMenuConfig != null && userMenuConfig.getShowSize() != null)
            buffer.append(",").append(userMenuConfig.getShowSize());
        buffer.append(");\n</script>");

        return buffer.toString();
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }
}
