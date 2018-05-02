package com.gzzm.platform.help;

import com.gzzm.platform.menu.MenuContainer;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 新功能提醒
 *
 * @author camel
 * @date 2010-12-13
 */
@Entity(table = "PFREMIND", keys = "remindId")
public class Remind
{
    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    /**
     * 提醒id
     */
    @Generatable(length = 7)
    private Integer remindId;

    /**
     * 提醒的内容
     */
    @Require
    private String content;

    /**
     * 关联的应用的id
     */
    @Require
    private String appId;

    /**
     * 是否有效
     */
    @Require
    private Boolean valid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 阅读列表
     */
    @OneToMany
    @NotSerialized
    private List<RemindRead> reads;

    public Remind()
    {
    }

    public Integer getRemindId()
    {
        return remindId;
    }

    public void setRemindId(Integer remindId)
    {
        this.remindId = remindId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public Boolean isValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public List<RemindRead> getReads()
    {
        return reads;
    }

    public void setReads(List<RemindRead> reads)
    {
        this.reads = reads;
    }

    public String getMenuTitle()
    {
        if (StringUtils.isBlank(appId))
            return "";
        else
            return menuContainerProvider.get().getMenu(appId).getTitle();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Remind))
            return false;

        Remind remind = (Remind) o;

        return remindId.equals(remind.remindId);
    }

    @Override
    public int hashCode()
    {
        return remindId.hashCode();
    }

    @Override
    public String toString()
    {
        return content;
    }

    @BeforeAdd
    public void beforeInsert()
    {
        createTime = new Date();
    }
}
