package com.gzzm.platform.login;

import com.gzzm.platform.commons.IDEncoder;
import net.cyan.commons.cache.Cache;
import net.cyan.nest.annotation.*;

import java.util.Random;

/**
 * @author camel
 * @date 2015/8/24
 */
@Injectable(singleton = true)
public class UserCheckService
{
    private static final Random RANDOM = new Random();

    @Inject("userCheckNumber")
    private Cache<String, Long> checkNumberCache;

    public UserCheckService()
    {
    }

    private long getCheckNumber0(Integer userId)
    {
        String key = userId.toString();
        Long checkNumber = checkNumberCache.getCache(key);
        if (checkNumber == null)
        {
            long l;
            synchronized (RANDOM)
            {
                l = RANDOM.nextLong();
            }

            if (l < 0)
                l = -l;

            if (l > 888888888888888888L)
                l = l / 10;

            checkNumber = l;

            checkNumberCache.update(key, checkNumber);
        }

        return checkNumber;
    }

    public String getCheckNumber(Integer userId)
    {
        long checkNumber = getCheckNumber0(userId);

        return IDEncoder.encode(checkNumber, true);
    }

    public boolean check(Integer userId, String checkNumber) throws Exception
    {
        long l = IDEncoder.decode(checkNumber);

        return l == getCheckNumber0(userId);
    }
}
