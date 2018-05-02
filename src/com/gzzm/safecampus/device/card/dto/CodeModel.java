package com.gzzm.safecampus.device.card.dto;

/**
 * @author liyabin
 * @date 2018/3/20
 */
public class CodeModel
{
    public CodeModel()
    {
    }

    public  static class ErpCodeReq
    {
        /**
         * 客户端请求接口流水Id，应答会回写该ID，为异步通讯提供支持。
         */
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

    public  static class ErpCodeRes
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        /**
         * 结果:
         * 0,成功；其他：失败
         */
        private String Result;
        private String Error;
        private String ERPKey;

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

        public String getERPKey()
        {
            return ERPKey;
        }

        public void setERPKey(String ERPKey)
        {
            this.ERPKey = ERPKey;
        }
    }
}
