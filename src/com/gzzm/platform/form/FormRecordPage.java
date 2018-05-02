package com.gzzm.platform.form;

import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.FormRole;

/**
 * @author camel
 * @date 11-9-21
 */
@Service
public class FormRecordPage extends FormBodyPage
{
    @Inject
    private FormDao dao;

    private Long recordId;

    @NotSerialized
    private FormRecord record;

    public FormRecordPage()
    {
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public FormRecord getRecord() throws Exception
    {
        if (record == null)
            record = dao.getFormRecord(recordId);
        return record;
    }

    public String getTitle() throws Exception
    {
        return getRecord().getTitle();
    }

    @Service(url = "/formbody/{recordId}")
    @Forward(page = "/platform/form/body.ptl")
    public String show() throws Exception
    {
        return null;
    }

    protected Long getBodyId(String name) throws Exception
    {
        if (name == null)
            return getRecord().getBodyId();

        return null;
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        return null;
    }

    @Override
    public Integer getBusinessDeptId() throws Exception
    {
        return getRecord().getDeptId();
    }
}
