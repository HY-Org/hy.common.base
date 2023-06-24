package org.hy.common;

import java.util.LinkedHashMap;
import java.util.Map;





/**
 * 时间分组统计
 *
 * @author      ZhengWei(HY)
 * @createDate  2023-06-21
 * @version     v1.0
 */
public class TimeGroupTotal extends LinkedHashMap<Date ,Long> implements Map<Date ,Long>
{

    private static final long serialVersionUID = -385135442173957943L;
    
    /** 按秒分组 */
    public static final int $SplitType_Second = 1;
    
    /** 按分钟分组 */
    public static final int $SplitType_Minute = 2;
    
    /** 按小时分组 */
    public static final int $SplitType_Hour   = 3;
    
    /** 按天分组 */
    public static final int $SplitType_Day    = 4;
    
    
    
    /** 时间分组类型。默认按分钟分组 */
    private int splitType = $SplitType_Minute;
    
    /** 时间分组的时间段大小。有效取值范围在：1~59，建议取值范围在：1~30 */
    private int splitSize = 1;
    
    /** 保存时间分组数据的最大数量。超过最大数量时自动删除最早的数据。0表示无限制 */
    private int maxSize   = 0;
    
    
    
    public TimeGroupTotal(int i_SplitSize)
    {
        super();
        this.splitSize = i_SplitSize;
    }
    
    
    
    public TimeGroupTotal(int i_SplitSize ,int i_SplitType)
    {
        super();
        this.splitType = i_SplitType;
        this.splitSize = i_SplitSize;
    }
    
    
    
    public TimeGroupTotal(int i_SplitSize ,int i_SplitType ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        this.splitType = i_SplitType;
        this.splitSize = i_SplitSize;
    }
    
    
    
    /**
     * 生成时间分组的Key
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_TimeData
     * @return
     */
    public Date getTimeKey(Date i_TimeData)
    {
        Date v_TimeGroup = null;
        
        if ( this.splitType == $SplitType_Minute )
        {
            v_TimeGroup = i_TimeData.getTimeGroup(this.splitSize);
        }
        else if ( this.splitType == $SplitType_Second )
        {
            v_TimeGroup = i_TimeData.getTimeGroupSecond(this.splitSize);
        }
        else if ( this.splitType == $SplitType_Hour )
        {
            v_TimeGroup = i_TimeData.getTimeGroupHour(this.splitSize);
        }
        else if ( this.splitType == $SplitType_Day )
        {
            v_TimeGroup = i_TimeData.getTimeGroupDay(this.splitSize);
        }
        else
        {
            v_TimeGroup = i_TimeData.getTimeGroup(this.splitSize);
        }
        
        return v_TimeGroup;
    }
    
    
    
    /**
     * 添加时间分组数据
     * 
     * 支持不同时间分组类型的分组转换
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_TimeDatas
     *
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends Date ,? extends Long> i_TimeDatas)
    {
        for (Map.Entry<? extends Date ,? extends Long> v_Item : i_TimeDatas.entrySet())
        {
            this.put(v_Item.getKey() ,v_Item.getValue());
        }
    }
    
    
    
    /**
     * 添加时间分组数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_TimeData    时间分组数据
     * @param i_TotalCount  时间分组的统计数据（将被累加）
     * @return
     *
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized Long put(Date i_TimeData ,Long i_TotalCount)
    {
        Date v_DataKey   = this.getTimeKey(i_TimeData);
        Long v_DataValue = super.get(v_DataKey);
        
        if ( v_DataValue == null )
        {
            v_DataValue = i_TotalCount;
        }
        else
        {
            v_DataValue += i_TotalCount.longValue();
        }
        
        super.put(v_DataKey ,v_DataValue);
        
        // 超过最大数量时自动删除最早的数据。0表示无限制
        if ( this.maxSize > 0 && super.size() > this.maxSize )
        {
            this.remove(super.keySet().iterator().next());
        }
        
        return v_DataValue;
    }



    /**
     * 添加时间分组数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_TimeData  时间分组数据
     * @return            返回当前时间分组的总数量
     */
    public Long put(Date i_TimeData)
    {
        return this.put(i_TimeData ,1L);
    }
    
    
    
    /**
     * 添加时间分组数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @return            返回当前时间分组的总数量
     */
    public Long put()
    {
        return this.put(new Date() ,1L);
    }
    
    
    
    /**
     * 获取时间分组的统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_Key
     * @return
     *
     */
    public Long get(Date i_Key)
    {
        return super.get(this.getTimeKey(i_Key));
    }
    
    
    
    /**
     * 获取时间分组的统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_Key
     * @return
     *
     * @see java.util.LinkedHashMap#get(java.lang.Object)
     */
    @Override
    public Long get(Object i_Key)
    {
        if ( i_Key == null )
        {
            return 0L;
        }
        else if ( i_Key instanceof Date )
        {
            return super.get(this.getTimeKey((Date)i_Key));
        }
        else if ( i_Key instanceof java.util.Date )
        {
            return super.get(this.getTimeKey(new Date((java.util.Date)i_Key)));
        }
        else if ( i_Key instanceof Long )
        {
            return super.get(this.getTimeKey(new Date((Long)i_Key)));
        }
        else
        {
            return 0L;
        }
    }
    
    
    
    /**
     * 删除某一个时间分组的统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_TimeData  时间分组数据
     * @return
     */
    public synchronized Long remove(Date i_TimeData)
    {
        Date v_DataKey   = this.getTimeKey(i_TimeData);
        Long v_DataValue = super.remove(v_DataKey);
        
        return v_DataValue;
    }
    
    
    
    /**
     * 删除某一个时间分组的统计数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-06-23
     * @version     v1.0
     *
     * @param i_Key
     * @return
     *
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    @Override
    public synchronized Long remove(Object i_Key)
    {
        if ( i_Key == null )
        {
            return 0L;
        }
        else if ( i_Key instanceof Date )
        {
            return super.remove(this.getTimeKey((Date)i_Key));
        }
        else if ( i_Key instanceof java.util.Date )
        {
            return super.remove(this.getTimeKey(new Date((java.util.Date)i_Key)));
        }
        else if ( i_Key instanceof Long )
        {
            return super.remove(this.getTimeKey(new Date((Long)i_Key)));
        }
        else
        {
            return 0L;
        }
    }
    
    
    
    /**
     * 清除数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-06-24
     * @version     v1.0
     *
     * @see java.util.LinkedHashMap#clear()
     */
    @Override
    public synchronized void clear()
    {
        super.clear();
    }
    
    
    
    /**
     * 获取：时间分组类型
     */
    public int getSplitType()
    {
        return splitType;
    }
    
    
    
    /**
     * 获取：时间分组的分钟大小。有效取值范围在：1~59，建议取值范围在：1~30
     */
    public int getSplitSize()
    {
        return splitSize;
    }


    
    /**
     * 获取：保存时间分组数据的最大数量。超过最大数量时自动删除最早的数据。0表示无限制
     */
    public int getMaxSize()
    {
        return maxSize;
    }


    
    /**
     * 设置：保存时间分组数据的最大数量。超过最大数量时自动删除最早的数据。0表示无限制
     * 
     * @param i_MaxSize 保存时间分组数据的最大数量。超过最大数量时自动删除最早的数据。0表示无限制
     */
    public void setMaxSize(int i_MaxSize)
    {
        this.maxSize = i_MaxSize;
    }
    
}
