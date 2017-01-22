package org.hy.common;

import java.util.List;





/**
 * 触发事件（公用的、基础类）。
 * 
 *  它一般与 java.util.EventListener 和 java.util.EventObject 两类配合使用，但不限于此。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 * @param <L>   事件的监听者。一般实现 java.util.EventListener 接口。但不限于此
 * @param <E>   事件数据。一般继承 java.util.EventObject 类。但不限于此
 */
public abstract class EventFire<L ,E>
{
    
    /** 事件监听者的集合 */
    protected List<L> listeners;
    
    
    
    /**
     * 触发事件。通知所有的事件监听者。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Event
     */
    protected abstract void fire(E i_Event);
    
    
    
    public EventFire(List<L> i_Listeners)
    {
        // 可以为元素个数为0的集合，但不能为空对象 
        if ( i_Listeners == null )
        {
            throw new NullPointerException("Listeners is null.");
        }
        
        this.listeners = i_Listeners;
    }
    
    
    
    /**
     * 获取所有事件的监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @return
     */
    protected List<L> getListeners()
    {
        return listeners;
    }
    
    
    
    /**
     * 获取事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Index
     * @return
     */
    protected L getListener(int i_Index)
    {
        return this.listeners.get(i_Index);
    }



    /**
     * 添加事件监听者
     * 
     * 配合XJava的XML文件配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void setListener(L i_Listener)
    {
        this.addListener(i_Listener);
    }
    
    
    
    /**
     * 添加事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void addListener(L i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("Listener is null.");
        }
        
        this.listeners.add(i_Listener);
    }
    
    
    
    /**
     * 移除事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Listener
     * @return
     */
    public L removeListener(L i_Listener)
    {
        if ( !Help.isNull(this.listeners) )
        {
            for (int v_Index=this.listeners.size()-1; v_Index>=0; v_Index--)
            {
                L v_Listener = this.listeners.get(v_Index);
                if ( v_Listener == i_Listener )
                {
                    return this.listeners.remove(v_Index);
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 清空所有事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     */
    public void clearListeners()
    {
        this.listeners.clear();
    }
    
}
