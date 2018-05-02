package com.gzzm.platform.flow.worksheet;

import net.cyan.valmiki.flow.FlowWorkSheetItem;

import java.util.List;

/**
 * @author camel
 * @date 11-11-7
 */
public class WorkSheetRemark
{
    /**
     * 来源列表，一个步骤可能包含多个来自不同步骤的数据
     */
    private List<FlowWorkSheetItem.SourceInfo> subItems;

    /**
     * 等待的数据
     */
    private List<FlowWorkSheetItem.WaitForItem> waitForItems;

    public WorkSheetRemark()
    {
    }

    public List<FlowWorkSheetItem.SourceInfo> getSubItems()
    {
        return subItems;
    }

    public void setSubItems(List<FlowWorkSheetItem.SourceInfo> subItems)
    {
        this.subItems = subItems;
    }

    public List<FlowWorkSheetItem.WaitForItem> getWaitForItems()
    {
        return waitForItems;
    }

    public void setWaitForItems(List<FlowWorkSheetItem.WaitForItem> waitForItems)
    {
        this.waitForItems = waitForItems;
    }
}
