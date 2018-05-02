package com.gzzm.platform.update;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.nest.annotation.Inject;

import java.io.File;

/**
 * 系统自动更新
 *
 * @author camel
 * @date 13-5-22
 */
@Service
public class UpdatePage
{
    @Inject
    private OrganDao organDao;

    @Inject
    private UpdateConfig config;

    /**
     * 要更新的更新包
     */
    private InputFile updatePack;

    /**
     * 管理员用户名
     */
    private String loginName;

    /**
     * 管理员密码
     */
    private String password;

    public UpdatePage()
    {
    }

    public InputFile getUpdatePack()
    {
        return updatePack;
    }

    public void setUpdatePack(InputFile updatePack)
    {
        this.updatePack = updatePack;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Service(url = "/platform/update", method = HttpMethod.post)
    @PlainText
    public String update() throws Exception
    {
        User user = organDao.getUserByLoginName(loginName, UserType.in);

        if (user == null)
        {
            String message = Tools.getMessage("login.no_user");
            Tools.log(message);
            return message;
        }

        String realPassword = user.getPassword();

        if (!PasswordUtils.checkPassword(password, realPassword, user.getUserId()))
        {
            String message = Tools.getMessage("login.password_error");
            Tools.log(message);
            return message;
        }

        if (user.getAdminUser() == null || !user.getAdminUser())
        {
            String message = Tools.getMessage("login.password_error");
            Tools.log(message);
            return message;
        }

        CompressUtils.decompress("zip", updatePack.getInputStream(), config.getTempPath());

        Tools.run(new Runnable()
        {
            public void run()
            {
                synchronized (this)
                {
                    try
                    {
                        wait(1000 * 30);
                    }
                    catch (InterruptedException ex)
                    {
                        //
                    }
                }

                File file = new File(Tools.getConfigPath("update.bat"));
                if (file.exists())
                {
                    try
                    {
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getAbsolutePath());
                    }
                    catch (Throwable ex)
                    {
                        Tools.log(ex);
                    }
                }
            }
        });

        return "";
    }
}
