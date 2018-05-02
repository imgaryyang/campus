package com.gzzm.portal.cms.information;

import net.cyan.commons.util.InputFile;
import net.cyan.crud.importers.DataRecord;

/**
 * 信息发布扩展功能组件
 *
 * @author camel
 * @date 13-11-1
 */
public interface InformationComponent
{
    /**
     * 编辑采编新增和修改的页面
     *
     * @return 采编新增和修改的页面
     */
    public String page();

    /**
     * 数据之前的动作
     *
     * @param information 当前在保存的信息
     * @param isNew       是否新建
     * @param crud        当前的crud对象
     * @return 返回false表示不允许保存
     * @throws Exception 允许实现类抛出异常
     */
    public boolean beforeSave(InformationEdit information, boolean isNew, InformationCrud crud) throws Exception;

    /**
     * 保存数据之后的动作
     *
     * @param information 当前在保存的信息
     * @param isNew       是否新建
     * @param crud        当前的crud对象
     * @throws Exception 允许实现类抛出异常
     */
    public void afterSave(InformationEdit information, boolean isNew, InformationCrud crud) throws Exception;


    /**
     * 新增是初始化数据
     *
     * @param information 新增的信息
     * @param crud        当前crud对象
     * @throws Exception 允许实现类抛出异常
     */
    public void init(InformationEdit information, InformationCrud crud) throws Exception;

    /**
     * 加载数据
     *
     * @param information 正在加载的信息
     * @param crud        当前的crud对象
     * @throws Exception 允许实现类抛出异常
     */
    public void load(InformationEdit information, InformationCrud crud) throws Exception;

    /**
     * 复制一条数据
     *
     * @param information       复制的结果
     * @param sourceInformation 原信息
     * @param crud              当前的crud对象
     * @throws Exception 允许实现类抛出异常
     */
    public void clone(InformationEdit information, InformationEdit sourceInformation, InformationCrud crud)
            throws Exception;

    /**
     * 发布前的动作
     *
     * @param informationEidt 要发布的信息的informationEdit
     * @param information     要发布的信息的information
     * @param first           是否第一次发布
     * @return 返回false表示不允许发布
     * @throws Exception 允许实现类抛出异常
     */
    public boolean beforePublish(InformationEdit informationEidt, Information information, boolean first)
            throws Exception;

    /**
     * 发布后的动作
     *
     * @param informationEdit 要发布的信息的informationEdit
     * @param information     要发布的信息的information
     * @param first           是否第一次发布
     * @throws Exception 允许实现类抛出异常
     */
    public void afterPublish(InformationEdit informationEdit, Information information, boolean first)
            throws Exception;

    /**
     * 加载文本内容
     *
     * @param information 信息
     * @throws Exception 允许实现类抛出异常
     */
    public String getText(InformationBase<?, ?> information) throws Exception;


    /**
     * 是否支持数据导入
     *
     * @param crud 当前crud
     * @return 支持返回true，不支持返回false
     * @throws Exception 允许实现类抛出异常
     */
    public boolean isSupportedImport(InformationCrud crud) throws Exception;

    /**
     * 导入的时候填充一条记录
     *
     * @param record      从数据源读入的数据
     * @param information 信息
     * @param crud        当前crud对象
     */
    public void importFill(DataRecord record, InformationEdit information, InformationCrud crud) throws Exception;

    /**
     * 导入数据，覆盖系统默认的导入方式
     *
     * @param file 要导入的文件
     * @param crud 当前crud对象
     * @return 返回true表示覆盖系统默认的导入方式，返回false表示此方法没有实现，使用系统默认的导入方式
     */
    public boolean imp(InputFile file, InformationCrud crud) throws Exception;
}