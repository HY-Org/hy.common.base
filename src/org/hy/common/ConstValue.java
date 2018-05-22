package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 通用常量值的父类
 * 
 * 同一常量类型下的所有常量值都是惟一的。
 * 
 * 不同常量类型下的常量值是可以相同的，但这毫不影响对比。
 * 
 * 此类可以对字符串类型常量进行最高效的对比。
 * 
 * 并且可以统一的管理所有常量信息。
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2011-11-17
 */
public class ConstValue implements Comparable<ConstValue> ,Cloneable ,java.io.Serializable
{
    private static final long serialVersionUID = -7634825538489687236L;
    
    

    /** 常量的常量类型的常量类型。即最高级的常量类型，此常量类型也是唯一不存入$AllConst中的实例 */
	public final static ConstValue $ConstType_Type$ = new ConstValue("$ConstType_Type$" ,true);
	
	/**
	 * Map.Key         : 为常量类型。这里十分微妙，它的类型也是一个常量对象ConstValue的实现。
	 * Map.Value       : 为 ConstValue 类型下的所有常量
	 * Map.Value.Key   : 为 ConstValue.id
	 * Map.Value.Value : 为 ConstValue 的实例
	 */
	private static Map<ConstValue ,Map<Integer ,ConstValue>> $AllConst = null;

	/** 序列号。用于自动生成ID */
	private static int                                       $SerialNo = 0;
	
	
	
	/** 常量类型 */
	private ConstValue constType; 
	
	/** 值 */
	private int        id;
	
	/** 名称 */
	private String     name;
	
	
	
	/**
	 * 获取惟一的ID
	 * 
	 * @return
	 */
	protected synchronized static int getSerialNo()
	{
		return $SerialNo++;
	}
	
	
	
	/**
	 * 按常量类型与常量值，获取任何一个常量对象
	 * 
	 * @param i_ConstType  常量类型
	 * @param i_ID         常量值
	 * @return             异常或不存在返回 null
	 */
	public static ConstValue getConstValue(ConstValue i_ConstType ,int i_ID)
	{
		if ( i_ConstType == null )
		{
			return null;
		}
		
		
		Integer v_ID = Integer.valueOf(i_ID);
		
		if ( $AllConst.containsKey(i_ConstType) )
		{
			if ( $AllConst.get(i_ConstType).containsKey(v_ID) )
			{
				return $AllConst.get(i_ConstType).get(v_ID);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	
	
	/**
	 * 获取常量类型下的所有常量对象
	 * 
	 * @param i_ConstType  常量类型
	 * @return
	 */
	public static Map<Integer ,ConstValue> getConsts(ConstValue i_ConstType)
	{
		if ( i_ConstType == null || $AllConst == null )
		{
			return new Hashtable<Integer ,ConstValue>();
		}
		else
		{
			if ( !$AllConst.containsKey(i_ConstType) )
			{
				return new Hashtable<Integer ,ConstValue>();
			}
			else
			{
				return $AllConst.get(i_ConstType);
			}
		}
	}
	
	
	
	/**
	 * 获取常量类型下的所有常量对象
	 * 
	 * @param i_ConstTypeName  常量类型名称
	 * @return
	 */
	public static Map<Integer ,ConstValue> getConsts(String i_ConstTypeName)
	{
		ConstValue v_TempConst = getConstType(i_ConstTypeName);
		
		if ( v_TempConst == null )
		{
			return new Hashtable<Integer ,ConstValue>();
		}
		else
		{
			return $AllConst.get(v_TempConst);
		}
	}
	
	
	
	/**
	 * 获取常量类型
	 * 
	 * @param i_ConstTypeName  常量类型名称
	 * @return
	 */
	public static ConstValue getConstType(String i_ConstTypeName)
	{
		if ( i_ConstTypeName == null || $AllConst == null )
		{
			return null;
		}
		
		Iterator<ConstValue> v_Iterator = $AllConst.get($ConstType_Type$).values().iterator();
		
		for ( ; v_Iterator.hasNext(); )
		{
			ConstValue v_TempConst = v_Iterator.next();
			
			if ( i_ConstTypeName.equals(v_TempConst.getName()) )
			{
				return v_TempConst;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * 创建常量类型的实例
	 * 
	 * @param i_ConstTypeName
	 * @return
	 */
	public synchronized static ConstValue newConstType(String i_ConstTypeName)
	{
		if (Help.isNull(i_ConstTypeName))
		{
			throw new NullPointerException("名称为空!");
		}
		
		return new ConstValue(i_ConstTypeName ,false);
	}
	
	
	
	/**
	 * 构造器
	 * 
	 * @param i_ConstType  常量类型
	 * @param i_Name       常量名称或描述
	 */
	public ConstValue(ConstValue i_ConstType ,String i_Name)
	{
		this(i_ConstType ,getSerialNo() ,i_Name);
	}
	
	
	/**
	 * 构造器
	 * 
	 * @param i_ConstType  常量类型
	 * @param i_ID         常量ID
	 * @param i_Name       常量名称或描述
	 */
	public ConstValue(ConstValue i_ConstType ,int i_ID ,String i_Name)
	{
		if ( i_ConstType == null )
		{
			throw new NullPointerException("常量类型为空!");
		}
		
		if (Help.isNull(i_Name))
		{
			throw new NullPointerException("名称为空!");
		}
		
		
		this.init(i_ConstType, i_ID, i_Name);
	}
	
	
	/**
	 * 构造器(私有的)
	 * 
	 * 专用于构造"常量类型"的常量
	 * 
	 * @param i_Name              常量类型名称
	 * @param i_IsConstType_Type  是否为常量的常量类型的常量类型
	 */
	protected ConstValue(String i_ConstTypeName ,boolean i_IsConstType_Type)
	{
		if ( i_IsConstType_Type )
		{
			this.constType = null;
			this.id        = getSerialNo();
			this.name      = i_ConstTypeName;
		}
		else
		{
			this.init($ConstType_Type$, getSerialNo(), i_ConstTypeName);
		}
	}
	
	
	/**
	 * 初始化方法
	 * 
	 * @param i_ConstType  常量类型
	 * @param i_ID         常量ID
	 * @param i_Name       常量名称或描述
	 */
	private synchronized void init(ConstValue i_ConstType ,int i_ID ,String i_Name)
	{
		this.constType = i_ConstType;
		this.id        = i_ID;
		this.name      = i_Name;
		
		
		if ( $AllConst == null )
		{
			$AllConst = new Hashtable<ConstValue ,Map<Integer ,ConstValue>>();
		}
		
		if ( $AllConst.containsKey(this.constType) )
		{
			Map<Integer ,ConstValue> v_SubConsts = $AllConst.get(this.constType);
			
			if ( v_SubConsts == null )
			{
				v_SubConsts = new Hashtable<Integer ,ConstValue>();
				
				v_SubConsts.put(Integer.valueOf(this.id), this);
			}
			else
			{
				Integer v_ID = Integer.valueOf(this.id);
				
				if ( v_SubConsts.containsKey(v_ID) )
				{
					throw new ExceptionInInitializerError("常量已存在");
				}
				else
				{
					v_SubConsts.put(v_ID, this);
				}
			}
		}
		else
		{
			Map<Integer ,ConstValue> v_SubConsts = new Hashtable<Integer ,ConstValue>();
			
			v_SubConsts.put(Integer.valueOf(this.id), this);
			
			$AllConst.put(this.constType , v_SubConsts);
		}	
	}
	
	
	/**
	 * 获取常量类型下的所有常量对象
	 * 
	 * @param i_ConstType  常量类型
	 * @return
	 */
	public Map<Integer ,ConstValue> getConsts()
	{
		return getConsts(this.constType);
	}
	
	
	/**
	 * 获取（实现类）常量类型下的所有常量的个数
	 * 
	 * @return
	 */
	public int getSize()
	{
		return this.getConsts().size();
	}
	
	
	/**
	 * 获取常量类型的对象实例
	 * 
	 * @return
	 */
	public ConstValue getConstType()
	{
		return this.constType;
	}
	
	
	/**
	 * 获取常量类型的名称
	 * 
	 * @return
	 */
	public String getConstTypeName()
	{
		return this.constType.getName();
	}
	
	
	/**
	 * 获取常量的值
	 * 
	 * @return
	 */
	public int getId() 
	{
		return id;
	}

	
	/**
	 * 获取常量的名称
	 * 
	 * @return
	 */
	public String getName() 
	{
		return name;
	}
	
	
	/**
	 * 按常量值，获取同一常量类型的其它常量对象
	 * 
	 * @param i_ID  常量值
	 * @return
	 */
	public ConstValue getOtherConst(int i_ID)
	{
		return getConstValue(this.constType ,i_ID);
	}
	
	
	/**
	 * 比较规则：按常量id比较
	 */
	public int compareTo(ConstValue o) 
	{
		if (o == null)
		{
			return 1;
		}
		else if (this == o)
		{
			return 0;
		}
		else if (o instanceof ConstValue)
		{
			ConstValue v_Another = (ConstValue)o;
			
			if ( this.constType.getId() == v_Another.getConstType().getId() )
			{
				return this.id - v_Another.getId();
			}
			else
			{
				return this.constType.getId() - v_Another.getConstType().getId();
			}
		}
		else
		{
			return 1;
		}
	}
	
	
	
	@Override
    public int hashCode()
    {
	    if ( this.constType == null )
	    {
	        return this.name.hashCode();
	    }
	    else
	    {
	        return this.constType.getId();
	    }
    }



    @Override
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
		else if (o instanceof ConstValue)
		{
			ConstValue v_Another = (ConstValue)o;
			
			if ( this.constType.getId() == v_Another.getConstType().getId()  )
			{
				return this.id == v_Another.getId();
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
	 * 克隆时，直接返回自己
	 */
	public Object clone() 
	{
		return this;
	}
	
	
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
}
