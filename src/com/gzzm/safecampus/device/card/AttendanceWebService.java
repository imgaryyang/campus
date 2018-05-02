package com.gzzm.safecampus.device.card;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.jws.*;

/**
 * @author liyabin
 * @date 2018/3/16
 */
@WebService(targetNamespace = "http://service.eastriver.com")
public class AttendanceWebService
{
    @Inject
    private static Provider<AttendanceService> attendanceServiceProvider;

    public AttendanceWebService()
    {
    }

    /**
     * 获取加密秘钥
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getERPKeys(@WebParam(name = "xml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getKey(xml);
    }

    /**
     * 用户登录
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String login(@WebParam(name = "loginXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().userLogin(xml);
    }

    /**
     * 卡格式
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String cardFormat(@WebParam(name = "cardFormatXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().cardFormat(xml);
    }

    /**
     * 卡片类型的配置
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String cardTypeConfig(@WebParam(name = "cardTypeXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().cardTypeConfig(xml);
    }

    /**
     * 1.0 卡格式参数配置
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String cardParamV1(@WebParam(name = "cardParamV1Xml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().cardParamV1(xml);
    }

    /**
     * 2.0 卡格式的参数配置- 只是实现了接口具体流程没有跑过
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String cardParamV2(@WebParam(name = "cardParamV2Xml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().cardParamV2(xml);
    }

    /**
     * CPU 卡格式的配置- 只是实现了接口具体流程没有跑过
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String cpuCardParam(@WebParam(name = "cpuCardParamXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().cardParamV2(xml);
    }

    /**
     * 提交卡片应用密钥
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String SendCardKeys(@WebParam(name = "in0") String xml) throws Exception
    {
        return attendanceServiceProvider.get().sendCardKeys(xml);
    }

    /**
     * 地区配置
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String areaConfig(@WebParam(name = "areaConfigXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().areaConfigModel(xml);
    }

    /**
     * 卡应用
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String cardAppType(@WebParam(name = "cardAppXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().cardAppType(xml);
    }

    /**
     *获取部门信息 -部门树
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getDeparts(@WebParam(name = "departsXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getDeparts(xml);
    }

    /**
     * 发卡用户列表
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getEmpList(@WebParam(name = "empListXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getEmpList(xml);
    }

    /**
     * 用户卡别表
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getEmpCardList(@WebParam(name = "empCardListXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getEmpCardList(xml);
    }

    /**
     * 获取卡号
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getCard_ID(@WebParam(name = "card_IdXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getCardID(xml);
    }

    /**
     * 获取学生/员工用户信息
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getEmpInfo(@WebParam(name = "empXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getEmpInfo(xml);
    }

    /**
     *发卡
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String addCardInfo(@WebParam(name = "addCardXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().addCardInfo(xml);
    }

    /**
     * 获取卡号的信息
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getCardInfo(@WebParam(name = "cardXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getCardInfo(xml);
    }

    /**
     *  获取卡片类型 根据卡序列号
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getCardTypeFromSN(@WebParam(name = "xml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getCardTypeFromSN(xml);
    }

    /**
     * 获取卡的状态
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getCardSNState(@WebParam(name = "xml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getCardSNState(xml);
    }

    /**
     *查询职位资料
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getPost(@WebParam(name = "postXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getPost(xml);
    }

    /**
     * 获取证件类型
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getIDInfo(@WebParam(name = "idXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getIDInfo(xml);
    }

    /**
     * 获取分组信息
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String getGroup(@WebParam(name = "groupXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().getGroupInfo(xml);
    }

    /**
     * 获取或者设置系统配置
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String setAndGetSYSData(@WebParam(name = "xml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().SYSData(xml);
    }

    /**
     * 获取、设置 水务信息
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String SetAndGetWaterData(@WebParam(name = "setAndGetWaterDataNoXml") String xml) throws Exception
    {
        if (StringUtils.isEmpty(xml)) xml =
                "<ERRECORD><SequenceId>74</SequenceId><ERPCode>72</ERPCode><Token>e5a006288d313aa0d55760a63feb07ff</Token><TimeStamp>20180328113829</TimeStamp><ActionCode>R</ActionCode></ERRECORD>";
        return attendanceServiceProvider.get().SYSData(xml);
    }

    /**
     * 退卡
     * @param xml
     * @return
     * @throws Exception
     */
    @WebMethod
    @WebResult
    public String returnCard(@WebParam(name = "returnCardXml") String xml) throws Exception
    {
        return attendanceServiceProvider.get().returnCard(xml);
    }
}
