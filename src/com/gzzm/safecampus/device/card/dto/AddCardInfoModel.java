package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/28
 */
public class AddCardInfoModel
{
    public AddCardInfoModel()
    {
    }

    public static class ReqModel
    {
        /**
         * 客户端请求接口流水Id，应答会回写该ID，为异步通讯提供支持。
         */
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;

        @ReflectionField("walletFlag")
        private String emp_id;
        @ReflectionField("cardNo")
        private String card_id;
        @ReflectionField("cardSn")
        private String card_sn;
        @ReflectionField("cardType")
        private String CardType;
        @ReflectionField("mobilePhone")
        private String MobilePhone;
        @ReflectionField("cardTypeCode")
        private String CardTypeCode;
        @ReflectionField("areaCode")
        private String AreaCode;
        @ReflectionField("useFlag")
        private String UseFlag;
        @ReflectionField("customerType")
        private String CustomerType;
        @ReflectionField("empFlag")
        private String EmpFlag;
        @ReflectionField("cardState")
        private String CardState;
        @ReflectionField("cardVerifyCode")
        private String CardVerifyCode;
        @ReflectionField("cardBegDate")
        private String CardBegDate;
        @ReflectionField("cardEndDate")
        private String CardEndDate;
        @ReflectionField("depositValue")
        private String DepositValue;
        @ReflectionField("maintenance")
        private String Maintenance;
        @ReflectionField("password")
        private String vQueryPsw;
        @ReflectionField("usePwd")
        private String UsePwd;
        @ReflectionField("limitOneTime")
        private String LimitOneTime;
        @ReflectionField("limitDay")
        private String LimitDay;
        @ReflectionField("privillege")
        private String Privillege;
        @ReflectionField("machID")
        private String MachID;
        @ReflectionField("empUserCode")
        private String Emp_Erp_Code;
        @ReflectionField("opuser")
        private String op_user;
        private List<Record> RecordList;

        public List<Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<Record> recordList)
        {
            RecordList = recordList;
        }

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

        public String getEmp_id()
        {
            return emp_id;
        }

        public void setEmp_id(String emp_id)
        {
            this.emp_id = emp_id;
        }

        public String getCard_id()
        {
            return card_id;
        }

        public void setCard_id(String card_id)
        {
            this.card_id = card_id;
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

        public String getMachID()
        {
            return MachID;
        }

        public void setMachID(String machID)
        {
            MachID = machID;
        }

        public String getEmp_Erp_Code()
        {
            return Emp_Erp_Code;
        }

        public void setEmp_Erp_Code(String emp_Erp_Code)
        {
            Emp_Erp_Code = emp_Erp_Code;
        }

        public String getOp_user()
        {
            return op_user;
        }

        public void setOp_user(String op_user)
        {
            this.op_user = op_user;
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
    }

    public static class Record
    {
        @ReflectionField("walletFlag")
        private String WalletFlag;
        @ReflectionField("cardSequ")
        private String Card_sequ;
        @ReflectionField("balance")
        private String Balance;

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

        public String getBalance()
        {
            return Balance;
        }

        public void setBalance(String balance)
        {
            Balance = balance;
        }
    }
}
