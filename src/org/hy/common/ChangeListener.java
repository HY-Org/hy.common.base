package org.hy.common;

import java.util.EventListener;





/**
 * 数值改变事件的监听接口
 * 
 * V：表示数值的类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2015-11-02
 * @version     v1.0
 */
public interface ChangeListener<V> extends EventListener
{
    
    /**
     * 数值改变后触发的动作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-11-02
     * @version     v1.0
     *
     * @param i_Event
     * @return         表示是否中断后面的监听器的继续执行
     */
    public boolean valueChanged(ChangeEvent<V> i_Event);
    
}
