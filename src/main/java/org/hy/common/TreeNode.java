package org.hy.common;





/**
 * 树结构的节点信息。
 * 
 * 与 TreeMap 类搭配一起使用。
 * 
 * <O> 为附属对象 this.info 的类型
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2012-10-09
 */
public class TreeNode<O> implements Comparable<O> ,java.io.Serializable
{
    private static final long serialVersionUID = 3800637276777810516L;
    
    

    /** 树目录的层级。下标从1开始 */
	private int         level;
	
	/** 
	 *  树目录的节点ID。此属性可以为空。
	 *  对于树的所有节点而言，此值当应是惟一的。
	 *  
	 *  当此属性非空时，将独立保存在 TreeMap.nodeIDMap 中。
	 */
	private String      nodeID;
	
	/** 树目录的排序ID。此属性为非空值 */
	private String      orderByID;
	
	/** 父节点的引用 */
	private TreeNode<O> superNode;
	
	/** 是否为叶子节点 */
	private boolean     isLeaf;
	
	/** 附属对象 */
	private O           info;
	
	
	
	
	public TreeNode(String i_OrderByID)
	{
		this(i_OrderByID ,null ,null ,null);
	}
	
	
	
	public TreeNode(String i_OrderByID ,String i_NodeID)
	{
		this(i_OrderByID ,i_NodeID ,null ,null);
	}
	
	
	
	public TreeNode(String i_OrderByID ,String i_NodeID ,TreeNode<O> io_SuperNode)
	{
		this(i_OrderByID ,i_NodeID ,io_SuperNode ,null);
	}
	
	
	
	public TreeNode(String i_OrderByID ,String i_NodeID ,TreeNode<O> io_SuperNode ,O i_Info)
	{
		if ( i_OrderByID == null || "".equals(i_OrderByID.trim()) )
		{
			throw new NullPointerException("Tree node orderby ID is null.");
		}
		
		if ( io_SuperNode == null )
		{
			this.level     = 1;
			this.nodeID    = i_NodeID;
			this.orderByID = i_OrderByID.trim();
			this.superNode = null;
			this.isLeaf    = true;
		}
		else
		{
			this.superNode = io_SuperNode;
			this.superNode.isLeaf = false;
			this.isLeaf    = true;
			this.nodeID    = i_NodeID;
			this.level     = this.superNode.getLevel() + 1;
			this.orderByID = i_OrderByID.trim();
			
			if ( !this.orderByID.startsWith(this.superNode.getOrderByID()) || this.orderByID.equals(this.superNode.getOrderByID()) )
			{
				this.orderByID = this.superNode.getOrderByID() + this.orderByID;
			}
		}
		
		this.info = i_Info;
	}
	
	
	
	/**
	 * 树目录的层级。下标从1开始
	 * 
	 * @return
	 */
	public int getLevel()
	{
		return this.level;
	}
	
	
	
	/** 
	 *  树目录的节点ID。此属性可以为空。
	 *  对于树的所有节点而言，此值当应是惟一的。
	 *  
	 *  当此属性非空时，将独立保存在 TreeMap.nodeIDMap 中。
	 *  
	 * @return
	 */
	public String getNodeID()
	{
		return this.nodeID;
	}
	
	
	
	/**
	 * 树目录的排序ID。此属性为非空值
	 * 
	 * @return
	 */
	public String getOrderByID() 
	{
		return this.orderByID;
	}
	
	
	
	/**
	 * 父节点的引用
	 * 
	 * @return
	 */
	public TreeNode<O> getSuper()
	{
		return this.superNode;
	}
	
	
	
	/**
	 * 是否为叶子节点
	 * 
	 * @return
	 */
	public boolean isLeaf()
	{
		return this.isLeaf;
	}
	
	
	
	/**
	 * 获取附属对象
	 * 
	 * @return
	 */
	public O getInfo()
	{
		return this.info;
	}
	
	
	
	/**
	 * 设置附属对象
	 * 
	 * @param i_Info
	 */
	public void setInfo(O i_Info)
	{
		this.info = i_Info;
	}
	
	
	
	
	/**
	 * 比较规则：按 this.orderByID 比较
	 */
	public int compareTo(Object o) 
	{
		if (o == null)
		{
			return 1;
		}
		else if (this == o)
		{
			return 0;
		}
		else if (o instanceof TreeNode)
		{
			TreeNode<?> v_Another = (TreeNode<?>)o;
			
			return this.orderByID.compareTo(v_Another.getOrderByID());
		}
		else
		{
			return 1;
		}
	}
	
	
	
	@Override
    public int hashCode()
    {
        return this.orderByID.hashCode();
    }



    /**
	 * 比较规则：按 this.orderByID 比较
	 */
	public boolean equals(Object o) 
	{
		if (o == null)
		{
			return false;
		}
		else if (this == o)
		{
			return true;
		}
		else if (o instanceof TreeNode)
		{
			TreeNode<?> v_Another = (TreeNode<?>)o;
			
			return this.orderByID.equals(v_Another.getOrderByID());
		}
		else
		{
			return false;
		}
	}
	
	
	
	@Override
	public String toString() 
	{
	    StringBuilder v_Buffer = new StringBuilder();
		
		v_Buffer.append("OrderByID=").append(this.orderByID);
		v_Buffer.append("   Level=") .append(this.level);
		v_Buffer.append("   IsLeaf=").append(this.isLeaf);
		
		if ( this.nodeID == null )
		{
			v_Buffer.append("   NodeID=NULL");
		}
		else
		{
			v_Buffer.append("   NodeID=").append(this.nodeID);
		}
		
		if ( this.info == null )
		{
			v_Buffer.append("   Info=NULL");
		}
		else
		{
			v_Buffer.append("   Info=").append(this.info.toString());
		}
		
		return v_Buffer.toString();
	}
	
}
