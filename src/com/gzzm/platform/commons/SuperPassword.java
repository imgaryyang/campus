package com.gzzm.platform.commons;

import net.cyan.commons.util.*;

import java.math.BigInteger;

/**
 * @author camel
 * @date 2017/6/19
 */
public class SuperPassword
{
    private static final String MODULUS_BASE64 =
            "AJaJQZm/WzVGAjN1vtTaWFaw0aHi1/XvBzaK8uQUhRNyACWfa6ieqHOE8SeUg2T/dIsFSzy6u2h481lnikDk3zAbe7DifbTCZl86lpFvUtq2zQPqyjGUejz0AiWMbi21/rlHGH9TlIThkbSy5XJMYJQz8aXeTtDZT5nJgczpYIlO2YYYkEJyxn8Ri71f8z7xm6yLT5gBP3xzDlhtV+goBevMMelyPa1vUcNUE5k90XvwrRhdG4Emb0/Buf74409zz4ge2hYhxRUbmdFZTGg6oLu4Mcsu+z71pye0JkJ+BptmC8O943GWOxZtTo6nCIkaZOHwp9PqIiz4GYiMi4wT0J8=";

    public static final BigInteger MODULUS = new BigInteger(CommonUtils.base64ToByteArray(MODULUS_BASE64));

    private static BigInteger x;

    private static BigInteger m;

    private static final String DEFAULTPASSWORD =
            "WagXMQc/XQkmii4we/DmpkZ6+FITtAnMXH5tQR7SRHIqlNP0PV/+JL9qsSgqIHV8FldFmfg6yVJXV57QRyO2dqQXSwxfZsLsTPMTB1BJfR1UQ3+r/S1RSHXdJxPELDS2cs8Frt/0rV9P4DC+Vv93dOhBMxDrGtiuEAV7cwC8G/aS6z4RFNsujXxIkFFR17edl0Llc9lgzv95gKdGVjGmzMd+jzzCqJTZkmvIP3QIuhPDF4vy/N0cCeLk20UfumZYuXBi9Dm2ohQ33x8Jj/VK4L49isc12VVpkHp2JU7uP0pF0Ljbofq2MBTr4dIqTDHMCsPIj0wqyNout+RdX1k47A==";

    public SuperPassword()
    {
    }

    private static synchronized BigInteger getX() throws Exception
    {
        if (x == null)
        {
            String s = null;
            try
            {
                s = Tools.getConfig("SUPERPASSWORD");
            }
            catch (Throwable ex)
            {
                //
            }
            if (StringUtils.isEmpty(s))
                s = DEFAULTPASSWORD;

            x = new BigInteger(CommonUtils.base64ToByteArray(s));

            try
            {
                s = Tools.getConfig("SUPERPASSWORD_MOD");

                if (!StringUtils.isEmpty(s))
                    m = new BigInteger(s);
            }
            catch (Throwable ex)
            {
                //
            }
        }

        return x;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static boolean check(String password) throws Exception
    {
        if (password.length() < 10)
            return false;

        BigInteger x = getX();

        BigInteger y = new BigInteger(password.getBytes("UTF-8")).abs();
        if (m != null)
            y = y.divide(m);

        BigInteger y1 = x.modPow(y, MODULUS);

        return y1.equals(y);
    }
}
