package org.hy.common.comparate;

import java.lang.reflect.Method;
import java.util.Comparator;





/**
 * "方法"类的排序比较器
 * 
 * 主要用于，将"方法"按方法名称排序
 * 
 * @author      ZhengWei(HY)
 * @createDate  2014-11-30
 * @version     v1.0  
 */
public class MethodComparator implements Comparator<Method>
{
    private static MethodComparator $MY;
    
    
    
    public static synchronized MethodComparator getInstance()
    {
        if ( $MY == null )
        {
            $MY = new MethodComparator();
        }
        
        return $MY;
    }
    

    
    public int compare(Method i_Method1 ,Method i_Method2)
    {
        if ( i_Method1 == i_Method2 )
        {
            return 0;
        }
        else if ( i_Method1 == null )
        {
            return -1;
        }
        else if ( i_Method2 == null )
        {
            return 1;
        }
        else
        {
            return i_Method1.getName().compareTo(i_Method2.getName());
        }
    }
    
}
