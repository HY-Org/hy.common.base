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
        System.out.println((new Date("2018-11-01 01:01:01.0")).getFull());
        System.out.println((new Date("9999-12-31 23:59:59")).getFull());
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
        System.out.println(SolarTerm.getSoralTerm(new Date("2019-06-22")));
    }
    
    
    
    public void test_LocalDateTime()
    {
        LocalDateTime v_Now = LocalDateTime.now();
    }
    
}
