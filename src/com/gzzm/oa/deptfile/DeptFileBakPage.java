package com.gzzm.oa.deptfile;

import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 文件历史记录查询
 *
 * @author camel
 * @date 13-11-24
 */
@Service
public class DeptFileBakPage
{
    @Inject
    private DeptFileDao dao;

    private Integer fileId;

    public DeptFileBakPage()
    {
    }

    public Integer getFileId()
    {
        return fileId;
    }

    public void setFileId(Integer fileId)
    {
        this.fileId = fileId;
    }

    @Service(url = "/oa/deptfile/file/{fileId}/baks")
    public String showBaks() throws Exception
    {
        return "baks";
    }

    @NotSerialized
    public List<DeptFileBak> getBaks() throws Exception
    {
        return dao.getBaks(fileId);
    }

    @Service(url = "/oa/deptfile/file/bak/{$0}")
    public InputFile down(Long bakId) throws Exception
    {
        DeptFileBak bak = dao.getBak(bakId);

        return new InputFile(bak.getContent(), bak.getDeptFile().getFileName() + "." + bak.getFileType());
    }
}
