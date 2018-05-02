function pay() {
    $("#payMask").css("display", "block")
    var h = $("#pay").height()
    $("#pay").css("bottom", -h)
    $("#pay").animate({
        'bottom': 0
    }, {
        duration: 300,		//时长
        complete: function () {
            //完成时执行
        }
    })

}

$(".gb").click(function () {
    // $("#payMask").css("display","none")
    $("#payMask").fadeOut()
})

$(".zflb li a").click(function () {
    $(".zflb li a").removeClass("active")
    $(this).addClass("active")

})


function submit(billId) {
    //TODO 测试支付页面
    window.location.href = "/test/pay/payment/" + billId;

    //TODO 正式使用切换
    // var HttpPathPay = "http://121.15.180.66:801/NetPayment/BaseHttp.dll?MB_EUserPay";
    // $.get("/wx/pay/payment/simple/" + billId, function (json) {
    //     sendFormRequest(json, HttpPathPay);
    // })
}