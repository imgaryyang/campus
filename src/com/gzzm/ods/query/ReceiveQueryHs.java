package com.gzzm.ods.query;

import com.gzzm.ods.bak.OdDeptBakService;
import com.gzzm.ods.exchange.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 单位收文
 *
 * @author LDP
 * @date 2015/11/19
 */
@Service(url = "/ods/receivequeryhs")
public class ReceiveQueryHs extends DeptOwnedQuery<Receive, Integer>
{
    @Inject
    private static Provider<OdDocFileDao> docFileDaoProvider;

    @Inject
    private static Provider<OdDeptBakService> odDeptBakServiceProvider;

    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    @UserId
    private Integer curUserId;

    /**
     * 文件名称
     */
    @Like("receiveBase.document.title")
    private String title;

    @Lower(column = "receiveBase.acceptTime")
    private Date acceptTime_start;

    @Upper(column = "receiveBase.acceptTime")
    private Date acceptTime_end;

    /**
     * 状态
     */
    @In("receiveBase.state")
    private ReceiveState[] state = new ReceiveState[]{
            ReceiveState.accepted,
            ReceiveState.flowing,
            ReceiveState.end
    };

    @Contains("receiveBase.document.textContent")
    private String text;

    /**
     * 来文单位（查询条件）
     */
    @Like("receiveBase.document.sourceDept")
    private String sourceDept;

    public ReceiveQueryHs()
    {
        addOrderBy("receiveBase.sendTime", OrderType.desc);
        addOrderBy("receiveBase.acceptTime", OrderType.desc);
    }

    public Integer getCurUserId()
    {
        return curUserId;
    }

    public void setCurUserId(Integer curUserId)
    {
        this.curUserId = curUserId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getAcceptTime_start()
    {
        return acceptTime_start;
    }

    public void setAcceptTime_start(Date acceptTime_start)
    {
        this.acceptTime_start = acceptTime_start;
    }

    public Date getAcceptTime_end()
    {
        return acceptTime_end;
    }

    public void setAcceptTime_end(Date acceptTime_end)
    {
        this.acceptTime_end = acceptTime_end;
    }

    public ReceiveState[] getState()
    {
        return state;
    }

    public void setState(ReceiveState[] state)
    {
        this.state = state;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    @Equals("receiveBase.deptId")
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @In("receiveBase.deptId")
    @Override
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    protected String getUserIdField() throws Exception
    {
        return "receiveBase.receiver";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("来文单位", "sourceDept");
        view.addComponent("文件名", "title");
        view.addComponent("单位收文日期", "acceptTime_start", "acceptTime_end");
        view.addComponent("文件内容", "text");

        view.addColumn("单位收文日期", new FieldCell("receiveBase.acceptTime").setFormat("yyyy-MM-dd")).setWidth("85")
                .setWrap(true).setAlign(Align.center);
        view.addColumn("办理时限", "receiveBase.document.attributes['办理时限']").setWidth("75").setAlign(Align.center)
                .setWrap(true);
        view.addColumn("来文单位", "receiveBase.document.sourceDept").setWidth("70").setWrap(true);
        view.addColumn("文号", "receiveBase.document.sendNumber").setWidth("75").setWrap(true);
        view.addColumn("文件标题", new HrefCell("receiveBase.document.title").setAction("openFlow(${receiveId})"));
        view.addColumn("公文类型", "attributes['公文类型']").setWidth("80").setAlign(Align.center);
        view.addColumn("收文登记人", "receiveBase.receiverUser.userName").setWidth("80").setWrap(true);
        view.addColumn("单位收文编号", "serial").setWidth("85").setWrap(true);
        view.addColumn("办理状态", new CLabel("${receiveBase.state.name()== 'end' ? '办结' : '在办'}")).setAlign(Align.center)
                .setWidth("80");

        view.addColumn("跟踪", new ConditionComponent().add("receiveBase.state.name()!='noAccepted'",
                new CHref("跟踪").setAction("track(${receiveId})"))).setWidth("40");

        view.addButton(Buttons.query());
        view.addButton("批量导出", "downDocFile()");

        view.importJs("/ods/query/receivequeryhs.js");

        return view;
    }

    @Override
    protected Object createView(String action) throws Exception
    {
        if("expDocFile".equals(action))
            return createListView();
        return super.createView(action);
    }

    /**
     * 批量导出
     */
    @Service
    public void expDocFile() throws Exception
    {
        final List<Receive> receives = loadExportData();
        if(receives == null || receives.size() < 1)
            throw new NoErrorException("请先在列表查询出要导出的公文！");

        final CacheData listCache = new CacheData();
        exp("xls").writeTo(listCache);

        final OdDocFile docFile = saveDocFile();

        //起一条线程后台去生成文件
        Tools.run(new Runnable()
        {
            @Override
            public void run()
            {
                CacheData cache = new CacheData();
                OdDeptBakService odDeptBakService = odDeptBakServiceProvider.get();

                try
                {
                    CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);

                    zip.addFile("收文列表.xls", listCache.getInputStream());

                    for (Receive receive : receives)
                    {

                        odDeptBakService.zip(receive, zip);
                    }

                    zip.close();

                    Attachment attachment = new Attachment();
                    attachment.setInputable(new Inputable.StreamInput(cache.getInputStream()));
                    attachment.setAttachmentName("发文数据.zip");
                    attachment.setFileName("发文数据.zip");
                    attachment.setUserId(userOnlineInfo.getUserId());
                    attachment.setDeptId(userOnlineInfo.getDeptId());

                    List<Attachment> attachments = new ArrayList<Attachment>(1);
                    attachments.add(attachment);
                    docFile.setAttachmentId(attachmentServiceProvider.get().save(attachments));

                    docFile.setState(1);//生成完成
                    docFile.setFinishTime(new java.util.Date());
                }
                catch (Exception ex)
                {
                    docFile.setState(2);//生成失败
                    Tools.log(ex);
                }
                finally
                {
                    try
                    {
                        //清除缓存的文件
                        cache.clear();
                        docFileDaoProvider.get().update(docFile);
                    }
                    catch (Exception e)
                    {
                        Tools.log(e);
                    }
                }
            }
        });
    }

    private OdDocFile saveDocFile() throws Exception
    {
        OdDocFile docFile = docFileDaoProvider.get().getDocFile(getCurUserId(), 1);//收文

        if(docFile == null)
        {
            docFile = new OdDocFile();
            docFile.setUserId(getCurUserId());
            docFile.setDocFileType(1);
        }

        docFile.setActionTime(new java.util.Date());
        docFile.setState(0);//生成中
        docFile.setFinishTime(Null.Date);
        Long attachmentId = docFile.getAttachmentId();
        if(attachmentId != null) attachmentServiceProvider.get().delete(attachmentId);
        docFile.setAttachmentId(Null.Long);

        docFileDaoProvider.get().save(docFile);

        return docFile;
    }
}
