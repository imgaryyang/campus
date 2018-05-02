System.Opinion = {
    edit: function (text, callback)
    {
        System.Opinion.text = text;

        System.showModal("/opinion/edit", function (result)
        {
            System.Opinion.title = null;
            System.Opinion.remark = null;

            if (result != null)
            {
                callback(result);
            }
        });
    },
    edit_simple: function (text, callback)
    {
        System.Opinion.text = text;

        System.showModal("/opinion/simple", function (result)
        {
            System.Opinion.title = null;
            System.Opinion.remark = null;

            if (result != null)
                callback(result);
        });
    },
    editHtml: function (text, callback)
    {
        System.Opinion.text = text;

        System.showModal("/opinion/html", function (result)
        {
            System.Opinion.title = null;
            System.Opinion.remark = null;

            if (result != null)
            {
                callback(result);
            }
        });
    }
};