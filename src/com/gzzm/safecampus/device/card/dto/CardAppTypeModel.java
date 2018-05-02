package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class CardAppTypeModel
{
    public CardAppTypeModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String ActionCode;
        private List<CardAppTypeModel.Record> RecordList;

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

        public String getActionCode()
        {
            return ActionCode;
        }

        public void setActionCode(String actionCode)
        {
            ActionCode = actionCode;
        }

        public List<CardAppTypeModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<CardAppTypeModel.Record> recordList)
        {
            RecordList = recordList;
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
        private String ActionCode;
        private List<CardAppTypeModel.Record> RecordList;

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

        public String getActionCode()
        {
            return ActionCode;
        }

        public void setActionCode(String actionCode)
        {
            ActionCode = actionCode;
        }

        public List<CardAppTypeModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<CardAppTypeModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("appIndex")
        private String AppIdx;
        @ReflectionField("appName")
        private String AppName;
        @ReflectionField("expireDate")
        private String ExpireDate;
        @ReflectionField("cardAppType")
        private String CardAppType;
        @ReflectionField("walletType")
        private String WalletType;
        @ReflectionField("walletBalType")
        private String WalletBalType;
        @ReflectionField("walletOverLimit")
        private String WalletOverLimit;
        @ReflectionField("debitFlag")
        private String DebitFlag;
        @ReflectionField("maxDebit")
        private String MaxDebit;
        @ReflectionField("balanceValidity")
        private String BalanceValidity;
        @ReflectionField("balanceValidityFlag")
        private String BalanceValidityFlag;
        @ReflectionField("opUser")
        private String OpUser;
        @ReflectionField("opDate")
        private String OpDate;
        @ReflectionField("createUser")
        private String CreateUser;
        @ReflectionField("createDate")
        private String CreateDate;
        @ReflectionField("description")
        private String Description;

        public String getAppIdx()
        {
            return AppIdx;
        }

        public void setAppIdx(String appIdx)
        {
            AppIdx = appIdx;
        }

        public String getAppName()
        {
            return AppName;
        }

        public void setAppName(String appName)
        {
            AppName = appName;
        }

        public String getExpireDate()
        {
            return ExpireDate;
        }

        public void setExpireDate(String expireDate)
        {
            ExpireDate = expireDate;
        }

        public String getCardAppType()
        {
            return CardAppType;
        }

        public void setCardAppType(String cardAppType)
        {
            CardAppType = cardAppType;
        }

        public String getWalletType()
        {
            return WalletType;
        }

        public void setWalletType(String walletType)
        {
            WalletType = walletType;
        }

        public String getWalletBalType()
        {
            return WalletBalType;
        }

        public void setWalletBalType(String walletBalType)
        {
            WalletBalType = walletBalType;
        }

        public String getWalletOverLimit()
        {
            return WalletOverLimit;
        }

        public void setWalletOverLimit(String walletOverLimit)
        {
            WalletOverLimit = walletOverLimit;
        }

        public String getDebitFlag()
        {
            return DebitFlag;
        }

        public void setDebitFlag(String debitFlag)
        {
            DebitFlag = debitFlag;
        }

        public String getMaxDebit()
        {
            return MaxDebit;
        }

        public void setMaxDebit(String maxDebit)
        {
            MaxDebit = maxDebit;
        }

        public String getBalanceValidity()
        {
            return BalanceValidity;
        }

        public void setBalanceValidity(String balanceValidity)
        {
            BalanceValidity = balanceValidity;
        }

        public String getBalanceValidityFlag()
        {
            return BalanceValidityFlag;
        }

        public void setBalanceValidityFlag(String balanceValidityFlag)
        {
            BalanceValidityFlag = balanceValidityFlag;
        }

        public String getOpUser()
        {
            return OpUser;
        }

        public void setOpUser(String opUser)
        {
            OpUser = opUser;
        }

        public String getOpDate()
        {
            return OpDate;
        }

        public void setOpDate(String opDate)
        {
            OpDate = opDate;
        }

        public String getCreateUser()
        {
            return CreateUser;
        }

        public void setCreateUser(String createUser)
        {
            CreateUser = createUser;
        }

        public String getCreateDate()
        {
            return CreateDate;
        }

        public void setCreateDate(String createDate)
        {
            CreateDate = createDate;
        }

        public String getDescription()
        {
            return Description;
        }

        public void setDescription(String description)
        {
            Description = description;
        }
    }
}
