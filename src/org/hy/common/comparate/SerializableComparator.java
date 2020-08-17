package org.hy.common.comparate;

import java.util.Comparator;

import org.hy.common.Help;
import org.hy.common.Serializable;





/**
 * 多维的排序比较器
 *   1. 支持数据库SQL一样形式的多个属性上不同方向的排序
 *   2. 支持属性值的类型是String(或其它)，但按数字排序的功能。为空值是，默认填充数字0。
 *         优点是，不会改变原属性值的保存格式。
 *   3. 支持只对正确匹配到属性方法的属性名排序。在匹配时，属性名不区分大小写。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2015-11-24
 * @version     v1.0  
 */
public class SerializableComparator implements Comparator<Serializable>
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
    private String [] propertyNames; 
    
    /** 
     * 每个属性的排序标记的集合。标记值含意如下：
     *   -1   倒序。
     *    1   正序。从小到大。
     *   -2   字符转数字后，按倒序排。
     *    2   字符转数字后，按正序排。
     */
    private int    [] propertyOrders;
    
    /** 
     * 属性名称对应出在对象中的属性索引号的集合
     * 
     * 注意：propertyIndexes的大小可能小于上面两个数组，因为它中是保留正确匹配到属性方法的属性索引号
     */
    private int    [] propertyIndexes;
    
    
    
    /**
     * 构造器 
     *
     * @author      ZhengWei(HY)
     * @createDate  2015-11-24
     * @version     v1.0
     *
     * @param i_MetadataObj        一个用于获取序列对象元数据的样例对象
     * @param i_SortPropertyNames  参与排序的属性名称及排序标识。样式如，["name desc" ,"age NumDesc" ,"sex asc"]。
     *                             没有排序标识的，默认按从小到大排序，即正序 
     * @throws InstantiationException 
     */
    public SerializableComparator(Serializable i_MetadataObj ,String ... i_SortPropertyNames)
    {
        if ( i_MetadataObj == null )
        {
            throw new NullPointerException("MetadataObj is null.");
        }
        
        if ( Help.isNull(i_SortPropertyNames) )
        {
            throw new NullPointerException("SortPropertyNames is null.");
        }
        
        propertyNames   = new String[i_SortPropertyNames.length];
        propertyOrders  = new int   [i_SortPropertyNames.length];
        propertyIndexes = new int   [i_SortPropertyNames.length];
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
        
        // 先都初始化为-1。-1表示没有匹配到对应的属性方法
        for (int v_SNIndex=0; v_SNIndex<propertyNames.length; v_SNIndex++)
        {
            propertyIndexes[v_SNIndex] = -1;
        }
        
        int v_ValidCount = 0;
        for (int v_PIndex=0; v_PIndex<i_MetadataObj.gatPropertySize(); v_PIndex++)
        {
            for (int v_SNIndex=0; v_SNIndex<propertyNames.length; v_SNIndex++)
            {
                if ( i_MetadataObj.gatPropertyName(v_PIndex).equalsIgnoreCase("get" + propertyNames[v_SNIndex])
                  || i_MetadataObj.gatPropertyName(v_PIndex).equalsIgnoreCase("is"  + propertyNames[v_SNIndex]) )
                {
                    propertyIndexes[v_SNIndex] = v_PIndex;
                    v_ValidCount++;
                    break;
                }
            }
        }
        
        // 去除没有正确匹配到属性方法的
        if ( v_ValidCount != propertyIndexes.length )
        {
            int [] v_PIndexes   = new int[v_ValidCount];
            int    v_ValidIndex = 0;
            for (int v_SNIndex=0; v_SNIndex<propertyIndexes.length; v_SNIndex++)
            {
                if ( propertyIndexes[v_SNIndex] >= 0 )
                {
                    v_PIndexes[v_ValidIndex++] = propertyIndexes[v_SNIndex];
                }
            }
            
            propertyIndexes = v_PIndexes;
        }
        
        if ( propertyIndexes.length <= 0 )
        {
            throw new NullPointerException("Can't matching any Property.");
        }
    }
    
    
    
    @SuppressWarnings({"rawtypes" ,"unchecked"})
    public int compare(Serializable i_Obj01 ,Serializable i_Obj02)
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
            for (int v_PIndex=0; v_PIndex<propertyIndexes.length; v_PIndex++)
            {
                Object v_Value01 = i_Obj01.gatPropertyValue(propertyIndexes[v_PIndex]);
                Object v_Value02 = i_Obj02.gatPropertyValue(propertyIndexes[v_PIndex]);
                
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
    
}
