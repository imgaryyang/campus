package com.gzzm.ods.document;

import com.gzzm.platform.commons.IDEncoder;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 11-10-20
 */
@Service
public class DocumentTextBakPage
{
    @Inject
    private DocumentTextBakDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Long textId;

    private boolean restoreable = true;

    public DocumentTextBakPage()
    {
    }

    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public Long getTextId()
    {
        return textId;
    }

    public void setTextId(Long textId)
    {
        this.textId = textId;
    }

    public boolean isRestoreable(DocumentTextBak bak)
    {
        if (userOnlineInfo.isAdmin() || userOnlineInfo.getDeptId() == 1)
            return true;

        if (!restoreable)
            return false;

        //遇到第一个不是自己保存的文件，后面的文件不允许恢复
        if (!bak.getUserId().equals(userOnlineInfo.getUserId()))
            restoreable = false;

        return true;
    }

    @NotSerialized
    public List<DocumentTextBak> getBaks() throws Exception
    {
        return dao.getBaks(textId);
    }

    @Service(url = "/ods/document/text/{textId}/baks")
    public String showList()
    {
        return "baks";
    }

    @Service(url = "/ods/document/text/bak/{$0}")
    public InputFile down(String id) throws Exception
    {
        Long bakId = IDEncoder.decode(id);

        DocumentTextBak bak = dao.getBak(bakId);
        DocumentText text = dao.getText(bak.getTextId());

        return new InputFile(bak.getTextBody(),
                bak.getUser().getUserName() + "(" + DateUtils.toString(bak.getSaveTime(), "MM月dd日HH时mm分") + ")." +
                        text.getType());
    }
}
