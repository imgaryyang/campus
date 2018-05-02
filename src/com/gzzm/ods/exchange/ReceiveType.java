package com.gzzm.ods.exchange;

/**
 * 收文的类型，包括收文，联合发文，会签公文
 *
 * @author camel
 * @date 2010-9-17
 */
public enum ReceiveType
{
    /**
     * 收文
     */
    receive,

    /**
     * 联合发文
     */
    union,

    /**
     * 联合发文盖章
     */
    unionseal,

    /**
     * 会签
     */
    collect,

    /**
     * 收文联合办文
     */
    uniondeal,

    /**
     * 抄送给用户的公文
     */
    copy
}
