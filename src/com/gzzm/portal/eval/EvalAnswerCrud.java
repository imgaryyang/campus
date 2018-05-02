package com.gzzm.portal.eval;

import com.gzzm.platform.commons.crud.*;

/**
 * @author sjy
 * @date 2018/2/27
 */
public class EvalAnswerCrud extends SubListCrud<EvalAnswer, Integer>
{
    private Integer questionId;

    @Override
    protected String getParentField()
    {
        return "questionId";
    }

    @Override
    protected void initListView(SubListView view) throws Exception
    {
        view.addColumn("答案内容", "answer");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("答案内容", "answer", true);
        view.addDefaultButtons();
        return view;
    }

    public Integer getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Integer questionId)
    {
        this.questionId = questionId;
    }
}
