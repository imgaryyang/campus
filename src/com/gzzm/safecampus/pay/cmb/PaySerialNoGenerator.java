package com.gzzm.safecampus.pay.cmb;

import com.gzzm.platform.wordnumber.WordNumber;

import java.util.Date;

/**
 * 一网通支付序列号生成
 * @author yuanfang
 * @date 18-04-09 11:12
 */
public class PaySerialNoGenerator
{
    public PaySerialNoGenerator()
    {
    }

    /**
     * 生成一网通账户协议号（17位）
     * 010 yyMMddHHmm XXXX
     * @return 协议号
     * @throws Exception 系统异常
     */
    public static String getAgrSerialNo() throws Exception
    {
        WordNumber wordNumber = new WordNumber("$date(yyMMddHHmm)$serial(0,4)");
        wordNumber.setTime(new Date());
        wordNumber.setType("payAgrNo");
        wordNumber.setDeptId(1);
        return "010"+wordNumber.getResult();
    }

    /**
     * 生成一网通协议开通流水号（17位）
     *  011 yyMMddHHmm XXXX
     * @return 协议开通流水号
     * @throws Exception 系统异常
     */
    public static String getMerchantSerialNo() throws Exception
    {
        WordNumber wordNumber = new WordNumber("$date(yyMMddHHmm)$serial(0,4)");
        wordNumber.setTime(new Date());

        wordNumber.setType("paySerialNo");
        wordNumber.setDeptId(1);
        return "011"+wordNumber.getResult();
    }

    /**
     * 生成一网通支付订单号（19位）
     *  001 yyMMddHHmmSS XXXX
     * @return 订单号
     * @throws Exception 系统异常
     */
    public static String getOrderNo() throws Exception
    {
        WordNumber wordNumber = new WordNumber("$date(yyMMddHHmmSS)$serial(0,4)");
        wordNumber.setTime(new Date());
        wordNumber.setType("payOrderNo");
        wordNumber.setDeptId(1);
        return "001"+wordNumber.getResult();
    }
}
