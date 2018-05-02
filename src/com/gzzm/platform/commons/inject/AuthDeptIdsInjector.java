package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.*;

/**
 * 注入AuthDeptIds
 *
 * @author camel
 * @date 2009-11-5
 */
public class AuthDeptIdsInjector implements InjectAnnotationParser<AuthDeptIds>
{
    public AuthDeptIdsInjector()
    {
    }

    public ValueFactory parse(AuthDeptIds annotation)
    {
        Filter<DeptInfo> filter = null;
        if (annotation.filter() != AllDeptFilter.class)
        {
            try
            {
                filter = annotation.filter().newInstance();
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }
        else if (!StringUtils.isEmpty(annotation.condition()))
        {
            filter = ExpressionDeptFilter.getFilter(annotation.condition());
        }

        return new AuthDeptIdsFactory(annotation.level(), filter, annotation.app());
    }
}
