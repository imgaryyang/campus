package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.Entity;

/**
 * @author camel
 * @date 2014/11/14
 */
@Entity(table = "PLSIMPLEINFORMATION", keys = "informationId")
public class SimpleInformation extends InformationBase0<SimpleInformationContent>
{
    public SimpleInformation()
    {
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SimpleInformation))
            return false;

        SimpleInformation that = (SimpleInformation) o;

        return getInformationId().equals(that.getInformationId());
    }

    @Override
    public int hashCode()
    {
        Long informationId = getInformationId();
        return informationId == null ? 0 : informationId.hashCode();
    }
}
