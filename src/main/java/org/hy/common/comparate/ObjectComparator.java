package org.hy.common.comparate;

import java.util.Comparator;

import org.hy.common.Help;
import org.hy.common.MethodReflect;





/**
 * 多维的排序比较器
 *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
 *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
 *         优点是，不会改变原属性值的保存格式。
 *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
 *   4. 支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
 * 
 * @author      ZhengWei(HY)
 * @createDate  2015-12-10
 * @version     v1.0
 *              v2.0  2017-06-15  添加：支持面向对象：参与排序的属性名，可实现xxx.yyy.www(或getXxx.getYyy.getWww)全路径的比较
 */
public class ObjectComparator implements Comparator<Object>
{
    /** 正序。从小到大 */
    public final static String $OrderType_Asc     = "ASC";
    
    /** 倒序 */
    public final static String $OrderType_Desc    = "DESC";
    
    /** 字符转数字后，按正序排 */
    public final static String $OrderType_NumAsc  = "NUMASC";
    
    /** 字符转数字后，按倒序排 */
    public final static String $OrderType_NumDesc = "NUMDESC";
    
    
    
    /** 属性名称的集合 */
    private String        [] propertyNames;
    
    /**
     * 每个属性的排序标记的集合。标记值含意如下：
     *   -1   倒序。
     *    1   正序。从小到大。
     *   -2   字符转数字后，按倒序排。
     *    2   字符转数字后，按正序排。
     */
    private int           [] propertyOrders;
    
    /**
     * 属性名称对应出在对象中的属性Getter(is)方法的集合
     * 
     * 注意：propertyMethods的大小可能小于上面两个数组，因为它中只是保留正确匹配到属性方法
     */
    private MethodReflect [] propertyMethods;
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2015-12-10
     * @version     v1.0
     *
     * @param i_MetadataObj        一个用于获取序列对象元数据的样例对象
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序
     * @throws InstantiationException
     */
    public ObjectComparator(Object i_MetadataObj ,String ... i_SortPropertyNames)
    {
        if ( i_MetadataObj == null )
        {
            throw new NullPointerException("MetadataObj is null.");
        }
        
        if ( Help.isNull(i_SortPropertyNames) )
        {
            throw new NullPointerException("SortPropertyNames is null.");
        }
        
        propertyNames   = new String       [i_SortPropertyNames.length];
        propertyOrders  = new int          [i_SortPropertyNames.length];
        propertyMethods = new MethodReflect[i_SortPropertyNames.length];
        for (int v_SNIndex=0; v_SNIndex<i_SortPropertyNames.length; v_SNIndex++)
        {
            String [] v_Temp = i_SortPropertyNames[v_SNIndex].replaceAll("  " ," ").split(" ");
            
            propertyNames[v_SNIndex] = v_Temp[0].trim();
            if ( v_Temp.length >= 2 )
            {
                v_Temp[v_Temp.length-1] = v_Temp[v_Temp.length-1].trim().toUpperCase();
                
                if ( $OrderType_Desc.equals(v_Temp[v_Temp.length-1]) )
                {
                    propertyOrders[v_SNIndex] = -1;   // 倒序
                }
                else if ( $OrderType_NumDesc.equals(v_Temp[v_Temp.length-1]) )
                {
                    propertyOrders[v_SNIndex] = -2;   // 字符转数字后，按倒序排。
                }
                else if ( $OrderType_NumAsc.equals(v_Temp[v_Temp.length-1]) )
                {
                    propertyOrders[v_SNIndex] = 2;    // 字符转数字后，按正序排。
                }
                else
                {
                    propertyOrders[v_SNIndex] = 1;    // 正序
                }
            }
            else
            {
                propertyOrders[v_SNIndex] = 1;        // 正序
            }
        }
        
        // 先都初始化为null。null表示没有匹配到对应的属性方法
        for (int v_SNIndex=0; v_SNIndex<propertyNames.length; v_SNIndex++)
        {
            propertyMethods[v_SNIndex] = null;
        }
        
        int v_ValidCount = 0;
        for (int v_SNIndex=0; v_SNIndex<propertyNames.length; v_SNIndex++)
        {
            // Method v_Method = MethodReflect.getGetMethod(i_MetadataObj.getClass() ,propertyNames[v_SNIndex] ,true);
            try
            {
                MethodReflect v_Method = new MethodReflect(i_MetadataObj.getClass() ,propertyNames[v_SNIndex] ,true ,MethodReflect.$NormType_Getter);
                propertyMethods[v_SNIndex] = v_Method;
                v_ValidCount++;
            }
            catch (Exception exce)
            {
                // 没有找到对应的方法
                // Nothing.
            }
        }
        
        // 去除没有正确匹配到属性方法的
        if ( v_ValidCount != propertyMethods.length )
        {
            MethodReflect [] v_PIndexes   = new MethodReflect[v_ValidCount];
            int              v_ValidIndex = 0;
            for (int v_SNIndex=0; v_SNIndex<propertyMethods.length; v_SNIndex++)
            {
                if ( propertyMethods[v_SNIndex] != null )
                {
                    v_PIndexes[v_ValidIndex++] = propertyMethods[v_SNIndex];
                }
            }
            
            propertyMethods = v_PIndexes;
        }
        
        if ( propertyMethods.length <= 0 )
        {
            throw new NullPointerException("Can't matching any Property.");
        }
    }
    
    
    
    @Override
    @SuppressWarnings({"rawtypes" ,"unchecked"})
    public int compare(Object i_Obj01 ,Object i_Obj02)
    {
        if ( i_Obj01 == i_Obj02 )
        {
            return 0;
        }
        else if ( i_Obj01 == null )
        {
            return -1;
        }
        else if ( i_Obj02 == null )
        {
            return 1;
        }
        else
        {
            for (int v_PIndex=0; v_PIndex<propertyMethods.length; v_PIndex++)
            {
                Object v_Value01 = null;
                Object v_Value02 = null;
                
                try
                {
                    v_Value01 = propertyMethods[v_PIndex].invokeForInstance(i_Obj01);
                }
                catch (Exception exce)
                {
                    v_Value01 = null;
                }
                
                try
                {
                    v_Value02 = propertyMethods[v_PIndex].invokeForInstance(i_Obj02);
                }
                catch (Exception exce)
                {
                    v_Value02 = null;
                }
                
                // 正序排序
                if ( propertyOrders[v_PIndex] == 1 )
                {
                    if ( v_Value01 == v_Value02 )
                    {
                        // Nothing.
                    }
                    else if ( v_Value01 == null )
                    {
                        return -1;
                    }
                    else if ( v_Value02 == null )
                    {
                        return 1;
                    }
                    else
                    {
                        if ( v_Value01 instanceof Comparable )
                        {
                            int v_Compare = ((Comparable)v_Value01).compareTo(v_Value02);
                            if ( v_Compare != 0 )
                            {
                                return v_Compare;
                            }
                        }
                        else
                        {
                            int v_Compare = v_Value01.hashCode() - v_Value02.hashCode();
                            if ( v_Compare != 0 )
                            {
                                return v_Compare > 0 ? 1 : -1;
                            }
                        }
                    }
                }
                // 倒序排序
                else if ( propertyOrders[v_PIndex] == -1 )
                {
                    if ( v_Value01 == v_Value02 )
                    {
                        // Nothing.
                    }
                    else if ( v_Value01 == null )
                    {
                        return 1;
                    }
                    else if ( v_Value02 == null )
                    {
                        return -1;
                    }
                    else
                    {
                        if ( v_Value01 instanceof Comparable )
                        {
                            int v_Compare = ((Comparable)v_Value01).compareTo(v_Value02);
                            if ( v_Compare != 0 )
                            {
                                return v_Compare * -1;
                            }
                        }
                        else
                        {
                            int v_Compare = v_Value01.hashCode() - v_Value02.hashCode();
                            if ( v_Compare != 0 )
                            {
                                return v_Compare > 0 ? -1 : 1;
                            }
                        }
                    }
                }
                // 字符转数字后，按正序排。
                else if ( propertyOrders[v_PIndex] == 2 )
                {
                    if ( v_Value01 == v_Value02 )
                    {
                        // Nothing.
                    }
                    else if ( v_Value01 == null )
                    {
                        return -1;
                    }
                    else if ( v_Value02 == null )
                    {
                        return 1;
                    }
                    else
                    {
                        Double v_Double01 = Double.valueOf(Help.NVL(v_Value01.toString() ,"0"));
                        Double v_Double02 = Double.valueOf(Help.NVL(v_Value02.toString() ,"0"));
                        
                        int v_Compare = v_Double01.compareTo(v_Double02);
                        if ( v_Compare != 0 )
                        {
                            return v_Compare;
                        }
                    }
                }
                // 字符转数字后，按倒序排。
                else
                {
                    if ( v_Value01 == v_Value02 )
                    {
                        // Nothing.
                    }
                    else if ( v_Value01 == null )
                    {
                        return 1;
                    }
                    else if ( v_Value02 == null )
                    {
                        return -1;
                    }
                    else
                    {
                        Double v_Double01 = Double.valueOf(Help.NVL(v_Value01.toString() ,"0"));
                        Double v_Double02 = Double.valueOf(Help.NVL(v_Value02.toString() ,"0"));
                        
                        int v_Compare = v_Double01.compareTo(v_Double02);
                        if ( v_Compare != 0 )
                        {
                            return v_Compare * -1;
                        }
                    }
                }
            }
            
            return 0;
        }
    }
    
    
    
    public void clear()
    {
        if ( !Help.isNull(this.propertyMethods) )
        {
            for (int x=0; x<this.propertyMethods.length; x++)
            {
                this.propertyMethods[x].clear();
                this.propertyMethods[x] = null;
            }
        }
        
        if ( !Help.isNull(this.propertyNames) )
        {
            for (int x=0; x<this.propertyNames.length; x++)
            {
                this.propertyNames[x] = null;
            }
        }
    }
    
    
    
    public void clearDestroy()
    {
        if ( !Help.isNull(this.propertyMethods) )
        {
            for (int x=0; x<this.propertyMethods.length; x++)
            {
                this.propertyMethods[x].clearDestroy();
                this.propertyMethods[x] = null;
            }
            
            this.propertyMethods = null;
        }
        
        if ( !Help.isNull(this.propertyNames) )
        {
            for (int x=0; x<this.propertyNames.length; x++)
            {
                this.propertyNames[x] = null;
            }
            
            this.propertyNames = null;
        }
    }
    
}
