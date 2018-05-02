package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;

/**
 * @author camel
 * @date 2018/1/29
 */

@Service(url = "/ods/exchange/backpaper")
public class BackPaperCrud extends DeptOwnedNormalCrud<BackPaper, Integer>
{
    @Like
    private String paperNanme;

    /**
     * 用于接收上传的模板文件
     */
    private InputFile file;

    public BackPaperCrud()
    {
    }

    public String getPaperNanme()
    {
        return paperNanme;
    }

    public void setPaperNanme(String paperNanme)
    {
        this.paperNanme = paperNanme;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (file != null)
        {
            String ext = IOUtils.getExtName(file.getName());

            Inputable content = file.getInputable();

            if ("doc".equals(ext) || "docx".equals(ext))
                content = OfficeUtils.wordToXml(content, ext);

            getEntity().setTemplate(content.getInputStream());
        }

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("回执名称", "paperName");

        view.addColumn("回执名称", "paperName").setWidth("200");
        view.addColumn("页面路径", "page");
        view.addColumn("下载模板", new CHref("下载模板", "/ods/exchange/backpaper/${paperId}/template").setTarget("_blank"))
                .setWidth("100");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("回执名称", "paperName");
        view.addComponent("页面路径", "page");
        view.addComponent("回执模板", new CFile(null).setProperty("name", "file").setFileType("xml doc docx"));

        view.addDefaultButtons();

        return view;
    }

    @Service(url = "/ods/exchange/backpaper/{$0}/template")
    public InputFile downTemplate(Integer paperId) throws Exception
    {
        BackPaper paper = getEntity(paperId);

        return new InputFile(paper.getTemplate(), paper.getPaperName() + ".xml");
    }
}
