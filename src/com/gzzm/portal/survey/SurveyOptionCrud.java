package com.gzzm.portal.survey;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * 问题选项管理
 *
 * @author wxj
 * @date 2011-6-17
 */

@Service(url = "/portal/survey/option")
public class SurveyOptionCrud extends DeptOwnedSubListCrud<SurveyOption, Integer>
{
    @Inject
    private SurveyDao dao;

    /**
     * 调查问题id
     */
    private Integer questionId;

    public SurveyOptionCrud()
    {
        setLog(true);
    }

    public Integer getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Integer questionId)
    {
        this.questionId = questionId;
    }

    @Override
    protected String getTopOwnerField()
    {
        return "question.survey.station";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 5;
    }

    @Override
    protected void initListView(SubListView view) throws Exception
    {
        view.setName("可选答案");
        view.addColumn("答案内容", "content");
        view.addColumn("图片", new CHref("${picture==null?'':'查看'}").setAction("showOptionPicture(${optionId})"));
        view.addColumn("链接目标", "url");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("答案内容", "content");
        view.addComponent("图片", new CFile("picture")).setFileType("$image");
        view.addComponent("链接目标", "url");
        view.addComponent("备注", new CTextArea("remark"));

        view.addDefaultButtons();

        return view;
    }

    /**
     * 显示图片的方法
     *
     * @param optionId :答案的id
     * @return 图片的字节数组
     * @throws Exception 数据库读取数据异常
     */
    @Service(url = "/portal/survey/option/{$0}/picture")
    public byte[] getPicture(Integer optionId) throws Exception
    {
        return getEntity(optionId).getPicture();
    }
}
