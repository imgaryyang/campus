function provinceChange() {
    Cyan.Arachne.refresh("entity.cityId");
}

function saveWithPic() {
    save0({
        callback: function (ret)
        {
            $.message(Cyan.Arachne.form.new$? "添加成功" : "修改成功", function ()
            {
                //新增
                if(isNew()) {
                    //重置input
                    reset();
                    //重置图片
                    Cyan.$$("#imageId").set("src","")
                }else{
                    //修改完成
                    closeWindow(true)
                }
            });
        },
        progress: false,
        form: Cyan.$$("form")[0]
    });
}


function chooseFile() {
    var preview = document.getElementById("imageId");
    var file = document.getElementById("t_file").files[0];
    var reader = new FileReader();
    reader.onloadend = function () {
        preview.src = reader.result;
    }
    if (file) {
        reader.readAsDataURL(file);
    } else {
        preview.src = "";
    }
}


function saveInst() {
    save(function () {
        closeWindow(function () {
            mainBody.reload();
        })
    })
}

