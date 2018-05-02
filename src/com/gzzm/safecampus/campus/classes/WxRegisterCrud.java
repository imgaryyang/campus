package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.wx.personal.WxRegister;
import com.gzzm.safecampus.wx.personal.WxStudent;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.nest.annotation.Constant;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 微信注册Curd
 *
 * @author yiuman
 * @date 2018/4/13
 */
@Service(url = "/campus/classes/wxregistercrud")
public class WxRegisterCrud extends BaseCrud<WxRegister, Integer> {

    @Inject
    private WxRegisterDao dao;

    @Like
    private String userName;

    @Constant("phone")
    private String phone;

    /**
     * 用区分是否是已审核
     */
    private Integer flag;

    public WxRegisterCrud() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Override
    protected String getComplexCondition() throws Exception {
        String condition = "state = 0";
        if (flag != null) {
            condition = "state <> 0";
        }
        return super.getComplexCondition() == null ? condition : super.getComplexCondition() + " and " + condition;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("家长姓名", "userName");
        view.addComponent("电话","phone");
        view.addColumn("家长姓名", "userName");
        view.addColumn("身份证", "idCard");
        view.addColumn("电话", "phone");
        view.addColumn("孩子姓名", "studentName");
        view.addColumn("年级", "grade.gradeName");
        view.addColumn("班级", "classes.classesName");
        view.addButton(Buttons.query());
        if (flag != null) {
            view.addColumn("状态", new BaseSimpleCell() {
                @Override
                public Object getValue(Object o) throws Exception {
                    WxRegister register = (WxRegister) o;
                    if (register.getState() == 1) {
                        return "通过";
                    } else if (register.getState() == 2) {
                        return "不通过";
                    }
                    return "";
                }
            });
        } else {
            view.addButton("通过", "pass(function(){refresh()})");
            view.addButton("不通过", "noPass(function(){refresh()})");
        }
        return view;
    }


    /**
     * 通过  创建关系
     *
     * @throws Exception
     */
    @Service
    public void pass() throws Exception {
        Integer[] keys = getKeys();
        if (keys.length > 0) {
            for (Integer key : keys) {
                WxRegister register = getEntity(key);
                Student student = dao.getStudentByClsIdAndName(register.getClassesId(), register.getStudentName());
                Guardian guardian = dao.getGuardianByStudentIdAndPhone(student.getStudentId(), register.getPhone());
                if (guardian == null) {
                    guardian = new Guardian();
                    guardian.setPhone(register.getPhone());
                    guardian.setName(register.getUserName());
                    guardian.setIdCard(register.getIdCard());
                    guardian.setStudentId(student.getStudentId());
                    dao.save(guardian);
                }

                WxStudent wxStudent = dao.getWxStudentByOpenIdAndSutdentId(register.getOpenId(),student.getStudentId());
                if(wxStudent==null){
                    WxUser user = dao.getWxUserByOpenIdAndPhone(register.getOpenId(),register.getPhone(),0);
                    wxStudent = new WxStudent();
                    wxStudent.setBindTime(new Date());
                    wxStudent.setStudentId(student.getStudentId());
                    wxStudent.setWxUserId(user.getWxUserId());
                }
                dao.save(wxStudent);
                register.setState(1);
                dao.save(register);
            }
        }
    }

    /**
     * 不通过
     */
    @Service
    public void noPass() {
        Integer[] keys = getKeys();
        if (CollectionUtils.isNotEmpty(keys)) {
            dao.changeSateByKeys(keys);
        }
    }
}
