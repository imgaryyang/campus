package com.gzzm.platform.commons;

import net.cyan.commons.util.*;
import net.cyan.commons.util.io.Base64_0;
import net.cyan.commons.util.json.*;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

/**
 * 生成临时密钥，对ID进行编码，并将密钥保存在session中
 *
 * @author camel
 * @date 13-8-28
 */
public final class IDEncoder
{
    public static class Timeout extends RuntimeException
    {
        private static final long serialVersionUID = -271349100983077777L;

        public Timeout()
        {
        }
    }

    private static final Base64_0 BASE64 = new Base64_0('_', '-', '$');

    public static class Key implements Serializable
    {
        private static final long serialVersionUID = -1666008784264591331L;

        private int p;

        private int q;

        private int m;

        private String v;

        public Key(int p, int q, int m, String v)
        {
            this.p = p;
            this.q = q;
            this.m = m;
            this.v = v;
        }

        public Key()
        {
        }

        public int getP()
        {
            return p;
        }

        public int getQ()
        {
            return q;
        }

        public int getM()
        {
            return m;
        }

        public String getV()
        {
            return v;
        }

        public void setP(int p)
        {
            this.p = p;
        }

        public void setQ(int q)
        {
            this.q = q;
        }

        public void setM(int m)
        {
            this.m = m;
        }

        public void setV(String v)
        {
            this.v = v;
        }
    }

    public static final String KEY_CONFIG_NAME = "id_encode_key";

    private static Key key;

    private IDEncoder()
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    private synchronized static Key getKey(boolean gen)
    {
        if (key == null)
        {
            try
            {
                String s = Tools.getConfig(KEY_CONFIG_NAME);

                if (!StringUtils.isEmpty(s))
                {
                    key = new JsonParser(s).parse(Key.class);
                }
            }
            catch (Exception ex)
            {
                //加载key失败，不影响系统运行，重新生成一个key
                Tools.log(ex);
            }

            if (key == null)
            {
                key = createKey();

                try
                {
                    Tools.setConfig(KEY_CONFIG_NAME, new JsonSerializer().serialize(key).toString());
                }
                catch (Exception ex)
                {
                    //设置key失败，不影响系统运行
                    Tools.log(ex);
                }
            }
        }

        return key;
    }

    public static Key createKey()
    {
        Random random = new Random(System.currentTimeMillis());
        int p1 = random.nextInt(30);
        if (p1 < 3)
            p1 = 3;
        int p2 = 128 / p1;
        int mod = p1 * p2 - 1;

        String v = Integer.toString(random.nextInt(999));
        v = StringUtils.leftPad(v, 3, '0');

        return new Key(p1, p2, mod, v);
    }

    public static String encode(long l)
    {
        return encode(l, false);
    }

    public static String encode(long l, boolean time)
    {
        Key key = getKey(true);

        if (key == null)
            return null;

        return encode(l, key, time);
    }

    public static long decode(String s) throws Exception
    {
        Key key = getKey(false);

        if (key == null)
            throw new NoErrorException("illegal argument");

        return decode(s, key);
    }

    public static String encode(long l, Key key, boolean time)
    {
        Random random = null;

        String s = Long.toString(l);

        StringBuilder buffer = new StringBuilder(18);
        buffer.append(s);

        while (buffer.length() < 18)
        {
            if (random == null)
                random = new Random(System.currentTimeMillis());

            int r = random.nextInt(40);

            if (r <= 20)
            {
                buffer.insert(0, (char) ('A' + r));
            }
            else
            {
                buffer.append((char) ('A' + r - 20));
            }
        }

        if (time)
        {
            buffer.append(DateUtils.toString(new Date(), "yyMMddHHmm"));
        }

        buffer.append(key.v);

        s = buffer.toString();

        byte[] bs = s.getBytes();

        for (int i = 0; i < bs.length; i++)
        {
            byte b = bs[i];

            b = (byte) (b * key.p % key.m);

            bs[i] = b;
        }

        return BASE64.byteArrayToBase64(bs);
    }

    public static long decode(String s, Key key)
    {
        String base64 = s;

        byte[] bs = BASE64.base64ToByteArray(s);

        for (int i = 0; i < bs.length; i++)
        {
            byte b = bs[i];

            b = (byte) (b * key.q % key.m);

            bs[i] = b;
        }

        s = new String(bs);

        if (s.length() < 3)
            throw new IllegalArgumentException();

        String v = s.substring(s.length() - 3, s.length());
        if (!v.equals(key.v))
        {
            throw new IllegalArgumentException("base64=" + base64 + " , v=" + key.v +
                    " , m=" + key.m + " , p=" + key.p + " , q=" + key.q);
        }

        s = s.substring(0, s.length() - 3);

        if (s.length() >= 28)
        {
            String timeString = s.substring(18, 28);
            Date time;
            try
            {
                time = DateUtils.getDateFormat("yyMMddHHmm").parse(timeString);
            }
            catch (ParseException ex)
            {
                throw new IllegalArgumentException("time=" + timeString + " , base64=" + base64 + " , v=" + key.v +
                        " , m=" + key.m + " , p=" + key.p + " , q=" + key.q);
            }

            long l = System.currentTimeMillis() - time.getTime();
            if (l > 1000 * 60 * 5 || l < -1000 * 60 * 5)
            {
                throw new Timeout();
            }

            s = s.substring(0, 18);
        }

        int start = -1;
        int end = -1;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (start == -1)
            {
                if (c >= '0' && c <= '9')
                    start = i;
            }
            else if (c < '0' || c > '9')
            {
                end = i;
                break;
            }
        }

        if (start > 0 || end > 0)
        {
            if (end == -1)
                end = s.length();

            s = s.substring(start, end);
        }

        return Long.parseLong(s);
    }
}
