package org.hy.common;

import java.util.EventObject;





/**
 * 线程方式执行相关的动作的事件 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 */
public class ExecuteEvent extends EventObject
{

    private static final long serialVersionUID = -1848289847015029490L;

    
    
    /** 执行实现用时时长（不含延时启动的延时时长）。单位：毫秒 */
    private long    timelen;
    
    /** 执行是否异常 */
    private boolean isError;
    
    /** 执行方法的返回值。执行异常、方法无返回值时均为null */
    private Object  result;
    
    
    
    /**
     * 构造器 
     *
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Source   事件发生源。执行方法所属对象的实例。
     * @param i_TimeLen  执行实现用时时长（不含延时启动的延时时长）。单位：毫秒
     * @param i_IsError  执行是否异常
     * @param i_Result   执行方法的返回值。执行异常、方法无返回值时均为null
     */
    public ExecuteEvent(Object i_Source ,long i_TimeLen ,boolean i_IsError ,Object i_Result)
    {
        super(i_Source);
        
        this.timelen = i_TimeLen;
        this.isError = i_IsError;
        this.result  = i_Result;
    }


    
    /**
     * 获取：执行实现用时时长（不含延时启动的延时时长）。单位：毫秒
     */
    public long getTimelen()
    {
        return timelen;
    }


    
    /**
     * 获取：执行是否异常
     */
    public boolean isError()
    {
        return isError;
    }


    
    /**
     * 获取：执行方法的返回值。执行异常、方法无返回值时均为null
     */
    public Object getResult()
    {
        return result;
    }
    
}
