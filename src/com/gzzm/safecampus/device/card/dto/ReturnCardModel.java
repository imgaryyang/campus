package com.gzzm.safecampus.device.card.dto;

import java.util.List;

/**
 * @author liyabin
 * @date 2018-04-04
 */
public class ReturnCardModel
{
    public ReturnCardModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String card_id;
        private String DepositValue;
        private String Maintenance;
        private String MachID;
        private String op_user;
        private List<Record> RecordList;

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
            card_id = card_id;
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

        public String getMachID()
        {
            return MachID;
        }

        public void setMachID(String machID)
        {
            MachID = machID;
        }

        public String getOp_user()
        {
            return op_user;
        }

        public void setOp_user(String op_user)
        {
            this.op_user = op_user;
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
        private String WalletFlag;
        private String Card_sequ;
        private String DecMoney;
        private String AllowMoney;

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

        public String getDecMoney()
        {
            return DecMoney;
        }

        public void setDecMoney(String decMoney)
        {
            DecMoney = decMoney;
        }

        public String getAllowMoney()
        {
            return AllowMoney;
        }

        public void setAllowMoney(String allowMoney)
        {
            AllowMoney = allowMoney;
        }
    }
}
