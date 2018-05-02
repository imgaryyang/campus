package com.gzzm.safecampus.device.card.dto;

/**
 * 获取卡号对接的数据bean
 * @author liyabin
 * @date 2018/3/23
 */
public class CardNoModel
{

    public CardNoModel()
    {
    }
    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String NetMark;
        private String Token;
        private String TimeStamp;
        private String emp_id;
        private String CardType;

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

        public String getNetMark()
        {
            return NetMark;
        }

        public void setNetMark(String netMark)
        {
            NetMark = netMark;
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

        public String getCardType()
        {
            return CardType;
        }

        public void setCardType(String cardType)
        {
            CardType = cardType;
        }

        public String getEmp_id()
        {
            return emp_id;
        }

        public void setEmp_id(String emp_id)
        {
            this.emp_id = emp_id;
        }
    }

    public static class ResModel
    {
        private String SequenceId;
        private String ERPCode;
        private String NetMark;
        private String Token;
        private String TimeStamp;
        private String Result;
        private String Error;
        private String Card_ID;

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

        public String getNetMark()
        {
            return NetMark;
        }

        public void setNetMark(String netMark)
        {
            NetMark = netMark;
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

        public String getCard_ID()
        {
            return Card_ID;
        }

        public void setCard_ID(String card_ID)
        {
            Card_ID = card_ID;
        }
    }

}
