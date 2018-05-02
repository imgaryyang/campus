package com.gzzm.safecampus.identification.common;

import com.gzzm.safecampus.identification.ResultIde;

import java.util.List;

/**
 * 接收数据
 * @author zy
 * @date 2018/4/11 10:12
 */
public class AcceptData
{
    /**
     * 0成功 1失败
     */
    private String resCode;

    /**
     * 错误消息
     */
    private String errorMsg;

    private Integer imageId;

    /**
     * 识别返回结果
     */
    private List<ResultIde> resultIdes;

    public AcceptData()
    {
    }

    public String getResCode()
    {
        return resCode;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public Integer getImageId()
    {
        return imageId;
    }

    public void setImageId(Integer imageId)
    {
        this.imageId = imageId;
    }

    public List<ResultIde> getResultIdes()
    {
        return resultIdes;
    }

    public void setResultIdes(List<ResultIde> resultIdes)
    {
        this.resultIdes = resultIdes;
    }
}
