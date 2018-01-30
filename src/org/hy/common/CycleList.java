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
 *              v2.0  2018-01-20  添加：current()方法，获取当前元素。 
 *              v3.0  2018-01-21  优化：首次执行next()方法时，从列表中首个元素开始循环。
 *                                修复：确保 cycleIndex 不会为负值。之前没有current()方法时，
 *                                     cycleIndex 为负值是没有关系的，而现在则不行。
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
     * 获取当前元素。
     * 
     * 当集合缩小并小于this.cycleIndex时，前首个元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-01-20
     * @version     v1.0
     *
     * @return
     */
    public synchronized V current()
    {
        int v_Size = this.size();
        
        if ( this.cycleIndex < v_Size )
        {
            return this.get(this.cycleIndex);
        }
        else if ( v_Size <= 0 )
        {
            return null;
        }
        else
        {
            this.cycleIndex = 0;  // 这设置成为0值，是为了下一次调用本方法时更快。
            return this.get(this.cycleIndex);
        }
    }
    
    
    
    /**
     * 轮转的方式获取下一个(或前一个)元素对象。
     * 
     * 有了此方法，就不用再去重写remove()、clearAll()等方法了，因为本方法是按size()重新计算读取cycleIndex位置的。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *              v2.0  优化：size()大小为1时的读取速度
     *
     * @param i_CycleType  轮转类型（轮转方向）
     * @return
     */
    private synchronized V getCycleValue(int i_CycleType)
    {
        int v_Size = this.size();
        
        if ( v_Size == 1 )
        {
            // 这里不用在对 cycleIndex 赋值为零。珍惜每一点点儿的性能
            return this.get(0);
        }
        else if ( v_Size <= 0 )
        {
            return null;
        }
        
        if ( i_CycleType >= 0 )
        {
            if ( this.cycleIndex >= v_Size - 1 )
            {
                this.cycleIndex = 0;
            }
            
            return this.get(this.cycleIndex++);
        }
        else
        {
            if ( this.cycleIndex <= 0 )
            {
                this.cycleIndex = v_Size;
            }
            
            return this.get(--this.cycleIndex);
        }
    }
    
}
