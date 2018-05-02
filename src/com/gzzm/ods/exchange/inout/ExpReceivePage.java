package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.query.OdDocFile;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.util.*;

/**
 * 导出收文
 *
 * @author LDP
 * @date 2017/4/14
 */
@Service(url = "/ods/exchange/inout/expreceivepage")
public class ExpReceivePage extends DeptOwnedQuery<ReceiveBase, Long>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 文件名称
     */
    @Like("document.title")
    private String title;

    /**
     * 原文号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    /**
     * 来文单位
     */
    @Like("document.sourceDept")
    private String sourceDept;

    @Lower(column = "sendTime")
    private Date sendTime_start;

    @Upper(column = "sendTime")
    private Date sendTime_end;

    @Lower(column = "acceptTime")
    private Date acceptTime_start;

    @Upper(column = "acceptTime")
    private Date acceptTime_end;

    /**
     * 状态
     */
    private ReceiveState[] state;

    /**
     * 收文类型
     */
    private ReceiveType[] type;

    @Contains("document.textContent")
    private String text;

    @Equals("state")
    private ReceiveState state1;

    public ExpReceivePage()
    {
        addOrderBy("nvl(sendTime,acceptTime)", OrderType.desc);
        addOrderBy("acceptTime", OrderType.desc);

        addForceLoad("document");

        setState1(ReceiveState.noAccepted);
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public Date getSendTime_start()
    {
        return sendTime_start;
    }

    public void setSendTime_start(Date sendTime_start)
    {
        this.sendTime_start = sendTime_start;
    }

    public Date getSendTime_end()
    {
        return sendTime_end;
    }

    public void setSendTime_end(Date sendTime_end)
    {
        this.sendTime_end = sendTime_end;
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

    public ReceiveType[] getType()
    {
        return type;
    }

    public void setType(ReceiveType[] type)
    {
        this.type = type;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public ReceiveState getState1()
    {
        return state1;
    }

    public void setState1(ReceiveState state1)
    {
        this.state1 = state1;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if(getAuthDeptIds() == null || getAuthDeptIds().size() > 3)
            setDefaultDeptId();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if(getDeptId() == null && (getAuthDeptIds() == null || getAuthDeptIds().size() > 3))
            setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", true) :
                new PageTableView(true);

        view.addComponent("文件名", "title");
        view.addComponent("来文单位", "sourceDept");
        view.addComponent("发文时间", "sendTime_start", "sendTime_end");
        view.addComponent("文件内容", "text");

        if(state == null)
        {
            view.addComponent("状态", new CCombox("state1", new ReceiveState[]{
                    ReceiveState.noAccepted,
                    ReceiveState.accepted,
                    ReceiveState.flowing,
                    ReceiveState.end
            }));
        }

        if(!showDeptTree && authDeptIds.size() > 1)
            view.addComponent("收文部门", "deptIds");

        view.addMoreComponent("原文号", "sendNumber");

        if(state == null || state.length != 1 || state[0] != ReceiveState.noAccepted)
            view.addMoreComponent("接收时间", "acceptTime_start", "acceptTime_end");

        view.setClass("${state.name()=='noAccepted'?'new_bold':''}");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${documentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称",
                new HrefCell("document.titleText").setAction("showReceive(${receiveId},'${state.name()}')"))
                .setOrderFiled("document.title").setWrap(true);

        view.addColumn("来文单位", "document.sourceDept").setWidth("90").setWrap(true);

        if(authDeptIds == null || authDeptIds.size() > 1)
        {
            view.addColumn("收文部门", "dept.deptName").setWidth("90").setWrap(true);
        }

        view.addColumn("原文号", "document.sendNumber").setWidth("95").setWrap(true);

        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("80")
                .setAlign(Align.center).setWrap(true);
        if(state == null || state.length > 1 || state[0] != ReceiveState.noAccepted)
        {
            view.addColumn("接收时间", new FieldCell("acceptTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("75")
                    .setAlign(Align.center).setWrap(true);
        }

        view.addColumn("类型", "type.name()=='receive'?sendType:type").setAlign(Align.center).setWidth("40")
                .setWrap(true);


        if(state == null || state.length > 1)
            view.addColumn("状态", "state").setWidth("50");

        view.addButton(Buttons.query());
        view.addButton(Buttons.getButton("导出", "receiveExport()"));
        view.importJs("/ods/exchange/inout/inout.js");
        view.importCss("/ods/exchange/receive.css");

        return view;
    }

    /**
     * @param remark 文件描述，描述本次导出文件，便于区分
     *               批量导出收文文件，每个文件单独导出成一个json文件，并打包成zip包
     *               此方法不直接返回文件输出流，而是将文件存放在指定的位置（根据系统配置，通过框架附件表存储），需要时去下载。
     */
    @Service(method = HttpMethod.post)
    public void expDocuments(final Long[] receiveIds, String remark) throws Exception
    {
        if(receiveIds == null || receiveIds.length == 0) return;

        new DocExpFileGenerator()
        {
            private CacheData cache = new CacheData();

            @Override
            protected void doExport(OdDocFile docFile) throws Exception
            {
                CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);

                for (Long receiveId : receiveIds)
                {
                    Map<String, Object> map = new HashMap<String, Object>();

                    ReceiveBase receiveBase = getEntity(receiveId);
                    map.put("receiveBase", DataUtils.createReceiveBaseMap(receiveBase));

                    OfficeDocument document = receiveBase.getDocument();
                    Map<String, Object> docMap = DataUtils.createDocumentDataMap(document);
                    map.put("document", docMap);

                    docMap.put("attributes", document.getAttributes());
                    docMap.put("text", DataUtils.createTextDataMap(document.getText()));
                    docMap.put("attachments", DataUtils.createAttachmentList(document.getAttachments()));
                    docMap.put("receiverList", DataUtils.createReceiverList(document.getReceiverList()));

                    //如果公文标题为空，为了避免文件名重复，将文件名定义为当前时间的毫秒数
                    zip.addFile((StringUtils.isBlank(document.getTitle()) ? System.currentTimeMillis() :
                            document.getTitle()) + ".json", new ByteArrayInputStream(
                            new JsonSerializer().serialize(map).toString().getBytes("UTF-8")));
                }

                zip.close();

                docFile.setAttachmentId(DataUtils
                        .saveAttachment(new Inputable.StreamInput(cache.getInputStream()), "收文数据.zip",
                                userOnlineInfo, "ods_export_receive", docFile.getRemark()));
            }

            @Override
            protected void successHandler()
            {
                try
                {
                    for (Long receiveId : receiveIds)
                    {
                        ReceiveBase receiveBase = getEntity(receiveId);
                        if(receiveBase.getState() == ReceiveState.noAccepted)
                        {
                            receiveBase.setState(ReceiveState.accepted);
                            getCrudService().update(receiveBase);
                        }
                    }
                }
                catch (Exception e)
                {
                    Tools.log(e);
                }
            }

            @Override
            protected void finallyHandler()
            {
                //清除缓存的文件
                cache.clear();
            }
        }.generate(remark, userOnlineInfo, 3);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "";
        if(state == null)
            s = "state<4";

        if(!StringUtils.isEmpty(s))
            return s;
        else
            return null;
    }
}
