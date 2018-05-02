package com.gzzm.oa.address;

import com.gzzm.platform.barcode.BarCodePage;
import com.gzzm.platform.barcode.BarCodeUtils;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.io.Base64;
import net.cyan.commons.util.io.CacheData;
import net.cyan.commons.util.io.Mime;
import net.cyan.nest.annotation.Inject;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 前端展示员工信息
 *
 * @author ljb
 * @date 2017/2/17 0017
 */
@Service
public class CardShowServce {

    @Inject
    private CardShowConfig config;

    private final static String SHOWURL = "/oa/address/addressCardInfo";

    private final static String DEFAULTIMGURL = "im/images/head.gif";

    @Inject
    private BarCodePage barCodePage;

    @Inject
    private CardShowDao dao;

    private AddressCard addressCard;

    private InputFile headInputFile;

    private String code;

    public CardShowServce() {
    }

    public CardShowConfig getConfig() {
        return config;
    }

    public void setConfig(CardShowConfig config) {
        this.config = config;
    }

    public AddressCard getAddressCard() {
        return addressCard;
    }

    public void setAddressCard(AddressCard addressCard) {
        this.addressCard = addressCard;
    }

    public InputFile getHeadInputFile() {
        return headInputFile;
    }

    public void setHeadInputFile(InputFile headInputFile) {
        this.headInputFile = headInputFile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取二位码
     *
     * @param empNo
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/address/getBarCodeById/{$0}")
    public InputFile getBarCodeById(String empNo) throws Exception {
        String empNoCode = idEncoderByempNo(empNo);
        if (empNoCode == null) {
            return null;
        }
        byte[] qr = BarCodeUtils.qr(config.getServiceUrl() + SHOWURL + "/" + URLEncoder.encode(empNoCode, "UTF-8"), 200);
        return new InputFile(qr, "qr.png", Mime.PNG);
    }

    /**
     * 导出制定范围的二位码
     *
     * @param index
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/address/getBarCodes/{$0}")
    public InputFile getBarCodes(Integer index) throws Exception {

        if (index == null || index > 999999)
            throw new NoErrorException("不能为空或者超过6位编码！");

        CacheData cache = new CacheData();
        //压缩对象
        CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);
        InputFile inputFile;
        try {

            if (index != null && index > 0)
                for (int i = 1; i <= index; i++) {
                    inputFile = getBarCodeById(toSixInt(i));
                    zip.addFile("二维码图片包/员工编码：" + toSixInt(i) + ".png", inputFile.getInputStream());
                }
        } catch (Exception ex) {
            Tools.log(ex);
        }
        if (zip != null)
            zip.close();
        return new InputFile(cache.getBytes(), toSixInt(index) + "以内员工编码二维码图片包.zip");
    }


    /**
     * 6位补0
     *
     * @param i
     * @return
     */
    private String toSixInt(Integer i) {
        if (i != null) {
            switch (String.valueOf(i).length()) {
                case 1:
                    return "00000" + i;
                case 2:
                    return "0000" + i;
                case 3:
                    return "000" + i;
                case 4:
                    return "00" + i;
                case 5:
                    return "0" + i;
                case 6:
                    return "" + i;
            }
        }
        return null;
    }

    /**
     * 获取个人信息
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Service(url = SHOWURL + "/{$0}", method = HttpMethod.all)
    public String getAddressCardInfo(String code) throws Exception {
        this.code = code;
        String empNoDeCode = idDecoderByempNo(code);
        addressCard = dao.getAddressCardByEmpNo("empNo", empNoDeCode);
        return "/oa/address/empbarcode.ptl";
    }

    /**
     * 获取头像
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/address/{$0}/getHeadInputFile")
    public InputFile getHeadImg(String code) throws Exception {
        AddressCard addressCard = dao.getAddressCardByEmpNo("empNo", idDecoderByempNo(code));
        if (addressCard != null && addressCard.getHeadImg() != null && addressCard.getHeadImg().length > 0)
            return new InputFile(addressCard.getHeadImg(), "headImg.png");
        return new InputFile(new File(DEFAULTIMGURL), "headImg.png");
    }

    /**
     * 数据标准：
     * N:姓;名
     * FN:姓名NICKNAME:nickName
     * ORG:公司;部门
     * TITLE:职位
     * NOTE;ENCODING=QUOTED-PRINTABLE:=C6=E4=CB=FB
     * TEL;WORK;VOICE:电话1
     * TEL;WORK;VOICE:电话2
     * TEL;HOME;VOICE:电话1
     * TEL;HOME;VOICE:电话2
     * TEL;CELL;VOICE:13590342862
     * TEL;PAGER;VOICE:0755
     * TEL;WORK;FAX:传真
     * TEL;HOME;FAX:传真
     * ADR;WORK:;;单位地址;深圳;广东;433000;国家
     * LABEL;WORK;ENCODING=QUOTED-PRINTABLE:=B5=A5=CE=BB=B5=D8=D6=B7
     * =C9=EE=DB=DA
     * =B9=E3=B6=AB
     * 433000
     * =B9=FA=BC=D2
     * ADR;HOME;POSTAL;PARCEL:;;街道地址;深圳;广东;433330;中国
     * LABEL;HOME;ENCODING=QUOTED-PRINTABLE:=BD=D6=B5=C0=B5=D8=D6=B7
     * =C9=EE=DB=DA
     * =B9=E3=B6=AB
     * 433330
     * =D6=D0=B9=FA
     * URL:网址
     * URL:单位主页
     * EMAIL;PREF;INTERNET:邮箱地址
     * X-QQ:38394246
     * X-ICQ:icq
     * X-WAB-GENDER:2
     * REV:20060220T180305Z
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/address/{$0}/getCardVcf")
    public InputFile getCardVcf(String code) throws Exception {
        AddressCard addressCard = dao.getAddressCardByEmpNo("empNo", idDecoderByempNo(code));
        if (addressCard != null) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("N;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE", addressCard.getCardName());
            data.put("TITLE;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE", addressCard.getAttributes().get("duty"));
            data.put("TEL;CELL", addressCard.getAttributes().get("mobilePhone"));
            data.put("TEL;WORK", addressCard.getAttributes().get("workPhone"));
            data.put("TEL;HOME", addressCard.getAttributes().get("mobilePhone"));
            data.put("EMAIL", addressCard.getAttributes().get("email"));
            return FileIoUitls.getVRF(data,addressCard.getCardName());
        }
        return null;
    }

    @Service(url = "/oa/address/toBaiduMap")
    public String toBaiduMap() throws Exception {
        return "/oa/address/baidumap.ptl";
    }

    /**
     * 加密方法
     *
     * @param str
     * @return
     * @throws Exception
     */
    private String idEncoderByempNo(String str) throws Exception {
        if (!"".equals(str) && str != null)
            return Base64.byteArrayToBase64(str.getBytes());
        return null;
    }

    /**
     * 解密
     *
     * @param str
     * @return
     * @throws Exception
     */
    private String idDecoderByempNo(String str) throws Exception {
        if (!"".equals(str) && str != null) {
            return new String(Base64.base64ToByteArray(str));
        }
        return null;
    }

}
