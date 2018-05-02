package com.gzzm.platform.commons.filestore;

import net.cyan.commons.util.InputFile;

import java.util.List;

/**
 * 文件存储，用于将文件存储到其他地方
 *
 * @author camel
 * @date 2011-4-6
 */
public interface FileStorer
{
    /**
     * 保存文件
     *
     * @param file      文件
     * @param userId    用户ID
     * @param deptId    部门ID
     * @param catalogId 目录ID
     * @param source    来源
     * @param remark    说明
     * @throws Exception 保存文件异常
     */
    public void save(InputFile file, Integer userId, Integer deptId, String catalogId, String source, String remark)
            throws Exception;

    /**
     * 获得文件
     *
     * @param fileId 文件Id
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 文件内容
     * @throws Exception 保存文件异常
     */
    public List<InputFile> get(String fileId, Integer userId, Integer deptId) throws Exception;

    /**
     * 获得文件目录列表
     *
     * @param parentCatalogId 上一级目录ID，如果是根目录为空
     * @param userId          用户ID
     * @param deptId          部门ID
     * @param writable        是否只取可写的目录
     * @return 文件目录列表
     * @throws Exception 读取数据错误，一般是数据库错误
     */
    public List<FileCatalog> getCatalogs(String parentCatalogId, Integer userId, Integer deptId, boolean writable)
            throws Exception;

    /**
     * 文件保存目标的id，如userfile等
     *
     * @return 目标的id
     */
    public String getId();

    /**
     * 文件保存目标的名称，如个人文件等
     *
     * @return 名称
     */
    public String getName();

    /**
     * 是否只读
     *
     * @return 只读返回true，不是返回false
     */
    public boolean isValid(Integer userId, Integer deptId, boolean readOnly);

    public void loadFileList(FileQuery fileQuery) throws Exception;
}