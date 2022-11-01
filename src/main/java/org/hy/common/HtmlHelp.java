package org.hy.common;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;





/**
 * HTML有很多方便的功能
 * 
 * @author      ZhengWei(HY)
 * @createDate  2013-08-26
 * @version     v1.0  
 *              v1.1  2018-05-15  添加：数据库java.sql.Timestamp时间的转换
 */
public final class HtmlHelp
{
    /** 设置<table>标签的属性 */
    public static final String $TABLE_ATTR = "table";
    
    /** 设置标题行的属性 */
    public static final String $TH_TR_ATTR = "thtr";
    
    /** 设置统一的标题属性 */
    public static final String $TH_ATTR    = "th";
    
    /** 独立设置每列的列标题的属性。下标从零开始，如 th_id_0、thid_1等。与 $TH_ATTR 相互冲突 */
    public static final String $TH_IDATTR  = "th_id_";
    
    /** 设置数据的行级属性 */
    public static final String $TR_ATTR    = "tr";
    
    /** 设置数据的列级属性 */
    public static final String $TD_ATTR    = "td";
    
    
    
    /**
     * 集合对象转成Html的<table>代码。
     * 
     * @param i_TableData  列表数据。可解析元素类型为List集合的；
     *                             可解析元素实现org.hy.common.Serializable接口的
     *                             如果实现Serializable接口，则标题也可以从此接口中读取。
     * @return
     */
    public static String toHtml(List<?> i_TableData)
    {
        return toHtml(null ,i_TableData ,null);
    }
    
    
    
    /**
     * 集合对象转成Html的<table>代码。
     * 
     * @param i_TitleName  标题。可没有
     * @param i_TableData  列表数据。可解析元素类型为List集合的；
     *                             可解析元素实现org.hy.common.Serializable接口的
     *                             如果实现Serializable接口，则标题也可以从此接口中读取。当然，优先读取i_TitleName为标题。
     * @return
     */
    public static String toHtml(List<String> i_TitleName ,List<?> i_TableData)
    {
        return toHtml(i_TitleName ,i_TableData ,null);
    }
    
    
    
    /**
     * 集合对象转成Html的<table>代码。
     * 
     * @param i_TableData  列表数据。可解析元素类型为List集合的；
     *                             可解析元素实现org.hy.common.Serializable接口的
     *                             如果实现Serializable接口，则标题也可以从此接口中读取。当然，优先读取i_TitleName为标题。
     * @param i_AttrMap    列表属性信息
     * @return
     */
    public static String toHtml(List<?> i_TableData ,Map<String ,String> i_AttrMap)
    {
        return toHtml(null ,i_TableData ,i_AttrMap);
    }
    
    
    
    /**
     * 集合对象转成Html的<table>代码。
     * 
     * @param i_TitleName  标题。可没有
     * @param i_TableData  列表数据。可解析元素类型为List集合的；
     *                             可解析元素实现org.hy.common.Serializable接口的
     *                             如果实现Serializable接口，则标题也可以从此接口中读取。当然，优先读取i_TitleName为标题。
     * @param i_AttrMap    列表属性信息
     * @return
     */
    public static String toHtml(List<String> i_TitleName ,List<?> i_TableData ,Map<String ,String> i_AttrMap)
    {
        if ( Help.isNull(i_TableData) )
        {
            return "";
        }
        
        if ( i_AttrMap == null )
        {
            i_AttrMap = new Hashtable<String ,String>();
        }
        
        
        StringBuilder v_Buffer = new StringBuilder();
        if ( i_AttrMap.containsKey($TABLE_ATTR) )
        {
            v_Buffer.append("<table ").append(i_AttrMap.get($TABLE_ATTR)).append(">");
        }
        else
        {
            v_Buffer.append("<table>");
        }
        
        // 如果有标题，设置<table>标题
        if ( !Help.isNull(i_TitleName) )
        {
            v_Buffer.append("<thead>");
            
            if ( i_AttrMap.containsKey($TH_TR_ATTR) )
            {
                v_Buffer.append("<tr ").append(i_AttrMap.get($TH_TR_ATTR)).append(">");
            }
            else
            {
                v_Buffer.append("<tr>");
            }
            
            if ( i_AttrMap.containsKey($TH_ATTR) )
            {
                for (int v_Col=0; v_Col<i_TitleName.size(); v_Col++)
                {
                    v_Buffer.append("<td id='COL_").append(v_Col).append("' ").append(i_AttrMap.get($TH_ATTR)).append(">");
                    v_Buffer.append(i_TitleName.get(v_Col));
                    v_Buffer.append("</td>");
                }
            }
            else
            {
                for (int v_Col=0; v_Col<i_TitleName.size(); v_Col++)
                {
                    if ( i_AttrMap.containsKey($TH_IDATTR + v_Col) )
                    {
                        v_Buffer.append("<td ").append(i_AttrMap.get($TH_IDATTR + v_Col)).append(">");
                    }
                    else
                    {
                        v_Buffer.append("<td id='COL_").append(v_Col).append("'>");
                    }
                    
                    v_Buffer.append(i_TitleName.get(v_Col));
                    v_Buffer.append("</td>");
                }
            }
            
            v_Buffer.append("</tr>");
            v_Buffer.append("</thead>");
        }
        else
        {
            if ( i_TableData.get(0) instanceof Serializable )
            {
                v_Buffer.append("<thead>");
                
                if ( i_AttrMap.containsKey($TH_TR_ATTR) )
                {
                    v_Buffer.append("<tr ").append(i_AttrMap.get($TH_TR_ATTR)).append(">");
                }
                else
                {
                    v_Buffer.append("<tr>");
                }
                
                Serializable v_TitleName = (Serializable)i_TableData.get(0);
                if ( i_AttrMap.containsKey($TH_ATTR) )
                {
                    for (int v_Col=0; v_Col<v_TitleName.gatPropertySize(); v_Col++)
                    {
                        v_Buffer.append("<td id='COL_").append(v_Col).append("' ").append(i_AttrMap.get($TH_ATTR)).append(">");
                        v_Buffer.append(v_TitleName.gatPropertyName(v_Col));
                        v_Buffer.append("</td>");
                    }
                }
                else
                {
                    for (int v_Col=0; v_Col<v_TitleName.gatPropertySize(); v_Col++)
                    {
                        if ( i_AttrMap.containsKey($TH_IDATTR + v_Col) )
                        {
                            v_Buffer.append("<td ").append(i_AttrMap.get($TH_IDATTR + v_Col)).append(">");
                        }
                        else
                        {
                            v_Buffer.append("<td id='COL_").append(v_Col).append("'>");
                        }
                        
                        v_Buffer.append(v_TitleName.gatPropertyName(v_Col));
                        v_Buffer.append("</td>");
                    }
                }
                
                v_Buffer.append("</tr>");
                v_Buffer.append("</thead>");
            }
        }
        
        
        String v_TR_Class = "";
        String v_TD_Class = "";
        if ( i_AttrMap.containsKey($TR_ATTR) )
        {
            v_TR_Class = " " + i_AttrMap.get($TR_ATTR);
        }
        if ( i_AttrMap.containsKey($TD_ATTR) )
        {
            v_TD_Class = " " + i_AttrMap.get($TD_ATTR);
        }
        
        // 设置列表数据
        int v_RowSize = i_TableData.size();
        for (int v_Row=0; v_Row<v_RowSize; v_Row++)
        {
            Object v_RowObj = i_TableData.get(v_Row);
            
            v_Buffer.append("<tr").append(v_TR_Class).append(">");
            
            if ( v_RowObj instanceof List )
            {
                List<?> v_RowInfo = (List<?>)v_RowObj;
                
                for (int v_Col=0; v_Col<v_RowInfo.size(); v_Col++)
                {
                    v_Buffer.append("<td").append(v_TD_Class).append(">");
                    
                    Object v_ColValue = v_RowInfo.get(v_Col);
                    if ( v_ColValue != null )
                    {
                        Class<?> v_ColClass = v_ColValue.getClass();
                        if ( Date.class == v_ColClass )
                        {
                            v_Buffer.append(((Date)v_ColValue).getFull());
                        }
                        else if ( java.util.Date.class == v_ColClass )
                        {
                            v_Buffer.append((new Date((java.util.Date)v_ColValue)).getFull());
                        }
                        // 添加对数据库时间的转换 Add ZhengWei(HY) 2018-05-15 
                        else if ( Timestamp.class == v_ColClass )
                        {
                            v_Buffer.append((new Date((Timestamp)v_ColValue)).getFull());
                        }
                        else
                        {
                            v_Buffer.append(v_ColValue);
                        }
                    }
                     
                    v_Buffer.append("</td>");
                }
            }
            else if ( v_RowObj instanceof Serializable )
            {
                Serializable v_RowInfo = (Serializable)v_RowObj;
                
                for (int v_Col=0; v_Col<v_RowInfo.gatPropertySize(); v_Col++)
                {
                    v_Buffer.append("<td").append(v_TD_Class).append(">");
                    
                    Object v_ColValue = v_RowInfo.gatPropertyValue(v_Col);
                    if ( v_ColValue != null )
                    {
                        Class<?> v_ColClass = v_ColValue.getClass();
                        
                        if ( Date.class == v_ColClass )
                        {
                            v_Buffer.append(((Date)v_ColValue).getFull());
                        }
                        else if ( java.util.Date.class == v_ColClass )
                        {
                            v_Buffer.append((new Date((java.util.Date)v_ColValue)).getFull());
                        }
                        // 添加对数据库时间的转换 Add ZhengWei(HY) 2018-05-15 
                        else if ( Timestamp.class == v_ColClass )
                        {
                            v_Buffer.append((new Date((Timestamp)v_ColValue)).getFull());
                        }
                        else
                        {
                            v_Buffer.append(v_ColValue);
                        }
                    }
                     
                    v_Buffer.append("</td>");
                }
            }
            
            v_Buffer.append("</tr>");
        }
        
        v_Buffer.append("</table>");
        
        return v_Buffer.toString();
    }
    
}
