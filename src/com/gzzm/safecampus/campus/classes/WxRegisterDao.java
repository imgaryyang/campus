package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.wx.personal.WxStudent;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 微信注册认证Dao
 * @author yiuman
 * @date 2018/4/13
 */
public abstract class WxRegisterDao extends GeneralDao {

    @OQLUpdate("update WxRegister set state = 2 where registerId in ?1")
    public abstract void changeSateByKeys(Integer[] keys);

    @OQL("select c from Student c where c.classesId = ?1 and c.studentName = ?2")
    public abstract Student getStudentByClsIdAndName(Integer classesId, String studentName);

    @OQL("select c from Guardian c where c.studentId=?1 and c.phone = ?2 ")
    public abstract Guardian getGuardianByStudentIdAndPhone(Integer studentId, String phone);

    @OQL("select c from WxUser c where c.openId = ?1")
    public abstract WxUser getWxUserByOpenId(String openId);

    @OQL("select c from WxStudent c where c.wxUser.openId = ?1 and c.studentId = ?2")
    public abstract WxStudent getWxStudentByOpenIdAndSutdentId(String openId, Integer studentId);

    /**
     * 根据微信openid和状态查询微信用户
     *
     * @param openId
     * @param status
     * @return
     */
    @OQL("select c from WxUser c where c.openId =?1 and c.phone =?2 and c.status=?3")
    public abstract WxUser getWxUserByOpenIdAndPhone(String openId, String phone, Integer status);
}
