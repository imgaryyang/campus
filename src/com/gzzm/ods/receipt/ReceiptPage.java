package com.gzzm.ods.receipt;

import com.gzzm.ods.document.*;
import com.gzzm.ods.flow.Constants;
import com.gzzm.platform.group.*;
import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.workday.WorkDayService;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.KeyValue;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和回执相关的web入口
 *
 * @author camel
 * @date 12-4-8
 */
@Service
public class ReceiptPage extends ReceiptBasePage
{
    @Inject
    private WorkDayService workDayService;

    /**
     * 回执类型
     */
    @Require
    private String type;

    private Long receiptId;

    @Require
    private Integer selectedDeptId;

    private java.util.Date deadline;

    private java.util.Date warningTime;

    @NotSerialized
    private Integer[] deptIds;

    /**
     * 已选单位
     */
    @NotSerialized
    private List<Dept> deptList;

    /**
     * 主送单位
     */
    @NotSerialized
    private List<Dept> mainDeptList;

    public ReceiptPage()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Integer getSelectedDeptId()
    {
        return selectedDeptId;
    }

    public void setSelectedDeptId(Integer selectedDeptId)
    {
        this.selectedDeptId = selectedDeptId;
    }

    public java.util.Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(java.util.Date deadline)
    {
        this.deadline = deadline;
    }

    public Date getWarningTime()
    {
        return warningTime;
    }

    public void setWarningTime(Date warningTime)
    {
        this.warningTime = warningTime;
    }

    public Integer[] getDeptIds()
    {
        return deptIds;
    }

    public void setDeptIds(Integer[] deptIds)
    {
        this.deptIds = deptIds;
    }

    @Service
    @NotSerialized
    public List<Dept> getAvailableDeptList() throws Exception
    {
        return getMainDeptList();
    }

    @Service
    @NotSerialized
    public List<Dept> getDeptList() throws Exception
    {
        if (deptList == null && documentId != null)
        {
            Receipt receipt = service.getDao().getReceiptByDocumentId(documentId);
            if (receipt == null)
                deptList = new ArrayList<Dept>(getMainDeptList());
            else
                deptList = receipt.getReceiptDepts();
        }
        return deptList;
    }

    public List<Dept> getMainDeptList() throws Exception
    {
        if (mainDeptList == null && documentId != null)
        {
            OfficeDocument document = service.getDao().get(OfficeDocument.class, documentId);
            if (document == null)
                return null;

            DocumentReceiverList documentReceiverList = document.getReceiverList();
            if (documentReceiverList == null)
                return null;

            ReceiverListList receivers = documentReceiverList.getReceivers();
            if (receivers == null)
                return null;

            List<ReceiverList> receiverLists = receivers.getReceiverLists();
            if (receiverLists == null)
                return null;

            mainDeptList = new ArrayList<Dept>();
            for (ReceiverList receiverList : receiverLists)
            {
                String sendType = receiverList.getSendType();
                if (Constants.Document.MAINSEND.equals(sendType))
                {
                    List<Member> members = receiverList.getReceivers();
                    if (members == null)
                        continue;

                    for (Member member : members)
                    {
                        if (member.getType() == MemberType.dept)
                            mainDeptList.add(service.getDao().getDept(member.getId()));
                    }
                }
            }
        }

        return mainDeptList;
    }

    @NotSerialized
    @Select(field = "type")
    public List<KeyValue<String>> getTypes()
    {
        return ReceiptComponents.getComponents();
    }

    @NotSerialized
    public String getDeptName() throws Exception
    {
        if (selectedDeptId != null)
            return service.getDao().getDeptName(selectedDeptId);

        return null;
    }

    @Service(url = "/ods/receipt/edit")
    public String editReceipt() throws Exception
    {
        Receipt receipt = service.getDao().getReceiptByDocumentId(documentId);

        initDeptId();

        if (deptId == null)
            selectedDeptId = businessDeptId;
        else
            selectedDeptId = deptId;

        String editUrl = null;

        if (receipt == null)
        {
            //之前没有设定回执，新建回执
            if (type != null)
            {
                receipt = new Receipt();
                receipt.setDocumentId(documentId);
                receipt.setType(type);
                receipt.setDeptId(selectedDeptId);
                receipt.setCreator(userOnlineInfo.getUserId());
                service.getDao().add(receipt);

                OfficeDocument document = new OfficeDocument();
                document.setDocumentId(documentId);
                document.setReceiptId(receiptId);
                service.getDao().update(document);
            }
        }
        else
        {
            editUrl = getEditUrl(receipt);
            type = receipt.getType();
            receiptId = receipt.getReceiptId();
            deadline = receipt.getDeadline();
        }

        if (editUrl == null)
            return "receipt";

        RequestContext.getContext().redirect(editUrl);

        return null;
    }

    @Service
    @ObjectResult
    public String saveReceipt() throws Exception
    {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(receiptId);
        receipt.setDocumentId(documentId);
        receipt.setType(type);
        receipt.setDeptId(selectedDeptId);
        receipt.setDeadline(deadline);
        receipt.setCreator(userOnlineInfo.getUserId());
        receipt.setWarningTime(warningTime);

        if (deptIds != null && deptIds.length > 0)
        {
            List<Dept> depts = new ArrayList<Dept>(deptIds.length);
            for (Integer deptId : deptIds)
            {
                Dept dept = new Dept();
                dept.setDeptId(deptId);
                depts.add(dept);
            }
            receipt.setReceiptDepts(depts);
        }

        service.getDao().save(receipt);

        return getEditUrl(receipt);
    }

    private String getEditUrl(Receipt receipt) throws Exception
    {
        ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());

        String url = component.getEditUrl(receipt);

        if (url != null)
        {
            if (url.indexOf("?") > 0)
                url += "&";
            else
                url += "?";

            url += "businessDeptId=" + businessDeptId;

            if (deptId != null)
                url += "&deptId=" + deptId;
        }

        return url;
    }

    @ObjectResult
    @Service(url = "/ods/receipt/fillurl")
    public String getFillUrl() throws Exception
    {
        Receipt receipt = service.getDao().getReceiptByDocumentId(documentId);

        if (receipt != null)
        {
            ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());

            return component.getFillUrl(receipt, deptId, false);
        }
        else if (type != null)
        {
            ReceiptComponent component = ReceiptComponents.getComponent(type);

            return component.getFillUrl(documentId, deptId);
        }

        return null;
    }

    @Transactional
    @ObjectResult
    @Service(url = "/ods/receipt/delete")
    public void removeReceipt() throws Exception
    {
        Receipt receipt = service.getDao().getReceiptByDocumentId(documentId);

        if (receipt != null)
        {
            ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());
            component.delete(receipt.getReceiptId());

            service.getDao().delete(receipt);
        }
    }

    @Redirect
    @Service(url = "/ods/receipt/track/{$0}")
    public String track(Long receiptId) throws Exception
    {
        Receipt receipt = service.getDao().getReceipt(receiptId);
        if (receipt != null)
        {
            service.getDao().setReplyReaded(receiptId);

            ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());
            return component.getTrackUrl(receipt);
        }

        return null;
    }

    @Service
    public Date calculateWarningTime(Date deadline)
    {
        if (deadline == null || deadline.before(new Date())) return null;

        //当前时间
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int h_now = now.get(Calendar.HOUR_OF_DAY);

        //限时办结时间
        Calendar dc = Calendar.getInstance();
        dc.setTime(deadline);
        int h_d = dc.get(Calendar.HOUR_OF_DAY);
        int h_m = dc.get(Calendar.MINUTE);

        //计算有多少个小时
        int diff = workDayService.diff(now.getTime(), deadline, 0) * 24 - h_now + h_d;

        if (diff <= 24)
        {
            //一天以内，提前一半时间
            if (now.get(Calendar.DAY_OF_MONTH) != dc.get(Calendar.DAY_OF_MONTH) && (diff / 2) > h_d)
            {
                now.add(Calendar.HOUR_OF_DAY, diff / 2);
                now.set(Calendar.MINUTE, diff % 2 * 30);
                return now.getTime();
            }
            else
            {
                dc.add(Calendar.HOUR_OF_DAY, -diff / 2);
                dc.set(Calendar.MINUTE, -diff % 2 * 30);
                return dc.getTime();
            }
        }
        else if (diff > 24 && diff <= 120)
        {
            //1~5天，提前1天
            dc.setTime(workDayService.add(deadline, -1, 0));
        }
        else if (diff > 120 && diff <= 240)
        {
            //5~10天，提前2天
            dc.setTime(workDayService.add(deadline, -2, 0));
        }
        else if (diff > 240)
        {
            //大于10天，提前3天
            dc.setTime(workDayService.add(deadline, -3, 0));
        }

        dc.set(Calendar.HOUR_OF_DAY, h_d);
        dc.set(Calendar.MINUTE, h_m);
        return dc.getTime();
    }
}
