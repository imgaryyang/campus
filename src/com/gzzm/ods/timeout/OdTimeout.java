package com.gzzm.ods.timeout;

import com.gzzm.platform.timeout.*;

import java.util.*;

/**
 * @author camel
 * @date 14-4-1
 */
public class OdTimeout implements TimeoutTypeProvider
{
    public static final String OD_ID = "@od";

    public static final String FLOW_ID = "od.flow";

    public static final String RECEIVE_ID = "od.receive";

    private static final TimeoutType OD;

    private static final TimeoutType FLOW;

    private static final TimeoutType RECEIVE;

    private static final List<TimeoutType> TYPES;

    static
    {
        OD = new TimeoutType(OD_ID);

        OD.addChild(FLOW = new TimeoutType(FLOW_ID));
        OD.addChild(RECEIVE = new TimeoutType(RECEIVE_ID));

        TYPES = Collections.singletonList(OD);
    }

    public OdTimeout()
    {
    }

    public List<TimeoutType> getTypes(String parentTypeId) throws Exception
    {
        return TYPES;
    }

    public TimeoutType getType(String typeId) throws Exception
    {
        if (FLOW_ID.equals(typeId))
            return FLOW;

        if (RECEIVE_ID.equals(typeId))
            return RECEIVE;

        return null;
    }
}
