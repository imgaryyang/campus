package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.cms.information.EditType;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 12-12-25
 */
@Service
public class ChannelSelectPage
{
    private boolean multiple;

    private EditType editType;

    private Integer deptId;

    private AuthChannelTreeModel channelTree;

    public ChannelSelectPage()
    {
    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    public EditType getEditType()
    {
        return editType;
    }

    public void setEditType(EditType editType)
    {
        this.editType = editType;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public AuthChannelTreeModel getChannelTree() throws Exception
    {
        if (channelTree == null)
        {
            channelTree = Tools.getBean(AuthChannelTreeModel.class);

            channelTree.setHasCheckBox(multiple);
            channelTree.setEditType(editType);
            channelTree.setDeptId(deptId);
        }

        return channelTree;
    }

    @Service(url = "/portal/selectchannel")
    public String show()
    {
        return "select";
    }
}
