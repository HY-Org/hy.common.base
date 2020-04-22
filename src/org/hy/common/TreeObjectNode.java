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
