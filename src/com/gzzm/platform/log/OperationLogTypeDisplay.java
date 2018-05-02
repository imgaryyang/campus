package com.gzzm.platform.log;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.crud.ToString;

/**
 * 操作日志类型列表的展示
 *
 * @author camel
 * @date 2010-7-6
 */
public class OperationLogTypeDisplay extends BaseQueryCrud<OperationLogType, String>
        implements ToString<OperationLogType>
{
    public OperationLogTypeDisplay()
    {
        addOrderBy("orderId");
        addOrderBy("type");
    }

    @Override
    protected Object createListView() throws Exception
    {
        return new SelectableListView();
    }

    @Override
    public void afterQuery() throws Exception
    {
        getList().add(0, new OperationLogType("all"));
    }

    public String toString(OperationLogType entity) throws Exception
    {
        String type = entity.getType();

        if ("all".equals(type))
            type = "com.gzzm.platform.log.OperationLogType.all";

        return Tools.getMessage(type);
    }
}
