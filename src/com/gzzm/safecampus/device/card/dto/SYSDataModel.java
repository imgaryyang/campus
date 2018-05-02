package com.gzzm.safecampus.device.card.dto;

/**
 * @author liyabin
 * @date 2018/3/28
 */
public class SYSDataModel
{
    public SYSDataModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        private String ActionCode;
        private String ReturnAllowBalance;
        private String ServerLoginPWD;
        private String MobileServersID;
        private String MobileCompanyID;
        private String ServerIP;
        private String ServerPort;

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

        public String getReturnAllowBalance()
        {
            return ReturnAllowBalance;
        }

        public void setReturnAllowBalance(String returnAllowBalance)
        {
            ReturnAllowBalance = returnAllowBalance;
        }

        public String getServerLoginPWD()
        {
            return ServerLoginPWD;
        }

        public void setServerLoginPWD(String serverLoginPWD)
        {
            ServerLoginPWD = serverLoginPWD;
        }

        public String getMobileServersID()
        {
            return MobileServersID;
        }

        public void setMobileServersID(String mobileServersID)
        {
            MobileServersID = mobileServersID;
        }

        public String getMobileCompanyID()
        {
            return MobileCompanyID;
        }

        public void setMobileCompanyID(String mobileCompanyID)
        {
            MobileCompanyID = mobileCompanyID;
        }

        public String getServerIP()
        {
            return ServerIP;
        }

        public void setServerIP(String serverIP)
        {
            ServerIP = serverIP;
        }

        public String getServerPort()
        {
            return ServerPort;
        }

        public void setServerPort(String serverPort)
        {
            ServerPort = serverPort;
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
        private String ReturnAllowBalance;
        private String ServerLoginPWD;
        private String MobileServersID;
        private String MobileCompanyID;
        private String ServerIP;
        private String ServerPort;

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

        public String getReturnAllowBalance()
        {
            return ReturnAllowBalance;
        }

        public void setReturnAllowBalance(String returnAllowBalance)
        {
            ReturnAllowBalance = returnAllowBalance;
        }

        public String getServerLoginPWD()
        {
            return ServerLoginPWD;
        }

        public void setServerLoginPWD(String serverLoginPWD)
        {
            ServerLoginPWD = serverLoginPWD;
        }

        public String getMobileServersID()
        {
            return MobileServersID;
        }

        public void setMobileServersID(String mobileServersID)
        {
            MobileServersID = mobileServersID;
        }

        public String getMobileCompanyID()
        {
            return MobileCompanyID;
        }

        public void setMobileCompanyID(String mobileCompanyID)
        {
            MobileCompanyID = mobileCompanyID;
        }

        public String getServerIP()
        {
            return ServerIP;
        }

        public void setServerIP(String serverIP)
        {
            ServerIP = serverIP;
        }

        public String getServerPort()
        {
            return ServerPort;
        }

        public void setServerPort(String serverPort)
        {
            ServerPort = serverPort;
        }
    }
}
