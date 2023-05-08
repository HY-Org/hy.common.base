package org.hy.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;





/**
 * 构建树结构的知识图谱
 * 
 * 说明：
 *      1. 节点ID不为空：  树节点ID。不可为空或空指针。
 *      2. 多个顶级根节点：允许存在多个顶级根节点。即，没有只能有一个顶级根节点的限制。
 *      3. 顶级根节点：    i_SuperID=空值并不是判定为顶级根节点的标准。
 *                          标准是：当任何节点均不引用它时，i_SuperID才能被判定为顶级根节点
 *      4. 不可递归：      不可在树结构中出现递归引用。相信大家能理解，不再赘述原因
 *      5. 重复覆盖：      重复节点ID在构建树时，相同节点ID将会覆盖，其子节点将被级联删除
 *      6. 节点顺序：      顶级根节点的顺序按 “先来后到” 排序。
 *                          子节点的顺序由最终用户决定。即实现接口 TreeObjectNode 方来维护。
 *      7. 对数据零要求：  节点ID编码无要求、数据排序无要求(先入父后入子性能最高)、树结构层次无限制。
 *      8. 节点收敛：      支持同节点归属多个不同的父节点
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-04-21
 * @version     v1.0
 *              v2.0  2022-12-06  添加：获取父级对象 和 计算树结构层次
 *              v3.0  2023-05-05  修正：未删除无效的顶级节点
 *                                添加：支持同节点归属多个不同的父节点
 * 
 * @param <V>
 */
public class TreeKnowledgeGraph<V extends TreeObjectNode<V>> extends TablePartition<String ,V>
{

    private static final long serialVersionUID = -5685234915207090172L;

    /**
     * 树的节点ID与父节点ID的对应关系
     * 
     * Map.key    为节点ID
     * Map.value  为父节点ID
     */
    private PartitionMap<String ,String>   superNodeIDs;
    
    /**
     * 树的节点与直属子节点的对应关系
     * 
     * Map.key    为节点ID
     * Map.value  为直属子节点ID的集合
     */
    private PartitionMap<String ,String>   childNodeIDs;
    
    /**
     * 顶级根节点ID
     * 
     * Map.key    为顶级根节点ID
     * Map.value  为顶级根节点ID
     * 
     * 此属性可被动态计算得出。它存在是为了查询性能的快。
     */
    private LinkedHashMap<String ,String>  rootNodeIDs;
    
    /** 是否为替换模式 */
    private boolean                        isReplaceMode;
    
    
    
    public TreeKnowledgeGraph()
    {
        this("");
    }
    
    
    
    public TreeKnowledgeGraph(int i_InitialCapacity)
    {
        this("" ,i_InitialCapacity);
    }
    
    
    
    public TreeKnowledgeGraph(String i_Connectors)
    {
        super();
        
        this.superNodeIDs  = new TablePartition<String ,String>();
        this.childNodeIDs  = new TablePartition<String ,String>();
        this.rootNodeIDs   = new LinkedHashMap <String ,String>();
        this.isReplaceMode = false;
    }
    
    
    
    public TreeKnowledgeGraph(String i_Connectors ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.superNodeIDs  = new TablePartition<String ,String>();
        this.childNodeIDs  = new TablePartition<String ,String>();
        this.rootNodeIDs   = new LinkedHashMap <String ,String>();
        this.isReplaceMode = false;
    }
    
    
    
    /**
     * 获取父级对象节点编号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     *
     * @param i_NodeID
     * @return
     */
    public List<String> getSuperNodeID(String i_NodeID)
    {
        return this.superNodeIDs.get(i_NodeID);
    }
    
    
    
    /**
     * 获取父级对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     *
     * @param i_NodeID
     * @return
     */
    public List<V> getSuper(String i_NodeID)
    {
        List<String> v_SuperNodeIDs = this.getSuperNodeID(i_NodeID);
        
        if ( Help.isNull(v_SuperNodeIDs) )
        {
            return null;
        }
        else
        {
            List<V> v_Supers = new ArrayList<V>();
            for (String v_SuperNodeID : v_SuperNodeIDs)
            {
                v_Supers.addAll(this.get(v_SuperNodeID));
            }
            return v_Supers;
        }
    }
    
    
    
    /**
     * 生成一颗树(顶级为List结构的，有顺序)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @return
     */
    public List<V> getTreeList()
    {
        List<V> v_Tree = new ArrayList<V>();
        
        if ( !Help.isNull(this.rootNodeIDs) )
        {
            for (String v_RootNodeID : this.rootNodeIDs.keySet())
            {
                v_Tree.addAll(this.get(v_RootNodeID));
            }
        }
        
        return v_Tree;
    }
    
    
    
    /**
     * 用哪个节点下的所有子节点，生成一颗子树
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-22
     * @version     v1.0
     *
     * @param i_SuperNodeID  树节点数据
     * @return
     */
    public List<V> getTreeList(String i_SuperNodeID)
    {
        List<V>      v_Tree         = new ArrayList<V>();
        List<String> v_ChildNodeIDs = this.childNodeIDs.get(i_SuperNodeID);
        
        if ( !Help.isNull(v_ChildNodeIDs) )
        {
            for (String v_ChildNodeID : v_ChildNodeIDs)
            {
                v_Tree.addAll(this.get(v_ChildNodeID));
            }
        }
        
        return v_Tree;
    }
    
    
    
    /**
     * 生成一颗树(顶级为Map结构的，有顺序)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @return      Map.key   为节点ID
     *              Map.value 为节点对象
     */
    public Map<String ,V> getTreeMap()
    {
        Map<String ,V> v_Tree = new LinkedHashMap<String ,V>();
        
        if ( !Help.isNull(this.rootNodeIDs) )
        {
            for (String v_RootNodeID : this.rootNodeIDs.keySet())
            {
                List<V> v_RootNodes = this.get(v_RootNodeID);
                if ( !Help.isNull(v_RootNodes) )
                {
                    // 顶级节点，只能有一个固定的父节点，且父节点是不存的
                    v_Tree.put(v_RootNodeID ,v_RootNodes.get(0));
                }
            }
        }
        
        return v_Tree;
    }
    
    
    
    /**
     * 用哪个节点下的所有子节点，生成一颗子树
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-22
     * @version     v1.0
     *
     * @return      Map.key   为节点ID
     *              Map.value 为节点对象
     */
    public Map<String ,V> getTreeMap(String i_SuperNodeID)
    {
        Map<String ,V> v_Tree         = new LinkedHashMap<String ,V>();
        List<String>   v_ChildNodeIDs = this.childNodeIDs.get(i_SuperNodeID);
        
        if ( !Help.isNull(v_ChildNodeIDs) )
        {
            for (String v_ChildNodeID : v_ChildNodeIDs)
            {
                List<V> v_ChildNodes = this.get(v_ChildNodeID);
                if ( !Help.isNull(v_ChildNodes) )
                {
                    for (V v_ChildNode : v_ChildNodes)
                    {
                        if ( i_SuperNodeID.equals(v_ChildNode.gatTreeObjectSuperID()) )
                        {
                            v_Tree.put(v_ChildNodeID ,v_ChildNode);
                        }
                    }
                }
            }
        }
        
        return v_Tree;
    }
    
    
    
    /**
     * 获取某节点，及子子节点信息
     *
     * @author      ZhengWei(HY)
     * @createDate  2020-04-22
     * @version     v1.0
     *
     * @param i_NodeID
     * @return
     */
    @Override
    public List<V> get(Object i_NodeID)
    {
        return super.get(i_NodeID);
    }
    
    
    
    /**
     * 添加树节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-22
     * @version     v1.0
     *
     * @param i_Node  树节点数据
     * @return
     */
    public V put(V i_Node)
    {
        return this.put(i_Node.gatTreeObjectNodeID() ,i_Node.gatTreeObjectSuperID() ,i_Node);
    }
    
    
    
    /**
     * 批量添加树的节点数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2020-04-22
     * @version     v1.0
     *
     * @param i_Nodes
     */
    public void putAll(List<V> i_Nodes)
    {
        if ( Help.isNull(i_Nodes) )
        {
            return;
        }
        
        for (V v_Node : i_Nodes)
        {
            if ( v_Node != null )
            {
                this.put(v_Node);
            }
        }
    }
    
    
    
    /**
     * 添加树节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @param i_NodeID   树节点ID。不可为空或空指针。相同i_NodeID节点将覆盖添加，其子节点也将被级联删除。
     * @param i_SuperID  父节点ID。可为空，但空并不能被判定为顶级根节点。当任何节点均不引用它时，i_SuperID即为顶级根节点
     * @param i_Node     树节点数据
     * @return
     */
    private synchronized V put(String i_NodeID ,String i_SuperID ,V i_Node)
    {
        if ( Help.isNull(i_NodeID) )
        {
            return null;
        }
        
        V       v_Ret      = null;
        V       v_SameNode = null;
        V       v_Node     = i_Node;
        List<V> v_Nodes    = this.get(i_NodeID);
        String  v_SuperID  = Help.NVL(i_SuperID);
        
        // 防止节点递归引用
        if ( this.isReplaceMode && !Help.isNull(v_Nodes) )
        {
            int v_Index = -1;
            for (V v_NodeItem : v_Nodes)
            {
                v_Index++;
                if ( v_NodeItem.gatTreeObjectSuperID().equals(i_SuperID) )
                {
                    List<V> v_Childs = v_NodeItem.gatTreeObjectChilds();
                    if ( !Help.isNull(v_Childs) )
                    {
                        for (V v_Child : v_Childs)
                        {
                            v_Node.addTreeObjectChild(v_Child);
                        }
                    }
                    
                    // 相同时，替换和覆盖
                    super.removeRow(i_NodeID ,v_Index);
                    this.superNodeIDs.removeRow(i_NodeID  ,i_SuperID);
                    this.childNodeIDs.removeRow(v_SuperID ,i_NodeID);
                    
                    v_SameNode = v_NodeItem;
                    break;
                }
            }
            
            // 支持同节点归属多个不同的父节点
            // Nothing.
        }
        
        
        List<V> v_Supers = this.get(v_SuperID);
        if ( !Help.isNull(v_Supers) )
        {
            for (V v_Super : v_Supers)
            {
                if ( v_SameNode != null )
                {
                    // 相同时，替换和覆盖
                    v_Super.delTreeObjectChild(v_SameNode);
                }
                v_Super.addTreeObjectChild(v_Node);
            }
        }
        
        v_Ret = super.putRow(i_NodeID ,v_Node);
        this.superNodeIDs.putRow(i_NodeID  ,v_SuperID);
        this.childNodeIDs.putRow(v_SuperID ,i_NodeID);
        this.calcRootIDs(i_NodeID ,v_SuperID ,v_Node);
        
        return v_Ret;
    }
    
    
    
    /**
     * 计算顶级根节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *              v2.0  2023-05-05  修正：未删除无效的顶级节点
     *
     * @param i_NodeID   树节点ID。不可为空或空指针。相同i_NodeID节点将覆盖添加，其子节点也将被级联删除。
     * @param i_SuperID  父节点ID。可为空，但空并不能被判定为顶级根节点。当任何节点均不引用它时，i_SuperID即为顶级根节点
     * @param i_Node     树节点数据
     */
    private void calcRootIDs(String i_NodeID ,String i_SuperID ,V i_Node)
    {
        if ( !this.superNodeIDs.containsKey(i_SuperID) )
        {
            this.rootNodeIDs.put(i_NodeID ,i_NodeID);
        }
        else
        {
            this.rootNodeIDs.remove(i_NodeID);
        }
        
        // 处理：先put子节点，后put父节点的情况
        List<String> v_ChildNodeIDs = this.childNodeIDs.get(i_NodeID);
        if ( !Help.isNull(v_ChildNodeIDs) )
        {
            for (String i_ChildNodeID : v_ChildNodeIDs)
            {
                if ( this.rootNodeIDs.containsKey(i_ChildNodeID) )
                {
                    this.rootNodeIDs.remove(i_ChildNodeID);
                    List<V> v_ChildNodes = this.get(i_ChildNodeID);
                    if ( !Help.isNull(v_ChildNodes) )
                    {
                        for (V v_ChildNode : v_ChildNodes)
                        {
                            i_Node.addTreeObjectChild(v_ChildNode);
                        }
                    }
                }
            }
        }
    }
    
    
    
    /**
     * 删除树节点，同时级联删除所有子节点
     *
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
     *
     * @param i_NodeID  节点ID
     * @return
     */
    @Override
    public synchronized List<V> remove(Object i_NodeID)
    {
        List<V> v_Removes = null;
        
        if ( i_NodeID != null )
        {
            v_Removes = super.remove(i_NodeID);
            if ( !Help.isNull(v_Removes) )
            {
                for (V v_Remove : v_Removes)
                {
                    this.superNodeIDs.remove(v_Remove.gatTreeObjectNodeID());
                    this.rootNodeIDs .remove(i_NodeID);
                    
                    List<String> v_ChildNodeIDs = this.childNodeIDs.get(v_Remove.gatTreeObjectSuperID());
                    if ( !Help.isNull(v_ChildNodeIDs) )
                    {
                        v_ChildNodeIDs.remove(i_NodeID);
                    }
                   
                    List<? extends TreeObjectNode<V>> v_Childs = v_Remove.gatTreeObjectChilds();
                    if ( !Help.isNull(v_Childs) )
                    {
                        for (TreeObjectNode<V> v_ChildNode : v_Childs)
                        {
                            // 不判定是否删除成功
                            this.remove(v_ChildNode.gatTreeObjectNodeID());
                        }
                    }
                }
            }
        }
        
        return v_Removes;
    }
    
    
    
    /**
     * 计算树中所有节点的树结构层次。
     * 
     * 可以整个树的数据都灌入本类完成后，再执行本方法计算结构层次
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     */
    public boolean calcLevels()
    {
        return this.calcLevels(1);
    }
    
    
    
    /**
     * 计算树中所有节点的树结构层次。
     * 
     * 可以整个树的数据都灌入本类完成后，再执行本方法计算结构层次
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-12-06
     * @version     v1.0
     *
     * @param i_RootLevel  设定顶级节点的层次数值
     */
    public boolean calcLevels(int i_RootLevel)
    {
        try
        {
            if ( !Help.isNull(this.rootNodeIDs) )
            {
                for (String v_RootNodeID : this.rootNodeIDs.keySet())
                {
                    this.calcLevels(this.get(v_RootNodeID) ,i_RootLevel ,null);
                }
            }
            
            return true;
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return false;
    }
    
    
    
    private void calcLevels(final List<V> i_Nodes ,final int i_NodeLevel ,final String i_SuperNodeID)
    {
        if ( Help.isNull(i_Nodes) )
        {
            return;
        }
        
        V v_Node = null;
        if ( i_SuperNodeID == null )
        {
            v_Node = i_Nodes.get(0);
        }
        else
        {
            for (V v_NodeItem : i_Nodes)
            {
                if ( i_SuperNodeID.equals(v_NodeItem.gatTreeObjectSuperID()) )
                {
                    v_Node = v_NodeItem;
                }
            }
        }
        
        if ( v_Node == null )
        {
            return;
        }
        
        v_Node.satLevel(i_NodeLevel);
        
        List<String> v_ChildNodeIDs = this.childNodeIDs.get(v_Node.gatTreeObjectNodeID());
        if ( !Help.isNull(v_ChildNodeIDs) )
        {
            for (String v_ChildNodeID : v_ChildNodeIDs)
            {
                this.calcLevels(this.get(v_ChildNodeID) ,i_NodeLevel + 1 ,v_Node.gatTreeObjectNodeID());
            }
        }
    }
    
    
    
    @Override
    public void clear()
    {
        this.superNodeIDs.clear();
        this.childNodeIDs.clear();
        this.rootNodeIDs .clear();
        super.clear();
    }
    
    
    @Deprecated
    @Override
    public synchronized V putRow(String i_Partition ,V i_Row)
    {
        /** 不允许使用些方法 */
        return null;
    }


    @Deprecated
    @Override
    public List<V> put(String i_Partition ,List<V> i_RowList)
    {
        /** 不允许使用些方法 */
        return null;
    }


    @Deprecated
    @Override
    public synchronized List<V> putRows(String i_Partition ,List<V> i_RowList)
    {
        /** 不允许使用些方法 */
        return null;
    }


    @Deprecated
    @Override
    public synchronized List<V> putRows(String i_Partition ,V [] i_Rows)
    {
        /** 不允许使用些方法 */
        return null;
    }


    @Deprecated
    @Override
    public synchronized void putRows(Map<String ,V> i_PartitionRows)
    {
        /** 不允许使用些方法 */
    }


    @Deprecated
    @Override
    public synchronized List<V> putRows(String i_Partition ,V i_Row)
    {
        /** 不允许使用些方法 */
        return null;
    }


    @Deprecated
    @Override
    public synchronized V removeRow(String i_Partition ,int i_RowNo)
    {
        /** 不允许使用些方法 */
        return null;
    }


    @Deprecated
    @Override
    public synchronized V removeRow(String i_Partition ,V i_Row)
    {
        /** 不允许使用些方法 */
        return null;
    }
    
}
