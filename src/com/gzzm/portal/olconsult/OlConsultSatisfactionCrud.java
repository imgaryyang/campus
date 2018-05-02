package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * Created with IntelliJ IDEA.
 * User: wym
 * Date: 13-6-5
 * Time: 上午9:30
 * 满意度管理
 */
@Service(url = "/portal/olconsult/satisfaction")
public class OlConsultSatisfactionCrud extends BaseNormalCrud<OlConsultSatisfaction, Integer>
{

    @Like
    private String satisfaction;

    private Integer typeId;

    public OlConsultSatisfactionCrud()
    {
        setLog(true);
    }

    public String getSatisfaction()
    {
        return satisfaction;
    }

    public void setSatisfaction(String satisfaction)
    {
        this.satisfaction = satisfaction;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    @Override
    public void initEntity(OlConsultSatisfaction entity) throws Exception
    {
        super.initEntity(entity);
        entity.setTypeId(typeId);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("满意度", "satisfaction");
        view.addColumn("满意度", "satisfaction");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("满意度", "satisfaction");
        view.addDefaultButtons();
        return view;
    }
}
