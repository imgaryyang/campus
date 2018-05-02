package com.gzzm.safecampus.device.card.common;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.device.card.dto.LoginModel;
import net.cyan.commons.security.SecurityUtils;
import net.cyan.commons.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/20
 */
public class BeanEncodeUtils
{
    public BeanEncodeUtils()
    {
    }

    /**
     * 生成token
     *
     * @param reqBean
     * @param key
     * @return
     * @throws IllegalAccessException
     */
    public static String createToken(Object reqBean, String key) throws IllegalAccessException
    {
        if (StringUtils.isEmpty(key)) key = Tools.getMessage("device.card.key");
        Field[] classFiled = ReflectionUtils.getClassFiled(reqBean.getClass());
        StringBuilder token = new StringBuilder();
        String timeStamp = "";
        for (Field field : classFiled)
        {
            Object temp = ReflectionUtils.getValue(reqBean, field);
            if (temp == null) continue;
            //如果是集合类型的字段
            if (Collection.class.isAssignableFrom(field.getType()))
            {
                List myList = (List) temp;
                Iterator iterator = myList.iterator();
                while (iterator.hasNext())
                {
                    Object value = iterator.next();
                    if (value != null)
                    {
                        Class<?> aClass = value.getClass();
                        Field[] fields = ReflectionUtils.getClassFiled(aClass);
                        for (Field child : fields)
                        {
                            Object childTemp = ReflectionUtils.getValue(value, child);
                            if(childTemp!=null) token.append(childTemp);
                        }
                    }
                }
                continue;
            }
            String keyWord = temp.toString();
            if (StringUtils.isEmpty(keyWord)) continue;
            if (field.getName().equals("Token"))
            {
                continue;
            }
            if (field.getName().equals("TimeStamp"))
            {
                token.append(keyWord);
                timeStamp = keyWord;
                continue;
            }
            token.append(keyWord);

        }
        token.append(SecurityUtils.md5(key).toLowerCase()).append(timeStamp);
        return SecurityUtils.md5(token.toString()).toLowerCase();
    }

    /**
     * 生成token
     *
     * @param reqBean
     * @return
     * @throws IllegalAccessException
     */
    public static String createToken(Object reqBean) throws IllegalAccessException
    {
        return createToken(reqBean, null);
    }

    public static boolean checkToken(Object object) throws NoSuchFieldException, IllegalAccessException
    {
        Object token = ReflectionUtils.getValue(object, "Token");
        String token1 = createToken(object);
        return token1.equals(token);
    }

    public static <T> T initToken(T object) throws NoSuchFieldException, IllegalAccessException
    {
        ReflectionUtils.setValue(object, "Token", createToken(object));
        return object;
    }


    /**
     * 加密
     *
     * @param map
     * @param key
     * @return
     */
    public static String encodeBean(Map<String, String> map, String key)
    {

        return null;
    }

    public static void main(String[] ages) throws Exception
    {

        String xml =
                "<ERRECORD><SequenceId>2</SequenceId><ERPCode>100001</ERPCode><NetMark>DC4A3E6CE17E</NetMark><Token>e2edfd838d84ceee6c397eea0d20d574</Token><TimeStamp>20180316162618</TimeStamp><opuser>admin</opuser><PassWord>123456</PassWord><MachID>0020161212848468</MachID></ERRECORD>";
        LoginModel.ReqModel bean =
                (LoginModel.ReqModel) XMLUtils.readXML(xml.replaceAll("op_user", "opuser"), LoginModel.ReqModel.class);
        System.out.println(createToken(bean, "728F1E178FF4924BE4DA"));
        System.out.println("821479850115e5f611c2e1ab3a15f970");

    }

}
