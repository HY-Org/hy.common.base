package org.hy.common;

import java.io.Serializable;





/**
 * 双通道缓存池
 * 
 * 预先创建对象（主要用于对象在创建时十分耗时的情况），在get()时直接使用，提高创建对象的性能。
 * 
 * 创建对象的时机：
 *   1. 构造本类的实例时。可通过i_IsInitPool参数调用创建对象的方式（true:同步创建对象； false:异步创建对象）。
 *   2. 调用get()方法时。当队列缓存池中的元素小于poolMinSize阀值时，将自动的、异步的、多线程的创建对象。
 *   
 * @author      ZhengWei(HY)
 * @createDate  2018-07-03
 * @version     v1.0
 * @param <O>   队列缓存池中预先创建的对象
 */
public class DualChannelPool<O> implements Serializable
{
    
    private static final long serialVersionUID = 4277466853337090846L;
    
    

    private DualChannel<O> dualChannel;
    
    /** 队列缓存池中的元素类型 */
    private Class<O>       poolDataClass;
    
    /** 队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中 */
    private int            poolMinSize;
    
    /** 创建队列缓存池中元素的最大线程数量（默认值：100） */
    private int            maxThreadCount;
    
    /** 创建队列缓存池中元素的当前程数量 */
    private int            threadCount;
    
    
    
    /**
     * 队列缓存池的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @param i_PoolDataClass   队列缓存池中的元素类型
     * @param i_PoolSize        队列缓存池的大小
     * @param i_PoolMinSize     队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     */
    public DualChannelPool(Class<O> i_PoolDataClass ,int i_PoolSize ,int i_PoolMinSize)
    {
        this(i_PoolDataClass ,i_PoolSize ,i_PoolMinSize ,true);
    }
    
    
    
    /**
     * 队列缓存池的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @param i_PoolDataClass   队列缓存池中的元素类型
     * @param i_PoolSize        队列缓存池的大小
     * @param i_PoolMinSize     队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     * @param i_IsInitPool      构造器中是否已同步方式初始化完成队列缓存池
     */
    public DualChannelPool(Class<O> i_PoolDataClass ,int i_PoolSize ,int i_PoolMinSize ,boolean i_IsInitPool)
    {
        if ( i_PoolDataClass == null )
        {
            throw new InstantiationError("Pool data class is null.");
        }
        
        this.dualChannel    = new DualChannel<O>(i_PoolSize);
        this.poolDataClass  = i_PoolDataClass;
        this.maxThreadCount = 10;
        this.threadCount    = 0;
        this.setPoolMinSize(i_PoolMinSize);
        
        if ( i_IsInitPool )
        {
            this.adding();
        }
        else
        {
            this.startAdding();
        }
    }
    
    
    
    /**
     * 从队列缓存池中获取一个新的元素。
     * 
     * 当队列缓存池中不存元素时，临时创建一个新元素返回。
     * 
     * 注：有意不加同步锁synchronized，好处是：在队列缓存中元素不足时，有机会产生多个线程同时创建新元素。
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @return
     */
    public O get() 
    {
        O v_Ret = this.dualChannel.poll();
        
        if ( v_Ret == null )
        {
            v_Ret = this.newObject();
        }
        
        if ( this.dualChannel.size() <= this.poolMinSize )
        {
            this.startAdding();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 开始异步方式执行向队列缓存池中添加元素。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-02
     * @version     v1.0
     *
     */
    public void startAdding()
    {
        (new Execute(this ,"adding")).start();
    }
    
    
    
    /**
     * 向队列缓存池中添加元素。
     * 
     * 注：有意不加同步锁synchronized，好处是：在队列缓存中元素不足时，有机会产生多个线程同时创建新元素。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-02
     * @version     v1.0
     *
     */
    public void adding()
    {
        if ( addingThreadCount() )
        {
            try
            {
                while ( this.dualChannel.size() < this.dualChannel.getChannelSize() )
                {
                    this.dualChannel.put(this.newObject());
                }
            }
            finally
            {
                this.addingFinsh();
            }
        }
    }
    
    
    
    /**
     * 添加线程计数器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @return
     */
    private synchronized boolean addingThreadCount()
    {
        if ( this.threadCount >= this.maxThreadCount )
        {
            return false;
        }
        else
        {
            this.threadCount++;
            return true;
        }
    }
    
    
    private synchronized void addingFinsh()
    {
        this.threadCount--;
    }
    
    
    
    /**
     * 创建队列缓存池中的元素对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-02
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
    public int getPoolSize()
    {
        return this.dualChannel.getChannelSize();
    }


    
    /**
     * 获取：队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     */
    public int getPoolMinSize()
    {
        return poolMinSize;
    }
    

    
    /**
     * 设置：队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     * 
     * @param i_PoolMinSize 
     */
    public void setPoolMinSize(int i_PoolMinSize)
    {
        if ( i_PoolMinSize < 0 )
        {
            throw new InstantiationError("Pool min size < 0.");
        }
        
        this.poolMinSize = i_PoolMinSize;
    }


    
    /**
     * 获取：创建队列缓存池中元素的最大线程数量（默认值：100）
     */
    public int getMaxThreadCount()
    {
        return maxThreadCount;
    }


    
    /**
     * 设置：创建队列缓存池中元素的最大线程数量（默认值：100）
     * 
     * @param maxThreadCount 
     */
    public void setAddingMaxThreadCount(int maxThreadCount)
    {
        this.maxThreadCount = maxThreadCount;
    }


    
    /**
     * 获取：创建队列缓存池中元素的当前程数量
     */
    public int getThreadCount()
    {
        return maxThreadCount;
    }
    
    
    
    /**
     * 双通道中的对象数量
     */
    public int size()
    {
        return this.dualChannel.size();
    }
    
}
