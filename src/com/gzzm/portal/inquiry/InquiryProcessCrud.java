package com.gzzm.portal.inquiry;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.DeptInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Null;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.sql.Date;
import java.util.*;

/**
 * 咨询投诉处理
 *
 * @author camel
 * @date 12-11-6
 */
@Service(url = "/portal/inquiry/process")
public class InquiryProcessCrud extends BaseInquiryCrud<InquiryProcess>
{
    @UserId
    private Integer userId;

    @AuthDeptIds(app = Constants.INQUIRY_DEPT_SELECT)
    private Collection<Integer> inquiryDeptIds;

    private ProcessState state;

    private ProcessState[] states;

    private DeptInfo inquiryDept;

    private boolean autoAccept;

    private boolean publishTypeEditable = true;

    private boolean turnable = true;

    private String page;

    public InquiryProcessCrud()
    {
        addOrderBy("startTime", OrderType.desc);

        addForceLoad("inquiry");
    }

    @Override
    public String getAlias()
    {
        return "process";
    }

    @NotSerialized
    public Collection<Integer> getInquiryDeptIds()
    {
        return inquiryDeptIds;
    }

    @NotSerialized
    public DeptInfo getInquiryDept() throws Exception
    {
        if (inquiryDept == null)
        {
            if (inquiryDeptIds != null && inquiryDeptIds.size() == 1)
            {
                inquiryDept = deptService.getDept(inquiryDeptIds.iterator().next());
            }
        }

        return inquiryDept;
    }

    @NotSerialized
    public List<DeptInfo> getInquiryDepts() throws Exception
    {
        if (inquiryDeptIds != null)
        {
            return deptService.getDepts(new ArrayList<Integer>(getInquiryDeptIds()));
        }

        return null;
    }

    @Lower(column = "startTime")
    public Date getTime_start()
    {
        return super.getTime_start();
    }

    @Upper(column = "startTime")
    public Date getTime_end()
    {
        return super.getTime_end();
    }

    @Like("inquiry.title")
    public String getTitle()
    {
        return super.getTitle();
    }

    @Like("inquiry.inquirerName")
    public String getInquirerName()
    {
        return super.getInquirerName();
    }

    @Like("inquiry.code")
    public String getCode()
    {
        return super.getCode();
    }

    @Contains("inquiry.text")
    public String getContent()
    {
        return super.getContent();
    }

    @Equals("inquiry.wayId")
    public Integer getWayId()
    {
        return super.getWayId();
    }

    @Equals("inquiry.typeId")
    public Integer getTypeId()
    {
        return super.getTypeId();
    }

    @Equals("inquiry.catalogId")
    public Integer getCatalogId()
    {
        return super.getCatalogId();
    }

    @Override
    @Equals("inquiry.publishType")
    public PublishType getPublishType()
    {
        return super.getPublishType();
    }

    @Override
    @Equals("inquiry.publishType")
    public void setPublishType(PublishType publishType)
    {
        super.setPublishType(publishType);
    }

    public ProcessState getState()
    {
        return state;
    }

    public void setState(ProcessState state)
    {
        this.state = state;
    }

    public ProcessState[] getStates()
    {
        return states;
    }

    public void setStates(ProcessState[] states)
    {
        this.states = states;
    }

    public boolean isAutoAccept()
    {
        return autoAccept;
    }

    public void setAutoAccept(boolean autoAccept)
    {
        this.autoAccept = autoAccept;
    }

    public boolean isPublishTypeEditable()
    {
        return publishTypeEditable;
    }

    public void setPublishTypeEditable(boolean publishTypeEditable)
    {
        this.publishTypeEditable = publishTypeEditable;
    }

    public boolean isTurnable()
    {
        return turnable;
    }

    public void setTurnable(boolean turnable)
    {
        this.turnable = turnable;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    @Select(field = {"entity.inquiry.catalogId", "catalogId"})
    public InquiryCatalogTreeModel getCatalogTree()
    {
        return super.getCatalogTree();
    }

    @NotSerialized
    public boolean isInquiryEditable() throws Exception
    {
        if (getEntity() != null)
        {
            ProcessState state = getEntity().getState();
            return state != ProcessState.NOACCEPTED && state != ProcessState.TURNED;
        }

        return false;
    }

    public boolean isInquiryPublicity()
    {
        return getEntity() != null && getEntity().getInquiry() != null &&
                getEntity().getInquiry().isPublicity() != null && getEntity().getInquiry().isPublicity();
    }

    @NotSerialized
    public boolean isTimeLimitEditable()
    {
        return false;
    }

    private boolean containsState(ProcessState state)
    {
        if (states == null)
            return true;

        for (ProcessState state1 : states)
        {
            if (state1 == state)
                return true;
        }

        return false;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "lastProcess=1 and inquiry.deleted=0";

        if (getTimeoutTypeId() != null)
        {
            s += " and processId in (select recordId from Timeout t where typeId in :timeoutTypeId " +
                    "and valid=1 and levelId in ?timeoutLevelId and timeoutTime>?timeoutTime_start " +
                    "and timeoutTime<=?timeoutTime_end)";
        }

        return s;
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        InquiryProcess inquiryProcess = getEntity();
        if (inquiryProcess.getRead() == null || !inquiryProcess.getRead())
        {
            inquiryProcess.setRead(true);
            update(inquiryProcess);
        }

        if (autoAccept)
        {
            if (getEntity().getState() == ProcessState.NOACCEPTED)
            {
                getEntity().setState(ProcessState.PROCESSING);
                getEntity().setAcceptTime(new java.util.Date());
                update(getEntity());
            }

            Inquiry inquiry = getEntity().getInquiry();
            if (inquiry.getState() == InquiryState.NOACCEPTED)
            {
                inquiry.setState(InquiryState.PROCESSING);
                service.getDao().update(inquiry);
            }
        }
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if (getEntity().getAcceptTime() == null)
            getEntity().setAcceptTime(new java.util.Date());

        getEntity().setUserId(userId);

        Inquiry inquiry = getEntity().getInquiry();
        if (inquiry != null)
        {
            inquiry.setInquiryId(getEntity().getInquiryId());

            if (inquiry.getState() == InquiryState.REPLYED)
                inquiry.setReplyTime(new java.util.Date());
            else if (inquiry.getState() != null)
                inquiry.setReplyTime(Null.Timestamp);

            inquiry.setLastTime(new java.util.Date());
            service.getDao().update(inquiry);
        }

        if (getEntity().getState() == ProcessState.REPLYED || getEntity().getState() == ProcessState.TURNED)
        {
            getEntity().setEndTime(new java.util.Date());
        }
        else if (getEntity().getState() != null)
        {
            getEntity().setEndTime(Null.Timestamp);
        }

        return true;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public Long turn(Integer deptId) throws Exception
    {
        getEntity().setUserId(userId);

        return service.turn(getEntity(), deptId);
    }

    @Override
    protected Long getInquiryId(InquiryProcess entity) throws Exception
    {
        return entity.getInquiryId();
    }

    @Override
    @Forwards({
            @Forward(page = "/portal/inquiry/process.ptl"),
            @Forward(name = "print", page = "/portal/inquiry/process_print.ptl"),
            @Forward(name = "print2", page = "/portal/inquiry/process_print.docp")
    })
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        if ("list".equals(page))
        {
            SimplePageListView view = new SimplePageListView();
            view.setDisplay(new CUnion(
                    new CLabel(new HrefCell("inquiry.title").setPrompt("${inquiry.title}").setAction(
                            "process(${processId})")).setClass(
                            "title ${((read==null||!read)&&state.name()=='NOACCEPTED')?'title_noread':'title_readed'}"),
                    new CLabel(new FieldCell("inquiry.sendTime").setFormat("yyyy-MM-dd")).setClass("time")));

            view.addAction(Action.more());

            view.importJs("/portal/inquiry/process.js");

            return view;
        }

        Collection<Integer> authDeptIds = getAuthDeptIds();

        PageTableView view = new PageTableView(false);

        view.setClass("${((read==null||!read)&&state.name()=='NOACCEPTED')?'new_bold':''}");

        view.addComponent("标题", "title");
        view.addComponent("内容", "content");

        if (getTimeoutTypeId() == null)
            view.addComponent("时间", "time_start", "time_end");
        else
            view.addComponent("超时时间", "timeoutTime_start", "timeoutTime_end");

        if (authDeptIds == null || authDeptIds.size() > 3)
            view.addComponent("处理部门", "deptIds");

        view.addMoreComponent("来信人", "inquirerName");
        view.addMoreComponent("编号", "code");

        if (states == null || states.length > 1)
            view.addMoreComponent("状态", "state");
        if (getWayId() == null)
        {
            if (getInquiryWays().size() > 1)
                view.addMoreComponent("收信渠道", "wayId");
        }
        view.addMoreComponent("类型", "typeId");
        view.addColumn("来信编号", "inquiry.code").setWidth("150");
        view.addColumn("标题", new HrefCell("inquiry.title").setAction("process(${processId})")).setWrap(true);
        view.addColumn("来信人", "inquiry.inquirerName").setWidth("80").setWrap(true);
        view.addColumn("来信时间", new FieldCell("inquiry.sendTime", "yyy-MM-dd HH:mm")).setWidth("130");

        initConfig(view);

        if (authDeptIds == null || authDeptIds.size() > 1)
        {
            view.addColumn("处理部门", "dept.deptName").setWidth("140").setWrap(true);
        }

        if (turnable)
        {
            view.addColumn("转自", "previousProcess.dept.deptName").setWidth("100").setWrap(true);
            view.addColumn("转达时间", new FieldCell("previousProcessId==null?null:startTime", "yyy-MM-dd HH:mm"))
                    .setOrderFiled("startTime").setWidth("130");
        }

        if (states == null || states.length > 0)
            view.addColumn("状态", "state").setWidth("50");

        if (containsState(ProcessState.REPLYED) || containsState(ProcessState.TURNED))
            view.addColumn("处理时间", new FieldCell("endTime", "yyy-MM-dd HH:mm")).setWidth("120");

        view.addColumn("公开方式", "inquiry.state.name()=='REPLYED'?inquiry.publishType:''")
                .setOrderFiled("inquiry.publishType").setWidth("80").setAlign(Align.center);

        if (isShowTimeout())
        {
            view.addColumn("警告", new ConditionComponent().add("validTimeout!=null",
                    new CImage("/timeout/level/icon/${validTimeout.levelId}").setPrompt("${validTimeout}")
                            .setProperty("style", "cursor:pointer")
            )).setWidth("45");
        }

        view.addColumn("处理", new CButton("处理", "process(${processId})")).setWidth("45");

        view.addButton(Buttons.query());

        view.addButton(Buttons.export("xls"));

        view.importJs("/portal/inquiry/process.js");

        return view;
    }
}
