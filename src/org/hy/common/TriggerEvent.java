package org.hy.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 触发事件。所有有事件功能的类都可以使用此类。
 * 存在主要目的就是方便，它将所有公用功能都提炼到此。
 *
 * @author      ZhengWei(HY)
 * @createDate  2015-11-02
 * @version     v1.0
 */
public class TriggerEvent
{
    
    /** 事件监听者的集合 */
    private Collection<EventListener>            eventListeners;
    
    /** 事件监听器的源类型 */
    private Class<? extends EventListener>       eventListenerClass;
    
    /** 
     * 事件监听器的所有方法对象。主要是目的预先解释缓存后提高速度
     * 
     * map.key    为方法的名称(全大写)
     * map.value  为方法对象 
     */
    private Map<String ,Method>                  eventListenerMethods;
    
    /** 
     * 是否允许中断功能(默认启用中断)
     * 
     * 当事件监听器，执行动作方法的返回值类型是Boolean时，是否表示中断后面的监听器的继续执行
     */
    private boolean                              isAllowBreak;
    
    
    
    public TriggerEvent()
    { 
        this(null);
    }
    
    
    
    public TriggerEvent(Class<? extends EventListener> i_EventListenerClass)
    {
        this.eventListeners       = new HashSet<EventListener>();
        this.eventListenerMethods = new Hashtable<String ,Method>();
        this.isAllowBreak         = true;
        this.setEventListenerClass(i_EventListenerClass);
    }
    
    
    
    /**
     * 添加事件监听者
     * 
     * 注意：1. 内部验证已保证事件监听者都是同一类型的
     *      2. 当没有定义事件监听者的类型时，第一个监听者的类型就是标准
     * 
     * @param i_EventListener
     */
    public synchronized void addListener(EventListener i_EventListener)
    {
        if ( i_EventListener != null )
        {
            if ( this.eventListenerClass != null )
            {
                // 判断入参是否为 this.eventListenerClass 的实现类，保证事件监听者都是同一类型的。
                if ( i_EventListener.getClass() != this.eventListenerClass )
                {
                    if ( !MethodReflect.isExtendImplement(i_EventListener ,this.eventListenerClass) )
                    {
                        throw new java.lang.ClassCastException("EventListener not implements " + this.eventListenerClass.getName() + ".");
                    }
                }
            }
            else if ( this.eventListeners.size() <= 0 )
            {
                // 当没有定义事件监听者的类型时，第一个监听者的类型就是标准
                this.setEventListenerClass(i_EventListener.getClass());
            }
            
            this.eventListeners.add(i_EventListener);
        }
        else
        {
            throw new NullPointerException("EventListener is null.");
        }
    }
    
    
    
    /**
     * 移除事件监听者
     * 
     * @param i_EventListener
     */
    public synchronized void removeListener(EventListener i_EventListener)
    {
        this.eventListeners.remove(i_EventListener);
    }
    
    
    
    /**
     * 触发(通知)所有监听者，执行某一动作的执行
     * 
     * 当执行动作方法的返回值类型是Boolean时，表示是否中断后面的监听器的继续执行
     * 
     * @param i_MethodName              执行动作的方法名称(不区分大小写)
     * @param i_MethodArgs              执行动作方法的参数
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public synchronized void trigger(String i_MethodName ,Object ... i_MethodArgs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        if ( Help.isNull(this.eventListeners) )
        {
            return;
        }
        
        if ( Help.isNull(this.eventListenerMethods) )
        {
            return;
        }
        
        if ( Help.isNull(i_MethodName) )
        {
            throw new NullPointerException("Method name is null.");
        }
        
        Method v_ActionMehod = this.eventListenerMethods.get(i_MethodName.trim().toUpperCase());
        if (  v_ActionMehod == null )
        {
            throw new NullPointerException("Method[" + i_MethodName + "] is not find.");
        }
        
        Iterator<EventListener> v_Iter       = this.eventListeners.iterator();
        boolean                 v_IsContinue = true;

        while ( v_IsContinue && v_Iter.hasNext() ) 
        {
            EventListener v_EventListener = v_Iter.next();
            Object        v_ActionRet     = v_ActionMehod.invoke(v_EventListener ,i_MethodArgs);
            
            if ( this.isAllowBreak )
            {
                if ( v_ActionRet instanceof Boolean )
                {
                    v_IsContinue = (Boolean)v_ActionRet;
                }
            }
        }
    }
    
    
    
    /**
     * 获取：事件监听器的源类型
     */
    public synchronized Class<? extends EventListener> getEventListenerClass()
    {
        return eventListenerClass;
    }


    
    /**
     * 设置：事件监听器的源类型
     * 
     * 注意：方法内调用 this.clearListeners()，用来清空所有事件监听者。
     * 
     * @param eventListenerClass 
     */
    public synchronized void setEventListenerClass(Class<? extends EventListener> i_EventListenerClass)
    {
        this.eventListenerClass = i_EventListenerClass;
        this.clearListeners();
        
        if ( this.eventListenerClass != null )
        {
            Method [] v_Methods = this.eventListenerClass.getMethods();
            
            this.eventListenerMethods.clear();
            for (Method v_Method : v_Methods)
            {
                this.eventListenerMethods.put(v_Method.getName().toUpperCase() ,v_Method);
            }
        }
    }
    
    
    
    /**
     * 获取：是否允许中断功能(默认启用中断)
     * 
     * 当事件监听器，执行动作方法的返回值类型是Boolean时，是否表示中断后面的监听器的继续执行
     */
    public boolean isAllowBreak()
    {
        return isAllowBreak;
    }


    
    /**
     * 设置：是否允许中断功能(默认启用中断)
     * 
     * 当事件监听器，执行动作方法的返回值类型是Boolean时，是否表示中断后面的监听器的继续执行
     * 
     * @param isAllowBreak 
     */
    public void setAllowBreak(boolean isAllowBreak)
    {
        this.isAllowBreak = isAllowBreak;
    }



    /**
     * 清空所有事件监听者
     */
    public synchronized void clearListeners()
    {
        this.eventListeners.clear();
    }
    
    
    
    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable
    {
        this.clearListeners();
        this.eventListeners       = null;
        this.eventListenerMethods.clear();
        this.eventListenerMethods = null;
        
        super.finalize();
    }
    */
    
}
