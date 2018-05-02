package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.log.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.AppInfo;
import com.gzzm.platform.template.*;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.crud.exporters.*;
import net.cyan.crud.util.CrudBeanUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.PersistenceManager;

import javax.servlet.http.HttpServletRequest;

/**
 * crud相关的一些操作
 *
 * @author camel
 * @date 2009-11-15
 */
public final class SystemCrudUtils
{
    @Inject
    private static Provider<UserOnlineInfo> onlineInfoProvider;

    @Inject
    private static Provider<LogDao> logDaoProvider;

    @Inject
    private static Provider<TemplateService> templateServiceProvider;

    private SystemCrudUtils()
    {
    }

    public static String listForward(String forward, Crud crud)
    {
        return forward(forward, crud);
    }

    public static String treeForward(String forward, Crud crud)
    {
        return forward(forward, crud);
    }

    public static String editForward(String forward, Crud crud)
    {
        return forward(forward, crud);
    }

    public static String forward(String forward, Crud crud)
    {
        if (StringUtils.isEmpty(forward) || "forward".equals(crud.getAction()))
        {
            Object view = null;
            try
            {
                view = crud.getView();
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }

            if (view instanceof PageView)
            {
                String page = ((PageView) view).getPage();
                if (page != null)
                {
                    String page1 = Pages.PAGES.get(page);
                    return page1 != null ? page1 : page;
                }

                if (view instanceof SimpleDialogView)
                {
                    if ("forward".equals(crud.getAction()))
                        return Pages.QUERY;
                    else
                        return Pages.EDIT;
                }
                else if (view instanceof ComplexTableView)
                {
                    return Pages.COMPLEX;
                }
                else if (view instanceof PageTableView)
                {
                    return Pages.LIST;
                }
                else if (view instanceof SimplePageListView)
                {
                    return Pages.SIMPLE_LIST;
                }
                else if (view instanceof PageTreeTableView)
                {
                    return Pages.TABLE_TREE;
                }
                else if (view instanceof PageTreeView)
                {
                    return Pages.TREE;
                }
                else if (view instanceof PageChartView)
                {
                    return Pages.CHART;
                }
                else if (view instanceof ComplexPanelView)
                {
                    return Pages.COMPLEX;
                }
                else if (view instanceof PagePanelView)
                {
                    return Pages.PANEL;
                }
            }
        }

        return forward;
    }

    /**
     * 保存操作日志
     *
     * @param entity 实体对象，可以为null，entity和crud必须有一个不为null
     * @param action 操作的类型，添加，修改或者删除
     * @param crud   对应的crud类
     * @param remark 日志说明，如果为null，表示记录对象原来的内容
     */
    @SuppressWarnings("unchecked")
    public static <E, K> void saveLog(E entity, LogAction action, EntityCrud<E, K> crud, String remark)
    {
        try
        {
            Class<E> type;
            if (crud != null)
                type = crud.getEntityType();
            else
                type = PersistenceManager.getType(entity);

            K key;
            if (crud != null)
                key = crud.getKey(entity);
            else
                key = (K) PersistenceManager.getManager(type).getKey(entity);


            OperationLog log = new OperationLog();
            log.fill(onlineInfoProvider.get());

            log.setLogAction(action);
            log.setTarget(DataConvert.getValueString(key));
            log.setType(type.getName());

            if (crud instanceof ToString)
                log.setTargetName(((ToString<E>) crud).toString(entity));
            else
                log.setTargetName(entity.toString());

            if (remark == null)
                log.setRemark(CrudBeanUtils.serialBeanToXml(entity));
            else
                log.setRemark(remark);

            logDaoProvider.get().log(log);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }

    public static <E, K> void insertLog(EntityCrud<E, K> crud)
    {
        try
        {
            E entity = crud.getEntity();
            if (entity != null)
                saveLog(entity, LogAction.add, crud, "");
        }
        catch (Throwable ex)
        {
            //记录日志错误不影响主题流程
            Tools.log(ex);
        }
    }

    public static <E, K> void updateLog(EntityCrud<E, K> crud)
    {
        try
        {
            E entity = crud.getEntity(crud.getKey(crud.getEntity()));

            if (entity != null)
                saveLog(entity, LogAction.modify, crud, null);
        }
        catch (Throwable ex)
        {
            //记录日志错误不影响主题流程
            Tools.log(ex);
        }
    }

    public static <E, K> void deleteLog(K key, EntityCrud<E, K> crud)
    {
        try
        {
            E entity = crud.getEntity(key);

            if (entity != null)
                saveLog(entity, LogAction.delete, crud, crud.getDeleteTagField() == null ? null : "");

        }
        catch (Throwable ex)
        {
            //记录日志错误不影响主题流程
            Tools.log(ex);
        }
    }

    public static <E, K> void deleteAllLog(EntityCrud<E, K> crud)
    {
        try
        {
            for (K key : crud.getKeys())
            {
                deleteLog(key, crud);
            }
        }
        catch (Throwable ex)
        {
            //记录日志错误不影响主题流程
            Tools.log(ex);
        }
    }

    public static String getCondition()
    {
        RequestContext context = RequestContext.getContext();

        if (context == null)
            return null;

        HttpServletRequest request = context.getRequest();

        String menuId = MenuItem.getMenuId(request);

        if (menuId != null)
        {
            MenuItem menu = MenuItem.getMenu(menuId);

            if (menu != null)
            {
                String condition = menu.getCondition();

                UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
                if (userOnlineInfo != null)
                {
                    AppInfo appInfo = userOnlineInfo.getApp(menuId);
                    if (appInfo != null)
                    {
                        String s = appInfo.getCondition();
                        if (condition == null)
                            condition = s;
                        else if (s != null)
                            condition = "(" + condition + ") and (" + s + ")";
                    }
                }

                return condition;
            }
        }

        return null;
    }

    public static String getCondition(String condition)
    {
        RequestContext context = RequestContext.getContext();

        if (context == null)
            return condition;

        HttpServletRequest request = context.getRequest();

        String menuId = MenuItem.getMenuId(request);

        if (menuId != null)
        {
            MenuItem menu = MenuItem.getMenu(menuId);

            if (menu != null)
            {
                String menuUrl = menu.getUrl();
                int index = menuUrl.indexOf('?');
                if (index > 0)
                    menuUrl = menuUrl.substring(0, index);
                index = menuUrl.lastIndexOf('.');
                if (index > 0)
                    menuUrl = menuUrl.substring(0, index);

                String uri = RequestContext.getContext().getOriginalRequestURI();
                if (uri != null)
                {
                    index = uri.lastIndexOf('.');
                    if (index > 0)
                        uri = uri.substring(0, index);

                    if (uri.equals(menuUrl))
                    {
                        String s = menu.getCondition();
                        if (condition == null)
                            condition = s;
                        else if (s != null)
                            condition += " and (" + s + ")";

                        if ("loadCount".equals(RequestContext.getContext().getMethod()))
                        {
                            s = menu.getCountCondition();
                            if (condition == null)
                                condition = s;
                            else if (s != null)
                                condition += " and (" + s + ")";
                        }

                        UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);
                        if (userOnlineInfo != null)
                        {
                            AppInfo appInfo = userOnlineInfo.getApp(menuId);
                            if (appInfo != null)
                            {
                                s = appInfo.getCondition();
                                if (condition == null)
                                    condition = s;
                                else if (s != null)
                                    condition += " and (" + s + ")";
                            }
                        }
                    }
                }
            }
        }

        return condition;
    }

    public static ExportParameters getExportParameters(ListCrud crud) throws Exception
    {
        return getExportParameters(crud, crud.getExportFormat());
    }

    public static ExportParameters getExportParameters(Object page, String format) throws Exception
    {
        RequestContext context = RequestContext.getContext();

        if (context != null)
        {
            MenuItem menu = MenuItem.getMenu(context.getRequest());

            if (menu != null)
            {
                TemplateService service = templateServiceProvider.get();

                String action = CrudConfig.getContext().getAction();

                Template template = service.getTemplate(menu.getMenuId(),
                        "exp".equals(action) ? TemplateType.list : TemplateType.entity, format, page);

                if (template != null)
                {
                    String fileName = template.getFileName();

                    if (StringUtils.isEmpty(fileName))
                        fileName = template.getTemplateName();
                    else
                        fileName = Tools.getMessage(fileName, page);

                    ExportParameters parameters = new ExportParameters(fileName);

                    String templatePath = service.getTemplatePath(template);
                    WordDataExporter.setTemplate(parameters, templatePath);
                    XlsDataExporter.setTemplate(parameters, templatePath);

                    return parameters;
                }
                else
                {
                    return new ExportParameters(menu.getTitle());
                }
            }
        }

        return null;
    }
}