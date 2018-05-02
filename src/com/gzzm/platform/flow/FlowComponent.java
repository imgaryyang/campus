package com.gzzm.platform.flow;

import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.FormContext;

import java.util.*;

/**
 * @author camel
 * @date 12-8-10
 */
public interface FlowComponent<C extends FlowComponentContext>
{
    /**
     * 初始化
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void init(C context) throws Exception;

    public void initForm(FormContext formContext, C context) throws Exception;

    /**
     * 显示流程页面之前的动作，一般是加载一些数据
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void beforeShow(C context) throws Exception;

    /**
     * 提取扩展的数据
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void extractData(C context) throws Exception;

    /**
     * 保存扩展的数据
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void saveData(C context) throws Exception;

    /**
     * 发送到下一个环节之前需要处理的扩展功能
     *
     * @param context     流程上下文信息
     * @param receiverMap 下环节及接收者信息
     * @throws Exception 允许实现类抛出错误
     */
    public void beforeSend(C context, Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception;

    /**
     * 发送到下一个环节之后需要处理的扩展功能
     *
     * @param context     上下文信息
     * @param receiverMap 下环节及接收者信息
     * @throws Exception 允许实现类抛出错误
     */
    public void afterSend(C context, Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception;

    /**
     * 结束流程之前的扩展动作
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void beforeEndFlow(C context) throws Exception;

    /**
     * 结束流程时的扩展动作
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void endFlow(C context) throws Exception;

    /**
     * 终止流程之前的扩展动作
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void beforeStopFlow(C context) throws Exception;

    /**
     * 终止流程时的扩展动作
     *
     * @param context 流程上下文信息
     * @throws Exception 允许实现类抛出错误
     */
    public void stopFlow(C context) throws Exception;


    /**
     * 需要引入的js文件
     *
     * @return js文件的全路径名
     */
    public String getJsFile();

    public boolean accept(Action action, FlowComponentContext context) throws Exception;
}