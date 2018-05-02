package com.gzzm.portal.cms.information;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.*;

import java.util.*;

/**
 * 映射信息编辑表，信息编辑表存放未发布的信息，当发布时将信息复制到Information
 * 当信息发布后，对信息做修改时，只修改此表，不修改Information表，当重新发布时才将数据复制过去
 *
 * @author camel
 * @date 2011-5-11
 */
@Entity(table = "PLINFORMATIONEDIT", keys = "informationId")
public class InformationEdit extends InformationBase<InformationContentEdit, InformationFileEdit>
{
    /**
     * 信息的状态，已发布或者未发布
     */
    private InformationState state;

    /**
     * 信息创建时间
     */
    @NotSerialized
    private Date createTime;

    /**
     * 是否已经发布
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean published;

    /**
     * 创建人
     */
    private Integer creator;

    /**
     * 关联创建人对象
     */
    @NotSerialized
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 信息发布者
     */
    private Integer publisher;

    /**
     * 关联信息发布人的对象
     */
    @NotSerialized
    @ToOne("PUBLISHER")
    private User publishUser;


    /**
     * 扩展的数据，在采编和发布列表中显示使用
     *
     * @see InformationCrud#loadExtraData
     */
    @NotSerialized
    @Transient
    private Object extraData;

    @Index
    private Long mainInformationId;

    @ToOne("MAININFORMATIONID")
    private Information mainInformation;

    @Index
    private Long sourceInformationId;

    @ToOne("SOURCEINFORMATIONID")
    private Information sourceInformation;

    @ManyToMany(table = "PLINFORMATIONEDITRELATED",joinColumn = "INFORMATIONID",reverseJoinColumn = "OTHERINFORMATIONID")
    @NotSerialized
    private List<InformationEdit> relatedInfos;

    public InformationEdit()
    {
    }

    public List<InformationEdit> getRelatedInfos()
    {
        return relatedInfos;
    }

    public void setRelatedInfos(List<InformationEdit> relatedInfos)
    {
        this.relatedInfos = relatedInfos;
    }

    @Override
    @CommonFileColumn(pathColumn = "photoFilePath", target = "{photoTarget}", defaultTarget = "portal",
            path = "{yyyyMM}/{yyyyMMdd}/portal/information/{informationId}_edit_photo", clear = true)
    public byte[] getPhoto()
    {
        return super.getPhoto();
    }

    @ValueMap(table = "PLINFOPROPERTYVALUEEDIT", keyColumn = "PROPERTYNAME", valueColumn = "PROPERTYVALUE",
            clearForUpdate = false)
    public Map<String, String> getProperties()
    {
        return super.getProperties();
    }

    public InformationState getState()
    {
        return state;
    }

    public void setState(InformationState state)
    {
        this.state = state;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Boolean isPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Integer getPublisher()
    {
        return publisher;
    }

    public void setPublisher(Integer publisher)
    {
        this.publisher = publisher;
    }

    public User getPublishUser()
    {
        return publishUser;
    }

    public void setPublishUser(User publishUser)
    {
        this.publishUser = publishUser;
    }

    public Object getExtraData()
    {
        return extraData;
    }

    public void setExtraData(Object extraData)
    {
        this.extraData = extraData;
    }

    public Long getMainInformationId()
    {
        return mainInformationId;
    }

    public void setMainInformationId(Long mainInformationId)
    {
        this.mainInformationId = mainInformationId;
    }

    public Information getMainInformation()
    {
        return mainInformation;
    }

    public void setMainInformation(Information mainInformation)
    {
        this.mainInformation = mainInformation;
    }

    public Long getSourceInformationId()
    {
        return sourceInformationId;
    }

    public void setSourceInformationId(Long sourceInformationId)
    {
        this.sourceInformationId = sourceInformationId;
    }

    public Information getSourceInformation()
    {
        return sourceInformation;
    }

    public void setSourceInformation(Information sourceInformation)
    {
        this.sourceInformation = sourceInformation;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InformationEdit))
            return false;

        InformationEdit that = (InformationEdit) o;

        return getInformationId().equals(that.getInformationId());
    }
}
