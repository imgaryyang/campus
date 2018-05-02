package com.gzzm.safecampus.wx.personal;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.classes.Guardian;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * 解绑service
 *
 * @author yiuman
 * @date 2018/4/9
 */
@Service
public class UnBindService extends BaseService {

    public UnBindService() {
    }

    /**
     * 跳转解绑页面
     *
     * @return
     */
    @Service(url = "/wx/unbingpage")
    public String unBindPage() {
        return "/safecampus/wx/personal/unbind.ptl";
    }

    /**
     * 跳转更改状态页面
     *
     * @return
     */
    @Service(url = "/wx/statusPage")
    public String statusPage() {
        return "/safecampus/wx/personal/status.ptl";
    }

    /**
     * 修改电话号码
     *
     * @param phone
     * @param verifyCode
     * @return
     * @throws Exception
     */
    @Service
    public AuthResult changePhone(String phone, String verifyCode) throws Exception {
        AuthResult result = new AuthResult();
        HttpSession session = RequestContext.getContext().getSession();
        String captcha0 = (String) session.getAttribute("MOCAPTCHA:" + phone);
        if (captcha0 == null || !verifyCode.equals(captcha0) || StringUtils.isBlank(verifyCode)) {
            result.setStatus("error");
            result.setMsg("验证码错误");
            return result;
        }
        return changePhoneWithoutVeriCode(result,phone);
    }

    @Service
    @ObjectResult
    public AuthResult changePhoneWithoutVeriCode(AuthResult result, String phone) throws Exception {
        if(result==null){
            result = new AuthResult();
        }
        if(StringUtils.isBlank(phone) && phone.equals(wxUserOnlineInfo.getPhone())){
            result.setMsg("修改的手机号与当前手机号不能一致");
            result.setStatus("error");
            return result;
        }
        WxUser wxUser = wxAuthDao.getWxUserByOpenId(wxUserOnlineInfo.getOpenId());
        if (wxUser != null){
            wxUser.setPhone(phone);
            wxAuthDao.save(wxUser);
        }
        wxAuthDao.updateGuardianPhone(wxUserOnlineInfo.getPhone(), phone, wxUserOnlineInfo.getUserName());
        wxAuthDao.updateTeacherPhone(wxUserOnlineInfo.getPhone(), phone, wxUserOnlineInfo.getUserName());
        wxUserOnlineInfo.setPhone(phone);
        result.setMsg("修改成功");
        result.setStatus("success");
        return result;
    }




}
