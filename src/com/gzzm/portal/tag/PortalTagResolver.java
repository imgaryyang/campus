package com.gzzm.portal.tag;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.annotation.Tag;
import net.cyan.commons.util.ClassResolver;

/**
 * 搜索所有的门户标签类
 *
 * @author camel
 * @date 2011-6-13
 */
public class PortalTagResolver implements ClassResolver
{
    public PortalTagResolver()
    {
    }

    @SuppressWarnings("unchecked")
    public void resolve(Class<?> c) throws Exception
    {
        if (PortalTag.class.isAssignableFrom(c))
        {
            Tag tag = c.getAnnotation(Tag.class);

            if (tag != null)
            {
                String name = tag.name();

                if (tag.singleton())
                    PortalTagContainer.add(name, (PortalTag) Tools.getBean(c));
                else
                    PortalTagContainer.add(name, (Class<? extends PortalTag>) c);
            }
        }
    }
}
