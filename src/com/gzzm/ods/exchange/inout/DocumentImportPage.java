package com.gzzm.ods.exchange.inout;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.nest.annotation.Inject;

/**
 * 公文导入操作页面
 * @author LDP
 * @date 2017/4/26
 */
@Service
public class DocumentImportPage
{
    public static class DocumentImportProgressInfo implements ProgressInfo{

        private String fileName;

        public DocumentImportProgressInfo()
        {
        }

        @Override
        public String getProgressName()
        {
            return Tools.getMessage("ods.inout.imp.progressname");
        }

        @Override
        public float getPercentage()
        {
            return 0;
        }

        @Override
        public String getDescription()
        {
            return Tools.getMessage("ods.inout.imp.progressdescription", new Object[]{fileName});
        }

        public void setFileName(String fileName)
        {
            this.fileName = fileName;
        }
    }

    @Inject
    private DocumentImportService service;

    /**
     * 公文类型
     * 0=发文；1=收文
     */
    private Integer type;

    private InputFile file;

    public DocumentImportPage()
    {
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @Service(url = "/ods/exchange/inout/import")
    public String show() {
        return "/ods/exchange/inout/import.ptl";
    }

    /**
     * 导入收文
     */
    @Service(method = HttpMethod.post)
    public void impReceive() throws Exception
    {
        if(!"zip".equalsIgnoreCase(file.getExtName())) throw new NoErrorException("只能上传zip文件");
        service.importReceive(file, RequestContext.getContext().getProgressInfo(DocumentImportProgressInfo.class));
    }

    /**
     * 导入收文
     */
    @Service(method = HttpMethod.post)
    public void impSend() throws Exception{
        if(!"zip".equalsIgnoreCase(file.getExtName())) throw new NoErrorException("只能上传zip文件");
        service.importSend(file, RequestContext.getContext().getProgressInfo(DocumentImportProgressInfo.class));
    }
}
