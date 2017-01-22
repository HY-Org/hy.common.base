package org.hy.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;





/**
 * 轮转、周而复始的循环
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-22
 * @version     v1.0
 * @param <V>
 */
public class CycleList<V> extends ArrayList<V> implements List<V> 
{

    private static final long serialVersionUID = -2247379996020664182L;
    
    /** 当前轮转到的索引号 */
    private int cycleIndex;
    
    
    
    public CycleList()
    {
        super();
        this.cycleIndex = 0;
    }
    
    
    
    public CycleList(int initialCapacity) 
    {
        super(initialCapacity);
        this.cycleIndex = 0;
    }
    
    
    
    public CycleList(Collection<? extends V> c) 
    {
        super(c);
        this.cycleIndex = 0;
    }
    
    
    
    /**
     * 轮转的方式获取下一个元素对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *
     * @return
     */
    public V next()
    {
        return this.getCycleValue(1);
    }
    
    
    
    /**
     * 轮转的方式获取前一个元素对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *
     * @return
     */
    public V previous()
    {
        return this.getCycleValue(-1);
    }
    
    
    
    /**
     * 轮转的方式获取下一个(或前一个)元素对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *
     * @param i_CycleType  轮转类型（轮转方向）
     * @return
     */
    private synchronized V getCycleValue(int i_CycleType)
    {
        if ( this.size() <= 0 )
        {
            return null;
        }
        
        if ( i_CycleType >= 0 )
        {
            if ( this.cycleIndex >= this.size() - 1 )
            {
                this.cycleIndex = -1;
            }
            
            return this.get(++this.cycleIndex);
        }
        else
        {
            if ( this.cycleIndex <= 0 )
            {
                this.cycleIndex = this.size();
            }
            
            return this.get(--this.cycleIndex);
        }
    }
    
}
