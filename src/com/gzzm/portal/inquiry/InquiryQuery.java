package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.sql.Date;
import java.util.*;

/**
 * 咨询投诉监察和管理
 *
 * @author camel
 * @date 12-11-7
 */
@Service(url = "/portal/inquiry/inquiry")
public class InquiryQuery extends BaseInquiryCrud<Inquiry>
{
    private InquiryState state;

    private boolean editable = true;

    private Integer inquiryDeptId;

    private AuthDeptTreeModel inquiryDeptTree;

    public InquiryQuery()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public InquiryState getState()
    {
        return state;
    }

    public void setState(InquiryState state)
    {
        this.state = state;
    }

    @Lower(column = "sendTime")
    public Date getTime_start()
    {
        return super.getTime_start();
    }

    @Upper(column = "sendTime")
    public Date getTime_end()
    {
        return super.getTime_end();
    }

    @Like("title")
    public String getTitle()
    {
        return super.getTitle();
    }

    @Like("inquirerName")
    public String getInquirerName()
    {
        return super.getInquirerName();
    }

    @Like("code")
    public String getCode()
    {
        return super.getCode();
    }

    @Contains("text")
    public String getContent()
    {
        return super.getContent();
    }

    @Select(field = {"entity.catalogId", "catalogId"})
    public InquiryCatalogTreeModel getCatalogTree()
    {
        return super.getCatalogTree();
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @NotSerialized
    public boolean isInquiryEditable() throws Exception
    {
        return editable;
    }

    @NotSerialized
    public boolean isPublishTypeEditable() throws Exception
    {
        return true;
    }

    public boolean isInquiryPublicity()
    {
        return getEntity() != null && getEntity().isPublicity() != null && getEntity().isPublicity();
    }

    @NotSerialized
    public boolean isTimeLimitEditable()
    {
        return editable;
    }

    public Integer getInquiryDeptId()
    {
        return inquiryDeptId;
    }

    public void setInquiryDeptId(Integer inquiryDeptId)
    {
        this.inquiryDeptId = inquiryDeptId;
    }

    @Select(field = "inquiryDeptId")
    public AuthDeptTreeModel getInquiryDeptTree()
    {
        if (inquiryDeptTree == null)
        {
            inquiryDeptTree = new AuthDeptTreeModel();
            inquiryDeptTree.setAppId(Constants.INQUIRY_DEPT_SELECT);
        }

        return inquiryDeptTree;
    }

    @Override
    @NotCondition
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    public String getComplexCondition() throws Exception
    {
        String s = null;

        //只有此部门处理过，就查询出来
        if (getQueryDeptIds() != null)
        {
            s = "exists p in processes : (p.deptId in :queryDeptIds and p.state<>3)";
        }

        if (getTimeoutTypeId() != null)
        {
            if (s != null)
                s += " and ";
            else
                s = "";

            s += " exists p in processes : (p.state<>3 and p.processId in " +
                    "(select recordId from Timeout t where typeId in :timeoutTypeId " +
                    "and valid=1 and levelId in ?timeoutLevelId and " +
                    "timeoutTime>?timeoutTime_start and timeoutTime<=?timeoutTime_end))";
        }

        return s;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleted";
    }

    @Override
    protected Long getInquiryId(Inquiry entity) throws Exception
    {
        return entity.getInquiryId();
    }

    @Service
    @ObjectResult
    public void modifyAcceptTimeLimit(Long processId, Integer timeLimit) throws Exception
    {
        service.modifyAcceptTimeLimit(processId, timeLimit);
    }


    @Service
    @ObjectResult
    public void modifyProcessTimeLimit(Long processId, Integer timeLimit) throws Exception
    {
        service.modifyProcessTimeLimit(processId, timeLimit);
    }

    @Service
    @ObjectResult
    public void back() throws Exception
    {
        service.back(getEntity().getInquiryId());
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void turn(Integer deptId, String opinion) throws Exception
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userInfo", userOnlineInfo);
        map.put("opinion", opinion);
        map.put("dept", deptService.getDept(deptId));

        opinion = Tools.getMessage("portal.inquiry.turnOpinion", new CollectionUtils.EvalMap(map));

        service.turn(getEntity().getInquiryId(), deptId, opinion);
    }

    @Service(url = "/portal/inquiry/turn")
    public String showTurn() throws Exception
    {
        return "turn";
    }

    @Override
    @Forwards({
            @Forward(page = "/portal/inquiry/inquiry.ptl"),
            @Forward(name = "print", page = "/portal/inquiry/print.ptl"),
            @Forward(name = "print2", page = "/portal/inquiry/process_print.docp")
    })
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("标题", "title");

        if (getTimeoutTypeId() == null)
            view.addComponent("来信时间", "time_start", "time_end");
        else
            view.addComponent("超时时间", "timeoutTime_start", "timeoutTime_end");

        view.addComponent("状态", "state");
        view.addComponent("部门", "deptIds");

        view.addMoreComponent("来信人", "inquirerName");
        view.addMoreComponent("编号", "code");
        view.addMoreComponent("内容", "content");

        if (getWayId() == null)
        {
            if (getInquiryWays().size() > 1)
                view.addMoreComponent("收信渠道", "wayId");
        }

        view.addMoreComponent("类型", "typeId");
        view.addColumn("来信编号", "code").setWidth("150");
        view.addColumn("标题", new HrefCell("title").setAction("display(${inquiryId})")).setWrap(true);
        view.addColumn("来信人", "inquirerName").setWidth("100").setWrap(true);
        view.addColumn("来信时间", new FieldCell("sendTime", "yyy-MM-dd HH:mm")).setWidth("120");

        initConfig(view);

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 0)
            view.addColumn("处理部门", new FieldCell("lastProcess.dept.deptName").setOrderable(false)).setWidth("140").setWrap(true);

        if (getInquiryWays().size() > 1)
            view.addColumn("收信渠道", "way.wayName").setWidth("70");

        view.addColumn("状态", "state").setWidth("60");
        view.addColumn("回复时间", new FieldCell("replyTime", "yyy-MM-dd HH:mm")).setWidth("120");
        view.addColumn("公开方式", "publishType").setOrderFiled("publishType").setWidth("95")
                .setAlign(Align.center);

        if (isShowTimeout())
        {
            view.addColumn("警告", new ConditionComponent().add("lastProcess.validTimeout!=null",
                    new CImage("/timeout/level/icon/${lastProcess.validTimeout.levelId}")
                            .setPrompt("${lastProcess.validTimeout}").setProperty("style", "cursor:pointer")
            )).setWidth("45");
        }

        view.addColumn("查看", new CButton("查看", "display(${inquiryId})")).setWidth("45");

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());

        view.addButton(Buttons.export("xls"));

        view.importJs("/portal/inquiry/inquiry.js");

        return view;
    }
}
