package com.gzzm.portal.cms.channel.syn;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.Dept;
import com.gzzm.portal.cms.channel.ChannelCrud;
import net.cyan.crud.CrudAdvice;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 13-6-14
 */
public abstract class ChannelSynchronizedDao extends GeneralDao
{
    @Inject
    private ChannelCrud channelCrud;

    public ChannelSynchronizedDao()
    {
    }

    public void initChannelTree(String schema) throws Exception
    {
        getSession().setSessionVariable(schema, "DATASYN_TAG", "1");

        try
        {
            CrudAdvice.before(channelCrud);

            channelCrud.initTree(0);

            CrudAdvice.after(channelCrud, null);
        }
        catch (Throwable ex)
        {
            CrudAdvice.catchHandle(channelCrud, ex);

            Tools.handleException(ex);
        }
        finally
        {
            CrudAdvice.finallyHandle(channelCrud);

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
