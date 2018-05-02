package com.gzzm.safecampus.device.attendance.service;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.device.*;
import com.gzzm.safecampus.device.attendance.dao.CommonDao;
import com.gzzm.safecampus.device.attendance.entity.AttendanceLog;
import com.gzzm.safecampus.device.attendance.util.*;
import com.gzzm.safecampus.wx.attendance.BusCardService;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/27
 */
public class AttendanceService
{
    /**
     * 当前请求数据的考勤机
     */
    private AttendanceDevice attendanceDevice;

    @Inject
    CommonDao commonDao;

    @Inject
    BusCardService busCardService;

    private String cityCode_Name;
    private String clientNo;
    private String clientSn;
    private String socketVersion;
    private String businessType;

    private SocketAddress socketAddress;

    private String ipAdress;
    private Integer port;

    public AttendanceService()
    {
    }

    public SocketAddress getSocketAddress()
    {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress)
    {
        this.socketAddress = socketAddress;
    }

    public String getIpAdress()
    {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress)
    {
        this.ipAdress = ipAdress;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    /**
     * 解析命令
     * @param hexCommand
     * @throws Exception
     */
    public void resolveCommand(String hexCommand) throws Exception
    {
        if (StringUtils.isEmpty(hexCommand) || hexCommand.length() < 4) return;
        businessType = hexCommand.substring(0, 4);
        if (!checkAuthority(hexCommand)) return;
        switch (Integer.parseInt(businessType, 16))
        {

            case 0xa002://终端签到/签退
                signOrOut(hexCommand);
                break;
            case 0xa003://心跳检查
                heartbeatCheck(hexCommand);
                break;
            case 0xa004://获取设备版本
                getDeviceVersion(hexCommand);
                break;
            case 0xa005://获取卡秘钥
                getDeviceCardKey(hexCommand);
                break;
            case 0xb004://获取白名单
                getWhiteList(hexCommand);
                break;
            case 0xb001://考勤报文回传
                addAttendanceLog(hexCommand);
                break;
        }
    }

    /**
     * 权限校验
     * 主要用于判断这个考勤设备是否合法-同时初始化一些数据
     *
     * @param command
     * @return
     */
    private boolean checkAuthority(String command) throws Exception
    {
        cityCode_Name = command.substring(4, 14);//初始化城市代码
        clientNo = command.substring(14, 22);//初始化客户端（考勤机）版本号
        clientSn = command.substring(22, 38);//初始化客户端（考勤机）系列号
        socketVersion = command.substring(38, 40);//初始化通讯版本信息，
        attendanceDevice = commonDao.getAttendanceDevice(clientNo, cityCode_Name);
        if (attendanceDevice == null) return false;
        switch (attendanceDevice.getStates())
        {
            //没有初始化-ip地址以及端口没有初始化，修改考勤机对象信息
            case UNINITIALIZED:
                attendanceDevice.setStates(AttendanceStates.ACTIVE);
                initAttendanceDevice(attendanceDevice);
                break;
            case ACTIVE:
                initAttendanceDevice(attendanceDevice);
                break;
            case DISABLE://失效拦截请求-考勤机状态为不可用请求不被处理
                initAttendanceDevice(attendanceDevice);
                return false;
        }
        return true;
    }

    /**
     * 初始信息
     *
     * @param attendanceDevice
     */
    private void initAttendanceDevice(AttendanceDevice attendanceDevice) throws Exception
    {
        if (StringUtils.isEmpty(attendanceDevice.getDeviceSn()) ||
                StringUtils.isEmpty(attendanceDevice.getDeviceSn()) ||
                StringUtils.isEmpty(attendanceDevice.getIpAddress()) ||
                StringUtils.isEmpty(attendanceDevice.getPort()))
        {
            attendanceDevice.setDeviceSn(clientSn);
            attendanceDevice.setIpAddress(ipAdress);
            attendanceDevice.setPort(String.valueOf(port));
            commonDao.save(attendanceDevice);
        }
    }

    /**
     * 心跳检测-通过心跳 修改对应返回信息的版本控制得以考勤机各种骚操作
     *
     * @param command
     * @throws Exception
     */
    public void heartbeatCheck(String command) throws Exception
    {
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(49));
        while (hexLen.length() != 8)
            hexLen.insert(0, Integer.toHexString(0));
        StringBuilder responseData = new StringBuilder(hexLen);
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        responseData.append("0000");
        //new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
        String time = DateUtils.toString(new Date(), "yyyyMMddHHmmss");
        //系统时间
        responseData.append(time);
        //黑名版版本号
        responseData.append("00000000");
        //白名单版本号;
        Integer upVersion = attendanceDevice.getWhiteVersion().getVersionNo();
        //不足位数前面补0
        StringBuilder versionNo = new StringBuilder(Integer.toHexString(upVersion));
        while (versionNo.length() < 8)
        {
            versionNo.insert(0, "0");
        }
        responseData.append(versionNo);
        //更新门禁信息标识
        responseData.append("0000");
        //远程控门
        responseData.append("0000");
        //服务端最新补贴序号
        responseData.append("0000");
        //企业代码版本号
        responseData.append("0000");
        //启用状态标识
        responseData.append("01");
        //通讯参数下载标识
        responseData.append("02");
        //实时上传
        responseData.append("01");
        //是否复采记录
        responseData.append("00");
        //终端机交易序号
        responseData.append("00000001");
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(responseData.toString())));
        ResponseUtils.sendData(DataUtils.hexStringToBytes(responseData.toString()), socketAddress);
    }

    /**
     * 签到签退
     *
     * @param command
     * @throws Exception
     */
    public void signOrOut(String command) throws Exception
    {
        String type = command.substring(40, 42);
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(18));
        while (hexLen.length() != 8)
        {
            hexLen.insert(0, Integer.toHexString(0));
        }
        StringBuilder responseData = new StringBuilder(hexLen);
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        responseData.append("0000");
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(responseData.toString())));
        ResponseUtils.sendData(DataUtils.hexStringToBytes(responseData.toString()), socketAddress);
    }

    /**
     * 获取设备版本号
     *
     * @param command
     * @throws Exception
     */
    public void getDeviceVersion(String command) throws Exception
    {
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(26));
        while (hexLen.length() != 8)
        {
            hexLen.insert(0, Integer.toHexString(0));
        }
        StringBuilder responseData = new StringBuilder(hexLen);
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        //结果标志
        responseData.append("0000");
        //卡片应用密钥版本号
        responseData.append("0000");
        //考勤工作模式版本号
        responseData.append("0000");
        //预留1
        responseData.append("0000");
        //预留2
        responseData.append("0000");
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(responseData.toString())));
        ResponseUtils.sendData(DataUtils.hexStringToBytes(responseData.toString()), socketAddress);
    }

    /**
     * 添加考勤记录上传
     *
     * @param command
     * @throws Exception
     */
    public void addAttendanceLog(String command) throws Exception
    {
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(22));
        while (hexLen.length() != 8)
        {
            hexLen.insert(0, Integer.toHexString(0));
        }
        StringBuilder responseData = new StringBuilder(hexLen);
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        String firstNo = command.substring(40, 48);
        try
        {
            // attendanceDevice 这个对象就是考勤机对象想要什么参数自己去传
            saveAttendanceLog(command.substring(40, command.length()));
        }
        catch (Exception e)
        {
            //如果业务方法发生异常那么这条考勤记录将会在条件允许的情况下重新上传
            Integer repeatSendCount = commonDao.repeatSendCount(Integer.parseInt(firstNo, 16),attendanceDevice.getDeviceNo());
            if(repeatSendCount==null)repeatSendCount=0;
            responseData.append(firstNo);
            int max = Integer.parseInt(Tools.getMessage("repeat.send.count"));
            if (max <= repeatSendCount)
            {
                responseData.append("0000");
                Tools.log("业务系统持续异常，考勤报文重发超过限。停止重发！");
            }
            else
            {
                responseData.append("0004");
                Tools.log("考勤记录上传成功，服务器业务处理异常！考勤机即将重发报文，剩余重发次数（"+(max-repeatSendCount)+"）",e);
            }
            responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(responseData.toString())));
            ResponseUtils.sendData(DataUtils.hexStringToBytes(responseData.toString()), socketAddress);

        }
        responseData.append(firstNo);
        responseData.append("0000");
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(responseData.toString())));
        ResponseUtils.sendData(DataUtils.hexStringToBytes(responseData.toString()), socketAddress);
    }

    /**
     * 获取白名单
     *
     * @param command
     */
    public void getWhiteList(String command) throws Exception
    {
        StringBuilder responseData = new StringBuilder();
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        //拉取当前考勤设备的白名单列表
        List<DeviceCard> deviceCards = commonDao.getDeviceCard(attendanceDevice);
        //设置为请求成功
        responseData.append("0000");
        byte[] commonHead = DataUtils.hexStringToBytes(responseData.toString());
        int count = 4;
        if (deviceCards == null) return;
        byte[] response = new byte[1024];
        System.arraycopy(commonHead, 0, response, count, commonHead.length);
        count = 4 + commonHead.length;
        //白名单版本
        Integer upVersion = attendanceDevice.getWhiteVersion().getVersionNo();
        StringBuilder versionNo = new StringBuilder(Integer.toHexString(upVersion));
        //由于白名单版本占4个字节不足前面需要补0
        while (versionNo.length() < 8)
        {
            versionNo.insert(0, "0");
        }
        int index = 0;
        //
        for (DeviceCard deviceCard : deviceCards)
        {
            //卡号
            byte[] bytes = DataUtils.hexStringToBytes(Integer.toHexString(Integer.parseInt(deviceCard.getCardSn())));
            byte[] cardNo = new byte[8];
            //不足前面补0  - 卡号
            System.arraycopy(bytes, 0, cardNo, 8 - bytes.length, bytes.length);
            System.arraycopy(cardNo, 0, response, count, 8);
            count += 8;
            //白名单版本
            byte[] version = DataUtils.hexStringToBytes(versionNo.toString());
            System.arraycopy(version, 0, response, count, 4);
            count += 4;
            //类型  新增还是删除 01 新增 02 删除

            byte[] listType = deviceCard.getStatus().equals(CardStatus.ACTIVE) ? DataUtils.hexStringToBytes("01") :
                    DataUtils.hexStringToBytes("02");
            System.arraycopy(listType, 0, response, count, 1);
            count += 1;
            //姓名
            byte[] empName = new byte[16];
            //工号
            byte[] empNo = new byte[16];
            switch (deviceCard.getType())
            {
                case STUDENT:
                    String studentName = deviceCard.getStudent().getStudentName();
                    empName = DataUtils.getChineseByte(studentName, 16);
                    //学号设置
                    String studentNo = deviceCard.getStudent().getStudentNo();
                    empNo = DataUtils.getChineseByte(studentNo, 16);
                    break;
                case TEACHER:
                    break;
            }
            System.arraycopy(empName, 0, response, count, 16);
            count += 16;
            System.arraycopy(empNo, 0, response, count, 16);
            count += 16;
            //工作时间 - 第一个字节表示工作日期  后面6个自己表示时间段
            byte[] workTime = DataUtils.hexStringToBytes("ffffffffffffff");
            System.arraycopy(workTime, 0, response, count, 7);
            count += 7;
            if (index >= 10)//数据量过大时需要拆分白名单-超过10个白名单数据会报错 亲测~
            {
                index = -1;
                //分段发送数据
                ResponseUtils.sendData(operatorResponse(response, count), socketAddress);
                //发送完以后清理数据,从新组装头部数据发送
                response = new byte[1024];
                System.arraycopy(commonHead, 0, response, 4, commonHead.length);
                count = 4 + commonHead.length;

            }
            index++;
        }
        ResponseUtils.sendData(operatorResponse(response, count), socketAddress);
    }

    /**
     * 操作生成白名单
     *
     * @return
     */
    private byte[] operatorResponse(byte[] response, int count) throws Exception
    {
        //拼接请求头
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(count));
        while (hexLen.length() != 8)
        {
            hexLen.insert(0, Integer.toHexString(0));
        }
        byte[] strLen = DataUtils.hexStringToBytes(hexLen.toString());
        System.arraycopy(strLen, 0, response, 0, 4);
        byte[] optionArr = new byte[count];
        System.arraycopy(response, 0, optionArr, 0, count);
        //得到 mac码
        byte[] encryption = DataUtils.hexStringToBytes(ParseDataUtils.encryption(optionArr));
        System.arraycopy(encryption, 0, response, count, 4);
        count += 4;
        byte[] result = new byte[count];
        System.arraycopy(response, 0, result, 0, count);
        return result;
    }

    /**
     * 获取卡配置
     *
     * @param command
     */

    public void getDeviceCardKey(String command) throws Exception
    {
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(169));
        while (hexLen.length() != 8)
        {
            hexLen.insert(0, Integer.toHexString(0));
        }
        StringBuilder responseData = new StringBuilder(hexLen);
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        //卡密版本
        responseData.append("0001");
        //CPU卡AID
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //CPU卡密钥因子
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //M1消费密钥(2.0卡格式)
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //M1充值密钥(2.0卡格式)
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //M1 TAC密钥(2.0卡格式)
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //目录区
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(1))));
        //KEYA/KEYB（简易卡格式）
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //扇区编号（简易卡格式）
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(1))));
        //5.0卡信息区使用扇区
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(1))));
        //5.0卡信息区密钥因子
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //5.0卡钱包区使用扇区
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(1))));
        //HEX，通信采用3DES ECB加密传输，加密的KEY为通信密钥
        responseData.append(DataUtils.getHexDefault(16));
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //5.0卡记录区使用扇区
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(1))));
        //5.0卡记录区密钥因子
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(DataUtils.getHexDefault(16))));
        //校验字
        responseData.append(ParseDataUtils.encryption(DataUtils.hexStringToBytes(responseData.toString())));
        ResponseUtils.sendData(DataUtils.hexStringToBytes(responseData.toString()), socketAddress);
    }

    /**
     * 保存考勤记录,只有考勤记录
     */
    public void saveAttendanceLog(String command) throws Exception
    {
        int len = 0;
        List<AttendanceLog> logs = new ArrayList<AttendanceLog>();
        StringBuilder hexLen = new StringBuilder(Integer.toHexString(22));
        while (hexLen.length() != 8)
        {
            hexLen.insert(0, Integer.toHexString(0));
        }
        StringBuilder responseData = new StringBuilder(hexLen);
        responseData.append(businessType);
        responseData.append(cityCode_Name);
        responseData.append(clientNo);
        responseData.append(socketVersion);
        while (true)
        {

            AttendanceLog log = new AttendanceLog();
            //交易编号
            Integer logNo = Integer.parseInt(command.substring(0, 8), 16);
            //设备序列号
            log.setDeviceSn(attendanceDevice.getDeviceNo());
            //交易卡号
            Integer cardId = Integer.parseInt(command.substring(8, 24), 16);
            //考勤类型  0-卡物理序列号 1-逻辑卡号
            Integer type = Integer.parseInt(command.substring(24, 26), 16);
            //考情时间
            String time = command.substring(26, 40);
            //0-正常卡 1-非法卡 2-黑名单卡
            Integer state = Integer.parseInt(command.substring(40, 42), 16);
            log.setLogNo(logNo);
            log.setCardNo("" + cardId);
            log.setState(state);
            log.setType(state);
            log.setAttendanceTime(DateUtils.toDate(time));
            logs.add(log);
            try
            {
                if (0 == state)
                {
                    DeviceCard deviceCard = null;
                    if (0 == type)
                        deviceCard = commonDao.getDeviceCardBySn("" + cardId);
                    else
                        deviceCard = commonDao.getDeviceCardByNo("" + cardId);
                    if (deviceCard != null)
                    {
                        switch (deviceCard.getType())
                        {
                            case STUDENT://// attendanceDevice 这个对象就是考勤机对象想要什么参数自己去传
                                busCardService
                                        .saveAttendance(deviceCard.getStudent().getStudentId(), log.getAttendanceTime());
                                break;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Tools.log("考勤记录异常",e);
            }
            len += 42;
            if (len > command.length() - 10) break;
        }
        commonDao.saveEntities(logs);
    }

    public static void main(String[] agrs) throws UnsupportedEncodingException
    {
        AttendanceService attendanceService = new AttendanceService();
        byte[] bytes = DataUtils.hexStringToBytes(Integer.toHexString(1600306596));
        byte[] cardNo = new byte[8];
        System.arraycopy(bytes, 0, cardNo, 8 - bytes.length, bytes.length);
        System.out.println(DataUtils.bytesToHexString(cardNo));
        //白名单版本号
        byte[] version = DataUtils.hexStringToBytes("00000001");
        //类型  新增还是删除 01 新增 02 删除
        byte[] listType = DataUtils.hexStringToBytes("01");
        //姓名
        byte[] empName = null;
        //工号
        byte[] empNo = null;
        //工作时间
        byte[] workTime = DataUtils.hexStringToBytes("ffffffffffffff");
    }


}
