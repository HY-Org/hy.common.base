package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 互联互通Map的简单实现 -- 即可以像普通Map一样用Key找到Value。也可以用Value找到指向它的【惟一的Key对象】。
 * 
 * Map.Key 与 Map.Value 都不可为空。
 * 
 * Map.key不可重复，Map.Value不可重复。
 * 
 * 比 InterconnectMap 结构上简单一层。
 * 
 * 算法举例说明：
 *     put("A" ,123);  此时    super.size()=1，reverseMap.size()=1。 A->123，反向时 123->A
 *     put("B" ,123);  此时还保持super.size()=1，reverseMap.size()=1。B->123，反向时 123->B
 *     
 *     集合并不出现两个元素，错误的造成 A->NULL，B->123，反向时123->B
 *                    或错误的造成 A->123 ，B->123，反向时123->B
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2025-02-27
 */
public class KVKMap<K ,V> extends Hashtable<K ,V> implements Map<K ,V>
{
    
    private static final long serialVersionUID = -1684373809573585375L;
    
    
    
    private Hashtable<V ,K> reverseMap;
    
    
    
    public KVKMap()
    {
        super();
        
        this.reverseMap = new Hashtable<V ,K>();
    }
    
    
    
    @Override
    public synchronized V put(K i_Key ,V i_Value) 
    {
        if ( i_Key == null )
        {
            throw new java.lang.NullPointerException("Param Key is null.");
        }
        if ( i_Value == null )
        {
            throw new java.lang.NullPointerException("Param Value is null.");
        }
        
        K v_OldKey   = this.reverseMap.get(i_Value);
        V v_OldValue = super.get(i_Key);
        if ( v_OldValue != null )
        {
            K v_Old = this.reverseMap.remove(v_OldValue);
            if ( v_Old != null )
            {
                super.remove(v_Old);
            }
        }
        if ( v_OldKey != null )
        {
            V v_Old = super.remove(v_OldKey);
            if ( v_Old != null )
            {
                this.reverseMap.remove(v_Old);
            }
        }
        
        V v_Ret = super.put(i_Key, i_Value);
        this.reverseMap.put(i_Value ,i_Key);
        
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
    public synchronized V remove(Object i_Key) 
    {
        V v_Ret = super.remove(i_Key);
        if ( v_Ret != null )
        {
            this.reverseMap.remove(v_Ret ,(K)i_Key);
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
     * @param i_Value
     * @return
     */
    public synchronized K getReverse(V i_Value) 
    {
        return this.reverseMap.get(i_Value);
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
    
}
