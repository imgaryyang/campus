/**
 * 新增站点前提示先填写路线信息
 */
function addnew() {
    var routeName = Cyan.$("entity.routeName").value;
    if (routeName == "")
        Cyan.message("请先填好路线名称");
    else {
        System.SubList.add('busSiteSubList');
    }
}