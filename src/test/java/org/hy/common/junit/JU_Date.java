package org.hy.common.junit;

import java.time.LocalDateTime;

import org.hy.common.Date;
import org.hy.common.Lunar;
import org.hy.common.SolarTerm;
import org.junit.Test;





public class JU_Date
{
    
    @Test
    public void test_Format()
    {
        String [] v_Datas = new String[] {
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
    
    
    
    public void test_LocalDateTime()
    {
        LocalDateTime v_Now = LocalDateTime.now();
    }
    
}
