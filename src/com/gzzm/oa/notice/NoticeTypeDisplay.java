package com.gzzm.oa.notice;

import com.gzzm.platform.commons.crud.*;

/**
 * @author czf
 * @date 2010-3-18
 */
public class NoticeTypeDisplay extends DeptOwnedQuery<NoticeType, Integer>
{
    private Integer sortId;

    public NoticeTypeDisplay()
    {
        addOrderBy("dept.leftValue");
        addOrderBy("orderId");
    }

    public Integer getSortId()
    {
        return sortId;
    }

    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }

    @Override
    public void afterQuery() throws Exception
    {
        NoticeType type = new NoticeType(0, "所有栏目");

        getList().add(0, type);
    }
}
