package com.gzzm.portal.cms.information;

import net.cyan.commons.util.*;

/**
 * @author camel
 * @date 11-12-6
 */
public class InformationComponentResolver implements ClassResolver
{
    public InformationComponentResolver()
    {
    }

    @SuppressWarnings("unchecked")
    public void resolve(Class<?> c) throws Exception
    {
        if (InformationComponent.class.isAssignableFrom(c) && BeanUtils.isRealClass(c))
        {
            InformationComponents.addComponent((Class<? extends InformationComponent>) c);
        }
    }
}
