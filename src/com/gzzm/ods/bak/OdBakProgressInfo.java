package com.gzzm.ods.bak;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.ProgressInfo;

import java.util.Date;

/**
 * @author camel
 * @date 13-9-27
 */
public class OdBakProgressInfo implements OdBakLisenter, ProgressInfo
{
    private static final long serialVersionUID = 1524745694898709379L;

    /**
     * 压缩包的名称
     */
    private String bakName;

    /**
     * 总的文件数
     */
    private int count;

    /**
     * 当前在压缩的文件的标题
     */
    private String title;

    /**
     * 当前在压缩的文件的时间
     */
    private Date time;

    /**
     * 当前在压缩的文件是第几个文件
     */
    private int index;

    public OdBakProgressInfo()
    {
    }

    public void init(String bakName, int count)
    {
        this.bakName = bakName;
        this.count = count;
    }

    public void add(String title, Date time)
    {
        this.title = title;
        this.time = time;

        index++;
    }

    public String getProgressName()
    {
        return Tools.getMessage("ods.bak.progressname", new Object[]{bakName});
    }

    public float getPercentage()
    {
        if (count > 0)
            return (float) index / count;
        else
            return 0;
    }

    public String getDescription()
    {
        return Tools.getMessage("ods.bak.description", title, time, index, count);
    }
}
