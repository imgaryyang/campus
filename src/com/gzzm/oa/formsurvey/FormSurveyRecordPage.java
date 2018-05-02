package com.gzzm.oa.formsurvey;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.form.*;
import com.gzzm.platform.template.TemplateInput;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.SysValue;
import net.cyan.valmiki.form.*;

import java.io.*;
import java.util.Date;

/**
 * 填写调研表单的界面
 *
 * @author camel
 * @date 13-4-23
 */
@Service
public class FormSurveyRecordPage extends FormBodyPage
{
    @Inject
    private FormSurveyDao dao;


    /**
     * 调研记录的ID
     */
    private Integer recordId;

    /**
     * 是否从审核的菜单进来
     */
    private boolean audit;


    /**
     * 调研，用于获得使用的表单，生成标题等
     */
    private FormSurvey survey;

    /**
     * 调研记录，如果是新增，为空
     */
    private FormSurveyRecord record;

    public FormSurveyRecordPage()
    {
    }

    public Integer getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Integer recordId)
    {
        this.recordId = recordId;
    }

    public boolean isAudit()
    {
        return audit;
    }

    public void setAudit(boolean audit)
    {
        this.audit = audit;
    }

    /**
     * 获得调研记录，因为有可能要在页面上使用，所以用public，但是不想被序列化，所以用 @NotSerialized
     *
     * @return 调研记录，如果是新增则返回空
     */
    @NotSerialized
    public FormSurveyRecord getRecord() throws Exception
    {
        if (record == null && recordId != null)
            record = dao.getRecord(recordId);

        return record;
    }

    /**
     * 获得调研记录，因为有可能要在页面上使用，所以用public，但是不想被序列化，所以用 @NotSerialized
     *
     * @return 调研记录，如果是新增则返回空
     */
    @NotSerialized
    public FormSurvey getSurvey() throws Exception
    {
        if (survey == null)
            survey = getRecord().getSurvey();

        return survey;
    }

    @Override
    protected Long getBodyId(String name) throws Exception
    {
        FormSurveyRecord record = getRecord();
        Long bodyId = record.getBodyId();

        if (bodyId == null)
        {
            FormBody body = new FormBody();
            body.setFormId(FormApi.getLastFormId(getSurvey().getFormId()));
            dao.add(body);

            bodyId = body.getBodyId();
            record.setBodyId(bodyId);
            dao.update(record);
        }

        return bodyId;
    }

    @Override
    protected FormRole getRole(SystemFormContext formContext) throws Exception
    {
        return formContext.getForm().createTopRole();
    }

    @Override
    protected boolean isReadOnly(SystemFormContext formContext) throws Exception
    {
        return record.getState() == RecordState.PASSED || record.getState() == RecordState.SUBMITED;
    }

    @Override
    public Integer getBusinessDeptId() throws Exception
    {
        return getRecord().getDeptId();
    }

    @Service(url = "/oa/formsurvey/body/{recordId}")
    @Forward(page = "/oa/formsurvey/body.ptl")
    public String show() throws Exception
    {
        return null;
    }

    @Override
    @Transactional
    @Service(url = "/oa/formsurvey/body/{recordId}", method = HttpMethod.post)
    public void save() throws Exception
    {
        super.save();

        StringUtils.StringTokens tokens = StringUtils.createStringTokens(getSurvey().getTitle(), "${", "}");
        String title = tokens.getString(getFormContext().getTextContext());

        FormSurveyRecord record = getRecord();
        record.setTitle(title);
        if (record.getState() == RecordState.NOSAVED)
            record.setState(RecordState.NOSUBMITED);
        record.setUpdateTime(new Date());

        dao.update(record);
    }

    @Service(url = "/oa/formsurvey/body/{recordId}/print/show")
    public String showPrint() throws Exception
    {
        return "/oa/formsurvey/print.ptl";
    }

    @Service(url = "/oa/formsurvey/body/{recordId}/print/down?toDoc={$0}")
    public InputFile downPrint(boolean toDoc) throws Exception
    {
        FormSurvey survey = getSurvey();

        String path = "/temp/oa/formsurvey/print/" + survey.getSurveyId();

        File file = new File(Tools.getAppPath(path + ".xml"));

        if (!file.exists() || file.lastModified() < survey.getLastModified().getTime())
        {
            InputStream content = survey.getPrintTemplate();
            if (content == null)
                return null;

            IOUtils.streamToFile(content, file);
        }

        FormContext formContext = getFormContext();

        return new InputFile(new TemplateInput(path, formContext.getTextContext(), toDoc),
                survey.getSurveyName() + (toDoc ? ".doc" : ".xml"));
    }

    @Service(method = HttpMethod.post)
    public void setState(RecordState state, String remark) throws Exception
    {
        FormSurveyRecord record = getRecord();
        record.setState(state);
        record.setRemark(remark);

        if (state == RecordState.PASSED)
            record.setAuditTime(new Date());
        else
            record.setAuditTime(SysValue.Null.Timestamp);

        dao.update(record);
    }

    @Override
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
        super.initFormContext(formContext);

        formContext.setShareAuths(FormRole.WRITABLE);

        if (!StringUtils.isEmpty(getRecord().getTitle()))
        {
            String title = formContext.getFormData().getString("事项名称");
            if (StringUtils.isEmpty(title))
            {
                formContext.getFormData().setValue("事项名称", getRecord().getTitle());
            }
            else if (!record.getTitle().equals(title))
            {
                formContext.getFormData().setValue("事项名称", getRecord().getTitle());
            }
        }
    }
}
