package com.gzzm.platform.commons.inject;

import com.gzzm.platform.annotation.BureauId;
import net.cyan.nest.*;

/**
 * 注入bureauId
 * @author camel
 * @date 2009-7-29
 */
public class BureauIdInjector implements InjectAnnotationParser<BureauId>
{
    public BureauIdInjector()
    {
    }

    public ValueFactory parse(BureauId annotation)
    {
        return DeptIdFactory.getFactory(1);
    }
}
