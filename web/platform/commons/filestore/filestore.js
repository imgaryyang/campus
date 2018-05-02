System.FileStore = {};

Cyan.onload(function ()
{
    Cyan.importJs("widgets/menu.js");
});

System.FileStore.init = function (deptId)
{
    if (!System.FileStore.inited)
    {
        System.FileStore.inited = true;
        Cyan.Arachne.createComponent("com.gzzm.platform.commons.filestore.FileCatalogTreeModel",
                "System.FileStore.fileCatalog", null, null, {deptId: deptId}, function ()
                {
                    System.FileStore.fileCatalogMenu = new Cyan.Menu(System.FileStore.fileCatalog);
                });
    }
};

System.FileStore.showCatalog = function (component, callback)
{
    if (!component)
    {
        component = window.event$.target;
        var button = System.getButton(component);
        if (button)
            component = button;
    }
    System.FileStore.fileCatalogMenu.onselect = function (widget)
    {
        var selectedItems = widget.selectedItems();
        if (selectedItems && selectedItems.length > 0)
        {
            var selectedItem = selectedItems[0];
            callback({
                id: selectedItem.value,
                name: selectedItem.text
            });
        }
    };
    System.FileStore.fileCatalogMenu.showWith(component);
};

System.FileStore.go = function (target)
{
    var index = target.indexOf(":");
    var type, id, url;
    if (index > 0)
    {
        type = target.substring(0, index);
        id = target.substring(index + 1);
    }
    else
    {
        type = target;
    }
    if (type == "userfile")
        url = "/oa/userfile/file";
    else if (type == "deptfile")
        url = "/oa/deptfile/file";

    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
};

System.FileStore.selectFile = function (callback)
{
    System.showModal("/filestore/query", function (fileIds)
    {
        callback(fileIds);
    }, {
        width: 1000,
        height: 450
    });
};
