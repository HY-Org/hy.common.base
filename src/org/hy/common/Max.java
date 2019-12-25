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
 * @createDate  2017-01-21
 * @version     v1.0
 *              v2.0  2019-05-29  添加：remove() 方法。删除时，重新计算最大值
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
        double v_Max = 0D;
        if ( i_Max != null )
        {
            v_Max = i_Max.doubleValue();
        }
        
        if ( maxValue < v_Max )
        {
            maxValue = v_Max;
        }
        
        Double v_Ret = super.put(i_Key ,Double.valueOf(v_Max));
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
        double v_Max = 0D;
        if ( i_Max != null )
        {
            v_Max = i_Max.doubleValue();
        }
        
        if ( maxValue < v_Max )
        {
            maxValue = v_Max;
        }
        
        Double v_OldValue = super.get(i_Key);
        if ( v_OldValue == null || v_OldValue.doubleValue() < v_Max )
        {
            return super.put(i_Key ,Double.valueOf(v_Max));
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
    
    
    
    /**
     * 删除时，重新计算最大值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-05-29
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    @Override
    public synchronized Double remove(Object i_Key)
    {
        Double v_RemoveValue = super.remove(i_Key);
        
        if ( this.maxValue <= v_RemoveValue.doubleValue() )
        {
            double v_MaxValue = 0;
            for (Double v_Item : super.values())
            {
                if ( v_Item.doubleValue() > v_MaxValue )
                {
                    v_MaxValue = v_Item.doubleValue();
                }
            }
            this.maxValue = v_MaxValue;
        }
        
        return v_RemoveValue;
    }



    public Double getMaxValue()
    {
        return maxValue;
    }
    
    
    
    /**
     * 模糊查询所有键值，取满足条件键值的最大值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-12-25
     * @version     v1.0
     *
     * @param i_KeyLike   模糊查询最大值的键（区分大小写）
     * @return
     */
    public double getMaxValueByLike(K i_KeyLike)
    {
        if ( Help.isNull(i_KeyLike) || this.isEmpty() )
        {
            return 0L;
        }
        
        double v_Max  = Double.MIN_VALUE;
        String v_Like = i_KeyLike.toString();
        for (Map.Entry<K ,Double> v_Item : this.entrySet())
        {
            if ( v_Item.getKey().equals(i_KeyLike) )
            {
                if ( v_Max < v_Item.getValue() )
                {
                    v_Max = v_Item.getValue();
                }
            }
            else if ( v_Item.getKey().toString().indexOf(v_Like) >= 0 )
            {
                if ( v_Max < v_Item.getValue() )
                {
                    v_Max = v_Item.getValue();
                }
            }
        }
        
        return v_Max;
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
    public Double getMaxValue(K i_Key)
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
    public Double getMaxValue(K [] i_Keys)
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
