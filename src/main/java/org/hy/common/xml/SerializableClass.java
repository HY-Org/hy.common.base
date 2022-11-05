package org.hy.common.xml;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.GenericsReturn;
import org.hy.common.Help;
import org.hy.common.MethodInfo;
import org.hy.common.MethodReflect;
import org.hy.common.Serializable;
import org.hy.common.StringHelp;
import org.hy.common.comparate.MethodComparator;





/**
 * 从 org.hy.common.xml.SerializableDef 类中抽出一公共的部分，形成此类。
 * 
 * 主要目的是：对没有继承org.hy.common.Serializable接口的Java类，通过外界静态方法进行处理
 * 
 * 注意：1.继承父接口的 gatPropertyValue() 方法，永远返回 null。
 *      2. 只要 Setter 与 Getter(Is) 方法成对出现的 Getter(Is) 方法
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2014-04-28
 */
public class SerializableClass implements Serializable
{
    private static final long serialVersionUID = -3234155217835228159L;
    
    
    protected List<MethodInfo> propertyMethods;
    
    protected Class<?>         myClass;
    
    
    
    /**
     * 特定序列化的Java元类型
     * 
     * 当入参为空中：表示序列化自己(继承者)
     * 
     * @param i_Class
     */
    public SerializableClass(Class<?> i_Class)
    {
        this.myClass = i_Class;
        if ( myClass == null )
        {
            myClass = this.getClass();
        }
        
        List<Method> v_Methods = MethodReflect.getStartMethods(myClass ,new String[]{"get" ,"is"} ,0);
        List<Method> v_Setters = MethodReflect.getStartMethods(myClass ,"set" ,1);
        
        Collections.sort(v_Methods ,MethodComparator.getInstance());
        
        for (int i=v_Methods.size() - 1; i>=0; i--)
        {
            // 只要 Setter 与 Getter(Is) 方法成对出现的 Getter(Is) 方法
            String v_Name = v_Methods.get(i).getName();
            
            if ( v_Name.startsWith("get") )
            {
                v_Name = "s"   + v_Name.substring(1);
            }
            else
            {
                v_Name = "set" + v_Name.substring(2);
            }
            
            boolean v_IsExistSetter = false;
            for (int x=0; x<v_Setters.size(); x++)
            {
                if ( v_Name.equals(v_Setters.get(x).getName()) )
                {
                    v_IsExistSetter = true;
                    x = v_Setters.size() + 1;
                }
            }
            
            if ( !v_IsExistSetter )
            {
                v_Methods.remove(i);
            }
        }
        
        this.propertyMethods = MethodInfo.toMethods(v_Methods);
    }
    
    
    
    /**
     * 获取属性的数量
     * 
     * @return
     */
    @Override
    public int gatPropertySize()
    {
        return this.propertyMethods.size();
    }
    
    
    
    /**
     * 获取指定顺序上的属性名称
     * 
     * @param i_PropertyIndex  下标从0开始
     * @return
     */
    @Override
    public String gatPropertyName(int i_PropertyIndex)
    {
        return this.propertyMethods.get(i_PropertyIndex).toMethod(this).getName();
    }
    
    
    
    /**
     * 获取短的属性名称
     * 
     * @param i_PropertyIndex  下标从0开始
     * @return
     */
    public String gatPropertyShortName(int i_PropertyIndex)
    {
        return Help.getPropertyShortName(this.gatPropertyName(i_PropertyIndex));
    }
    
    
    
    /**
     * 注意：只能返回空。因为本类自己的实例化对象，是无法获取属性值的
     */
    @Override
    public Object gatPropertyValue(int i_PropertyIndex)
    {
        return null;
    }
    
    
    
    /**
     * 获取实例对象上，指定顺序上的属性值
     * 
     * @param i_PropertyIndex  属性索引下标。下标从0开始
     * @param i_Instance       实例对象
     * @return
     */
    public Object gatPropertyValue(int i_PropertyIndex ,Object i_Instance)
    {
        try
        {
            Object v_Value = this.propertyMethods.get(i_PropertyIndex).toMethod(i_Instance).invoke(i_Instance);
            
            if ( v_Value == null )
            {
                return null;
            }
            else if ( v_Value instanceof java.util.Date )
            {
                return new Date((java.util.Date)v_Value);
            }
            else
            {
                return v_Value;
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return "";
    }
    
    
    
    /**
     * 对象转为Map集合(递归转换)
     * 
     * @return
     */
    public Map<String ,Object> toMap()
    {
        return this.toMap(true);
    }
    
    
    
    /**
     * 对象转为Map集合
     * 
     * @param i_IsRecursive  是否递归转换
     * @return
     */
    public Map<String ,Object> toMap(boolean i_IsRecursive)
    {
        Map<String ,Object> v_Ret = new HashMap<String ,Object>();
        
        for (int i=0; i<this.gatPropertySize(); i++)
        {
            Object v_Value = this.gatPropertyValue(i);
            
            if ( v_Value != null )
            {
                if ( i_IsRecursive && MethodReflect.isExtendImplement(v_Value ,SerializableClass.class) )
                {
                    // 递归转换，当属性也为序列的对象时
                    v_Ret.put(this.gatPropertyShortName(i) ,((SerializableClass)v_Value).toMap(i_IsRecursive));
                }
                else
                {
                    v_Ret.put(this.gatPropertyShortName(i) ,v_Value);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 对象转为Map集合(递归转换)
     * 
     * @param i_DefaultValue  当对象属性值为 null 时，Map集合填充的默认值
     * @return
     */
    public Map<String ,Object> toMap(Object i_DefaultValue)
    {
        return this.toMap(true ,i_DefaultValue);
    }
    
    
    
    /**
     * 对象转为Map集合
     * 
     * @param i_IsRecursive   是否递归转换
     * @param i_DefaultValue  当对象属性值为 null 时，Map集合填充的默认值
     * @return
     */
    public Map<String ,Object> toMap(boolean i_IsRecursive ,Object i_DefaultValue)
    {
        Map<String ,Object> v_Ret = new HashMap<String ,Object>();
        
        for (int i=0; i<this.gatPropertySize(); i++)
        {
            Object v_Value = this.gatPropertyValue(i);
            
            if ( v_Value != null )
            {
                if ( i_IsRecursive && MethodReflect.isExtendImplement(v_Value ,SerializableClass.class) )
                {
                    // 递归转换，当属性也为序列的对象时
                    v_Ret.put(this.gatPropertyShortName(i) ,((SerializableClass)v_Value).toMap(i_IsRecursive ,i_DefaultValue));
                }
                else
                {
                    v_Ret.put(this.gatPropertyShortName(i) ,v_Value);
                }
            }
            else
            {
                v_Ret.put(this.gatPropertyShortName(i) ,i_DefaultValue);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 构建一个新的对象实例
     * 
     * @return
     */
    public Object newObject()
    {
        try
        {
            return this.myClass.newInstance();
        }
        catch (Exception exce)
        {
            throw new RuntimeException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 与新实例对象对比，获取有变化的属性的索引下标
     * 
     * @return
     */
    public List<Integer> changeValues()
    {
        return this.changeValues(this.newObject());
    }
    
    
    
    /**
     * 对比获取有变化的属性的索引下标
     * 
     * @param i_Serializable
     * @return
     */
    public List<Integer> changeValues(Object i_Serializable)
    {
        if ( i_Serializable == null )
        {
            throw new NullPointerException("Serializable is null.");
        }
        
        if ( !i_Serializable.getClass().equals(this.getClass()) )
        {
            throw new java.lang.ClassCastException("Serializable is not equals this.");
        }
        
        
        Serializable v_Serializable = (SerializableClass)i_Serializable;
        List<Integer> v_Ret = new ArrayList<Integer>();
        for (int i=0; i<this.gatPropertySize(); i++)
        {
            Object v_Old = v_Serializable.gatPropertyValue(i);
            Object v_New = this.gatPropertyValue(i);
            
            if ( v_New == null )
            {
                if ( v_Old != null )
                {
                    v_Ret.add(i);
                }
            }
            else if ( v_Old == null )
            {
                v_Ret.add(i);
            }
            else if ( v_New == v_Old )
            {
                // Nothing.
            }
            else if ( !v_New.equals(v_Old) )
            {
                v_Ret.add(i);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取成双出现的getter方法和setter方法，与其对应属性的 @Doc 标记的注释
     * 
     * 如果方法的返回值也是一个SerializableClass类型的，则对其深入 getDocs()
     * 
     * 返回值是有排列顺序的
     * 
     * 注意：有意将 get... 写成 gat... 的，主要是防止 XJSON类 自动 json 字符串的。
     * 
     * @return
     */
    public Map<String ,DocInfo> gatDocs()
    {
        Map<String ,DocInfo> v_Ret           = XDocument.getDocFields(this.myClass);
        if ( Help.isNull(v_Ret) ) return v_Ret;
        Map<String ,Method>  v_Methods       = new Hashtable<String ,Method>(this.propertyMethods.size());
        List<String>         v_DocFieldNames = Help.toListKeys(v_Ret);
        
        for (MethodInfo v_Item : this.propertyMethods)
        {
            Method v_Method = v_Item.toMethod(this);
            v_Methods.put(v_Method.getName() ,v_Method);
        }
        
        for (String v_DocFieldName : v_DocFieldNames)
        {
            String v_MethodName = "get" + StringHelp.toUpperCaseByFirst(v_DocFieldName);
            
            if ( !v_Methods.containsKey(v_MethodName) )
            {
                v_Ret.remove(v_DocFieldName);
            }
            else
            {
                // 如果方法的返回值也是一个SerializableClass类型的，则对其深入 getDocs()
                Method v_Method = v_Methods.get(v_MethodName);
                
                if ( v_Method.getReturnType() != this.myClass   // 防止自己引用自己的情况，而造成的死循环
                  && MethodReflect.isExtendImplement(v_Method.getReturnType() ,SerializableClass.class) )
                {
                    try
                    {
                        Object               v_SerObj = v_Method.getReturnType().newInstance();
                        Map<String ,DocInfo> v_Childs = ((SerializableClass)v_SerObj).gatDocs();
                        
                        if ( !Help.isNull(v_Childs) )
                        {
                            DocInfo v_MyDocInfo = v_Ret.get(v_DocFieldName);
                            
                            // 建立父子相互引用关系
                            v_MyDocInfo.setChilds(v_Childs);
                            
                            for (DocInfo v_ChildDocInfo : v_Childs.values())
                            {
                                v_ChildDocInfo.setSuperDocInfo(v_MyDocInfo);
                            }
                        }
                    }
                    catch (Exception exce)
                    {
                        // Nothing.
                    }
                }
                // 如果方法的返回值是一个List，并且其范型是一个SerializableClass类型的
                else if ( MethodReflect.isExtendImplement(v_Method.getReturnType() ,List.class) )
                {
                    GenericsReturn v_GR      = MethodReflect.getGenericsReturn(v_Method);
                    DocInfo        v_DocInfo = v_Ret.get(v_DocFieldName);
                    
                    if ( !Help.isNull(v_GR.getMasterTypes()) )
                    {
                        for (Class<?> v_ListElement : v_GR.getMasterTypes())
                        {
                            if ( v_ListElement == List.class || MethodReflect.isExtendImplement(v_ListElement ,List.class) )
                            {
                                if ( v_DocInfo.getSuperDocInfo() != null )
                                {
                                    v_DocInfo.getSuperDocInfo().setChildsType(EChildsType.$List);
                                }
                                
                                Map<String ,DocInfo> v_TempDocChilds = new Hashtable<String ,DocInfo>(1);
                                DocInfo              v_TempDocInfo   = new DocInfo();
                                
                                v_TempDocInfo.setSuperDocInfo(v_DocInfo);
                                v_TempDocChilds.put("-" ,v_TempDocInfo); // 见："-" 此层会在 XDocument.getDocsInfo() 方法中去掉
                                
                                v_DocInfo.setChilds(v_TempDocChilds);
                                v_DocInfo.setChildsType(EChildsType.$List);
                                
                                v_DocInfo = v_TempDocInfo;
                            }
                        }
                    }
                    
                    if ( v_GR.getGenericType() != null
                      && v_GR.getGenericType() != this.myClass   // 防止自己引用自己的情况，而造成的死循环
                      && MethodReflect.isExtendImplement(v_GR.getGenericType() ,SerializableClass.class) )
                    {
                        try
                        {
                            if ( v_DocInfo.getSuperDocInfo() != null )
                            {
                                v_DocInfo.getSuperDocInfo().setChildsType(EChildsType.$List);
                            }
                            
                            Object               v_SerObj  = v_GR.getGenericType().newInstance();
                            Map<String ,DocInfo> v_Childs  = ((SerializableClass)v_SerObj).gatDocsForever();  // 调用递归。获取List元素范型类的父类的属性
                            
                            v_DocInfo.setChilds(v_Childs);
                            v_DocInfo.setChildsType(EChildsType.$List);
                        }
                        catch (Exception exce)
                        {
                            // Nothing.
                        }
                    }
                    
                }
                
            }
        }
        
        return XDocument.sortDocs(v_Ret);
    }
    
    
    
    /**
     * 获取某一属性唯一的@Doc信息
     * 
     * 建议：不要频繁调用此方法，如果频繁调用请使用 this.gatDocs() 方法。
     * 
     * @param i_FieldName
     * @return
     */
    public DocInfo gatDoc(String i_FieldName)
    {
        if ( Help.isNull(i_FieldName) )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Docs = this.gatDocs();
        
        return v_Docs.get(i_FieldName);
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.info 中的内容
     * 
     * 返回值是有排列顺序的
     * 
     * @return
     */
    public Map<String ,Object> gatDocsInfo()
    {
        return XDocument.getDocsInfo(this.gatDocs());
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.value[...] == i_CompValue 的 Doc.info 中的内容
     * 
     * 返回值是有排列顺序的
     * 
     * @return
     */
    public Map<String ,Object> gatDocsInfo(String i_CompValue)
    {
        return XDocument.getDocsInfo(this.gatDocs() ,i_CompValue);
    }
    
    
    
    /**
     * 获取成双出现的getter方法和setter方法，与其对应属性的 @Doc 标记的注释
     * 
     * 只要注释数组上某个下标上的数据
     * 
     * 返回值是有排列顺序的
     * 
     * @param i_Index  数组下标示。最小值从 0 开始
     * @return
     */
    public Map<String ,String> gatDocs(int i_Index)
    {
        return XDocument.getDocsOneValue(this.gatDocs() ,i_Index);
    }
    
    
    
    /**
     * 与 this.getDocs() 方法一样，只是本方法是递归的。
     * 
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String ,DocInfo> gatDocsForever()
    {
        Map<String ,DocInfo> v_Ret   = null;
        Class<?>             v_Super = this.myClass;
        
        if ( MethodReflect.isExtendImplement(v_Super.getSuperclass() ,SerializableClass.class) )
        {
            try
            {
                v_Ret = ((SerializableClass)v_Super.getSuperclass().newInstance()).gatDocsForever();
                
                if ( v_Ret != null )
                {
                    Map<String ,DocInfo> v_TempRet = this.gatDocs();
                    
                    if ( !Help.isNull(v_TempRet) )
                    {
                        v_Ret.putAll(v_TempRet);
                    }
                }
                else
                {
                    v_Ret = this.gatDocs();
                }
            }
            catch (InstantiationException exce)
            {
                v_Ret = this.gatDocs();
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        else
        {
            v_Ret = this.gatDocs();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 与 this.getDocs() 方法一样，只是本方法是递归的。
     * 
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String ,Object> gatDocsInfoForever()
    {
        Map<String ,Object> v_Ret   = new LinkedHashMap<String ,Object>();
        Class<?>            v_Super = this.myClass;
        
        if ( MethodReflect.isExtendImplement(v_Super.getSuperclass() ,SerializableClass.class) )
        {
            try
            {
                v_Ret = ((SerializableClass)v_Super.getSuperclass().newInstance()).gatDocsInfoForever();
                
                if ( v_Ret != null )
                {
                    Map<String ,Object> v_Temp = this.gatDocsInfo();
                    
                    if ( !Help.isNull(v_Temp) )
                    {
                        v_Ret.putAll(v_Temp);
                    }
                }
                else
                {
                    v_Ret = this.gatDocsInfo();
                }
            }
            catch (InstantiationException exce)
            {
                v_Ret = this.gatDocsInfo();
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        else
        {
            v_Ret = this.gatDocsInfo();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 与 this.getDocs() 方法一样，只是本方法是递归的。
     * 
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String ,Object> gatDocsInfoForever(String i_CompValue)
    {
        if ( i_CompValue == null )
        {
            return null;
        }
        
        Map<String ,Object> v_Ret   = null;
        Class<?>            v_Super = this.myClass;
        
        if ( MethodReflect.isExtendImplement(v_Super.getSuperclass() ,SerializableClass.class) )
        {
            try
            {
                v_Ret = ((SerializableClass)v_Super.getSuperclass().newInstance()).gatDocsInfoForever(i_CompValue);
                
                if ( v_Ret != null )
                {
                    Map<String ,Object> v_TempRet = this.gatDocsInfo(i_CompValue);
                    
                    if ( !Help.isNull(v_TempRet) )
                    {
                        v_Ret.putAll(v_TempRet);
                    }
                }
                else
                {
                    v_Ret = this.gatDocsInfo(i_CompValue);
                }
            }
            catch (InstantiationException exce)
            {
                v_Ret = this.gatDocsInfo(i_CompValue);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        else
        {
            v_Ret = this.gatDocsInfo(i_CompValue);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 与 this.getDocs(int) 方法一样，只是本方法是递归的。
     * 
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String ,String> gatDocsForever(int i_Index)
    {
        if ( i_Index < 0 )
        {
            return null;
        }
        
        Map<String ,String> v_Ret   = null;
        Class<?>            v_Super = this.myClass;
        
        if ( MethodReflect.isExtendImplement(v_Super.getSuperclass() ,SerializableClass.class) )
        {
            try
            {
                v_Ret = ((SerializableClass)v_Super.getSuperclass().newInstance()).gatDocsForever(i_Index);
                
                if ( v_Ret != null )
                {
                    Map<String ,String> v_TempRet = this.gatDocs(i_Index);
                    
                    if ( !Help.isNull(v_TempRet) )
                    {
                        v_Ret.putAll(v_TempRet);
                    }
                }
                else
                {
                    v_Ret = this.gatDocs(i_Index);
                }
            }
            catch (InstantiationException exce)
            {
                v_Ret = this.gatDocs(i_Index);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        else
        {
            v_Ret = this.gatDocs(i_Index);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 释放资源
     */
    @Override
    public void freeResource()
    {
        if ( this.propertyMethods != null )
        {
            this.propertyMethods.clear();
            this.propertyMethods = null;
        }
    }
    
    
    
    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable
    {
        this.freeResource();
        
        super.finalize();
    }
    */
    
}
