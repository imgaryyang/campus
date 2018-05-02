package com.gzzm.portal.eval;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;

/**
 * @author sjy
 * @date 2018/2/27
 */
@Service(url = "/portal/eval/evalQuestion")
public class EvalQuestionCrud extends BaseNormalCrud<EvalQuestion, Integer>
{
    private Integer typeId;

    @Override
    protected Object createListView() throws Exception
    {
        EvalTypeDisplay typeDisplay = Tools.getBean(EvalTypeDisplay.class);
        typeDisplay.setCatalog(EvalCatalog.QUESTION);
        PageTableView view = new ComplexTableView(typeDisplay, "typeId");
        view.addColumn("问题","question");
        view.addDefaultButtons();
        view.addButton(Buttons.sort());
        view.makeEditable();
        view.importJs("/portal/eval/eval_option.js");
        return view;
    }

    @Override
    public void initEntity(EvalQuestion entity) throws Exception
    {
        super.initEntity(entity);
        entity.setTypeId(typeId);
    }

    @Override
    public String add(String forward) throws Exception
    {
        super.add(forward);
        return "/portal/eval/eval_question.ptl";
    }

    @Override
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);
        return "/portal/eval/eval_question.ptl";
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        return "typeId=:typeId";
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

}
