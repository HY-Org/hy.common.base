package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;





/**
 * 表分区。 -- 类似于 Oracle 中表分区的概念。
 * 
 * 继承Hashtable，有同步功能。
 * 
 * 与 TablePartition 对比，TablePartitionRID有类似于主键索引的功能，因为 TablePartitionRID.value 也是一个Map。
 * 
 * LinkedHashMap是为了解决遍历Hash表的无序问题，
 *     它内部维护了一个链表用于记录你插入元素（或你访问元素的顺序）的位置，
 *     遍历时直接遍历链表，元素的顺序即为你插入的顺序，
 *     但是Entry对象要多加两个成员变量before和after用于记录链表的前驱和后继。
 *     所以LinkedHashMap的的存储效率要低于HashMap，但是遍历效率要高于HashMap。
 * 
 * P -- 为表分区信息。也是Map.key值
 * R -- 为一行记录信息。它存放在Map.Value(Map).value中。
 * Map.Value 表示表分区中一分区的信息
 * 
 * @author      ZhengWei(HY)
 * @createDate  2013-06-17
 * @version     v1.0
 *              v1.1  2017-01-25  修正：i_RowID 只判断是否为 null ，对于空字符串不再判断为非法的。
 */
public class TablePartitionRID<P ,R> extends Hashtable<P ,Map<String ,R>> implements Map<P ,Map<String ,R>>
{
    private static final long serialVersionUID = -1655261815653466839L;
    
    /** 获取记录行数 */
    private int rowCount;
    
    
    
    public TablePartitionRID()
    {
        super();
        
        this.rowCount = 0;
    }
    
    
    
    public TablePartitionRID(int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.rowCount = 0;
    }
    
    
    
    /**
     * 向某一表分区添加一行数据
     * 
     * 默认主键ID为i_Row.toString()
     * 
     * @param i_Partition  表分区信息
     * @param i_Row        一行数据信息
     * @return
     */
    public R putRow(P i_Partition ,R i_Row)
    {
        return this.putRow(i_Partition ,null ,i_Row);
    }
    
    
    
    /**
     * 向某一表分区添加一行数据
     * 
     * @param i_Partition  表分区信息
     * @param i_RowID      一行数据主键(ID)
     * @param i_Row        一行数据信息
     * @return
     */
    public synchronized R putRow(P i_Partition ,String i_RowID ,R i_Row)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( i_Row == null )
        {
            throw new java.lang.NullPointerException("Row is null.");
        }
        
        String v_RowID = i_RowID;
        if ( Help.isNull(i_RowID) )
        {
            v_RowID = i_Row.toString();
        }
        
        
        Map<String ,R> v_TabPart = null;
        
        if ( this.containsKey(i_Partition) )
        {
            v_TabPart = super.get(i_Partition);
            v_TabPart.put(v_RowID ,i_Row);
        }
        else
        {
            v_TabPart = new LinkedHashMap<String ,R>();
            v_TabPart.put(v_RowID ,i_Row);
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
     * @see 建议使用 putRows(P ,List<R>) 方法。因为此方法在多数情况下是无法达到预想目的的。
     */
    @Override
    public Map<String ,R> put(P i_Partition ,Map<String ,R> i_RowMap)
    {
        return putRows(i_Partition ,i_RowMap);
    }
    
    
    
    /**
     * 向某一表分区添加一批记录数据
     * 
     * put(P ,Map<String ,R>) 与本方法在的实现功能是一样的，从入参形式看上去是一样的。
     * 但实现上对于Java编译器来说，是完全不样的，我说不是方法的名称不一样，而重点说的是入参参数。
     * 
     * 主要在于本类的是TablePartitionRID<P ,R>，并实现了Map接口。那本类就是一个Map集合。
     * 因此，本Map的value是什么呢？当然是R了。但，是R吗？答：不是R，而是Map<String ,R>。
     * 
     * 这里有点儿绕。请仔细想想。
     * 那对于 Map.put(K ,V) 方法来说，V是什么呢？V是本类 R 还是 Map<String ,R> 呢？
     * 答：应当是 R 了。
     * 也就是说，我将Java编译器给绕晕了。它错误理解为 Map.put(K ,Map<String ,R>) ，而不是 Map.put(K ,R)。
     * 
     * 这样一来，对于外界使用来说，可能出现下面的问题
     *   TablePartitionRID<XSQL ,?> v_XSQLs;
     *   Map<String ,?>             v_Rows;
     * 
     *   v_XSQLs.put(new XSQL() ,v_Rows);   // 此处Java编译器报错，它认为范型不匹配
     * 
     * 也因为此，我写了本方法 2014-12-04
     * 
     * @param i_Partition
     * @param i_RowMap
     * @return
     */
    public synchronized Map<String ,R> putRows(P i_Partition ,Map<String ,R> i_RowMap)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition  is null.");
        }
        if ( Help.isNull(i_RowMap) )
        {
            throw new java.lang.NullPointerException("RowMap is null.");
        }
        
        
        Iterator <String> v_Iter  = i_RowMap.keySet().iterator();
        String            v_RowID = null;
        while ( v_Iter.hasNext() )
        {
            v_RowID = v_Iter.next();
            this.putRow(i_Partition ,v_RowID ,i_RowMap.get(v_RowID));
        }
        
        return this.get(i_Partition);
    }
    
    
    
    /**
     * 获取某个分区中的一行数据（主键ID）
     * 
     * @param i_Partition  表分区信息
     * @param i_RowID      主键(ID)
     * @return
     */
    public R getRow(P i_Partition ,String i_RowID)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition is null.");
        }
        if ( i_RowID == null )
        {
            throw new java.lang.NullPointerException("RowID is null.");
        }
        
        
        if ( super.containsKey(i_Partition) )
        {
            Map<String ,R> v_TabPart = super.get(i_Partition);
            
            if ( v_TabPart.containsKey(i_RowID) )
            {
                return v_TabPart.get(i_RowID);
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
     * 删除某个分区中的一行数据（按行号）
     * 
     * @param i_Partition  表分区信息
     * @param i_RowID      主键(ID)
     * @return
     */
    public synchronized R removeRow(P i_Partition ,String i_RowID)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition is null.");
        }
        if ( i_RowID == null )
        {
            throw new java.lang.NullPointerException("RowID is null.");
        }
        
        
        if ( super.containsKey(i_Partition) )
        {
            Map<String ,R> v_TabPart = super.get(i_Partition);
            
            if ( v_TabPart.containsKey(i_RowID) )
            {
                R v_Ret = v_TabPart.remove(i_RowID);
                if ( v_Ret != null )
                {
                    this.rowCount--;
                    return v_Ret;
                }
            }
            else
            {
                throw new java.lang.IndexOutOfBoundsException("RowID = " + i_RowID + " is not Find.");
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 清理给定分区中的所有数据
     */
    @Override
    public synchronized Map<String ,R> remove(Object i_Partition)
    {
        if ( i_Partition == null )
        {
            throw new java.lang.NullPointerException("Partition is null.");
        }
        
        if ( super.containsKey(i_Partition) )
        {
            Map<String ,R> v_TabPart = super.get(i_Partition);
            
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
    public int rowCount(P i_Partition)
    {
        if ( i_Partition == null )
        {
            return 0;
        }
        
        Map<String ,R> v_PartitionDatas = this.get(i_Partition);
        
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
        Iterator<Map<String ,R>> v_Iter = super.values().iterator();
        
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
    public synchronized void clear(R i_Partition)
    {
        if ( super.containsKey(i_Partition) )
        {
            this.remove(i_Partition);
        }
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
