package com.gzzm.in;

import com.gzzm.platform.commons.FileUploadService;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.InputStream;

/**
 * @author camel
 * @date 13-12-23
 */
@Service
public class FileUploadPage implements InterfacePage
{
    @Inject
    private FileUploadService service;

    public FileUploadPage()
    {
    }

    @Service(url = "/interface/fileupload", method = HttpMethod.post)
    public FileUploadResult upload() throws Exception
    {
        InputStream in = RequestContext.getContext().getRequest().getInputStream();

        String path = service.uploadFile(new InputFile(new Inputable.StreamInput(in), "temp"));

        return new FileUploadResult(path);
    }
}
