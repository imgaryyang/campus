/**
 * 验证单个信息
 */
function validate(obj) {
    var vali = true
    var text = obj.val()
    if (!(text == "" || text == null)) {
        if (obj.attr("id") == 'idCard') {
            if (isCardNo(text)) {
                obj.attr("class", "kuang green ")
            } else {
                vali = false;
                obj.attr("class", "kuang err require ")
            }
        } else if (obj.attr("id") == 'phone') {
            if (isPoneAvailable(text)) {
                obj.attr("class", "kuang green ")
            } else {
                vali = false;
                obj.attr("class", "kuang err require ")
            }
        } else {
            obj.attr("class", "kuang green ")
        }
    } else {
        vali = false;
        obj.attr("class", "kuang err require  ")
    }
    return vali;
}

/**
 * 验证手机号
 * @param phone
 * @returns {boolean}
 */
function isPoneAvailable(phone) {
    var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(phone)) {
        return false;
    } else {
        return true;
    }
}


/**
 * 身份证验证
 * @param card
 * @returns {boolean}
 */
function isCardNo(card) {
    // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if (reg.test(card) === false) {
        return false;
    } else {
        return true
    }
}