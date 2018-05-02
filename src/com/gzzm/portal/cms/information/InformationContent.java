package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.storage.CommonFileColumn;

/**
 * 信息内容，发布后使用
 *
 * @author camel
 * @date 2011-5-19
 */
@Entity(table = "PLINFORMATIONCONTENT", keys = {"informationId", "pageNo"})
public class InformationContent extends InformationContentBase<Information>
{
    public InformationContent()
    {
    }

    @Override
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "portal",
            path = "{yyyyMM}/{yyyyMMdd}/portal/information/{informationId}_publish_{pageNo}", clear = true)
    public char[] getContent()
    {
        return super.getContent();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InformationContent))
            return false;

        InformationContent that = (InformationContent) o;

        return getInformationId().equals(that.getInformationId()) && getPageNo().equals(that.getPageNo());
    }
}
