package com.gzzm.platform.visit;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.cache.Cache;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 访问记录的服务
 *
 * @author camel
 * @date 2011-6-21
 */
@Injectable(singleton = true)
public class VisitService
{
    @Inject
    private static Provider<VisitDao> daoProvider;

    @Inject
    private static Provider<VisitConfig> configProvider;

    /**
     * 缓存的记录，访问记录只需要缓存在本机上，不需要放到分布式缓存中
     */
    private List<VisitRecord> recordCaches;

    /**
     * 缓存所有的访问记录信息，此信息需要在多个服务器直接保持同步，需要使用分布式缓存
     */
    @Inject("visit")
    private Cache<String, Integer> totalCaches;

    public VisitService()
    {
    }

    public int getCacheSize()
    {
        return configProvider.get().getCacheSize();
    }

    /**
     * 得到某个对象的访问总数
     *
     * @param type     对象类型
     * @param objectId 对象ID
     * @return 访问总数
     * @throws Exception 从数据库加载访问次数失败
     */
    public int getVisitCount(int type, int objectId) throws Exception
    {
        String key = (type + "~" + objectId).intern();
        synchronized (key)
        {
            Integer total = totalCaches.getCache(key);

            if (total == null)
            {
                //还未加载此访问次数，从数据库加载
                total = daoProvider.get().getVisitTotal(type, objectId);

                //数据库也没有此访问次数，访问次数为0
                if (total == null)
                    total = 0;

                //将访问次数到缓存中
                totalCaches.update(key, total);
            }

            return total;
        }
    }

    /**
     * 访问一个对象
     *
     * @param type     对象类型
     * @param objectId 对象ID
     * @param userId   用户ID
     * @param ip       ip
     * @throws Exception 从数据库读取访问次数失败
     */
    public void visit(int type, int objectId, Integer userId, String ip) throws Exception
    {
        //构建访问记录
        VisitRecord record = new VisitRecord();
        record.setType(type);
        record.setObjectId(objectId);
        record.setUserId(userId);
        record.setIp(ip);
        record.setVisitTime(new Date());
        int cacheSize = getCacheSize();

        List<VisitRecord> toSubmitRecords = null;

        //将访问记录放到内存缓存中
        synchronized (this)
        {
            if (recordCaches == null)
                recordCaches = new ArrayList<VisitRecord>(cacheSize);

            recordCaches.add(record);

            if (recordCaches.size() >= cacheSize)
            {
                //超过缓存大小，需要提交到数据库
                toSubmitRecords = recordCaches;
                recordCaches = null;
            }
        }

        //增加访问次数
        String key = (type + "~" + objectId).intern();
        synchronized (key)
        {
            Integer total = totalCaches.getCache(key);

            if (total == null)
            {
                //还未加载此访问次数，从数据库加载
                total = daoProvider.get().getVisitTotal(type, objectId);

                //数据库也没有此访问次数，访问次数为0
                if (total == null)
                    total = 0;
            }

            //将访问次数+1后放到缓存中
            totalCaches.update(key, total + 1);
        }

        if (toSubmitRecords != null)
        {
            //创建一个任务，将缓存提交到数据库中
            final List<VisitRecord> toSubmitRecords0 = toSubmitRecords;

            Tools.run(new Runnable()
            {
                public void run()
                {
                    submit(toSubmitRecords0);
                }
            });
        }
    }

    public void submit(List<VisitRecord> records)
    {
        try
        {
            if (records != null)
            {
                //保存需要提交的访问数
                List<VisitTotal> totals = new ArrayList<VisitTotal>();

                //提交缓存记录到数据库中
                VisitDao dao = daoProvider.get();

                dao.pushVisitRecords(records);

                //将涉及的访问总数提交到数据库中
                for (VisitRecord record : records)
                {
                    //搜索此对象的访问数是否已经放到提交列表中
                    boolean exists = false;
                    for (VisitTotal total : totals)
                    {
                        if (total.getType().equals(record.getType()) &&
                                total.getObjectId().equals(record.getObjectId()))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                    {
                        //将访问总数放到提交列表中
                        VisitTotal total = new VisitTotal();
                        total.setType(record.getType());
                        total.setObjectId(record.getObjectId());
                        total.setVisitCount(totalCaches.getCache(record.getType() + "~" + record.getObjectId()));

                        totals.add(total);
                    }
                }

                dao.pushVisitTotals(totals);
            }
        }
        catch (Throwable ex)
        {
            //再外层是Runnable，不能再往外throw异常，记录错误日志
            Tools.log(ex);
        }
    }

    public Visit getVisit(String name) throws Exception
    {
        VisitDao dao = daoProvider.get();
        Visit visit = dao.getVisit(name);

        if (visit == null)
        {
            visit = new Visit();
            visit.setVisitName(name);
            dao.add(visit);
        }

        return visit;
    }
}
