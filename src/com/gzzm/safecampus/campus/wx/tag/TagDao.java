package com.gzzm.safecampus.campus.wx.tag;

import com.gzzm.safecampus.wx.user.IdentifyType;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.thunwind.annotation.GetByField;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author Neo
 * @date 2018/4/24 10:47
 */
public abstract class TagDao extends GeneralDao
{
    public TagDao()
    {
    }

    @GetByField({"openId", "status"})
    public abstract WxUser loadWxUser(String openId, Integer status) throws Exception;

    /**
     * 根据身份类型获取对应的标签
     *
     * @param identifyType 身份类型
     * @return 标签
     * @throws Exception 操作异常
     */
    @GetByField("identifyType")
    public abstract Tag loadTag(IdentifyType identifyType) throws Exception;
}
