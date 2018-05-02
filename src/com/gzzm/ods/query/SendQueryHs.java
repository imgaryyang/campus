package com.gzzm.ods.query;

import com.gzzm.ods.bak.OdDeptBakService;
import com.gzzm.ods.exchange.Send;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
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
 * 单位发文
 *
 * @author LDP
 * @date 2015/11/19
 */
@Service(url = "/ods/sendqueryhs")
public class SendQueryHs extends DeptOwnedQuery<Send, Integer>
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
     * 公文标题
     */
    @Like("document.title")
    private String title;

    @Lower(column = "sendTime")
    private Date date_start;

    @Upper(column = "sendTime")
    private Date date_end;

    @Contains("document.textContent")
    private String text;

    /**
     * 来文单位部门ID
     */
    @NotCondition
    private List<Integer> mainReceiveDeptIds;

    /**
     * 主送单位（查询条件）
     */
    @Contains("document.mainReceivers")
    private String mainReceivers;

    private AuthDeptTreeModel mainReceiverDeptTree;

    public SendQueryHs()
    {
        addOrderBy("sendTime", OrderType.desc);
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

    public Date getDate_start()
    {
        return date_start;
    }

    public void setDate_start(Date date_start)
    {
        this.date_start = date_start;
    }

    public Date getDate_end()
    {
        return date_end;
    }

    public void setDate_end(Date date_end)
    {
        this.date_end = date_end;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public List<Integer> getMainReceiveDeptIds()
    {
        return mainReceiveDeptIds;
    }

    public void setMainReceiveDeptIds(List<Integer> mainReceiveDeptIds)
    {
        this.mainReceiveDeptIds = mainReceiveDeptIds;
    }

    public String getMainReceivers() throws Exception
    {
        if(CollectionUtils.isNotEmpty(getMainReceiveDeptIds()))
        {
            if(getMainReceiveDeptIds().size() == 1)
            {
                mainReceivers = docFileDaoProvider.get().get(Dept.class, getMainReceiveDeptIds().get(0)).getDeptName();
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                for (Integer id : getMainReceiveDeptIds())
                {
                    sb.append(docFileDaoProvider.get().get(Dept.class, id).getDeptName()).append("|");
                }
                mainReceivers = sb.deleteCharAt(sb.length() - 1).toString();
            }
        }
        return mainReceivers;
    }

    @Select(field = "mainReceiveDeptIds")
    public AuthDeptTreeModel getMainReceiveDeptTree()
    {
        if(mainReceiverDeptTree == null)
        {
            mainReceiverDeptTree = new AuthDeptTreeModel();
            mainReceiverDeptTree.setFull(true);
            mainReceiverDeptTree.setShowBox(true);
            mainReceiverDeptTree.setAppId(Member.DEPT_SELECT_APP);
        }
        return mainReceiverDeptTree;
    }

    @Override
    protected String getUserIdField() throws Exception
    {
        return "creator";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return " state = 0 ";
    }

    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("主送单位", "mainReceiveDeptIds");
        view.addComponent("文件标题", "title");
        view.addComponent("单位发文日期", "date_start", "date_end");
        view.addComponent("文件内容", "text");

        view.addColumn("单位发文日期", new FieldCell("sendTime").setFormat("yyyy-MM-dd")).setWidth("85")
                .setAlign(Align.center);
        view.addColumn("办理时限", "document.attributes['办理时限']").setWidth("75").setAlign(Align.center).setWrap(true);
        view.addColumn("主送单位", "document.mainReceivers").setWidth("150").setWrap(true);
        view.addColumn("抄送单位", "document.otherReceivers").setWidth("150").setWrap(true);
        view.addColumn("文号", "document.sendNumber").setWidth("95").setWrap(true);
        view.addColumn("文件标题", new HrefCell("document.title").setAction("openFlow(${sendId})"));
        view.addColumn("拟稿人", "createUser.userName").setWidth("50").setWrap(true);
        view.addColumn("发文渠道", new CLabel("鹤山市政务OA")).setWidth("100").setAlign(Align.center);
//        view.addColumn("反馈文档").setWidth("80");

        view.addColumn("跟踪", new CHref("跟踪").setAction("track(${sendId})")).setWidth("40");
        view.addColumn("补发", new CHref("补发").setAction("reSendDoc(${sendId},${deptId})")).setWidth("40");

        view.addButton(Buttons.query());
        view.addButton("批量导出", "downDocFile()");

        view.importJs("/ods/query/sendqueryhs.js");

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
        final List<Send> sends = loadExportData();
        if(sends == null || sends.size() < 1)
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
                OdDeptBakService odDeptBakService = odDeptBakServiceProvider.get();
                CacheData cache = new CacheData();

                try
                {
                    CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);

                    zip.addFile("发文列表.xls", listCache.getInputStream());

                    for (Send send : sends)
                    {
                        odDeptBakService.zip(send, zip);
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
        OdDocFile docFile = docFileDaoProvider.get().getDocFile(getCurUserId(), 0);//发文

        if(docFile == null)
        {
            docFile = new OdDocFile();
            docFile.setUserId(getCurUserId());
            docFile.setDocFileType(0);
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
