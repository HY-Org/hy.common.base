package org.hy.common;

import java.io.Serializable;





/**
 * 钢琴键盘模式的队列缓存池
 * 
 * 预先创建对象（主要用于对象在创建时十分耗时的情况），在get()时直接使用，提高创建对象的性能。
 * 
 * 创建对象的时机：
 *   1. 构造本类的实例时。可通过i_IsInitPool参数调用创建对象的方式（true:同步创建对象； false:异步创建对象）。
 *   2. 调用get()方法时。当队列缓存池中的元素小于poolMinSize阀值时，将自动的、异步的、多线程的创建对象。
 *   
 * 测试数据：创建1000个Fel用时23秒，但1000次计算只需0.600秒。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-07-04
 * @version     v1.0
 * @param <O>   队列缓存池中预先创建的对象
 */
public class PianoKeyboardPool<O> implements Serializable
{

    private static final long serialVersionUID = 4396302890209694045L;
    
    
    /** 钢琴上所有键盘。即，数据对象的数组 */
    private PianoKeyboard<O> [] keyboards;
    
    /** 队列缓存池中的元素类型 */
    private Class<O>            poolDataClass;
    
    /** 队列缓存池的大小 */
    private int                 poolSize;
    
    /** 获取对象的指针序号 */
    private int                 readIndex;
    
    /** 获取对象的总次数 */
    private long                readCount;
    
    /** 获取对象时，队列池中无资源时，额外创建对象次数 */
    private long                readNewCount;
    
    /** 获取对象的总用时 */
    private long                readTime;
    
    /** 获取对象时，队列池中无资源时，额外创建对象用时 */
    private long                readNewTime;
    
    
    
    /**
     * 钢琴键盘模式的队列缓存池的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-04
     * @version     v1.0
     *
     * @param i_PoolDataClass   队列缓存池中的元素类型
     * @param i_PoolSize        队列缓存池的大小
     */
    public PianoKeyboardPool(Class<O> i_PoolDataClass ,int i_PoolSize)
    {
        this(i_PoolDataClass ,i_PoolSize ,true);
    }
    
    
    
    /**
     * 钢琴键盘模式的队列缓存池的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-04
     * @version     v1.0
     *
     * @param i_PoolDataClass   队列缓存池中的元素类型
     * @param i_PoolSize        队列缓存池的大小
     * @param i_PoolMinSize     队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     * @param i_IsInitPool      构造器中是否已同步方式初始化完成队列缓存池
     */
    @SuppressWarnings("unchecked")
    public PianoKeyboardPool(Class<O> i_PoolDataClass ,int i_PoolSize ,boolean i_IsInitPool)
    {
        if ( i_PoolDataClass == null )
        {
            throw new InstantiationError("Pool data class is null.");
        }
        if ( i_PoolSize < 1 )
        {
            throw new InstantiationError("Pool size < 1.");
        }
        
        this.poolSize      = i_PoolSize;
        this.keyboards     = new PianoKeyboard[this.poolSize];
        this.poolDataClass = i_PoolDataClass;
        this.readIndex     = -1;
        
        for (int i=0; i<this.poolSize; i++)
        {
            this.keyboards[i] = new PianoKeyboard<O>(this ,i_IsInitPool);
        }
    }
    
    
    
    /**
     * 获取数据对象，并触发创建新的数据对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-04
     * @version     v1.0
     *
     * @return
     */
    public synchronized O get()
    {
        long v_StartTime = System.currentTimeMillis();
        
        if ( this.readIndex >= this.poolSize - 1 )
        {
            this.readIndex = -1;
        }
        
        int v_Index = this.readIndex + 1;
        O   v_Ret   = this.keyboards[v_Index].poll();
        
        if ( v_Ret != null )
        {
            this.readIndex = v_Index;
        }
        else
        {
            long v_NewTime = System.currentTimeMillis();
            v_Ret = this.newObject();
            this.readNewCount++;
            this.readNewTime += System.currentTimeMillis() - v_NewTime;
        }
        
        this.readCount++;
        this.readTime += System.currentTimeMillis() - v_StartTime;
        
        return v_Ret;
    }
    
    
    
    /**
     * 创建队列缓存池中的元素对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-04
     * @version     v1.0
     *
     * @return
     */
    public O newObject()
    {
        try
        {
            return this.poolDataClass.newInstance();
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return null;
    }


    
    /**
     * 获取：队列缓存池中的元素类型
     */
    public Class<O> getPoolDataClass()
    {
        return poolDataClass;
    }
    

    
    /**
     * 获取：队列缓存池的大小
     */
    public int size()
    {
        return poolSize;
    }


    
    /**
     * 获取：获取对象的总次数
     */
    public long getReadCount()
    {
        return readCount;
    }
    

    
    /**
     * 获取：获取对象时，队列池中无资源时，额外创建对象次数
     */
    public long getReadNewCount()
    {
        return readNewCount;
    }


    
    /**
     * 获取：获取对象的总用时
     */
    public long getReadTime()
    {
        return readTime;
    }
    

    
    /**
     * 获取：获取对象时，队列池中无资源时，额外创建对象用时
     */
    public long getReadNewTime()
    {
        return readNewTime;
    }
    
}
