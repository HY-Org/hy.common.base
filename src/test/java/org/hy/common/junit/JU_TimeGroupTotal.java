package org.hy.common.junit;

import org.hy.common.Date;
import org.hy.common.TimeGroupTotal;
import org.junit.Test;





/**
 * 测试单元：时间分组统计
 *
 * @author      ZhengWei(HY)
 * @createDate  2023-06-23
 * @version     v1.0
 */
public class JU_TimeGroupTotal
{
    
    @Test
    public void test() throws InterruptedException
    {
        TimeGroupTotal v_TimeGroupTotal = new TimeGroupTotal(1);
        v_TimeGroupTotal.setMaxSize(5);
        
        for (int x=1; x<=100; x++)
        {
            Date v_Now = new Date();
            v_TimeGroupTotal.put();
            Thread.sleep(1000 * 60);
            System.out.println(v_Now.getFull() + "    " + x + " = " + v_TimeGroupTotal.size());
        }
    }
    
}
