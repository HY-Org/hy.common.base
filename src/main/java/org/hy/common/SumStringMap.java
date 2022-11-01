package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 合并（或拼接）字符串。
 * 
 * 相同Key值多次 put() 值时，多次put的值将按先后顺序合并拼接成一个大字符串保存在Map.Key中。 
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2018-06-01
 */
public class SumStringMap<K> extends Hashtable<K ,String> implements Map<K ,String>
{

    private static final long serialVersionUID = -8835763432149887369L;

    /** 连接符。默认是空字符串 */
    private String connector;
    
    
    
    public SumStringMap()
    {
        this("");
    }
    
    
    
    public SumStringMap(int i_InitialCapacity)
    {
        this("" ,i_InitialCapacity);
    }
    
    
    
    public SumStringMap(String i_Connector)
    {
        super();
        
        this.connector = i_Connector;
    }
    
    
    
    public SumStringMap(String i_Connector ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.connector = i_Connector;
    }
    
    
    
    /**
     * 覆盖性的设置值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value
     * @return
     */
    public synchronized String set(K i_Key ,String i_Value)
    {
        return super.put(i_Key ,i_Value);
    }
    
    
    
    /**
     * 字符串合并（或拼接）
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value  合并拼接的字符串。当为NULL时，不合并、不拼接。
     * @return
     *
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized String put(K i_Key ,String i_Value)
    {
        String v_Old = super.get(i_Key);
        
        if ( i_Value == null )
        {
            return v_Old;
        }
        else if ( v_Old == null )
        {
            return super.put(i_Key ,i_Value);
        }
        else
        {
            return super.put(i_Key ,v_Old + this.connector + i_Value);
        }
    }
    
    
    
    /**
     * 批量的合并拼接字符串 
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_AddValues
     *
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    @Override
    public synchronized void putAll(Map<? extends K, ? extends String> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends String>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends String> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    /**
     * 批量的覆盖性的设置值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_AddValues
     */
    public synchronized void setAll(Map<? extends K, ? extends String> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends String>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends String> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }


    
    /**
     * 获取：连接符。默认是空字符串
     */
    public String getConnector()
    {
        return connector;
    }
    

    
    /**
     * 设置：连接符。默认是空字符串
     * 
     * @param i_Connector 
     */
    public void setConnector(String i_Connector)
    {
        this.connector = Help.NVL(i_Connector);
    }
    
}
