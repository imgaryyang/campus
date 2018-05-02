package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 11-10-17
 */
public class Scopes
{
    @Inject
    protected static Provider<DeptService> deptServiceProvider;

    /**
     * 此功能的使用范围，即作用的部门，key为部门id，值DeptFilter，当不包括下级部门时为null
     * 当包括下级部门时表示对下级部门的过滤条件，如果没有过滤条件则为DeptFilter.All
     */
    protected Map<Integer, Filter<DeptInfo>> depts;

    protected boolean includeSub;

    private boolean excluded;

    protected boolean all;

    public Scopes()
    {
    }

    public Scopes(Integer scopeId, Integer deptId) throws Exception
    {
        this(scopeId, deptId, deptServiceProvider.get());
    }

    public Scopes(Integer scopeId, Integer deptId, Integer businessDeptId) throws Exception
    {
        this(scopeId, deptId, businessDeptId, deptServiceProvider.get());
    }

    public Scopes(Integer scopeId, Integer deptId, Integer businessDeptId, DeptService deptService) throws Exception
    {
        add(deptService.getDao().getScope(scopeId).getRoleScopeDepts(), deptId, businessDeptId, deptService);
    }

    public Scopes(Integer scopeId, Integer deptId, DeptService deptService) throws Exception
    {
        add(deptService.getDao().getScope(scopeId).getRoleScopeDepts(), deptId, deptService);
    }

    protected void setAll()
    {
        all = true;
    }

    protected void add(Collection<RoleScopeDept> roleScopeDepts, Integer userDeptId, DeptService service)
            throws Exception
    {
        if (!all || excluded)
            add(roleScopeDepts, userDeptId, null, service);
    }

    protected void add(Collection<RoleScopeDept> roleScopeDepts, Integer userDeptId, Integer businessDeptId,
                       DeptService service) throws Exception
    {
        if (all && !excluded)
            return;

        if (service == null)
            service = deptServiceProvider.get();

        for (RoleScopeDept roleScopeDept : roleScopeDepts)
        {
            Integer deptId = roleScopeDept.getDeptId();
            boolean includeSub = roleScopeDept.isIncludeSub() != null && roleScopeDept.isIncludeSub();
            boolean includeSup = roleScopeDept.isIncludeSup() != null && roleScopeDept.isIncludeSup();
            boolean includeSelf = roleScopeDept.isIncludeSelf() == null || roleScopeDept.isIncludeSelf();
            boolean excluded = roleScopeDept.isExcluded() != null && roleScopeDept.isExcluded();
            String expression = roleScopeDept.getFilter();

            if (excluded)
            {
                if (deptId > 0)
                {
                    putDept(deptId, includeSub, excluded,
                            roleScopeDept.isPriority() != null && roleScopeDept.isPriority(), null);
                }
            }
            else
            {
                if (expression != null && expression.trim().length() == 0)
                    expression = null;

                if (deptId == 0)
                {
                    deptId = userDeptId;
                }
                else if (deptId == -100)
                {
                    deptId = businessDeptId;
                }
                else if (deptId < 0)
                {
                    DeptInfo dept = service.getDept(userDeptId);

                    if (dept == null)
                        throw new NullPointerException(Integer.toString(userDeptId));

                    deptId = dept.getParentDept(-deptId - 1).getDeptId();
                }

                if (deptId == null)
                    continue;

                DeptInfo dept = null;

                if (includeSelf)
                {
                    putDept(deptId, includeSub, false, false, expression);
                }
                else if (includeSub)
                {
                    dept = service.getDept(deptId);

                    for (DeptInfo subDept : dept.subDepts())
                    {
                        putDept(subDept.getDeptId(), includeSub, false, false, expression);
                    }
                }

                if (includeSup)
                {
                    if (dept == null)
                        dept = service.getDept(deptId);

                    for (Integer supDeptId : dept.allParentDeptIds())
                    {
                        if (!supDeptId.equals(deptId))
                            putDept(supDeptId, false, false, false, expression);
                    }
                }
            }
        }
    }

    protected void putDept(Integer deptId, boolean includeSub, boolean excluded, boolean priority, String expression)
    {
        if (excluded)
        {
            if (depts == null)
                depts = new HashMap<Integer, Filter<DeptInfo>>();

            Filter<DeptInfo> filter = depts.get(deptId);

            if (priority)
            {
                this.excluded = true;
                if (includeSub || filter == AllDeptFilter.EXCLUDEDALL)
                {
                    depts.put(deptId, AllDeptFilter.PRIORITY_EXCLUDEDALL);
                }
                else if (filter != AllDeptFilter.PRIORITY_EXCLUDEDALL)
                {
                    depts.put(deptId, AllDeptFilter.PRIORITY_EXCLUDED);
                }
            }
            else if (!depts.containsKey(deptId) || filter == AllDeptFilter.EXCLUDED)
            {
                this.excluded = true;
                depts.put(deptId, includeSub ? AllDeptFilter.EXCLUDEDALL : AllDeptFilter.EXCLUDED);
            }

            return;
        }

        if (all)
            return;

        if (deptId == 1 && includeSub && StringUtils.isEmpty(expression))
            setAll();

        if (depts == null)
            depts = new HashMap<Integer, Filter<DeptInfo>>();

        if (includeSub)
            this.includeSub = true;

        if (depts.containsKey(deptId))
        {
            //之前已经加入此部门
            Filter<DeptInfo> filter = depts.get(deptId);
            if (filter == AllDeptFilter.PRIORITY_EXCLUDEDALL || filter == AllDeptFilter.PRIORITY_EXCLUDED)
                return;

            if (includeSub)
            {
                if (expression == null)
                {
                    //没有条件，不需要过滤
                    depts.put(deptId, AllDeptFilter.All);
                }
                else
                {
                    if (filter == null)
                    {
                        //原来null表示只接受本部门，不接受下级部门，创建组合条件，要求能接收本部门本身和条件满足的部门
                        UnionDeptFilter filter2 = new UnionDeptFilter();
                        filter2.addFilter(SingleDeptFilter.getFilter(deptId));
                        filter2.addFilter(ExpressionDeptFilter.getFilter(expression));
                        depts.put(deptId, filter2);
                    }
                    else if (filter instanceof ExpressionDeptFilter)
                    {
                        if (!((ExpressionDeptFilter) filter).getExpression().equals(expression))
                        {
                            //创建组合条件，组合两个条件
                            UnionDeptFilter filter2 = new UnionDeptFilter();
                            filter2.addFilter(filter);
                            filter2.addFilter(ExpressionDeptFilter.getFilter(expression));
                            depts.put(deptId, filter2);
                        }
                    }
                    else if (filter instanceof UnionDeptFilter)
                    {
                        UnionDeptFilter unionFilter = (UnionDeptFilter) filter;
                        for (Filter<DeptInfo> filter2 : unionFilter.getFilters())
                        {
                            //原来已经包含此条件，退出
                            if ((filter2 instanceof ExpressionDeptFilter) &&
                                    ((ExpressionDeptFilter) filter2).getExpression().equals(expression))
                                return;
                        }

                        //将此条件加入
                        unionFilter.addFilter(ExpressionDeptFilter.getFilter(expression));
                    }
                    else if (filter == AllDeptFilter.EXCLUDED || filter == AllDeptFilter.EXCLUDEDALL)
                    {
                        depts.put(deptId, ExpressionDeptFilter.getFilter(expression));
                    }
                }
            }
            else
            {
                if (filter instanceof ExpressionDeptFilter)
                {
                    //组合原来的条件，要求能接收本部门本身
                    UnionDeptFilter filter2 = new UnionDeptFilter();
                    filter2.addFilter(SingleDeptFilter.getFilter(deptId));
                    filter2.addFilter(filter);
                    depts.put(deptId, filter2);
                }
                else if (filter instanceof UnionDeptFilter)
                {
                    UnionDeptFilter unionFilter = (UnionDeptFilter) filter;
                    for (Filter<DeptInfo> filter2 : unionFilter.getFilters())
                    {
                        //原来已经能够接收本部门本身了，退出
                        if (filter2 instanceof SingleDeptFilter)
                            return;
                    }

                    //组合原来的条件，要求能接收本部门本身
                    unionFilter.addFilter(SingleDeptFilter.getFilter(deptId));
                }
                else if (filter == AllDeptFilter.EXCLUDED || filter == AllDeptFilter.EXCLUDEDALL)
                {
                    depts.put(deptId, null);
                }
            }
        }
        else
        {
            if (includeSub)
                depts.put(deptId, expression == null ? AllDeptFilter.All : ExpressionDeptFilter.getFilter(expression));
            else
                depts.put(deptId, null);
        }
    }

    /**
     * 获得此功能作用的范围
     *
     * @param level  只加载此级别的部门
     * @param filter 部门过滤器，过滤出满足条件的
     * @return 代表作用范围的部门id列表
     */
    @SuppressWarnings("unchecked")
    public Collection<Integer> getDeptIds(int level, Filter<DeptInfo> filter)
    {
        if (all && !excluded && filter == null)
            return null;

        DeptService service = deptServiceProvider.get();

        if (depts == null)
            return Collections.EMPTY_SET;

        if (filter != null || includeSub)
        {
            //需要加载子部门或者过滤条件
            try
            {
                return service.loadDeptIds(depts, level, filter);
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }

        //不需要加载子部门
        if (excluded)
        {
            Collection<Integer> result = new ArrayList<Integer>(depts.keySet());

            for (Map.Entry<Integer, Filter<DeptInfo>> entry : depts.entrySet())
            {
                Integer deptId = entry.getKey();
                Filter<DeptInfo> filter1 = entry.getValue();


                if (filter1 == AllDeptFilter.EXCLUDED || filter1 == AllDeptFilter.PRIORITY_EXCLUDED ||
                        filter1 == AllDeptFilter.EXCLUDEDALL)
                {
                    result.remove(deptId);
                }
                else if (filter1 == AllDeptFilter.PRIORITY_EXCLUDEDALL)
                {
                    try
                    {
                        result.removeAll(service.getDept(deptId).allSubDeptIds());
                    }
                    catch (Exception ex)
                    {
                        Tools.wrapException(ex);
                    }
                }
            }

            return result;
        }
        else
        {
            return Collections.unmodifiableSet(depts.keySet());
        }
    }

    public Collection<Integer> getDeptIds()
    {
        return getDeptIds(-1, null);
    }

    public Collection<Integer> getDeptIds(int level)
    {
        return getDeptIds(level, null);
    }

    public Collection<Integer> getDeptIds(Filter<DeptInfo> filter)
    {
        return getDeptIds(-1, filter);
    }
}
