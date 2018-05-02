package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.PageUserSelector;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;

/**
 * User: wym
 * Date: 13-5-31
 * Time: 上午11:44
 * 坐席管理crud
 */
@Service(url = "/portal/olconsult/seat")
public class OlConsultSeatCrud extends BaseNormalCrud<OlConsultSeat, Integer>
{
    private Integer typeId;

    @Like
    private String seatName;

    private PageUserSelector selector;

    public OlConsultSeatCrud()
    {
        setLog(true);
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getSeatName()
    {
        return seatName;
    }

    public void setSeatName(String seatName)
    {
        this.seatName = seatName;
    }

    @Override
    public void initEntity(OlConsultSeat entity) throws Exception
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
    protected String[] getOrderWithFields()
    {
        return new String[]{"typeId"};
    }

    @Select(field = "entity.userId")
    public PageUserSelector getSelector()
    {
        if (selector == null)
            selector = new PageUserSelector();

        return selector;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("客服名称", "seatName");

        view.addColumn("客服名称", "seatName");
        view.addColumn("客服人员", "user.userName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("客服名称", "seatName");
        view.addComponent("客服人员", "userId").setProperty("text",
                getEntity().getUser() != null ? getEntity().getUser().getUserName() : "");

        view.addDefaultButtons();

        view.importCss("/portal/olconsult/seat.css");

        return view;
    }
}
