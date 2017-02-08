package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 计数器 -- Mini型的。只是最单纯的计数
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2014-12-15
 */
public class Counter<K> extends Hashtable<K ,Long>
{

    private static final long serialVersionUID = -7922813660591015550L;

    /** 最小值 */
    private long minValue = 0;
    
    /** 最大值 */
    private long maxValue = 0;
    
    /** 合计值 */
    private long sumValue = 0;
    
    
    
    public Counter()
    {
        super();
    }
    
    
    
    public Counter(int i_InitialCapacity)
    {
        super(i_InitialCapacity);
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Count
     * @return
     */
    public Long set(K i_Key ,Integer i_Count)
    {
        return this.set(i_Key ,i_Count.longValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Count
     * @return
     */
    public synchronized Long set(K i_Key ,Long i_Count)
    {
        long v_Count = 0L;
        if ( i_Count != null )
        {
            v_Count = i_Count.longValue();
        }
        
        sumValue += v_Count;
        
        if ( minValue > v_Count )
        {
            minValue = v_Count;
        }
        
        if ( maxValue < v_Count )
        {
            maxValue = v_Count;
        }
        
        Long v_Ret = super.put(i_Key ,Long.valueOf(v_Count));
        return v_Ret == null ? 0L : v_Ret;
    }
    
    
    
    /**
     * 计数型的递增值
     */
    public Long put(K i_Key)
    {
        return this.put(i_Key ,1L);
    }
    
    
    
    /**
     * 计数型的递减值
     */
    public Long putMinus(K i_Key)
    {
        return this.put(i_Key ,-1L);
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    public Long put(K i_Key ,Integer i_Count)
    {
        return this.put(i_Key ,i_Count.longValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    @Override
    public synchronized Long put(K i_Key ,Long i_Count)
    {
        long v_Count = 0L;
        if ( i_Count != null )
        {
            v_Count = i_Count.longValue();
        }
        
        sumValue += v_Count;
        
        if ( this.containsKey(i_Key) )
        {
            v_Count = v_Count + super.get(i_Key).longValue();
        }
        
        if ( minValue > v_Count )
        {
            minValue = v_Count;
        }
        
        if ( maxValue < v_Count )
        {
            maxValue = v_Count;
        }
        
        Long v_Ret = super.put(i_Key ,Long.valueOf(v_Count));
        return v_Ret == null ? 0L : v_Ret;
    }
    
    
    
    @Override
    public synchronized void putAll(Map<? extends K, ? extends Long> i_Counters) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Long>> i = i_Counters.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Long> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    public synchronized void setAll(Map<? extends K, ? extends Long> i_Counters) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Long>> i = i_Counters.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Long> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }



    public long getMinValue()
    {
        return minValue;
    }

    
    
    public long getMaxValue()
    {
        return maxValue;
    }
    
    
    
    public long getSumValue()
    {
        return sumValue;
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
    public long getSumValue(String i_Key)
    {
        if ( Help.isNull(i_Key) )
        {
            return 0L;
        }
        else if ( this.containsKey(i_Key) )
        {
            return this.get(i_Key);
        }
        else
        {
            return 0L;
        }
    }
    
    
    
    /**
     * 求某些键值的合计次数
     * 
     * @param i_Keys
     * @return
     */
    public long getSumValue(String [] i_Keys)
    {
        long v_Sum = 0;
        
        if ( Help.isNull(i_Keys) )
        {
            return v_Sum;
        }
        
        for (int i=0; i<i_Keys.length; i++)
        {
            if ( i_Keys[i] != null )
            {
                Long v_Temp = this.get(i_Keys[i]);
                
                if ( v_Temp != null )
                {
                    v_Sum += v_Temp.longValue();
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
    public long getSumValueExclude(String [] i_Keys)
    {
        return this.sumValue - this.getSumValue(i_Keys);
    }
    
}
