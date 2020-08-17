package org.hy.common;

import java.io.Serializable;





/**
 * 双通道结构的数据集合。
 * 
 * 实现读、写分离。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-07-03
 * @version     v1.0
 * @param <O>
 */
public class DualChannel<O> implements Serializable
{
    private static final long serialVersionUID = 7988903474514405379L;
    
    

    /** 双通道结构中的通道1 */
    private Object [] oneChannel;
    
    /** 双通道结构中的通道2 */
    private Object [] twoChannel;
    
    /** 单个通道的大小 */
    private int channelSize;
    
    /** 双通道中的对象数量 */
    private int dataSize;
    
    /** 获取对象的指针序号 */
    private int readIndex;
    
    /** 写入对象的指针序号 */
    private int writeIndex;
    
    /** 获取对象的通道编号（0:通道1； 1:通道2） */
    private int readChannelNo;
    
    /** 写入对象的通道编号（0:通道1； 1:通道2） */
    private int writeChannelNo;
    
    
    
    public DualChannel(int i_ChannelSize)
    {
        if ( i_ChannelSize < 1 )
        {
            throw new IndexOutOfBoundsException("Channel size < 1.");
        }
        
        this.oneChannel     = new Object[i_ChannelSize];
        this.twoChannel     = new Object[i_ChannelSize];
        this.channelSize    = i_ChannelSize;
        this.dataSize       = 0;
        this.readIndex      = -1;
        this.writeIndex     = -1;
        this.readChannelNo  = 0;
        this.writeChannelNo = 0;
    }
    
    
    
    /**
     * 向双通道中添加数据对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @param i_Data
     * @return
     */
    public synchronized O put(O i_Data)
    {
        if ( i_Data == null )
        {
            return null;
        }
        
        if ( this.writeIndex >= this.channelSize - 1 )
        {
            this.writeIndex = -1;
            this.writeChannelNo = ++this.writeChannelNo % 2;
        }
        
        this.dataSize++;
        
        if ( this.writeChannelNo == 0 )
        {
            this.oneChannel[++this.writeIndex] = i_Data;
        }
        else
        {
            this.twoChannel[++this.writeIndex] = i_Data;
        }
        
        return i_Data;
    }
    
    
    
    /**
     * 从双通道中获取数据对象并移除。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized O poll()
    {
        if ( this.readIndex >= this.channelSize - 1 )
        {
            this.readIndex = -1;
            this.readChannelNo = ++this.readChannelNo % 2;
        }
        
        Object v_Ret   = null;
        int    v_Index = this.readIndex + 1;
        if ( this.readChannelNo == 0 )
        {
            v_Ret = this.oneChannel[v_Index];
            this.oneChannel[v_Index] = null;
        }
        else
        {
            v_Ret = this.twoChannel[v_Index];
            this.twoChannel[v_Index] = null;
        }
        
        if ( v_Ret != null )
        {
            this.dataSize--;
            this.readIndex = v_Index;
        }
        
        return (O)v_Ret;
    }


    
    /**
     * 获取：单个通道的大小
     */
    public int getChannelSize()
    {
        return channelSize;
    }
    
    
    
    /**
     * 获取：双通道中的对象数量
     */
    public int size()
    {
        return dataSize;
    }
    

    
    /**
     * 获取：获取对象的指针序号
     */
    public int getReadIndex()
    {
        return readIndex;
    }
    

    
    /**
     * 获取：写入对象的指针序号
     */
    public int getWriteIndex()
    {
        return writeIndex;
    }
    

    
    /**
     * 获取：获取对象的通道编号（0:通道1； 1:通道2）
     */
    public int getReadChannelNo()
    {
        return readChannelNo;
    }
    

    
    /**
     * 获取：写入对象的通道编号（0:通道1； 1:通道2）
     */
    public int getWriteChannelNo()
    {
        return writeChannelNo;
    }
    
}
