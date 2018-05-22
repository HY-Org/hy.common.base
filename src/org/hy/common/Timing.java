package org.hy.common;

import java.util.ArrayList;
import java.util.List;





/**
 * 计时器
 * 
 * 首次用途为：性能测试
 * 
 * @author      ZhengWei(HY)
 * @createDate  2015-01-05 
 * @version     v1.0 
 */
public class Timing implements java.io.Serializable
{
    
    private static final long serialVersionUID = 3781736567889784229L;
    
    

    /** 总体最早开始计时时间 */
    private Date             totalBeginTime;
    
    /** 总体最后结束计时时间 */
    private Date             totalEndTime;
    
    /** 
     * 总体有效计时时长。不包含暂时时长
     * 
     * 即，totalEndTime - totalBeginTime 不一定等于 totalTimeLen
     */
    private long             totalTimeLen;
    
    /** 历次(分段)计时详情 */
    private List<TimingInfo> timings;
    
    
    
    public Timing()
    {
        this.totalBeginTime = null;
        this.totalEndTime   = null;
        this.timings        = new ArrayList<TimingInfo>();
        
        this.timing();
    }
    
    
    
    public Timing(String i_Info)
    {
        this.totalBeginTime = null;
        this.totalEndTime   = null;
        this.timings        = new ArrayList<TimingInfo>();
        
        this.timing(i_Info);
    }
    
    
    
    /**
     * 计时动作
     */
    public void timing()
    {
        this.timing("");
    }
    
    
    
    /**
     * 计时动作
     */
    public synchronized void timing(String i_Info)
    {
        this.timings.add(new TimingInfo(this ,i_Info));
    }
    
    
    
    /**
     * 暂停计时
     */
    public void pause()
    {
        if ( this.timings.size() >= 1 )
        {
            int v_Size = this.timings.size();
            this.timings.get(v_Size - 1).setEndTime(new Date());
        }
        else
        {
            throw new VerifyError("Not start timing, cannot pause timing.");
        }
    }
    
    
    
    /**
     * 获取：历次(分段)计时详情
     */
    public List<TimingInfo> getTimings()
    {
        return new ArrayList<TimingInfo>(timings);
    }
    
    
    
    /**
     * 获取：分段计时的总次数 
     * 
     * @return
     */
    public int getSize()
    {
        return this.timings.size();
    }



    /**
     * 获取：总体最早开始计时时间
     */
    public Date getTotalBeginTime()
    {
        return totalBeginTime;
    }


    
    /**
     * 获取：总体最后结束计时时间
     */
    public Date getTotalEndTime()
    {
        return totalEndTime;
    }

    
    
    /**
     * 获取：总体有效计时时长。不包含暂时时长
     * 
     * 即，totalEndTime - totalBeginTime 不一定等于 totalTimeLen
     */
    public long getTotalTimeLen()
    {
        return totalTimeLen;
    }
    
    
    


    /**
     * 计时信息类 
     *
     * @author      ZhengWei(HY)
     * @createDate  2015-01-05
     * @version     v1.0
     */
    class TimingInfo implements java.io.Serializable
    {
        private static final long serialVersionUID = 1074796360731584951L;
        
        

        /** 计时顺序 */
        private int    index;
        
        /** 计时开始时间 */
        private Date   beginTime;
        
        /** 计时结束时间（只能为设置一次） */
        private Date   endTime;
        
        /** 计时时长(为了快而容存) */
        private long   timeLen;
        
        /** 计时信息，可为空 */
        private String info;
        
        /** 所有者 */
        private Timing master;
        
        
        
        public String toString()
        {
            StringBuilder v_Buffer = new StringBuilder();
            
            v_Buffer.append(this.beginTime.getFullMilli());
            
            if ( null != this.endTime )
            {
                v_Buffer.append(" ~ ");
                v_Buffer.append(this.endTime.getFullMilli());
                v_Buffer.append("\t");
                v_Buffer.append(this.timeLen);
            }
            
            if ( null != this.info )
            {
                v_Buffer.append("\t");
                v_Buffer.append(this.info);
            }
            
            return v_Buffer.toString();
        }

        
        
        public TimingInfo(Timing io_Timing)
        {
            this(io_Timing ,"");
        }
        
        
        
        public TimingInfo(Timing io_Timing ,String i_Info)
        {
            this.beginTime = new Date();
            this.timeLen   = 0L;
            this.index     = io_Timing.timings.size();
            this.info      = i_Info;
            this.master    = io_Timing;
            
            if ( this.index >= 1 )
            {
                TimingInfo v_Old = io_Timing.timings.get(this.index - 1);
                
                v_Old.setEndTime(this.beginTime);
            }
            else
            {
                io_Timing.totalBeginTime = this.beginTime;
            }
        }
        
        
        
        /**
         * 获取：计时顺序
         */
        public int getIndex()
        {
            return index;
        }

        
        
        /**
         * 获取：计时开始时间
         */
        public Date getBeginTime()
        {
            return beginTime;
        }

        
        
        /**
         * 获取：计时结束时间（只能为设置一次）
         */
        public synchronized Date getEndTime()
        {
            return endTime;
        }


        
        /**
         * 设置：计时结束时间（只能为设置一次）
         * 
         * @param endTime 
         */
        public synchronized void setEndTime(Date i_EndTime)
        {
            if ( this.endTime == null && i_EndTime != null )
            {
                this.endTime              = i_EndTime;
                this.timeLen              = this.endTime.getTime() - this.beginTime.getTime();
                this.master.totalEndTime  = i_EndTime;
                this.master.totalTimeLen += this.timeLen;
            }
        }

        

        /**
         * 获取：计时分段时长
         */
        public long getTimeLen()
        {
            return this.timeLen;
        }

        
        
        /**
         * 获取：计时信息，可为空
         */
        public String getInfo()
        {
            return info;
        }

        
        /**
         * 设置：计时信息，可为空
         * 
         * @param info 
         */
        public void setInfo(String info)
        {
            this.info = info;
        }



        /**
         * 获取：所有者
         */
        public Timing getMaster()
        {
            return master;
        }
        
    }    
    
}
