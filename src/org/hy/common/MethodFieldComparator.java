package org.hy.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;





/**
 * "方法"类的排序比较器
 * 
 * 主要用于，将"方法"按方法名称排序，是按方法对应的成员属性名称在类中的编程编写的顺序排序的。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2018-04-25
 * @version     v1.0  
 */
public class MethodFieldComparator implements Comparator<Method>
{
    private Field [] fields;
    
    
    
    public MethodFieldComparator(Field [] i_Fileds)
    {
        this.fields = i_Fileds;
    }
    
    
    
    /**
     * 查询与方法名称一样（不区分大小写）的成员属性的位置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-25
     * @version     v1.0
     *
     * @param i_Method
     * @return         返回 -1 表示：没有找到
     */
    private int findFieldIndex(Method i_Method)
    {
        String v_MethodName = i_Method.getName();
        int    v_Index      = -1;
        
        // getter
        if ( i_Method.getParameterTypes().length <= 0 )
        {
            if ( v_MethodName.startsWith("get") )
            {
                v_Index = this.findFieldIndex(StringHelp.toLowerCaseByFirst(v_MethodName.substring(3)));
            }
            else if ( v_MethodName.startsWith("is") )
            {
                v_Index = this.findFieldIndex(v_MethodName);
                
                if ( v_Index < 0 )
                {
                    v_Index = this.findFieldIndex(StringHelp.toLowerCaseByFirst(v_MethodName.substring(2)));
                }
            }
            else
            {
                v_Index = this.findFieldIndex(v_MethodName);
            }
        }
        // setter
        else
        {
            if ( v_MethodName.startsWith("set") )
            {
                v_Index = this.findFieldIndex(StringHelp.toLowerCaseByFirst(v_MethodName.substring(3)));
            }
            else
            {
                v_Index = this.findFieldIndex(v_MethodName);
            }
        }
        
        return v_Index;
    }
    
    
    
    /**
     * 查询名称一样（不区分大小写）的成员属性的位置。
     * 
     * 先按区分大小写精确匹配，找不时再按不区分大小写模糊匹配。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-25
     * @version     v1.0
     *
     * @param i_Name
     * @return       返回 -1 表示：没有找到
     */
    private int findFieldIndex(String i_Name)
    {
        for (int i=0; i<this.fields.length; i++)
        {
            if ( this.fields[i].getName().equals(i_Name) )
            {
                return i;
            }
        }
        
        for (int i=0; i<this.fields.length; i++)
        {
            if ( this.fields[i].getName().equalsIgnoreCase(i_Name) )
            {
                return i;
            }
        }
        
        return -1;
    }
    
    
    
    public int compare(Method i_Method1 ,Method i_Method2)
    {
        if ( i_Method1 == i_Method2 )
        {
            return 0;
        }
        else if ( i_Method1 == null )
        {
            return 1;
        }
        else if ( i_Method2 == null )
        {
            return -1;
        }
        else
        {
            int v_Index1 = findFieldIndex(i_Method1);
            int v_Index2 = findFieldIndex(i_Method2);
            
            if ( v_Index1 < 0 )
            {
                if ( v_Index2 < 0 )
                {
                    return i_Method1.getName().compareTo(i_Method2.getName());
                }
                else
                {
                    return 1;
                }
            }
            else if ( v_Index2 < 0 )
            {
                return -1;
            }
            else
            {
                if ( v_Index1 < v_Index2 )
                {
                    return -1;
                }
                else if ( v_Index1 > v_Index2 )
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        }
    }
    
}
