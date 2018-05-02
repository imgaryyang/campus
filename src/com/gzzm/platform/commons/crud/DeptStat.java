package com.gzzm.platform.commons.crud;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 11-8-30
 */
@Service
public class DeptStat extends DeptOwnedStat<Dept>
{
    public DeptStat()
    {
    }

    @Override
    protected void initStats() throws Exception
    {
        addStat("dept", "dept", "null");

        initOrders();
    }

    protected void initOrders() throws Exception
    {
        addOrderBy("dept.leftValue");
    }
}
