package com.gzzm.safecampus.wx.personal;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 我的信息
 *
 * @author Yiuman
 * @date 2018/4/27
 */
@Service
public class PersonalService extends BaseService {

    private Map<String, Object> dataMap;

    public PersonalService() {
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }


    /**
     * 我的信息页面
     *
     * @return
     */
    @Service(url = "/wx/pageInfoPage")
    public String pageInfoPage() {
        initData();
        return "/safecampus/wx/personal/person_info.ptl";
    }

    private void initData() {
        if (dataMap == null) dataMap = new HashMap<>();
        dataMap.put("userName", wxUserOnlineInfo.getUserName());
        dataMap.put("phone", wxUserOnlineInfo.getPhone());
        dataMap.put("identify", wxUserOnlineInfo.getIdentifyType().ordinal());
        dataMap.put("imgUrl", wxUserOnlineInfo.getImgUrl());
        dataMap.put("students", wxAuthDao.queryStudentByWxUserId(wxUserOnlineInfo.getUserId()));
    }

    /**
     * 解绑
     *
     * @return
     */
    @Transactional
    @Service
    public AuthResult unbind() {
        AuthResult result = new AuthResult();
        try {
            WxUser wxUser = wxAuthDao.getWxUserByOpenId(wxUserOnlineInfo.getOpenId());
            if (wxUser != null) {
                wxUser.setStatus(1);
                tagService.unTaggingUser(wxUser.getOpenId());
                wxAuthDao.save(wxUser);
            }
            wxLoginService.logout();
            result.setMsg("解绑成功");
            result.setStatus("success");
            return result;
        } catch (Exception e) {
            Tools.log(e);
            result.setMsg("解绑失败");
            result.setStatus("error");
            return result;
        }
    }

}
