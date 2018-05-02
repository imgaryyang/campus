package com.gzzm.oa.formsurvey;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuItem;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DataConvert;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.nest.annotation.Inject;

/**
 * 调研统计
 *
 * @author camel
 * @date 13-4-24
 */
@Service(url = "/oa/formsurvey/stat")
public class FormSurveyStat extends DeptStat
{
    private Integer surveyId;

    @Inject
    private MenuItem menuItem;

    public FormSurveyStat()
    {
        setLoadTotal(true);
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    @Override
    protected void initStats() throws Exception
    {
        join(FormSurveyRecord.class, "r", "r.deptId=dept.deptId and r.surveyId=:surveyId and r.state>0");

        setGroupField("dept.deptId");

        addStat("deptName", "min(dept.deptName)", "'合计'");

        for (RecordState state : RecordState.values())
        {
            if (state != RecordState.NOSAVED)
            {
                addStat("total_" + state.name(), "count(r.state='" + state.ordinal() + "')");
            }
        }

        addStat("total", "count(r.recordId)");
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("部门", "topDeptIds");

        view.addColumn("部门", "deptName");

        for (RecordState state : RecordState.values())
        {
            if (state != RecordState.NOSAVED)
            {
                view.addColumn(DataConvert.toString(state), "total_" + state.name());
            }
        }

        view.addColumn("合计", "total");

        view.defaultInit();

        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters parameters = super.getExportParameters();
        parameters.setFileName(menuItem.getTitle());

        return parameters;
    }
}
