package com.gzzm.safecampus.device.card.dto;

import java.util.List;

/**
 * @author liyabin
 * @date 2018/3/24
 */
public class CardTypeFromSNModel
{
    public CardTypeFromSNModel()
    {
    }
    public static class ReqModel
     {
         private String SequenceId;
         private String ERPCode;
         private String Token;
         private String TimeStamp;
         private String card_sn;

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

         public String getCard_sn()
         {
             return card_sn;
         }

         public void setCard_sn(String card_sn)
         {
             this.card_sn = card_sn;
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
         private String CardType;
         private String Card_id;
         private String Emp_id;
         private List<CardInfoModel.Record> RecordList;

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


         public String getCardType()
         {
             return CardType;
         }

         public void setCardType(String cardType)
         {
             CardType = cardType;
         }

         public String getCard_id()
         {
             return Card_id;
         }

         public void setCard_id(String card_id)
         {
             Card_id = card_id;
         }

         public String getEmp_id()
         {
             return Emp_id;
         }

         public void setEmp_id(String emp_id)
         {
             Emp_id = emp_id;
         }

         public List<CardInfoModel.Record> getRecordList()
         {
             return RecordList;
         }

         public void setRecordList(List<CardInfoModel.Record> recordList)
         {
             RecordList = recordList;
         }
     }
}
