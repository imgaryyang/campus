Cyan.Validator.getError = function (name)
{
    return name ? name + "不合法" : "数据不合法";
};
Cyan.Validator.getRequireError = function (name)
{
    return name + "不能为空";
};
Cyan.Validator.getNotBlankError = function (name)
{
    return name + "不能为空";
};
Cyan.Validator.getDataTypeError = function (name, value, type, element)
{
    if (type == "number")
        return name + "必须是数字";
    if (type == "integer")
        return name + "必须是整数";
    if (type == "positiveNumber")
        return name + "必须是正数";
    if (type == "positiveInteger")
        return name + "必须是正整数";
    if (type == "date" || type == "time" || type == "datetime" || type == "dateTime")
    {
        var format = element.getAttribute("foramt");
        if (format)
            return name ? name + "的格式必须是" + format : "格式必须是" + format;
        if (type == "date")
            return name + "不是合法的日期格式";
        if (type == "time")
            return name + "不是合法的时间格式";
        return name + "不是合法的日期时间格式";
    }
    var f = Cyan.Validator["getDataTypeError_" + type];
    return f ? f(name, value, element) : name + "格式不合法";
};
Cyan.Validator.getMaxLenError = function (name, value, len)
{
    return name ? name + "的长度不能超过" + len : "长度不能超过" + len;
};
Cyan.Validator.getMinLenError = function (name, value, len)
{
    return name ? name + "的长度不能小于" + len : "长度不能小于" + len;
};
Cyan.Validator.getLenError = function (name, value, len)
{
    return name ? name + "的长度必须是" + len : "长度必须是" + len;
};
Cyan.Validator.getMaxValError = function (name, value, max)
{
    return name + "不能大于" + max;
};
Cyan.Validator.getMinValError = function (name, value, min)
{
    return name + "不能小于" + min;
};
Cyan.Validator.getRangeError = function (name, value, range)
{
    range = range.split(",");
    return name + "必须在" + range[0] + "和" + range[1] + "之间";
};
Cyan.Validator.getEqualError = function (name, value, equal)
{
    var equalName = Cyan.Validator.getShowName(equal);
    return equalName ? name + "和" + equalName + "不一致" : "数据不一致";
};
Cyan.Validator.getLessThanError = function (name, value, less)
{
    return name + "必须小于" + Cyan.Validator.getShowName(less);
};
Cyan.Validator.getLessEqualError = function (name, value, less)
{
    return name + "不能大于" + Cyan.Validator.getShowName(less);
};
Cyan.Validator.getLargerThanError = function (name, value, larger)
{
    return name + "必须大于" + Cyan.Validator.getShowName(larger);
};
Cyan.Validator.getLargerEqualError = function (name, value, larger)
{
    return name + "不能小于" + Cyan.Validator.getShowName(larger);
};
Cyan.Validator.getMaxCountError = function (name, value, count)
{
    return name + "最多只能选择" + count + "项";
};
Cyan.Validator.getMinCountError = function (name, value, count)
{
    return name + "最少要选择" + count + "项";
};
Cyan.Validator.getFileTypeError = function (name, value, fileTypes)
{
    return name + "只能上传" + Cyan.Validator.changeFileType(fileTypes) + "格式的文件";
};
Cyan.Validator.chineseNumber = ["０", "１", "２", "３", "４", "５", "６", "７", "８", "９"];
Cyan.Validator.decorateNumberValidator = function (f)
{
    Cyan.Validator[f + "_"] = Cyan.Validator[f];
    Cyan.Validator[f] = function (value, element)
    {
        if (element)
        {
            for (var i in Cyan.Validator.chineseNumber)
                value = Cyan.replaceAll(value, Cyan.Validator.chineseNumber[i], "" + i);
            if (value != element.value)
                element.value = value;
        }
        return Cyan.Validator[f + "_"](value);
    };
};
Cyan.Validator.decorateNumberValidator("isNumber");
Cyan.Validator.decorateNumberValidator("isInteger");
Cyan.Validator.decorateNumberValidator("isPositiveNumber");
Cyan.Validator.decorateNumberValidator("isPositiveInteger");
Cyan.Validator.getDataTypeError_email = function (name, value, element)
{
    return name + "不是合法的电子邮箱地址";
};
Cyan.Validator.getDataTypeError_ip = function (name, value, element)
{
    return name + "不合法的IP地址";
};