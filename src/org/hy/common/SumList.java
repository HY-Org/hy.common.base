package org.hy.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





/**
 * 对象属性的合并（或拼接）字符串。
 * 
 * 类似于 org.hy.common.SumMap，不同是，按对象关键属性值做标记，合并（或拼接）对象的其它属性。
 * 
 * 要求1：被合并（或拼接）对象属性的类型必须是 java.lang.String
 * 要求2：被合并（或拼接）对象属性必须有Public访问域的Geter、Setter方法。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-07-13
 * @version     v1.0
 * @param <V>
 */
public class SumList<V> extends ArrayList<V>
{

    private static final long serialVersionUID = -3325332111680395262L;
    
    
    /** 连接符、对象属性名称的分隔符。默认是逗号 */
    private String               split;
    
    /** 
     * 连接符。默认是空字符串。多个连接符间用this.split指定字符分隔 
     * 
     * 当连接符数量少于对象属性名称数量时，多出的对象属性按最后一个连接符连接
     */
    private String []            connectors;
    
    /** 合并或拼接对象属性的Setter方法 */
    private List<MethodReflect>  methodSetters;
    
    /** 合并或拼接对象属性的Getter方法 */
    private List<MethodReflect>  methodGetters;
    
    /** 合并或拼接对象的哪个属性。支持面向对象，可实现xxx.yyy.www全路径的解释。多个属性间用this.split指定字符分隔  */
    private String               methodURLs;
    
    /** 关键属性。按对象的哪个属性合并或拼接对象的其它属性的。支持面向对象，可实现xxx.yyy.www全路径的解释。（只能指定对象的一个属性） */
    private String               keyMethodURL;
    
    /** 关键属性的Getter方法。 */
    private MethodReflect        keyMethodGetter;
    
    /** 关键属性所在List集合的索引号位置 */
    private Map<Object ,Integer> keyListIndex;
    
    /** 是否允许合并或拼接。内部属性。为了性能及简化判定而存在 */
    private boolean              isAllowSum;
    
    
    
    public SumList()
    {
        this("");
    }
    
    
    
    public SumList(int i_InitialCapacity)
    {
        this("" ,i_InitialCapacity);
    }
    
    
    
    public SumList(String i_Connectors)
    {
        super();
        
        this.keyListIndex = new HashMap<Object ,Integer>();
        this.isAllowSum   = false; 
        this.setSplit(",");
        this.setConnectors(i_Connectors);
    }
    
    
    
    public SumList(String i_Connectors ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.keyListIndex = new HashMap<Object ,Integer>();
        this.isAllowSum   = false;
        this.setSplit(",");
        this.setConnectors(i_Connectors);
    }
    
    
    
    /**
     * 根据入参反射出属性方法，一般情况下只执行一次。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-13
     * @version     v1.0
     *
     * @param i_NewObject
     * @return
     */
    private boolean initMethods(V i_NewObject)
    {
        if ( this.isAllowSum )
        {
            return this.isAllowSum;
        }
        
        if ( !Help.isNull(this.keyMethodURL) )
        {
            try
            {
                this.keyMethodGetter = new MethodReflect(i_NewObject.getClass() ,this.keyMethodURL ,true ,MethodReflect.$NormType_Getter);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
        
        if ( !Help.isNull(this.methodURLs) )
        {
            this.methodGetters = new ArrayList<MethodReflect>();
            this.methodSetters = new ArrayList<MethodReflect>();
            String [] v_MethodURLs = this.methodURLs.split(this.split);
            for (String v_MethodURL : v_MethodURLs)
            {
                try
                {
                    this.methodGetters.add(new MethodReflect(i_NewObject.getClass() ,v_MethodURL ,true ,MethodReflect.$NormType_Getter));
                    this.methodSetters.add(new MethodReflect(i_NewObject.getClass() ,v_MethodURL ,true ,MethodReflect.$NormType_Setter));
                }
                catch (Exception exce)
                {
                    exce.printStackTrace();
                    throw new RuntimeException(exce);
                }
            }
        }
        
        if ( this.keyMethodGetter != null 
          && !Help.isNull(this.methodGetters)
          && !Help.isNull(this.methodSetters) )
        {
            this.isAllowSum = true;
        }
        
        return this.isAllowSum;
    }
    
    
    
    /**
     * 覆盖性的设置值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-13
     * @version     v1.0
     *
     * @param i_Index
     * @param i_NewObject  待合并拼接的对象。当对象NULL或对象合并拼接的属性为NULL时，不合并、不拼接。
     *                     关键属性的数据不允许NULL，否则不添加到集合中。
     * @return
     */
    public synchronized V set(int i_Index ,V i_NewObject)
    {
        if ( i_NewObject == null )
        {
            return null;
        }
        
        if ( initMethods(i_NewObject) )
        {
            try
            {
                Object v_KeyValue = this.keyMethodGetter.invokeForInstance(i_NewObject);
                if ( v_KeyValue == null )
                {
                    return null;
                }
                
                Integer v_OldIndex  = this.keyListIndex.get(v_KeyValue);
                V       v_OldObject = null;
                
                // 全新的元素
                if ( v_OldIndex == null )
                {
                    super.set(i_Index ,i_NewObject);
                    this.keyListIndex.put(v_KeyValue ,i_Index);
                    v_OldObject = i_NewObject;
                }
                // 需要拼接或合并的元素
                else
                {
                    v_OldObject = super.get(v_OldIndex);
                    
                    for (int v_Index=0; v_Index<this.methodGetters.size(); v_Index++)
                    {
                        MethodReflect v_MGetter = this.methodGetters.get(v_Index);
                        MethodReflect v_MSetter = this.methodSetters.get(v_Index);
                        
                        String v_NewValue = (String)v_MGetter.invokeForInstance(i_NewObject);
                        
                        v_MSetter.invokeSetForInstance(v_OldObject ,v_NewValue);
                    }
                }
                
                return v_OldObject;
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 对象属性的字符串合并（或拼接）
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-13
     * @version     v1.0
     *
     * @param i_NewObject  待合并拼接的对象。当对象NULL或对象合并拼接的属性为NULL时，不合并、不拼接。
     *                     关键属性的数据不允许NULL，否则不添加到集合中。
     * @return
     *
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    public synchronized boolean add(V i_NewObject)
    {
        if ( i_NewObject == null )
        {
            return false;
        }
        
        if ( initMethods(i_NewObject) )
        {
            try
            {
                Object v_KeyValue = this.keyMethodGetter.invokeForInstance(i_NewObject);
                if ( v_KeyValue == null )
                {
                    return false;
                }
                
                Integer v_OldIndex  = this.keyListIndex.get(v_KeyValue);
                V       v_OldObject = null;
                
                // 全新的元素
                if ( v_OldIndex == null )
                {
                    super.add(i_NewObject);
                    this.keyListIndex.put(v_KeyValue ,super.size() - 1);
                    v_OldObject = i_NewObject;
                }
                // 需要拼接或合并的元素
                else
                {
                    v_OldObject = super.get(v_OldIndex);
                    
                    for (int v_Index=0; v_Index<this.methodGetters.size(); v_Index++)
                    {
                        MethodReflect v_MGetter = this.methodGetters.get(v_Index);
                        MethodReflect v_MSetter = this.methodSetters.get(v_Index);
                        
                        String v_NewValue = (String)v_MGetter.invokeForInstance(i_NewObject);
                        if ( v_NewValue == null )
                        {
                            continue;
                        }
                        
                        String v_OldValue = (String)v_MGetter.invokeForInstance(v_OldObject);
                        if ( v_OldValue != null )
                        {
                            v_NewValue = v_OldValue 
                                       + this.connectors[Math.min(v_Index ,this.connectors.length - 1)] 
                                       + v_NewValue;
                        }
                        
                        v_MSetter.invokeSetForInstance(v_OldObject ,v_NewValue);
                    }
                }
                
                return true;
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
        
        return false;
    }
    
    
    
    /**
     * 批量的对象属性的合并拼接字符串 
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-13
     * @version     v1.0
     *
     * @param i_AddValues
     */
    @Override
    public synchronized boolean addAll(Collection<? extends V> i_AddValues) 
    {
        if ( Help.isNull(i_AddValues) )
        {
            return false;
        }
        
        for (V v_Item : i_AddValues)
        {
            if ( !this.add(v_Item) )
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    
    /**
     * 将索引号删除
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-13
     * @version     v1.0
     *
     * @param index
     * @return
     *
     * @see java.util.ArrayList#remove(int)
     */
    @Override
    public synchronized V remove(int index)
    {
        V v_RemoveObject = super.remove(index);
        
        if ( initMethods(v_RemoveObject) )
        {
            try
            {
                Object v_Key = this.keyMethodGetter.invokeForInstance(v_RemoveObject);
                
                this.keyListIndex.remove(v_Key);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
        
        return v_RemoveObject;
    }



    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object i_Object)
    {
        boolean v_Ret = super.remove(i_Object);
        
        if ( initMethods((V)i_Object) )
        {
            try
            {
                Object v_Key = this.keyMethodGetter.invokeForInstance(i_Object);
                
                this.keyListIndex.remove(v_Key);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
        
        
        return v_Ret;
    }



    @Override
    public boolean removeAll(Collection<?> i_Objects)
    {
        if ( Help.isNull(i_Objects) )
        {
            return false;
        }
        
        for (Object v_Object : i_Objects)
        {
            if ( !this.remove(v_Object) )
            {
                return false;
            }
        }
        
        return true;
    }



    @Override
    public void clear()
    {
        this.keyListIndex.clear();
        super.clear();
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
        this.isAllowSum    = false;
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
     * @param split 
     */
    public void setSplit(String split)
    {
        this.split = split;
    }


    
    /**
     * 获取：关键属性。按对象的哪个属性合并或拼接对象的其它属性的。支持面向对象，可实现xxx.yyy.www全路径的解释。（只能指定对象的一个属性）
     */
    public String getKeyMethodURL()
    {
        return keyMethodURL;
    }
    

    
    /**
     * 设置：关键属性。按对象的哪个属性合并或拼接对象的其它属性的。支持面向对象，可实现xxx.yyy.www全路径的解释。（只能指定对象的一个属性）
     * 
     * @param keyMethodURL 
     */
    public void setKeyMethodURL(String keyMethodURL)
    {
        this.keyMethodURL    = keyMethodURL;
        this.keyMethodGetter = null;
        this.isAllowSum      = false;
    }
    
}
