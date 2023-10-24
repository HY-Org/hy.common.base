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
 *              v2.0  添加：数值名称、改变时间、分类类型、源对象的XID
 */
public class ChangeEvent<V> extends EventObject
{

    private static final long serialVersionUID = -4921659559191557467L;
    
    
    
    /** 数值改变时间 */
    private String changeTime;
    
    /** 数值改变源的对象的XJava标记ID */
    private String sourceXID;
    
    /** 数值分类类型 */
    private String valueType;
    
    /** 数值名称 */
    private String valueName;
    
    /** 改变前的旧值 */
    private V      oldValue;
    
    /** 改变后的新值 */
    private V      newValue;
    
    
    
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
        this(i_Source ,"" ,"" ,i_OldValue ,i_NewValue);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-09-22
     * @version     v1.0
     *
     * @param i_Source    事件发生源(或叫事件触发者、或是说谁被改变了)
     * @param i_OldValue  旧值
     * @param i_newValve  新值
     */
    public ChangeEvent(Object i_Source ,String i_ValueType ,String i_ValueName ,V i_OldValue ,V i_NewValue)
    {
        super(i_Source);
        
        this.changeTime = Date.getNowTime().getFullMilli();
        this.valueType  = i_ValueType;
        this.valueName  = i_ValueName;
        this.oldValue   = i_OldValue;
        this.newValue   = i_NewValue;
        
        if ( i_Source != null && i_Source instanceof XJavaID )
        {
            this.sourceXID = ((XJavaID) i_Source).getXJavaID();
        }
        else
        {
            this.sourceXID = "";
        }
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


    
    /**
     * 获取：数值名称
     */
    public String getValueName()
    {
        return valueName;
    }


    
    /**
     * 获取：数值改变时间
     */
    public String getChangeTime()
    {
        return changeTime;
    }


    
    /**
     * 获取：数值分类类型
     */
    public String getValueType()
    {
        return valueType;
    }


    
    /**
     * 获取：数值改变源的对象的XJava标记ID
     */
    public String getSourceXID()
    {
        return sourceXID;
    }
    
}
