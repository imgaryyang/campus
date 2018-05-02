package com.gzzm.portal.eval;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

import static net.cyan.crud.QueryType.oql;

/**
 * @author sjy
 * @date 2018/2/27
 */
public abstract class EvalDao extends GeneralDao
{
    @OQL("select t from com.gzzm.portal.eval.EvalRecord t where t.objectId=:1 and t.evalType=:2 and t.optionId is not null order by t.evalTime desc")
    public abstract EvalRecord queryEvalRecord(Long objectId, TargetType evalType);

    @OQL("select count(t) as totalEval,sum(t.option.score) as totalScore from com.gzzm.portal.eval.EvalRecord t where t.optionId is not null and t.objectId=:1 and t.evalType=:2")
    public abstract EvalStatResultBean countEval(Long objectId, TargetType evalType);

    @OQL("select count(t) from com.gzzm.portal.eval.EvalRecord t where t.objectId=:1 and t.evalType=:2")
    public abstract Long countAllEval(Long objectId, TargetType evalType);

    @OQL("select t from com.gzzm.portal.eval.EvalOption t where t.typeId=:1 order by t.orderId")
    public abstract List<EvalOption> queryEvalOptions(Integer typeId);

    @OQL("select count(t) from com.gzzm.portal.eval.EvalRecord t where t.objectId=:1 and t.evalType=:2 and t.optionId=:3")
    public abstract Long countOptionEval(Long objectId, TargetType evalType, Integer optionId);

    @OQL("select a.answer.question.typeId from EvalAnswerResult a where a.record.evalType=:2 and a.record.objectId=:1")
    public abstract Integer queryQuestionTypeId(Long objectId, TargetType evalType);

    @OQL("select count(t.answerId) from com.gzzm.portal.eval.EvalAnswerResult t where t.answer.questionId=:1 and t.record.evalType=:3 and t.record.objectId=:2")
    public abstract Integer countAllAnswer(Integer questionId, Long objectId, TargetType evalType);

    @OQL("select count(t.answerId) from com.gzzm.portal.eval.EvalAnswerResult t where t.answerId=:1 and t.record.evalType=:3 and t.record.objectId=:2")
    public abstract Integer countAnswer(Integer answerId, Long objectId, TargetType evalType);

    @OQL("select t from com.gzzm.portal.eval.EvalQuestion t where t.typeId=:1 order by t.orderId")
    public abstract List<EvalQuestion> queryQuestions(Integer typeId);

    /**
     * 对于同一封信件，每个ip只能评价一次
     *
     * @param ip       用户ip
     * @param objectId 信件id
     * @return
     */
    @OQL("select t from com.gzzm.portal.eval.EvalRecord t where t.ip=:1 and t.objectId=:2")
    public abstract EvalRecord queryEvalRecordByIp(String ip, Long objectId);

    @OQL("select t.record from com.gzzm.portal.eval.EvalAnswerResult t where t.answerId = :1")
    public abstract List<EvalRecord> queryRecords(Integer answerId);

    @OQL("select t from com.gzzm.portal.eval.EvalQuestion t order by orderId")
    public abstract List<EvalQuestion> queryQuestions();

    @OQL("select t from com.gzzm.portal.eval.EvalType t where t.deleteTag = 0 and t.typeName like :1")
    public abstract List<EvalType> queryTypes(String name);

}
