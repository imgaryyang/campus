package com.gzzm.safecampus.device.card.dao;

import com.gzzm.safecampus.campus.device.DeviceCard;
import com.gzzm.safecampus.device.card.entity.*;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/23
 */
public abstract class ServiceDao extends GeneralDao
{

    @OQL("select max(1) from DeviceCard")
    public abstract Integer getCardNo();

    @OQL("select count(0)+1 from CardInfo t")
    public abstract Integer getCardNo(String schoolNum, Integer cardType);

    @OQL("select t from CardInfo t where t.cardNo =:1")
    public abstract CardInfo getCardInfo(String cardNo);

    @OQL("select t from DeviceCard t where t.cardNo =:1")
    public abstract DeviceCard getDeviceCard(String cardNo);

    @OQL("select t from DeviceCard t where t.type =:1 and t.targetId =:2")
    public abstract DeviceCard findDeviceCard(Integer cardType, Integer targetId);

    @OQL("select t from CardItem t where t.cardId =:1")
    public abstract List<CardItem> findCardItems(Integer cardId);

    @OQL("select t from DeviceCard t where t.cardSn =:1")
    public abstract DeviceCard findDeviceCardBySn(String cardSn );

    @OQL("select t from DeviceCard t where t.cardSn =:1 and t.status =1")
     public abstract DeviceCard checkDeviceCardBySn(String cardSn);
}
