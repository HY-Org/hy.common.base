package org.hy.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





/**
 * Map与List的融合体。即有关键字找值的功能，又有按编号找值的功能。
 * 
 * Map继承Hashtable，有同步功能。
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 *              v2.0  2014-10-17  添加isSafe属性
 *                                添加getKeys()方法
 *                                添加getKey(...)方法
 *              v3.0  2023-02-01  添加getValues()方法（有顺序的）
 * @createDate  2012-07-25
 */
public class ListMap<K ,V> extends Hashtable<K ,V> implements Map<K ,V>
{
    
    private static final long serialVersionUID = -5793511506137811113L;
    
    
    private List<K> keyList;
    
    /** 是否安全。主要指针 getKeys() 方法。默认是安全的，但性能略略略慢 */
    private boolean isSafe;
    
    
    
    public ListMap()
    {
        this(11 ,true);
    }
    
    
    
    public ListMap(int i_InitialCapacity)
    {
        this(i_InitialCapacity ,true);
    }
    
    
    
    public ListMap(boolean i_IsSafe)
    {
        this(11 ,i_IsSafe);
    }
    
    
    
    public ListMap(int i_InitialCapacity ,boolean i_IsSafe)
    {
        super(i_InitialCapacity);
        
        this.isSafe = i_IsSafe;
        
        this.keyList = new ArrayList<K>();
    }
    
    
    
    @Override
    public synchronized V put(K key ,V value)
    {
        V v_Ret = null;
        
        if ( this.containsKey(key) )
        {
            v_Ret = super.put(key, value);
        }
        else
        {
            v_Ret = super.put(key, value);
            this.keyList.add(key);
        }
        
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
    
    
    
    @Override
    public synchronized V remove(Object key)
    {
        V v_Ret = super.remove(key);
        
        if ( v_Ret != null )
        {
            try
            {
                this.keyList.remove(key);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
            return v_Ret;
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 按索引号删除元素
     * 
     * @param i_Index  索引号
     * @return
     */
    public synchronized V remove(int i_Index)
    {
        return this.remove(this.keyList.get(i_Index));
    }
    
    
    
    @Override
    public synchronized void clear()
    {
        super.clear();
        
        try
        {
            this.keyList.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 按索引号获取对象值
     * 
     * @param i_Index
     * @return
     */
    public synchronized V get(int i_Index)
    {
        if ( i_Index < 0 || i_Index >= this.keyList.size() )
        {
            throw new ArrayIndexOutOfBoundsException("Index[" + i_Index + "] is out of Bounds. Max size is " + this.keyList.size() + ".");
        }
        
        return this.get(this.keyList.get(i_Index));
    }
    
    
    
    /**
     * 按索引号获取关键字
     * 
     * @param i_Index
     * @return
     */
    public synchronized K getKey(int i_Index)
    {
        if ( i_Index < 0 || i_Index >= this.keyList.size() )
        {
            throw new ArrayIndexOutOfBoundsException("Index[" + i_Index + "] is out of Bounds. Max size is " + this.keyList.size() + ".");
        }
        
        return this.keyList.get(i_Index);
    }
    
    
    
    /**
     * 获取关键字所在的索引号
     * 
     * @param i_Key
     * @return
     */
    public synchronized int getIndex(K i_Key)
    {
        return this.keyList.indexOf(i_Key);
    }
    
    
    
    /**
     * 获取所有Key值
     * 
     * @return
     */
    public synchronized List<K> getKeys()
    {
        if ( this.isSafe )
        {
            if ( Help.isNull(this.keyList) )
            {
                return null;
            }
            
            List<K> v_Ret = new ArrayList<K>(this.keyList.size());
            
            v_Ret.addAll(this.keyList);
            
            return v_Ret;
        }
        else
        {
            return this.keyList;
        }
    }
    
    
    
    /**
     * 按排序获取所有Value值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-02-01
     * @version     v1.0
     *
     * @return
     */
    public synchronized List<V> getValues()
    {
        if ( Help.isNull(this.keyList) )
        {
            return null;
        }
        
        List<V> v_Ret = new ArrayList<V>(this.keyList.size());
        
        for (K v_Key : this.keyList)
        {
            v_Ret.add(this.get(v_Key));
        }
        
        return v_Ret;
    }
    
    
    
    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable
    {
        this.clear();
        
        this.keyList = null;
        
        super.finalize();
    }
    */
    
}
