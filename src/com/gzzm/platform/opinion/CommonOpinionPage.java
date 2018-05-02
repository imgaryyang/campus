package com.gzzm.platform.opinion;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.CrudUtils;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author camel
 * @date 14-1-24
 */
@Service(url = "/opinion/common/select")
public class CommonOpinionPage extends BaseQueryCrud<CommonOpinion, Integer>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    public CommonOpinionPage()
    {
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addColumn("意见标题", "title").setWidth("150");
        view.addColumn("意见内容", "content");

        view.defaultInit(false);

        view.addButton(Buttons.ok().setIcon(Buttons.getIcon("ok")));

        view.importJs("/platform/opinion/common_select.js");

        return view;
    }

    @NotSerialized
    public Collection<Integer> getDeptIds() throws Exception
    {
        return userOnlineInfo.getParentDeptIds();
    }

    @Service(method = HttpMethod.post)
    @Transactional
    @ObjectResult
    public void select() throws Exception
    {
        for (Integer key : getKeys())
        {
            CommonOpinion entity = getEntity(key);

            Opinion opinion = new Opinion();
            opinion.setUserId(userOnlineInfo.getUserId());
            opinion.setTitle(entity.getTitle());
            opinion.setContent(entity.getContent());
            opinion.setOrderId(CrudUtils.getOrderValue(6, false));

            getCrudService().add(opinion);
        }
    }
}
