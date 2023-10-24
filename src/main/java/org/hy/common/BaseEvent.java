package org.hy.common;

import java.util.EventObject;





/**
 * 基础事件
 * 
 * 此类是个只读类，即只有 getter 方法。
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2012-04-04
 */
public class BaseEvent extends EventObject
{
    private static final long serialVersionUID = -5301212377825264042L;

    /** 开始时间 */
    protected Date   beginTime;
    
    /** 结束时间 */
    protected Date   endTime;
    
    /** 信息。对于文件处理来说，就可能是目标文件名称 */
    protected String info;
    
    /** 大小(如，拷贝文件的字节数；创建文件的行数等等) */
    protected long   size;
    
    /** 已完成大小(如，已拷贝文件的字节数；创建文件已写入的行数等等) */
    protected long   completedSize;
    
    
    
    public BaseEvent(Object i_Source)
    {
        super(i_Source);
        this.beginTime     = new Date();
        this.completedSize = 0;
    }
    
    
    
    public BaseEvent(Object i_Source ,long i_Size)
    {
        this(i_Source);
        this.size = i_Size;
    }
    
    
    
    /**
     * 开始时间
     * 
     * @return
     */
    public Date getBeginTime()
    {
        return new Date(this.beginTime);
    }
    
    
    
    /**
     * 完成时间
     * 
     * @return
     */
    public Date getEndTime()
    {
        return new Date(this.endTime);
    }
    
    
    
    /**
     * 获取：信息。对于文件处理来说，就可能是文件名称
     */
    public String getInfo()
    {
        return info;
    }



    /**
     * 大小(如，拷贝文件的字节数；创建文件的行数等等)
     * 
     * @return
     */
    public long getSize()
    {
        return this.size;
    }
    
    
    
    /**
     * 已完成大小(如，已拷贝文件的字节数；创建文件已写入的行数等等)
     * 
     * @return
     */
    public long getCompletedSize()
    {
        return this.completedSize;
    }
    
    
    
    /**
     * 判断是否完成
     * 
     * @return
     */
    public boolean isComplete()
    {
        return this.completedSize == this.size && this.size > 0;
    }
    
    
    
    /**
     * 大小（转为计算机相应的单位）
     * 
     * @return
     */
    public String getSizeUnit()
    {
        return StringHelp.getComputeUnit(this.getSize());
    }
    
    
    
    /**
     * 已完成大小（转为计算机相应的单位）
     * 
     * @return
     */
    public String getCompletedSizeUnit()
    {
        return StringHelp.getComputeUnit(this.getCompletedSize());
    }
    
    
    
    /**
     * 进度的百分比
     * 
     * @param i_Digit  保留的小数位数
     * @return
     */
    public String getProcessRate(int i_Digit)
    {
        int v_Digit = i_Digit;
        
        if ( v_Digit <= 0 )
        {
            v_Digit = 0;
        }
        
        return StringHelp.doubleParse((this.getCompletedSize() * 100d) / (this.getSize() + 0d) ,v_Digit);
    }

}
