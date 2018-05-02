package com.gzzm.portal.eval;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import com.gzzm.portal.cms.station.*;
import com.gzzm.portal.inquiry.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.*;

import java.sql.Date;
import java.util.*;


/**
 * @author sjy
 * @date 2018/3/1
 */
@Service(url = "/portal/eval/evalDetailResult")
public class EvalResultDetailPage extends BaseNormalCrud<EvalRecord, Long>
{
    @Inject
    private EvalDao dao;

    private TargetType evalType;

    private Long objectId;

    @Lower(column = "evalTime")
    private Date evalTime_start;

    @Upper(column = "evalTime")
    private Date evalTime_end;

    @NotSerialized
    private EvalRecord record;

    @NotSerialized
    private List<EvalOption> options;

    @NotSerialized
    private List<EvalQuestion> questions;

    public EvalResultDetailPage()
    {
        addOrderBy("evalTime", OrderType.desc);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        String title = initTitle(evalType, objectId) + "-评价列表";
        view.setTitle(title);
        view.setRemark(title);
        view.addComponent("评价时间", "evalTime_start", "evalTime_end");
        view.addColumn("评价时间", "evalTime");
        view.addColumn("IP", "ip");
        view.addColumn("查看详情", new CButton("查看详情", "showEvalDetailPage(${recordId})"));
        view.addButton(Buttons.query());
        view.importJs("/portal/eval/eval_result.js");
        return view;
    }

    @Service(url = "/portal/eval/openEvalDetailPage")
    public String openEvalDetailPage(Long recordId) throws Exception
    {
        record = dao.load(EvalRecord.class, recordId);
        EvalOption option = record.getOption();
        List<EvalAnswer> answerResults = record.getAnswerResults();
        if (option != null)
        {
            options = dao.queryEvalOptions(option.getTypeId());
        }
        if (answerResults != null && answerResults.size() > 0)
        {
            questions = dao.queryQuestions(answerResults.get(0).getQuestion().getTypeId());
        }
        return "/portal/eval/eval_result_detail.ptl";
    }

    private String initTitle(TargetType evalType, Long objectId) throws Exception
    {
        if (evalType == TargetType.CHANNEL)
        {
            Integer channelId = objectId.intValue();
            return dao.load(Channel.class, channelId).getChannelName();
        }
        else if (evalType == TargetType.INFORMATION)
        {
            return dao.load(Information.class, objectId).getTitle();
        }
        else if (evalType == TargetType.INQUIRY)
        {
            return dao.load(Inquiry.class, objectId).getTitle();
        }
        else if (evalType == TargetType.STATION)
        {
            Integer stationId = objectId.intValue();
            return dao.load(Station.class, stationId).getTitle();
        }
        return null;
    }

    public List<EvalQuestion> getQuestions()
    {
        return questions;
    }

    public void setQuestions(List<EvalQuestion> questions)
    {
        this.questions = questions;
    }

    public List<EvalOption> getOptions()
    {
        return options;
    }

    public void setOptions(List<EvalOption> options)
    {
        this.options = options;
    }

    public EvalRecord getRecord()
    {
        return record;
    }

    public void setRecord(EvalRecord record)
    {
        this.record = record;
    }

    public TargetType getEvalType()
    {
        return evalType;
    }

    public void setEvalType(TargetType evalType)
    {
        this.evalType = evalType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public Date getEvalTime_start()
    {
        return evalTime_start;
    }

    public void setEvalTime_start(Date evalTime_start)
    {
        this.evalTime_start = evalTime_start;
    }

    public Date getEvalTime_end()
    {
        return evalTime_end;
    }

    public void setEvalTime_end(Date evalTime_end)
    {
        this.evalTime_end = evalTime_end;
    }
}
