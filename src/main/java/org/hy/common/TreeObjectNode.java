package org.hy.common;

import java.util.List;





/**
 * 配合 org.hy.common.TreeObject 集合构建树结构的集合
 * 
 * 用于 TreeObject 集合中元素的接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-04-21
 * @version     v1.0
 *              v2.0  2022-12-06  添加：节点的叶子标示和层次
 */
public interface TreeObjectNode<V extends TreeObjectNode<?>>
{
    
    /**
     * 获取当前节点ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @return
     */
    public String gatTreeObjectNodeID();
    
    
    
    /**
     * 获取父节点ID
     * 
     * 此接口可以没有，但为了能将普通的List结构转为一颗树结构，而冗余定义的。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @return
     */
    public String gatTreeObjectSuperID();
    
    
    
    /**
     * 获取当前节点的直属子节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @return
     */
    public List<V> gatTreeObjectChilds();
    
    
    
    /**
     * 当前节点的叶子标示。
     * 
     *   大于0时，表示直属子节点的数量，即当前节点为：非叶子节点
     *   等于0时，表示没有子节点，     即当前节点为：叶子节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     *
     * @return
     */
    public default int gatLeaf()
    {
        return this.gatTreeObjectChilds() == null ? 0 : this.gatTreeObjectChilds().size();
    }
    
    
    
    /**
     * 设置节点的树层次等级，数值越大层次越深，顶层数值最小
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     *
     * @param i_Level
     */
    public void satLevel(int i_Level);
    
    
    
    /**
     * 获取节点的树层次等级，数值越大层次越深，顶层数值最小
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     *
     * @return
     */
    public int gatLevel();
    
    
    
    /**
     * 添加子节点
     * 
     * 注意：在添加时异常时，请抛出异常，无异常即表示成功。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @param i_ChildNode    子节点数据
     */
    public void addTreeObjectChild(V i_ChildNode);
    
    
    
    /**
     * 删除子节点
     * 
     * 注意：在删除时异常时，请抛出异常，无异常即表示成功。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @param i_ChildNode    子节点数据
     */
    public void delTreeObjectChild(V i_ChildNode);
    
}
