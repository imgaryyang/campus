package com.gzzm.safecampus.device.card;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.LoginService;
import com.gzzm.platform.organ.*;
import com.gzzm.safecampus.campus.account.SchoolYear;
import com.gzzm.safecampus.campus.classes.*;
import com.gzzm.safecampus.campus.device.*;
import com.gzzm.safecampus.device.card.common.*;
import com.gzzm.safecampus.device.card.common.XMLUtils;
import com.gzzm.safecampus.device.card.dao.CommonDao;
import com.gzzm.safecampus.device.card.dto.*;
import com.gzzm.safecampus.device.card.entity.*;
import com.gzzm.safecampus.device.card.entity.CardType;
import com.gzzm.safecampus.device.card.enumtype.ParamType;
import com.gzzm.safecampus.device.card.service.DeviceService;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class AttendanceService
{
    private String oldCode;
    @Inject
    LoginService loginService;

    @Inject
    DeviceService deviceService;

    @Inject
    CommonDao commonDao;
    private String andGetSYSData;

    @Inject
    private OrganDao organDao;

    public AttendanceService()
    {
    }

    public String getOldCode()
    {
        return oldCode;
    }

    public void setOldCode(String oldCode)
    {
        this.oldCode = oldCode;
    }

    /**
     * 返回通讯秘钥key
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String getKey(String xml) throws Exception
    {
        CodeModel.ErpCodeReq bean = XMLUtils.readXML(xml, CodeModel.ErpCodeReq.class);
        setErpCode(bean);
        CodeModel.ErpCodeRes resBean = XMLUtils.createResBean(bean, CodeModel.ErpCodeRes.class);
        resBean.setERPKey(Tools.getMessage("device.card.key"));
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 用户登录
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String userLogin(String xml) throws Exception
    {
        LoginModel.ReqModel model = XMLUtils.readXML(xml.replaceAll("op_user", "opuser"), LoginModel.ReqModel.class);
        if (!Utils.checkBean(model)) return Utils.createError();
        setErpCode(model);

        LoginModel.ResModel resModel = XMLUtils.createResBean(model, LoginModel.ResModel.class);
        if (StringUtils.isEmpty(model.getPassWord()))
        {
            resModel.setResult("-1");
            resModel.setError("请输入用户名和密码！");
            return XMLUtils.response(resModel);
        }
        User user = loginService.passwordLogin(model.getOpuser(), model.getPassWord(), false, UserType.in);

        //设置公司名称-建显示在卡务系统左上角
        resModel.setCompanyName(Tools.getMessage("device.card.companyName"));
        //设置网卡mac控制客户端是否可用
        resModel.setNetMark(model.getNetMark());
        //权限列表
        resModel.setRight(Tools.getMessage("device.card.right"));
        if (user == null)
        {
            resModel.setResult("-1");
            resModel.setError("用户名或者密码不正确");
            Utils.initBean(resModel);
            resCode(resModel);
            return XMLUtils.toXML(resModel);
        }
        resCode(resModel);
        return XMLUtils.response(resModel);
    }

    /**
     * 卡类型
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String cardTypeConfig(String xml) throws Exception
    {
        CardTypeModel.ReqModel bean = XMLUtils.readXML(xml, CardTypeModel.ReqModel.class);
        if (!Utils.checkBean(bean)) return Utils.createError();
        setErpCode(bean);

        CardTypeModel.ResModel resBean = XMLUtils.createResBean(bean, CardTypeModel.ResModel.class);
        List<CardType> allEntities = commonDao.getAllEntities(CardType.class);
        if (bean.getActionCode().equals("W") && bean.getRecordList().size() > 0)//如果是添加卡资料
        {
            List<CardType> cardTypeList = new ArrayList<CardType>(bean.getRecordList().size());
            for (CardTypeModel.Record record : bean.getRecordList())
            {
                CardType cardType =
                        ReflectionUtils.copyBeanValue(CardType.class, record, CardTypeModel.Record.class);
                if (cardType.getCode() == null) return Utils.createError("必须输入卡片类型！");
                cardTypeList.add(cardType);
            }
            commonDao.saveEntities(cardTypeList);
        }
        else
        {
            if (allEntities != null && allEntities.size() > 0)
                ReflectionUtils
                        .setFiledValue(resBean, "RecordList", allEntities, CardTypeModel.Record.class, CardType.class);
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String cardFormat(String xml) throws Exception
    {
        CardFormatModel.ReqModel bean = XMLUtils.readXML(xml, CardFormatModel.ReqModel.class);
        if (!Utils.checkBean(bean)) return Utils.createError();
        setErpCode(bean);

        CardFormatModel.ResModel resBean = XMLUtils.createResBean(bean, CardFormatModel.ResModel.class);
        if (bean.getActionCode().equals("W") && bean.getRecordList().size() > 0)//如果是添加卡资料
        {
            saveEntity(CardFormat.class, bean.getRecordList());
        }
        else//如果是获取卡片资料
        {
            pushDateToResponse(CardFormat.class, CardFormatModel.Record.class, resBean);
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 1.0卡设置
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String cardParamV1(String xml) throws Exception
    {
        CardParamModel.ReqModel bean = XMLUtils.readXML(xml, CardParamModel.ReqModel.class);
        if (!Utils.checkBean(bean)) return Utils.createError();
        setErpCode(bean);
        CardParamModel.ResModel resBean = XMLUtils.createResBean(bean, CardParamModel.ResModel.class);
        resBean.setResult("0");
        resBean.setError("OK");
        if ("W".equals(bean.getActionCode()))
        {
            CardParam param = new CardParam();
            param.setSector(bean.getSector());
            param.setCardParam(bean.getCardParam());
            param.setParamType(ParamType.V1);
            commonDao.save(param);
        }
        else
        {
            CardParam cardParamOne = commonDao.getCardParamOne(ParamType.V1.ordinal());
            if (cardParamOne == null)
            {
                resBean.setSector("1");
                resBean.setCardParam(null);
//                resBean.setResult("-1");
//                resBean.setError("无法获取卡设置参数");
            }
            else
            {
                resBean.setSector(cardParamOne.getSector());
                resBean.setCardParam(cardParamOne.getCardParam());
            }
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 提交卡片应用密钥
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String sendCardKeys(String xml) throws Exception
    {
        SendCardKeysModel.ReqModel reqbean = XMLUtils.readXML(xml, SendCardKeysModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError();
        setErpCode(reqbean);
        CardKeys cardKeys = ReflectionUtils.copyBeanValue(CardKeys.class, reqbean, SendCardKeysModel.ReqModel.class);
        SendCardKeysModel.ResModel resBean = XMLUtils.createResBean(reqbean, SendCardKeysModel.ResModel.class);
        try
        {
            commonDao.save(cardKeys);
        }
        catch (Exception e)
        {
            resBean.setResult("-1");
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 地区代码设置
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String areaConfigModel(String xml) throws Exception
    {
        AreaConfigModel.ReqModel reqbean = XMLUtils.readXML(xml, AreaConfigModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError();
        setErpCode(reqbean);
        AreaConfigModel.ResModel resBean = XMLUtils.createResBean(reqbean, AreaConfigModel.ResModel.class);
        if (reqbean.getActionCode().equals("W") && reqbean.getRecordList().size() > 0)//如果是添加卡资料
        {
            saveEntity(AreaConfig.class, reqbean.getRecordList());
        }
        else//如果是获取卡片资料
        {
            pushDateToResponse(AreaConfig.class, AreaConfigModel.Record.class, resBean);
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 卡应用(钱包)类型CardAppType
     *
     * @param xml
     * @return
     */
    public String cardAppType(String xml) throws Exception
    {
        CardAppTypeModel.ReqModel reqbean = XMLUtils.readXML(xml, CardAppTypeModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError();
        setErpCode(reqbean);
        CardAppTypeModel.ResModel resBean = XMLUtils.createResBean(reqbean, CardAppTypeModel.ResModel.class);
        if (reqbean.getActionCode().equals("W"))
            saveEntity(CardAppType.class, reqbean.getRecordList());
        else
            pushDateToResponse(CardAppType.class, CardAppTypeModel.Record.class, resBean);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 获取部门信息
     *
     * @param xml
     * @return
     */
    public String getDeparts(String xml) throws Exception
    {
        DeptModel.ReqModel reqbean = XMLUtils.readXML(xml, DeptModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("token 认证失败");
        setErpCode(reqbean);
        List<DeptDto> childDeptDto = commonDao.getDeptTree(Integer.parseInt(reqbean.getERPCode()));
        DeptModel.ResModel resBean = XMLUtils.createResBean(reqbean, DeptModel.ResModel.class);
        pushDateToResponse(childDeptDto, DeptDto.class, DeptModel.Record.class, resBean);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 获取员工信息
     *
     * @param xml
     * @return
     */
    public String getEmpList(String xml) throws Exception
    {
        EmpListModel.ReqModel reqbean = XMLUtils.readXML(xml, EmpListModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("token 认证失败");
        setErpCode(reqbean);
        List<EmpDto> empList = commonDao
                .getEmpList(Integer.parseInt(reqbean.getERPCode()), reqbean.getDepart_id(), reqbean.getEmp_id(),
                        reqbean.getEmp_fname(), reqbean.getMobilePhone(), reqbean.getId_card(), reqbean.getHadCard());
        EmpListModel.ResModel resBean = XMLUtils.createResBean(reqbean, EmpListModel.ResModel.class);
        pushDateToResponse(empList, EmpDto.class, EmpListModel.Record.class, resBean);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 获取部门信息
     *
     * @param xml
     * @return
     */
    public String getCardID(String xml) throws Exception
    {
        CardNoModel.ReqModel reqbean = XMLUtils.readXML(xml, CardNoModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("token 认证失败");
        setErpCode(reqbean);
        if (!deviceService.chechNetMark(reqbean.getNetMark())) return Utils.createError("此电脑没有权限进行此操作");
        CardNoModel.ResModel resBean = XMLUtils.createResBean(reqbean, CardNoModel.ResModel.class);
        resBean.setCard_ID(deviceService.getCardNo(reqbean));
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 获取部门信息
     *
     * @param xml
     * @return
     */
    public String getEmpCardList(String xml) throws Exception
    {
        EmpCardModel.ReqModel reqbean = XMLUtils.readXML(xml, EmpCardModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        EmpCardModel.ResModel resBean = XMLUtils.createResBean(reqbean, EmpCardModel.ResModel.class);
        //如果获取学生
        String emp_id = reqbean.getEmp_id();
        String[] split = emp_id.split("_");
        if ("0".equals(split[0]))//教师卡
        {
            Integer teacherId = Integer.valueOf(split[1]);
            Teacher teacher = commonDao.load(Teacher.class, teacherId);
            List<DeviceCard> deviceCardList =
                    commonDao.getDeviceCardList(Integer.valueOf(reqbean.getERPCode()), teacherId);
            List<EmpCardModel.Record> listBean =
                    ReflectionUtils.createListBean(EmpCardModel.Record.class, deviceCardList, DeviceCard.class,
                            new OptionInterface<EmpCardModel.Record, Teacher>()
                            {
                                @Override
                                public void pushBefore(EmpCardModel.Record bean, Teacher data)
                                {
                                    if (data != null && !StringUtils.isEmpty(data.getPhone()))
                                        bean.setMobilePhone(data.getPhone());
                                }
                            }, teacher);
            resBean.setRecordList(listBean);
        }//学生卡
        else
        {
            Integer studentId = null;
            if (split.length == 1) studentId = Integer.valueOf(split[0]);
            else
                studentId = Integer.valueOf(split[1]);
            commonDao.getDeviceCardList(Integer.valueOf(reqbean.getERPCode()), studentId);
            Student student = commonDao.load(Student.class, studentId);
            List<DeviceCard> deviceCardList =
                    commonDao.getDeviceCardList(Integer.valueOf(reqbean.getERPCode()), studentId);
            List<EmpCardModel.Record> listBean =
                    ReflectionUtils.createListBean(EmpCardModel.Record.class, deviceCardList, DeviceCard.class,
                            new OptionInterface<EmpCardModel.Record, Student>()
                            {
                                @Override
                                public void pushBefore(EmpCardModel.Record bean, Student data)
                                {
                                    if (data != null && !StringUtils.isEmpty(data.getPhone()))
                                        bean.setMobilePhone(data.getPhone());
                                }
                            }, student);
            resBean.setRecordList(listBean);
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 获取员工信息
     *
     * @param xml
     * @return
     */
    public String getEmpInfo(String xml) throws Exception
    {
        EmpInfoModel.ReqModel reqbean = XMLUtils.readXML(xml, EmpInfoModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        EmpInfoModel.ResModel resBean = XMLUtils.createResBean(reqbean, EmpInfoModel.ResModel.class);
        //如果获取学生
        String emp_id = reqbean.getEmp_id();
        String[] split = emp_id.split("_");
        String targetId = split[split.length - 1];
        Integer shcoolId = Integer.parseInt(reqbean.getERPCode());
        SchoolYear schoolYear = commonDao.getSchoolYearBydeptId(shcoolId);
        if (schoolYear == null) return Utils.createError("获取学年信息失败");
        if ("0".equals(split[0]))//教师卡
        {
            Teacher teacher = commonDao.load(Teacher.class, Integer.valueOf(targetId));
            resBean.setEmp_fname(teacher.getTeacherName());
            resBean.setIDType("0");
            resBean.setDepart_id(reqbean.getEmp_id());
        }
        else
        {
            Student student = commonDao.findStudent(shcoolId, targetId);
            IDType iDtype = commonDao.findIDtype();
            Job job = commonDao.findJob();
            GroupInfo groupInfo = commonDao.findGroupInfo();
            if (iDtype != null)
                resBean.setIDType("" + iDtype.getTypeNo());
            if (groupInfo != null)
                resBean.setGroup_id(groupInfo.getGroupNo());
            if (job != null)
                resBean.setJob_id(job.getJobId());
            if (student == null) return XMLUtils.response(resBean);
            resBean.setEmp_fname(student.getStudentName());
            resBean.setDepart_id("" + student.getDeptId());
            if (student.getSex().equals(Sex.male))
            {
                resBean.setSex("男");
            }
            else
            {
                resBean.setSex("女");
            }
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 获取卡信息
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String getCardInfo(String xml) throws Exception
    {
        CardInfoModel.ReqModel reqbean = XMLUtils.readXML(xml, CardInfoModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        CardInfoModel.ResModel resBean = XMLUtils.createResBean(reqbean, CardInfoModel.ResModel.class);
        CardInfo card = deviceService.findCard(reqbean.getCard_id());
        DeviceCard deviceCard = deviceService.findDeviceCard(reqbean.getCard_id());
        if (card == null || deviceCard == null)
        {
            resBean.setResult("-1");
            resBean.setError("卡号不存在！");
        }
        else
        {
            if (card.getCardType().equals(com.gzzm.safecampus.campus.device.CardType.STUDENT))
            {
                Integer deptId = Integer.parseInt(reqbean.getERPCode());
                resBean.setEmp_id(card.getCardType().ordinal() + "_" + deviceCard.getStudent().getStudentNo());
            }
            resBean.setvQueryPsw(card.getPassWord());
            resBean.setUseFlag("0");
            switch (deviceCard.getStatus())
            {
                //未激活
                case UNACTIVATED:
                    resBean.setCardState("0");
                    resBean.setUseFlag("0");
                    resBean.setResult("-1");
                    break;
                //已激活
                case ACTIVE:
                    resBean.setCardState("1");
                    resBean.setUseFlag("1");
                    break;
                // 已停用、挂失后为此状态
                case EXPIRED:
                    resBean.setCardState("2");
                    resBean.setUseFlag("1");
                    break;
                // 已退卡
                case RETIRED:
                    resBean.setCardState("3");
                    resBean.setUseFlag("1");
                    break;
                //已补办、补卡后。老卡状态为已补办
                case DISABLED:
                    resBean.setCardState("5");
                    resBean.setUseFlag("1");
                    break;
                //已过期
                case REFUNDED:
                    resBean.setCardState("4");
                    resBean.setUseFlag("1");
                    break;
            }
            List<CardItem> cardItems = deviceService.findCardItems(card.getCardId());
            List<CardInfoModel.Record> records =
                    ReflectionUtils.createListBean(CardInfoModel.Record.class, cardItems, CardItem.class,
                            new OptionInterface<CardInfoModel.Record, CardInfo>()
                            {
                                @Override
                                public void pushBefore(CardInfoModel.Record bean, CardInfo data)
                                {
                                    if (StringUtils.isEmpty(bean.getLastDealTime()))
                                        bean.setLastDealTime(DateUtils.toString(new Date(), "yyyyMMddHHmmSS"));
                                }
                            }, card);
            resBean.setRecordList(records);
        }
        // resBean.setResult("-1");
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    private <E, T> boolean saveEntity(Class<E> entity, List<T> records) throws Exception
    {
        return saveEntity(entity, records, null);
    }

    private <E, T> boolean saveEntity(Class<E> entityClass, List<T> records, CheckEntity checkEntity) throws Exception
    {
        List<E> entitys = new ArrayList<E>();
        if (records == null || records.size() == 0) return false;
        for (T record : records)
        {
            E entity = ReflectionUtils.copyBeanValue(entityClass, record, record.getClass());
            if (checkEntity != null && !checkEntity.checkEntity(entity)) return false;
            entitys.add(entity);
        }
        commonDao.saveEntities(entitys);
        return true;
    }

    private <E, T> void pushDateToResponse(Class<E> entity, Class<T> recordClass, Object resBean) throws Exception
    {
        List<E> allEntities = commonDao.getAllEntities(entity);
        if (allEntities != null && allEntities.size() > 0)
            ReflectionUtils.setFiledValue(resBean, "RecordList", allEntities, recordClass, entity);
        return;
    }

    private <E, T> void pushDateToResponse(List<E> listEntitys, Class<E> entity, Class<T> recordClass, Object resBean)
            throws Exception
    {
        if (listEntitys != null && listEntitys.size() > 0)
            ReflectionUtils.setFiledValue(resBean, "RecordList", listEntitys, recordClass, entity);
        return;
    }

    public String getCardTypeFromSN(String xml) throws Exception
    {
        CardTypeFromSNModel.ReqModel reqbean = XMLUtils.readXML(xml, CardTypeFromSNModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        CardTypeFromSNModel.ResModel resBean = XMLUtils.createResBean(reqbean, CardTypeFromSNModel.ResModel.class);
        DeviceCard card = deviceService.findDeviceCardBySn(reqbean.getCard_sn());
        resBean.setCard_id(card.getCardNo());
        if (card.getType().equals(com.gzzm.safecampus.campus.device.CardType.STUDENT))
        {
            Student load = commonDao.load(Student.class, card.getTargetId());
            resBean.setEmp_id("1_" + load.getStudentNo());
        }
        resBean.setCardType("4");
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String getCardSNState(String xml) throws Exception
    {
        CardSNStateModel.ReqModel reqbean = XMLUtils.readXML(xml, CardSNStateModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        CardSNStateModel.ResModel resBean = XMLUtils.createResBean(reqbean, CardSNStateModel.ResModel.class);
        DeviceCard deviceCard = deviceService.findDeviceCardBySn(reqbean.getCard_sn());
        resBean.setState("0");
        if (deviceCard != null)
        {
            switch (deviceCard.getStatus())
            {
                case EXPIRED:
                    resBean.setState("1");
            }
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String getPost(String xml) throws Exception
    {
        JobModel.ReqModel reqbean = XMLUtils.readXML(xml, JobModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        JobModel.ResModel resBean = XMLUtils.createResBean(reqbean, JobModel.ResModel.class);
        pushDateToResponse(Job.class, JobModel.Record.class, resBean);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String getIDInfo(String xml) throws Exception
    {
        IDTypeModel.ReqModel reqbean = XMLUtils.readXML(xml, IDTypeModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        IDTypeModel.ResModel resBean = XMLUtils.createResBean(reqbean, IDTypeModel.ResModel.class);
        pushDateToResponse(IDType.class, IDTypeModel.Record.class, resBean);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String getGroupInfo(String xml) throws Exception
    {
        GroupInfoModel.ReqModel reqbean = XMLUtils.readXML(xml, GroupInfoModel.ReqModel.class);
        if (!Utils.checkBean(reqbean)) return Utils.createError("tooken 认证失败");
        setErpCode(reqbean);
        GroupInfoModel.ResModel resBean = XMLUtils.createResBean(reqbean, GroupInfoModel.ResModel.class);
        pushDateToResponse(GroupInfo.class, GroupInfoModel.Record.class, resBean);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 1.0卡设置
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public String cardParamV2(String xml) throws Exception
    {
        CardParamV2Model.ReqModel bean = XMLUtils.readXML(xml, CardParamV2Model.ReqModel.class);
        if (!Utils.checkBean(bean)) return Utils.createError();
        setErpCode(bean);
        CardParamV2Model.ResModel resBean = XMLUtils.createResBean(bean, CardParamV2Model.ResModel.class);
        if ("W".equals(bean.getActionCode()))
        {
            CardParam param = new CardParam();
            param.setSector(bean.getDataKey());
            param.setCardParam(bean.getCardParam());
            param.setParamType(ParamType.V2);
            commonDao.save(param);
        }
        else
        {
            CardParam cardParamOne = commonDao.getCardParamOne(ParamType.V2.ordinal());
            if (cardParamOne== null)
            {
//                resBean.setResult("-1");
//                resBean.setError("无法获取卡设置参数");
                resBean.setDataKey("ERCARDKY");
                resBean.setCardParam(null);
            }
            else
            {
                resBean.setDataKey(cardParamOne.getSector());
                resBean.setCardParam(cardParamOne.getCardParam());
            }
        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String SYSData(String xml) throws Exception
    {
        SYSDataModel.ReqModel reqModel = XMLUtils.readXML(xml, SYSDataModel.ReqModel.class);
        if (!Utils.checkBean(reqModel)) return Utils.createError();
        setErpCode(reqModel);

        SYSDataModel.ResModel resBean = XMLUtils.createResBean(reqModel, SYSDataModel.ResModel.class);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    @Transactional
    public String addCardInfo(String xml) throws Exception
    {
        AddCardInfoModel.ReqModel reqModel = XMLUtils.readXML(xml, AddCardInfoModel.ReqModel.class);
        if (!Utils.checkBean(reqModel)) return Utils.createError();
        setErpCode(reqModel);
        String emp_id = reqModel.getEmp_id();
        String[] split = emp_id.split("_");
        if ("0".equals(split[0]))//教师类型
        {

        }
        else
        {
            //卡号信息
            CardInfo card = deviceService.findCard(reqModel.getCard_id());
            //小白卡信息
            DeviceCard deviceCard = deviceService.findDeviceCard(reqModel.getCard_id());
            boolean check = deviceService.checkDeviceCard(reqModel.getCard_sn());
            if (!check)
            {
                AddCardInfoModel.ResModel resBean = XMLUtils.createResBean(reqModel, AddCardInfoModel.ResModel.class);
                resBean.setResult("-1");
                resBean.setError("此卡已经被使用！");
                resCode(resBean);
                return XMLUtils.response(resBean);
            }
            Integer shcoolId = Integer.parseInt(reqModel.getERPCode());
            //学年
            SchoolYear schoolYear = commonDao.getSchoolYearBydeptId(shcoolId);
            //获得学号
            String studentNo = split[split.length - 1];
            String card_id = reqModel.getCard_id();
            Student sutdent = commonDao.findOne(shcoolId, studentNo);
            card.setCardSn(reqModel.getCard_sn());
            deviceCard.setCardSn(reqModel.getCard_sn());
            deviceCard.setStatus(CardStatus.ACTIVE);
            deviceCard.setReleaseTime(DateUtils.toSQLDate(new Date()));
            Date parse = DateUtils.getDateFormat("yyyy-MM-dd HH:mm:ss").parse(reqModel.getCardEndDate());
            deviceCard.setExpireTime(DateUtils.toSQLDate(parse));
            CardInfoDetailed detailed =
                    ReflectionUtils.copyBeanValue(CardInfoDetailed.class, reqModel, AddCardInfoModel.ReqModel.class);
            List<CardPackLog> listBean = ReflectionUtils
                    .createListBean(CardPackLog.class, reqModel.getRecordList(), AddCardInfoModel.Record.class,
                            new OptionInterface<CardPackLog, DeviceCard>()
                            {
                                @Override
                                public void pushBefore(CardPackLog bean, DeviceCard data)
                                {
                                    bean.setCardNo(data.getCardNo());
                                    bean.setCardSq(data.getCardSn());
                                }
                            }, deviceCard);

            detailed.setCardId(card.getCardId());
            commonDao.save(detailed);
            commonDao.saveEntities(listBean);
            commonDao.save(deviceCard);
            commonDao.save(card);

        }
        AddCardInfoModel.ResModel resBean = XMLUtils.createResBean(reqModel, AddCardInfoModel.ResModel.class);
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    public String returnCard(String xml) throws Exception
    {
        ReturnCardModel.ReqModel reqModel = XMLUtils.readXML(xml, ReturnCardModel.ReqModel.class);
        if (!Utils.checkBean(reqModel)) return Utils.createError();
        setErpCode(reqModel);
        ReturnCardModel.ResModel resBean = XMLUtils.createResBean(reqModel, ReturnCardModel.ResModel.class);
        User user = organDao.getUserByLoginName(reqModel.getOp_user(), UserType.in);
        if (user == null)
        {
            resBean.setResult("-1");
            resBean.setError("您没有权限进行此操作！");
            return XMLUtils.response(resBean);
        }
        else
        {
            DeviceCard deviceCard = deviceService.findDeviceCard(reqModel.getCard_id());
            if (deviceCard == null || !deviceCard.getStatus().equals(CardStatus.ACTIVE))
            {
                resBean.setResult("-1");
                resBean.setError("卡号不存在或者卡号未被激活！");
                return XMLUtils.response(resBean);
            }
            else
            {
                deviceCard.setStatus(CardStatus.RETIRED);
                deviceCard.setReturnOperation(user.getUserId());
                commonDao.save(deviceCard);
            }

        }
        resCode(resBean);
        return XMLUtils.response(resBean);
    }

    /**
     * 把部门编码转部门id
     * @param bean
     */
    private void setErpCode(Object bean)
    {
        try
        {
            Object erpCode = ReflectionUtils.getValue(bean, bean.getClass(), "ERPCode");
            String deptCode = String.valueOf(erpCode);
            oldCode =deptCode;
            if (StringUtils.isEmpty(deptCode))
            {
                ReflectionUtils.setValue(bean, "ERPCode", "");
            }
            else
            {
                Dept dept = commonDao.getDeptInfoByCode(deptCode);
                String deptId= dept==null?"":""+dept.getDeptId();
                ReflectionUtils.setValue(bean, "ERPCode", deptId);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 把部门id转成企业编码
     * @param bean
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void resCode(Object bean) throws NoSuchFieldException, IllegalAccessException
    {
        ReflectionUtils.setValue(bean, "ERPCode", oldCode);
    }
}
