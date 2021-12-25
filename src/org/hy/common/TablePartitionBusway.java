package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 表分区。 -- 类似于 Oracle 中表分区的概念。
 * 
 * 继承Hashtable，有同步功能。
 * 
 * P -- 为表分区信息。也是Map.key值
 * R -- 为一行记录信息。它存放在Map.Value(Set).value中。
 * Map.Value 表示表分区中一分区的信息
 * 
 * 与 TablePartition 类似，但表对象为Busway集合。与它对比有如下不同
 *   不同点1：没有实现 PartitionMap 接口
 *   不同点2：没有 public R getRow(P i_Partition ,int i_RowNo) 方法
 *   不同点3：没有 public R getRow(P i_Partition ,R i_Row)     方法
 *   不同点4：没有 public synchronized R removeRow(P i_Partition ,int i_RowNo) 方法
 *   不同点5：本类元素是队列的（先入先出 或 先入后出）
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2021-12-23
 */
public class TablePartitionBusway<P ,R> extends Hashtable<P ,Busway<R>> implements Map<P ,Busway<R>>
{
    private static final long serialVersionUID = 2636651193881621815L;
    
    /** 获取记录行数 */
    private int rowCount;
    
    /** 默认的队列长度 */
    private int defaultWayLength;
    
    
    
    public TablePartitionBusway()
    {
        super();
        
        this.rowCount         = 0;
        this.defaultWayLength = 1000;
    }
    
    
    
    public TablePartitionBusway(int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.rowCount         = 0;
        this.defaultWayLength = 1000;
    }
    
    
    
    /**
     * 向某一表分区添加一行数据
     * 
     * @param i_Partition  表分区信息
     * @param i_Row        一行数据信息
     * @return
     */
    public synchronized R putRow(P i_Partition ,R i_Row)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( i_Row == null )
        {
            throw new java.lang.NullPointerException("Row is null.");
        }
        
        
        Busway<R> v_TabPart = null;
        
        if ( this.containsKey(i_Partition) )
        {
            v_TabPart = super.get(i_Partition);
            v_TabPart.put(i_Row);
        }
        else
        {
            v_TabPart = new Busway<R>(this.defaultWayLength);
            v_TabPart.put(i_Row);
            super.put(i_Partition, v_TabPart);
        }
        
        
        this.rowCount++;
        return i_Row;
    }
    
    
    
    /**
     * 向某一表分区添加一批记录数据
     * 
     * @param i_Partition  表分区信息
     * @param i_RowList    一批记录数据
     * @return
     * 
     * @see 建议使用 putRows(P ,Set<R>) 方法。因为此方法在多数情况下是无法达到预想目的的。
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized Busway<R> put(P i_Partition ,Busway<R> i_RowList)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( Help.isNull(i_RowList) )
        {
            throw new java.lang.NullPointerException("RowList is null.");
        }
        
        Object [] v_Datas = i_RowList.getArray();
        for (Object v_Data : v_Datas)
        {
            this.putRow(i_Partition ,(R)v_Data);
        }
        
        return this.get(i_Partition);
    }
    
    
    
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
    @SuppressWarnings("unchecked")
    public synchronized Busway<R> putRows(P i_Partition ,Busway<R> i_RowList)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( Help.isNull(i_RowList) )
        {
            throw new java.lang.NullPointerException("RowList is null.");
        }
        
        Object [] v_Datas = i_RowList.getArray();
        for (Object v_Data : v_Datas)
        {
            this.putRow(i_Partition ,(R)v_Data);
        }
        
        return this.get(i_Partition);
    }
    
    
    
    /**
     * 向某一表分区添加一批记录数据
     * 
     * @param i_Partition
     * @param i_Rows
     * @return
     */
    public synchronized Busway<R> putRows(P i_Partition ,R [] i_Rows)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( Help.isNull(i_Rows) )
        {
            throw new java.lang.NullPointerException("RowList is null.");
        }
        
        
        for (int i=0; i<i_Rows.length; i++)
        {
            this.putRow(i_Partition ,i_Rows[i]);
        }
        
        return this.get(i_Partition);
    }
    
    
    
    /**
     * 向某一表分区添加一批记录数据。
     * 
     * 与 putRow() 方法功能一样。只是 putRows() 在编程时，写多处时，比较统一，也不易出错。
     * 
     * @param i_Partition
     * @param i_Rows
     * @return
     */
    public synchronized Busway<R> putRows(P i_Partition ,R i_Row)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( i_Row == null )
        {
            throw new java.lang.NullPointerException("Row is null.");
        }
        
        
        this.putRow(i_Partition ,i_Row);
        
        return this.get(i_Partition);
    }
    
    
    
    /**
     * 删除某个分区中的一行数据（按行级对象）
     * 
     * @param i_Partition  表分区信息
     * @param i_Row        行级对象
     * @return
     */
    public synchronized R removeRow(P i_Partition ,R i_Row)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition is null.");
        }
        if ( i_Row == null )
        {
            throw new java.lang.NullPointerException("Row is null.");
        }
        
        
        if ( super.containsKey(i_Partition) )
        {
            Busway<R> v_TabPart = super.get(i_Partition);
            
            if ( v_TabPart.remove(i_Row) )
            {
                this.rowCount--;
                return i_Row;
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 清理给定分区中的所有数据
     */
    @Override
    public synchronized Busway<R> remove(Object i_Partition)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition is null.");
        }
        
        if ( super.containsKey(i_Partition) )
        {
            Busway<R> v_TabPart = super.get(i_Partition);
            
            super.remove(i_Partition);
            this.rowCount -= v_TabPart.size();
            
            return v_TabPart;
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 获取记录行数
     * 
     * @return
     */
    public int rowCount()
    {
        return this.rowCount;
    }
    
    
    
    /**
     * 获取定分区中记录行数
     * 
     * @param i_Partition
     * @return
     */
    public long rowCount(P i_Partition)
    {
        if ( i_Partition == null )
        {
            return 0;
        }
        
        Busway<R> v_PartitionDatas = this.get(i_Partition);
        
        if ( v_PartitionDatas != null )
        {
            return v_PartitionDatas.size();
        }
        else
        {
            return 0;
        }
    }
    
    
    
    /**
     * 清理所有分区中的所有数据
     */
    @Override
    public synchronized void clear()
    {
        Iterator<Busway<R>> v_Iter = super.values().iterator();
        
        while ( v_Iter.hasNext() )
        {
            v_Iter.next().clear();
        }
        
        super.clear();
        
        this.rowCount = 0;
    }
    
    
    
    /**
     * 清理给定分区中的所有数据
     * 
     * @param i_Partition
     */
    @SuppressWarnings("unlikely-arg-type")
    public synchronized void clear(R i_Partition)
    {
        if ( super.containsKey(i_Partition) )
        {
            this.remove(i_Partition);
        }
    }


    
    /**
     * 获取队列的默认的队列长度
     * @return
     */
    public int getDefaultWayLength()
    {
        return defaultWayLength;
    }



    /**
     * 设置默认的队列长度
     * 
     * @param i_DefaultWayLength
     */
    public void setDefaultWayLength(int i_DefaultWayLength)
    {
        this.defaultWayLength = i_DefaultWayLength;
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
