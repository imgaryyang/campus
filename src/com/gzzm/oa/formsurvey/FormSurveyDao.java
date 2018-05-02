package com.gzzm.oa.formsurvey;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 调研功能相关的dao
 *
 * @author camel
 * @date 13-4-23
 */
public abstract class FormSurveyDao extends GeneralDao
{
    public FormSurveyDao()
    {
    }

    public FormSurvey getSurvey(Integer surveyId) throws Exception
    {
        return load(FormSurvey.class, surveyId);
    }

    public FormSurveyRecord getRecord(Integer recordId) throws Exception
    {
        return load(FormSurveyRecord.class, recordId);
    }

    @GetByField({"surveyId", "linkId"})
    public abstract FormSurveyRecord getRecordByLinkId(Integer surveyId, String linkId) throws Exception;

    @OQLUpdate("update FormSurveyRecord set state=:2,auditTime=:3 where recordId in :1")
    public abstract void setRecords(Integer[] recordIds, RecordState state, Date auditTime) throws Exception;

    @OQL("select s from FormSurvey s where valid=1 order by orderId")
    public abstract List<FormSurvey> getSurveys() throws Exception;
}
