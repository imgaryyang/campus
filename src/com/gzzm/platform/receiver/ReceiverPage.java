package com.gzzm.platform.receiver;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.DeptService;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 和选择接收者相关的http服务
 *
 * @author camel
 * @date 2010-4-28
 */
@Service
public class ReceiverPage
{
    @Inject
    private static Provider<HttpServletRequest> requestProvider;

    @Inject
    private DeptService deptService;

    /**
     * 匹配查询时最多返回多少条数据
     */
    private int maxCount = 10;

    /**
     * 类型，如邮件，手机号码等
     *
     * @see com.gzzm.platform.receiver.ReceiversLoadContext#type
     * @see com.gzzm.platform.receiver.ReceiversLoadContext#EMAIL
     * @see com.gzzm.platform.receiver.ReceiversLoadContext#PHONE
     */
    private String type;

    /**
     * type的显示名称，如手机，电子邮箱
     */
    private String typeName;

    /**
     * 用户在线信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 拥有权限的部门
     */
    private Collection<Integer> authDeptIds;


    private String appId;

    private Integer deptId;

    @Inject
    private ReceiverService service;

    public ReceiverPage()
    {
    }

    public int getMaxCount()
    {
        return maxCount;
    }

    public void setMaxCount(int maxCount)
    {
        this.maxCount = maxCount;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @SuppressWarnings("unchecked")
    protected Collection<Integer> getAuthDeptIds() throws Exception
    {
        if (authDeptIds == null)
        {
            HttpServletRequest request = requestProvider.get();
            UserOnlineInfo userOnlineInfo = UserOnlineInfo.getUserOnlineInfo(request);

            if (authDeptIds == null)
            {
                if (appId == null)
                    authDeptIds = userOnlineInfo.getAuthDeptIds(request, null);
                else
                    authDeptIds = userOnlineInfo.getAuthDeptIds(appId, null);
            }

            if (authDeptIds != null && authDeptIds.isEmpty())
            {
                authDeptIds =
                        deptService.getDept(deptId == null ? userOnlineInfo.getBureauId() : deptId).allSubDeptIds();
            }
        }

        return authDeptIds;
    }

    /**
     * 转到选择接收者的页面
     *
     * @return 转向选择接收者的页面
     */
    @Service(url = "/receiver/select")
    @Forward(page = "/platform/receiver/select.ptl")
    public String showPage()
    {
        return null;
    }

    /**
     * 根据一个字符串查询相关的接收人，
     *
     * @param s 输入的字符串，可以是姓名的，也可以是拼音，或者是简拼或者是邮件地址
     * @return 匹配的接收者列表
     * @throws Exception 异常
     */
    @Service(url = "/receiver/query?word={$0}")
    public List<Receiver> queryReceiver(String s) throws Exception
    {
        long l = System.currentTimeMillis();

        ReceiversLoadContext context = new ReceiversLoadContext();

        context.setUser(userOnlineInfo);
        context.setType(type);
        context.setAuthDeptIds(getAuthDeptIds());
        context.setMaxCount(maxCount);

        return service.queryReceiver(s, context);
    }
}
