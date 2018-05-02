package com.gzzm.safecampus.device.card.dto;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class CardParamModel
{
    public CardParamModel()
    {
    }
    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String ActionCode;
        private String Sector;
        private String CardParam;

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

        public String getSector()
        {
            return Sector;
        }

        public void setSector(String sector)
        {
            Sector = sector;
        }

        public String getCardParam()
        {
            return CardParam;
        }

        public void setCardParam(String cardParam)
        {
            CardParam = cardParam;
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
        private String Sector;
        private String CardParam;

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

        public String getSector()
        {
            return Sector;
        }

        public void setSector(String sector)
        {
            Sector = sector;
        }

        public String getCardParam()
        {
            return CardParam;
        }

        public void setCardParam(String cardParam)
        {
            CardParam = cardParam;
        }
    }
}
