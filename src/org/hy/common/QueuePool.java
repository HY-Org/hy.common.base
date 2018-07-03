package org.hy.common;

import java.util.concurrent.ConcurrentLinkedQueue;





/**
 * 队列缓存池。
 * 
 * 预先创建对象（主要用于对象在创建时十分耗时的情况），在get()时直接使用，提高创建对象的性能。
 * 
 * 创建对象的时机：
 *   1. 构造本类的实例时。可通过i_IsInitPool参数调用创建对象的方式（true:同步创建对象； false:异步创建对象）。
 *   2. 调用get()方法时。当队列缓存池中的元素小于poolMinSize阀值时，将自动的、异步的、多线程的创建对象。
 *   
 * 测试数据：创建1000个Fel用时23秒，但1000次计算只需0.200秒。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-07-02
 * @version     v1.0
 * @param <O>   队列缓存池中预先创建的对象
 */
public class QueuePool<O> extends ConcurrentLinkedQueue<O>
{

    private static final long serialVersionUID = -8087291628889068333L;
    
    
    /** 队列缓存池中的元素类型 */
    private Class<O> poolDataClass;
    
    /** 队列缓存池的大小 */
    private int      poolSize;
    
    /** 队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中 */
    private int      poolMinSize;
    
    /** 创建队列缓存池中元素的最大线程数量（默认值：10） */
    private int      addingMaxThreadCount;
    
    /** 创建队列缓存池中元素的当前程数量 */
    private int      addingThreadCount;
    
    
    
    /**
     * 队列缓存池中的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-02
     * @version     v1.0
     *
     * @param i_PoolDataClass   队列缓存池中的元素类型
     * @param i_PoolSize        队列缓存池的大小
     * @param i_PoolMinSize     队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     */
    public QueuePool(Class<O> i_PoolDataClass ,int i_PoolSize ,int i_PoolMinSize)
    {
        this(i_PoolDataClass ,i_PoolSize ,i_PoolMinSize ,true);
    }
    
    
    
    /**
     * 队列缓存池中的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-02
     * @version     v1.0
     *
     * @param i_PoolDataClass   队列缓存池中的元素类型
     * @param i_PoolSize        队列缓存池的大小
     * @param i_PoolMinSize     队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     * @param i_IsInitPool      在构造器中初始化完成队列缓存池
     */
    public QueuePool(Class<O> i_PoolDataClass ,int i_PoolSize ,int i_PoolMinSize ,boolean i_IsInitPool)
    {
        this.poolDataClass        = i_PoolDataClass;
        this.poolSize             = i_PoolSize;
        this.poolMinSize          = i_PoolMinSize;
        this.addingMaxThreadCount = 10;
        this.addingThreadCount    = 0;
        
        if ( this.poolDataClass == null )
        {
            throw new InstantiationError("Pool data class is null.");
        }
        this.setPoolSize   (i_PoolSize);
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
     * @createDate  2018-07-02
     * @version     v1.0
     *
     * @return
     *
     * @see org.hy.common.Queue#get()
     */
    public O get() 
    {
        O v_Ret = this.poll();
        
        if ( v_Ret == null )
        {
            v_Ret = this.newObject();
        }
        
        if ( this.size() <= this.poolMinSize )
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
                while ( this.size() < this.poolSize )
                {
                    this.add(this.newObject());
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
        if ( this.addingThreadCount > this.addingMaxThreadCount )
        {
            return false;
        }
        else
        {
            this.addingThreadCount++;
            return true;
        }
    }
    
    
    private synchronized void addingFinsh()
    {
        this.addingThreadCount--;
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
        return poolSize;
    }


    
    /**
     * 获取：队列缓存池的最小大小，当小于此值时，将创建新的元素并添加到池中
     */
    public int getPoolMinSize()
    {
        return poolMinSize;
    }
    

    
    /**
     * 设置：队列缓存池的大小
     * 
     * @param i_PoolSize 
     */
    public void setPoolSize(int i_PoolSize)
    {
        if ( i_PoolSize < 1 )
        {
            throw new InstantiationError("Pool size < 1.");
        }
        
        this.poolSize = i_PoolSize;
        
        this.startAdding();
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
        
        this.startAdding();
    }


    
    /**
     * 获取：创建队列缓存池中元素的最大线程数量（默认值：10）
     */
    public int getAddingMaxThreadCount()
    {
        return addingMaxThreadCount;
    }


    
    /**
     * 设置：创建队列缓存池中元素的最大线程数量（默认值：10）
     * 
     * @param addingMaxThreadCount 
     */
    public void setAddingMaxThreadCount(int addingMaxThreadCount)
    {
        this.addingMaxThreadCount = addingMaxThreadCount;
    }


    
    /**
     * 获取：创建队列缓存池中元素的当前程数量
     */
    public int getAddingThreadCount()
    {
        return addingThreadCount;
    }
    
}
