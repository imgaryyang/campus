package com.gzzm.safecampus.wx.personal;

import com.gzzm.platform.commons.Sex;
import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.classes.Guardian;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.wx.user.IdentifyType;
import com.gzzm.safecampus.wx.user.WxUser;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 身份认证
 *
 * @author yiuman
 * @date 2018/3/21
 */
@Service
public class AuthenticationSerivce extends BaseService {


    @Service(url = "/wx/personal/toauthpage")
    public String toAuthPage() {
        return "/safecampus/wx/personal/authpage.ptl";
    }

    /**
     * 检查验证码及是否认证成功
     *
     * @param verifCode
     * @return
     */
    @Service
    @ObjectResult
    public AuthResult checkWxAuth(String verifCode, String phone, IdentifyType identifyType, String name, String idCard) throws Exception {
        RequestContext context = RequestContext.getContext();
        HttpSession session = context.getSession();
        //从session获取验证码
        String captcha0 = (String) session.getAttribute("MOCAPTCHA:" + phone);
        AuthResult result = new AuthResult();
        //检查验证码
        if (captcha0 == null || !verifCode.equals(captcha0) || StringUtils.isBlank(verifCode)) {
            result.setStatus("error");
            result.setMsg("验证码错误，请重新获取");
            return result;
        } else {
            return checkWxAuthWithVeriCode(result, phone, identifyType, name, idCard);
        }
    }

    /**
     * 非验证码认证
     *
     * @param result       返回结构
     * @param phone        电话
     * @param identifyType 身份类型
     * @param userName         教师名字
     * @param idCard       身份证号
     * @return
     * @throws Exception
     */
    @Service
    @ObjectResult
    public AuthResult checkWxAuthWithVeriCode(AuthResult result, String phone, IdentifyType identifyType, String userName, String idCard) throws Exception {
        if (result == null) result = new AuthResult();
        RequestContext context = RequestContext.getContext();
        HttpSession session = context.getSession();
        //从session获取微信用户
        WxMpUser wxMpUser = (WxMpUser) session.getAttribute(CampusContants.WXMPUSER_SESSIONNAME);
        //从session跳转的Url
        String url = (String) session.getAttribute(CampusContants.RECORD_URL);
        //从家属检查有有没与此微信号对应电话号码的家属
        WxUser wxUser = null;
        Guardian guardian = wxAuthDao.getGuardianByPhoneAndName(phone, userName);
        //如果是认证家属走这里
        if (identifyType == IdentifyType.GUARDIAN) {
            if (guardian == null) {
                result.setStatus("error");
                result.setMsg("认证失败，没在系统中找到相应的家长信息");
                return result;
            } else {
                wxUser = authIdentify(wxMpUser, null, guardian.getName(), guardian.getGuardianId(), phone, IdentifyType.GUARDIAN, idCard);
                addWxStudentIdentify(wxUser);
                tagService.taggingUser(wxUser.getOpenId());
            }
        } else {
            //如果是老师认证走这里
            if (StringUtils.isBlank(userName) || StringUtils.isBlank(idCard)) {
                result.setStatus("error");
                result.setMsg("老师姓名或身份证号不能为空");
                return result;
            }
            Teacher teacher = wxAuthDao.getTeacherByPhoneAndNameAndIdCard(phone, userName, idCard);
            if (teacher == null) {
                result.setStatus("error");
                result.setMsg("认证失败，没在系统中找到相应的教师信息");
                return result;
            } else {
                wxUser = authIdentify(wxMpUser, teacher.getDeptId(), teacher.getTeacherName(), teacher.getTeacherId(), phone, IdentifyType.TEACHER, idCard);
                addWxStudentIdentify(wxUser);
                tagService.taggingUser(wxUser.getOpenId());
            }
        }
        if(IdentifyType.TEACHER !=wxUser.getIdentifyType()){
            result.setUrl(CampusContants.FACEPAGE);
        }
        result.setStatus("success");
        //认证成功，自动登录
        wxLoginService.login(wxUser, context.getRequest(), context.getResponse());
        return result;
    }

    /**
     * 认证身份
     *
     * @param wxMpUser     微信用户
     * @param identifyid   身份ID
     * @param phone        认证电话
     * @param identifyType 身份类型
     * @throws Exception
     */
    public WxUser authIdentify(WxMpUser wxMpUser, Integer detpId, String userName, Integer identifyid, String phone, IdentifyType identifyType, String idCard) throws Exception {
        WxUser wxUser = wxAuthDao.getWxUserByOpenIdAndPhone(wxMpUser.getOpenId(), phone, null);
        if (wxUser == null) {
            wxUser = new WxUser();
        }
        Tools.log(wxMpUser.toString());
        wxUser.setAuthTime(new Date());
        wxUser.setUserName(userName);
        wxUser.setCity(wxMpUser.getCity());
        wxUser.setIdentifyId(identifyid);
        wxUser.setIdentifyType(identifyType);
        wxUser.setNickName(wxMpUser.getNickname());
        wxUser.setPhone(phone);
        wxUser.setDeptId(detpId);
        wxUser.setStatus(0);
        wxUser.setImgUrl(wxMpUser.getHeadImgUrl());
        wxUser.setOpenId(wxMpUser.getOpenId());
        wxUser.setProvince(wxMpUser.getProvince());
        if (wxMpUser.getSexId() == 1 || wxMpUser.getSexId() == 2) {
            wxUser.setSex(Sex.values()[wxMpUser.getSexId() - 1]);
        }
        wxUser.setIdCard(idCard);
        wxAuthDao.save(wxUser);
        return wxUser;
    }

    /**
     * 添加微信用户与学生的关联关系
     *
     * @param wxUser
     * @throws Exception
     */
    public void addWxStudentIdentify(WxUser wxUser) throws Exception {
        List<Student> studentList = wxAuthDao.queryStudentByGuardianPhone(wxUser.getPhone());
        if (CollectionUtils.isNotEmpty(studentList)) {
            if (IdentifyType.TEACHER == wxUser.getIdentifyType()) {
                wxUser.setIdentifyType(IdentifyType.BOTH);
                wxAuthDao.save(wxUser);
            }
            List<Integer> wxStudents = wxAuthDao.getWxStudentsByWxuserId(wxUser.getWxUserId());
            for (Student student : studentList) {
                if (wxStudents != null && wxStudents.contains(student.getStudentId())) continue;
                WxStudent wxStudent = new WxStudent();
                wxStudent.setBindTime(new Date());
                wxStudent.setStudent(student);
                wxStudent.setStudentId(student.getStudentId());
                wxStudent.setWxUserId(wxUser.getWxUserId());
                wxAuthDao.save(wxStudent);
            }
        }
    }

}
