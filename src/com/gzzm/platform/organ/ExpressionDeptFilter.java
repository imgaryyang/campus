package com.gzzm.platform.organ;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用一个表达式过滤部门，当表达式为true时接收部门，否则不接收部门
 *
 * @author camel
 * @date 2009-7-28
 */
public class ExpressionDeptFilter implements Filter<DeptInfo>
{
    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    private static final Map<String, ExpressionDeptFilter> FILTER_MAP = new HashMap<String, ExpressionDeptFilter>();

    private String expression;

    private ExpressionDeptFilter(String expression)
    {
        this.expression = expression;
    }

    public synchronized static ExpressionDeptFilter getFilter(String expression)
    {
        ExpressionDeptFilter filter = FILTER_MAP.get(expression);
        if (filter == null)
            FILTER_MAP.put(expression, filter = new ExpressionDeptFilter(expression));
        return filter;
    }

    public boolean accept(DeptInfo dept) throws Exception
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        Map<String, Object> context = null;

        if (userOnlineInfo != null)
        {
            context = new HashMap<String, Object>();
            context.put("user", userOnlineInfo);
        }

        Object result = BeanUtils.eval(expression, dept, context);

        return result != null && (!(result instanceof Boolean) || (Boolean) result);
    }

    public String getExpression()
    {
        return expression;
    }

    public boolean equals(Object o)
    {
        return this == o ||
                o instanceof ExpressionDeptFilter && expression.equals(((ExpressionDeptFilter) o).expression);
    }

    public int hashCode()
    {
        return expression.hashCode();
    }
}
