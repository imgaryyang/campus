package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2017/11/29
 */
@Entity(table = "PLINVALIDLINKINFORMATION", keys = {"informationId", "pageNo"})
public class InvalidLinkInformation
{
    @ColumnDescription(type = "number(11)")
    private Long informationId;

    @ColumnDescription(type = "number(3)")
    private Integer pageNo;

    /**
     * 内容
     */
    private char[] content;

    public InvalidLinkInformation()
    {
    }

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public Integer getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(Integer pageNo)
    {
        this.pageNo = pageNo;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InvalidLinkInformation))
            return false;

        InvalidLinkInformation that = (InvalidLinkInformation) o;

        return informationId.equals(that.informationId) && pageNo.equals(that.pageNo);
    }

    @Override
    public int hashCode()
    {
        int result = informationId.hashCode();
        result = 31 * result + pageNo.hashCode();
        return result;
    }
}
