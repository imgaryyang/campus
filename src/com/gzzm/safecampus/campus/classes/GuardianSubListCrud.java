package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.common.ScSubListCrud;
import com.gzzm.safecampus.campus.common.ScSubListView;
import com.gzzm.safecampus.wx.personal.WxAuthDao;
import com.gzzm.safecampus.wx.personal.WxStudent;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 学生详情下的家长信息管理
 *
 * @author yuanfang
 * @date 18-03-19 20:02
 */
@Service
public class GuardianSubListCrud extends ScSubListCrud<Guardian, Integer>
{
    @Inject
    private GuardianDao guardianDao;

    @Inject
    private WxAuthDao wxAuthDao;

    private Integer studentId;

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public GuardianSubListCrud()
    {
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    @Override
    protected String getParentField()
    {
        return "studentId";
    }

    @Override
    protected void initListView(ScSubListView view)
    {
        view.addColumn("姓名", "name").setWidth("80px");
        view.addColumn("身份证号", "idCard").setWidth("160px");
        view.addColumn("性别", "sex").setWidth("60px");
        view.addColumn("关系", "relationInfo").setWidth("60px");
        view.addColumn("联系电话", "phone");
    }

    @Override
    protected void initShowView(SimpleDialogView view)
    {
        view.setTitle("家属信息");
        view.addComponent("姓名", "name", true);
        view.addComponent("身份证号", "idCard");
        view.addComponent("性别", "sex");
        view.addComponent("关系", "relationInfo", true);
        view.addComponent("联系电话", "phone", true);
        view.addComponent("出生日期", "birthday");
        view.addComponent("住址", "address");
    }

    @Override
    public String show(Integer key, String forward) throws Exception
    {
        Guardian guardian = getEntity(key);
        if (guardian != null)
        {
            name = guardian.getName();
        }
        return super.show(key, forward);
    }

    @Override
    public void afterInsert() throws Exception
    {
        //新增家属信息到微信用户-学生关联表
        //根据手机查微信用户（家长）
        List<WxUser> wxUsers = wxAuthDao.getWxUserByPhone(getEntity().getPhone());
        if (CollectionUtils.isNotEmpty(wxUsers))
            for (WxUser wxUser : wxUsers)
            {
                wxAuthDao.save(new WxStudent(wxUser.getWxUserId(), studentId, new Date()));
            }
        super.afterSave();
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        Guardian guardian = getEntity();

        String old = guardianDao.getPhone(guardian.getGuardianId());
        if (!old.equals(guardian.getPhone()))
        {
            Tools.debug("修改手机号码，重新关联");
            List<WxUser> users = wxAuthDao.getWxUsers(guardian.getPhone());
            for (WxUser user : users)
            {
                Integer i = wxAuthDao.deleteUsersStudent(guardian.getStudentId(), user.getWxUserId());
                Tools.debug("更新删除微信关联学生：" + i+"/"+guardian.getStudentId());
            }
            List<WxUser> wxUsers = wxAuthDao.getWxUserByPhone(getEntity().getPhone());
            if (CollectionUtils.isNotEmpty(wxUsers))
                for (WxUser wxUser : wxUsers)
                {
                    wxAuthDao.save(new WxStudent(wxUser.getWxUserId(), guardian.getStudentId(), new Date()));
                }
        }
        return super.beforeUpdate();
    }

    //删除微信关联学生
    @Override
    public boolean beforeDelete(Integer key) throws Exception
    {
        Guardian guardian = getEntity(key);
      List<WxUser> users = wxAuthDao.getWxUsers(guardian.getPhone());
        for (WxUser user : users)
        {
            Integer i = wxAuthDao.deleteUsersStudent(guardian.getStudentId(), user.getWxUserId());
            Tools.debug("删除微信关联学生：" + i+"/"+guardian.getStudentId());
        }
        return super.beforeDelete(key);
    }
}