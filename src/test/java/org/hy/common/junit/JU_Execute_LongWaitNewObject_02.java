package org.hy.common.junit;





/**
 * 测试类：支持超时自动终止线程功能
 * 
 * @author  ZhengWei(HY)
 * @version 2014-08-14  
 */
public class JU_Execute_LongWaitNewObject_02
{
    
    static
    {
        try
        {
            Thread.sleep(1000 * 60 * 1);
        }
        catch (Exception exce)
        {
            // Nothing.
        }
    }
    
}
