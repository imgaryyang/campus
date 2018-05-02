package com.gzzm.ods.exchange;

import com.gzzm.ods.document.*;
import com.gzzm.ods.flow.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.group.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.FlowStep;
import net.cyan.valmiki.form.ComponentData;
import net.cyan.valmiki.form.components.FileListData;

import java.util.*;

/**
 * 公文交换发文服务，包括一些和发文相关的逻辑
 *
 * @author camel
 * @date 11-9-23
 */
public class ExchangeSendService
{
    @Inject
    private static Provider<ReceiptDao> receiptDaoProvider;

    @Inject
    private static Provider<GroupDao> groupDaoProvider;

    @Inject
    private ExchangeSendDao dao;

    @Inject
    private OdFlowDao odFlowDao;

    public ExchangeSendService()
    {
    }

    public ExchangeSendDao getDao()
    {
        return dao;
    }

    /**
     * 发送公文
     *
     * @param document 要发送的公文
     * @throws Exception 发送公文错误，一般由数据库错误引起
     */
    @Transactional
    public void sendDocument(OfficeDocument document) throws Exception
    {
        //获得公文的接收者
        DocumentReceiverList documentReceiverList = document.getReceiverList();
        if (documentReceiverList == null)
            documentReceiverList = dao.getDocumentReceiverList(document.getReceiverListId());

        //记录已经发送的部门，避免重复发送
        Set<Integer> sendedDepts = new HashSet<Integer>();

        //记录已经发送的用户，避免重复发送
        Set<Integer> sendedUsers = new HashSet<Integer>();

        //发给所有的接收者
        List<ReceiverList> receiverLists = documentReceiverList.getReceivers().getReceiverLists();
        if (receiverLists != null)
        {
            for (ReceiverList receiverList : receiverLists)
            {
                sendDocumentTo(document, receiverList.getReceivers(), receiverList.getSendType(), null, sendedDepts,
                        sendedUsers);
            }
        }
    }

    @Transactional
    public List<Long> sendDocumentTo(OfficeDocument document, List<Member> receivers, String sendType,
                                     Long sourceReceiveId) throws Exception
    {
        return sendDocumentTo(document, receivers, sendType, sourceReceiveId, new HashSet<Integer>(),
                new HashSet<Integer>());
    }

    /**
     * 发送公文给某个接收者
     *
     * @param document        公文
     * @param receivers       接收者列表
     * @param sendType        接收者类型，如主送，抄送等
     * @param sourceReceiveId 转发自哪个收文
     * @param sendedDepts     保存已经发送过的部门的id，避免重复发送
     * @param sendedUsers     保存已经发送过的用户的id，避免重复发送
     * @return 收文ID列表
     * @throws Exception 数据库写入数据错误
     */

    private List<Long> sendDocumentTo(OfficeDocument document, List<Member> receivers, String sendType,
                                      Long sourceReceiveId, Set<Integer> sendedDepts, Set<Integer> sendedUsers)
            throws Exception
    {
        GroupDao groupDao = null;

        List<Long> receiveIds = new ArrayList<Long>();

        for (Member member : receivers)
        {
            switch (member.getType())
            {
                case dept:
                {
                    //发送公文给部门
                    Long receiveId =
                            sendDocumentToDept(document, member.getId(), sendType, sourceReceiveId, sendedDepts);

                    if (receiveId != null)
                        receiveIds.add(receiveId);

                    break;
                }
                case deptgroup:

                    //部门组，分别发给部门组中的部门

                    if (groupDao == null)
                        groupDao = groupDaoProvider.get();

                    for (Dept dept : groupDao.getDeptGroup(member.getId()).getDepts())
                    {
                        if (dept.getState() == 0)
                        {
                            Long receiveId =
                                    sendDocumentToDept(document, dept.getDeptId(), sendType, sourceReceiveId,
                                            sendedDepts);

                            if (receiveId != null)
                                receiveIds.add(receiveId);
                        }
                    }

                    break;
                case user:
                {
                    //发送公文给用户

                    Long receiveId = sendDocumentToUser(document, member.getId(), sendType, sendedUsers);

                    if (receiveId != null)
                        receiveIds.add(receiveId);

                    break;
                }
                case usergroup:

                    //发送公文给用户组，分别发送给用户组中的用户

                    if (groupDao == null)
                        groupDao = groupDaoProvider.get();

                    for (User user : groupDao.getUserGroup(member.getId()).getUsers())
                    {
                        Long receiveId = sendDocumentToUser(document, user.getUserId(), sendType, sendedUsers);
                        if (receiveId != null)
                            receiveIds.add(receiveId);
                    }

                    break;

                case station:

                    //发送公文给用户组，分别发送给用户组中的用户

                    if (groupDao == null)
                        groupDao = groupDaoProvider.get();

                    Station station = groupDao.getStation(member.getId());

                    for (User user : groupDao.getUsersOnStation(station.getDeptId(), station.getStationId()))
                    {
                        sendDocumentToUser(document, user.getUserId(), sendType, sendedUsers);
                    }

                    break;
            }
        }

        return receiveIds;
    }

    /**
     * 发送公文给部门
     *
     * @param document 公文
     * @param deptId   部门ID
     * @param sendType 接收者类型，如主送，抄送等
     * @return 收文ID
     * @throws Exception 数据库写入数据错误
     */
    @Transactional
    public Long sendDocumentToDept(OfficeDocument document, Integer deptId, String sendType)
            throws Exception
    {
        return sendDocumentToDept(document, deptId, sendType, null, null);
    }

    private Long sendDocumentToDept(OfficeDocument document, Integer deptId, String sendType, Long sourceReceiveId,
                                    Set<Integer> sendedDepts)
            throws Exception
    {
        if (sendedDepts != null)
        {
            //已经发送给此部门，不再重复发送
            if (sendedDepts.contains(deptId))
                return null;

            sendedDepts.add(deptId);
        }

        //收文基本信息
        ReceiveBase receiveBase = new ReceiveBase();

        receiveBase.setDocumentId(document.getDocumentId());
        receiveBase.setDeptId(deptId);
        receiveBase.setSendTime(new Date());
        receiveBase.setType(ReceiveType.receive);
        receiveBase.setMethod(ReceiveMethod.system);
        receiveBase.setState(ReceiveState.noAccepted);
        receiveBase.setSendType(sendType);
        receiveBase.setSourceDeptId(document.getCreateDeptId());

        dao.add(receiveBase);

        Receive receive = new Receive();
        receive.setReceiveId(receiveBase.getReceiveId());
        receive.setSourceReceiveId(sourceReceiveId);

        dao.add(receive);

        return receiveBase.getReceiveId();
    }

    /**
     * 发送公文给用户
     *
     * @param document 公文
     * @param userId   用户ID
     * @param sendType 接收者类型，如主送，抄送等
     * @return 收文ID
     * @throws Exception 数据库写入数据错误
     */
    @Transactional
    public Long sendDocumentToUser(OfficeDocument document, Integer userId, String sendType) throws Exception
    {
        return sendDocumentToUser(document, userId, sendType, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    private Long sendDocumentToUser(OfficeDocument document, Integer userId, String sendType, Set<Integer> sendedUsers)
            throws Exception
    {
        if (sendedUsers != null)
        {
            //已经发送给此用户，不再重复发送
            if (sendedUsers.contains(userId))
                return null;

            sendedUsers.add(userId);
        }

        Date now = new Date();

        //收文基本信息
        ReceiveBase receiveBase = new ReceiveBase();
        receiveBase.setDocumentId(document.getDocumentId());
        receiveBase.setSendTime(now);
        receiveBase.setType(ReceiveType.copy);
        receiveBase.setMethod(ReceiveMethod.system);
        receiveBase.setState(ReceiveState.noAccepted);
        receiveBase.setReceiver(userId);
        User user = dao.getUser(userId);
        if (user != null)
            receiveBase.setReceiverName(user.getUserName());
        receiveBase.setSendType(sendType);
        receiveBase.setSourceDeptId(document.getCreateDeptId());
        dao.add(receiveBase);

        OdSystemFlowDao systemFlowDao = OdSystemFlowDao.getInstance();

        OdFlowInstance odFlowInstance = odFlowDao.getCopyOdFlowInstanceByDocumentId(document.getDocumentId());
        if (odFlowInstance == null)
        {
            SystemFlowInstance systemFlowInstance = systemFlowDao.newFlowInstance();
            systemFlowInstance.setInstanceId(Long.valueOf(systemFlowDao.createInstanceId()));
            systemFlowInstance.setTitle(document.getTitle());
            systemFlowInstance.setStartTime(now);
            systemFlowInstance.setFlowTag("copy");
            odFlowDao.add(systemFlowInstance);

            odFlowInstance = new OdFlowInstance();
            odFlowInstance.setStartTime(now);
            odFlowInstance.setInstanceId(systemFlowInstance.getInstanceId());
            odFlowInstance.setDocumentId(document.getDocumentId());
            odFlowInstance.setType("copy");
            odFlowInstance.setState(OdFlowInstanceState.closed);
            odFlowDao.add(odFlowInstance);
        }

        Long stepId = Long.valueOf(systemFlowDao.createStepId());
        SystemFlowStep step = systemFlowDao.newFlowStep();
        step.setInstanceId(odFlowInstance.getInstanceId());
        step.setStepId(stepId);
        step.setGroupId(Long.valueOf(systemFlowDao.createStepGroupId()));
        step.setNodeName(sendType);
        step.setPreStepId(0L);
        step.setTopStepId(stepId);
        step.setReceiver(userId.toString());
        step.setState(FlowStep.PASSNOACCEPT);
        step.setReceiveTime(now);
        step.setShowTime(now);
        step.setLastStep(true);
        step.setDealer(userId);
        odFlowDao.add(step);

        Copy copy = new Copy();
        copy.setReceiveId(receiveBase.getReceiveId());
        copy.setStepId(stepId);
        dao.add(copy);

        systemFlowDao.refreshStepQ(odFlowInstance.getInstanceId());

        return receiveBase.getReceiveId();
    }

    public List<Long> reSend(Long documentId, List<Member> receivers) throws Exception
    {
        return sendDocumentTo(dao.getDocument(documentId), receivers, "补发", null);
    }

    @Transactional
    public void reSend(OfficeDocument document, List<Member> receivers) throws Exception
    {
        sendDocumentTo(document, receivers, "补发", null);
    }

    /**
     * 发送一份文给联合发文单位
     *
     * @param documentId  公文ID
     * @param deptIds     要发送的联合发文单位
     * @param type        发送类型，发送给联合发文单位处理还是盖章
     * @param unionDeptId 发起联合发文的部门的ID
     * @return 实际发送的联合发文的收文ID列表
     * @throws Exception 发送异常，一般由数据库错误引起
     */
    @Transactional
    public List<Long> sendUnions(Long documentId, List<Integer> deptIds, ReceiveType type, Integer unionDeptId)
            throws Exception
    {
        if (type != ReceiveType.union && type != ReceiveType.unionseal)
            throw new IllegalArgumentException(type.name());

        List<Long> result = new ArrayList<Long>();

        for (Integer deptId : deptIds)
        {
            Union union = dao.getCurrentUnion(documentId, deptId);

            if (union == null)
            {
                //仅当部门当前没有联合发文在处理时才发送
                ReceiveBase receiveBase = new ReceiveBase();

                receiveBase.setDeptId(deptId);
                receiveBase.setDocumentId(documentId);
                receiveBase.setSendTime(new Date());
                receiveBase.setType(type);
                receiveBase.setMethod(ReceiveMethod.system);
                receiveBase.setState(ReceiveState.noAccepted);
                receiveBase.setSourceDeptId(unionDeptId);

                dao.add(receiveBase);

                union = new Union();
                union.setReceiveId(receiveBase.getReceiveId());
                union.setUnionDeptId(unionDeptId);
                dao.add(union);

                result.add(receiveBase.getReceiveId());
            }
        }

        return result;
    }

    public List<Union> getUnions(Long documentId, Date time) throws Exception
    {
        return dao.getUnions(documentId, time);
    }

    public Integer getUnionCount(Long documentId, Date time) throws Exception
    {
        return dao.getUnionCount(documentId, time);
    }

    public List<Integer> getCurrentUnionDeptIds(Long documentId) throws Exception
    {
        return dao.getCurrentUnionDeptIds(documentId);
    }

    public List<Union> getCurrentUnions(Long documentId) throws Exception
    {
        return dao.getCurrentUnions(documentId);
    }

    public void cancelSend(Long sendId, Integer deptId, Integer userId) throws Exception
    {
        cancelSend(dao.getSend(sendId), new UserInfo(userId, deptId));
    }

    /**
     * 撤销发文
     *
     * @param send 要撤销的发文的对象
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public void cancelSend(Send send, UserInfo userInfo) throws Exception
    {
        //标志发文为已撤回的状态
        send.setState(SendState.canceled);
        dao.update(send);

        SendFlowInstance sendFlowInstance = odFlowDao.getSendFlowInstanceByDocumentId(send.getDocumentId());
        if (sendFlowInstance != null)
        {
            OdFlowInstance flowInstance = odFlowDao.getOdFlowInstance(sendFlowInstance.getInstanceId());

            if (flowInstance != null)
            {
                //修改公文流转实例的状态
                flowInstance.setState(OdFlowInstanceState.unclosed);
                flowInstance.setEndTime(Null.Timestamp);
            }

            //复制公文，保证对公文的修改不影响原来已经发送出去的公文
            OfficeDocument document = dao.copyDocument(send.getDocument());
            document.setFinalTime(send.getDocument().getFinalTime());

            if (flowInstance != null)
            {
                if ("send".equals(flowInstance.getType()))
                    flowInstance.setDocumentId(document.getDocumentId());
                odFlowDao.update(flowInstance);
            }

            sendFlowInstance.setDocumentId(document.getDocumentId());
            sendFlowInstance.setSentOut(false);
            odFlowDao.update(sendFlowInstance);

            ReceiptDao receiptDao = receiptDaoProvider.get();
            Receipt receipt = receiptDao.getReceiptByDocumentId(send.getDocumentId());
            if (receipt != null)
            {
                receipt.setDocumentId(document.getDocumentId());
                receipt.setSended(false);
                receiptDao.update(receipt);
            }

            FlowApi.getController(sendFlowInstance.getInstanceId(), OdSystemFlowDao.getInstance()).cancelInstance(null);

            //替换表单中的附件ID为新的附件ID
            if (document.getAttachmentId() != null)
            {
                BusinessContext businessContext = new BusinessContext();
                businessContext.setUser(userInfo);
                businessContext.setBusinessDeptId(send.getDeptId());

                SystemFormContext formContext = new SystemFormContext("", sendFlowInstance.getBodyId());
                formContext.setBusinessContext(businessContext);

                FormApi.loadFormContext(formContext);

                ComponentData attachmentData = formContext.getFormData().getData(Constants.Document.ATTACHMENT);
                if (attachmentData instanceof FileListData)
                {
                    FileListData fileListData = (FileListData) attachmentData;
                    fileListData.setFileListId(document.getAttachmentId().toString());

                    FormApi.saveFormBody(formContext);
                }
            }
        }
    }

    /**
     * 转发公文
     *
     * @param receiveId 要转发的收文的ID
     * @param receivers 收文单位列表，即转发的目标
     * @param userId    当前操作人ID
     * @param deptId    当前操作人所属部门
     * @return 转发后的发文ID
     * @throws Exception 转发公文失败
     */
    @Transactional
    public Long turnDocument(Long receiveId, List<Member> receivers, Integer userId, Integer deptId)
            throws Exception
    {
        ReceiveBase receiveBase = dao.getReceiveBase(receiveId);

        //复制一份公文
        OfficeDocument document = receiveBase.getDocument();

        OfficeDocument document2 = new OfficeDocument();
        document2.setTitle(document.getTitle());
        document2.setSubject(document.getSubject());
        document2.setSendNumber(document.getSendNumber());
        document2.setPriority(document.getPriority());
        document2.setSecret(document.getSecret());
        document2.setSendCount(document.getSendCount());
        document2.setSigner(document.getSigner());
        document2.setSignTime(document.getSignTime());
        document2.setNoticeType(document.getNoticeType());
        document2.setAttributes(new HashMap<String, String>(document.getAttributes()));
        document2.setAttachmentId(document.getAttachmentId());
        document2.setTextId(document.getTextId());
        document2.setCreateDeptId(receiveBase.getDeptId());
        Dept dept = receiveBase.getDept();
        document2.setSourceDept(dept.getAllName(1));

        String sourceDeptCode;
        if (!StringUtils.isEmpty(dept.getOrgCode()))
            sourceDeptCode = dept.getOrgCode();
        else
            sourceDeptCode = dept.getDeptCode();
        document2.setSourceDeptCode(sourceDeptCode);

        document2.setLastTime(new Date());
        dao.add(document2);

        User user = dao.getUser(userId);

        //创建收文记录
        Send send = new Send();
        send.setDeptId(receiveBase.getDeptId());
        send.setCreator(userId);
        send.setCreatorName(user.getUserName());
        send.setCreateDeptId(deptId);
        send.setSender(userId);
        send.setSenderName(user.getUserName());
        send.setDocumentId(document2.getDocumentId());
        send.setSendTime(new Date());
        send.setState(SendState.sended);
        getDao().add(send);

        sendDocumentTo(document2, receivers, "转发", receiveId);

        return send.getSendId();
    }
}
