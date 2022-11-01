package org.hy.common.junit;

import static org.junit.Assert.*;

import java.net.ServerSocket;

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
    
    
    
    /**
     * 测试同一端口，在“端口可重用”的情况下，是否可以重用？
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-12-28
     * @version     v1.0
     *
     */
    @Test
    public void test_ServerSocket()
    {
        ServerSocket v_ServerSocket1 = null;
        ServerSocket v_ServerSocket2 = null;
        
        try
        {
            v_ServerSocket1 = Help.getServerSocket(12345 ,true);
            System.out.println("12345打开" + (v_ServerSocket1 != null ? "成功" : "失败"));
            
            v_ServerSocket2 = Help.getServerSocket(12345 ,true);
            System.out.println("重复打开" + (v_ServerSocket2 != null ? "成功" : "失败")); 
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        finally
        {
            if ( v_ServerSocket1 != null )
            {
                try
                {
                    v_ServerSocket1.close();
                }
                catch (Exception exce)
                {
                    exce.printStackTrace();
                }
            }
            
            if ( v_ServerSocket2 != null )
            {
                try
                {
                    v_ServerSocket2.close();
                }
                catch (Exception exce)
                {
                    exce.printStackTrace();
                }
            }
        }
    }
}
