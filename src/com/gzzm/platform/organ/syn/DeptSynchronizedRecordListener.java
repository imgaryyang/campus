package com.gzzm.platform.organ.syn;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.pool.job.SingleRunnable;
import net.cyan.commons.util.*;
import net.cyan.datasyn.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 13-6-14
 */
public class DeptSynchronizedRecordListener implements SynchronizedRecordListener
{
    @Inject
    private static Provider<DeptSynchronizedDao> deptSynchronizedServiceProvider;

    private static final Runnable DEPT_INIT = new SingleRunnable(new Runnable()
    {
        public void run()
        {
            try
            {
                Tools.log("start init dept tree");
                deptSynchronizedServiceProvider.get().initDeptTree(schema);
                Tools.log("end init dept tree");
            }
            catch (Throwable ex)
            {
                Tools.log(ex);
            }
        }
    });

    private static String schema;

    public DeptSynchronizedRecordListener()
    {
    }

    public boolean beforeInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        return true;
    }

    public void afterInsert(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PFDEPT".equals(table.getTable()))
        {
            Integer deptId = ((Number) keys.get("DEPTID")).intValue();
            if (deptId == 1)
                return;

            if (((Number) values.get("PARENTDEPTID")).intValue() == 1)
            {
                return;
            }

            initDeptTree(table.getSynSchema());
        }
    }

    public boolean beforeUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PFDEPT".equals(table.getTable()))
        {
            Integer deptId = ((Number) keys.get("DEPTID")).intValue();
            if (deptId == 1)
                return false;

            if (((Number) values.get("PARENTDEPTID")).intValue() == 1)
            {
                return false;
            }

            values.remove("LEFTVALUE");
            values.remove("RIGHTVALUE");
        }

        return true;
    }

    public void afterUpdate(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values)
            throws Exception
    {
        if ("PFDEPT".equals(table.getTable()))
        {
            initDeptTree(table.getSynSchema());
        }
    }

    public boolean beforeDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        return true;
    }

    public void afterDelete(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
        if ("PFDEPT".equals(table.getTable()))
            initDeptTree(table.getSynSchema());
    }

    public void beforeExists(SynchronizedInfo table, Map<String, Object> keys) throws Exception
    {
    }

    @Override
    public void afterLoad(SynchronizedInfo table, Map<String, Object> keys, Map<String, Object> values) throws Exception
    {
    }

    private void initDeptTree(final String schema) throws Exception
    {
        DeptSynchronizedRecordListener.schema = schema;

        if (!StringUtils.isEmpty(schema))
        {
            Jobs.addJob(DEPT_INIT, new Date(System.currentTimeMillis() + 1000 * 60 * 5));
        }
    }
}
