package com.gzzm.platform.barcode;

import net.cyan.thunwind.annotation.*;

/**
 * 保存外部二维码，条形码对应到的系统资源，
 * 当扫描某个二维码或条形码时能快速定位到本系统的资源
 *
 * @author camel
 * @date 2015/8/9
 */
@Entity(table = "PFBARCODE", keys = "barCodeId")
public class BarCode
{
    @Generatable(length = 11)
    private Long barCodeId;

    /**
     * 二维码条形码的内容
     */
    @Index
    @ColumnDescription(type = "varchar(1000)")
    private String content;

    @Index
    @ColumnDescription(type = "varchar(1000)")
    private String linkContent;

    public BarCode()
    {
    }

    public Long getBarCodeId()
    {
        return barCodeId;
    }

    public void setBarCodeId(Long barCodeId)
    {
        this.barCodeId = barCodeId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getLinkContent()
    {
        return linkContent;
    }

    public void setLinkContent(String linkContent)
    {
        this.linkContent = linkContent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof BarCode))
            return false;

        BarCode barCode = (BarCode) o;

        return barCodeId.equals(barCode.barCodeId);
    }

    @Override
    public int hashCode()
    {
        return barCodeId.hashCode();
    }
}
