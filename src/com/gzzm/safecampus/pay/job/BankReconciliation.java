package com.gzzm.safecampus.pay.job;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.account.Merchant;
import com.gzzm.safecampus.pay.cmb.BankBusiness;
import com.gzzm.safecampus.pay.cmb.PayDao;
import com.gzzm.safecampus.pay.cmb.PayUtil;
import com.gzzm.safecampus.pay.cmb.QueryOrder;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.json.JsonObject;
import net.cyan.commons.util.json.JsonParser;
import net.cyan.nest.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 银行对账
 *
 * @author yuanfang
 * @date 18-04-12 18:26
 */
public class BankReconciliation implements Runnable
{

    @Inject
    private static Provider<PayDao> payDaoProvider;

    QueryOrder queryOrder = new QueryOrder();

    /**
     * 银行回传数据集合
     */
    List<Map<String, String>> resultMapList = new ArrayList<>();

    public BankReconciliation()
    {
    }

    @Override
    public void run()
    {
        try
        {
            SaveReconcileToday();
        } catch (Exception e)
        {
            Tools.debug(PayUtil.getTrace(e));
            Tools.log("核对错误");
        }
    }

    /**
     * 对当天所有商户进行对账并保存
     *
     * @return 结果
     * @throws Exception 数据库异常
     */
    public List<BankBusiness> SaveReconcileToday() throws Exception
    {
        List<Merchant> merchantList = payDaoProvider.get().getAllEntities(Merchant.class);
        //查询当日银行流水
        String date = PayUtil.getNowDate();
        return reconcile(merchantList.subList(0,1), date, date, true);
    }

    /**
     * 对商户集合进行对账
     *
     * @param merchantList 商户集合
     * @param beginDate    开始时间
     * @param endDate      结束时间
     * @param flag         是否持久化结果
     * @return 结果
     * @throws Exception 数据库异常
     */
    public List<BankBusiness> reconcile(List<Merchant> merchantList, String beginDate, String endDate, Boolean flag) throws Exception
    {
        List<BankBusiness> businesses = new ArrayList<>();
        for (Merchant merchant : merchantList)
        {
            //TODO 更换测试商户
            if (merchant.getSecretKey() != null && ("666666aaaaAAAAAA").equals(merchant.getSecretKey()))
            {
                Tools.debug("queryMerchantID: "+merchant.getMerchantId());
                queryAllOrder(merchant, beginDate, endDate, null);
                for (Map map1 : resultMapList)
                {
                    BankBusiness business = new BankBusiness();
                    setFieldValue(business, map1);
                    businesses.add(business);
                }
                if (flag)
                {
                    payDaoProvider.get().saveEntities(businesses);
                    Tools.log("已保存对账信息...");
                }
                //TODO
                //break;
            }
            //TODO 支付核对
        }
        return businesses;
    }

    /**
     * 查询某天账单
     *
     * @param merchant     商户
     * @param beginDate    开始时间
     * @param endDate      结束时间
     * @param nextKeyValue 续传值
     */
    public void queryAllOrder(Merchant merchant, String beginDate, String endDate, String nextKeyValue)
    {
        String result = queryOrder.queryDate(merchant, beginDate, endDate, nextKeyValue);
        String rspMsg = "";
        String rspCode = "";
        try
        {
            JsonObject jsonObject = new JsonParser(result).parse();
            String rspData = jsonObject.get("rspData").toString();
            JsonObject rspDataObject = new JsonParser(rspData).parse();
            rspCode = (String) rspDataObject.get("rspCode");
            Tools.debug("queryAllOrder: "+rspCode +"/"+ rspMsg);
            if ("SUC0000".equals(rspCode))
            {
                String dataCount = (String) rspDataObject.get("dataCount");
                if (!"0".equals(dataCount))
                {
                    String dataList = (String) rspDataObject.get("dataList");
                    String[] list = dataList.split("\r\n");
                    //头部
                    String[] name = list[0].split(",`");
                    for (int i = 1; i < list.length; i++)
                    {
                        if (list[i].length() > 2)
                        {
                            Map<String, String> map = new HashMap();
                            String[] data = list[i].split(",`");
                            for (int j = 0; j < data.length; j++)
                            {
                                map.put(name[j], data[j]);
                                // Tools.debug(name[j] + " : " + data[j]);
                            }
                            resultMapList.add(map);
                        }
                    }
                }
                String hasNext = (String) rspDataObject.get("hasNext");
                if ("Y".equals(hasNext))
                {
                    nextKeyValue = (String) rspDataObject.get("nextKeyValue");
                    queryAllOrder(merchant, beginDate, endDate, nextKeyValue);
                }
            } else
            {
                rspMsg = (String) rspDataObject.get("rspMsg");
            }
        } catch (Exception e)
        {
            Tools.debug("账单查询错误：" + rspCode+rspMsg);
            Tools.debug(PayUtil.getTrace(e));
        }
    }

    /**
     * set属性的值到Bean
     */
    public static void setFieldValue(Object bean, Map<String, String> valMap)
    {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields)
        {
            try
            {
                String fieldSetName = parSetName(field.getName());
                if (!checkSetMet(methods, fieldSetName))
                {
                    continue;
                }
                Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
                String fieldKeyName = field.getName();
                String value = valMap.get(fieldKeyName);
                if (null != value && !"".equals(value))
                {
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equals(fieldType))
                    {
                        fieldSetMet.invoke(bean, value);
                    }
                }
            } catch (Exception e)
            {
                Tools.debug("error");
            }
        }
    }


    /**
     * 判断是否存在某属性的 set方法
     */
    private static boolean checkSetMet(Method[] methods, String fieldSetMet)
    {
        for (Method met : methods)
        {
            if (fieldSetMet.equals(met.getName()))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * 拼接在某属性的 set方法
     */
    private static String parSetName(String fieldName)
    {
        if (null == fieldName || "".equals(fieldName))
        {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

}
