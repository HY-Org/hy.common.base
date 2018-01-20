package org.hy.common;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;





/**
 * 因Java自带的 java.lang.reflect.Method 类没有实现序列化接口。
 * 而有些情况下，对象是需要 java.lang.reflect.Method 类型的信息时，就可用本类代替。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-15
 * @version     v1.0
 *              v2.0  2017-12-23  添加：javaType属性，使转为java.lang.reflect.Method时更加独立和方便。
 */
public class MethodInfo implements Serializable ,Comparable<MethodInfo>
{
    
    private static final long serialVersionUID = 1261690656307247327L;
    
    
    /** 方法所在的Java类的类型 */
    private Class<?>    javaType;

    /** 方法的名称（区分大小写） */
    private String      name;
    
    /** 方法的所有入参的Java元类型 */
    private Class<?> [] parameterTypes;
    
    
    
    /**
     * 将 java.lang.reflect.Method 集合转为本类的集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_Methods
     * @return
     */
    public static List<MethodInfo> toMethods(List<Method> i_Methods)
    {
        List<MethodInfo> v_Ret = new ArrayList<MethodInfo>(); 
        
        if ( Help.isNull(i_Methods) )
        {
            return v_Ret;
        }
        
        for (Method v_Method : i_Methods)
        {
            v_Ret.add(new MethodInfo(v_Method));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将本类集合还原为Java自带的 java.lang.reflect.Method 的集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_Instance
     * @param i_MethodInfos
     * @return
     */
    public static List<Method> toMethods(Object i_Instance ,List<MethodInfo> i_MethodInfos)
    {
        return toMethods(i_Instance.getClass() ,i_MethodInfos);
    }
    
    
    
    /**
     * 将本类集合还原为Java自带的 java.lang.reflect.Method 的集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_Class
     * @param i_MethodInfos
     * @return
     */
    public static List<Method> toMethods(Class<?> i_Class ,List<MethodInfo> i_MethodInfos)
    {
        List<Method> v_Ret = new ArrayList<Method>(); 
        
        if ( Help.isNull(i_MethodInfos) )
        {
            return v_Ret;
        }
        
        for (MethodInfo v_MethodInfo : i_MethodInfos)
        {
            v_Ret.add(v_MethodInfo.toMethod(i_Class));
        }
        
        return v_Ret;
    }
    
    
    
    public MethodInfo()
    {
        this(null ,null ,null);
    }
    
    
    
    public MethodInfo(Method i_Method)
    {
        this(i_Method.getDeclaringClass() ,i_Method.getName() ,i_Method.getParameterTypes());
    }
    
    
    
    public MethodInfo(String i_Name ,Class<?> [] i_ParameterTypes)
    {
        this(null ,i_Name ,i_ParameterTypes);
    }
    
    
    
    public MethodInfo(Class<?> i_JavaType ,String i_Name ,Class<?> [] i_ParameterTypes)
    {
        this.javaType       = i_JavaType;
        this.name           = i_Name;
        this.parameterTypes = i_ParameterTypes;
    }
    
    
    
    /**
     * 将本类还原为Java自带的 java.lang.reflect.Method
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_Instance
     * @return
     */
    public Method toMethod(Object i_Instance)
    {
        return this.toMethod(i_Instance.getClass());
    }

    
    
    /**
     * 将本类还原为Java自带的 java.lang.reflect.Method
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_Class
     * @return
     */
    public java.lang.reflect.Method toMethod(Class<?> i_Class)
    {
        try
        {
            return i_Class.getMethod(this.name ,this.parameterTypes);
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        return null;
    }
    
    
    
    /**
     * 将本类还原为Java自带的 java.lang.reflect.Method
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-23
     * @version     v1.0
     *
     * @param i_Class
     * @return
     */
    public java.lang.reflect.Method toMethod()
    {
        if ( this.javaType != null )
        {
            return this.toMethod(this.javaType);
        }
        else
        {
            return null;
        }
    }

    
    
    /**
     * 获取：方法的名称（区分大小写）
     */
    public String getName()
    {
        return name;
    }


    
    /**
     * 设置：方法的名称（区分大小写）
     * 
     * @param name 
     */
    public void setName(String name)
    {
        this.name = name;
    }


    
    /**
     * 获取：方法的所有入参的Java元类型
     */
    public Class<?> [] getParameterTypes()
    {
        return parameterTypes;
    }


    
    /**
     * 设置：方法的所有入参的Java元类型
     * 
     * @param parameterTypes 
     */
    public void setParameterTypes(Class<?> [] parameterTypes)
    {
        this.parameterTypes = parameterTypes;
    }


    
    /**
     * 获取：方法所在的Java类的类型
     */
    public Class<?> getJavaType()
    {
        return javaType;
    }
    

    
    /**
     * 设置：方法所在的Java类的类型
     * 
     * @param javaType 
     */
    public void setJavaType(Class<?> javaType)
    {
        this.javaType = javaType;
    }
    
    
    
    public int hashCode()
    {   
        int v_HashCode = 0;
        
        if ( !Help.isNull(this.name) )
        {
            v_HashCode += this.name.hashCode() * 10000;
        }
        
        if ( !Help.isNull(this.parameterTypes) )
        {
            for (Class<?> v_Class : this.parameterTypes)
            {
                v_HashCode += v_Class.hashCode() * 1000;
            }
        }
        
        if ( this.javaType != null )
        {
            v_HashCode += this.javaType.hashCode();
        }
        
        return v_HashCode;
    }



    @Override
    public int compareTo(MethodInfo i_Other)
    {
        if ( i_Other == null )
        {
            return 1;
        }
        else if ( this == i_Other )
        {
            return 0;
        }
        else if ( this.name.equals(i_Other.getName()) && this.javaType == i_Other.getJavaType() )
        {
            if ( Help.isNull(this.parameterTypes) )
            {
                if ( Help.isNull(i_Other.getParameterTypes()) )
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
            else if ( Help.isNull(i_Other.getParameterTypes()) )
            {
                return 1;
            }
            else 
            {
                for (int i=0; i<this.parameterTypes.length; i++)
                {
                    if ( this.parameterTypes[i] != i_Other.getParameterTypes()[i] )
                    {
                        return 1;
                    }
                }
                
                return 0;
            }
        }
        else 
        {
            return this.hashCode() - i_Other.hashCode();
        }
    }
    
    
    
    @Override
    public boolean equals(Object i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else if ( this == i_Other )
        {
            return true;
        }
        else if ( i_Other instanceof MethodInfo )
        {
            return this.compareTo((MethodInfo)i_Other) == 0;
        }
        else if ( i_Other instanceof Method )
        {
            return this.compareTo(new MethodInfo((Method)i_Other)) == 0;
        }
        else 
        {
            return false;
        }
    }
    
}
