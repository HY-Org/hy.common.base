package org.hy.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;





/**
 * 简单字符串转对象
 * 
 * 注：为了性能，此类所有方法均不考虑入参为NULL的情况
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2021-12-11
 */
public class HelpToObject
{
    
    /** 元类型创建实例对象的高速缓存 */
    private static final Map<Class<?> ,Method> $ClassToObject = new HashMap<Class<?> ,Method>();
    
    /** 枚举类型的默认toObject方法 */
    private static       Method                $ClassToObject_Default_Enum;
    
    
    
    /** 初始化元类型与new方法的映射关系 */
    static
    {
        Method [] v_Methods = HelpToObject.class.getMethods();
        
        for (Method v_Method : v_Methods)
        {
            if ( v_Method.getName().startsWith("new") && v_Method.getParameterTypes().length == 2 && v_Method.getParameterTypes()[0] == Class.class )
            {
                $ClassToObject.put(v_Method.getReturnType() ,v_Method);
                
                if ( v_Method.getReturnType() == Enum.class )
                {
                    $ClassToObject_Default_Enum = v_Method;
                }
            }
        }
    }
    
    
    
    /**
     * 匹配toObject方法
     * 
     * 兜兜转转一圈，只为了优化之前多if语句的判定 和 易扩展性的设计。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Method findToObject(Class<?> i_Class ,String i_Value)
    {
        Method v_Ret = $ClassToObject.get(i_Class);
        
        if ( v_Ret == null )
        {
            if ( MethodReflect.isExtendImplement(i_Class ,Enum.class) )
            {
                v_Ret = $ClassToObject_Default_Enum;
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 匹配并执行new方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object executeToObject(Class<?> i_Class ,String i_Value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        Method v_NewMethod = findToObject(i_Class ,i_Value);
        
        if ( v_NewMethod == null )
        {
            return i_Class.newInstance();
        }
        else
        {
            return v_NewMethod.invoke(null ,new Object[] {i_Class ,i_Value});
        }
    }
    
    
    
    /**
     * 实例化String
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static String newString(Class<?> i_Class ,String i_Value)
    {
        return i_Value;
    }
    
    
    
    /**
     * 实例化boolean
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static boolean newboolean(Class<?> i_Class ,String i_Value)
    {
        return Boolean.parseBoolean(i_Value.trim());
    }
    

    
    /**
     * 实例化Boolean
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Boolean newBoolean(Class<?> i_Class ,String i_Value)
    {
        return Boolean.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Enum
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Enum<?> newEnum(Class<?> i_Class ,String i_Value)
    {
        Enum<?> [] v_EnumValues = StaticReflect.getEnums((Class<? extends Enum<?>>) i_Class);
        
        // ZhengWei(HY) Add 2018-05-08  支持枚举toString()的匹配
        for (Enum<?> v_Enum : v_EnumValues)
        {
            if ( i_Value.equalsIgnoreCase(v_Enum.toString()) )
            {
                return v_Enum;
            }
        }
        
        // ZhengWei(HY) Add 2018-05-08  支持枚举名称的匹配
        for (Enum<?> v_Enum : v_EnumValues)
        {
            if ( i_Value.equalsIgnoreCase(v_Enum.name()) )
            {
                return v_Enum;
            }
        }
        
        // 尝试用枚举值匹配
        if ( Help.isNumber(i_Value) )
        {
            int v_IntValue = Integer.parseInt(i_Value.trim());
            if ( 0 <= v_IntValue && v_IntValue < v_EnumValues.length )
            {
                return v_EnumValues[v_IntValue];
            }
        }
        
        throw new IndexOutOfBoundsException("Enum [" + i_Class.getName() + "] is not find Value[" + i_Value + "].");
    }
    
    
    
    /**
     * 实例化byte
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static byte newbyte(Class<?> i_Class ,String i_Value)
    {
        return Byte.parseByte(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Byte
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Byte newByte(Class<?> i_Class ,String i_Value)
    {
        return Byte.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化char
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static char newchar(Class<?> i_Class ,String i_Value)
    {
        return i_Value.trim().charAt(0);
    }
    
    
    
    /**
     * 实例化Character
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Character newCharacter(Class<?> i_Class ,String i_Value)
    {
        return Character.valueOf(i_Value.trim().charAt(0));
    }
    
    
    
    /**
     * 实例化short
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static short newshort(Class<?> i_Class ,String i_Value)
    {
        return Short.parseShort(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Short
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Short newShort(Class<?> i_Class ,String i_Value)
    {
        return Short.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化int
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static int newint(Class<?> i_Class ,String i_Value)
    {
        return Integer.parseInt(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Integer
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Integer newInteger(Class<?> i_Class ,String i_Value)
    {
        return Integer.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化long
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static long newlong(Class<?> i_Class ,String i_Value)
    {
        return Long.parseLong(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Long
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Long newLong(Class<?> i_Class ,String i_Value)
    {
        return Long.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化double
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static double newdouble(Class<?> i_Class ,String i_Value)
    {
        return Double.parseDouble(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Double
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Double newDouble(Class<?> i_Class ,String i_Value)
    {
        return Double.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化float
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static float newfloat(Class<?> i_Class ,String i_Value)
    {
        return Float.parseFloat(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Float
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Float newFloat(Class<?> i_Class ,String i_Value)
    {
        return Float.valueOf(i_Value.trim());
    }
    
    
    
    /**
     * 实例化BigDecimal
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static BigDecimal newBigDecimal(Class<?> i_Class ,String i_Value)
    {
        return new BigDecimal(i_Value.trim());
    }
    
    
    
    /**
     * 实例化Date
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Date newDate(Class<?> i_Class ,String i_Value)
    {
        return new Date(i_Value.trim());
    }
    
    
    
    /**
     * 实例化java.util.Date
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static java.util.Date newjavautilDate(Class<?> i_Class ,String i_Value)
    {
        return new Date(i_Value.trim()).getDateObject();
    }
    
    
    
    /**
     * 实例化java.sql.Date
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static java.sql.Date newjavasqlDate(Class<?> i_Class ,String i_Value)
    {
        return new Date(i_Value.trim()).getSQLDate();
    }
    
    
    
    /**
     * 实例化Timestamp
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     */
    public static Timestamp newTimestamp(Class<?> i_Class ,String i_Value)
    {
        return new Date(i_Value.trim()).getSQLTimestamp();
    }
    
    
    
    /**
     * 实例化Class
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将字符串转什么类型
     * @param i_Value  待转换的字符串
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> newClass(Class<?> i_Class ,String i_Value) throws ClassNotFoundException
    {
        return Help.forName(i_Value.trim());
    }
    
    
    
    private HelpToObject()
    {
        // 私有的
    }
    
}
