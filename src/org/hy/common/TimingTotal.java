package org.hy.common;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hy.common.Timing.TimingInfo;





/**
 * 计时器的横向统计类
 * 
 * 横向统计：将计时器 Timing 分段详情中，同一分段位置上的用时时长累加
 * 
 * 首次用途为：性能测试
 *
 * @author      ZhengWei(HY)
 * @createDate  2015-01-05
 * @version     v1.0
 */
public class TimingTotal
{
    
    /**
     * 计时器详情数据
     * 
     * Map.key   为使用者自行定义的标识
     * Map.value 为计时器详情数据
     */
    private PartitionMap<String ,Timing>         timings;
    
    /** 
     * 计时器的横向统计结果
     * 
     * Map.key        为使用者自行定义的标识
     * Counter.key    为计时器的横向下标值。即 Timing.timings 集合的下标索引
     * Counter.value  为计时器的横向统计结果
     */
    private Map<String ,Counter<Integer>>       transverse;
    
    /**
     * 每个计时器总用时时长的合计值
     * 
     * Counter.key    为使用者自行定义的标识
     * Counter.value  为每个计时器总用时时长的合计值
     */
    private Counter<String>                      total;
    
    
    
    public TimingTotal()
    {
        this.timings    = new TablePartition<String ,Timing>();
        this.transverse = new Hashtable<String ,Counter<Integer>>();
        this.total      = new Counter<String>();
    }
    
    
    
    /**
     * 累计
     * 
     * @param i_Timing     计时器
     */
    public void sum(Timing i_Timing)
    {
        this.sum("" ,i_Timing);
    }
    
    
    
    /**
     * 累计
     * 
     * @param i_NameSpace  空间名：为使用者自行定义的标识
     * @param i_Timing     计时器
     */
    public synchronized void sum(String i_NameSpace ,Timing i_Timing)
    {
        if ( i_Timing == null || i_Timing.getSize() <= 0 )
        {
            return;
        }
        
        i_Timing.pause();
        
        String v_NameSpace = Help.NVL(i_NameSpace).trim();
        
        this.timings.putRow(v_NameSpace ,i_Timing);
        this.total.put(     v_NameSpace ,i_Timing.getTotalTimeLen());
        
        Counter<Integer> v_TempCounter = null;
        List<TimingInfo> v_TempTiming  = i_Timing.getTimings();
        if ( this.transverse.containsKey(v_NameSpace) )
        {
            v_TempCounter = this.transverse.get(v_NameSpace);
            
            for (int i=0; i<v_TempTiming.size(); i++)
            {
                v_TempCounter.put(i ,v_TempTiming.get(i).getTimeLen());
            }
        }
        else
        {
            v_TempCounter = new Counter<Integer>();
            
            for (int i=0; i<v_TempTiming.size(); i++)
            {
                v_TempCounter.put(i ,v_TempTiming.get(i).getTimeLen());
            }
            
            this.transverse.put(v_NameSpace ,v_TempCounter);
        }
    }
    
    
    
    /**
     * 显示占比
     * 
     * @param i_NameSpace
     */
    public void showProportion(String i_NameSpace)
    {
        Counter<Integer> v_TimeLens    = this.transverse.get(i_NameSpace);
        long             v_Total       = this.total.get(i_NameSpace);
        Timing           v_FirstTiming = this.timings.getRow(i_NameSpace ,0);
        List<TimingInfo> v_TimingInfos = v_FirstTiming.getTimings();
        
        for (int i=0; i<v_TimeLens.size(); i++)
        {
            long v_TimeLen = v_TimeLens.get(Integer.valueOf(i));
            
            System.out.println("-- " + i + " : " 
                             + StringHelp.lpad(Help.round(Help.division(v_TimeLen * 100 ,v_Total) ,4) ,7 ," ") 
                             + "%    "
                             + StringHelp.lpad(v_TimeLen ,10 ," ") 
                             + "ms   "
                             + Help.NVL(v_TimingInfos.get(i).getInfo()));
        }
    }
    
    
    
    /**
     * 获取：计时器详情数据
     * 
     * Map.key   为使用者自行定义的标识
     * Map.value 为计时器详情数据
     */
    public PartitionMap<String ,Timing> getTimings()
    {
        return timings;
    }


    
    /**
     * 获取：计时器的横向统计结果
     * 
     * Map.key        为使用者自行定义的标识
     * Counter.key    为计时器的横向下标值。即 Timing.timings 集合的下标索引
     * Counter.value  为计时器的横向统计结果
     */
    public Map<String ,Counter<Integer>> getTransverse()
    {
        return transverse;
    }


    
    /**
     * 获取：每个计时器总用时时长的合计值
     * 
     * Counter.key    为使用者自行定义的标识
     * Counter.value  为每个计时器总用时时长的合计值
     */
    public Counter<String> getTotal()
    {
        return total;
    }
    
}
