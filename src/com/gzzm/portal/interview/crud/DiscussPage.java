package com.gzzm.portal.interview.crud;

import com.gzzm.portal.interview.dao.DiscussDao;
import com.gzzm.portal.interview.entity.GuestDiscuss;
import com.gzzm.portal.interview.enumtype.DiscussGuestType;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 门户网站-网友留言
 *
 * @author lzt
 * @date 2018/3/21 17:29
 */
@Service
public class DiscussPage
{
    @Inject
    private DiscussDao dao;

    private Integer interviewId;

    private String content;

    private String userName;


    public DiscussPage()
    {
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getInterviewId()
    {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId)
    {
        this.interviewId = interviewId;
    }

    @Service(url = "/portal/interview/visitor/post/discuss")
    @Transactional
    public void visitorDiscuss() throws Exception
    {
        String remoteAddr = RequestContext.getContext().getRequest().getRemoteAddr();

        RequestContext.getContext().getSession().setAttribute("nickName", userName);
        GuestDiscuss discuss = new GuestDiscuss();

        discuss.setInterviewId(interviewId);
        discuss.setGuestType(DiscussGuestType.VISITOR);
        discuss.setVisitorName(userName);
        discuss.setGuestIp(remoteAddr);
        discuss.setCreateTime(new Date());
        discuss.setContent(content.toCharArray());
        dao.save(discuss);
    }
}
