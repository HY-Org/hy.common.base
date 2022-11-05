package org.hy.common.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.xml.annotation.Doc;





/**
 * 获取 @Doc 标记的注解信息的类
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2014-11-30
 */
public class XDocument
{
    
    /**
     * 获取所有属性上与 @Doc 相关的注解
     * 
     * 返回值是有排列顺序的
     * 
     * @return  Map.key 为属性的名字
     */
    public Map<String ,DocInfo> getDocFieldsSort()
    {
        return sortDocs(getDocFields(this.getClass()));
    }
    
    
    
    /**
     * 获取所有方法上与 @Doc 相关的注解
     * 
     * 返回值是有排列顺序的
     * 
     * 注：重载方法的多个 @Doc 注解，有被覆盖的情况
     * 
     * @return  Map.key 为方法的名字
     */
    public Map<String ,DocInfo> getDocMethodsSort()
    {
        return sortDocs(getDocMethods(this.getClass()));
    }
    
    
    
    /**
     * 获取所有构造器上与 @Doc 相关的注解
     * 
     * 返回值是有排列顺序的
     * 
     * 注：重载构造器的多个 @Doc 注解，有被覆盖的情况
     * 
     * @return  Map.key 为构造器的名字
     */
    public Map<String ,DocInfo> getDocConstructorsSort()
    {
        return sortDocs(getDocConstructors(this.getClass()));
    }
    
    
    
    /**
     * 获取类上与 @Doc 相关的注解
     * 
     * @param i_Class
     * @return
     */
    public DocInfo getDocClass()
    {
        return getDocClass(this.getClass());
    }
    
    
    
    /**
     * 获取所有与 @Doc 相关的注解
     * 
     * 返回值是有排列顺序的
     * 
     * 注：相同Map.key 上的 @Doc 注解，有被覆盖的情况
     * 
     * @return
     */
    public Map<String ,DocInfo> getDocAllSort()
    {
        return sortDocs(getDocAll(this.getClass()));
    }
    
    
    
    /**
     * 获取所有属性上与 @Doc 相关的注解
     * 
     * @return  Map.key 为属性的名字
     */
    public Map<String ,DocInfo> getDocFields()
    {
        return getDocFields(this.getClass());
    }
    
    
    
    /**
     * 获取所有方法上与 @Doc 相关的注解
     * 
     * 注：重载方法的多个 @Doc 注解，有被覆盖的情况
     * 
     * @return  Map.key 为方法的名字
     */
    public Map<String ,DocInfo> getDocMethods()
    {
        return getDocMethods(this.getClass());
    }
    
    
    
    /**
     * 获取所有构造器上与 @Doc 相关的注解
     * 
     * 注：重载构造器的多个 @Doc 注解，有被覆盖的情况
     * 
     * @return  Map.key 为构造器的名字
     */
    public Map<String ,DocInfo> getDocConstructors()
    {
        return getDocConstructors(this.getClass());
    }
    
    
    
    /**
     * 获取所有与 @Doc 相关的注解
     * 
     * 注：相同Map.key 上的 @Doc 注解，有被覆盖的情况
     * 
     * @return
     */
    public Map<String ,DocInfo> getDocAll()
    {
        return getDocAll(this.getClass());
    }
    
    
    
    /**
     * 获取所有属性上与 @Doc 相关的注解
     * 
     * @return  Map.key 为属性的名字
     */
    public static Map<String ,DocInfo> getDocFields(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Ret = new Hashtable<String ,DocInfo>();
        
        for (Field v_Field : i_Class.getDeclaredFields() )
        {
            Doc v_Doc = v_Field.getAnnotation(Doc.class);
            
            if ( v_Doc != null )
            {
                v_Ret.put(v_Field.getName() ,new DocInfo(v_Doc));
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取所有方法上与 @Doc 相关的注解
     * 
     * 注：重载方法的多个 @Doc 注解，有被覆盖的情况
     * 
     * @return  Map.key 为方法的名字
     */
    public static Map<String ,DocInfo> getDocMethods(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Ret = new Hashtable<String ,DocInfo>();
        
        for (Method v_Method : i_Class.getDeclaredMethods() )
        {
            Doc v_Doc = v_Method.getAnnotation(Doc.class);
            
            if ( v_Doc != null )
            {
                v_Ret.put(v_Method.getName() ,new DocInfo(v_Doc));
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取所有构造器上与 @Doc 相关的注解
     * 
     * 注：重载构造器的多个 @Doc 注解，有被覆盖的情况
     * 
     * @return  Map.key 为构造器的名字
     */
    public static Map<String ,DocInfo> getDocConstructors(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Ret = new Hashtable<String ,DocInfo>();
        
        for (Constructor<?> v_Constructor : i_Class.getDeclaredConstructors() )
        {
            Doc v_Doc = v_Constructor.getAnnotation(Doc.class);
            
            if ( v_Doc != null )
            {
                v_Ret.put(v_Constructor.getName() ,new DocInfo(v_Doc));
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取类上与 @Doc 相关的注解
     * 
     * @param i_Class
     * @return
     */
    public static DocInfo getDocClass(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return null;
        }
        
        Doc v_Doc = i_Class.getAnnotation(Doc.class);
        
        if ( v_Doc == null )
        {
            return null;
        }
        else
        {
            return new DocInfo(v_Doc);
        }
    }
    
    
    
    /**
     * 获取所有与 @Doc 相关的注解
     * 
     * 注：相同Map.key 上的 @Doc 注解，有被覆盖的情况
     * 
     * @return
     */
    public static Map<String ,DocInfo> getDocAll(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Ret = getDocFields(i_Class);
        
        v_Ret.putAll(getDocMethods(i_Class));
        v_Ret.putAll(getDocConstructors(i_Class));
        
        DocInfo v_DocInfo = getDocClass(i_Class);
        
        if ( v_DocInfo != null )
        {
            v_Ret.put(i_Class.getClass().getSimpleName() ,v_DocInfo);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 对 @Doc 注解集合排序。按 @Doc.value[0] 排序，即多个注解数组中的，首个数组元素排序
     * 
     * @param i_Docs
     * @return
     */
    public static Map<String ,DocInfo> sortDocs(Map<String ,DocInfo> i_Docs)
    {
        if ( Help.isNull(i_Docs) )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Ret   = new LinkedHashMap<String ,DocInfo>(i_Docs.size());
        List<String>         v_Sorts = new ArrayList<String>(i_Docs.size());
        
        for (String v_Key : i_Docs.keySet())
        {
            String v_NewKey = i_Docs.get(v_Key).index() + "@" + v_Key;
            
            v_Sorts.add(v_NewKey);
        }
        
        Collections.sort(v_Sorts);
        
        for (String v_NewKey : v_Sorts)
        {
            String v_OldKey = v_NewKey.split("@")[1];
            
            v_Ret.put(v_OldKey ,i_Docs.get(v_OldKey));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.value[...] == i_CompValue 的 Doc 的内容
     * 
     * 返回值是有顺序的
     * 
     * @param i_CompValue  比较的Value值
     * @return
     */
    public static Map<String ,DocInfo> getDocs(Map<String ,DocInfo> i_Docs ,String i_CompValue)
    {
        if ( Help.isNull(i_Docs) || i_CompValue == null )
        {
            return null;
        }
        
        Map<String ,DocInfo> v_Ret = new LinkedHashMap<String ,DocInfo>();
        
        for (String v_FieldName : i_Docs.keySet())
        {
            DocInfo v_DocInfo = i_Docs.get(v_FieldName);
            
            // 按道理来说，此属性不应为空。但当它为空时，表示为由'我'自己创建出的 DocInfo 对象实例。其它无外。
            // 当为空时，表示总是被匹配
            if ( Help.isNull(v_DocInfo.value()) )
            {
                v_Ret.put(v_FieldName ,v_DocInfo);
            }
            else
            {
                for (String v_Value : v_DocInfo.value())
                {
                    if ( v_Value.equals(i_CompValue) )
                    {
                        v_Ret.put(v_FieldName ,v_DocInfo);
                    }
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.info 中的内容
     * 
     * 当指定下标上没有对应的数据时，就不包括在返回的结果内。
     * 
     * 返回值是有顺序的
     * 
     * @param i_Index  数组下标。最小值从 0 开始
     * @return
     */
    public static Map<String ,Object> getDocsInfo(Map<String ,DocInfo> i_Docs)
    {
        if ( Help.isNull(i_Docs) )
        {
            return null;
        }
        
        Map<String ,Object> v_Ret = new LinkedHashMap<String ,Object>();
        
        for (String v_FieldName : i_Docs.keySet())
        {
            DocInfo v_DocInfo = i_Docs.get(v_FieldName);
            
            // 如果有子类，那就递归的获取子类的注解信息
            if ( !Help.isNull(v_DocInfo.getChilds()) )
            {
                Map<String ,Object> v_TempDocs = getDocsInfo(v_DocInfo.getChilds());
                String              v_FName    = null;
                
                if ( !Help.isNull(v_DocInfo.info()) )
                {
                    v_FName = v_FieldName + "@" + v_DocInfo.info().trim();
                }
                else
                {
                    v_FName = v_FieldName;
                }
                
                if ( v_DocInfo.getChildsType() == EChildsType.$List )
                {
                    List<Map<String ,Object>> v_TempList = new ArrayList<Map<String ,Object>>(1);
                    
                    v_TempList.add(v_TempDocs);
                    
                    v_Ret.put(v_FName ,v_TempList);
                }
                else
                {
                    v_Ret.put(v_FName ,v_TempDocs);
                }
            }
            else if ( !Help.isNull(v_DocInfo.info()) )
            {
                v_Ret.put(v_FieldName ,v_DocInfo.info().trim());
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.value[...] == i_CompValue 的 Doc.info 中的内容
     * 
     * 返回值是有顺序的
     * 
     * @param i_CompValue  比较的Value值
     * @return
     */
    public static Map<String ,Object> getDocsInfo(Map<String ,DocInfo> i_Docs ,String i_CompValue)
    {
        if ( Help.isNull(i_Docs) || i_CompValue == null )
        {
            return null;
        }
        
        Map<String ,Object> v_Ret       = new LinkedHashMap<String ,Object>();
        String              v_CompValue = i_CompValue.trim();
        
        for (String v_FieldName : i_Docs.keySet())
        {
            DocInfo v_DocInfo = i_Docs.get(v_FieldName);
            
            // 按道理来说，此属性不应为空。但当它为空时，表示为由'我'自己创建出的 DocInfo 对象实例。其它无外。
            // 当为空时，表示总是被匹配
            if ( Help.isNull(v_DocInfo.value()) )
            {
                // 如果有子类，那就递归的获取子类的注解信息
                if ( !Help.isNull(v_DocInfo.getChilds()) )
                {
                    Map<String ,Object> v_TempDocs = getDocsInfo(v_DocInfo.getChilds() ,v_CompValue);
                    String              v_FName    = null;
                    
                    if ( !Help.isNull(v_DocInfo.info()) )
                    {
                        v_FName = v_FieldName + "@" + v_DocInfo.info().trim();
                    }
                    else
                    {
                        v_FName = v_FieldName;
                    }
                    
                    if ( v_DocInfo.getChildsType() == EChildsType.$List )
                    {
                        List<Map<String ,Object>> v_TempList = new ArrayList<Map<String ,Object>>(1);
                        
                        v_TempList.add(v_TempDocs);
                        
                        v_Ret.put(v_FName ,v_TempList);
                    }
                    else
                    {
                        v_Ret.put(v_FName ,v_TempDocs);
                    }
                }
                else if ( !Help.isNull(v_DocInfo.info()) )
                {
                    v_Ret.put(v_FieldName ,v_DocInfo.info().trim());
                }
            }
            else
            {
                for (String v_Value : v_DocInfo.value())
                {
                    if ( v_Value.trim().equals(v_CompValue) )
                    {
                        // 如果有子类，那就递归的获取子类的注解信息
                        if ( !Help.isNull(v_DocInfo.getChilds()) )
                        {
                            Map<String ,Object> v_TempDocs = getDocsInfo(v_DocInfo.getChilds() ,v_CompValue);
                            String              v_FName    = null;
                            
                            if ( !Help.isNull(v_DocInfo.info()) )
                            {
                                v_FName = v_FieldName + "@" + v_DocInfo.info().trim();
                            }
                            else
                            {
                                v_FName = v_FieldName;
                            }
                            
                            if ( v_DocInfo.getChildsType() == EChildsType.$List )
                            {
                                // 当为双层 List<List<Object>> 时，去掉 "-" 层次，并生成 [ [ { ... } ] ] 这个结构的 json 字符串结构
                                if ( v_TempDocs.size() == 1 && "-".equals(v_TempDocs.keySet().iterator().next()) )
                                {
                                    List<Object> v_TempList = new ArrayList<Object>(1);
                                    
                                    v_TempList.add(v_TempDocs.get("-"));  // 见:SerialzableClass.gatDocs() 方法：用于产生 "-"
                                    
                                    v_Ret.put(v_FName ,v_TempList);
                                }
                                else
                                {
                                    List<Map<String ,Object>> v_TempList = new ArrayList<Map<String ,Object>>(1);
                                    
                                    v_TempList.add(v_TempDocs);
                                    
                                    v_Ret.put(v_FName ,v_TempList);
                                }
                            }
                            else
                            {
                                v_Ret.put(v_FName ,v_TempDocs);
                            }
                        }
                        else if ( !Help.isNull(v_DocInfo.info()) )
                        {
                            v_Ret.put(v_FieldName ,v_DocInfo.info().trim());
                        }
                    }
                }
                
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.value 中的内容
     * 
     * 只要注释数组上某个下标上的数据
     * 
     * 当指定下标上没有对应的数据时，就不包括在返回的结果内。
     * 
     * 返回值是有顺序的
     * 
     * @return
     */
    public static Map<String ,String []> getDocsValue(Map<String ,Doc> i_Docs)
    {
        if ( Help.isNull(i_Docs) )
        {
            return null;
        }
        
        Map<String ,String []> v_Ret = new LinkedHashMap<String ,String []>();
        
        for (String v_FieldName : i_Docs.keySet())
        {
            Doc v_Doc = i_Docs.get(v_FieldName);
            
            if ( !Help.isNull(v_Doc.value()) )
            {
                v_Ret.put(v_FieldName ,i_Docs.get(v_FieldName).value());
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取Doc集合中的 Doc.value[x] 中的内容
     * 
     * 只要注释数组上某个下标上的数据
     * 
     * 当指定下标上没有对应的数据时，就不包括在返回的结果内。
     * 
     * 返回值是有顺序的
     * 
     * @param i_Index  数组下标。最小值从 0 开始
     * @return
     */
    public static Map<String ,String> getDocsOneValue(Map<String ,DocInfo> i_Docs ,int i_Index)
    {
        if ( Help.isNull(i_Docs) || i_Index < 0 )
        {
            return null;
        }
        
        Map<String ,String> v_Ret = new LinkedHashMap<String ,String>();
        
        for (String v_FieldName : i_Docs.keySet())
        {
            DocInfo v_DocInfo = i_Docs.get(v_FieldName);
            
            if ( !Help.isNull(v_DocInfo.value()) && i_Index < v_DocInfo.value().length )
            {
                v_Ret.put(v_FieldName ,v_DocInfo.value()[i_Index].trim());
            }
        }
        
        return v_Ret;
    }
    
}
