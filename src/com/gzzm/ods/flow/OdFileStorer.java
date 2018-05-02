package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.filestore.*;
import net.cyan.commons.util.*;
import net.cyan.crud.QueryType;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 从公文中提取文件
 *
 * @author camel
 * @date 13-12-5
 */
public class OdFileStorer implements FileStorer
{
    @Inject
    private static Provider<OdFlowDao> daoProvider;


    public OdFileStorer()
    {
    }

    public String getId()
    {
        return "od";
    }

    public String getName()
    {
        return null;
    }

    @Override
    public boolean isValid(Integer userId, Integer deptId, boolean readOnly)
    {
        return readOnly;
    }

    public List<FileCatalog> getCatalogs(String parentCatalogId, Integer userId, Integer deptId, boolean writable)
            throws Exception
    {
        return Collections.emptyList();
    }


    public void save(InputFile file, Integer userId, Integer deptId, String catalogId, String source, String remark)
            throws Exception
    {
        //公文不支持写
    }

    public List<InputFile> get(String fileId, Integer userId, Integer deptId) throws Exception
    {
        OdFlowDao dao = daoProvider.get();

        Long instanceId = Long.valueOf(fileId);

        OdFlowInstance odFlowInstance = dao.getOdFlowInstance(instanceId);

        OfficeDocument document = odFlowInstance.getDocument();

        List<InputFile> files = new ArrayList<InputFile>();

        DocumentText text = document.getText();
        if (text != null && text.getTextBody() != null)
        {
            files.add(new InputFile(text.getTextBody(),
                    document.getTitle() + "." + (StringUtils.isEmpty(text.getType()) ? "doc" : text.getType())));
        }

        SortedSet<Attachment> attachments = document.getAttachments();
        if (attachments != null)
        {
            for (Attachment attachment : attachments)
            {
                files.add(attachment.getInputFile());
            }
        }

        return files;
    }

    public void loadFileList(FileQuery fileQuery) throws Exception
    {
        fileQuery.setQueryType(QueryType.oql);
        fileQuery.setBeanType(OdFlowInstance.class);

        fileQuery.setQueryString("select i from OdFlowInstance i where (select s from SystemFlowStep s where " +
                "s.receiver=tochar(:userId) and s.instanceId=i.instanceId) is not empty and state<2 and " +
                "document.title like ?title and contains(document.textContent,?text) and startTime>=?time_start and startTime<?time_end" +
                " order by startTime desc");

        fileQuery.setTransformer(new FileItemTransformer()
        {
            public FileItem transform(Object bean) throws Exception
            {
                OdFlowInstance odFlowInstance = (OdFlowInstance) bean;

                FileItem fileItem = new FileItem();

                fileItem.setId(odFlowInstance.getInstanceId().toString());
                fileItem.setTitle(odFlowInstance.getDocument().getTitleText());
                fileItem.setTime(odFlowInstance.getStartTime());
                fileItem.setSource(odFlowInstance.getDocument().getSourceDept());
                fileItem.setRemark(odFlowInstance.getDocument().getSendNumber());

                return fileItem;
            }
        });

        fileQuery.loadList0();
    }
}
