package org.hy.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;





/**
 * Map.key有生存时间的Map，当生存时间期满时，Map.key就失效了。
 * 
 * 是 ExpireMap 的性能优化版本，并与其保持一样的功能、一样的方法，替代成本几乎为零。
 * 基本 google 的 Cache 类，并在它的基础上，支持定义每个key有自己的生存时间，包括永远不过期。
 * 同时保留 Cache 类统一的、默认的过期时长的功能。
 * 
 * 性能对比（小数据量时差别可忽略）
 * ------------------------------------------------------
 * 类别版本：2016 ExpireMap    2026 ExpireCache
 * 数据量级：1千万              1千万
 * 写入时长：0 00:20:44.050    0 00:18:15.190    下降：11.97%
 * 读取时长：0 00:16:50.644    0 00:16:50.403    下降： 0.02%
 * 总用时长：0 00:37:34.694    0 00:35:05.594    下降： 6.61%
 * 内存占用：129.8 MB          66.2 MB           下降：49.00%
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-02-11
 * @version     v1.0
 */
public class ExpireCache<K ,V> implements Map<K ,V> ,java.io.Serializable ,Cloneable
{
    private static final long serialVersionUID = -8450338749065213400L;
    
    

    /** 过期时间大于0毫秒的所有过期缓存对象的集合 */
    private Map<Long ,Cache<K ,ExpireValue>> caches;
    
    /** 过期时间为0毫秒，即永不过期的数据 */
    private Map<K ,ExpireValue>              cacheZero;
    
    /** 统一的、默认的过期时长（单位：毫秒） */
    private long                             expireMilli;
    
    /** 缓存最大容量，防止缓存占用过多内存 */
    private long                             maximumSize;
    
    
    
    public ExpireCache()
    {
        this.caches      = new ConcurrentHashMap<Long ,Cache<K,ExpireValue>>();
        this.cacheZero   = new ConcurrentHashMap<K ,ExpireValue>();
        this.expireMilli = 0L;
        this.maximumSize = 10000 * 10000L;
    }
    
    
    
    public ExpireCache(int i_InitialCapacity)
    {
        this.caches      = new ConcurrentHashMap<Long ,Cache<K,ExpireValue>>(i_InitialCapacity);
        this.cacheZero   = new ConcurrentHashMap<K ,ExpireValue>            (i_InitialCapacity);
        this.expireMilli = 0L;
        this.maximumSize = 10000 * 10000L;
    }
    
    
    
    public ExpireCache(int i_InitialCapacity, float i_LoadFactor)
    {
        this.caches      = new ConcurrentHashMap<Long ,Cache<K,ExpireValue>>(i_InitialCapacity ,i_LoadFactor);
        this.cacheZero   = new ConcurrentHashMap<K ,ExpireValue>            (i_InitialCapacity ,i_LoadFactor);
        this.expireMilli = 0L;
        this.maximumSize = 10000 * 10000L;
    }
    
    
    
    /**
     * 获取：统一的、默认的过期时长（单位：毫秒）
     */
    public long getExpireMilli()
    {
        return expireMilli;
    }


    
    /**
     * 设置：统一的、默认的过期时长（单位：毫秒）
     * 
     * @param i_ExpireMilli 统一的、默认的过期时长（单位：毫秒）
     */
    public ExpireCache<K ,V> setExpireMilli(long i_ExpireMilli)
    {
        this.expireMilli = Help.max(i_ExpireMilli ,0L);
        return this;
    }



    /**
     * 获取：缓存最大容量，防止缓存占用过多内存
     */
    public long getMaximumSize()
    {
        return maximumSize;
    }


    
    /**
     * 设置：缓存最大容量，防止缓存占用过多内存
     * 
     * @param i_MaximumSize 缓存最大容量，防止缓存占用过多内存
     */
    public ExpireCache<K ,V> setMaximumSize(long i_MaximumSize)
    {
        if ( i_MaximumSize < 0 )
        {
            throw new RuntimeException("MaximumSize is less than or equal to 0");
        }
        this.maximumSize = i_MaximumSize;
        return this;
    }
    
    
    
    /**
     * 获取指定过期时长的过期缓存对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_ExpireMilli  过期时长（单位：毫秒）
     * @return
     */
    private synchronized Cache<K ,ExpireValue> getCache(long i_ExpireMilli)
    {
        Cache<K ,ExpireValue> v_Cache = this.caches.get(i_ExpireMilli);
        if ( v_Cache == null )
        {
            v_Cache = this.createCache(i_ExpireMilli);
        }
        return v_Cache;
    }



    /**
     * 创建过期缓存对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_ExpireMilli  过期时长（单位：毫秒）
     */
    private Cache<K ,ExpireValue> createCache(long i_ExpireMilli)
    {
        Cache<K ,ExpireValue> v_NewCache = CacheBuilder.newBuilder()
                                                       .expireAfterWrite(Duration.ofMillis(i_ExpireMilli))
                                                       .maximumSize(this.maximumSize)
                                                       .build();
        this.caches.put(i_ExpireMilli ,v_NewCache);
        return v_NewCache;
    }
    
    
    
    /**
     * 主键获取数值及过期时间
     * 
     * 注：此方法为内部使用，不再独立用 synchronized 加锁，请在调用端使用
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return
     */
    private ExpireTimeKeyValue getTKV(K i_Key)
    {
        if ( !Help.isNull(this.caches) )
        {
            for (Map.Entry<Long, Cache<K ,ExpireValue>> v_Item : this.caches.entrySet())
            {
                ExpireValue v_EValue = v_Item.getValue().getIfPresent(i_Key);
                if ( v_EValue != null )
                {
                    return new ExpireTimeKeyValue(v_Item.getKey() ,i_Key ,v_EValue);
                }
            }
        }
        
        ExpireValue v_EValue = this.cacheZero.get(i_Key);
        if ( v_EValue != null )
        {
            return new ExpireTimeKeyValue(0L ,i_Key ,v_EValue);
        }
        
        return null;
    }
    
    
    
    /**
     * 删除指定过期时长的主键数据
     * 
     * 注：此方法为内部使用，不再独立用 synchronized 加锁，请在调用端使用
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_ExpireMilli  过期时长（单位：毫秒）
     * @param i_Key          主键
     */
    private void remove(long i_ExpireMilli ,K i_Key)
    {
        if ( i_ExpireMilli <= 0 )
        {
            this.cacheZero.remove(i_Key);
        }
        else
        {
            this.caches.get(i_ExpireMilli).invalidate(i_Key);
        }
    }
    
    
    
    /**
     * 添加数据
     * 
     * 注：此方法为内部使用，不再独立用 synchronized 加锁，请在调用端使用
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_ExpireMilli  过期时长（单位：毫秒）
     * @param i_Key          主键
     * @param i_Value        数值
     * @return               带创建时间的数值
     */
    private ExpireValue put(long i_ExpireMilli ,K i_Key ,V i_Value)
    {
        return put(i_ExpireMilli ,i_Key ,i_Value ,new Date());
    }
    
    
    
    /**
     * 添加数据
     * 
     * 注：此方法为内部使用，不再独立用 synchronized 加锁，请在调用端使用
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_ExpireMilli  过期时长（单位：毫秒）
     * @param i_Key          主键
     * @param i_Value        数值
     * @param i_CreateTime   创建时间(首次将Key添加到集合时的时间)
     * @return               带创建时间的数值
     */
    private ExpireValue put(long i_ExpireMilli ,K i_Key ,V i_Value ,Date i_CreateTime)
    {
        ExpireValue v_EValue = new ExpireValue(i_Value ,i_CreateTime);
        if ( i_ExpireMilli <= 0 )
        {
            this.cacheZero.put(i_Key ,v_EValue);
        }
        else
        {
            this.getCache(i_ExpireMilli).put(i_Key ,v_EValue);
        }
        return v_EValue;
    }
    
    
    
    /**
     * 设置Map.key的过期时长（单位：秒）
     * 
     * 如果Map.key已过期，将无法设置。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key      主键
     * @param i_Second   过期时长（单位：秒）
     * @return
     */
    public V setExpireTime(K i_Key ,long i_Second)
    {
        return this.setExpireTimeMilli(i_Key ,i_Second * 1000L);
    }
    
    
    
    /**
     * 设置Map.key的过期时长（单位：毫秒）
     * 
     * 如果Map.key已过期，将无法设置。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key           主键
     * @param i_Millisecond   过期时长（单位：毫秒）
     * @return                当Map.key已经过期时，返回 null
     */
    public V setExpireTimeMilli(K i_Key ,long i_Millisecond)
    {
        return this.getAndKeepMilli(i_Key ,i_Millisecond);
    }
    
    
    
    /**
     * 添加元素，带有过期时长功能（单位：秒）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key     主键
     * @param i_Value   数值
     * @param i_Second  过期时长(单位：秒)。指当前时刻过i_Second秒后过期失效。
     *                  小于 0 时，表示永远不失效。
     * @return
     */
    public Expire<K ,V> put(K i_Key ,V i_Value ,long i_Second)
    {
        return this.putMilli(i_Key ,i_Value ,i_Second * 1000L);
    }
    
    
    
    /**
     * 添加元素，带有过期时长功能（单位：毫秒）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key          主键
     * @param i_Value        数值
     * @param i_Millisecond  过期时长(单位：毫秒)。指当前时刻过i_Millisecond毫秒后过期失效。
     *                       小于等于 0 时，表示永远不失效。
     * @return
     */
    public synchronized Expire<K ,V> putMilli(K i_Key ,V i_Value ,long i_Millisecond)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV(i_Key);
        if ( v_TKV != null )
        {
            this.remove(v_TKV.getExpireMilli() ,i_Key);
        }
        ExpireValue v_EValue = this.put(i_Millisecond ,i_Key ,i_Value);
        return new ExpireTimeKeyValue(i_Millisecond ,i_Key ,v_EValue);
    }
    
    
    
    /**
     * 添加元素，带有创建时间和过期的时间戳
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key          主键
     * @param i_Value        数值
     * @param i_CreateTime   创建时间
     * @param i_ExpireTime   过期的时间戳，与 getExpireTimeLen() 方法返回值同义
     * @return
     */
    public synchronized Expire<K ,V> putMilli(K i_Key ,V i_Value ,Date i_CreateTime ,long i_ExpireTime)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV(i_Key);
        if ( v_TKV != null )
        {
            this.remove(v_TKV.getExpireMilli() ,i_Key);
        }
        
        long v_Millisecond = this.expireMilli;
        if ( i_ExpireTime > 0L )
        {
            v_Millisecond = i_ExpireTime - i_CreateTime.getTime();
            if ( v_Millisecond <= 0L )
            {
                v_Millisecond = this.expireMilli;
            }
        }
        ExpireValue v_EValue = this.put(v_Millisecond ,i_Key ,i_Value ,i_CreateTime);
        return new ExpireTimeKeyValue(v_Millisecond ,i_Key ,v_EValue);
    }
    
    
    
    /**
     * 批量添加元素，带有过期时长功能
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Datas  过期集合数据
     */
    public synchronized void putAll(ExpireCache<K ,V> i_Datas)
    {
        if ( !Help.isNull(i_Datas.caches) )
        {
            for (Map.Entry<Long ,Cache<K ,ExpireValue>> v_Item : i_Datas.caches.entrySet())
            {
                Cache<K ,ExpireValue> v_Cache = v_Item.getValue();
                if ( v_Cache.size() >= 1 )
                {
                    Cache<K ,ExpireValue>         v_MyCache    = this.getCache(v_Item.getKey());
                    ConcurrentMap<K, ExpireValue> v_CacheAsMap = v_Cache.asMap();
                    for (Map.Entry<K, ExpireValue> v_ItemChild : v_CacheAsMap.entrySet())
                    {
                        v_MyCache.put(v_ItemChild.getKey() ,v_ItemChild.getValue());
                    }
                }
            }
        }
        
        if ( !Help.isNull(i_Datas.cacheZero) )
        {
            this.cacheZero.putAll(i_Datas.cacheZero);
        }
    }
    
    
    
    /**
     * 添加元素，按统一的、默认的过期时长（单位：毫秒）
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key    主键
     * @param i_Value  数值
     * @return
     */
    @Override
    public V put(K i_Key ,V i_Value)
    {
        return this.putMilli(i_Key ,i_Value ,this.expireMilli).getValue();
    }
    
    
    
    /**
     * 批量添加元素，永远有效的元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Datas  过期集合数据
     */
    @Override
    public synchronized void putAll(Map<? extends K ,? extends V> i_Datas)
    {
        if ( Help.isNull(i_Datas) )
        {
            return;
        }
        
        for (Map.Entry<? extends K ,? extends V> v_Data : i_Datas.entrySet())
        {
            this.putMilli(v_Data.getKey() ,v_Data.getValue() ,this.expireMilli);
        }
    }
    
    
    
    /**
     * 获取过期的时间戳。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return       已过期的Map.key，返回 null
     *               返回 0 时，表示永远有效
     */
    public synchronized Long getExpireTime(K i_Key)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV(i_Key);
        
        if ( null != v_TKV )
        {
            return v_TKV.getTime();
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取剩余有效时间的时长（单位：毫秒）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return       已过期的Map.key，返回 -1
     *               不存在的Map.key，返回 -1
     *               永远有效Map.key，返回 Long.MAX_VALUE
     */
    public synchronized long getExpireTimeLen(K i_Key)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV(i_Key);
        
        if ( null != v_TKV )
        {
            if ( v_TKV.getExpireMilli() == 0L )
            {
                return Long.MAX_VALUE;
            }
            else
            {
                return v_TKV.getExpireMilli();
            }
        }
        else
        {
            return -1L;
        }
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 返回 null
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized V get(Object i_Key)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV((K) i_Key);
        
        if ( null != v_TKV )
        {
            return v_TKV.getValue();
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 返回 null
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized Expire<K ,V> getExpire(Object i_Key)
    {
        return this.getTKV((K) i_Key);
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 返回 null
     * 
     * 当Map.key在有效期内时，可重新设备过期时长（单位：秒）。
     * 
     * 类似于 get() + setExpireTime() 两方法的组合，但性能更高。
     * 
     * 此方法可用于实现Session机制。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key     主键
     * @param i_Second  过期时长(单位：秒)。指当前时刻过i_Second秒后过期失效。
     * @return
     */
    public V getAndKeep(K i_Key ,long i_Second)
    {
        return this.getAndKeepMilli(i_Key ,i_Second * 1000L);
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 返回 null
     * 
     * 当Map.key在有效期内时，可重新设备过期时长（单位：毫秒）。
     * 
     * 类似于 get() + setExpireTime() 两方法的组合，但性能更高。
     * 
     * 此方法可用于实现Session机制。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key          主键
     * @param i_Millisecond  过期时长(单位：毫秒)。指当前时刻过i_Millisecond毫秒后过期失效。
     * @return
     */
    public synchronized V getAndKeepMilli(K i_Key ,long i_Millisecond)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV(i_Key);
        
        if ( null != v_TKV )
        {
            this.put(i_Millisecond ,v_TKV.getKey() ,v_TKV.getValue());
            return v_TKV.getValue();
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value 的创建时间。
     * 过期的 Map.key 返回 null
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return
     */
    public synchronized Date getCreateTime(K i_Key)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV(i_Key);
        
        if ( null != v_TKV )
        {
            return v_TKV.getCreateTime();
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized int size()
    {
        long v_Size = 0;
        if ( !Help.isNull(this.caches) )
        {
            for (Cache<K ,ExpireValue> v_Item : this.caches.values())
            {
                v_Item.cleanUp();
                v_Size += v_Item.size();
            }
        }
        
        v_Size += this.cacheZero.size();
        return (int) v_Size;
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized boolean isEmpty()
    {
        if ( !Help.isNull(this.caches) )
        {
            for (Cache<K ,ExpireValue> v_Item : this.caches.values())
            {
                v_Item.cleanUp();
                if ( v_Item.size() >= 1 )
                {
                    return false;
                }
            }
        }
        
        return Help.isNull(this.cacheZero);
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized Set<K> keySet()
    {
        Set<K> v_Keys = new HashSet<K>();
        if ( !Help.isNull(this.caches) )
        {
            for (Cache<K ,ExpireValue> v_Item : this.caches.values())
            {
                if ( v_Item.size() >= 1 )
                {
                    v_Keys.addAll(v_Item.asMap().keySet());
                }
            }
        }
        
        if ( !Help.isNull(this.cacheZero) )
        {
            v_Keys.addAll(this.cacheZero.keySet());
        }
        
        return v_Keys;
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized Collection<V> values()
    {
        List<V> v_Values = new ArrayList<V>();
        if ( !Help.isNull(this.caches) )
        {
            for (Cache<K ,ExpireValue> v_Item : this.caches.values())
            {
                if ( v_Item.size() >= 1 )
                {
                    Collection<ExpireValue> v_ChildItem = v_Item.asMap().values();
                    for (ExpireValue v_EValue : v_ChildItem)
                    {
                        v_Values.add(v_EValue.getValue());
                    }
                }
            }
        }
        
        if ( !Help.isNull(this.cacheZero) )
        {
            for (ExpireValue v_EValue : this.cacheZero.values())
            {
                v_Values.add(v_EValue.getValue());
            }
        }
        
        return v_Values;
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized Set<Map.Entry<K, V>> entrySet()
    {
        Set<Map.Entry<K, V>> v_Datas = new HashSet<Map.Entry<K, V>>();
        if ( !Help.isNull(this.caches) )
        {
            for (Map.Entry<Long, Cache<K ,ExpireValue>> v_Item : this.caches.entrySet())
            {
                if ( v_Item.getValue().size() >= 1 )
                {
                    Set<Map.Entry<K, ExpireValue>> v_ChildItems = v_Item.getValue().asMap().entrySet();
                    for (Map.Entry<K, ExpireValue> v_ChildItem : v_ChildItems)
                    {
                        v_Datas.add(new ExpireTimeKeyValue(v_Item.getKey() ,v_ChildItem.getKey() ,v_ChildItem.getValue()));
                    }
                }
            }
        }
        
        if ( !Help.isNull(this.cacheZero) )
        {
            for (Map.Entry<K, ExpireValue> v_Item : this.cacheZero.entrySet())
            {
                v_Datas.add(new ExpireTimeKeyValue(0L ,v_Item.getKey() ,v_Item.getValue()));
            }
        }
        
        return v_Datas;
    }
    
    
    
    /**
     * 只有当 Map.key 在有效期内时，才返回 true。
     * 过期的 Map.key 返回 false
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return
     */
    @Override
    public synchronized boolean containsKey(Object i_Key)
    {
        if ( !Help.isNull(this.caches) )
        {
            for (Map.Entry<Long, Cache<K ,ExpireValue>> v_Item : this.caches.entrySet())
            {
                ExpireValue v_EValue = v_Item.getValue().getIfPresent(i_Key);
                if ( v_EValue != null )
                {
                    return true;
                }
            }
        }
        
        return this.cacheZero.containsKey(i_Key);
    }
    
    
    
    /**
     * 只有当 Map.key 在有效期内时，才返回 true。
     * 过期的 Map.key 返回 false
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     *
     * @param i_Key  主键
     * @return
     */
    @Override
    public boolean containsValue(Object i_Value)
    {
        // 暂时不支持
        return false;
    }
    
    
    
    /**
     * 删除元素
     *
     * @param i_Key  主键
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized V remove(Object i_Key)
    {
        ExpireTimeKeyValue v_TKV = this.getTKV((K) i_Key);
        if ( v_TKV == null )
        {
            return null;
        }
        
        this.remove(v_TKV.getExpireMilli() ,v_TKV.getKey());
        return v_TKV.getValue();
    }
    
    
    
    /**
     * 清空集合
     */
    @Override
    public synchronized void clear()
    {
        if ( !Help.isNull(this.caches) )
        {
            for (Cache<K ,ExpireValue> v_Item : this.caches.values())
            {
                v_Item.invalidateAll();
            }
        }
        
        this.cacheZero.clear();
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object i_Other)
    {
        if (i_Other == null)
        {
            return false;
        }
        else if (this == i_Other)
        {
            return true;
        }
        else if (i_Other instanceof ExpireCache)
        {
            ExpireCache<K ,V> v_Another = (ExpireCache<K ,V>)i_Other;
            return this.caches.equals(v_Another.caches) && this.cacheZero.equals(v_Another.cacheZero);
        }
        else
        {
            return false;
        }
    }
    
    
    
    @Override
    public int hashCode()
    {
        return this.caches.hashCode() + this.cacheZero.hashCode();
    }
    
    
    
    @Override
    public synchronized ExpireCache<K ,V> clone()
    {
        ExpireCache<K ,V> v_Clone = new ExpireCache<K ,V>();
        v_Clone.setMaximumSize(this.getMaximumSize());
        v_Clone.setExpireMilli(this.getExpireMilli());
        v_Clone.putAll(this);
        return v_Clone;
    }
    
    
    
    
    
    /**
     * 带创建时间的数值。真正保存在Cache缓存对象中的值对象
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     */
    class ExpireValue
    {
        /** 创建时间(首次将Key添加到集合时的时间) */
        private Date createTime;
        
        /** 数值 */
        private V    value;
        
        
        public ExpireValue(V i_Value)
        {
            this(i_Value ,new Date());
        }
        
        
        public ExpireValue(V i_Value ,Date i_CreateTime)
        {
            this.value      = i_Value;
            this.createTime = i_CreateTime;
        }

        
        /**
         * 获取：创建时间(首次将Key添加到集合时的时间)
         */
        public Date getCreateTime()
        {
            return createTime;
        }
        
        /**
         * 获取：数值
         */
        public V getValue()
        {
            return value;
        }
        
    }
    
    
    
    
    
    /**
     * 过期时长、主键与数值的包装类
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-11
     * @version     v1.0
     */
    class ExpireTimeKeyValue implements Expire<K ,V>
    {
        private static final long serialVersionUID = -2404946095058070032L;

        /** 统一的、默认的过期时长（单位：毫秒） */
        private long        expireMilli;
        
        /** 主键值 */
        private K           key;
        
        /** 数值 */
        private ExpireValue value;
        
        
        public ExpireTimeKeyValue(long i_ExpireMilli ,K i_Key ,ExpireValue i_Value)
        {
            this.expireMilli = i_ExpireMilli;
            this.key         = i_Key;
            this.value       = i_Value;
        }

        /**
         * 获取：主键值
         */
        public K getKey()
        {
            return key;
        }

        /**
         * 获取：数值
         */
        public V getValue()
        {
            return value.getValue();
        }

        @Override
        public V setValue(V value)
        {
            return null;
        }
        
        /**
         * 获取：统一的、默认的过期时长（单位：毫秒）
         */
        public long getExpireMilli()
        {
            return expireMilli;
        }

        /**
         * 获取：时间戳。保存期满时间。小于等于0表示永远有效
         */
        @Override
        public long getTime()
        {
            if ( this.expireMilli <= 0 )
            {
                return 0L;
            }
            else
            {
                return this.value.getCreateTime().getTime() + this.expireMilli;
            }
        }

        /**
         * 创建时间(首次将Key添加到集合时的时间)
         */
        @Override
        public Date getCreateTime()
        {
            return this.value.getCreateTime();
        }
        
    }
    
}
