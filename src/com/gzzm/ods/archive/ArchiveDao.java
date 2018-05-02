package com.gzzm.ods.archive;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2016/8/27
 */
public abstract class ArchiveDao extends GeneralDao
{
    public ArchiveDao()
    {
    }

    @LoadByKey
    public abstract Dept getDept(Integer deptId) throws Exception;

    @LoadByKey
    public abstract Archive getArchive(Long archiveId) throws Exception;

    public void lockCatalog(Integer catalogId) throws Exception
    {
        lock(ArchiveCatalog.class, catalogId);
    }

    @LoadByKey
    public abstract ArchiveConfig getConfig(Integer deptId) throws Exception;

    @OQL("select timeLimit from ArchiveTimeLimit order by orderId")
    public abstract List<String> getTimeLimits() throws Exception;

    @OQL("select nvl(max(serial),0) from Archive a where catalogId=:1")
    public abstract Integer getMaxSerial(Integer catalogId) throws Exception;

    @GetByField({"year", "deptId", "catalogName"})
    public abstract ArchiveCatalog getCatalogByName(Integer year, Integer deptId, String catalogName) throws Exception;

    @OQL("select catalogName from ArchiveCatalog where year=:1 and deptId=:2 order by orderId")
    public abstract List<String> getCatalogNames(Integer year, Integer deptId) throws Exception;

    @OQL("select a from Archive a where catalogId=:1 and serial<:2 order by serial desc limit 1")
    public abstract Archive getPreArchive(Integer catalogId, Integer serial) throws Exception;

    @OQL("select a from Archive a where catalogId=:1 and serial>:2 order by serial limit 1")
    public abstract Archive getNexArchive(Integer catalogId, Integer serial) throws Exception;

    @OQLUpdate("update Archive set serial=serial-1 where catalogId=:1 and serial>:2")
    public abstract void upSerials(Integer catalogId, Integer serial) throws Exception;
}
