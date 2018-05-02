package com.gzzm.safecampus.wx.personal;

/**
 * 验证结果
 *
 * @author yiuman
 * @date 2018/3/23
 */
public class AuthResult {

    private String msg;

    private String status;

    private String code;

    private String url;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
