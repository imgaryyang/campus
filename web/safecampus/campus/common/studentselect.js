System.selectStudents = function (callback) {
    System.showModal("/campus/common/studentselect", function (result) {
        if (callback)
            callback(result);
    }, {width: 800, height: 450});
};