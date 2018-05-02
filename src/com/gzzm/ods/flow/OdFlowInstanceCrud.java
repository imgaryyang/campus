package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 流程实例管理，目前只用于部门草稿箱
 *
 * @author camel
 * @date 11-9-22
 */
@Service(url = "/ods/flow/instance")
public class OdFlowInstanceCrud extends DeptOwnedEditableCrud<OdFlowInstance, Long>
{
    @Inject
    private OdFlowService service;

    /**
     * 类型
     */
    private String[] type;

    @Equals("business.tag")
    private BusinessTag tag;

    /**
     * 状态
     */
    private OdFlowInstanceState state = OdFlowInstanceState.unclosed;

    @Like("document.title")
    private String title;

    @Lower(column = "startTime")
    private Date time_start;

    @Upper(column = "startTime")
    private Date time_end;

    /**
     * 发文编号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    /**
     * 主题词
     */
    @Like("document.subject")
    private String subject;

    @Contains("document.textContent")
    private String text;

    public OdFlowInstanceCrud()
    {
        addOrderBy("startTime", OrderType.desc);
    }

    public OdFlowInstanceState getState()
    {
        return state;
    }

    public void setState(OdFlowInstanceState state)
    {
        this.state = state;
    }

    public String[] getType()
    {
        return type;
    }

    public void setType(String[] type)
    {
        this.type = type;
    }

    public BusinessTag getTag()
    {
        return tag;
    }

    public void setTag(BusinessTag tag)
    {
        this.tag = tag;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 3;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId") : new PageTableView();

        view.addComponent("标题", "title");
        view.addComponent("拟稿时间", "time_start", "time_end");
        view.addMoreComponent("发文字号", "sendNumber");
//        view.addMoreComponent("主题词", "subject");
        view.addMoreComponent("文件内容", "text");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment")))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("标题", new HrefCell("document.titleText").setAction("openDocument(${instanceId})"))
                .setOrderFiled("document.title");
        view.addColumn("编号", "serial");
        view.addColumn("拟稿时间", "startTime");
        view.addColumn("拟稿人", "createUser.userName");

        if (!showDeptTree && authDeptIds.size() > 1)
            view.addColumn("发文部门", "dept.deptName");

        view.addColumn("紧急程度", "document.priority");

        view.addButton(Buttons.query());
        if (state == OdFlowInstanceState.unclosed)
            view.addButton(Buttons.delete());

        view.importJs("/ods/flow/instance.js");

        return view;
    }

    /**
     * 打开公文
     *
     * @param instanceId 公文实例ID
     * @param readOnly   是否以只读方式打开
     * @return 打开公文的url
     * @throws Exception 数据库查询数据错误
     */
    @Redirect
    @Service(url = "/ods/flow/instance/{$0}/open?readOnly={$1}")
    public String openDocument(Long instanceId, boolean readOnly) throws Exception
    {
        String url = service.getFirstStepUrl(instanceId);

        if (readOnly)
            url += "?readOnly=" + readOnly;

        String menuId = MenuItem.getMenuId(RequestContext.getContext().getRequest());
        if (!StringUtils.isEmpty(menuId))
            url = url + (url.indexOf("?") > 0 ? "&" : "?") + MenuItem.MENUID + "=" + menuId;

        return url;
    }

    @Override
    @Transactional
    public int deleteAll() throws Exception
    {
        Long[] keys = getKeys();
        if (keys == null)
            return 0;

        for (Long key : keys)
            delete(key);

        return keys.length;
    }

    @Override
    @Transactional
    public boolean delete(Long key) throws Exception
    {
        FlowApi.getController(key, OdSystemFlowDao.getInstance()).deleteInstance();

        return super.delete(key);
    }
}
