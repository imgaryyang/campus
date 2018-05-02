package com.gzzm.platform.update;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.IOUtils;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.Date;

/**
 * @author camel
 * @date 13-5-22
 */
@Service
public class UpdateSQLService
{
    @Inject
    private UpdateSQLDao dao;

    public UpdateSQLService()
    {
    }

    public void execute()
    {
        File path = new File(Tools.getConfigPath("initsql"));

        File[] files = path.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.getName().endsWith(".sql"))
                {
                    try
                    {
                        execute(file);
                    }
                    catch (Throwable ex)
                    {
                        Tools.log("execute sql " + file.getName() + " failed", ex);
                    }
                }
            }
        }
    }

    public void execute(File file) throws Exception
    {
        UpdateSQL updateSQL = dao.getUpdateSQL(file.getName());

        if (updateSQL == null)
        {
            String error = null;
            try
            {
                dao.executeNativeSql(IOUtils.fileToString(file));
            }
            catch (Throwable ex)
            {
                Tools.log("execute sql " + file.getName() + " failed", ex);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ex.printStackTrace(new PrintStream(out));

                error = out.toString("UTF-8");
            }

            updateSQL = new UpdateSQL();
            updateSQL.setFileName(file.getName());
            updateSQL.setExeucteTime(new Date());
            if (error != null)
                updateSQL.setError(error.toCharArray());
            dao.add(updateSQL);
        }
    }
}
