package org.hy.common;





/**
 * 过程及函数返回多内容值的对象
 * 
 * 1. 基本代码为 java.lang.Boolean 类的代码。
 * 2. 在此基础上附加三种类型的返回参数(int、String、Object)
 * 3. value不再为final，即变的
 * 4. 支持 java.lang.Boolean 类的对比及比较功能。
 * 5. 默认实例化为: false
 * 6. 所有setter方法均有返回值，并且为this
 * 
 * @author      ZhengWei(HY)
 * @createDate  2013-10-28
 * @version     v1.0
 * @version     v2.0  2015-02-06  添加：异常对象属性exception
 * @version     v3.0  2017-02-04  添加：标准的setter、getter方法。
 */
public class Return<O> implements java.io.Serializable ,Comparable<Object>
{
    private static final long serialVersionUID  = -4990237526348797524L;

    

    /**
     * The value of the Boolean.
     * 
     * @serial
     */
    private boolean             value = false;
    
    /**
     * Int可选参数
     */
    public int                  paramInt;
    
    /**
     * 字符串可选参数
     */
    public String               paramStr;
    
    /**
     * 对象可选参数
     */
    public O                    paramObj;
    
    /**
     * 异常对象
     */
    public Exception            exception;
    
    
    
    public Return()
    {
        this(false);
    }
    
    
    
    /**
     * Allocates a <code>Boolean</code> object representing the
     * <code>value</code> argument.
     * 
     * <p>
     * <b>Note: It is rarely appropriate to use this constructor. Unless a
     * <i>new</i> instance is required, the static factory
     * {@link #valueOf(boolean)} is generally a better choice. It is likely to
     * yield significantly better space and time performance.</b>
     * 
     * @param value
     *            the value of the <code>Boolean</code>.
     */
    public Return(boolean value)
    {
        this.value     = value;
        this.paramInt  = 0;
        this.paramStr  = null;
        this.paramObj  = null;
        this.exception = null;
    }
    
    
    
    /**
     * Allocates a <code>Boolean</code> object representing the
     * <code>value</code> argument.
     * 
     * <p>
     * <b>Note: It is rarely appropriate to use this constructor. Unless a
     * <i>new</i> instance is required, the static factory
     * {@link #valueOf(boolean)} is generally a better choice. It is likely to
     * yield significantly better space and time performance.</b>
     * 
     * @param value
     *            the value of the <code>Boolean</code>.
     * @param obj
     */
    public Return(boolean value ,O obj)
    {
        this.value     = value;
        this.paramInt  = 0;
        this.paramStr  = null;
        this.paramObj  = obj;
        this.exception = null;
    }



    /**
     * Allocates a <code>Boolean</code> object representing the value
     * <code>true</code> if the string argument is not <code>null</code> and
     * is equal, ignoring case, to the string {@code "true"}. Otherwise,
     * allocate a <code>Boolean</code> object representing the value
     * <code>false</code>. Examples:
     * <p>
     * {@code new Boolean("True")} produces a <tt>Boolean</tt> object that
     * represents <tt>true</tt>.<br>
     * {@code new Boolean("yes")} produces a <tt>Boolean</tt> object that
     * represents <tt>false</tt>.
     * 
     * @param s
     *            the string to be converted to a <code>Boolean</code>.
     */
    public Return(String s)
    {
        this(toBoolean(s));
    }
    
    
    
    /**
     * Allocates a <code>Boolean</code> object representing the value
     * <code>true</code> if the string argument is not <code>null</code> and
     * is equal, ignoring case, to the string {@code "true"}. Otherwise,
     * allocate a <code>Boolean</code> object representing the value
     * <code>false</code>. Examples:
     * <p>
     * {@code new Boolean("True")} produces a <tt>Boolean</tt> object that
     * represents <tt>true</tt>.<br>
     * {@code new Boolean("yes")} produces a <tt>Boolean</tt> object that
     * represents <tt>false</tt>.
     * 
     * @param s
     *            the string to be converted to a <code>Boolean</code>.
     */
    public Return(String s ,O obj)
    {
        this(toBoolean(s));
        this.paramObj = obj;
    }
    
    
    
    public Return<O> set(boolean i_Value)
    {
        this.value = i_Value;
        return this;
    }
    
    
    
    public boolean get()
    {
        return this.value;
    }
    
    
    
    public int paramInt()
    {
        return paramInt;
    }


    
    public Return<O> paramInt(int i_ParamInt)
    {
        this.paramInt = i_ParamInt;
        return this;
    }


    
    public O paramObj()
    {
        return paramObj;
    }



    
    public Return<O> paramObj(O i_ParamObj)
    {
        this.paramObj = i_ParamObj;
        return this;
    }


    
    public String paramStr()
    {
        return paramStr;
    }


    
    public Return<O> paramStr(String i_ParamStr)
    {
        this.paramStr = i_ParamStr;
        return this;
    }


    
    public Exception exception()
    {
        return exception;
    }

    
    
    public Return<O> exception(Exception exception)
    {
        this.exception = exception;
        return this;
    }



    /**
     * Parses the string argument as a boolean. The <code>boolean</code>
     * returned represents the value <code>true</code> if the string argument
     * is not <code>null</code> and is equal, ignoring case, to the string
     * {@code "true"}.
     * <p>
     * Example: {@code Boolean.parseBoolean("True")} returns <tt>true</tt>.<br>
     * Example: {@code Boolean.parseBoolean("yes")} returns <tt>false</tt>.
     * 
     * @param s
     *            the <code>String</code> containing the boolean
     *            representation to be parsed
     * @return the boolean represented by the string argument
     * @since 1.5
     */
    public static boolean parseBoolean(String s)
    {
        return toBoolean(s);
    }



    /**
     * Returns the value of this <tt>Boolean</tt> object as a boolean
     * primitive.
     * 
     * @return the primitive <code>boolean</code> value of this object.
     */
    public boolean booleanValue()
    {
        return value;
    }



    /**
     * Returns a <tt>Boolean</tt> instance representing the specified
     * <tt>boolean</tt> value. If the specified <tt>boolean</tt> value is
     * <tt>true</tt>, this method returns <tt>Boolean.TRUE</tt>; if it is
     * <tt>false</tt>, this method returns <tt>Boolean.FALSE</tt>. If a
     * new <tt>Boolean</tt> instance is not required, this method should
     * generally be used in preference to the constructor
     * {@link #Boolean(boolean)}, as this method is likely to yield
     * significantly better space and time performance.
     * 
     * @param b
     *            a boolean value.
     * @return a <tt>Boolean</tt> instance representing <tt>b</tt>.
     * @since 1.4
     */
    public static Return<?> valueOf(boolean b)
    {
        return new Return<Object>(b);
    }



    /**
     * Returns a <code>Boolean</code> with a value represented by the
     * specified string. The <code>Boolean</code> returned represents a true
     * value if the string argument is not <code>null</code> and is equal,
     * ignoring case, to the string {@code "true"}.
     * 
     * @param s
     *            a string.
     * @return the <code>Boolean</code> value represented by the string.
     */
    public static Return<?> valueOf(String s)
    {
        return toBoolean(s) ? new Return<Object>(true) : new Return<Object>(false);
    }



    /**
     * Returns a <tt>String</tt> object representing the specified boolean. If
     * the specified boolean is <code>true</code>, then the string
     * {@code "true"} will be returned, otherwise the string {@code "false"}
     * will be returned.
     * 
     * @param b
     *            the boolean to be converted
     * @return the string representation of the specified <code>boolean</code>
     * @since 1.4
     */
    public static String toString(boolean b)
    {
        return b ? "true" : "false";
    }



    /**
     * Returns a <tt>String</tt> object representing this Boolean's value. If
     * this object represents the value <code>true</code>, a string equal to
     * {@code "true"} is returned. Otherwise, a string equal to {@code "false"}
     * is returned.
     * 
     * @return a string representation of this object.
     */
    public String toString()
    {
        return value ? "true" : "false";
    }



    /**
     * Returns a hash code for this <tt>Boolean</tt> object.
     * 
     * @return the integer <tt>1231</tt> if this object represents
     *         <tt>true</tt>; returns the integer <tt>1237</tt> if this
     *         object represents <tt>false</tt>.
     */
    public int hashCode()
    {
        return value ? 1231 : 1237;
    }



    /**
     * Returns <code>true</code> if and only if the argument is not
     * <code>null</code> and is a <code>Boolean</code> object that
     * represents the same <code>boolean</code> value as this object.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if the Boolean objects represent the same
     *         value; <code>false</code> otherwise.
     */
    public boolean equals(Object obj)
    {
        if ( obj == null )
        {
            return false;
        }
        else if ( obj instanceof Return )
        {
            return value == ((Return<?>) obj).booleanValue();
        }
        else if ( obj.getClass().equals(Boolean.class) )
        {
            return value == ((Boolean)obj).booleanValue();
        }
        return false;
    }



    /**
     * Returns <code>true</code> if and only if the system property named by
     * the argument exists and is equal to the string {@code "true"}.
     * (Beginning with version 1.0.2 of the Java<small><sup>TM</sup></small>
     * platform, the test of this string is case insensitive.) A system property
     * is accessible through <code>getProperty</code>, a method defined by
     * the <code>System</code> class.
     * <p>
     * If there is no property with the specified name, or if the specified name
     * is empty or null, then <code>false</code> is returned.
     * 
     * @param name
     *            the system property name.
     * @return the <code>boolean</code> value of the system property.
     * @see java.lang.System#getProperty(java.lang.String)
     * @see java.lang.System#getProperty(java.lang.String, java.lang.String)
     */
    public static boolean getBoolean(String name)
    {
        boolean result = false;
        try
        {
            result = toBoolean(System.getProperty(name));
        }
        catch (IllegalArgumentException e)
        {
        }
        catch (NullPointerException e)
        {
        }
        return result;
    }



    /**
     * Compares this <tt>Boolean</tt> instance with another.
     * 
     * @param b
     *            the <tt>Boolean</tt> instance to be compared
     * @return zero if this object represents the same boolean value as the
     *         argument; a positive value if this object represents true and the
     *         argument represents false; and a negative value if this object
     *         represents false and the argument represents true
     * @throws NullPointerException
     *             if the argument is <tt>null</tt>
     * @see Comparable
     * @since 1.5
     */
    public int compareTo(Object b)
    {
        if ( b == null )
        {
            return 1;
        }
        else if ( b instanceof Return )
        {
            return (((Return<?>)b).value == value ? 0 : (value ? 1 : -1));
        }
        else if ( b instanceof Boolean )
        {
            return (((Boolean)b).booleanValue() == value ? 0 : (value ? 1 : -1));
        }
        else
        {
            return 1;
        }
    }


    
    /**
     * 获取：The value of the Boolean.
     * 
     * @serial
     */
    public boolean isValue()
    {
        return value;
    }

    
    
    /**
     * 设置：The value of the Boolean.
     * 
     * @serial
     * 
     * @param value 
     */
    public Return<O> setValue(boolean value)
    {
        this.value = value;
        return this;
    }


    
    /**
     * 获取：Int可选参数
     */
    public int getParamInt()
    {
        return paramInt;
    }


    
    /**
     * 设置：Int可选参数
     * 
     * @param paramInt 
     */
    public Return<O> setParamInt(int paramInt)
    {
        this.paramInt = paramInt;
        return this;
    }


    
    /**
     * 获取：字符串可选参数
     */
    public String getParamStr()
    {
        return paramStr;
    }


    
    /**
     * 设置：字符串可选参数
     * 
     * @param paramStr 
     */
    public Return<O> setParamStr(String paramStr)
    {
        this.paramStr = paramStr;
        return this;
    }


    
    /**
     * 获取：对象可选参数
     */
    public O getParamObj()
    {
        return paramObj;
    }


    
    /**
     * 设置：对象可选参数
     * 
     * @param paramObj 
     */
    public Return<O> setParamObj(O paramObj)
    {
        this.paramObj = paramObj;
        return this;
    }


    
    /**
     * 获取：异常对象
     */
    public Exception getException()
    {
        return exception;
    }


    
    /**
     * 设置：异常对象
     * 
     * @param exception 
     */
    public Return<O> setException(Exception exception)
    {
        this.exception = exception;
        return this;
    }



    private static boolean toBoolean(String name)
    {
        return ((name != null) && name.equalsIgnoreCase("true"));
    }
    
}
