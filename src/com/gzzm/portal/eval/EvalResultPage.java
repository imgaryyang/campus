package com.gzzm.portal.eval;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import com.gzzm.portal.cms.station.*;
import com.gzzm.portal.inquiry.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 评价结果
 *
 * @author sjy
 * @date 2018/2/28
 */
@Service(url = "/portal/eval/evalResult")
public class EvalResultPage extends BaseNormalCrud<EvalRecord, Long>
{
    @Inject
    private EvalDao dao;

    @Require
    private TargetType evalType = TargetType.INQUIRY;

    private Long objectId;

    @Like
    private String title;

    @NotSerialized
    private EvalStatResultBean statBean;

    @Override
    protected String getQueryString() throws Exception
    {
        String sql = "select max(t.recordId) as recordId, t.evalType as evalType,t.objectId as objectId from com.gzzm.portal.eval.EvalRecord t where t.evalType=:evalType ";
        if (StringUtils.isNotBlank(title))
        {
            if (evalType == TargetType.INQUIRY)
            {
                sql += " and t.objectId in (select i.inquiryId from com.gzzm.portal.inquiry.Inquiry i where i.title like ?title)";
            }
            else if (evalType == TargetType.INFORMATION)
            {
                sql += " and t.objectId in (select i.informationId from com.gzzm.portal.cms.information.Information i where i.title like ?title)";
            }
            else if (evalType == TargetType.CHANNEL)
            {
                sql += " and t.objectId in (select i.channelId from com.gzzm.portal.cms.channel.Channel i where i.channelName like ?title)";
            }
            else if (evalType == TargetType.STATION)
            {
                sql += " and t.objectId in (select i.stationId from com.gzzm.portal.cms.station.Station i where i.title like ?title)";
            }
        }
        sql += " group by t.evalType,t.objectId order by max(t.evalTime) desc";
        return sql;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        view.addComponent("标题", "title");
        view.addComponent("类型", "evalType");
        view.addColumn("标题", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                return null;
            }

            @Override
            public String display(Object entity) throws Exception
            {
                EvalRecord record = (EvalRecord) entity;
                return initTitle(record);
            }
        }).setWrap(true);
        view.addColumn("类型", "evalType");
        view.addColumn("评价统计", new CButton("查看结果", "openEvalStatPage(${evalType.ordinal()},${objectId})"));
        view.addColumn("评价列表", new CButton("查看列表", "openEvalListPage(${evalType.ordinal()},${objectId})"));
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.importJs("/portal/eval/eval_result.js");
        return view;
    }

    @NotSerialized
    @Service(url = "/portal/eval/openEvalStatPage")
    public String openStatPage() throws Exception
    {
        statBean = new EvalStatResultBean();
        statBean.setTitle(initTitle(evalType, objectId));
        statBean.setAllTotalEval(dao.countAllEval(objectId, evalType));
        EvalRecord record = dao.queryEvalRecord(objectId, evalType);
        if (record != null)
        {
            EvalStatResultBean queryStatBean = dao.countEval(objectId, evalType);
            statBean.setTotalEval(queryStatBean.getTotalEval());
            statBean.setTotalScore(queryStatBean.getTotalScore());
            List<EvalOption> evalOptions = dao.queryEvalOptions(record.getOption().getTypeId());
            ListMap<EvalOption, Long> optionResult = new ListMap<EvalOption, Long>();
            statBean.setOptionResult(optionResult);
            for (EvalOption option : evalOptions)
            {
                Long optionEval = dao.countOptionEval(objectId, evalType, option.getOptionId());
                optionResult.put(option, optionEval);
            }
        }
        Integer questionTypeId = dao.queryQuestionTypeId(objectId, evalType);
        if (questionTypeId != null)
        {
            List<EvalQuestion> questions = dao.queryQuestions(questionTypeId);
            if (questions != null && questions.size() > 0)
            {
                List<QuestionStatBean> listMap = new ArrayList<QuestionStatBean>();
                statBean.setQuestionResult(listMap);
                for (EvalQuestion question : questions)
                {
                    QuestionStatBean answerStatBeen = new QuestionStatBean();
                    answerStatBeen.setQuestion(question.getQuestion());
                    answerStatBeen.setAllAnswer(dao.countAllAnswer(question.getQuestionId(), objectId, evalType));
                    listMap.add(answerStatBeen);
                    List<AnswerStatBean> answers = new ArrayList<AnswerStatBean>();
                    answerStatBeen.setAnswerStat(answers);
                    for (EvalAnswer evalAnswer : question.getAnswers())
                    {
                        AnswerStatBean bean = new AnswerStatBean();
                        answers.add(bean);
                        bean.setAnswer(evalAnswer.getAnswer());
                        bean.setAnswerCount(dao.countAnswer(evalAnswer.getAnswerId(), objectId, evalType));
                    }
                }
            }
        }
        return "/portal/eval/eval_result.ptl";
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

    private String initTitle(EvalRecord record) throws Exception
    {
        return initTitle(record.getEvalType(), record.getObjectId());
    }

    public TargetType getEvalType()
    {
        return evalType;
    }

    public void setEvalType(TargetType evalType)
    {
        this.evalType = evalType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public EvalStatResultBean getStatBean()
    {
        return statBean;
    }

    public void setStatBean(EvalStatResultBean statBean)
    {
        this.statBean = statBean;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }
}
