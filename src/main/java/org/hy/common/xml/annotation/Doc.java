package org.hy.common.xml.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;





/**
 * Java代码注释。
 * 
 * 可在编译后的，通过反射方法获取注释内容。
 * 并且可以设置注释的显示顺序。
 * 
 * 建议：注释的内容不要太多、太长
 * 
 * @author      ZhengWei(HY)
 * @createDate  2014-11-30
 * @version     v1.0
 */
@Documented
@Target({ElementType.TYPE
        ,ElementType.METHOD
        ,ElementType.FIELD
        ,ElementType.CONSTRUCTOR
        ,ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Doc
{
    
    /**
     * 索引号。XDocument 按此属性排列顺序
     */
    public String index() default "";
    
    
    
    /**
     * 表示多个复杂的注解内容
     */
    public String [] value() default {};
    
    
    
    /**
     * 表示单个注解内容
     */
    public String info() default "";
    
}
