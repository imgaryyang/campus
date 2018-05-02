package com.gzzm.platform.help;

import java.io.Serializable;

/**
 * 发送给客户端的帮助信息
 *
 * @author camel
 * @date 2010-12-13
 */
public class HelpInfo implements Serializable
{
    /**
     * 帮助ID
     */
    private Integer helpId;

    /**
     * 帮助内容
     */
    private String content;

    public HelpInfo(Integer helpId, String content)
    {
        this.helpId = helpId;
        this.content = content;
    }

    public Integer getHelpId()
    {
        return helpId;
    }

    public String getContent()
    {
        return content;
    }
}
