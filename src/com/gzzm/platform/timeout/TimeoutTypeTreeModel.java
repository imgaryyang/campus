package com.gzzm.platform.timeout;

import net.cyan.arachne.components.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 14-4-1
 */
public class TimeoutTypeTreeModel implements PageTreeModel<TimeoutType>, SelectableModel<TimeoutType>
{
    @Inject
    private static Provider<TimeoutService> serviceProvider;

    private TimeoutService service;

    private List<TimeoutType> topTypes;

    public TimeoutTypeTreeModel()
    {
    }

    private TimeoutService getService()
    {
        if (service == null)
            service = serviceProvider.get();

        return service;
    }

    private List<TimeoutType> getTopTypes() throws Exception
    {
        if (topTypes == null)
        {
            topTypes = new ArrayList<TimeoutType>();

            List<TimeoutTypeProvider> typeProviders = getService().getTypeProviders();
            if (typeProviders != null)
            {
                for (TimeoutTypeProvider provider : typeProviders)
                {
                    List<TimeoutType> types = provider.getTypes(null);

                    if (types != null)
                        topTypes.addAll(types);
                }
            }
        }

        return topTypes;
    }

    public TimeoutType getRoot() throws Exception
    {
        return TimeoutType.ROOT;
    }

    public boolean isLeaf(TimeoutType timeoutType) throws Exception
    {
        return timeoutType != TimeoutType.ROOT &&
                (timeoutType.getChildren() == null || timeoutType.getChildren().isEmpty());
    }

    public int getChildCount(TimeoutType parent) throws Exception
    {
        if (parent == TimeoutType.ROOT)
            return getTopTypes().size();

        if (parent.getChildren() != null)
            return parent.getChildren().size();

        return 0;
    }

    public TimeoutType getChild(TimeoutType parent, int index) throws Exception
    {
        if (parent == TimeoutType.ROOT)
            return getTopTypes().get(index);

        if (parent.getChildren() != null)
            return parent.getChildren().get(index);

        return null;
    }

    public String getId(TimeoutType timeoutType) throws Exception
    {
        return timeoutType.getTypeId();
    }

    public String toString(TimeoutType timeoutType) throws Exception
    {
        return timeoutType.toString();
    }

    public TimeoutType getNode(String id) throws Exception
    {
        return getService().getTimeoutType(id);
    }

    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    public boolean isSelectable(TimeoutType timeoutType) throws Exception
    {
        return !timeoutType.getTypeId().startsWith("@");
    }
}
