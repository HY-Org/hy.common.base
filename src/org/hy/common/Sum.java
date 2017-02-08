package org.hy.common;

import java.util.Iterator;
import java.util.Map;





/**
 * 合计器 -- Mini型的。只是最单纯的合计
 * 
 *  与org.hy.common.Counter类似
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2016-07-15
 *              2016-08-28  1.添加：平均数计算功能。
 *                          2.添加：最大长度，Map集合的最大长度限定，当超过这个长度时，就删除最早的元素。类似于队列
 */
public class Sum<K> extends ListMap<K ,Double> implements Map<K ,Double>
{

    private static final long serialVersionUID = -7922813660591015550L;

    /** 合计值 */
    private double sumValue = 0;
    
    /** 最大长度 */
    private int    maxSize  = 0;
    
    
    
    public Sum()
    {
        super();
    }
    
    
    
    public Sum(int i_InitialCapacity)
    {
        super(i_InitialCapacity);
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_AddValue
     * @return
     */
    public Double set(K i_Key ,Integer i_AddValue)
    {
        return this.set(i_Key ,i_AddValue.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_AddValue
     * @return
     */
    public Double set(K i_Key ,Float i_AddValue)
    {
        return this.set(i_Key ,i_AddValue.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_AddValue
     * @return
     */
    public Double set(K i_Key ,Long i_AddValue)
    {
        return this.set(i_Key ,i_AddValue.doubleValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_AddValue
     * @return
     */
    public synchronized Double set(K i_Key ,Double i_AddValue)
    {
        double v_AddValue = 0D;
        
        if ( i_AddValue != null )
        {
            v_AddValue = i_AddValue.doubleValue();
        }
        
        sumValue += v_AddValue;
        
        Double v_Ret = super.put(i_Key ,Double.valueOf(v_AddValue));
        
        if ( this.maxSize >= 1 && this.size() == this.maxSize )
        {
            this.sumValue -= this.remove(0);
        }
        
        return v_Ret == null ? 0L : v_Ret;
    }
    
    
    
    /**
     * 计数型的递增值
     */
    public Double put(K i_Key)
    {
        return this.put(i_Key ,1L);
    }
    
    
    
    /**
     * 计数型的递减值
     */
    public Double putMinus(K i_Key)
    {
        return this.put(i_Key ,-1L);
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    public Double put(K i_Key ,Integer i_AddValue)
    {
        return this.put(i_Key ,i_AddValue.doubleValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    public Double put(K i_Key ,Float i_AddValue)
    {
        return this.put(i_Key ,i_AddValue.doubleValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    public Double put(K i_Key ,Long i_AddValue)
    {
        return this.put(i_Key ,i_AddValue.doubleValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    @Override
    public synchronized Double put(K i_Key ,Double i_AddValue)
    {
        double v_AddValue = 0D;
        
        if ( i_AddValue != null )
        {
            v_AddValue = i_AddValue.doubleValue();
        }
        
        sumValue += v_AddValue;
        
        if ( this.containsKey(i_Key) )
        {
            v_AddValue = v_AddValue + super.get(i_Key).doubleValue();
        }
        
        Double v_Ret = super.put(i_Key ,Double.valueOf(v_AddValue));
        
        if ( this.maxSize >= 1 && this.size() > this.maxSize )
        {
            this.sumValue -= this.remove(0);
        }
        
        return v_Ret == null ? 0D : v_Ret;
    }
    
    
    
    @Override
    public synchronized void putAll(Map<? extends K, ? extends Double> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Double>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Double> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    public synchronized void setAll(Map<? extends K, ? extends Double> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Double>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Double> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }



    public double getMinValue()
    {
        return Help.toSort(Help.toList(this)).get(0);
    }

    
    
    public double getMaxValue()
    {
        return Help.toReverse(Help.toList(this)).get(0);
    }
    
    
    
    public double getSumValue()
    {
        return sumValue;
    }
    
    
    
    public double getAvgValue()
    {
        return Help.division(this.getSumValue() ,this.size()); 
    }
    
    
    
    /**
     * 获取：最大长度
     */
    public int getMaxSize()
    {
        return maxSize;
    }


    
    /**
     * 设置：最大长度
     * 
     * @param maxSize 
     */
    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }
    
    
    
    /**
     * 求某个键值的合计次数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-21
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    public double getSumValue(String i_Key)
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
     * 求某些键值的合计次数
     * 
     * @param i_Keys
     * @return
     */
    public double getSumValue(String [] i_Keys)
    {
        double v_Sum = 0;
        
        if ( Help.isNull(i_Keys) )
        {
            return v_Sum;
        }
        
        for (int i=0; i<i_Keys.length; i++)
        {
            if ( i_Keys[i] != null )
            {
                Double v_Temp = this.get(i_Keys[i]);
                
                if ( v_Temp != null )
                {
                    v_Sum += v_Temp.doubleValue();
                }
            }
        }
        
        return v_Sum;
    }
    
    
    
    /**
     * 求排除某些键值的其余键值的合计次数
     * 
     * @param i_Keys
     * @return
     */
    public double getSumValueExclude(String [] i_Keys)
    {
        return this.sumValue - this.getSumValue(i_Keys);
    }
    
}
