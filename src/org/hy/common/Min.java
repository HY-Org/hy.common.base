package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 最小值统计及分类最小值统计
 * 
 * 与org.hy.common.Counter类似
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2017-01-21
 */
public class Min<K> extends Hashtable<K ,Double>
{

    private static final long serialVersionUID = 6548043068210498395L;

    /** 最小值 */
    private double minValue = 0D;
    
    
    
    public Min()
    {
        super();
    }
    
    
    
    public Min(int i_InitialCapacity)
    {
        super(i_InitialCapacity);
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Min
     * @return
     */
    public Double set(K i_Key ,Integer i_Min)
    {
        return this.set(i_Key ,i_Min.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Min
     * @return
     */
    public Double set(K i_Key ,Long i_Min)
    {
        return this.set(i_Key ,i_Min.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Min
     * @return
     */
    public Double set(K i_Key ,Float i_Min)
    {
        return this.set(i_Key ,i_Min.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Min
     * @return
     */
    public synchronized Double set(K i_Key ,Double i_Min)
    {
        double v_Min = 0D;
        if ( i_Min != null )
        {
            v_Min = i_Min.doubleValue();
        }
        
        if ( minValue > v_Min )
        {
            minValue = v_Min;
        }
        
        Double v_Ret = super.put(i_Key ,Double.valueOf(v_Min));
        return v_Ret == null ? 0D : v_Ret;
    }
    
    
    
    /**
     * 最小值统计及分类最小值统计
     */
    public Double put(K i_Key ,Integer i_Min)
    {
        return this.put(i_Key ,i_Min.doubleValue());
    }
    
    
    
    /**
     * 最小值统计及分类最小值统计
     */
    public Double put(K i_Key ,Long i_Min)
    {
        return this.put(i_Key ,i_Min.doubleValue());
    }
    
    
    
    /**
     * 最小值统计及分类最小值统计
     */
    public Double put(K i_Key ,Float i_Min)
    {
        return this.put(i_Key ,i_Min.doubleValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    @Override
    public synchronized Double put(K i_Key ,Double i_Min)
    {
        double v_Min = 0D;
        if ( i_Min != null )
        {
            v_Min = i_Min.doubleValue();
        }
        
        if ( minValue > v_Min )
        {
            minValue = v_Min;
        }
        
        Double v_OldValue = super.get(i_Key);
        if ( v_OldValue == null || v_OldValue.doubleValue() > v_Min )
        {
            return super.put(i_Key ,Double.valueOf(v_Min));
        }
        else
        {
            return v_OldValue;
        }
    }
    
    
    
    @Override
    public synchronized void putAll(Map<? extends K, ? extends Double> i_Mins) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Double>> i = i_Mins.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Double> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    public synchronized void setAll(Map<? extends K, ? extends Double> i_Mins) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Double>> i = i_Mins.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Double> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }



    public Double getMinValue()
    {
        return minValue;
    }
    
    
    
    /**
     * 求某个键值的最小值（分类的最小值）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-21
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    public Double getMinValue(String i_Key)
    {
        if ( Help.isNull(i_Key) )
        {
            return 0D;
        }
        else if ( this.containsKey(i_Key) )
        {
            return this.get(i_Key);
        }
        else
        {
            return 0D;
        }
    }
    
    
    
    /**
     * 求某些键值的最小值（每个分类中的最小值，再次比较后的最小值）
     * 
     * @param i_Keys
     * @return
     */
    public Double getMinValue(String [] i_Keys)
    {
        Double v_Min = 0D;
        
        if ( Help.isNull(i_Keys) )
        {
            return v_Min;
        }
        
        for (int i=0; i<i_Keys.length; i++)
        {
            if ( i_Keys[i] != null )
            {
                Double v_Temp = this.get(i_Keys[i]);
                
                if ( v_Temp != null )
                {
                    if ( v_Temp.doubleValue() < v_Min.doubleValue() )
                    {
                        v_Min = v_Temp;
                    }
                }
            }
        }
        
        return v_Min;
    }
    
}
