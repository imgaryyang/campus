package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.face.FaceService;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生照片录入
 *
 * @author yiuman
 * @date 2018/3/26
 */
@Service
public class StudentFaceService extends BaseService{

    private Map<String, Object> dataMap;

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    @Service(url = "/wx/facepage")
    public String facePage() {
        initData();
        return "/safecampus/wx/personal/photo_auth.ptl";
    }

    private void initData() {
        if (dataMap == null)
            dataMap = new HashMap<>();
        List<Student> students = wxAuthDao.queryStudentByWxUserId(wxUserOnlineInfo.getUserId());
        dataMap.put("students", students);
    }


}
