package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 退文登记表
 *
 * @author ldp
 * @date 2018/1/15
 */
@Service(url = "/ods/exchange/backrecord")
public class BackRecordCrud extends DeptOwnedNormalCrud<BackRecord, Long>
{

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Like
    private String title;

    @Like
    private String sendNumber;

    @Lower(column = "backTime")
    private java.sql.Date startDate;

    @Upper(column = "backTime")
    private java.sql.Date endDate;

    @Like
    private String sourceDept;


    public BackRecordCrud()
    {
        addOrderBy("backTime", OrderType.desc);
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

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    @Override
    public String getOrderField()
    {
        return null;
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

        view.addComponent("文件名称", "title");
        view.addComponent("发文字号", "sendNumber");
        view.addComponent("发文单位", "sourceDept");
        view.addComponent("退文时间", "startDate", "endDate");

        view.addColumn("退文编号", "backNo").setWidth("100");
        view.addColumn("文件名称", "title");
        view.addColumn("发文字号", "sendNumber").setWidth("100");
        view.addColumn("退文时间", "backTime").setWidth("130");
        view.addColumn("退文人", "backUserName").setWidth("100");
        view.addColumn("发文单位", "sourceDept").setWidth("150");
        view.addColumn("退文理由", "reason").setWidth("200");
        view.addColumn("错情摘要", "remark").setWidth("200");

        view.defaultInit(false);
        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    public boolean isReadOnly()
    {
        return !("show".equals(getAction()) && getEntity() != null && getEntity().getBackId() != null) &&
                super.isReadOnly();
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("退文编号", "backNo").setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("文件名称", "title").setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("发文字号", "sendNumber").setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("退文时间", "backTime").setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("退文人", "backUserName").setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("发文单位", "sourceDept").setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("退文理由", new CTextArea("reason")).setProperty("readonly", "${crud$.readOnly}");
        view.addComponent("错情摘要", new CTextArea("remark")).setProperty("readonly", "${crud$.readOnly}");

        view.addDefaultButtons();

        return view;
    }
}
