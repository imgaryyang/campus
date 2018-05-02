package com.gzzm.ods.in;

import com.gzzm.ods.document.*;
import com.gzzm.platform.commons.IDEncoder;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/12/4
 */
@Service
public class DocumentTextDownPage
{
    @Inject
    private OdInterfaceDao dao;

    public DocumentTextDownPage()
    {
    }

    @Service(url = "/interface/ods/text/{id}")
    public InputFile down(String id) throws Exception
    {
        Long documentId = IDEncoder.decode(id);

        OfficeDocument document = dao.getDocument(documentId);
        DocumentText text = document.getText();

        if (StringUtils.isEmpty(text.getOtherFileName()))
        {
            return new InputFile(text.getTextBody(), document.getTitle() + "." + text.getType());
        }
        else
        {
            return new InputFile(text.getOtherFile(), text.getOtherFileName());
        }
    }
}
