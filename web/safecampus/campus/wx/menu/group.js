function showCreateMenu() {
    var keys = Cyan.$$("#keys").checkedValues();
    if (keys.length == 0) {
        Cyan.message("请选择菜单分组");
        return;
    }
    System.showModal("/campus/wx/menu/showCreateMenu?keys=" + keys);
}

function editMenu(groupId) {
    System.openPage("/campus/wx/menu/menu?groupId=" + groupId);
}