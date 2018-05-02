package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.common.AbstractTokenCheck;

/**
 * @author liyabin
 * @date 2018/3/20
 */
public class LoginModel
{
    public LoginModel()
    {
    }

    public  static class ReqModel extends AbstractTokenCheck
    {
        private String SequenceId;
        private String ERPCode;
        private String NetMark;
        private String Token;
        private String TimeStamp;
        private String opuser;
        private String PassWord;
        private String MachID;

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

        public String getOpuser()
        {
            return opuser;
        }

        public void setOpuser(String opuser)
        {
            this.opuser = opuser;
        }

        public String getPassWord()
        {
            return PassWord;
        }

        public void setPassWord(String passWord)
        {
            PassWord = passWord;
        }

        public String getMachID()
        {
            return MachID;
        }

        public void setMachID(String machID)
        {
            MachID = machID;
        }
    }

    public  static class ResModel extends AbstractTokenCheck
    {
        private String SequenceId;
        private String ERPCode;
        private String NetMark;
        private String Token;
        private String TimeStamp;
        private String Result;
        private String Error;
        private String Right;
        private String CompanyName;
        private String Op_User;
        private String PassWord;
        private String MachID;

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

        public String getRight()
        {
            return Right;
        }

        public void setRight(String right)
        {
            Right = right;
        }

        public String getCompanyName()
        {
            return CompanyName;
        }

        public void setCompanyName(String companyName)
        {
            CompanyName = companyName;
        }

        public String getOp_User()
        {
            return Op_User;
        }

        public void setOp_User(String op_User)
        {
            Op_User = op_User;
        }

        public String getPassWord()
        {
            return PassWord;
        }

        public void setPassWord(String passWord)
        {
            PassWord = passWord;
        }

        public String getMachID()
        {
            return MachID;
        }

        public void setMachID(String machID)
        {
            MachID = machID;
        }
    }
}
