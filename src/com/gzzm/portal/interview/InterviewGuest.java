package com.gzzm.portal.interview;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

@Entity(table="PLINTERVIEWGUEST", keys={"guestId"})
public class InterviewGuest
{
  @Generatable(length=8)
  private Integer guestId;
  @Require
  @ColumnDescription(type="varchar(50)", nullable=false)
  private String guestName;
  @Require
  @ColumnDescription(nullable=false)
  private GuestType guestType;
  private char[] introduction;
  private byte[] photo;
  @ColumnDescription(type="number(6)")
  private Integer orderId;
  
  public Integer getGuestId()
  {
    return this.guestId;
  }
  
  public void setGuestId(Integer guestId)
  {
    this.guestId = guestId;
  }
  
  public String getGuestName()
  {
    return this.guestName;
  }
  
  public void setGuestName(String guestName)
  {
    this.guestName = guestName;
  }
  
  public GuestType getGuestType()
  {
    return this.guestType;
  }
  
  public void setGuestType(GuestType guestType)
  {
    this.guestType = guestType;
  }
  
  public char[] getIntroduction()
  {
    return this.introduction;
  }
  
  public void setIntroduction(char[] introduction)
  {
    this.introduction = introduction;
  }
  
  public byte[] getPhoto()
  {
    return this.photo;
  }
  
  public void setPhoto(byte[] photo)
  {
    this.photo = photo;
  }
  
  public Integer getOrderId()
  {
    return this.orderId;
  }
  
  public void setOrderId(Integer orderId)
  {
    this.orderId = orderId;
  }
  
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    InterviewGuest that = (InterviewGuest)o;
    if (!this.guestId.equals(that.guestId)) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    return this.guestId.hashCode();
  }
  
  public String toString()
  {
    return this.guestName;
  }
}
