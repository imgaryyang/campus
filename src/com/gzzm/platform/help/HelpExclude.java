package com.gzzm.platform.help;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.Entity;

/**
 * 记录某些用户不提示某些帮助
 *
 * @author camel
 * @date 2010-12-14
 */
@Entity(table = "PFHELPEXCLUDE", keys = {"helpId", "userId"})
public class HelpExclude
{
    private Integer helpId;

    private Integer userId;

    private Help help;

    private User user;

    public HelpExclude()
    {
    }

    public Integer getHelpId()
    {
        return helpId;
    }

    public void setHelpId(Integer helpId)
    {
        this.helpId = helpId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Help getHelp()
    {
        return help;
    }

    public void setHelp(Help help)
    {
        this.help = help;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
