package com.gzzm.ods.exchange;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Map;

/**
 * 公文收文
 *
 * @author camel
 * @date 2010-9-9
 */
@Entity(table = "ODRECEIVE", keys = "receiveId")
public class Receive
{
    /**
     * 收文ID
     */
    @ColumnDescription(type = "number(12)")
    private Long receiveId;

    @Cascade
    @Lazy(false)
    private ReceiveBase receiveBase;

    //收文的更多信息

    /**
     * 收文类型编号
     */
    private Integer receiveTypeId;

    private com.gzzm.ods.receivetype.ReceiveType receiveType;

    /**
     * 收文编号
     */
    @ColumnDescription(type = "varchar(250)")
    private String serial;

    /**
     * 承办人
     */
    private Integer dealer;

    @ToOne("DEALER")
    private User dealUser;

    /**
     * 承办科室
     */
    private Integer dealDeptId;

    @ToOne("DEALDEPTID")
    private Dept dealDept;

    /**
     * 当此公文为外部接口对接时，保留第三方的id
     */
    @Index
    @ColumnDescription(type = "varchar(50)")
    private String sourceId;

    /**
     * 当此公文为外部接口对接时，保留第三方的发送部门ID
     */
    @Index
    @ColumnDescription(type = "varchar(50)")
    private String sender;

    /**
     * 当需要将此公文同步到第三方系统时，记录同步状态，false为未同步，true为已同步
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean syned;

    /**
     * 当需要将此公文的流程处理结束状态同步到第三方系统时，记录流程处理结束的同步状态，false为未同步，true为已同步
     */
    private Boolean syned1;

    /**
     * 此收文转发自哪个收文，如果不是转发的，则此字段为空
     */
    @ColumnDescription(type = "number(12)")
    private Long sourceReceiveId;

    /**
     * 附加属性
     */
    @NotSerialized
    @ValueMap(table = "ODRECEIVEATTRIBUTEVALUE", keyColumn = "ATTRIBUTENAME", valueColumn = "ATTRIBUTEVALUE",
            clearForUpdate = false)
    private Map<String, String> attributes;

    /**
     * 此记录在转发单位流程中的stepId，每个被转发的公文会在转发单位的流程中生成一个step，以方便转发单位跟踪
     */
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    public Receive()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public ReceiveBase getReceiveBase()
    {
        return receiveBase;
    }

    public void setReceiveBase(ReceiveBase receiveBase)
    {
        this.receiveBase = receiveBase;
    }

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public com.gzzm.ods.receivetype.ReceiveType getReceiveType()
    {
        return receiveType;
    }

    public void setReceiveType(com.gzzm.ods.receivetype.ReceiveType receiveType)
    {
        this.receiveType = receiveType;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public Integer getDealer()
    {
        return dealer;
    }

    public void setDealer(Integer dealer)
    {
        this.dealer = dealer;
    }

    public User getDealUser()
    {
        return dealUser;
    }

    public void setDealUser(User dealUser)
    {
        this.dealUser = dealUser;
    }

    public Integer getDealDeptId()
    {
        return dealDeptId;
    }

    public void setDealDeptId(Integer dealDeptId)
    {
        this.dealDeptId = dealDeptId;
    }

    public Dept getDealDept()
    {
        return dealDept;
    }

    public void setDealDept(Dept dealDept)
    {
        this.dealDept = dealDept;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public Boolean getSyned()
    {
        return syned;
    }

    public void setSyned(Boolean syned)
    {
        this.syned = syned;
    }

    public Boolean getSyned1()
    {
        return syned1;
    }

    public void setSyned1(Boolean syned1)
    {
        this.syned1 = syned1;
    }

    public Long getSourceReceiveId()
    {
        return sourceReceiveId;
    }

    public void setSourceReceiveId(Long sourceReceiveId)
    {
        this.sourceReceiveId = sourceReceiveId;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Receive))
            return false;

        Receive receive = (Receive) o;

        return receiveId.equals(receive.receiveId);

    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
