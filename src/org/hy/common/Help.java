package org.hy.common;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.hy.common.app.Param;





/**
 * Java 编程的辅助类
 * 
 * @author  ZhengWei(HY)
 * @version 1.0  2008-06-17
 *               2017-06-10  1. 替换：division()方法用BigDecimal的除法替换并重新实现
 *                           2. 添加：同时添加加、减、乘三个BigDecimal实现的加法、减法、乘法
 *               2017-06-14  1. 添加：findSames(...)系列方法。用于查找出集合中重复的元素。
 *               2017-06-15  1. 添加：toSort    (? ,String ...)支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
 *                           2. 添加：findSames (? ,String ...)支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
 *                           3. 添加：toDistinct(? ,String ...)支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
 *               2017-07-23  1. 添加：getMacs() 获取本机全部的Mac
 *               2017-09-27  1. 添加：toLike(...)系列方法。可实现SQL语句中的like查询。
 *               2017-10-23  1. 修复：当forName不实例的方式异常时，二次尝试传统模式。
 *               2017-11-20  1. 优化：toMap(Object ...)系列方法，添加高速缓存。对同一对象类型的高频密集性的转换时，能显示提升性能。
 *               2017-12-19  1. 添加：isNull(Object) 方法。
 *               2018-01-04  1. 添加：对加、减、乘、除四个系列的方法，均添加不定参数的支持。使其能多个数字运算。
 *                           2. 添加：插值法（内推法）的interpolation()方法。
 *               2018-01-18  1. 添加：支持BigDecimal类型           
 *               2018-01-26  1. 修复：Arrays.asList()生成的集合是只读，不能对集合进行add()、remove()等修改操作。 
 *
 */
public class Help
{
    
    /** ToMap()方法的高速缓存 */
    private static final ExpireMap<Class<?> ,List<Method>> $ToMapCaches  = new ExpireMap<Class<?> ,List<Method>>();
    
    /** ToMap()方法缓存的超时时长。单位：秒 */
    public  static       long                              $ToMapTimeOut = 30; 
    
    
    
    /**
     * 私有构建器
     */
    protected Help()
    {
        
    }
    
    
    
    /**
     * 判断是否为数字
     * 
     * @param i_Str
     * @return
     */
    public final static boolean isNumber(String i_Str)
    {
        return StringHelp.isNumber(i_Str);
    }
    
    
    
    /**
     * 转为字符串数组
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-01-04
     * @version     v1.0
     *
     * @param i_ValueX
     * @return
     */
    private final static <N extends Number> String[] numbersToStrings(N ... i_ValueX)
    {
        String [] v_ValueX = new String[i_ValueX.length];
        
        for (int i=0; i<i_ValueX.length; i++)
        {
            v_ValueX[i] = i_ValueX[i].toString();
        }
        
        return v_ValueX;
    }
    
    
    
    /**
     * 高精度的加法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX   可变参数使用N时，第三方调用者为有一个Java警告，所以改为Number
     * @return
     */
    public final static <N extends Number> double addition(N i_Value01 ,Number ... i_ValueX)
    {
        return addition(i_Value01.toString() ,numbersToStrings(i_ValueX));
    }
    
    
    
    /**
     * 高精度的加法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double addition(String i_Value01 ,N i_Value02)
    {
        return addition(i_Value01 ,i_Value02.toString());
    }
    
    
    
    /**
     * 高精度的加法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double addition(N i_Value01 ,String i_Value02)
    {
        return addition(i_Value01.toString() ,i_Value02);
    }
    
    
    
    /**
     * 高精度的加法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX
     * @return
     */
    public final static double addition(String i_Value01 ,String ... i_ValueX)
    {
        BigDecimal v_Ret = new BigDecimal(i_Value01.trim());
        
        for (String v_ValueStr : i_ValueX)
        {
            BigDecimal v_Value = new BigDecimal(v_ValueStr.trim());
            
            v_Ret = v_Ret.add(v_Value);
        }
        
        return v_Ret.doubleValue();    
    }
    
    
    
    /**
     * 高精度的减法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX   可变参数使用N时，第三方调用者为有一个Java警告，所以改为Number
     * @return
     */
    public final static <N extends Number> double subtract(N i_Value01 ,Number ... i_ValueX)
    {
        return subtract(i_Value01.toString() ,numbersToStrings(i_ValueX));
    }
    
    
    
    /**
     * 高精度的减法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double subtract(String i_Value01 ,N i_Value02)
    {
        return subtract(i_Value01 ,i_Value02.toString());
    }
    
    
    
    /**
     * 高精度的减法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double subtract(N i_Value01 ,String i_Value02)
    {
        return subtract(i_Value01.toString() ,i_Value02);
    }
    
    
    
    /**
     * 高精度的减法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX
     * @return
     */
    public final static double subtract(String i_Value01 ,String ... i_ValueX)
    {
        BigDecimal v_Ret = new BigDecimal(i_Value01.trim()); 
        
        for (String v_ValueStr : i_ValueX)
        {
            BigDecimal v_Value = new BigDecimal(v_ValueStr.trim());
            
            v_Ret = v_Ret.subtract(v_Value);
        }
        
        return v_Ret.doubleValue();    
    }
    
    
    
    /**
     * 高精度的乘法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX   可变参数使用N时，第三方调用者为有一个Java警告，所以改为Number
     * @return
     */
    public final static <N extends Number> double multiply(N i_Value01 ,Number ... i_ValueX)
    {
        return multiply(i_Value01.toString() ,numbersToStrings(i_ValueX));
    }
    
    
    
    /**
     * 高精度的乘法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double multiply(String i_Value01 ,N i_Value02)
    {
        return multiply(i_Value01 ,i_Value02.toString());
    }
    
    
    
    /**
     * 高精度的乘法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double multiply(N i_Value01 ,String i_Value02)
    {
        return multiply(i_Value01.toString() ,i_Value02);
    }
    
    
    
    /**
     * 高精度的乘法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX
     * @return
     */
    public final static double multiply(String i_Value01 ,String ... i_ValueX)
    {
        BigDecimal v_Ret = new BigDecimal(i_Value01.trim());
        
        for (String v_ValueStr : i_ValueX)
        {
            BigDecimal v_Value = new BigDecimal(v_ValueStr.trim());
            
            v_Ret = v_Ret.multiply(v_Value);
        }
        
        return v_Ret.doubleValue();    
    }
    
    
    
    /**
     * 高精度的除法
     * 
     * 防止被除数为零的情况
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX   可变参数使用N时，第三方调用者为有一个Java警告，所以改为Number
     * @return
     */
    public final static <N extends Number> double division(N i_Value01 ,Number ... i_ValueX)
    {
        return division(i_Value01.toString() ,numbersToStrings(i_ValueX));
    }
    
    
    
    /**
     * 高精度的除法
     * 
     * 防止被除数为零的情况
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double division(String i_Value01 ,N i_Value02)
    {
        return division(i_Value01 ,i_Value02.toString());
    }
    
    
    
    /**
     * 高精度的除法
     * 
     * 防止被除数为零的情况
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *
     * @param i_Value01
     * @param i_Value02
     * @return
     */
    public final static <N extends Number> double division(N i_Value01 ,String i_Value02)
    {
        return division(i_Value01.toString() ,i_Value02);
    }
    
    
    
    /**
     * 高精度的除法
     * 
     * 防止被除数为零的情况
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-10
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Value01
     * @param i_ValueX
     * @return
     */
    public final static double division(String i_Value01 ,String ... i_ValueX)
    {
        return division(9 ,i_Value01 ,i_ValueX);
    }
    
    
    
    /**
     * 高精度的除法
     * 
     * 防止被除数为零的情况
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-13
     * @version     v1.0
     *              v2.0  2018-01-04  支持不定多参
     *
     * @param i_Scale    精度
     * @param i_Value01
     * @param i_ValueX
     * @return
     */
    public final static double division(int i_Scale ,String i_Value01 ,String ... i_ValueX)
    {
        BigDecimal v_Ret = new BigDecimal(i_Value01.trim());
        
        for (String v_ValueStr : i_ValueX)
        {
            BigDecimal v_Value = new BigDecimal(v_ValueStr.trim());
            
            if ( v_Value.compareTo(BigDecimal.ZERO) == 0 )
            {
                return 0;
            }
            
            v_Ret = v_Ret.divide(v_Value ,i_Scale ,BigDecimal.ROUND_HALF_UP);
        }
        
        return v_Ret.doubleValue();    
    }
    
    
    
    /**
     * 插值法（内推法）
     * 
     * ((i_XValue - i_XMin) * (i_YMax - i_YMin) / (i_XMax - i_XMin) ) + i_YMin
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-01-04
     * @version     v1.0
     *
     * @param i_XMin    X系范围的最小值
     * @param i_XMax    X系范围的最大值
     * @param i_YMin    Y系范围的最小值
     * @param i_YMax    Y系范围的最大值
     * @param i_XValue  X系的当前值
     * @return          计算出Y系的当前值（相对于i_XValue）
     */
    public final static <N extends Number> double interpolation(N i_XMin ,N i_XMax ,N i_YMin ,N i_YMax ,N i_XValue)
    {
        return addition(division(multiply(subtract(i_XValue ,i_XMin) ,subtract(i_YMax ,i_YMin)) ,subtract(i_XMax ,i_XMin)) ,i_YMin);
    }
    
    
    
    /**
     * 四舍五入。
     * 
     * 解决Java本身无法完全处理四舍五入的问题
     * 
     * @param i_Value
     * @param i_Digit   保留小数位数
     * @return
     * @see   org.hy.common.xml.junit.JU_Round
     */
    public final static <N extends Number> double round(N i_Value ,int i_Digit) 
    {
        return round(i_Value.toString() ,i_Digit);
    }
    
    
    
    /**
     * 四舍五入。
     * 
     * 解决Java本身无法完全处理四舍五入的问题
     * 
     * @param i_Value
     * @param i_Digit   保留小数位数
     * @return
     * @see   org.hy.common.xml.junit.JU_Round
     */
    public final static double round(String i_Value ,int i_Digit) 
    {
        BigDecimal v_Value = new BigDecimal(i_Value.trim());
                
        if ( v_Value.compareTo(BigDecimal.ZERO) == 0 )
        {
            return 0D;
        }
        
        BigDecimal v_Pow      = new BigDecimal(Math.pow(10d ,i_Digit));
        BigDecimal v_Big      = v_Value.multiply(v_Pow);
        BigDecimal v_Small    = new BigDecimal(Math.floor(v_Big.doubleValue()));
        double     v_Subtract = v_Big.subtract(v_Small).doubleValue();
        
        if ( v_Subtract >= 0.5d )
        {
            v_Small = v_Small.add(new BigDecimal(1d));
        }
        
        v_Small = v_Small.divide(v_Pow);
        
        return v_Small.doubleValue();
        
        // 下面代码无法解决 4.015 四舍五入2位等于 4.02 的目标
        // BigDecimal v_BigDecimal = new BigDecimal(String.valueOf(i_Value));
        // return v_BigDecimal.setScale(i_Digit ,BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
    
    
    
    /**
     * 生成指定范围的随机数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-03-05
     * @version     v1.0
     *
     * @param i_Max  最大值
     * @return       生成随机数据范围为：0 <= random <= i_Max
     */
    public final static int random(int i_Max)
    {
        return random(0 ,i_Max);
    }
    
    
    
    /**
     * 生成指定范围的随机数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-03-05
     * @version     v1.0
     *
     * @param i_Min  最小值
     * @param i_Max  最大值
     * @return       生成随机数据范围为：i_Min <= random <= i_Max
     */
    public final static int random(int i_Min ,int i_Max)
    {
        int    v_Min    = i_Min < 0 ? 0 : i_Min;
        int    v_Max    = Math.abs(i_Max);
        Random v_Random = new Random();
        
        if ( v_Max == 0 )
        {
            v_Max = Integer.MAX_VALUE;
        }
        
        if ( v_Max <= v_Min )
        {
            return v_Random.nextInt(v_Max + 1);
        }
        else if ( v_Min == 0 )
        {
            return v_Random.nextInt(v_Max + 1);
        }
        else
        {
            return v_Random.nextInt(v_Max) % (v_Max - v_Min + 1) + v_Min;
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(int [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(long [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(double [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(float [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(char [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(short [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Array
     */
    public final static void print(byte [] i_Array)
    {
        if ( !Help.isNull(i_Array) )
        {
            for (int v_Index=0; v_Index<i_Array.length; v_Index++)
            {
                System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Array[v_Index]);
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_List
     */
    public final static void print(Object [] i_Objs)
    {
        if ( !Help.isNull(i_Objs) )
        {
            for (int v_Index=0; v_Index<i_Objs.length; v_Index++)
            {
                if ( i_Objs[v_Index] == null )
                {
                    System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," "));
                }
                else
                {
                    System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_Objs[v_Index].toString());
                }
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_List
     */
    public final static void print(Collection<?> i_List)
    {
        if ( !Help.isNull(i_List) )
        {
            Iterator<?> v_Iter  = i_List.iterator();
            int         v_Index = 0;
            
            while ( v_Iter.hasNext() )
            {
                Object v_Value = v_Iter.next();
                
                if ( v_Value == null )
                {
                    System.out.print("-- " + StringHelp.rpad(v_Index++ ,6 ," "));
                }
                else
                {
                    System.out.println("-- " + StringHelp.rpad(v_Index++ ,6 ," ") + v_Value.toString());
                }
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_List
     */
    public final static void print(List<?> i_List)
    {
        if ( !Help.isNull(i_List) )
        {
            for (int v_Index=0; v_Index<i_List.size(); v_Index++)
            {
                Object v_Value = i_List.get(v_Index);
                
                if ( v_Value == null )
                {
                    System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," "));
                }
                else
                {
                    System.out.println("-- " + StringHelp.rpad(v_Index ,6 ," ") + i_List.get(v_Index).toString());
                }
            }
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Map
     */
    public final static void print(Map<? ,?> i_Map)
    {
        if ( !Help.isNull(i_Map) )
        {
            int v_Index = 0;
            
            for (Entry<? ,?> v_Data : i_Map.entrySet())
            {
                if ( v_Data.getKey() == null )
                {
                    System.out.println("-- " + StringHelp.rpad(++v_Index ,6 ," "));
                }
                else
                {
                    if ( v_Data.getValue() == null )
                    {
                        System.out.println("-- " + StringHelp.rpad(++v_Index ,6 ," ") + v_Data.getKey().toString() + " -> ");
                    }
                    else
                    {
                        System.out.println("-- " + StringHelp.rpad(++v_Index ,6 ," ") + v_Data.getKey().toString() + " -> " + v_Data.getValue().toString());
                    }
                }
            }
            
        }
    }
    
    
    
    /**
     * 向控制台打印信息--就是为了方便
     * 
     * @param i_Set
     */
    public final static void print(Set<?> i_Set)
    {
        if ( !Help.isNull(i_Set) )
        {
            int         v_Index = 0;
            Iterator<?> v_Iter  = i_Set.iterator();
            
            while ( v_Iter.hasNext() )
            {
                Object v_Value = v_Iter.next();
                
                if ( v_Value == null )
                {
                    System.out.println("-- " + StringHelp.rpad(++v_Index ,6 ," "));
                }
                else
                {
                    System.out.println("-- " + StringHelp.rpad(++v_Index ,6 ," ") + v_Value.toString());
                }
            }
        }
    }
    
    
    
    /**
     * 模拟于Oracle中的 NVL() 函数
     *
     * @param i_Str    被判断的字符串
     * @return String  当i_Str为空时，返回 "" ，其它情况返回 i_Str
     */
    public final static String NVL(String i_Str)
    {
        if ( i_Str == null || "".equals(i_Str.trim()) )
        {
            return "";
        }
        
        return i_Str;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Obj
     * @return Object       
     */
    public final static Object NVL(Object i_Obj)
    {
        if ( i_Obj == null )
        {
            return "";
        }
        
        return i_Obj;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Obj
     * @return Object       
     */
    public final static Boolean NVL(Boolean i_Value)
    {
        if ( i_Value == null )
        {
            return Boolean.FALSE;
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Object       
     */
    public final static Object NVL(Short i_Value)
    {
        if ( i_Value == null )
        {
            return new Short((short) 0);
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Object       
     */
    public final static Object NVL(Byte i_Value)
    {
        if ( i_Value == null )
        {
            return new Byte((byte)0);
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Object       
     */
    public final static Object NVL(Character i_Value)
    {
        if ( i_Value == null )
        {
            return new Character(' ');
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Date
     * @return Object       
     */
    public final static Object NVL(java.util.Date i_Date)
    {
        if ( i_Date == null )
        {
            return "";
        }
        
        return i_Date;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Date
     * @return Object       
     */
    public final static Object NVL(org.hy.common.Date i_Date)
    {
        if ( i_Date == null )
        {
            return "";
        }
        
        return i_Date;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Double       
     */
    public final static Double NVL(Double i_Value)
    {
        if ( i_Value == null )
        {
            return 0d;
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Double       
     */
    public final static Long NVL(Long i_Value)
    {
        if ( i_Value == null )
        {
            return 0L;
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Float       
     */
    public final static Float NVL(Float i_Value)
    {
        if ( i_Value == null )
        {
            return 0f;
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @return Integer       
     */
    public final static Integer NVL(Integer i_Value)
    {
        if ( i_Value == null )
        {
            return 0;
        }
        
        return i_Value;
    }
    
    
    
    /**
     * 重载 NVL 方法
     * @param <T>
     *
     * @param i_List
     * @return Object       
     */
    public final static <T> Collection<T> NVL(Collection<T> i_List)
    {
        if ( i_List == null )
        {
            return new ArrayList<T>();
        }
        
        return i_List;
    }
    
    
    
    /**
     * 重载 NVL 方法
     * @param <T>
     *
     * @param i_List
     * @return Object       
     */
    public final static <T> List<T> NVL(List<T> i_List)
    {
        if ( i_List == null )
        {
            return new ArrayList<T>();
        }
        
        return i_List;
    }
    
    
    
    /**
     * 重载 NVL 方法
     * @param <T1>
     *
     * @param i_Map
     * @return Object       
     */
    public final static <T1 ,T2> Map<T1 ,T2> NVL(Map<T1 ,T2> i_Map)
    {
        if ( i_Map == null )
        {
            return new Hashtable<T1 ,T2>();
        }
        
        return i_Map;
    }
    
    
    
    /**
     * 重载 NVL 方法
     * @param <T>
     *
     * @param i_Set
     * @return Object       
     */
    public final static <T> Set<T> NVL(Set<T> i_Set)
    {
        if ( i_Set == null )
        {
            return new HashSet<T>();
        }
        
        return i_Set;
    }
    
    
    
    /**
     * 模拟于Oracle中的 NVL() 函数
     *
     * @param i_Str         被判断的字符串
     * @param i_ElseReturn  当 i_Str 为空时，返回的值
     * @return String       当i_Str为空时，返回 i_ElseReturn ，其它情况返回 i_Str
     */
    public final static String NVL(String i_Str ,String i_ElseReturn)
    {
        if ( i_Str == null || "".equals(i_Str.trim()) )
        {
            if ( i_ElseReturn == null )
            {
                return "";
            }
            else
            {
                return i_ElseReturn;
            }
        }
        
        return i_Str;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @param i_ElseReturn
     * @return Double       
     */
    public final static Double NVL(Double i_Value ,Double i_ElseReturn)
    {
        if ( i_Value == null)
        {
            if ( i_ElseReturn == null )
            {
                return 0d;
            }
            else
            {
                return i_ElseReturn;
            }
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @param i_ElseReturn
     * @return Double       
     */
    public final static Long NVL(Long i_Value ,Long i_ElseReturn)
    {
        if ( i_Value == null)
        {
            if ( i_ElseReturn == null )
            {
                return 0L;
            }
            else
            {
                return i_ElseReturn;
            }
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @param i_ElseReturn
     * @return Double       
     */
    public final static Float NVL(Float i_Value ,Float i_ElseReturn)
    {
        if ( i_Value == null)
        {
            if ( i_ElseReturn == null )
            {
                return 0f;
            }
            else
            {
                return i_ElseReturn;
            }
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Value
     * @param i_ElseReturn
     * @return Integer       
     */
    public final static Integer NVL(Integer i_Value ,Integer i_ElseReturn)
    {
        if ( i_Value == null)
        {
            if ( i_ElseReturn == null )
            {
                return 0;
            }
            else
            {
                return i_ElseReturn;
            }
        }
        
        return i_Value;
    }
    
    
    /**
     * 重载 NVL 方法
     *
     * @param i_Obj
     * @param i_ElseReturn
     * @return Object       
     */
    public final static <O> O NVL(O i_Obj ,O i_ElseReturn)
    {
        if ( i_Obj == null)
        {
            return i_ElseReturn;
        }
        
        return i_Obj;
    }
    
    
    
    /**
     * 判断对象(字符串、List、Set、Map)是否为空
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-19
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public final static boolean isNull(Object i_Value)
    {
        if ( i_Value == null )
        {
            return true;
        }
        else if ( i_Value instanceof String )
        {
            return Help.isNull(i_Value.toString());
        }
        else if ( i_Value instanceof List )
        {
            return Help.isNull((List<?>)i_Value);
        }
        else if ( i_Value instanceof Map )
        {
            return Help.isNull((Map<? ,?>)i_Value);
        }
        else if ( i_Value instanceof Set )
        {
            return Help.isNull((Set<?>)i_Value);
        }
        else if ( i_Value instanceof Collection )
        {
            return Help.isNull((Collection<?>)i_Value);
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * 判断字符串是否为空
     *
     * @param i_Str
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(String i_Str)
    {
        if ( i_Str == null || "".equals(i_Str.trim()) )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断时间是否为空
     *
     * @param i_Date
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(java.util.Date i_Date)
    {
        if ( i_Date == null )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断时间是否为空
     *
     * @param i_Date
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(org.hy.common.Date i_Date)
    {
        if ( i_Date == null )
        {
            return true;
        }
        
        return false;
    }
    
    
    
    /**
     * 判断集合对象是否为空
     *
     * @param i_List
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(Collection<?> i_List)
    {
        if ( i_List == null || i_List.isEmpty() )
        {
            return true;
        }
        
        return false;
    }
    
    
    
    /**
     * 判断集合对象是否为空
     *
     * @param i_List
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static <L extends List<?>> boolean isNull(L i_List)
    {
        if ( i_List == null || i_List.isEmpty() )
        {
            return true;
        }
        
        return false;
    }
    
    
    
    /**
     * 判断集合对象是否为空
     *
     * @param i_Map
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static <M extends Map<? ,?>> boolean isNull(M i_Map)
    {
        if ( i_Map == null || i_Map.isEmpty() )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断集合对象是否为空
     *
     * @param i_Set
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static <S extends Set<?>> boolean isNull(S i_Set)
    {
        if ( i_Set == null || i_Set.isEmpty() )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断int数组是否为空
     *
     * @param  int [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(int [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断long数组是否为空
     *
     * @param  long [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(long [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断double数组是否为空
     *
     * @param  double [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(double [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断float数组是否为空
     *
     * @param  float [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(float [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断short数组是否为空
     *
     * @param  short [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(short [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断char数组是否为空
     *
     * @param  char [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(char [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断byte数组是否为空
     *
     * @param  byte [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static boolean isNull(byte [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断字符型数组是否为空
     * @param <T>
     *
     * @param  T [] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static <T> boolean isNull(T [] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断字符型数组是否为空
     * @param <T>
     *
     * @param  T [][] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static <T> boolean isNull(T [][] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 判断字符型数组是否为空
     * @param <T>
     *
     * @param  T [][][] args
     * @return boolean  为空返回 True，其它返回 False
     */
    public final static <T> boolean isNull(T [][][] args)
    {
        if ( args == null || args.length == 0 )
        {
            return true;
        }
        
        return false;
    }
    
    
    /**
     * 获取操作系统的文件路径的分割符
     * 
     * @return
     */
    public final static String getSysPathSeparator()
    {
        return System.getProperty("file.separator");
    }
    
    
    /**
     * 获取操作系统的当前目录
     * 
     * @return
     */
    public final static String getSysCurrentPath()
    {
        return System.getProperty("user.dir");
    }
    
    
    
    /**
     * 获取当前实例对象的class目录位置。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-05-10
     * @version     v1.0
     *
     * @param i_Obj
     * @return
     */
    public final static String getClassPath(Object i_Obj)
    {
        try
        {
            return i_Obj.getClass().getResource("").getFile().toString();
        }
        catch (Exception exce)
        {
            // 在手机设备上必报错，在电脑上不报错，因此不再输出异常信息
            // exce.printStackTrace();
        }
        
        return "";
    }
    
    
    /**
     * 获取class目录位置。
     * 如 C:/xx/bin/
     * 
     * @return
     */
    public final static String getClassHomePath()
    {
        try
        {
            return Thread.currentThread().getContextClassLoader().getResource("").getFile().toString();
        }
        catch (Exception exce)
        {
            // 在手机设备上必报错，在电脑上不报错，因此不再输出异常信息
            // exce.printStackTrace();
        }
        
        return "";
    }
    
    
    
    /**
     * 获取Web服务的class目录位置。
     * 如 C:/Tomcat/Webapps/Web项目名称/WEB-INF/classes/
     * 
     * @return
     */
    public final static String getWebClassPath()
    {
        // Thread.currentThread().getContextClassLoader().getResource("").getFile().toString()
        // Help.class.getResource("/").getFile().toString();
        try
        {
            String v_Ret = Thread.currentThread().getContextClassLoader().getResource("").getFile().toString();
            if ( v_Ret.indexOf(":") >= 0 )
            {
                return v_Ret.substring(1);
            }
            else
            {
                return v_Ret;
            }
        }
        catch (Exception exce)
        {
            // 在手机设备上必报错，在电脑上不报错，因此不再输出异常信息
            // exce.printStackTrace();
        }
        
        return "";
    }
    
    
    
    /**
     * 获取Web服务的WEB-INF目录位置。
     * 如 C:/Tomcat/Webapps/Web项目名称/WEB-INF/
     * 
     * @return
     */
    public final static String getWebINFPath()
    {
        return StringHelp.replaceAll(Help.getWebClassPath() ,"classes/" ,"");
    }
    
    
    
    /**
     * 获取Web服务的根目录位置。
     * 如 C:/Tomcat/Webapps/Web项目名称/
     * 
     * @return
     */
    public final static String getWebHomePath()
    {
        return StringHelp.replaceAll(Help.getWebClassPath() ,"WEB-INF/classes/" ,"");
    }
    
    
    
    /**
     * 获取操作系统的行分隔符
     * 
     * @return
     */
    public final static String getSysLineSeparator()
    {
        return System.getProperty("line.separator");
    }
    
    
    
    /**
     * 获取操作系统的临时文件夹目录
     * 
     * @return
     */
    public final static String getSysTempPath()
    {
        return System.getProperty("java.io.tmpdir");
    }
    
    
    
    /**
     * 获得本机IP
     * 
     * @param i_Split  分割符中多少
     * @param i_IsPad  是否填充 0
     * @return
     */
    public final static String getIP(String i_Split ,boolean i_IsPad)
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        try
        {
            InetAddress v_InetAddress = InetAddress.getLocalHost();
            byte []     v_IPAddrArr   = v_InetAddress.getAddress();
            int         i             = 0;
            int         v_IPByte      = 0;
            
            while ( i < v_IPAddrArr.length )
            {
                v_IPByte = v_IPAddrArr[i];
                
                if ( v_IPByte < 0 )
                {
                    v_IPByte = 256 + v_IPByte;
                }
                
                if ( i == 0 )
                {
                    v_Buffer.append(v_IPByte);
                }
                else
                {
                    v_Buffer.append(i_Split);
                    if ( i_IsPad )
                    {
                        v_Buffer.append(StringHelp.lpad(v_IPByte ,3 ,"0"));
                    }
                    else
                    {
                        v_Buffer.append(v_IPByte);
                    }
                }
                
                i++;
            }
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 获得本机IP
     * 
     * @return
     */
    public final static String getIP(boolean i_IsPad)
    {
        return getIP("." ,i_IsPad);
    }
    
    
    
    /**
     * 获得本机IP
     * 
     * @return
     */
    public final static String getIPN()
    {
        return getIP("" ,true);
    }
    
    
    
    /**
     * 获得本机IP
     * 
     * @return
     */
    public final static String getIP()
    {
        return getIP("." ,false);
    }
    
    
    
    /**
     * 获得本机全部IP
     * 
     * @return
     */
    public final static String getIPs()
    {
        StringBuilder  v_Ret           = new StringBuilder();
        Enumeration<?> v_NetInterfaces = null;
        InetAddress    v_IP            = null;
        try
        {
            v_NetInterfaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (java.net.SocketException e)
        {
            return v_Ret.toString();
        }
        
        
        while ( v_NetInterfaces.hasMoreElements() )
        {
            NetworkInterface v_NetInterface = (NetworkInterface) v_NetInterfaces.nextElement();
            Enumeration<?>   v_Addresses    = v_NetInterface.getInetAddresses();
            int              v_Count        = 0;
            
            v_Ret.append(v_NetInterface.getName()).append("=");
            
            while ( v_Addresses.hasMoreElements() )
            {
                v_IP = (InetAddress) v_Addresses.nextElement();
                if ( v_IP != null && v_IP instanceof Inet4Address )
                {
                    v_Ret.append(v_IP.getHostAddress());
                    v_Count++;
                    
                    if ( v_Count > 1 )
                    {
                        v_Ret.append(";");
                    }
                }
            }
            
            v_Ret.append(" ");
        }
        
        return v_Ret.toString().trim();
    }
    
    
    
    /**
     * 获取本机全部的Mac。用;分号分隔
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-07-23
     * @version     v1.0
     *
     * @return
     */
    public static String getMacs()
    {
        StringBuilder  v_Ret           = new StringBuilder();
        Enumeration<?> v_NetInterfaces = null;
        int            v_Count         = 0;
        
        try
        {
            v_NetInterfaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (java.net.SocketException e)
        {
            return v_Ret.toString();
        }
        
        while ( v_NetInterfaces.hasMoreElements() )
        {
            NetworkInterface v_NetInterface = (NetworkInterface) v_NetInterfaces.nextElement();
            Enumeration<?>   v_Addresses    = v_NetInterface.getInetAddresses();
            
            while ( v_Addresses.hasMoreElements() )
            {
                InetAddress v_IP  = (InetAddress) v_Addresses.nextElement();;
                byte []     v_Mac = null;
                
                if ( v_IP != null && v_IP instanceof Inet4Address )
                {
                    try
                    {
                        v_Mac = NetworkInterface.getByInetAddress(v_IP).getHardwareAddress();
                    }
                    catch (Exception exce)
                    {
                        return null;
                    }
                    
                    if ( !Help.isNull(v_Mac) )
                    {
                        if ( v_Count >= 1 )
                        {
                            v_Ret.append(";");
                        }
                        
                        // 把Mac地址拼装成String
                        for (int i=0; i<v_Mac.length; i++)
                        {
                            if ( i != 0 )
                            {
                                v_Ret.append("-");
                            }
                            
                            String v_Hex = Integer.toHexString(v_Mac[i] & 0xFF);  // Mac[i] & 0xFF 是为了把byte转化为正整数
                            if ( v_Hex.length() == 1 )
                            {
                                v_Ret.append("0");
                            }
                            v_Ret.append(v_Hex.toUpperCase());
                        }
                        
                        v_Count++;
                    }
                }
            }
        }
        
        return v_Ret.toString();
    }
    
    
    
    /**
     * 判断本机端口是否有效
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @param i_Port      本机端口号
     * @return
     */
    public final static boolean isPortOpen(int i_Port)
    {
        ServerSocket v_Server = Help.getServerSocket(i_Port);
        
        if ( v_Server != null )
        {
            try
            {
                v_Server.close();
            }
            catch (Exception exce)
            {
                // Nothing.
            }
            
            v_Server = null;
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 获取本机服务端的ServerSocket对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @param i_Port  本机的端口号
     * @return
     */
    public final static ServerSocket getServerSocket(int i_Port)
    {
        ServerSocket v_Ret = null;
        try
        {
            v_Ret = new ServerSocket(i_Port);
        }
        catch (Exception exce)
        {
            v_Ret = null;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 判断端口是否有效
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @param i_HostName  主机名称
     * @param i_Port      端口号
     * @return
     */
    public final static boolean isPortOpen(String i_HostName ,int i_Port)
    {
        Socket v_Socket = Help.getSocket(i_HostName ,i_Port);
        
        if ( v_Socket != null )
        {
            try
            {
                v_Socket.close();
            }
            catch (Exception exce)
            {
                // Nothing.
            }
            
            v_Socket = null;
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 通过主机名称和端口，获取Socket对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @param i_HostName  主机名称
     * @param i_Port      端口号
     * @return            异常时返回空
     */
    public final static Socket getSocket(String i_HostName ,int i_Port)
    {
        if ( i_Port <= 0 || i_Port > 65535 )
        {
            throw new IndexOutOfBoundsException("Port isn't between 0 and 65535.");
        }
        
        Socket v_Ret = null;
        try
        {
            InetAddress v_InetAddress = InetAddress.getByName(i_HostName);
            
            if ( v_InetAddress != null )
            {
                v_Ret = new Socket(v_InetAddress ,i_Port);
            }
        }
        catch (Exception exce)
        {
            v_Ret = null;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 通过Java类生成查询SQL
     * 
     * 生成如下格式的查询SQL
     * 
     *   SELECT  FieldName01
     *          ,FieldName02
     *     FROM  ClassName
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @return
     */
    public final static String toSQLSelect(Class<?> i_Class)
    {
        return toSQLSelect(i_Class ,null ,null ,true);
    }
    
    
    
    /**
     * 通过Java类生成查询SQL
     * 
     * 生成如下格式的查询SQL
     * 
     *   SELECT  FieldName01
     *          ,FieldName02
     *     FROM  ClassName
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_TableAlias      表的别名
     * @param i_FirstUpperCase  首字母是否大写
     * @return
     */
    public final static String toSQLSelect(Class<?> i_Class ,boolean i_FirstUpperCase)
    {
        return toSQLSelect(i_Class ,null ,null ,i_FirstUpperCase);
    }
    
    
    
    /**
     * 通过Java类生成查询SQL
     * 
     * 当 i_TableAlias = A 时，生成如下格式的查询SQL
     * 
     *   SELECT  A.FieldName01
     *          ,A.FieldName02
     *     FROM  ClassName  A
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_TableAlias      表的别名
     * @return
     */
    public final static String toSQLSelect(Class<?> i_Class ,String i_TableAlias)
    {
        return toSQLSelect(i_Class ,i_TableAlias ,null ,true);
    }
    
    
    
    /**
     * 通过Java类生成查询SQL
     * 
     * 当 i_TableAlias = A 时，生成如下格式的查询SQL
     * 
     *   SELECT  A.FieldName01
     *          ,A.FieldName02
     *     FROM  ClassName  A
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_TableAlias      表的别名
     * @param i_FirstUpperCase  首字母是否大写
     * @return
     */
    public final static String toSQLSelect(Class<?> i_Class ,String i_TableAlias ,boolean i_FirstUpperCase)
    {
        return toSQLSelect(i_Class ,i_TableAlias ,null ,i_FirstUpperCase);
    }
    
    
    
    /**
     * 通过Java类生成查询SQL
     * 
     * 当 i_TableAlias = A， i_ObjectAlias = HY 时，生成如下格式的查询SQL
     * 
     *   SELECT  A.FieldName01     AS "ObjectAlias.FieldName01"
     *          ,A.FieldName02     AS "ObjectAlias.FieldName02"
     *     FROM  ClassName  A
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_TableAlias      表的别名
     * @param i_ObjectAlias     Java类的名称
     * @return
     */
    public final static String toSQLSelect(Class<?> i_Class ,String i_TableAlias ,String i_ObjectAlias)
    {
        return toSQLSelect(i_Class ,i_TableAlias ,i_ObjectAlias ,true);
    }
    
    
    
    /**
     * 通过Java类生成查询SQL
     * 
     * 当 i_TableAlias = A， i_ObjectAlias = HY 时，生成如下格式的查询SQL
     * 
     *   SELECT  A.FieldName01     AS "ObjectAlias.FieldName01"
     *          ,A.FieldName02     AS "ObjectAlias.FieldName02"
     *     FROM  ClassName  A
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_TableAlias      表的别名
     * @param i_ObjectAlias     Java类的名称
     * @param i_FirstUpperCase  首字母是否大写
     * @return
     */
    public final static String toSQLSelect(Class<?> i_Class ,String i_TableAlias ,String i_ObjectAlias ,boolean i_FirstUpperCase)
    {
        if ( i_Class == null )
        {
            return "";
        }
        
        Map<String ,Method> v_Getters  = MethodReflect.getGetMethodsMS(i_Class);
        StringBuilder       v_Buffer   = new StringBuilder();
        int                 v_Index    = 0;
        int                 v_MaxLen   = 0;
        int                 v_Len      = 0;
        final int           v_SpaceLen = 3;  // Java类的名称与前面的间隔长度                 
        
        if ( Help.isNull(v_Getters) )
        {
            return "";
        }
        
        if ( !Help.isNull(i_ObjectAlias) )
        {
            for (String v_Name : v_Getters.keySet())
            {
                v_Len = v_Name.length();
                
                if ( v_Len > v_MaxLen )
                {
                    v_MaxLen = v_Len;
                }
            }
            v_MaxLen += v_SpaceLen;
        }
        
        if ( Help.isNull(i_TableAlias) )
        {
            if ( Help.isNull(i_ObjectAlias) )
            {
                for (Entry<String ,Method> v_Item : v_Getters.entrySet())
                {
                    String v_Name = i_FirstUpperCase ? StringHelp.toUpperCaseByFirst(v_Item.getKey()) : StringHelp.toLowerCaseByFirst(v_Item.getKey());
                    
                    if ( ++v_Index == 1 )
                    {
                        v_Buffer.append("SELECT  ");
                    }
                    else
                    {
                        v_Buffer.append("       ,");
                    }
                    
                    v_Buffer.append(v_Name).append(Help.getSysLineSeparator());
                }
            }
            else
            {
                for (Entry<String ,Method> v_Item : v_Getters.entrySet())
                {
                    String v_Name = i_FirstUpperCase ? StringHelp.toUpperCaseByFirst(v_Item.getKey()) : StringHelp.toLowerCaseByFirst(v_Item.getKey());
                    
                    if ( ++v_Index == 1 )
                    {
                        v_Buffer.append("SELECT  ");
                    }
                    else
                    {
                        v_Buffer.append("       ,");
                    }
                    
                    v_Buffer.append(StringHelp.rpad(v_Name ,v_MaxLen ," "))
                            .append("\"")
                            .append(i_ObjectAlias)
                            .append(".")
                            .append(v_Name)
                            .append("\"")
                            .append(Help.getSysLineSeparator());
                }
            }
            
            v_Buffer.append("  FROM  ").append(i_Class.getSimpleName());
        }
        else
        {
            if ( Help.isNull(i_ObjectAlias) )
            {
                for (Entry<String ,Method> v_Item : v_Getters.entrySet())
                {
                    String v_Name = i_FirstUpperCase ? StringHelp.toUpperCaseByFirst(v_Item.getKey()) : StringHelp.toLowerCaseByFirst(v_Item.getKey());
                    
                    if ( ++v_Index == 1 )
                    {
                        v_Buffer.append("SELECT  ");
                    }
                    else
                    {
                        v_Buffer.append("       ,");
                    }
                    
                    v_Buffer.append(i_TableAlias).append(".").append(v_Name).append(Help.getSysLineSeparator());
                }
            }
            else
            {
                for (Entry<String ,Method> v_Item : v_Getters.entrySet())
                {
                    String v_Name = i_FirstUpperCase ? StringHelp.toUpperCaseByFirst(v_Item.getKey()) : StringHelp.toLowerCaseByFirst(v_Item.getKey());
                    
                    if ( ++v_Index == 1 )
                    {
                        v_Buffer.append("SELECT  ");
                    }
                    else
                    {
                        v_Buffer.append("       ,");
                    }
                    
                    v_Buffer.append(i_TableAlias)
                            .append(".")
                            .append(StringHelp.rpad(v_Name ,v_MaxLen ," "))
                            .append("AS \"")
                            .append(i_ObjectAlias)
                            .append(".")
                            .append(v_Name)
                            .append("\"")
                            .append(Help.getSysLineSeparator());
                }
            }
            
            v_Buffer.append("  FROM  ").append(i_Class.getSimpleName()).append("  ").append(i_TableAlias);
        }
                
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 通过Java类生成插入SQL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @return
     */
    public final static String toSQLInsert(Class<?> i_Class)
    {
        return toSQLInsert(i_Class ,true);
    }
    
    
    
    /**
     * 通过Java类生成插入SQL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_FirstUpperCase  首字母是否大写
     * @return
     */
    public final static String toSQLInsert(Class<?> i_Class ,boolean i_FirstUpperCase)
    {
        if ( i_Class == null )
        {
            return "";
        }
        
        Map<String ,Method> v_Getters      = MethodReflect.getGetMethodsMS(i_Class);
        StringBuilder       v_BufferInsert = new StringBuilder();
        StringBuilder       v_BufferValues = new StringBuilder();
        int                 v_Index        = 0;
        
        if ( Help.isNull(v_Getters) )
        {
            return "";
        }
        
        v_BufferInsert.append("INSERT  INTO ").append(i_Class.getSimpleName()).append(Help.getSysLineSeparator());
        v_BufferInsert.append("       (").append(Help.getSysLineSeparator());
        v_BufferValues.append("VALUES (").append(Help.getSysLineSeparator());
        
        for (Entry<String ,Method> v_Item : v_Getters.entrySet())
        {
            String v_Name = i_FirstUpperCase ? StringHelp.toUpperCaseByFirst(v_Item.getKey()) : StringHelp.toLowerCaseByFirst(v_Item.getKey());
            
            if ( ++v_Index == 1 )
            {
                v_BufferInsert.append("        ");
                v_BufferValues.append("        ");
            }
            else
            {
                v_BufferInsert.append("       ,");
                v_BufferValues.append("       ,");
            }
            
            v_BufferInsert.append(v_Name).append(Help.getSysLineSeparator());
            
            if (         String.class == v_Item.getValue().getReturnType()
              ||           Date.class == v_Item.getValue().getReturnType()
              || java.util.Date.class == v_Item.getValue().getReturnType() )
            {
                v_BufferValues.append("':").append(v_Name).append("'").append(Help.getSysLineSeparator());
            }
            else
            {
                v_BufferValues.append(" :").append(v_Name).append(Help.getSysLineSeparator());
            }
        }
        
        v_BufferInsert.append("       )").append(Help.getSysLineSeparator());
        v_BufferValues.append("       )");
        
        v_BufferInsert.append(v_BufferValues.toString());
        
        return v_BufferInsert.toString();
    }
    
    
    
    /**
     * 通过Java类生成更新SQL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @return
     */
    public final static String toSQLUpdate(Class<?> i_Class)
    {
        return toSQLUpdate(i_Class ,true);
    }
    
    
    
    /**
     * 通过Java类生成更新SQL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-28
     * @version     v1.0
     *
     * @param i_Class
     * @param i_FirstUpperCase  首字母是否大写
     * @return
     */
    public final static String toSQLUpdate(Class<?> i_Class ,boolean i_FirstUpperCase)
    {
        if ( i_Class == null )
        {
            return "";
        }
        
        Map<String ,Method> v_Getters  = MethodReflect.getGetMethodsMS(i_Class);
        StringBuilder       v_Buffer   = new StringBuilder();
        int                 v_Index    = 0;
        int                 v_MaxLen   = 0;
        int                 v_Len      = 0;
        final int           v_SpaceLen = 1;  // Java类的名称与前面的间隔长度  
        
        if ( Help.isNull(v_Getters) )
        {
            return "";
        }
        
        for (String v_Name : v_Getters.keySet())
        {
            v_Len = v_Name.length();
            
            if ( v_Len > v_MaxLen )
            {
                v_MaxLen = v_Len;
            }
        }
        v_MaxLen += v_SpaceLen;
        
        v_Buffer.append("UPDATE  ").append(i_Class.getSimpleName()).append(Help.getSysLineSeparator());
        
        for (Entry<String ,Method> v_Item : v_Getters.entrySet())
        {
            String v_Name = i_FirstUpperCase ? StringHelp.toUpperCaseByFirst(v_Item.getKey()) : StringHelp.toLowerCaseByFirst(v_Item.getKey());
            
            if ( ++v_Index == 1 )
            {
                v_Buffer.append("   SET  ").append(Help.getSysLineSeparator());
            }
            v_Buffer.append(" <[    ,");
            
            v_Buffer.append(StringHelp.rpad(v_Name ,v_MaxLen ," ")).append("= ");
            
            if (         String.class == v_Item.getValue().getReturnType()
              ||           Date.class == v_Item.getValue().getReturnType()
              || java.util.Date.class == v_Item.getValue().getReturnType() )
            {
                v_Buffer.append("':").append(v_Name).append("'");
                v_Buffer.append(StringHelp.lpad("]>" ,v_MaxLen - v_Name.length() + 4," "));
            }
            else
            {
                v_Buffer.append(" :").append(v_Name);
                v_Buffer.append(StringHelp.lpad("]>" ,v_MaxLen - v_Name.length() + 5 ," "));
            }
            
            v_Buffer.append(Help.getSysLineSeparator());
        }
        
        v_Buffer.append(" WHERE  1 = 2").append(Help.getSysLineSeparator());
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 忽略Map.key的大小写匹配查找到对应的Map.value
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-01-21
     * @version     v1.0
     *
     * @param i_Map
     * @param i_FindKey
     * @return
     */
    public final static <V> V getValueIgnoreCase(Map<? ,V> i_Map ,String i_FindKey)
    {
        // 取消非法验证，保证在外部循环调用时的性能
        /*
        if ( Help.isNull(i_Map) || null == i_FindKey )
        {
            return null;
        }
        */
        
        // 先通过常规方式快速获取一次，如果没有获取到，再忽略大小写的方式遍历。
        V v_Ret = i_Map.get(i_FindKey);
        
        if ( v_Ret == null )
        {
            for (Entry<? ,V> v_Data : i_Map.entrySet())
            {
                if ( null != v_Data.getKey() )
                {
                    if ( v_Data.getKey().toString().equalsIgnoreCase(i_FindKey) )
                    {
                        return v_Data.getValue();
                    }
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 设置"老Map"中的元素，用"新Map"中的元素设置"老Map"中对应Key的Value。
     * 
     *   1. "老Map"的元素个数不会增减
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-21
     * @version     v1.0
     *
     * @param io_OldMap
     * @param i_NewMap
     * @return          为什么方便，再返回一下
     */
    public final static <K> Map<K ,Object> setMapValues(Map<K ,Object> io_OldMap ,Map<K ,?> i_NewMap)
    {
        if ( Help.isNull(io_OldMap) || Help.isNull(i_NewMap) )
        {
            return null;
        }
        
        for (K v_Key : io_OldMap.keySet())
        {
            if ( i_NewMap.containsKey(v_Key) )
            {
                io_OldMap.put(v_Key ,i_NewMap.get(v_Key));
            }
        }
        
        return io_OldMap;
    }
    
    
    
    /**
     * Map集合转为占位符对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-05-12
     * @version     v1.0
     *
     * @param i_Map              
     * @param i_PlaceholderBegin  占位符前缀
     * @param i_PlaceholderEnd    占位符后缀
     * @return
     */
    public final static Map<String ,?> toPlaceholders(Map<String ,?> i_Map ,String i_PlaceholderBegin)
    {
        return toPlaceholders(i_Map ,i_PlaceholderBegin ,"");
    }
    
    
    
    /**
     * Map集合转为占位符对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-05-12
     * @version     v1.0
     *
     * @param i_Map              
     * @param i_PlaceholderBegin  占位符前缀
     * @param i_PlaceholderEnd    占位符后缀
     * @return
     */
    public final static Map<String ,?> toPlaceholders(Map<String ,?> i_Map ,String i_PlaceholderBegin ,String i_PlaceholderEnd)
    {
        Map<String ,Object> v_Ret = new HashMap<String ,Object>();
        
        for (Map.Entry<String ,?> v_Item : i_Map.entrySet())
        {
            v_Ret.put(i_PlaceholderBegin + v_Item.getKey() + i_PlaceholderEnd ,v_Item.getValue());
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将对象转为Map集合。
     * 
     * Map.key   为对象的属性名称(不包含get、set、is前缀)
     * Map.value 为对象的属性值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-08-26
     * @version     v1.0
     *
     * @param i_Obj  被转换的对象
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public final static Map<String ,Object> toMap(Object i_Obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return toMap(i_Obj ,null ,true ,true);
    }
    
    
    
    /**
     * 将对象转为Map集合。
     * 
     * Map.key   为对象的属性名称(不包含get、set、is前缀)
     * Map.value 为对象的属性值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-08-26
     * @version     v1.0
     *
     * @param i_Obj           被转换的对象
     * @param i_DefaultValue  当对象的属性值为null时，赋予的默认方法
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public final static Map<String ,Object> toMap(Object i_Obj ,Object i_DefaultValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return toMap(i_Obj ,i_DefaultValue ,true ,true ,true);
    }
    
    
    
    /**
     * 将对象转为Map集合。
     * 
     * Map.key   为对象的属性名称(不包含get、set、is前缀)
     * Map.value 为对象的属性值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-08-26
     * @version     v1.0
     *
     * @param i_Obj           被转换的对象
     * @param i_DefaultValue  当对象的属性值为null时，赋予的默认方法
     * @param i_HaveNullValue 返回Map集合，是否包含对象属性值为null的元素
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public final static Map<String ,Object> toMap(Object i_Obj ,Object i_DefaultValue ,boolean i_HaveNullValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return toMap(i_Obj ,i_DefaultValue ,i_HaveNullValue ,true ,true);
    }
    
    
    
    /**
     * 将对象转为Map集合。
     * 
     * Map.key   为对象的属性名称(不包含get、set、is前缀)
     * Map.value 为对象的属性值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-08-26
     * @version     v1.0
     *
     * @param i_Obj           被转换的对象
     * @param i_DefaultValue  当对象的属性值为null时，赋予的默认方法
     * @param i_HaveNullValue 返回Map集合，是否包含对象属性值为null的元素
     * @param i_IsOrderBy     返回Map集合，是否名属性名称排序
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public final static Map<String ,Object> toMap(Object i_Obj ,Object i_DefaultValue ,boolean i_HaveNullValue ,boolean i_IsOrderBy) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return toMap(i_Obj ,i_DefaultValue ,i_HaveNullValue ,i_IsOrderBy ,true);
    }
    
    
    
    /**
     * 将对象转为Map集合。
     * 
     * Map.key   为对象的属性名称(不包含get、set、is前缀)
     * Map.value 为对象的属性值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-08-26
     * @version     v1.0
     *              v2.0  2017-11-20  添加高速缓存。对同一对象类型的高频密集性的转换时，能显示提升性能。
     *
     * @param i_Obj           被转换的对象
     * @param i_DefaultValue  当对象的属性值为null时，赋予的默认方法
     * @param i_HaveNullValue 返回Map集合，是否包含对象属性值为null的元素
     * @param i_IsOrderBy     返回Map集合，是否名属性名称排序
     * @param i_IgnoreError   是否忽略错误。即当遇到错误时断续执行
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public final static Map<String ,Object> toMap(Object i_Obj ,Object i_DefaultValue ,boolean i_HaveNullValue ,boolean i_IsOrderBy ,boolean i_IgnoreError) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        if ( i_Obj == null )
        {
            if ( i_IsOrderBy )
            {
                return new LinkedHashMap<String ,Object>(0);
            }
            else
            {
                return new HashMap      <String ,Object>(0);
            }
        }
        
        
        List<Method> v_PropertyMethods = $ToMapCaches.getAndKeep(i_Obj.getClass() ,$ToMapTimeOut);
        if ( v_PropertyMethods == null )
        {
            v_PropertyMethods = MethodReflect.getStartMethods(i_Obj.getClass() ,new String[]{"get" ,"is"} ,0);
            
            Collections.sort(v_PropertyMethods ,MethodComparator.getInstance());
            
            $ToMapCaches.put(i_Obj.getClass() ,v_PropertyMethods ,$ToMapTimeOut);
        }
        
        Map<String ,Object> v_Ret = null;
        if ( i_IsOrderBy )
        {
            v_Ret = new LinkedHashMap<String ,Object>(v_PropertyMethods.size());
        }
        else
        {
            v_Ret = new HashMap      <String ,Object>(v_PropertyMethods.size());
        }
        
        for (int i=0; i<v_PropertyMethods.size(); i++)
        {
            Object v_Value = null;
            
            try
            {
                v_Value = v_PropertyMethods.get(i).invoke(i_Obj);
            }
            catch (IllegalAccessException exce)
            {
                if ( !i_IgnoreError )
                {
                    throw exce;
                }
            }
            catch (IllegalArgumentException exce)
            {
                if ( !i_IgnoreError )
                {
                    throw exce;
                }
            }
            catch (InvocationTargetException exce)
            {
                if ( !i_IgnoreError )
                {
                    throw exce;
                }
            }
            
            if ( v_Value == null )
            {
                // Nothing.
            }
            else if ( v_Value instanceof java.util.Date )
            {
                v_Value = new Date((java.util.Date)v_Value);
            }
            
            if ( v_Value != null )
            {
                v_Ret.put(getPropertyShortName(v_PropertyMethods.get(i).getName()) ,v_Value);
            }
            else
            {
                if ( i_HaveNullValue )
                {
                    v_Ret.put(getPropertyShortName(v_PropertyMethods.get(i).getName()) ,i_DefaultValue);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 1. 简单的将List转Map。
     * 2. 将List中的元素put到Map中。
     * 
     * Map.key   为 List中的元素
     * Map.Value 为 null。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-14
     * @version     v1.0
     *
     * @param i_Datas
     * @param io_Map
     * @return
     */
    public final static <T> Map<T ,?> toMap(List<T> i_Datas ,Map<T ,?> io_Map)
    {
        if ( Help.isNull(i_Datas) )
        {
            return null;
        }
        
        Map<T ,?> v_Ret = null;
        
        if ( io_Map == null )
        {
            v_Ret = new Hashtable<T ,Object>(i_Datas.size());
        }
        else
        {
            v_Ret = io_Map;
        }
        
        for (T v_Item : i_Datas)
        {
            v_Ret.put(v_Item ,null);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     *  纵向抽取集合元素中的某两二个属性值，形成新的Map集合
     *  
     *  可形成两种类型的Map集合
     *    1. Map.key   为 0..x 序列整数值
     *       Map.value 为 元素对象本身
     *       
     *    2. Map.key   为 元素对象中的某个属性的属性值
     *       Map.value 为 元素对象本身
     *       
     * @author      ZhengWei(HY)
     * @createDate  2015-12-20
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_KeyPropertyName    生成Map集合的key  值的属性名称。可选参数，当为空时，生成Map的key  为0..x 序列整数值
     * @return
     */
    public final static Map<? ,?> toMap(List<?> i_Datas ,String i_KeyPropertyName)
    {
        return toMap(i_Datas ,i_KeyPropertyName ,null ,null);
    }
    
    
    
    /**
     *  纵向抽取集合元素中的某两二个属性值，形成新的Map集合
     *  
     *  可形成两种类型的Map集合
     *    1. Map.key   为 0..x 序列整数值
     *       Map.value 为 元素对象本身
     *       
     *    2. Map.key   为 元素对象中的某个属性的属性值
     *       Map.value 为 元素对象本身
     *       
     * @author      ZhengWei(HY)
     * @createDate  2015-12-20
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_KeyPropertyName    生成Map集合的key  值的属性名称。可选参数，当为空时，生成Map的key  为0..x 序列整数值
     * @param i_MapClass           生成Map集合的元类型。当为LinkedHashMap时，生成的Map是有顺序的。可选参数，默认生成 Hashtable的集合。
     * @return
     */
    public final static Map<? ,?> toMap(List<?> i_Datas ,String i_KeyPropertyName ,Class<? extends Map<? ,?>> i_MapClass)
    {
        return toMap(i_Datas ,i_KeyPropertyName ,null ,i_MapClass);
    }
    
    
    
    /**
     *  纵向抽取集合元素中的某两二个属性值，形成新的Map集合
     *  
     *  可形成四种类型的Map集合
     *    1. Map.key   为 0..x 序列整数值
     *       Map.value 为 元素对象本身
     *       
     *    2. Map.key   为 0..x 序列整数值
     *       Map.value 为 元素对象中的某个属性的属性值
     *       
     *    3. Map.key   为 元素对象中的某个属性的属性值
     *       Map.value 为 元素对象本身
     *       
     *    4. Map.key   为 元素对象中的某个属性的属性值
     *       Map.value 为 元素对象中的某个属性的属性值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-20
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_KeyPropertyName    生成Map集合的key  值的属性名称。可选参数，当为空时，生成Map的key  为0..x 序列整数值
     * @param i_ValuePropertyName  生成Map集合的value值的属性名称。可选参数，当为空时，生成Map的value为元素对象本身
     * @return
     */
    public final static Map<? ,?> toMap(List<?> i_Datas ,String i_KeyPropertyName ,String i_ValuePropertyName)
    {
        return toMap(i_Datas ,i_KeyPropertyName ,i_ValuePropertyName ,null);
    }
    
    
    
    /**
     *  纵向抽取集合元素中的某两二个属性值，形成新的Map集合
     *  
     *  可形成四种类型的Map集合
     *    1. Map.key   为 0..x 序列整数值
     *       Map.value 为 元素对象本身
     *       
     *    2. Map.key   为 0..x 序列整数值
     *       Map.value 为 元素对象中的某个属性的属性值
     *       
     *    3. Map.key   为 元素对象中的某个属性的属性值
     *       Map.value 为 元素对象本身
     *       
     *    4. Map.key   为 元素对象中的某个属性的属性值
     *       Map.value 为 元素对象中的某个属性的属性值
     *       
     * @author      ZhengWei(HY)
     * @createDate  2015-12-20
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_KeyPropertyName    生成Map集合的key  值的属性名称。可选参数，当为空时，生成Map的key  为0..x 序列整数值
     * @param i_ValuePropertyName  生成Map集合的value值的属性名称。可选参数，当为空时，生成Map的value为元素对象本身
     * @param i_MapClass           生成Map集合的元类型。当为LinkedHashMap时，生成的Map是有顺序的。可选参数，默认生成 Hashtable的集合。
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static Map<? ,?> toMap(List<?> i_Datas ,String i_KeyPropertyName ,String i_ValuePropertyName ,Class<? extends Map<? ,?>> i_MapClass)
    {
        if ( Help.isNull(i_Datas) )
        {
            return null;
        }
        
        Object v_One = i_Datas.get(0);
        if ( v_One == null )
        {
            return null;
        }
        
        try
        {
            Map<Object ,Object> v_Ret = null;
            
            if ( i_MapClass == null )
            {
                v_Ret = new Hashtable<Object ,Object>(i_Datas.size());
            }
            else
            {
                v_Ret = (Map<Object ,Object>)i_MapClass.newInstance();
            }
        
            if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
            {
                int v_KeyPIndex   = Help.gatPropertyIndex((Serializable)v_One ,i_KeyPropertyName);
                int v_ValuePIndex = Help.gatPropertyIndex((Serializable)v_One ,i_ValuePropertyName);
                Serializable v_Item = null;
                
                if ( v_KeyPIndex <= -1 && v_ValuePIndex <= -1 )
                {
                    // Map.key   为 0..x 序列整数值
                    // Map.value 为 元素对象本身
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Ret.put(Integer.valueOf(x) ,i_Datas.get(x));
                    }
                }
                else if ( v_KeyPIndex <= -1 )
                {
                    // Map.key   为 0..x 序列整数值
                    // Map.value 为 元素对象中的某个属性的属性值
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Item = (Serializable)i_Datas.get(x);
                        v_Ret.put(Integer.valueOf(x) ,v_Item.gatPropertyValue(v_ValuePIndex));
                    }
                }
                else if ( v_ValuePIndex <= -1 )
                {
                    // Map.key   为 元素对象中的某个属性的属性值
                    // Map.value 为 元素对象本身
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Item = (Serializable)i_Datas.get(x);
                        v_Ret.put(v_Item.gatPropertyValue(v_KeyPIndex) ,v_Item);
                    }
                }
                else
                {
                    // Map.key   为 元素对象中的某个属性的属性值
                    // Map.value 为 元素对象中的某个属性的属性值
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Item = (Serializable)i_Datas.get(x);
                        v_Ret.put(v_Item.gatPropertyValue(v_KeyPIndex) ,v_Item.gatPropertyValue(v_ValuePIndex));
                    }
                }
            }
            else
            {
                Method v_KeyPMethod   = MethodReflect.getGetMethod(i_Datas.get(0).getClass() ,i_KeyPropertyName   ,true);
                Method v_ValuePMethod = MethodReflect.getGetMethod(i_Datas.get(0).getClass() ,i_ValuePropertyName ,true);
                Object v_Item         = null;
                
                if ( null == v_KeyPMethod && null == v_ValuePMethod )
                {
                    // Map.key   为 0..x 序列整数值
                    // Map.value 为 元素对象本身
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Ret.put(Integer.valueOf(x) ,i_Datas.get(x));
                    }
                }
                else if ( null == v_KeyPMethod )
                {
                    // Map.key   为 0..x 序列整数值
                    // Map.value 为 元素对象中的某个属性的属性值
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Item = i_Datas.get(x);
                        v_Ret.put(Integer.valueOf(x) ,v_ValuePMethod.invoke(v_Item));
                    }
                }
                else if ( null == v_ValuePMethod )
                {
                    // Map.key   为 元素对象中的某个属性的属性值
                    // Map.value 为 元素对象本身
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Item = i_Datas.get(x);
                        v_Ret.put(v_KeyPMethod.invoke(v_Item) ,v_Item);
                    }
                }
                else
                {
                    // Map.key   为 元素对象中的某个属性的属性值
                    // Map.value 为 元素对象中的某个属性的属性值
                    for (int x=0; x<i_Datas.size(); x++)
                    {
                        v_Item = i_Datas.get(x);
                        v_Ret.put(v_KeyPMethod.invoke(v_Item) ,v_ValuePMethod.invoke(v_Item));
                    }
                }
            }
            
            return v_Ret;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    
    /**
     * 获取短的属性名称
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-08-26
     * @version     v1.0
     *
     * @param i_PropertyName
     * @return
     */
    public final static String getPropertyShortName(String i_PropertyName)
    {
        String v_Name = i_PropertyName;
        
        if ( v_Name.startsWith("is") )
        {
            v_Name = v_Name.substring(2 ,3).toLowerCase() + v_Name.substring(3);
        }
        else
        {
            v_Name = v_Name.substring(3 ,4).toLowerCase() + v_Name.substring(4);
        }
        
        return v_Name;
    }
    
    
    
    /**
     * 数组转集合。
     * 
     * 一切为了方便。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-09
     * @version     v1.0
     *              v2.0  2018-01-26  修复：Arrays.asList()生成的集合是只读，不能对集合进行add()、remove()等修改操作。
     *
     * @param i_Array
     * @return
     */
    public final static <T> List<T> toList(T ... i_Array)
    {
        return new ArrayList<T>(Arrays.asList(i_Array));
    }
    
    
    
    /**
     * Set转为List
     * 
     * @param <T>
     * 
     * @param i_Map
     * @return
     */
    public final static <T> List<T> toList(Set<T> i_Set)
    {
        List<T> v_Ret = null;
        
        if ( !Help.isNull(i_Set) )
        {
            v_Ret = new ArrayList<T>(i_Set);
        }
        else
        {
            v_Ret = new ArrayList<T>(0);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * Map.Value转为List
     * 
     * @param <T>
     * 
     * @param i_Map
     * @return
     */
    public final static <T> List<T> toList(Map<? ,T> i_Map)
    {
        List<T> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            v_Ret = new ArrayList<T>(i_Map.size());
            
            for (T v_Value : i_Map.values())
            {
                v_Ret.add(v_Value);
            }
        }
        else
        {
            v_Ret = new ArrayList<T>(0);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * PartitionMap.Value转为List
     * 
     * @param <T>
     * 
     * @param i_Map
     * @return
     */
    public final static <T> List<T> toList(PartitionMap<? ,T> i_Map)
    {
        List<T> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            v_Ret = new ArrayList<T>(i_Map.size());
        
            for (List<T> v_SmailList : i_Map.values())
            {
                v_Ret.addAll(v_SmailList);
            }
        }
        else
        {
            v_Ret = new ArrayList<T>(0);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * PartitionMap.Value转为List
     * 
     * @param <T>
     * 
     * @param i_Map
     * @return
     */
    public final static <T> List<List<T>> toListList(PartitionMap<? ,T> i_Map)
    {
        List<List<T>> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            v_Ret = new ArrayList<List<T>>(i_Map.size());
        
            for (List<T> v_SmailList : i_Map.values())
            {
                v_Ret.add(v_SmailList);
            }
        }
        else
        {
            v_Ret = new ArrayList<List<T>>(0);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * TablePartitionRID.Value转为List
     * 
     * @param <T>
     * 
     * @param i_Map
     * @return
     */
    public final static <T> List<T> toList(TablePartitionRID<? ,T> i_Map)
    {
        List<T> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            v_Ret = new ArrayList<T>(i_Map.size());
            
            for (Map<String ,T> v_SmailMap : i_Map.values())
            {
                v_Ret.addAll(v_SmailMap.values());
            }
        }
        else
        {
            v_Ret = new ArrayList<T>(0);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * Map.Key转为List
     * @param <T>
     * 
     * @param i_Map
     * @return
     */
    public final static <T> List<T> toListKeys(Map<T ,?> i_Map)
    {
        List<T> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            v_Ret = new ArrayList<T>(i_Map.size());
            
            for (T v_Key : i_Map.keySet())
            {
                v_Ret.add(v_Key);
            }
        }
        else
        {
            v_Ret = new ArrayList<T>(); 
        }
        
        return v_Ret;
    }
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-18
     * @version     v1.0
     *              v2.0  2016-06-24  支持属性名称的多级获取 xxx.yyy.zzz
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     *                              支持属性名称的多级获取 xxx.yyy.zzz
     * @return                      创建一个新的集合。异常时，返回null
     */
    @SuppressWarnings("unchecked")
    public final static List<?> toList(Set<?> i_Datas ,String i_SelectPropertyName)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        Object v_One = i_Datas.iterator().next();
        if ( v_One == null )
        {
            return null;
        }
        
        String [] v_SelectPropertyNameArr = i_SelectPropertyName.split("\\.");
        List<?>   v_Ret                   = null;
        
        if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
        {
            v_Ret = toListSerializable((Set<? extends Serializable>)i_Datas ,v_SelectPropertyNameArr[0]);
        }
        else
        {
            v_Ret = toListObject(i_Datas ,v_SelectPropertyNameArr[0]);
        }
        
        for (int v_Index=1; v_Index<v_SelectPropertyNameArr.length; v_Index++)
        {
            v_Ret = toList(v_Ret ,v_SelectPropertyNameArr[v_Index]);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-18
     * @version     v1.0
     *              v2.0  2016-06-24  支持属性名称的多级获取 xxx.yyy.zzz
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     *                              支持属性名称的多级获取 xxx.yyy.zzz
     * @return                      创建一个新的集合。异常时，返回null
     */
    @SuppressWarnings("unchecked")
    public final static List<?> toList(Map<? ,?> i_Datas ,String i_SelectPropertyName)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        Object v_One = i_Datas.values().iterator().next();
        if ( v_One == null )
        {
            return null;
        }
        
        String [] v_SelectPropertyNameArr = i_SelectPropertyName.split("\\.");
        List<?>   v_Ret                   = null;
        
        if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
        {
            v_Ret = toListSerializable((Map<? ,? extends Serializable>)i_Datas ,v_SelectPropertyNameArr[0]);
        }
        else
        {
            v_Ret = toListObject(i_Datas ,v_SelectPropertyNameArr[0]);
        }
        
        for (int v_Index=1; v_Index<v_SelectPropertyNameArr.length; v_Index++)
        {
            v_Ret = toList(v_Ret ,v_SelectPropertyNameArr[v_Index]);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *              v2.0  2016-06-24  支持属性名称的多级获取 xxx.yyy.zzz
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     *                              支持属性名称的多级获取 xxx.yyy.zzz
     * @return                      创建一个新的集合。异常时，返回null
     */
    @SuppressWarnings("unchecked")
    public final static List<?> toList(List<?> i_Datas ,String i_SelectPropertyName)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        Object v_One = i_Datas.get(0);
        if ( v_One == null )
        {
            return null;
        }
        
        String [] v_SelectPropertyNameArr = i_SelectPropertyName.split("\\.");
        List<?>   v_Ret                   = null;
        if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
        {
            v_Ret = toListSerializable((List<? extends Serializable>)i_Datas ,v_SelectPropertyNameArr[0]);
        }
        else if ( MethodReflect.isExtendImplement(v_One ,Map.class) )
        {
            v_Ret = toListMap((List<Map<? ,?>>)i_Datas ,v_SelectPropertyNameArr[0]);
        }
        else
        {
            v_Ret = toListObject(i_Datas ,v_SelectPropertyNameArr[0]);
        }
        
        for (int v_Index=1; v_Index<v_SelectPropertyNameArr.length; v_Index++)
        {
            v_Ret = toList(v_Ret ,v_SelectPropertyNameArr[v_Index]);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-07-17
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null。
     */
    private final static List<?> toListMap(List<Map<? ,?>> i_Datas ,String i_SelectPropertyName)
    {
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (int v_Index=0; v_Index<i_Datas.size(); v_Index++)
        {
            try
            {
                v_Ret.add(i_Datas.get(v_Index).get(i_SelectPropertyName));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null。
     */
    private final static List<?> toListObject(List<?> i_Datas ,String i_SelectPropertyName)
    {
        Method v_Method = MethodReflect.getGetMethod(i_Datas.get(0).getClass() ,i_SelectPropertyName ,true);
        if ( v_Method == null )
        {
            return null;
        }
        
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (int v_Index=0; v_Index<i_Datas.size(); v_Index++)
        {
            try
            {
                v_Ret.add(v_Method.invoke(i_Datas.get(v_Index)));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-18
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null。
     */
    private final static List<?> toListObject(Set<?> i_Datas ,String i_SelectPropertyName)
    {
        Method v_Method = MethodReflect.getGetMethod(i_Datas.iterator().next().getClass() ,i_SelectPropertyName ,true);
        if ( v_Method == null )
        {
            return null;
        }
        
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (Object v_Data : i_Datas)
        {
            try
            {
                v_Ret.add(v_Method.invoke(v_Data));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-18
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null。
     */
    private final static List<?> toListObject(Map<? ,?> i_Datas ,String i_SelectPropertyName)
    {
        Method v_Method = MethodReflect.getGetMethod(i_Datas.values().iterator().next().getClass() ,i_SelectPropertyName ,true);
        if ( v_Method == null )
        {
            return null;
        }
        
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (Object v_Data : i_Datas.values())
        {
            try
            {
                v_Ret.add(v_Method.invoke(v_Data));
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-11-25
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null
     */
    private final static List<?> toListSerializable(List<? extends Serializable> i_Datas ,String i_SelectPropertyName)
    {
        Serializable v_One = i_Datas.get(0);
        
        int v_PIndex = Help.gatPropertyIndex(v_One ,i_SelectPropertyName);
        if ( v_PIndex < 0 )
        {
            return null;
        }
        
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (int v_Index=0; v_Index<i_Datas.size(); v_Index++)
        {
            v_Ret.add(i_Datas.get(v_Index).gatPropertyValue(v_PIndex));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-18
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null
     */
    private final static List<?> toListSerializable(Set<? extends Serializable> i_Datas ,String i_SelectPropertyName)
    {
        Serializable v_One = i_Datas.iterator().next();
        
        int v_PIndex = Help.gatPropertyIndex(v_One ,i_SelectPropertyName);
        if ( v_PIndex < 0 )
        {
            return null;
        }
        
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (Serializable v_Data : i_Datas)
        {
            v_Ret.add(v_Data.gatPropertyValue(v_PIndex));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向抽取集合元素中的某一个属性值，形成新的简单集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-18
     * @version     v1.0
     *
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向抽取的属性名称，不区分大小写。
     * @return                      创建一个新的集合。异常时，返回null
     */
    private final static List<?> toListSerializable(Map<? ,? extends Serializable> i_Datas ,String i_SelectPropertyName)
    {
        Serializable v_One = i_Datas.values().iterator().next();
        
        int v_PIndex = Help.gatPropertyIndex(v_One ,i_SelectPropertyName);
        if ( v_PIndex < 0 )
        {
            return null;
        }
        
        // 开始纵向抽取
        List<Object> v_Ret = new ArrayList<Object>(i_Datas.size());
        for (Serializable v_Data : i_Datas.values())
        {
            v_Ret.add(v_Data.gatPropertyValue(v_PIndex));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询。
     * 
     * (包含任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(List<T> i_Datas ,String i_SelectPropertyName ,String i_FindKeys)
    {
        return toLike(i_Datas ,i_SelectPropertyName ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(List<T> i_Datas ,String i_SelectPropertyName ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        T v_One = i_Datas.get(0);
        if ( v_One == null )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            MethodReflect v_MethodReflect = new MethodReflect(v_One ,i_SelectPropertyName ,true ,MethodReflect.$NormType_Getter);
            
            for (T v_Item : i_Datas)
            {
                Object v_Value = v_MethodReflect.invokeForInstance(v_Item);
                
                if ( v_Value != null 
                  && StringHelp.isContains(v_Value.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询。
     * 
     * (包含任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Map<? ,T> i_Datas ,String i_SelectPropertyName ,String i_FindKeys)
    {
        return toLike(i_Datas ,i_SelectPropertyName ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Map<? ,T> i_Datas ,String i_SelectPropertyName ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        T v_One = i_Datas.values().iterator().next();
        if ( v_One == null )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            MethodReflect v_MethodReflect = new MethodReflect(v_One ,i_SelectPropertyName ,true ,MethodReflect.$NormType_Getter);
            
            for (T v_Item : i_Datas.values())
            {
                Object v_Value = v_MethodReflect.invokeForInstance(v_Item);
                
                if ( v_Value != null 
                  && StringHelp.isContains(v_Value.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询。
     * 
     * (包含任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(PartitionMap<? ,T> i_Datas ,String i_SelectPropertyName ,String i_FindKeys)
    {
        return toLike(i_Datas ,i_SelectPropertyName ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(PartitionMap<? ,T> i_Datas ,String i_SelectPropertyName ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) || i_Datas.rowCount() <= 0 )
        {
            return null;
        }
        
        T v_One = i_Datas.values().iterator().next().get(0);
        if ( v_One == null )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            MethodReflect v_MethodReflect = new MethodReflect(v_One ,i_SelectPropertyName ,true ,MethodReflect.$NormType_Getter);
            
            for (List<T> v_ChildList : i_Datas.values())
            {
                if ( !Help.isNull(v_ChildList) )
                {
                    for (T v_Item : v_ChildList)
                    {
                        Object v_Value = v_MethodReflect.invokeForInstance(v_Item);
                        
                        if ( v_Value != null 
                          && StringHelp.isContains(v_Value.toString() ,i_IsAllContains ,i_FindKeys) )
                        {
                            v_Ret.add(v_Item);
                        }
                    }
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询。
     * 
     * (包含任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Set<T> i_Datas ,String i_SelectPropertyName ,String i_FindKeys)
    {
        return toLike(i_Datas ,i_SelectPropertyName ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Set<T> i_Datas ,String i_SelectPropertyName ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        T v_One = i_Datas.iterator().next();
        if ( v_One == null )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            MethodReflect v_MethodReflect = new MethodReflect(v_One ,i_SelectPropertyName ,true ,MethodReflect.$NormType_Getter);
            
            for (T v_Item : i_Datas)
            {
                Object v_Value = v_MethodReflect.invokeForInstance(v_Item);
                
                if ( v_Value != null 
                  && StringHelp.isContains(v_Value.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询。
     * 
     * (包含任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(T [] i_Datas ,String i_SelectPropertyName ,String i_FindKeys)
    {
        return toLike(i_Datas ,i_SelectPropertyName ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 纵向比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_SelectPropertyName  纵向比较的属性名称，不区分大小写。支持属性名称的多级获取 xxx.yyy.zzz
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(T [] i_Datas ,String i_SelectPropertyName ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) || Help.isNull(i_SelectPropertyName) )
        {
            return null;
        }
        
        T v_One = i_Datas[0];
        if ( v_One == null )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            MethodReflect v_MethodReflect = new MethodReflect(v_One ,i_SelectPropertyName ,true ,MethodReflect.$NormType_Getter);
            
            for (T v_Item : i_Datas)
            {
                Object v_Value = v_MethodReflect.invokeForInstance(v_Item);
                
                if ( v_Value != null 
                  && StringHelp.isContains(v_Value.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(List<T> i_Datas ,String i_FindKeys)
    {
        return toLike(i_Datas ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(List<T> i_Datas ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            for (T v_Item : i_Datas)
            {
                if ( v_Item != null 
                  && StringHelp.isContains(v_Item.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Map<? ,T> i_Datas ,String i_FindKeys)
    {
        return toLike(i_Datas ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Map<? ,T> i_Datas ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            for (T v_Item : i_Datas.values())
            {
                if ( v_Item != null 
                  && StringHelp.isContains(v_Item.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(PartitionMap<? ,T> i_Datas ,String i_FindKeys)
    {
        return toLike(i_Datas ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(PartitionMap<? ,T> i_Datas ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) || i_Datas.rowCount() <= 0 )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            for (List<T> v_ChildList : i_Datas.values())
            {
                if ( !Help.isNull(v_ChildList) )
                {
                    for (T v_Item : v_ChildList)
                    {
                        if ( v_Item != null 
                          && StringHelp.isContains(v_Item.toString() ,i_IsAllContains ,i_FindKeys) )
                        {
                            v_Ret.add(v_Item);
                        }
                    }
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Set<T> i_Datas ,String i_FindKeys)
    {
        return toLike(i_Datas ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(Set<T> i_Datas ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            for (T v_Item : i_Datas)
            {
                if ( v_Item != null 
                  && StringHelp.isContains(v_Item.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(T [] i_Datas ,String i_FindKeys)
    {
        return toLike(i_Datas ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 比较集合元素中的某一个属性值是否包含多个关键字，并形成新的集合。新集合与原集合结构相同。
     * 
     * 模拟SQL语句中的like查询
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-27
     * @version     v1.0
     * 
     * @param i_Datas               集合数据
     * @param i_IsAllContains       是否包含全部关键字为true
     * @param i_FindKeys            关键字组
     * 
     * @return                      创建一个新的集合。异常时，返回null
     */
    public final static <T> List<T> toLike(T [] i_Datas ,boolean i_IsAllContains ,String ... i_FindKeys)
    {
        if ( Help.isNull(i_Datas) )
        {
            return null;
        }
        
        List<T> v_Ret = new ArrayList<T>();
        
        try
        {
            for (T v_Item : i_Datas)
            {
                if ( v_Item != null 
                  && StringHelp.isContains(v_Item.toString() ,i_IsAllContains ,i_FindKeys) )
                {
                    v_Ret.add(v_Item);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static int[] toSort(int[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static long[] toSort(long[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static double[] toSort(double[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static float[] toSort(float[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static short[] toSort(short[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static char[] toSort(char[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static byte[] toSort(byte[] io_Array)
    {
        Arrays.sort(io_Array);
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static int[] toReverse(int[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int v_Index       = io_Array.length - i - 1;
            int v_Data        = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static long[] toReverse(long[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int  v_Index      = io_Array.length - i - 1;
            long v_Data       = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static double[] toReverse(double[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int    v_Index    = io_Array.length - i - 1;
            double v_Data     = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static float[] toReverse(float[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int    v_Index    = io_Array.length - i - 1;
            float  v_Data     = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static short[] toReverse(short[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int    v_Index    = io_Array.length - i - 1;
            short  v_Data     = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static char[] toReverse(char[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int    v_Index    = io_Array.length - i - 1;
            char   v_Data     = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 数组倒序排序。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param io_Array
     * @return
     */
    public final static byte[] toReverse(byte[] io_Array)
    {
        io_Array = toSort(io_Array);
        int v_Len  = io_Array.length / 2;
        
        for (int i=0; i<v_Len; i++)
        {
            int    v_Index    = io_Array.length - i - 1;
            byte   v_Data     = io_Array[i];
            io_Array[i]       = io_Array[v_Index];
            io_Array[v_Index] = v_Data;
        }
        
        return io_Array;
    }
    
    
    
    /**
     * 字符串转数字后，再比较排序（正序）
     * 
     * 优点是，不会改变原属性值的保存格式。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static List<String> toSortByNum(String ... i_Values)
    {
        if ( Help.isNull(i_Values) )
        {
            return new ArrayList<String>(0);
        }
        
        List<Param> v_Values = new ArrayList<Param>(i_Values.length);
        
        for (String v_Item : i_Values)
        {
            v_Values.add(new Param(null ,v_Item));
        }
        
        Help.toSort(v_Values ,"value NumAsc");
        
        return (List<String>)Help.toList(v_Values ,"value");
    }
    
    
    
    /**
     * 字符串转数字后，再比较排序（倒序）
     * 
     * 优点是，不会改变原属性值的保存格式。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static List<String> toReverseByNum(String ... i_Values)
    {
        if ( Help.isNull(i_Values) )
        {
            return new ArrayList<String>(0);
        }
        
        List<Param> v_Values = new ArrayList<Param>(i_Values.length);
        
        for (String v_Item : i_Values)
        {
            v_Values.add(new Param(null ,v_Item));
        }
        
        Help.toSort(v_Values ,"value NumDesc");
        
        return (List<String>)Help.toList(v_Values ,"value");
    }
    
    
    
    /**
     * 简单的单维比较排序（正序）。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    public final static <T extends Comparable<? super T>> List<T> toSort(List<T> io_Values)
    {
        Collections.sort(io_Values);
        return io_Values;
    }
    
    
    
    /**
     * 简单的单维比较排序（正序）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    @SafeVarargs
    public final static <T extends Comparable<? super T>> List<T> toSort(T ... i_Values)
    {
        if ( Help.isNull(i_Values) )
        {
            return new ArrayList<T>(0);
        }
        
        List<T> v_Values = new ArrayList<T>(i_Values.length);
        
        for (T v_Item : i_Values)
        {
            v_Values.add(v_Item);
        }
        
        Collections.sort(v_Values);
        
        return v_Values;
    }
    
    
    
    /**
     * 简单的单维比较排序（倒序）。为了好记方便，并无特别操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-24
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    public final static <T extends Comparable<? super T>> List<T> toReverse(List<T> io_Values)
    {
        Collections.sort(io_Values ,Collections.reverseOrder());
        return io_Values;
    }
    
    
    
    /**
     * 简单的单维比较排序（倒序）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    @SafeVarargs
    public final static <T extends Comparable<? super T>> List<T> toReverse(T ... i_Values)
    {
        if ( Help.isNull(i_Values) )
        {
            return new ArrayList<T>(0);
        }
        
        List<T> v_Values = new ArrayList<T>(i_Values.length);
        
        for (T v_Item : i_Values)
        {
            v_Values.add(v_Item);
        }
        
        Collections.sort(v_Values ,Collections.reverseOrder());
        
        return v_Values;
    }
    
    
    
    /**
     * 多维的排序比较器
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-05-23
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     */
    @SuppressWarnings({"rawtypes" ,"unchecked"})
    public final static void toSort(Collection io_Datas ,String ... i_SortPropertyNames)
    {
        if ( Help.isNull(io_Datas) || Help.isNull(i_SortPropertyNames) )
        {
            return;
        }
        
        List<?> v_Datas = new ArrayList<Object>(io_Datas);
        
        Help.toSort(v_Datas ,i_SortPropertyNames);
        
        io_Datas.clear();
        io_Datas.addAll(v_Datas);
    }
    
    
    
    /**
     * 多维的排序比较器
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     */
    public final static void toSort(List<?> io_Datas ,String ... i_SortPropertyNames)
    {
        if ( Help.isNull(io_Datas) || Help.isNull(i_SortPropertyNames) )
        {
            return;
        }
        
        Object v_One = io_Datas.get(0);
        if ( v_One == null )
        {
            return;
        }
        
        /*
        if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
        {
            toSortSerializable((List<? extends Serializable>)io_Datas ,i_SortPropertyNames);
        }
        else
        {
        */
        
        toSortObject(io_Datas ,i_SortPropertyNames);
    }
    
    
    
    /**
     * 多维的排序比较器
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     */
    private final static void toSortObject(List<?> io_Datas ,String ... i_SortPropertyNames)
    {
        ObjectComparator v_SComparator = new ObjectComparator(io_Datas.get(0) ,i_SortPropertyNames);
        Collections.sort(io_Datas ,v_SComparator);
    }
    
    
    
    /**
     * 多维的排序比较器
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-11-25
     * @version     v1.0
     *
     * @param io_Datas             集合数据
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     *                             
     * 2017-06-15 被 toSortObject() 方法取替，暂时保存不删除此段代码
     */
    @SuppressWarnings("unused")
    private final static void toSortSerializable(List<? extends Serializable> io_Datas ,String ... i_SortPropertyNames)
    {
        SerializableComparator v_SComparator = new SerializableComparator(io_Datas.get(0) ,i_SortPropertyNames);
        Collections.sort(io_Datas ,v_SComparator);
    }
    
    
    
    /**
     * 多维的排序比较后，查找并过滤重复的元素(多维都相等，即为重复)
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     *   4. 返回且只返回首个重复的元素：（即，返回值中的每个元素不重复）
     *       4.1 元素0与元素1相同重复时，将元素0添加到返回集合中；
     *       4.2 当元素2与元素1、元素0相同重复时，只将元素0添加到返回集合中，元素1、元素2不返回。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-14
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据。会改变集合元素排列的顺序
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     * @return                     返回值中的每个元素不重复
     */
    public final static <T> List<T> findSames(List<T> io_Datas ,String ... i_SortPropertyNames)
    {
        List<T> v_Sames = new ArrayList<T>();
        
        if ( Help.isNull(io_Datas) || Help.isNull(i_SortPropertyNames) )
        {
            return v_Sames;
        }
        
        Object v_One = io_Datas.get(0);
        if ( v_One == null )
        {
            return v_Sames;
        }
        
        /*
        if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
        {
            Help.toSortSerializable((List<? extends Serializable>)io_Datas ,i_SortPropertyNames);
            
            SerializableComparator v_Comparator = new SerializableComparator((Serializable)v_One ,i_SortPropertyNames);
            
            int v_SameIndex = -1; // 上次相同重复的索引下标
            for (int v_Index=1; v_Index<io_Datas.size(); v_Index++)
            {
                int v_Ret = v_Comparator.compare((Serializable)io_Datas.get(v_Index-1) ,(Serializable)io_Datas.get(v_Index));
                
                if ( v_Ret == 0 )
                {
                    if ( v_Index - 1 > v_SameIndex )
                    {
                        v_Sames.add(io_Datas.get(v_Index-1));
                    }
                    
                    v_SameIndex = v_Index;
                }
            }
        }
        else
        {
        */
        Help.toSortObject(io_Datas ,i_SortPropertyNames);
        
        ObjectComparator v_Comparator = new ObjectComparator(v_One ,i_SortPropertyNames);
        
        int v_SameIndex = -1; // 上次相同重复的索引下标
        for (int v_Index=1; v_Index<io_Datas.size(); v_Index++)
        {
            int v_Ret = v_Comparator.compare(io_Datas.get(v_Index-1) ,io_Datas.get(v_Index));
            
            if ( v_Ret == 0 )
            {
                if ( v_Index - 1 > v_SameIndex )
                {
                    v_Sames.add(io_Datas.get(v_Index-1));
                }
                
                v_SameIndex = v_Index;
            }
        }
        
        return v_Sames;
    }
    
    
    
    /**
     * 多维的排序比较后，查找并过滤重复的元素(多维都相等，即为重复)
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     *   4. 返回且只返回首个重复的元素：（即，返回值中的每个元素不重复）
     *       4.1 元素0与元素1相同重复时，将元素0添加到返回集合中；
     *       4.2 当元素2与元素1、元素0相同重复时，只将元素0添加到返回集合中，元素1、元素2不返回。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-14
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据。会改变集合元素排列的顺序
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     * @return                     返回值中的每个元素不重复
     */
    public final static <T> List<T> findSames(Set<T> io_Datas ,String ... i_SortPropertyNames)
    {
        return findSames(new ArrayList<T>(io_Datas) ,i_SortPropertyNames);
    }
    
    
    
    /**
     * 简单的单维比较后，查找并过滤重复的元素（生成的结果可能还是无序的，因为Set接口除 LinkedHashSet 外，都无顺序）
     *   1. 返回且只返回首个重复的元素：（即，返回值中的每个元素不重复）
     *       1.1 元素0与元素1相同重复时，将元素0添加到返回集合中；
     *       1.2 当元素2与元素1、元素0相同重复时，只将元素0添加到返回集合中，元素1、元素2不返回。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-14
     * @version     v1.0
     *
     * @param io_Datas  集合数据。会改变集合元素排列的顺序
     * @return          返回值中的每个元素不重复
     */
    public final static <T extends Comparable<? super T>> List<T> findSames(Set<T> io_Datas)
    {
        return findSames(new ArrayList<T>(io_Datas));
    }
    
    
    
    /**
     * 简单的单维比较后，查找并过滤重复的元素（正序）
     *   1. 返回且只返回首个重复的元素：（即，返回值中的每个元素不重复）
     *       1.1 元素0与元素1相同重复时，将元素0添加到返回集合中；
     *       1.2 当元素2与元素1、元素0相同重复时，只将元素0添加到返回集合中，元素1、元素2不返回。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-06-24
     * @version     v1.0
     *
     * @param io_Datas  集合数据。会改变集合元素排列的顺序
     * @return          返回值中的每个元素不重复
     */
    public final static <T extends Comparable<? super T>> List<T> findSames(List<T> io_Datas)
    {
        List<T> v_Datas     = Help.toSort(io_Datas);
        int     v_SameIndex = -1; // 上次相同重复的索引下标
        List<T> v_Sames     = new ArrayList<T>();
        
        for (int v_Index=1; v_Index<io_Datas.size(); v_Index++)
        {
            int v_Ret = v_Datas.get(v_Index-1).compareTo(v_Datas.get(v_Index));
            
            if ( v_Ret == 0 )
            {
                if ( v_Index - 1 > v_SameIndex )
                {
                    v_Sames.add(v_Datas.get(v_Index-1));
                }
                
                v_SameIndex = v_Index;
            }
        }
        
        return v_Sames;
    }
    
    
    
    /**
     * 简单的单维比较后，查找并过滤重复的元素（正序）
     *   1. 返回且只返回首个重复的元素：（即，返回值中的每个元素不重复）
     *       1.1 元素0与元素1相同重复时，将元素0添加到返回集合中；
     *       1.2 当元素2与元素1、元素0相同重复时，只将元素0添加到返回集合中，元素1、元素2不返回。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-14
     * @version     v1.0
     *
     * @param i_Values  集合数据
     * @return          返回值中的每个元素不重复
     */
    @SafeVarargs
    public final static <T extends Comparable<? super T>> List<T> findSames(T ... i_Values)
    {
        List<T> v_Datas     = Help.toSort(i_Values);
        int     v_SameIndex = -1; // 上次相同重复的索引下标
        List<T> v_Sames     = new ArrayList<T>();
        
        for (int v_Index=1; v_Index<v_Datas.size(); v_Index++)
        {
            int v_Ret = v_Datas.get(v_Index-1).compareTo(v_Datas.get(v_Index));
            
            if ( v_Ret == 0 )
            {
                if ( v_Index - 1 > v_SameIndex )
                {
                    v_Sames.add(v_Datas.get(v_Index-1));
                }
                
                v_SameIndex = v_Index;
            }
        }
        
        return v_Datas;
    }
    
    
    
    /**
     * 多维的排序比较后，去除重复的元素(多维都相等，即为重复)
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     * 
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-15
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     */
    public final static <T> void toDistinct(Set<T> io_Datas ,String ... i_SortPropertyNames)
    {
        List<T> v_Ret = new ArrayList<T>(io_Datas);
        
        toDistinct(v_Ret ,i_SortPropertyNames);
        
        io_Datas.clear();
        io_Datas.addAll(v_Ret);
    }
    
    
    
    /**
     * 多维的排序比较后，去除重复的元素(多维都相等，即为重复)
     *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
     *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
     *         优点是，不会改变原属性值的保存格式。
     *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-17
     * @version     v1.0
     *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
     *
     * @param io_Datas             集合数据
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     */
    public final static <T> void toDistinct(List<T> io_Datas ,String ... i_SortPropertyNames)
    {
        if ( Help.isNull(io_Datas) || Help.isNull(i_SortPropertyNames) )
        {
            return;
        }
        
        T v_One = io_Datas.get(0);
        if ( v_One == null )
        {
            return;
        }
        
        /*
        if ( MethodReflect.isExtendImplement(v_One ,Serializable.class) )
        {
            Help.toSortSerializable((List<? extends Serializable>)io_Datas ,i_SortPropertyNames);
            
            SerializableComparator v_Comparator = new SerializableComparator((Serializable)v_One ,i_SortPropertyNames);
            
            for (int v_Index=io_Datas.size()-1; v_Index>=1; v_Index--)
            {
                int v_Ret = v_Comparator.compare((Serializable)io_Datas.get(v_Index-1) ,(Serializable)io_Datas.get(v_Index));
                if ( v_Ret == 0 )
                {
                    io_Datas.remove(v_Index);
                }
            }
        }
        else
        {
        */
        Help.toSortObject(io_Datas ,i_SortPropertyNames);
        
        ObjectComparator v_Comparator = new ObjectComparator(v_One ,i_SortPropertyNames);
        
        for (int v_Index=io_Datas.size()-1; v_Index>=1; v_Index--)
        {
            int v_Ret = v_Comparator.compare(io_Datas.get(v_Index-1) ,io_Datas.get(v_Index));
            if ( v_Ret == 0 )
            {
                io_Datas.remove(v_Index);
            }
        }
    }
    
    
    
    /**
     * 简单的单维比较后，去除重复的元素（生成的结果可能还是无序的，因为Set接口除 LinkedHashSet 外，都无顺序）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-15
     * @version     v1.0
     *
     * @param io_Values
     * @return
     */
    public final static <T extends Comparable<? super T>> Set<T> toDistinct(Set<T> io_Values)
    {
        List<T> v_Ret = new ArrayList<T>(io_Values);
        
        v_Ret = toDistinct(v_Ret);
        
        io_Values.clear();
        io_Values.addAll(v_Ret);
        
        return io_Values;
    }
    
    
    
    /**
     * 简单的单维比较后，去除重复的元素（正序）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-06-24
     * @version     v1.0
     *
     * @param io_Values
     * @return
     */
    public final static <T extends Comparable<? super T>> List<T> toDistinct(List<T> io_Values)
    {
        List<T> v_Datas = Help.toSort(io_Values);
        
        for (int v_Index=v_Datas.size()-1; v_Index>=1; v_Index--)
        {
            int v_Ret = v_Datas.get(v_Index-1).compareTo(v_Datas.get(v_Index));
            if ( v_Ret == 0 )
            {
                v_Datas.remove(v_Index);
            }
        }
        
        return v_Datas;
    }
    
    
    
    /**
     * 简单的单维比较后，去除重复的元素（正序）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-17
     * @version     v1.0
     *
     * @param i_Values
     * @return
     */
    @SafeVarargs
    public final static <T extends Comparable<? super T>> List<T> toDistinct(T ... i_Values)
    {
        List<T> v_Datas = Help.toSort(i_Values);
        
        for (int v_Index=v_Datas.size()-1; v_Index>=1; v_Index--)
        {
            int v_Ret = v_Datas.get(v_Index-1).compareTo(v_Datas.get(v_Index));
            if ( v_Ret == 0 )
            {
                v_Datas.remove(v_Index);
            }
        }
        
        return v_Datas;
    }
    
    
    
    /**
     * 对Map集合的Keys升序排序生成一个新的LinkedMap
     * 
     * 字符串转数字后，再比较排序（正序）
     * 
     * 优点是，不会改变原属性值的保存格式。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-12-19
     * @version     v1.0
     * 
     * @param i_Map
     * @return
     */
    public final static <T2> Map<String ,T2> toSortByNum(Map<String ,T2> i_Map)
    {
        return toSortReverseByNum(i_Map ,1);
    }
    
    
    
    /**
     * 对Map集合的Keys降序排序生成一个新的LinkedMap
     * 
     * 字符串转数字后，再比较排序（降序）
     * 
     * 优点是，不会改变原属性值的保存格式。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-12-19
     * @version     v1.0
     *
     * @param i_Map
     * @return
     */
    public final static <T2> Map<String ,T2> toReverseByNum(Map<String ,T2> i_Map)
    {
        return toSortReverseByNum(i_Map ,-1);
    }
    
    
    
    /**
     * 对Map集合的Keys升序排序生成一个新的LinkedMap
     * 
     * @param i_Map
     * @return
     */
    public final static <T1 extends Comparable<? super T1> ,T2> Map<T1 ,T2> toSort(Map<T1 ,T2> i_Map)
    {
        return toSortReverse(i_Map ,1);
    }
    
    
    
    /**
     * 对Map集合的Keys降序排序生成一个新的LinkedMap
     * 
     * @param i_Map
     * @return
     */
    public final static <T1 extends Comparable<? super T1> ,T2> Map<T1 ,T2> toReverse(Map<T1 ,T2> i_Map)
    {
        return toSortReverse(i_Map ,-1);
    }
    
    
    
    /**
     * 对Map集合的Keys排序生成一个新的LinkedMap
     * 
     * @param i_Map
     * @param i_Direction  排序方向。>= 0 表示升序。 < 0表示降序
     * @return
     */
    private final static <T1 extends Comparable<? super T1> ,T2> Map<T1 ,T2> toSortReverse(Map<T1 ,T2> i_Map ,int i_Direction)
    {
        Map<T1 ,T2> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            List<T1> v_Keys = toListKeys(i_Map);
            v_Ret = new LinkedHashMap<T1 ,T2>(i_Map.size());
            
            if ( i_Direction >= 0 )
            {
                Collections.sort(v_Keys);
            }
            else
            {
                Collections.sort(v_Keys ,Collections.reverseOrder());
            }
            
            for (T1 v_Key : v_Keys)
            {
                v_Ret.put(v_Key ,i_Map.get(v_Key));
            }
        }
        else
        {
            v_Ret = i_Map;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 对Map集合的Keys排序生成一个新的LinkedMap
     * 
     * 字符串转数字后，再比较排序（降序）
     * 
     * 优点是，不会改变原属性值的保存格式。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-12-19
     * @version     v1.0
     *
    * @param i_Map
     * @param i_Direction  排序方向。>= 0 表示升序。 < 0表示降序
     * @return
     */
    private final static <T2> Map<String ,T2> toSortReverseByNum(Map<String ,T2> i_Map ,int i_Direction)
    {
        Map<String ,T2> v_Ret = null;
        
        if ( !Help.isNull(i_Map) )
        {
            List<String> v_Keys = toListKeys(i_Map);
            v_Ret = new LinkedHashMap<String ,T2>(i_Map.size());
            
            if ( i_Direction >= 0 )
            {
                v_Keys = Help.toSortByNum(v_Keys.toArray(new String [] {}));
            }
            else
            {
                v_Keys = Help.toReverseByNum(v_Keys.toArray(new String [] {}));
            }
            
            for (String v_Key : v_Keys)
            {
                v_Ret.put(v_Key ,i_Map.get(v_Key));
            }
        }
        else
        {
            v_Ret = i_Map;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将字符串转成对应的类型
     * 
     * @param i_Class
     * @param i_Value
     * @return
     */
    public final static Object toObject(Class<?> i_Class ,String i_Value)
    {
        if ( i_Class == null || isNull(i_Value) )
        {
            return null;
        }
        else if ( i_Class == String.class )
        {
            return i_Value.trim();
        }
        else if ( i_Class == Integer.class )
        {
            return Integer.valueOf(i_Value.trim());
        }
        else if ( i_Class == int.class )
        {
            return Integer.parseInt(i_Value.trim());
        }
        else if ( i_Class == Double.class )
        {
            return Double.valueOf(i_Value.trim());
        }
        else if ( i_Class == double.class )
        {
            return Double.parseDouble(i_Value.trim());
        }
        else if ( i_Class == Float.class )
        {
            return Float.valueOf(i_Value.trim());
        }
        else if ( i_Class == float.class )
        {
            return Float.parseFloat(i_Value.trim());
        }
        else if ( i_Class == Boolean.class )
        {
            return Boolean.valueOf(i_Value.trim());
        }
        else if ( i_Class == boolean.class )
        {
            return Boolean.parseBoolean(i_Value.trim());
        }
        else if ( i_Class == Long.class )
        {
            return Long.valueOf(i_Value.trim());
        }
        else if ( i_Class == long.class )
        {
            return Long.parseLong(i_Value.trim());
        }
        else if ( i_Class == Date.class )
        {
            return new Date(i_Value.trim());
        }
        else if ( i_Class == java.util.Date.class )
        {
            return new Date(i_Value.trim()).getDateObject();
        }
        else if ( i_Class == BigDecimal.class )
        {
            return new BigDecimal(i_Value.trim());
        }
        else if ( MethodReflect.isExtendImplement(i_Class ,Enum.class) )
        {
            @SuppressWarnings("unchecked")
            Enum<?> [] v_EnumValues = StaticReflect.getEnums((Class<? extends Enum<?>>) i_Class);
            
            int v_Index = Integer.parseInt(i_Value.trim());
            if ( 0 <= v_Index && v_Index < v_EnumValues.length )
            {
                return v_EnumValues[v_Index];
            }
            else
            {
                throw new java.lang.IndexOutOfBoundsException("Enum [" + i_Class.getName() + "] is not find Value[" + v_Index + "].");
            }
        }
        else if ( i_Class == Short.class )
        {
            return Short.valueOf(i_Value.trim());
        }
        else if ( i_Class == short.class )
        {
            return Short.parseShort(i_Value.trim());
        }
        else if ( i_Class == Byte.class )
        {
            return Byte.valueOf(i_Value.trim());
        }
        else if ( i_Class == byte.class )
        {
            return Byte.parseByte(i_Value.trim());
        }
        else if ( i_Class == Character.class )
        {
            return Character.valueOf(i_Value.trim().charAt(0));
        }
        else if ( i_Class == char.class )
        {
            return i_Value.trim().charAt(0);
        }
        else
        {
            return i_Value.trim();
        }
    }
    
    
    
    /**
     * 将字符串转成对应的类型的默认值 
     * 
     * @param i_Class
     * @param i_Value
     * @return
     */
    public final static Object toObject(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return null;
        }
        else if ( i_Class == String.class )
        {
            return "";
        }
        else if ( i_Class == Integer.class )
        {
            return new Integer(0);
        }
        else if ( i_Class == int.class )
        {
            return new Integer(0);
        }
        else if ( i_Class == Double.class )
        {
            return new Double(0);
        }
        else if ( i_Class == double.class )
        {
            return new Double(0);
        }
        else if ( i_Class == Float.class )
        {
            return new Float(0);
        }
        else if ( i_Class == float.class )
        {
            return new Float(0);
        }
        else if ( i_Class == Boolean.class )
        {
            return Boolean.FALSE;
        }
        else if ( i_Class == boolean.class )
        {
            return Boolean.FALSE;
        }
        else if ( i_Class == Long.class )
        {
            return new Long(0);
        }
        else if ( i_Class == long.class )
        {
            return new Long(0);
        }
        else if ( i_Class == Date.class )
        {
            return new Date();
        }
        else if ( i_Class == java.util.Date.class )
        {
            return new Date().getDateObject();
        }
        else if ( i_Class == BigDecimal.class )
        {
            return new BigDecimal(0);
        }
        else if ( MethodReflect.isExtendImplement(i_Class ,Enum.class) )
        {
            @SuppressWarnings("unchecked")
            Enum<?> [] v_EnumValues = StaticReflect.getEnums((Class<? extends Enum<?>>) i_Class);
            
            return v_EnumValues[0];
        }
        else if ( i_Class == Short.class )
        {
            return new Short((short)0);
        }
        else if ( i_Class == short.class )
        {
            return new Short((short)0);
        }
        else if ( i_Class == Byte.class )
        {
            return new Byte((byte)0);
        }
        else if ( i_Class == byte.class )
        {
            return new Byte((byte)0);
        }
        else if ( i_Class == Character.class )
        {
            return new Character(' ');
        }
        else if ( i_Class == char.class )
        {
            return new Character(' ');
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 从包package中获取所有的Class 
     * 
     * @param i_PackageName  包路径。如java.lang.ref
     * @return
     */
    public final static List<Class<?>> getClasses()
    {
        return getClasses("" ,true);
    }
    
    
    
    /**
     * 从包package中获取所有的Class 
     * 
     * @param i_PackageName  包路径。如java.lang.ref。同时，也可以是具体的Java类，如 java.lang.ref.Reference
     * @return
     */
    public final static List<Class<?>> getClasses(String i_PackageName)
    {
        return getClasses(Help.NVL(i_PackageName) ,true);
    }
    
    
    
    /** 
     * 从包package中获取所有的Class 
     * 
     * @param i_PackageName  包路径。如java.lang.ref。同时，也可以是具体的Java类，如 java.lang.ref.Reference
     * @param i_Recursive    是否循环迭代 
     * @return 
     * 
     * 参考于：mailto:ohergal@gmail.com
     */  
    public final static List<Class<?>> getClasses(String i_PackageName ,boolean i_Recursive)
    {
        List<Class<?>> v_Classes        = new ArrayList<Class<?>>();
        String         v_PackageDirName = Help.NVL(i_PackageName).trim().replace('.' ,'/');
        boolean        v_IsWhile        = false;
        
        try
        {
            // 定义一个枚举的集合 并进行循环来处理这个目录
            Enumeration<URL> v_Urls = Thread.currentThread().getContextClassLoader().getResources(v_PackageDirName);
            while ( v_Urls.hasMoreElements() )
            {
                v_IsWhile = true;
                URL v_Url = v_Urls.nextElement();
                
                // 如果是以文件的形式保存在服务器上
                if ( "file".equals(v_Url.getProtocol()) )
                {
                    File v_File = new File(URLDecoder.decode(v_Url.getFile() ,"UTF-8"));
                    
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    getClasses(Help.NVL(i_PackageName) ,v_File ,i_Recursive ,v_Classes);
                    
                    v_File = null;
                }
                // 如果是jar包文件
                else if ( "jar".equals(v_Url.getProtocol()) )
                {
                    v_Classes.addAll(getClasses(Help.NVL(i_PackageName) ,((JarURLConnection) v_Url.openConnection()).getJarFile()));
                }
            }
            
            if ( !v_IsWhile )
            {
                try
                {
                    Class<?> v_Class = Help.forName(i_PackageName.trim());
                    
                    if ( v_Class != null )
                    {
                        v_Classes.add(v_Class);
                    }
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return v_Classes;
    }
    
    
    
    /**
     * 从Jar包中获取所有的Class 
     * 
     * @param i_JarFile       Jar文件
     * @return
     */
    public final static List<Class<?>> getClasses(JarFile i_JarFile)
    {
        return getClasses("" ,i_JarFile ,true);
    }
    
    
    
    /**
     * 从Jar包中获取所有的Class 
     * 
     * @param i_PackageName   包路径。如java.lang.ref。同时，也可以是具体的Java类，如 java.lang.ref.Reference
     * @param i_JarFile       Jar文件
     * @return
     */
    public final static List<Class<?>> getClasses(String i_PackageName ,JarFile i_JarFile)
    {
        return getClasses(Help.NVL(i_PackageName) ,i_JarFile ,true);
    }
    
    
    
    /**
     * 从Jar包中获取所有的Class 
     * 
     * @param i_PackageName   包路径。如java.lang.ref。同时，也可以是具体的Java类，如 java.lang.ref.Reference
     * @param i_JarFile       Jar文件
     * @param i_Recursive     是否循环迭代(暂时没有用，所以本方法暂时不对外开放)
     * @return
     */
    private static List<Class<?>> getClasses(String i_PackageName ,JarFile i_JarFile ,boolean i_Recursive)
    {
        List<Class<?>> v_Classes        = new ArrayList<Class<?>>();
        String         v_PackageDirName = Help.NVL(i_PackageName).trim().replace('.' ,'/');
        
        try
        {
            Enumeration<JarEntry> v_Entries = i_JarFile.entries();
            while ( v_Entries.hasMoreElements() )
            {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry v_JarEntry = v_Entries.nextElement();
                String   v_Name     = v_JarEntry.getName();
                
                // 如果是以/开头的
                if ( v_Name.charAt(0) == '/' )
                {
                    // 获取后面的字符串
                    v_Name = v_Name.substring(1);
                }
                
                // 如果前半部分和定义的包名相同
                if ( v_Name.startsWith(v_PackageDirName) )
                {
                    if ( v_Name.endsWith(".class") && !v_JarEntry.isDirectory() )
                    {
                        // 去掉后面的".class" 获取真正的类名
                        String v_ClassName = v_Name.substring(0 ,v_Name.length() - 6).replaceAll("/" ,".");
                        try
                        {
                            v_Classes.add(Help.forName(v_ClassName));
                        }
                        catch (ClassNotFoundException e)
                        {
                            // Nothing.
                            // 对于内部混淆过的类，无法获取Class
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return v_Classes;
    }
    
    
    
    /**
     * 从指定文件目录中获取所有的Class 
     * 
     * @param i_PackagePath   指定的文件目录
     * @param i_Recursive     是否循环迭代
     * @return
     */
    public final static List<Class<?>> getClasses(File i_PackagePath ,boolean i_Recursive)
    {
        List<Class<?>> v_Classes = new ArrayList<Class<?>>();
        
        getClasses("" ,i_PackagePath ,i_Recursive ,v_Classes);
        
        return v_Classes;
    }
    
    
    
    /**
     * 从指定文件目录中获取所有的Class 
     * 
     * @param i_PackageName   包路径。如java.lang.ref
     * @param i_PackagePath   指定的文件目录
     * @param i_Recursive     是否循环迭代
     * @return
     */
    public final static List<Class<?>> getClasses(String i_PackageName ,File i_PackagePath ,boolean i_Recursive)
    {
        List<Class<?>> v_Classes = new ArrayList<Class<?>>();
        
        getClasses(Help.NVL(i_PackageName) ,i_PackagePath ,i_Recursive ,v_Classes);
        
        return v_Classes;
    }
    
    
    
    /** 
     * 从指定文件目录中获取所有的Class 
     * 
     * 为了不在递归中反复创建 new ArrayList<Class<?>>(); 而存在此方法。
     * 所以，本方法不对外公开 
     * 
     * @param i_PackageName  包路径。如java.lang.ref
     * @param i_PackagePath  包的物理路径
     * @param i_Recursive    是否循环迭代
     * @param io_Classes 
     */  
    private static void getClasses(String i_PackageName ,File i_PackagePath ,final boolean i_Recursive ,List<Class<?>> io_Classes)
    {
        if ( !i_PackagePath.exists() || !i_PackagePath.isDirectory() )
        {
            return;
        }
        
        File [] v_Files = i_PackagePath.listFiles(new FileFilter() 
        {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
            public boolean accept(File i_File)
            {
                return (i_Recursive && i_File.isDirectory()) || (i_File.getName().endsWith(".class"));
            }
        });
        
        if ( Help.isNull(v_Files) )
        {
            return;
        }
        
        
        // 包路径为空的情况，主要用于全部遍历的情况
        if ( Help.isNull(i_PackageName) )
        {
            for (int i=0; i<v_Files.length; i++)
            {
                File v_File = v_Files[i];
                
                // 如果是目录 则继续扫描  
                if ( v_File.isDirectory() )
                {
                    getClasses(v_File.getName() ,v_File ,i_Recursive ,io_Classes);
                }
                else
                {
                    // 如果是java类文件 去掉后面的.class 只留下类名  
                    String v_ClassName = v_File.getName().substring(0 ,v_File.getName().length() - 6);
                    try
                    {
                        io_Classes.add(Help.forName(v_ClassName));
                    }
                    catch (ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            for (int i=0; i<v_Files.length; i++)
            {
                File v_File = v_Files[i];
                
                // 如果是目录 则继续扫描  
                if ( v_File.isDirectory() )
                {
                    getClasses(i_PackageName + "." + v_File.getName() ,v_File ,i_Recursive ,io_Classes);
                }
                else
                {
                    // 如果是java类文件 去掉后面的.class 只留下类名  
                    String v_ClassName = v_File.getName().substring(0 ,v_File.getName().length() - 6);
                    try
                    {
                        /*
                        if ( v_ClassName.endsWith("ScanFileCache") )
                        {
                            // 不知道为什么这个类会在 Class.forName 后引起线程数增加
                            System.out.println("-- Thread.currentThread().activeCount() = " + Thread.currentThread().activeCount());
                        }
                        */
                        io_Classes.add(Help.forName(i_PackageName + '.' + v_ClassName));
                    }
                    catch (ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    
    /**
     * forName() 将 initialize 设定为 false，这样在加载类时并不会立即运行静态区块，而会在使用类建立对象时才运行静态区块。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-12-20
     * @version     v1.0
     *
     * @param i_ClassName
     * @return
     * @throws ClassNotFoundException
     */
    public final static Class<?> forName(String i_ClassName) throws ClassNotFoundException
    {
        try
        {
            return Class.forName(i_ClassName ,false ,Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException exce)
        {
            return Class.forName(i_ClassName);
        }
    }
    
    
    
    /**
     * 查找简单序列化对象中的属性名称对应的属性索引号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-20
     * @version     v1.0
     *
     * @param i_Object
     * @param i_PropertyName  属性名称，不区分大小写。
     * @return                查找不到或异常，返回 -1
     */
    public final static int gatPropertyIndex(Serializable i_Object ,String i_PropertyName)
    {
        if ( i_Object == null || Help.isNull(i_PropertyName) )
        {
            return -1;
        }
        
        int    v_PIndex = -1;
        String v_SPName   = "get" + i_PropertyName.trim();
        String v_SPNameIs = "is"  + i_PropertyName.trim();
        for (int v_Index=0; v_Index<i_Object.gatPropertySize(); v_Index++)
        {
            if ( i_Object.gatPropertyName(v_Index).equalsIgnoreCase(v_SPName)
              || i_Object.gatPropertyName(v_Index).equalsIgnoreCase(v_SPNameIs) )
            {
                v_PIndex = v_Index;
                break;
            }
        }
        
        return v_PIndex;
    }
    
}
