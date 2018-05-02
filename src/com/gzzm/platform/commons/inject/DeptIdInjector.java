package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.DeptId;
import net.cyan.nest.*;

/**
 * 注入部门id
 * @author camel
 * @date 2009-7-29
 */
public class DeptIdInjector implements InjectAnnotationParser<DeptId>
{
    public DeptIdInjector()
    {
    }

    public ValueFactory parse(DeptId annotation)
    {
        return DeptIdFactory.getFactory(annotation.level());
    }
}
