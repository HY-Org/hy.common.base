package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 最大值统计及分类最大值统计
 * 
 * 与org.hy.common.Counter类似
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2017-01-21
 */
public class Max<K> extends Hashtable<K ,Double>
{

    private static final long serialVersionUID = 6548043068210498395L;

    /** 最大值 */
    private double maxValue = 0D;
    
    
    
    public Max()
    {
        super();
    }
    
    
    
    public Max(int i_InitialCapacity)
    {
        super(i_InitialCapacity);
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Max
     * @return
     */
    public Double set(K i_Key ,Integer i_Max)
    {
        return this.set(i_Key ,i_Max.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Max
     * @return
     */
    public Double set(K i_Key ,Long i_Max)
    {
        return this.set(i_Key ,i_Max.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Max
     * @return
     */
    public Double set(K i_Key ,Float i_Max)
    {
        return this.set(i_Key ,i_Max.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Max
     * @return
     */
    public synchronized Double set(K i_Key ,Double i_Max)
    {
        Double v_Max = i_Max;
        
        if ( v_Max == null )
        {
            v_Max = 0D;
        }
        
        if ( maxValue < v_Max.doubleValue() )
        {
            maxValue = v_Max;
        }
        
        Double v_Ret = super.put(i_Key ,i_Max);
        return v_Ret == null ? 0D : v_Ret;
    }
    
    
    
    /**
     * 最大值统计及分类最大值统计
     */
    public Double put(K i_Key ,Integer i_Max)
    {
        return this.put(i_Key ,i_Max.doubleValue());
    }
    
    
    
    /**
     * 最大值统计及分类最大值统计
     */
    public Double put(K i_Key ,Long i_Max)
    {
        return this.put(i_Key ,i_Max.doubleValue());
    }
    
    
    
    /**
     * 最大值统计及分类最大值统计
     */
    public Double put(K i_Key ,Float i_Max)
    {
        return this.put(i_Key ,i_Max.doubleValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    @Override
    public synchronized Double put(K i_Key ,Double i_Max)
    {
        Double v_Max = i_Max;
        
        if ( v_Max == null )
        {
            v_Max = 0D;
        }
        
        if ( maxValue < v_Max.doubleValue() )
        {
            maxValue = v_Max;
        }
        
        Double v_OldValue = super.get(i_Key);
        if ( v_OldValue == null || v_OldValue < v_Max )
        {
            return super.put(i_Key ,v_Max);
        }
        else
        {
            return v_OldValue;
        }
    }
    
    
    
    @Override
    public synchronized void putAll(Map<? extends K, ? extends Double> i_Maxs) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Double>> i = i_Maxs.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Double> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    public synchronized void setAll(Map<? extends K, ? extends Double> i_Maxs) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Double>> i = i_Maxs.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Double> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }



    public Double getMaxValue()
    {
        return maxValue;
    }
    
    
    
    /**
     * 求某个键值的最大值（分类的最大值）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-21
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    public Double getMaxValue(String i_Key)
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
     * 求某些键值的最大值（每个分类中的最大值，再次比较后的最大值）
     * 
     * @param i_Keys
     * @return
     */
    public Double getMaxValue(String [] i_Keys)
    {
        Double v_Max = 0D;
        
        if ( Help.isNull(i_Keys) )
        {
            return v_Max;
        }
        
        for (int i=0; i<i_Keys.length; i++)
        {
            if ( i_Keys[i] != null )
            {
                Double v_Temp = this.get(i_Keys[i]);
                
                if ( v_Temp != null )
                {
                    if ( v_Temp.doubleValue() > v_Max.doubleValue() )
                    {
                        v_Max = v_Temp;
                    }
                }
            }
        }
        
        return v_Max;
    }
    
}
