package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;

import java.util.Date;

/**
 * 用于将会签记录传到前台页面展示的数据结构
 *
 * @author camel
 * @date 12-10-23
 */
public class CollectInfo
{
    private Collect collect;

    /**
     * 此会签记录显示在哪个表单，即发起部门的表单
     */
    private String formName;

    public CollectInfo(Collect collect, String formName)
    {
        this.collect = collect;
        this.formName = formName;
    }

    protected ReceiveBase getReceiveBase()
    {
        return collect.getReceiveBase();
    }

    public Long getReceiveId()
    {
        return collect.getReceiveId();
    }

    public Long getCollectInstanceId()
    {
        return collect.getCollectInstanceId();
    }

    public boolean isHidden()
    {
        return collect.isHidden() != null && collect.isHidden();
    }

    public Date getEndTime()
    {
        return collect.getEndTime();
    }

    public boolean isPublished()
    {
        return collect.isPublished() != null && collect.isPublished();
    }

    public Long getDocumentId()
    {
        return collect.getDocumentId();
    }

    public String getEncodedDocumentId()
    {
        return OfficeDocument.encodeId(getDocumentId());
    }

    public Integer getDeptId()
    {
        return getReceiveBase().getDeptId();
    }

    public Date getSendTime()
    {
        return getReceiveBase().getSendTime();
    }

    public Date getAcceptTime()
    {
        return getReceiveBase().getAcceptTime();
    }

    public ReceiveState getState()
    {
        return getReceiveBase().getState();
    }

    public String getDeptName()
    {
        return getReceiveBase().getDept().getDeptName();
    }

    public boolean isUrged()
    {
        return collect.isUrged() != null && collect.isUrged();
    }

    public String getFormName()
    {
        return formName;
    }

    public String getTextFormat()
    {
        OfficeDocument document = collect.getDocument();
        if (document != null)
        {
            DocumentText text = document.getText();
            if (text != null)
                return text.getType();
        }
        return null;
    }
}
