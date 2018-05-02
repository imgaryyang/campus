package com.gzzm.platform.consignation;

/**
 * 委托信息，包括被委托人和委托ID
 * @author camel
 * @date 2010-8-30
 */
public class ConsignationInfo
{
    /**
     * 委托id
     */
    private Integer consignationId;

    /**
     * 被委托人
     */
    private Integer consignee;

    public ConsignationInfo(Integer consignationId, Integer consignee)
    {
        this.consignationId = consignationId;
        this.consignee = consignee;
    }

    public Integer getConsignationId()
    {
        return consignationId;
    }

    public Integer getConsignee()
    {
        return consignee;
    }
}
