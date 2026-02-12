package org.hy.common.junit;

import java.util.Iterator;

import org.hy.common.Date;
import org.hy.common.ExpireMap;
import org.hy.common.Expire;
import org.hy.common.ExpireCache;
import org.hy.common.Help;
import org.hy.common.app.AppParameter;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：Map.key有生存时间的Map，当生存时间期满时，Map.key就失效了。
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-02-25
 * @version     v1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JU_ExpireMap
{
    
    @Test
    public void test_001() throws InterruptedException
    {
        ExpireMap<String ,Date> v_ExpireMap = new ExpireMap<String ,Date>();
        
        long                 v_Second = 2L;
        Date                 v_Now    = new Date();
        Date                 v_Time   = new Date(v_Now.getTime() + v_Second * 1000L);
        String               v_Key    = v_Time.getFullMilli();
        Expire<String ,Date> v_Expire = v_ExpireMap.put(v_Key ,v_Time ,v_Second);
        String               v_ETS    = (new Date(v_Expire.getTime())).getFullMilli();
        
        
        System.out.println("-- 当前时间：" + v_Now.getFullMilli());
        while ( true )
        {
            v_Time = v_ExpireMap.get(v_Key);
            
            if ( null != v_Time )
            {
                System.out.println("-- 当前时间：" + Date.getNowTime().getFullMilli() + "\t剩余时长：" + v_ExpireMap.getExpireTimeLen(v_Key));
            }
            else
            {
                System.out.println("-- 当前时间：" + Date.getNowTime().getFullMilli() + "\t过期时间：" + v_ETS);
                break;
            }
        }
        
        Iterator<String> v_Iterator = v_ExpireMap.keySet().iterator();
        while ( v_Iterator.hasNext() )
        {
            String v_ID = v_Iterator.next();
            
            System.out.println(v_ID);
        }
    }
    
    
    
    @Test
    public void test_002() throws InterruptedException
    {
        ExpireMap<String ,Date> v_ExpireMapOld = new ExpireMap<String ,Date>();
        ExpireMap<String ,Date> v_ExpireMapNew = new ExpireMap<String ,Date>();
        
        long   v_Second = 2L;
        Date   v_Now    = new Date();
        Date   v_Time   = new Date(v_Now.getTime() + v_Second * 1000L);
        String v_Key    = v_Time.getFullMilli();
        
        v_ExpireMapOld.put(v_Key ,v_Time ,v_Second);
        System.out.println("-- 原集合能否得到值：" + v_ExpireMapOld.get(v_Key));
        
        // 等待过期
        Thread.sleep((v_Second + 2) * 1000L);
        
        v_ExpireMapNew.putAll(v_ExpireMapOld);

        System.out.println("-- 过期后，复制到新集合中");
        System.out.println("-- 原集合能否得到值：" + v_ExpireMapOld.get(v_Key));
        System.out.println("-- 新集合能否得到值：" + v_ExpireMapNew.get(v_Key));
    }
    
    
    
    /**
     * 1千万时（小数据量时无差别）
     * 写入时长：0 00:20:44.050
     * 读取时长：0 00:16:50.644
     * 总用时长：0 00:37:34.694
     * 内存占用：129.8 MB  （读写处理完时）
     */
    @Test
    public void test_003() throws InterruptedException
    {
        Date                    v_BeginTime = new Date();
        ExpireMap<String ,Date> v_ExpireMap = new ExpireMap<String ,Date>();
        long                    v_Count     = 10000 * 1000;                               // 总数量 
        long                    v_CountPer  = 1000;                                       // 每次加载多少数量后等待一小段时间
        long                    v_WaitLen   = 100;                                        // 等待一小段时间
        long                    v_TimeLen   = v_Count / v_CountPer / (1000 / v_WaitLen);  // 加载全部对象估算用时（单位：秒）
        
        ProcessHandle currentProcess = ProcessHandle.current();
        long pid = currentProcess.pid();                                                  // 获取进程ID（PID）
        System.out.println("加载全部对象预计用时：" + Date.toTimeLen(v_TimeLen * 1000));
        System.out.println("请导出堆快照 jmap -dump:format=b,file=oldgen_heap.hprof " + pid + "\n\n");
        
        for (int x=1; x<=v_Count; x++)
        {
            v_ExpireMap.put(x + "" ,new Date() ,v_TimeLen + 10);
            
            if ( x % v_CountPer == 0 )
            {
                Thread.sleep(v_WaitLen);
                System.out.println("1: " + new Date().getFullMilli() + " " + Help.division(2 ,(x * 100) + "" ,v_Count + "") + "%  " + v_ExpireMap.size());
            }
        }
        
        Date v_MiddleTime = new Date();
        while (v_ExpireMap.size() > 0)
        {
            System.out.println("2: " + new Date().getFullMilli() + "  " + v_ExpireMap.size());
            Thread.sleep(1000L);
        }
        
        System.out.println("写入时长：" + Date.toTimeLen(v_MiddleTime     .differ(v_BeginTime)));
        System.out.println("读取时长：" + Date.toTimeLen(Date.getNowTime().differ(v_MiddleTime)));
        System.out.println("总用时长：" + Date.toTimeLen(Date.getNowTime().differ(v_BeginTime)));
        System.out.println("请导出堆快照 jmap -dump:format=b,file=oldgen_heap2016.hprof " + pid);
        Thread.sleep(10 * 60 * 1000);
    }
    
    
    
    /**
     * 1千万时（小数据量时无差别）
     * 写入时长：0 00:18:15.190
     * 读取时长：0 00:16:50.403
     * 总用时长：0 00:35:05.594
     * 内存占用：66.2 MB  （读写处理完时）
     */
    @Test
    public void test_004() throws InterruptedException
    {
        Date                      v_BeginTime   = new Date();
        ExpireCache<String ,Date> v_ExpireCache = new ExpireCache<String ,Date>();
        long                      v_Count       = 10000 * 1000;                               // 总数量 
        long                      v_CountPer    = 1000;                                       // 每次加载多少数量后等待一小段时间
        long                      v_WaitLen     = 100;                                        // 等待一小段时间
        long                      v_TimeLen     = v_Count / v_CountPer / (1000 / v_WaitLen);  // 加载全部对象估算用时（单位：秒）
        
        v_ExpireCache.setMaximumSize(v_Count);
        v_ExpireCache.setExpireMilli(v_TimeLen + 10);
        
        ProcessHandle currentProcess = ProcessHandle.current();
        long pid = currentProcess.pid();                                                      // 获取进程ID（PID）
        System.out.println("加载全部对象预计用时：" + Date.toTimeLen(v_TimeLen * 1000));
        System.out.println("请导出堆快照 jmap -dump:format=b,file=oldgen_heap.hprof " + pid + "\n\n");
        
        for (int x=1; x<=v_Count; x++)
        {
            v_ExpireCache.put(x + "" ,new Date() ,v_TimeLen + 10);
            
            if ( x % v_CountPer == 0 )
            {
                Thread.sleep(v_WaitLen);
                System.out.println("1: " + new Date().getFullMilli() + " " + Help.division(2 ,(x * 100) + "" ,v_Count + "") + "%  " + v_ExpireCache.size());
            }
        }
        
        Date v_MiddleTime = new Date();
        while (v_ExpireCache.size() > 0)
        {
            System.out.println("2: " + new Date().getFullMilli() + "  " + v_ExpireCache.size());
            Thread.sleep(1000L);
        }
        
        System.out.println("写入时长：" + Date.toTimeLen(v_MiddleTime     .differ(v_BeginTime)));
        System.out.println("读取时长：" + Date.toTimeLen(Date.getNowTime().differ(v_MiddleTime)));
        System.out.println("总用时长：" + Date.toTimeLen(Date.getNowTime().differ(v_BeginTime)));
        System.out.println("请导出堆快照 jmap -dump:format=b,file=oldgen_heap2026.hprof " + pid);
        Thread.sleep(10 * 60 * 1000);
    }
    
    
    
    /**
     * java -cp classes;test-classes;D:\Maven\com\google\guava\guava\33.5.0-jre\* org.hy.common.junit.JU_ExpireMap version=2026
     * java -cp classes;test-classes;D:\Maven\com\google\guava\guava\33.5.0-jre\* org.hy.common.junit.JU_ExpireMap version=2016
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-12
     * @version     v1.0
     *
     * @param i_Args
     * @throws InterruptedException
     */
    public static void main(String [] i_Args) throws InterruptedException
    {
        AppParameter v_AppParams = new AppParameter(i_Args);
        String       v_Version   = v_AppParams.getParamValue("version");
        
        if ( "2026".equals(v_Version) )
        {
            System.out.println("2026 版本 ExpireCache");
            new JU_ExpireMap().test_004();
        }
        else
        {
            System.out.println("2016 版本 ExpireMap");
            new JU_ExpireMap().test_003();
        }
    }
    
}
