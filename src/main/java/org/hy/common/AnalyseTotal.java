package org.hy.common;





/**
 * 分析统计
 * 
 * 从XSQL抽象独立出来
 *
 * @author      ZhengWei(HY)
 * @createDate  2023-06-04
 * @version     v1.0
 */
public class AnalyseTotal
{
    /** 请求数据库的次数 */
    protected long                           requestCount;
    
    /** 请求成功，并成功返回次数 */
    protected long                           successCount;
    
    /**
     * 请求成功，并成功返回的累计用时时长。
     * 用的是Double，而不是long，因为在批量执行时。为了精度，会出现小数
     */
    protected double                         successTimeLen;
    
    /**
     * 请求成功，并成功返回的最大用时时长。
     */
    protected double                         successTimeLenMax;
    
    /** 读写行数。查询结果的行数或写入数据库的记录数 */
    protected long                           ioRowCount;
    
    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     */
    protected Date                           executeTime;
    
    
    
    public AnalyseTotal()
    {
        this.requestCount       = 0L;
        this.successCount       = 0L;
        this.successTimeLen     = 0D;
        this.successTimeLenMax  = 0D;
        this.ioRowCount         = 0L;
        this.executeTime        = null;
    }
    
    
    
    /**
     * 重置统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-03-05
     * @version     v1.0
     *
     */
    public void reset()
    {
        this.requestCount      = 0L;
        this.successCount      = 0L;
        this.successTimeLen    = 0D;
        this.successTimeLenMax = 0D;
        this.ioRowCount        = 0L;
        this.executeTime       = null;
    }
    
    
    
    /**
     * 数据请求时的统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-03-05
     * @version     v1.0
     *
     * @return
     */
    protected synchronized Date request()
    {
        ++this.requestCount;
        this.executeTime = new Date();
        return this.executeTime;
    }
    
    
    
    /**
     * 数据处理成功时的统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-03-05
     * @version     v1.0
     *
     * @param i_ExecuteTime  执行时间
     * @param i_TimeLen      用时时长（单位：毫秒）
     * @param i_SumCount     成功次数
     * @param i_IORowCount   读写行数
     */
    protected synchronized void success(Date i_ExecuteTime ,double i_TimeLen ,int i_SumCount ,long i_IORowCount)
    {
        // 外界提供DB连接时，部分XSQL的写操作，不在XSQL内提交时 i_SumCount 会为0值，
        // 此处简单的处理方案是：先按提交 1 次统计数量。
        // 暂时忽略（不监控）外界回滚的情况，待有好的方案时再将回滚的纳入统计 TODO
        this.requestCount     += Help.max(i_SumCount ,1) - 1;
        this.successCount     += Help.max(i_SumCount ,1);
        this.successTimeLen   += i_TimeLen;
        this.successTimeLenMax = Math.max(this.successTimeLenMax ,i_TimeLen);
        this.executeTime       = i_ExecuteTime;
        this.ioRowCount       += i_IORowCount;
    }
    
    
    
    /**
     * 获取：请求数据库的次数
     */
    public long getRequestCount()
    {
        return requestCount;
    }


    
    /**
     * 获取：请求成功，并成功返回次数
     */
    public long getSuccessCount()
    {
        return successCount;
    }



    /**
     * 获取：请求成功，并成功返回的累计用时时长。
     * 用的是Double，而不是long，因为在批量执行时。为了精度，会出现小数
     */
    public double getSuccessTimeLen()
    {
        return successTimeLen;
    }
    
    
    
    /**
     * 获取：请求成功，并成功返回的最大用时时长。
     */
    public double getSuccessTimeLenMax()
    {
        return successTimeLenMax;
    }



    /**
     * 获取：读写行数。查询结果的行数或写入数据库的记录数
     */
    public long getIoRowCount()
    {
        return ioRowCount;
    }



    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @return
     */
    public Date getExecuteTime()
    {
        return this.executeTime;
    }
    
}
