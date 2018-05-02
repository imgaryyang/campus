package com.gzzm.portal.cms.information;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * @author camel
 * @date 2014/11/14
 */
public class InformationBase0<C extends InformationContentBase>
{
    /**
     * 信息id
     */
    @Generatable(name = "PLINFORMATIONID", length = 11)
    private Long informationId;

    /**
     * 标题图片
     */
    @NotSerialized
    private byte[] photo;

    /**
     * 标题文件路径，如果附件是以文件格式保存在硬盘中，则指向硬盘中的路径，filePath和content只能有一个为空
     */
    @ColumnDescription(type = "varchar(250)")
    @NotSerialized
    private String photoFilePath;

    /**
     * 标题图片保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    @NotSerialized
    private String photoTarget;

    /**
     * 图片扩展名
     */
    @ColumnDescription(type = "varchar(25)")
    @NotSerialized
    private String extName;

    @OneToMany
    @OrderBy(column = "PAGENO")
    private List<C> contents;

    public InformationBase0()
    {
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto(byte[] photo)
    {
        this.photo = photo;
    }

    public String getPhotoFilePath()
    {
        return photoFilePath;
    }

    public void setPhotoFilePath(String photoFilePath)
    {
        this.photoFilePath = photoFilePath;
    }

    public String getPhotoTarget()
    {
        return photoTarget;
    }

    public void setPhotoTarget(String photoTarget)
    {
        this.photoTarget = photoTarget;
    }

    public String getExtName()
    {
        return extName;
    }

    public void setExtName(String extName)
    {
        this.extName = extName;
    }

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public List<C> getContents()
    {
        return contents;
    }

    public void setContents(List<C> contents)
    {
        this.contents = contents;
    }

    @Override
    public int hashCode()
    {
        return informationId.hashCode();
    }
}
