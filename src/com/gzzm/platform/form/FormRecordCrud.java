package com.gzzm.platform.form;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CHref;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * 表单测试
 *
 * @author camel
 * @date 11-9-15
 */
@Service(url = "/formrecord")
public class FormRecordCrud extends DeptOwnedEditableCrud<FormRecord, Long>
{
    @Inject
    private FormDao dao;

    @UserId
    private Integer userId;

    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<FormType> formTypes;

    @Like
    private String title;

    /**
     * 开始时间
     */
    @Lower(column = "createTime")
    private Date time_start;

    /**
     * 结束时间
     */
    @Upper(column = "createTime")
    private Date time_end;

    @Require
    private FormType formType;

    @Require
    private Integer formId;

    public FormRecordCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public FormType getFormType()
    {
        if (formType == null)
        {
            if (getEntity() != null && getEntity().getBody() != null && getEntity().getBody().getFormInfo() != null)
            {
                formType = getEntity().getBody().getFormInfo().getType();
            }

            if (formType == null)
            {
                formType = formTypes.get(0);
            }
        }
        return formType;
    }

    public void setFormType(FormType formType)
    {
        this.formType = formType;
    }

    public Integer getFormId()
    {
        if (getEntity() != null && getEntity().getBody() != null)
        {
            formId = getEntity().getBody().getFormId();
        }

        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @NotSerialized
    @Select(field = "formId")
    public List<FormInfo> getFormInfos() throws Exception
    {
        return FormApi.getSelectableFormList(getDeptId(), getFormType().valueOf(), getAuthDeptIds());
    }

    @Service(url = "/formrecord/{$0}/body")
    public InputFile downBody(Long recordId) throws Exception
    {
        FormRecord record = getEntity(recordId);

        FormBody body = record.getBody();

        if (body == null || body.getContent() == null)
            return null;

        return new InputFile(new String(body.getContent()).getBytes("UTF-8"), record.getTitle() + ".xml");
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        FormBody body = new FormBody();
        body.setFormId(formId);

        dao.add(body);

        getEntity().setBodyId(body.getBodyId());
        getEntity().setUserId(userId);
        getEntity().setCreateTime(new java.util.Date());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        FormBody body = new FormBody();
        body.setBodyId(getEntity().getBodyId());
        body.setFormId(formId);

        dao.update(body);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("标题", "title");
        view.addComponent("创建时间", "time_start", "time_end");

        view.addColumn("标题", "title");
        view.addColumn("表单", "body.formInfo.formName");
        view.addColumn("创建人", "user.userName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("编辑", new CHref("编辑").setAction("showBody(${recordId})"));
        view.addColumn("下载", new CHref("下载", "/formrecord/${recordId}/body").setTarget("_blank"));

        view.importJs("/platform/form/record.js");

        view.defaultInit(false);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("标题", "title");
        view.addComponent("表单类型", "this.formType").setProperty("onchange", "Cyan.Arachne.refresh('formId')");
        view.addComponent("表单", "this.formId");

        view.addDefaultButtons();

        return view;
    }
}
