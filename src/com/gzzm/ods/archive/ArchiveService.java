package com.gzzm.ods.archive;

import com.gzzm.platform.organ.Dept;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/8/27
 */
public class ArchiveService
{
    @Inject
    private ArchiveDao dao;

    public ArchiveService()
    {
    }

    protected ArchiveDao getDao()
    {
        return dao;
    }

    protected void lockCatalog(Integer catalogId) throws Exception
    {
        dao.lockCatalog(catalogId);
    }

    @Transactional
    public void up(Long archiveId) throws Exception
    {
        Archive archive = dao.getArchive(archiveId);
        lockCatalog(archive.getCatalogId());

        Archive preArchive = dao.getPreArchive(archive.getCatalogId(), archive.getSerial());
        if (preArchive != null)
        {
            Integer serial = archive.getSerial();
            archive.setSerial(preArchive.getSerial());
            dao.update(archive);

            preArchive.setSerial(serial);
            dao.update(preArchive);
        }
    }

    @Transactional
    public void down(Long archiveId) throws Exception
    {
        Archive archive = dao.getArchive(archiveId);
        lockCatalog(archive.getCatalogId());

        Archive nextArchive = dao.getNexArchive(archive.getCatalogId(), archive.getSerial());
        if (nextArchive != null)
        {
            Integer serial = archive.getSerial();
            archive.setSerial(nextArchive.getSerial());
            dao.update(archive);

            nextArchive.setSerial(serial);
            dao.update(nextArchive);
        }
    }

    public ArchiveConfig getConfig(Integer deptId) throws Exception
    {
        ArchiveConfig config = dao.getConfig(deptId);

        if (config == null)
        {
            Dept dept = dao.getDept(deptId);
            if (dept != null)
            {
                config = new ArchiveConfig();
                config.setDeptId(deptId);
                config.setGeneralName(dept.getDeptName());

                dao.add(config);
            }
        }

        return config;
    }
}
