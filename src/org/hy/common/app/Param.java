package org.hy.common.app;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.hy.common.ChangeEvent;
import org.hy.common.ChangeListener;
import org.hy.common.Help;
import org.hy.common.TriggerEvent;





/**
 * 参数信息
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2013-04-20
 *           v2.0  2015-11-02  添加：是否为只读状态，当为只读状态时，this.value只能被设置一次。默认为只读状态
 *                             添加：this.value 的数值改变事
 *           v3.0  2021-12-14  添加：Setter方法均返回对象自己
 */
public class Param implements Serializable
{
    
    private static final long serialVersionUID = 6011553944173663382L;
    
    

    private String       name;
    
    private String       value;
    
    private String       comment;
    
    /** 是否为只读状态，当为只读状态时，this.value只能被设置一次。默认为只读状态 */
    private boolean      isOnlyRead;
    
    /** 添加 this.value 的数值改变事件(默认不启用) */
    private TriggerEvent changedTrigger;
    
    
    
    public Param()
    {
        this(null ,null ,null);
    }
    
    
    
    public Param(String i_Name ,String i_Value)
    {
        this(i_Name ,i_Value ,null);
    }
    
    
    
    public Param(String i_Name ,String i_Value ,String i_Comment)
    {
        this.name           = i_Name;
        this.value          = i_Value;
        this.comment        = i_Comment;
        this.isOnlyRead     = true;
        this.changedTrigger = null;
    }
    
    
    
    public String getName()
    {
        return name;
    }

    
    
    public Param setName(String name)
    {
        this.name = name;
        return this;
    }

    
    
    public String getValue()
    {
        return value;
    }

    
    
    /**
     * 当为只读状态时，this.value只能被设置一次。默认为只读状态
     * 
     * @param value
     */
    public Param setValue(String i_Value)
    {
        if ( this.isOnlyRead && !Help.isNull(this.value) )
        {
            throw new IllegalArgumentException("Value is only setting once. (isOnlyRead=" + this.isOnlyRead + ")");
        }
        
        String v_OldValue = this.value;
        this.value        = i_Value;
        
        if ( this.changedTrigger != null )
        {
            try
            {
                this.changedTrigger.trigger("valueChanged" ,new ChangeEvent<String>(this ,v_OldValue ,i_Value));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        
        return this;
    }

    
    
    public String getComment()
    {
        return comment;
    }

    
    
    public Param setComment(String comment)
    {
        this.comment = comment;
        return this;
    }
    
    
    
    /**
     * 获取：是否为只读状态，当为只读状态时，this.value只能被设置一次。默认为只读状态
     */
    public boolean isOnlyRead()
    {
        return isOnlyRead;
    }
    

    
    /**
     * 设置：是否为只读状态，当为只读状态时，this.value只能被设置一次。默认为只读状态
     * 
     * @param isOnlyRead
     */
    public Param setOnlyRead(boolean isOnlyRead)
    {
        this.isOnlyRead = isOnlyRead;
        return this;
    }
    
    
    
    /**
     * 添加 this.value 的数值改变事件
     * 
     * 注意：当执行此方法后，只读状态自动变为可写状态
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-11-02
     * @version     v1.0
     *
     * @param i_ChangeListener
     */
    public synchronized Param addChangedListener(ChangeListener<String> i_ChangeListener)
    {
        if ( this.changedTrigger == null )
        {
            this.changedTrigger = new TriggerEvent(ChangeListener.class);
        }
        
        this.isOnlyRead = false;
        this.changedTrigger.addListener(i_ChangeListener);
        return this;
    }



    @Override
    public String toString()
    {
        return this.value;
    }
    
}
