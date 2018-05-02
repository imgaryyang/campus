package com.gzzm.portal.inquiry;

import com.gzzm.platform.timeout.*;

import java.util.*;

/**
 * @author camel
 * @date 14-4-1
 */
public class InquiryTimeout implements TimeoutTypeProvider
{
    private static final String INQUIEYID = "@inquiry";

    public static final String ACCEPTID = "inquiry.accept";

    public static final String PROCESSID = "inquiry.process";

    private static final TimeoutType INQUIRY;

    private static final TimeoutType ACCEPT;

    private static final TimeoutType PROCESS;

    private static final List<TimeoutType> TYPES;

    static
    {
        INQUIRY = new TimeoutType(INQUIEYID);

        INQUIRY.addChild(ACCEPT = new TimeoutType(ACCEPTID));
        INQUIRY.addChild(PROCESS = new TimeoutType(PROCESSID));

        TYPES = Collections.singletonList(INQUIRY);
    }

    public InquiryTimeout()
    {
    }

    public List<TimeoutType> getTypes(String parentTypeId) throws Exception
    {
        return TYPES;
    }

    public TimeoutType getType(String typeId) throws Exception
    {
        if (INQUIEYID.equals(typeId))
            return INQUIRY;

        if (ACCEPTID.equals(typeId))
            return ACCEPT;

        if (PROCESSID.equals(typeId))
            return PROCESS;

        return null;
    }
}
