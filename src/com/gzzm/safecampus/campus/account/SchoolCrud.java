package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.PasswordUtils;
import com.gzzm.platform.commons.SystemMessageException;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import com.gzzm.safecampus.campus.service.ServiceDao;
import com.gzzm.safecampus.campus.service.ServiceInfo;
import com.gzzm.safecampus.campus.service.ServiceType;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.crud.CrudUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

import java.io.File;
import java.util.*;

/**
 * 学校账号创建
 *
 * @author yuanfang
 * @date 18-02-06 16:13
 */
@Service(url = "/campus/account/schoolcrud")
public class SchoolCrud extends BaseNormalCrud<School, Integer>
{
    @Inject
    private SchoolDao dao;

    @Inject
    private ServiceDao serviceDao;

    @Inject
    private SchoolService schoolService;

    @Like
    private String schoolName;

    private Merchant merchant;

    @Like
    private String contractMan;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private List<ServiceType> serviceTypes;

    private List<Integer> serviceIds;

    private DeptTreeModel deptTreeModel;

    private PageUserSelector userSelector;

    private Integer schoolUserId;

    /**
     * 学校级别查询条件
     */
    @Like("schoolLevel.levelName")
    private String levelName;

    /**
     * 学校等级选择下拉框
     */
    private SchoolLevelList schoolLevelList;

    public SchoolCrud()
    {
        addOrderBy("updateTime", OrderType.desc);
    }

    public String getSchoolName()
    {
        return schoolName;
    }

    public void setSchoolName(String schoolName)
    {
        this.schoolName = schoolName;
    }

    public Merchant getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
    }

    public List<ServiceType> getServiceTypes()
    {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes)
    {
        this.serviceTypes = serviceTypes;
    }

    public List<Integer> getServiceIds()
    {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds)
    {
        this.serviceIds = serviceIds;
    }

    public String getContractMan()
    {
        return contractMan;
    }

    public void setContractMan(String contractMan)
    {
        this.contractMan = contractMan;
    }

    public Integer getSchoolUserId()
    {
        return schoolUserId;
    }

    public void setSchoolUserId(Integer schoolUserId)
    {
        this.schoolUserId = schoolUserId;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    @Select(field = "entity.registerDeptId")
    public DeptTreeModel getDeptTreeModel() throws Exception
    {
        if (deptTreeModel == null)
        {
            deptTreeModel = Tools.getBean(DeptTreeModel.class);
            deptTreeModel.setRootId(userOnlineInfo.getDeptId(2));
        }
        return deptTreeModel;
    }


    @Select(field = "entity.registerId")
    public PageUserSelector getUserSelector() throws Exception
    {
        if (userSelector == null)
        {
            userSelector = Tools.getBean(PageUserSelector.class);
        }
        return userSelector;
    }

    @Select(field = "entity.levelId")
    public SchoolLevelList getSchoolLevelList() throws Exception
    {
        if (schoolLevelList == null)
            schoolLevelList = Tools.getBean(SchoolLevelList.class);
        return schoolLevelList;
    }

    public void setSchoolLevelList(SchoolLevelList schoolLevelList)
    {
        this.schoolLevelList = schoolLevelList;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        //只加载当前部门登记的学校
        return "registerDeptId=" + userOnlineInfo.getDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("学校名称", "schoolName");
        view.addComponent("学校级别", "levelName");
        view.addComponent("联系人", "contractMan");
        view.addColumn("学校名称", "schoolName");
        view.addColumn("学校级别", "schoolLevel.levelName").setWidth("80");
        view.addColumn("学生规模", "studentScale").setWidth("150");
        view.addColumn("联系人", "contractMan").setWidth("100");
        view.addColumn("联系电话", "phone").setWidth("120");
        view.addColumn("学校编号", "schoolCode").setWidth("200");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.addColumn("修改", new CImage("/platform/styles/plain/icons/edit.gif", "showSchool(${schoolId})")
                .setProperty("class", "crud-edit"));
        view.setOnDblClick("showSchool(${schoolId})");
        view.importJs("/safecampus/campus/account/school.js");
        view.importCss("/safecampus/campus/account/schoolcrud.css");
        return view;
    }

    @Service
    public String showSchool(Integer key) throws Exception
    {
        setEntity(getEntity(key));
        merchant = dao.getSchoolMerchant(key);
        initServiceInfo(getEntity().getServiceInfoList());
        return "/safecampus/campus/account/school.ptl";
    }

    @Override
    public void initEntity(School entity) throws Exception
    {
        super.initEntity(entity);
        merchant = new Merchant();
        initServiceInfo(null);
    }

    private void initServiceInfo(List<ServiceInfo> serviceInfoList) throws Exception
    {
        serviceTypes = serviceDao.getServiceTypeListByNoNull();
        if (serviceInfoList != null && serviceInfoList.size() > 0)
        {
            for (ServiceType serviceType : serviceTypes)
            {
                for (ServiceInfo serviceInfo : serviceType.getServiceInfoList())
                {
                    for (ServiceInfo service : serviceInfoList)
                    {
                        if (Objects.equals(serviceInfo.getServiceId(), service.getServiceId()))
                        {
                            serviceInfo.setChecked(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String add(String forward) throws Exception
    {
        super.add(forward);
        return "/safecampus/campus/account/school.ptl";
    }

    @Service(url = "/campus/account/schoolpassword/{$0}")
    public String changePassword(Integer userId)
    {
        setSchoolUserId(userId);
        return "/safecampus/campus/account/password.ptl";
    }

    @Service
    @ObjectResult
    public String updatePassword(String password) throws Exception
    {
        User user = new User();
        user.setUserId(getSchoolUserId());
        user.setPassword(PasswordUtils.hashPassword(password, user.getUserId()));
        dao.save(user);
        return "ok";
    }

    @Service
    @ObjectResult
    public String genLoginName(String userName)
    {
        return User.getSpell(userName);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreateTime(new Date());
        getEntity().setRegisterDate(DateUtils.getSQLDate());
        getEntity().setRegisterDeptId(userOnlineInfo.getDeptId());
        getEntity().setRegisterId(userOnlineInfo.getUserId());
        String schoolNum = dao.getSchoolMaxNum();
        if (StringUtils.isBlank(schoolNum))
        {
            schoolNum = "001";
        } else
        {
            if (Integer.valueOf(schoolNum) == 999)
            {
                throw new SystemMessageException("编号问题", "学校编号超过999，请联系管理员");
            }
            schoolNum = DataConvert.format(Integer.valueOf(schoolNum) + 1, "000");
        }
        getEntity().setSchoolNum(schoolNum);
        getEntity().setSchoolCode(DateUtils.toString(new Date(), "yyMMdd") + schoolNum);
        return super.beforeInsert();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        if (serviceIds != null && serviceIds.size() > 0)
        {
            for (Integer serviceId : serviceIds)
            {
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceId(serviceId);
                serviceInfos.add(serviceInfo);
            }
        }
        getEntity().setServiceInfoList(serviceInfos);
        return super.beforeSave();
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        User user = getEntity().getUser();
        user.setUserName(getEntity().getSchoolName());
        user.setPhone(getEntity().getPhone());
        if (isNew$())
        {
            user.setType(UserType.in);
            user.setAdminUser(false);
        }
        String password = user.getPassword();
        dao.save(user);
        School school = getEntity();
        school.setUserId(user.getUserId());
        if (isNew$() || school.getDeptId() == null || Null.Integer.equals(school.getDeptId()))
        {
            Dept dept = new Dept();
            dept.setDeptName(getEntity().getSchoolName());
            dept.setParentDeptId(Integer.valueOf(Tools.getMessage("campus.school.parentDeptId")));
            dept.setDeptLevel((byte) 0);
            Tools.getBean(DeptCrud.class).insert(dept);
            Dept.setUpdated();
            school.setUserId(user.getUserId());
            school.setDeptId(dept.getDeptId());
            dao.save(school);

            if (isNew$() && password != null)
            {
                user.setPassword(PasswordUtils.hashPassword(password, user.getUserId()));
                dao.save(user);
            }
            //新增用户，同时新增用户部门，用户的角色，用户的岗位
            UserDept userDept = new UserDept(user.getUserId(), dept.getDeptId());
            userDept.setOrderId(getOrderValue(true));
            userDept.setDefaultDept(true);
            dao.save(userDept);
            SchoolYear schoolYear = new SchoolYear();
            schoolYear.setStatus(true);
            schoolYear.setDeleteTag(0);
            schoolYear.setSchoolId(school.getSchoolId());
            schoolYear.setDeptId(school.getDeptId());
            schoolYear.setYear(DateUtils.getYear());
            schoolYear.setOrderId(CrudUtils.getOrderValue(6, false));
            dao.save(schoolYear);
        } else
        {
            Dept dept = new Dept();
            dept.setDeptName(getEntity().getSchoolName());
            dept.setDeptId(school.getDeptId());
            Tools.getBean(DeptCrud.class).update(dept);
            Dept.setUpdated();
        }
        List<Integer> roleIds = dao.getUserRoleByUser(getEntity().getUserId(), getEntity().getDeptId());
        if (serviceIds != null && serviceIds.size() > 0)
        {
            List<UserRole> userRoles = new ArrayList<>();
            List<ServiceInfo> serviceInfos = dao.getEntities(ServiceInfo.class, serviceIds);
            for (ServiceInfo serviceInfo : serviceInfos)
            {
                if (serviceInfo.getRoles() != null)
                {
                    for (Role role : serviceInfo.getRoles())
                    {
                        if (roleIds == null || !roleIds.contains(role.getRoleId()))
                        {
                            UserRole userRole =
                                    new UserRole(getEntity().getUserId(), getEntity().getDeptId(), role.getRoleId());
                            if (!userRoles.contains(userRole))
                                userRoles.add(userRole);
                        } else
                        {
                            roleIds.remove(role.getRoleId());
                        }
                    }
                }
            }
            dao.saveEntities(userRoles);
        }
        if (roleIds != null && roleIds.size() > 0)
        {
            List<UserRole> removeUserRoles = new ArrayList<>();
            for (Integer roleId : roleIds)
            {
                UserRole userRole = new UserRole(getEntity().getUserId(), getEntity().getDeptId(), roleId);
                removeUserRoles.add(userRole);
            }
            dao.deleteEntities(removeUserRoles);
        }
        if (isNew$())
        {
            schoolService.init(getEntity());
        }

        merchant.setSchoolId(getEntity().getSchoolId());
        merchant.setDeptId(getEntity().getDeptId());
        dao.save(merchant);
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        for(Integer key:getKeys())
        {
            Dept dept=getEntity(key).getDept();
            dept.setState((byte)1);
            Tools.getBean(DeptCrud.class).update(dept);
            Dept.setUpdated();
        }
        return super.beforeDeleteAll();
    }

    @Service(url = "/campus/account/schoolimage/{$0}")
    public byte[] getSchoolImage(Integer schoolId) throws Exception
    {
        if (schoolId == null)
            return new InputFile(
                    new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/organ.jpg"))).getBytes();
        School school = getEntity(schoolId);
        return school.getImage() == null ?
                new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/organ.jpg")))
                        .getBytes() : school.getImage();
    }

    //生成随机数字和字母,
    @Service
    @ObjectResult
    public String getStringRandom(int length)
    {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        //length为几位密码
        for (int i = 0; i < length; i++)
        {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum))
            {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else
            {
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }
}
