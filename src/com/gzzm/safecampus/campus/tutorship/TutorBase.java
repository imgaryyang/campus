package com.gzzm.safecampus.campus.tutorship;

import net.cyan.thunwind.annotation.ColumnDescription;

/**
 * @author yiuman
 * @date 2018/3/12
 */
public class TutorBase {

    /**
     * 服务QQ
     */
    @ColumnDescription(type = "varchar(100)")
    private String qq;

    /**
     * 服务微信
     */
    @ColumnDescription(type = "varchar(100)")
    private String weChat;

    /**
     * 跳转地址
     */
    @ColumnDescription(type = "varchar(500)")
    private String url;


    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeChat() {
        return weChat;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
