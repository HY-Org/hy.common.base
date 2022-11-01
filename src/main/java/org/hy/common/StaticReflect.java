package org.hy.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;





/**
 * 静态字段(成员属性)的反射。
 * 
 * 1. 可实现 java.sql.Types.VARCHAR 全路径的解释，并获取静态的值
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2013-08-01
 *              2019-05-27  v2.0  添加：执行静态方法
 */
public final class StaticReflect
{
    /** 静态字段(成员属性)所属的Java类 */
    private Class<?>  classObj;
    
    /** 静态字段(成员属性)的Field对象 */
    private Field     staticField;
    
    /** 静态字段(成员属性)的值 */
    private Object    staticValue;
    
    
    
    /**
     * 获取枚举类型的所有枚举值
     * 
     * @param i_EnumClass  枚举类型
     * @return
     */
    public static Enum<?> [] getEnums(Class<? extends Enum<?>> i_EnumClass)
    {
        Enum<?> [] v_Ret = null;
        
        try
        {
            Method v_Method_Values = i_EnumClass.getMethod("values");
        
            v_Ret = (Enum [])v_Method_Values.invoke(null);
        }
        catch (Exception exce)
        {
            v_Ret = new Enum[]{};
        }
        
        return v_Ret;
    }
    
    
    
    public static Object getStaticValue(String i_StaticURL)
    {
        try
        {
            StaticReflect v_StaticReflect = new StaticReflect(i_StaticURL);
            
            return v_StaticReflect.getStaticValue();
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        return null;
    }
    
    
    
    /**
     * 执行静态方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-05-27
     * @version     v1.0
     *
     * @param i_StaticMethod               静态方法
     * @param i_ParameterTypes             静态方法的参数
     * @return                             返回静态方法的返回值 
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object invoke(Method i_StaticMethod ,Object ... i_Parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return i_StaticMethod.invoke(null ,i_Parameters);
    }
    
    
    
    public StaticReflect(String i_StaticURL) throws IllegalAccessException, ClassNotFoundException, IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        if ( Help.isNull(i_StaticURL) )
        {
            throw new NullPointerException("Static URL is null.");
        }
        
        this.parser(i_StaticURL.trim());
    }
    
    
    
    /**
     * 解释
     * 
     * @param i_StaticURL
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    private void parser(String i_StaticURL) throws IllegalAccessException, ClassNotFoundException, IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        String [] v_StaticURLArr = i_StaticURL.replace("." ,"@").split("@");
        
        if ( v_StaticURLArr.length < 2 )
        {
            throw new IllegalAccessException("Static URL[" + i_StaticURL + "] is not Valid.");
        }
        
        
        // 解释出 Class 类型名称及静态字段的名称
        StringBuilder v_ClassName  = new StringBuilder();
        String        v_StaticName = v_StaticURLArr[v_StaticURLArr.length-1];
        for (int i=0; i<v_StaticURLArr.length-1; i++)
        {
            if ( i > 0 )
            {
                v_ClassName.append(".");
            }
            v_ClassName.append(v_StaticURLArr[i]);
        }
        this.classObj = Help.forName(v_ClassName.toString());
        
        
        Field [] v_FieldArr = this.classObj.getFields();
        for (int i=0; i<v_FieldArr.length; i++)
        {
            Field v_Field = v_FieldArr[i];
            
            // (v_Field.getModifiers() & 8) == 8 静态
            if ( v_Field.getName().equalsIgnoreCase(v_StaticName) )
            {
                this.staticField = v_Field;
                this.staticValue = v_Field.get(this.classObj);
            }
        }
    }
    
    
    
    public Class<?> getClassObj()
    {
        return this.classObj;
    }
    
    
    
    public Object getStaticValue()
    {
        return this.staticValue;
    }


    
    public String getStaticURL()
    {
        if ( this.classObj != null && this.staticField != null )
        {
            return this.classObj.getName() + "." + this.staticField.getName();
        }
        else
        {
            return "";
        }
    }
    
    
    
    public String toString()
    {
        if ( this.staticValue != null )
        {
            return this.staticValue.toString();
        }
        else
        {
            return "";
        }
    }
    
}
