package com.gzzm.oa.formsurvey;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.form.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * 调查名称
 *
 * @author camel
 * @date 13-4-23
 */
@Service(url = "/oa/formsurvey/survey")
public class FormSurveyCrud extends BaseNormalCrud<FormSurvey, Integer>
{
    @UserId
    private Integer userId;

    @Like
    private String surveyName;

    public FormSurveyCrud()
    {
        setLog(true);
    }

    public String getSurveyName()
    {
        return surveyName;
    }

    public void setSurveyName(String surveyName)
    {
        this.surveyName = surveyName;
    }

    @NotSerialized
    @Select(field = "entity.formId", text = "formName", value = "ieFormId")
    public List<FormInfo> getFormInfos() throws Exception
    {
        //只取平台管理下的表单
        if (getEntity() != null)
            return FormApi.getSelectableFormList(1, "survey", null);

        return null;
    }

    @Service(url = "/oa/formsurvey/survey/print/{$0}")
    public InputFile downPrimtTemplate(Integer surveyId) throws Exception
    {
        FormSurvey survey = getEntity(surveyId);
        return new InputFile(survey.getPrintTemplate(), survey.getSurveyName() + ".xml");
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public void initEntity(FormSurvey entity) throws Exception
    {
        super.initEntity(entity);

        entity.setValid(true);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreator(userId);
        getEntity().setCreateTime(new Date());
        getEntity().setLastModified(new Date());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if (getEntity().getPrintTemplate() != null)
            getEntity().setLastModified(new Date());

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("调研名称", "surveyName");

        view.addColumn("调研名称", "surveyName");
        view.addColumn("表单", "form.formName");
        view.addColumn("创建人", "user.userName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("有效", "valid");
        view.addColumn("打印模板", new CHref("下载", "/oa/formsurvey/survey/print/${surveyId}").setTarget("_blank"));

        view.defaultInit();

        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("调研名称", "surveyName");
        view.addComponent("调研表单", "formId");
        view.addComponent("标题", "title");
        view.addComponent("标题名称", "titleName");
        view.addComponent("打印模板", new CFile("printTemplate").setFileType("xml"));
        view.addComponent("js路径", "jsPath");
        view.addComponent("有效", "valid");

        view.addDefaultButtons();

        return view;
    }
}
