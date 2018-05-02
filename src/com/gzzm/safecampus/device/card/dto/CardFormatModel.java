package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class CardFormatModel
{
    public CardFormatModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String ActionCode;
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

        public String getActionCode()
        {
            return ActionCode;
        }

        public void setActionCode(String actionCode)
        {
            ActionCode = actionCode;
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
        private String ActionCode;
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
        @ReflectionField("cardType")
        private String CardType;
        @ReflectionField("cardName")
        private String CardName;
        @ReflectionField("attendFlag")
        private String AttendFlag;
        @ReflectionField("doorFlag")
        private String DoorFlag;
        @ReflectionField("offLineMealFlag")
        private String OffLineMealFlag;
        @ReflectionField("useFlag")
        private String UseFlag;
        @ReflectionField("cardFlag")
        private String CardFlag;

        public String getCardType()
        {
            return CardType;
        }

        public void setCardType(String cardType)
        {
            CardType = cardType;
        }

        public String getCardName()
        {
            return CardName;
        }

        public void setCardName(String cardName)
        {
            CardName = cardName;
        }

        public String getAttendFlag()
        {
            return AttendFlag;
        }

        public void setAttendFlag(String attendFlag)
        {
            AttendFlag = attendFlag;
        }

        public String getDoorFlag()
        {
            return DoorFlag;
        }

        public void setDoorFlag(String doorFlag)
        {
            DoorFlag = doorFlag;
        }

        public String getOffLineMealFlag()
        {
            return OffLineMealFlag;
        }

        public void setOffLineMealFlag(String offLineMealFlag)
        {
            OffLineMealFlag = offLineMealFlag;
        }

        public String getUseFlag()
        {
            return UseFlag;
        }

        public void setUseFlag(String useFlag)
        {
            UseFlag = useFlag;
        }

        public String getCardFlag()
        {
            return CardFlag;
        }

        public void setCardFlag(String cardFlag)
        {
            CardFlag = cardFlag;
        }
    }
}
