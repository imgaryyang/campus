function forwardProblem(groupId)
{
    // 跳转到投票问题管理视图
    System.openPage(System.formatUrl("/oa/vote/VoteProblem?voteId=" + Cyan.Arachne.form.voteId + "&groupId=" +
            groupId));

    if (Cyan.Arachne.form.duplicateKey)
    {
        Cyan.Window.setReturnValue(Cyan.Arachne.form.entity.groupId);
    }
}