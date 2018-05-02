package com.gzzm.oa.version;

/**
 * 发布方式
 * @author zy
 * @date 2017/1/19 17:21
 */
public enum ReleaseMode
{
    /**
     * 系统自动识别
     */
    SYSTEM,
    /**
     * 人工手动添加
     */
    MANUAL;

    @Override
    public String toString()
    {
        switch (this)
        {
            case SYSTEM:
                return "自动";
            case MANUAL:
                return "手动";
        }

        return "自动";
    }

}
