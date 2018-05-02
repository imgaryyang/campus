package com.gzzm.oa.vote;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.WordProteusForwardProcessor;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 投票页面业务
 *
 * @author db
 * @date 11-12-4
 */
@Service
public class VotePage
{
    private static final DecimalFormat RATIOFORMAT = new DecimalFormat("#.##%");

    @Inject
    private VoteDao dao;

    /**
     * @noinspection MismatchedQueryAndUpdateOfCollection
     */
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 投票部门
     */
    private Integer deptId;

    /**
     * 接收传来的投票信息ID
     */
    private Integer voteId;

    /**
     * 接收网页传来的单选多选数据值，key为problemId，值为选择的选项
     */

    @NotSerialized
    private Map<String, List<Integer>> options;

    /**
     * 接收网传来的输入数据值，key为problemId，值为填写的值
     */
    @NotSerialized
    private Map<String, String> inputs;

    @NotSerialized
    private Map<String, String> optionInputs;

    /**
     * 接收网传来的输入文件，key为problemId，值为上传的文件
     */
    @NotSerialized
    private Map<String, InputFile> files;

    /**
     * 每个问题的得分，key是problemId，value是得分
     */
    private Map<Integer, Integer> scores;

    /**
     * 总的得分
     */
    private Integer score;

    /**
     * 自动注入当前用户信息
     * 投票人信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 投票信息实例
     */
    @NotSerialized
    private Vote vote;

    @NotSerialized
    private VoteScope scope;

    /**
     * 当前用户之前的投票，如果还未投票，则为空
     */
    @NotSerialized
    private VoteRecord record;

    @NotSerialized
    private Boolean readOnly;

    @NotSerialized
    private boolean preview;

    /**
     * 记录数，用于统计
     */
    @NotSerialized
    private Integer recordCount;

    private Map<Integer, Integer> optionVoteCounts;

    private Map<Integer, Integer> inputCounts;

    private Map<Integer, VoteInputStat> voteInputStats;

    private VoteType type;

    private boolean anonymous;

    private Integer recordId;

    private OptionOrderType optionOrderType;

    private Comparator<VoteOption> optionComparator;

    private boolean export;

    private Integer costTime;

    /**
     * 是否已经超过时间限制
     */
    private boolean overtime;

    /**
     * 新加投票
     */
    private boolean add;

    public VotePage()
    {
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Map<String, List<Integer>> getOptions()
    {
        return options;
    }

    public void setOptions(Map<String, List<Integer>> options)
    {
        this.options = options;
    }

    public Map<String, String> getInputs()
    {
        return inputs;
    }

    public void setInputs(Map<String, String> inputs)
    {
        this.inputs = inputs;
    }

    public Map<String, String> getOptionInputs()
    {
        return optionInputs;
    }

    public void setOptionInputs(Map<String, String> optionInputs)
    {
        this.optionInputs = optionInputs;
    }

    public Map<String, InputFile> getFiles()
    {
        return files;
    }

    public void setFiles(Map<String, InputFile> files)
    {
        this.files = files;
    }

    public boolean isAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    public Integer getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Integer recordId)
    {
        this.recordId = recordId;
    }

    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public Vote getVote()
    {
        return vote;
    }

    public Integer getTimeLimit()
    {
        return vote == null || vote.getTimeLimit() == null ? null : vote.getTimeLimit() * 60;
    }

    public Integer getCostTime()
    {
        return costTime;
    }

    public boolean isOvertime()
    {
        return overtime;
    }

    public VoteScope getScope()
    {
        return scope;
    }

    public VoteRecord getRecord()
    {
        return record;
    }

    protected VoteType getType()
    {
        return type;
    }

    public boolean isAdd()
    {
        return add;
    }

    public void setAdd(boolean add)
    {
        this.add = add;
    }

    @NotSerialized
    public List<VoteRecord> getRecords() throws Exception
    {
        if (vote != null && scope != null)
        {
            if (scope.getType() == VoteScopeType.DEPT)
            {
                return dao.getVoteRecordsByDeptId(voteId, deptId, VoteRecordState.committed);
            }
            else
            {
                return dao.getVoteRecordsByUserId(voteId, userOnlineInfo.getUserId(), VoteRecordState.committed);
            }
        }

        return null;
    }

    @NotSerialized
    public String getTypeName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getTypeName();
    }

    @NotSerialized
    public String getActionName()
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    @NotSerialized
    public String getObjectName()
    {
        if (vote != null && vote.getScopeType() != null)
        {
            switch (vote.getScopeType())
            {
                case DEPT:
                    return "部门";
                case USER:
                    return "用户";

            }
        }

        return "用户或部门";
    }

    protected Comparator<VoteOption> getOptionComparator()
    {
        if (optionComparator == null)
        {
            optionComparator = new Comparator<VoteOption>()
            {
                public int compare(VoteOption option1, VoteOption option2)
                {
                    try
                    {
                        if (optionOrderType == OptionOrderType.ASC)
                            return getOptionVoteCount(option1).compareTo(getOptionVoteCount(option2));
                        else
                            return getOptionVoteCount(option2).compareTo(getOptionVoteCount(option1));
                    }
                    catch (Exception ex)
                    {
                        Tools.wrapException(ex);

                        return 0;
                    }
                }
            };
        }

        return optionComparator;
    }

    protected VoteScope getVoteScope(Vote vote)
    {
        if (vote.getScopeType() == VoteScopeType.DEPT)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (authDeptIds == null && userOnlineInfo.getDeptId().equals(scope.getObjectId()) ||
                        authDeptIds != null && authDeptIds.contains(scope.getObjectId()))
                {
                    return scope;
                }
            }
        }
        else if (vote.getScopeType() == VoteScopeType.USER)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (scope.getType() == VoteScopeType.USER && userOnlineInfo.getUserId().equals(scope.getObjectId()))
                    return scope;
            }

            List<Integer> parentDeptIds = userOnlineInfo.getParentDeptIds();

            for (VoteScope scope : vote.getVoteScopes())
            {
                if (scope.getType() == VoteScopeType.DEPT && parentDeptIds.contains(scope.getObjectId()))
                    return scope;
            }
        }
        else if (vote.getScopeType() == VoteScopeType.ALL)
        {
            for (VoteScope scope : vote.getVoteScopes())
            {
                if (scope.getType() == VoteScopeType.USER && userOnlineInfo.getUserId().equals(scope.getObjectId()))
                    return scope;
            }

            for (VoteScope scope : vote.getVoteScopes())
            {
                if (authDeptIds == null && userOnlineInfo.getDeptId().equals(scope.getObjectId()) ||
                        authDeptIds != null && authDeptIds.contains(scope.getObjectId()))
                {
                    return scope;
                }
            }
        }

        return null;
    }

    @NotSerialized
    public List<VoteProblemGroup> getGroups() throws Exception
    {
        List<VoteProblemGroup> groups = new ArrayList<VoteProblemGroup>(getVote().getGroups());

        VoteProblemGroup group = new VoteProblemGroup();
        group.setProblems(getProblems(group));

        groups.add(group);

        return groups;
    }

    public List<VoteProblem> getProblems(VoteProblemGroup group) throws Exception
    {
        if (group.getGroupId() != null)
            return group.getProblems();

        return dao.getProblemsWithoutGroup(voteId == null ? vote.getVoteId() : voteId);
    }

    @NotSerialized
    public List<VoteProblem> getProblems()
    {
        return vote.getProblems();
    }

    public Integer getRecordCount() throws Exception
    {
        if (recordCount == null)
            recordCount = dao.getVoteRecordCountByVoteId(getVote().getVoteId());

        return recordCount;
    }

    public List<VoteOption> getOptionList(VoteProblem problem) throws Exception
    {
        List<VoteOption> voteOptions = problem.getOptionList();

        if (problem.getOther() != null && problem.getOther())
        {
            voteOptions = new ArrayList<VoteOption>(voteOptions);

            VoteOption other = new VoteOption();
            other.setProblemId(problem.getProblemId());
            other.setOptionId(-1);
            other.setOptionName("其他");
            voteOptions.add(other);
        }

        optionOrderType = problem.getOrderType();
        if (optionOrderType != null && optionOrderType != OptionOrderType.ORIGINAL)
        {
            Collections.sort(voteOptions, getOptionComparator());
        }

        return voteOptions;
    }

    public Integer getOptionVoteCount(VoteOption option) throws Exception
    {
        if (option.getOptionId() == -1)
            return getVoteInputCount(option.getProblemId());
        else
            return getOptionVoteCount(option.getOptionId());
    }

    public String getOptionVoteRatio(VoteOption option) throws Exception
    {
        if (option.getOptionId() == -1)
            return getVoteInputRatio(option.getProblemId());
        else
            return getOptionVoteRatio(option.getOptionId());
    }

    /**
     * 获取投票选项被投票结果
     *
     * @param optionId 投票选项ID
     * @return 对应投票选项ID的选项被投结果数
     * @throws Exception 查询数据库错误
     */
    public Integer getOptionVoteCount(Integer optionId) throws Exception
    {
        if (optionVoteCounts == null)
            optionVoteCounts = new HashMap<Integer, Integer>();
        Integer count = optionVoteCounts.get(optionId);
        if (count == null)
            optionVoteCounts.put(optionId, count = dao.getOptionVoteCount(optionId));

        return count;
    }

    public String getOptionVoteRatio(Integer optionId) throws Exception
    {
        Integer count = getOptionVoteCount(optionId);

        return RATIOFORMAT.format(count.floatValue() / getRecordCount().floatValue());
    }

    /**
     * 获取某个问题其他选项被投票数量
     *
     * @param problemId 投票问题ID
     * @return 对应问题选其他的数量
     * @throws Exception 查询数据库错误
     */
    public Integer getVoteInputCount(Integer problemId) throws Exception
    {
        if (inputCounts == null)
            inputCounts = new HashMap<Integer, Integer>();
        Integer count = inputCounts.get(problemId);
        if (count == null)
            inputCounts.put(problemId, count = dao.getVoteInputCount(problemId));

        return count;
    }

    public String getVoteInputRatio(Integer problemId) throws Exception
    {
        Integer count = getVoteInputCount(problemId);

        return RATIOFORMAT.format(count.floatValue() / getRecordCount().floatValue());
    }

    public VoteInputStat getVoteInputStat(Integer problemId) throws Exception
    {
        if (voteInputStats == null)
            voteInputStats = new HashMap<Integer, VoteInputStat>();

        VoteInputStat stat = voteInputStats.get(problemId);
        if (stat == null)
            voteInputStats.put(problemId, stat = dao.getVoteInputStat(problemId));

        return stat;
    }

    public Integer getScore(Integer problemId) throws Exception
    {
        if (scores != null)
            return scores.get(problemId);

        return null;
    }

    public Integer getScore()
    {
        return score;
    }

    public Boolean isReadOnly()
    {
        if (readOnly == null)
            readOnly = getVote().isEnd();
        return readOnly;
    }

    public boolean isPreview()
    {
        return preview;
    }

    public void setPreview(boolean preview)
    {
        this.preview = preview;
    }

    public boolean isExport()
    {
        return export;
    }

    public void setExport(boolean export)
    {
        this.export = export;
    }

    @Service(url = "/oa/vote/VotePage")
    public String forwardVote() throws Exception
    {
        vote = dao.getVoteById(voteId);
        type = vote.getType();
        scope = getVoteScope(vote);

        VoteScopeType scopeType = vote.getScopeType();
        if (scopeType == VoteScopeType.ALL)
            scopeType = scope == null ? VoteScopeType.USER : scope.getType();

        boolean b = vote.getPeriod() == VotePeriod.INDEFINITE ||
                scope != null && scope.getVoteCount() != null && scope.getVoteCount() > 1;

        if (scopeType == VoteScopeType.DEPT)
        {
            if (deptId == null)
            {
                deptId = scope == null ? userOnlineInfo.getDeptId() : scope.getObjectId();
            }

            record = dao.getVoteRecordByDeptId(voteId, deptId, VoteRecordState.uncommitted);
            if (record == null && !(b && add))
            {
                record = dao.getVoteRecordByDeptId(voteId, deptId, VoteRecordState.committed);
            }

            if (record != null)
            {
                recordId = record.getRecordId();
            }
        }
        else
        {
            record = dao.getVoteRecordByUserId(voteId, userOnlineInfo.getUserId(), VoteRecordState.uncommitted);
            if (record == null && !(b && add))
            {
                record = dao.getVoteRecordByUserId(voteId, userOnlineInfo.getUserId(), VoteRecordState.committed);
            }

            if (record != null)
                recordId = record.getRecordId();
        }

        String forward = show();

        if (!preview && !isReadOnly())
        {
            Integer timeLimit = getTimeLimit();
            if (timeLimit != null)
            {
                if (record != null)
                {
                    costTime = record.getCostTime();
                    if (costTime == null)
                        costTime = 0;
                }
                else
                {
                    costTime = 0;
                }
            }
        }

        return forward;
    }

    @Service(url = "/oa/vote/record/{$0}/show")
    public String showRecord(Integer recordId) throws Exception
    {
        this.recordId = recordId;
        record = dao.getVoteRecordById(recordId);
        vote = record.getVote();
        type = vote.getType();
        readOnly = true;

        return show();
    }

    @Service(url = "/oa/vote/record/{$0}/edit")
    public String editRecord(Integer recordId) throws Exception
    {
        this.recordId = recordId;
        record = dao.getVoteRecordById(recordId);
        vote = record.getVote();
        type = vote.getType();
        readOnly = false;

        String forward = show();

        if (!isReadOnly())
        {
            Integer timeLimit = getTimeLimit();
            if (timeLimit != null)
            {
                if (record != null)
                {
                    costTime = record.getCostTime();
                    if (costTime == null)
                        costTime = 0;
                }
                else
                {
                    costTime = 0;
                }
            }
        }

        return forward;
    }

    @Service(url = "/oa/vote/VoteResult")
    public String showVoteResult() throws Exception
    {
        vote = dao.getVoteById(getVoteId());
        type = vote.getType();

        if (export)
        {
            WordProteusForwardProcessor.setFileName(vote.getTitle() + "(结果统计)");
        }

        // 返回投票页面，并显示
        if (export)
            return "voteresult.docp";
        else
            return "voteresult.ptl";
    }

    private String show() throws Exception
    {
        inputs = new HashMap<String, String>();
        options = new HashMap<String, List<Integer>>();

        if (record != null)
        {
            for (VoteInput voteInput : record.getVoteInputList())
            {
                String s = "p" + voteInput.getProblemId();
                inputs.put(s, voteInput.getInputContent());

                List<Integer> optionIds = options.get(s);
                if (optionIds == null)
                    options.put(s, optionIds = new ArrayList<Integer>());

                optionIds.add(-1);
            }

            for (VoteSelected selected : record.getSelecteds())
            {
                if (options == null)
                    options = new HashMap<String, List<Integer>>();

                VoteOption option = selected.getVoteOption();
                Integer problemId = option.getProblemId();

                String s = "p" + problemId;
                List<Integer> optionIds = options.get(s);
                if (optionIds == null)
                    options.put(s, optionIds = new ArrayList<Integer>());

                optionIds.add(selected.getOptionId());

                if (record.getState() == VoteRecordState.committed)
                {
                    Integer score = null;
                    if (scores != null)
                        score = scores.get(problemId);

                    if (option.getScore() != null)
                    {
                        if (score == null)
                            score = option.getScore();
                        else
                            score += option.getScore();

                        if (this.score == null)
                            this.score = option.getScore();
                        else
                            this.score += option.getScore();
                    }

                    if (score != null)
                    {
                        if (scores == null)
                            scores = new HashMap<Integer, Integer>();

                        scores.put(problemId, score);
                    }
                }
            }

            //已经提交，不能修改
            if (record.getState() == VoteRecordState.committed)
            {
                readOnly = true;
            }
            else
            {
                if (vote.getTimeLimit() != null && record.getCostTime() != null &&
                        record.getCostTime() >= vote.getTimeLimit() * 60)
                {
                    overtime = true;
                }
            }

            if (record.isAnonymous() != null)
                anonymous = record.isAnonymous();
        }
        else
        {
            if (vote.isAnonymous() != null)
                anonymous = vote.isAnonymous();
        }

        // 返回投票页面，并显示

        if (export)
        {
            WordProteusForwardProcessor.setFileName(vote.getTitle());
        }

        if (type != null && !StringUtils.isBlank(type.getShowPage()))
        {
            String s = type.getShowPage();
            int index = s.lastIndexOf('.');

            if (export)
            {
                if (index >= 0)
                    return s.substring(index + 1) + "docp";
                else
                    return s + ".docp";
            }
            else
            {
                if (index >= 0)
                    return s;
                else
                    return s + ".ptl";
            }
        }
        else if (export)
        {
            return "votepage.docp";
        }
        else
        {
            return "votepage.ptl";
        }
    }

    /**
     * 加有@ObjectResult注解标签的，返回是一个结果，而不再是返回一个页面
     *
     * @param commit 是否提交
     * @return 返回提示信息
     * @throws Exception 保存到数据库错误
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public Object save(boolean commit) throws Exception
    {
        // 根据ID取得投票信息
        Vote vote = dao.getVoteById(voteId);
        type = vote.getType();

        // 判断投票是否开始
        if (System.currentTimeMillis() < vote.getStartTime().getTime())
        {
            return Tools.getMessage("oa.vote.noStarted", getActionName());
        }
        else if (vote.isEnd())
        {
            return Tools.getMessage("oa.vote.ended", getActionName());
        }

        // 投票记录
        VoteRecord record;
        VoteScope voteScope = getVoteScope(vote);
        VoteScopeType scopeType = vote.getScopeType();
        if (scopeType == VoteScopeType.ALL)
            scopeType = voteScope == null ? VoteScopeType.USER : voteScope.getType();

        if (deptId == null && voteScope != null && voteScope.getType() == VoteScopeType.DEPT)
            deptId = voteScope.getObjectId();

        if (recordId != null)
        {
            record = dao.getVoteRecordById(recordId);
        }
        else
        {
            if (scopeType == VoteScopeType.DEPT)
            {
                if (vote.getPeriod() != VotePeriod.INDEFINITE && voteScope != null && voteScope.getVoteCount() != null)
                {
                    List<VoteRecord> records = dao.getVoteRecordsByDeptId(voteId, deptId, VoteRecordState.committed);
                    if (records.size() >= voteScope.getVoteCount())
                    {
                        return Tools.getMessage("oa.vote.limit", new Object[]{records.size()});
                    }
                }

                record = dao.getVoteRecordByDeptId(voteId, deptId, VoteRecordState.uncommitted);
            }
            else
            {
                if (vote.getPeriod() != VotePeriod.INDEFINITE && voteScope != null && voteScope.getVoteCount() != null)
                {
                    List<VoteRecord> records =
                            dao.getVoteRecordsByUserId(voteId, userOnlineInfo.getUserId(), VoteRecordState.committed);
                    if (records.size() >= voteScope.getVoteCount())
                    {
                        return Tools.getMessage("oa.vote.limit", new Object[]{records.size()});
                    }
                }

                record = dao.getVoteRecordByUserId(voteId, userOnlineInfo.getUserId(), VoteRecordState.uncommitted);
            }
        }

        if (record == null)
        {
            record = new VoteRecord();
            record.setUserId(this.getUserId());
            record.setDeptId(deptId);
            record.setVoteId(this.getVoteId());
            record.setVoteTime(new Date());
            record.setAnonymous(anonymous);
            record.setState(commit ? VoteRecordState.committed : VoteRecordState.uncommitted);
            record.setType(scopeType);

            dao.add(record);
        }
        else
        {
            if (vote.getTimeLimit() != null && record.getCostTime() != null &&
                    record.getCostTime() >= vote.getTimeLimit() * 60)
            {
                overtime = true;
            }

            record.setVoteTime(new Date());
            record.setAnonymous(anonymous);
            if (commit)
                record.setState(VoteRecordState.committed);
            dao.update(record);

            if ((options != null || inputs != null) && !overtime)
            {
                dao.deleteInputsByRecordId(record.getRecordId());
                dao.deleteSelectedsByRecordId(record.getRecordId());
            }
        }

        // 处理单选和多选数据（不包括其它值）
        if (options != null && !overtime)
        {
            for (List<Integer> optionIds : options.values())
            {
                for (Integer optionId : optionIds)
                {
                    if (optionId > 0)
                    {
                        VoteSelected selected = new VoteSelected();
                        selected.setRecordId(record.getRecordId());
                        selected.setOptionId(optionId);
                        dao.add(selected);
                    }
                }
            }
        }

        dao.update(record);

        // 处理输入框数据（包括单选和多选的其它值）
        if (inputs != null && !overtime)
        {
            for (Map.Entry<String, String> entry : inputs.entrySet())
            {
                String value = entry.getValue();

                if (!StringUtils.isEmpty(value))
                {
                    Integer problemId = Integer.valueOf(entry.getKey().substring(1));

                    VoteInput input = new VoteInput();
                    input.setRecordId(record.getRecordId());
                    input.setProblemId(problemId);
                    input.setInputContent(value);

                    VoteProblem problem = dao.getVoteProblemById(problemId);
                    ProblemDataType dataType = problem.getDataType();
                    if (dataType == ProblemDataType.INTEGER || dataType == ProblemDataType.NUMBER)
                    {
                        input.setNumberValue(Float.valueOf(value));
                    }
                    else if (dataType == ProblemDataType.DATE)
                    {
                        input.setDateValue(DateUtils.toDate(value));
                    }

                    // 将选项输入实例持久化
                    dao.add(input);
                }
            }
        }

        // 处理输入框数据（包括单选和多选的其它值）
        if (files != null && !overtime)
        {
            for (Map.Entry<String, InputFile> entry : files.entrySet())
            {
                InputFile file = entry.getValue();

                if (file != null)
                {
                    Integer problemId = Integer.valueOf(entry.getKey().substring(1));

                    VoteInput input = new VoteInput();
                    input.setRecordId(record.getRecordId());
                    input.setProblemId(problemId);
                    input.setInputContent(file.getName());
                    input.setFileContent(file.getInputStream());

                    // 将选项输入实例持久化
                    dao.save(input);
                }
            }
        }

        return record.getRecordId();
    }

    @Service(url = "/oa/vote/record/{$0}/down/{$1}")
    public InputFile down(Integer recordId, Integer problemId) throws Exception
    {
        VoteInput voteInput = dao.getVoteInput(recordId, problemId);

        return new InputFile(voteInput.getFileContent(), voteInput.getInputContent());
    }

    @Service(method = HttpMethod.post)
    public void updateCostTime(Integer recordId, Integer costTime) throws Exception
    {
        VoteRecord record = new VoteRecord();
        record.setRecordId(recordId);
        record.setCostTime(costTime);

        dao.update(record);
    }
}
