package com.gzzm.platform.organ.syn;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.crud.CrudAdvice;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 13-6-14
 */
public abstract class DeptSynchronizedDao extends GeneralDao
{
    @Inject
    private DeptCrud deptCrud;

    public DeptSynchronizedDao()
    {
    }

    public void initDeptTree(String schema) throws Exception
    {
        getSession().setSessionVariable(schema, "DATASYN_TAG", "1");

        try
        {
            CrudAdvice.before(deptCrud);

            deptCrud.initTree(1);

            CrudAdvice.after(deptCrud, null);
        }
        catch (Throwable ex)
        {
            CrudAdvice.catchHandle(deptCrud, ex);

            Tools.handleException(ex);
        }
        finally
        {
            CrudAdvice.finallyHandle(deptCrud);

            try
            {
                getSession().setSessionVariable(schema, "DATASYN_TAG", "0");
            }
            catch (Throwable ex)
            {
                //
            }
        }

        Dept.setUpdated();
    }
}
