package com.gzzm.portal.solicit;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * Created by sjy on 2016/3/22.
 * 民意征集回复 实体类
 */
@Entity(table = "PLSOLICITREPLY", keys = "replyId")
public class SolicitReply {

  /**
   * 主键
   */
  @Generatable(length = 8)
  private Integer replyId;

  /**
   * 关联民意征集Id
   */
  private Integer solicitId;

  @ToOne("solicitId")
  @NotSerialized
  private Solicit solicit;

  /**
   * 回复内容
   */
  @NotSerialized
  private char[] content;

  private Integer userId;

  @ToOne("userId")
  private User user;

  /**
   * 回复人名字，显示在网站上
   */
  @ColumnDescription(type = "varchar(100)", nullable = false)
  private String userName;

  /**
   * 回复人真实名称
   */
  @ColumnDescription(type = "varchar(50)")
  private String realName;

  /**
   * 回复人Ip
   */
  @ColumnDescription(type = "varchar(20)", nullable = false)
  private String ip;

  /**
   * 回复人手机号码
   */
  @ColumnDescription(type = "varchar(20)")
  private String phone;
  /**
   * 回复人邮箱
   */
  @ColumnDescription(type = "varchar(200)")
  private String email;

  /**
   * 回复人地址
   */
  @ColumnDescription(type = "varchar(500)")
  private String address;

  /**
   * 回复时间
   */
  private Date replyTime;

  /**
   * 审核状态
   */
  @ColumnDescription(defaultValue = "0")
  private SolicitReplyState state;

  /**
   * 审核人
   */
  private Integer checkId;

  @ToOne("checkId")
  private User checker;

  /**
   * 审核时间
   */
  private Date checkTime;

  /**
   * 审核内容
   */
  @NotSerialized
  private char[] checkContent;

  public Integer getReplyId() {
    return replyId;
  }

  public void setReplyId(Integer replyId) {
    this.replyId = replyId;
  }

  public Integer getSolicitId() {
    return solicitId;
  }

  public void setSolicitId(Integer solicitId) {
    this.solicitId = solicitId;
  }

  public Solicit getSolicit() {
    return solicit;
  }

  public void setSolicit(Solicit solicit) {
    this.solicit = solicit;
  }

  public char[] getContent() {
    return content;
  }

  public void setContent(char[] content) {
    this.content = content;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getReplyTime() {
    return replyTime;
  }

  public void setReplyTime(Date replyTime) {
    this.replyTime = replyTime;
  }

  public SolicitReplyState getState() {
    return state;
  }

  public void setState(SolicitReplyState state) {
    this.state = state;
  }

  public Integer getCheckId() {
    return checkId;
  }

  public void setCheckId(Integer checkId) {
    this.checkId = checkId;
  }

  public User getChecker() {
    return checker;
  }

  public void setChecker(User checker) {
    this.checker = checker;
  }

  public Date getCheckTime() {
    return checkTime;
  }

  public void setCheckTime(Date checkTime) {
    this.checkTime = checkTime;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public char[] getCheckContent() {
    return checkContent;
  }

  public void setCheckContent(char[] checkContent) {
    this.checkContent = checkContent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SolicitReply that = (SolicitReply) o;

    return replyId.equals(that.replyId);

  }

  @Override
  public int hashCode() {
    return replyId.hashCode();
  }
}
