package com.gzzm.ods.exchange.back;

import com.gzzm.ods.exchange.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.template.TemplateInput;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.File;
import java.util.*;

/**
 * 退文单方式退文
 *
 * @author ldp
 * @date 2018/1/9
 */
@Service
public class BackPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private ExchangeReceiveService exchangeReceiveService;

    @Inject
    private BackService backService;

    @Inject
    private ExchangeNotifyService exchangeNotifyService;

    private boolean readOnly;

    private Long receiveId;

    private Long backId;

    private Long recordId;

    @NotSerialized
    private BackRecord record;

    private Integer backNumberId;

    private Integer paperId;

    private Integer deptId;

    @NotSerialized
    private List<BackNumber> backNumbers;

    public BackPage()
    {
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public Long getBackId() throws Exception
    {
        return backId;
    }

    public void setBackId(Long backId)
    {
        this.backId = backId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public BackRecord getRecord()
    {
        return record;
    }

    public void setRecord(BackRecord record)
    {
        this.record = record;
    }

    public Integer getBackNumberId()
    {
        return backNumberId;
    }

    public void setBackNumberId(Integer backNumberId)
    {
        this.backNumberId = backNumberId;
    }

    public Integer getPaperId()
    {
        return paperId;
    }

    public void setPaperId(Integer paperId)
    {
        this.paperId = paperId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public List<BackNumber> getBackNumbers() throws Exception
    {
        if (backNumbers == null)
            backNumbers = backService.getBackNumbers(deptId);
        return backNumbers;
    }

    @Service(url = "/ods/exchange/backbypaper")
    public String show() throws Exception
    {
        BackDao dao = backService.getDao();
        ReceiveBase receive = null;
        Back back;

        if (recordId != null)
        {
            record = dao.getBackRecord(recordId);
            backId = record.getBackId();
            deptId = record.getDeptId();

            back = record.getBack();
        }
        else if (backId != null)
        {
            back = dao.getBack(backId);
            receive = back.getReceiveBase();
            deptId = back.getDeptId();
        }
        else
        {
            receive = exchangeReceiveService.getDao().getReceiveBase(receiveId);
            deptId = receive.getDeptId();
            back = dao.getBackByReceiveId(receiveId);

            if (back == null)
            {
                back = new Back();
                back.setReceiveId(receiveId);
                back.setDeptId(deptId);
                back.setBackTime(new Date());
            }
            else
            {
                backId = back.getBackId();
            }
        }

        if (back != null && back.getPaperId() != null)
            paperId = back.getPaperId();

        if (record == null && back != null)
        {
            if (back.getBackId() != null)
                record = dao.getBackRecordByBackId(back.getBackId());

            if (record == null)
            {
                record = new BackRecord();
                record.setTitle(receive.getDocument().getTitle());
                record.setSendNumber(receive.getDocument().getSendNumber());
                record.setSourceDept(receive.getDocument().getSourceDept());
                record.setBackTime(back.getBackTime());
                if (back.getReason() != null)
                    record.setReason(new String(back.getReason()));
                record.setDeptId(deptId);
                record.setDept(receive.getDept());

                List<BackNumber> backNumbers = getBackNumbers();
                if (backNumbers != null && backNumbers.size() == 1)
                {
                    record.setBackNo(backService.getBackNumberString(backNumbers.get(0)));
                }
            }
        }

        return dao.getBackPaper(paperId).getPage();
    }

    @Transactional
    @Service(method = HttpMethod.post)
    public void save() throws Exception
    {
        Long backId = exchangeReceiveService.back(receiveId, record.getReason(), userOnlineInfo.getUserId());

        if (backId == null)
            return;

        ExchangeReceiveDao dao = exchangeReceiveService.getDao();

        Back back = dao.getBack(backId);
        back.setPaperId(paperId);
        dao.update(back);

        record.setDeptId(back.getReceiveBase().getDeptId());
        record.setBackId(backId);
        record.setTitle(back.getReceiveBase().getDocument().getTitle());
        record.setSendNumber(back.getReceiveBase().getDocument().getSendNumber());
        record.setBackTime(back.getBackTime());
        record.setBackUserId(back.getBackUserId());
        record.setSourceDept(back.getReceiveBase().getSourceDept().getDeptName());
        dao.save(record);

        try
        {
            exchangeNotifyService.sendBackMessage(backId);
        }
        catch (Throwable ex)
        {
            //发送消息失败，不影响其他逻辑
        }
    }

    @Service(url = "/ods/exchange/back/{backId}/paper/down")
    public InputFile downBackPaper() throws Exception
    {
        if (backId == null)
            throw new NullPointerException("backId cannot be null");

        Back back = backService.getDao().getBack(backId);
        BackPaper paper = back.getPaper();

        String path = "/temp/backpaper/" + paper.getPaperId() + ".xml";
        File file = new File(Tools.getAppPath(path));

        if (!file.exists() || paper.getLastModified() == null ||
                file.lastModified() < paper.getLastModified().getTime())
        {
            if (paper.getTemplate() == null)
                return null;

            IOUtils.streamToFile(paper.getTemplate(), file);
        }

        BackRecord record = backService.getDao().getBackRecordByBackId(backId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dept", record.getDept().getDeptName());
        params.put("backNo", record.getBackNo());
        params.put("title", record.getTitle());
        params.put("sendNumber", record.getSendNumber());
        params.put("backDate", DateUtils.toString(record.getBackTime(), "yyyy-MM-dd"));
        params.put("sourceDept", record.getSourceDept());
        params.put("reason", record.getReason());
        params.put("remark", record.getRemark());
        params.put("rejectDept", record.getDept().getDeptName());

        TemplateInput input = new TemplateInput(path, params, true);
        return new InputFile(input, record.getTitle() + "_退文单.doc");
    }
}
