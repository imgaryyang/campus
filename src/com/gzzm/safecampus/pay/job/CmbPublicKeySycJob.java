package com.gzzm.safecampus.pay.job;


import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.pay.cmb.CmbConfig;
import com.gzzm.safecampus.pay.cmb.PayUtil;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.json.JsonObject;
import net.cyan.commons.util.json.JsonParser;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 获取招行公钥-由定时任务完成
 *
 * @author yuanfang
 * @date 18-02-01 14:04
 */
public class CmbPublicKeySycJob implements Runnable
{
    @Inject
    private static Provider<CmbConfig> cmbConfigProvider;

    public CmbPublicKeySycJob()
    {
    }

    @Override
    public void run()
    {
        Tools.debug("running get public key.. ");
        try
        {
            Map<String, String> reqDataMap = new HashMap<>();
            reqDataMap.put("dateTime", PayUtil.getNowTime());
            reqDataMap.put("txCode", "FBPK");
            reqDataMap.put("branchNo", cmbConfigProvider.get().getBranchNo());
            reqDataMap.put("merchantNo", cmbConfigProvider.get().getMerchantNo());
            Tools.debug("branchNo: " + cmbConfigProvider.get().getBranchNo());
            String json = PayUtil.buildParam(reqDataMap, cmbConfigProvider.get().getScrectKey());
            String callBack = PayUtil.postSimpleData(json, cmbConfigProvider.get().getPublicKeyUrl());
            Tools.debug("CallBack of get public key: " + callBack);
            String fbPubKey;

            JsonObject jsonObject = new JsonParser(callBack).parse();
            String noticeData = jsonObject.get("rspData").toString();

            JsonObject rspData = new JsonParser(noticeData).parse();
            String rspCode = (String) rspData.get("rspCode");
            if ("SUC0000".equals(rspCode))
            {
                fbPubKey = (String) rspData.get("fbPubKey");
                PayUtil.publicKey = fbPubKey;
                Tools.log("查询公钥成功：" + fbPubKey);
            } else
            {
                String errMsg = (String) rspData.get("rspMsg");
                Tools.log("查询公钥失败：" + errMsg);
            }

        } catch (Exception e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
    }
}

