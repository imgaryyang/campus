package com.gzzm.ods.flow;

import net.cyan.valmiki.flow.*;

/**
 * @author camel
 * @date 2014/8/29
 */
public interface SendFlowActions extends OdFlowActions
{
    /**
     * 查看正文
     */
    public static final String SHOW_SEND_TEXT = "showSendText";

    /**
     * 编辑正文
     */
    public static final String EDIT_SEND_TEXT = "editSendText";

    /**
     * 查看痕迹
     */
    public static final String SHOW_BACK_TEXT = "showBackText";

    /**
     * 成文
     */
    public static final String FINALIZE_TEXT = "finalizeText";

    /**
     * 取消成文
     */
    public static final String UN_FINALIZE_TEXT = "unFinalizeText";

    /**
     * 联合发文单位处理
     */
    public static final String SEND_UNIONS = "sendUnions";

    /**
     * 联合发文单位盖章
     */
    public static final String SEND_UNIONS_SEAL = "sendUnionsSeal";

    /**
     * 转发文
     */
    public static final String TURN_SEND = "turnSend";

    /**
     * 取消发文
     */
    public static final String CANCEL_TURN_SEND = "cancelTurnSend";

    /**
     * 设置回执
     */
    public static final String EDIT_RECEIPT = "editReceipt";

    /**
     * 取消回执
     */
    public static final String REMOVE_RECEIPT = "removeReceipt";

    public static final DefaultAction SHOW_SEND_TEXT_ACTION = new BaseDefaultAction(SHOW_SEND_TEXT, null);

    public static final DefaultAction EDIT_SEND_TEXT_ACTION = new BaseDefaultAction(EDIT_SEND_TEXT, null);

    public static final DefaultAction SHOW_BACK_TEXT_ACTION = new BaseDefaultAction(SHOW_BACK_TEXT, null);

    public static final DefaultAction FINALIZE_TEXT_ACTION = new BaseDefaultAction(FINALIZE_TEXT, null);

    public static final DefaultAction UN_FINALIZE_TEXT_ACTION = new BaseDefaultAction(UN_FINALIZE_TEXT, null);

    @SuppressWarnings("UnusedDeclaration")
    public static final DefaultAction SEND_UNIONS_ACTION = new BaseDefaultAction(SEND_UNIONS, null);

    public static final DefaultAction SEND_UNIONS_SEAL_ACTION = new BaseDefaultAction(SEND_UNIONS_SEAL, null);

    public static final DefaultAction CANCEL_TURN_SEND_ACTION = new BaseDefaultAction(CANCEL_TURN_SEND, null);

    public static final DefaultAction REMOVE_RECEIPT_ACTION = new BaseDefaultAction(REMOVE_RECEIPT, null);
}