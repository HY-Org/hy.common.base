package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





/**
 * 互联互通Map -- 即可以像普通Map一样用Key找到Value。也可以用Value找到指向它的所有Key对象。
 * 
 * Map.Key 与 Map.Value 都不可为空。
 * 
 * Map.key不可重复，但Map.Value是允许重复的。
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2013-07-02
 */
public class InterconnectMap<K ,V> extends Hashtable<K ,V> implements Map<K ,V>
{
    private static final long serialVersionUID = -8192535025511638038L;
    
    
    private PartitionMap<V ,K> reverseMap;
    
    
    
    public InterconnectMap()
    {
        super();
        
        this.reverseMap = new TablePartition<V ,K>();
    }
    
    
    
    @Override
    public synchronized V put(K key ,V value) 
    {
        if ( key == null )
        {
            throw new java.lang.NullPointerException("Param Key is null.");
        }
        if ( value == null )
        {
            throw new java.lang.NullPointerException("Param Value is null.");
        }
        
        
        V v_Ret = null;
        
        if ( this.containsKey(key) )
        {
            V v_OldValue = super.remove(key);
            this.reverseMap.removeRow(v_OldValue ,key);
        }
        v_Ret = super.put(key, value);
        this.reverseMap.putRow(value ,key);
        
        return v_Ret;
    }
    
    
    
    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> t) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends V>> i = t.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    @Override
    public synchronized V remove(Object key) 
    {
        V v_Ret = super.remove(key);
        
        if ( v_Ret != null )
        {
            this.reverseMap.removeRow(v_Ret ,(K)key);
            
            return v_Ret;
        }
        else
        {
            return null;
        }
    }
    
    
    
    @Override
    public synchronized void clear() 
    {
        super.clear();
        
        try
        {
            this.reverseMap.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 反向获取。获取指向Value的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse(V value) 
    {
        return this.reverseMap.get(value);
    }
    
    
    
    /**
     * 获取所有Value，即TablePartition中的分区类型
     * 
     * @return
     */
    public Iterator<V> getValues()
    {
        return this.reverseMap.keySet().iterator();
    }
    
    
    
    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable 
    {
        this.clear();
        
        super.finalize();
    }
    */
    
}
