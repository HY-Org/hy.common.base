package org.hy.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





/**
 * 对象属性的合并（或拼接）字符串。
 * 
 * 类似于 org.hy.common.SumStringMap，不同是，此类是对对象属性的合并或拼接字符串。
 * 
 * 要求1：被合并（或拼接）对象属性的类型必须是 java.lang.String
 * 要求2：被合并（或拼接）对象属性必须有Public访问域的Geter、Setter方法。
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2018-06-01
 */
public class SumMap<K ,V> extends Hashtable<K ,V> implements Map<K ,V>
{

    private static final long serialVersionUID = -7589287786273391713L;
    
    
    
    /** 连接符、对象属性名称的分隔符。默认是逗号 */
    private String              split;
    
    /** 
     * 连接符。默认是空字符串。多个连接符间用this.split指定字符分隔 
     * 
     * 当连接符数量少于对象属性名称数量时，多出的对象属性按最后一个连接符连接
     */
    private String []           connectors;
    
    /** 合并或拼接对象属性的Setter方法 */
    private List<MethodReflect> methodSetters;
    
    /** 合并或拼接对象属性的Getter方法 */
    private List<MethodReflect> methodGetters;
    
    /** 合并或拼接对象的哪个属性。支持面向对象，可实现xxx.yyy.www全路径的解释。多个属性间用this.split指定字符分隔  */
    private String              methodURLs;
    
    
    
    public SumMap()
    {
        this("");
    }
    
    
    
    public SumMap(int i_InitialCapacity)
    {
        this("" ,i_InitialCapacity);
    }
    
    
    
    public SumMap(String i_Connectors)
    {
        super();
        
        this.setSplit(SumList.$Default_Split);
        this.setConnectors(i_Connectors);
    }
    
    
    
    public SumMap(String i_Connectors ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.setSplit(SumList.$Default_Split);
        this.setConnectors(i_Connectors);
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
    public synchronized V set(K i_Key ,V i_Value)
    {
        return super.put(i_Key ,i_Value);
    }
    
    
    
    /**
     * 对象属性的字符串合并（或拼接）
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value  待合并拼接的对象。当对象NULL或对象合并拼接的属性为NULL时，不合并、不拼接。
     * @return         永远返回Map.Key值相同的首个Map.Value对象
     *
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized V put(K i_Key ,V i_Value)
    {
        V v_Old = super.get(i_Key);
        
        if ( i_Value == null )
        {
            return v_Old;
        }
        else if ( v_Old == null )
        {
            if ( this.methodSetters == null )
            {
                if ( !Help.isNull(this.methodURLs) )
                {
                    this.methodGetters = new ArrayList<MethodReflect>();
                    this.methodSetters = new ArrayList<MethodReflect>();
                    String [] v_MethodURLs = this.methodURLs.split(this.split);
                    for (String v_MethodURL : v_MethodURLs)
                    {
                        try
                        {
                            this.methodGetters.add(new MethodReflect(i_Value.getClass() ,v_MethodURL ,true ,MethodReflect.$NormType_Getter));
                            this.methodSetters.add(new MethodReflect(i_Value.getClass() ,v_MethodURL ,true ,MethodReflect.$NormType_Setter));
                        }
                        catch (Exception exce)
                        {
                            exce.printStackTrace();
                            throw new RuntimeException(exce);
                        }
                    }
                }
            }
            
            return super.put(i_Key ,i_Value);
        }
        else
        {
            try
            {
                for (int v_Index=0; v_Index<this.methodGetters.size(); v_Index++)
                {
                    MethodReflect v_MGetter = this.methodGetters.get(v_Index);
                    MethodReflect v_MSetter = this.methodSetters.get(v_Index);
                    
                    String v_NewValue = (String)v_MGetter.invokeForInstance(i_Value);
                    if ( v_NewValue == null )
                    {
                        continue;
                    }
                    
                    String v_OldValue = (String)v_MGetter.invokeForInstance(v_Old);
                    if ( v_OldValue != null )
                    {
                        v_NewValue = v_OldValue 
                                   + this.connectors[Math.min(v_Index ,this.connectors.length - 1)] 
                                   + v_NewValue;
                    }
                    
                    v_MSetter.invokeSetForInstance(v_Old ,v_NewValue);
                }
                
                return v_Old;
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
    }
    
    
    
    /**
     * 批量的对象属性的合并拼接字符串 
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
    public synchronized void putAll(Map<? extends K, ? extends V> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends V>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
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
    public synchronized void setAll(Map<? extends K, ? extends V> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends V>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }
    
    
    
    /**
     * 获取：连接符。默认是空字符串。多个连接符间用this.split指定字符分隔
     */
    public String getConnectors()
    {
        return StringHelp.toString(this.connectors ,"" ,this.split);
    }
    

    
    /**
     * 设置：连接符。默认是空字符串。多个连接符间用this.split指定字符分隔
     * 
     * @param i_Connectors
     */
    public void setConnectors(String i_Connectors)
    {
        this.connectors = Help.NVL(i_Connectors).split(this.split);
    }


    
    /**
     * 获取：合并或拼接对象的那个属性。支持面向对象，可实现xxx.yyy.www全路径的解释
     * 
     *      多个连接符间用this.split指定字符分隔
     */
    public String getMethodURLs()
    {
        return methodURLs;
    }
    


    /**
     * 设置：合并或拼接对象的那个属性。支持面向对象，可实现xxx.yyy.www全路径的解释。
     * 
     *      多个连接符间用this.split指定字符分隔
     * 
     * @param methodURL 
     */
    public void setMethodURLs(String methodURLs)
    {
        this.methodURLs    = methodURLs;
        this.methodGetters = null;
        this.methodSetters = null;
    }


    
    /**
     * 获取：连接符、对象属性名称的分隔符。默认是逗号
     */
    public String getSplit()
    {
        return split;
    }
    

    
    /**
     * 设置：连接符、对象属性名称的分隔符。默认是逗号
     * 
     * @param i_Split 
     */
    public void setSplit(String i_Split)
    {
        this.split = Help.NVL(i_Split ,SumList.$Default_Split);
    }
    
}
