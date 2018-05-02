package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.LabelCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * 邮件标记维护
 *
 * @author lfx
 * @date 2010-3-29
 */
@Service(url = "/oa/mail/mark")
public class MailMarkCrud extends BaseNormalCrud<MailMark, Long>
{
    @Like
    private String markName;

    private MailMarkType markType;

    /**
     * 邮件操作类
     */
    @Inject
    private MailDao dao;


    @UserId
    @NotCondition
    private Integer userId;

    public MailMarkCrud()
    {
        setLog(true);
    }

    public String getMarkName()
    {
        return markName;
    }

    public void setMarkName(String markName)
    {
        this.markName = markName;
    }

    public MailMarkType getMarkType()
    {
        return markType;
    }

    public void setMarkType(MailMarkType markType)
    {
        this.markType = markType;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String getSortListQueryString() throws Exception
    {
        if (markType == MailMarkType.user)
            return "select m from MailMark m where userId=:userId and markType=1 order by orderId";
        else
            return "select m from MailMark m where markType=0 order by orderId";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (markType == MailMarkType.user)
            return "userId=:userId";

        return null;
    }


    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("标记名称", "markName");

        view.addColumn("标记名称", "markName");
        view.addColumn("颜色", new LabelCell("color").setColor("${color}"));
        view.addColumn("图标", new CImage("/oa/mail/mark/icon/${markId}").setProperty("class", "icon"));

        view.importCss("/oa/mail/mark.css");

        view.defaultInit();
        view.addButton(Buttons.sort());
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("标记名称", "markName");
        view.addComponent("颜色", "color").setProperty("datatype", "color");
        view.addComponent("图标", new CFile("icon")).setFileType("$image");
        view.addComponent("当前图标",
                new CImage(getEntity().getIcon() == null ? "" : "/oa/mail/mark/icon/" + getEntity().getMarkId())
                        .setProperty("class", "icon"));

        view.addDefaultButtons();
        view.importJs("/oa/mail/mark.js");
        view.importCss("/oa/mail/mark.css");

        return view;
    }

    /**
     * 显示邮件标记图片的方法
     *
     * @param markId 邮件标记ID
     * @return 邮件标记图标的字节数组
     * @throws Exception 数据库读取数据异常
     */
    @Service(url = "/oa/mail/mark/icon/{$0}")
    public byte[] getIcon(Long markId) throws Exception
    {
        return dao.getMailMark(markId).getIcon();
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setUserId(getUserId());
        getEntity().setMarkType(getMarkType());
        return true;
    }
}
