package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Provider;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author : wmy
 * @date : 2010-3-15
 */
@Service(url = "/oa/userfile/config")
public class UserFileConfigCrud extends UserConfigCrud
{
    /**
     * 使用Provider实现配置的惰性加载，避免每次访问都从数据库加载配置
     */
    @Inject
    private static Provider<UserFileCommonConfig> commonConfigProvider;

    /**
     * 个人资料配置，在显示用户列表时做临时变量，查看用户信息时使用，修改用户信息时用于接收请求
     */
    private UserFileConfig config;

    private UserFileCommonConfig commonConfig;

    public UserFileConfigCrud()
    {
    }

    public UserFileCommonConfig getCommonConfig()
    {
        if (commonConfig == null)
            commonConfig = commonConfigProvider.get();

        return commonConfig;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("姓名", "userName");
        view.addButton(Buttons.query());

        view.addColumn("姓名", new FieldCell("userName").setOrderable(false));

        view.addColumn("总容量", new FieldCell("crud$.getLibrarySize(userId)")
                .setFormat("bytesize")).setAlign(Align.right);
        view.addColumn("已使用空间", new FieldCell("crud$.getUsedSize(userId)")
                .setFormat("bytesize")).setAlign(Align.right);
        view.addColumn("剩余空间", new FieldCell("crud$.getRemainingSize(userId)")
                .setFormat("bytesize")).setAlign(Align.right);
        view.addColumn("使用率", new FieldCell("crud$.getUsedPercent(userId)")
                .setFormat("##.##%")).setAlign(Align.right);
        view.addColumn("每个文件不允许超过", new FieldCell(
                "crud$.getUploadSize(userId)").setFormat("bytesize")).setWidth("150").setAlign(Align.right);


        view.makeEditable();
        view.importJs("/platform/organ/userconfig.js");
        view.addButton(new CButton("批量修改容量", "batchShow()"));

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("姓名", "userName").setProperty("readonly", true);
        initBytesComponent(view.addComponent("总容量", "this.config.librarySize"));
        initBytesComponent(view.addComponent("上传大小", "this.config.uploadSize"));

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createBatchShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        initBytesComponent(view.addComponent("总容量", "this.config.librarySize"));
        initBytesComponent(view.addComponent("上传大小", "this.config.uploadSize"));

        view.addButton("crud.save", "batchSave()");
        view.addButton(Buttons.close());
        view.importJs("/platform/organ/userconfig.js");

        return view;
    }

    private CData initBytesComponent(CData component)
    {
        return component.setProperty("datatype", "bytesize").setProperty("unit", "K");
    }

    /**
     * 获取配置展示的对象
     *
     * @param userId 用户id
     * @return 返回配置对象
     * @throws Exception 数据库操作抛出异常
     */
    private UserFileConfig getFileConfig(Integer userId) throws Exception
    {
        if (config == null || !config.getUserId().equals(userId))
        {
            config = getCrudService().get(UserFileConfig.class,
                    userId);
        }

        return config;
    }

    /**
     * 查询总容量
     *
     * @param userId 用户id
     * @return 总容量，以字节为单位
     * @throws Exception 数据库异常
     */
    public long getLibrarySize(Integer userId) throws Exception
    {
        UserFileConfig userFileConfig = getFileConfig(userId);
        if (userFileConfig == null || userFileConfig.getLibrarySize() == null
                || userFileConfig.getLibrarySize() == 0)
        {
            return getCommonConfig().getLibrarySize() * 1024L;
        }
        else
        {
            return userFileConfig.getLibrarySize() * 1024L;
        }
    }

    /**
     * 允许单个文件上传大小
     *
     * @param userId 用户id
     * @return 允许单个文件上传的大小，以字节为单位
     * @throws Exception 数据库操作抛出异常
     */
    public long getUploadSize(Integer userId) throws Exception
    {
        UserFileConfig userFileConfig = getFileConfig(userId);
        if (userFileConfig == null || userFileConfig.getUploadSize() == null
                || userFileConfig.getUploadSize() == 0)
        {
            return getCommonConfig().getUploadSize() * 1024L;
        }
        else
        {
            return userFileConfig.getUploadSize() * 1024L;
        }
    }

    /**
     * 查询用户已使用容量
     *
     * @param userId 用户id
     * @return 已使用容量，以字节为单位
     * @throws Exception 数据库操作抛出异常
     */
    public long getUsedSize(Integer userId) throws Exception
    {
        UserFileConfig userFileConfig = getFileConfig(userId);
        if (userFileConfig == null || userFileConfig.getUsed() == null)
        {
            return 0;
        }
        else
        {
            return userFileConfig.getUsed();
        }
    }

    /**
     * 计算剩余容量
     *
     * @param userId 用户id
     * @return 剩余容量，已字节为单位
     * @throws Exception 数据库操作抛出异常
     */
    public long getRemainingSize(Integer userId) throws Exception
    {
        long remaining = getLibrarySize(userId) - getUsedSize(userId);
        if (remaining < 0)
            remaining = 0;

        return remaining;
    }

    /**
     * 计算使用比率
     *
     * @param userId 用户id
     * @return 使用比率，以字节为单位
     * @throws Exception 数据库操作抛出异常
     */
    public double getUsedPercent(Integer userId) throws Exception
    {
        double percent = getUsedSize(userId) / (double) getLibrarySize(userId);
        if (percent > 1)
            percent = 1;
        return percent;
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        setConfig(getCrudService().get(UserFileConfig.class, getEntity().getUserId()));

        if (config == null)
            config = new UserFileConfig();

        if (config.getLibrarySize() == null || config.getLibrarySize() == 0)
            config.setLibrarySize(getCommonConfig().getLibrarySize());

        if (config.getUploadSize() == null || config.getUploadSize() == 0)
            config.setUploadSize(getCommonConfig().getUploadSize());
    }

    @Override
    public void update(User entity) throws Exception
    {
        config.setUserId(entity.getUserId());

        //已使用空间不允许修改
        config.setUsed(null);

        getCrudService().save(config);
    }

    @Override
    protected void save(Integer userId) throws Exception
    {
        UserFileConfig config = getConfig().copy();
        config.setUserId(userId);

        getCrudService().save(config);
    }

    public UserFileConfig getConfig()
    {
        return config;
    }

    public void setConfig(UserFileConfig config)
    {
        this.config = config;
    }
}
