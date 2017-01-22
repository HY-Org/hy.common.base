package org.hy.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





/**
 * 方法的反射。
 * 
 * 1. 可实现xxx.yyy.www(或getXxx.getYyy.setWww)全路径的解释
 * 2. 可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
 * 3. 可实现xxx(p1).yyy(p1 ,p2).www 方法带参数的全路径的解释
 * 4. 无须反射，只许简单的执行方法就成。应用于：外界已明确了待执行的方法（通常是getter方法）。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2012-09-21
 * @version     v2.0  2014-07-11  可实现xxx(p1).yyy(p1 ,p2).www 方法带参数的全路径的解释。
 *              v3.0  2016-03-18  添加新的构造器。无须反射，只许简单的执行方法就成。应用于：外界已明确了待执行的方法（通常是getter方法）。
 *                                添加：向数据库表插入数据时，通过Java生成主键的功能。与SQL占位符配合使用。
 *              v4.0  2016-07-30  添加：getMapValue()方法，从Map集合中取值。实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
 *              v4.1  2017-01-21  修正：isExtendImplement()方法对接口继承的接口与要作判定。
 */
public class MethodReflect
{
    
    /**
     * 正则表达式对：方法名称的识别
     * 如：add(row)
     */
    private final static String $REGEX_METHOD         = "\\w+[\\(]";
    
    /**
     * 正则表达式对：方法填写有效性的验证。
     * 如：xxx(p1 ,p2 ,... pn)
     * 如：xxx(o1.p1 ,o2.p1 ,... on.pn)
     */
    private final static String $REGEX_METHOD_VERIFY  = "^\\w+\\( *((\\w+\\.\\w+ *, *)|(\\w+ *, *))*((\\w+\\.\\w+)|(\\w+)) *\\)$";
    
    
    
	/** Setter规范的方法 */
	public final static int $NormType_Setter = 1;
	
	/** Getter规范的方法 */
	public final static int $NormType_Getter = -1;
	
	
	
	/** 
	 * i_MethodURL整体是否为规范的setter或getter方法
	 * 
	 * 即，当 isNorm = true  时，i_MethodURL = xxx.yyy.www 
	 * 相等于 isNorm = false 时，i_MethodURL = getXxx.getYyy.setWww
	 **/
	private boolean            isNorm;
	
	/** 
	 *  规范的类型
	 *  
	 *  1. normType =  1 为 setter 方法，如：getXxx.getYyy.setWww
	 *  2. normType = -1 为 getter 方法，如：getXxx.getYyy.getWww
	 **/
	private int                normType;
	
	/** 方法全路径的Mehod返回对象实例的集合。第一元素为构造器的第一个参数值 */
	private List<Object>       instances;
	
	/** 
	 * 方法全路径的Mehod的集合 
	 * 外层List对应：方法全路径上.第几层次上方法
	 * 内层List对应：具体层次上的多个相同重载的方法
	 */
	private List<List<Method>> methods;
	
	/**
	 * 方法全路径的Mehod的集合的参数集合
	 * 外层List对应：方法全路径上.第几层次上方法
	 * 内层List对应：具体层次上的方法的多个入参参数
	 */
	private List<List<String>> methodsParams;
	
	/** 方法全路径的分段数组 */
	private String []          methodNames;
	
	/** 方法名称的全路径 */
	private String             methodURL;
	
	
	
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
                throw new NoSuchMethodError("Method name[" + i_Text + "] is not exist.");
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
                throw new NoSuchMethodError("Method name[" + i_Text + "] is not exist.");
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
        
        Class<?> [] v_Interfaces = i_ObjectClass.getInterfaces();
        
        for (int i=0; i<v_Interfaces.length; i++)
        {
            // 判断某个类是否实现了i_InterfaceClass接口
            if ( v_Interfaces[i] == i_InterfaceClass )
            {
                return true;
            }
            
            // 递归判断某个类实现的接口中是否继承了i_InterfaceClass接口
            if ( isExtendImplement(v_Interfaces[i].getSuperclass() ,i_InterfaceClass) )
            {
                return true;
            }
            
            // 判断接口类的继承的接口  ZhengWei(HY) Add 2017-01-21
            Class<?> [] v_Interface_Interfaces = v_Interfaces[i].getInterfaces();
            for (int x=0; x<v_Interface_Interfaces.length; x++)
            {
                if ( isExtendImplement(v_Interface_Interfaces[x] ,i_InterfaceClass) )
                {
                    return true;
                }
            }
        }
        
        // 判断某个类是否继承了i_InterfaceClass类
        if ( i_ObjectClass.getSuperclass() == i_InterfaceClass )
        {
            return true;
        }
        else if ( i_ObjectClass.getSuperclass() == Object.class )
        {
            return false;
        }
        else
        {
            // 递归判断某个类的父类是否实现了i_InterfaceClass接口
            // 递归判断某个类的父类是否继承了i_InterfaceClass类
            return isExtendImplement(i_ObjectClass.getSuperclass() ,i_InterfaceClass);
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
    
    
    
    public static Class<?> getGenerics(ArrayList<?> i_List)
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
		String v_SetMethodName = i_SetMethodName.trim();
		
		if ( i_IsNorm )
		{
			v_SetMethodName = "set" + StringHelp.toUpperCaseByFirst(v_SetMethodName);
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
        List<Method> v_Ret           = new ArrayList<Method>();
        String       v_SetMethodName = i_SetMethodName.trim();
        
        if ( i_IsNorm )
        {
            v_SetMethodName = "set" + StringHelp.toUpperCaseByFirst(v_SetMethodName);
        }
        
        Method [] v_Methods = i_Class.getMethods();
        
        
        for (int i=0; i<v_Methods.length; i++)
        {
            if ( v_Methods[i].getName().equals(v_SetMethodName) )
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
        return getGetSetMethods(i_Class).get("GET");
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
        return getGetSetMethods(i_Class).get("SET");
    }
    
    
    
    /**
     * 获取成对出现的 Getter、Setter 方法集合
     * 
     * 返回结果是按方法名称排序的。
     * 
     * @param i_Class
     * @return         TablePartitionRID.key    只有两种值GET或SET
     *                 TablePartitionRID.index  为方法的短名称
     *                 TablePartitionRID.value  为方法对象
     */
    public static TablePartitionRID<String ,Method> getGetSetMethods(Class<?> i_Class)
    {
        TablePartitionRID<String ,Method> v_Ret     = new TablePartitionRID<String ,Method>(2);
        Method []                         v_Methods = i_Class.getMethods();
        final String                      v_GET     = "GET";
        final String                      v_SET     = "SET";
        
        Arrays.sort(v_Methods ,MethodComparator.getInstance());
        
        // 先行过滤出Getter方法(包括is开头的方法)
        for (int i=0; i<v_Methods.length; i++)
        {
            Method v_Method = v_Methods[i];
            
            if ( v_Method.getParameterTypes().length == 0 )
            {
                if ( v_Method.getName().startsWith("get") )
                {
                    v_Ret.putRow(v_GET ,v_Method.getName().substring(3) ,v_Method);
                }
                else if ( v_Method.getName().startsWith("is") )
                {
                    v_Ret.putRow(v_GET ,v_Method.getName().substring(2) ,v_Method);
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
                    Method v_Getter    = v_Ret.getRow(v_GET ,v_ShortName);
                    
                    if ( v_Getter != null )
                    {
                        if ( v_Method.getParameterTypes()[0] == v_Getter.getReturnType() )
                        {
                            v_Ret.putRow(v_SET ,v_ShortName ,v_Method);
                        }
                    }
                }
            }
        }
        
        // 核对是否成对出现
        List<String> v_ShortNames = Help.toListKeys(v_Ret.get(v_GET));
        for (String v_ShortName : v_ShortNames)
        {
            if ( v_Ret.getRow(v_SET ,v_ShortName) == null )
            {
                v_Ret.removeRow(v_GET ,v_ShortName);
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
		String v_GetMethodName_Get = i_GetMethodName.trim();
		String v_GetMethodName_Is  = i_GetMethodName.trim();
		
		if ( i_IsNorm )
		{
		    v_GetMethodName_Get = "get" + StringHelp.toUpperCaseByFirst(v_GetMethodName_Get);
		    v_GetMethodName_Is  = "is"  + StringHelp.toUpperCaseByFirst(v_GetMethodName_Is);
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
	 * @param i_Class
	 * @param i_MethodName
	 * @return
	 */
	public static List<Method> getMethods(Class<?> i_Class ,String i_MethodName)
	{
		List<Method> v_Ret     = new ArrayList<Method>();
		Method []    v_Methods = i_Class.getMethods();
		
		for (int i=0; i<v_Methods.length; i++)
		{
			if ( v_Methods[i].getName().equals(i_MethodName) )
			{
				v_Ret.add(v_Methods[i]);
			}
		}
		
		return v_Ret;
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
        List<Method> v_Ret     = new ArrayList<Method>();
        Method []    v_Methods = i_Class.getDeclaredMethods();
        
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
        
        return v_Ret;
    }
	
	
	
	/**
	 * 获取某一方法名称的所有方法对象。包括重载的多个方法
	 * 
	 * @param i_Class
	 * @param i_MethodName
	 * @param i_ParamSize
	 * @return
	 */
	public static List<Method> getMethods(Class<?> i_Class ,String i_MethodName ,int i_ParamSize)
	{
		List<Method> v_Ret     = new ArrayList<Method>();
		Method []    v_Methods = i_Class.getMethods();
		
		for (int i=0; i<v_Methods.length; i++)
		{
			if ( v_Methods[i].getName().equals(i_MethodName) )
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
            throw new java.lang.InstantiationError("");
        }
	}
	
	
	
	/**
     * 从Map集合中取值。实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-29
     * @version     v1.0
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
        
        String v_ChildMethodURL = StringHelp.replaceAll(i_MethodURL ,v_MethodURLArr[0] + "." ,"");
        
        if ( MethodReflect.isExtendImplement(v_MapValue ,Map.class) )
        {
            return getMapValue((Map<String ,?>)v_MapValue ,v_ChildMethodURL);
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
                return v_MethodReflect.invoke();
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
        Method []    v_Methods   = i_Instance.getClass().getMethods();
        List<Method> v_OKMethods = new ArrayList<Method>();
        Method       v_Method    = null;
        
        for (int i=0; i<v_Methods.length; i++)
        {
            v_Method = v_Methods[i];
            
            if ( v_Method.getName().equals(i_MethodName) )
            {
                if ( v_Method.getParameterTypes().length == i_MethodParams.length )
                {
                    boolean v_PTypeOK = true;
                    
                    // 先过滤出符合条件的方法  ZhengWei(HY) Add 2017-01-06
                    for (int v_PIndex=0; v_PIndex<i_MethodParams.length; v_PIndex++)
                    {
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
            return v_OKMethods.get(0);
        }
        else
        {
            return null;
        }
    }
	
	
	
	/**
	 * 方法的反射
	 * 
	 * 可实现xxx.yyy.www(或getXxx.getYyy.setWww)全路径的解释
	 * 可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的解释
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
		this.instances     = new ArrayList<Object>();
		this.methods       = new ArrayList<List<Method>>();
		this.methodsParams = new ArrayList<List<String>>();
		this.instances.add(i_Instance);
		this.methodURL     = i_MethodURL.trim();
		this.methodNames   = this.methodURL.replace("." ,"@").split("@");
		this.isNorm        = i_IsNorm;
		this.normType      = i_NormType;
		
		this.parser();
	}
	
	
	
	/**
	 * 方法的反射
	 * 
	 * 可实现getXxx.getYyy.setWww全路径的解释
	 * 可实现getXxx.getYyy.getWww全路径的解释
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
        this.methods       = new ArrayList<List<Method>>();
        this.methodsParams = new ArrayList<List<String>>();
        this.methodURL     = null;
        this.methodNames   = null;
        this.isNorm        = false;
        this.normType      = $NormType_Getter;
        
        this.instances.add(i_Instance);
        this.methods.add(new ArrayList<Method>());
        this.methods.get(0).add(i_Method);
        this.methodsParams.add(new ArrayList<String>());
	}
	
	
	
	/**
	 * 解释方法全路径
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
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
				this.methodNames[v_Index] = "get" + StringHelp.toUpperCaseByFirst(this.methodNames[v_Index]);
			}
			
			// 最后一个方法，有可能是Setter方法。
			if ( this.normType == $NormType_Setter )
			{
				this.methodNames[v_Index] = "set" + StringHelp.toUpperCaseByFirst(this.methodNames[v_Index]);
			}
			else
			{
				this.methodNames[v_Index] = "get" + StringHelp.toUpperCaseByFirst(this.methodNames[v_Index]);
			}
		}
		
		
		for (v_Index = 0; v_Index<this.methodNames.length - 1; v_Index++)
		{
			Class<?>     v_Class         = this.instances.get(v_Index).getClass();
			List<Method> v_Methods       = getMethods(v_Class ,this.methodNames[v_Index] ,this.methodsParams.get(v_Index).size());
			Object       v_ChildInstance = null;
			
			if ( Help.isNull(v_Methods) )
			{
			    throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
			}
			else if ( v_Methods.size() >= 2 )
			{
			    throw new VerifyError("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is more same Method name.");
			}
			
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
			
			this.methods.add(v_Methods);
			this.instances.add(v_ChildInstance);
		}
		
		
		// 解释方法名称的全路径上的最后一个
		Object v_LastInstance = this.instances.get(v_Index);
 		
		if ( this.normType == $NormType_Setter )
		{
		    List<Method> v_Methods = getSetMethods(v_LastInstance.getClass() ,this.methodNames[v_Index] ,false);
		    
		    if ( !Help.isNull(v_Methods) )
		    {
		        this.methods.add(v_Methods);
    		    return;
		    }
		    else
		    {
		        throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
		    }
		}
		else
		{
		    List<Method> v_Methods = getMethods(v_LastInstance.getClass() ,this.methodNames[v_Index] ,this.methodsParams.get(v_Index).size());
			
		    if ( Help.isNull(v_Methods) )
            {
                throw new NullPointerException("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is not exists.");
            }
            else if ( v_Methods.size() >= 2 )
            {
                throw new VerifyError("Method[" + methodURL + "]'s '" + this.methodNames[v_Index] + "' is more same Method name.");
            }
            else
            {
                this.methods.add(v_Methods);
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
		
		if ( this.normType == $NormType_Setter )
		{
		    if ( !Help.isNull(this.methods) )
		    {
    			List<Method> v_Methods = this.methods.get(v_Index);
    			for (int i=0; i<v_Methods.size(); i++)
    		    {
    			    if ( isExtendImplement(i_ParamValue ,v_Methods.get(i).getParameterTypes()[0]) )
    			    {
    			        v_Methods.get(i).invoke(this.instances.get(v_Index) ,i_ParamValue);
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
        int v_Index = this.instances.size() - 1;
        
        if ( this.normType == $NormType_Setter )
        {
            return null;
        }
        else
        {
            return this.methods.get(v_Index).get(0).getReturnType();
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
		int v_Index = this.instances.size() - 1;
		
		if ( this.normType == $NormType_Setter )
		{
			return null;
		}
		else
		{
		    Method v_Method = this.methods.get(v_Index).get(0);
		    
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
