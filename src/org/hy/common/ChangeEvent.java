package org.hy.common;

import java.util.EventObject;





/**
 * 数值改变事件 
 * 
 * V：表示数值的类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2015-11-01
 * @version     v1.0
 */
public class ChangeEvent<V> extends EventObject
{

    private static final long serialVersionUID = -4921659559191557467L;

    /** 改变前的旧值 */
    private V oldValue;
    
    /** 改变后的新值 */
    private V newValue;
    
    
    
    /**
     * 构造器 
     *
     * @author      ZhengWei(HY)
     * @createDate  2015-11-01
     * @version     v1.0
     *
     * @param i_Source    事件发生源(或叫事件触发者、或是说谁被改变了)
     * @param i_OldValue  旧值
     * @param i_newValve  新值
     */
    public ChangeEvent(Object i_Source ,V i_OldValue ,V i_NewValue)
    {
        super(i_Source);
        
        this.oldValue = i_OldValue;
        this.newValue = i_NewValue;
    }


    
    /**
     * 获取：改变前的旧值
     */
    public V getOldValue()
    {
        return oldValue;
    }


    
    /**
     * 获取：改变后的新值
     */
    public V getNewValue()
    {
        return newValue;
    }
    
}
