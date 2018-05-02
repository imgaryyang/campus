package com.gzzm.platform.desktop;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 显示待办列表的页面
 *
 * @author camel
 * @date 2010-6-11
 */
@Service(url = "/pending")
public class PendingPage extends BaseListCrud<PendingCount>
{
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Inject
    private List<PendingItem> items;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private OrganDao organDao;

    /**
     * 显示空的待办事项
     */
    private boolean displayEmpty;

    private String name;

    public PendingPage()
    {
        setPageSize(-1);
    }

    public boolean isDisplayEmpty()
    {
        return displayEmpty;
    }

    public void setDisplayEmpty(boolean displayEmpty)
    {
        this.displayEmpty = displayEmpty;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    protected void loadList() throws Exception
    {
        List<PendingCount> result;
        if (items != null && items.size() > 0)
        {
            result = new ArrayList<PendingCount>(items.size());

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("userId", userOnlineInfo.getUserId());
            parameters.put("deptId", userOnlineInfo.getDeptId());
            parameters.put("bureauId", userOnlineInfo.getBureauId());
            Dept bureau = organDao.getDept(userOnlineInfo.getBureauId());
            parameters.put("sourceBureauId", bureau.getSourceId());
            parameters.put("sourceUserId", userOnlineInfo.getUserEntity().getSourceId());

            for (PendingItem item : items)
            {
                if (name == null && item.getName() != null || name != null && !name.equals(item.getName()))
                    continue;

                String menuId = item.getMenuId();
                if (StringUtils.isEmpty(menuId) || userOnlineInfo.isAdmin() || userOnlineInfo.hasApp(menuId))
                {
                    String sql = item.getSql();

                    int count;

                    if (StringUtils.isEmpty(sql))
                    {
                        //没有定义sql
                        if (!StringUtils.isEmpty(menuId))
                            count = getCountByUrl(item);
                        else
                            count = 0;
                    }
                    else
                    {
                        //定义了sql，用sql计算
                        if (!StringUtils.isEmpty(menuId))
                        {
                            Collection<Integer> deptIds = userOnlineInfo.getAuthDeptIds(menuId);
                            parameters.put("deptIds", deptIds);
                        }

                        String database = item.getDatabase();
                        if (StringUtils.isEmpty(database))
                        {
                            count = getCrudService().sqlQueryFirst(sql.trim(), Integer.class, parameters);
                        }
                        else
                        {
                            count = SimpleDao.getInstance(database)
                                    .sqlQueryFirst(sql.trim(), Integer.class, parameters);
                        }
                    }

                    if (displayEmpty || item.isDisplayEmpty() || count != 0)
                        result.add(new PendingCount(item, count));
                }
            }
        }
        else
        {
            result = Collections.emptyList();
        }

        setList(result);
    }

    @Override
    protected Object createListView() throws Exception
    {
        SimplePageListView view = new SimplePageListView();

        view.importJs("/platform/desktop/pending.js");
        view.importCss("/platform/desktop/pending.css");

        return view;
    }

    public static int getCountByUrl(PendingItem item) throws Exception
    {
        String url = item.getUrl();

        int index = url.indexOf('?');
        String queryString = null;
        if (index > 0)
        {
            queryString = url.substring(index + 1);
            url = url.substring(0, index);
        }

        Map<String, String[]> parameters = null;
        if (queryString != null)
        {
            parameters = WebUtils.queryStringToParameterMap(queryString);
        }

        if (item.getArgs() != null)
        {
            if (parameters == null)
            {
                parameters = new HashMap<String, String[]>();
            }

            for (Map.Entry<String, String> entry : item.getArgs().entrySet())
            {
                parameters.put(entry.getKey(), new String[]{entry.getValue()});
            }
        }

        PageClass pageClass = null;
        try
        {
            pageClass = PageClass.getClassInfo(url);
        }
        catch (Exception ex)
        {
            //url不是对应一个class，跳过
        }

        if (pageClass != null)
        {
            if (CountCrud.class.isAssignableFrom(pageClass.getType()))
            {
                HttpServletRequest request = RequestContext.getContext().getRequest();
                String menuId = MenuItem.getMenuId(request);
                try
                {
                    if (!StringUtils.isEmpty(item.getMenuId()))
                        MenuItem.setMenuId(request, item.getMenuId());

                    Crud crud = PageConfig.getBean(pageClass.getType());

                    if (parameters != null)
                    {
                        BeanUtils.setExpressionValue(crud, parameters);
                    }

                    try
                    {
                        CrudAdvice.before(crud);

                        int count = ((CountCrud) crud).loadCount();

                        CrudAdvice.after(crud, count);

                        return count;
                    }
                    catch (Throwable ex)
                    {
                        CrudAdvice.catchHandle(crud, ex);

                        Tools.handleException(ex);
                    }
                    finally
                    {
                        CrudAdvice.finallyHandle(crud);
                    }
                }
                finally
                {
                    MenuItem.setMenuId(request, menuId);
                }
            }
        }

        return 0;
    }
}