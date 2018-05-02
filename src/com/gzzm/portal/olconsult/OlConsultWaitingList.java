package com.gzzm.portal.olconsult;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.message.comet.CometService;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.OrderType;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * User: wym
 * Date: 13-6-3
 * Time: 下午6:23
 * 在线咨询 等待列表
 */
@Service(url = "/portal/olconsult/waiting")
public class OlConsultWaitingList extends BaseQueryCrud<OlConsult, Integer>
{
    @Inject
    private CometService cometService;

    @Inject
    private OlConsultDao dao;

    private Integer typeId;

    @UserId
    private Integer userId;

    public OlConsultWaitingList()
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

    public OlConsultState getState()
    {
        return OlConsultState.WAITING;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("咨询人", "userName");
        view.addColumn("发起时间", "applyTime");
        view.addColumn("咨询人IP", "browserIp");
        view.addColumn("响应", new CButton("响应", "accept(${consultId})"));

        view.importJs("/portal/olconsult/waiting.js");

        view.defaultInit();

        return view;
    }

    /**
     * 响应咨询
     *
     * @param consultId 咨询ID
     * @throws Exception 数据库操作错误
     */
    @Service(url = "/portal/olconsult/consult/{$0}/accept")
    @ObjectResult
    @Transactional
    public void accept(Integer consultId) throws Exception
    {
        dao.lock(OlConsult.class, consultId);
        OlConsult consult = dao.getConsult(consultId);

        if (consult.getState() == OlConsultState.ACCEPTED || consult.getState() == OlConsultState.END)
        {
            throw new NoErrorException("portal.olconsult.accepted");
        }
        else if (consult.getState() == OlConsultState.CANCELED)
        {
            throw new NoErrorException("portal.olconsult.canceled");
        }

        OlConsultSeat seat = dao.getSeatByUserId(userId, typeId);
        if (seat == null)
        {
            throw new NoErrorException("portal.olconsult.noSeat");
        }

        consult.setStartTime(new Date());
        consult.setAnswerUserId(userId);
        consult.setSeatId(seat.getSeatId());
        consult.setState(OlConsultState.ACCEPTED);
        dao.update(consult);

        ConsultAcceptMessage message = new ConsultAcceptMessage();
        message.setConsultId(consultId);
        message.setSeatName(seat.getSeatName());

        cometService.sendMessage(message, "olconsult:" + consultId);
    }
}
