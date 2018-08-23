package org.hy.common;

import java.util.LinkedList;





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
 * @version  v1.1  2014-08-16  1.支持 "先进后出" 方式
 *                             2.新增队列类型
 *                             3.队列类型初始化不能修改，防止异常
 *           v1.2  2014-12-07  1.添加查看队列中的数据，并不影响队列，类似于只读模式。
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
    
    
    
	private LinkedList<O>    linkedList;
	
	/** 独立出大小是为性能与速度 */
	private long             queueSize;
	
	/** 曾经进入过队列的对象次数 */
	private long             putedCount;
	
	/** 队列类型 */
	private QueueType        queueType;
	
	
	
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
	    
		this.queueSize  = 0;
		this.putedCount = 0;
		this.linkedList = new LinkedList<O>();
		this.queueType  = i_QueueType;
	}
	
	
	public synchronized void put(O i_Object) 
	{
	    if ( QueueType.$First_IN_First_OUT == this.queueType )
	    {
	        this.linkedList.addFirst(i_Object);
	    }
	    else
	    {
	        this.linkedList.addLast(i_Object);
	    }
	    
		this.queueSize++;
		this.putedCount++;
	}
	
	
	/**
	 * 获取队列中的元素。当队列为空时，返回null
	 * 
	 * @return
	 */
	public synchronized O get() 
	{ 
		if ( this.queueSize <= 0 )
		{
			return null;
		}
		else
		{
			this.queueSize--;
			return this.linkedList.removeLast();
		}
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
	
	
	
	public synchronized boolean isEmpty() 
	{ 
	    return this.linkedList.isEmpty(); 
	}
	
	
	/**
	 * 比 isEmpty() 高效
	 * 
	 * @return
	 */
	public long size()
	{
		return this.queueSize;
	}
	
	
	/**
	 * 删除队列中对象
	 * 
	 * @param i_Object
	 */
	public synchronized void remove(O i_Object)
	{
		if ( this.linkedList.remove(i_Object) )
		{
    		this.queueSize--;
    		this.putedCount--;
		}
	}
	
	
	/**
	 * 清空队列中所有对象
	 */
	public synchronized void clear()
	{
		this.linkedList.clear();
		this.putedCount = this.putedCount - this.queueSize;
		this.queueSize  = 0;
	}
	
	
	/**
	 * 获取曾经进入过队列的对象次数
	 * 
	 * @return
	 */
	public long getPutedCount()
	{
		return this.putedCount;
	}
	
	
	/**
	 * 获取曾经出去过队列的对象次数
	 * 
	 * @return
	 */
	public long getOutedCount()
	{
		return this.putedCount - this.queueSize;
	}
	
	
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
