package org.hy.common;

import java.util.Iterator;





/**
 * 只读的迭代器 
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-11-08
 * @version     v1.0
 */
public class IteratorReadOnly<O> implements Iterator<O>
{
    
    /** 原始的迭代器对象 */
    private final Iterator<O> orgIterator;
    
    
    
    public IteratorReadOnly(Iterator<O> i_Iterator)
    {
        this.orgIterator = i_Iterator;
    }

    
    @Override
    public boolean hasNext()
    {
        return this.orgIterator.hasNext();
    }

    
    @Override
    public O next()
    {
        return this.orgIterator.next();
    }

    
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("read-only iterator.");
    }
    
}
