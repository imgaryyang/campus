package com.gzzm.ods.receipt.attachment;

import com.gzzm.ods.receipt.*;
import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.CacheData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author camel
 * @date 2016/6/14
 */
@Service(url = "/ods/receipt/attachment")
public class AttachmentReceiptAttachmentCrud extends ReceiptAttachmentCrud
{
    public AttachmentReceiptAttachmentCrud()
    {
        repliable = true;
    }

    @Override
    protected Long loadAttachmentId() throws Exception
    {
        Receipt receipt = service.getDao().getReceipt(receiptId);

        Long attachmentId = receipt.getLinkId();
        if (attachmentId == null)
        {
            attachmentId = dao.getAttachmentId();

            receipt.setLinkId(attachmentId);
            dao.update(receipt);
        }

        return attachmentId;
    }

    @Override
    public void reply() throws Exception
    {
        Long attachmentId = getAttachmentId();

        List<Attachment> attachments = dao.getAttachmentsByDept(attachmentId, getDeptId());

        if (attachments.size() == 0)
            throw new NoErrorException("ods.receipt.attachment.require");
        super.reply();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (PageTableView) super.createListView();

        view.setTitle("材料回执反馈信息");
        if (isReadOnly())
        {
            view.addButton(Buttons.getButton("批量下载", "downAllAttachment()","down"));
        }
        return view;
    }

    @Service
    public InputFile downAllAttachment() throws Exception{
        CacheData cacheData = new CacheData();
        cacheData.setAutoClear(true);
        CompressUtils.Compress compress = CompressUtils.createCompress("zip", cacheData);
        Map<String,List<Attachment>> deptMap = new HashMap<String,List<Attachment>>();
        for(Attachment attachment:getList()){
            String deptName=attachment.getDept().getDeptName();
            if(StringUtils.isBlank(deptName)){
                continue;
            }
            if(deptMap.get(deptName)==null){
                List<Attachment> unitList=new ArrayList<Attachment>();
                unitList.add(attachment);
                deptMap.put(deptName,unitList);
            }else{
                deptMap.get(deptName).add(attachment);
            }

        }
        zip(compress, deptMap);
        compress.close();
        return new InputFile(cacheData, service.getDao().getReceipt(receiptId).getDocument().getTitle()+"回执反馈信息.zip");
    }

    private void zip(CompressUtils.Compress compress, Map<String,List<Attachment>> deptMap) throws Exception {
        for(Map.Entry<String,List<Attachment>> entry:deptMap.entrySet()){
            compress.addDirectory(entry.getKey());
            for(Attachment attachment:entry.getValue()){
                zip(compress,attachment,entry.getKey());
            }
        }
    }

    private void zip(CompressUtils.Compress compress, Attachment attachment,String path) throws Exception {
        compress.addFile(path + "/" + attachment.getAttachmentName(), attachment.getInputStream(),
                attachment.getUploadTime().getTime(), attachment.getRemark());
    }
}
