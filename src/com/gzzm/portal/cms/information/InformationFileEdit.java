package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.io.InputStream;

/**
 * InformationFile表对应的edit状态
 *
 * @author camel
 * @date 2011-5-21
 */
@Entity(table = "PLINFORMATIONFILEEDIT", keys = "informationId")
public class InformationFileEdit extends InformationFileBase<InformationEdit>
{
    public InformationFileEdit()
    {
    }

    @Override
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "portal",
            path = "{yyyyMM}/{yyyyMMdd}/portal/information/{informationId}_edit_file", clear = true)
    public InputStream getContent()
    {
        return super.getContent();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InformationFileEdit))
            return false;

        InformationFileEdit that = (InformationFileEdit) o;

        return getInformationId().equals(that.getInformationId());
    }
}
