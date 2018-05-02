package com.gzzm.safecampus.wx.user;

import com.gzzm.platform.commons.Sex;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 微信用户类
 *
 * @author yiuman
 * @date 2018/3/22
 */
@Entity(table = "SCWXUSER", keys = "wxUserId")
public class WxUser extends BaseBean {

    /**
     * Id
     */
    @Generatable(length = 6)
    private Integer wxUserId;

    /**
     * 关联学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    private School school;

    /**
     * 微信用户名
     */
    @ColumnDescription(type = "varchar2(50)")
    private String userName;

    /**
     * 昵称
     */
    @ColumnDescription(type = "varchar2(50)")
    private String nickName;

    /**
     * 性别
     */
    @ColumnDescription(type = "number(1)")
    private Sex sex;

    /**
     * 电话
     */
    @ColumnDescription(type = "varchar2(50)")
    private String phone;

    /**
     * 微信号Openid（微信用户的唯一标识）
     */
    @ColumnDescription(type = "varchar2(50)")
    private String openId;

    /**
     * 认证时间
     */
    private Date authTime;

    /**
     * 关注时间
     */
    private Date subscribeTime;

    /**
     * 所在城市
     */
    @ColumnDescription(type = "varchar2(50)")
    private String city;

    /**
     * 所在省份
     */
    @ColumnDescription(type = "varchar2(50)")
    private String province;

    /**
     * 头像
     */
    @ColumnDescription(type = "blob")
    private byte[] heardImg;

    /**
     * 认证身份类型
     */
    @ColumnDescription(type = "number(1)")
    private IdentifyType identifyType;

    /**
     * 身份Id
     */
    @ColumnDescription(type = "number(6)")
    private Integer identifyId;

    @ColumnDescription(type = "varchar2(50)")
    private String idCard;

    /**
     * 微信用户状态
     * 0-->正常
     * 1-->解绑
     */
    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Integer status;

    @ColumnDescription(type = "varchar(500)")
    private String imgUrl;

    public WxUser() {
    }

    public Integer getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Integer wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    public Date getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public byte[] getHeardImg() {
        return heardImg;
    }

    public void setHeardImg(byte[] heardImg) {
        this.heardImg = heardImg;
    }

    public IdentifyType getIdentifyType() {
        return identifyType;
    }

    public void setIdentifyType(IdentifyType identifyType) {
        this.identifyType = identifyType;
    }

    public Integer getIdentifyId() {
        return identifyId;
    }

    public void setIdentifyId(Integer identifyId) {
        this.identifyId = identifyId;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WxUser)) return false;

        WxUser wxUser = (WxUser) o;

        return wxUserId != null ? wxUserId.equals(wxUser.wxUserId) : wxUser.wxUserId == null;
    }

    @Override
    public int hashCode() {
        return wxUserId != null ? wxUserId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return userName;
    }
}
