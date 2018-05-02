package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.UserInfo;
import net.cyan.commons.util.DateUtils;
import net.cyan.valmiki.flow.*;

/**
 * @author camel
 * @date 11-11-17
 */
public class TrackInfo
{
    private InstanceStruct instance;

    private StepStruct step;

    /**
     * 当前用户
     */
    private UserInfo user;

    public TrackInfo(InstanceStruct instance, Long groupId, UserInfo user)
    {
        this.instance = instance;
        this.user = user;

        String s = groupId.toString();

        for (StepStruct step : instance.getSteps())
        {
            if(step.getGroupId().equals(s))
            {
                this.step = step;
                break;
            }
            else
            {
                //匹配协办步骤
                for (FlowStep flowStep : step.getCopySteps())
                {
                    if(flowStep.getGroupId().equals(s))
                    {
                        try
                        {
                            this.step = new StepStruct(flowStep, null);
                        }
                        catch (WorkFlowException ex)
                        {
                            Tools.wrapException(ex);
                        }
                        break;
                    }
                }

                if(this.step != null)
                    break;

                //匹配传阅步骤
                for (FlowStep flowStep : step.getPassSteps())
                {
                    if(flowStep.getGroupId().equals(s))
                    {
                        try
                        {
                            this.step = new StepStruct(flowStep, null);
                        }
                        catch (WorkFlowException ex)
                        {
                            Tools.wrapException(ex);
                        }
                        break;
                    }
                }

                if(this.step != null)
                    break;
            }
        }
    }

    public InstanceStruct getInstance()
    {
        return instance;
    }

    public StepStruct getStep()
    {
        return step;
    }

    public String getTitle()
    {
        return instance.getInstance().getTitle();
    }

    public UserInfo getUser()
    {
        return user;
    }

    public int getReceiveDays()
    {
        return (int) ((System.currentTimeMillis() - DateUtils.truncate(step.getReceiveTime()).getTime()) / (1000 * 60 *
                60 * 24));
    }
}
