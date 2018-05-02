/**
 * Created by zy on 2018/4/12.
 */
function predictFace0()
{
    predictFace({
        callback:function (ret) {
            $.message(ret);
        }
    })
}

function retrainFace0()
{
    retrainFace({
        callback:function (ret) {
            $.message(ret);
        }
    })
}