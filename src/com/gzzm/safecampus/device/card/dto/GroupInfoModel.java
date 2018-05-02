package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/28
 */
public class GroupInfoModel
{
    public GroupInfoModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;

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

    }

    public static class ResModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String Result;
        private String Error;
        private List<IDTypeModel.Record> RecordList;

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


        public List<IDTypeModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<IDTypeModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("groupNo")
        private String group_id;
        @ReflectionField("groupName")
        private String group_name;
        @ReflectionField("groupInfo")
        private String Group_info;

        public String getGroup_id()
        {
            return group_id;
        }

        public void setGroup_id(String group_id)
        {
            this.group_id = group_id;
        }

        public String getGroup_name()
        {
            return group_name;
        }

        public void setGroup_name(String group_name)
        {
            this.group_name = group_name;
        }

        public String getGroup_info()
        {
            return Group_info;
        }

        public void setGroup_info(String group_info)
        {
            Group_info = group_info;
        }
    }
}
