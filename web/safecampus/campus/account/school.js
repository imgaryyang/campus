/**
 * Created by zy on 2018/3/14.
 */
Cyan.onload(function () {
    Cyan.Class.overwrite(window, "showSchool", function (key) {
        var showSchool = this.inherited;
        showSchool(key, {
            target: '_page'
        });
    });

    System.Crud.messages.addSuccess = "开通成功";

    window.updateSuccess = function () {
        Cyan.message(System.Crud.messages.updateSuccess, function () {
            Cyan.Window.setReturnValue(true);
            System.closePage();
        });
    };

    window.addSuccess = function (key) {
        var password = Cyan.$("entity.user.password").value;
        System.Crud.messages.addSuccess = "开通成功，请记住登录初始密码为：" + password;
        Cyan.message(System.Crud.messages.addSuccess, function () {
            var close = Cyan.Arachne.form.duplicateKey || window.closeAfterAdd;
            if (close)
            {
                Cyan.Window.setReturnValue(key);
                Cyan.Window.closeWindow();
            }
            else
            {
                if (key)
                {
                    if (!returnValue)
                        returnValue = [key];
                    else
                        returnValue.push(key);
                    Cyan.Window.setReturnValue(returnValue);
                }
                Cyan.$$("form").reset();
                getStringRandom(8, {
                    callback: function (ret) {
                        Cyan.$("entity.user.password").value = ret;
                    }
                })
            }
        });
    }
});

function chooseFile()
{
    var preview = document.getElementById("imageId");
    var file = document.getElementById("t_file").files[0];
    var reader = new FileReader();
    reader.onloadend = function () {
        preview.src = reader.result;
    }
    if (file)
    {
        reader.readAsDataURL(file);
    }
    else
    {
        preview.src = "";
    }
}


function onUserNameChange()
{
    var userName = Cyan.$("entity.phone").value;
    if (userName)
    {
        var loginName = Cyan.$("entity.user.loginName");
        if (loginName && !loginName.value)
        {
            Cyan.$("entity.user.loginName").value = userName;
        }
    }
}

function showUpdatePassword(userId)
{
    System.showModal("/campus/account/schoolpassword/" + userId);

}

function savePassword()
{
    var password = Cyan.$("password").value;
    updatePassword(password, {
        callback: function (ret) {
            if (ret)
            {
                $.message("修改成功", function () {
                    closeWindow();
                })
            }
        },
        validate: true
    });
}