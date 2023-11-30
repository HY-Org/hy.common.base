package org.hy.common.app;

import java.io.Serializable;

import org.hy.common.ChangeEvent;
import org.hy.common.ChangeListener;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.TriggerEvent;





/**
 * 参数信息
 * 
 * @author   ZhengWei(HY)
 * @version  V1.0  2013-04-20
 *           V2.0  2015-11-02  添加：是否为只读状态，当为只读状态时，this.value只能被设置一次。默认为只读状态
 *                             添加：this.value 的数值改变事
 *           V3.0  2023-11-30  添加：Integer、Long、Double、Datel四种常用类型
 */
public class Param implements Serializable
{
    
    private static final long serialVersionUID = 6011553944173663382L;
    

    /** 名称 */
    private String       name;
    
    /** 字符类型的 */
    private String       value;
    
    /** 整数类型的 */
    private Integer      valueInt;
    
    /** 长整数类型的 */
    private Long         valueLong;
    
    /** 浮点类型的 */
    private Double       valueDouble;
    
    /** 时间类型的 */
    private Date         valueTime;
    
    /** 注释说明 */
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
     * @param i_Value  参数值
     */
    public void setValue(String i_Value)
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
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    
    
    public String getComment()
    {
        return comment;
    }

    
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    
    
    /**
     * 获取：整数类型的
     */
    public Integer getValueInt()
    {
        return valueInt;
    }


    
    /**
     * 设置：整数类型的
     * 
     * @param i_ValueInt 整数类型的
     */
    public void setValueInt(Integer i_ValueInt)
    {
        if ( this.isOnlyRead && !Help.isNull(this.valueInt) )
        {
            throw new IllegalArgumentException("Value is only setting once. (isOnlyRead=" + this.isOnlyRead + ")");
        }
        
        Integer v_OldValue = this.valueInt;
        this.valueInt      = i_ValueInt;
        
        if ( this.changedTrigger != null )
        {
            try
            {
                this.changedTrigger.trigger("valueChanged" ,new ChangeEvent<Integer>(this ,v_OldValue ,i_ValueInt));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    
    /**
     * 获取：长整数类型的
     */
    public Long getValueLong()
    {
        return valueLong;
    }


    
    /**
     * 设置：长整数类型的
     * 
     * @param i_ValueLong 长整数类型的
     */
    public void setValueLong(Long i_ValueLong)
    {
        if ( this.isOnlyRead && !Help.isNull(this.valueLong) )
        {
            throw new IllegalArgumentException("Value is only setting once. (isOnlyRead=" + this.isOnlyRead + ")");
        }
        
        Long v_OldValue = this.valueLong;
        this.valueLong  = i_ValueLong;
        
        if ( this.changedTrigger != null )
        {
            try
            {
                this.changedTrigger.trigger("valueChanged" ,new ChangeEvent<Long>(this ,v_OldValue ,i_ValueLong));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    
    /**
     * 获取：浮点类型的
     */
    public Double getValueDouble()
    {
        return valueDouble;
    }


    
    /**
     * 设置：浮点类型的
     * 
     * @param i_ValueDouble 浮点类型的
     */
    public void setValueDouble(Double i_ValueDouble)
    {
        if ( this.isOnlyRead && !Help.isNull(this.valueDouble) )
        {
            throw new IllegalArgumentException("Value is only setting once. (isOnlyRead=" + this.isOnlyRead + ")");
        }
        
        Double v_OldValue = this.valueDouble;
        this.valueDouble  = i_ValueDouble;
        
        if ( this.changedTrigger != null )
        {
            try
            {
                this.changedTrigger.trigger("valueChanged" ,new ChangeEvent<Double>(this ,v_OldValue ,i_ValueDouble));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    
    /**
     * 获取：时间类型的
     */
    public Date getValueTime()
    {
        return valueTime;
    }


    
    /**
     * 设置：时间类型的
     * 
     * @param i_ValueTime 时间类型的
     */
    public void setValueTime(Date i_ValueTime)
    {
        if ( this.isOnlyRead && !Help.isNull(this.valueTime) )
        {
            throw new IllegalArgumentException("Value is only setting once. (isOnlyRead=" + this.isOnlyRead + ")");
        }
        
        Date v_OldValue = this.valueTime;
        this.valueTime  = i_ValueTime;
        
        if ( this.changedTrigger != null )
        {
            try
            {
                this.changedTrigger.trigger("valueChanged" ,new ChangeEvent<Date>(this ,v_OldValue ,i_ValueTime));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
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
    public void setOnlyRead(boolean isOnlyRead)
    {
        this.isOnlyRead = isOnlyRead;
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
