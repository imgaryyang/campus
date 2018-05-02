package com.gzzm.ods.receipt;

/**
 * 回执组件，定义各种类型的回执
 *
 * @author camel
 * @date 12-4-8
 */
public interface ReceiptComponent
{
    /**
     * 回执类型
     *
     * @return 回执类型
     * @see Receipt#type
     */
    public String getType();

    /**
     * 设置回执的url
     *
     * @param receipt 回执对象
     * @return 设置回执的url
     * @throws Exception 允许子类抛出异常
     */
    public String getEditUrl(Receipt receipt) throws Exception;

    /**
     * 填写回执的url
     *
     * @param receipt  回执对象
     * @param deptId   填报部门
     * @param readOnly 只读方式打开
     * @return 填写回执的url
     * @throws Exception 允许子类抛出异常
     */
    public String getFillUrl(Receipt receipt, Integer deptId, boolean readOnly) throws Exception;

    /**
     * 收文单位主动创建回执
     *
     * @param documentId 公文ID
     * @param deptId     填报部门
     * @return 填写回执的url
     * @throws Exception 允许子类抛出异常
     */
    public String getFillUrl(Long documentId, Integer deptId) throws Exception;

    /**
     * 发文单位跟踪回执的url
     *
     * @param receipt 回执对象
     * @return 发文单位跟踪回执的url
     * @throws Exception 允许子类抛出异常
     */
    public String getTrackUrl(Receipt receipt) throws Exception;

    public void delete(Long receiptId) throws Exception;
}