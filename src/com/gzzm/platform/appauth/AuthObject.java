package com.gzzm.platform.appauth;

/**
 * @author camel
 * @date 2016/5/4
 */
public class AuthObject
{
    protected AuthType type;

    protected Integer objectId;

    public AuthObject()
    {
    }

    public AuthObject(AuthType type, Integer objectId)
    {
        this.type = type;
        this.objectId = objectId;
    }

    public AuthType getType()
    {
        return type;
    }

    public void setType(AuthType type)
    {
        this.type = type;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Integer objectId)
    {
        this.objectId = objectId;
    }

    public static String toString(AuthObject authObject)
    {
        return authObject.getType() + "_" + authObject.getObjectId();
    }

    public static AuthObject parse(String s)
    {
        String[] ss = s.split("_");

        AuthType type = AuthType.valueOf(ss[0]);
        Integer id = Integer.valueOf(ss[1]);

        return new AuthObject(type, id);
    }
}
