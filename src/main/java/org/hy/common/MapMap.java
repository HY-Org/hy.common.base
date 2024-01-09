package org.hy.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;





/**
 * 由Map和Map组成的层次结构，并且是按Map.key的关键字分隔符分隔层次的。
 * 
 * 支持1：无限层次的分隔
 * 支持2：局部同步锁，并非绝对安全
 * 
 *    如默认按英文点分隔 put("datas.name" ,"Hello") 时，生成 Map<String ,Map<String ,Object>> 的结构
 *    即 Map.get("datas") 的结构是 Map<String ,Object>。
 *       Map.get("datas.name") 的值是 "Hello";
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-01-08
 * @version     v1.0
 */
public class MapMap extends HashMap<String ,Object>
{

    private static final long serialVersionUID = -3067815539900323338L;
    
    
    
    /** 数据结构层次的分隔符。默认为：英文的点 */
    private String splitValue;
    
    
    
    public MapMap()
    {
        super();
        this.splitValue = ".";
    }
    
    
    
    public MapMap(String i_SplitValue)
    {
        super();
        this.splitValue = i_SplitValue;
    }
    
    
    
    public MapMap(int i_InitialCapacity ,String i_SplitValue)
    {
        super(i_InitialCapacity);
        this.splitValue = i_SplitValue;
    }


    
    /**
     * 获取：数据结构层次的分隔符。默认为：英文的点
     */
    public String gatSplitValue()
    {
        return splitValue;
    }


    
    /**
     * 设置：数据结构层次的分隔符。默认为：英文的点
     * 
     * @param i_SplitValue 数据结构层次分隔符。默认为：英文的点
     */
    public void satSplitValue(String i_SplitValue)
    {
        this.splitValue = i_SplitValue;
    }
    
    
    
    /**
     * 解释Map的关键字，解释出它的第一层次和其它层次
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     */
    public String [] parserKey(String i_Key)
    {
        int    v_FirstDot = i_Key.indexOf(this.splitValue);
        String v_FirstKey = i_Key;
        String v_OtherKey = null;
        
        if ( v_FirstDot > 0 && v_FirstDot + 1 < i_Key.length())
        {
            v_FirstKey = i_Key.substring(0 ,v_FirstDot);
            v_OtherKey = i_Key.substring(v_FirstDot + 1);
        }
        
        return new String [] {v_FirstKey ,v_OtherKey};
    }



    /**
     * 赋值
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @param i_Value
     * @return
     *
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(String i_Key ,Object i_Value)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key);
        if ( v_Keys[1] != null )
        {
            MapMap v_Child = null;
            synchronized (this)
            {
                Object v_OldChild = this.get(v_Keys[0]);
                if ( v_OldChild == null )
                {
                    v_Child = new MapMap(this.splitValue);
                    super.put(v_Keys[0] ,v_Child);
                }
                else if ( v_OldChild instanceof MapMap )
                {
                    v_Child = (MapMap) v_OldChild;
                }
                else
                {
                    v_Child = new MapMap(this.splitValue);
                    super.put(v_Keys[0] ,v_Child);
                }
            }
            
            v_Child.put(v_Keys[1] ,i_Value);
            return v_Child;
        }
        else
        {
            return super.put(v_Keys[0] ,i_Value);
        }
    }



    /**
     * 复制另一个Map集合
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     * @param i_Map
     *
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends String ,? extends Object> i_Map)
    {
        Iterator<? extends Map.Entry<? extends String, ? extends Object>> i = i_Map.entrySet().iterator();
        
        while (i.hasNext())
        {
            Map.Entry<? extends String, ? extends Object> e = i.next();
            this.put(e.getKey(), e.getValue());
        }
    }



    /**
     * 获取元素数值，支持
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     *
     * @see java.util.HashMap#get(java.lang.Object)
     */
    @Override
    public Object get(Object i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key.toString());
        if ( v_Keys[1] != null )
        {
            return this.get(v_Keys[1]);
        }
        else
        {
            return super.get(i_Key);
        }
    }



    /**
     * 判定关键字是否存在
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     *
     * @see java.util.HashMap#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key.toString());
        if ( v_Keys[1] != null )
        {
            return this.containsKey(v_Keys[1]);
        }
        else
        {
            return super.containsKey(i_Key);
        }
    }



    /**
     * 删除元素
     * 
     * 注：被删除元素子子元素内存没有释放，须用户自行释放
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     *
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    @Override
    public Object remove(Object i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key.toString());
        if ( v_Keys[1] != null )
        {
            return this.remove(v_Keys[1]);
        }
        else
        {
            return super.remove(i_Key);
        }
    }



    /**
     * 清理所有层次结构中的所有数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-08
     * @version     v1.0
     *
     *
     * @see java.util.HashMap#clear()
     */
    @Override
    public void clear()
    {
        Iterator<Object> v_Iter = super.values().iterator();
        
        while ( v_Iter.hasNext() )
        {
            Object v_Item = v_Iter.next();
            if ( v_Item == null )
            {
                continue;
            }
            else if ( v_Item instanceof MapMap )
            {
                ((MapMap)v_Item).clear();
            }
        }
        
        super.clear();
    }
    
}
