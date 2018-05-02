/**
 * Created by zy on 2018/3/17.
 */
var name;
function showSubjectName()
{
    var code=Cyan.$("entity.subjectCode").value;
    var subjectName=Cyan.$("entity.subjectName").value;
    if((!subjectName)||(name==null||subjectName==name))
    {
        getSubjectNameByCode(code,{
            callback:function (ret) {
                Cyan.$("entity.subjectName").value=ret;
                name=ret;
            }
        });
    }

}