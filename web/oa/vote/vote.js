/**
 *
 * @author db
 * @date 11-12-5
 */
Cyan.onload(function ()
{
    setTimeout(function ()
    {
        if (window.memberSelector && window.memberSelector.getSelector)
        {
            var voteScopes = Cyan.Arachne.form.entity.voteScopes;
            if (voteScopes)
            {
                var n = voteScopes.length;
                for (var i = 0; i < n; i++)
                {
                    var scope = voteScopes[i];
                    memberSelector.getSelector().addSelected({
                        type: scope.type.toLowerCase(), id: scope.objectId, name: scope.objectName
                    });
                }
            }

            scopeTypeChange();

            $$("#selector_search").onkeyup(function ()
            {
                memberSelector.getSelector().query(this.value);
            });
        }
    }, 100);

    if (Cyan.Arachne.form.duplicateKey)
    {
        Cyan.Window.setReturnValue(Cyan.Arachne.form.entity.voteId);
    }
});

function forwardProblem(voteId)
{
    // 跳转到投票问题管理视图
    System.openPage("/oa/vote/VoteProblem?voteId=" + voteId);
}

function forwardGroup(voteId)
{
    if (!voteId)
        voteId = Cyan.Arachne.form.voteId;
    System.openPage("/oa/vote/ProblemGroup?voteId=" + voteId);
}

function showRecord(voteId)
{
    // 跳转到投票记录视图
    System.openPage("/oa/vote/VoteRecord?voteId=" + voteId);
}


function showScope(voteId)
{
    // 跳转到投票记录视图
    System.openPage("/oa/vote/VoteScope?notVoted=true&voteId=" + voteId);
}

function showResult(voteId)
{
    // 跳转到投票结果视图
    System.openPage("/oa/vote/VoteResult?voteId=" + voteId);
}

function showOvertime(voteId, day1, day2, tag)
{
    var url = "/oa/vote/VoteScope?voteId=" + voteId + "&notVoted=false";

    if (day1 != null)
        url += "&day1=" + day1;

    if (day2 != null)
        url += "&day2=" + day2;

    if (tag)
        url += "&tag=" + tag;

    System.openPage(url);
}

function showItems(voteId)
{
    System.openPage("/oa/vote/items?voteId=" + voteId);
}

function showRecordItems(voteId, anonymous)
{
    if (anonymous)
        showRecord(voteId);
    else
        showItems(voteId);
}

function forwardVote(voteId)
{
    // 跳转到投票视图
    var url = "/oa/vote/VotePage?voteId=" + voteId;
    if (Cyan.getUrlParameter("add") == "true")
        url += "&add=true";
    System.openPage(url);
}

function preview(voteId)
{
    if (!voteId)
        voteId = Cyan.Arachne.form.voteId;
    System.openPage("/oa/vote/VotePage?voteId=" + voteId + "&preview=true");
}

function beforeSave()
{
    var members = memberSelector.getSelector().selected;
    var n = members.length;
    var voteScopes = Cyan.Arachne.form.entity.voteScopes = new Array(n);
    for (var i = 0; i < n; i++)
    {
        var member = members[i];
        voteScopes[i] = {type: member.type.toUpperCase(), objectId: member.id};
    }

    return true;
}

function showRecordContent(recordId)
{
    System.openPage("/oa/vote/record/" + recordId + "/show");
}

function editRecordContent(recordId)
{
    System.openPage("/oa/vote/record/" + recordId + "/edit");
}

function editScope(voteId)
{
    System.openPage("/oa/vote/VoteScopeCrud?voteId=" + voteId);
}

function scopeTypeChange()
{
    if (!$("entity.scopeType"))
        return;


    var scopeType = $("entity.scopeType").value;
    if (scopeType == "DEPT")
    {
        window.memberSelector.getSelector().tree.filter(function (node)
        {
            return node.id != "user" && node.id != "usergroup";
        });
        window.memberSelector.types = ["dept"];
    }
    else if (scopeType == "USER")
    {
        window.memberSelector.getSelector().tree.filter(function (node)
        {
            return node.id != "deptgroup";
        });
        window.memberSelector.types = ["dept", "user"];
    }
    else if (scopeType == "ALL")
    {
        window.memberSelector.getSelector().tree.filter(function (node)
        {
            return true;
        });
        window.memberSelector.types = ["dept", "user"];
    }
}