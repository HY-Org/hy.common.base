package org.hy.common;

import java.util.Hashtable;
import java.util.Map;





/**
 * 全局量
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2011-12-06
 */
public class Global
{
	/** 保存全局量的地方 */
	private static Global         $Global = new Global();
	
	
	private Map<Object ,Object>   global;
	
	
	
	/**
	 * 获取全局量实例化对象
	 * 
	 * @return
	 */
	public static Global getGlobal()
	{
		return $Global;
	}
	
	
	/**
	 * 私有
	 */
	private Global()
	{
		this.global = new Hashtable<Object ,Object>();
	}
	
	
	/**
	 * 添加全局量。添加成功后，返回全局量对象
	 * 
	 * 如果全局量已存在，则不在添加，保持全局量的惟一性，并返回集合中已存在的全局量对象。
	 * 
	 * @param i_Key    关键字
	 * @param i_Value  全局量对象
	 * @return
	 */
	public synchronized Object add(Object i_Key ,Object i_Value)
	{
		if ( i_Key == null )
		{
			throw new NullPointerException("Key is null.");
		}
		
		if ( i_Value == null )
		{
			throw new NullPointerException("Value is null.");
		}
		
		if ( this.global.containsKey(i_Key) )
		{
			return this.global.get(i_Key);
		}
		
		return this.global.put(i_Key, i_Value);
	}
	
	
	/**
	 * 获取全局量对象。
	 * 
	 * 如果全局量不存在，则返回 null
	 * 
	 * @param i_Key  关键字
	 * @return
	 */
	public synchronized Object get(Object i_Key)
	{
		if ( i_Key == null )
		{
			throw new NullPointerException("Key is null.");
		}
		
		if ( this.global.containsKey(i_Key) )
		{
			return this.get(i_Key);
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * 删除全局量。并返回全局量
	 * 
	 * 如果全局量不存在，则返回 null
	 * 
	 * @param i_Key  关键字
	 * @return
	 */
	public synchronized Object remove(Object i_Key)
	{
		if ( i_Key == null )
		{
			throw new NullPointerException("Key is null.");
		}
		
		if ( this.global.containsKey(i_Key) )
		{
			return this.remove(i_Key);
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * 获取全局量的总数
	 * 
	 * @return
	 */
	public int size()
	{
		return this.global.size();
	}
	
}
