package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class CardTypeModel
{
    public CardTypeModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String ActionCode;
        private List<CardTypeModel.Record> RecordList;

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

        public List<CardTypeModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<CardTypeModel.Record> recordList)
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
        private List<CardTypeModel.Record> RecordList;

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

        public List<CardTypeModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<CardTypeModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("id")
        private String ID;
        @ReflectionField("name")
        private String Name;
        @ReflectionField("code")
        private String Code;
        @ReflectionField("isDefault")
        private String IsDefault;
        @ReflectionField("note")
        private String Note;

        public String getID()
        {
            return ID;
        }

        public void setID(String ID)
        {
            this.ID = ID;
        }

        public String getName()
        {
            return Name;
        }

        public void setName(String name)
        {
            Name = name;
        }

        public String getCode()
        {
            return Code;
        }

        public void setCode(String code)
        {
            Code = code;
        }

        public String getIsDefault()
        {
            return IsDefault;
        }

        public void setIsDefault(String isDefault)
        {
            IsDefault = isDefault;
        }

        public String getNote()
        {
            return Note;
        }

        public void setNote(String note)
        {
            Note = note;
        }
    }
}
