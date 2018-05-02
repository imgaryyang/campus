package com.gzzm.safecampus.wx.common;

import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.thunwind.annotation.GetByKey;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author Neo
 * @date 2018/4/2 18:44
 */
public abstract class WxDao extends GeneralDao
{
    public WxDao()
    {
    }

    @OQL("select s.wxUser from WxStudent s where s.studentId=:1 and (s.wxUser.status = 0 or s.wxUser.status is null)")
    public abstract List<WxUser> getWxUsersByStudentId(Integer studentId) throws Exception;

    @GetByKey
    public abstract WxUser getWxUser(Integer wxUserId) throws Exception;
}
