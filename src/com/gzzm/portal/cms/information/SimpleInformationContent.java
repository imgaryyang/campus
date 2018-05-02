package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.Entity;

/**
 * @author camel
 * @date 2014/11/14
 */
@Entity(table = "PLSIMPLEINFORMATIONCONTENT", keys = {"informationId", "pageNo"})
public class SimpleInformationContent extends InformationContentBase<SimpleInformation>
{
    public SimpleInformationContent()
    {
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SimpleInformationContent))
            return false;

        SimpleInformationContent that = (SimpleInformationContent) o;

        return getInformationId().equals(that.getInformationId()) && getPageNo().equals(that.getPageNo());
    }
}
