package org.hy.common;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;





/**
 * 队列类
 * 
 * 1. 支持 "先进先出" 方式，默认方式
 * 2. 支持 "先进后出" 方式
 * 
 * <O>指队列中元素的对象类型
 *
 * @author   ZhengWei(HY)
 * @version  v1.0  2011-05-08
 * @version  v1.1  2014-08-16  1.添加：支持 "先进后出" 方式
 *                             2.添加：队列类型
 *                             3.添加：队列类型初始化不能修改，防止异常
 *           v1.2  2014-12-07  1.添加：查看队列中的数据，并不影响队列，类似于只读模式。
 *           v1.3  2018-08-24  1.添加：remove()方法添加返回值，表示是否删除成功。
 *           v1.4  2023-04-28  1.添加：iterator()获取迭代器，大数量频繁操作时，比 toArray 安全
 *           v2.0  2026-02-15  1.优化：ConcurrentLinkedDeque替换LinkedList，增强线程安全
 *           v2.1  2026-11-08  1.优化：去除 synchronized 改用 AtomicLong，发挥 ConcurrentLinkedDeque 的性能
 *                             1.修正：iterator()方法返回迭代器支持remove()，会从内部删除元素，但不会减少queueSize，因此会造成计数永久错误
 */
public class Queue<O> implements java.io.Serializable
{
    private static final long serialVersionUID = -4906701281382456673L;



    /** 队列类型 */
    public enum QueueType
    {
        /** 先进先出 */
        $First_IN_First_OUT
        
        /** 先进后出 */
       ,$First_IN_Last_OUT
    }
    
    
    
    private ConcurrentLinkedDeque<O> linkedList;
    
    /** 独立出大小是为性能与速度 */
    private AtomicLong               queueSize;
    
    /** 曾经进入过队列的对象次数 */
    private AtomicLong               putedCount;
    
    /** 队列类型 */
    private QueueType                queueType;
    
    
    
    public Queue()
    {
        this(QueueType.$First_IN_First_OUT);
    }
    
    
    public Queue(QueueType i_QueueType)
    {
        if ( i_QueueType == null )
        {
            throw new NullPointerException("QueueType is null.");
        }
        
        this.queueSize  = new AtomicLong(0);
        this.putedCount = new AtomicLong(0);
        this.linkedList = new ConcurrentLinkedDeque<O>();
        this.queueType  = i_QueueType;
    }
    
    
    public void put(O i_Object)
    {
        if ( QueueType.$First_IN_First_OUT == this.queueType )
        {
            this.linkedList.addFirst(i_Object);
        }
        else
        {
            this.linkedList.addLast(i_Object);
        }
        
        this.queueSize .incrementAndGet();
        this.putedCount.incrementAndGet();
    }
    
    
    /**
     * 获取队列中的元素。当队列为空时，返回null
     * 
     * @return
     */
    public O get()
    {
        O v_Ret = this.linkedList.pollLast();
        if (v_Ret != null)
        {
            this.queueSize.decrementAndGet();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 只简单的获取队列中的所有元素。并不影响队列，类似于只读模式。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-05
     * @version     v1.0
     *
     * @return
     */
    public Object [] getArray()
    {
        return this.toArray();
    }
    
    
    
    /**
     * private 隐性私有的setter方法。只是为了与getArray()方法配对。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-05
     * @version     v1.0
     *
     * @param i_Objects
     */
    public void setArray(Object [] i_Objects)
    {
        // private 隐性私有的setter方法。只是为了与getArray()方法配对。
    }
    

    
    /**
     * 只简单的获取队列中的所有元素。并不影响队列，类似于只读模式。
     * 
     * @return
     */
    public Object [] toArray()
    {
        return this.linkedList.toArray();
    }
    
    
    
    /**
     * 只简单的获取队列中的所有元素。并不影响队列，类似于只读模式。
     * 
     * @return
     */
    public <T> T [] toArray(T [] i_Arr)
    {
        return this.linkedList.toArray(i_Arr);
    }
    
    
    
    /**
     * 获取迭代器，大数量频繁操作时，比 toArray 安全
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-04-28
     * @version     v1.0
     *
     * @return
     */
    public Iterator<O> iterator()
    {
        Iterator<O> v_Iterator = null;
        if ( QueueType.$First_IN_First_OUT == this.queueType )
        {
            v_Iterator = this.linkedList.iterator();
        }
        else
        {
            v_Iterator = this.linkedList.descendingIterator();
        }
        
        return new IteratorReadOnly<O>(v_Iterator);
    }
    
    
    
    /**
     * 是否为空
     * 
     * @author      ZhengWei(HY)
     * @createDate  2011-05-08
     * @version     v1.0
     *
     * @return
     */
    public boolean isEmpty()
    {
        return this.queueSize.get() <= 0;
    }
    
    
    /**
     * 队列大小
     * 
     * @return
     */
    public long size()
    {
        return this.queueSize.get();
    }
    
    
    /**
     * 删除队列中对象
     * 
     * @param i_Object
     */
    public boolean remove(O i_Object)
    {
        if ( this.linkedList.remove(i_Object) )
        {
            this.queueSize.decrementAndGet();
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 清空队列中所有对象
     */
    public void clear()
    {
        this.linkedList.clear();
        this.queueSize.set(0);
    }
    
    
    /**
     * 获取曾经进入过队列的对象次数
     * 
     * @return
     */
    public long getPutedCount()
    {
        return this.putedCount.get();
    }
    
    
    /**
     * 获取曾经出去过队列的对象次数
     * 
     * @return
     */
    public long getOutedCount()
    {
        return this.putedCount.get() - this.queueSize.get();
    }
    
    
    /**
     * 获取队列类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2011-05-08
     * @version     v1.0
     *
     * @return
     */
    public QueueType getQueueType()
    {
        return queueType;
    }
    

    /**
     * 只简单的获取队列中的所有元素。并不影响队列，类似于只读模式。
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-05
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        return this.linkedList.toString();
    }

    

    /*
    public static void main(String[] args)
    {
        Queue<String> v_Queue = new Queue<String>();
        
        for(int i = 0; i < 10; i++)
        {
            v_Queue.put(Integer.toString(i));
        }
        
        while( v_Queue.size() > 0 )
        {
            System.out.println(v_Queue.get());
        }
    }
    */
    
}
