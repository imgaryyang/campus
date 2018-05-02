/**
 * 发布
 * @param pointId
 */
function toPublish(pointId) {
    toPublicTag(pointId, function (ref) {
        $.message(ref, function () {
            refresh();
        });
    });
}
/**
 * 全部发布
 * @param pointId
 */
function toAllPublic() {
    if($$("#keys")&&$$("#keys").checkedValues()&&$$("#keys").checkedValues().length>0) {
        toAllPublicTag(function (ref) {
            $.message(ref, function () {
                refresh();
            });
        });
    }else {
        $.message("请勾选！");
    }
}