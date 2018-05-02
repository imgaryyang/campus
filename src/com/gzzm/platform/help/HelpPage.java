package com.gzzm.platform.help;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2010-12-13
 */
@Service
public class HelpPage
{
    @Inject
    private HelpDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    public HelpPage()
    {
    }

    /**
     * 设置某个帮助不再对当前用户提示
     *
     * @param helpId 帮助id
     * @throws Exception 数据库操作错误
     */
    @Service(url = "/help/{$0}/exclude")
    @ObjectResult
    public void excludeHelp(Integer helpId) throws Exception
    {
        dao.excludeHelp(userOnlineInfo.getUserId(), helpId);
    }

    /**
     * 获得当前用户未阅读的提醒
     *
     * @return 为阅读的提醒列表
     * @throws Exception 数据库查询错误
     */
    @Service(url = "/reminds")
    public List<Remind> loadReminds() throws Exception
    {
        return dao.getReminds(userOnlineInfo.getUserId(), userOnlineInfo.isAdmin() ? null : userOnlineInfo.getAppIds());
    }

    /**
     * 设置某个提醒已经被当前用户阅读
     *
     * @param remindId 提醒id
     * @throws Exception 数据库操作错误
     */
    @Service(url = "/remind/{$0}/read")
    @ObjectResult
    public void readRemind(Integer remindId) throws Exception
    {
        dao.readRemind(userOnlineInfo.getUserId(), remindId);
    }
}
