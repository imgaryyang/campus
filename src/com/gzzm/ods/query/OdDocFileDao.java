package com.gzzm.ods.query;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author LDP
 * @date 2015/11/27
 */
public abstract class OdDocFileDao extends GeneralDao
{
    public OdDocFileDao()
    {
    }

    /**
     * 根据用户ID及导出文件类型查询文件
     * 此方法只适用于导出单位公文簿使用，每个人只对应一条发文簿和一条收文簿记录
     * @param userId 用户ID
     * @param docFileType 导出文件类型
     */
    @OQL("select c from OdDocFile c where c.userId = ?1 and c.docFileType = ?2")
    public abstract OdDocFile getDocFile(Integer userId, Integer docFileType);

    /**
     * 获取所有保存在数据库的文件
     */
    @OQL("select f from OdDocFile f where f.content is not null")
    public abstract List<OdDocFile> getDbFileList() throws Exception;
}
