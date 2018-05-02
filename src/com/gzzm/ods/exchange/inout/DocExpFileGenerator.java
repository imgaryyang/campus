package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.query.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * 收发文导出任务基础类
 *
 * @author LDP
 * @date 2017/4/27
 */
public abstract class DocExpFileGenerator implements Runnable
{
    @Inject
    private static Provider<OdDocFileDao> docFileDaoProvider;

    /**
     * 导出文件记录
     */
    private OdDocFile docFile;

    public DocExpFileGenerator()
    {
    }

    public void generate(String remark, UserOnlineInfo userOnlineInfo, Integer type) throws Exception
    {
        docFile = new OdDocFile();
        docFile.setUserId(userOnlineInfo.getUserId());
        docFile.setDeptId(userOnlineInfo.getDeptId());
        docFile.setDocFileType(type);//收文json文件
        docFile.setActionTime(new java.util.Date());
        docFile.setState(0);//生成中
        docFile.setRemark(remark);
        docFileDaoProvider.get().save(docFile);

        //起一条线程后台去生成文件
        Tools.run(this);
    }

    @Override
    public void run()
    {
        try
        {
            doExport(docFile);

            docFile.setState(1);//生成完成
            docFile.setFinishTime(new java.util.Date());

            successHandler();
        }
        catch (Exception ex)
        {
            docFile.setState(2);//生成失败
            Tools.log(ex);
            errorHandler();
        }
        finally
        {
            try
            {
                docFileDaoProvider.get().update(docFile);
            }
            catch (Exception e)
            {
                Tools.log(e);
            }
            finallyHandler();
        }
    }

    /**
     * 处理具体的数据导出逻辑
     */
    protected abstract void doExport(OdDocFile docFile) throws Exception;

    /**
     * 文件成功生成后的处理逻辑
     */
    protected void successHandler()
    {
    }

    /**
     * 文件生成失败后的处理逻辑
     */
    protected void errorHandler()
    {
    }

    /**
     * 程序执行到最后必须处理的逻辑
     */
    protected void finallyHandler()
    {
    }
}
