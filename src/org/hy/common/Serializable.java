package org.hy.common;





/**
 * 将对象简单序列化，具备并按顺序读取的统一接口。
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2013-08-26
 */
public interface Serializable extends java.io.Serializable 
{

	/**
	 * 获取属性的数量
	 * 
	 * @return
	 */
	public int gatPropertySize();
	
	
	
	/**
	 * 获取指定顺序上的属性名称
	 * 
	 * @param i_PropertyIndex  下标从0开始
	 * @return
	 */
	public String gatPropertyName(int i_PropertyIndex);
	
	
	
	/**
	 * 获取指定顺序上的属性值
	 * 
	 * @param i_PropertyIndex  下标从0开始
	 * @return
	 */
	public Object gatPropertyValue(int i_PropertyIndex);
	
	
	
	/**
	 * 释放资源
	 */
	public void freeResource();
	
}
