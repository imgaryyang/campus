package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.storage.CommonFileColumn;

/**
 * 信息内容，在采编的时候使用
 *
 * @author camel
 * @date 2011-5-19
 */
@Entity(table = "PLINFORMATIONCONTENTEDIT", keys = {"informationId", "pageNo"})
public class InformationContentEdit extends InformationContentBase<InformationEdit>
{
    public InformationContentEdit()
    {
    }

    @Override
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "portal",
            path = "{yyyyMM}/{yyyyMMdd}/portal/information/{informationId}_edit_{pageNo}", clear = true)
    public char[] getContent()
    {
        return super.getContent();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InformationContentEdit))
            return false;

        InformationContentEdit that = (InformationContentEdit) o;

        return getInformationId().equals(that.getInformationId()) && getPageNo().equals(that.getPageNo());
    }
}
