package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.comet.CometService;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.integration.RemoteAddr;

import java.util.*;

/**
 * 在线咨询相关的类
 *
 * @author camel
 * @date 13-5-31
 */
@Service
public class OlConsultPage
{
    @Inject
    private OlConsultDao dao;

    @Inject
    private OlConsultSatisfactionDao olConsultSatisfactionDao;

    @Inject
    private CometService cometService;

    private Integer typeId=1;

    @RemoteAddr
    private String ip;

    /**
     * 当前正在咨询的咨询ID
     */
    private Integer consultId;

    /**
     * 0表示咨询者，1表示回答者
     */
    private int flag;

    @NotSerialized
    private OlConsult consult;

    private List<OlConsultSatisfaction> olConsultSatisfactionList;

    public OlConsultPage()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getConsultId()
    {
        return consultId;
    }

    public void setConsultId(Integer consultId)
    {
        this.consultId = consultId;
    }

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public List<OlConsultSatisfaction> getOlConsultSatisfactionList()
    {
        return olConsultSatisfactionList;
    }

    public OlConsult getConsult() throws Exception
    {
        if (consult == null && consultId != null)
            consult = dao.getConsult(consultId);

        return consult;
    }

    @Service(url = {"/[$0].chat", "/portal/olconsult/chat"})
    public String show(String path)
    {
        olConsultSatisfactionList = olConsultSatisfactionDao.getAllSatisfaction();
        if (path == null)
        {
            flag = 1;
            return "chat";
        }
        else
        {
            flag = 0;
            return path;
        }
    }

    @Service(url = "/portal/olconsult/start", method = HttpMethod.post)
    @Transactional
    public Integer start(String userName, String content) throws Exception
    {
        OlConsult consult = new OlConsult();
        consult.setUserName(userName);
        consult.setApplyTime(new Date());
        consult.setState(OlConsultState.WAITING);
        consult.setBrowserIp(ip);
        consult.setTypeId(typeId);
        dao.add(consult);

        OlConsultRecord record = new OlConsultRecord();
        record.setConsultId(consult.getConsultId());
        record.setChatTime(new Date());
        record.setFlag(0);
        record.setContent(content);
        dao.add(record);

        Message message = new Message();
        message.setUrl("/portal/olconsult/waiting?typeId=" + typeId);
        message.setMessage(Tools.getMessage("portal.olconsult.notify"));
        message.setMethods(ImMessageSender.IM);

        message.sendTo();

        return consult.getConsultId();
    }

    @Service(url = "/portal/olconsult/cancel")
    public void cancel() throws Exception
    {
        OlConsult consult = new OlConsult();
        consult.setConsultId(consultId);
        consult.setState(OlConsultState.CANCELED);
        consult.setEndTime(new Date());

        dao.update(consult);
    }

    @Service(url = "/portal/olconsult/end")
    public void end() throws Exception
    {
        OlConsult consult = new OlConsult();
        consult.setConsultId(consultId);
        consult.setState(OlConsultState.END);
        consult.setEndTime(new Date());

        dao.update(consult);

        //咨询人发送给客服
        if(flag==0){
            ConsultEndMessage message = new ConsultEndMessage();
            message.setConsultId(consultId);
            cometService.sendMessage(message, getConsult().getAnswerUserId());
        }else {
            ConsultEndMessage message = new ConsultEndMessage();
            message.setConsultId(consultId);
            cometService.sendMessage(message,"olconsult:" + consultId);
        }
    }

    /**
     * 咨询满意度
     *
     * @param satisId
     * @param satisfaction
     * @param consultId
     * @throws Exception
     */
    @Service(url = "/portal/olconsult/satisfaction.commit", method = HttpMethod.post)
    @ObjectResult
    public void satisfaction(Integer satisId, String satisfaction, Integer consultId) throws Exception
    {
        if (consultId != null)
        {
            OlConsult ol = dao.getConsult(consultId);
            if (ol != null)
            {
                ol.setSatisfactionId(satisId);
                ol.setSatisfactionRemark(satisfaction);
                dao.update(ol);
            }
        }

    }

    @NotSerialized
    @Service(url = "/portal/olconsult/{consultId}/records")
    public List<OlConsultRecord> getRecords() throws Exception
    {
        return dao.getRecords(consultId);
    }

    @NotSerialized
    @Service
    @ObjectResult
    public String getSeatName() throws Exception
    {
        return getConsult().getSeat().getSeatName();
    }

    @NotSerialized
    @Service
    @ObjectResult
    public String getUserName() throws Exception
    {
        OlConsult consult = getConsult();
        return consult == null ? null : consult.getUserName();
    }

    @Service(url = "/portal/olconsult/send", method = HttpMethod.post)
    @ObjectResult
    public void send(String content) throws Exception
    {
        OlConsultRecord record = new OlConsultRecord();
        record.setConsultId(consultId);
        record.setChatTime(new Date());
        record.setFlag(flag);
        record.setContent(content);

        dao.add(record);

        ConsultRecordMessage message = new ConsultRecordMessage(record);

        if (flag == 0)
        {
            //咨询人发送给客服
            cometService.sendMessage(message, getConsult().getAnswerUserId());
        }
        else
        {
            //客服发送给咨询人
            cometService.sendMessage(message, "olconsult:" + consultId);
        }
    }
}
