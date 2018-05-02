package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * @author camel
 * @date 13-11-3
 */
public class InformationComponents
{
    private static List<Class<? extends InformationComponent>> components =
            new ArrayList<Class<? extends InformationComponent>>();

    public InformationComponents()
    {
    }

    public static synchronized void addComponent(Class<? extends InformationComponent> component)
    {
        if (!components.contains(component))
            components.add(component);
    }

    public static synchronized List<KeyValue<String>> getComponents()
    {
        List<KeyValue<String>> result = new ArrayList<KeyValue<String>>(components.size());
        for (Class<? extends InformationComponent> component : components)
        {
            String className = component.getName();
            String name = Tools.getMessage(className);

            if (!StringUtils.isEmpty(name) && !className.equals(name))
                result.add(new KeyValue<String>(className, name));
        }

        return result;
    }
}
