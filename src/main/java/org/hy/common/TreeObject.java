package org.hy.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;





/**
 * 构建树结构对象
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
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-04-21
 * @version     v1.0
 *              v2.0  2022-12-06  添加：获取父级对象 和 计算树结构层次
 * @param <V>
 */
public class TreeObject<V extends TreeObjectNode<V>> extends Hashtable<String ,V>
{

    private static final long serialVersionUID = 8613137547771634890L;
    
    /**
     * 树的节点ID与父节点ID的对应关系
     * 
     * Map.key    为节点ID
     * Map.value  为父节点ID
     */
    private HashMap<String ,String>        superNodeIDs;
    
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
    
    
    
    public TreeObject()
    {
        this("");
    }
    
    
    
    public TreeObject(int i_InitialCapacity)
    {
        this("" ,i_InitialCapacity);
    }
    
    
    
    public TreeObject(String i_Connectors)
    {
        super();
        
        this.superNodeIDs = new HashMap       <String ,String>();
        this.childNodeIDs = new TablePartition<String ,String>();
        this.rootNodeIDs  = new LinkedHashMap <String ,String>();
    }
    
    
    
    public TreeObject(String i_Connectors ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.superNodeIDs = new HashMap       <String ,String>();
        this.childNodeIDs = new TablePartition<String ,String>();
        this.rootNodeIDs  = new LinkedHashMap <String ,String>();
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
    public String getSuperNodeID(String i_NodeID)
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
    public V getSuper(String i_NodeID)
    {
        String v_SuperNodeID = this.getSuperNodeID(i_NodeID);
        
        if ( Help.isNull(v_SuperNodeID) )
        {
            return null;
        }
        else
        {
            return this.get(v_SuperNodeID);
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
                v_Tree.add(this.get(v_RootNodeID));
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
                v_Tree.add(this.get(v_ChildNodeID));
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
                v_Tree.put(v_RootNodeID ,this.get(v_RootNodeID));
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
                v_Tree.put(v_ChildNodeID ,this.get(v_ChildNodeID));
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
     *
     * @see java.util.Hashtable#get(java.lang.Object)
     */
    @Override
    public V get(Object i_NodeID)
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
     *
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> i_Nodes)
    {
        if ( Help.isNull(i_Nodes) )
        {
            return;
        }
        
        for (V v_Node : i_Nodes.values())
        {
            if ( v_Node != null )
            {
                this.put(v_Node);
            }
        }
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
        
        V v_Ret    = null;
        V v_MySelf = this.get(i_NodeID);
        
        // 防止节点递归引用
        if ( v_MySelf != null )
        {
            v_MySelf = this.remove(i_NodeID);
            if ( v_MySelf == null )
            {
                return v_Ret;
            }
        }
        
        String v_SuperID = Help.NVL(i_SuperID);
        V      v_Super   = this.get(v_SuperID);
        if ( v_Super != null )
        {
            v_Super.addTreeObjectChild(i_Node);
        }
        
        v_Ret = super.put(i_NodeID ,i_Node);
        this.superNodeIDs.put(i_NodeID ,v_SuperID);
        this.childNodeIDs.putRow(v_SuperID ,i_NodeID);
        this.calcRootIDs(i_NodeID ,v_SuperID ,i_Node);
        
        return v_Ret;
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
    public synchronized V remove(Object i_NodeID)
    {
        V v_Remove = null;
        
        if ( i_NodeID != null )
        {
            v_Remove = super.remove(i_NodeID);
            if ( v_Remove != null )
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
        
        return v_Remove;
    }
    
    
    
    /**
     * 计算顶级根节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-04-21
     * @version     v1.0
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
            
            // 处理：先put子节点，后put父节点的情况
            List<String> v_ChildNodeIDs = this.childNodeIDs.get(i_NodeID);
            if ( !Help.isNull(v_ChildNodeIDs) )
            {
                for (String i_ChildNodeID : v_ChildNodeIDs)
                {
                    if ( this.rootNodeIDs.containsKey(i_ChildNodeID) )
                    {
                        this.rootNodeIDs.remove(i_ChildNodeID);
                        V v_ChildNode = this.get(i_ChildNodeID);
                        i_Node.addTreeObjectChild(v_ChildNode);
                    }
                }
            }
        }
        else
        {
            this.rootNodeIDs.remove(i_NodeID);
        }
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
                    this.calcLevels(this.get(v_RootNodeID) ,i_RootLevel);
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
    
    
    
    private void calcLevels(final V i_Node ,final int i_NodeLevel)
    {
        if ( i_Node == null )
        {
            return;
        }
        
        i_Node.satLevel(i_NodeLevel);
        
        List<String> v_ChildNodeIDs = this.childNodeIDs.get(i_Node.gatTreeObjectNodeID());
        if ( !Help.isNull(v_ChildNodeIDs) )
        {
            for (String v_ChildNodeID : v_ChildNodeIDs)
            {
                this.calcLevels(this.get(v_ChildNodeID) ,i_NodeLevel + 1);
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
    public V put(String i_Key ,V i_Value)
    {
        /** 不允许使用些方法 */
        return null;
    }
    
}
