package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/24
 */
public class CardInfoModel
{

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String card_id;
        private String UsePwd;
        private String LimitOneTime;
        private String LimitDay;
        private String Privillege;
        private String BlackCount;

        public String getSequenceId()
        {
            return SequenceId;
        }

        public void setSequenceId(String sequenceId)
        {
            SequenceId = sequenceId;
        }

        public String getERPCode()
        {
            return ERPCode;
        }

        public void setERPCode(String ERPCode)
        {
            this.ERPCode = ERPCode;
        }

        public String getToken()
        {
            return Token;
        }

        public void setToken(String token)
        {
            Token = token;
        }

        public String getTimeStamp()
        {
            return TimeStamp;
        }

        public void setTimeStamp(String timeStamp)
        {
            TimeStamp = timeStamp;
        }

        public String getCard_id()
        {
            return card_id;
        }

        public void setCard_id(String card_id)
        {
            this.card_id = card_id;
        }

        public String getUsePwd()
        {
            return UsePwd;
        }

        public void setUsePwd(String usePwd)
        {
            UsePwd = usePwd;
        }

        public String getLimitOneTime()
        {
            return LimitOneTime;
        }

        public void setLimitOneTime(String limitOneTime)
        {
            LimitOneTime = limitOneTime;
        }

        public String getLimitDay()
        {
            return LimitDay;
        }

        public void setLimitDay(String limitDay)
        {
            LimitDay = limitDay;
        }

        public String getPrivillege()
        {
            return Privillege;
        }

        public void setPrivillege(String privillege)
        {
            Privillege = privillege;
        }

        public String getBlackCount()
        {
            return BlackCount;
        }

        public void setBlackCount(String blackCount)
        {
            BlackCount = blackCount;
        }
    }

    public static class ResModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String Result;
        private String Error;
        private String emp_id;
        private String card_sn;
        private String CardType;
        private String MobilePhone;
        private String CardTypeCode;
        private String AreaCode;
        private String UseFlag;
        private String CustomerType;
        private String EmpFlag;
        private String CardState;
        private String StateDate;
        private String CardVerifyCode;
        private String CardBegDate;
        private String CardEndDate;
        private String DepositValue;
        private String Maintenance;
        private String vQueryPsw;
        private List<CardInfoModel.Record> RecordList;

        public String getSequenceId()
        {
            return SequenceId;
        }

        public void setSequenceId(String sequenceId)
        {
            SequenceId = sequenceId;
        }

        public String getERPCode()
        {
            return ERPCode;
        }

        public void setERPCode(String ERPCode)
        {
            this.ERPCode = ERPCode;
        }

        public String getToken()
        {
            return Token;
        }

        public void setToken(String token)
        {
            Token = token;
        }

        public String getTimeStamp()
        {
            return TimeStamp;
        }

        public void setTimeStamp(String timeStamp)
        {
            TimeStamp = timeStamp;
        }

        public String getResult()
        {
            return Result;
        }

        public void setResult(String result)
        {
            Result = result;
        }

        public String getError()
        {
            return Error;
        }

        public void setError(String error)
        {
            Error = error;
        }

        public String getEmp_id()
        {
            return emp_id;
        }

        public void setEmp_id(String emp_id)
        {
            this.emp_id = emp_id;
        }

        public String getCard_sn()
        {
            return card_sn;
        }

        public void setCard_sn(String card_sn)
        {
            this.card_sn = card_sn;
        }

        public String getCardType()
        {
            return CardType;
        }

        public void setCardType(String cardType)
        {
            CardType = cardType;
        }

        public String getMobilePhone()
        {
            return MobilePhone;
        }

        public void setMobilePhone(String mobilePhone)
        {
            MobilePhone = mobilePhone;
        }

        public String getCardTypeCode()
        {
            return CardTypeCode;
        }

        public void setCardTypeCode(String cardTypeCode)
        {
            CardTypeCode = cardTypeCode;
        }

        public String getAreaCode()
        {
            return AreaCode;
        }

        public void setAreaCode(String areaCode)
        {
            AreaCode = areaCode;
        }

        public String getUseFlag()
        {
            return UseFlag;
        }

        public void setUseFlag(String useFlag)
        {
            UseFlag = useFlag;
        }

        public String getCustomerType()
        {
            return CustomerType;
        }

        public void setCustomerType(String customerType)
        {
            CustomerType = customerType;
        }

        public String getEmpFlag()
        {
            return EmpFlag;
        }

        public void setEmpFlag(String empFlag)
        {
            EmpFlag = empFlag;
        }

        public String getCardState()
        {
            return CardState;
        }

        public void setCardState(String cardState)
        {
            CardState = cardState;
        }

        public String getStateDate()
        {
            return StateDate;
        }

        public void setStateDate(String stateDate)
        {
            StateDate = stateDate;
        }

        public String getCardVerifyCode()
        {
            return CardVerifyCode;
        }

        public void setCardVerifyCode(String cardVerifyCode)
        {
            CardVerifyCode = cardVerifyCode;
        }

        public String getCardBegDate()
        {
            return CardBegDate;
        }

        public void setCardBegDate(String cardBegDate)
        {
            CardBegDate = cardBegDate;
        }

        public String getCardEndDate()
        {
            return CardEndDate;
        }

        public void setCardEndDate(String cardEndDate)
        {
            CardEndDate = cardEndDate;
        }

        public String getDepositValue()
        {
            return DepositValue;
        }

        public void setDepositValue(String depositValue)
        {
            DepositValue = depositValue;
        }

        public String getMaintenance()
        {
            return Maintenance;
        }

        public void setMaintenance(String maintenance)
        {
            Maintenance = maintenance;
        }

        public String getvQueryPsw()
        {
            return vQueryPsw;
        }

        public void setvQueryPsw(String vQueryPsw)
        {
            this.vQueryPsw = vQueryPsw;
        }

        public List<Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("walletFlag")
        private String WalletFlag;

        @ReflectionField("cardSequ")
        private String Card_sequ;

        @ReflectionField("lastDealTime")
        private String LastDealTime;

        @ReflectionField("balance")
        private String Balance;

        @ReflectionField("allowMoney")
        private String AllowMoney;

        @ReflectionField("appState")
        private String AppState;

        public String getWalletFlag()
        {
            return WalletFlag;
        }

        public void setWalletFlag(String walletFlag)
        {
            WalletFlag = walletFlag;
        }

        public String getCard_sequ()
        {
            return Card_sequ;
        }

        public void setCard_sequ(String card_sequ)
        {
            Card_sequ = card_sequ;
        }

        public String getLastDealTime()
        {
            return LastDealTime;
        }

        public void setLastDealTime(String lastDealTime)
        {
            LastDealTime = lastDealTime;
        }

        public String getBalance()
        {
            return Balance;
        }

        public void setBalance(String balance)
        {
            Balance = balance;
        }

        public String getAllowMoney()
        {
            return AllowMoney;
        }

        public void setAllowMoney(String allowMoney)
        {
            AllowMoney = allowMoney;
        }

        public String getAppState()
        {
            return AppState;
        }

        public void setAppState(String appState)
        {
            AppState = appState;
        }
    }
}
