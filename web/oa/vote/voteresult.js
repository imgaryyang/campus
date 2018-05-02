Cyan.importJs("/platform/weboffice/office.js");

function showDetail(problemId)
{
    System.openPage("/oa/vote/VoteRecord?voteId=" + Cyan.Arachne.form.voteId + "&problemId=" + problemId);
}

function exportResult()
{
    window.open(System.formatUrl("/oa/vote/VoteResult?voteId=" + Cyan.Arachne.form.voteId + "&export=true"));
}

function printResult()
{
    System.Office.print(System.formatUrl("/oa/vote/VoteResult?voteId=" + Cyan.Arachne.form.voteId + "&export=true"),
            "doc");
}