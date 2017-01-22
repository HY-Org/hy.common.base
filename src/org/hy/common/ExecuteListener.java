package org.hy.common;

import java.util.EventListener;




/**
 * 线程方式执行相关的动作的事件监听接口 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 */
public interface ExecuteListener extends EventListener
{
    
    /**
     * 执行结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Event
     */
    public void result(ExecuteEvent i_Event);
    
}
