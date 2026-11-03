package org.hy.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;





/**
 * 快照迭代器。支持多次退回的迭代器
 * 
 * 默认工作模式为：快照迭代器
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-11-02
 * @version     v1.0
 * @param <E>
 */
public class IteratorSnapshot<E> implements Iterator<E>
{
    
    /** 工作模式：真迭代器为true，快照迭代器为false */
    private boolean     mode;
    
    /** 原始迭代器 */
    private Iterator<E> iterator;
    
    /** 快照 */
    private List<E>     snapshot;
    
    
    
    public IteratorSnapshot(Iterator<E> i_Iterator)
    {
        if ( i_Iterator == null )
        {
            throw new NullPointerException("new IteratorSnapshot(Iterator is null)");
        }
        
        this.iterator = i_Iterator;
        this.snapshotMode();
    }
    
    

    /**
     * 注意：它只能被调用一次，工作在快照迭代器模式下，多次重复读取本方法会有问题 
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-11-02
     * @version     v1.0
     *
     * @return
     *
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public synchronized boolean hasNext()
    {
        boolean v_HasNext = this.iterator.hasNext();
        
        if ( this.mode )
        {
            return v_HasNext;
        }
        else
        {
            if ( !v_HasNext )
            {
                this.iterator = this.snapshot.iterator();
                // this.snapshot.clear();                 // 注意：此处不能清空之前的旧快照
                this.snapshot = new ArrayList<E>();      
            }
            return v_HasNext;
        }
    }

    
    
    @Override
    public synchronized E next()
    {
        if ( this.mode )
        {
            return this.iterator.next();
        }
        else
        {
            E v_Element = this.iterator.next();
            this.snapshot.add(v_Element);
            return v_Element;
        }
    }
    
    
    
    /**
     * 快照迭代器的工作模式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-02
     * @version     v1.0
     *
     */
    public void snapshotMode()
    {
        this.setMode(false);
    }
    
    
    
    /**
     * 真迭代器的工作模型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-02
     * @version     v1.0
     *
     */
    public void trueMode()
    {
        this.setMode(true);
    }


    
    /**
     * 获取：工作模式：真迭代器为true，快照迭代器为false
     */
    public boolean isMode()
    {
        return mode;
    }
    
    
    
    /**
     * 设置：工作模式：真迭代器为true，快照迭代器为false
     * 
     * @param i_Mode 工作模式：真迭代器为true，快照迭代器为false
     */
    public synchronized void setMode(boolean i_Mode)
    {
        this.mode = i_Mode;
        this.clearSnapshot();
        if ( !this.mode )
        {
            this.snapshot = new ArrayList<E>();
        }
    }
    
    
    
    /**
     * 清空快照
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-02
     * @version     v1.0
     *
     */
    private void clearSnapshot()
    {
        if ( this.snapshot != null )
        {
            // 注意：此处不能清空快照
            // this.snapshot.clear();
        }
        this.snapshot = null;
    }
    
}
