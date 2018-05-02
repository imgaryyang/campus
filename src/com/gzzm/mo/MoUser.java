package com.gzzm.mo;

import com.gzzm.platform.commons.Patterns;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.commons.validate.annotation.*;
import net.cyan.crud.annotation.Unique;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 开动移动办公的用户
 *
 * @author camel
 * @date 2014/5/12
 */
@Entity(table = "MOUSER", keys = "moUserId")
public class MoUser
{
    @Inject
    private static Provider<MoDao> daoProvider;

    @Generatable(length = 9)
    private Integer moUserId;

    @Require
    private Integer userId;

    @NotSerialized
    private User user;

    @Require
    @Unique
    @Index
    @Pattern(Patterns.PHONE)
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    @ColumnDescription(type = "varchar(50)")
    private String password;

    /**
     * 是否有效，true为有效，false为无效
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean valid;

    public MoUser()
    {
    }

    public Integer getMoUserId()
    {
        return moUserId;
    }

    public void setMoUserId(Integer moUserId)
    {
        this.moUserId = moUserId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MoUser))
            return false;

        MoUser moUser = (MoUser) o;

        return userId.equals(moUser.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }

    @AfterUpdate
    public static void afterUpdateUser(User user) throws Exception {
        if (user != null && user.getState() != null && user.getState() == 2) {
            MoDao dao = daoProvider.get();
            List<MoUser> moUsers = dao.getMoUsers(user.getUserId());
            if (moUsers.size() > 0) {
                for (MoUser moUser : moUsers) {
                    dao.deleteMoBind(moUser.getMoUserId());
                    dao.delete(moUser);
                }
            }
        }
    }
}
