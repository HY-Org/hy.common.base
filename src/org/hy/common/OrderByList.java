package org.hy.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





/**
 * 排序集合。
 * 
 * <K>  表示惟一标记的类型
 * <E>  表示元素的附属信息。这个属性提供给实际使用者使用的类型
 * 
 * 当元素排序序号动态变化时，能实时体显排序变化。
 * 
 * 并且，List与Map的融合体。即有按 "排序顺序"找值的功能，又有按关键字找值的功能。
 * 
 * 可分两种更新类型
 *    1. 读快，写慢。即每次添加、删除后，立即更新排序
 *    2. 读慢，写快。即每次添加、删除后，并不更新排序，而是在每次读取前更新排序。
 *    
 * 可分两种排序规则
 *    1. 升序
 *    2. 降序
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2012-10-22
 */
public class OrderByList<K ,E> extends ArrayList<OrderByElement<K ,E>> implements List<OrderByElement<K ,E>>
{
	
	private static final long serialVersionUID = 777444952906646040L;
	
	/** 更新排序类型。读快，写慢。即每次添加、删除后，立即更新排序 */
	public final static int   $RefreshType_Read  = 1;
	
	/** 更新排序类型。读慢，写快。即每次添加、删除后，并不更新排序，而是在每次读取前更新排序。 */
	public final static int   $RefreshType_Write = -1;
	
	/** 排序类型。升序 */
	public final static int   $OrderType_Asc     = 1;
	
	/** 排序类型。降序 */
	public final static int   $OrderType_Desc    = -1;
	
	
	
	/** 按关键存储的排序数据。主要作用读取 */
	private Map<K ,OrderByElement<K ,E>> values;
	
	/** 
	 * 更新排序类型。默认为 1 。 
	 * 
	 *  1  表示：读快，写慢。即每次添加、删除后，立即更新排序
	 * -1  表示：读慢，写快。即每次添加、删除后，并不更新排序，而是在每次读取前更新排序。
	 */
	private int                       refreshType;
	
	/**
	 * 排序类型。默认为 1 。 
	 * 
	 *  1  表示：升序
	 * -1  表示：降序
	 */
	private int                       orderByType;
	
	
	
	public OrderByList()
	{
		super();
		
		this.refreshType = 1;
		this.orderByType = 1;
		this.values      = new Hashtable<K ,OrderByElement<K ,E>>();
	}
	
	
	
	public OrderByList(int i_IinitialCapacity)
	{
		super(i_IinitialCapacity);
		
		this.refreshType = 1;
		this.orderByType = 1;
		this.values      = new Hashtable<K ,OrderByElement<K ,E>>();
	}
	
	
	
	/**
	 * 验证关键字所指元素是否存在
	 * 
	 * @param i_Key
	 * @return
	 */
	public boolean containsKey(K i_Key)
	{
		return this.values.containsKey(i_Key);
	}
	
	
	
	/**
	 * 添加排序数据
	 * 
	 * @param i_K
	 * @param i_E
	 */
	public boolean add(K i_K ,E i_E)
	{
		return add(new OrderByElement<K ,E>(i_K ,i_E));
	}
	
	
	
	/**
	 * 添加排序数据
	 * 
	 * @param i_K
	 * @param i_E
	 */
	public void add(int i_OrderByID ,K i_K ,E i_E)
	{
		add(i_OrderByID ,new OrderByElement<K ,E>(i_K ,i_E));
	}
	
	
	
	/**
	 * 添加排序数据
	 * 
	 * @param io_OrderByElement
	 */
	public synchronized boolean add(OrderByElement<K ,E> io_OrderByElement)
	{
		if ( io_OrderByElement == null )
		{
			throw new NullPointerException("Put OrderByElement is null.");
		}
		
		if ( io_OrderByElement.getKey() == null )
		{
			throw new NullPointerException("Put OrderByElement.key is null.");
		}
		
		if ( this.values.containsKey(io_OrderByElement) )
		{
			throw new SecurityException("OrderByElement.key is only.");
		}
		else
		{
			this.values.put(io_OrderByElement.getKey() ,io_OrderByElement);
			super.add(io_OrderByElement);
			
			io_OrderByElement.setOrderByList(this);
			
			if ( this.refreshType >= 1 )
			{
				this.fireChange();
			}
			
			return true;
		}
	}
	
	
	
	/**
	 * 添加排序数据
	 * 
	 * @param i_OrderByIndex
	 * @param i_OrderByElement
	 */
	public void add(int i_OrderByIndex, OrderByElement<K ,E> io_OrderByElement)
	{
		io_OrderByElement.setOrderBy(i_OrderByIndex);
		add(io_OrderByElement);
	}
	
	
	
	/**
	 * 添加排序数据
	 * 
	 * @param i_OrderByElement
	 */
	@SuppressWarnings("unchecked")
	public synchronized boolean addAll(Collection<? extends OrderByElement<K ,E>> i_Collection) 
	{
		Iterator<OrderByElement<K ,E>> v_Iterator = (Iterator<OrderByElement<K ,E>>) i_Collection.iterator();
		
		while ( v_Iterator.hasNext() )
		{
			this.add(v_Iterator.next());
		}
		
		return true;
	}
	
	
	
	/**
	 * 添加排序数据
	 * 
	 * 不支持定点添加
	 * 
	 * @param i_OrderByElement
	 */
	public synchronized boolean addAll(int i_Index ,Collection<? extends OrderByElement<K ,E>> i_Collection)
	{
		return this.addAll(i_Collection);
	}
	
	
	
	/**
	 * 不支持定点设置
	 * 
	 * 此方法只能调用super.set()方法实现，否则 Collections.sort() 时，会出现 java.lang.StackOverflowError 异常
	 */
	public OrderByElement<K ,E> set(int i_Index ,OrderByElement<K ,E> i_OrderByElement)
	{
		return super.set(i_Index ,i_OrderByElement);
	}
	
	
	
	/**
	 * 删除排序数据
	 */
	public synchronized boolean remove(Object i_OrderByElement)
	{
		if ( i_OrderByElement == null )
		{
			return false;
		}
		else if ( i_OrderByElement instanceof OrderByElement )
		{
			boolean v_Ret = super.remove(i_OrderByElement);
			
			if ( v_Ret )
			{
				this.values.remove(i_OrderByElement);
				
				if ( this.refreshType >= 1 )
				{
					this.fireChange();
				}
				
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	
	
	/**
	 * 删除排序数据
	 */
	public synchronized OrderByElement<K ,E> remove(int i_Index)
	{
		OrderByElement<K ,E> v_Ret = super.remove(i_Index);
		
		if ( v_Ret != null )
		{
			this.values.remove(v_Ret.getKey());
			
			if ( this.refreshType >= 1 )
			{
				this.fireChange();
			}
			
			return v_Ret;
		}
		else
		{
			return null;
		}
	}
	
	
	
	/**
	 * 删除所有排序数据
	 */
	public synchronized boolean removeAll(Collection<?> i_Collection)
	{
		boolean                     v_Modified = false;
		Iterator<OrderByElement<K ,E>> v_Iterator = iterator();
		
		while ( v_Iterator.hasNext() ) 
		{
			OrderByElement<K ,E> v_OrderByElement = v_Iterator.next();
		    if ( i_Collection.contains(v_OrderByElement) ) 
		    {
		    	v_Iterator.remove();
		    	this.values.remove(v_OrderByElement);
		    	
		    	if ( this.refreshType >= 1 )
				{
					this.fireChange();
				}
		    	
		    	v_Modified = true;
		    }
		}
		
		return v_Modified;
	}
	
	
	
	/**
	 * 删除排序数据
	 * 
	 * @param i_Key
	 * @return
	 */
	public synchronized OrderByElement<K ,E> removeByKey(K i_Key)
	{
		if ( i_Key == null )
		{
			throw new NullPointerException("Remove Key is null.");
		}
		else if ( !this.values.containsKey(i_Key) )
		{
			throw new SecurityException("Remove Key is not exist.");
		}
		else
		{
			OrderByElement<K ,E> v_Ret = this.values.remove(i_Key);
			super.remove(v_Ret);
			
			if ( this.refreshType >= 1 )
			{
				this.fireChange();
			}
			
			return v_Ret;
		}
	}
	
	
	
	/**
	 * 清理所有排序数据
	 */
	public synchronized void clear() 
	{
		this.values.clear();
		super.clear();
	}
	
	
	
	/**
	 * 获取排序数据，按惟一标记Key
	 * 
	 * @param i_Key
	 * @return
	 */
	public OrderByElement<K ,E> getByKey(K i_Key)
	{
		if ( i_Key == null )
		{
			throw new NullPointerException("Get Key is null.");
		}
		else if ( !this.values.containsKey(i_Key) )
		{
			throw new SecurityException("Get Key is not exist.");
		}
		else
		{
			return this.values.get(i_Key);
		}
	}
	
	
	
	/**
	 * 获取排序数据，按排序顺序
	 * 
	 * 下标从 0 开始
	 * 
	 * @param i_Index
	 * @return
	 */
	public OrderByElement<K ,E> get(int i_Index)
	{
		if ( i_Index < 0 || i_Index >= this.size() )
		{
			return null;
		}
		
		return super.get(i_Index);
	}
	
	
	
	/**
	 * 获取元素的排序顺序
	 * 
	 * 下标从 0 开始
	 * 
	 * 返回 -1 表示元素不存在。
	 * 
	 * @param i_OrderByElement
	 * @return
	 */
	public int indexOf(Object i_OrderByElement)
	{
		if ( i_OrderByElement == null )
		{
			return -1;
		}
		else if ( i_OrderByElement instanceof OrderByElement )
		{
			return super.indexOf(i_OrderByElement);
		}
		else
		{
			return -1;
		}
	}
	
	
	
	/** 
	 * 获取排序后的排序数据
	 */
	public Iterator<OrderByElement<K ,E>> iterator()
	{
		if ( this.refreshType < 1 )
		{
			this.fireChange();
		}
		
		return super.iterator();
	}
	
	
	
	/** 
	 * 获取排序后的排序数据
	 */
	public Object [] toArray()
	{
		if ( this.refreshType < 1 )
		{
			this.fireChange();
		}
		
		return super.toArray();
	}
	
	
	
	/** 
	 * 获取排序后的排序数据
	 */
	public List<OrderByElement<K ,E>> subList(int i_FromIndex ,int i_ToIndex)
	{
		if ( this.refreshType < 1 )
		{
			this.fireChange();
		}
		
		return super.subList(i_FromIndex ,i_ToIndex);
	}
	
	
	
	/**
	 * 触发数据集合排序动作
	 * 
	 * 注：在OrderByElement中与有调用
	 */
	public synchronized void fireChange()
	{
		if ( this.size() >= 2 )
		{
			Collections.sort(this);
		}
	}
	
	
	
	/** 
	 * 更新排序类型。默认为 1 。 
	 * 
	 *  1  表示：读快，写慢。即每次添加、删除后，立即更新排序
	 * -1  表示：读慢，写快。即每次添加、删除后，并不更新排序，而是在每次读取前更新排序。
	 */
	public int getRefreshType() 
	{
		return this.refreshType;
	}
	
	
	
	/** 
	 * 更新排序类型。默认为 1 。
	 * 
	 *  1  表示：读快，写慢。即每次添加、删除后，立即更新排序
	 * -1  表示：读慢，写快。即每次添加、删除后，并不更新排序，而是在每次读取前更新排序。
	 */
	public void setRefreshType(int i_RefreshType) 
	{
		this.refreshType = i_RefreshType;
	}
	
	
	
	/**
	 * 排序类型。默认为 1 。 
	 * 
	 *  1  表示：升序
	 * -1  表示：降序
	 */
	public int getOrderByType() 
	{
		return this.orderByType;
	}
	
	
	
	/**
	 * 排序类型。默认为 1 。 
	 * 
	 *  1  表示：升序
	 * -1  表示：降序
	 */
	public void setOrderByType(int i_OrderByType) 
	{
		if ( i_OrderByType >= 1 && this.orderByType >= 1 )
		{
			this.orderByType = i_OrderByType;
		}
		else if ( i_OrderByType < 1 && this.orderByType < 1 )
		{
			this.orderByType = i_OrderByType;
		}
		else
		{
			this.orderByType = i_OrderByType;
			
			this.fireChange();
		}
	}
	


    @Override
	public String toString() 
	{
        StringBuilder v_Buffer = new StringBuilder();
		
		for (int i=0; i<this.size() && i<10; i++)
		{
			v_Buffer.append(i).append("  ");
			v_Buffer.append(this.get(i).getOrderBy());
			v_Buffer.append("-");
			v_Buffer.append(this.get(i).getKey());
			v_Buffer.append("\n");
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
