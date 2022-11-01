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
 *              v2.0  2020-01-15  添加：对方法参数的比较
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
    

    
    /**
     * 排序规则是：先按方法名称，再按参数类型、再按参数数量
     *
     * @author      ZhengWei(HY)
     * @createDate  2014-11-30
     * @version     v1.0
     *
     * @param i_Method1
     * @param i_Method2
     * @return
     */
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
            int v_Ret = i_Method1.getName().compareTo(i_Method2.getName());
            if ( v_Ret == 0 )
            {
                Class<?> [] v_ParamTyps1 = i_Method1.getParameterTypes();
                Class<?> [] v_ParamTyps2 = i_Method2.getParameterTypes();
                
                int v_MinLen = Math.min(v_ParamTyps1.length ,v_ParamTyps2.length);
                if ( v_MinLen == 0 )
                {
                    return v_ParamTyps1.length - v_ParamTyps2.length;
                }
                
                for (int i=0; i<v_MinLen; i++)
                {
                    String v_CName1 = v_ParamTyps1[i].getName();
                    String v_CName2 = v_ParamTyps2[i].getName();
                    
                    v_Ret = v_CName1.compareTo(v_CName2);
                    if ( v_Ret != 0 )
                    {
                        return v_Ret;
                    }
                }
                
                return v_ParamTyps1.length - v_ParamTyps2.length;
            }
            
            return v_Ret;
        }
    }
    
}
