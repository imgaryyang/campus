package com.gzzm.platform.timeout;

import java.util.List;

/**
 * @author camel
 * @date 14-4-1
 */
public interface TimeoutTypeProvider
{
    public List<TimeoutType> getTypes(String parentTypeId) throws Exception;

    public TimeoutType getType(String typeId) throws Exception;
}
