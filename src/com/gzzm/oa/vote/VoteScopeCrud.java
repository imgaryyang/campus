package com.gzzm.oa.vote;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 12-10-25
 */
@Service(url = "/oa/vote/VoteScopeCrud")
public class VoteScopeCrud extends BaseNormalCrud<VoteScope, String>
{
    @Inject
    private VoteDao dao;

    private Integer voteId;

    private Vote vote;

    @Like
    private String objectName;

    public VoteScopeCrud()
    {
        setPageSize(-1);
        addOrderBy("type");
        addOrderBy("dept.leftValue");
        addOrderBy("objectId");
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    @Override
    public VoteScope getEntity(String key) throws Exception
    {
        String[] ss = key.split("_");
        VoteScopeType type = VoteScopeType.valueOf(ss[0]);
        Integer objectId = Integer.valueOf(ss[1]);

        return dao.getVoteScope(voteId, type, objectId);
    }

    @Override
    public String getKey(VoteScope entity) throws Exception
    {
        return entity.getType() + "_" + entity.getObjectId();
    }

    protected Vote getVote() throws Exception
    {
        if (vote == null)
            vote = dao.getVoteById(voteId);
        return vote;
    }

    protected VoteType getType() throws Exception
    {
        return getVote().getType();
    }

    protected String getTypeName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getTypeName();
    }

    protected String getActionName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    public VoteScopeType getScopeType() throws Exception
    {
        return getVote().getScopeType();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.setTitle(getActionName() + "范围");

        view.addComponent("对象名称", "objectName");

        view.addColumn("对象类型", "type");
        view.addColumn("对象名称", "objectName");
        view.addColumn("允许" + getActionName() + "数量", "voteCount");

        view.addButton(Buttons.query());
        view.addButton(Buttons.getButton("crud.add", "addScopes()", "add"));
        view.addButton(Buttons.delete());

        view.makeEditable();

        view.importJs("/oa/vote/votescope.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent(getActionName() + "数量", "voteCount");

        view.addDefaultButtons();

        return view;
    }

    @Override
    public boolean delete(String key) throws Exception
    {
        String[] ss = key.split("_");
        VoteScopeType type = VoteScopeType.valueOf(ss[0]);
        Integer objectId = Integer.valueOf(ss[1]);

        return dao.deleteVoteScope(voteId, type, objectId);
    }

    @Override
    public int deleteAll() throws Exception
    {
        int ret = 0;
        for (String key : getKeys())
        {
            if (remove(key))
                ret++;
        }

        return ret;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public boolean addScopes(List<VoteScope> scopes) throws Exception
    {
        Vote vote = getVote();

        boolean result = false;

        List<VoteScope> scopes0 = vote.getVoteScopes();
        for (VoteScope scope : scopes)
        {
            boolean exists = false;
            for (VoteScope scope0 : scopes0)
            {
                if (scope0.getObjectId().equals(scope.getObjectId()) && scope0.getType() == scope.getType())
                {
                    exists = true;
                    break;
                }
            }

            if (!exists)
            {
                result = true;
                scope.setVoteId(voteId);
                dao.add(scope);
            }
        }

        return result;
    }
}
