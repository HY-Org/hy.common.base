package org.hy.common.comparate;

import java.io.Serializable;





/**
 * 两个相同元素类型的集合（数组、List、Set、Map）对比的结果。
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
public class ComparateResult<R> implements Serializable
{

    private static final long serialVersionUID = 5952319632949807297L;
    
    
    /** 两对象A与B对比时，A中没有，B中有的数据 */
    private R newData;
    
    /** 两对象A与B对比时，A、B均有并相同的数据 */
    private R sameData;
    
    /** 两对象A与B对比时，A、B均有，但不相同的数据（仅用于Map集合的对比）。记录B的值Map.value */
    private R diffData;
    
    /** 两对象A与B对比时，A中有，B中没有的数据 */
    private R delData;

    
    
    /**
     * 获取：两对象A与B对比时，A中没有，B中有的数据
     */
    public R getNewData()
    {
        return newData;
    }

    
    /**
     * 设置：两对象A与B对比时，A中没有，B中有的数据
     * 
     * @param newData 
     */
    public void setNewData(R newData)
    {
        this.newData = newData;
    }

    
    /**
     * 获取：两对象A与B对比时，A、B均有并相同的数据
     */
    public R getSameData()
    {
        return sameData;
    }

    
    /**
     * 设置：两对象A与B对比时，A、B均有并相同的数据
     * 
     * @param sameData 
     */
    public void setSameData(R sameData)
    {
        this.sameData = sameData;
    }

    
    /**
     * 获取：两对象A与B对比时，A、B均有，但不相同的数据
     */
    public R getDiffData()
    {
        return diffData;
    }

    
    /**
     * 设置：两对象A与B对比时，A、B均有，但不相同的数据
     * 
     * @param diffData 
     */
    public void setDiffData(R diffData)
    {
        this.diffData = diffData;
    }

    
    /**
     * 获取：两对象A与B对比时，A中有，B中没有的数据
     */
    public R getDelData()
    {
        return delData;
    }

    
    /**
     * 设置：两对象A与B对比时，A中有，B中没有的数据
     * 
     * @param delData 
     */
    public void setDelData(R delData)
    {
        this.delData = delData;
    }
    
}
