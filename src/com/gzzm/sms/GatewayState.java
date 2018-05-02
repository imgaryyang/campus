package com.gzzm.sms;

/**
 * 网关状态
 *
 * @author camel
 * @date 2010-11-5
 */
public enum GatewayState
{
    /**
     * 已停止
     */
    stoped,

    /**
     * 正在运行
     */
    running,

    /**
     * 正在启动
     */
    starting,

    /**
     * 正在停止
     */
    stoping,

    /**
     * 标识已关闭
     */
    closed,

    /**
     * 启动失败
     */
    error;


    @Override
    public String toString()
    {
        switch (this)
        {
            case stoped:
                return "已停止";
            case running:
                return "运行中";
            case starting:
                return "启动中";
            case stoping:
                return "停止中";
            case closed:
                return "已关闭";
            case error:
                return "启动失败";
        }

        return null;
    }
}
