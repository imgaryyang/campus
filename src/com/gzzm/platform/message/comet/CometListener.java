package com.gzzm.platform.message.comet;

/**
 * comet监听器，监听用户连接和断开的事件
 *
 * @author camel
 * @date 2011-2-8
 */
public interface CometListener
{
    /**
     * 连接事件
     *
     * @param cometInfo 用户的comet信息
     * @param service service服务
     * @throws Exception 允许实现类抛出异常
     */
    public void connect(CometInfo cometInfo, CometService service) throws Exception;

    /**
     * 断开事件
     *
     * @param cometInfo 用户的comet信息
     * @param service service服务
     * @throws Exception 允许实现类抛出异常
     */
    public void disconnect(CometInfo cometInfo, CometService service) throws Exception;
}
