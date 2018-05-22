package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;





/**
 * 树结构的集合。
 * 
 * 与 TreeNode 类搭配一起使用。
 * 
 * <O> 为附属对象 TreeMap.TreeNode.info 的类型
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2012-10-08
 */
public class TreeMap<O> implements java.io.Serializable
{
	
    private static final long serialVersionUID = 8893165781470163765L;
    
    

    /** 
	 * 保存树结构信息
	 * Map.key   为 TreeNode.getOrderByID()
	 * Map.Value 为 TreeNode 本身
	 */
	private java.util.TreeMap<String ,TreeNode<O>>  treeMap;
	
	/** 
	 * 保存树节点 TreeNode.getNodeID() 值非空的集合信息
	 * 
	 * Map.key   为 TreeNode.getNodeID()
	 * Map.Value 为 TreeNode 本身
	 */
	private Map<String ,TreeNode<O>>                nodeIDMap;
	
	
	
	public TreeMap()
	{
		this.treeMap   = new java.util.TreeMap<String ,TreeNode<O>>();
		this.nodeIDMap = new Hashtable<String ,TreeNode<O>>();
	}
	
	
	
	/**
	 * 获取树结构节点的数量
	 * 
	 * @return
	 */
	public int size()
	{
		return this.treeMap.size();
	}
	
	
	
	/**
	 * 验证树目录的节点ID是否存在
	 * 
	 * @param i_NodeID
	 * @return
	 */
	public boolean containsNodeID(String i_NodeID)
	{
		return this.nodeIDMap.containsKey(i_NodeID);
	}
	
	
	
	/**
	 * 验证树目录的排序ID是否存在
	 * 
	 * @param i_OrderByID
	 * @return
	 */
	public boolean containsOrderByID(String i_OrderByID)
	{
		return this.treeMap.containsKey(i_OrderByID);
	}
	
	
	
	/**
	 * 构造树结构。即向树结构中添加节点。
	 * 
	 * 实现同步操作
	 * 
	 * @param i_NodeInfo
	 */
	public synchronized void put(TreeNode<O> i_NodeInfo)
	{
		if ( i_NodeInfo == null )
		{
			throw new NullPointerException("Tree node is null.");
		}
		
		this.treeMap.put(i_NodeInfo.getOrderByID() ,i_NodeInfo);
		
		if ( i_NodeInfo.getNodeID() != null )
		{
		    // ZhengWei(HY) Edit 2016-01-04 删除下的重复不put，改为重复覆盖。
		    //                              当节点的附属对象为空时，先用旧附属对象的实例
			if ( null == i_NodeInfo.getInfo() && this.nodeIDMap.containsKey(i_NodeInfo.getNodeID()) )
			{
			    i_NodeInfo.setInfo(this.nodeIDMap.get(i_NodeInfo.getNodeID()).getInfo());
			}
			
			this.nodeIDMap.put(i_NodeInfo.getNodeID() ,i_NodeInfo);
		}
	}
	
	
	
	/**
	 * 按排序OrderByID，删除节点信息
	 * 
	 * @param i_NodeInfo
	 * @return
	 */
	public synchronized TreeNode<O> remove(String i_OrderByID)
	{
		return this.remove(this.get(i_OrderByID));
	}
	
	
	
	/**
	 * 按节点NodeID，删除节点信息
	 * 
	 * @param i_NodeInfo
	 * @return
	 */
	public synchronized TreeNode<O> removeNodeID(String i_NodeID)
	{
		return this.remove(this.getByNodeID(i_NodeID));
	}
	
	
	
	/**
	 * 删除节点信息
	 * 
	 * @param i_NodeInfo
	 * @return
	 */
	public synchronized TreeNode<O> remove(TreeNode<O> i_NodeInfo)
	{
		if ( i_NodeInfo == null || i_NodeInfo.getOrderByID() == null )
		{
			return null;
		}
		else if ( this.treeMap.containsKey(i_NodeInfo.getOrderByID()) )
		{
			this.treeMap.remove(i_NodeInfo.getOrderByID());
			
			if ( i_NodeInfo.getNodeID() != null )
			{
				if ( this.nodeIDMap.containsKey(i_NodeInfo.getNodeID()) )
				{
					try
					{
						this.nodeIDMap.remove(i_NodeInfo.getNodeID());
					}
					catch (Exception exce)
					{
						// 如果删除异常，则重新put进集合
						this.treeMap.put(i_NodeInfo.getOrderByID() ,i_NodeInfo);
						return null;
					}
				}
			}
		}
		else
		{
			return null;
		}
		
		return i_NodeInfo;
	}
	
	
	
	/**
	 * 按树结构的排序ID获取对应的节点信息
	 * 
	 * @param i_OrderByID
	 * @return
	 */
	public TreeNode<O> get(String i_OrderByID)
	{
		if ( i_OrderByID == null )
		{
			return null;
		}
		else if ( this.treeMap.containsKey(i_OrderByID) )
		{
			return this.treeMap.get(i_OrderByID);
		}
		else
		{
			return null;
		}
	}
	
	
	
	/**
	 * 按树节点的NodeID获取对应的节点信息
	 * 
	 * @param i_NodeID
	 * @return
	 */
	public TreeNode<O> getByNodeID(String i_NodeID)
	{
		if ( i_NodeID == null )
		{
			return null;
		}
		else if ( this.nodeIDMap.containsKey(i_NodeID) )
		{
			return this.nodeIDMap.get(i_NodeID);
		}
		else
		{
			return null;
		}
	}
	
	
	
	/**
	 * 获取树目录的 orderByID 的迭代器
	 * 
	 * @return
	 */
	public Iterator<String> keySet()
	{
		return this.treeMap.keySet().iterator();
	}
	
	
	
	/**
	 * 获取树目录的 nodeID 的迭代器
	 * 
	 * @return
	 */
	public Iterator<String> keySetNodeID()
	{
		return this.nodeIDMap.keySet().iterator();
	}
	
	
	
	/**
	 * 获取树目录节点 TreeNode 的迭代器
	 * 
	 * @return
	 */
	public Iterator<TreeNode<O>> values()
	{
		return this.treeMap.values().iterator();
	}
	
	
	
	/**
	 * 获取树目录节点，相关有 nodeID 值的 TreeNode 的迭代器
	 * 
	 * @return
	 */
	public Iterator<TreeNode<O>> valuesNodeID()
	{
		return this.nodeIDMap.values().iterator();
	}
	
	
	
	/**
	 * 按树节点NodeID，获取对应节点的子树结构
	 * 
	 * @param i_SuperNode_NodeID
	 * @return
	 */
	public TreeMap<O> getChildTreeByNodeID(String i_SuperNode_NodeID)
	{
		return this.getChildTree(this.getByNodeID(i_SuperNode_NodeID));
	}
	
	
	
	/**
	 * 按树结构的排序ID，获取对应节点的子树结构
	 * 
	 * @param i_SuperNode_NodeID
	 * @return
	 */
	public TreeMap<O> getChildTree(String i_SuperNode_OrderByID)
	{
		return this.getChildTree(this.get(i_SuperNode_OrderByID));
	}
	
	
	
	/**
	 * 按树节点对象，获取对应节点的子树结构
	 * 
	 * @param i_SuperNode_NodeID
	 * @return
	 */
	public TreeMap<O> getChildTree(TreeNode<O> i_SuperNode)
	{
		if ( i_SuperNode == null )
		{
			throw new NullPointerException("Super node is null.");
		}
		
		TreeMap<O>                     v_Ret      = new TreeMap<O>();
		SortedMap<String ,TreeNode<O>> v_TailMap  = this.treeMap.tailMap(i_SuperNode.getOrderByID());
		Iterator<TreeNode<O>>          v_Iterator = v_TailMap.values().iterator();
		boolean                        v_IsGoOn   = true;
		while ( v_IsGoOn && v_Iterator.hasNext() )
		{
			TreeNode<O> v_NodeInfo = v_Iterator.next();
			
			if ( i_SuperNode.getLevel() < v_NodeInfo.getLevel() )
			{
				v_Ret.put(v_NodeInfo);
			}
			else
			{
				if ( v_Ret.size() >= 1 )
				{
					v_IsGoOn = false;
				}
			}
		}
		
		return v_Ret;
	}
	
	
	
	/**
	 * 清空树结构中的所有节点信息
	 */
	public void clear()
	{
		this.treeMap.clear();
		this.nodeIDMap.clear();
	}
	
	
	
	public String toString()
	{
	    StringBuilder         v_Buffer   = new StringBuilder();
		Iterator<TreeNode<O>> v_Iterator = this.treeMap.values().iterator();
		
		while ( v_Iterator.hasNext() )
		{
			TreeNode<O> v_TreeNode = v_Iterator.next();
			
			for (int v_Level=1; v_Level<=v_TreeNode.getLevel(); v_Level++)
			{
				
				if ( v_Level == v_TreeNode.getLevel() )
				{
					v_Buffer.append("|--- ");
				}
				else
				{
					v_Buffer.append("|    ");
				}
			}
			
			//v_Buffer.append(v_TreeNode.getOrderByID()).append(" ");
			v_Buffer.append("[").append(v_TreeNode.getNodeID()).append("] ");
			v_Buffer.append(v_TreeNode.getInfo().toString());
			v_Buffer.append(Help.getSysLineSeparator());
		}
		
		return v_Buffer.toString();
	}
	
	
	
	/*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable 
    {
        this.clear();
        
        super.finalize();
    }
    */
	
}
