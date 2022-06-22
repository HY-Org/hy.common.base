package org.hy.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;





/**
 * Map.key有生存时间的Map，当生存时间期满时，Map.key就失效了。
 * 
 * 生存时间的精度为：毫秒
 * 
 * 注意：当系统时刻与生存时间（过期时间）相等时，视为有效的Map.key。
 *      只有超过生存时间（过期时间）时，才视为失效的Map.key。
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-02-24
 * @version     v1.0
 *              v2.0  2017-02-28  添加：元素首次加入集合的创建时间
 *              v3.0  2017-11-20  添加：getAndKeep()方法，可实现Session机制。
 *              v4.0  2022-06-22  添加：getExpire()方法，一个方法即可返回所有参数信息，使用它得到多组信息时，性能更高
 *                                添加：entrySetExpire()方法，为本类转Json 或通过Json序列化提供支持
 */
public class ExpireMap<K ,V> implements Map<K ,V> ,java.io.Serializable ,Cloneable
{
    private static final long serialVersionUID = 1240278758504986975L;
    
    
    /** 真实的数据 */
    private ConcurrentHashMap<K ,Expire<K ,V>> datas;
    
    /**
     * 一样的数据，不同的结构。
     * 主要用于 ExpireMap.values() 和 ExpireMap.entrySet() 两个方法。
     * 注意：ExpireMap并没有直接继承 Hashtable<K ,V>，而是将 Hashtable<K ,V>作为内部属性。
     */
    private ConcurrentHashMap<K ,V>                   datasSame;
    
    /**
     * 最小的过期时间戳。
     * 
     * 它的主要作用是优化检查过期失效的性能
     *   1. 0值 不计算在内。
     *   2. 但初始化值为 0值。
     */
    private long                                      minTime;
    
    
    
    public ExpireMap()
    {
        this.datas     = new ConcurrentHashMap<K ,Expire<K ,V>>();
        this.datasSame = new ConcurrentHashMap<K ,V>();
        this.minTime   = 0L;
    }
    
    
    
    public ExpireMap(int i_InitialCapacity)
    {
        this.datas     = new ConcurrentHashMap<K ,Expire<K ,V>>(i_InitialCapacity);
        this.datasSame = new ConcurrentHashMap<K ,V>                  (i_InitialCapacity);
        this.minTime   = 0L;
    }
    
    
    
    public ExpireMap(int i_InitialCapacity, float i_LoadFactor)
    {
        this.datas     = new ConcurrentHashMap<K ,Expire<K ,V>>(i_InitialCapacity ,i_LoadFactor);
        this.datasSame = new ConcurrentHashMap<K ,V>           (i_InitialCapacity ,i_LoadFactor);
        this.minTime   = 0L;
    }
    
    
    
    /**
     * 检查所有元素是否过期失效
     * 
     * 当过期失效时，内部会消积删除。
     * 并重新计算最小的过期时间戳
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     */
    private synchronized Map<K ,Expire<K ,V>> checkExpires()
    {
        if ( this.datas.isEmpty() )
        {
            this.minTime = 0L;
        }
        else
        {
            if ( 0L < this.minTime && this.minTime < Date.getNowTime().getTime() )
            {
                long v_MinTime = 0L;
                
                for (Expire<K ,V> v_Data : this.datas.values())
                {
                    if ( null != ((ExpireElement<K ,V>)v_Data).checkExpire(false) )
                    {
                        if ( v_MinTime == 0L || v_Data.getTime() < v_MinTime )
                        {
                            v_MinTime = v_Data.getTime();
                        }
                    }
                }
                
                // 注意：上面的循环，绝对不能使用 this.minTime。
                //      否则逻辑异常，并有可能引发死循环的递归检查。
                this.minTime = v_MinTime;
            }
        }
        
        return this.datas;
    }
    
    
    
    /**
     * 设置Map.key的过期时长（单位：秒）
     * 
     * 如果Map.key已过期，将无法设置。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-25
     * @version     v1.0
     *
     * @param i_Key
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
     * @createDate  2016-02-25
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Millisecond   过期时长（单位：毫秒）
     * @return                当Map.key已经过期时，返回 null
     */
    public synchronized V setExpireTimeMilli(K i_Key ,long i_Millisecond)
    {
        Expire<K ,V> v_Data = this.datas.get(i_Key);
        
        if ( null == v_Data )
        {
            return null;
        }
        else if ( ((ExpireElement<K ,V>)v_Data).checkExpire() == null )
        {
            return null;
        }
        
        long v_Time = Date.getNowTime().getTime() + i_Millisecond;
        ((ExpireElement<K ,V>)v_Data).time = v_Time;
        if ( this.minTime == 0L || v_Time < this.minTime )
        {
            this.minTime = v_Time;
        }
        
        return v_Data.getValue();
    }
    
    
    
    /**
     * 添加元素，带有过期时长功能（单位：秒）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value
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
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value
     * @param i_Millisecond  过期时长(单位：毫秒)。指当前时刻过i_Millisecond毫秒后过期失效。
     *                       小于 0 时，表示永远不失效。
     * @return
     */
    public synchronized Expire<K ,V> putMilli(K i_Key ,V i_Value ,long i_Millisecond)
    {
        ExpireElement<K ,V> v_OldData = (ExpireElement<K ,V>)this.datas.get(i_Key);
        ExpireElement<K ,V> v_NewData = null;
        
        if ( v_OldData != null )
        {
            v_NewData = v_OldData;
            v_NewData.setValue(i_Value);
        }
        else
        {
            v_NewData = new ExpireElement<K ,V>(i_Key ,i_Value);
            this.datas.put(i_Key ,v_NewData);
        }
        
        this.datasSame.put(i_Key ,i_Value);
        
        if ( i_Millisecond > 0L )
        {
            long v_Time = Date.getNowTime().getTime() + i_Millisecond;
            v_NewData.time = v_Time;
            
            if ( this.minTime == 0L || v_Time < this.minTime )
            {
                this.minTime = v_Time;
            }
        }
        
        return v_NewData;
    }
    
    
    
    /**
     * 批量添加元素，带有过期时长功能
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Datas
     */
    public synchronized void putAll(ExpireMap<K ,V> i_Datas)
    {
        if ( Help.isNull(i_Datas) )
        {
            return;
        }
        
        this.datas    .putAll(i_Datas.datas);
        this.datasSame.putAll(i_Datas.datasSame);
        
        if ( this.minTime == 0L )
        {
            this.minTime = i_Datas.minTime;
        }
        else if ( i_Datas.minTime == 0L )
        {
            // Nothing.
        }
        else if ( i_Datas.minTime < this.minTime )
        {
            this.minTime = i_Datas.minTime;
        }
    }
    
    
    
    /**
     * 添加元素，永远有效的元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value
     * @return
     */
    @Override
    public V put(K i_Key ,V i_Value)
    {
        return this.putMilli(i_Key ,i_Value ,0L).getValue();
    }
    
    
    
    /**
     * 批量添加元素，永远有效的元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Datas
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
            this.putMilli(v_Data.getKey() ,v_Data.getValue() ,0L);
        }
    }
    
    
    
    /**
     * 获取过期的时间戳。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-25
     * @version     v1.0
     *
     * @param i_Key
     * @return       已过期的Map.key，返回 null
     *               返回 0 时，表示永远有效
     */
    public synchronized Long getExpireTime(K i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            return v_Data.checkExpire() == null ? null : v_Data.getTime();
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
     * @createDate  2016-02-25
     * @version     v1.0
     *
     * @param i_Key
     * @return       已过期的Map.key，返回 -1
     *               不存在的Map.key，返回 -1
     *               永远有效Map.key，返回 Long.MAX_VALUE
     */
    public synchronized long getExpireTimeLen(K i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            if ( null == v_Data.checkExpire() )
            {
                return -1L;
            }
            else if ( v_Data.time == 0L )
            {
                return Long.MAX_VALUE;
            }
            else
            {
                return v_Data.time - Date.getNowTime().getTime();
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
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    @Override
    public synchronized V get(Object i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            return v_Data.checkExpire() == null ? null : v_Data.getValue();
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
     * @createDate  2022-06-22
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    public synchronized Expire<K ,V> getExpire(Object i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            return v_Data.checkExpire() == null ? null : v_Data;
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
     * 当Map.key在有效期内时，可重新设备过期时长（单位：秒）。
     * 
     * 类似于 get() + setExpireTime() 两方法的组合，但性能更高。
     * 
     * 此方法可用于实现Session机制。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-20
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Second
     * @return
     */
    public V getAndKeep(Object i_Key ,long i_Second)
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
     * @createDate  2017-11-20
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Second
     * @return
     */
    public synchronized V getAndKeepMilli(Object i_Key ,long i_Millisecond)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            if ( v_Data.checkExpire() == null )
            {
                return null;
            }
            else
            {
                long v_Time = Date.getNowTime().getTime() + i_Millisecond;
                v_Data.time = v_Time;
                if ( this.minTime == 0L || v_Time < this.minTime )
                {
                    this.minTime = v_Time;
                }
                
                return v_Data.getValue();
            }
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
     * @createDate  2017-02-28
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    public synchronized Date getCreateTime(Object i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            return v_Data.checkExpire() == null ? null : v_Data.getCreateTime();
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
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized int size()
    {
        return this.checkExpires().size();
    }


    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized boolean isEmpty()
    {
        return this.checkExpires().isEmpty();
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized Set<K> keySet()
    {
        return this.checkExpires().keySet();
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-25
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized Collection<V> values()
    {
        this.checkExpires();
        return this.datasSame.values();
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-25
     * @version     v1.0
     *
     * @return
     */
    @Override
    public synchronized Set<Map.Entry<K, V>> entrySet()
    {
        this.checkExpires();
        return this.datasSame.entrySet();
    }
    
    
    
    /**
     * 只返回 Map.key 在有效期内的 Map.value。
     * 过期的 Map.key 不在返回之内
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-22
     * @version     v1.0
     *
     * @return
     */
    public synchronized Set<Entry<K ,Expire<K ,V>>> entrySetExpire()
    {
        this.checkExpires();
        return this.datas.entrySet();
    }
    
    
    
    /**
     * 只有当 Map.key 在有效期内时，才返回 true。
     * 过期的 Map.key 返回 false
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    @Override
    public synchronized boolean containsKey(Object i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.get(i_Key);
        
        if ( null != v_Data )
        {
            return v_Data.checkExpire() != null;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 只有当 Map.key 在有效期内时，才返回 true。
     * 过期的 Map.key 返回 false
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Key
     * @return
     */
    @Override
    public synchronized boolean containsValue(Object i_Value)
    {
        if ( null == i_Value )
        {
            throw new NullPointerException();
        }

        return this.datas.containsValue(i_Value);
    }
    
    
    
    /**
     * 删除元素
     *
     * @param i_Key
     * @return
     */
    @Override
    public synchronized V remove(Object i_Key)
    {
        ExpireElement<K ,V> v_Data = (ExpireElement<K ,V>)this.datas.remove(i_Key);
        this.datasSame.remove(i_Key);
        
        if ( null != v_Data )
        {
            if ( null == v_Data.checkExpire() )
            {
                return null;
            }
            else
            {
                this.checkExpires();
                return v_Data.getValue();
            }
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 清空集合
     */
    @Override
    public synchronized void clear()
    {
        this.datas    .clear();
        this.datasSame.clear();
        this.minTime = 0;
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
        else if (i_Other instanceof ExpireMap)
        {
            ExpireMap<K ,V> v_Another = (ExpireMap<K ,V>)i_Other;
            
            return this.datas.equals(v_Another.datas);
        }
        else
        {
            return false;
        }
    }

    
    
    @Override
    public int hashCode()
    {
        return this.datas.hashCode();
    }

    

    @Override
    public synchronized ExpireMap<K ,V> clone()
    {
        ExpireMap<K ,V> v_Clone = new ExpireMap<K ,V>(this.size());
        
        for (Expire<K ,V> v_Data : this.datas.values())
        {
            v_Clone.put(v_Data.getKey() ,v_Data.getValue() ,v_Data.getTime());
        }
        
        return v_Clone;
    }



    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable
    {
        this.clear();
        this.datas     = null;
        this.datasSame = null;
        super.finalize();
    }
    */
    
    
    
    
    
    /**
     * 定义此接口后，外界才能有限制的访问。如访问 ExpireMap.put( , , ) 的返回值。
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-25
     * @version     v1.0
     *              v2.0  2017-02-28 添加：创建时间
     */
    public interface Expire<K ,V> extends Map.Entry<K ,V> ,java.io.Serializable
    {
        
        /**
         * 获取：时间戳。保存期满时间。0表示永远有效
         */
        public long getTime();
        
        
        
        /**
         * 创建时间(首次将Key添加到集合时的时间)
         * 
         * @author      ZhengWei(HY)
         * @createDate  2017-02-28
         * @version     v1.0
         *
         * @return
         */
        public Date getCreateTime();
        
    }
    
    
    
    
    
    /**
     * ExpireMap集合的元素类，即Map.value的类型 。
     * 
     * 是对外界用户Map.value的封装。所以，比较、相等判断都按外界用户Map.value的为准。
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *              v2.0  2017-02-28 添加：创建时间
     * @param <EK>  它是ExpireMap的<K>，只是Java编码时，无法重复定义，所以写成了<EK>，其实就是同一个对象。
     * @param <EV>  它是ExpireMap的<V>，只是Java编码时，无法重复定义，所以写成了<EV>，其实就是同一个对象。
     */
    class ExpireElement<EK ,EV> implements Expire<EK ,EV> ,Cloneable
    {
        private static final long serialVersionUID = -7996663565815856686L;

        private EK                             key;
        
        private EV                             value;
        
        /** 时间戳。保存期满时间。0表示永远有效 */
        private long                           time;
        
        /** 创建时间(首次将Key添加到集合时的时间) */
        private Date                           createTime;
        
        
        
        public ExpireElement(EK i_Key ,EV i_Value)
        {
            this(i_Key ,i_Value ,0L ,new Date());
        }
        
        
        
        public ExpireElement(EK i_Key ,EV i_Value ,long i_Time)
        {
            this(i_Key ,i_Value ,i_Time ,new Date());
        }
        
        
        
        public ExpireElement(EK i_Key ,EV i_Value ,Date i_CreateTime)
        {
            this(i_Key ,i_Value ,0L ,new Date());
        }
        
        
        
        public ExpireElement(EK i_Key ,EV i_Value ,long i_Time ,Date i_CreateTime)
        {
            this.key        = i_Key;
            this.value      = i_Value;
            this.time       = i_Time;
            this.createTime = i_CreateTime;
        }
        
        
        
        /**
         * 检查是否过期失效（会有可能引发递归检查）
         * 
         * 当过期失效时，内部会消积删除
         * 
         * @author      ZhengWei(HY)
         * @createDate  2016-02-24
         * @version     v1.0
         *
         * @return
         */
        private ExpireElement<EK ,EV> checkExpire()
        {
            return this.checkExpire(true);
        }
        
        
        
        /**
         * 检查是否过期失效
         * 
         * 当过期失效时，内部会消积删除
         * 
         * @author      ZhengWei(HY)
         * @createDate  2016-02-24
         * @version     v1.0
         *
         * @param i_Recursive  是否引发递归检查
         * @return
         */
        private ExpireElement<EK ,EV> checkExpire(boolean i_Recursive)
        {
            if ( this.time > 0L )
            {
                if ( Date.getNowTime().getTime() <= this.time )
                {
                    return this;
                }
                else
                {
                    this.removeBySuper(i_Recursive);
                    return null;
                }
            }
            else
            {
                return this;
            }
        }
        
        
        
        /**
         * 消积删除生存时间失效的元素
         * 
         * @author      ZhengWei(HY)
         * @createDate  2016-02-24
         * @version     v1.0
         *
         * @param i_Recursive  是否引发递归检查
         */
        @SuppressWarnings("unlikely-arg-type")
        private void removeBySuper(boolean i_Recursive)
        {
            // 正常情况下不会出现为 null 的情况。
            // 但在某些极端的情况下，会出现为 null 的情况。如：外界设置为 null
            datas    .remove(this.key);
            datasSame.remove(this.key);
            
            if ( this.time == minTime && i_Recursive )
            {
                checkExpires();
            }
        }
        
        
        
        @Override
        public String toString()
        {
            if ( null == this.value )
            {
                return "";
            }
            
            if ( this.time > 0L )
            {
                if ( Date.getNowTime().getTime() <= this.time )
                {
                    return this.value.toString();
                }
                else
                {
                    this.removeBySuper(true);
                    return "";
                }
            }
            else
            {
                return this.value.toString();
            }
        }



        @Override
        public int hashCode()
        {
            if ( null == this.value )
            {
                return -1;
            }
            
            if ( this.time > 0L )
            {
                if ( Date.getNowTime().getTime() <= this.time )
                {
                    return this.value.hashCode();
                }
                else
                {
                    this.removeBySuper(true);
                    return -1;
                }
            }
            else
            {
                return this.value.hashCode();
            }
        }



        @Override
        public boolean equals(Object i_Other)
        {
            if ( null == this.value )
            {
                return false;
            }
            
            if ( this.time > 0L )
            {
                if ( Date.getNowTime().getTime() <= this.time )
                {
                    return this.value.equals(i_Other);
                }
                else
                {
                    this.removeBySuper(true);
                    return false;
                }
            }
            else
            {
                return this.equals(i_Other);
            }
        }


        
        @Override
        public ExpireElement<EK ,EV> clone()
        {
            return new ExpireElement<EK ,EV>(this.key ,this.value ,this.time ,this.createTime);
        }



        /*
        ZhengWei(HY) Del 2016-07-30
        不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
        它会在元素还有用，但集合对象本身没有用时，释放元素对象
        
        一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
        protected void finalize() throws Throwable
        {
            // 不要在此将其它属性设置为 null 。会出错的
            super.finalize();
        }
        */



        @Override
        public EK getKey()
        {
            return key;
        }


        
        @Override
        public synchronized EV getValue()
        {
            return value;
        }
        
        
        
        @Override
        @SuppressWarnings({"unchecked" ,"unlikely-arg-type"})
        public synchronized EV setValue(EV i_Value)
        {
            if ( this.checkExpire() != null )
            {
                this.value = i_Value;
                datasSame.remove(this.key);
                datasSame.put((K)this.key ,(V)this.value);
                return this.value;
            }
            else
            {
                return null;
            }
        }

        
        
        /**
         * 获取：时间戳。保存期满时间。0表示永远有效
         */
        @Override
        public long getTime()
        {
            return time;
        }
        
        
        
        /**
         * 创建时间(首次将Key添加到集合时的时间)
         * 
         * @author      ZhengWei(HY)
         * @createDate  2017-02-28
         * @version     v1.0
         *
         * @return
         */
        @Override
        public Date getCreateTime()
        {
            return this.createTime;
        }

    }
    
}
