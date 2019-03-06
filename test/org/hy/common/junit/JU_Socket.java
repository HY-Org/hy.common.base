package org.hy.common.junit;

import static org.junit.Assert.*;

import org.hy.common.Date;
import org.hy.common.Help;
import org.junit.Test;





/**
 * Socket通讯测试
 *
 * @author      ZhengWei(HY)
 * @createDate  2019-03-06
 * @version     v1.0
 */
public class JU_Socket
{
    
    @Test
    public void test_isAllowConnect()
    {
        System.out.println("Socket通讯测试：开始时间 = " + Date.getNowTime().getFullMilli());
        boolean v_IsAllowConnect = Help.isAllowConnect("100.100.100.100" ,100 ,10 * 1000);
        System.out.println("Socket通讯测试：结束时间 = " + Date.getNowTime().getFullMilli());
        
        assertTrue(v_IsAllowConnect);
    }
    
}
