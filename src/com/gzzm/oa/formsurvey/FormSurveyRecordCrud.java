package com.gzzm.oa.formsurvey;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.SysValue;

import java.util.*;

/**
 * 调研的curd，实现调研数据的上报功能，每个单位可以上报多条记录
 *
 * @author camel
 * @date 13-4-23
 */
@Service(url = "/oa/formsurvey/record")
public class FormSurveyRecordCrud extends DeptOwnedNormalCrud<FormSurveyRecord, Integer>
{
    @Inject
    private FormSurveyDao dao;

    @UserId
    private Integer userId;

    @Like
    private String title;

    private Integer surveyId;

    private FormSurvey survey;

    /**
     * 是否为审核功能，true表示进入审核功能，false表示上报功能
     */
    private boolean audit;

    @Inject
    private MenuItem menuItem;

    public FormSurveyRecordCrud()
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

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public boolean isAudit()
    {
        return audit;
    }

    public void setAudit(boolean audit)
    {
        this.audit = audit;
    }

    public RecordState[] getStates()
    {
        if (audit)
            return new RecordState[]{RecordState.SUBMITED, RecordState.PASSED};
        else
            return new RecordState[]{RecordState.NOSUBMITED, RecordState.SUBMITED, RecordState.PASSED,
                    RecordState.NOPASSED};
    }

    private FormSurvey getSurvey() throws Exception
    {
        if (survey == null)
            survey = dao.getSurvey(surveyId);

        return survey;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Redirect
    @Service(url = "/oa/formsurvey/newrecord")
    public String newRecord() throws Exception
    {
        FormSurveyRecord record = new FormSurveyRecord();
        record.setDeptId(getDeptId());
        record.setSurveyId(surveyId);
        record.setState(RecordState.NOSAVED);
        record.setCreateTime(new Date());
        record.setUserId(userId);

        dao.add(record);

        return "/oa/formsurvey/body/" + record.getRecordId();
    }

    @Transactional
    @Service(method = HttpMethod.post)
    public void setStates(RecordState state) throws Exception
    {
        if (getKeys() != null && getKeys().length > 0)
        {
            dao.setRecords(getKeys(), state, state == RecordState.PASSED ? new Date() : SysValue.Null.Timestamp);
        }
    }

    @Transactional
    @Service(method = HttpMethod.post)
    public void setState(Integer recordId, RecordState state, String remark) throws Exception
    {
        FormSurveyRecord record = getEntity(recordId);

        record.setState(state);
        record.setRemark(remark);

        if (state == RecordState.PASSED)
            record.setAuditTime(new Date());
        else
            record.setAuditTime(SysValue.Null.Timestamp);

        dao.update(record);
    }

    protected List<CMenuItem> getSurveyMenus() throws Exception
    {
        List<CMenuItem> menus = new ArrayList<CMenuItem>();

        for (FormSurvey survey : dao.getSurveys())
        {
            if (!survey.getSurveyId().equals(surveyId))
            {
                menus.add(new CMenuItem(survey.getSurveyName(), "modifySurvey(" + survey.getSurveyId() + ")"));
            }
        }

        return menus;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void modifySurvey(Integer surveyId) throws Exception
    {
        if (getKeys() != null)
        {
            for (Integer recordId : getKeys())
            {
                modifySurvey(recordId, surveyId);
            }
        }
    }

    private void modifySurvey(Integer recordId, Integer surveyId) throws Exception
    {
        FormSurveyRecord record = dao.getRecord(recordId);

        record.setSurveyId(surveyId);
        dao.update(record);

        FormSurvey survey = dao.getSurvey(surveyId);
        Integer lastFormId = FormApi.getLastFormId(survey.getFormId());

        SystemFormContext context0 = FormApi.getFormContext(record.getBodyId());
        SystemFormContext context1 = FormApi.newFormContext(lastFormId);

        FormApi.copyForm(context0, context1, false);

        FormBody body = new FormBody();
        char[] content = new String(FormApi.saveFormData(context1), "UTF-8").toCharArray();
        body.setBodyId(record.getBodyId());
        body.setFormId(lastFormId);
        body.setContent(content);
        dao.update(body);
    }

    @Override
    public FormSurveyRecord clone(FormSurveyRecord entity) throws Exception
    {
        FormSurveyRecord record = super.clone(entity);

        record.setCreateTime(new Date());

        if ("copyAllTo".equals(getAction()))
        {
            SystemFormContext context0 = FormApi.getFormContext(entity.getBodyId());
            SystemFormContext context1 = FormApi.newFormContext(Integer.valueOf(context0.getForm().getFormId()));

            FormApi.copyForm(context0, context1, true);


            FormApi.saveFormBody(context1);

            record.setBodyId(context1.getBodyId());
        }

        return record;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (!audit)
            setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        PageTableView view = !audit && (authDeptIds == null || authDeptIds.size() > 1) ?
                new ComplexTableView(new AuthDeptDisplay(), "deptId").enableDD() : new PageTableView();

        if (audit)
            view.setCheckable("${state.name()=='SUBMITED'||state.name()=='PASSED'}");
        else
            view.setCheckable("${state.name()=='NOSUBMITED'||state.name()=='NOPASSED'}");

        String titleName = getSurvey().getTitleName();
        view.addComponent(titleName, "title");

        if (audit && (authDeptIds == null || authDeptIds.size() > 1))
        {
            view.addComponent("部门", "topDeptIds");
        }

        view.addColumn(titleName, "title");
        view.addColumn("添加时间", "createTime");
        view.addColumn("修改时间", "updateTime");

        if (audit)
            view.addColumn("部门", "dept.deptName");

        view.addColumn("创建人", "user.userName");
        view.addColumn("状态", "state").setWidth("70");

        if (audit)
        {
            view.addColumn("审核时间", "auditTime");
            view.addColumn("审核不通过", new CButton("审核不通过", "setState(${recordId},'NOPASSED')"))
                    .setWidth("200").setWrap(true).setWidth("90");
        }
        else
        {
            view.addColumn("审核信息", "remark").setWidth("200").setWrap(true);
        }

        if (audit)
        {
            view.addButton(Buttons.query());
            view.addButton(Buttons.getButton("审核通过", "setStates('PASSED')", "ok"));
            view.addButton(Buttons.getButton("取消审核", "setStates('SUBMITED')", "cancel"));
            List<CMenuItem> surveyMenus = getSurveyMenus();
            if (surveyMenus.size() > 0)
                view.addButton(new CMenuButton(null, "修改类型", surveyMenus));

            view.addButton(new CMenuButton("deptTree", "复制到")).setIcon(Buttons.getIcon("catalog"))
                    .setProperty("onclick", "copyAllTo(null,this.value)");

            view.addButton(Buttons.export("xls"));

            view.makeEditable();
        }
        else
        {
            view.addButton(Buttons.query());
            view.addButton(Buttons.add(null));
            view.addButton(Buttons.getButton("提交", "setStates('SUBMITED')", "ok"));
            view.addButton(Buttons.delete());
            List<CMenuItem> surveyMenus = getSurveyMenus();
            if (surveyMenus.size() > 0)
                view.addButton(new CMenuButton(null, "修改类型", surveyMenus));

            view.addButton(new CMenuButton("deptTree", "复制到")).setIcon(Buttons.getIcon("catalog"))
                    .setProperty("onclick", "copyAllTo(null,this.value)");

            view.addButton(Buttons.export("xls"));

            view.makeEditable();
        }

        view.importJs("/oa/formsurvey/record.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters parameters = super.getExportParameters();
        parameters.setFileName(menuItem.getTitle() + "列表");

        return parameters;
    }
}
