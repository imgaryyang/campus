package com.gzzm.portal.eval;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;

/**
 * @author sjy
 * @date 2018/2/26
 */
@Service(url = "/portal/eval/evalOption")
public class EvalOptionCrud extends BaseNormalCrud<EvalOption, Integer>
{
    private Integer typeId;

    @Like
    private String optionName;

    @Override
    protected Object createListView() throws Exception
    {
        EvalTypeDisplay typeDisplay = Tools.getBean(EvalTypeDisplay.class);
        typeDisplay.setCatalog(EvalCatalog.BASE);
        PageTableView view = new ComplexTableView(typeDisplay, "typeId");
        view.addComponent("名称","optionName");
        view.addColumn("名称","optionName");
        view.addColumn("得分","score").setAlign(Align.center);
        view.addDefaultButtons();
        view.addButton(Buttons.sort());
        view.makeEditable();
        view.importJs("/portal/eval/eval_option.js");
        return view;
    }

    @Override
    public void initEntity(EvalOption entity) throws Exception
    {
        super.initEntity(entity);
        entity.setTypeId(typeId);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("名称","optionName",true);
        view.addComponent("得分","score",true);
        view.addDefaultButtons();
        return view;
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

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getOptionName()
    {
        return optionName;
    }

    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }
}
