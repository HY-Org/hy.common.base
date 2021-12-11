package org.hy.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;





/**
 * 实例化对象
 * 
 * 注：为了性能，此类所有方法均不考虑入参为NULL的情况
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2021-12-11
 */
public class HelpNewInstance
{
    
    /** 元类型创建实例对象的高速缓存 */
    private static final Map<Class<?> ,Method> $ClassToNew = new HashMap<Class<?> ,Method>();
    
    /** 枚举类型的默认New方法 */
    private static       Method                $ClassToNew_Default_Enum;
    
    
    
    /** 初始化元类型与new方法的映射关系 */
    static
    {
        Method [] v_Methods = HelpNewInstance.class.getMethods();
        
        for (Method v_Method : v_Methods)
        {
            if ( v_Method.getName().startsWith("new") && v_Method.getParameterTypes().length == 1 && v_Method.getParameterTypes()[0] == Class.class )
            {
                $ClassToNew.put(v_Method.getReturnType() ,v_Method);
                
                if ( v_Method.getReturnType() == Enum.class )
                {
                    $ClassToNew_Default_Enum = v_Method;
                }
            }
        }
    }
    
    
    
    /**
     * 匹配new方法
     * 
     * 兜兜转转一圈，只为了优化之前多if语句的判定 和 易扩展性的设计。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Method findNew(Class<?> i_Class)
    {
        Method v_Ret = $ClassToNew.get(i_Class);
        
        if ( v_Ret == null )
        {
            if ( MethodReflect.isExtendImplement(i_Class ,Enum.class) )
            {
                v_Ret = $ClassToNew_Default_Enum;
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
     * @param i_Class  将什么类型实例化
     * @return
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object executeNew(Class<?> i_Class) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        Method v_NewMethod = findNew(i_Class);
        
        if ( v_NewMethod == null )
        {
            return i_Class.newInstance();
        }
        else
        {
            return v_NewMethod.invoke(null ,new Object[] {i_Class});
        }
    }
    
    
    
    /**
     * 实例化String
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static String newString(Class<?> i_Class)
    {
        return new String();
    }
    
    
    
    /**
     * 实例化boolean
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static boolean newboolean(Class<?> i_Class)
    {
        return false;
    }
    

    
    /**
     * 实例化Boolean
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Boolean newBoolean(Class<?> i_Class)
    {
        return Boolean.FALSE;
    }
    
    
    
    /**
     * 实例化Enum
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Enum<?> newEnum(Class<?> i_Class)
    {
        Enum<?> [] v_EnumValues = StaticReflect.getEnums((Class<? extends Enum<?>>) i_Class);
        
        return v_EnumValues[0];
    }
    
    
    
    /**
     * 实例化byte
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static byte newbyte(Class<?> i_Class)
    {
        return 0;
    }
    
    
    
    /**
     * 实例化Byte
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Byte newByte(Class<?> i_Class)
    {
        return new Byte((byte)0);
    }
    
    
    
    /**
     * 实例化char
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static char newchar(Class<?> i_Class)
    {
        return ' ';
    }
    
    
    
    /**
     * 实例化Character
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static char newCharacter(Class<?> i_Class)
    {
        return new Character(' ');
    }
    
    
    
    /**
     * 实例化short
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static short newshort(Class<?> i_Class)
    {
        return 0;
    }
    
    
    
    /**
     * 实例化Short
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Short newShort(Class<?> i_Class)
    {
        return new Short((short)0);
    }
    
    
    
    /**
     * 实例化int
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static int newint(Class<?> i_Class)
    {
        return 0;
    }
    
    
    
    /**
     * 实例化Integer
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Integer newInteger(Class<?> i_Class)
    {
        return new Integer(0);
    }
    
    
    
    /**
     * 实例化long
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static long newlong(Class<?> i_Class)
    {
        return 0L;
    }
    
    
    
    /**
     * 实例化Long
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Long newLong(Class<?> i_Class)
    {
        return new Long(0);
    }
    
    
    
    /**
     * 实例化double
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static double newdouble(Class<?> i_Class)
    {
        return 0D;
    }
    
    
    
    /**
     * 实例化Double
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Double newDouble(Class<?> i_Class)
    {
        return new Double(0);
    }
    
    
    
    /**
     * 实例化float
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static float newfloat(Class<?> i_Class)
    {
        return 0F;
    }
    
    
    
    /**
     * 实例化Float
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Float newFloat(Class<?> i_Class)
    {
        return new Float(0);
    }
    
    
    
    /**
     * 实例化BigDecimal
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static BigDecimal newBigDecimal(Class<?> i_Class)
    {
        return new BigDecimal(0);
    }
    
    
    
    /**
     * 实例化Date
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Date newDate(Class<?> i_Class)
    {
        return new Date();
    }
    
    
    
    /**
     * 实例化java.util.Date
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static java.util.Date newjavautilDate(Class<?> i_Class)
    {
        return new Date().getDateObject();
    }
    
    
    
    /**
     * 实例化java.sql.Date
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static java.sql.Date newjavasqlDate(Class<?> i_Class)
    {
        return new Date().getSQLDate();
    }
    
    
    
    /**
     * 实例化Timestamp
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Timestamp newTimestamp(Class<?> i_Class)
    {
        return new Date().getSQLTimestamp();
    }
    
    
    
    /**
     * 实例化Class
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-11
     * @version     v1.0
     * 
     * @param i_Class  将什么类型实例化
     * @return
     */
    public static Class<?> newClass(Class<?> i_Class)
    {
        return i_Class;
    }
    
    
    
    private HelpNewInstance()
    {
        // 私有的
    }
    
}
