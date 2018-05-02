package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.io.InputStream;

/**
 * 上传文件方式采编的信息，保存上传的文件
 *
 * @author camel
 * @date 2011-5-21
 */
@Entity(table = "PLINFORMATIONFILE", keys = "informationId")
public class InformationFile extends InformationFileBase<Information>
{
    public InformationFile()
    {
    }

    @Override
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "portal",
            path = "{yyyyMM}/{yyyyMMdd}/portal/information/{informationId}_publish_file", clear = true)
    public InputStream getContent()
    {
        return super.getContent();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InformationFile))
            return false;

        InformationFile that = (InformationFile) o;

        return getInformationId().equals(that.getInformationId());
    }
}
