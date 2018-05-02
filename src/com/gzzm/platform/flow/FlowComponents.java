package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * @author camel
 * @date 11-12-6
 */
public class FlowComponents
{
    private static List<Class<? extends FlowComponent>> components = new ArrayList<Class<? extends FlowComponent>>();

    public FlowComponents()
    {
    }

    public static synchronized void addComponent(Class<? extends FlowComponent> component)
    {
        if (!components.contains(component))
            components.add(component);
    }

    public static synchronized List<KeyValue<String>> getComponents(Class<? extends FlowComponent> type)
    {
        List<KeyValue<String>> result = new ArrayList<KeyValue<String>>(components.size());
        for (Class<? extends FlowComponent> component : components)
        {
            if (type == null || type.isAssignableFrom(component))
            {
                String className = component.getName();
                String name = Tools.getMessage(className);

                if (!StringUtils.isEmpty(name) && !className.equals(name))
                    result.add(new KeyValue<String>(className, name));
            }
        }

        return result;
    }
}
