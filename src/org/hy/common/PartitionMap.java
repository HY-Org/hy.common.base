package org.hy.common;

import java.util.List;
import java.util.Map;





/**
 * 表分区。 -- 类似于 Oracle 中表分区的概念。
 * 
 * P -- 为表分区信息。也是Map.key值
 * R -- 为一行记录信息。它存放在Map.Value(List).value中。
 * Map.Value 表示表分区中一分区的信息
 * 
 * 将 TablePartition 和 TablePartitionLink 两个实体类，抽象出此统一接口出来
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2014-12-24
 */
public interface PartitionMap<P ,R> extends Map<P ,List<R>>
{
	
	/**
	 * 向某一表分区添加一行数据
	 * 
	 * @param i_Partition  表分区信息
	 * @param i_Row        一行数据信息
	 * @return
	 */
	public R putRow(P i_Partition ,R i_Row);
	
	
	
	/**
	 * 向某一表分区添加一批记录数据
	 * 
	 * put(P ,List<R>) 与本方法在的实现功能是一样的，从入参形式看上去是一样的。
	 * 但实现上对于Java编译器来说，是完全不样的，我说不是方法的名称不一样，而重点说的是入参参数。
	 * 
	 * 主要在于本类的是TablePartition<P ,R>，并实现了Map接口。那本类就是一个Map集合。
	 * 因此，本Map的value是什么呢？当然是R了。但，是R吗？答：不是R，而是List<R>。
	 * 
	 * 这里有点儿绕。请仔细想想。
	 * 那对于 Map.put(K ,V) 方法来说，V是什么呢？V是本类 R 还是 List<R> 呢？
	 * 答：应当是 R 了。
	 * 也就是说，我将Java编译器给绕晕了。它错误理解为 Map.put(K ,List<R>) ，而不是 Map.put(K ,R)。 
	 * 
	 * 这样一来，对于外界使用来说，可能出现下面的问题
	 *   TablePartition<XSQL ,?> v_XSQLs;
	 *   List<?>                 v_Rows;
	 *   
	 *   v_XSQLs.put(new XSQL() ,v_Rows);   // 此处Java编译器报错，它认为范型不匹配
	 *   
	 * 也因为此，我写了本方法 2014-12-04
	 * 
	 * @param i_Partition
	 * @param i_RowList
	 * @return
	 */
	public List<R> putRows(P i_Partition ,List<R> i_RowList);
	
	
	
	/**
	 * 向某一表分区添加一批记录数据
	 * 
	 * @param i_Partition
	 * @param i_Rows
	 * @return
	 */
	public List<R> putRows(P i_Partition ,R [] i_Rows);
	
	
	
	/**
     * 向某多个表分区，分别都添加一条记录数据
     * 
     * @param i_PartitionRows  它的Key就是分区信息，它的Value就是一条分区记录
     */
    public void putRows(Map<P ,R> i_PartitionRows);
	
	
	
	/**
     * 向某一表分区添加一批记录数据。
     * 
     * 与 putRow() 方法功能一样。只是 putRows() 在编程时，写多处时，比较统一，也不易出错。
     * 
     * @param i_Partition
     * @param i_Rows
     * @return
     */
    public List<R> putRows(P i_Partition ,R i_Row);
    
    
    
	/**
	 * 获取某个分区中的一行数据（按行号）
	 * 
	 * @param i_Partition  表分区信息
	 * @param i_RowNo      行号。下标从 0 开始。
	 * @return
	 */
	public R getRow(P i_Partition ,int i_RowNo);
	
    
	
	/**
	 * 获取某个分区中的一行数据（按行级对象）
	 * 
	 * @param i_Partition  表分区信息
	 * @param i_Row        行级对象
	 * @return
	 */
	public R getRow(P i_Partition ,R i_Row);
	
	
	
	/**
	 * 删除某个分区中的一行数据（按行号）
	 * 
	 * @param i_Partition  表分区信息
	 * @param i_RowNo      行号。下标从 0 开始。
	 * @return
	 */
	public R removeRow(P i_Partition ,int i_RowNo);
	
	
	
	/**
	 * 删除某个分区中的一行数据（按行级对象）
	 * 
	 * @param i_Partition  表分区信息
	 * @param i_Row        行级对象
	 * @return
	 */
	public R removeRow(P i_Partition ,R i_Row);
	
	
	
	/**
	 * 获取记录行数
	 * 
	 * @return
	 */
	public int rowCount();
    
    
    
    /**
     * 获取定分区中记录行数
     * 
     * @param i_Partition
     * @return
     */
    public int rowCount(P i_Partition);
	
	
	
	/**
	 * 清理给定分区中的所有数据
	 * 
	 * @param i_Partition
	 */
	public void clear(P i_Partition);
	
}
