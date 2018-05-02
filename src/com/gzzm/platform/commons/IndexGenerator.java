package com.gzzm.platform.commons;

import net.cyan.thunwind.PersistenceManager;

/**
 * 生成索引的任务
 *
 * @author camel
 * @date 2010-7-7
 */
public abstract class IndexGenerator implements Runnable
{
    /**
     * 要生成索引的类
     */
    private Class[] classes;

    private String manager = "";

    protected IndexGenerator()
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void setManager(String manager)
    {
        this.manager = manager;
    }

    protected void setClasses(Class... classes)
    {
        this.classes = classes;
    }

    public void run()
    {
        try
        {
            PersistenceManager manager = PersistenceManager.getManager(this.manager);

            for (Class c : classes)
            {
                try
                {
                    manager.indexFullText(c);
                }
                catch (Throwable ex)
                {
                    //建立一个索引失败不影响下一个索引的建立，只记录日志
                    Tools.log(ex);
                }
            }
        }
        catch (Throwable ex)
        {
            //run方法为最外层的方法，不允许在抛出异常 ，记录日志
            Tools.log(ex);
        }
    }
}
