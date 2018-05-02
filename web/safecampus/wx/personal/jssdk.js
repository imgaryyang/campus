/**
 * 微信jssdk设置
 */
function jssdk(debug) {
    if (debug == null) debug = true;
    $.ajax({
        url: "/wx/personal/jsapiConfig",
        type: 'post',
        dataType: 'json',
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        data: {
            'url': location.href.split('#')[0]
        },
        success: function (data) {
            wx.config({
                debug: debug,
                appId: data.data.appId,
                timestamp: data.data.timestamp,
                nonceStr: data.data.nonceStr,
                signature: data.data.signature,
                jsApiList: ['checkJsApi', 'onMenuShareTimeline',
                    'onMenuShareAppMessage', 'onMenuShareQQ',
                    'onMenuShareWeibo', 'hideMenuItems',
                    'showMenuItems', 'hideAllNonBaseMenuItem',
                    'showAllNonBaseMenuItem', 'translateVoice',
                    'startRecord', 'stopRecord', 'onRecordEnd',
                    'playVoice', 'pauseVoice', 'stopVoice',
                    'uploadVoice', 'downloadVoice', 'chooseImage',
                    'previewImage', 'uploadImage', 'downloadImage',
                    'getNetworkType', 'openLocation', 'getLocation',
                    'hideOptionMenu', 'showOptionMenu', 'closeWindow',
                    'scanQRCode', 'chooseWXPay', 'openProductSpecificView',
                    'addCard', 'chooseCard',
                    'openCard']
            });
        }
    });
}