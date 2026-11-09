package org.hy.common;

import java.util.concurrent.ConcurrentHashMap;

import org.hy.common.Queue.QueueType;





/**
 * 数据按XID排队，当相同XID对应的数据多次发生变化时，排队顺序不变。 
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-11-08
 * @version     v1.0
 * @param <O>   数据的类型
 */
public class QueueMap<O> implements java.io.Serializable
{
    
    private static final long serialVersionUID = -2024806360583226204L;
    
    
    
    /** 数据XID对应的队列顺序 */
    private Queue<String>                xidQueue;
    
    /** 真正的数据对象集合 */
    private ConcurrentHashMap<String ,O> datas;
    
    
    
    public QueueMap()
    {
        this.xidQueue = new Queue<>(QueueType.$First_IN_First_OUT);
        this.datas    = new ConcurrentHashMap<>();
    }
    
    
    
    /**
     * 相同XID不用重复排队，保持原先的位置不变，但数据本体允许发生变化（即用最新的数据）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-08
     * @version     v1.0
     *
     * @param i_XID   数据的XID
     * @param i_Data  数据本体
     */
    public void put(String i_XID ,O i_Data)
    {
        if ( Help.isNull(i_XID) || i_Data == null )
        {
            throw new NullPointerException("XID or Object is null");
        }
        
        boolean v_IsQueue = false;
        synchronized (this)
        {
            O v_OldData = this.datas.get(i_XID);
            this.datas.put(i_XID ,i_Data);
            v_IsQueue = (v_OldData == null);
        }
        if ( v_IsQueue )
        {
            this.xidQueue.put(i_XID);
        }
    }
    
    
    
    /**
     * 获取队列中的元素。当队列为空时，返回null
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-08
     * @version     v1.0
     *
     * @return
     */
    public synchronized O get()
    {
        String v_XID = this.xidQueue.get();
        if ( v_XID != null )
        {
            return this.datas.remove(v_XID);
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 是否为空
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-08
     * @version     v1.0
     *
     * @return
     */
    public boolean isEmpty()
    {
        return this.xidQueue.isEmpty();
    }
    
    
    /**
     * 队列大小
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-08
     * @version     v1.0
     *
     * @return
     */
    public long size()
    {
        return this.xidQueue.size();
    }
    
    
    /**
     * 删除队列中对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-08
     * @version     v1.0
     *
     * @param i_XID   数据的XID
     * @return
     */
    public synchronized boolean remove(String i_XID)
    {
        if ( this.xidQueue.remove(i_XID) )
        {
            this.datas.remove(i_XID);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 清空队列中所有对象
     */
    public synchronized void clear()
    {
        this.xidQueue.clear();
        this.datas   .clear();
    }
    
    
    /**
     * 获取曾经进入过队列的对象次数
     * 
     * @return
     */
    public long getPutedCount()
    {
        return this.xidQueue.getPutedCount();
    }
    
    
    /**
     * 获取曾经出去过队列的对象次数
     * 
     * @return
     */
    public long getOutedCount()
    {
        return this.xidQueue.getOutedCount();
    }

}
