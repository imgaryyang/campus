package com.gzzm.safecampus.campus.wx.tag;

import com.gzzm.safecampus.campus.wx.WxMpServiceProvider;
import com.gzzm.safecampus.wx.user.IdentifyType;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.nest.annotation.Inject;

/**
 * 微信用户标签相关业务处理类
 *
 * @author Neo
 * @date 2018/4/23 11:15
 */
public class TagService
{
    @Inject
    private TagDao tagDao;

    public TagService()
    {
    }

    public Integer tagCreate(String tagName) throws Exception
    {
        return WxMpServiceProvider.getUserTagService().tagCreate(tagName).getId().intValue();
    }

    public boolean batchTagging(Integer tagId, String[] openIds) throws Exception
    {
        return WxMpServiceProvider.getUserTagService().batchTagging(Long.valueOf(tagId), openIds);
    }

    public boolean unBatchTagging(Integer tagId, String[] openIds) throws Exception
    {
        return WxMpServiceProvider.getUserTagService().batchUntagging(Long.valueOf(tagId), openIds);
    }

    /**
     * 给用户打标签
     * 1、用户认证成功后
     * 2、教师增加小孩后（认证为家长，双重身份）
     * 3、已认证用户重新关注后
     *
     * @param openId 用户Id
     * @throws Exception 操作异常
     */
    public void taggingUser(String openId) throws Exception
    {
        WxUser wxUser = tagDao.loadWxUser(openId, 0);
        if (wxUser != null)
        {
            IdentifyType identifyType = wxUser.getIdentifyType();
            Tag tag = tagDao.loadTag(identifyType);
            if (tag != null)
                batchTagging(tag.getTagId(), new String[]{openId});
        }
    }

    /**
     * 取消用户身份标签
     * 用户解除绑定时
     *
     * @param openId 用户Id
     * @throws Exception 操作异常
     */
    public void unTaggingUser(String openId) throws Exception
    {
        WxUser wxUser = tagDao.loadWxUser(openId, 0);
        if (wxUser != null)
        {
            IdentifyType identifyType = wxUser.getIdentifyType();
            Tag tag = tagDao.loadTag(identifyType);
            if (tag != null)
                unBatchTagging(tag.getTagId(), new String[]{openId});
        }
    }
}
