package org.hy.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;





/**
 * Java元类型的信息。
 * 
 * 主要用于注解分析功能
 * 
 * @author      ZhengWei(HY)
 * @createDate  2014-04-17
 * @version     v1.0  
 *              v2.0  2026-11-10  添加：获取类级的注释信息
 */
public class ClassInfo implements Comparable<ClassInfo>
{
    
    /** Java元类型 */
    private final Class<?>              classObj;
    
    /** 对应Java元类型中，方法被注解的所有方法 */
    private List<Method>                methods;
    
    /** 对应Java元类型中，属性被注解的所有属性 */
    private List<Field>                 fields;
    
    
    
    public ClassInfo(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            throw new NullPointerException("Class is null.");
        }
        
        this.classObj = i_Class;
    }
    
    
    
    /**
     * 获取类级的注释信息。即，Target(ElementType.TYPE）级的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-11-10
     * @version     v1.0
     *
     * @param <A>
     * @param i_AnnotationClass
     * @return
     */
    public <A extends Annotation> A getAnnotation(Class<? extends A> i_AnnotationClass)
    {
        return this.classObj.getAnnotation(i_AnnotationClass);
    }
    
    
    
    /**
     * 获取：Java元类型
     */
    public Class<?> getClassObj()
    {
        return classObj;
    }


    /**
     * 获取：对应Java元类型中，方法被注解的所有方法
     */
    public List<Method> getMethods()
    {
        return methods;
    }

    
    /**
     * 设置：对应Java元类型中，方法被注解的所有方法
     * 
     * @param i_Methods 对应Java元类型中，方法被注解的所有方法
     */
    public void setMethods(List<Method> i_Methods)
    {
        this.methods = i_Methods;
    }

    
    /**
     * 获取：对应Java元类型中，属性被注解的所有属性
     */
    public List<Field> getFields()
    {
        return fields;
    }

    
    /**
     * 设置：对应Java元类型中，属性被注解的所有属性
     * 
     * @param i_Fields 对应Java元类型中，属性被注解的所有属性
     */
    public void setFields(List<Field> i_Fields)
    {
        this.fields = i_Fields;
    }


    /**
     * 方法的注解，在排序上优先于属性的注解
     */
    @Override
    public int hashCode()
    {   
        int v_HashCode = this.classObj.hashCode() * 10000;
        
        if ( !Help.isNull(this.methods) )
        {
            v_HashCode += this.methods.size();
        }
        
        if ( !Help.isNull(this.fields) )
        {
            v_HashCode += this.fields.size() * 1000;
        }
        
        return v_HashCode;
    }
    
    
    
    public int compareTo(ClassInfo i_Other)
    {
        if ( i_Other == null )
        {
            return 1;
        }
        else if ( this == i_Other )
        {
            return 0;
        }
        else if ( this.getClassObj() == i_Other.getClassObj() )
        {
            return 0;
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
        else if ( i_Other instanceof ClassInfo )
        {
            return this.getClassObj().equals(((ClassInfo)i_Other).getClassObj());
        }
        else 
        {
            return false;
        }
    }
    
}
