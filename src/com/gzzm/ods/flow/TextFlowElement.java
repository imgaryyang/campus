package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.organ.UserInfo;
import com.gzzm.platform.weboffice.OfficeEditType;
import net.cyan.commons.util.InputFile;
import net.cyan.valmiki.flow.*;

import java.util.List;

/**
 * 能编辑或显示正文的流程对象
 *
 * @author camel
 * @date 2014/8/28
 */
public interface TextFlowElement extends OdFlowActions
{
    public OdFlowDao getDao();

    /**
     * 获得当前用户id
     *
     * @return 当前用户ID
     * @throws Exception 允许子类抛出异常
     */
    public Integer getUserId() throws Exception;

    /**
     * 获得当前用户信息
     *
     * @return 当前用户信息
     * @throws Exception 允许子类抛出异常
     */
    public UserInfo getUserInfo() throws Exception;

    public BusinessContext getBusinessContext() throws Exception;

    public Integer getBusinessDeptId() throws Exception;

    /**
     * 当前流程步骤ID
     *
     * @return 当前流程步骤ID
     * @throws Exception 允许子类抛出异常
     */
    public Long getStepId() throws Exception;

    public Long getInstanceId() throws Exception;

    public String getNodeId() throws Exception;

    public boolean isEditable() throws Exception;

    public FlowContext getFlowContext() throws Exception;

    public List<Action> getActions() throws Exception;

    /**
     * 当前正在编辑的正文类型，一个流程可以绑定多个正文，例如收文的来文，转发文等
     *
     * @return 当前正在编辑或者查看的正文类型
     */
    public String getTextType();

    /**
     * 当前正在编辑的正文文档
     *
     * @return 当前正在编辑的正文文档
     */
    public OfficeDocument getTextDocument();

    /**
     * 设置当前正在编辑的正文文档，之所以用put，不用set是为了防止被反射注入数据
     *
     * @param textDocument 当前正在编辑的正文文档
     */
    public void putTextDocument(OfficeDocument textDocument);

    public Long getTextDocumentId();

    public String getEncodedTextDocumentId();

    /**
     * 当前正文是否允许编辑
     *
     * @return 允许编辑返回true，不允许返回false，从客户端传过来的参数，传过来false表示已只读模式打开
     */
    public boolean isTextEditable();

    /**
     * 设置textEditable属性
     *
     * @param textEditable 见isTextEditable
     */
    public void setTextEditable(boolean textEditable);

    /**
     * 当前正在编辑的正文的编辑类型
     *
     * @return 当前正在编辑的正文的编辑类型
     */
    public OfficeEditType getTextEditType();

    /**
     * 当前正在编辑的正文的编辑类型，之所以用put，不用set是为了防止被反射注入数据
     *
     * @param textEditType 编辑类型
     */
    public void putTextEditType(OfficeEditType textEditType);

    /**
     * 某个正文当前环节应该有的编辑方式
     *
     * @param document 正文文档
     * @param type     正文类型
     * @return 编辑方式，包括不留痕编辑，留痕，只读三种
     * @throws Exception 允许子类抛出异常
     */
    public OfficeEditType getEditType(OfficeDocument document, String type) throws Exception;

    /**
     * 得到当前正文的标题
     *
     * @return 文件标题
     */
    public String getTextTitle();

    /**
     * 锁定了此正文的用户的姓名
     *
     * @return 锁定了此正文的用户的姓名
     */
    public String getEditUserName();

    /**
     * 设置锁定了此正文的用户的姓名,之所以用put，不用set是为了防止被反射注入数据
     *
     * @param editUserName 锁定了此正文的用户的姓名
     */
    public void putEditUserName(String editUserName);

    /**
     * 当前编辑的正文的格式
     *
     * @return 当前编辑的正文的格式
     */
    public String getTextFormat();

    /**
     * 设置当前编辑的正文的格式
     *
     * @param textFormat 当前编辑的正文的格式
     */
    public void setTextFormat(String textFormat);

    /**
     * 加载某个正文
     *
     * @param type 正文类型
     * @return 正文对象
     * @throws Exception 允许子类抛出异常
     */
    public OfficeDocument getDocument(String type) throws Exception;

    /**
     * 上传的正文
     *
     * @return 上传的正文
     */
    public InputFile getText();

    public void setText(InputFile text);

    /**
     * 转向显示正文的页面
     *
     * @return 页面路径
     * @throws Exception 允许子类抛出异常
     */
    public String showText() throws Exception;

    public String getText(String textType, int length) throws Exception;

    /**
     * 保存正文
     *
     * @throws Exception 允许实现类抛出异常
     */
    public void saveText() throws Exception;

    /**
     * 保存其他格式正文
     *
     * @throws Exception 允许实现类抛出异常
     */
    public void saveOtherFile(String textType) throws Exception;

    /**
     * 释放正文
     *
     * @throws Exception 允许实现类抛出异常
     */
    public void releaseText() throws Exception;

    public Action[] getTextActions() throws Exception;

    public Action[] getTextActions(String type, boolean editable) throws Exception;
}