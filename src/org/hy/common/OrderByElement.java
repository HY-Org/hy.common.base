package org.hy.common;





/**
 * 排序集合的集合元素
 * 
 * <K>  表示惟一标记的类型
 * <E>  表示元素的附属信息。这个属性提供给实际使用者使用的类型
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2012-10-22
 */
public class OrderByElement<K ,E> implements Comparable<OrderByElement<K ,E>> ,Cloneable
{

	/** 惟一标记。一经设置不再改变 */
	private K                 key;
	
	/** 排序序号 */
	private int               orderBy;
	
	/** 更新orderBy值的时间 */
	private Date              refreshTime;
	
	/** 
	 * 数据集合的引用。私有，不对子类和外部提供引用
	 * 
	 * 一经设置不再改变，为防止将同一元素向多个OrderByList集合中添加而造成的混乱
	 * 也为此才实现的 clone() 方法。
	 * 在克隆时，克隆复品 orderByList 设置为 null；
	 *         克隆复品 element     不被克隆，而是引用传递。
	 */
	private OrderByList<K ,E> orderByList;
	
	/** 元素的附属信息。这个属性提供给实际使用者使用 */
	private E                 element;
	
	
	
	public OrderByElement()
	{
		this(null ,null);
	}
	
	
	
	public OrderByElement(E i_Element)
	{
		this(null ,i_Element);
	}
	
	
	
	public OrderByElement(K i_Key ,E i_Element)
	{
		this.orderBy     = 0;
		this.refreshTime = new Date();
		this.orderByList = null;
		this.element     = i_Element;
		
		this.setKey(i_Key);
	}
	
	
	
	/**
	 * 设置惟一标记。一经设置不再改变
	 * 
	 * 不可为null
	 * 
	 * @param i_Key
	 */
	public void setKey(K i_Key)
	{
		if ( i_Key == null )
		{
			return;
		}
		
		if ( this.key == null )
		{
			this.key = i_Key;
		}
	}
	
	
	
	/** 获取惟一标记。 */
	public K getKey()
	{
		return this.key;
	}
	
	
	
	/**
	 * 获取排序序号
	 * 
	 * @return
	 */
	public synchronized int getOrderBy() 
	{
		return orderBy;
	}

	
	
	/**
	 * 更新排序序号
	 * 
	 * 会触发数据集合排序动作。
	 * 
	 * @param orderBy
	 */
	public synchronized void setOrderBy(int i_OrderBy) 
	{
		if ( this.orderBy != i_OrderBy )
		{
			this.orderBy     = i_OrderBy;
			this.refreshTime = new Date();
			
			if ( this.orderByList != null )
			{
				this.orderByList.fireChange();
			}
		}
	}
	
	
	
	/**
	 * 更新排序序号。加加 或 减减
	 * 
	 * 会触发数据集合排序动作。
	 * 
	 * @param i_Offset  偏移量
	 */
	public synchronized void orderBy(int i_Offset)
	{
		this.orderBy     = this.orderBy + i_Offset;
		this.refreshTime = new Date();
		
		if ( this.orderByList != null )
		{
			this.orderByList.fireChange();
		}
	}
	
	
	
	/**
	 * 更新orderBy值的时间
	 * 
	 * @return
	 */
	public Date getRefreshTime()
	{
		return this.refreshTime;
	}
	
	
	
	/** 
	 * 数据集合的引用。私有，不对子类和外部提供引用
	 * 
	 * 一经设置不再改变，为防止将同一元素向多个OrderByList集合中添加而造成的混乱
	 * 也为此才实现的 clone() 方法。
	 * 在克隆时，克隆复品 orderByList 设置为 null；
	 *         克隆复品 element 不被克隆，而是引用。
	 *
	 * 由 OrderByList.add() 方法及相关方法的调用。
	 * 
	 * @param orderByList
	 */
	public void setOrderByList(OrderByList<K ,E> orderByList) 
	{
		if ( this.orderByList == null )
		{
			this.orderByList = orderByList;
		}
	}
	
	
	
	/**
	 * 元素的附属信息。这个属性提供给实际使用者使用
	 * 
	 * @return
	 */
	public E getElement() 
	{
		return element;
	}
	
	
	
	/**
	 * 元素的附属信息。这个属性提供给实际使用者使用
	 * 
	 * @param element
	 */
	public void setElement(E element) 
	{
		this.element = element;
	}



	/**
	 * 比较规则：先比较 orderBy ，后比较 refreshTime
	 */
	public int compareTo(OrderByElement<K ,E> i_Other)
	{
		if ( this.orderByList != null )
		{
			// 升序
			if ( this.orderByList.getOrderByType() >= 1 )
			{
				return compareOfASC(i_Other);
			}
			// 降序
			else
			{
				return compareOfASC(i_Other) * -1;
			}
		}
		else
		{
			return compareOfASC(i_Other);
		}
	}
	
	
	
	/**
	 * 比较规则：先比较 orderBy ，后比较 refreshTime
	 * 
	 * 升序比较
	 */
	private int compareOfASC(OrderByElement<K ,E> i_Other)
	{
		if ( i_Other == null )
		{
			return 1;
		}
		else
		{
			if ( this == i_Other )
			{
				return 0;
			}
			else if ( this.orderBy > i_Other.getOrderBy() )
			{
				return 1;
			}
			else if ( this.orderBy < i_Other.getOrderBy() )
			{
				return -1;
			}
			else
			{
				return this.refreshTime.compareTo(i_Other.getRefreshTime());
			}
		}
	}
	
	
	
	@Override
    public int hashCode()
    {
        return this.key.hashCode();
    }

	

    /**
	 * 比较规则：比较 key 值。如果 key 为空，永为不相等
	 */
	@Override
	public boolean equals(Object i_Other) 
	{
		if ( i_Other == null )
		{
			return false;
		}
		else if ( this == i_Other )
		{
			return true;
		}
		else if ( i_Other instanceof OrderByElement )
		{
			OrderByElement<? ,?> v_Obj = (OrderByElement<? ,?>)i_Other;
			
			if ( v_Obj.getKey() == null )
			{
				return false;
			}
			else if ( this.key == null )
			{
				return false;
			}
			else
			{
				return this.key.equals(v_Obj.getKey());
			}
		}
		else
		{
			return false;
		}
	}
	
	
	
	@Override
	protected OrderByElement<K ,E> clone()
	{
		OrderByElement<K ,E> v_Clone = new OrderByElement<K ,E>(this.key ,this.element);
		
		v_Clone.orderBy     = this.orderBy;
		v_Clone.refreshTime = new Date(this.refreshTime);
		
		return v_Clone;
	}
	
	
	
    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
	protected void finalize() throws Throwable
	{
		this.orderByList = null;
		this.refreshTime = null;
		
		super.finalize();
	}
	*/
	
}
