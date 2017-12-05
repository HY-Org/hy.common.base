package org.hy.common;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.TablePartition;





/**
 * 类的反射。
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2014-04-17
 *              2017-12-04  1.添加：getAnnotationMethods()方法。
 */
public final class ClassReflect
{
    
    /**
     * 从指定集合i_Classes中，挑出有注解的Class。并分类存储。
     * 
     * @param i_Classes          Java元类型的集合
     * @param i_AnnotationClass  注解类型的元类型
     * @return
     */
    public static PartitionMap<ElementType ,ClassInfo> getAnnotations(List<Class<?>> i_Classes ,Class<? extends Annotation> i_AnnotationClass)
    {
        PartitionMap<ElementType ,ClassInfo> v_Ret = new TablePartition<ElementType ,ClassInfo>();
        
        for (int i=0; i<i_Classes.size(); i++)
        {
            Class<?>  v_Class     = i_Classes.get(i);
            ClassInfo v_ClassInfo = new ClassInfo(v_Class);  // 为了在多个分区引用同一对象，所以在此无论是否被注解都new
            
            // 判断类是否注解
            if ( v_Class.isAnnotationPresent(i_AnnotationClass) )
            {
                v_Ret.putRow(ElementType.TYPE ,v_ClassInfo);
            }
            
            // 判断类中的自有方法是否注解
            List<Method> v_Methods = MethodReflect.getAnnotationMethods(v_Class ,i_AnnotationClass);
            if ( !Help.isNull(v_Methods) )
            {
                v_ClassInfo.setMethods(v_Methods);
                
                v_Ret.putRow(ElementType.METHOD ,v_ClassInfo);
            }
            
            // 判断类中的自有属性是否注解
            List<Field> v_Fields = getAnnotationFields(v_Class ,i_AnnotationClass);
            if ( !Help.isNull(v_Fields) )
            {
                v_ClassInfo.setFields(v_Fields);
                
                v_Ret.putRow(ElementType.FIELD ,v_ClassInfo);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 从指定集合i_Classes中，挑出有注解的Class的方法。
     * 
     * @param i_Classes          Java元类型的集合
     * @param i_AnnotationClass  注解类型的元类型
     * @return
     */
    public static List<ClassInfo> getAnnotationMethods(List<Class<?>> i_Classes ,Class<? extends Annotation> i_AnnotationClass)
    {
        List<ClassInfo> v_Ret = new ArrayList<ClassInfo>();
        
        for (int i=0; i<i_Classes.size(); i++)
        {
            Class<?>  v_Class     = i_Classes.get(i);
            ClassInfo v_ClassInfo = new ClassInfo(v_Class);
            
            // 判断类中的自有方法是否注解
            List<Method> v_Methods = MethodReflect.getAnnotationMethods(v_Class ,i_AnnotationClass);
            if ( !Help.isNull(v_Methods) )
            {
                v_ClassInfo.setMethods(v_Methods);
                
                v_Ret.add(v_ClassInfo);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取注解的所有属性对象
     * 
     * 只获取"本类自己"的属性
     * 
     * @param i_Class
     * @param i_AnnotationClass   注解类型的元类型
     * @return
     */
    public static List<Field> getAnnotationFields(Class<?> i_Class ,Class<? extends Annotation> i_AnnotationClass)
    {
        List<Field> v_Ret    = new ArrayList<Field>();
        Field []    v_Fields = i_Class.getDeclaredFields();
        

        for (int i=0; i<v_Fields.length; i++)
        {
            Field v_Field = v_Fields[i];
            
            if ( v_Field.isAnnotationPresent(i_AnnotationClass) )
            {
                v_Ret.add(v_Field);
            }
        }
        
        return v_Ret;
    }
    
    
    
    private ClassReflect()
    {
        
    }
    
}
