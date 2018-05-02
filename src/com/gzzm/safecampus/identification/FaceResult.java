package com.gzzm.safecampus.identification;

/**
 * 人脸接口返回数据
 * @author zy
 * @date 2018/3/26 9:26
 */
public class FaceResult
{
    /**
     * 接收标志 0成功 1失败
     */
    private String ret_code="1";

    /**
     * 错误消息
     */
    private String error_msg;

    public FaceResult()
    {
    }

    public String getRet_code()
    {
        return ret_code;
    }

    public void setRet_code(String ret_code)
    {
        this.ret_code = ret_code;
    }

    public String getError_msg()
    {
        return error_msg;
    }

    public void setError_msg(String error_msg)
    {
        this.error_msg = error_msg;
    }
}
