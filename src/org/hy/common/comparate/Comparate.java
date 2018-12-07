package org.hy.common.comparate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Help;





/**
 * 两个相同元素类型的集合（数组、List、Set、Map）对比。
 * 
 * 可以区分出如下四种情况
 *   1. New： 两对象A与B对比时，A中没有，B中有的数据
 *   2. Same：两对象A与B对比时，A、B均有并相同的数据
 *   3. Diff：两对象A与B对比时，A、B均有，但不相同的数据（仅用于Map集合的对比）
 *   4. Del： 两对象A与B对比时，A中有，B中没有的数据
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-12-07
 * @version     v1.0
 */
public class Comparate
{
    
    /**
     * 对比两元素类型相同的Set集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-07
     * @version     v1.0
     *
     * @param i_A
     * @param i_B
     * @return
     */
    public static <V> ComparateResult<Set<V>> comparate(Set<V> i_A ,Set<V> i_B)
    {
        ComparateResult<Set<V>> v_CResult = new ComparateResult<Set<V>>();
        
        if ( i_A == i_B )
        {
            v_CResult.setSameData(i_B);
            return v_CResult;
        }
        else if ( Help.isNull(i_A) && Help.isNull(i_B) )
        {
            return v_CResult;
        }
        else if ( Help.isNull(i_A) && !Help.isNull(i_B) )
        {
            v_CResult.setNewData(i_B);
            return v_CResult;
        }
        else if ( !Help.isNull(i_A) && Help.isNull(i_B) )
        {
            v_CResult.setDelData(i_A);
            return v_CResult;
        }
        
        Iterator<V> v_AIter     = i_A.iterator();
        Iterator<V> v_BIter     = i_B.iterator();
        Set<V>      v_NewDatas  = new HashSet<V>();
        Set<V>      v_SameDatas = new HashSet<V>();
        Set<V>      v_DelDatas  = new HashSet<V>();
        for ( ; v_AIter.hasNext() ; )
        {
            V v_AItem = v_AIter.next();
            
            if ( v_AItem == null )
            {
                continue;
            }
            
            if ( i_B.contains(v_AItem) )
            {
                v_SameDatas.add(v_AItem);
            }
            else
            {
                v_DelDatas.add(v_AItem);
            }
        }
        
        for ( ; v_BIter.hasNext() ; )
        {
            V v_BItem = v_BIter.next();
            
            if ( !i_A.contains(v_BItem) )
            {
                v_NewDatas.add(v_BItem);
            }
        }
        
        if ( !Help.isNull(v_NewDatas) )
        {
            v_CResult.setNewData(v_NewDatas);
        }
        if ( !Help.isNull(v_SameDatas) )
        {
            v_CResult.setSameData(v_SameDatas);
        }
        if ( !Help.isNull(v_DelDatas) )
        {
            v_CResult.setDelData(v_DelDatas);
        }
        
        return v_CResult;
    }
    
    
    
    /**
     * 对比两元素类型相同的List集合
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-07
     * @version     v1.0
     *
     * @param i_A
     * @param i_B
     * @return
     */
    public static <V> ComparateResult<List<V>> comparate(List<V> i_A ,List<V> i_B)
    {
        ComparateResult<List<V>> v_CResult = new ComparateResult<List<V>>();
        
        if ( i_A == i_B )
        {
            v_CResult.setSameData(i_B);
            return v_CResult;
        }
        else if ( Help.isNull(i_A) && Help.isNull(i_B) )
        {
            return v_CResult;
        }
        else if ( Help.isNull(i_A) && !Help.isNull(i_B) )
        {
            v_CResult.setNewData(i_B);
            return v_CResult;
        }
        else if ( !Help.isNull(i_A) && Help.isNull(i_B) )
        {
            v_CResult.setDelData(i_A);
            return v_CResult;
        }
        
        Map<V ,Integer> v_AMap = new HashMap<V ,Integer>();
        Map<V ,Integer> v_BMap = new HashMap<V ,Integer>();
        for (int i=i_B.size()-1; i>=0; i--)
        {
            V v_BItem = i_B.get(i);
            
            if ( v_BItem == null )
            {
                continue;
            }
            
            v_BMap.put(v_BItem ,i);
        }
        
        List<V> v_NewDatas  = new ArrayList<V>();
        List<V> v_SameDatas = new ArrayList<V>();
        List<V> v_DelDatas  = new ArrayList<V>();
        for (int i=i_A.size()-1; i>=0; i--)
        {
            V v_AItem = i_A.get(i);
            
            if ( v_AItem == null )
            {
                continue;
            }
            
            v_AMap.put(v_AItem ,i);
            
            if ( v_BMap.containsKey(v_AItem) )
            {
                v_SameDatas.add(v_AItem);
            }
            else
            {
                v_DelDatas.add(v_AItem);
            }
        }
        
        for (int i=i_B.size()-1; i>=0; i--)
        {
            V v_BItem = i_B.get(i);
            
            if ( !v_AMap.containsKey(v_BItem) )
            {
                v_NewDatas.add(v_BItem);
            }
        }
        
        if ( !Help.isNull(v_NewDatas) )
        {
            v_CResult.setNewData(v_NewDatas);
        }
        if ( !Help.isNull(v_SameDatas) )
        {
            v_CResult.setSameData(v_SameDatas);
        }
        if ( !Help.isNull(v_DelDatas) )
        {
            v_CResult.setDelData(v_DelDatas);
        }
        
        return v_CResult;
    }
    
    
    
    /**
     * 对比两元素类型相同的数组
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-07
     * @version     v1.0
     *
     * @param i_A
     * @param i_B
     * @return
     */
    public static <V> ComparateResult<V []> comparate(V [] i_A ,V [] i_B)
    {
        ComparateResult<V []> v_CResult = new ComparateResult<V []>();
        
        if ( i_A == i_B )
        {
            v_CResult.setSameData(i_B);
            return v_CResult;
        }
        else if ( i_A == null && i_B != null )
        {
            v_CResult.setNewData(i_B);
            return v_CResult;
        }
        else if ( i_A != null && i_B == null )
        {
            v_CResult.setDelData(i_A);
            return v_CResult;
        }
        
        Map<V ,Integer> v_AMap = new HashMap<V ,Integer>();
        Map<V ,Integer> v_BMap = new HashMap<V ,Integer>();
        for (int i=i_B.length-1; i>=0; i--)
        {
            if ( i_B[i] == null )
            {
                continue;
            }
            
            v_BMap.put(i_B[i] ,i);
        }
        
        List<V> v_NewDatas  = new ArrayList<V>();
        List<V> v_SameDatas = new ArrayList<V>();
        List<V> v_DelDatas  = new ArrayList<V>();
        for (int i=i_A.length-1; i>=0; i--)
        {
            if ( i_A[i] == null )
            {
                continue;
            }
            
            v_AMap.put(i_A[i] ,i);
            
            if ( v_BMap.containsKey(i_A[i]) )
            {
                v_SameDatas.add(i_A[i]);
            }
            else
            {
                v_DelDatas.add(i_A[i]);
            }
        }
        
        for (int i=i_B.length-1; i>=0; i--)
        {
            if ( !v_AMap.containsKey(i_B[i]) )
            {
                v_NewDatas.add(i_B[i]);
            }
        }
        
        if ( !Help.isNull(v_NewDatas) )
        {
            v_CResult.setNewData(v_NewDatas.toArray(i_A));
        }
        if ( !Help.isNull(v_SameDatas) )
        {
            v_CResult.setSameData(v_SameDatas.toArray(i_A));
        }
        if ( !Help.isNull(v_DelDatas) )
        {
            v_CResult.setDelData(v_DelDatas.toArray(i_A));
        }
        
        return v_CResult;
    }
    
    
    
    private Comparate()
    {
        // Nothing.
    }
    
}
