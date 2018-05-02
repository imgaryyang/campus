package com.gzzm.im;

/**
 * @author wxj
 * @date 11-12-4
 */
public enum OperationType
{
    /**
     * 仅仅发送消息
     */
    sendMessage,

    /**
     * 添加群成员并发送消息
     */
    addGroupMember,

    /**
     * 删除群成员并发送消息
     */
    deleteGroupMember
}
