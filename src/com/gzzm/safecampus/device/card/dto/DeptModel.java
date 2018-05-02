package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/22
 */
public class DeptModel
{
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
        private List<DeptModel.Record> RecordList;

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

        public List<DeptModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<DeptModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }
    public static class Record
    {
        @ReflectionField("deptId")
        private String depart_id;
        @ReflectionField("deptName")
        private String depart_name;
        @ReflectionField("parentDeptId")
        private String Parent_id;
        @ReflectionField("deptCode")
        private String Depart_ErpCode;

        public String getDepart_id()
        {
            return depart_id;
        }

        public void setDepart_id(String depart_id)
        {
            this.depart_id = depart_id;
        }

        public String getDepart_name()
        {
            return depart_name;
        }

        public void setDepart_name(String depart_name)
        {
            this.depart_name = depart_name;
        }

        public String getParent_id()
        {
            return Parent_id;
        }

        public void setParent_id(String parent_id)
        {
            Parent_id = parent_id;
        }

        public String getDepart_ErpCode()
        {
            return Depart_ErpCode;
        }

        public void setDepart_ErpCode(String depart_ErpCode)
        {
            Depart_ErpCode = depart_ErpCode;
        }
    }
}
