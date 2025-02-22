package org.hy.common.junit;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.hy.common.Date;
import org.hy.common.Lunar;
import org.hy.common.SolarTerm;
import org.hy.common.StringHelp;
import org.junit.Test;





public class JU_Date
{
    
    @Test
    public void test_Format()
    {
        String [] v_Datas = new String[] {
                "2025-02-21 08:39:00.123456789",
                "2024-05-30T14:43:38-05:00",
                "2024-05-30T14:43:38+05:00",
                "2024-05-30T14:43:38.976-05:00",
                "2024-05-30T14:43:38.976+05:00",
                "2024-05-30T01:01:01.123-04:00[America/New_York]",
                "2024-05-30T01:01:01.123456789-04:00[America/New_York]",
                "2024-05-30T01:01:01.123456789+08:00[Asia/Shanghai]",
                "2024-05-30T14:43:38.976177400",
                "2024-05-30T14:43:38.976",
                "2024-05-30T14:43:38",
                "2024-1-2",
                "2024-1-02",
                "2024-1-2 9:8:7",
                "2024-1-2 9:8",
                "2024-1-2 9",
                "2024-1-02 9:8:7",
                "2024-1-02 9:8",
                "2024-1-02 9",
                "2024-1-02 09:8:7",
                "2024-1-02 09:8",
                "2024-1-02 09",
                "2024-01-02 09:8:7",
                "2024-01-02 09:8",
                "2024-01-02 09",
                "2024-01-02 09:08:7",
                "2024-01-02 09:08:07",
                "2024-01-02 09:08",
                "99991231010101Z",
                "99991231010101",
                "99991231010101987",
                "9999-12-31 00:00:00.1234567",
                "202401",
                "2024/1",
                "2024-1-",
                "2024/1/14 9:8:7",
                "2024年1月14 9:8:7",
                "2024年1月14日 9:8:7",
                "2024年1月14日",
                "2018-11-01 59:01:01.0",
                "2018-11-01 01:01:01.0",
                "9999-12-31 23:59:59",
                "9999-12-31 23:59:59.1",
                "9999-12-31 23:59:59.123",
                "23:59:59 9999-12-31",
                "9:8:7 2024/1/14"
        };
        
        for (String v_Data : v_Datas)
        {
            System.out.println(new Date(v_Data).getFullMilli() + "\t\t" + v_Data);
        }
    }
    
    
    
    /**
     * 纳秒测试
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-22
     * @version     v1.0
     * @throws InterruptedException 
     *
     */
    @Test
    public void test_Nano() throws InterruptedException
    {
        long v_NanoTimestamp1 = Date.getTimeNano();
        long v_NanoTimestamp2 = Date.getTimeNano();
        
        Thread.sleep(1000);
        
        long v_NanoTimestamp3 = Date.getTimeNano();
        long v_NanoDiffA      = v_NanoTimestamp2 - v_NanoTimestamp1;
        long v_NanoDiffB      = v_NanoTimestamp3 - v_NanoTimestamp2;
        
        System.out.println("纳秒级时间戳: " + v_NanoTimestamp1);
        System.out.println(Date.nanoToDate(v_NanoTimestamp1).getFullMilli());
        
        System.out.println("纳秒级差值2-1: " + Date.toTimeLenNano(v_NanoDiffA));
        System.out.println("纳秒级差值3-2: " + Date.toTimeLenNano(v_NanoDiffB));
        
        System.out.println("逆转纳秒级差值2-1: " + Date.toTimeValueNano(Date.toTimeLenNano(v_NanoDiffA)));
        System.out.println("逆转纳秒级差值3-2: " + Date.toTimeValueNano(Date.toTimeLenNano(v_NanoDiffB)));
        
        System.out.println("0 = " + Date.toTimeLenNano(0L));
    }
    
    
    
    /**
     * 时区的测试
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     */
    @Test
    public void test_setTimeZone()
    {
        String [] v_TimeZones = new String[] {
                 "GMT+05:00"
                ,"GMT+05"
                ,"GMT+5"
                ,"GMT05:00"
                ,"GMT05"
                ,"GMT5"
                ,"+05:00"
                ,"05:00"
                ,"05"
                ,"5"
        };
        
        String [] v_TimeZoneTypes = new String[] {"GMT" ,"UTC" ,"CST" ,"CET" ,"DST" ,"EDT" ,"PDT"};
        
        for (String v_TimeZoneType : v_TimeZoneTypes)
        {
            Date v_Time = new Date("2024-05-30 11:22:33.123");
            System.out.println(v_Time.getFullMilli() + " \t 初始时间");
            
            for (String v_TimeZone : v_TimeZones)
            {
                if ( v_TimeZone.startsWith("GMT") )
                {
                    v_TimeZone = StringHelp.replaceAll(v_TimeZone ,"GMT" ,v_TimeZoneType);
                }
                else if ( !"GMT".equals(v_TimeZoneType) )
                {
                    continue;
                }
                
                // v_Time = new Date("2024-05-30 11:22:33.123");
                String v_NewTimeZone = v_Time.setTimeZone(v_TimeZone);
                System.out.println(v_Time.getFullMilli() + " \t " + v_NewTimeZone + " for " + v_TimeZone);
            }
            
            System.out.println("\n");
        }
    }
    
    
    
    /**
     * 带时区的时间转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     */
    @Test
    public void test_getZonedDateTime()
    {
        Date v_Time = new Date("2024-05-30 11:22:33.123");
        
        ZonedDateTime v_ZonedDateTime = v_Time.getZonedDateTime();
        
        System.out.println(v_ZonedDateTime);
        System.out.println(Date.getNowTime());
        System.out.println(ZonedDateTime.now());
        
        v_ZonedDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        System.out.println("美国时间：" + v_ZonedDateTime);
        System.out.println("中国时间：" + new Date(v_ZonedDateTime));
    }
    
    
    
    /**
     * 本地时间的转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     */
    @Test
    public void test_getLocalDateTime()
    {
        Date v_Time = new Date("2024-05-30 11:22:33.123");
        
        LocalDateTime v_LocalDateTime = v_Time.getLocalDateTime();
        
        System.out.println(v_LocalDateTime);
        System.out.println(Date.getNowTime());
        System.out.println(LocalDateTime.now());
    }
    
    
    
    @Test
    public void test_getTimeGroup()
    {
        System.out.println(" 0值分组：" + new Date("2023-06-21 12:02:00").getTimeGroup(0));
        System.out.println("58值分组：" + new Date("2023-06-21 12:58:00").getTimeGroup(58));
        System.out.println("59值分组：" + new Date("2023-06-21 12:59:00").getTimeGroup(59));
    }
    
    
    
    @Test
    public void test_getTimeGroupSecond()
    {
        System.out.println(" 0值分组：" + new Date("2023-06-21 12:10:02").getTimeGroupSecond(0));
        System.out.println("30值分组：" + new Date("2023-06-21 12:10:02").getTimeGroupSecond(30));
        System.out.println("30值分组：" + new Date("2023-06-21 12:10:32").getTimeGroupSecond(30));
        System.out.println("58值分组：" + new Date("2023-06-21 12:10:58").getTimeGroupSecond(58));
        System.out.println("59值分组：" + new Date("2023-06-21 12:10:59").getTimeGroupSecond(59));
    }
    
    
    
    @Test
    public void test_getTimeGroupHour()
    {
        System.out.println(" 0值分组：" + new Date("2023-06-21 12:00:00").getTimeGroupHour(0));
        System.out.println(" 1值分组：" + new Date("2023-06-21 12:00:00").getTimeGroupHour(1));
        System.out.println(" 6值分组：" + new Date("2023-06-21 05:00:00").getTimeGroupHour(6));
        System.out.println(" 8值分组：" + new Date("2023-06-21 07:00:00").getTimeGroupHour(8));
        System.out.println("12值分组：" + new Date("2023-06-21 11:00:00").getTimeGroupHour(12));
        System.out.println("12值分组：" + new Date("2023-06-21 13:00:00").getTimeGroupHour(12));
    }
    
    
    
    @Test
    public void test_getTimeGroupDay()
    {
        System.out.println(" 0值分组：" + new Date("2023-07-01 00:00:00").getTimeGroupDay(0));
        System.out.println(" 2值分组：" + new Date("2023-07-06 00:00:00").getTimeGroupDay(2));
        System.out.println(" 2值分组：" + new Date("2023-07-07 00:00:00").getTimeGroupDay(2));
    }
    
    
    
    public void test01(String i_Text)
    {
       
    }
    
    
    
    public void test01(Object i_Text)
    {
        
    }
    
    
    
    @Test
    public void test_getDateByWork()
    {
        this.test01(0);
        
        Date v_Now = new Date("2016-07-15");
        
        for (int v_WorkDay=0; v_WorkDay<=366 * 4; v_WorkDay++)
        {
            Date v_WorkDate = v_Now.getDateByWork(v_WorkDay * -1);
            System.out.println(v_WorkDate.getYMD() + "    星期" + v_WorkDate.getWeek() + "\t" + v_WorkDay);
            
            if ( v_WorkDate.getWeek() == 1 )
            {
                System.out.println("");
            }
        }
    }
    
    
    
    @Test
    public void test_Lunar()
    {
        System.out.println(new Lunar(new Date()));
        System.out.println(SolarTerm.getSoralTerm(new Date("2021-12-21")));
        System.out.println(SolarTerm.getSoralTerm(new Date("2022-01-05")));
    }
    
}
