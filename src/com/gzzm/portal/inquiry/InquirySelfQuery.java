package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 查询跟踪自己或本部门录入的咨询投诉
 *
 * @author camel
 * @date 12-11-7
 */
@Service(url = "/portal/inquiry/self")
public class InquirySelfQuery extends DeptOwnedQuery<Inquiry, Long>
{
    @Inject
    private InquiryDao dao;

    @Lower(column = "sendTime")
    private Date time_start;

    @Upper(column = "startTime")
    private Date time_end;

    @Like
    private String title;

    @Like
    private String inquirerName;

    @Like
    private String code;

    private InquiryState state;

    @Contains("text")
    private String content;

    private Integer typeId;

    private Integer catalogId;

    private AuthDeptTreeModel inquiryDeptTree;

    private List<Integer> inquiryDeptIds;

    private InquiryCatalogTreeModel catalogTree;

    public InquirySelfQuery()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getInquirerName()
    {
        return inquirerName;
    }

    public void setInquirerName(String inquirerName)
    {
        this.inquirerName = inquirerName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public InquiryState getState()
    {
        return state;
    }

    public void setState(InquiryState state)
    {
        this.state = state;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    @Select(field = {"inquiry.catalogId", "catalogId"})
    public InquiryCatalogTreeModel getCatalogTree()
    {
        if (catalogTree == null)
            catalogTree = new InquiryCatalogTreeModel();

        return catalogTree;
    }

    public List<Integer> getInquiryDeptIds()
    {
        return inquiryDeptIds;
    }

    public void setInquiryDeptIds(List<Integer> inquiryDeptIds)
    {
        this.inquiryDeptIds = inquiryDeptIds;
    }

    @NotSerialized
    @Select(field = "typeId")
    public List<InquiryType> getInquiryTypes() throws Exception
    {
        return dao.getTypes();
    }

    @NotSerialized
    public boolean isInquiryEditable() throws Exception
    {
        return false;
    }

    @NotSerialized
    public boolean isPublishTypeEditable() throws Exception
    {
        return false;
    }

    public boolean isInquiryPublicity()
    {
        return getEntity() != null && getEntity().isPublicity() != null && getEntity().isPublicity();
    }

    @NotSerialized
    public boolean isTimeLimitEditable()
    {
        return false;
    }

    @NotSerialized
    public boolean isShowTimeout() throws Exception
    {
        return false;
    }

    public InquiryConfig getInquiryConfig() throws Exception
    {
        return null;
    }

    @Override
    @In("inquireDeptId")
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Select(field = "inquiryDeptIds")
    public AuthDeptTreeModel getInquiryDeptTree()
    {
        if (inquiryDeptTree == null)
        {
            inquiryDeptTree = new AuthDeptTreeModel();
            inquiryDeptTree.setShowBox(true);
            inquiryDeptTree.setAppId(Constants.INQUIRY_DEPT_SELECT);
        }

        return inquiryDeptTree;
    }

    @Override
    public Integer getDeptId(Inquiry entity) throws Exception
    {
        return entity.getInquireDeptId();
    }

    @Override
    public String getComplexCondition() throws Exception
    {
        //只有此部门处理过，就查询出来
        if (getInquiryDeptIds() != null)
        {
            return "exists p in processes : p.deptId in :inquiryDeptIds";
        }

        return null;
    }

    @Override
    @Forward(page = "/portal/inquiry/inquiry.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("标题", "title");
        view.addComponent("时间", "time_start", "time_end");
        view.addComponent("状态", "state");
        view.addComponent("部门", "inquiryDeptIds");

        view.addMoreComponent("来信人", "inquirerName");
        view.addMoreComponent("编号", "code");
        view.addMoreComponent("内容", "content");
        view.addMoreComponent("类型", "typeId");

        view.addColumn("标题", "title").setWrap(true);
        view.addColumn("来信人", "inquirerName").setWidth("100").setWrap(true);
        view.addColumn("来信时间", new FieldCell("sendTime", "yyy-MM-dd HH:mm")).setWidth("120");
        view.addColumn("处理部门", new FieldCell("lastProcess.dept.deptName").setOrderable(false)).setWidth("140").setWrap(true);
        view.addColumn("状态", "state").setWidth("60");
        view.addColumn("回复时间", new FieldCell("replyTime", "yyy-MM-dd HH:mm")).setWidth("120");
        view.addColumn("公开方式", "state.name()=='REPLYED'?publishType:''").setOrderFiled("publishType").setWidth("95")
                .setAlign(Align.center);

        view.addColumn("查看", new CButton("查看", "display(${inquiryId})")).setWidth("45");

        view.addButton(Buttons.query());

        view.importJs("/portal/inquiry/self.js");

        return view;
    }
}
