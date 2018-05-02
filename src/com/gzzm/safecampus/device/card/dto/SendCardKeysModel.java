package com.gzzm.safecampus.device.card.dto;

import com.gzzm.safecampus.device.card.annotation.ReflectionField;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class SendCardKeysModel
{
    public SendCardKeysModel()
    {
    }

    public static class ReqModel
    {
        private String SequenceId;
        private String ERPCode;
        private String Token;
        private String TimeStamp;
        @ReflectionField("cpuaid")
        private String CPUAID;
        @ReflectionField("cpukey")
        private String CPUKey;
        @ReflectionField("m1addkey")
        private String M1AddKey;
        @ReflectionField("m1deckey")
        private String M1DecKey;
        @ReflectionField("m1tackey")
        private String M1TacKey;
        @ReflectionField("sectorv2")
        private String SectorV2;
        @ReflectionField("sectorv1")
        private String SectorV1;
        @ReflectionField("keyv1")
        private String KeyV1;
        @ReflectionField("erkeyv2")
        private String ERKeyV2;
        @ReflectionField("erkeycpu")
        private String ERKeyCPU;

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

        public String getCPUAID()
        {
            return CPUAID;
        }

        public void setCPUAID(String CPUAID)
        {
            this.CPUAID = CPUAID;
        }

        public String getCPUKey()
        {
            return CPUKey;
        }

        public void setCPUKey(String CPUKey)
        {
            this.CPUKey = CPUKey;
        }

        public String getM1AddKey()
        {
            return M1AddKey;
        }

        public void setM1AddKey(String m1AddKey)
        {
            M1AddKey = m1AddKey;
        }

        public String getM1DecKey()
        {
            return M1DecKey;
        }

        public void setM1DecKey(String m1DecKey)
        {
            M1DecKey = m1DecKey;
        }

        public String getM1TacKey()
        {
            return M1TacKey;
        }

        public void setM1TacKey(String m1TacKey)
        {
            M1TacKey = m1TacKey;
        }

        public String getSectorV2()
        {
            return SectorV2;
        }

        public void setSectorV2(String sectorV2)
        {
            SectorV2 = sectorV2;
        }

        public String getSectorV1()
        {
            return SectorV1;
        }

        public void setSectorV1(String sectorV1)
        {
            SectorV1 = sectorV1;
        }

        public String getKeyV1()
        {
            return KeyV1;
        }

        public void setKeyV1(String keyV1)
        {
            KeyV1 = keyV1;
        }

        public String getERKeyV2()
        {
            return ERKeyV2;
        }

        public void setERKeyV2(String ERKeyV2)
        {
            this.ERKeyV2 = ERKeyV2;
        }

        public String getERKeyCPU()
        {
            return ERKeyCPU;
        }

        public void setERKeyCPU(String ERKeyCPU)
        {
            this.ERKeyCPU = ERKeyCPU;
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

    }
}
