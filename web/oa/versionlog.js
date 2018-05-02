Cyan.onload(function () {

    var projectCode = "oa";

    if (showlog) {
        Cyan.Arachne.get("/oa/version/checkpush?project.projectCode="+projectCode, null, function (result) {
            if (result) {
                showlog(projectCode);
            }
        });

        if(Cyan.$("logo")) {
            Cyan.$("logo").onclick = function () {
                showlog(projectCode);
            }
        }
    }
});
