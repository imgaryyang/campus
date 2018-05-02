package com.gzzm.ods.redhead;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 红头模版维护
 *
 * @author camel
 * @date 2010-12-17
 */
@Service(url = "/ods/RedHead")
public class RedHeadCrud extends DeptOwnedNormalCrud<RedHead, Integer>
{
    public static class ProgressRedHeadUpListener implements RedHeadUpListener, ProgressInfo
    {
        private static final long serialVersionUID = 6439266596116272257L;

        private String fileName;

        public ProgressRedHeadUpListener()
        {
        }

        public String getProgressName()
        {
            return Tools.getMessage("ods.redhead.up.progressname");
        }

        public float getPercentage()
        {
            return 0;
        }

        public String getDescription()
        {
            return Tools.getMessage("ods.redhead.up.progressdescription", new Object[]{fileName});
        }

        public void upFile(String fileName) throws Exception
        {
            this.fileName = fileName;
        }
    }

    @Inject
    private RedHeadService service;

    private Integer typeId;

    @Like
    private String redHeadName;

    @NotSerialized
    private InputFile file;

    public RedHeadCrud()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getRedHeadName()
    {
        return redHeadName;
    }

    public void setRedHeadName(String redHeadName)
    {
        this.redHeadName = redHeadName;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @NotSerialized
    @Select(field = {"typeId", "entity.typeId"})
    public List<RedHeadType> getTypes() throws Exception
    {
        return service.getDao().getRedHeadTypes();
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("红头名称", "redHeadName");
        view.addComponent("红头类型", "typeId");

        view.addColumn("红头名称", "redHeadName");
        view.addColumn("红头类型", "type.typeName");
        view.addColumn("测试", new CButton("测试", "test(${redHeadId})"));
        view.addColumn("下载红头文件", new CHref("下载红头文件", "/ods/RedHead/${redHeadId}/down").setTarget("_blank"))
                .setWidth("100");

        view.defaultInit();
        view.addButton(Buttons.sort());
        view.addButton(Buttons.getButton("打包下载", "zip()", "zip"));
        view.addButton(Buttons.getButton("上传", "", "upload").setProperty("id", "upZip"));

        view.importJs("/ods/redhead/redhead.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("红头名称", "redHeadName");
        view.addComponent("红头类型", "typeId");
        view.addComponent("红头文件", new CFile("redHead").setFileType("doc"));
        view.addComponent("二维码左", "qrLeft");
        view.addComponent("二维码上", "qrTop");

        view.addDefaultButtons();

        view.importJs("/ods/redhead/redhead.js");

        return view;
    }

    @Service(url = "/ods/RedHead/{$0}/down")
    public InputFile downFile(Integer redHeadId) throws Exception
    {
        RedHead redHead = getEntity(redHeadId);

        return new InputFile(redHead.getRedHead(), redHead.getRedHeadName() + ".doc");
    }

    /**
     * 打包下载所有红头文件
     *
     * @return 红头文件的压缩包
     * @throws Exception 从数据库加载红头文件或者压缩包错误
     */
    @Service(url = "/ods/RedHead/zip/get")
    public InputFile zip() throws Exception
    {
        return new InputFile(service.zip(getAuthDeptIds()), "红头文件.zip");
    }

    /**
     * 上传红头文件压缩包，将压缩包解压并将相应红头上传到数据库中
     *
     * @throws Exception 解压错误或者数据库操作错误
     */
    @Service(url = "/ods/RedHead/zip/put", method = HttpMethod.post)
    public void upZip() throws Exception
    {
        service.up(file, getAuthDeptIds(),
                RequestContext.getContext().getProgressInfo(ProgressRedHeadUpListener.class));
    }
}
