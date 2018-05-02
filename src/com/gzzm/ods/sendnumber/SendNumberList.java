package com.gzzm.ods.sendnumber;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 12-12-15
 */
@Service
public class SendNumberList extends DeptOwnedQuery<SendNumber, Integer>
{
    public SendNumberList()
    {
        addOrderBy("dept.leftValue");
        addOrderBy("orderId");
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        getList().add(0, new SendNumber(-1, "全部公文"));
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }
}
