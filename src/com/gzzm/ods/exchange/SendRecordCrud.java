package com.gzzm.ods.exchange;

import com.gzzm.ods.sendnumber.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-8-28
 */
@Service(url = "/ods/sendrecord")
public class SendRecordCrud extends DeptOwnedNormalCrud<SendRecord, Long>
{
    @Inject
    private SendNumberDao sendNumberDao;

    @Like
    private String title;

    @Like
    private String sendNumber;

    /**
     * 主题词
     */
    @Like
    private String subject;

    @Lower(column = "recordTime")
    private java.sql.Date recordTime_start;

    @Upper(column = "recordTime")
    private java.sql.Date recordTime_end;

    private SendRecordState state;

    private Integer sendNumberId;

    @Like
    private String creator;

    @Like
    private String createDeptName;

    @Like
    private String signer;

    public SendRecordCrud()
    {
        addOrderBy("recordTime", OrderType.desc);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public java.sql.Date getRecordTime_end()
    {
        return recordTime_end;
    }

    public void setRecordTime_end(java.sql.Date recordTime_end)
    {
        this.recordTime_end = recordTime_end;
    }

    public java.sql.Date getRecordTime_start()
    {
        return recordTime_start;
    }

    public void setRecordTime_start(java.sql.Date recordTime_start)
    {
        this.recordTime_start = recordTime_start;
    }

    public SendRecordState getState()
    {
        return state;
    }

    public void setState(SendRecordState state)
    {
        this.state = state;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getCreateDeptName()
    {
        return createDeptName;
    }

    public void setCreateDeptName(String createDeptName)
    {
        this.createDeptName = createDeptName;
    }

    public String getSigner()
    {
        return signer;
    }

    public void setSigner(String signer)
    {
        this.signer = signer;
    }

    @NotSerialized
    @Select(field = {"sendNumberId", "entity.sendNumberId"})
    public List<SendNumber> getSendNumbers() throws Exception
    {
        return sendNumberDao.getSendNumbers(getDeptId());
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();
        getEntity().setRecordTime(new Date());
        getEntity().setState(SendRecordState.SENDED);
        return true;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 1;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", true) :
                new PageTableView(true);

        view.setCheckable("${documentId==null}");

        view.addComponent("公文标题", "title");
        if (!showDeptTree)
            view.addComponent("发文类型", "sendNumberId");
        view.addComponent("发文字号", "sendNumber");
        view.addComponent("登记时间", "recordTime_start", "recordTime_end");

        view.addMoreComponent("主题词", "subject");
        view.addMoreComponent("拟稿人", "creator");
        view.addMoreComponent("拟稿科室", "createDept");
        view.addMoreComponent("签发人", "signer");
        view.addMoreComponent("状态", "state");

        view.addColumn("登记时间", new FieldCell("recordTime", "yyyy-MM-dd HH:mm")).setWidth("110");
        view.addColumn("发文字号", "sendNumber").setWidth("100").setWrap(true);
        view.addColumn("公文标题", "title").setAutoExpand(true).setWrap(true);
        view.addColumn("密级", "secret").setWidth("50").setAlign(Align.center);
        view.addColumn("状态", "state").setWidth("60");
        view.addColumn("份数", "sendCount").setWidth("50");
        view.addColumn("拟稿人及科室", "creatorText").setWidth("90").setWrap(true);

        view.addColumn("签发人及日期", new FieldCell("signText").setOrderable(false)).setWidth("135");

        view.defaultInit(false);
        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        if (isNew$() || getEntity().getDocumentId() == null)
        {
            view.addComponent("公文标题", "title");
            view.addComponent("发文类型", "sendNumberId");
            view.addComponent("发文编号", "sendNumber");
            view.addComponent("主题词", "subject");
            view.addComponent("密级", "secret");
            view.addComponent("优先级", "priority");
            view.addComponent("份数", "sendCount");
            view.addComponent("拟稿人", "creator");
            view.addComponent("拟稿科室", "createDeptName");
            view.addComponent("签发人", "signer");
            view.addComponent("签发时间", "signTime");
            view.addComponent("发往部门", new CTextArea("sendDepts"));
            view.addComponent("备注", new CTextArea("remark"));
        }
        else
        {
            view.addComponent("公文标题", "title").setProperty("readOnly", null);
            view.addComponent("发文编号", "sendNumber");
            view.addComponent("主题词", "subject").setProperty("readOnly", null);
            view.addComponent("密级", "secret").setProperty("readOnly", null);
            view.addComponent("优先级", "priority").setProperty("readOnly", null);
            view.addComponent("份数", "sendCount").setProperty("readOnly", null);
            view.addComponent("拟稿人", "creator").setProperty("readOnly", null);
            view.addComponent("拟稿科室", "createDeptName").setProperty("readOnly", null);
            view.addComponent("签发人", "signer").setProperty("readOnly", null);
            view.addComponent("签发时间", "signTime").setProperty("readOnly", null);
            view.addComponent("发往部门", new CTextArea("sendDepts")).setProperty("readOnly", null);
            view.addComponent("备注", new CTextArea("remark"));
        }

        view.addDefaultButtons();

        return view;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("发文记录");
    }
}
