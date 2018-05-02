package com.gzzm.safecampus.wx.common;

import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author Neo
 * @date 2018/4/2 18:43
 */
public class WxService
{
    @Inject
    private WxDao dao;

    public WxService()
    {
    }

    public WxDao getDao()
    {
        return dao;
    }

    /**
     * 获取某个学生被哪些微信用户绑定了
     *
     * @param studentId 学生Id
     * @return 微信用户Id
     * @throws Exception 操作异常
     */
    public List<WxUser> getWxUsersByStudentId(Integer studentId) throws Exception
    {
        return dao.getWxUsersByStudentId(studentId);
    }
}
