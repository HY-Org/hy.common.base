package org.hy.common;





/**
 * 统计。可用于执行统计、请求统计等
 * 
 * 时间精度达到：纳秒
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-22
 * @version     v1.0
 */
public class TotalNano
{
    
    /** 累计的执行次数 */
    private long   requestTotal;
    
    /** 累计的执行成功次数 */
    private long   successTotal;
    
    /** 执行的次数 */
    private long   requestCount;
    
    /** 执行的成功次数 */
    private long   successCount;
    
    /** 执行成功，并成功返回的累计用时时长 */
    private long   successTimeLen;
    
    /** 执行成功，并成功返回的最大用时时长 */
    private long   successTimeLenMax;
    
    /**
     * 最后执行时间点。
     *   1. 在开始执行时，此时间点会记录一次。
     *   2. 在执行结束后，此时间点会记录一次。
     *   3. 当出现异常时，此时间点保持最近一次，不变。
     *   4. 当多个线程同时操作时，记录最新的时间点。
     *   5. 未执行时，此属性为NULL
     */
    private Long   executeTime;
    
    
    
    public TotalNano()
    {
        this(0L ,0L);
    }
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public TotalNano(long i_RequestTotal ,long i_SuccessTotal)
    {
        this.reset(i_RequestTotal ,i_SuccessTotal);
    }
    
    
    /**
     * 记录执行次数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-17
     * @version     v1.0
     *
     * @return  精度达到纳秒
     */
    protected synchronized Long request()
    {
        ++this.requestTotal;
        ++this.requestCount;
        this.executeTime = System.nanoTime();
        return this.executeTime;
    }
    
    
    /**
     * 记录成功次数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-17
     * @version     v1.0
     *
     * @param i_TimeLen  执行时长。精度达到纳秒
     */
    protected synchronized void success(long i_TimeLen)
    {
        ++this.successTotal;
        ++this.successCount;
        this.successTimeLen += i_TimeLen;
        this.successTimeLenMax = Math.max(this.successTimeLenMax ,i_TimeLen);
        this.executeTime = System.nanoTime();
    }
    
    
    /**
     * 重置统计。即统计归零
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     */
    public synchronized void reset()
    {
        this.reset(0L ,0L);
    }
    
    
    /**
     * 重置统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-18
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public synchronized void reset(long i_RequestTotal ,long i_SuccessTotal)
    {
        this.requestTotal      = i_RequestTotal;
        this.successTotal      = i_SuccessTotal;
        this.requestCount      = 0L;
        this.successCount      = 0L;
        this.successTimeLen    = 0L;
        this.successTimeLenMax = 0L;
    }
    
    
    /**
     * 获取：累计的执行次数
     */
    public long getRequestTotal()
    {
        return requestTotal;
    }

    
    /**
     * 获取：累计的执行成功次数
     */
    public long getSuccessTotal()
    {
        return successTotal;
    }

    
    /**
     * 获取：执行的次数
     */
    public long getRequestCount()
    {
        return requestCount;
    }

    
    /**
     * 获取：执行的成功次数
     */
    public long getSuccessCount()
    {
        return successCount;
    }
    
    
    /**
     * 获取：执行成功，并成功返回的累计用时时长。精度达到纳秒
     */
    public long getSuccessTimeLen()
    {
        return successTimeLen;
    }
    
    
    /**
     * 获取：执行成功，并成功返回的最大用时时长。精度达到纳秒
     */
    public long getSuccessTimeLenMax()
    {
        return successTimeLenMax;
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
     * @createDate  2025-02-17
     * @version     v1.0
     *
     * @return  精度达到纳秒
     */
    public Long getExecuteTime()
    {
        return this.executeTime;
    }
    
}
