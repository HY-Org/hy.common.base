package org.hy.common.junit;

import org.hy.common.Date;
import org.hy.common.Execute;





/**
 * 测试类：支持超时自动终止线程功能
 * 
 * @author  ZhengWei(HY)
 * @version 2014-08-14  
 */
public class JU_Execute_LongWaitNewObject
{
    
    public static void main(String [] args) throws Throwable
    {
        Execute v_Execute = new Execute(new JU_Execute_LongWaitNewObject() ,"newObject");
        
        v_Execute.start(1000 * 5 * 2);
        
        v_Execute.waitting(System.out);
        
        
        System.out.println("\n-- 运行开始时间：" + v_Execute.getRunBeginTime().toString());
        System.out.println(  "-- 运行结束时间：" + v_Execute.getRunEndTime()  .toString());
        System.out.println(  "-- 运行用时时长：" + Date.toTimeLen(v_Execute.getRunTimeLen()));
        if ( v_Execute.isError() )
        {
            if ( v_Execute.isTimeout() )
            {
                System.out.println("-- 运行超时");
            }
            
            throw v_Execute.getException();
        }
        else
        {
            System.out.println("-- 成功执行方法.");
        }
    }
    
    
    
    public String newObject()
    {
        new JU_Execute_LongWaitNewObject_02();
        
        return "OK";
    }
    
}
