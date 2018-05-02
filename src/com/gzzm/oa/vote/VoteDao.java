package com.gzzm.oa.vote;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 投票Dao
 *
 * @author db
 * @date 11-12-4
 */
public abstract class VoteDao extends GeneralDao
{
    public VoteDao()
    {
    }

    public VoteType getVoteType(Integer typeId) throws Exception
    {
        return load(VoteType.class, typeId);
    }

    /**
     * 获取投票信息实例
     *
     * @param voteId 投票信息ID
     * @return 一个对应投票信息ID的投票信息实例
     * @throws Exception 读取数据异常
     */
    public Vote getVoteById(Integer voteId) throws Exception
    {
        return load(Vote.class, voteId);
    }

    public VoteScope getVoteScope(Integer voteId, VoteScopeType type, Integer objectId) throws Exception
    {
        return load(VoteScope.class, voteId, type, objectId);
    }

    public boolean deleteVoteScope(Integer voteId, VoteScopeType type, Integer objectId) throws Exception
    {
        return delete(VoteScope.class, voteId, type, objectId);
    }

    @OQL("select v from Vote v where deptId=:1 and typeId=?2 order by createTime desc")
    public abstract List<Vote> getVotes(Integer deptId, Integer typeId) throws Exception;

    @OQL("select v from Vote v where deptId=:1 and typeId=?2 and valid=1 and " +
            "sysdate()>=startTime and addDay(endTime,1)>sysdate() order by createTime desc")
    public abstract List<Vote> getValidVotes(Integer deptId, Integer typeId) throws Exception;

    /**
     * 获取投票问题实例
     *
     * @param problemId 问题ID
     * @return 一个对应投票问题ID的投票问题实例
     * @throws Exception 读取数据异常
     */
    public VoteProblem getVoteProblemById(Integer problemId) throws Exception
    {
        return load(VoteProblem.class, problemId);
    }

    @OQL("select count(*) from VoteScope where voteId=:1 and type=:2")
    public abstract Integer getScopeCount(Integer voteId, VoteScopeType type) throws Exception;

    @OQL("exists VoteOption where score is not null and voteProblem.voteId=:1")
    public abstract boolean hasScore(Integer voteId) throws Exception;

    @OQL("select p from VoteProblem p where voteId=:1 and groupId is null order by orderId")
    public abstract List<VoteProblem> getProblemsWithoutGroup(Integer voteId) throws Exception;

    @OQL("select g from VoteProblemGroup g where voteId=:1 order by orderId")
    public abstract List<VoteProblemGroup> getProblemGroups(Integer voteId) throws Exception;


    /**
     * 获取投票记录实例
     *
     * @param recordId 投票记录ID
     * @return 对应投票记录ID的投票记录实例
     * @throws Exception 读取数据异常
     */
    public VoteRecord getVoteRecordById(Integer recordId) throws Exception
    {
        return get(VoteRecord.class, recordId);
    }

    @OQL("select r from VoteRecord r where voteId=: and userId=: and state=? and nvl(type,vote.scopeType)=1 order by recordId desc")
    public abstract List<VoteRecord> getVoteRecordsByUserId(Integer voteId, Integer userId, VoteRecordState state)
            throws Exception;

    @OQL("select r from VoteRecord r where voteId=: and deptId=: and state=? and nvl(type,vote.scopeType)=0 order by recordId desc")
    public abstract List<VoteRecord> getVoteRecordsByDeptId(Integer voteId, Integer deptId, VoteRecordState state)
            throws Exception;

    /**
     * 获取投票记录实例
     *
     * @param voteId 投票ID
     * @param userId 用户ID
     * @param state  状态
     * @return 当前用户ID对应的投票记录实例
     * @throws Exception 读取数据异常
     */
    @OQL("select r from VoteRecord r where voteId=: and userId=: and state=? and nvl(type,vote.scopeType)=1 order by recordId desc")
    public abstract VoteRecord getVoteRecordByUserId(Integer voteId, Integer userId, VoteRecordState state)
            throws Exception;

    /**
     * 获取投票记录实例
     *
     * @param voteId 投票ID
     * @param deptId 部门ID
     * @param state  状态
     * @return 当前用户ID对应的投票记录实例
     * @throws Exception 读取数据异常
     */
    @OQL("select r from VoteRecord r where voteId=: and deptId=: and state=? and nvl(type,vote.scopeType)=0 order by recordId desc")
    public abstract VoteRecord getVoteRecordByDeptId(Integer voteId, Integer deptId, VoteRecordState state)
            throws Exception;

    @OQLUpdate("delete from VoteSelected where recordId=:1")
    public abstract void deleteSelectedsByRecordId(Integer recordId);

    @OQLUpdate("delete from VoteInput where recordId=:1 and fileContent is null")
    public abstract void deleteInputsByRecordId(Integer recordId);

    /**
     * 获取投票信息的投票记录数
     *
     * @param voteId 　投票信息ID
     * @return 对应投票信息ID的投票记录总数
     * @throws Exception 读取数据异常
     */
    @OQL("select count(*) from VoteRecord where voteId=:1 and state=1")
    public abstract Integer getVoteRecordCountByVoteId(Integer voteId) throws Exception;

    @OQL("select count(*) from VoteSelected where optionId=:1 and record.state=1")
    public abstract Integer getOptionVoteCount(Integer optionId) throws Exception;

    @OQL("select count(*) from VoteInput where problemId=:1 and record.state=1")
    public abstract Integer getVoteInputCount(Integer problemId) throws Exception;

    @OQL("select count(*) count,avg(numberValue) avg,min(numberValue) min," +
            "max(numberValue) max from VoteInput where problemId=:1 and record.state=1")
    public abstract VoteInputStat getVoteInputStat(Integer problemId) throws Exception;

    public VoteInput getVoteInput(Integer recordId, Integer problemId) throws Exception
    {
        return load(VoteInput.class, recordId, problemId);
    }

    @OQL("select count(*) from VoteRecord where voteId=:1 and state=1")
    public abstract Integer getVoteRecordCount(Integer voteId) throws Exception;

    @OQL("select count(*) from VoteScope s where voteId=:1 and " +
            "(select 1 from VoteRecord r where r.voteId=:1 and r.deptId=s.objectId and s.type=0 " +
            "or r.userId=s.objectId and s.type=1 and r.state=1) is empty")
    public abstract Integer getNotVotedCount(Integer voteId) throws Exception;

    public VoteProject getRootProject() throws Exception
    {
        VoteProject project = load(VoteProject.class, 0);

        if (project == null)
        {
            project = new VoteProject();
            project.setProjectId(0);
            project.setProjectName("根节点");
            add(project);
        }

        return project;
    }
}