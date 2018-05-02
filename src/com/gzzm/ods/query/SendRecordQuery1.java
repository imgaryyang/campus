package com.gzzm.ods.query;

import com.gzzm.ods.exchange.SendRecord;
import com.gzzm.ods.sendnumber.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.DeptService;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 发文登记表，此查询为清远两办设计
 *
 * @author camel
 * @date 13-12-5
 */
@Service(url = "/ods/query/sendrecord1")
public class SendRecordQuery1 extends DeptOwnedQuery<SendRecord, Long>
{
    @Inject
    private SendNumberDao sendNumberDao;

    @Inject
    private DeptService deptService;

    @Like
    private String title;

    @Like
    private String sendNumber;

    private Integer sendNumberId;

    @Like
    private String creator;

    @Like
    private String createDeptName;

    @Like
    private String signer;

    @Lower(column = "recordTime")
    private java.sql.Date time_start;

    @Upper(column = "recordTime")
    private java.sql.Date time_end;

    public SendRecordQuery1()
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

    @NotSerialized
    @Select(field = "sendNumberId")
    public List<SendNumber> getSendNumbers() throws Exception
    {
        if (getAuthDeptIds() != null)
        {
            List<SendNumber> sendNumbers = new ArrayList<SendNumber>();
            Set<Integer> loadDeptIds = new HashSet<Integer>();

            for (Integer deptId : getAuthDeptIds())
            {
                List<SendNumber> sendNumbers1 = getSendNumbers(deptId, loadDeptIds);

                if (sendNumbers1 != null)
                    sendNumbers.addAll(sendNumbers1);
            }

            return sendNumbers;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private List<SendNumber> getSendNumbers(Integer deptId, Set<Integer> loadDeptIds) throws Exception
    {
        if (loadDeptIds.contains(deptId))
            return null;

        loadDeptIds.add(deptId);

        List<SendNumber> sendNumbers = sendNumberDao.getSendNumbers(deptId);
        if (sendNumbers == null || sendNumbers.size() == 0)
        {
            Integer parentDeptId = deptService.getDept(deptId).getParentDeptId();
            if (parentDeptId != null && parentDeptId != 0)
                return getSendNumbers(parentDeptId, loadDeptIds);
        }

        return sendNumbers;
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
        if (getDeptId() != null)
        {
            return "deptId=:deptId or createDeptId=:deptId";
        }
        else
        {
            return "deptId in ?queryDeptIds or createDeptId in ?queryDeptIds";
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();

        PageTableView view = new PageTableView(false);

        view.addComponent("发文类型", "sendNumberId").setWidth("120px");
        if (authDeptIds == null || authDeptIds.size() > 1)
            view.addComponent("承办科室", "deptIds");
        view.addComponent("标题", "title");
        view.addComponent("文号", "sendNumber");
        view.addComponent("登记时间", "time_start", "time_end");

        view.addMoreComponent("承办人", "creator");
        view.addMoreComponent("签发人", "signer");

        view.addColumn("签发日期", new FieldCell("signTime", "yyyy-MM-dd")).setWidth("95");
        view.addColumn("文号", "sendNumber").setWidth("120").setWrap(true);
        view.addColumn("标题", "title").setAutoExpand(true).setWrap(true);
        view.addColumn("主送单位", "mainSendDepts").setWidth("140");
        view.addColumn("签发人", "signer").setWidth("90").setWrap(true);
        view.addColumn("承办单位（科室）", "createDeptName").setWidth("140").setWrap(true);
        view.addColumn("承办人", "creator").setWidth("90").setWrap(true);
        view.addColumn("备注", "remark").setWidth("90").setWrap(true);

        view.defaultInit(false);
        view.addButton(Buttons.export("doc"));

        return view;
    }
}
