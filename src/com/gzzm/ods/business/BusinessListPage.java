package com.gzzm.ods.business;

import com.gzzm.platform.annotation.MenuId;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.SimpleDeptInfo;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 发起业务流程时选择业务的界面
 *
 * @author camel
 * @date 11-9-21
 */
@Service
public class BusinessListPage
{
    public static final String OD_BUSINESS_SELECT_APP = "od_business_select";

    public static final String OD_BUSINESS_SELECT_SCOPE = "公文业务选择";

    public static class BusinessList
    {
        private Integer deptId;

        private String deptName;

        private List<BusinessModel> business;

        public BusinessList(Integer deptId, String deptName, List<BusinessModel> business)
        {
            this.deptId = deptId;
            this.deptName = deptName;
            this.business = business;
        }

        public Integer getDeptId()
        {
            return deptId;
        }

        public String getDeptName()
        {
            return deptName;
        }

        public List<BusinessModel> getBusiness()
        {
            return business;
        }
    }

    @Inject
    private BusinessService service;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 业务类型
     */
    private String[] type;

    /**
     * 如果只查询交换或者oa的数据，可传入此值过滤
     */
    private BusinessTag tag;

    /**
     * 当前部门
     */
    private Integer deptId;

    @MenuId
    private String menuId;

    /**
     * 关联的功能
     */
    private String component;

    public BusinessListPage()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String[] getType()
    {
        return type;
    }

    public void setType(String[] type)
    {
        this.type = type;
    }

    public BusinessTag getTag()
    {
        return tag;
    }

    public void setTag(BusinessTag tag)
    {
        this.tag = tag;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent(String component)
    {
        this.component = component;
    }

    public BusinessTag[] getTags()
    {
        if (tag == null)
            return new BusinessTag[]{BusinessTag.exchange, BusinessTag.flow};
        else
            return new BusinessTag[]{tag};
    }

    @NotSerialized
    public List<BusinessModel> getBusiness() throws Exception
    {
        return getBusiness(type, tag);
    }

    public List<BusinessModel> getBusiness(BusinessTag tag) throws Exception
    {
        return getBusiness(type, tag);
    }

    public List<BusinessModel> getBusiness(String type) throws Exception
    {
        return getBusiness(type, tag);
    }

    public List<BusinessModel> getBusiness(String[] type) throws Exception
    {
        return getBusiness(type, tag);
    }

    public List<BusinessModel> getBusiness(String type, BusinessTag tag) throws Exception
    {
        return getBusiness(new String[]{type}, tag);
    }

    public List<BusinessModel> getBusiness(String[] type, BusinessTag tag) throws Exception
    {
        Collection<Integer> authDeptIds =
                userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP + "_" + menuId + "_" + deptId);

        if (authDeptIds == null || authDeptIds.isEmpty())
            authDeptIds = userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP + "_" + deptId);

        if (authDeptIds == null || authDeptIds.isEmpty())
            authDeptIds = userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP + "_" + menuId);

        if (authDeptIds == null || authDeptIds.isEmpty())
            authDeptIds = userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP);

        if (authDeptIds == null || authDeptIds.isEmpty())
        {
            authDeptIds = service.getDeptService()
                    .getDeptIdsByScopeName(OD_BUSINESS_SELECT_SCOPE, deptId, userOnlineInfo.getDeptId());
        }

        if (authDeptIds != null && authDeptIds.isEmpty())
            authDeptIds = Arrays.asList(deptId, 1);

        return service.getSelectableBusinesses(deptId, type, tag, authDeptIds);
    }

    @NotSerialized
    public List<BusinessList> getBusinessLists() throws Exception
    {
        Collection<Integer> businessDeptIds = userOnlineInfo.getAuthDeptIds(RequestContext.getContext().getRequest());
        if (businessDeptIds != null && businessDeptIds.isEmpty())
            return Collections.emptyList();

        List<? extends SimpleDeptInfo> businessDepts;

        if (deptId == null)
        {
            if (businessDeptIds == null)
            {
                businessDepts = Collections.singletonList(userOnlineInfo.getBureau());
            }
            else
            {
                if (businessDeptIds.size() == 1)
                {
                    businessDepts = Collections.singletonList(
                            service.getDeptService().getDept(businessDeptIds.iterator().next()));
                }
                else
                {
                    businessDepts = service.getDeptService().getDao().getDepts(businessDeptIds);
                }
            }
        }
        else
        {
            if (businessDeptIds == null || businessDeptIds.contains(deptId))
            {
                businessDepts = Collections.singletonList(service.getDeptService().getDept(deptId));
            }
            else
            {
                return Collections.emptyList();
            }
        }

        List<BusinessList> businessLists = new ArrayList<BusinessList>(businessDepts.size());

        Collection<Integer> authDeptIds0 = null;

        for (SimpleDeptInfo dept : businessDepts)
        {
            Integer deptId = dept.getDeptId();
            Collection<Integer> authDeptIds =
                    userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP + "_" + menuId + "_" + deptId);

            if (authDeptIds == null || authDeptIds.isEmpty())
                authDeptIds = userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP + "_" + deptId);

            if (authDeptIds == null || authDeptIds.isEmpty())
            {
                if (authDeptIds0 == null)
                {
                    authDeptIds0 = userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP + "_" + menuId);

                    if (authDeptIds0 == null || authDeptIds0.isEmpty())
                        authDeptIds0 = userOnlineInfo.getAuthDeptIds(OD_BUSINESS_SELECT_APP);

                    if (authDeptIds0 == null || authDeptIds0.isEmpty())
                    {
                        authDeptIds0 = service.getDeptService().getDeptIdsByScopeName(OD_BUSINESS_SELECT_SCOPE, deptId,
                                userOnlineInfo.getDeptId());
                    }

                    if (authDeptIds0 == null)
                        authDeptIds0 = Collections.emptyList();
                }

                authDeptIds = authDeptIds0;
            }

            if (authDeptIds != null && authDeptIds.isEmpty())
                authDeptIds = Arrays.asList(deptId, 1);

            List<BusinessModel> businesses = service.getSelectableBusinesses(dept.getDeptId(), type, tag, authDeptIds);

            if (businesses.size() == 0 && businessDepts.size() == 1 && tag == BusinessTag.exchange && type.length == 1)
            {
                businesses = service.getSelectableBusinesses(dept.getDeptId(), type, tag,
                        Arrays.asList(dept.getDeptId(), 1));
            }

            if (businesses.size() == 0 && businessDepts.size() == 1 && tag == BusinessTag.flow && type.length == 1)
            {
                businesses = service.getSelectableBusinesses(dept.getDeptId(), type, BusinessTag.exchange,
                        Collections.singleton(1));
            }

            if (businesses.size() > 0)
            {
                if (!StringUtils.isEmpty(component))
                {
                    for (Iterator<BusinessModel> iterator = businesses.iterator(); iterator.hasNext(); )
                    {
                        BusinessModel businessModel = iterator.next();
                        if (!component.equals(businessModel.getComponentType()))
                            iterator.remove();
                    }
                }

                businessLists.add(new BusinessList(dept.getDeptId(), dept.getDeptName(), businesses));
            }
        }

        return businessLists;
    }

    @Service(url = "/ods/business/list")
    public String showList() throws Exception
    {
        if (type.length == 1)
        {
            if (tag == BusinessTag.exchange || !StringUtils.isEmpty(component))
            {
                List<BusinessList> businessLists = getBusinessLists();

                if (businessLists.size() == 1)
                {
                    BusinessList businessList = businessLists.get(0);

                    if (businessList.getBusiness().size() == 1)
                    {
                        BusinessModel business = businessList.getBusiness().get(0);

                        //公文交换收发文，只有一个业务，直接转向新建收文或者新建发文的页面
                        RequestContext.getContext().redirect(
                                "/ods/flow/start?businessId=" + business.getBusinessId() + "&deptId=" +
                                        businessList.getDeptId()
                        );

                        return null;
                    }
                }
            }
        }

        return "businesslist";
    }

    @Service(url = "/ods/business/select")
    public String showSelect()
    {
        return "select";
    }

    @NotSerialized
    @Service(url = "/ods/business/businessId")
    public Integer getBusinessId() throws Exception
    {
        List<BusinessModel> business1 = getBusiness(BusinessTag.exchange);
        List<BusinessModel> business2 = getBusiness(BusinessTag.flow);

        if (business1 == null || business1.size() == 0)
        {
            if (business2 != null && business2.size() == 1)
                return business2.get(0).getBusinessId();
        }
        else if (business2 == null || business2.size() == 0)
        {
            if (business1 != null && business1.size() == 1)
                return business1.get(0).getBusinessId();
        }

        return null;
    }
}
