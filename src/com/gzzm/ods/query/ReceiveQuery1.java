package com.gzzm.ods.query;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receivetype.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 收文登记表，此查询为清远两办设计
 *
 * @author camel
 * @date 13-12-5
 */
@Service(url = "/ods/query/receive1")
public class ReceiveQuery1 extends DeptOwnedQuery<Receive, Long>
{
    @Inject
    private ReceiveTypeDao receiveTypeDao;

    @Like
    private String serial;

    @Like("receiveBase.document.sendNumber")
    private String sendNumber;

    @Lower(column = "receiveBase.acceptTime")
    private java.sql.Date time_start;

    @Upper(column = "receiveBase.acceptTime")
    private java.sql.Date time_end;

    @Like("receiveBase.document.sourceDept")
    private String sourceDept;

    @Like("receiveBase.document.title")
    private String title;

    @Like("dealUser.userName")
    private String dealer;

    @NotCondition
    private Integer receiveTypeId;

    @NotCondition
    @NotSerialized
    private List<Integer> receiveTypeIds;

    @In("receiveBase.state")
    private ReceiveState[] state;

    private ReceiveTypeTreeModel receiveTypeTree;

    public ReceiveQuery1()
    {
        addOrderBy("receiveBase.acceptTime", OrderType.desc);
        addOrderBy("receiveBase.sendTime", OrderType.desc);
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDealer()
    {
        return dealer;
    }

    public void setDealer(String dealer)
    {
        this.dealer = dealer;
    }

    public ReceiveState[] getState()
    {
        return state;
    }

    public void setState(ReceiveState[] state)
    {
        this.state = state;
    }

    public Integer getReceiveTypeId()
    {
        if (receiveTypeId != null && receiveTypeId == 0)
            return null;

        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public List<Integer> getReceiveTypeIds()
    {
        return receiveTypeIds;
    }

    @Select(field = "receiveTypeId")
    public ReceiveTypeTreeModel getReceiveTypeTree()
    {
        if (receiveTypeTree == null)
        {
            receiveTypeTree = new ReceiveTypeTreeModel();
            receiveTypeTree.setDeptId(userOnlineInfo.getDeptId());
        }
        return receiveTypeTree;
    }

    @Override
    @NotCondition
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    @NotCondition
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (time_start == null && time_end == null)
            time_start = new java.sql.Date(DateUtils.addDate(new java.util.Date(), -30).getTime());
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s;

        if (getDeptId() != null)
        {
            s = "(receiveBase.deptId=:deptId or dealDeptId=:deptId)";
        }
        else
        {
            s = "(receiveBase.deptId in ?queryDeptIds or dealDeptId in ?queryDeptIds)";
        }


        Integer receiveTypeId = getReceiveTypeId();
        if (receiveTypeId != null)
        {
            if (receiveTypeId == -1)
            {
                s += " and receiveTypeId is null";
            }
            else if (receiveTypeId > 0)
            {
                com.gzzm.ods.receivetype.ReceiveType receiveType = receiveTypeDao.getReceiveType(receiveTypeId);

                if (receiveType != null)
                {
                    receiveTypeIds = receiveType.getAllReceiveTypeIds();

                    s += " and receiveTypeId in :receiveTypeIds";
                }
            }
        }

        return s;
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();

        PageTableView view = new PageTableView(false);

        view.addComponent("收文类型", "receiveTypeId");

        if (authDeptIds == null || authDeptIds.size() > 1)
            view.addComponent("承办科室", "deptIds");

        view.addComponent("标题", "title");
        view.addComponent("文号", "sendNumber");
        view.addComponent("接收时间", "time_start", "time_end");

        view.addMoreComponent("收文编号", "serial");
        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("承办人", "signer");

        view.addColumn("收文编号", "serial").setWidth("120").setWrap(true);
        view.addColumn("日期", new FieldCell("receiveBase.acceptTime", "yyyy-MM-dd")).setWidth("95");
        view.addColumn("来文单位", "receiveBase.document.sourceDept").setWidth("140").setWrap(true);
        view.addColumn("文号", "receiveBase.document.sendNumber").setWidth("120").setWrap(true);
        view.addColumn("标题", "receiveBase.document.title").setAutoExpand(true).setWrap(true);
        view.addColumn("承办科室", "dealDept.deptName").setWidth("100").setWrap(true);
        view.addColumn("承办人", "dealUser.userName").setWidth("90").setWrap(true);
        view.addColumn("备注", "''").setWidth("90").setWrap(true);

        view.defaultInit(false);
        view.addButton(Buttons.export("doc"));

        return view;
    }
}
