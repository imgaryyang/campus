package com.gzzm.safecampus.device.card.dto;

/**
 * @author liyabin
 * @date 2018/3/23
 */
public class EmpInfoModel
{

    public EmpInfoModel()
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
        private String emp_fname;
        private String sex;
        private String IDType;
        private String id_card;
        private String depart_id;
        private String job_id;
        private String group_id;
        private String MobilePhone;

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

        public String getEmp_fname()
        {
            return emp_fname;
        }

        public void setEmp_fname(String emp_fname)
        {
            this.emp_fname = emp_fname;
        }

        public String getIDType()
        {
            return IDType;
        }

        public String getSex()
        {
            return sex;
        }

        public void setSex(String sex)
        {
            this.sex = sex;
        }

        public void setIDType(String IDType)
        {
            this.IDType = IDType;
        }

        public String getId_card()
        {
            return id_card;
        }

        public void setId_card(String id_card)
        {
            this.id_card = id_card;
        }

        public String getDepart_id()
        {
            return depart_id;
        }

        public void setDepart_id(String depart_id)
        {
            this.depart_id = depart_id;
        }

        public String getJob_id()
        {
            return job_id;
        }

        public void setJob_id(String job_id)
        {
            this.job_id = job_id;
        }

        public String getGroup_id()
        {
            return group_id;
        }

        public void setGroup_id(String group_id)
        {
            this.group_id = group_id;
        }

        public String getMobilePhone()
        {
            return MobilePhone;
        }

        public void setMobilePhone(String mobilePhone)
        {
            MobilePhone = mobilePhone;
        }
    }
}
