package org.hy.common;

import java.lang.reflect.Field;
import java.util.Map;





/**
 * 成员属性的反射。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-11-24
 * @version     v1.0
 */
public class FieldReflect
{
    
    
    /**
     * 按属性名称（不区别大小写），获取成员属性对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-24
     * @version     v1.0
     *
     * @param i_Instance   实例对象
     * @param i_FieldName  成员属性名称（不区别大小写）
     * @return             找到不时，返回空
     */
    public static Field get(Object i_Instance ,String i_FieldName)
    {
        return get(i_Instance.getClass() ,i_FieldName);
    }
    
    
    
    /**
     * 按属性名称（不区别大小写），获取成员属性对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-24
     * @version     v1.0
     *
     * @param i_Class      Java类型
     * @param i_FieldName  成员属性名称（不区别大小写）
     * @return             找到不时，返回空
     */
    public static Field get(Class<?> i_Class ,String i_FieldName)
    {
        try
        {
            // 首先尝试名称全匹配
            return i_Class.getDeclaredField(i_FieldName);
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        Field [] v_Fields = i_Class.getDeclaredFields();
        
        if ( !Help.isNull(v_Fields) )
        {
            for (Field v_Field : v_Fields)
            {
                if ( v_Field.getName().equalsIgnoreCase(i_FieldName) )
                {
                    return v_Field;
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 对成员属性赋值。
     * 
     * 可将字符串表达形式的任何值，转换为真实的Java类型后，对成员属性赋值。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-24
     * @version     v1.0
     *
     * @param i_Field      成员属性
     * @param i_Instance   实例对象
     * @param i_Value      可以为字符串表达形式的任何值。如下情况
     *                         1. i_Value = "true"，当成员属性为Boolean类型时，将转为 true 进行赋值。
     *                         2. i_Value = "true"，当成功属性为String 类型时，将转为"true"进行赋值。
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static void set(Field i_Field ,Object i_Instance ,Object i_Value) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException
    {
        set(i_Field ,i_Instance ,i_Value ,null);
    }
    
    
    
    /**
     * 对成员属性赋值。
     * 
     * 可将字符串表达形式的任何值，转换为真实的Java类型后，对成员属性赋值。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-24
     * @version     v1.0
     *
     * @param i_Field      成员属性
     * @param i_Instance   实例对象
     * @param i_Value      可以为字符串表达形式的任何值。如下情况
     *                         1. i_Value = "true"，当成员属性为Boolean类型时，将转为 true 进行赋值。
     *                         2. i_Value = "true"，当成功属性为String 类型时，将转为"true"进行赋值。
     * @param i_Replaces   须替换的字符。注：只对String、Class两类型生效。
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static void set(Field i_Field ,Object i_Instance ,Object i_Value ,Map<String ,String> i_Replaces) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException
    {
        Class<?> v_ClassType  = i_Field.getType();
        boolean  v_Accessible = i_Field.isAccessible();
        
        if ( !v_Accessible )
        {
            i_Field.setAccessible(!v_Accessible);
        }
        
        // 这里只对String、枚举、日期等特殊的类进行处理，其它的都是类型，而不是类
        if ( String.class == v_ClassType )
        {
            if ( i_Value != null )
            {
                i_Field.set(i_Instance ,StringHelp.replaceAll(i_Value.toString() ,i_Replaces ,false));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( MethodReflect.isExtendImplement(v_ClassType ,Enum.class) )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                Enum<?> [] v_EnumValues = StaticReflect.getEnums((Class<? extends Enum<?>>) v_ClassType);
                boolean    v_EnumOK     = false;
                
                // ZhengWei(HY) Add 2017-10-31  支持枚举名称的匹配 
                for (Enum<?> v_Enum : v_EnumValues)
                {
                    if ( v_Enum.name().equalsIgnoreCase(i_Value.toString().trim()) )
                    {
                        i_Field.set(i_Instance ,v_Enum);
                        v_EnumOK = true;
                        break;
                    }
                }
                
                // 尝试用枚举值匹配 
                if ( !v_EnumOK && Help.isNumber(i_Value.toString()) )
                {
                    int v_ParamValueInt = Integer.parseInt(i_Value.toString().trim());
                    if ( 0 <= v_ParamValueInt && v_ParamValueInt < v_EnumValues.length )
                    {
                        i_Field.set(i_Instance ,v_EnumValues[v_ParamValueInt]);
                    }
                }
            }
        }
        else if ( Date.class == v_ClassType )
        {
            // 以下每个if分支的空判定，只能写在此处，不能统一提炼，预防Java对象.toString()是空的情况。
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,new Date(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( java.util.Date.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,(new Date(i_Value.toString().trim()).getDateObject()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( int.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Integer.parseInt(i_Value.toString().trim()));
            }
        }
        else if ( Integer.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Integer.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( boolean.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Boolean.parseBoolean(i_Value.toString().trim()));
            }
        }
        else if ( Boolean.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Boolean.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( double.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Double.parseDouble(i_Value.toString().trim()));
            }
        }
        else if ( Double.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Double.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( float.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Float.parseFloat(i_Value.toString().trim()));
            }
        }
        else if ( Float.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Float.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( long.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Long.parseLong(i_Value.toString().trim()));
            }
        }
        else if ( Long.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Long.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( short.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Short.parseShort(i_Value.toString().trim()));
            }
        }
        else if ( Short.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Short.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( byte.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Byte.parseByte(i_Value.toString().trim()));
            }
        }
        else if ( Byte.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Byte.valueOf(i_Value.toString().trim()));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( char.class == v_ClassType )
        {
            // 此不要加 .trim() 方法
            if ( i_Value != null && i_Value.toString() != null && !"".equals(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,i_Value.toString().charAt(0));
            }
        }
        else if ( Character.class == v_ClassType )
        {
            // 此不要加 .trim() 方法
            if ( i_Value != null && i_Value.toString() != null && !"".equals(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,i_Value.toString().charAt(0));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else if ( Class.class == v_ClassType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_Field.set(i_Instance ,Help.forName(StringHelp.replaceAll(i_Value.toString().trim() ,i_Replaces ,false)));
            }
            else
            {
                i_Field.set(i_Instance ,null);
            }
        }
        else
        {
            i_Field.set(i_Instance ,i_Value);
        }
        
        if ( !v_Accessible )
        {
            i_Field.setAccessible(v_Accessible);
        }
    }
    
}
