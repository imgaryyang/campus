package com.gzzm.portal.cms.information;

import net.cyan.arachne.annotation.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.*;

import java.util.*;

/**
 * 信息采编
 *
 * @author camel
 * @date 2011-5-11
 */
@Entity(table = "PLINFORMATION", keys = "informationId")
public class Information extends InformationBase<InformationContent, InformationFile>
{
    public Information()
    {
    }

    @ValueMap(table = "PLINFOPROPERTYVALUE", keyColumn = "PROPERTYNAME", valueColumn = "PROPERTYVALUE",
            clearForUpdate = false)
    public Map<String, String> getProperties()
    {
        return super.getProperties();
    }

    @Override
    @CommonFileColumn(pathColumn = "photoFilePath", target = "{photoTarget}", defaultTarget = "portal",
            path = "{yyyyMM}/{yyyyMMdd}/portal/information/{informationId}_publish_photo", clear = true)
    public byte[] getPhoto()
    {
        return super.getPhoto();
    }



    @ManyToMany(table = "PLINFORMATIONRELATED",joinColumn = "INFORMATIONID",reverseJoinColumn = "OTHERINFORMATIONID")
    @NotSerialized
    private List<Information> relatedInfos;

    public List<Information> getRelatedInfos()
    {
        return relatedInfos;
    }

    public void setRelatedInfos(List<Information> relatedInfos)
    {
        this.relatedInfos = relatedInfos;
    }

    /**
     * 用来做全文索引的文本
     *
     * @return 用于全文索引的文本
     * @throws Exception 数据库错误或者生成文本错误
     */
    @FullText
    @NotSerialized
    public String getText() throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        //标题，主题词，关键字，摘要
        buffer.append(getTitle());

        if (getSubject() != null)
            buffer.append(" ").append(getSubject());

        if (getSummary() != null)
            buffer.append(" ").append(getSummary());

        if (getKeywords() != null)
            buffer.append(" ").append(getKeywords());

        Map<String, String> properties = getProperties();
        if (properties != null)
        {
            for (String s : properties.values())
                buffer.append(" ").append(s);
        }

        getContentText(buffer);

        return buffer.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Information))
            return false;

        Information that = (Information) o;

        return getInformationId().equals(that.getInformationId());
    }
}
