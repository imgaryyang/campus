package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/22
 */
public class EmpListModel
{
    public EmpListModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String depart_id;
        private String emp_id;
        private String emp_fname;
        private String id_card;
        private String HadCard;
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

        public String getDepart_id()
        {
            return depart_id;
        }

        public void setDepart_id(String depart_id)
        {
            this.depart_id = depart_id;
        }

        public String getEmp_id()
        {
            return emp_id;
        }

        public void setEmp_id(String emp_id)
        {
            this.emp_id = emp_id;
        }

        public String getEmp_fname()
        {
            return emp_fname;
        }

        public void setEmp_fname(String emp_fname)
        {
            this.emp_fname = emp_fname;
        }

        public String getId_card()
        {
            return id_card;
        }

        public void setId_card(String id_card)
        {
            this.id_card = id_card;
        }

        public String getHadCard()
        {
            return HadCard;
        }

        public void setHadCard(String hadCard)
        {
            HadCard = hadCard;
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

    public static class ResModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String Result;
        private String Error;
        private List<EmpListModel.Record> RecordList;

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

        public List<EmpListModel.Record> getRecordList()
        {
            return RecordList;
        }

        public void setRecordList(List<EmpListModel.Record> recordList)
        {
            RecordList = recordList;
        }
    }

    public static class Record
    {
        @ReflectionField("empId")
        private String emp_id;
        @ReflectionField("empName")
        private String emp_fname;
        @ReflectionField("sex")
        private String sex;
        private String Emp_ErpCode;

        public String getEmp_id()
        {
            return emp_id;
        }

        public void setEmp_id(String emp_id)
        {
            this.emp_id = emp_id;
        }

        public String getEmp_fname()
        {
            return emp_fname;
        }

        public void setEmp_fname(String emp_fname)
        {
            this.emp_fname = emp_fname;
        }

        public String getSex()
        {
            return sex;
        }

        public void setSex(String sex)
        {
            this.sex = sex;
        }

        public String getEmp_ErpCode()
        {
            return Emp_ErpCode;
        }

        public void setEmp_ErpCode(String emp_ErpCode)
        {
            Emp_ErpCode = emp_ErpCode;
        }
    }
}
