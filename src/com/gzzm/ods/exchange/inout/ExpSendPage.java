package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.query.OdDocFile;
import com.gzzm.ods.sendnumber.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.util.*;

/**
 * 导出发文
 *
 * @author LDP
 * @date 2017-4-27
 */
@Service(url = "/ods/exchange/inout/expsendpage")
public class ExpSendPage extends DeptOwnedQuery<Send, Long>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private static Provider<DocumentDao> documentDaoProvider;

    @Inject
    private SendNumberDao sendNumberDao;

    @Inject
    private DeptService deptService;

    @UserId
    private Integer userId;

    /**
     * 公文标题
     */
    @Like("document.title")
    private String title;

    /**
     * 发文编号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    @Lower(column = "sendTime")
    private Date date_start;

    @Upper(column = "sendTime")
    private Date date_end;

    @Contains("document.textContent")
    private String text;

    private SendState state;

    /**
     * 是否只查询自己拟稿的公文
     */
    private boolean self;

    @NotCondition
    private Integer sendNumberId;

    private Integer[] createDeptId;

    @Equals("creatorUser.userName")
    private String userName;

    private Integer creator;

    private AuthDeptTreeModel createDeptTree;

    public ExpSendPage()
    {
        addOrderBy("sendTime", OrderType.desc);

        addForceLoad("document");
        addForceLoad("createUser");

        //过滤出已没有被撤回的公文
        state = SendState.sended;
    }

    public Integer getUserId()
    {
        return userId;
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

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public SendState getState()
    {
        return state;
    }

    public void setState(SendState state)
    {
        this.state = state;
    }

    public boolean isSelf()
    {
        return self;
    }

    public void setSelf(boolean self)
    {
        this.self = self;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public Integer[] getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer[] createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    @NotSerialized
    @Select(field = "deptIds")
    public List<DeptInfo> getDepts() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        if(authDeptIds != null && authDeptIds.size() <= 3)
        {
            return deptService.getDepts(authDeptIds);
        }

        return null;
    }

    @NotSerialized
    @Select(field = "sendNumberId")
    public List<SendNumber> getSendNumbers() throws Exception
    {
        if(!self && getAuthDeptIds() != null && getAuthDeptIds().size() <= 3)
            return sendNumberDao.getSendNumbers(getAuthDeptIds());
        else
            return null;
    }

    @Select(field = "createDeptId")
    public AuthDeptTreeModel getCreateDeptTree() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        if(createDeptTree == null && !self && authDeptIds != null && authDeptIds.size() <= 3)
        {
            createDeptTree = new AuthDeptTreeModel();
            createDeptTree.setShowBox(true);
            DeptService deptService = createDeptTree.getService();
            List<Integer> deptIds = new ArrayList<Integer>();
            for (Integer deptId : authDeptIds)
            {
                deptIds.addAll(deptService.getDept(deptId).allSubDeptIds());
            }

            createDeptTree.setAuthDeptIds(deptIds);
        }
        return createDeptTree;
    }

    @Override
    public String getComplexCondition() throws Exception
    {
        String s = "";

        if(self)
            s = "creator=:userId";

        if(sendNumberId != null && sendNumberId > 0)
        {
            if(s.length() > 0)
                s += " and ";

            s += "sendNumberId=:sendNumberId";
        }

        if(!StringUtils.isEmpty(s))
            return s;
        else
            return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        Collection<Integer> authDeptIds = getAuthDeptIds();
        if(!self && (authDeptIds == null || authDeptIds.size() > 3))
            setDefaultDeptId();
    }

    @Override
    @Forward(page = "/ods/exchange/send.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = !self && (authDeptIds == null || authDeptIds.size() > 3);

        PageTableView view;

        if(showDeptTree)
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
        else if(sendNumberId != null && sendNumberId == 0)
            view = new ComplexTableView(Tools.getBean(SendNumberList.class), "sendNumberId");
        else
            view = new PageTableView();

        view.addComponent("文件名", "title");
        view.addComponent("发文时间", "date_start", "date_end");
        view.addComponent("文件内容", "text");

        if(!showDeptTree && authDeptIds != null && authDeptIds.size() > 1)
            view.addComponent("发文部门", "deptIds");

        view.addMoreComponent("发文字号", "sendNumber");

        if(sendNumberId == null && !showDeptTree && !self)
        {
            view.addMoreComponent("发文类型", "sendNumberId");
            view.addMoreComponent("拟稿科室", "createDeptId");
        }

        if(!self)
            view.addMoreComponent("拟稿人", "userName");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${documentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("document.title").setAction("show(${sendId})")).setWrap(true);
        view.addColumn("发文字号", "document.sendNumber").setWidth("95").setWrap(true);
        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth("75")
                .setAlign(Align.center).setWrap(true);
        view.addColumn("拟稿人", "createUser.userName").setWidth("50").setWrap(true);
        if(self || authDeptIds == null || authDeptIds.size() > 1)
            view.addColumn("发文部门", "dept.deptName").setWidth("70").setWrap(true);

        view.addButton(Buttons.query());
        view.addButton(Buttons.getButton("导出", "sendExport()"));

        view.importJs("/ods/exchange/inout/inout.js");
        view.importCss("/ods/exchange/send.css");

        return view;
    }

    /**
     * @param remark 文件描述，描述本次导出文件，便于区分
     *               批量导出发文文件，每个文件单独导出成一个json文件，并打包成zip包
     *               此方法不直接返回文件输出流，而是将文件存放在指定的位置（根据系统配置，通过框架附件表存储），需要时去下载。
     */
    @Service(method = HttpMethod.post)
    public void expDocuments(final Long[] sendIds, String remark) throws Exception
    {
        if(sendIds == null || sendIds.length == 0) return;

        new DocExpFileGenerator()
        {
            private CacheData cache = new CacheData();

            @Override
            protected void doExport(OdDocFile docFile) throws Exception
            {
                CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);

                for (Long sendId : sendIds)
                {
                    Map<String, Object> map = new HashMap<String, Object>();

                    Send send = getEntity(sendId);
                    map.put("send", DataUtils.createSendMap(send));

                    OfficeDocument document = send.getDocument();
                    List<ReceiveBase> receiveBaseList = documentDaoProvider.get().getReceiveBaseList(document.getDocumentId());

                    //如果发文没有任何外单位收文，则无需导出
                    if(CollectionUtils.isEmpty(receiveBaseList)) continue;

                    List<Map<String, Object>> rbl = new ArrayList<Map<String, Object>>();
                    for(ReceiveBase rb : receiveBaseList) {
                        rbl.add(DataUtils.createReceiveBaseMap(rb));
                    }
                    map.put("receiveBaseList", rbl);

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
                        .saveAttachment(new Inputable.StreamInput(cache.getInputStream()), "发文数据.zip",
                                userOnlineInfo, "ods_export_send", docFile.getRemark()));
            }

            @Override
            protected void finallyHandler()
            {
                //清除缓存的文件
                cache.clear();
            }
        }.generate(remark, userOnlineInfo, 2);
    }
}
