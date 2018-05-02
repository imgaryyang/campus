package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * User: wym
 * Date: 13-5-31
 * Time: 上午11:49
 * 咨询记录crud
 */
@Service(url = "/portal/olconsult/consult")
public class OlConsultQuery extends BaseQueryCrud<OlConsult, Integer>
{
    @Inject
    private OlConsultDao dao;

    private Integer typeId;

    private Integer seatId;

    @Lower(column = "applyTime")
    private Date time_start;

    @Upper(column = "applyTime")
    private Date time_end;

    private OlConsultState state;

    public OlConsultQuery()
    {
        addOrderBy("applyTime", OrderType.desc);
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getSeatId()
    {
        return seatId;
    }

    public void setSeatId(Integer seatId)
    {
        this.seatId = seatId;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public OlConsultState getState()
    {
        return state;
    }

    public void setState(OlConsultState state)
    {
        this.state = state;
    }

    @Select(field = "seatId")
    @NotSerialized
    public List<OlConsultSeat> getSeats() throws Exception
    {
        return dao.getSeats(typeId);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("发起时间", "time_start", "time_end");
        view.addComponent("状态", "state");
        view.addComponent("客服名称", "seatId");

        view.addColumn("咨询人", "userName");
        view.addColumn("发起时间", "applyTime");
        view.addColumn("响应时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("咨询人IP", "browserIp");
        view.addColumn("状态", "state");
        view.addColumn("客服名称", "seat.seatName");
        view.addColumn("客服姓名", "anserUser.userName");
        view.addColumn("满意度","olConsultSatisfaction.satisfaction");
        view.addColumn("评价","satisfactionRemark");
        view.addColumn("咨询内容", new CButton("查看", "showRecords(${consultId})"));

        view.importJs("/portal/olconsult/consult.js");

        view.defaultInit();

        return view;
    }

}
