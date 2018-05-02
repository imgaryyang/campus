package com.gzzm.safecampus.device.card.service;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.account.*;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.device.*;
import com.gzzm.safecampus.campus.device.CardType;
import com.gzzm.safecampus.device.card.dao.*;
import com.gzzm.safecampus.device.card.dto.CardNoModel;
import com.gzzm.safecampus.device.card.entity.*;
import com.gzzm.safecampus.device.card.enumtype.UseFlag;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.DataConvert;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/23
 */
public class DeviceService
{
    @Inject
    ServiceDao serviceDao;

    @Inject
    CommonDao commonDao;

    public DeviceService()
    {
    }

    /**
     * 检查网卡是否可用暂时不实现
     *
     * @param NetMark
     * @return
     */
    public boolean chechNetMark(String NetMark)
    {
        return true;
    }

    /**
     * 目前不去别卡类型后续在拓展
     *
     * @param reqbean
     * @return
     */
    @Transactional
    public String getCardNo(CardNoModel.ReqModel reqbean) throws Exception
    {
        Integer deptId = Integer.parseInt(reqbean.getERPCode());
        School school = commonDao.getSchool(deptId);
        String emp_id = reqbean.getEmp_id();
        String[] split = emp_id.split("_");
        Integer cardType = Integer.valueOf(split[0]);
        CardType type = DataConvert.convertType(CardType.class, cardType);
//        Integer studentNo = Integer.valueOf(split[split.length - 1]);
        String schoolNum = school.getSchoolNum();
        Integer cardNo = serviceDao.getCardNo(schoolNum, cardType);
        String cardNoStr = null;
        if (cardNo < 10) cardNoStr = "0000000" + cardNo;
        else if (cardNo < 100) cardNoStr = "000000" + cardNo;
        else if (cardNo < 1000) cardNoStr = "00000" + cardNo;
        else if (cardNo < 10000) cardNoStr = "0000" + cardNo;
        else if (cardNo < 100000) cardNoStr = "000" + cardNo;
        else if (cardNo < 1000000) cardNoStr = "00" + cardNo;
        String cardNum =cardNoStr;
        DeviceCard deviceCard = new DeviceCard();
//        SchoolYear schoolYear = commonDao.getSchoolYearBydeptId(deptId);
        Integer targetId = null;
        if (CardType.STUDENT.equals(type))//学生卡
        {
            Student student = commonDao.findOne(deptId, split[1]);
            targetId = student.getStudentId();
            deviceCard.setTargetId(student.getStudentId());
            DeviceCard hasCard = serviceDao.findDeviceCard(1, targetId);
            if (hasCard != null && hasCard.getStatus() != null)
            {
                switch (hasCard.getStatus())
                {
                    case UNACTIVATED:
                        return hasCard.getCardNo();
                    case ACTIVE:
                        return hasCard.getCardNo();
                }
            }

        }
        else
        {

        }
        deviceCard.setTargetId(targetId);
        deviceCard.setDeptId(deptId);
        deviceCard.setCardNo(cardNum);
        deviceCard.setSchoolId(school.getSchoolId());
        deviceCard.setType(type);
        deviceCard.setStatus(CardStatus.UNACTIVATED);//未激活
        //卡号
        CardInfo cardInfo = new CardInfo();
        cardInfo.setCardNo(cardNum);
        cardInfo.setPassFlag(UseFlag.ENABLE);
        cardInfo.setPassWord(Tools.getMessage("campus.device.CardInfo.Default.PASSWORD"));
        cardInfo.setSchoolNum(schoolNum);
        cardInfo.setCardType(DataConvert.convertType(CardType.class, cardType));
        //生成钱包类型
        CardItem item = new CardItem();
        item.setCardInfo(cardInfo);
        item.setAllowMoney(0);
        item.setBalance(0);
        item.setWalletFlag("0");
        item.setAppState(CardStatus.UNACTIVATED);
        item.setCardSequ(0);
        commonDao.save(cardInfo);
        commonDao.save(item);
        commonDao.save(deviceCard);
        return cardNum;
    }


    public CardInfo findCard(String cardNum)
    {
        CardInfo cardInfo = serviceDao.getCardInfo(cardNum);
        return cardInfo;
    }

    public DeviceCard findDeviceCard(String cardNum)
    {
        return serviceDao.getDeviceCard(cardNum);
    }

    public DeviceCard findDeviceCardBySn(String cardSn)
    {
        return serviceDao.findDeviceCardBySn(cardSn);
    }

    public boolean checkDeviceCardByCardSn(String cardSn)
    {
        DeviceCard card = serviceDao.findDeviceCardBySn(cardSn);

        return (card == null || card.getStatus().equals(CardStatus.UNACTIVATED));
    }

    public boolean checkDeviceCard(String cardSn)
    {
        DeviceCard card = serviceDao.checkDeviceCardBySn(cardSn);
        return (card == null);
    }

    public String getDeviceState(String cardSn)
    {
        DeviceCard card = serviceDao.findDeviceCardBySn(cardSn);
        if (card == null)
        {
            return "0";
        }
        String result = "";
        switch (card.getStatus())
        {
            case REFUNDED:
                result = "0";
                break;
            case DISABLED:
                result = "0";
                break;
            case RETIRED:
                result = "0";
                break;
            case EXPIRED:
                result = "0";
                break;
            case ACTIVE:
                result = "1";
                break;
            case UNACTIVATED:
                result = "1";
                break;
        }

        return ("" + card.getStatus().ordinal());
    }

    public List<CardItem> findCardItems(Integer cardId)
    {
        return serviceDao.findCardItems(cardId);
    }

    public static void main(String[] agrs)
    {
        System.out.println("000122".substring(3, 4));
    }
}
