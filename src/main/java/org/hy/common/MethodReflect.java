package org.hy.common;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hy.common.comparate.MethodComparator;
import org.hy.common.comparate.MethodFieldComparator;





/**
 * 方法的反射。
 * 
 * 1. 可实现xxx.yyy.www(或getXxx.getYyy.setWww)全路径的解释。不再区分大小写
 * 2. 可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释。不再区分大小写
 * 3. 可实现xxx(p1).yyy(p1 ,p2).www 方法带参数的全路径的解释。不再区分大小写
 * 4. 无须反射，只许简单的执行方法就成。应用于：外界已明确了待执行的方法（通常是getter方法）。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2012-09-21
 * @version     v2.0  2014-07-11  可实现xxx(p1).yyy(p1 ,p2).www 方法带参数的全路径的解释。
 *              v3.0  2016-03-18  添加新的构造器。无须反射，只许简单的执行方法就成。应用于：外界已明确了待执行的方法（通常是getter方法）。
 *                                添加：向数据库表插入数据时，通过Java生成主键的功能。与SQL占位符配合使用。
 *              v4.0  2016-07-30  添加：getMapValue()方法，从Map集合中取值。实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
 *              v4.1  2017-01-21  修正：isExtendImplement()方法对接口继承的接口与要作判定。
 *              v4.2  2017-02-15  添加：将"GET"、"SET"两个分区关键字对外公开。
 *              v5.0  2017-03-18  添加：Class<?> 类型对识别方法反射的构造器
 *              v5.1  2017-03-23  添加：xxx.$getYyy.www 全路径中$符号后跟的为完整方法名称的解释功能。
 *                                     即，全路径中一部是简写方法名称的方式，一部又是方法的完整名称。
 *              v6.0  2017-06-15  添加：MethodReflect()构造器中入参 "i_MethodURL方法全路径" 中的每个方法名称，不再区分大小写
 *              v7.0  2017-06-23  修正：三个invokeForInstance(...)方法在中间实现对象为Null的处理。
 *              v8.0  2017-07-12  修改：所有方法名称的判定都不再区分大小。
 *                                修正：当方法名称正的以$开头时($为本类的关键字符，见v5.1)，也要能正确匹配到方法。parser()方法除外。
 *              v9.0  2017-11-24  添加：invokeSet(...)调用对象的Setter赋值。
 *              v10.0 2017-12-18  添加：getParameterAnnotations(...)
 *              v11.0 2017-12-23  修正：MethodReflect实现序列接口，但this.methods的类型Method是非序列，用MethodInfo代替。
 *              v12.0 2018-01-18  添加：支持BigDecimal类型
 *              v12.1 2018-01-29  添加：扫描项目所有类时，当发现某一类引用的类不存在时，只错误提示不中断服务。
 *              v12.2 2018-03-11  添加：解释方法全路径parser()，最后一个Getter方法支持isXXX()方法，支持逻辑方法。
 *              v12.3 2018-04-26  添加：按方法对应的成员属性名称在类中的编程编写的顺序排序的getGetSetMethods(...)。
 *              v12.4 2018-05-04  添加：isExtendImplement()方法判定基本类型与包装类型是否一样。
 *              v12.5 2018-05-08  添加：支持枚举toString()的匹配
 *                                修改： 当子类实现父类接口时，方法重载时，可能出现方法名称相同的两个getter方法。
 *                                      方法按修饰符排序后取首个方法，不再向外界抛错。
 *              v12.6 2018-05-15  添加：数据库java.sql.Timestamp时间的转换
 *              v13.0 2020-01-14  添加：获取排除某些前缀的成员方法 getMethodsExcludeStart()
 *              v13.1 2021-02-01  修正：解释xx.yy.zz时，当yy为空指针时的解释异常
 *              v14.0 2021-12-16  添加：判定类是否允许被实例化（默认无参构造器的实例化）allowNew()
 *              v14.0 2022-05-17  优化：isExtendImplement 方法的判定性能 10万次计算减少用时3秒
 *              v15.0 2025-07-04  修正：在替换子组方法路径时，用 StringHelp.replaceFirst() 方法替换 StringHelp.replaceAll() 方法
 *              v16.0 2025-07-14  添加：从List集合中取值。实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
 *              v16.1 2025-07-15  添加：支持 Map.Object.List.0.name 的方法路径取值
 */
public class MethodReflect implements Serializable
{
    
    private static final long serialVersionUID = -4388208505011166797L;
    
    

    /** Getter、is方法的分区关键字。主用于 getGetSetMethods() 方法的返回值：分区结构 */
    public static final String $Partition_GET         = "GET";
    
    /** Setter方法的分区关键字。主用于 getGetSetMethods() 方法的返回值：分区结构 */
    public static final String $Partition_SET         = "SET";
    
    
    
    /**
     * 类似于Excel单元格中的固定引用单元格的标示符。
     * 
     * 当方法的全路径为：xxx.$getYyy.www 时，$符号表示 getYyy 是一个方法的完整名称，无须再添加 "get"、"is" 或 "set" 前缀。
     * 
     * 只对 isNorm = true 有效
     */
    public final static String $FixedMethodName       = "$";
    
    /**
     * 占位符的标示符
     */
    public final static String $Placeholder           = ":";
    
    /**
     * 占位符+变量名称的正式表达式片段
     */
    private final static String $PV                   = "[\\" + $Placeholder + "\\w]";
    
    /**
     * 正则表达式对：方法名称的识别
     * 如：add(row)
     */
    private final static String $REGEX_METHOD         = "[\\" + $FixedMethodName + "\\w]+[\\(]";
    
    /**
     * 正则表达式对：方法填写有效性的验证。
     * 如：xxx(p1 ,p2 ,... pn)
     * 如：xxx(o1.p1 ,o2.p1 ,... on.pn)
     */
    private final static String $REGEX_METHOD_VERIFY  = "^[\\" + $FixedMethodName + "\\w]+\\( *((" + $PV + "+\\." + $PV + "+ *, *)|(" + $PV + "+ *, *))*((" + $PV + "+\\." + $PV + "+)|(" + $PV + "+)) *\\)$";
    
    
    
    /** Setter规范的方法 */
    public final static int $NormType_Setter = 1;
    
    /** Getter规范的方法 */
    public final static int $NormType_Getter = -1;
    
    /** 继承及实现的判断的高速缓存 */
    private final static TablePartitionRID<Class<?> ,Boolean> $IsExtendImplementCache = new TablePartitionRID<Class<?> ,Boolean>();
    
    
    
    /**
     * i_MethodURL整体是否为规范的setter或getter方法
     * 
     * 即，当 isNorm = true  时，i_MethodURL = xxx.yyy.www
     * 相等于 isNorm = false 时，i_MethodURL = getXxx.getYyy.setWww
     **/
    private boolean                isNorm;
    
    /**
     *  规范的类型
     * 
     *  1. normType =  1 为 setter 方法，如：getXxx.getYyy.setWww
     *  2. normType = -1 为 getter 方法，如：getXxx.getYyy.getWww
     **/
    private int                    normType;
    
    /** 方法全路径的Mehod返回对象类型的集合 */
    private List<Class<?>>         classes;
    
    /** 方法全路径的Mehod返回对象实例的集合。第一元素为构造器的第一个参数值 */
    private List<Object>           instances;
    
    /**
     * 方法全路径的Mehod的集合
     * 外层List对应：方法全路径上.第几层次上方法
     * 内层List对应：具体层次上的多个相同重载的方法
     */
    private List<List<MethodInfo>> methods;
    
    /**
     * 方法全路径的Mehod的集合的参数集合
     * 外层List对应：方法全路径上.第几层次上方法
     * 内层List对应：具体层次上的方法的多个入参参数
     */
    private List<List<String>>     methodsParams;
    
    /** 方法全路径的分段数组 */
    private String []              methodNames;
    
    /** 方法名称的全路径 */
    private String                 methodURL;
    
    
    
    /**
     * 方法填写有效性的验证
     * 
     * 如：xxx(p1 ,p2 ,... pn)
     * 如：xxx(o1.p1 ,o2.p1 ,... on.pn)
     * 
     * @param i_Text
     * @return
     */
    public static boolean methodVerify(String i_Text)
    {
        if ( Help.isNull(i_Text) )
        {
            return false;
        }
        
        Pattern v_Pattern = Pattern.compile($REGEX_METHOD_VERIFY);
        Matcher v_Matcher = v_Pattern.matcher(i_Text);
        
        return v_Matcher.find();
    }
    
    
    
    /**
     * 解释如 xxx(p1 ,p2 ,... pn) 格式的方法名称
     * 
     * @param i_Text
     * @return
     */
    public static String parserMethodName(String i_Text)
    {
        if ( methodVerify(i_Text) )
        {
            Pattern v_Pattern    = Pattern.compile($REGEX_METHOD);
            Matcher v_Matcher    = v_Pattern.matcher(i_Text);
            String  v_MethodName = "";
            
            // 识别行级填充方法名称
            if ( v_Matcher.find() )
            {
                v_MethodName = v_Matcher.group();
                v_MethodName = v_MethodName.substring(0 ,v_MethodName.length() - 1);
            }
            else
            {
                throw new RuntimeException("Method name[" + i_Text + "] is not exist.");
            }
            
            return v_MethodName;
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 解释如 xxx(p1 ,p2 ,... pn) 格式的方法参数
     * 
     * @param i_Text
     * @return
     */
    public static String [] parserMethodParams(String i_Text)
    {
        if ( methodVerify(i_Text) )
        {
            Pattern v_Pattern    = Pattern.compile($REGEX_METHOD);
            Matcher v_Matcher    = v_Pattern.matcher(i_Text);
            String  v_MethodName = "";
            int     v_EndIndex   = 0;
            String  v_Params     = null;
            
            
            // 识别行级填充方法名称
            if ( v_Matcher.find() )
            {
                v_MethodName = v_Matcher.group();
                v_MethodName = v_MethodName.substring(0 ,v_MethodName.length() - 1);
                v_EndIndex   = v_Matcher.end();
            }
            else
            {
                throw new RuntimeException("Method name[" + i_Text + "] is not exist.");
            }
            
            
            v_Params = i_Text.substring(v_EndIndex ,i_Text.length() - 1);
            return v_Params.split(",");
        }
        else
        {
            return new String[0];
        }
    }
    
    
    
    /**
     * 继承及实现的判断
     * 
     * 1. 判断某个类是否实现了i_InterfaceClass接口
     * 2. 递归判断某个类实现的接口中是否继承了i_InterfaceClass接口
     * 3. 递归判断某个类实现的接口的继承的接口是否继承了i_InterfaceClass接口
     * 4. 判断某个类是否继承了i_InterfaceClass类
     * 5. 递归判断某个类的父类是否实现了i_InterfaceClass接口
     * 6. 递归判断某个类的父类是否继承了i_InterfaceClass类
     * 
     * @param i_ObjectClass     类的元类型
     * @param i_InterfaceClass  接口的元类型
     * @return
     */
    public static boolean isExtendImplement(Object i_Object ,Class<?> i_InterfaceClass)
    {
        return isExtendImplement(i_Object.getClass() ,i_InterfaceClass);
    }
    
    
    
    /**
     * 继承及实现的判断
     * 
     * 1. 判断某个类是否实现了i_InterfaceClass接口
     * 2. 递归判断某个类实现的接口中是否继承了i_InterfaceClass接口
     * 3. 递归判断某个类实现的接口的继承的接口是否继承了i_InterfaceClass接口
     * 4. 判断某个类是否继承了i_InterfaceClass类
     * 5. 递归判断某个类的父类是否实现了i_InterfaceClass接口
     * 6. 递归判断某个类的父类是否继承了i_InterfaceClass类
     * 
     * @param i_ObjectClass     类的元类型
     * @param i_InterfaceClass  接口的元类型
     * @return
     */
    public static boolean isExtendImplement(Class<?> i_ObjectClass ,Class<?> i_InterfaceClass)
    {
        if ( i_ObjectClass == i_InterfaceClass )
        {
            return true;
        }
        else if ( i_ObjectClass == null || i_InterfaceClass == null )
        {
            return false;
        }
        
        String  v_InterfaceClassName = i_InterfaceClass.getName();
        Boolean v_Ret                = $IsExtendImplementCache.getRow(i_ObjectClass ,v_InterfaceClassName);
        if ( v_Ret != null )
        {
            return v_Ret;
        }
        
        // 判定Boolean.class == boolean.class  ZhengWei(HY) Add 2018-05-04
        // 对于Number类型，不处理
        if ( i_ObjectClass.getName().startsWith("java.lang") )
        {
            String v_LangName1 = i_ObjectClass   .getSimpleName().toLowerCase();
            String v_LangName2 = i_InterfaceClass.getSimpleName().toLowerCase();
            
            if ( v_LangName1.startsWith(v_LangName2) )
            {
                $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,true);
                return true;
            }
            else
            {
                // 不能简单的返回 false。如当 String 与 java.io.Serializable 比较时也应返回true的。
            }
        }
        // 判定boolean.class == Boolean.class  ZhengWei(HY) Add 2018-05-04
        // 对于Number类型，不处理
        else if ( v_InterfaceClassName.startsWith("java.lang") )
        {
            String v_LangName1 = i_InterfaceClass.getSimpleName().toLowerCase();
            String v_LangName2 = i_ObjectClass   .getSimpleName().toLowerCase();
            
            if ( v_LangName1.startsWith(v_LangName2) )
            {
                $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,true);
                return true;
            }
            else
            {
                // 不能简单的返回 false。如当 String 与 java.io.Serializable 比较时也应返回true的。
            }
        }
        
        Class<?> [] v_Interfaces = i_ObjectClass.getInterfaces();
        
        for (int i=0; i<v_Interfaces.length; i++)
        {
            // 判断某个类是否实现了i_InterfaceClass接口
            if ( v_Interfaces[i] == i_InterfaceClass )
            {
                $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,true);
                return true;
            }
            
            // 递归判断某个类实现的接口中是否继承了i_InterfaceClass接口
            if ( isExtendImplement(v_Interfaces[i].getSuperclass() ,i_InterfaceClass) )
            {
                $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,true);
                return true;
            }
            
            // 判断接口类的继承的接口  ZhengWei(HY) Add 2017-01-21
            Class<?> [] v_Interface_Interfaces = v_Interfaces[i].getInterfaces();
            for (int x=0; x<v_Interface_Interfaces.length; x++)
            {
                if ( isExtendImplement(v_Interface_Interfaces[x] ,i_InterfaceClass) )
                {
                    $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,true);
                    return true;
                }
            }
        }
        
        // 判断某个类是否继承了i_InterfaceClass类
        if ( i_ObjectClass.getSuperclass() == i_InterfaceClass )
        {
            $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,true);
            return true;
        }
        else if ( i_ObjectClass.getSuperclass() == Object.class )
        {
            $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,false);
            return false;
        }
        else
        {
            // 递归判断某个类的父类是否实现了i_InterfaceClass接口
            // 递归判断某个类的父类是否继承了i_InterfaceClass类
            v_Ret = isExtendImplement(i_ObjectClass.getSuperclass() ,i_InterfaceClass);
            $IsExtendImplementCache.putRow(i_ObjectClass ,v_InterfaceClassName ,v_Ret);
            return v_Ret;
        }
    }
    
    
    
    /**
     * 判定类是否允许被实例化（默认无参构造器的实例化）
     * 
     * 即判定执行 i_ObjectClass.newInstance() 是否会报错
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-16
     * @version     v1.0
     * 
     * @param i_ObjectClass
     * @return
     */
    public static boolean allowNew(Class<?> i_ObjectClass)
    {
        if ( i_ObjectClass == null )
        {
            return false;
        }
        
        Constructor<?>[] v_Constructors = i_ObjectClass.getConstructors();
        if ( Help.isNull(v_Constructors) )
        {
            return false;
        }
            
        for (Constructor<?> v_Item : v_Constructors)
        {
            if ( v_Item.getParameters().length == 0 )
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    
    /**
     * 获取方法首个入参参数上的数组元素Class原型（方法入参类型为数组）
     * 
     * @param i_Method
     * @return
     */
    public static Class<?> getArrayElementType(Method i_Method)
    {
        return getArrayElementType(i_Method ,0);
    }
    
    
    
    /**
     * 获取方法某个入参参数上的数组元素Class原型（方法入参类型为数组）
     * 
     * @param i_Method
     * @param i_ParamIndex      方法的入参参数位置
     * @return
     */
    public static Class<?> getArrayElementType(Method i_Method ,int i_ParamIndex)
    {
        try
        {
            Class<?> v_ArrayClass = i_Method.getParameterTypes()[i_ParamIndex];
            return v_ArrayClass.getComponentType();
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取某个Java类的属性上首个位置的范型Class原型
     * 
     * @param i_Class
     * @param i_FieldName  属性名称（只能是本类自有的属性）
     * @return
     */
    public static Class<?> getGenerics(Class<?> i_Class ,String i_FieldName)
    {
        try
        {
            return getGenerics(i_Class.getDeclaredField(i_FieldName));
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取某个Java类的属性上首个位置的范型Class原型
     * 
     * @param i_Class
     * @param i_FieldName      属性名称（只能是本类自有的属性）
     * @param i_GenericsIndex  范型的位置
     * @return
     */
    public static Class<?> getGenerics(Class<?> i_Class ,String i_FieldName ,int i_GenericsIndex)
    {
        try
        {
            return getGenerics(i_Class.getDeclaredField(i_FieldName) ,i_GenericsIndex);
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取某个Java类的方法上某个入参参数的某个位置的范型Class原型
     * 
     * @param i_Class
     * @param i_MethodName     方法名称
     * @param i_ParamSize      方法入参的个数
     * @param i_GenericsIndex  范型的位置
     * @return
     */
    public static Class<?> getGenerics(Class<?> i_Class ,String i_MethodName ,int i_ParamSize ,int i_GenericsIndex)
    {
        try
        {
            return getGenerics(getMethods(i_Class ,i_MethodName ,i_ParamSize).get(0) ,i_GenericsIndex);
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取属性上首个位置的范型Class原型
     * 
     * @param i_Field
     * @return
     */
    public static Class<?> getGenerics(Field i_Field)
    {
        return getGenerics(i_Field ,0);
    }
    
    
    
    /**
     * 获取属性上某个位置的范型Class原型
     * 
     * @param i_Field
     * @param i_GenericsIndex  范型的位置
     * @return
     */
    public static Class<?> getGenerics(Field i_Field ,int i_GenericsIndex)
    {
        try
        {
            ParameterizedType v_PType = (ParameterizedType) i_Field.getGenericType();
            Type              v_Type  = v_PType.getActualTypeArguments()[i_GenericsIndex];
            return (Class<?>)v_Type;
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取方法首个入参参数上的首个位置的范型Class原型
     * 
     * @param i_Method
     * @return
     */
    public static Class<?> getGenerics(Method i_Method)
    {
        return getGenerics(i_Method ,0 ,0);
    }
    
    
    
    /**
     * 获取方法某个入参参数上的首个位置的范型Class原型
     * 
     * @param i_Method
     * @param i_ParamIndex
     * @return
     */
    public static Class<?> getGenerics(Method i_Method ,int i_ParamIndex)
    {
        return getGenerics(i_Method ,i_ParamIndex ,0);
    }
    
    
    
    /**
     * 获取方法某个入参参数上的某个位置的范型Class原型
     * 
     * @param i_Method
     * @param i_ParamIndex      方法的入参参数位置
     * @param i_GenericsIndex   入参参数范型的位置
     * @return
     */
    public static Class<?> getGenerics(Method i_Method ,int i_ParamIndex ,int i_GenericsIndex)
    {
        try
        {
            ParameterizedType v_PType = (ParameterizedType) i_Method.getGenericParameterTypes()[i_ParamIndex];
            Type              v_Type  = v_PType.getActualTypeArguments()[i_GenericsIndex];
            return (Class<?>)v_Type;
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取方法返回值上的首个位置的范型Class原型
     * 
     * @param i_Class
     * @param i_MethodName      方法名称
     * @param i_ParamSize       方法入参的个数
     * @return
     */
    public static GenericsReturn getGenericsReturn(Class<?> i_Class ,String i_MethodName ,int i_ParamSize)
    {
        return getGenericsReturn(i_Class ,i_MethodName ,i_ParamSize ,0);
    }
    
    
    
    /**
     * 获取方法返回值上的某个位置的范型Class原型
     * 
     * @param i_Class
     * @param i_MethodName      方法名称
     * @param i_ParamSize       方法入参的个数
     * @param i_GenericsIndex   范型的位置
     * @return
     */
    public static GenericsReturn getGenericsReturn(Class<?> i_Class ,String i_MethodName ,int i_ParamSize ,int i_GenericsIndex)
    {
        try
        {
            return getGenericsReturn(getMethods(i_Class ,i_MethodName ,i_ParamSize).get(0) ,i_GenericsIndex);
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取方法返回值上的首个位置的范型Class原型
     * 
     * @param i_Method
     * @return
     */
    public static GenericsReturn getGenericsReturn(Method i_Method)
    {
        return getGenericsReturn(i_Method ,0);
    }
    
    
    
    /**
     * 获取方法返回值上的某个位置的范型Class原型
     * 
     * @param i_Method
     * @param i_GenericsIndex   范型的位置
     * @return
     */
    public static GenericsReturn getGenericsReturn(Method i_Method ,int i_GenericsIndex)
    {
        GenericsReturn v_GR = new GenericsReturn();
        
        try
        {
            ParameterizedType v_PType = (ParameterizedType) i_Method.getGenericReturnType();
            Type              v_Type  = v_PType.getActualTypeArguments()[i_GenericsIndex];
            
            while ( v_Type instanceof ParameterizedType )
            {
                v_PType = (ParameterizedType) v_Type;
                v_GR.addMaster((Class<?>) (v_PType.getRawType()) );
                
                v_Type = v_PType.getActualTypeArguments()[i_GenericsIndex];
            }
            
            v_GR.setGenericType( (Class<?>)v_Type );
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        return v_GR;
    }
    
    
    
    /**
     * 获取Java类的父类上的首个位置的范型Class原型
     * 
     * @param i_Class
     * @return
     */
    public static Class<?> getGenericsSuper(Class<?> i_Class)
    {
        return getGenericsSuper(i_Class ,0);
    }
    
    
    
    /**
     * 获取Java类的父类上的某个位置的范型Class原型
     * 
     * @param i_Class
     * @param i_GenericsIndex   范型的位置
     * @return
     */
    public static Class<?> getGenericsSuper(Class<?> i_Class ,int i_GenericsIndex)
    {
        try
        {
            ParameterizedType v_PType = (ParameterizedType) i_Class.getGenericSuperclass();
            Type              v_Type  = v_PType.getActualTypeArguments()[i_GenericsIndex];
            return (Class<?>)v_Type;
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    public static Class<?> getGenerics(Class<?> i_Class ,int i_GenericsIndex)
    {
        try
        {
            ParameterizedType v_PType = (ParameterizedType) i_Class.getGenericInterfaces()[0];
            Type              v_Type  = v_PType.getActualTypeArguments()[i_GenericsIndex];
            return (Class<?>)v_Type;
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    public static <V> Class<?> getGenerics(List<V> i_List)
    {
        try
        {
            return getGenerics(getMethods(i_List.getClass() ,"add" ,1).get(0));
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取默认 Setter 方法（入参个数只有一个的）
     * 
     * @param i_Class
     * @param i_SetMethodName
     * @param i_IsNorm
     * @return
     */
    public static Method getSetMethod(Class<?> i_Class ,String i_SetMethodName ,boolean i_IsNorm)
    {
        String v_SetMethodName      = i_SetMethodName.trim();
        String v_SetMethodNameFixed = null;
        
        if ( i_IsNorm )
        {
            if ( v_SetMethodName.startsWith($FixedMethodName) )
            {
                v_SetMethodNameFixed = "set" + v_SetMethodName;
                v_SetMethodName      = v_SetMethodName.substring(1);
            }
            else
            {
                v_SetMethodName = "set" + StringHelp.toUpperCaseByFirst(v_SetMethodName);
            }
        }
        
        Method [] v_Methods = i_Class.getMethods();
        
        
        for (int i=0; i<v_Methods.length; i++)
        {
            if ( v_Methods[i].getName().equalsIgnoreCase(v_SetMethodName) )
            {
                if ( v_Methods[i].getParameterTypes().length == 1 )
                {
                    return v_Methods[i];
                }
            }
        }
        
        // 当方法名称真的是get$xxx()、set$xxx()的形式，也是要尝试查询匹配一次的  2017-07-12
        if ( v_SetMethodNameFixed != null )
        {
            for (int i=0; i<v_Methods.length; i++)
            {
                if ( v_Methods[i].getName().equalsIgnoreCase(v_SetMethodNameFixed) )
                {
                    if ( v_Methods[i].getParameterTypes().length == 1 )
                    {
                        return v_Methods[i];
                    }
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 获取多个重载的 Setter 方法集合（入参个数只有一个的）
     * 
     * @param i_Class
     * @param i_SetMethodName
     * @param i_IsNorm
     * @return
     */
    public static List<Method> getSetMethods(Class<?> i_Class ,String i_SetMethodName ,boolean i_IsNorm)
    {
        List<Method> v_Ret                = new ArrayList<Method>();
        String       v_SetMethodName      = i_SetMethodName.trim();
        String       v_SetMethodNameFixed = null;
        
        
        if ( i_IsNorm )
        {
            if ( v_SetMethodName.startsWith($FixedMethodName) )
            {
                v_SetMethodNameFixed = "set" + v_SetMethodName;
                v_SetMethodName      = v_SetMethodName.substring(1);
            }
            else
            {
                v_SetMethodName = "set" + StringHelp.toUpperCaseByFirst(v_SetMethodName);
            }
        }
        
        Method [] v_Methods = i_Class.getMethods();
        
        // 当方法名称真的是get$xxx()、set$xxx()的形式，也是要尝试查询匹配一次的  2017-07-12
        for (int i=0; i<v_Methods.length; i++)
        {
            // 不再区分大小写 2017-07-12
            if ( v_Methods[i].getName().equalsIgnoreCase(v_SetMethodName)
              || v_Methods[i].getName().equalsIgnoreCase(v_SetMethodNameFixed) )
            {
                if ( v_Methods[i].getParameterTypes().length == 1 )
                {
                    v_Ret.add(v_Methods[i]);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合中的Getter方法
     * 
     * 返回结果是按方法名称排序的。
     * 
     * @param i_Class
     * @return         Map.key    为方法的短名称
     *                 Map.value  为方法对象
     */
    public static Map<String ,Method> getGetMethodsMS(Class<?> i_Class)
    {
        return getGetSetMethods(i_Class).get($Partition_GET);
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合中的Getter方法
     * 
     * 返回结果是按方法名称排序的。
     * 
     * 方法名称排序规则升级为：按方法对应的成员属性名称在类中的编程编写的顺序排序。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-26
     * @version     v1.0
     *
     * @param i_Class
     * @return         Map.key    为方法的短名称
     *                 Map.value  为方法对象
     */
    public static Map<String ,Method> getGetMethodsMSByJava(Class<?> i_Class)
    {
        return getGetSetMethodsByJava(i_Class).get($Partition_GET);
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合中的Setter方法
     * 
     * 返回结果是按方法名称排序的。
     * 
     * @param i_Class
     * @return         Map.key    为方法的短名称
     *                 Map.value  为方法对象
     */
    public static Map<String ,Method> getSetMethodsMG(Class<?> i_Class)
    {
        return getGetSetMethods(i_Class).get($Partition_SET);
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合中的Setter方法
     * 
     * 返回结果是按方法名称排序的。
     * 
     * 方法名称排序规则升级为：按方法对应的成员属性名称在类中的编程编写的顺序排序。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-26
     * @version     v1.0
     *
     * @param i_Class
     * @return         Map.key    为方法的短名称
     *                 Map.value  为方法对象
     */
    public static Map<String ,Method> getSetMethodsMGByJava(Class<?> i_Class)
    {
        return getGetSetMethodsByJava(i_Class).get($Partition_SET);
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合
     * 
     * 返回结果是按方法名称排序的。
     * 
     * @param i_Class
     * @return         TablePartitionRID.key    只有两种值GET或SET。is开头的方法，也在GET分区中。
     *                 TablePartitionRID.index  为方法的短名称
     *                 TablePartitionRID.value  为方法对象
     */
    public static TablePartitionRID<String ,Method> getGetSetMethods(Class<?> i_Class)
    {
        TablePartitionRID<String ,Method> v_Ret     = new TablePartitionRID<String ,Method>(2);
        Method []                         v_Methods = i_Class.getMethods();
        
        Arrays.sort(v_Methods ,MethodComparator.getInstance());
        
        // 先行过滤出Getter方法(包括is开头的方法)
        for (int i=0; i<v_Methods.length; i++)
        {
            Method v_Method = v_Methods[i];
            
            if ( v_Method.getParameterTypes().length == 0 )
            {
                if ( v_Method.getName().startsWith("get") )
                {
                    v_Ret.putRow($Partition_GET ,v_Method.getName().substring(3) ,v_Method);
                }
                else if ( v_Method.getName().startsWith("is") )
                {
                    v_Ret.putRow($Partition_GET ,v_Method.getName().substring(2) ,v_Method);
                }
            }
        }
        
        // 再过滤出Setter方法。并要求参数类型与Getter方法的类型一致
        for (int i=0; i<v_Methods.length; i++)
        {
            Method v_Method = v_Methods[i];
            
            if ( v_Method.getParameterTypes().length == 1 )
            {
                if ( v_Method.getName().startsWith("set") )
                {
                    String v_ShortName = v_Method.getName().substring(3);
                    Method v_Getter    = v_Ret.getRow($Partition_GET ,v_ShortName);
                    
                    if ( v_Getter != null )
                    {
                        if ( v_Method.getParameterTypes()[0] == v_Getter.getReturnType() )
                        {
                            v_Ret.putRow($Partition_SET ,v_ShortName ,v_Method);
                        }
                    }
                }
            }
        }
        
        // 核对是否成对出现
        List<String> v_ShortNames = Help.toListKeys(v_Ret.get($Partition_GET));
        for (String v_ShortName : v_ShortNames)
        {
            if ( v_Ret.getRow($Partition_SET ,v_ShortName) == null )
            {
                v_Ret.removeRow($Partition_GET ,v_ShortName);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合
     * 
     * 返回结果是按方法名称排序的。
     * 
     * 方法名称排序规则升级为：按方法对应的成员属性名称在类中的编程编写的顺序排序。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-26
     * @version     v1.0
     *
     * @param i_Class
     * @return         TablePartitionRID.key    只有两种值GET或SET。is开头的方法，也在GET分区中。
     *                 TablePartitionRID.index  为方法的短名称
     *                 TablePartitionRID.value  为方法对象
     */
    public static TablePartitionRID<String ,Method> getGetSetMethodsByJava(Class<?> i_Class)
    {
        TablePartitionRID<String ,Method> v_Ret     = new TablePartitionRID<String ,Method>(2);
        Method []                         v_Methods = i_Class.getMethods();
        
        Arrays.sort(v_Methods ,new MethodFieldComparator(i_Class.getDeclaredFields()));
        
        // 先行过滤出Getter方法(包括is开头的方法)
        for (int i=0; i<v_Methods.length; i++)
        {
            Method v_Method = v_Methods[i];
            
            if ( v_Method.getParameterTypes().length == 0 )
            {
                if ( v_Method.getName().startsWith("get") )
                {
                    v_Ret.putRow($Partition_GET ,v_Method.getName().substring(3) ,v_Method);
                }
                else if ( v_Method.getName().startsWith("is") )
                {
                    v_Ret.putRow($Partition_GET ,v_Method.getName().substring(2) ,v_Method);
                }
            }
        }
        
        // 再过滤出Setter方法。并要求参数类型与Getter方法的类型一致
        for (int i=0; i<v_Methods.length; i++)
        {
            Method v_Method = v_Methods[i];
            
            if ( v_Method.getParameterTypes().length == 1 )
            {
                if ( v_Method.getName().startsWith("set") )
                {
                    String v_ShortName = v_Method.getName().substring(3);
                    Method v_Getter    = v_Ret.getRow($Partition_GET ,v_ShortName);
                    
                    if ( v_Getter != null )
                    {
                        if ( v_Method.getParameterTypes()[0] == v_Getter.getReturnType() )
                        {
                            v_Ret.putRow($Partition_SET ,v_ShortName ,v_Method);
                        }
                    }
                }
            }
        }
        
        // 核对是否成对出现
        List<String> v_ShortNames = Help.toListKeys(v_Ret.get($Partition_GET));
        for (String v_ShortName : v_ShortNames)
        {
            if ( v_Ret.getRow($Partition_SET ,v_ShortName) == null )
            {
                v_Ret.removeRow($Partition_GET ,v_ShortName);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取默认 Getter 方法（无入参的）
     * 
     * @param i_Class
     * @param i_GetMethodName
     * @param i_IsNorm
     * @return
     */
    public static Method getGetMethod(Class<?> i_Class ,String i_GetMethodName ,boolean i_IsNorm)
    {
        if ( i_GetMethodName == null )
        {
            return null;
        }
        
        String v_GetMethodName_Get       = i_GetMethodName.trim();
        String v_GetMethodName_Is        = i_GetMethodName.trim();
        String v_GetMethodName_Fixed_Get = null;
        String v_GetMethodName_Fixed_Is  = null;
        
        if ( i_IsNorm )
        {
            if ( v_GetMethodName_Get.startsWith($FixedMethodName) )
            {
                v_GetMethodName_Fixed_Get = "get" + v_GetMethodName_Get;
                v_GetMethodName_Fixed_Is  = "is"  + v_GetMethodName_Is;
                v_GetMethodName_Get       = v_GetMethodName_Get.substring(1);
                v_GetMethodName_Is        = v_GetMethodName_Is.substring(1);
            }
            else
            {
                v_GetMethodName_Get = "get" + StringHelp.toUpperCaseByFirst(v_GetMethodName_Get);
                v_GetMethodName_Is  = "is"  + StringHelp.toUpperCaseByFirst(v_GetMethodName_Is);
            }
        }
        
        Method [] v_Methods = i_Class.getMethods();
        
        
        for (int i=0; i<v_Methods.length; i++)
        {
            if ( v_Methods[i].getName().equalsIgnoreCase(v_GetMethodName_Get)
              || v_Methods[i].getName().equalsIgnoreCase(v_GetMethodName_Is) )
            {
                if ( v_Methods[i].getParameterTypes().length == 0 )
                {
                    return v_Methods[i];
                }
            }
        }
        
        // 当方法名称真的是get$xxx()、set$xxx()的形式，也是要尝试查询匹配一次的  2017-07-12
        if ( v_GetMethodName_Fixed_Get != null )
        {
            for (int i=0; i<v_Methods.length; i++)
            {
                if ( v_Methods[i].getName().equalsIgnoreCase(v_GetMethodName_Fixed_Get)
                  || v_Methods[i].getName().equalsIgnoreCase(v_GetMethodName_Fixed_Is) )
                {
                    if ( v_Methods[i].getParameterTypes().length == 0 )
                    {
                        return v_Methods[i];
                    }
                }
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 获取前缀相同的方法
     * 
     * 只获取"本类自己"的方法
     * 
     * @param i_Class
     * @param i_MethodPrefixName
     * @return
     */
    public static List<Method> getStartMethods(Class<?> i_Class ,String i_MethodPrefixName)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        
        for (int i=0; i<v_Methods.length; i++)
        {
            // 只获取"本类自己"的方法
            if ( !Modifier.isNative(v_Methods[i].getModifiers()) )
            {
                if ( v_Methods[i].getName().startsWith(i_MethodPrefixName) )
                {
                    v_Ret.add(v_Methods[i]);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取多个前缀相同的方法
     * 
     * 只获取"本类自己"的方法
     * 
     * @param i_Class
     * @param i_MethodPrefixNames
     * @return
     */
    public static List<Method> getStartMethods(Class<?> i_Class ,String [] i_MethodPrefixNames)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        
        for (int i=0; i<v_Methods.length; i++)
        {
            // 只获取"本类自己"的方法
            if ( !Modifier.isNative(v_Methods[i].getModifiers()) )
            {
                for (int x=0; x<i_MethodPrefixNames.length; x++)
                {
                    if ( v_Methods[i].getName().startsWith(i_MethodPrefixNames[x]) )
                    {
                        v_Ret.add(v_Methods[i]);
                        break;
                    }
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取前缀相同，入参参数匹配的方法
     * 
     * 只获取"本类自己"的方法
     * 
     * @param i_Class
     * @param i_MethodPrefixName  参数名称前缀
     * @param i_ParamSize         参数个数
     * @return
     */
    public static List<Method> getStartMethods(Class<?> i_Class ,String i_MethodPrefixName ,int i_ParamSize)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        
        
        for (int i=0; i<v_Methods.length; i++)
        {
            // 只获取"本类自己"的方法
            if ( !Modifier.isNative(v_Methods[i].getModifiers()) )
            {
                if ( v_Methods[i].getParameterTypes().length == i_ParamSize )
                {
                    if ( v_Methods[i].getName().startsWith(i_MethodPrefixName) )
                    {
                        v_Ret.add(v_Methods[i]);
                    }
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取多个前缀相同，入参参数匹配的方法
     * 
     * 只获取"本类自己"的方法
     * 
     * @param i_Class
     * @param i_MethodPrefixName  参数名称前缀
     * @param i_ParamSize         参数个数
     * @return
     */
    public static List<Method> getStartMethods(Class<?> i_Class ,String [] i_MethodPrefixNames ,int i_ParamSize)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        
        
        for (int i=0; i<v_Methods.length; i++)
        {
            // 只获取"本类自己"的方法
            if ( !Modifier.isNative(v_Methods[i].getModifiers()) )
            {
                if ( v_Methods[i].getParameterTypes().length == i_ParamSize )
                {
                    for (int x=0; x<i_MethodPrefixNames.length; x++)
                    {
                        if ( v_Methods[i].getName().startsWith(i_MethodPrefixNames[x]) )
                        {
                            v_Ret.add(v_Methods[i]);
                            break;
                        }
                    }
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取某一方法名称的所有方法对象。包括重载的多个方法
     * 
     * 不再区分大小写 2017-07-12
     * 
     * @param i_Class
     * @param i_MethodName
     * @return
     */
    public static List<Method> getMethods(Class<?> i_Class ,String i_MethodName)
    {
        return getMethodsIgnoreCase(i_Class ,i_MethodName);
    }
    
    
    
    /**
     * 获取某一方法名称的所有方法对象。包括重载的多个方法。（忽然大小的匹配）
     * 
     * @param i_Class
     * @param i_MethodName
     * @return
     */
    public static List<Method> getMethodsIgnoreCase(Class<?> i_Class ,String i_MethodName)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        
        for (int i=0; i<v_Methods.length; i++)
        {
            if ( v_Methods[i].getName().equalsIgnoreCase(i_MethodName) )
            {
                v_Ret.add(v_Methods[i]);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取指定参数个数的所有方法对象。
     * 
     * 只获取"本类自己"的方法
     * 只获取公共方法，即 Public 方法
     * 
     * @param i_Class
     * @param i_ParamSize
     * @return
     */
    public static List<Method> getMethods(Class<?> i_Class ,int i_ParamSize)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getDeclaredMethods();
        
        for (int i=0; i<v_Methods.length; i++)
        {
            Method v_Method = v_Methods[i];
            
            if ( v_Method.getParameterTypes().length == i_ParamSize )
            {
                if ( Modifier.isPublic(v_Method.getModifiers()) )
                {
                    v_Ret.add(v_Methods[i]);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取排除某些前缀的成员方法
     * 
     * 获取本类以及父类或者父接口中所有的公共方法(public修饰符修饰的)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-01-14
     * @version     v1.0
     *
     * @param i_Class              Java元类
     * @param i_StartMethodNames   不区分大小写配对
     * @return
     */
    public static List<Method> getMethodsExcludeStart(Class<?> i_Class ,String [] i_StartMethodNames)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        String []    v_Starts  = new String[i_StartMethodNames.length];
        
        if ( Help.isNull(i_StartMethodNames) )
        {
            return v_Ret;
        }
        
        // 不再区分大小写
        for (int i=0; i<i_StartMethodNames.length; i++)
        {
            v_Starts[i] = i_StartMethodNames[i].toLowerCase();
        }
        
        for (int i=0; i<v_Methods.length; i++)
        {
            String v_MName = v_Methods[i].getName().toLowerCase();
            
            if ( !StringHelp.isStartsWith(v_MName ,v_Starts) )
            {
                v_Ret.add(v_Methods[i]);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取方法入参参数上的指定类型的注解
     * 
     * 1. 方法入参有三个，其中两个参数有注解。返回集合元素为三个，未注解参数的集合元素为null。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-18
     * @version     v1.0
     *
     * @param i_Method
     * @param i_AnnotationClass
     * @return                   所有入参数均无注解时，返回null
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> List<A> getParameterAnnotations(Method i_Method ,Class<A> i_AnnotationClass)
    {
        List<A> v_Params = null;
        int     v_PCount = 0;
        
        Annotation[][] v_PAnnos = i_Method.getParameterAnnotations();
        if ( !Help.isNull(v_PAnnos) )
        {
            v_Params = new ArrayList<A>(i_Method.getParameterTypes().length);
            
            for (int xp=0; xp<v_PAnnos.length; xp++)
            {
                A v_Temp = null;
                
                for (int xpAI=0; xpAI<v_PAnnos[xp].length; xpAI++)
                {
                    if ( i_AnnotationClass == v_PAnnos[xp][xpAI].annotationType() )
                    {
                        v_Temp = (A)v_PAnnos[xp][xpAI];
                        v_PCount++;
                        break;
                    }
                }
                
                v_Params.add(v_Temp);
            }
            
            if ( v_PCount <= 0 )
            {
                v_Params = null;
            }
        }
        
        return v_Params;
    }
    
    
    
    /**
     * 获取指定参数个数的，并且为某一注解的所有方法对象
     * 
     * 只获取"本类自己"的方法
     * 只获取公共方法，即 Public 方法
     * 获取任一参数个数的方法
     * 
     * @param i_Class
     * @param i_AnnotationClass   注解类型的元类型
     * @return
     */
    public static List<Method> getAnnotationMethods(Class<?> i_Class ,Class<? extends Annotation> i_AnnotationClass)
    {
        return getAnnotationMethods(i_Class ,i_AnnotationClass ,-1);
    }
    
    
    
    /**
     * 获取指定参数个数的，并且为某一注解的所有方法对象
     * 
     * 只获取"本类自己"的方法
     * 只获取公共方法，即 Public 方法
     * 
     * @param i_Class
     * @param i_AnnotationClass   注解类型的元类型
     * @param i_ParamSize         参数个数。小于0表示：无参数个数限制
     * @return
     */
    public static List<Method> getAnnotationMethods(Class<?> i_Class ,Class<? extends Annotation> i_AnnotationClass ,int i_ParamSize)
    {
        List<Method> v_Ret = new ArrayList<Method>();
        
        try
        {
            Method [] v_Methods = i_Class.getDeclaredMethods();
            
            // 无参数个数限制
            if ( i_ParamSize < 0 )
            {
                for (int i=0; i<v_Methods.length; i++)
                {
                    Method v_Method = v_Methods[i];
                    
                    if ( Modifier.isPublic(v_Method.getModifiers()) )
                    {
                        if ( v_Method.isAnnotationPresent(i_AnnotationClass) )
                        {
                            v_Ret.add(v_Method);
                        }
                    }
                }
            }
            else
            {
                for (int i=0; i<v_Methods.length; i++)
                {
                    Method v_Method = v_Methods[i];
                    
                    if ( v_Method.getParameterTypes().length == i_ParamSize )
                    {
                        if ( Modifier.isPublic(v_Method.getModifiers()) )
                        {
                            if ( v_Method.isAnnotationPresent(i_AnnotationClass) )
                            {
                                v_Ret.add(v_Method);
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable exce)
        {
            // 2018-01-29 扫描项目所有类时，当发现某一类引用的类不存在时，只错误提示不中断服务。
            System.err.println("Error: " + i_Class.getName() + ": " + exce.getMessage() + " " + exce.getClass().getName());
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取某一方法名称的所有方法对象。包括重载的多个方法
     * 
     * 不再区分大小写 2017-07-12
     * 
     * @param i_Class
     * @param i_MethodName
     * @param i_ParamSize
     * @return
     */
    public static List<Method> getMethods(Class<?> i_Class ,String i_MethodName ,int i_ParamSize)
    {
        return getMethodsIgnoreCase(i_Class ,i_MethodName ,i_ParamSize);
    }
    
    
    
    /**
     * 获取某一方法名称的所有方法对象。包括重载的多个方法。（忽然大小的匹配）
     * 
     * @param i_Class
     * @param i_MethodName
     * @param i_ParamSize
     * @return
     */
    public static List<Method> getMethodsIgnoreCase(Class<?> i_Class ,String i_MethodName ,int i_ParamSize)
    {
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getMethods();
        
        for (int i=0; i<v_Methods.length; i++)
        {
            if ( v_Methods[i].getName().equalsIgnoreCase(i_MethodName) )
            {
                if ( v_Methods[i].getParameterTypes().length == i_ParamSize )
                {
                    v_Ret.add(v_Methods[i]);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 按方法入参类型配对最佳方法，最佳方法有多少个时，按从前到后优先级排序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-13
     * @version     v1.0
     * 
     * @param i_Class
     * @param i_MethodName
     * @param i_ParamValues  方法参数的值
     * @return
     */
    public static List<Method> getMethodsBest(Class<?> i_Class ,String i_MethodName ,Object [] i_ParamValues)
    {
        if ( Help.isNull(i_ParamValues) )
        {
            return getMethodsBest(i_Class ,i_MethodName ,null);
        }
        else
        {
            Class<?> [] v_ParamClassTypes = new Class<?>[i_ParamValues.length];
            
            for (int x=0; x<i_ParamValues.length; x++)
            {
                v_ParamClassTypes[x] = i_ParamValues[x] == null ? Object.class : i_ParamValues[x].getClass();
            }
            
            return getMethodsBest(i_Class ,i_MethodName ,v_ParamClassTypes);
        }
    }
    
    
    
    /**
     * 按方法入参类型配对最佳方法，最佳方法有多少个时，按从前到后优先级排序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-13
     * @version     v1.0
     * 
     * @param i_Class
     * @param i_MethodName
     * @param i_ParamClassTypes  方法参数值的类型
     * @return
     */
    public static List<Method> getMethodsBest(Class<?> i_Class ,String i_MethodName ,Class<?> [] i_ParamClassTypes)
    {
        List<Method> v_Methods = null;
                
        if ( Help.isNull(i_ParamClassTypes) )
        {
            v_Methods = MethodReflect.getMethodsIgnoreCase(i_Class ,i_MethodName ,0);
        }
        else
        {
            v_Methods = MethodReflect.getMethodsIgnoreCase(i_Class ,i_MethodName ,i_ParamClassTypes.length);
        }
        
        if ( v_Methods.size() <= 1 )
        {
            return v_Methods;
        }
        
        Map<Integer ,Method> v_Bests = new HashMap<Integer ,Method>();
        for (Method v_Method : v_Methods)
        {
            int         v_BestValue    = 0;
            Class<?> [] v_MPClassTypes = v_Method.getParameterTypes();
            for (int y=i_ParamClassTypes.length - 1; y>=0; y--)
            {
                if ( i_ParamClassTypes[y] == v_MPClassTypes[y] )                                       // 3级：完全配对
                {
                    v_BestValue += Math.pow(1 ,i_ParamClassTypes.length - y) * 3;
                }
                else if ( v_MPClassTypes[y] == Object.class )                                          // 1级：模糊配对
                {
                    v_BestValue += Math.pow(1 ,i_ParamClassTypes.length - y);
                }
                else if ( MethodReflect.isExtendImplement(i_ParamClassTypes[y] ,v_MPClassTypes[y]) )   // 2级：继承配对 或 接口实现配对
                {
                    v_BestValue += Math.pow(1 ,i_ParamClassTypes.length - y) * 2;
                }
            }
            
            if ( v_BestValue > 0 )
            {
                v_Bests.put(v_BestValue ,v_Method);
            }
        }
        
        if ( !Help.isNull(v_Bests) )
        {
            v_Methods.clear();
            v_Methods = Help.toList(Help.toReverse(v_Bests));
            v_Bests.clear();
            return v_Methods;
        }
        else
        {
            return new ArrayList<Method>();
        }
    }
    
    
    
    /**
     * 向数据库表插入数据时，通过Java生成主键的功能。与SQL占位符配合使用。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-18
     * @version     v1.0
     *
     * @return
     */
    public static MethodReflect getMethodReflectUUID()
    {
        MethodReflectUUID v_MRUUID = MethodReflectUUID.getInstance();
        
        try
        {
            return new MethodReflect(v_MRUUID ,v_MRUUID.getClass().getMethod("getUUID" ,new Class[]{}));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    
    
    /**
     * 从Map集合中取值。实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-29
     * @version     v1.0
     *              v2.0  2025-07-04  修正：在替换子组方法路径时，用 StringHelp.replaceFirst() 方法替换 StringHelp.replaceAll() 方法
     *                                     解决：父级名称与子组名称一样的问题，
     *                                       如：占位符 :APIRets.data.data.Automatic 中的第二级与第三级的 data 相同  
     *
     * @param i_MapValues  Map集合
     * @param i_MethodURL  方法全路径（不区分大小写）
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getMapValue(Map<String ,?> i_MapValues ,String i_MethodURL)
    {
        String [] v_MethodURLArr = i_MethodURL.split("\\.");
        if ( v_MethodURLArr.length <= 1 )
        {
            return Help.getValueIgnoreCase(i_MapValues ,i_MethodURL);
        }
        
        Object v_MapValue = Help.getValueIgnoreCase(i_MapValues ,v_MethodURLArr[0]);
        if ( v_MapValue == null )
        {
            return null;
        }
        
        String v_ChildMethodURL = StringHelp.replaceFirst(i_MethodURL ,v_MethodURLArr[0] + "." ,"");
        
        if ( MethodReflect.isExtendImplement(v_MapValue ,Map.class) )
        {
            return getMapValue((Map<String ,?>)v_MapValue ,v_ChildMethodURL);
        }
        else if ( MethodReflect.isExtendImplement(v_MapValue ,List.class) )
        {
            return getListValue((List<?>)v_MapValue ,v_ChildMethodURL);
        }
        else if ( MethodReflect.class.equals(v_MapValue.getClass()) )
        {
            return v_MapValue;
        }
        else
        {
            MethodReflect v_MethodReflect = null;
            
            try
            {
                v_MethodReflect = new MethodReflect(v_MapValue ,v_ChildMethodURL ,true ,MethodReflect.$NormType_Getter);
                Object v_Ret = v_MethodReflect.invoke();
                
                v_MethodReflect.clearDestroy();
                v_MethodReflect = null;
                
                return v_Ret;
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            return null;
        }
    }
    
    
    
    /**
     * 从List集合中取值。实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
     * 
     * 支持如下占位符
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-07-14
     * @version     v1.0
     *
     * @param i_ListValues  List集合
     * @param i_MethodURL   方法全路径（不区分大小写）
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getListValue(List<?> i_ListValues ,String i_MethodURL)
    {
        String [] v_MethodURLArr = i_MethodURL.split("\\.");
        Object    v_ListValue    = null;
        
        if ( Help.isNumber(v_MethodURLArr[0]) )
        {
            int v_Index = Integer.parseInt(v_MethodURLArr[0]);
            if ( 0 <= v_Index && v_Index < i_ListValues.size() )
            {
                v_ListValue = i_ListValues.get(v_Index);
            }
        }
        else if ( "size".equalsIgnoreCase(v_MethodURLArr[0]) )
        {
            v_ListValue = i_ListValues.size();
        }
        
        if ( v_ListValue == null )
        {
            return null;
        }
        
        String v_ChildMethodURL = StringHelp.replaceFirst(i_MethodURL ,v_MethodURLArr[0] + "." ,"");
        
        if ( MethodReflect.isExtendImplement(v_ListValue ,Map.class) )
        {
            return getMapValue((Map<String ,?>)v_ListValue ,v_ChildMethodURL);
        }
        else if ( MethodReflect.isExtendImplement(v_ListValue ,List.class) )
        {
            return getListValue((List<?>)v_ListValue ,v_ChildMethodURL);
        }
        else if ( MethodReflect.class.equals(v_ListValue.getClass()) )
        {
            return v_ListValue;
        }
        else
        {
            MethodReflect v_MethodReflect = null;
            
            try
            {
                v_MethodReflect = new MethodReflect(v_ListValue ,v_ChildMethodURL ,true ,MethodReflect.$NormType_Getter);
                Object v_Ret = v_MethodReflect.invoke();
                
                v_MethodReflect.clearDestroy();
                v_MethodReflect = null;
                
                return v_Ret;
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            return null;
        }
    }
    
    
    
    /**
     * 根据方法的入参类型，获取某一具体的方法。
     * 
     * 支持对重载方法的判定。对每参数类型都进行比较确认惟一的执行方法
     * 
     * 从org.hy.common.Execute类中提炼而出。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Instance      实例对象
     * @param i_MethodName    方法名称（区分大小写）
     * @param i_MethodParams  方法的入参
     * @return
     */
    public static Method getMethod(Object i_Instance ,String i_MethodName ,Object [] i_MethodParams)
    {
        return getMethod(i_Instance.getClass() ,i_MethodName ,i_MethodParams);
    }
    
    
    
    /**
     * 根据方法的入参类型，获取某一具体的方法。
     * 
     * 支持对重载方法的判定。对每参数类型都进行比较确认惟一的执行方法
     * 
     * 从org.hy.common.Execute类中提炼而出。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *              v2.0  2021-04-09  修正：防止i_MethodParams的某个元素为NULL的情况。
     *
     * @param i_Class         对象类型
     * @param i_MethodName    方法名称（不区分大小写）
     * @param i_MethodParams  方法的入参
     * @return
     */
    public static Method getMethod(Class<?> i_Class ,String i_MethodName ,Object [] i_MethodParams)
    {
        Method []    v_Methods   = i_Class.getMethods();
        List<Method> v_OKMethods = new ArrayList<Method>();
        Method       v_Method    = null;
        
        for (int i=0; i<v_Methods.length; i++)
        {
            v_Method = v_Methods[i];
            
            // 不再区分大小写 2017-07-12
            if ( v_Method.getName().equalsIgnoreCase(i_MethodName) )
            {
                if ( v_Method.getParameterTypes().length == i_MethodParams.length )
                {
                    boolean v_PTypeOK = true;
                    
                    // 先过滤出符合条件的方法  ZhengWei(HY) Add 2017-01-06
                    for (int v_PIndex=0; v_PIndex<i_MethodParams.length; v_PIndex++)
                    {
                        if ( i_MethodParams[v_PIndex] == null )
                        {
                            continue;
                        }
                        if ( i_MethodParams[v_PIndex].getClass() != v_Method.getParameterTypes()[v_PIndex]
                          && !MethodReflect.isExtendImplement(i_MethodParams[v_PIndex] ,v_Method.getParameterTypes()[v_PIndex]) )
                        {
                            if ( int    .class == v_Method.getParameterTypes()[v_PIndex]
                              && Integer.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( double.class == v_Method.getParameterTypes()[v_PIndex]
                                   && Double.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( float.class == v_Method.getParameterTypes()[v_PIndex]
                                   && Float.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( long.class == v_Method.getParameterTypes()[v_PIndex]
                                   && Long.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( boolean.class == v_Method.getParameterTypes()[v_PIndex]
                                   && Boolean.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( BigDecimal.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( short.class == v_Method.getParameterTypes()[v_PIndex]
                                   && Short.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( byte.class == v_Method.getParameterTypes()[v_PIndex]
                                   && Byte.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else if ( char     .class == v_Method.getParameterTypes()[v_PIndex]
                                   && Character.class == i_MethodParams[v_PIndex].getClass() )
                            {
                                // Nothing.
                            }
                            else
                            {
                                v_PTypeOK = false;
                                break;
                            }
                        }
                    }
                    
                    if ( !v_PTypeOK )
                    {
                        continue;
                    }
                    
                    v_OKMethods.add(v_Method);
                }
            }
        }
        
        // 再去除多余的方法：其参数为 Object 类型的方法  ZhengWei(HY) Add 2017-01-06
        for (int v_PIndex=0; v_PIndex<i_MethodParams.length; v_PIndex++)
        {
            if ( v_OKMethods.size() <= 1 )
            {
                break;
            }
            
            for (int i=v_OKMethods.size()-1; i>=0; i--)
            {
                v_Method = v_OKMethods.get(i);
                
                if ( v_OKMethods.size() >= 2 )
                {
                    if ( v_Method.getParameterTypes()[v_PIndex] == Object.class )
                    {
                        v_OKMethods.remove(i);
                    }
                }
                else
                {
                    break;
                }
            }
        }
        
        if ( v_OKMethods.size() == 1 )
        {
            v_Method = v_OKMethods.get(0);
            v_OKMethods.clear();
            v_OKMethods = null;
            return v_Method;
        }
        else
        {
            v_OKMethods.clear();
            v_OKMethods = null;
            return null;
        }
    }
    
    
    
    /**
     * 调用对象的Setter赋值。
     * 
     * 可将字符串表达形式的任何值，转换为真实的Java类型后，调用对象的Setter赋值。
     * 
     * 从XJava中的提炼出来。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-24
     * @version     v1.0
     *
     * @param i_SetMethod  方法对象
     * @param i_Instance   实例对象
     * @param i_Value      可以为字符串表达形式的任何值。如下情况
     *                         1. i_Value = "true"，当成员属性为Boolean类型时，将转为 true 进行赋值。
     *                         2. i_Value = "true"，当成功属性为String 类型时，将转为"true"进行赋值。
     * @param i_Replaces   须替换的字符。注：只对String、Class两类型生效。
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     */
    public static void invokeSet(Method i_SetMethod ,Object i_Instance ,Object i_Value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
    {
        invokeSet(i_SetMethod ,i_Instance ,i_Value ,null);
    }
    
    
    
    /**
     * 调用对象的Setter赋值。
     * 
     * 可将字符串表达形式的任何值，转换为真实的Java类型后，调用对象的Setter赋值。
     * 
     * 从XJava中的提炼出来。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-24
     * @version     v1.0
     *
     * @param i_SetMethod  方法对象
     * @param i_Instance   实例对象
     * @param i_Value      可以为字符串表达形式的任何值。如下情况
     *                         1. i_Value = "true"，当成员属性为Boolean类型时，将转为 true 进行赋值。
     *                         2. i_Value = "true"，当成功属性为String 类型时，将转为"true"进行赋值。
     * @param i_Replaces   须替换的字符。注：只对String、Class两类型生效。
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static void invokeSet(Method i_SetMethod ,Object i_Instance ,Object i_Value ,Map<String ,String> i_Replaces) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
    {
        Class<?> v_ParamType = i_SetMethod.getParameterTypes()[0];
        
        // 这里只对String、枚举、日期等特殊的类进行处理，其它的都是类型，而不是类
        if ( String.class == v_ParamType )
        {
            if ( i_Value != null )
            {
                i_SetMethod.invoke(i_Instance ,StringHelp.replaceAll(i_Value.toString() ,i_Replaces ,false));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(String)null);
            }
        }
        else if ( MethodReflect.isExtendImplement(v_ParamType ,Enum.class) )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                Enum<?> [] v_EnumValues = StaticReflect.getEnums((Class<? extends Enum<?>>) v_ParamType);
                String     v_Value      = i_Value.toString();
                
                // ZhengWei(HY) Add 2018-05-08  支持枚举toString()的匹配
                for (Enum<?> v_Enum : v_EnumValues)
                {
                    if ( v_Value.equalsIgnoreCase(v_Enum.toString()) )
                    {
                        i_SetMethod.invoke(i_Instance ,v_Enum);
                        return;
                    }
                }
                
                // ZhengWei(HY) Add 2017-10-31  支持枚举名称的匹配
                for (Enum<?> v_Enum : v_EnumValues)
                {
                    if ( v_Value.equalsIgnoreCase(v_Enum.name()) )
                    {
                        i_SetMethod.invoke(i_Instance ,v_Enum);
                        return;
                    }
                }
                
                // 尝试用枚举值匹配
                if ( Help.isNumber(v_Value) )
                {
                    int v_IntValue = Integer.parseInt(v_Value.trim());
                    if ( 0 <= v_IntValue && v_IntValue < v_EnumValues.length )
                    {
                        i_SetMethod.invoke(i_Instance ,v_EnumValues[v_IntValue]);
                    }
                }
            }
        }
        else if ( Date.class == v_ParamType )
        {
            // 以下每个if分支的空判定，只能写在此处，不能统一提炼，预防Java对象.toString()是空的情况。
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,new Date(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Date)null);
            }
        }
        else if ( java.util.Date.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,(new Date(i_Value.toString()).getDateObject()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(java.util.Date)null);
            }
        }
        // 添加对数据库时间的转换 Add ZhengWei(HY) 2018-05-15
        else if ( Timestamp.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,(new Date(i_Value.toString()).getSQLTimestamp()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Timestamp)null);
            }
        }
        else if ( int.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Integer.parseInt(i_Value.toString()));
            }
        }
        else if ( Integer.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Integer.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Integer)null);
            }
        }
        else if ( boolean.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Boolean.parseBoolean(i_Value.toString()));
            }
        }
        else if ( Boolean.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Boolean.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Boolean)null);
            }
        }
        else if ( double.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Double.parseDouble(i_Value.toString()));
            }
        }
        else if ( Double.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Double.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Double)null);
            }
        }
        else if ( float.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Float.parseFloat(i_Value.toString()));
            }
        }
        else if ( Float.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Float.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Float)null);
            }
        }
        else if ( long.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Long.parseLong(i_Value.toString()));
            }
        }
        else if ( Long.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Long.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Long)null);
            }
        }
        else if ( BigDecimal.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,new BigDecimal(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(BigDecimal)null);
            }
        }
        else if ( short.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Short.parseShort(i_Value.toString()));
            }
        }
        else if ( Short.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Short.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Short)null);
            }
        }
        else if ( byte.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Byte.parseByte(i_Value.toString()));
            }
        }
        else if ( Byte.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Byte.valueOf(i_Value.toString()));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Byte)null);
            }
        }
        else if ( char.class == v_ParamType )
        {
            // 此不要加 .trim() 方法
            if ( i_Value != null && i_Value.toString() != null && !"".equals(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,i_Value.toString().charAt(0));
            }
        }
        else if ( Character.class == v_ParamType )
        {
            // 此不要加 .trim() 方法
            if ( i_Value != null && i_Value.toString() != null && !"".equals(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,i_Value.toString().charAt(0));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Character)null);
            }
        }
        else if ( Class.class == v_ParamType )
        {
            if ( i_Value != null && !Help.isNull(i_Value.toString()) )
            {
                i_SetMethod.invoke(i_Instance ,Help.forName(StringHelp.replaceAll(i_Value.toString().trim() ,i_Replaces ,false)));
            }
            else
            {
                i_SetMethod.invoke(i_Instance ,(Class<?>)null);
            }
        }
        else
        {
            i_SetMethod.invoke(i_Instance ,i_Value);
        }
    }
    
    
    
    public void clear()
    {
        if ( this.classes != null )
        {
            this.classes.clear();
        }
        
        if ( this.instances != null )
        {
            this.instances.clear();
        }
        
        if ( this.methodsParams != null )
        {
            for (List<String> v_Item : methodsParams)
            {
                if ( v_Item != null )
                {
                    v_Item.clear();
                }
            }
            
            this.methodsParams.clear();
        }
        
        if ( this.methods != null )
        {
            for (List<MethodInfo> v_Item : this.methods)
            {
                if ( v_Item != null )
                {
                    v_Item.clear();
                }
            }
            
            this.methods.clear();
        }
        
        if ( !Help.isNull(this.methodNames) )
        {
            for (int x=0; x<this.methodNames.length; x++)
            {
                this.methodNames[x] = null;
            }
        }
    }
    
    
    
    public void clearDestroy()
    {
        if ( this.classes != null )
        {
            this.classes.clear();
            this.classes = null;
        }
        
        if ( this.instances != null )
        {
            this.instances.clear();
            this.instances = null;
        }
        
        if ( this.methodsParams != null )
        {
            for (List<String> v_Item : methodsParams)
            {
                if ( v_Item != null )
                {
                    v_Item.clear();
                    v_Item = null;
                }
            }
            
            this.methodsParams.clear();
            this.methodsParams = null;
        }
        
        if ( this.methods != null )
        {
            for (List<MethodInfo> v_Item : this.methods)
            {
                if ( v_Item != null )
                {
                    v_Item.clear();
                    v_Item = null;
                }
            }
            
            this.methods.clear();
            this.methods = null;
        }
        
        if ( Help.isNull(this.methodNames) )
        {
            for (int x=0; x<this.methodNames.length; x++)
            {
                this.methodNames[x] = null;
            }
            
            this.methodNames = null;
        }
    }
    
    
    
    /**
     * 方法的反射
     * 
     * 可实现xxx.yyy.www(或getXxx.getYyy.setWww)全路径的解释
     * 可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-03-17
     * @version     v1.0
     *              v2.0  2017-06-15  添加：i_MethodURL方法全路径中的每个方法名称，不再区分大小写
     * 
     * @param i_Class       最上层的对象类型
     * @param i_MethodURL   方法全路径
     * @param i_IsNorm      方法全路径是否符合规范
     * @param i_NormType    规范类型
     * 
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public MethodReflect(Class<?> i_Class ,String i_MethodURL ,boolean i_IsNorm ,int i_NormType) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        this.classes       = new ArrayList<Class<?>>();
        this.instances     = null;
        this.methods       = new ArrayList<List<MethodInfo>>();
        this.methodsParams = new ArrayList<List<String>>();
        this.methodURL     = i_MethodURL.trim();
        this.methodNames   = this.methodURL.replace("." ,"@").split("@");
        this.isNorm        = i_IsNorm;
        this.normType      = i_NormType;
        
        this.classes.add(i_Class);
        
        this.parser();
    }
    
    
    
    /**
     * 方法的反射
     * 
     * 可实现xxx.yyy.www(或getXxx.getYyy.setWww)全路径的解释
     * 可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-03-17
     * @version     v1.0
     *              v2.0  2017-06-15  添加：i_MethodURL方法全路径中的每个方法名称，不再区分大小写
     * 
     * @param i_Class       最上层的对象类型
     * @param i_MethodURL   方法全路径
     * @param i_NormType    规范类型
     * 
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public MethodReflect(Class<?> i_Class ,String i_MethodURL ,int i_NormType) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        this(i_Class ,i_MethodURL ,false ,i_NormType);
    }
    
    
    
    /**
     * 方法的反射
     * 
     * 可实现xxx.yyy.www(或getXxx.getYyy.setWww)全路径的解释
     * 可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
     * 
     * v2.0  2017-06-15  添加：i_MethodURL方法全路径中的每个方法名称，不再区分大小写
     * 
     * @param i_Instance    最上层的实例对象实例
     * @param i_MethodURL   方法全路径
     * @param i_IsNorm      方法全路径是否符合规范
     * @param i_NormType    规范类型
     * 
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public MethodReflect(Object i_Instance ,String i_MethodURL ,boolean i_IsNorm ,int i_NormType) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        this.classes       = new ArrayList<Class<?>>();
        this.instances     = new ArrayList<Object>();
        this.methods       = new ArrayList<List<MethodInfo>>();
        this.methodsParams = new ArrayList<List<String>>();
        this.methodURL     = i_MethodURL.trim();
        this.methodNames   = this.methodURL.replace("." ,"@").split("@");
        this.isNorm        = i_IsNorm;
        this.normType      = i_NormType;
        
        this.instances.add(i_Instance);
        
        this.parser();
    }
    
    
    
    /**
     * 方法的反射
     * 
     * 可实现getXxx.getYyy.setWww全路径的解释
     * 可实现getXxx.getYyy.getWww全路径的解释
     * 
     * v2.0  2017-06-15  添加：i_MethodURL方法全路径中的每个方法名称，不再区分大小写
     * 
     * @param i_Instance    最上层的实例对象实例
     * @param i_MethodURL   方法全路径
     * @param i_NormType    规范类型
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public MethodReflect(Object i_Instance ,String i_MethodURL ,int i_NormType) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        this(i_Instance ,i_MethodURL ,false ,i_NormType);
    }
    
    
    
    /**
     * 无须反射，只许简单的执行方法就成。
     * 
     * 应用于：外界已明确了待执行的方法（通常是getter方法）。
     * 
     * 已用在：DBSQL.getSQL(Map)方法。
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-03-18
     * @version     v1.0
     *
     * @param i_Instance
     * @param i_Method
     */
    public MethodReflect(Object i_Instance ,Method i_Method)
    {
        this.instances     = new ArrayList<Object>();
        this.methods       = new ArrayList<List<MethodInfo>>();
        this.methodsParams = new ArrayList<List<String>>();
        this.methodURL     = null;
        this.methodNames   = null;
        this.isNorm        = false;
        this.normType      = $NormType_Getter;
        
        this.instances.add(i_Instance);
        this.methods.add(new ArrayList<MethodInfo>());
        this.methods.get(0).add(new MethodInfo(i_Method));
        this.methodsParams.add(new ArrayList<String>());
    }
    
    
    
    /**
     * 解释方法全路径
     * 
     * 2017-06-15 添加：方法名称不再区分大小写
     * 2018-05-08 修改：当子类实现父类接口时，方法重载时，可能出现方法名称相同的两个getter方法。
     *                 方法按修饰符排序后取首个方法，不再向外界抛错。
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private void parser() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        int v_Index = 0;
        
        
        // 解释出每一层次上的方法的入参参数
        for ( ; v_Index<this.methodNames.length; v_Index++)
        {
            String [] v_Params = parserMethodParams(this.methodNames[v_Index]);
            
            if ( v_Params != null && v_Params.length >= 1 )
            {
                this.methodNames[v_Index] = parserMethodName(this.methodNames[v_Index]);
                
                List<String> v_ParamList = new ArrayList<String>();
                for (int x=0; x<v_Params.length; x++)
                {
                    if ( !Help.isNull(v_Params[x]) )
                    {
                        v_ParamList.add(v_Params[x].trim());
                    }
                }
                
                this.methodsParams.add(v_ParamList);
            }
            else
            {
                this.methodsParams.add(new ArrayList<String>());
            }
        }
        
        
        if ( this.isNorm )
        {
            // 最后方法名之前的所有方法，都自动采用Getter形式的补全方法
            for (v_Index = 0; v_Index<this.methodNames.length - 1; v_Index++)
            {
                if ( this.methodNames[v_Index].startsWith($FixedMethodName) )
                {
                    this.methodNames[v_Index] = this.methodNames[v_Index].substring(1);
                }
                else
                {
                    this.methodNames[v_Index] = "get" + StringHelp.toUpperCaseByFirst(this.methodNames[v_Index]);
                }
            }
            
            // 最后一个方法，有可能是Setter方法。
            if ( this.methodNames[v_Index].startsWith($FixedMethodName) )
            {
                this.methodNames[v_Index] = this.methodNames[v_Index].substring(1);
            }
            else
            {
                if ( this.normType == $NormType_Setter )
                {
                    this.methodNames[v_Index] = "set" + StringHelp.toUpperCaseByFirst(this.methodNames[v_Index]);
                }
                else
                {
                    this.methodNames[v_Index] = "get" + StringHelp.toUpperCaseByFirst(this.methodNames[v_Index]);
                }
            }
        }
        
        
        boolean v_IsClass = !Help.isNull(this.classes);
        
        if ( v_IsClass )
        {
            for (v_Index = 0; v_Index<this.methodNames.length - 1; v_Index++)
            {
                Class<?>     v_Class   = this.classes.get(v_Index);
                List<Method> v_Methods = getMethodsIgnoreCase(v_Class ,this.methodNames[v_Index] ,this.methodsParams.get(v_Index).size());
                
                if ( Help.isNull(v_Methods) )
                {
                    if ( MethodReflect.isExtendImplement(v_Class ,Map.class) )
                    {
                        String v_MapGetValueName = this.methodNames[v_Index];
                        if ( v_MapGetValueName.startsWith("get") )
                        {
                            v_MapGetValueName = v_MapGetValueName.substring(3);
                        }
                        
                        this.methodsParams.set(v_Index ,new ArrayList<String>());
                        this.methodsParams.get(v_Index).add(v_MapGetValueName);
                        v_Methods = MethodReflect.getMethods(Map.class ,"get");
                    }
                    else if ( MethodReflect.isExtendImplement(v_Class ,List.class) )
                    {
                        String v_MapGetValueName = this.methodNames[v_Index];
                        if ( v_MapGetValueName.startsWith("get") )
                        {
                            v_MapGetValueName = v_MapGetValueName.substring(3);
                        }
                        
                        if ( Help.isNumber(v_MapGetValueName) )
                        {
                            List<?> v_ListValues = (List<?>) this.instances.get(v_Index);
                            int v_ListIndex = Integer.parseInt(v_MapGetValueName);
                            if ( 0 <= v_ListIndex && v_ListIndex < v_ListValues.size() )
                            {
                                this.methodsParams.set(v_Index ,new ArrayList<String>());
                                this.methodsParams.get(v_Index).add(v_MapGetValueName);
                                v_Methods = MethodReflect.getMethods(List.class ,"get");
                            }
                        }
                        else if ( "size".equalsIgnoreCase(v_MapGetValueName) )
                        {
                            this.methodsParams.set(v_Index ,new ArrayList<String>());
                            v_Methods = MethodReflect.getMethods(List.class ,"size");
                        }
                    }
                    else
                    {
                        throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
                    }
                }
                else if ( v_Methods.size() >= 2 )
                {
                    throw new RuntimeException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is more same Method name.");
                }
                
                if ( this.methodsParams.get(v_Index).size() <= 0 )
                {
                    this.methods.add(MethodInfo.toMethods(v_Methods));
                    this.classes.add(v_Methods.get(0).getReturnType());
                }
                else
                {
                    // 当方法的返回值类型为List集合时，进行二次解释  2017-03-22
                    if ( v_Index >= 1 && MethodReflect.isExtendImplement(this.methods.get(v_Index-1).get(0).toMethod().getReturnType() ,List.class) )
                    {
                        GenericsReturn v_GenericsReturn = MethodReflect.getGenericsReturn(this.methods.get(v_Index-1).get(0).toMethod());
                        
                        if ( v_GenericsReturn.getGenericType() != null )
                        {
                            this.methods.add(MethodInfo.toMethods(v_Methods));
                            this.classes.add(v_GenericsReturn.getGenericType());
                        }
                    }
                }
            }
        }
        else
        {
            this.classes.add(this.instances.get(0).getClass());
            
            for (v_Index = 0; v_Index<this.methodNames.length - 1; v_Index++)
            {
                Class<?>     v_Class         = this.instances.get(v_Index).getClass();
                List<Method> v_Methods       = getMethodsIgnoreCase(v_Class ,this.methodNames[v_Index] ,this.methodsParams.get(v_Index).size());
                Object       v_ChildInstance = null;
                
                if ( Help.isNull(v_Methods) )
                {
                    if ( MethodReflect.isExtendImplement(v_Class ,Map.class) )
                    {
                        String v_MapGetValueName = this.methodNames[v_Index];
                        if ( v_MapGetValueName.startsWith("get") )
                        {
                            v_MapGetValueName = v_MapGetValueName.substring(3);
                        }
                        
                        this.methodsParams.set(v_Index ,new ArrayList<String>());
                        this.methodsParams.get(v_Index).add(v_MapGetValueName);
                        v_Methods = MethodReflect.getMethods(Map.class ,"get");
                        v_ChildInstance = Help.getValueIgnoreCase((Map<? ,Object>) this.instances.get(v_Index) ,v_MapGetValueName);
                    }
                    else if ( MethodReflect.isExtendImplement(v_Class ,List.class) )
                    {
                        String v_MapGetValueName = this.methodNames[v_Index];
                        if ( v_MapGetValueName.startsWith("get") )
                        {
                            v_MapGetValueName = v_MapGetValueName.substring(3);
                        }
                        
                        if ( Help.isNumber(v_MapGetValueName) )
                        {
                            List<?> v_ListValues = (List<?>) this.instances.get(v_Index);
                            int v_ListIndex = Integer.parseInt(v_MapGetValueName);
                            if ( 0 <= v_ListIndex && v_ListIndex < v_ListValues.size() )
                            {
                                this.methodsParams.set(v_Index ,new ArrayList<String>());
                                this.methodsParams.get(v_Index).add(v_MapGetValueName);
                                v_Methods = MethodReflect.getMethods(List.class ,"get");
                                v_ChildInstance = v_ListValues.get(v_ListIndex);
                            }
                        }
                        else if ( "size".equalsIgnoreCase(v_MapGetValueName) )
                        {
                            List<?> v_ListValues = (List<?>) this.instances.get(v_Index);
                            this.methodsParams.set(v_Index ,new ArrayList<String>());
                            v_Methods = MethodReflect.getMethods(List.class ,"size");
                            v_ChildInstance = v_ListValues.size();
                        }
                    }
                    else
                    {
                        throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
                    }
                }
                else if ( v_Methods.size() >= 2 )
                {
                    throw new RuntimeException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is more same Method name.");
                }
                
                if ( v_ChildInstance == null )
                {
                    if ( this.methodsParams.get(v_Index).size() <= 0 )
                    {
                        v_ChildInstance = v_Methods.get(0).invoke(this.instances.get(this.instances.size() - 1));
                    }
                    else
                    {
                        Object   [] v_ParamObjs  = new Object[this.methodsParams.get(v_Index).size()];
                        Class<?> [] v_ParamClass = v_Methods.get(0).getParameterTypes();
                        for (int x=0; x<v_ParamClass.length; x++)
                        {
                            v_ParamObjs[x] = Help.toObject(v_ParamClass[x] ,this.methodsParams.get(v_Index).get(x));
                        }
                        
                        v_ChildInstance = v_Methods.get(0).invoke(this.instances.get(this.instances.size() - 1) ,v_ParamObjs);
                    }
                }
                
                this.methods.add(MethodInfo.toMethods(v_Methods));
                if ( v_ChildInstance == null )
                {
                    return;
                }
                
                this.instances.add(v_ChildInstance);
                this.classes  .add(v_ChildInstance.getClass());
            }
        }
        
        
        // 解释方法名称的全路径上的最后一个
        Class<?> v_LastClass = null;
        if ( v_IsClass )
        {
            v_LastClass = this.classes.get(v_Index);
        }
        else
        {
            v_LastClass = this.instances.get(v_Index).getClass();
        }
        
        
        if ( this.normType == $NormType_Setter )
        {
            List<Method> v_Methods = getMethodsIgnoreCase(v_LastClass ,this.methodNames[v_Index] ,1);
            
            if ( !Help.isNull(v_Methods) )
            {
                this.methods.add(MethodInfo.toMethods(v_Methods));
                return;
            }
            else
            {
                throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
            }
        }
        else
        {
            List<Method> v_Methods = getMethodsIgnoreCase(v_LastClass ,this.methodNames[v_Index] ,this.methodsParams.get(v_Index).size());
            
            if ( Help.isNull(v_Methods) )
            {
                // 最后一个Getter方法支持isXXX()方法，支持逻辑方法  2018-03-11
                if ( this.methodNames[v_Index].startsWith("get") && this.methodNames[v_Index].length() >= 4 )
                {
                    v_Methods = getMethodsIgnoreCase(v_LastClass ,"is" + this.methodNames[v_Index].substring(3) ,this.methodsParams.get(v_Index).size());
                }
                
                if ( Help.isNull(v_Methods) )
                {
                    throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
                }
            }
            
            if ( v_Methods.size() >= 2 )
            {
                // 当子类实现父类接口时，方法重载时，可能出现方法名称相同的两个getter方法
                // 这里按修饰符降序后，尝试性只保留首个元素，不再将再抛错。 ZhengWei(HY) Add 2018-05-08
                // throw new RuntimeException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is more same Method name.");
                /**
                 * 以下为Method.getModifiers()的基本数值。
                    PUBLIC:        1（二进制  0000 0001）
                    PRIVATE:       2（二进制  0000 0010）
                    PROTECTED:     4（二进制  0000 0100）
                    STATIC:        8（二进制  0000 1000）
                    FINAL:        16（二进制  0001 0000）
                    SYNCHRONIZED: 32（二进制  0010 0000）
                    VOLATILE:     64（二进制  0100 0000）
                    TRANSIENT:   128（二进制  1000 0000）
                    NATIVE:      256（二进制  0001 0000 0000）
                    INTERFACE:   512（二进制  0010 0000 0000）
                    ABSTRACT:   1024（二进制  0100 0000 0000）
                    STRICT:     2048（二进制  1000 0000 0000）
                 */
                Help.toSort(v_Methods ,"modifiers");
                for (int i=v_Methods.size()-1; i>=1; i--)
                {
                    v_Methods.remove(i);
                }
                this.methods.add(MethodInfo.toMethods(v_Methods));
            }
            else
            {
                this.methods.add(MethodInfo.toMethods(v_Methods));
            }
        }
    }
    
    
    
    /**
     * 方法全路径的赋值(Setter)
     * 
     * 支持 Setter 方法的多个重载
     * 
     * @param i_ParamValue  方法的入参参数
     * 
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void invoke(Object i_ParamValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        int v_Index = this.instances.size() - 1;
        
        if ( v_Index >= this.methods.size() )
        {
            return;
        }
        
        if ( this.normType == $NormType_Setter )
        {
            if ( !Help.isNull(this.methods) )
            {
                List<MethodInfo> v_Methods = this.methods.get(v_Index);
                for (int i=0; i<v_Methods.size(); i++)
                {
                    if ( isExtendImplement(i_ParamValue ,v_Methods.get(i).getParameterTypes()[0]) )
                    {
                        v_Methods.get(i).toMethod().invoke(this.instances.get(v_Index) ,i_ParamValue);
                        return;
                    }
                }
            }
        }
    }
    
    
    
    /**
     * 方法全路径的返回值类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @return
     */
    public Class<?> getReturnType()
    {
        if ( this.normType == $NormType_Setter )
        {
            return null;
        }
        
        boolean v_IsClass = !Help.isNull(this.classes);
        int     v_Index   = 0;
        
        if ( v_IsClass )
        {
            v_Index = this.classes.size() - 1;
            
            return this.methods.get(v_Index).get(0).toMethod().getReturnType();
        }
        else
        {
            v_Index = this.instances.size() - 1;
            
            return this.methods.get(v_Index).get(0).toMethod().getReturnType();
        }
    }
    
    
    
    /**
     * 方法全路径的返回值的方法对象(最后一个方法)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-03-27
     * @version     v1.0
     *
     * @return
     */
    public Method getReturnMethod()
    {
        if ( this.normType == $NormType_Setter )
        {
            return null;
        }
        
        boolean v_IsClass = !Help.isNull(this.classes);
        int     v_Index   = 0;
        
        if ( v_IsClass )
        {
            v_Index = this.classes.size() - 1;
            
            return this.methods.get(v_Index).get(0).toMethod();
        }
        else
        {
            v_Index = this.instances.size() - 1;
            
            return this.methods.get(v_Index).get(0).toMethod();
        }
    }
    
    
    
    /**
     * 方法全路径的获取值(Getter)
     * 
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object invoke() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if ( this.normType == $NormType_Setter )
        {
            return null;
        }
        else
        {
            int v_Index = this.instances.size() - 1;
            if ( v_Index >= this.methods.size() )
            {
                return null;
            }
            
            Method v_Method = this.methods.get(v_Index).get(0).toMethod();
            
            if ( this.methodsParams.get(v_Index).size() <= 0 )
            {
                return v_Method.invoke(this.instances.get(v_Index));
            }
            else
            {
                Object   [] v_ParamObjs  = new Object[this.methodsParams.get(v_Index).size()];
                Class<?> [] v_ParamClass = v_Method.getParameterTypes();
                for (int x=0; x<v_ParamClass.length; x++)
                {
                    v_ParamObjs[x] = Help.toObject(v_ParamClass[x] ,this.methodsParams.get(v_Index).get(x));
                }
                
                return v_Method.invoke(this.instances.get(v_Index) ,v_ParamObjs);
            }
        }
    }
    
    
    
    /**
     * 方法全路径的获取值(Getter)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-03-17
     * @version     v1.0
     *
     * @param i_Instance  实例对象
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object invokeForInstance(Object i_Instance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if ( this.normType == $NormType_Setter )
        {
            return null;
        }
        else
        {
            Object v_Instance = i_Instance;
            for (int v_Index=0; v_Index<this.methods.size(); v_Index++)
            {
                Method v_Method = this.methods.get(v_Index).get(0).toMethod();
                
                if ( v_Instance == null ){ break; }
                
                if ( this.methodsParams.get(v_Index).size() <= 0 )
                {
                    v_Instance = v_Method.invoke(v_Instance);
                }
                else
                {
                    Object   [] v_ParamObjs  = new Object[this.methodsParams.get(v_Index).size()];
                    Class<?> [] v_ParamClass = v_Method.getParameterTypes();
                    for (int x=0; x<v_ParamClass.length; x++)
                    {
                        v_ParamObjs[x] = Help.toObject(v_ParamClass[x] ,this.methodsParams.get(v_Index).get(x));
                    }
                    
                    v_Instance = v_Method.invoke(v_Instance ,v_ParamObjs);
                }
            }
            
            return v_Instance;
        }
    }
    
    
    
    /**
     * 方法全路径的获取值(Getter)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-03-22
     * @version     v1.0
     *
     * @param i_Instance  实例对象
     * @param i_Params    执行参数。
     *                    Map.key   为 xxx.yyy(Index).www 格式中的 Index 参数名称
     *                    Map.value 为参数的值
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object invokeForInstance(Object i_Instance ,Map<String ,Object> i_Params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if ( this.normType == $NormType_Setter )
        {
            return null;
        }
        else
        {
            Object v_Instance = i_Instance;
            for (int v_Index=0; v_Index<this.methods.size(); v_Index++)
            {
                Method v_Method = this.methods.get(v_Index).get(0).toMethod();
                
                if ( v_Instance == null ){ break; }
                
                if ( this.methodsParams.get(v_Index).size() <= 0 )
                {
                    v_Instance = v_Method.invoke(v_Instance);
                }
                else
                {
                    int       v_ParamSize = this.methodsParams.get(v_Index).size();
                    Object [] v_ParamObjs = new Object[v_ParamSize];
                    for (int x=0; x<v_ParamSize; x++)
                    {
                        v_ParamObjs[x] = i_Params.get(this.methodsParams.get(v_Index).get(x));
                    }
                    
                    v_Instance = v_Method.invoke(v_Instance ,v_ParamObjs);
                }
            }
            
            return v_Instance;
        }
    }
    
    
    
    /**
     * 方法全路径的填充值(Setter)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-05-08
     * @version     v1.0
     *
     * @param i_Instance  实例对象
     * @param i_Value     Setter方法的入参数值
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public void invokeSetForInstance(Object i_Instance ,Object i_Value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException
    {
        if ( this.normType == $NormType_Getter )
        {
            return;
        }
        else
        {
            Object v_Instance    = i_Instance;
            Object v_InstanceOld = null;
            int    v_Index       = 0;
                    
            for (; v_Index<this.methods.size()-1; v_Index++)
            {
                Method v_Method = this.methods.get(v_Index).get(0).toMethod();
                
                if ( v_Instance == null ){ break; }
                
                v_InstanceOld = v_Instance;
                        
                if ( this.methodsParams.get(v_Index).size() <= 0 )
                {
                    v_Instance = v_Method.invoke(v_Instance);
                }
                else
                {
                    Object   [] v_ParamObjs  = new Object[this.methodsParams.get(v_Index).size()];
                    Class<?> [] v_ParamClass = v_Method.getParameterTypes();
                    for (int x=0; x<v_ParamClass.length; x++)
                    {
                        v_ParamObjs[x] = Help.toObject(v_ParamClass[x] ,this.methodsParams.get(v_Index).get(x));
                    }
                    
                    v_Instance = v_Method.invoke(v_Instance ,v_ParamObjs);
                }
                
                if ( null == v_Instance )
                {
                    String v_MethodName = v_Method.getName();
                    
                    if ( v_MethodName.startsWith("is") )
                    {
                        v_MethodName = v_MethodName.substring(2);
                    }
                    else if ( v_MethodName.startsWith("get") )
                    {
                        v_MethodName = v_MethodName.substring(3);
                    }
                    
                    Method v_SetMethod = MethodReflect.getSetMethod(v_InstanceOld.getClass() ,v_MethodName ,true);
                    
                    if ( null != v_SetMethod )
                    {
                        v_Instance = v_Method.getReturnType().getDeclaredConstructor().newInstance();
                        v_SetMethod.invoke(v_InstanceOld ,v_Instance);
                    }
                    else
                    {
                        throw new NullPointerException("Method[" + v_Method.getName() + "] return is null.");
                    }
                }
            }
            
            if ( v_Instance == null ){ return; }
            
            Method v_Method = this.methods.get(v_Index).get(0).toMethod();
            
            if ( MethodReflect.isExtendImplement(v_Method.getParameterTypes()[0] ,i_Value.getClass()) )
            {
                v_Method.invoke(v_Instance ,i_Value);
            }
            else
            {
                v_Method.invoke(v_Instance ,Help.toObject(v_Method.getParameterTypes()[0] ,i_Value.toString()));
            }
        }
    }
    
    
    
    public String getMethodURL()
    {
        return methodURL;
    }



    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable
    {
        this.instances.clear();
        this.methods.clear();
        
        super.finalize();
    }
    */
    
}





/**
 * 向数据库表插入数据时，通过Java生成主键的功能。与SQL占位符配合使用。
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-03-18
 * @version     v1.0
 */
class MethodReflectUUID
{
    
    private static MethodReflectUUID $MRUUID;
    
    
    
    public synchronized static MethodReflectUUID getInstance()
    {
        if ( $MRUUID == null )
        {
            $MRUUID = new MethodReflectUUID();
        }
        return $MRUUID;
    }
    
    
    
    private MethodReflectUUID()
    {
        
    }
    
    
    
    /**
     * 获取UUID。全部大写，并且去除"-"字符的东东
     * 
     * @return
     */
    public String getUUID()
    {
        return StringHelp.getUUID();
    }
    
}
