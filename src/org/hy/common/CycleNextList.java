package org.hy.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;





/**
 * 轮转、周而复始的循环（只前进，只能next）。
 * 
 * 源于org.hy.common.CycleList，但比它更简单，单向循环时性能更好。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-01-20
 * @version     v1.0
 * @param <V>
 */
public class CycleNextList<V> extends ArrayList<V> implements List<V> 
{

    private static final long serialVersionUID = -6860598668289724942L;
    
    /** 当前轮转到的索引号 */
    private int cycleIndex;
    
    
    
    public CycleNextList()
    {
        super();
        this.cycleIndex = 0;
    }
    
    
    
    public CycleNextList(int initialCapacity) 
    {
        super(initialCapacity);
        this.cycleIndex = 0;
    }
    
    
    
    public CycleNextList(Collection<? extends V> c) 
    {
        super(c);
        this.cycleIndex = 0;
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
     * 轮转的方式获取下一个元素对象
     * 
     * 有了此方法，就不用再去重写remove()、clearAll()等方法了，因为本方法是按size()重新计算读取cycleIndex位置的。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *              v2.0  优化：size()大小为1时的读取速度
     *
     * @return
     */
    public synchronized V next()
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
        
        if ( this.cycleIndex >= v_Size - 1 )
        {
            this.cycleIndex = -1;
        }
        
        return this.get(++this.cycleIndex);
    }
    
}
