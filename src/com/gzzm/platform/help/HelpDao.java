package com.gzzm.platform.help;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 帮助相关的操作
 *
 * @author camel
 * @date 2010-12-13
 */
public abstract class HelpDao extends GeneralDao
{
    public HelpDao()
    {
    }

    /**
     * 获得所有帮助
     *
     * @return 所有帮助
     * @throws Exception 数据库查询错误
     */
    @OQL("select h from Help h")
    public abstract List<Help> getAllHelps() throws Exception;

    /**
     * 获得某个用户没有阅读的提醒
     *
     * @param userId 用户ID
     * @param appIds 用户拥有访问权限的功能
     * @return 此用户没有阅读的提醒列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select r from Remind r where valid<>0 and all m in reads : m.userId<>:1 and r.appId in ?2 order by remindId")
    public abstract List<Remind> getReminds(Integer userId, Collection<String> appIds) throws Exception;

    /**
     * 设置某个提醒已经被某用户阅读
     *
     * @param userId   用户ID
     * @param remindId 提醒ID
     * @throws Exception 插入数据到数据库中错误
     */
    public void readRemind(Integer userId, Integer remindId) throws Exception
    {
        RemindRead read = new RemindRead();
        read.setUserId(userId);
        read.setRemindId(remindId);

        save(read);
    }

    public void excludeHelp(Integer userId, Integer helpId) throws Exception
    {
        HelpExclude exclude = new HelpExclude();
        exclude.setUserId(userId);
        exclude.setHelpId(helpId);

        save(exclude);
    }

    @OQL("exists HelpExclude where userId=:1 and helpId=:2")
    public abstract boolean isHelpExcluded(Integer userId, Integer helpId) throws Exception;
}
