package com.gzzm.safecampus.device.card.crud;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.device.card.entity.Job;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * @author liyabin
 * @date 2018/3/26
 */
@Service(url="/device/crud/CardJobCrud")
public class CardJobCrud extends BaseNormalCrud<Job,Integer>
{
    @Like
    private String jobId;

    @Like
    private String jobName;

    public CardJobCrud()
    {
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.addComponent("职务编号", "jobId");
        view.addComponent("职务名称", "jobName");
        view.addColumn("职务名称", "jobName");
        view.addColumn("职务编号", "jobId");
        view.addColumn("权限类别", "privillege");
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("职务编号", "jobId");
        view.addComponent("职务名称", "jobName");
        view.addComponent("权限类别", "privillege");
        view.addDefaultButtons();
        return view;
    }
}
