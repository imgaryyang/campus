package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.DeptOwnedCrudUtils;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.message.sms.SmsVerifier;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.commons.PortalUtils;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.captcha.ArachneCaptchaSupport;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 内网填写咨询投诉的页面
 *
 * @author camel
 * @date 12-11-8
 */
@Service
public class InquiryWritePage
{
    @Inject
    private InquiryService service;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private Collection<Integer> authDeptIds;

    private AuthDeptTreeModel deptTree;

    private Integer wayId;

    private Inquiry inquiry;

    private InquiryCatalogTreeModel catalogTree;

    private boolean inner;

    /**
     * 短信验证码
     */
    private String sms;

    public InquiryWritePage()
    {
    }

    public Integer getWayId()
    {
        return wayId;
    }

    public void setWayId(Integer wayId)
    {
        this.wayId = wayId;
    }

    public Inquiry getInquiry()
    {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry)
    {
        this.inquiry = inquiry;
    }

    public boolean isInner()
    {
        return inner;
    }

    public void setInner(boolean inner)
    {
        this.inner = inner;
    }

    public String getSms()
    {
        return sms;
    }

    public void setSms(String sms)
    {
        this.sms = sms;
    }

    @Select(field = "inquiry.catalogId")
    public InquiryCatalogTreeModel getCatalogTree()
    {
        if (catalogTree == null)
            catalogTree = new InquiryCatalogTreeModel();

        return catalogTree;
    }

    @NotSerialized
    @Select(field = "inquiry.typeId")
    public List<InquiryType> getTypes() throws Exception
    {
        return service.getDao().getTypes();
    }

    @Select(field = "inquiry.deptId")
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
            deptTree.setAppId(Constants.INQUIRY_DEPT_SELECT);
        }

        return deptTree;
    }

    @Service(url = "/portal/inquiry/write")
    public String show() throws Exception
    {
        if (wayId == null)
        {
            List<InquiryType> types = service.getDao().getTypes();
            if (types.size() > 0)
                wayId = types.get(0).getTypeId();
        }

        inquiry = new Inquiry();
        inquiry.setWayId(wayId);

        inner = true;

        return "write";
    }

    @Service(url = "/portal/inquiry/write", method = HttpMethod.post, validateType = ValidateType.all)
    public Long save() throws Exception
    {
        return write().getInquiryId();
    }

    /**
     * 保存来信，返回来信编号
     *
     * @return 来信编号
     * @throws Exception
     */
    @Service(url = "/portal/inquiry/write_new", method = HttpMethod.post, validateType = ValidateType.all)
    @ObjectResult
    public String save$() throws Exception
    {
        return write().getCode();
    }

    private Inquiry write() throws Exception
    {
        if (inner)
        {
            if (userOnlineInfo == null || userOnlineInfo.getUserType() == UserType.out)
                throw new LoginExpireException();
        }
        else
        {
            if (!StringUtils.isEmpty(sms))
                SmsVerifier.check(inquiry.getPhone(), sms);
            else
                ArachneCaptchaSupport.check();
        }

        String ip = PortalUtils.getIp(RequestContext.getContext().getRequest());
        inquiry.setIp(ip);

        if (inner)
        {
            inquiry.setInquirer(userOnlineInfo.getUserId());
            inquiry.setInquireDeptId(DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this));
        }
        else if (userOnlineInfo != null && userOnlineInfo.getUserType() == UserType.out)
        {
            inquiry.setInquirer(userOnlineInfo.getUserId());
        }
        else
        {
            int ipLimit = -1;
            try
            {
                //d     单位小时
                ipLimit = Integer.valueOf(service.getCommonDao().getConfig("InquiryIpLimit"));
            }
            catch (Exception e)
            {
                Tools.log(e);
            }
            if (ipLimit >= 0)
            {
                Date startTime = new Date();
                if (ipLimit == 0)
                {
                    startTime = DateUtils.truncate(startTime);
                }
                else
                {
                    startTime = DateUtils.addHour(startTime, ipLimit);
                }
                ip = ip.split(":")[0] + "%";


                if (service.getDao().countIp(ip, startTime))
                {
                    if (ipLimit == 0)
                    {
                        throw new SystemException("当天内不能多次提交");
                    }
                    else
                    {
                        throw new SystemException(ipLimit + "小时内不能多次提交");
                    }
                }
            }

        }
        return service.write(inquiry);
    }

}
