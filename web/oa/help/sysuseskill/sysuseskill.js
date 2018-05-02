function publish(id){
    changeTag(id,1,function () {
        $.message("发布成功",function () {
            refresh();
        });
    })
}

function noPublish(id){
    changeTag(id,0,function () {
        $.message("取消发布成功",function () {
            refresh();
        });

    })
}