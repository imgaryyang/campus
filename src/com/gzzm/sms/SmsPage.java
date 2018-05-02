package com.gzzm.sms;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.syn.OrganSynDao;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2010-11-15
 */
@Service
public class SmsPage
{
    @Inject
    private static Provider<OrganSynDao> organSynDaoProvider;

    @Inject
    private SmsService service;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户ID，与userName/passowrd互斥
     */
    private Integer userId;

    /**
     * 部门ID，与userName/passowrd userId互斥
     */
    private Integer deptId;

    private String sourceDeptId;

    /**
     * 目标电话号码
     */
    private String[] phone;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 短信序号
     */
    private String serial;

    /**
     * 客户端代码
     */
    private String clientCode;

    private boolean base64;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    public SmsPage()
    {
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String[] getPhone()
    {
        return phone;
    }

    public void setPhone(String[] phone)
    {
        this.phone = phone;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getSourceDeptId()
    {
        return sourceDeptId;
    }

    public void setSourceDeptId(String sourceDeptId)
    {
        this.sourceDeptId = sourceDeptId;
    }

    public String getClientCode()
    {
        return clientCode;
    }

    public void setClientCode(String clientCode)
    {
        this.clientCode = clientCode;
    }

    public boolean isBase64()
    {
        return base64;
    }

    public void setBase64(boolean base64)
    {
        this.base64 = base64;
    }

    @NotSerialized
    @Select(field = "userId")
    public List<SmsUser> getUsers() throws Exception
    {
        //管理员可以看到所有用户，其他人只能看到自己拥有权限的用户
        if (userOnlineInfo.isAdmin())
            return service.getDao().getSmsUsers();
        else
            return service.getDao().getSmsUsers(userOnlineInfo.getUserId());
    }

    private SmsUser getSmsUser() throws Exception
    {
        SmsUser user;

        if (!StringUtils.isEmpty(sourceDeptId))
        {
            deptId = organSynDaoProvider.get().getDeptIdWithSourceId(sourceDeptId);
            if (deptId == null)
                throw new NoErrorException("错误的部门ID:" + deptId);
        }

        if (deptId != null)
        {
            user = service.getUserByDeptId(deptId);

            if (user == null)
                throw new NoErrorException("没有配置相应的短信用户,deptId:" + deptId);
        }
        else if (StringUtils.isEmpty(userName))
        {
            throw new NoErrorException("缺少用户名");
        }
        else if (StringUtils.isEmpty(password))
        {
            throw new NoErrorException("缺少密码");
        }
        else
        {
            user = service.getUserByLoginName(userName);

            if (user == null || !user.getPassword().equals(password))
            {
                throw new NoErrorException("用户名密码错误");
            }
        }

        return user;
    }

    @Service(url = "/sms/send", method = HttpMethod.post)
    @Xml
    public SendResult doSend()
    {
        return send();
    }

    /**
     * 发送短信的url接口
     *
     * @return 短信发送结果，包含每个电话号码发送的短信对应的id号和错误信息（如果发送错误的话）
     */
    @Service(url = "/sms/send")
    @Xml
    public SendResult send()
    {
        String error;
        try
        {
            if (StringUtils.isEmpty(content))
            {
                error = "内容为空";
            }
            else if (phone == null)
            {
                error = "电话号码为空";
            }
            else
            {
                if (base64)
                {
                    content = new String(CommonUtils.base64ToByteArray(content.replace(" ", "+")), "UTF-8");
                }

                List<SendResultItem> items = service.sendSms(content, phone, serial, getSmsUser(), clientCode);

                SendResult result = new SendResult();
                result.setItems(items);
                return result;
            }
        }
        catch (NoErrorException ex)
        {
            error = ex.getMessage();
        }
        catch (Throwable ex)
        {
            String id = Tools.getUUID();
            Tools.log(id, ex);
            error = id;
        }

        SendResult result = new SendResult();
        result.setError(error);
        result.setItems(Collections.<SendResultItem>emptyList());

        return result;
    }

    /**
     * 接收短信，提供接收短信的url接口
     *
     * @return 短信列表
     */
    @Service(url = "/sms/receive")
    @Xml
    public SmsList receive()
    {
        String error;
        try
        {
            List<SmsMo> moList = service.readSmsMoList(getSmsUser().getUserId(), clientCode);

            return new SmsList(moList);
        }
        catch (NoErrorException ex)
        {
            error = ex.getMessage();
        }
        catch (Throwable ex)
        {
            String id = Tools.getUUID();
            Tools.log(id, ex);

            error = id;
        }

        SmsList list = new SmsList();
        list.setError(error);
        return list;
    }

    /**
     * 通过一个序号接收短信回复的信息
     *
     * @return 短信列表
     */
    @Service(url = "/sms/receive/{phone}/{serial}")
    @Xml
    public SmsList receiveBySerial()
    {
        String error;
        try
        {
            if (phone == null || phone.length == 0)
                throw new NoErrorException("缺少电话号码");

            String phone = this.phone[0];
            if (StringUtils.isEmpty(phone))
                throw new NoErrorException("缺少电话号码");

            List<SmsMo> moList = service.querySmsMoListBySerial(phone, serial);

            return new SmsList(moList);
        }
        catch (NoErrorException ex)
        {
            error = ex.getMessage();
        }
        catch (Throwable ex)
        {
            String id = Tools.getUUID();
            Tools.log(id, ex);

            error = id;
        }

        SmsList list = new SmsList();
        list.setError(error);
        return list;
    }

    /**
     * 读取短信状态报告，提供url接口
     *
     * @return 短信状态报告列表
     */
    @Service(url = "/sms/receipt")
    @Xml
    public ReceiptList receipt()
    {
        String error;
        try
        {
            List<SmsMt> mtList = service.readSmsMtList(getSmsUser().getUserId(), clientCode);

            return new ReceiptList(mtList);
        }
        catch (NoErrorException ex)
        {
            error = ex.getMessage();
        }
        catch (Throwable ex)
        {
            String id = Tools.getUUID();
            Tools.log(id, ex);

            error = id;
        }

        ReceiptList list = new ReceiptList();
        list.setError(error);
        return list;
    }

    /**
     * 打开发送短信的页面
     *
     * @return 发送短信的页面
     */
    @Service(url = {"/sms/user/{userId}/sendSms", "/sms/sendSms"})
    public String show()
    {
        return "sms";
    }

    /**
     * 发送短信
     *
     * @return 发送成功返回true，否则返回false
     * @throws Exception 发送短信错误
     */
    @Service(method = HttpMethod.post)
    public boolean sendSms() throws Exception
    {
        if (phone.length == 1)
        {
            phone = phone[0].split(",");
        }

        List<SendResultItem> items = service.sendSms(content, phone, serial, userId, clientCode);

        for (SendResultItem item : items)
        {
            if (!StringUtils.isEmpty(item.getError()))
                return false;
        }

        return true;
    }
}