package com.gzzm.oa.mail;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.filestore.*;
import net.cyan.commons.util.*;
import net.cyan.crud.QueryType;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 从邮件里提取文件
 *
 * @author camel
 * @date 13-12-5
 */
public class MailFileStorer implements FileStorer
{
    @Inject
    private static Provider<MailDao> daoProvider;

    public MailFileStorer()
    {
    }

    public String getId()
    {
        return "mail";
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
    }

    public List<InputFile> get(String fileId, Integer userId, Integer deptId) throws Exception
    {
        Long mailId = Long.valueOf(fileId);

        Mail mail = daoProvider.get().getMail(mailId);

        SortedSet<Attachment> attachments = mail.getBody().getAttachments();

        if (attachments != null)
        {
            List<InputFile> files = new ArrayList<InputFile>(attachments.size());

            for (Attachment attachment : attachments)
            {
                files.add(attachment.getInputFile());
            }

            return files;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public void loadFileList(FileQuery fileQuery) throws Exception
    {
        fileQuery.setQueryType(QueryType.oql);
        fileQuery.setBeanType(Mail.class);
        fileQuery.setQueryString("select m from Mail m where userId=:userId and attachment=1 and deleted=0 and " +
                "title like ?title and contains(body.text,?text) and sendTime>=?time_start and sendTime<?time_end" +
                " order by sendTime desc");

        fileQuery.setTransformer(new FileItemTransformer()
        {
            public FileItem transform(Object bean) throws Exception
            {
                Mail mail = (Mail) bean;

                FileItem fileItem = new FileItem();

                fileItem.setId(mail.getMailId().toString());
                fileItem.setTitle(mail.getTitle());

                fileItem.setTime(mail.getSendTime());
                fileItem.setSource(mail.getSenderName());

                return fileItem;
            }
        });

        fileQuery.loadList0();
    }
}
