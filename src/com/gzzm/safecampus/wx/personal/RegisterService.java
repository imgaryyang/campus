package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.Grade;
import com.gzzm.safecampus.campus.classes.Guardian;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.wx.user.IdentifyType;
import com.gzzm.safecampus.wx.user.WxUser;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 注册认证
 *
 * @author yiuman
 * @date 2018/3/27
 */
@Service
public class RegisterService extends BaseService {

    private WxRegister wxRegister;

    @NotSerialized
    private List<School> schoolList;

    public RegisterService() {
    }

    public List<School> getSchoolList() throws Exception {
        if (schoolList == null) schoolList = wxAuthDao.getSchools();
        return schoolList;
    }

    public void setSchoolList(List<School> schoolList) {
        this.schoolList = schoolList;
    }

    public WxRegister getWxRegister() {
        return wxRegister;
    }

    public void setWxRegister(WxRegister wxRegister) {
        this.wxRegister = wxRegister;
    }

    @Service(url = "/wx/resgiterpage")
    public String registerPage() {
        return "/safecampus/wx/personal/register.ptl";
    }

    /**
     * 获取年级班级数据
     *
     * @param schoolId 学校ID
     * @param gradeId  年级ID
     * @return
     */
    @Service
    public Map<String, Object> gradeData(Integer schoolId, Integer gradeId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        if (gradeId == null) {
            List<Grade> gradeList = wxAuthDao.getGradeListBySchoolId(schoolId);
            for (Grade grade : gradeList) {
                Map<String, Object> data = new HashMap<>();
                data.put("key", grade.getGradeId());
                data.put("name", grade.getGradeName());
                list.add(data);
            }
        } else {
            //根据年级Id获取班级列表
            List<Classes> classesList = wxAuthDao.getClassesByGradeId(gradeId);
            for (Classes cls : classesList) {
                Map<String, Object> data = new HashMap<>();
                data.put("key", cls.getClassesId());
                data.put("name", cls.getClassesName());
                list.add(data);
            }
        }
        map.put("items", list);
        return map;
    }


    @Service
    @ObjectResult
    public AuthResult register(String verifCode) throws Exception {
        if (wxRegister != null) {
            HttpSession session = RequestContext.getContext().getSession();
            //从session获取验证码
            String captcha0 = (String) session.getAttribute("MOCAPTCHA:" + wxRegister.getPhone());
            WxMpUser wxMpUser = (WxMpUser) session.getAttribute(CampusContants.WXMPUSER_SESSIONNAME);

            AuthResult result = new AuthResult();
            if (captcha0 == null || !verifCode.equals(captcha0) || StringUtils.isBlank(verifCode) || wxRegister == null) {
                result.setStatus("error");
                result.setMsg("验证码错误，请重新获取");
                return result;
            } else {
                return registerWithVeriCode(result);
            }
        }
        return null;
    }

    @Service
    @ObjectResult
    public AuthResult registerWithVeriCode(AuthResult result) throws Exception {
        if (wxRegister == null) return null;
        if (result == null) result = new AuthResult();
        HttpSession session = RequestContext.getContext().getSession();
        WxMpUser wxMpUser = (WxMpUser) session.getAttribute(CampusContants.WXMPUSER_SESSIONNAME);
        //根据信息找到学生
        Student student = wxAuthDao.queryStudentBySidGidCidAndName(wxRegister.getClassesId(), wxRegister.getStudentName());

        if (student == null) {
            result.setMsg("没有找到学生信息");
            result.setStatus("error");
            return result;
        }
        //判断是否登录关联
        if (wxUserOnlineInfo != null && wxUserOnlineInfo.getOpenId() != null) {
            //登录了找到微信用户
            WxUser wxUser = wxAuthDao.get(WxUser.class, wxUserOnlineInfo.getUserId());

            //如果有此学生信息 则根据微信用户与学生  查找微信用户与学生关系表是否存在数据
            WxStudent wxStudent = wxAuthDao.getWxUserByUserIdAndStudenId(wxUser.getWxUserId(), student.getStudentId());
            if (wxStudent != null) {
                result.setStatus("error");
                result.setMsg("已经存在关联关系");
                return result;
            } else {
                Guardian guardian = wxAuthDao.getGuardian(student.getStudentId(), wxRegister.getPhone());
                if (guardian != null) {
                    MakeRelation(result, student.getStudentId(), wxUser.getWxUserId());
                    return result;
                }
                checkRegsiter(result, wxAuthDao, wxUserOnlineInfo.getOpenId());
                return result;
            }
        } else {
            //没登录的时候
            //查看有没家属信息
            Guardian guardian = wxAuthDao.getGuardian(student.getStudentId(), wxRegister.getPhone());
            if (guardian != null) {
                result.setStatus("error");
                result.setMsg("已经存在关联关系，请前往认证");
                return result;
            }
            checkRegsiter(result, wxAuthDao, wxMpUser.getOpenId());
            return result;
        }
    }

    /**
     * 创建关系
     *
     * @param result    返回结构
     * @param studentId 学生ID
     * @param wxUserId  微信用户ID
     * @throws Exception
     */
    private void MakeRelation(AuthResult result, Integer studentId, Integer wxUserId) throws Exception {
        WxStudent wxStudent;
        wxStudent = new WxStudent();
        wxStudent.setBindTime(new Date());
        wxStudent.setStudentId(studentId);
        wxStudent.setWxUserId(wxUserId);
        wxAuthDao.save(wxStudent);
        WxUser wxUser = wxAuthDao.get(WxUser.class, wxUserId);
        if (IdentifyType.TEACHER == wxUser.getIdentifyType()) {
            wxUser.setIdentifyType(IdentifyType.BOTH);
            wxAuthDao.save(wxUser);
            tagService.taggingUser(wxUser.getOpenId());
        }
        result.setStatus("success");
        result.setUrl(CampusContants.PERCENTER_URL);
        result.setMsg("关联成功");
    }

    /**
     * 检查有没注册信息
     *
     * @param result
     * @param wxAuthDao
     * @throws Exception
     */
    private void checkRegsiter(AuthResult result, WxAuthDao wxAuthDao, String openId) throws Exception {
        //如果没有此学生信息 则查看注册记录
        WxRegister wx = wxAuthDao.getWxRegisterByPhoneAndIdCard(wxRegister.getPhone(), wxRegister.getIdCard(), wxRegister.getSchoolId(), wxRegister.getStudentName());
        if (wx != null) {
            result.setStatus("error");
            result.setMsg("已经注册过此信息，请耐心等待审核");
        } else {
            //如无记录则添加注册记录
            wxRegister.setDeptId(wxAuthDao.getDeptIdBySchoolId(wxRegister.getSchoolId()));
            wxRegister.setState(0);
            wxRegister.setOpenId(openId);
            wxAuthDao.save(wxRegister);
            result.setStatus("error");
            result.setMsg("注册成功，请等待审核");
        }
    }

}
