package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;

/**
 * @author camel
 * @date 11-12-6
 */
public class FlowComponentResolver implements ClassResolver
{
    public FlowComponentResolver()
    {
    }

    @SuppressWarnings("unchecked")
    public void resolve(Class<?> c) throws Exception
    {
        try
        {
            if (FlowComponent.class.isAssignableFrom(c) && BeanUtils.isRealClass(c) &&
                    !FlowExtension.class.isAssignableFrom(c))
            {
                FlowComponents.addComponent((Class<? extends FlowComponent>) c);
            }
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
