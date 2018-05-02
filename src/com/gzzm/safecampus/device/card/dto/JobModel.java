package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/26
 */
public class JobModel
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
        private List<JobModel.Record> RecordList;

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


        public List<JobModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<JobModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("jobId")
        private String Job_id;
        @ReflectionField("jobName")
        private String Job_name;
        @ReflectionField("privillege")
        private String Privillege;

        public String getJob_id()
        {
            return Job_id;
        }

        public void setJob_id(String job_id)
        {
            Job_id = job_id;
        }

        public String getJob_name()
        {
            return Job_name;
        }

        public void setJob_name(String job_name)
        {
            Job_name = job_name;
        }

        public String getPrivillege()
        {
            return Privillege;
        }

        public void setPrivillege(String privillege)
        {
            Privillege = privillege;
        }
    }
}
