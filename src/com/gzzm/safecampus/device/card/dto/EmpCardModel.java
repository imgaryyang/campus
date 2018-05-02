package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/23
 */
public class EmpCardModel
{
    public EmpCardModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String emp_id;

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
    }

    public static class ResModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String Result;
        private String Error;
        private List<EmpCardModel.Record> RecordList;

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

        public List<EmpCardModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<EmpCardModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("studentNo")
        private String Card_ID;
        @ReflectionField("studentName")
        private String MobilePhone;
        @ReflectionField("cardState")
        private String CardState;

        public String getCard_ID()
        {
            return Card_ID;
        }

        public void setCard_ID(String card_ID)
        {
            Card_ID = card_ID;
        }

        public String getMobilePhone()
        {
            return MobilePhone;
        }

        public void setMobilePhone(String mobilePhone)
        {
            MobilePhone = mobilePhone;
        }

        public String getCardState()
        {
            return CardState;
        }

        public void setCardState(String cardState)
        {
            CardState = cardState;
        }
    }
}
